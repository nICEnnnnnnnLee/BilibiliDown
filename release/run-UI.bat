rem 如果没有java环境,且下载的是精简jre
:: set Path=D:\Program Files\Java\minimal-bilibilidown-jre\bin\;%Path%
cd /d %~dp0
start javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar