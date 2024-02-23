
# 自定义消息推送器
## 前置条件
请注意，将`.java`编译为`.class`需要JDK环境，目前Release提供的精简Win64 JRE11并不满足要求。  

## 简述
同`自定义解析器`，只不过文件夹`parsers`变成了`pushers`，指示加载顺序的文件由`parsers.ini`变成了`pushers.ini`。  



## 一个最简单的示例  

参考[SimplePrintPush.java](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/dev/src/nicelee/bilibili/pushers/impl/SimplePrintPush.java)


```
在代码中，
可以通过 Global.msgPushAccount获取配置bilibili.download.push.account
可以通过 Global.msgPushToken获取配置bilibili.download.push.token
可以通过 Global.settings.get(String key)获取key对应配置value
```