# INeedBiliAV - BilibiliDown
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache--2.0-green.svg)  
![当前版本](https://img.shields.io/github/release/nICEnnnnnnnLee/BilibiliDown.svg?style=flat-square)
![Release 下载总量](https://img.shields.io/github/downloads/nICEnnnnnnnLee/BilibiliDown/total.svg?style=flat-square)
![CI](https://github.com/nICEnnnnnnnLee/BilibiliDown/workflows/CI/badge.svg)
![最近更新](https://img.shields.io/github/last-commit/nICEnnnnnnnLee/BilibiliDown.svg?style=flat-square&color=FF9900)

Bilibili 视频下载器，用于下载B站视频。  
===============================
**以下多图警告**

## :smile:特性  
- [x] 支持UI界面(自认为是傻瓜式操作)  
- [x] 支持扫码/密码登录(能看=能下，反过来也一样)  
- [x] 支持各种链接解析(直接输入BVXXX/avXXX，或者各种网页链接， V4.3 新增画廊/相簿解析)**不再支持番剧解析！！**
- [x] **支持自定义各种链接解析**(V5.8新增)
- [x] 支持收藏夹下载  
- [x] 支持稍后再看下载  
- [x] 支持UP主视频/频道下载  
- [x] 支持断点续传下载(因异常原因退出后, 只要下载目录不变, 直接在上次基础上继续下载)
- [x] 支持CC字幕下载  
- [x] 支持互动视频下载  
- [x] 支持课程视频下载  
- [x] 支持相簿图片下载
   
## :smile:关于下载速度     
+ 默认最大同时下载数为3，有需要可以在`config/app.config`更改(不推荐)  
+ 关于下载速度，和下载任务数、配置、网络条件等相关，直接上截图  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/download-speed.png)  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/download-speed2.png)  
   
## :smile:使用方法
<details>
<summary>安装(可选)</summary>


其实这是一款绿色软件，安装只是创建了一个快捷方式。。。  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/install.gif)  
</details>

<details>
<summary>密码登录(可选)</summary>


+ 默认为扫码登录，使用该功能需要在`config/app.config`中更改配置项  
`bilibili.user.login = pwd`  
+ 程序打开后，点击主界面右上角登录按钮即可  
</details>

<details>
<summary>扫码登录(可选)</summary>


+ 如果当前为密码登录，可在`config/app.config`中更改配置项  
`bilibili.user.login = qr`  
+ 点击主界面右上角登录按钮，在手机端使用哔哩哔哩app扫描弹出的二维码  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/login.gif) 
</details>

<details>
<summary>普通下载</summary>



![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/download.gif)  
</details>

<details>
<summary>批量下载</summary>


<details>
<summary>下载单个标签页全部视频</summary>


+ `单击标签` -> `选择优先清晰度` -> `点击批量下载`  
+ `右键单击标签` -> `批量下载此标签`  
    + 默认优先清晰度为`1080P`, 有需要请修改`bilibili.tab.download.qn`
</details>
<details>
<summary>下载全部标签页的全部视频</summary>


+ 右上角菜单栏`操作` -> `批量下载Tab页`  
    + 优先清晰度(仅适用于菜单栏操作)： 右上角菜单栏`配置` -> `优先清晰度`
    + 默认优先清晰度为`1080P`, 有需要请修改`bilibili.menu.download.qn`
+ `右键单击标签` -> `批量下载全部标签`  
    + 默认优先清晰度为`1080P`, 有需要请修改`bilibili.tab.download.qn`
</details>
</details>

<details>
<summary>下载封面</summary>


单击封面图片获取URL。  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/ClickPicToGetUrl.gif)  
</details>

<details>
<summary>更换预览图(适用于单页呈现多部作品的情况)</summary>


长按作品文字，然后对应预览图会更新(网络原因可能会有卡顿，正常)。  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/LongClickToChangePreview.gif)  
</details>

<details>
<summary>获取作品信息(适用于单页呈现多部作品的情况)</summary>


