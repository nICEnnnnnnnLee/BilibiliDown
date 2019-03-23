@echo off
set/p option=继续卸载请按y确认:
if "%option%"=="y" echo 您输入了y &goto :unistall
if "%option%"=="Y" echo 您输入了Y &goto :unistall
echo 您输入了%option% &goto :end

:unistall
rd /s /q  %~dp0
:end