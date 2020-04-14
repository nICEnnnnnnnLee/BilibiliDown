:: cd 到脚本所在目录
cd /d %~dp0

:: 复制整个文件夹
xcopy src target\ /s /f /h

:: 删除不需要的java文件
rmdir /s/q target\nicelee\test\

:: 获取java文件列表
cd target
dir /s /B *.java > ../sources.txt
cd ..

:: 获取环境变量,解压lib包
cd libs
dir /s /B *.jar > ../libs.txt
cd ../target
setlocal enabledelayedexpansion
set classpath=.
for /f "tokens=*" %%i in (../libs.txt) do (
set classpath=!classpath!;%%i
jar xvf %%i
)
cd ..

:: 编译java
javac -cp !classpath! -encoding UTF-8 @sources.txt

:: 删除所有.java文件
cd target
del /a /f /s /q  "*.java"
cd ..

:: 打包
jar cvfe INeedBiliAV.jar nicelee.ui.FrameMain -C ./target .

echo 按任意键删除临时文件
pause

rmdir /s/q  target\
del sources.txt
del libs.txt
