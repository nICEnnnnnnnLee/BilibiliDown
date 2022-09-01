# tasks.config

本文件保存正在下载中的任务。  
## 内容格式  
下面列出写该文件的部分代码  
```java
File dir = ResourcesUtil.search("config");
File downloadingTasks = new File(dir, "tasks.config");
// \r\n##\r\n 分隔每个任务
// \r\n@@\r\n 分隔 ClipInfo属性和 Qn
final String taskSep = "\r\n##\r\n";
final String attrSep = "\r\n@@\r\n";
try(BufferedWriter writer = new BufferedWriter(new FileWriter(downloadingTasks))){
    for(DownloadInfoPanel dp : Global.downloadTaskList.keySet()) {
        ClipInfo c = dp.getClipInfo();
        writer.append(c.getAvTitle());
        writer.append(attrSep);
        writer.append(Long.toString(c.getcId()));
        writer.append(attrSep);
        writer.append(c.getAvId());
        writer.append(attrSep);
        writer.append(Integer.toString(c.getPage()));
        writer.append(attrSep);
        writer.append(c.getTitle());
        writer.append(attrSep);
        writer.append(c.getListName());
        writer.append(attrSep);
        writer.append(c.getListOwnerName());
        writer.append(attrSep);
        writer.append(Long.toString(c.getFavTime()));
        writer.append(attrSep);
        writer.append(Long.toString(c.getcTime()));
        writer.append(attrSep);
        writer.append(c.getUpName());
        writer.append(attrSep);
        writer.append(c.getUpId());
        writer.append(attrSep);
        writer.append(Integer.toString(c.getRemark()));
        writer.append(attrSep);
        writer.append(Integer.toString(dp.getQn()));
        
        writer.append(taskSep);
        writer.flush();
    }
}catch (Exception e1) {
    e1.printStackTrace();
}
```