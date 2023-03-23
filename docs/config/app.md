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
- 默认值: `Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0`  
- 释义:   
    程序在使用WEB端API时，会使用的UserAgent

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
    `Github|Supabase|Railway|Cloudinary|Imagekit`  
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
    `Github|Supabase|Railway|Cloudinary|Imagekit`  
- 释义:   
    可用的更新源，与配置`bilibili.download.ffmpeg.url.{源名称}`搭配。   
    例如： `Github`更新源对应的配置是`bilibili.download.ffmpeg.url.Github`  

## bilibili.download.ffmpeg.sources.active
- 取值范围:   
    局限于`bilibili.download.ffmpeg.sources`
- 默认值: `Github`  
- 释义:   
    正在使用的ffmpeg源

## bilibili.download.ffmpeg.url.\{源名称}
- 取值范围:   
    用于描述ffmpeg的下载地址
- 释义:   
    例如，Github源，`https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg_N-108857-g00b03331a0-20221027.exe`

## bilibili.dash.video.codec.priority
- 默认值:   
    `12, 7, 13`(如果存在app.config)  
    `7, 12, 13`(如果不存在app.config)
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
