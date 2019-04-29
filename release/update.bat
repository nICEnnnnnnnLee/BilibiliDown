@echo off
:: 切换到当前文件目录
cd /d %~dp0
echo 等待程序关闭
timeout 3

:: 如果传入pid，且进程仍然存在，那么强制终止该进程
set pid=%2
if "%pid%"=="" goto :copy
echo 进程pid%pid%
tasklist|findstr /i "%pid% " || goto :copy
echo 强制终止进程
taskkill /F /PID %pid%

:copy
:: 复制文件(不提示直接覆盖)
copy /Y "update\INeedBiliAV.update.jar" "INeedBiliAV.jar"

if "%1"=="1" (echo 更新后重启 &goto :runApp) else (echo 仅更新 &goto :end)

:runApp
:: 运行程序
start javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar

:end
::pause
exit