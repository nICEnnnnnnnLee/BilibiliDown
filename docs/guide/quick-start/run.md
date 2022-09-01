# 运行程序


程序的目录结构大致如下：  
![](/img/project-snapshot.png)

核心是以jar包所在为工作目录，通过命令行运行jar包。  
不管什么系统都是围绕这一点实现的。    
**请勿直接双击jar包运行**  

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

### 直接运行jar
```bash
java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
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
java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar
```