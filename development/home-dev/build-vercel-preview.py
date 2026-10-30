#!/usr/bin/env python3
"""Build only public assets for the isolated Upbrella Vercel development site."""
import argparse, json, os, re, subprocess
from pathlib import Path
from urllib.parse import urlparse

parser = argparse.ArgumentParser()
parser.add_argument('--api-origin', default='https://porodev.asuscomm.com/upbrella-dev/api')
args = parser.parse_args()
url = urlparse(args.api_origin)
if url.scheme != 'https' or not url.hostname or url.username or url.password or url.query or url.fragment:
    raise SystemExit('API origin must be an HTTPS URL without credentials, query or fragment.')
folder = Path(__file__).resolve().parent
workspace = Path(os.environ.get('UPBRELLA_WORKSPACE', str(folder.parent.parent)))
source = workspace/'frontend'
bundle = folder/'.run/vercel-preview'
output = bundle/'.vercel/output'
output.mkdir(parents=True,exist_ok=True)
environment = os.environ.copy()
environment.update(VITE_BASE_PATH='/', VITE_UPBRELLA_API_BASE_URL='/upbrella-dev/api',
                   VITE_DEV_LOGIN='true', VITE_GA_MEASUREMENT_ID='disabled', VITE_MOCK_MODE='false')
subprocess.run(['npm','run','build','--','--mode','home-dev','--outDir',str(output/'static'),'--emptyOutDir'],
               cwd=source,env=environment,check=True)
html = (output/'static/index.html').read_text()
if not re.search(r'maps\.js\?ncpKeyId=[A-Za-z0-9_-]+', html):
    raise SystemExit('Naver map client key missing. Configure VITE_NAVER_MAP_API_KEY in frontend/.env.home-dev.local before deployment.')
config = {'version':3,'routes':[
    {'src':'/upbrella-dev/api/(.*)','dest':args.api_origin.rstrip('/')+'/$1',
     'headers':{'Cache-Control':'no-store','CDN-Cache-Control':'no-store','Vercel-CDN-Cache-Control':'no-store'}},
    {'src':'/(.*)','headers':{'Permissions-Policy':'camera=(self), microphone=()',
                            'X-Content-Type-Options':'nosniff','X-Robots-Tag':'noindex, nofollow'},'continue':True},
    {'handle':'filesystem'},
    {'src':'/.*','dest':'/index.html'}]}
(output/'config.json').write_text(json.dumps(config,indent=2)+'\n')
(bundle/'vercel.json').write_text(json.dumps({'framework':None})+'\n')
print('Public build ready:',bundle)
