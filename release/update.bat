@echo off
:: 切换到当前文件目录
cd /d %~dp0
echo 等待程序关闭
timeout 3

:: 如果传入pid，且进程仍然存在，那么强制终止该进程
set pid=%2
if "%pid%"=="" goto :download_and_unzip_jar
echo 进程pid%pid%
tasklist|findstr /i "%pid% " || goto :copy
echo 强制终止进程
taskkill /F /PID %pid%
goto :copy

:: 如果没有传入pid，说明是人工调用，此时下载最新包并解压
:download_and_unzip_jar
set Path=%~dp0minimal-bilibilidown-jre\bin\;%Path%
set Path=%~dp0runtime\bin\;%Path%
java -Dfile.encoding=utf-8 -cp INeedBiliAV.jar nicelee.bilibili.util.VersionManagerUtil
if "%errorlevel%"=="1" (echo 更新成功 &goto :copy)
if "%errorlevel%"=="0" (echo 当前已是最新版本 &goto :end_pause)
if "%errorlevel%"=="-1" (echo 未能更新成功 &goto :end_pause)

:copy
:: 复制文件(不提示直接覆盖)
copy /Y "update\INeedBiliAV.update.jar" "INeedBiliAV.jar"

if "%1"=="0" (echo 仅更新 &goto :end_pause) else (echo 更新后重启 &goto :runApp)

:runApp
:: 运行程序
start javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar

:end

exit

:end_pause
pause