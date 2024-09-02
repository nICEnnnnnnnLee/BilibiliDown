# 运行程序


直接运行`BilibiliDown.exe`即可。  

程序会对`配置的数据目录`、`配置的下载文件夹及其子目录`进行**读写**操作，请注意赋予相应权限。参考[issue#214](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/214)，或者你也可以以管理员权限运行该程序(不推荐)。  

总之，建议不要把程序放在系统盘。    

这里，`配置的数据目录`默认为`jar包所在目录及其子目录`，你可以参考[JVM传参](/config/jvm_args#bilibili-prop-datadirpath)进行配置。

另外，抓取log请双击`Double-Click-to-Run-for-Win-debug.bat`。  

## 注意事项
+ 使用`.msi`包升级会覆盖`app/BilibiliDown.cfg`。  
    如果你对其进行了修改，建议移动到`Appdata`相应目录下。比如：  
    + `C:\Users\[用户名]\AppData\Local\BilibiliDown\BilibiliDown.cfg`
    + `C:\Users\[用户名]\AppData\Roaming\BilibiliDown\BilibiliDown.cfg`