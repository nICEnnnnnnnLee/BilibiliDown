# 工作目录在项目顶级
import re, os
from datetime import datetime, timedelta, timezone

version, about, buildType = None, None, os.environ.get("BUILD_TYPE", 'Release')
repo = os.environ.get("GITHUB_REPO", 'nICEnnnnnnnLee/BilibiliDown')
hashSHA = os.environ.get("GITHUB_SHA", 'unknown_sha')
runId = os.environ.get("GITHUB_RUN_ID", 'unknown_run_id')
utc_dt = datetime.utcnow().replace(tzinfo=timezone.utc)
bj_dt = utc_dt.astimezone(timezone(timedelta(hours=8)))
dateTime = bj_dt.strftime("%Y-%m-%d %H:%M:%S")
with open('src/nicelee/ui/Global.java', encoding='utf-8') as file:
    content = file.read()
    pattern = r'@Config\(key *= *"bilibili.version", *defaultValue *= *"v([\d\.]+)"'
    searchObj = re.search(pattern, content)
    version = searchObj.group(1)
    with open(os.environ.get("GITHUB_OUTPUT"),'w', encoding='utf-8') as output:
        output.write("value=" + version)
        
with open('src/resources/about.html', 'r', encoding='utf-8') as file:
    about = file.read()
    
with open('src/resources/about.html', 'w', encoding='utf-8') as file:
    buildInfo = f'''<div id="versionInfo" >
                    <p>版本信息: v{version} {buildType} - commit hash:<a href="https://github.com/{repo}/commit/{hashSHA}">{hashSHA}</a></p>
                    <p>编译信息: Build by Github Actions at {dateTime}, workflow: <a href="https://github.com/{repo}/actions/runs/{runId}">{runId}</a></p>
                </div>'''
    txtToSave = about.replace('<div id="versionInfo" ></div>', buildInfo)
    file.write(txtToSave)
