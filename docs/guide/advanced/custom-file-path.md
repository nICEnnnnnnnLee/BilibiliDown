# 自定义下载保存路径
+ 使用文本编辑器打开`config/app.config`

+ 找到`bilibili.savePath`，修改它的值

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