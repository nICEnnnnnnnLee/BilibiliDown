<script setup>
import { version } from '../../../package.json'

let asset = `BilibiliDown.v${version}.release.zip`
let assetSHA1 = `BilibiliDown.v${version}.release.zip.sha1`
let assetWithJre = `BilibiliDown.v${version}.win_x64_jre11.release.zip`
let assetWithJreSHA1 = `BilibiliDown.v${version}.win_x64_jre11.release.zip.sha1`
let assetWithMsi = `BilibiliDown.v${version}.win_x64.msi`
let assetWithMsiSHA1 = `BilibiliDown.v${version}.win_x64.msi.sha1`

let urlFromGithub = (version, fileName)=>{
    return `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V${version}/${fileName}`
}
let urlFromBitbucket = (version, fileName)=>{
    return `https://bitbucket.org/niceleeee/bilibilidown/downloads/${fileName}`
}
let urlFromSupaBase = (version, fileName)=>{
    return `https://vezfoeoqirnvcqsuiext.supabase.co/storage/v1/object/public/bili/release/${fileName}`
}
let urlFromCloudinary = (version, fileName)=>{
    return `https://res.cloudinary.com/dcrcvyjzu/raw/upload/bili/${fileName}`
}
// let urlFromImagekit = (version, fileName)=>{
//     return `https://ik.imagekit.io/n1ce/release/${fileName}`
// }
let urlFromTwicpics = (version, fileName)=>{
    return `https://bilibili.twic.pics/release/V${version}/${fileName}`
}
let urlFromTransloadit = (version, fileName)=>{
    return `https://bili.tlcdn.com/release/V${version}/${fileName}`
}
</script>
# 下载程序

[[toc]]

## 下载Release程序
你可以前往[Github Release](https://github.com/nICEnnnnnnnLee/BilibiliDown/releases)页面查看更新详情以及历史版本  
或者直接在下面的列表里选择进行下载。  
|文件     |链接 | 备注 |
|-              |---|---|
|{{ asset }} | <a :href="urlFromGithub(version, asset)" target="_blank" rel="noreferrer">Github</a>&nbsp;&nbsp;<a :href="urlFromGithub(version, assetSHA1)" target="_blank" rel="noreferrer">Github-SHA1</a><br/> <a :href="urlFromBitbucket(version, asset)" target="_blank" rel="noreferrer">Bitbucket</a>&nbsp;&nbsp;<a :href="urlFromBitbucket(version, assetSHA1)" target="_blank" rel="noreferrer">Bitbucket-SHA1</a><br/> <a :href="urlFromSupaBase(version, asset)" target="_blank" rel="noreferrer">SUPABASE</a> &nbsp;&nbsp; <a :href="urlFromSupaBase(version, assetSHA1)" target="_blank" rel="noreferrer">SUPABASE-SHA1</a> <br/> <a :href="urlFromTransloadit(version, asset)" target="_blank" rel="noreferrer">Transloadit</a> &nbsp;&nbsp; <a :href="urlFromTransloadit(version, assetSHA1)" target="_blank" rel="noreferrer">Transloadit-SHA1</a><br/> <a :href="urlFromCloudinary(version, asset)" target="_blank" rel="noreferrer">Cloudinary</a> &nbsp;&nbsp; <a :href="urlFromCloudinary(version, assetSHA1)" target="_blank" rel="noreferrer">Cloudinary-SHA1</a><br/> <a :href="urlFromTwicpics(version, asset)" target="_blank" rel="noreferrer">Twicpics</a> &nbsp;&nbsp; <a :href="urlFromTwicpics(version, assetSHA1)" target="_blank" rel="noreferrer">Twicpics-SHA1</a>|-|
|{{ assetWithJre }} | <a :href="urlFromGithub(version, assetWithJre)" target="_blank" rel="noreferrer">Github</a> <br/><a :href="urlFromGithub(version, assetWithJreSHA1)" target="_blank" rel="noreferrer">Github-SHA1</a>| Win x64压缩包<br/>`.vbs`创建桌面快捷方式<br/>`.bat`脚本运行或Debug |
|{{ assetWithMsi }} | <a :href="urlFromGithub(version, assetWithMsi)" target="_blank" rel="noreferrer">Github</a> <br/><a :href="urlFromGithub(version, assetWithMsiSHA1)" target="_blank" rel="noreferrer">Github-SHA1</a>| Win x64安装包 |

**注意**: **SHA1**值用于校验文件完整性，并防止篡改。  
**注意**: 请尽量使用Github源进行下载，其它白嫖网站有可能会因为额度使用完毕或其它原因导致无法使用。  

### 关于如何校验SHA1值
+ Windows:   
`certutil -hashfile <file> SHA1`
+ Linux:   
`sha1sum <file>`
+ Mac:   
`shasum -a 1 <file>`

### 关于如何选择下载附件
+ 如果有java 8及以上的环境  
-> BilibiliDown.v{{ version }}.release.zip  
+ 如果没有java 8及以上的环境  
    + Windows  
        -> BilibiliDown.v{{ version }}.win_x64_jre11.release.zip  
    + 其它系统  
        -> BilibiliDown.v{{ version }}.release.zip 并安装配置Java环境

### 关于如何安装Java环境  
可以自行百度，或者选择以下任意Java发行版下载安装，Java版本>=8即可。  

|发行     |官方链接|License|
|-              |-      |-   |
|Eclipse Temurin | [Link](https://adoptium.net/zh-CN/temurin/releases) | [Link](https://adoptium.net/about.html)   |
|Azul Zulu OpenJDK | [Link](https://www.azul.com/downloads/?version=java-8-lts) | [Link](https://www.azul.com/products/zulu-and-zulu-enterprise/zulu-terms-of-use/)   |
|Liberica JDK | [Link](https://bell-sw.com/pages/downloads/#/java-8-lts) | [Link](https://bell-sw.com/liberica_eula/)   |
|Microsoft Build of OpenJDK| [Link](https://learn.microsoft.com/zh-cn/java/openjdk/download) | [Link](https://docs.microsoft.com/java/openjdk/faq)   |
|Amazon Corretto Build of OpenJDK | [Link](https://aws.amazon.com/corretto/) | [Link](https://aws.amazon.com/corretto/faqs/)   |
|AdoptOpenJDK Hotspot | [Link](https://adoptopenjdk.net/) | [Link](https://adoptopenjdk.net/about.html)   |
|Oracle JDK| [Link](https://www.oracle.com/java/technologies/downloads/) | [Link](https://www.oracle.com/downloads/licenses/no-fee-license.html)   |

**注意**: AdoptOpenJDK 已移至 Eclipse Temurin，不再进行更新。详见[Good-bye AdoptOpenJDK post](https://blog.adoptopenjdk.net/2021/08/goodbye-adoptopenjdk-hello-adoptium/)


## 下载Pre Release程序
为了减少版本发布频率，并预留一定的测试缓冲时间，并不是每一次改动都会生成Release。  

当出现某个问题并得以解决/开发出新特性时，代码会commit到dev分支，并生成预览版本到Github Workflow Artifact。  

你可以[访问该页面](https://github.com/nICEnnnnnnnLee/BilibiliDown/actions/workflows/pre-release-artifacts.yml)查看最近的预发布版本情况，并选择恰当的版本(建议最新的)进行下载。  

你可能需要登录Github账号，并在页面的最下方找到`Artifacts`并下载它。  
![](/img/preRelease.png)