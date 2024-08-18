# JVM传参
有些参数必须在程序启动时传入才能生效，比如设置默认编码为`utf8`，只能在启动的时候传递参数`-Dfile.encoding=utf-8`    


## 如何设置参数
以设置`foo1`为`bar1`,`foo2`为`bar2`为例

### 通过命令行启动程序
```sh
java -Dfoo1=bar1  -Dfool2=bar2 -jar launch.jar
```
注意： 如果`bar1`或`bar2`含有空格，可以将其用`"`包裹。下面这样也是可以的：
```sh
java -Dfoo1="bar1"  -Dfool2="bar2" -jar launch.jar
```


### 通过`BilibiliDown.exe`启动程序
+ 你需要先找到`BilibiliDown.cfg`
    + 一般的位置在exe程序同级的`app`目录下
    + `{AppData}/Local/BilibiliDown`
    + `{AppData}/Roaming/BilibiliDown`

+ 设置`JavaOptions`
```ini
[Application]
app.classpath=launch.jar
app.mainclass=nicelee.memory.App
app.runtime=D:\Program Files\BilibiliDown\runtime

[JavaOptions]
java-options=-Dfoo1=bar1
java-options=-Dfoo2=bar2
java-options=-Dfile.encoding=utf-8
java-options=-Dbilibili.prop.dataDirPath=D:\Program Files\BilibiliDown
java-options=-Dbilibili.prop.log=false
```
配置里面的值即使有空格，也无需特殊对待


## 相关参数
### `file.encoding`
- 释义:   
    程序的默认字符编码，通常为`utf-8`。  
    正常情况下可以无需设置，出现乱码一般可以通过设置这个解决。  

### `bilibili.prop.dataDirPath`
- 引入版本: V6.33
- 取值范围:   
    合法的文件夹路径
- 释义:   
    指定数据文件夹位置，不指定的话数据默认存放在程序所在目录。  

### `bilibili.prop.log`
- 引入版本: V6.33
- 取值范围:   
    `true | false`
- 释义:   
    是否打印输出  

### `sun.java2d.uiScale`
- 取值范围:   
    正实数
- 释义:   
    UI是否等比例放大/缩小。参考[issues#133](https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/133)  
    它一般与多个参数联动，以达到界面美观的效果，比如：
    ```
    -Dsun.java2d.uiScale=1.0 -Dswing.boldMetal=false -Dsun.java2d.dpiaware=false
    ```
