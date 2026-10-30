#!/usr/bin/env python3
"""Read-only audit of the observed native Cove services. Never print secrets."""
import json, os, subprocess, zipfile
from pathlib import Path

folder = Path(__file__).resolve().parent
report = {"mysql_safe_to_stop": False, "redis_safe_to_stop": False}
try:
    with zipfile.ZipFile('/home/poro/cove-api-server-0.0.1-SNAPSHOT.jar') as archive:
        source = archive.read('BOOT-INF/classes/application.properties').decode()
    config = dict(line.split('=', 1) for line in source.splitlines()
                  if '=' in line and not line.strip().startswith('#'))
    config = {key.strip(): value.strip() for key, value in config.items()}
    if config.get('DATABASE_URL', '').startswith('jdbc:mysql://localhost:3306/covelabs_prod'):
        environment = os.environ.copy()
        environment['MYSQL_PWD'] = config['DATABASE_PASSWORD']
        def sql(statement):
            return subprocess.check_output(['mysql', '-h127.0.0.1', '-u', config['DATABASE_USERNAME'],
                                            '-N', '-e', statement], env=environment,
                                           stderr=subprocess.DEVNULL, text=True).strip()
        schemas = sql('SHOW DATABASES').splitlines()
        connections = int(sql("SELECT COUNT(*) FROM information_schema.PROCESSLIST WHERE ID != CONNECTION_ID() AND COMMAND != 'Daemon'"))
        development_tables = int(sql("SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='upbrella_dev'"))
        allowed = {'covelabs_prod', 'information_schema', 'mysql', 'performance_schema', 'sys', 'upbrella_dev'}
        applications = json.loads(subprocess.check_output(['docker','inspect','upbrella-api','upbrella-dev-api-1']))
        isolated = []
        for app in applications:
            variables = dict(item.split('=',1) for item in app['Config']['Env'] if '=' in item)
            url = variables.get('DATABASE_URL','')
            isolated.append(app['State'].get('Health',{}).get('Status') == 'healthy'
                            and (url.startswith('jdbc:mysql://mysql:3306/') or url.startswith('jdbc:mysql://db:3306/')))
        report.update(mysql_schemas=schemas, mysql_active_connections=connections,
                      legacy_development_table_count=development_tables,
                      current_upbrella_databases_isolated=all(isolated),
                      mysql_safe_to_stop='covelabs_prod' in schemas and set(schemas) <= allowed
                      and connections == 0 and all(isolated))
    if config.get('REDIS_HOST') in ('localhost', '127.0.0.1') and config.get('REDIS_PORT') == '6379':
        clients = subprocess.check_output(['redis-cli', 'CLIENT', 'LIST'], text=True).splitlines()
        other_clients = [line for line in clients if 'cmd=client|list' not in line]
        report.update(redis_other_clients=len(other_clients), redis_safe_to_stop=len(other_clients) == 0)
except (OSError, KeyError, ValueError, subprocess.SubprocessError, zipfile.BadZipFile) as error:
    report['audit_error_type'] = type(error).__name__
(folder / 'evidence').mkdir(exist_ok=True)
(folder / 'evidence/legacy-services.json').write_text(json.dumps(report, indent=2) + '\n')
print(json.dumps(report))
