# 支持链接

|视频类型     |示例链接|备注|
|-              |-      |-   |
|普通视频|`av498806761`<br/><br/>`BV1eK411H7Hq`<br/><br/>`https://b23.tv/U9SiGZ`<br/><br/>`https://www.bilibili.com/video/BV1eK411H7Hq` <br/><br/>`https://www.bilibili.com/video/av498806761`| 包括互动视频   |
|课程视频|`https://www.bilibili.com/cheese/play/ep1808`<br/><br/>`https://www.bilibili.com/cheese/play/ss117`|-   |
|音频|`https://www.bilibili.com/audio/au3688627`<br/><br/>`https://www.bilibili.com/audio/am33012874`<br/><br/>`https://www.bilibili.com/audio/mycollection/[0-9]+`|-   |
|单个相簿|`https://h.bilibili.com/38894082`|-   |
|相簿收藏夹|`https://space.bilibili.com/{spaceID}/favlist?fid=albumfav`|-   |
|~~番剧视频~~|`https://www.bilibili.com/bangumi/play/ss33378`<br/><br/>`https://www.bilibili.com/bangumi/media/md134912`<br/><br/>`https://www.bilibili.com/bangumi/play/ep250435`|**V5.9以后不再支持**  |
|稍后再看|`https://www.bilibili.com/watchlater/#/list`|-   |
|收藏夹|`mlxxxx`<br/><br/>`https://www.bilibili.com/medialist/detail/ml761171511`<br/><br/>`https://space.bilibili.com/492744983/favlist?fid=933034683`| V6.23开始，收藏夹解析也包含音频   |
|UP主合集|`https://space.bilibili.com/{spaceID}/favlist?fid=405855&ftype=collect&ctype=21`<br/><br/>`https://space.bilibili.com/593987248/channel/collectiondetail?sid=508765`|-  |
|UP主视频列表|`https://space.bilibili.com/378034/channel/seriesdetail?sid=918669`<br/><br/>`https://www.bilibili.com/medialist/play/378034?from=space&business=space_series&business_id=918669&desc=1`|- |
|UP主所有视频|`https://space.bilibili.com/378034/`<br/><br/>`https://space.bilibili.com/378034/video`<br/><br/>`https://space.bilibili.com/378034/video?tid=3&keyword=&order=stow`<br/><br/>`https://space.bilibili.com/378034/search/video?tid=3&keyword=&order=pubdate`<br/><br/>`https://www.bilibili.com/medialist/play/378034?from=space&business=space&sort_field=play&tid=3`| 支持 最新发布、最多播放、最多收藏分页查询<br/><br/><br/><br/>支持关键词搜索,但此时网络请求数会增多  |

## UP主所有视频的链接参数
```
https://space.bilibili.com/1276787/video?tid=0&keyword=&order=pubdate

以下这些参数可以是与浏览器里面的地址一致的，当然你也可以根据需要自己填写
- tid: 分区
  - 全部 0
  - 游戏 4
  - 知识 36
  - 生活 160
  ...

- keyword: 关键词

- order: 顺序
  - 最新发布 pubdate 
  - 最多点击 click 
  - 最多收藏 stow
```

