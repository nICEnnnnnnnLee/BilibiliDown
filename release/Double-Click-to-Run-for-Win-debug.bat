cd /d %~dp0
set Path=%~dp0minimal-bilibilidown-jre\bin\;%Path%
set Path=%~dp0runtime\bin\;%Path%
:: -Dhttps.protocols=TLSv1.2 https://bugs.openjdk.org/browse/JDK-8206923
:: java -Dfile.encoding=utf-8  -Dhttps.protocols=TLSv1.2 -jar INeedBiliAV.jar
java -Dfile.encoding=utf-8  -Dhttps.protocols=TLSv1.2 -jar launch.jar
pause