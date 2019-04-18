# Bilibili Down Down Down! 之十万个为什么

+ 使用非常规功能时，需要知道的一些配置
    + `config/app.config` - 配置文件
    + `config/cookie.config` - 登录cookie本地保存
    + `config/repo.config` - 下载成功记录仓库 active from `v3.1`
    <details>
    <summary>仓库样例</summary>


    ```
    av9502825-16-p1
    av9502825-16-p4
    av9502825-16-p7
    av9502825-16-p3
    av9502825-16-p5
    ```
    </details>

<details>
<summary>视频没下完，就出现了异常，怎么办？手抖关了程序怎么办？在线等，急急急</summary>


* 不用慌，下载的临时文件还在，重新下载还是上次的进度
</details>
<details>
<summary>怎样修改下载目录</summary>


* 修改`config/app.config`
    * 相对路径 => `bilibili.savePath = download/`
    * 绝对路径 => `bilibili.savePath = D:\Workspace\bilibili\`
    * 在本地测试文件分隔符`/`和`\`似乎都可以 
</details>
<details>
<summary>怎样修改优先下载的视频格式</summary>


* 修改`config/app.config`
    * 优先`MP4` => `bilibili.format = 0`
    * 优先`FLV` => `bilibili.format = 1`
</details>

<details>
<summary>为什么设置的是 `MP4` ，下载的格式还是 `FLV` </summary>


* 设置的格式是**优先下载**，其实对应的分别是```HTML5```/```FLASH```播放方式。  
**但是**，B站如果没有对应源的话那也没辙，例如[西游记之大圣归来](https://www.bilibili.com/bangumi/play/ep116206)就只有`FLASH`。  
这种情况下只能换种方式，所以一定要的话。。。请自行转换格式
</details>

<details>
<summary>我可以自定义下载文件的命名方式吗？ ver>=3.1</summary>


* 通过修改`config/app.config`，可以实现自定义命名  
    ```
    # 下载文件命名格式
    ## avId - av号 e.g. av1234567
    ## pAv - av 的第几个视频  e.g. p1
    ## pDisplay - 合集的第几个视频 e.g. pn2
    ## qn - 清晰度值  e.g. 80
    ## avTitle - av标题 
    ## clipTitle - 视频小标题
    ###    pDisplay 和 pAv 可能不一致, 比如有的ss是分布在不同的av的第一个视频, 有的则是分布在同一av的不同p
    ###    建议使用pDisplay而不是pAv
    bilibili.name.format = avTitle-pDisplay-clipTitle-qn
    FLV==>                     av标题-pn2-视频小标题-80.flv
    bilibili.name.format = 开头av标题-pDisplay中间pAv-qn-pAv结尾
    MP4==>                     开头av标题-pn2中间p1-80结尾.mp4
    ```
</details>

<details>
<summary>我自定义了重命名格式，为什么下载后文件没有变成想要的 ver>=3.1</summary>


* 修改`config/app.config`
    * 下载后马上重命名 => `bilibili.name.doAfterComplete = true`
    * 人工重命名 => 运行下载目录下的`rename.bat`
</details>

<details>
<summary>程序好丑，能不能换个主题</summary>


* 修改`config/app.config`
    * swing默认 => `bilibili.theme = default`
    * 跟随系统 => `bilibili.theme = system`
* 还不满意，那就没法子了😔
</details>

<details>
<summary>有些视频我已经下了，不想再重新再下一遍？</summary>


* 如果视频没有改名也没有移动，程序是不会再下载的(＾Ｕ＾)ノ~ＹＯ)
* 修改`config/app.config` ver>=3.1
    * 开启仓库模式 => `bilibili.repo = on`
    * 开启后成功的下载记录会一直保存在`config/repo.config` (程序对该文件只存在append操作，不想要的记录请人工删除，也可以根据需要人工添加哦)
    * 仓库里的视频将不会再进行下载
    * 同一视频已经有了某清晰度，不想再下 => `bilibili.definitionStrictMode = off`
    * 同一视频已经有了某清晰度，还想再下另一种清晰度 => `bilibili.definitionStrictMode = on`
</details>

<details>
<summary>想下视频，但是提示我已经下载了？ ver>=3.1</summary>


* 请确认你真的没有下载？？
* 请确认是否下过该视频的另一种清晰度的版本？
    * 是 => `bilibili.definitionStrictMode = on`
    * 否，请继续
* 找到`config/repo.config`里的记录，把它删除 或者
* 关闭仓库模式 => `bilibili.repo = off`
</details>

<details>
<summary>我不想保存任何下载记录 ver>=3.1</summary>


* 修改`config/app.config`
    * 关闭仓库功能 => `bilibili.repo = off`
    * 关闭仓库记录功能 => `bilibili.repo.save = off`
    * 删除`config/repo.config`
    * 视频下载完成后马上重命名 => `bilibili.name.doAfterComplete = true`
    * 删除下载目录的 `rename.bat`
    * 下载目录的其它视频等文件请自行斟酌
</details>

<details>
<summary>临时文件太多？</summary>


* 修改`config/app.config`
    * 开启临时文件严格模式 => `bilibili.restrictTempMode = on`
* 出现了预期外的异常，人工删吧，骚年😔
</details>

<details>
<summary>临时文件被莫名删除？</summary>


* 修改`config/app.config`
    * 关闭临时文件严格模式 => `bilibili.restrictTempMode = off`
</details>

<details>
<summary>我在国外，没法下B站视频。。。😳</summary>


* 使用代理，一般是SOCKS代理吧。修改`config/app.config`
    * 设置代理host => `socksProxyHost = 127.0.0.1`
    * 设置代理端口 => `socksProxyPort = 1080`
* 其它代理方式类似，参见配置文件里的备注
</details>

<details>
<summary>收藏夹/频道只打开前面5个页面？</summary>


* 在链接后面加上 `p=2`, 就是第6 到10 个了
* 在链接后面加上 `p=3`, 就是第11 到15 个
* ...
* 例如 `https://space.bilibili.com/8741628/favlist?fid=101422828&ftype=create p=2`
* 例如 `ml101422828 p=3`
* 想一次打开多一点页面
    * 修改配置 => `bilibili.pageSize = 5`
</details>

<details>
<summary>我想把收藏夹/频道结果放到一个页面？</summary>


* 一个页面显示 ==> `bilibili.pageDisplay = listAll`
</details>

<details>
<summary>我想把收藏夹/频道结果每个av分别显示</summary>


* 弹出页面显示 ==> `bilibili.pageDisplay = promptAll`
</details>

<details>
<summary>我想同时下载很多个视频？</summary>


* 修改`config/app.config`
    * 修改线程池大小 => `bilibili.download.poolSize = 3`
    * 这个不是越大越好哦，老铁😄
</details>

<details>
<summary>av页面打开后一直在等待转圈圈</summary>


* 对于已删除视频或违规视频，这是正常的
* 再打开该av试一试，看是不是偶然情况
* 试试其它av
* 如果都不行，请关闭程序，用浏览器打开b站某av，再返回程序试一试
* 老铁，打开```run-UI-debug.bat```，重复操作，看看啥异常，蟹蟹
</details>
