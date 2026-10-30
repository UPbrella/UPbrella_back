#!/usr/bin/env bash
set -euo pipefail
if [ "$(id -u)" -ne 0 ]; then echo 'Run this file with sudo in your authenticated home-server terminal.'; exit 1; fi
python3 - <<'PY'
from pathlib import Path
import shutil,datetime
p=Path('/etc/nginx/sites-enabled/default').resolve()
s=p.read_text()
marker='# Upbrella isolated development gateway'
if marker not in s:
    target='    location ^~ /dram-live-api/'
    if target not in s: raise SystemExit('Expected porodev server location not found; no changes made.')
    insertion='''    # Upbrella isolated development gateway
    location = /upbrella-dev { return 301 /upbrella-dev/; }
    location ^~ /upbrella-dev/ {
        proxy_pass http://127.0.0.1:13000;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        client_max_body_size 6m;
    }

'''
    backup=Path('/home/poro/upbrella-dev/evidence/nginx-before-'+datetime.datetime.now().strftime('%Y%m%d%H%M%S')+'.conf')
    shutil.copy2(p,backup)
    p.write_text(s.replace(target,insertion+target,1))
    import subprocess
    if subprocess.run(['nginx','-t']).returncode:
        shutil.copy2(backup,p); raise SystemExit('Configuration check failed; original configuration restored.')
PY
nginx -t
systemctl reload nginx
# Read-only inventory of legacy services, so their ownership can be checked before stopping them.
mysql -N -e 'SHOW DATABASES' > /home/poro/upbrella-dev/evidence/legacy-databases.txt 2>/dev/null || true
redis-cli INFO keyspace > /home/poro/upbrella-dev/evidence/legacy-redis.txt 2>/dev/null || true
chown poro:poro /home/poro/upbrella-dev/evidence/legacy-*.txt
python3 /home/poro/upbrella-dev/audit-legacy-services.py
python3 - <<'PY'
import json,subprocess
from pathlib import Path
p=Path('/home/poro/upbrella-dev/evidence/legacy-services.json')
report=json.loads(p.read_text())
for service,flag in [('mysql','mysql_safe_to_stop'),('redis-server','redis_safe_to_stop')]:
    if report.get(flag):
        subprocess.run(['systemctl','disable','--now',service],check=True)
        report[service+'_stopped']=True
    else:
        print(service+': audit did not establish exclusive unused Cove ownership; retained.')
p.write_text(json.dumps(report,indent=2)+'\n')
PY
chown poro:poro /home/poro/upbrella-dev/evidence/legacy-services.json
echo 'Development HTTPS route installed; verified unused Cove native services stopped.'
