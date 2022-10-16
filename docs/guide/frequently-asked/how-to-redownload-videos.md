# 批量下载的清晰度不符合预期，如何解决？

## 问题背景
`我`用程序下载收藏夹/UP主合集视频，以前用的好好的，清晰度也没问题。  
但是最近下载的视频的清晰度都比较模糊，不符合预期。怎么把这些视频再下回来？  

## 为什么会出现这样的问题？
视频API接口不是一成不变的，它会频繁地变动。  
如果程序不跟着进行更新，那么原先的功能也将出现异常。  

## 解决思路
### 首先，得更新版本或修改配置，确保下载后的视频符合预期。  
比如，版本`V6.17`，单独的MP4现在只能获取到360P的画质视频。  
我们需要改为音视频分离下载再合并，即设置`bilibili.format = 0`。  
请参考[进阶使用 - 设置视频格式](/guide/advanced/media-type-format)

### 其次，我们得获取低糊视频记录，确定哪些要重新下载。  
文本编辑器打开`config/repo.config`, 找到那些清晰度为`16`的下载记录。  
在额外保存到其它地方后，删掉这些行。    
```
BV1cU4y1u7nM-208-p1
BV1Yf4y1K7Gs-16-p1
BV1Ag411U7nf-16-p1
BV1pY411E7Y9-16-p1
...
BV1x7411f7MM-16-p1
BV13g41197sr-16-p1
BV13g41197sr-16-p2
BV1qP411J7LP-16-p1
BV1x7411f7MM-16-p2
BV1kV4y1K7Z9-16-p1
BV1Ee411u7EM-16-p1
BV1jY4y157No-16-p2
BV1DG41157Z1-16-p1
BV1CB4y1g7sF-16-p1
```

### 再次，使用一键下载功能模拟曾经的下载行为。  
假设`我`平时只下载`收藏夹1`、`收藏夹2`的视频，每隔一段时间`查找`、`下一页`、`下一页`...直到弹出已下载。   
那么对应的一键下载配置如下：  
```
[favorite:收藏夹1,收藏夹2]
# 表示 遇到下载过的就停止查询
stop.condition = _:downloaded

# 表示 只下载没有下载过的
download.condition = _!downloaded
stop.bv.bounds = exclude
stop.alert = false
```

具体来讲，新建`config/batchDownload.config`并用文本编辑器打开，复制上述内容并以`utf-8`编码保存。  
在菜单配置优先清晰度，然后点击`一键下载`。  
![](/img/batchDownload.png)

<hr/>

以上配置对应的是日常的追更操作，现在要重下部分视频，所以不能遇到已经下载过的就停止查询，得换成页数到了20就停止。  
```
[favorite:收藏夹1,收藏夹2]
stop.condition = page:20

# 表示 只下载没有下载过的
download.condition = _!downloaded
stop.bv.bounds = exclude
stop.alert = false
```

具体来讲，新建`config/batchDownload.前20页下载重试.config`并用文本编辑器打开，复制上述内容并以`utf-8`编码保存。  
在菜单配置优先清晰度，选择一键下载配置文件，然后点击`一键下载`。  
![](/img/batchDownload-select.png)  


<hr/>

通过以上配置下载的视频，可能在额外保存的记录里面，也可能不在。  
这种数量可能多也可能少的情况，不方便我们对比记录进行查漏补缺。  
我们得确保只下载额外保存的记录里面的视频，以下为记录对应的相应配置。  
```
[favorite:收藏夹1,收藏夹2]
stop.condition = page:20

download.condition = bv:BV1Yf4y1K7Gs
download.condition = bv:BV1Ag411U7nf
download.condition = bv:BV1pY411E7Y9
...
download.condition = bv:BV1CB4y1g7sF
stop.bv.bounds = exclude
stop.alert = false
```

在下载完毕后，通过`repo.config`的前后变化与额外保存的记录进行比较，剩下的漏网之鱼应该不是通过合集方式下载的，可以参考下述步骤进行处理。  


### 最后，使用一键下载功能补充下载指定内容。  
举例，下载`BV17T411K7gi`，配置如下：  
```
[url:BV17T411K7gi]
# 表示 不会停止查询(因为不是分类查询类型的默认在page为2时会自动停止查询)
stop.condition = _!_
# stop.condition = page:2
# 表示 查询到的视频都会尝试加入到下载队列
download.condition = _:_
## 表示 只下载没有下载过的
##download.condition = _!downloaded
# 表示 此轮查询结束后不弹出提示
stop.alert = false
```

对于下载记录  
```
BV1Yf4y1K7Gs-16-p1
BV1Ag411U7nf-16-p1
BV1pY411E7Y9-16-p1
...
```

对应的配置为
```
[url:BV1Yf4y1K7Gs]
stop.condition = _!_
download.condition = _:_
stop.alert = false

[url:BV1Ag411U7nf]
stop.condition = _!_
download.condition = _:_
stop.alert = false

[url:BV1pY411E7Y9]
stop.condition = _!_
download.condition = _:_
stop.alert = false

...
```

具体来讲，新建`config/batchDownload.下载指定内容.config`并用文本编辑器打开，复制上述内容并以`utf-8`编码保存。  
在菜单配置优先清晰度，选择一键下载配置文件，然后点击`一键下载`。  
![](/img/batchDownload-select.png)  