## UPDATE
* V6.38 `2024-12-22`
    * 修复：在查询清晰度时带上cookie，这是因为某些视频必须登录才能查看，比如`BV1fx411x7QS` #240
    * 优化：对查询清晰度的API进行了升级，并增强了鲁棒性(虽然旧的也还能用)
    * 新增：提供多种清晰度查询策略，目的是减少不必要的网络请求次数
        + `tryNormalTypeFirst` 先尝试普通视频，报错后尝试其它类型; 绝大多数情况1次网络请求，少数2次。  
        + `judgeTypeFirst` 先判断视频类型，再进行查询; 2次网络请求。**这是旧版本的查询策略**。  
        + `returnFixedValue` 不查询，直接返回固定值; 无网络请求。**这是新版本的默认查询策略**。  
        + 无论是何策略，若单个BV下子视频数量多于5，总会返回固定列表。  
        + 引入该功能的主要原因是`BV1g5pqeBEXP`，这个互动视频有上百个片段，查询清晰度会“卡死”在那，实际上后台一直在获取每个视频的清晰度。但这是不必要的。  
        + 不建议在配置文件中修改该值。若实在有需要，可以在菜单栏临时变更策略，程序关闭后失效。  
    * 新增：当`clipTitle`和视频标题`avTitle`一致时，允许将`clipTitle`置空 #237
        + `bilibili.name.format.clipTitle.allowNull`为`true`时功能启用(默认关闭)，此时可以配合条件判断进行使用，避免文件名出现冗余的重复信息。  
    * 其它详见[V6.37...V6.38](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.37...V6.38)
* V6.37 `2024-11-24`
    * 修复：纠正一键下载时，以日期作为条件判断不准确的错误 #235
    * 修复：更新字幕api的解析 #232
    * 其它详见[V6.36...V6.37](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.36...V6.37)
* V6.36 `2024-10-27`
    * 修复：更新合辑视频链接的解析 #225
    * 修复：纠正互动视频`graph_version`的获取方式
    * 新增：一键下载：支持以标题/小标题是否匹配正则表达式为条件 #229
    * 新增：仅下载音频时，允许自定义ffmpeg音频转换命令、自定义音频后缀格式(可能需要ffmpeg支持) #226,#227
    * 其它详见[V6.35...V6.36](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.35...V6.36)
* V6.35 `2024-09-25`
    * 修复：解决配置面板修改配置后无法保存的问题 #216
    * 其它详见[V6.34...V6.35](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.34...V6.35)
