# 补充说明


::: details  如何获取封面链接
单击封面图片获取URL
![](/img/ClickPicToGetUrl.gif) 
:::

::: details 如何更换预览图
长按作品文字，然后对应预览图会更新(网络原因可能会有卡顿，正常)
![](/img/LongClickToChangePreview.gif)
:::

::: details  如何获取作品信息
双击作品文字，然后文本 + `avId`将会复制到剪贴板 
![](/img/doubleClick2CopyClipInfo.gif)
:::

::: details  如何设置代理
在`config/app.config`中配置相应代理类型的地址和端口，关键词`proxy`
:::



::: details  关于清晰度的说明
当因权限不足，或视频不存在该清晰度时，将返回不大于该qn值的合法最大qn值对应的清晰度。(FLV格式另有例外，请看下面的示例)
  
| 清晰度  | qn值 |
| ------------- | ------------- |
| 8K  | 127 |
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
    
* 举例，假设某av存在4K/1080P/720P/480P/360P   
    * 大会员cookie 发起 ```8K``` **FLV**请求 =====>  得到```1080P```链接  
    * 大会员cookie 发起 ```8K``` **M4S**请求 =====>  得到```4K```链接  
    * 大会员cookie 发起 ```4K``` 请求 =====>  得到```4K```链接  
    * 大会员cookie 发起 ```1080P``` 请求 =====>  得到```1080P```链接  
    * 大会员cookie 发起 ```720P60``` 请求 =====>  得到```720P```链接  
:::


::: details  关于互动视频的说明

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
:::

::: details 关于字幕的说明
+ 本程序的逻辑认为字幕是一种特别清晰度的视频，而基于程序**不会同时下载同一视频的不同清晰度的链接的Feature**，  
    + 当下载面板存在视频任务时，其对应的字幕下载任务不会被发起
    + 当下载面板存在字幕任务时，其对应的视频下载任务不会被发起
    + 如有需要，请在下载面板删除对应任务后再继续尝试
+ 如果存在多语种，默认优先下载中文简体`zh-CN`  
+ 如有需要，可在`app.config`更改配置，如：    
```
bilibili.cc.lang = en-US
```  
+ 关于语言的配置，详见[此处](https://github.com/nICEnnnnnnnLee/BilibiliDown/tree/master/release/wiki/langs.txt)  
::: 
