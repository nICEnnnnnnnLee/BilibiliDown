cd /d %~dp0
:: 将精简jre写入当前环境的path
set Path=%~dp0minimal-bilibilidown-jre\bin;%Path%
start javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar