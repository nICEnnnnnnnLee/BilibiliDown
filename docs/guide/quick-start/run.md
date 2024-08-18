# 运行程序


程序的目录结构大致如下：  
![](/img/project-snapshot.png)

和其它Java程序一样，直接运行jar包即可。命令行或双击都行。(Since V6.24)  
命令行调用的话，如果不想开着终端影响感官，可以百度一下`nohup`命令。  

会有`INeedBiliAV.jar`、`launch.jar`两个jar包。  
后者会将前者加载到内存然后启动，这样运行程序时可以直接将`INeedBiliAV.jar`进行替换，便于版本升级。  
抛开升级这个功能，运行哪个jar包都无所谓。  
建议是使用`launch.jar`。    

另外，程序会对`配置的数据目录`、`配置的下载文件夹及其子目录`进行**读写**操作，请注意赋予相应权限。  
比如，在Windows系统下，如果把程序直接放在C盘，那么就需要以管理员权限运行。(所以不建议这么做)     

这里，`配置的数据目录`默认为`jar包所在目录及其子目录`，你可以参考[JVM传参](/config/jvm_args#bilibili-prop-datadirpath)进行配置

当然，在下面程序还提供了一些脚本，可以方便地运行或者创建快捷方式，一劳永逸。  

## Windows 环境

### 建立快捷方式(可选)
+ 双击脚本 `Create-Shortcut-on-Desktop-for-Win.vbs`
+ 以后在桌面双击快捷方式即可  
![](/img/win-desktop-quick-link.png)

### 直接双击脚本
+ 双击`Double-Click-to-Run-for-Win.bat`


## Linux 环境

### 建立快捷方式(可选)
+ 给予权限并执行脚本 `Create-Shortcut-on-Desktop-for-Linux.sh`
+ 在桌面上右键快捷方式赋予执行权限
+ 以后在桌面双击快捷方式即可
![](/img/Ubuntu-run.png)

### 直接运行jar
```bash
java -Dfile.encoding=utf-8 -jar launch.jar
```

## Mac 环境(未测试, 需要反馈)

### 建立快捷方式(可选)
+ 给予权限并执行脚本 `Create-Shortcut-on-Desktop-for-Mac.sh`
+ 以后在桌面双击快捷方式即可

### 直接双击脚本
+ 赋予脚本执行权限
+ 双击`Double-Click-to-Run-for-Mac.command`

### 直接运行jar
```bash
java -Dfile.encoding=utf-8 -jar launch.jar
```