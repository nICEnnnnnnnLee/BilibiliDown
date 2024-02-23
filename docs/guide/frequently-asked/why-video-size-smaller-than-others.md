# 为什么下载的视频同分辨率比别的工具下的体积要小？


一般来说，B站的视频会提供三种编码方式的链接，分别是`AVC(H.264)`、`HEVC(H.265)`和`AV1`。  
其中，`AVC(H.264)`兼容性最好，基本上所有设备或程序都能支持播放；但它压缩率最差，同等信息下体积相较更大。一般浏览器播放或者工具下载都默认采用该种编码。  
本程序默认优先采用的是`HEVC(H.265)`编码，相同质量的视频比`AVC(H.264)`占用空间更少，并且绝大部分设备或程序都支持。  


如果你需要修改优先的编码格式，请参考配置
- [bilibili.dash.video.codec.priority](/config/app#bilibili-dash-video-codec-priority)
- [bilibili.dash.video.codec.priority.map](/config/app#bilibili-dash-video-codec-priority-map)

  