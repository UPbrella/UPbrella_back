#!/usr/bin/env python3
"""Clone the local production snapshot into a NEW isolated development schema.

Run on the home server. No row contents, credentials or SQL dumps are printed.
The original development schema is retained; activation is a separate step.
"""
import argparse, gzip, hashlib, json, os, re, shutil, subprocess, zipfile
from datetime import datetime, timezone
from pathlib import Path

folder = Path(__file__).resolve().parent
source_container = 'upbrella-mysql'
target_container = 'upbrella-dev-db-1'
source_database = 'upbrella'
parser = argparse.ArgumentParser()
parser.add_argument('--verify', metavar='MANIFEST', help='Verify original columns after API startup.')
args = parser.parse_args()

def private_write(path, value):
    fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_TRUNC, 0o600)
    with os.fdopen(fd, 'w') as output:
        output.write(value)
    path.chmod(0o600)

def command(container, program, arguments):
    assert container in (source_container, target_container)
    import shlex
    shell = 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec '+shlex.join([program, '-uroot', *arguments])
    return ['docker', 'exec', '-i', container, 'sh', '-c', shell]

def sql(container, statement, database=None):
    arguments = ['--batch', '--raw', '--skip-column-names', '--default-character-set=utf8mb4']
    if database:
        assert re.fullmatch(r'[a-zA-Z0-9_]+', database)
        arguments.append(database)
    result = subprocess.run(command(container, 'mysql', arguments), input=statement.encode(), capture_output=True)
    if result.returncode:
        raise RuntimeError('Database operation failed; no row contents printed.')
    return result.stdout

def identifier(name):
    assert re.fullmatch(r'[a-zA-Z0-9_]+', name), 'Unexpected database identifier'
    return '`'+name+'`'

def fingerprints(database, structure):
    result = {}
    for table, info in structure.items():
        columns = ','.join('HEX(CAST('+identifier(c)+' AS BINARY))' for c in info['columns'])
        boundary = '' if info['maximum_id'] is None else ' WHERE `id` <= '+str(info['maximum_id'])
        content = sql(target_container, 'SELECT JSON_ARRAY('+columns+') FROM '+identifier(table)+boundary+' ORDER BY `id`;', database)
        count = int(sql(target_container, 'SELECT COUNT(*) FROM '+identifier(table)+boundary+';', database).strip())
        result[table] = {'rows':count, 'sha256':hashlib.sha256(content).hexdigest()}
    return result

if args.verify:
    manifest_path = Path(args.verify).resolve()
    assert manifest_path.is_relative_to(folder/'backups')
    manifest = json.loads(manifest_path.read_text())
    actual = fingerprints(manifest['target_database'], manifest['structure'])
    assert actual == manifest['fingerprints'], 'Original snapshot rows changed after startup'
    report = {'status':'passed', 'target_database':manifest['target_database'],
              'original_rows_preserved':True, 'tables':{table:v['rows'] for table,v in actual.items()},
              'snapshot_started_at':manifest['snapshot_started_at'], 'snapshot_completed_at':manifest['snapshot_completed_at']}
    (folder/'evidence/production-clone-checks.json').write_text(json.dumps(report, indent=2)+'\n')
    print(json.dumps(report))
    raise SystemExit(0)

assert source_container != target_container
inspection = json.loads(subprocess.check_output(['docker','inspect',source_container,target_container]))
assert inspection[0]['Id'] != inspection[1]['Id']
assert all(item['State']['Running'] for item in inspection)
private_folder = folder/'.private'
private_folder.mkdir(exist_ok=True,mode=0o700)
private_folder.chmod(0o700)
# Read only the encryption configuration needed to use the copied ciphertext.
# Production AWS, social-login and notification credentials are never copied.
production_api = json.loads(subprocess.check_output(['docker','inspect','upbrella-api']))[0]['Config']
production_environment = dict(value.split('=',1) for value in production_api['Env'] if '=' in value)
encryption = {key:production_environment[key] for key in ('SECRET_KEY','ALGORITHM') if key in production_environment}
if len(encryption) != 2:
    jar_paths = [value for value in (production_api.get('Cmd') or [])+(production_api.get('Entrypoint') or []) if value.endswith('.jar')]
    assert len(jar_paths) == 1
    jar_path = str(Path(production_api.get('WorkingDir') or '/')/jar_paths[0])
    local_jar = private_folder/'production-configuration.jar'
    try:
        subprocess.run(['docker','cp','upbrella-api:'+jar_path,str(local_jar)],check=True,stdout=subprocess.DEVNULL)
        local_jar.chmod(0o600)
        with zipfile.ZipFile(local_jar) as archive:
            for name in archive.namelist():
                if name.startswith('BOOT-INF/classes/application') and name.endswith('.properties'):
                    for line in archive.read(name).decode().splitlines():
                        if '=' in line and not line.lstrip().startswith('#'):
                            key,value = line.split('=',1)
                            if key.strip() in ('SECRET_KEY','ALGORITHM') and key.strip() not in production_environment:
                                encryption[key.strip()] = value.strip()
    finally:
        if local_jar.exists(): local_jar.unlink()
assert len(encryption.get('SECRET_KEY','').encode()) in (16,24,32)
assert encryption.get('ALGORITHM') and all('${' not in value for value in encryption.values())
private_write(private_folder/'production-encryption.env',''.join(key+'='+value+'\n' for key,value in encryption.items()))
environment = dict(line.split('=',1) for line in (folder/'secret.env').read_text().splitlines() if '=' in line)
assert environment['AWS_ACCESS_KEY'] == 'development-disabled'
assert environment['AWS_SECRET_ACCESS_KEY'] == 'development-disabled'
assert environment['SLACK_WEB_HOOK_URL'] == 'http://127.0.0.1:9/disabled'
assert environment['KAKAO_CLIENT_ID_DEV'] == 'disabled'

