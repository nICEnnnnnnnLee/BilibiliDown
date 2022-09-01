# 为什么总是弹出已经下载？

方法1：  
+ 使用文本编辑器打开`config/repo.config`
+ 删除提示已经下载的记录

方法2：  
+ 使用文本编辑器打开`config/app.config`

+ 找到`bilibili.repo`和`bilibili.alert.isAlertIfDownloded`，酌情修改它们的值


## bilibili.repo
- 取值范围: `on | off`
- 默认值: `on`
- 释义:  
    + 当值为`on`时, 每次下载前都会先从仓库查询记录。 若存在，则不开始任务
    + 当值为`off`时, 每次直接下载

## bilibili.repo.save
- 取值范围: `on | off`
- 默认值: `on`
- 释义:  
    + 当值为`on`时, 即使仓库功能关闭，仍保存下载成功的记录
    + 当值为`off`时, 在仓库功能关闭时，不再保存下载成功的记录

## bilibili.repo.definitionStrictMode
- 取值范围: `on | off`
- 默认值: `off`
- 释义:  
    + 当值为`on`时, 同一视频两种清晰度算不同记录
    + 当值为`off`时, 同一视频两种清晰度算相同记录

## bilibili.alert.isAlertIfDownloded
- 取值范围: `true | false`
- 默认值: `true`
- 释义:  
    + 当值为`true`时, 在`bilibili.repo=on`的情况下，下载已完成的视频时，弹出提示
    + 当值为`false`时, 下载已完成的视频时，始终不弹出提示

## bilibili.alert.maxAlertPrompt
- 取值范围: 数字
- 默认值: `5`
- 释义:  
    批量下载时，最大提示框弹出数
