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
<details>
<summary>安装(可选)</summary>


其实这是一款绿色软件，安装只是创建了一个快捷方式。。。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/install.gif)  
</details>

<details>
<summary>扫码登录(可选)</summary>


点击主界面右上角登录按钮，在手机端使用哔哩哔哩app扫描弹出的二维码  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/login.gif) 
</details>

<details>
<summary>普通下载</summary>



![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/download.gif)  
</details>

<details>
<summary>批量下载</summary>


<details>
<summary>根据策略下载所有打开标签页的(全部/第一个)视频</summary>


![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/downloadAllTab.png) 
</details>
<details>
<summary>根据策略批量下载多p视频</summary>


![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/downloadSingleTab.png)  
</details>
</details>

<details>
<summary>下载封面</summary>


单击封面图片获取URL。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/ClickPicToGetUrl.gif)  
</details>

<details>
<summary>更换预览图(适用于单页呈现多部作品的情况)</summary>


长按作品文字，然后对应预览图会更新(网络原因可能会有卡顿，正常)。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/LongClickToChangePreview.gif)  
</details>

<details>
<summary>获取作品信息(适用于单页呈现多部作品的情况)</summary>


双击作品文字，然后文本 + `avId`将会复制到剪贴板。
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliDown/master/release/prelook/doubleClick2CopyClipInfo.gif)  
</details>

<details>
<summary>其它</summary>


* 关闭作品信息页面
    * 双击Tab标签（单击Tab标签为切换焦点）  
* 复制作品信息
    * 在作品Tab页单击想要复制的目标文字   
* 修改优先下载的视频格式
    * 在```config/app.config```中配置```bilibili.format```选项    
* 批量重命名
    * 找到下载目录中的`rename.bat`，双击它(`V3.1`增加自定义重命名，且支持下载完自动改名)   
* 卸载 
    * 找到下载目录中的```unistall.bat```，双击它(仅仅只是删除了文件夹)   
* 设置代理
    * 在```config/app.config```中配置相应代理类型的地址和端口    
* 修改其它配置
    * ```config/app.config```即可，详见文件见备注  
* 更多问题请见[Wiki](https://github.com/nICEnnnnnnnLee/BilibiliDown/wiki)
</details>

<details>
<summary>清晰度说明</summary>

当因权限不足，或视频不存在该清晰度时，将返回不大于该qn值的合法最大qn值对应的清晰度。
  
| 清晰度  | qn值 |
| ------------- | ------------- |
| 1080P60  | 116 |
| 1080P+  | 112 |
| 1080P  | 80 |
| 720P60  | 74 |
| 720P  | 64 |
| 480P  | 32 |
| 360P  | 16 |
* 举例
```
https://www.bilibili.com/video/av39405510
"accept_description": ["高清 1080P60", "高清 720P60", "高清 1080P", "高清 720P", "清晰 480P", "流畅 360P"],
"accept_quality": [116, 74, 80, 64, 32, 16]

https://www.bilibili.com/bangumi/play/ep116157/
"accept_description": ["高清 1080P+", "高清 1080P", "高清 720P", "清晰 480P", "流畅 360P"],
"accept_quality": [112, 80, 64, 32, 16]
```
* 举例，假设某av存在1080P+/1080P/720P/480P/360P，1080P+/1080P 需要大会员才能观看，720P需要登录才能观看。  
    * 无cookie 发起 ```1080P+``` 请求 =====>  得到```480P```链接  
    * 普通cookie 发起 ```1080P+``` 请求 =====>  得到```720P```链接  
    * 大会员cookie 发起 ```1080P+``` 请求 =====>  得到```1080P+```链接  
    * 大会员cookie 发起 ```720P60``` 请求 =====>  得到```720P```链接  
</details>



## :smile:第三方库使用声明  
* 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
* 使用[zxing](https://github.com/zxing/zxing)库生成链接二维码图片[![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://raw.githubusercontent.com/zxing/zxing/master/LICENSE)  
* 使用[ffmpeg](http://www.ffmpeg.org)进行转码(短片段flv未使用ffmpeg，仅多flv合并及m4s转换mp4格式需要用到)[![](https://img.shields.io/badge/license-LGPL%20(%3E%3D%202.1)%2FGPL%20(%3E%3D%202)-green.svg)](http://www.ffmpeg.org/legal.html)  

## :smile:媒体素材使用声明  
* [主页背景图](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/src/resources/loading.gif?raw=true)取自[b站壁纸娘 - 22&33](https://h.bilibili.com/597708)  
* [加载等待图](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/src/resources/loading.gif?raw=true)取自[数英 - Seven Dai](https://www.digitaling.com/articles/18383.html)  
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