
# 周期性一键批量下载

点击菜单`操作` -> `按计划下载`。  
开始后，请避免对程序进行不必要的操作，以免并发的线程运行时出现未知问题。

在当前情况下，默认表现为： 
```
马上执行一键下载任务。  
当次一键下载任务完成后，时间在每天6点到第二天0点，随机休眠300~480秒，再继续下次一键下载任务
当次一键下载任务完成后，时间在每天2点到6点，休眠到当天6点，再随机休眠0~360秒，再继续下次一键下载任务
当次一键下载任务完成后，时间在每天0点到2点，当次任务完成后，休眠600秒，再继续下次一键下载任务
```

想要修改间隔周期，请查看配置[bilibili.download.batch.plan](/config/app#bilibili-download-batch-plan)  
想要在程序启动时自动开始周期性下载，请查看配置[bilibili.download.batch.plan.runOnStartup](/config/app#bilibili-download-batch-plan-runonstartup)  