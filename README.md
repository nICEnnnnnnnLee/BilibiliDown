# INeedBiliAV - BilibiliDown
**INeedBiliAV** 为Bilibili 视频下载器，用于下载B站视频。  
**INeedBiliAV**支持各种清晰度下载，但部分高清格式可能需要用户登录的Cookies。
**INeedBiliAV**提供UI操作界面，在安装了JDK环境下，只需双击运行程序即可。
**INeedBiliAV**封装了一些用于登录/下载的API，没有第三方库，可直接拿去使用。

[TOC]

## 简介
### 使用语言及外部库
* 该工程为纯Java编写；
* 使用自带的HttpUrlConnection做为网络连接，未用到Okhttp/HttpClient/HtmlUnit/Jsoup等外部库；
* 使用org.json库做简单的Json解析；
* 使用zxing库生成链接二维码图片。

### 核心功能实现
主要为**nicelee.bilibili**包下的三个类：  

| Module  | Description |
| ------------- | ------------- |
| INeedAV  | 实现了根据av号获取（所有）视频信息、下载视频等功能。（下载格式为flv，支持各种清晰度，支持Cookies）  |
| INeedAVbPhone  | 实现了根据av号获取（第一个）视频信息、下载视频等功能。（下载格式为mp4，清晰度无法保证）  |
| INeedLogin  | 实现了测试用户有效性、获取用户信息、扫码登录等功能，登录成功后保存至同级目录下的cookies.config中  |



### 工具类封装
工具放在为**nicelee.util**包下：

| Module            |  Description |
| ----------------- | ----------- |
| HttpCookies       |  提供String与HttpCookieList的转换，保存有一个静态的全局Cookies，用于全局的Cookie存取（非线程安全，请注意） |
| HttpHeaders       |  用于各类Http请求的头部生成 |
| HttpRequestUtil   |  用于实现各种Get/Post类型的Http请求，并对下载功能做了封装 |
| QrCodeUtil        |  用于二维码图片的生成 |


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
该模式下偶尔会报http socket connect/read timeout异常，目前尚未处理，解决方案是再次进行尝试。
* 运行程序（java/javaw 均可）
``` dos
javaw -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
```
PS：不能直接双击jar文件运行，因为可能存在中文乱码，必须要设置file.encoding=utf-8。  
该JVM设置在启动前已经读取，代码里面再修改无效。  
一个解决办法是修改注册表：   


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

## UPDATE  
* v2.1 
    * 增加了断点续传的下载功能, 如果发现上次未下载完成的```.part```文件,会在上次的基础上继续进行下载;
    * (UI)新增暂停/下载异常后继续下载功能, 与断点续传功能相匹配;
    * (UI)在作品详情页面点击文字可以复制信息;
    * 考虑过把一个视频分成很多Fragment多线程下载的, 但这样似乎对服务器不是很好, 并且可以预见会有很多bug(这点最重要??), 再加上本身已经能够同时下载多个不同的av, 故而并没有继续;
    * 考虑过使用HTML5的播放源, ```.m3u8```的直接合并就行, 但像这种```.m4s```,木有经验额??. 如何解析报头的**SegmentBase**, 如何合并音视频, 目前正在潜水学习中... 关键是没有多媒体处理经验,不会ffmpeg
    * 附: 部分视频信息, 有会的请吱一声
```
{
    "duration": 83,
        "minBufferTime": 1.5,
        "video": [{
        ...
        
        "id": 80,
        "baseUrl": "http://upos-hz-mirrorkodou.acgvideo.com/upgcxcode/42/19/78601942/78601942-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEVEuxTEto8i8o859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_\u0026deadline=1551413836\u0026gen=playurl\u0026nbs=1\u0026oi=3728813686\u0026os=kodou\u0026platform=pc\u0026trid=28f5dc24eae8486794b7ce58e2887103\u0026uipk=5\u0026upsig=5d9bde6c9ab8aba0875b3262cd741050",
        "backupUrl": null,
        "bandwidth": 2442010,
        "mimeType": "video/mp4",
        "codecs": "avc1.640028",
        "width": 1920,
        "height": 1080,
        "frameRate": "25",
        "sar": "1:1",
        "startWithSap": 1,
        "SegmentBase": {
            "Initialization": "0-975",
            "indexRange": "976-1211"
        },
        "codecid": 7
    }],
    "audio": [{
        "id": 30280,
        "baseUrl": "http://upos-hz-mirrorkodou.acgvideo.com/upgcxcode/42/19/78601942/78601942-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEVEuxTEto8i8o859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_\u0026deadline=1551413836\u0026gen=playurl\u0026nbs=1\u0026oi=3728813686\u0026os=kodou\u0026platform=pc\u0026trid=28f5dc24eae8486794b7ce58e2887103\u0026uipk=5\u0026upsig=449ec944995095c74727a44392c704ec",
        "backupUrl": null,
        "bandwidth": 124909,
        "mimeType": "audio/mp4",
        "codecs": "mp4a.40.2",
        "width": 0,
        "height": 0,
        "frameRate": "",
        "sar": "",
        "startWithSap": 0,
        "SegmentBase": {
            "Initialization": "0-919",
            "indexRange": "920-1155"
        },
        "codecid": 0
    }
```
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




