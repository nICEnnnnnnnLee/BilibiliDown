# 文件结构说明
以下为程序的全量的工作目录结构，把可能出现的文件基本上都列了出来
```
-
|-- BilibiliDown.desktop                     Linux下创建的快捷方式模板(不存在的话会由脚本创建)
|-- Create-Shortcut-on-Desktop-for-Linux.sh  Linux下创建快捷方式的脚本
|-- Create-Shortcut-on-Desktop-for-Mac.sh    MacOS下创建快捷方式的脚本
|-- Create-Shortcut-on-Desktop-for-Win.vbs   Windows下创建快捷方式的脚本
|-- Double-Click-to-Run-for-Mac.command      MacOS下双击即可运行
|-- Double-Click-to-Run-for-Win-debug.bat    Windows下双击即可运行(带命令行输出)
|-- Double-Click-to-Run-for-Win.bat          Windows下双击即可运行(纯视图)
|-- Run-for-Linux.sh                         Linux下的运行脚本
|-- ffmpeg.exe                               Windows下的ffmpeg
|-- INeedBiliAV.jar                          主程序
|-- launch.jar                               启动程序, 目的是能够不借助脚本实现jar包自升级
|-- uninstall.bat                            Windows下卸载脚本(其实就是删除所在文件夹)
|-- update.bat                               Windows下更新脚本(程序调用+双击皆可)
|-- update.sh                                Linux/Mac下的更新脚本(不存在的话会由程序创建)
|-- app                                      Windows下，安装包自动生成。告诉`exe`文件如何正确运行
|   |-- .package                             用于指示程序名称
|   |-- BilibiliDown.cfg                     用于指示JVM运行的相关配置
|-- config                                   配置文件夹
|   |-- app.config                           主要配置
|   |-- background.jpg                       自定义背景图片(jpg、png均可)
|   |-- batchDownload.config                 一键下载配置
|   |-- cookies.config                       登录凭证(明文保存Cookies)
|   |-- favicon.ico                          快捷方式显示的程序图标
|   |-- fingerprint.config                   登录时随机生成的指纹
|   |-- notice.wav                           自定义下载完成后的提示音
|   |-- repo.config                          完成的下载任务记录(仅记录ID,一行一个)
|   |-- tasks.config                         保存的正在下载中的任务
|-- download                                 默认的视频保存位置
|-- LICENSE                                  LICENSE文件夹
|-- minimal-bilibilidown-jre                 Windows下的精简JRE 11(压缩包版本)
|-- runtime                                  Windows下的精简JRE 11(安装包版本)
|-- parsers                                  自定义解析器文件夹
|   |-- EPParser.class                       .class是程序自动编译的
|   |-- EPParser.java                        .java需要自己写, 可以参考一下同名源代码
|   |-- MdParser.class
|   |-- MdParser.java
|   |-- parsers.ini                          如果存在，可以指定ClassLoader的加载内容和顺序
|   |-- SSParser.class
|   |-- SSParser.java
|-- pushers                                  自定义消息推送器文件夹（用于推送周期性下载的结果）
|-- update                                   一键更新的下载内容，看名字懂意思
    |-- BilibiliDown.v6.8.release.zip
    |-- BilibiliDown.v6.9.release.zip
    |-- INeedBiliAV.update.jar               一键更新脚本会把这个替换掉旧版本的主程序
```
    