* V6.34 `2024-09-03`
    该版本主要用来解决某些使用上的问题，如果您已经可以正常使用，可以忽略该更新。  
    * 精简jre支持的https加密套件有限，不支持ecc。这会导致默认的ffmpeg源bitbucket下载失败。随着网站加密套件的更新，未来或许还会有更多的`TLS handshake failure`。   
    这个问题在旧版本可以通过更换ffmpeg源，或者自行获取ffmpeg并在程序设置好path的方式来解决。  
    新版本为精简JRE添加了模块`jdk.crypto.ec`，用于解决部分HTTPS链接握手出错的问题。  
    需要注意的是，这个模块在JDK 22被标为deprecated，相关实现会被挪到`java.base`模块。详见[JDK-8312267](https://bugs.openjdk.org/browse/JDK-8312267)   
    另外，新版本为Windows x64用户打包的`zip`、`msi`添加了精简编译的`ffmpeg.exe`。  
    
    * 在转码/合并失败时，现在会提示检查ffmpeg配置。    
    现在Web端基本上获取不到高清晰度的mp4、flv。随着时间推移，ffmpeg成了必选项，以前的逻辑、设计和提示语都有点过时。   
    尝试让用户明白三件事情：  
        + ffmpeg是必需的。
        + 程序可以提供仅基础功能的精简版编译下载。
        + 如果计算机里有现成的ffmpeg，可以通过配置进行指定。

    * 修复程序自更新时下载Beta版本报错的问题。    
        下载Github Action的artifact需要登录，虽然不甘心，但可以理解。  
        下载链接301到新链接后，继承使用原来的header会报错，这个行为有点抽象。  
        大部分工具都是follow redirect可以直接下载的，但这里的逻辑是那小部分。    
     
    * 现在程序会检查数据目录的`写`权限。#214  
        不推荐将程序放在系统盘。如果你这么做了，你需要进行额外的操作（三选一）：  
        + 以管理员身份运行程序(不推荐)
        + 参考#214 将程序目录设置为可写可修改
        + 设置另外的有权限的数据目录，通过传入JVM参数`-Dbilibili.prop.dataDirPath`进行指定。不会可以在参考文档中搜索关键词。    
    
    * 部分环境可能出现显示错误的情况。#213  
    问题存在，但找不到原因。可以通过更换入口类规避。  
    现在可以通过传入JVM参数`-Dbilibili.prop.mainClass`参数给`launch.jar`，来指定`INeedBiliAV.jar`的运行入口。 
        
        修改`BilibiliDown.cfg`
        ```
        [Application]
        app.classpath=launch.jar
        app.mainclass=nicelee.memory.App

        [JavaOptions]
        java-options=-Dfile.encoding=utf-8
        java-options=-Dbilibili.prop.mainClass=nicelee.ui.FrameMain_v3_4
        ```
        
        或者脚本
        `java -Dbilibili.prop.mainClass=nicelee.ui.FrameMain_v3_4 -jar launch.jar`
    
* V6.33  `2024-08-18` 
    * 新增: release 附件中`win64_jre`压缩文件加入`exe`程序
    * 新增: 添加专栏图片解析
    * 新增: 添加专栏文集图片解析
    * 新增: 添加图文动态解析
    * 新增: 添加图文动态个人收藏解析
    * 新增: 可以JVM传入参数`-Dbilibili.prop.dataDirPath={dataDirPath}`来指定数据文件夹位置(可以不是程序所在目录)
    * 新增: 可以JVM传入参数`-Dbilibili.prop.log=true/false`来尽可能减少打印信息
    * 新增: 可以在登录时获取服务器时间并以之为基准
    * 新增(ui): 配置面板中，针对文件/文件夹类型的配置，可以通过文件选择器来选择路径
    * 新增(ci): 现在可以手动触发release ci，此时可以选择是否同步上传代码、附件到第三方
    * 新增(ci): 现在会将`commit hash`,`workflow id`信息写入作品信息页面，`buildTime`改为`GMT+8`时区
    * 已知问题： jpackage 打包的exe程序无法自重启 [JDK-8325924](https://bugs.openjdk.org/browse/JDK-8325924)/[JDK-8325203](https://bugs.openjdk.org/browse/JDK-8325203)
    
* V6.32  `2024-07-05`  
    * 修复: 当编码不为`utf8`时，下载弹幕乱码的问题。[#197](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/197)
    * 修复: 卸载脚本在删除文件时考虑路径中包含有空格的情况
    * 修复: 当搜索UP主视频的结果存在课程时，跳过课程解析。`e.g. https://space.bilibili.com/345024422/search/video?keyword=保姆`
    * 修复: 当视频链接中包含 au+数字 时，会被识别成音频[#197](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/204)
    * 修复: 保存配置时，考虑多行配置同一个key的情况
    * 新增: 现在可以提供Windows amd64下的安装包
    * 新增: 增加配置，可以在软件启动时开始按计划周期性批量下载[#199](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/199)
    * 新增: 现在可以替换音视频下载地址的host，建议在走PCDN表现不佳的情况下尝试使用
        + 可能使情况变好，也可能更坏
        + 右上角菜单`配置` -> `音视频链接替换host?` -> `替换` （临时启用，程序重启后失效）
        + 设置`bilibili.download.host.forceReplace = true`  （持久化生效）
    * 新增: 现在可以强制音视频下载走http而不是https
        + 下载链接如果指定了端口的话，那就只能走https协议，无论配置怎么样
    * 优化: 下载队列的url存活时间超过90min(参数可调整)时，会重新查询url再进行下载  
        + 一次生成这么多任务，_**你有点太极端了**_
        + 在这个场景下，最好设置成：失败重试/继续下载任务时，重新查询下载链接(搜`retry`或`reloadDownloadUrl`)  
    * 优化: 失败重试/暂停后继续下载的任务优先级更高，而不是排在任务队列的最后
    * 优化: `UP主所有视频`支持更多类型的url
        + 现在增加支持`https://space.bilibili.com/336399506/?spm_id_from=333.999.0.0`
        + 以前的类型参数是直接跟在数字后面，而不是`/`后面
            + `https://space.bilibili.com/336399506/`
            + `https://space.bilibili.com/336399506?spm_id_from=333.999.0.0`
    * 优化: Windows下jre11版本、modules更新 
        + 版本`Oracle 11+28 2018-09-25`升级为`Temurin 11.0.23+9-LTS 2024-04-16`
        + modules
            + 前`java.base,java.compiler,java.datatransfer,java.desktop,java.management`
            + 后`java.base,java.compiler,java.datatransfer,java.desktop,java.management,java.security.sasl,java.xml,java.logging` 
    * [帮助文档]修复: 导航提示汉化覆盖完毕
    * [帮助文档]新增: 添加搜索功能
    * [帮助文档]优化: VitePress由`alpha`升级为`release`版本
    * [帮助文档]优化: Github Pages由读取指定分支改为Actions附件上传
    * 其它常规优化，详见[V6.31...V6.32](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.31...V6.32)
    
* V6.31  `2024-05-08`  
    * 新增: 重命名文件失败时，尝试添加序号继续重命名。[#185](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/185)
        + 目的地址存在文件时，会在约定的名称末尾尝试添加`(01)、(02)...`这样的序号
        + 通过配置`bilibili.name.autoNumber`可以开启/关闭该功能
    * 优化: 查询UP主所有链接时添加`dm_img`系列参数，防止返回352。（不登录也能用了）
    * 修复: 查询UP主所有链接时相关请求添加`referer`，防止返回412。[#192](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/192)
    * 其它常规优化，详见[V6.30...V6.31](https://github.com/nICEnnnnnnnLee/BilibiliDown/compare/V6.30...V6.31)
    
* V6.30  `2024-02-23`  
    * 新增: 添加功能，可以周期性地进行“一键下载”，并通报结果。  
    * 优化: 现在按平台和架构编译了四个版本ffmpeg，缺省时符合条件的会提示进行下载：`win_amd64`、`linux_amd64`、`win_arm64`、`linux_arm64`  
    * 优化: 现在补充完善了浏览器指纹等方面的cookie，期望是预防风控[#177](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/177), [#180](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/180)
        + 因为尚不清楚相关机制，目前`通过API上传指纹`这一动作只在`刷新cookie`时才会进行。在遇到风控时，不妨先试一试菜单栏里的`刷新cookie`选项。  
        + 现在最好不要随意修改配置的UA，如果必要，需要在隐私模式下抓取cookie并抓包相应API的payload。详见配置页。
    * 修复: [#182](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/182) 考虑在`UP主所有视频`类型的链接解析时，keyword中含有空格的情况。
    * 删除: 移除解析分页链接时`promptAll`模式相关代码。  

* V6.29  `2023-12-01`  
    * 新增(GUI): 菜单配置栏添加`下载前先查询记录?`配置项，更改后可临时开启/关闭仓库功能。重启后失效。  
    * 删除(GUI): 菜单配置栏删除`下载策略`配置项。  
    * 优化(GUI): [issues 165](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/165)点击`关闭全部Tab页`后，弹出提示框。  
    * 优化: [issues 167](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/167) 优先清晰度添加`字幕`/`弹幕`选项，用于快速批量下载。  
        + 需要注意，`字幕`/`弹幕`不计入下载记录，因而也没有相应判断。  
        + 需要注意，视频、对应字幕、弹幕的下载任务不能同时存在于下载面板。你需要清空下载任务以后再继续。    
    * 优化: 更新适用范围更广的AV和BV转换方式。
    * 优化: 代码和附件都镜像到了`Bitbucket`，程序里面也添加了镜像源。这意味着程序自升级门槛更低了。    

    
* V6.28  `2023-10-14`  
    * 优化: 更新视频链接获取方式，优化实际清晰度判断逻辑。  
        现在，即使不登录也能获取1080P DASH视频(但后续不做任何保证)。
    * 优化: [issues 157](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/157) 下载完毕后buffer置空。在不清空任务面板的情况下，可以容纳多得多的下载任务。
    * 优化: 镜像源去除Imagekit, 添加Twicpics。
    * 优化(GUI): 设置面板的内容高度微调，关于页面内容调整。
    
* V6.27  `2023-08-13`  
    * 修复: [issues 155](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/155) 重写对于UP主所有视频的分页查询逻辑  
        需要注意的是，若UP主上传有多BV的合集，此时自定义文件名中的参数`pDisplay`将不再准确。  
    * 修复: [issues 152](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/152) 考虑只有杜比视界而没有杜比音效的解析场景(e.g. BV1SN411A7KT)  
    * 优化: [issues 151](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/151) 增加配置`bilibili.tab.display.previewPic`，可以开启/关闭Tab页的视频封面预览 
    * 优化: 通过配置`bilibili.login.cookie.tryRefreshOnStartup`，可以使程序在每次打开时尝试刷新cookie。不再需要手动点击对应菜单。  
    
* V6.26  `2023-06-06`  
    * 修复: [issues 146](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/146),[issues 147](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/147) 解决api更换导致的UP主所有视频无法查询的问题  
    * 修复: [issues 149](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/149), 作品信息页面尝试兼容mac下的UI布局 
    * 优化: [issues 140](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/140) 卸载脚本增加更多提示 
    * 优化: [issues 141](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/141) 增加配置`bilibili.alert.qualityUnexpected`，可以开启/关闭对非期望的低画质清晰度视频的判断
    * 优化: [issues 145](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/145) 增加相关配置，可以针对不同分辨率设置不同的视频编码优先级
    * 优化: 现在可以直接在程序代码中刷新cookie，而不必再打开浏览器
    
* V6.25  `2023-03-23`  
    * 修复`V6.24`引入的一个bug，该bug导致jar包路径存在空格或者中文时，程序不能正确运行。  
    * 优化: 出现报错弹窗时，输出更详细的异常信息
    
* V6.24  `2023-02-22`  
	* 新增: 现在可以通过双击/命令行调用`launch.jar`启动程序。  
		* `launch.jar`会先将`INeedBiliAV.jar`加载到内存，然后再调用。这样原来的jar包就可以被删除，便于程序自更新。    
		* 当然，如果不考虑更新的问题，通过双击/命令行调用`INeedBiliAV.jar`启动程序也是可以的。    
		* 如果你是通过旧版本的自更新升级上来的，照旧使用不会有任何问题。
	* 修复: 一键下载的优先清晰度现在不仅受配置文件控制，还受菜单栏控制
	* 优化: 可以通过配置`bilibili.dash.ffmpeg.command.merge`调整DASH类型的音视频FFMPEG合并命令
		*  经测试，配合全功能编译的FFMPEG，指令`-hwaccel cuda`似乎有点效果，详见[FFmpegTest](/src/nicelee/test/junit/FFmpegTest.java)。   
			该指令理论上可以借助NVIDIA硬解，但是，监控显示GPU的调用率一直是0%，就很费解。  
	* 优化: 现在，所有的提示框文本都能够被选择并复制(javax.swing.JOptionPane -> nicelee.ui.item.JOptionPane)
	* 优化: 现在，可以通过配置选择是否输出ffmpeg的处理过程
	* 优化: 历史记录缓存使用`ConcurrentHashMap`,而不是`CopyOnWriteArraySet`
	* 优化: 现在，程序理论上总是会使用`utf-8`编码而不是默认编码(不再需要设置`file.encoding=utf-8`)
	* 优化: 现在，程序理论上可以在任意工作目录正常运行(不再需要cd到jar包所在目录)
	
* V6.23  `2023-01-22`  
    * 新增: 在设置Github token后，可以在菜单栏选择更新Beta版本，省去使用浏览器打开Github Action的步骤
        + `bilibili.github.token = [github token]`  
        + 似乎JRE下重定向的域名`pipelines.actions.githubusercontent.com`会被连接重置，但JDK不受影响。  
    * 新增: 实现最小化到系统托盘功能
        + `bilibili.sysTray.enable = true/false` 是否开启系统托盘功能
        + `bilibili.sysTray.minimizeToSystray = true/false` 是否点击最小化按钮到托盘
        + `bilibili.sysTray.closeToSystray = true/false` 是否点击关闭按钮到托盘
    * 新增: 音频基础解析`e.g. https://www.bilibili.com/audio/au3688627`
    * 新增: 音频歌单解析`e.g. https://www.bilibili.com/audio/am33012874`
    * 新增: 自己创建的默认歌单解析`e.g. https://www.bilibili.com/audio/mycollection/[0-9]+`
    * 优化: 现在可以从收藏夹中解析到音频了`e.g. https://space.bilibili.com/35849261/favlist?fid=1509975661&ftype=create`
    * 优化: 现在，API返回412会弹框提示 [issues 90](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/90)
    * 优化: 现在，查询下载链接解析出错会弹框提示
    * 优化: 现在，在登录后，查询高质量链接却返回360P清晰度会弹框提示
    * 修复：API返回实际清晰度不对劲导致的解析错误 `eg. BV1K14y1g7iU 无cookie`
    * ci: 增加编译后的jar包有效性测试，预防编译失败的低级问题  
    * ci: 人工触发上传时，可选择目标站点
    * ci: 将运行的脚本内容从逐渐臃肿的yaml文件中抽离
      
* V6.20  `2022-12-30`  
    * 新增: [issues 124](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/124) 添加只下载音频/视频功能(考虑到ffmpeg的兼容性问题，容器的格式仍然为`mp4`)。  
    * 优化: [issues 128](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/128) 当点击`加载下载任务`且`task.config`不存在或为空时，log不再显示异常。  
    * 修复: [issues 129](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/129) 当`bilibili.format=2`且**登录**时，可以下载单独的`1080P`MP4文件(该功能随时有可能失效，不建议使用该设置)。  
* V6.19  `2022-11-22`  
    * 新增: [issues 120](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/120)通过配置或菜单，在尝试重新下载时，能够选择是否重新查询下载链接。  
    * 新增: 通过配置，DASH方式视频可以选择编码优先级；音频可以选择码率优先级。  
        另外，音频可以选择`杜比`或者`Hi-Res无损`，但是需要`ffmpeg`的配合(Win64用户可以删除原来的ffmpeg.exe再重启程序)。  
    * 优化: 默认的`ffmpeg.exe`使用了ffmpeg master分支的较新的编译
    * 优化: Actions自动编译打包时写入相关信息，你可以`关于` -> `作品信息`，在界面左上角进行查看
    * 优化：在需要创建socketServer时仅监听本地，避免弹出防火墙提示

* V6.18  `2022-10-24`  
    * 修复: [issues 117](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/117)下载格式为单独MP4时，总是返回低画质的视频  
    * 优化: [issues 40](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/40)分页查询的Tab页面添加了`下一页`按钮  
    * 优化: `一键下载`功能添加对非`分页查询类型`的链接的支持  
        + 这个功能优化是[针对下载视频清晰度模糊的补救措施](https://nicennnnnnnlee.github.io/BilibiliDown/guide/frequently-asked/how-to-redownload-videos)  
        某种程度上可以做到根据提供的BV号列表进行批量下载(**不宜过多!不宜过多!不宜过多!**)
    * 优化: Tab页面预览图更改为显示当前页的第一个视频的预览图  
    * 优化: 辅助配置面板增加筛选功能  
    * 优化: 移除操作菜单中的`转换仓库`功能  
    * 优化: release添加附件SHA1校验值
    * 优化: 程序以及ffmpeg额外上传至多个渠道(白嫖不稳定，仅供备用)   
    
* V6.17  `2022-09-30`  
    * 修复: [issues 114](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/114) 使用带指纹的cookies查询用户上传视频
    * 优化: CI - 新增人工触发并生成程序到artifacts的工作流，目的是减少Release频率。
    
* V6.16  `2022-09-13`  
	* 调整：各登录方式额外保存`refresh_token`信息，用于Cookie刷新
    * 新增：支持Cookie刷新操作
    * 新增：程序使用说明文档<https://nICEnnnnnnnLee.github.io/BilibiliDown>    
    * 新增：新推出**一键下载功能**，点击一下，下载配置定义的所有。    
		你只需要花3min写一个配置，以后会方便很多。详见[进阶使用-通过配置一键下载](https://nICEnnnnnnnLee.github.io/BilibiliDown/guide/advanced/quick-batch-download)  
    * 优化：UP主合集链接解析现只需要发出两次查询的网络请求。  
		现在大部分链接解析的网络请求次数并不与分页大小相关，您可以**尝试**将该配置由`5`改为`20`。  
		即`bilibili.pageSize = 20`
    * 优化：为了避免文件名过长而出现问题，现将字段`clipTitle`从**默认配置**的文件名中移除
	* 优化：更新扫码登录API
    * 修复：Linux快捷方式的运行方式没有指定正确的工作目录  
* V6.15  `2022-08-20`  
    * 优化：补充实现在Windows平台外的一键更新功能
    * 优化：自定义Button添加抗锯齿设置[issues 107](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/107) 
    * 优化：自定义文件名的格式字符串新增否定类型的条件语句 
    * 优化：`package.sh`换行符去掉 `\r`[issues 107](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/107)  
    * 优化运行、更新的脚本逻辑[issues 110](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/110)  
    
* V6.14  `2022-07-28`
    * 优化: [issues 87](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/87) 可以通过配置`app.config`设置下载任务的相关间隔  
    ```
    #每个下载任务完成后的等待时间(ms)
    bilibili.download.period.between.download = 0
    #每个关于下载的查询任务完成后的等待时间(ms)
    bilibili.download.period.between.query = 0
    ```
    * 优化： 检查更新时，将显示最新版本的CHANGE LOG
    * 修复`V6.13`短信验证码登录引起的用户名密码登录失败的问题  
    
* V6.13  `2022-07-28`  
    * 优化：减少release构建的配置数量, 预防版本号与实际不相符的问题
    * 优化：UP主所有视频解析
        + 增加url类型`https://space.bilibili.com/378034/search/video?tid=3&keyword=歌曲&order=stow`
        + 更换api，减少搜索url类型外的解析的网络请求次数
    * 新增： 合集解析
        + 针对url类型`https://space.bilibili.com/593987248/channel/collectiondetail?sid=508765`
        + 该类型解析会针对合集的每一个BV进行查询，导致较多网络请求。  
            请不要在短时间内打开过多该类型Tab页，以免被BAN
    * 新增： 视频列表解析
        + 老版本频道channel解析仍然生效，但在网页端已经找不到该类型的链接了  
        `https://space.bilibili.com/378034/channel/detail?cid=189`
        + 新版本针对的链接类型如下  
        ```
        https://space.bilibili.com/378034/channel/seriesdetail?sid=918669
        https://www.bilibili.com/medialist/play/378034?from=space&business=space_series&business_id=918669&desc=1
        ```
        + 以上三种链接内容实质上是相同的  
    * 新增： 可通过配置文件设置System Property
    * 新增： 可通过配置文件设置HTTP请求的UserAgent
    * 新增： 短信验证码登录方式  
    * 新增： 用户名密码登录方式  
        + 需要打开浏览器，人工通过极验验证码
        + 如果提示风控，请更改密码之后再进行尝试
    * 修复： java11 高分辨率时界面的缩放比不恰当的问题
    * 修复： 运行时增加`-Dhttps.protocols=TLSv1.2`参数，防止tls handshake failure
    
* V6.10  `2022-07-24`  
    * 新增：Release打包增加了附带精简jre11的选项, 为没有Java环境的win64用户提供了另一种可能。    
      不要再问我
      [#99](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/99)
      [#68](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/68)
      [#12](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/12)
      为什么`win找不到javaw?`了，真的。
    * 新增：尝试外链MP4解析方式，该方法无法选择清晰度，一般返回1080P，不需要音视频分离再使用ffmpeg合并  
        `bilibili.format = 0 for MP4(音视频分离合并), 1 for FLV, 2 for MP4(无法选择清晰度)`
        
        如果你是Win64用户,且没有java环境，请下载`*.jre11_win_x64.zip`
* V6.9  `2022-06-27`  
    * 新增：下载任务保存/加载功能
    * 新增：恢复对MP4/FLV下载格式优先选择的支持
    * 修复：将路径分隔符统一替换为当前系统分隔符，修复在非Windows平台可能存在的问题
    * 删除：去除密码登录模式
* V6.8  `2021-12-07`  
    * 新增：普通视频新增8K清晰度(测试BV1KS4y197BN)  
    * 修复：撤销对UI的改动[issue#84](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/84)  
* V6.7  `2021-11-21`  
    * 修复弹幕内容为空的问题(测试BV1o44y1e7oU)[issue#81](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/81)  
* V6.6  `2021-11-14`  
    * 修复: 去除路径中可能存在的非法字符`\t`[issue#79](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/79)  
* V6.5  `2021-10-16`  
    * 修复：添加跳过HTTPS证书认证选项[issue#77](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/77)  
    * 优化： 在自定义文件名配置项中，可以添加收藏时间和更新时间
        + 例如，下面配置的可能的文件名称如右边：**标题-211016-pn1-小标题-80**
        ```
        bilibili.name.date.favTime.pattern = yyMMdd
        bilibili.name.format = avTitle-(:favTime favTime-)pDisplay-clipTitle-qn
        ```
    * 优化： 在自定义文件名配置项中，序号可以指定宽度，不足补零。原来的用法不受影响。
        + 例如`pAv2-pDisplay3`, 对应名称可能为`p01-pn001`、`p02-pn002`...`p111-pn111`
    * 优化版本更新功能，使得windows下直接双击update.bat脚本也能更新
    * 给MenuBar设置PreferSize
* V6.4  `2021-09-13`  
    * 修复：解决合并分段的flv文件时有可能陷入死循环的问题[issue#72](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/72)  
    * 移除：自定义host功能  
* V6.3  `2021-05-02`  
    * 优化：收藏夹解析增加对不同种类的`已失效视频`的兼容处理[issue#66](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/66)  
* V6.2  `2021-04-21`
    * 修复：B站API调整导致封面图获取失败的问题[issue#63](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/63)  
    * 优化：增加设置页面
    * 优化：将配置读取改为通过注解反射遍历完成；
    * 优化：将部分功能函数移至其应有的模块
* V6.1  `2021-03-25`
    * 修复: 网络请求时去掉br支持，解决有概率返回乱码的问题[issue#58](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/58)   
    * 优化：`UP主所有视频`链接支持`分区`、`关键字搜索`、`按更新时间、播放次数、收藏量排序`。详见[issue#57](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/57)
    * 全部任务完成后可添加提示音，且支持自定义提示音  
        + 打开该功能`bilibili.download.playSound = true`  
        + 将音频文件更名`notice.wav`并放入`config`文件夹即可  
        + 注意：只支持wav格式  
        + 注意：提示音播放在监视进程中进行，时间过长将阻塞线程，影响体验   
    * 优化: 将权重排序移至加载完毕后进行，而不必每次都要排序一遍  
    * 修复: 图片验证码接口增加`BiliDroid`字段  
* 增加各版本release时间
```
query { 
  user(login:"nICEnnnnnnnLee"){
    repository(name:"BilibiliDown"){
      createdAt,
      releases(first:100, orderBy:{field:CREATED_AT, direction:DESC}){
        nodes {
          name,
          createdAt,
          publishedAt,
        }
      }
    }
  }
}
```   
* V6.0 `2021-03-04`
	* 添加登出(即注销登录状态)功能  
    * 添加下载完成后点赞功能(**【测试】**默认关闭，`bilibili.download.thumbUp = true`可打开)
    * 修复 [issue#53](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/53) 修复用户名密码登录失败的问题  
    * 增加`@Bilibili`注解的weight权重属性，使得解析器按权重顺序生效  
    * 修复一个bug，该bug使得某些配置下视频id号不能转为数字时会出现错误
    * 标签页增加了右键菜单，可实现批量关闭功能
    * 标签页增加了右键菜单，可实现批量下载功能(优先清晰度跟随Tab默认值`bilibili.tab.download.qn`)
* V5.9 `2020-10-29`
	* 去除番剧支持  
	* 优化 [issue#46](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/46) 自定义文件名提供数字av号选择  
	* 修复 [issue#47](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/47) 修复标题中含有换行符、退格符时无法重命名的情况  
* V5.8 `2020-10-02`
    * 升级密码登录API为V3版本  
    * 升级弹幕下载保存格式为ass(测试,原来仅下载xml)  
    * 去除原背景图片[b站壁纸娘 - 22&33](https://h.bilibili.com/597708)   
    * 新增自定义背景图片功能(将`background.jpg`或`background.png`放在`config`文件夹下)  
    * 新增自定义解析功能(将实现的`xxxParser.java`放在`parsers`文件夹下)  
	* 新增`真彩 HDR`画质支持(`BV1rp4y1e745`)  
	* ps0: 推荐背景图[唧唧看板娘——唧娜](https://blog.jixiaob.cn/?post=14)  
	* ps1: 紧跟潮流，考虑在以后的某个版本去除对番剧的支持  
    * ps2: 自定义解析功能如果需要指定加载顺序，请将`parsers.ini`放在`parsers`文件夹下，其内容举例：  
      ```
      AbstractBaseParser
      BVParser
      B23Parser
      ```
* V5.7 `2020-08-01`
    * 新增b23.tv短链接解析(<https://b23.tv/U9SiGZ>)  
    * 多线程下载时去除Accept-Ranges头部判断
    * 新增内置hosts功能，防止github域名被污染导致版本查询失败
* V5.6 `2020-06-19`
    * 修复一个bug，该bug导致某些类型的互动视频查询不全  
    * [issue#37](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/37)添加多线程下载实现。该功能默认关闭，不建议开启。具体使用详见`app.config`注释
* V5.5 `2020-06-07`
    * 修复一个bug，该bug导致某些类型的互动视频查询会陷入死循环  
    * 优化 [issue#34](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/34) 实现用户名密码自动登录  
    * 尝试一种新的button按钮样式，如不适应，可在配置文件中回退，设置如下：  
        `bilibili.button.style = default`  
    
* V5.4 `2020-06-03`
    * 版本更新时，从后台下载改为列入下载面板下载，可以直观看到下载进度
    * 新增课程解析  
        * <https://www.bilibili.com/cheese/play/ep1808>  
        * <https://www.bilibili.com/cheese/play/ss117>
    * 修复 [issue#33](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/33) 
        * <https://www.bilibili.com/video/BV1bb411Y7HH>  
        * <https://www.bilibili.com/video/BV1De411s71p>  
* V5.3 `2020-05-30`
    * 完善 继番剧4K[av56995872](https://www.bilibili.com/video/av56995872)后，支持UP主4K视频下载
        * 测试[BV1xV411C7UF 4K50帧](https://www.bilibili.com/video/BV1xV411C7UF)  
        * 测试[BV1fK4y1t7hj 4K120帧](https://www.bilibili.com/video/BV1fK4y1t7hj)  
* V5.2 `2020-05-17`
    * 完善 当某ss下p数超过20时，不再为每个视频详细查询支持清晰度，减少网络请求(以[ss33378](https://www.bilibili.com/bangumi/play/ss33378)为例，集数过千。。。)  
    * 修复 [issue#31](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/31) 为了适应flv合并的ffmpeg命令调用，保存文件夹配置路径末尾转化为`/`  
    * 修复 [issue#31](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/31) 为了解决某些ffmpeg应对不了的flv合并情况，增加了该功能的java实现
* V5.1 `2020-05-01`
    * 新增 为Windows用户增加了FFmpeg.exe下载功能(指向自编译的上传在Release assets的附件)，进一步小白化  
	* 完善 当某av下p数超过20时，不再为每个视频详细查询支持清晰度，减少网络请求(特别是某些教程，以BV1pt41127FZ为例，分p数约400~)  
	* 完善 打包脚本新增cd到文件所在目录操作，进一步防止误操作  
* V5.0 `2020-04-11`
    * workflow发布测试，功能上无更新
    * 提供了`package.sh`和`package.bat`两个脚本，支持不依赖IDE的jar包打包生成(**内含删除操作，请注意工作目录务必正确！！！**)
* V4.9 `2020-03-31`
    * 新增 [#27](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/27) 稍后再看的批量下载
    * 修复 [#28](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/28) up所有视频下载解析失效问题
* V4.8 `2020-03-25`
    * 本地进行av和bv相互转换，减少网络请求  
* V4.7 `2020-03-24`
    * 修复v4.6引入的多p视频只显示第一个的bug  
* V4.6 `2020-03-24`
    * 新增BV Parser  
    * 修复Av/EP/SS/ML/UP主频道/UP主所有视频解析  
    * 因B站API修改，下载历史改用BV作为关键词，提供了低版本到高版本的历史记录转换功能  
    * 修复一处可能存在非法文件路径的bug  
    * 精力有限，**不再支持调整优先下载格式(默认mp4-DASH优先)**  
    * 精力有限，**不再支持调整查找集合的分页弹出(默认在一页显示)**  
    
* V4.5 `2020-02-06`
    * 修复：4K返回1080P+的问题，现已正常[av56995872](https://www.bilibili.com/video/av56995872)
    * 优化：账号登录后可获取收藏夹，不必再专门打开网页复制url了
    * 优化：批量下载的默认设置可以在`app.config`中配置
    * 优化：防止程序在运行时被重复打开。该功能配置默认关闭，`bilibili.lockCheck=true`可打开
    * 优化：增加过渡动画，加载完毕后再显示界面，防止卡顿体验。双击动画可迅速跳过。
    * 其它：下载控制、Httpheader整理、预览图片链接复制的一个bug
* V4.4 `2020-01-29`
    * 优化：收藏夹对应的listName去掉分页数，自定义下载名称更加合理
    * 修复：[issue#21](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/21) 当API返回的首选下载链接失效时，使用备用链接
    * 修复：[issue#21](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/21) 当av不存在音频时，增加处理逻辑
* V4.3  `2019-12-18`
	* 修复： 如果channel中存在已失效视频，那么该分页中已失效视频之后的视频信息将全部丢失
	* 新增： 相簿/画廊解析
        * `https://space.bilibili.com/[0-9]+/favlist?fid=albumfav` 相簿收藏夹√
        * [`https://h.bilibili.com/38894082`](https://h.bilibili.com/38894082)  单个相簿√  
        * [`https://space.bilibili.com/20358094/album`](https://space.bilibili.com/20358094/album) 某Up的所有相簿× `没有付出的爱是廉价的，你连收藏都不肯😳`
	* 优化：自定义文件名
        * 现在支持路径分隔符`/\`，e.g. 可将同质的某些视频放入同一个文件夹中
        * 增加可自定义字段 阿婆主名称/id。详见[app.config](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/src/resources/app.config)
* V4.2 `2019-10-19` 
	* 修复bug： 关闭扫码图/关于框时，如果有活动的任务，会错误地弹出提示
	* 去除WebSocket依赖，实时弹幕相关可以参考[弹幕点歌姬](https://github.com/nICEnnnnnnnLee/DanmuMusicPlayer)
	* 部分util优化
* V4.1 `2019-09-28`
    * 增加HTTP Deflate解析
    * 增加弹幕下载(提供下载链接 => 直接下载文件)
    * 修复收藏夹第一个为已失效视频，则无法解析的bug
    * 针对非Windows系统进行了部分适配工作
* V4.0 `2019-09-15`
    * 新增互动视频下载(如av64006660)
* V3.9 `2019-09-01`
    * 新增CC字幕下载，保存为`srt`格式(如果存在的话，比如av34218168)
* V3.8 `2019-07-15`
    * 新增app独享视频下载(PC端不能看，以av2478750为例)
    * 增加4K清晰度(以av56995872为例)
    * 包扫描机制优化
    * INeedAV的Main入口做了部分优化
* V3.7 `2019-05-25`
    *  [issue#10](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/10)新增`正在转码状态`,细化下载任务状态提示  
    * 增加ffmpeg环境判断与提示  
    * 修复菜单里的Repo重载刷新问题  
    * Tab结果页里面av各p标题名称分情况显示  
    * 退出时如有活动任务，将给出提示
* v3.6 `2019-05-12`
    * 优化下载异常处理，失败后自动重新添加到下载队列(默认3次后停止,`bilibili.download.maxFailRetry = 3`)
    * 优化自定义名称
        * 目标是使通过`收藏夹` 和 `单独av`等不同方式得到的 `av标题` 和 `视频小标题` 均保持一致
        * 增加 `集合名称/拥有者` 字段，可通过条件来命名(也就是没有这个字段时不算数)
        * 更多请查看`[app.config](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/src/resources/app.config)`
    * 优化ToolTip提示，鼠标悬浮可显示内容过长时省略的内容
    * 增加菜单功能 - 配置/仓库文件改动后，可直接重新加载，而不必重启程序(某些设置必须重启的除外)
    * 增加功能 - 复制弹幕下载链接(实验)
    * ~~WebSocket接收实时弹幕~~然而并没有，鸡肋
* v3.5 `2019-05-01`
    * 修复`bug` - 当实际清晰度低于预期下载清晰度时，如果下载过程中有停止再继续的行为，将导致重命名失败，且清晰度失真
    * 修复`bug` - `V3.4`因UI更新引入的批量下载优先清晰度始终为`1080P60`的问题
    * 主程序将菜单栏移到标题栏中
    * `扫码`和`关于`界面自定义标题栏，防止系统主题下不出现关闭按钮
    * `README`预览图压缩，节省流量
* v3.4 `2019-04-30`
    * 增加功能 - 下载任务面板根据任务状态的不同，显示不同的背景色
    * 增加一键更新功能
    * 增加`关于`页面
    * UI优化 - 增加菜单栏
    * 其它微不足道的改动
* v3.3 `2019-04-27`
    * 批量下载时，针对已下载弹出框提示太多的情况，做了优化
         *  增加弹出框提示开关，在配置文件里面设置后，可以不再提示
         *  弹出框增加关闭所有提示框按钮，点击可以关闭所有提示
         *  增加最大弹出框数量限制，超出后不再弹出提示（默认为5）
* v3.2 `2019-04-25`
    * 修复下载路径不存在时，程序关闭不了的问题
    * 双击视频某p Title可获取作品信息(适用于批量打开的情况)
    * 长按视频某p Title可更新预览图
    * 点击预览图可获取图片链接
    * `parser`遍历选择时增加break，减少不必要的循环  
    * 解决文件路径中含多个空格问题
* v3.1 beta `2019-04-18`
    * 程序标题显示版本号
    * 代码重构，整个框架有较大改动  `beta`
    * 增加批量查询的呈现方式，可以全部放在一页里面，也可以打开Tab页(将所有视频设为默认呈现，没有为所有视频再详细查询支持清晰度  `beta`
    * 增加下载完成记录保存功能 `beta`
    * 修复临时文件误删除的问题 
    * 修复一个cookie相关的问题 - cookie在验证无效后，继续使用会导致后续请求被拒，应当置空
    * 纠正下载文件命名中的清晰度值  
    * 纠正输入框直接右键粘贴 和 PlaceHolder的逻辑处理问题  
    * 纠正解析失败后`Enter`键的处理，输入框`Enter`键的监听事件`KeyReleased` -> `KeyPressed` 
    * 纠正下载ss剧集时属于av第几p的问题 
    * 优化查询分页API，不再有20页限制    
    * 优化清晰度字典，使用`Enum`
    * 优化从下载任务栏打开文件夹功能，打开时选中文件(如果文件存在)
    * 优化UP主个人全部主页匹配规则
    * 优化重命名功能，可以根据需要配置下载文件名(请注意,使用不同解析方式得到的下载文件名可能不同,例如avXXX和打开收藏夹mlXXX后对应的avXXX不会相同,后者会包括更多信息)
    * 优化其它逻辑
    * 去掉等待动图
    
* v3.0 `2019-04-13`
    * 修复下载面板任务过多时，下拉到底不能列出所有任务的问题  
    * 批量下载优先策略增加```1080P60```、```720P60```选项  
    * 下载面板增加批量下载控制选项 
    * 查找输入框增加`Enter`快捷键响应  
    * 程序关闭，以及每次ffmpeg转换完毕，增加删除所有临时文件判断
    * 新增跟随系统主题，与swing默认有所区别(测试中)    
    * 其它UI优化  
* v2.9 `2019-04-11`
    * 新增功能 - 批量下载av的所有视频  
    * 新增功能 - 批量下载所有打开的标签页的视频  
    * 其它UI改动  
* v2.8 `2019-04-10`
    * 主页输入框添加右键菜单  
    * 更换收藏夹信息获取api，并增加```mlXXX```形式的解析   
    ```https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=XXX&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp```  
    改为  
    ```https://api.bilibili.com/medialist/gateway/base/detail?media_id=XXX&pn=%d&ps=%d```  
    (前者需要personID参数构造header，否则没有权限)
    * 分页信息查询的最大个数可以在```app.config```中灵活设置
* v2.7 `2019-04-09`
    * Tab页标题过长时, 省略部分内容, 以...代替  
    * 增加SOCKS/HTTP/HTTPS代理支持   
    * release 压缩包去除好压    
* v2.6  `2019-03-23`
    * 解决部分视频下载不完整问题 - 发现电影是分段播的，原来的方式只能下载大概前5~6分钟，例如<https://www.bilibili.com/bangumi/play/ss10007>
    * 因为不怎么使用登录功能(一次登录cookies可以用很久)，以前未发现并处理因为网络原因造成的异常，现已解决
* v2.5  `2019-03-10`
    * 增加收藏夹的连接解析，例如<https://space.bilibili.com/3156365/favlist?fid=75463865>(url请务必包含fid参数)
    * 增加UP主个人页面的链接解析，例如<https://space.bilibili.com/5276/video> 
    * 增加UP主个人页面特定频道的链接解析，例如<https://space.bilibili.com/378034/channel/detail?cid=189>     
    * 修复某些链接的异常解析，例如<https://www.bilibili.com/video/av41515020?from=search&seid=11021327663579949519>
    * 增加卸载脚本```unistall.bat```(其实就是删除文件夹。。。)
    * 当前个人页面获取信息顺序为最新发布，且固定为每页5个(官网每页30个)，以防止跳出的Tab页面过多。想要获取请在后面加上p=[pageNumber]。
    e.g. 想要下载UP主```Hivane```最新发布的第31~35个视频(需确保后面没有空格)    
    ```https://space.bilibili.com/17154307/video?tid=0&page=2&keyword=&order=pubdate p=7```
* v2.4  `2019-03-07`
    * 增加官方番剧URL集合的链接解析，例如<https://www.bilibili.com/bangumi/play/ss25739> 
    * 增加官方番剧URL集合的链接解析，例如<https://www.bilibili.com/bangumi/media/md134912>     
    * (UI)优化最大同时下载数的显示
    * (UI)修复并优化部分UI显示
* v2.3  `2019-03-06`
    * (UI)增加下载速度显示   
    * (UI)新增vbs脚本，可以创建桌面快捷方式  
    * (UI)配置文件统一移入config文件夹中
    * 当选择为MP4而目标源仅存在FLV时，优化为自动切换FLV  
    * 修复一个bug，该bug使得在调用ffmpeg转码时，有概率会失败卡住  
    * 增加官方番剧URL(单集)的链接解析，例如<https://www.bilibili.com/bangumi/play/ep250435>  
    * 增加下载番剧的功能(以前一直测试的UP主上传的视频，突然发现追番的话似乎有点不同，于是增加了这个功能)       
    * PS： ep号转av号是直接从HTML里面爬出来的，不够优雅，暂时还没提取出提供api接口...
* v2.2  `2019-03-02`
    * 增加了HTML5播放源的下载方式,支持FLV/MP4两种格式 
    * (UI)下载面板优化为不允许存在相同的视频下载任务(不分辨清晰度)
    * (UI)在下载目录下增加了重命名```rename.bat```，默认格式为```avId-qn-p.(flv|mp4)```，可以使用该批处理批量改标题为```视频标题-qn-p.(flv|mp4)```(重命名功能暂只支持Windows)
    * (UI)增加了下载格式配置(flv 取Flash播放源, mp4 取HTML5播放源)
    * (UI)增加了下载路径配置
* v2.1 `2019-03-01`
    * 增加了断点续传的下载功能, 如果发现上次未下载完成的```.part```文件,会在上次的基础上继续进行下载;
    * (UI)新增暂停/下载异常后继续下载功能, 与断点续传功能相匹配;
    * (UI)在作品详情页面点击文字可以复制信息;
    * 考虑过把一个视频分成很多Fragment多线程下载的, 但这样似乎对服务器不是很好, 并且可以预见会有很多bug(这点最重要??), 再加上本身已经能够同时下载多个不同的av, 故而并没有继续;
    * 考虑过使用HTML5的播放源, ```.m3u8```的直接合并就行, 但像这种```.m4s```,木有经验额??. 如何解析报头的**SegmentBase**, 如何合并音视频, 目前正在潜水学习中... 关键是没有多媒体处理经验,不会ffmpeg
    
* v2.0 `2019-02-24`
    * 修复一个bug,该bug导致部分无效cookies验证抛出异常,而不是返回false;
    * (UI)修复一个bug,该bug使得扫码登录后未能及时更新用户头像等信息;
    * (UI)增加二维码扫码时限性,一分钟后自动销毁;
    * (UI)增加登录框点击动态效果,让人明白你点了它;
    * 下载flv名称由 ```avId-p.flv``` 改为```avId-qn-p.flv```,增加清晰度标识
    * (UI)优化了.bat脚本,```run-UI.bat```运行后命令窗口退出,```run-UI-debug.bat```运行后命令窗口留存,并且可查看输出信息

...
* 更古老的不再记录