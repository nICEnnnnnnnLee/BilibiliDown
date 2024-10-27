# batchDownload.config
该文件用于一键下载

## 写在前面
如果觉得文字描述比较复杂，可以使用现成的配置 -> [进阶使用-通过配置一键下载](/guide/advanced/quick-batch-download)

## 基本格式
+ 以`#`开头的行内容会被视为注释
+ 文件由若干个`合集下载配置`构成。   
+ `合集下载配置`由`合集头`开始，加上若干个`合集配置`，直到另一个`合集头`出现或文件末尾结束。
    即： `[{type}:{content}]` + `{key}={value}` * N

+ `合集头`有两种类型： 
    + `[favorite:{content}]`  
    content可以为`all`, 或者 若干个收藏夹名称的集合，以`,`隔开
    + `[url:{content}]`  
    content可以视为主页面的输入链接，~~但必须是分页查询的类型~~ `V6.18开始可以不是了`

+ `合集配置`有三种类型：
    + `stop.condition = {condition1},{condition2},...`  
        表示分页查询停止的条件
    + `download.condition = {condition1},{condition2},...`  
        表示判断是否下载查询到的BV的条件
    + 其它配置`{key} = {value}`

### 关于条件判断：
行与行之间是**或**的关系；一行里面`,`分割的条件之间是**与**的关系  
```
stop.condition = {c1},{c2},{c3}
stop.condition = {c4},{c5}
stop.condition = {c6}
```
上述配置下，在`(c1 && c2 && c3) || (c4 && c5) || c6`为真的情况下停止查询

### 关于条件condition  
+ 合法的左边部分有：  
    + `_` 表示占位
    + `page` 表示查询页数
    + `bv` 表示BV号
    + `favTime` 表示收藏时间
    + `cTime` 表示用户投稿时间  
    + `avTitle` 表示视频标题  
    + `clipTitle` 表示视频小标题  
+ 合法的操作符有：    
    + `:` 表示等于(或者匹配正则表达式)
    + `!` 表示不等于(或者不匹配正则表达式)
    + `<` 表示小于
    + `>` 表示大于  
+ 合法的右边部分比较复杂，随前两者变动。  
基本上有一点，不能含有分隔符`,`号。  
下面举例以方便理解：  
    + `_:_`  表示真 
    + `_!_`  表示假 
    + `_:downloaded`  表示BV被下载过时为真 
    + `page:20`  表示页数为20时为真
    + `bv:BVxxx`  表示BV号为BVxxx时为真
    + `favTime>2022-08-31`  表示BV号收藏时间晚于`2022-08-31`为真
    + `cTime<2022-08-31`  表示BV号投稿时间早于`2022-08-31`为真
    + `avTitle:.*热血老年人.*`  表示视频标题中含有内容`热血老年人`(匹配正则`.*热血老年人.*`)时为真

### 关于其它配置(可以缺省，不是必要配置)  

- `start.page`  
    - 取值范围: 数字
    - 默认值:`1` 
    - 释义:   
        分页查询从{page}页开始  

- `stop.bv.bounds`  
    - 取值范围: `exclude | include`  
    - 默认值:`exclude`  
    - 释义:   
        `exclude` 表示不包含边界, 当遇到`stop.condition`中的bv而停止时，不会下载该BV  
        `include` 表示包含边界, 当遇到`stop.condition`中的bv而停止时，若`download.condition`为真则下载该BV  

- `stop.alert`  
    - 取值范围: `true | false`  
    - 默认值:`true`  
    - 释义:   
        `true` 表示当合集下载完毕时，弹框提示  
        `false` 表示当合集下载完毕时，不弹框提示  

## 模板实例解析
```
[favorite:all]             <------   合集头，表示查询所有收藏夹         ---
# 表示遇到 page=3 或者  favTime<2022-01-01 的视频时，停止查询            |
stop.condition = page:3                     <-------- 合集配置       合集下载配置   
stop.condition = favTime<2022-01-01         <-------- 合集配置   
# 表示只要查询到，我就加入下载队列                                       |
download.condition = _:_                    <-------- 合集配置         ---

[favorite:默认收藏夹,学习]  <------   合集头，表示查询相应收藏夹         ---
# 表示遇到 page=3 或者  下载过的视频时，停止查询                         |
stop.condition = page:3                     <-------- 合集配置       合集下载配置   
stop.condition = _:downloaded               <-------- 合集配置   
# 表示只要查询到，我就加入下载队列                                       |
download.condition = _:_                    <-------- 合集配置         ---

[url:https://space.bilibili.com/378034/]    <--------   合集头         ---
start.page = 1                              <-------- 合集配置          |
# 表示遇到下载过的视频时,停止查询                                       
stop.condition = _:downloaded               <-------- 合集配置         合集下载配置
# 表示只要查询到，我就加入下载队列                                        |
download.condition = _:_                    <-------- 合集配置         ---
```