stamp = datetime.now(timezone.utc).strftime('%Y%m%d_%H%M%S')
target_database = 'upbrella_dev_clone_'+stamp
identifier(target_database)
backup_root = folder/'backups'
backup_root.mkdir(exist_ok=True, mode=0o700)
backup_root.chmod(0o700)
backup = backup_root/stamp
backup.mkdir(mode=0o700)
log = backup/'operations.log'
private_write(log, '')
for name in ['secret.env','.env','compose.yaml']:
    if (folder/name).exists():
        shutil.copy2(folder/name, backup/(name+'.before'))
        (backup/(name+'.before')).chmod(0o600)

current_database = 'upbrella_dev'
if (folder/'.env').exists():
    for line in (folder/'.env').read_text().splitlines():
        if line.startswith('UPBRELLA_DEV_DATABASE_NAME='):
            current_database = line.split('=',1)[1]
assert current_database == 'upbrella_dev' or current_database.startswith('upbrella_dev_clone_')
identifier(current_database)
engines = sql(source_container, "SELECT TABLE_NAME,ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA='upbrella' ORDER BY TABLE_NAME;").decode().splitlines()
assert engines and all(row.split('\t')[1] == 'InnoDB' for row in engines), 'Consistent snapshot requires InnoDB business tables.'
business_tables = [row.split('\t')[0] for row in engines if not row.split('\t')[0].upper().startswith('SPRING_SESSION')]
assert len(business_tables) == 12, 'Review a changed production schema before cloning.'

def dump(container, database, destination, tables=None):
    arguments = ['--single-transaction','--quick','--skip-lock-tables','--no-tablespaces',
                 '--set-gtid-purged=OFF','--hex-blob','--default-character-set=utf8mb4',
                 '--skip-add-locks','--routines','--triggers','--skip-comments',database]
    if tables:
        arguments.extend(tables)
    # SQL remains private on this server, never in stdout or a local Git checkout.
    with open(log, 'ab') as errors:
        process = subprocess.Popen(command(container, 'mysqldump', arguments), stdout=subprocess.PIPE, stderr=errors)
        fd = os.open(destination, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
        with os.fdopen(fd,'wb') as raw, gzip.GzipFile(fileobj=raw,mode='wb') as compressed:
            shutil.copyfileobj(process.stdout, compressed)
        process.stdout.close()
        assert process.wait() == 0, 'Dump failed; inspect private operations.log'

dump(target_container, current_database, backup/'development-before.sql.gz')
started = datetime.now(timezone.utc).isoformat()
snapshot = backup/'production-snapshot.sql.gz'
dump(source_container, source_database, snapshot, business_tables)
completed = datetime.now(timezone.utc).isoformat()
assert snapshot.stat().st_size > 100
charset = sql(source_container, "SELECT DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='upbrella';").decode().strip().split('\t')
assert len(charset) == 2 and all(re.fullmatch(r'[a-zA-Z0-9_]+',v) for v in charset)
sql(target_container, 'CREATE DATABASE '+identifier(target_database)+' CHARACTER SET '+charset[0]+' COLLATE '+charset[1]+';')
# Do not enable database event scheduling or copy authentication sessions.
assert sql(target_container, 'SELECT @@event_scheduler;').strip() in (b'OFF',b'DISABLED')
with gzip.open(snapshot,'rb') as source:
    result = subprocess.run(command(target_container,'mysql',['--binary-mode','--default-character-set=utf8mb4',target_database]),
                            input=source.read(), capture_output=True)
    if result.returncode:
        with open(log,'ab') as errors: errors.write(result.stderr)
        raise RuntimeError('Restore failed; original development database remains intact.')
sql(target_container, 'GRANT ALL PRIVILEGES ON '+identifier(target_database)+".* TO 'upbrella_dev'@'%';")
# Production uses bigint classification references with an int primary key.
# Widen the clone's key without changing values so the current ORM can add its FKs.
sql(target_container, 'ALTER TABLE `classification` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT;', target_database)
structure = {}
for table in business_tables:
    columns = sql(target_container, "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='"+target_database+"' AND TABLE_NAME='"+table+"' ORDER BY ORDINAL_POSITION;").decode().splitlines()
    assert 'id' in columns
    maximum = sql(target_container, 'SELECT MAX(`id`) FROM '+identifier(table)+';', target_database).decode().strip()
    structure[table] = {'columns':columns,'maximum_id':None if maximum=='NULL' else int(maximum)}
manifest = {'status':'staged','source_database':source_database,'target_database':target_database,
            'previous_database':current_database, 'snapshot_started_at':started,'snapshot_completed_at':completed,
            'production_dump_sha256':hashlib.sha256(snapshot.read_bytes()).hexdigest(), 'structure':structure,
            'fingerprints':fingerprints(target_database,structure), 'backup_directory':str(backup)}
manifest_path = backup/'manifest.json'
private_write(manifest_path,json.dumps(manifest,indent=2)+'\n')
private_write(folder/'.private/current-clone-manifest',str(manifest_path)+'\n')
print(json.dumps({'status':'staged','target_database':target_database,'manifest':str(manifest_path),
                  'snapshot_bytes_compressed':snapshot.stat().st_size,
                  'tables':{table:v['rows'] for table,v in manifest['fingerprints'].items()},
                  'original_development_database_retained':True,'production_writes':False}))
