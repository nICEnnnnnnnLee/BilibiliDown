# app.config


## bilibili.name.format
## bilibili.name.date.favTime.pattern
## bilibili.name.date.cTime.pattern
以上用于自定义下载文件名，详见[进阶使用-自定义文件名](/guide/advanced/custom-file-name)

## bilibili.format
以上用于指定优先下载的视频格式，详见[进阶使用-设置视频格式](/guide/advanced/media-type-format)

## bilibili.repo
## bilibili.repo.save
## bilibili.repo.definitionStrictMode
## bilibili.alert.isAlertIfDownloded
## bilibili.alert.maxAlertPrompt
以上用于视频是否下载的判断与提示，详见[常见问题-为什么总是弹出已下载？](/guide/frequently-asked/why-always-prompt)

## bilibili.name.doAfterComplete
- 取值范围: `true | false`
- 默认值:`true` 
- 释义:  
    当值为`true`时, 下载完成后马上重命名文件  
    当值为`false`时, 不会重命名文件, 但会输出一个`rename.bat`的脚本。此时文件名类似于`BV1Yt4y1x7Eh-80-p2.mp4`

## bilibili.download.thumbUp
- 取值范围: `true | false`
- 默认值: `false`
- 释义:  
    当值为`true`时, 下载完成后尝试给相关作品点赞👍(已经点赞的不会取消)  
    当值为`false`时, 不做任何操作


## bilibili.download.playSound
- 取值范围: `true | false`
- 默认值: `true`
- 释义:  
    当值为`true`时, 全部任务完成后播放提示音  
    当值为`false`时, 不做任何操作

## bilibili.download.maxFailRetry
- 取值范围: 数字
- 默认值: 3
- 释义:  
    当值大于0时, 为下载异常后尝试次数  
    当值等于0时, 下载异常后不再尝试

## bilibili.download.retry.reloadDownloadUrl
- 引入版本: V6.19
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    在下载任务失败重试时，是否重新获取下载链接。  
    [V6.32] 需要注意的是，如果url的存在时间超过阈值，不管该配置如何，都会重新获取下载链接。  
    

## bilibili.cc.lang
- 取值范围: 详见release/wiki/langs.txt
- 默认值: zh-CN
- 释义:  
    CC字幕优先下载语种

## bilibili.ffmpegPath
- 取值范围: 符合路径命名规范，不包含非法符号  
- 默认值: `ffmpeg`  
- 释义:  
    ffmpeg环境配置
- 举例:
    `bilibili.ffmpegPath = D:\Workspace\javaweb-springboot\BilibiliDown\ffmpeg.exe`


## bilibili.flv.ffmpeg
- 取值范围: 符合路径命名规范，不包含非法符号  
- 默认值: `false`  
- 释义:  
    当值为`true`时, 多个flv合并使用ffmpeg  
    当值为`false`时, 多个flv合并基于程序代码实现
- 相关issue
    + https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/31
    + 出问题的BV `BV1Ss411h7Ge-80-p7`

## bilibili.menu.download.plan
- 取值范围: `0 | 1`  
- 默认值: `1`  
- 释义:  
    当值为`0`时, 菜单里的`批量下载Tab页`只会下载每个Tab页的第一个视频  
    当值为`1`时, 菜单里的`批量下载Tab页`会下载每个Tab页的所有视频

## bilibili.menu.download.qn
- 取值范围: `8K/HDR/4K/1080P60/1080P+/1080P/720P60/720P/480P/320P`  
- 默认值: `1080P`  
- 释义:  
    通过右上角菜单、右键后的菜单批量下载时，优先下载的清晰度

## bilibili.tab.download.qn
- 取值范围: `8K/HDR/4K/1080P60/1080P+/1080P/720P60/720P/480P/320P`  
- 默认值: `1080P`  
- 释义:  
    通过Tab页里的按钮批量下载时，优先下载的清晰度

## bilibili.pageSize
- 取值范围: 数字 
- 默认值: `5`  
- 释义:  
    分页查询时，每页最大显示个数

## ~~bilibili.pageDisplay~~
- 取值范围: `promptAll | listAll`  
- 默认值: `listAll`  
- 释义:  
    ~~当值为`promptAll`时, 每个av会弹出一个Tab页~~ 该功能已不再维护  
    当值为`listAll`时, 所有选项在一个Tab页面里呈现

## bilibili.savePath
- 取值范围: 符合路径命名规范，不包含非法符号
- 默认值: `download/`
- 释义:  
    文件下载的保存目录，它是一个文件夹。  
    可以是相对路径，也可以是绝对路径。
- 举例:  
```
bilibili.savePath = download/
bilibili.savePath = D:\Workspace\bilibili\
bilibili.savePath = /home/user123/download/
```

