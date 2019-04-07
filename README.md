# INeedBiliAV - BilibiliDown
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache--2.0-green.svg)  


Bilibili 视频下载器，用于下载B站视频。  
===============================
**以下多图警告**

## :smile:特性  
+ 支持UI界面(自认为是傻瓜式操作)  
+ 支持扫码登录(能看=能下，反过来也一样)  
+ 支持各种链接解析(直接输入avXXX/epXXX/ssXXX/mdXXX，或者各种网页链接)
+ 支持多p下载!(看了看部分别人的作品, 听说有的只支持单p?)  
+ 支持收藏夹下载!!  
+ 支持UP主视频下载!!!  
+ 支持长视频，杜绝片头式下载!!!!(试金石av3248542)  
+ 支持断点续传下载!!!!!(因异常原因退出后, 只要下载目录不变, 直接在上次基础上继续下载)
   
## :smile:使用方法
* 安装(可选)  
其实这是一款绿色软件，安装只是创建了一个快捷方式。。。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/install.gif)  

* 扫码登录(可选)   
点击主界面右上角登录按钮，在手机端使用哔哩哔哩app扫描弹出的二维码  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/login.gif)  

* 下载  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/download.gif)  

* 其它  
关闭作品信息页面 - 双击Tab标签（单击Tab标签为切换焦点）  
复制作品信息 - 在作品Tab页单击想要复制的目标文字   
修改下载视频格式 - 在```app.config```中配置```bilibili.format```选项    
批量重命名 - 找到下载目录中的```rename.bat```，双击它   
卸载 - 找到下载目录中的```unistall.bat```，双击它(仅仅只是删除了文件夹)   
修改配置 - 编辑```config/app.config```即可

## :smile:第三方库使用声明
* 使用org.json库做简单的Json解析；
* 使用zxing库生成链接二维码图片；
* 使用ffmpeg进行转码(短片段flv未使用ffmpeg，仅多flv合并及m4s转换mp4格式需要用到)

## :smile:Win32/Linux/Mac用户请看过来
+ 自带的```ffmpeg.exe```为WIN 64位，32位系统或其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序；  
+ 对于非WIN用户，请直接使用命令行调用该程序  
```javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar```

## :smile:其它  
* **下载地址**: [https://github.com/nICEnnnnnnnLee/BilibiliDown/releases](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)  
* **Gitee码云**: [https://gitee.com/NiceLeee/BilibiliDown](https://gitee.com/NiceLeee/BilibiliDown)  
* **LICENSE**: [Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0.html)
* [**更新日志**](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/UPDATE.md)