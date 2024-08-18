cd /d %~dp0
:: 将精简jre写入当前环境的path
set Path=%~dp0minimal-bilibilidown-jre\bin;%Path%
set Path=%~dp0runtime\bin\;%Path%
:: -Dhttps.protocols=TLSv1.2 https://bugs.openjdk.org/browse/JDK-8206923
:: start javaw -Dhttps.protocols=TLSv1.2 -jar INeedBiliAV.jar
start javaw -Dfile.encoding=utf-8 -Dbilibili.prop.log=false  -Dhttps.protocols=TLSv1.2 -jar launch.jar