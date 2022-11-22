# 设置视频编码优先级和音频质量优先级
+ 使用文本编辑器打开`config/app.config`  

+ 找到`bilibili.dash.video.codec.priority`以及`bilibili.dash.audio.quality.priority`  

+ 建议更改完毕后先下载一个视频进行测试，因为部分功能需要更加强大的`ffmpeg`版本进行配合。    

`杜比全景声`和`Hi-Res无损`经过测试，配合ffmpeg主干分支License`gpl`版的编译，  
得到的mp4是能看能听的，但是硬件有点挫，声音效果我就感觉不出来了。  

## bilibili.dash.video.codec.priority
- 默认值: `12, 7, 13`  
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