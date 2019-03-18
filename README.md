# INeedBiliAV - BilibiliDown
**INeedBiliAV** 为Bilibili 视频下载器，用于下载B站视频。  
**INeedBiliAV** 支持各种清晰度下载，但部分高清格式可能需要用户登录的Cookies。   
**INeedBiliAV** 提供UI操作界面，在安装了JRE(java version "1.8.0_101")环境下，只需双击运行程序即可。   
**INeedBiliAV** 封装了一些用于登录/下载的API，无需导入第三方依赖(org.json以```.java```形式存在)，API可直接拿去使用。   

[TOC]

## 简介
### 使用语言及外部库
* 该工程为纯Java(java version "1.8.0_101")编写；
* 使用自带的HttpUrlConnection做为网络连接，未用到Okhttp/HttpClient/HtmlUnit/Jsoup等外部库；
* 使用org.json库做简单的Json解析；
* 使用zxing库生成链接二维码图片；
* 使用ffmpeg进行转码(flv格式未使用ffmpeg，仅mp4格式需要用到，这两者清晰度是一致的)
* 支持**收藏夹/UP主视频列表**解析

### 核心功能实现
主要为**nicelee.bilibili**包下的三个类：  

| Module  | Description |
| ------------- | ------------- |
| INeedAV  | 实现了根据av号获取（所有）视频信息、下载视频等功能。（下载格式为flv/mp4，支持各种清晰度，支持Cookies）  |
| INeedAVbPhone  | 实现了根据av号获取（第一个）视频信息、下载视频等功能。（下载格式为mp4，清晰度无法保证，弃用）  |
| INeedLogin  | 实现了测试用户有效性、获取用户信息、扫码登录等功能，登录成功后保存至同级目录下的cookies.config中  |



### 工具类封装
工具放在为**nicelee.util**包下：

| Module            |  Description |
| ----------------- | ----------- |
| HttpCookies       |  提供String与HttpCookieList的转换，保存有一个静态的全局Cookies，用于全局的Cookie存取（非线程安全，请注意） |
| HttpHeaders       |  用于各类Http请求的头部生成 |
| HttpRequestUtil   |  用于实现各种Get/Post类型的Http请求，并对下载功能做了封装 |
| QrCodeUtil        |  用于二维码图片的生成 |
| ConfigUtil        |  用于从```app.config```读取配置 |
| CmdUtil        |  调用```ffmpeg.exe```将下载的音视频```.m4s```文件合并转成MP4(若配置为FLV且未分段，则未使用该工具，平台通用性更好) |

注： 自带的```ffmpeg.exe```为WIN 64位，32位系统或其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序

## 使用方法：
### 命令行模式
该模式比较粗糙，将就着用o((>ω< ))o。。。
* 利用二维码扫码登录获取cookies（生成了qrcode.jpg，登录后保存到cookies.config）
``` dos
java -cp INeedBiliAV.jar nicelee.bilibili.INeedLogin 1
```

* 测试cookies的有效性（读取cookies.config）
``` dos
java -cp INeedBiliAV.jar nicelee.bilibili.INeedLogin 0
```

* 下载当前cookie所能下载的最清晰链接
``` dos
java -cp INeedBiliAV.jar nicelee.bilibili.INeedAV "av40877923" 
```

### UI模式
#### 配置
可以不用管，使用默认配置即可 
```
# 0: MP4 1:FLV
bilibili.format = 0

#下载文件保存路径， 可以是相对路径，也可以是绝对路径
bilibili.savePath = download/
#bilibili.savePath = D:\Workspace\bilibili\

#最大的同时下载任务数
bilibili.download.poolSize = 3
```

* 直接双击```run-UI.bat``` 或```run-UI-debug.bat```  
* 脚本原理（java/javaw 均可）
``` dos
javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
```
PS：不能直接双击jar文件运行，因为可能存在中文乱码，必须要设置file.encoding=utf-8。  
该JVM设置在启动前已经读取，代码里面再修改无效。  
* 如果想直接双击jar运行，一个解决办法是修改注册表：   


| 属性  | 值 |
| ------------- | ------------- |
| 位置  | HKEY_CLASSES_ROOT\Applications\javaw.exe\shell\open\command  |
| 原始值  |"...\bin\javaw.exe" -jar "%1"  |
| 现有值  | "...\bin\javaw.exe" -Dfile.encoding=utf-8 -jar "%1"  |
   

* 扫码登录(可选)   
点击主界面右上角登录按钮，在手机端使用哔哩哔哩app扫描弹出的二维码  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/qrcode-login.png)
* 获取作品信息  
在主界面搜索框输入av号或者av播放链接，点击右方按钮查找  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/index.png)
* 下载  
在作品信息界面点击想要的视频清晰度进行下载  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/avDetails.png)  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/download.png)
* 其它  
关闭作品信息页面 - 双击Tab标签（单击Tab标签为切换焦点）  
复制作品信息 - 在作品Tab页单击想要复制的目标文字   
修改下载视频格式 - 在```app.config```中配置```bilibili.format```选项    
批量重命名 - 找到下载目录中的```rename.bat```，双击它   
安装 - 找到下载目录中的```install.vbs```，双击它(仅仅只是创建了快捷方式)   
卸载 - 找到下载目录中的```unistall.bat```，双击它(仅仅只是删除了文件夹)   

## UPDATE  
* master  
    * 解决部分视频下载不完整问题 - 发现电影是分段播的，原来的方式只能下载大概前5~6分钟，例如<https://www.bilibili.com/bangumi/play/ss10007>
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