## bilibili.download.poolSize
- 取值范围: 数字
- 默认值: `3`  
- 释义:  
    最大的同时下载任务数

## bilibili.download.period.between.download
- 取值范围: 数字
- 默认值: `0`  
- 释义:  
    每个下载任务完成后的等待时间(ms)

## bilibili.download.period.between.query
- 取值范围: 数字
- 默认值: `0`  
- 释义:  
    每个关于下载的查询任务完成后的等待时间(ms)

## bilibili.download.multiThread.count
- 取值范围: 数字
- 默认值: `0`  
- 释义:  
    下载文件时，分{count}部分同时下载。0 或 1 为不开启多线程下载。

## bilibili.download.multiThread.minFileSize
- 取值范围: 数字
- 默认值: `0`  
- 释义:  
    当文件大小小于{minFileSize} MB时，不开启多线程下载。  
    0 为不进行文件大小判断。

## bilibili.download.multiThread.singlePattern
- 取值范围: 正则匹配表达式
- 默认值: `github|ffmpeg|\.m4s|\.jpg|\.png|\.webp|\.xml`  
- 释义:  
    当下载链接匹配该正则时，不进行多线程下载

## ~~bilibili.theme~~
- 取值范围: `default | system`
- 默认值: `default`  
- 释义:  
    当值为`default`时,UI主题使用swing默认  
    当值为`system`时,~~UI主题跟随系统~~， 该功能不再维护

## ~~bilibili.button.style~~
- 取值范围: `default | design`
- 默认值: `design`  
- 释义:  
    当值为`design`时, Button按钮使用设计样式  
    当值为`default`时,~~Button按钮使用默认样式~~， 该功能不再维护

## bilibili.restrictTempMode
- 取值范围: `on | off`
- 默认值: `on`  
- 释义:  
    当值为`on`时, 如果已经存在下载好的视频(无论视频损坏与否)，该视频对应的临时文件将会被删除  
    当值为`off`时, 当下载完成后，只有视频大小达标，该视频对应的临时文件将会被删除。某些异常可能会导致临时文件未被删除而一直存在。


## proxyHost / proxyPort
- 取值范围: IP地址 / 端口
- 默认值: 空  
- 释义:   
    HTTP + HTTPS 代理设置
- 举例  
    proxyHost = 127.0.0.1  
    proxyPort = 1080  

## socksProxyHost / socksProxyPort
- 取值范围: IP地址 / 端口
- 默认值: 空  
- 释义:   
    SOCKS 代理设置
- 举例  
    socksProxyHost = 127.0.0.1  
    socksProxyPort = 1080  

## bilibili.https.allowInsecure
- 取值范围: `true | false`
- 默认值: false  
- 释义:   
    当值为`true`时, 所有的网络请求将跳过HTTPS证书检查
- 相关issue  
    https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/77


## bilibili.lockCheck
- 取值范围: `true | false`
- 默认值: false  
- 释义: 
    当值为`true`时, 程序将只允许单例运行，防止多开

## bilibili.userAgent.pc
- 取值范围: User Agent
- 默认值: `Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0`  
- 释义:   
    程序在使用WEB端API时，会使用的UserAgent。  
    修改时，下面两项也需要改动。  
    - `bilibili.userAgent.pc.fingerprint`  
    - `bilibili.userAgent.pc.payload`

## bilibili.user.login
- 取值范围: `qr | pwd | sms`
- 默认值: `qr`  
- 释义:   
    程序默认使用的登录方式。  
    + qr： 扫码登录  
    + pwd： 用户名密码登录  
    + sms： 短信验证登录  

## bilibili.server.port
- 取值范围: 端口号
- 默认值: `8787`  
- 释义: 
    用户名密码登录或短信验证登录时，为了过验证码需要开启本地服务。  
    这里配置的是监听端口。

## bilibili.system.properties.jre{版本号}.  
- 取值范围: 与运行时-D传参一致
- 默认值:   
```
bilibili.system.properties.jre11 = -Dhttps.protocols=TLSv1.2 -Dsun.java2d.uiScale=1.0 -Dswing.boldMetal=false -Dsun.java2d.dpiaware=false
```
- 释义:   
    与运行时的命令行`java -Dkey1=value1 -Dkey2=value2 ...`实现相同效果    
    通过`System.setProperty(key, value)`实现  
    
## bilibili.system.properties.jre{版本号}.override  
- 取值范围: `true | false`
- 默认值:  
```
bilibili.system.properties.jre11.override = false
``` 
- 释义:   
    当值为`true`时, 上面的参数值会覆盖命令行传入的对应参数


## bilibili.download.batch.config.name
- 取值范围: 符合文件名命名规范
- 默认值: `batchDownload.config`  
- 释义: 
    一键下载配置的默认名称

