cd /d %~dp0
:: 将精简jre写入当前环境的path
:: set Path=D:\Program Files\Java\minimal-bilibilidown-jre\bin\;%Path%
set Path=%~dp0minimal-bilibilidown-jre;%Path%
java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
pause