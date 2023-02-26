#!/bin/bash

# cd 到脚本所在目录
cd $(dirname $0)
base_path=$(pwd)
echo "当前路径为${base_path}"

current_user=$(whoami)
echo "当前用户为${current_user}"

desktop_path="/home/${current_user}/desktop"
if [ ! -d desktop_path ]; then
    desktop_path="/home/${current_user}/桌面"
fi
echo "当前桌面为${desktop_path}"

echo '创建运行脚本并添加可执行权限'
echo "#!/bin/bash" > Run-for-Linux.sh
echo "" >> Run-for-Linux.sh
echo 'cd $(dirname $0)' >> Run-for-Linux.sh
echo "java -Dfile.encoding=utf-8 -Dhttps.protocols=TLSv1.2 -jar launch.jar" >> Run-for-Linux.sh
sudo chmod +x Run-for-Linux.sh

echo "[Desktop Entry]" > BilibiliDown.desktop
echo "Encoding=UTF-8" >> BilibiliDown.desktop
echo "Terminal=false" >> BilibiliDown.desktop
echo "Name=BilibiliDown" >> BilibiliDown.desktop
echo "Type=Application" >> BilibiliDown.desktop
echo "Exec=bash ${base_path}/Run-for-Linux.sh" >> BilibiliDown.desktop
echo "Icon=${base_path}/config/favicon.ico" >> BilibiliDown.desktop
echo "Categories=Application;Network; " >> BilibiliDown.desktop
echo "Comment=BilibiliDown from https://github.com/nICEnnnnnnnLee/BilibiliDown" >> BilibiliDown.desktop

# cat BilibiliDown.desktop
# chmod +x BilibiliDown.desktop
echo '在桌面上创建快捷方式'
cp -f ${base_path}/BilibiliDown.desktop ${desktop_path}/BilibiliDown.desktop

if test ! -x ${desktop_path}/BilibiliDown.desktop ; then
    echo '为桌面快捷方式添加可执行权限'
    sudo chmod +x ${desktop_path}/BilibiliDown.desktop
fi


echo '在启动栏创建快捷方式'
sudo cp -f ${base_path}/BilibiliDown.desktop /usr/share/applications/BilibiliDown.desktop
if test ! -x /usr/share/applications/BilibiliDown.desktop ; then
    echo '为启动栏快捷方式添加可执行权限'
    sudo chmod +x /usr/share/applications/BilibiliDown.desktop
fi


