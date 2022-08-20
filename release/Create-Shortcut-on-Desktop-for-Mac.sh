#!/bin/bash

# cd 到脚本所在目录
cd $(dirname $0)
base_path=$(pwd)
echo "当前路径为${base_path}"

current_user=$(whoami)
echo "当前用户为${current_user}"

desktop_path="/Users/${current_user}/Desktop"
if [ ! -d desktop_path ]; then
    desktop_path="/Users/${current_user}/桌面"
fi
echo "当前桌面路径为${desktop_path}"


if test ! -x ${base_path}/Double-Click-to-Run-for-Mac.command ; then
    echo '为双击运行脚本添加可执行权限'
    sudo chmod +x ${base_path}/Double-Click-to-Run-for-Mac.command
fi

echo '在桌面上创建快捷方式软链接'
ln -s ${base_path}/Double-Click-to-Run-for-Mac.command ${desktop_path}/BilibiliDown.link


