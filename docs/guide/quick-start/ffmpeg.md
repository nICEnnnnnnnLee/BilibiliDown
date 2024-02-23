# 设置ffmpeg路径

程序默认会下载分离的音频和视频，然后进行合并，这会使用到`ffmpeg`。   



+ 对于Windows、Linux用户，程序默认会下载`ffmpeg.exe`(3MB)到文件夹下，因而**无需配置**。  
    + 通过像BV号/视频链接一样在首页输入`ffmpeg`，也可以唤出相应下载页
+ 对于其它的用户，需要人为指定`ffmpeg`的位置。  
    + 使用文本编辑器打开`config/repo.config`
    + 设置参数`bilibili.ffmpegPath`，下面是参数释义
    ::: details bilibili.ffmpegPath
    - 取值范围: 符合路径命名规范，不包含非法符号  
    - 默认值: `ffmpeg`  
    - 释义:  
        ffmpeg环境配置
    - 举例:  
        `bilibili.ffmpegPath = D:\BilibiliDown\ffmpeg.exe`  
        `bilibili.ffmpegPath = /usr/local/bin/ffmpeg`
    :::
    + Mac用户可以参考[issues#32](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/32)

+ 当然，下载`FLV`或独立的`MP4`文件可以避免`ffmpeg`的使用。  
请参考[进阶使用 - 设置视频格式](/guide/advanced/media-type-format)。