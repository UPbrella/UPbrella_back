#!/usr/bin/env python3
"""Activate a staged clone, preserving the previous database and a rollback path."""
import json, os, re, shutil, subprocess, time, urllib.request
from pathlib import Path

folder = Path(__file__).resolve().parent
manifest_path = Path((folder/'.private/current-clone-manifest').read_text().strip()).resolve()
assert manifest_path.is_relative_to(folder/'backups')
manifest = json.loads(manifest_path.read_text())
assert manifest['status'] == 'staged'
database = manifest['target_database']
assert re.fullmatch(r'upbrella_dev_clone_\d{8}_\d{6}', database)
backup = Path(manifest['backup_directory'])

def private_write(path, text):
    fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_TRUNC, 0o600)
    with os.fdopen(fd,'w') as output: output.write(text)
    path.chmod(0o600)

values = dict(line.split('=',1) for line in (folder/'secret.env').read_text().splitlines() if '=' in line)
encryption = dict(line.split('=',1) for line in (folder/'.private/production-encryption.env').read_text().splitlines() if '=' in line)
assert len(encryption['SECRET_KEY'].encode()) in (16,24,32)
assert all(re.fullmatch(r'[a-zA-Z0-9_./-]+',v) for v in encryption.values()), 'Review special characters before env-file activation.'
values.update(encryption)
old_settings = (folder/'.env').read_text() if (folder/'.env').exists() else ''
new_settings = '\n'.join(line for line in old_settings.splitlines() if not line.startswith('UPBRELLA_DEV_DATABASE_NAME='))

def restart_api():
    with open(backup/'activation.log','ab') as log:
        subprocess.run(['docker','compose','up','-d','--no-deps','api'],cwd=folder,check=True,stdout=log,stderr=log)
    deadline = time.monotonic()+120
    while time.monotonic() < deadline:
        try:
            with urllib.request.urlopen('http://127.0.0.1:18090/health',timeout=3) as response:
                if response.status == 200 and json.loads(response.read())['status'] == 'UP': return
        except Exception:
            pass
        time.sleep(2)
    raise RuntimeError('Development API did not become healthy.')

private_write(backup/'activation.log','')
try:
    private_write(folder/'secret.env',''.join(k+'='+v+'\n' for k,v in values.items()))
    private_write(folder/'.env',new_settings+'\nUPBRELLA_DEV_DATABASE_NAME='+database+'\n')
    print('Switching only the development API to the new snapshot.',flush=True)
    restart_api()
    check = subprocess.run(['python3',str(folder/'clone-production.py'),'--verify',str(manifest_path)],capture_output=True,text=True)
    if check.returncode:
        raise RuntimeError('Original snapshot verification failed.')
    manifest['status'] = 'active-and-verified'
    private_write(manifest_path,json.dumps(manifest,indent=2)+'\n')
    print(check.stdout.strip())
    print('Development snapshot active. Previous database and private backup retained.')
except Exception:
    shutil.copy2(backup/'secret.env.before',folder/'secret.env')
    (folder/'secret.env').chmod(0o600)
    if (backup/'.env.before').exists():
        shutil.copy2(backup/'.env.before',folder/'.env')
    elif (folder/'.env').exists():
        (folder/'.env').unlink()
    restart_api()
    manifest['status'] = 'activation-failed-previous-database-restored'
    private_write(manifest_path,json.dumps(manifest,indent=2)+'\n')
    raise SystemExit('Activation failed; previous development database and configuration restored. Inspect private activation.log.')
