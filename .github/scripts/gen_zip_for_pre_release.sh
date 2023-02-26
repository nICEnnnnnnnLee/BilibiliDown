# 工作目录在项目顶级
# 必须的环境变量： VERSION_NUMBER

rm -rf ./config
rm -rf ./LICENSE
mkdir ./config/
mkdir ./LICENSE/
mv -f ./release/Create-Shortcut-on-Desktop-for-Linux.sh .
mv -f ./release/Create-Shortcut-on-Desktop-for-Mac.sh .
mv -f ./release/Create-Shortcut-on-Desktop-for-Win.vbs .
mv -f ./release/Double-Click-to-Run-for-Mac.command .
mv -f ./release/Double-Click-to-Run-for-Win.bat .
mv -f ./release/Double-Click-to-Run-for-Win-debug.bat .
mv -f ./release/uninstall.bat .
mv -f ./release/update.bat .
mv -f ./release/config/* ./config/
mv -f ./release/LICENSE/* ./LICENSE/        

zip "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" INeedBiliAV.jar
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./launch.jar
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Create-Shortcut-on-Desktop-for-Linux.sh
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Create-Shortcut-on-Desktop-for-Mac.sh
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Create-Shortcut-on-Desktop-for-Win.vbs
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Double-Click-to-Run-for-Mac.command
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Double-Click-to-Run-for-Win.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./Double-Click-to-Run-for-Win-debug.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./uninstall.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./update.bat
zip -rm "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./config/
zip -rm "BilibiliDown.v$VERSION_NUMBER.pre-release.zip" ./LICENSE/