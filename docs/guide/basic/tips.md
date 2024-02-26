# 使用技巧

一般来说，即使不看任何说明，使用本程序应该也没有多大难度。  
但是知道以下这些小Tips能帮助你更有效率的实现目的。  

---

+ 程序内**提示框**的文本是可以选中的，这意味着你可以通过`Ctrl + C`轻松复制想要的信息。  

+ 右上角菜单的`配置`选项更改仅适用于当次程序运行，如果你想持久化保存某些更改，则需要修改配置文件。  

+ 配置文件`app.config`内可能并不包括所有配置项(随着功能增多有些会使用默认值)。  
  最新最全的配置请参考[配置文档](/config/app)  

+ 配置页面的搜索关键词的命中区域是**描述文本** + **对应键** + **对应值**。举例来说：  
    ```
    描述文本:   创建下载任务前判断是否已下载(会自动保存下载的BV号)  
    对应键:     bilibili.repo  
    对应值:     on  
    ```
    搜索 `已下载`、`repo`、`on`等等都会命中该目标。  


+ 如果你想关注程序有哪些新功能，可以查看每次更新的Release Notes，  
  或者查看汇总起来的[Change log](https://github.com/nICEnnnnnnnLee/BilibiliDown/blob/master/UPDATE.md)，  
  或者下拉[配置文档](/config/app)到最后，看新增了哪些配置项。    