# INeedBiliAV - BilibiliDown
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache-2.0.svg)

**INeedBiliAV** 为Bilibili 视频下载器，用于下载B站视频。  
**以下多图警告**
===============================
## 特性  
+ 支持UI界面(自认为是傻瓜式操作)  
+ 支持扫码登录(如果要下高清, 不必自己扣cookies)  
+ 支持各种链接解析(直接上avXXX/epXXX/ssXXX/epXXX，以及各种网页链接)
+ 支持多p下载!(看了看部分别人的作品, 听说有的只支持单p?)  
+ 支持收藏夹下载!!  
+ 支持UP主视频下载!!!  
+ 支持长视频(例如电影)下载!!!!  
+ 支持断点续传下载!!!!!(因异常原因退出后, 只要下载目录不变, 直接在上次基础上继续下载)
   
## 使用方法
* 安装(可选)  
其实这是一款绿色软件，安装只是创建了一个快捷方式。。。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/install.gif)  
* 扫码登录(可选)   
点击主界面右上角登录按钮，在手机端使用哔哩哔哩app扫描弹出的二维码  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/login.gif)
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/qrcode-login.png)
* 获取作品信息  
在主界面搜索框输入av号或者av播放链接，点击右方按钮查找  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/index.png)
* 下载  
在作品信息界面点击想要的视频清晰度进行下载  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/avDetails.png)  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/download.png)
预览
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/download.gif)
* 其它  
关闭作品信息页面 - 双击Tab标签（单击Tab标签为切换焦点）  
复制作品信息 - 在作品Tab页单击想要复制的目标文字   
修改下载视频格式 - 在```app.config```中配置```bilibili.format```选项    
批量重命名 - 找到下载目录中的```rename.bat```，双击它   
卸载 - 找到下载目录中的```unistall.bat```，双击它(仅仅只是删除了文件夹)   


## 配置
在```config/app.config```中，可以不用管，使用默认配置即可 
```
# 0: MP4 1:FLV
bilibili.format = 0

#下载文件保存路径， 可以是相对路径，也可以是绝对路径
bilibili.savePath = download/
#bilibili.savePath = D:\Workspace\bilibili\

#最大的同时下载任务数
bilibili.download.poolSize = 3
```

## 第三方库使用声明
* 使用org.json库做简单的Json解析；
* 使用zxing库生成链接二维码图片；
* 使用ffmpeg进行转码(短片段flv未使用ffmpeg，仅多flv合并及m4s转换mp4格式需要用到)

## Win32/Linux/Mac用户请看过来
+ 自带的```ffmpeg.exe```为WIN 64位，32位系统或其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序；  
+ 对于非WIN用户，请直接使用命令行调用该程序  
```javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar```

## UPDATE  
* v2.6  
    * 解决部分视频下载不完整问题 - 发现电影是分段播的，原来的方式只能下载大概前5~6分钟，例如<https://www.bilibili.com/bangumi/play/ss10007>
    * 因为不怎么使用登录功能(一次登录cookies可以用很久)，以前未发现并处理因为网络原因造成的异常，现已解决
* v2.5  
    * 增加收藏夹的连接解析，例如<https://space.bilibili.com/3156365/favlist?fid=75463865>(url请务必包含fid参数)
    * 增加UP主个人页面的链接解析，例如<https://space.bilibili.com/5276/video> 
    * 增加UP主个人页面特定频道的链接解析，例如<https://space.bilibili.com/378034/channel/detail?cid=189>     
    * 修复某些链接的异常解析，例如<https://www.bilibili.com/video/av41515020?from=search&seid=11021327663579949519>
    * 增加卸载脚本```unistall.bat```(其实就是删除文件夹。。。)
    * 当前个人页面获取信息顺序为最新发布，且固定为每页5个(官网每页30个)，以防止跳出的Tab页面过多。想要获取请在后面加上p=[pageNumber]。
    e.g. 想要下载UP主```Hivane```最新发布的第31~35个视频(需确保后面没有空格)    
    ```https://space.bilibili.com/17154307/video?tid=0&page=2&keyword=&order=pubdate p=7```
* v2.4  
    * 增加官方番剧URL集合的链接解析，例如<https://www.bilibili.com/bangumi/play/ss25739> 
    * 增加官方番剧URL集合的链接解析，例如<https://www.bilibili.com/bangumi/media/md134912>     
    * (UI)优化最大同时下载数的显示
    * (UI)修复并优化部分UI显示
* v2.3 
    * (UI)增加下载速度显示   
    * (UI)新增vbs脚本，可以创建桌面快捷方式  
    * (UI)配置文件统一移入config文件夹中
    * 当选择为MP4而目标源仅存在FLV时，优化为自动切换FLV  
    * 修复一个bug，该bug使得在调用ffmpeg转码时，有概率会失败卡住  
    * 增加官方番剧URL(单集)的链接解析，例如<https://www.bilibili.com/bangumi/play/ep250435>  
    * 增加下载番剧的功能(以前一直测试的UP主上传的视频，突然发现追番的话似乎有点不同，于是增加了这个功能)       
    * PS： ep号转av号是直接从HTML里面爬出来的，不够优雅，暂时还没提取出提供api接口...
* v2.2 
    * 增加了HTML5播放源的下载方式,支持FLV/MP4两种格式 
    * (UI)下载面板优化为不允许存在相同的视频下载任务(不分辨清晰度)
    * (UI)在下载目录下增加了重命名```rename.bat```，默认格式为```avId-qn-p.(flv|mp4)```，可以使用该批处理批量改标题为```视频标题-qn-p.(flv|mp4)```(重命名功能暂只支持Windows)
    * (UI)增加了下载格式配置(flv 取Flash播放源, mp4 取HTML5播放源)
    * (UI)增加了下载路径配置
* v2.1 
    * 增加了断点续传的下载功能, 如果发现上次未下载完成的```.part```文件,会在上次的基础上继续进行下载;
    * (UI)新增暂停/下载异常后继续下载功能, 与断点续传功能相匹配;
    * (UI)在作品详情页面点击文字可以复制信息;
    * 考虑过把一个视频分成很多Fragment多线程下载的, 但这样似乎对服务器不是很好, 并且可以预见会有很多bug(这点最重要??), 再加上本身已经能够同时下载多个不同的av, 故而并没有继续;
    * 考虑过使用HTML5的播放源, ```.m3u8```的直接合并就行, 但像这种```.m4s```,木有经验额??. 如何解析报头的**SegmentBase**, 如何合并音视频, 目前正在潜水学习中... 关键是没有多媒体处理经验,不会ffmpeg
    
* v2.0 
    * 修复一个bug,该bug导致部分无效cookies验证抛出异常,而不是返回false;
    * (UI)修复一个bug,该bug使得扫码登录后未能及时更新用户头像等信息;
    * (UI)增加二维码扫码时限性,一分钟后自动销毁;
    * (UI)增加登录框点击动态效果,让人明白你点了它;
    * 下载flv名称由 ```avId-p.flv``` 改为```avId-qn-p.flv```,增加清晰度标识
    * (UI)优化了.bat脚本,```run-UI.bat```运行后命令窗口退出,```run-UI-debug.bat```运行后命令窗口留存,并且可查看输出信息
## 其它  
* **下载地址**: [https://github.com/nICEnnnnnnnLee/BilibiliDown/releases](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)  
* **Gitee码云**: [https://gitee.com/NiceLeee/BilibiliDown](https://gitee.com/NiceLeee/BilibiliDown)  
* **LICENSE**: [Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0.html)




