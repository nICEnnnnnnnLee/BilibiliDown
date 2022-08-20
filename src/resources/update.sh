#!/bin/bash

update(){
    echo "等待程序关闭"
    sleep 3

    #  如果传入pid，且进程仍然存在，那么强制终止该进程
    if [ $2 ]; then
        echo "终止进程: pid$2"
        kill -9 $2
    fi


    # 复制文件(不提示直接覆盖)
    cp -f update/INeedBiliAV.update.jar INeedBiliAV.jar
    if test "$1" = "1" 
    then
        echo "更新后重启"
        java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
    else
        echo "仅更新"
    fi
}


if test "$1" = "@1" 
then
    nohup ./update.sh 1 $2 >$3 2>&1 &
else
    update $*
fi