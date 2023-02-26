:: cd 到脚本所在目录
cd /d %~dp0
cd ../..

pause
:: 复制整个文件夹
xcopy src-launcher target-launcher\ /s /f /h

:: 删除不需要的java文件
:: rmdir /s/q target-launcher\nicelee\test\

:: 获取java文件列表
cd target-launcher
dir /s /B *.java > ../sources.txt
cd ..

:: 编译java
javac -encoding UTF-8 @sources.txt

:: 删除所有.java文件
cd target-launcher
del /a /f /s /q  "*.java"
cd ..

:: 打包
jar cvfe launch.jar nicelee.memory.App -C ./target-launcher .

echo 按任意键删除临时文件
pause

rmdir /s/q  target-launcher\
del sources.txt
