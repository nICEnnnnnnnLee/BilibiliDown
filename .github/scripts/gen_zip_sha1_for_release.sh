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

zip "BilibiliDown.v$VERSION_NUMBER.release.zip" INeedBiliAV.jar
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./launch.jar
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Create-Shortcut-on-Desktop-for-Linux.sh
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Create-Shortcut-on-Desktop-for-Mac.sh
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Create-Shortcut-on-Desktop-for-Win.vbs
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Double-Click-to-Run-for-Mac.command
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Double-Click-to-Run-for-Win.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./Double-Click-to-Run-for-Win-debug.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./uninstall.bat
zip -m "BilibiliDown.v$VERSION_NUMBER.release.zip" ./update.bat
zip -rm "BilibiliDown.v$VERSION_NUMBER.release.zip" ./config/
zip -rm "BilibiliDown.v$VERSION_NUMBER.release.zip" ./LICENSE/

wget https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/minimal-bilibilidown-jre11.0.23_9.crypto.ec_win_x64.zip
unzip minimal-bilibilidown-jre11.0.23_9.crypto.ec_win_x64.zip
cp "BilibiliDown.v$VERSION_NUMBER.release.zip" "BilibiliDown.v$VERSION_NUMBER.win_x64_jre11.release.zip"
zip -rm "BilibiliDown.v$VERSION_NUMBER.win_x64_jre11.release.zip" ./minimal-bilibilidown-jre/

wget https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg-20240123-win-amd64.exe
mv ffmpeg-20240123-win-amd64.exe ffmpeg.exe
zip -m "BilibiliDown.v$VERSION_NUMBER.win_x64_jre11.release.zip" ./ffmpeg.exe

(sha1sum "BilibiliDown.v$VERSION_NUMBER.win_x64_jre11.release.zip"| cut -d' ' -f1) > "BilibiliDown.v$VERSION_NUMBER.win_x64_jre11.release.zip.sha1"
(sha1sum "BilibiliDown.v$VERSION_NUMBER.release.zip"| cut -d' ' -f1) > "BilibiliDown.v$VERSION_NUMBER.release.zip.sha1"

