# 更新程序

在程序右上角点击菜单`关于` -> `检查更新`，根据提示操作即可。  
![](/img/update.png)  

需要注意的是，Linux、Mac的首次更新可能需要赋予`update.sh`执行权限。  

如果因为未知原因无法更新，可以在`update`目录下找到`INeedBiliAV.update.jar`，手动替换掉原来的`INeedBiliAV.jar`  


不要担心`app.config`无法对应新版本的问题，作者在开发时会考虑这点的。    
所有新特性都有缺省默认设置，不会影响基础功能的使用。  

可能影响使用的不兼容更新将会列在下方。  

## 不兼容更新 Breaking Changes
+ v6.15
    程序运行脚本的名称进行了更改。  
    `run-UI.bat` 更名为 `Double-Click-to-Run-for-Win.bat`。  
    `run-UI-debug.bat` 更名为 `Double-Click-to-Run-for-Win-debug.bat`。  
    上述改动可能对用其它应用管理本程序更新的情况有所影响，但是程序内更新正常使用。  