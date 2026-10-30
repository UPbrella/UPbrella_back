#!/usr/bin/env bash
set -euo pipefail
deployment_dir="$(cd "$(dirname "$0")" && pwd)"
workspace="${UPBRELLA_WORKSPACE:-$(dirname "$deployment_dir")}"
if [ ! -d "$workspace/frontend" ]; then workspace="$(dirname "$workspace")"; fi
cd "$workspace/frontend"
npm run build -- --mode home-dev
python3 - <<'PY'
from pathlib import Path
import json
p=Path('dist/manifest.json');d=json.loads(p.read_text());base='/upbrella-dev/'
for k in ['id','start_url','scope']:d[k]=base
for i in d['icons']:i['src']=base+i['src'].lstrip('/')
for s in d.get('shortcuts',[]):
 s['url']=base+s['url'].lstrip('/')
 for i in s.get('icons',[]):i['src']=base+i['src'].lstrip('/')
d.pop('screenshots',None);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
PY
