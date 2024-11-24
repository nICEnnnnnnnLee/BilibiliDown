# 更新程序

+ 在程序右上角点击菜单`关于` -> `检查更新`，根据提示操作即可。  
![](/img/update.png)  

+ 如果需要更新Beta版本，可以设置`bilibili.github.token`，`关于` -> `更新Beta版本`，根据提示操作即可。  
此时下载的Github Actions最新编译的artifact。  

如果因为未知原因无法更新，可以在`update`目录下找到`INeedBiliAV.update.jar`，手动替换掉原来的`INeedBiliAV.jar`  



不要担心`app.config`无法对应新版本的问题，作者在开发时会考虑这点的。    
所有新特性都有缺省默认设置，不会影响基础功能的使用。  

可能影响使用的不兼容更新将会列在下方。  

## 不兼容更新 / Breaking Changes
+ V6.37
    + 现在不登录的话，没法获取字幕。
+ V6.34
    + `launch.jar`会通过JVM传参`bilibili.prop.mainClass`来定义入口类，这是为了规避[issue#213](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/213)。你可以参考[JVM传参](/config/jvm_args#bilibili-prop-mainclass)进行配置。    
+ V6.33
    + `launch.jar`会在**当前目录**和JVM传参目录`bilibili.prop.dataDirPath`中寻找`INeedBiliAV.jar`，并使用
    **最后修改时间较新**的版本。  
+ V6.24
    + 启动包现在改成了`launch.jar`，当然`INeedBiliAV.jar`仍然可以使用。  
    + `launch.jar`会将`INeedBiliAV.jar`加载至内存，在程序运行的时候不会对其进行占用，此时文件可以被删除。
+ V6.19
    默认分发的`ffmpeg.exe`进行了新版本编译的替换，主要是支持杜比视界和Hi-Res无损。  
    + [旧版本下载链接（32位）](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg.exe)
    + [新版本下载链接（64位）](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V4.5/ffmpeg_N-108857-g00b03331a0-20221027.exe)
+ v6.15
    程序运行脚本的名称进行了更改。  
    `run-UI.bat` 更名为 `Double-Click-to-Run-for-Win.bat`。  
    `run-UI-debug.bat` 更名为 `Double-Click-to-Run-for-Win-debug.bat`。  
    上述改动可能对用其它应用管理本程序更新的情况有所影响，但是程序内更新正常使用。  