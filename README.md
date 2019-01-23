# INeedBiliAV - BilibiliDown
**INeedBiliAV** 为Bilibili 视频下载器，用于下载B站视频。  
**INeedBiliAV**支持各种清晰度下载，但部分高清格式可能需要用户登录的Cookies。

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


## 其它  
GitHub: [https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)  
Gitee码云: [https://gitee.com/NiceLeee/BilibiliDown](https://gitee.com/NiceLeee/BilibiliDown)  
LICENSE: [Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0.html)




