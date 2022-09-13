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