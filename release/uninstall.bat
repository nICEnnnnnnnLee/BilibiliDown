@echo off
echo 目标文件夹为： %~dp0
echo 该脚本将会删除文件夹下的所有文件
set/p option=继续卸载请按y确认:
if "%option%"=="y" echo 您输入了y &goto :unistall
if "%option%"=="Y" echo 您输入了Y &goto :unistall
echo 您输入了%option% &goto :end

:unistall
rd /s /q  "%~dp0"
:end