## bilibili.download.batch.config.name.pattern
- 取值范围: 正则匹配表达式
- 默认值: `^batchDownload.*\.config$`  
- 释义:   
    一键下载配置名称的匹配正则表达式。   
    程序会扫描config文件夹下文件名符合该正则的文件，在菜单`配置` -> `一键下载配置`中提供切换的选项  

## bilibili.download.update.sources
- 取值范围:   
    单个源名称不包含`|`、空格等特殊字符，源与源之间以`|`隔开
- 默认值:   
    ~~`Github|Supabase|Railway|Cloudinary|Imagekit`~~  
    `Github|Bitbucket|Supabase|Cloudinary|Twicpics`  
- 释义:   
    可用的更新源，与配置`bilibili.download.update.patterns.{源名称}`搭配。   
    例如： `Github`更新源对应的配置是`bilibili.download.update.patterns.Github`  

## bilibili.download.update.sources.active
- 取值范围:   
    局限于`bilibili.download.update.sources`
- 默认值: `Github`  
- 释义:   
    正在使用的更新源

## bilibili.download.update.patterns.\{源名称}
- 取值范围: 用于描述更新压缩包及对应SHA1哈希值的下载地址
- 释义:   
    支持`{version}`、`{file}`两个变量。例如：
    + Github源  
        `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V{version}/{file}`
    + 对应ZIP下载链接  
        `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V6.18/BilibiliDown.v6.18.release.zip`
    + 对应SHA1下载链接  
        `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V6.18/BilibiliDown.v6.18.release.zip.sha1`

## bilibili.download.ffmpeg.sources
- 取值范围:   
    单个源名称不包含`|`、空格等特殊字符，源与源之间以`|`隔开
- 默认值:   
    ~~`Github|Supabase|Railway|Cloudinary|Imagekit`~~  
    `Github|Bitbucket|Supabase|Cloudinary|Twicpics`  
- 释义:   
    可用的更新源，与配置`bilibili.download.ffmpeg.url.{源名称}`搭配。   
    例如： `Github`更新源对应的配置是`bilibili.download.ffmpeg.url.Github`  

## bilibili.download.ffmpeg.sources.active
- 取值范围:   
    局限于`bilibili.download.ffmpeg.sources`
- 默认值: ~~`Github`~~ `Bitbucket`  
- 释义:   
    正在使用的ffmpeg源

## bilibili.download.ffmpeg.url.\{源名称}
- 取值范围:   
    用于描述ffmpeg的下载地址
- 释义:   
    例如，Github源  
    ~~`https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg_N-108857-g00b03331a0-20221027.exe`~~  
    `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg-20240123-{os}-{arch}{exeSuffix}`  

## bilibili.dash.video.codec.priority
- 默认值:   
    `12, 7, 13`   
    ~~`7, 12, 13`(如果不存在app.config)~~ V6.32后统一配置
- 释义:   
    DASH视频的优先编码方式, 越往前优先级越高, 用`,`分开，接受空格
    ```
    # -1	表示随意，会选取API回复内容里面的第一个选项
    # 7 	AVC编码 
    # 12 	HEVC编码 	
    # 13 	AV1编码 
    ```
## bilibili.dash.audio.quality.priority 
- 默认值: `30280, 30232, 30216, -1, 30251, 30250`  
- 释义:   
    DASH音频的优先质量, 越往前优先级越高, 用`,`分开，接受空格
    ```
    ### 启用 杜比全景声 和 Hi-Res无损 的时候，需要注意ffmpeg的版本是否支持
    ###		对于Windows用户，程序`V6.19`程序版本及之后理论上可行
    ###		对于Windows用户，程序`V6.18`及以前的版本，请先删除原有ffmpeg.exe，再重新打开程序即可
    ### 	对于其他平台的，建议访问ffmpeg官网: https://ffmpeg.org/download.html
    ###		建议ffmpeg替换后进行测试
    ## 测试Hi-Res无损 BV1tB4y1E7oT
    ## 测试杜比视界 BV13L4y1K7th
    # -1	表示随意，会选取API回复内容里面的第一个选项
    # 30216 	64K             (实际大小不一定匹配，但码率只有这三档，大小的相对关系是正确的)
    # 30232 	132K
    # 30280 	192K
    # 30250 	杜比全景声
    # 30251 	Hi-Res无损
    ```

## bilibili.dash.checkUrl
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    查询DASH方式的下载链接时，每个选择的可用链接一般有两个，即`base_url`和`backup_url`。  

    值为`false`时，直接返回`base_url`。  
    值为`true`时，检查链接`base_url`有效性。有效返回`base_url`，否则返回`backup_url`。  

    检查链接有效性会发送一个`Range: byte=0-100`网络请求。  
    建议遇到下载失败的情况再尝试开启，尽量避免不必要的请求交互。  

## bilibili.dash.download.mode
- 取值范围:   
    `0 | 1 | 2`
