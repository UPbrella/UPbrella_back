#!/usr/bin/env python3
from pathlib import Path
import os, secrets
p=Path(__file__).parent/'secret.env'
if p.exists():
    print('Existing development secrets retained.'); raise SystemExit(0)
password=secrets.token_hex(24)
values={'MYSQL_ROOT_PASSWORD':secrets.token_hex(24),'MYSQL_PASSWORD':password,'DATABASE_PASSWORD':password,
'UPBRELLA_DEV_LOGIN_KEY':secrets.token_urlsafe(36),'SECRET_KEY':secrets.token_hex(16),'ALGORITHM':'AES/CBC/PKCS5Padding',
'KAKAO_CLIENT_ID_DEV':'disabled','KAKAO_CLIENT_SECRET_DEV':'disabled',
'KAKAO_REDIRECT_URI_DEV':'https://porodev.asuscomm.com/upbrella-dev/auth','KAKAO_LOGIN_URI_DEV':'https://porodev.asuscomm.com/upbrella-dev/login',
'AWS_ACCESS_KEY':'development-disabled','AWS_SECRET_ACCESS_KEY':'development-disabled','AWS_REGION':'ap-northeast-2','AWS_S3_BUCKET':'upbrella-development-disabled',
'SLACK_WEB_HOOK_URL':'http://127.0.0.1:9/disabled'}
fd=os.open(p,os.O_WRONLY|os.O_CREAT|os.O_EXCL,0o600)
with os.fdopen(fd,'w') as f: f.write(''.join(k+'='+v+'\n' for k,v in values.items()))
print('Isolated development secrets created; values not printed.')
