# fingerprint.config
~~登录时随机生成的指纹，如果删掉的话每次登录都会被认为是新设备。(待验证)~~  
模仿浏览器生成的指纹，预期是能够避免被风控。    
目前还不是很确定一定要频繁地通过指定API上报，因而只有在刷新cookie时才会触发指纹上传。    


## 内容格式
```
_uuid=xxx; b_lsid=xxx; b_nut=xxx; buvid3=xxx; buvid4=xxx; buvid_fp=xxx; fingerprint=xxx; b_ut=xxx; i-wanna-go-back=-1
```