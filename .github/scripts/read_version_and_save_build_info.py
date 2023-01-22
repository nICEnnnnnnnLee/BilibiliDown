# 工作目录在项目顶级
import re, os, datetime
from time import strftime

version, about, buildType = None, None, os.environ.get("BUILD_TYPE", 'Release')
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
    now=datetime.datetime.now()
    buildInfo = '''<div id="versionInfo" >
                    <p>版本信息: v{version} {buildType}</p>
                    <p>编译信息: Build by Github Actions at {dateTime}</p>
                </div>'''.format(
                    version = version,
                    buildType = buildType,
                    dateTime = now.strftime("%Y-%m-%d %H:%M:%S")
                )
    txtToSave = about.replace('<div id="versionInfo" ></div>', buildInfo)
    file.write(txtToSave)