- 默认值:   
    `0`  
- 释义:   
    值为`0`时，下载音视频并转码为mp4。  
    值为`1`时，仅下载视频并转码为mp4。  
    值为`2`时，仅下载音频并转码为mp4。  
    以上仅针对DASH模式(即`bilibili.format=0`)有效
    
## bilibili.github.token
- 引入版本: V6.23
- 取值范围:   
    [Github token](https://github.com/settings/tokens)
- 释义:   
    暂只用于下载最新的Beta版本，即Github Action生成的artifact。  
    目前已知的问题是： 下载链接会重定向到域名`pipelines.actions.githubusercontent.com`。   
    使用项目精简的JRE 11访问它会被GFW重置连接，但JDK 11不会。  

## bilibili.sysTray.enable
- 引入版本: V6.23
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    是否开启系统托盘功能

## bilibili.sysTray.minimizeToSystray
- 引入版本: V6.23
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    值为`true`时，开启系统托盘功能后，点击最小化按钮到托盘（从任务栏隐藏）    
    值为`false`时，开启系统托盘功能后，点击最小化按钮到任务栏

## bilibili.sysTray.closeToSystray
- 引入版本: V6.23
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    值为`true`时，开启系统托盘功能后，点击关闭按钮到托盘（从任务栏隐藏）    
    值为`false`时，开启系统托盘功能后，点击关闭按钮退出程序

## bilibili.dash.ffmpeg.command.merge
- 引入版本: V6.24
- 取值范围:   
    FFmpeg命令行调用, 中间`,`是将其分割成`String[]`
- 默认值:   
    ```
    {FFmpeg}, -i, {SavePath}{VideoName}, -i, {SavePath}{AudioName}, -c, copy, {SavePath}{DstName}
    ```  
- 释义:   
    音视频都存在时的ffmpeg调用命令行。  
    如果自行改动的话，请注意ffmpeg编译的版本，以及硬件上是否支持你改的内容。  
    一个使用英伟达硬解的参考例子：  
    ```
    {FFmpeg}, -hwaccel, cuda, -i, {SavePath}{VideoName}, -i, {SavePath}{AudioName}, -c, copy, {SavePath}{DstName}
    ```
## bilibili.cmd.debug
- 引入版本: V6.24
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    值为`true`时，外部命令行调用会显示输出    
    值为`false`时，外部命令行调用不会显示输出    

## bilibili.alert.qualityUnexpected
- 引入版本: V6.26
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    针对[#141](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/141)引入。  
    在已经登录的情况下，程序认为视频的质量应当不低于720P，因此遇到低画质的链接会抛出异常。  
    值为`true`时，当遇到不期望的480P视频时会抛出异常  
    值为`false`时，当遇到不期望的480P视频时不会抛出异常


## bilibili.dash.video.codec.priority.map
- 引入版本: V6.26
- 默认值:   
    `32:7, 12, 13| 16:7, 12, 13`  
- 释义:   
    针对[#145](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/145)引入。  
    ```
    7：AVC编码 12：HEVC编码 13：AV1编码
    ```
    ```  
    127：超高清         125：真彩 HDR   120：超清4K     116：高清1080P60   
    112：高清1080P+     80：高清1080P   64：720P        32：480P   
    ```  
    该配置优先级更高，如果没有在该配置中指定优先级，则配合`bilibili.dash.video.codec.priority`使用。  
	举例，假设：
    ```  
    bilibili.dash.video.codec.priority      = 12, 7, 13  
    bilibili.dash.video.codec.priority.map  = 64: 7, 12, 13| 32:7, 12, 13| 16:7, 12, 13  
    ```
    如果请求得到的1080P，那么优先HEVC编码；  
    如果请求得到的480P，那么优先AVC编码。   
    需要注意的是，分隔符均使用英文标点，注意区分全半角。  

## bilibili.login.cookie.refresh.runWASMinBrowser
- 引入版本: V6.26
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    值为`true`时，刷新cookie时，借用浏览器环境运行wasm  
    值为`false`时，刷新cookie时，直接在程序内运行代码

## bilibili.login.cookie.tryRefreshOnStartup
- 引入版本: V6.27
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    值为`true`时，且`bilibili.login.cookie.refresh.runWASMinBrowser`为`false`时，每次程序打开时会尝试刷新Cookie。  
    值为`false`时，每次程序打开时不会尝试刷新Cookie。    
    
    这里的尝试刷新Cookie，会先通过api判断Cookie是否需要刷新，然后才会尝试刷新。  

## bilibili.tab.display.previewPic
- 引入版本: V6.27
- 取值范围:   
    `on | off`
- 默认值:   
    `on`  
- 释义:   
    值为`on`时，Tab页面自动显示第一个作品预览图。   
    值为`off`时，Tab页面不自动显示作品预览图。你需要在右侧选择相应的视频标题，长按鼠标左键后松开，可显示对应预览图。  
- 相关issue
    + https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/151

## bilibili.menu.tab.promptBeforeCloseAllTabs
- 引入版本: V6.29
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    值为`true`时，在菜单栏里，点击`关闭全部Tab页`后，弹出确认框。   
    值为`false`时，在菜单栏里，点击`关闭全部Tab页`后，直接关闭全部Tab页。  
- 相关issue
    + https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/165

## bilibili.download.batch.plan
- 引入版本: V6.30
- 默认值:   
    `06:00~02:00=>r(300,480); 00:00~02:00=>r(300,480); 02:00~06:00=>~06:00+r(0,360); 00:00~00:00=>r(600,600)`  
- 释义:   
    ```
    # 按计划分配每次一键下载任务之间的间隔
    # 配置格式为 {时间段1} => {间隔时间1} ; {时间段2} => {间隔时间2} ; {时间段3} => {间隔时间3} ; ... {时间段n} => {间隔时间n}
    # 左边配置的优先级更高
    ## 时间段格式为闭区间 {时刻1}~{时刻2}
    ### 时刻格式为北京时间24小时制 HH:mm
    ### 如果时刻1 大于等于 时刻2，则表示当天时刻1到第二天时刻2(但实际只包括当天剩余时间，即到第二天零点为止。遇到跨天最好在后面再补一个区间 00:00~{时刻2})
    ## 间隔时间格式1为 r(t1,t2) 
    ### t1<=t2, 正整数，单位为秒，表示随机等待t1 到 t2秒
    ## 间隔时间格式2为 ~HH:mm
    ### HH:mm为北京时间24小时制时刻，表示等待到该时刻
    ## 间隔时间格式3为 ~HH:mm+r(t1,t2)
    ### 参考格式1/2,表示等待到该时刻后再随机等待t1 到 t2秒
    
    当次任务完成后，时间在每天6点到第二天0点（不是2点），随机休眠300~480秒，再继续下次任务
    当次任务完成后，时间在每天2点到6点，休眠到当天6点，再随机休眠0~360秒，再继续下次任务
    其它情况（每天0点~2点这一段时间才能到这来），当次任务完成后，休眠600秒，再继续下次任务
    06:00~02:00=>r(300,480); 02:00~06:00=>~06:00+r(0,360); 00:00~00:00=>r(600,600)

    当次任务完成后，时间在每天6点到第二天2点，随机休眠300~480秒，再继续下次任务
    当次任务完成后，时间在每天2点到6点，休眠到当天6点，再随机休眠0~360秒，再继续下次任务
    其它情况（实际上不可能到这个判断），当次任务完成后，休眠600秒，再继续下次任务
    06:00~00:00=>r(300,480); 00:00~02:00=>r(300,480); 02:00~06:00=>~06:00+r(0,360); 00:00~00:00=>r(600,600)
    ```

## bilibili.download.push.type
- 引入版本: V6.30
- 取值范围:   
    `Print | Mail`
- 默认值:   
    `Print`  
- 释义:   
    每次按计划一键下载后，使用什么来推送消息  
    `Print`，仅打印  
    `Mail`，通过邮件通知  

## bilibili.download.push.account
- 引入版本: V6.30
- 取值范围:   
    取决于`bilibili.download.push.type`
- 默认值:   
    空字符串  
- 释义:   
    推送消息需要的账户。当消息类型为邮件时，可参考以下注释  
    ```
    /**
    * 以下为相关配置
    * @bilibili.download.push.type      Mail
    * @bilibili.download.push.account   发送的邮箱地址
    * @bilibili.download.push.token     发送的邮箱凭证，需要注意的是并不一定是密码
    * @mail.smtp.to.addr                接收的邮箱地址，为空时等于发送的邮箱地址
    * @mail.smtp.host                   选填，邮箱不为@sina.com @163.com @qq.com时，必填
    * @mail.smtp.port					选填，邮箱不为@sina.com @163.com @qq.com时，必填
    * @mail.smtp.ssl.enable	            选填，邮箱不为@sina.com @163.com @qq.com时，必填  值为 true/false
    * @mail.smtp.starttls.enale         选填，需要starttls再填写 true/false
    * @mail.smtp.debug                  选填，是否输出debug。值为 true/false，默认false
    */
    ```

## bilibili.download.push.token
- 引入版本: V6.30
- 取值范围:   
    取决于`bilibili.download.push.type`
- 默认值:   
    空字符串  
- 释义:   
    推送消息需要的密码或者凭证 

## bilibili.userAgent.pc.fingerprint
- 引入版本: V6.30
- 默认值:   
    `a8bad806241b0b0f7add1024fbd701fa`  
- 释义:   
    `bilibili.userAgent.pc`对应的浏览器指纹(取自cookie buvid_fp)  

## bilibili.userAgent.pc.payload
- 引入版本: V6.30
- 默认值:   
    ```
    {"3064":1,"5062":"1707365865753","03bf":"https%3A%2F%2Fwww.bilibili.com%2F","39c8":"333.1007.fp.risk","34f1":"","d402":"","654a":"","6e7c":"1536x684","3c43":{"2673":0,"5766":24,"6527":0,"7003":1,"807e":1,"b8ce":"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0","641c":0,"07a4":"zh-CN","1c57":"not available","0bd0":4,"748e":[864,1536],"d61f":[824,1536],"fc9d":-480,"6aa9":"Asia/Shanghai","75b8":1,"3b21":1,"8a1c":0,"d52f":"not available","adca":"Win32","80c9":[["PDF Viewer","Portable Document Format",[["application/pdf","pdf"],["text/pdf","pdf"]]],["Chrome PDF Viewer","Portable Document Format",[["application/pdf","pdf"],["text/pdf","pdf"]]],["Chromium PDF Viewer","Portable Document Format",[["application/pdf","pdf"],["text/pdf","pdf"]]],["Microsoft Edge PDF Viewer","Portable Document Format",[["application/pdf","pdf"],["text/pdf","pdf"]]],["WebKit built-in PDF","Portable Document Format",[["application/pdf","pdf"],["text/pdf","pdf"]]]],"13ab":"x7DlAAAAAElFTkSuQmCC","bfe9":"SAAmYUAFhmrCZRFNCvwHPGlBW1raHI4gAAAABJRU5ErkJggg==","a3c1":["extensions:ANGLE_instanced_arrays;EXT_blend_minmax;EXT_color_buffer_half_float;EXT_float_blend;EXT_frag_depth;EXT_shader_texture_lod;EXT_sRGB;EXT_texture_compression_bptc;EXT_texture_compression_rgtc;EXT_texture_filter_anisotropic;OES_element_index_uint;OES_fbo_render_mipmap;OES_standard_derivatives;OES_texture_float;OES_texture_float_linear;OES_texture_half_float;OES_texture_half_float_linear;OES_vertex_array_object;WEBGL_color_buffer_float;WEBGL_compressed_texture_s3tc;WEBGL_compressed_texture_s3tc_srgb;WEBGL_debug_renderer_info;WEBGL_debug_shaders;WEBGL_depth_texture;WEBGL_draw_buffers;WEBGL_lose_context","webgl aliased line width range:[1, 1]","webgl aliased point size range:[1, 1024]","webgl alpha bits:8","webgl antialiasing:yes","webgl blue bits:8","webgl depth bits:24","webgl green bits:8","webgl max anisotropy:16","webgl max combined texture image units:32","webgl max cube map texture size:16384","webgl max fragment uniform vectors:1024","webgl max render buffer size:16384","webgl max texture image units:16","webgl max texture size:16384","webgl max varying vectors:30","webgl max vertex attribs:16","webgl max vertex texture image units:16","webgl max vertex uniform vectors:4096","webgl max viewport dims:[32767, 32767]","webgl red bits:8","webgl renderer:ANGLE (Intel, Intel(R) HD Graphics 400 Direct3D11 vs_5_0 ps_5_0)","webgl shading language version:WebGL GLSL ES 1.0","webgl stencil bits:0","webgl vendor:Mozilla","webgl version:WebGL 1.0","webgl unmasked vendor:Google Inc. (Intel)","webgl unmasked renderer:ANGLE (Intel, Intel(R) HD Graphics 400 Direct3D11 vs_5_0 ps_5_0)","webgl vertex shader high float precision:23","webgl vertex shader high float precision rangeMin:127","webgl vertex shader high float precision rangeMax:127","webgl vertex shader medium float precision:23","webgl vertex shader medium float precision rangeMin:127","webgl vertex shader medium float precision rangeMax:127","webgl vertex shader low float precision:23","webgl vertex shader low float precision rangeMin:127","webgl vertex shader low float precision rangeMax:127","webgl fragment shader high float precision:23","webgl fragment shader high float precision rangeMin:127","webgl fragment shader high float precision rangeMax:127","webgl fragment shader medium float precision:23","webgl fragment shader medium float precision rangeMin:127","webgl fragment shader medium float precision rangeMax:127","webgl fragment shader low float precision:23","webgl fragment shader low float precision rangeMin:127","webgl fragment shader low float precision rangeMax:127","webgl vertex shader high int precision:0","webgl vertex shader high int precision rangeMin:31","webgl vertex shader high int precision rangeMax:30","webgl vertex shader medium int precision:0","webgl vertex shader medium int precision rangeMin:31","webgl vertex shader medium int precision rangeMax:30","webgl vertex shader low int precision:0","webgl vertex shader low int precision rangeMin:31","webgl vertex shader low int precision rangeMax:30","webgl fragment shader high int precision:0","webgl fragment shader high int precision rangeMin:31","webgl fragment shader high int precision rangeMax:30","webgl fragment shader medium int precision:0","webgl fragment shader medium int precision rangeMin:31","webgl fragment shader medium int precision rangeMax:30","webgl fragment shader low int precision:0","webgl fragment shader low int precision rangeMin:31","webgl fragment shader low int precision rangeMax:30"],"6bc5":"Google Inc. (Intel)~ANGLE (Intel, Intel(R) HD Graphics 400 Direct3D11 vs_5_0 ps_5_0)","ed31":0,"72bd":0,"097b":0,"52cd":[0,0,0],"a658":["Arial","Arial Black","Arial Narrow","Arial Unicode MS","Book Antiqua","Bookman Old Style","Calibri","Cambria","Cambria Math","Century","Century Gothic","Comic Sans MS","Consolas","Courier","Courier New","Georgia","Helvetica","Impact","Lucida Bright","Lucida Calligraphy","Lucida Console","Lucida Fax","Lucida Handwriting","Lucida Sans Unicode","Microsoft Sans Serif","Monotype Corsiva","MS Gothic","MS PGothic","MS Reference Sans Serif","MS Sans Serif","MS Serif","Palatino Linotype","Segoe Print","Segoe Script","Segoe UI","Segoe UI Light","Segoe UI Semibold","Segoe UI Symbol","Tahoma","Times","Times New Roman","Trebuchet MS","Verdana","Wingdings","Wingdings 2","Wingdings 3"],"d02f":"35.7383295930922"},"54ef":"{\"b_ut\":\"7\",\"home_version\":\"V8\",\"i-wanna-go-back\":\"-1\",\"in_new_ab\":true,\"ab_version\":{\"for_ai_home_version\":\"V8\",\"tianma_banner_inline\":\"CONTROL\",\"enable_web_push\":\"DISABLE\"},\"ab_split_num\":{\"for_ai_home_version\":54,\"tianma_banner_inline\":54,\"enable_web_push\":10}}","8b94":"","df35":"6D30A3F0-669B-6582-5832-00B5EC7795C51E174Cinfoc","07a4":"zh-CN","5f45":null,"db46":0}
    ```  
- 释义:   
    截取自api请求
    `https://api.bilibili.com/x/internal/gaia-gateway/ExClimbWuzhi`  
    注意，不是整个json，而是该json的`payload`的值

## bilibili.name.autoNumber
- 引入版本: V6.31
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    遇到同名文件时是否自动添加序号 (01)、(02)...   
    如果为`false`，会在下载目录留存有类似于`BVxxxxxxx-80-p1.mp4`的文件。  

## bilibili.download.batch.plan.runOnStartup
- 引入版本: V6.32
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    在程序启动时，是否按计划进行周期性批量下载。

## bilibili.download.host.forceReplace
- 引入版本: V6.32
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    是否强制开启替换下载服务器host功能。   

    如果为`true`，会将音/视频下载链接中的域名(包括端口)替换为配置值`bilibili.download.host.alternative`。  
    此时，没有必要测试之前链接的有效性，建议`bilibili.dash.checkUrl = false`。    

    主要是针对MCDN/PCDN下载过慢或者失败的场景，即下载链接的域名大概像这样的情况：  
    `xy123x184x63x196xy.mcdn.bilivideo.cn:4483`、`a26blo3f.v1d.szbdyd.com:8997`  

## bilibili.download.host.alternative
- 引入版本: V6.32
- 取值范围:   
    B站下载服务器的域名
- 默认值:   
    `upos-sz-mirror08c.bilivideo.com`  
- 释义:   
    音/视频下载链接中的域名(加上端口)的替代值。下列值仅供参考：
    ```
    百度云？
    upos-sz-mirrorbd.bilivideo.com
    upos-sz-mirrorbos.bilivideo.com
    腾讯云？
    upos-sz-mirrorcos.bilivideo.com
    upos-sz-mirrorcosb.bilivideo.com
    upos-sz-mirrorcoso1.bilivideo.com
    华为云？
    upos-sz-mirrorhw.bilivideo.com
    upos-sz-mirrorhwb.bilivideo.com
    upos-sz-mirrorhwo1.bilivideo.com
    upos-sz-mirror08c.bilivideo.com
    upos-sz-mirror08h.bilivideo.com
    upos-sz-mirror08ct.bilivideo.com
    阿里云？
    upos-sz-mirrorali.bilivideo.com
    upos-sz-mirroralib.bilivideo.com
    upos-sz-mirroralio1.bilivideo.com
    新网？？？
    upos-sz-estghw.bilivideo.com
    海外？ Akamai、腾讯、阿里
    upos-hz-mirrorakam.akamaized.net
    upos-sz-mirrorcosov.bilivideo.com
    upos-sz-mirroraliov.bilivideo.com
    免流？？
    upos-tf-all-hw.bilivideo.com
    upos-tf-all-tx.bilivideo.com
    ```

## bilibili.download.forceHttp
- 引入版本: V6.32
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    是否强制将音视频下载地址的https转为http(PCDN除外)。   
    如果为`true`，会将音/视频下载链接中的`https`换成`http`。  
    当然，如果下载链接已经指定了端口，像`*.mcdn.bilivideo.cn:4483`，那么无论配置怎样都不会替换。  

    显而易见的，`http`比`https`连接速度和传输速率都要好。  
    如果开启功能后没有遭遇到下载失败的问题，那么建议一直开启。  

## bilibili.download.urlValidPeriod
- 引入版本: V6.32
- 取值范围:   
    正整数
- 默认值:   
    `90`  
- 释义:   
    下载url的有效时长，单位分钟。  
    当下载任务开始时，如果距离查询该视频的链接已经超过了配置的时长，那么就重新查询。  


## bilibili.time.syncServer
- 引入版本: V6.33
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    当为`true`时，在启动的时候会对比B站的服务器时间，并以之为基准。  

## bilibili.alert.ffmpegFail
- 引入版本: V6.35
- 取值范围:   
    `true | false`
- 默认值:   
    `true`  
- 释义:   
    当为`true`时，使用ffmpeg合并视频失败时会抛出异常并弹窗报错。  

## bilibili.dash.ffmpeg.command.transAudioOnly
- 引入版本: V6.36
- 取值范围:   
    FFmpeg命令行调用, 中间`,`是将其分割成`String[]`
- 默认值:   
    ```
    {FFmpeg}, -y, -i, {SavePath}{AudioName}, -vn, -c:a, copy, {SavePath}{DstName}
    ```  
- 相关issue
    + https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/227
- 释义:   
    仅仅下载音频时的ffmpeg调用命令行。  
    如果自行改动的话，请注意ffmpeg是否支持相应的编解码工作。  
    需要配合`bilibili.dash.suffix4AudioOnly`使用，它决定了`{DstName}`的后缀。  

    默认采用后缀为`.mp4`，编码`-c:a copy`，速度最快  
    可以尝试使用后缀`.aac`，编码`-c:a copy`，适用于大多数状况(无损除外，需改后缀为`.flac`)  
    可以尝试后缀为`.mp3`，需要编码转换，速度较慢，且**需要ffmpeg支持**，参考命令如下
    ```
    {FFmpeg}, -y, -i, {SavePath}{AudioName}, -vn, -acodec, libmp3lame, -q:a, 2, {SavePath}{DstName}
    ```
    另外，调试ffmpeg配置时建议打开调用外部命令行的log输出。  
    ```
    bilibili.cmd.debug = true
    ```


## bilibili.dash.suffix4AudioOnly
- 引入版本: V6.36
- 取值范围:   
    仅下载音频时的文件后缀(带符号.)
- 默认值:   
    `.mp4`
- 释义:   
    常见的取值有`.mp4`、`.aac`、`.flac`、`.mp3`。  
    搭配`bilibili.dash.ffmpeg.command.transAudioOnly`使用
    
    
## bilibili.info.query.strategy
- 引入版本: V6.38
- 取值范围:   
    `tryNormalTypeFirst | judgeTypeFirst | returnFixedValue`
- 默认值:   
    `returnFixedValue`  
- 释义:   
    + `tryNormalTypeFirst` 先尝试普通视频，报错后尝试其它类型; 绝大多数情况1次网络请求，少数2次。  
    + `judgeTypeFirst` 先判断视频类型，再进行查询; 2次网络请求。**这是旧版本的查询策略**。  
    + `returnFixedValue` 不查询，直接返回固定值; 无网络请求。**这是新版本的默认查询策略**。  
    + 无论是何策略，若单个BV下子视频数量多于5，总会返回固定列表。  
    + 不建议在配置文件中修改该值。若实在有需要，可以在菜单栏临时变更策略，程序关闭后失效。  
- 相关issue  
    `BV1g5pqeBEXP`，这个互动视频有上百个片段，查询清晰度会“卡死”在那，但实际上后台一直在获取每个视频的清晰度。这是不必要的。 

## bilibili.name.format.clipTitle.allowNull
- 引入版本: V6.38
- 取值范围:   
    `true | false`
- 默认值:   
    `false`  
- 释义:   
    当为`true`时，若`clipTitle`和视频标题`avTitle`一致，会将`clipTitle`置空。  
    此时可以配合条件判断进行使用，避免文件名出现冗余的重复信息。 
- 相关issue
    + https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/237