双击作品文字，然后文本 + `avId`将会复制到剪贴板。  
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/doubleClick2CopyClipInfo.gif)  
</details>

<details>
<summary>版本升级</summary>


`关于` -> `更新版本`。(最近版本比较多，省得麻烦😳 目前只是人工触发版本检查。  
不是WINDOWS的话需要人工`update/INeedBiliAV.update.jar`替换掉`INeedBiliAV.jar`( ╯□╰ )   
![](https://cdn.jsdelivr.net/gh/nICEnnnnnnnLee/BilibiliDown@master/release/preview/AutoUpdate.gif)  
</details>
<details>
<summary>其它</summary>


* 关闭作品信息页面
    * 双击Tab标签（单击Tab标签为切换焦点）  
* 复制作品信息
    * 在作品Tab页单击想要复制的目标文字   
* ~~修改优先下载的视频格式~~[V4.6取消]
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
| HDR  | 125 |
| 4K  | 120 |
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

<details>
<summary>关于互动视频说明</summary>

+ 仅支持单个互动视频下载。  
  当从收藏夹/UP主链接查询时，仅下载互动视频的首片段。  

+ 输入互动视频av号查询时，将会罗列所有世界线的全部视频片段，同时也支持这些片段的下载。  
片段title名称规则为：  
`i.j-上次做出的选择`   
其中，i为第i条世界线；j为世界线的第j+1个视频  

+ 举例 av64006660
```
【互动式视频】史蒂夫的故事，你决定Steve的命运《我的世界》
该互动视频包含5个片段：
    pn0-起始
    pn1-0.1-A 造个木剑防御怪物
    pn2-1.1-B 造个木镐开始挖矿_2.1-B 造个木镐开始挖矿
    pn3-1.2-A 先造个庇护所
    pn4-2.2-B 先种树再造房

以上有3条世界线，观看顺序为：  
    世界线0： pn0 -> pn1
    世界线1： pn0 -> pn2 -> pn3
    世界线2： pn0 -> pn2 -> pn4
```
</details>

<details>
<summary>关于字幕的说明</summary>

+ 本程序的逻辑认为字幕是一种特别清晰度的视频，而基于程序**不会同时下载同一视频的不同清晰度的链接的Feature**，  
    + 当下载面板存在视频任务时，其对应的字幕下载任务不会被发起
    + 当下载面板存在字幕任务时，其对应的视频下载任务不会被发起
    + 如有需要，请在下载面板删除对应任务后再继续尝试
+ 如果存在多语种，默认优先下载中文简体`zh-CN`  
+ 如有需要，可在`app.config`更改配置，如：    
```
bilibili.cc.lang = en-US
```  
+ 关于语言的配置，详见[此处](/release/wiki/langs.txt)  
</details>

<details>
<summary>关于自定义背景图片</summary>

+ 将`background.jpg`或`background.png`放在`config`文件夹下即可  
+ 推荐背景图[b站壁纸娘 - 22&33](https://h.bilibili.com/597708)  
+ 推荐背景图[唧唧看板娘——唧娜](https://blog.jixiaob.cn/?post=14)  
</details>

<details>
<summary>关于自定义各种链接解析</summary>

+ 一般来说，将各种实现的`xxxParser.java`放在`parsers`文件夹下即可  
+ 如果自定实现的类与已有的是相同名称，请确保它在已有的类之前加载。  
而为了实现这个，需要指定加载顺序，需要在`parsers`文件夹下创建配置`parsers.ini`。举例：   
```
假设你自定义实现了BVParser 和 B23Parser，其中B23Parser继承BVParser。  
如果先尝试加载B23Parser，那么它在加载时会搜寻其父类，于是会找到现有的BVParser。  
其结果是你自定义的BVParser没用了，自定义的B23Parser也可能因此而存在逻辑问题。  
解决方案是，在parsers文件夹下创建parsers.ini，将顺序指定，其内容为：
BVParser
B23Parser
```
</details>

## :smile:第三方库使用声明  
* 账号密码登录实现参考了[Bilibili-Toolkit](https://github.com/Hsury/Bilibili-Toolkit)[![](https://img.shields.io/badge/license-SATA-green.svg)](https://github.com/Hsury/Bilibili-Toolkit/blob/master/LICENSE)  
* AV和BV转换参考了[AV-BV-Convert](https://github.com/CCRcmcpe/AV-BV-Convert)[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/CCRcmcpe/AV-BV-Convert/blob/master/LICENSE)  
* 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
* 使用[zxing](https://github.com/zxing/zxing)库生成链接二维码图片[![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://raw.githubusercontent.com/zxing/zxing/master/LICENSE)  
* 以外部库的方式调用[ffmpeg](http://www.ffmpeg.org)进行转码(短片段flv未使用ffmpeg，仅多flv合并及m4s转换mp4格式需要用到)[![](https://img.shields.io/badge/license-depends-orange.svg)](http://www.ffmpeg.org/legal.html)  
* 内置hosts文件使用了[GithubHost](https://github.com/ButterAndButterfly/GithubHost)[![](https://img.shields.io/badge/license-SATA-green.svg)](https://github.com/ButterAndButterfly/GithubHost/blob/master/LICENSE.txt)  
* ~~将要使用[Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)(先占坑，但不一定啥时候哦)~~`V4.2去除`实时弹幕相关可以参考[弹幕点歌姬](https://github.com/nICEnnnnnnnLee/DanmuMusicPlayer)  
* 图形验证码识别API调用了[https://bili.dev:2233/captcha](https://github.com/Hsury/Bilibili-Toolkit/#图形验证码识别api) 暂时失效，具体看情况  

## :smile:Linux/Mac用户请看过来  
+ Mac用户可参考[简单说下如何在mac下跑起来](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/32)  
+ ffmpeg环境设置参考[issues #15](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/15#issuecomment-536194416)  
+ 自带的```ffmpeg.exe```为`Win10+Msys+MingW+msvc`自行编译，其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序；  
+ 对于非WIN用户，请直接使用命令行调用该程序  
```java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar```
+ 对于非WIN用户，如需使用程序的一键更新功能，请在程序操作完毕并退出后，人工`update/INeedBiliAV.update.jar`替换掉`INeedBiliAV.jar`

## :smile:其它  
* **下载地址**: [https://github.com/nICEnnnnnnnLee/BilibiliDown/releases](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)  
* **Gitee码云**: [https://gitee.com/NiceLeee/BilibiliDown](https://gitee.com/NiceLeee/BilibiliDown)  
* [**更新日志**](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/UPDATE.md)


## :smile:LICENSE  
+ [第三方LICENSE](https://github.com/nICEnnnnnnnLee/BilibiliDown/tree/master/release/LICENSE/third-party)  
+ 本项目提供的`ffmpeg.exe`源码[在此](https://gitee.com/NiceLeee/FFmpeg/)，编译命令如下：  
```
// 为了能够编译成功，注释掉了cmdutils.c中CC_IDENT所在的第1156行
./configure --toolchain=msvc --arch=x86 --enable-yasm --enable-asm --disable-debug --disable-doc --disable-ffplay --disable-ffprobe --enable-static --disable-shared --disable-network --disable-autodetect --disable-decoders --disable-gpl --disable-version3 --enable-decoder='h264,aac*,mp3*,mp4' --disable-encoders --disable-demuxers --enable-demuxer='concat,mov,m4v,flv,mp3' --disable-muxers --enable-muxer='flv,mp4,mp3' --enable-encoder='libmp3lame,mp3' --disable-parsers --enable-parser=h264 --disable-protocols --enable-protocol='concat,file' --disable-bsfs --enable-bsf='h264_metadata,h264_mp4toannexb' --disable-filters --enable-filter='concat,aresample' --disable-iconv --enable-small
make
```

+ 本项目遵守开源协议`Apache 2.0`。  
为了分发的便利，历史版本可能直接使用过其它编译版本ffmpeg.     
当协议不兼容时，具体情况请以使用的ffmpeg版本为准（大概率是`GPL 3.0`）  
```
Copyright (C) 2019 NiceLee. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


