---
layout: home

title: BilibiliDown
titleTemplate:  nICEnnnnnnnLee

hero: {'text': ''}
features: []
---

<style>
.VPHome>.VPHero.VPHomeHero:first-child {
    display: none
}

.VPHome>.VPFeatures.VPHomeFeatures:nth-child(2) {
    display: none
}
</style>
<script setup>
import { VPHomeHero, VPHomeFeatures, VPTeamMembers } from "vitepress/theme";
import { onMounted, reactive } from 'vue'
import { withBase } from 'vitepress'
// import axios from 'axios'

const features = [
    {
        "title": "开源",
        "details": "代码完全开源，程序通过Github Actions进行编译打包发布，整个CI流程清晰透明",
    },
    {
        "title": "易上手",
        "details": "简约的保姆式人机交互界面，直接输入相关链接，再点几个按钮即可完成下载",
    },
    {
        "title": "跨平台",
        "details": "程序适用于Windows、Linux以及Mac",
    },
    {
        "title": "可定制",
        "details": "提供了丰富的配置选项，可以通过自定义进行优化个性化",
    },
]

const hero = reactive({
    "text": "BilibiliDown",
    "tagline": "一款开源的、易上手的、跨平台的、可定制的B站视频下载工具",
    "actions": [
        {
            "theme": "brand",
            "text": "快速开始",
            "link": "/guide/quick-start/what-is-BilibiliDown",
        },
        {
            "theme": "alt",
            "text": "下载",
            "link": "/guide/quick-start/download",
        },
        {
            "theme": "alt",
            "text": "GitHub",
            "link": "https://github.com/nICEnnnnnnnLee/BilibiliDown",
        },
    ]

})
onMounted(() => {
    // 想了想，还是算了。 程序就放在Github Release吧，也算是筛选用户
    // axios.get(`${withBase('latest.json')}?_t=${Date.now()}`)
    //     .then(res => {
    //         const latestVersionInStaticSite = res.data.version
    //         axios.get('https://api.github.com/repos/nICEnnnnnnnLee/BilibiliDown/releases?per_page=1').then(res2 => {
    //             const tag_name = res2.data[0].tag_name
    //             const latestVersion = tag_name.toLowerCase()
    //             const fileName = `BilibiliDown.${latestVersion}.release.zip`
    //             if (latestVersionInStaticSite == latestVersion) {
    //                 const relativePath = withBase(fileName)
    //                 const absoulutPath = window.location.origin + relativePath
    //                 hero.actions[1].link = absoulutPath
    //             }else{
    //                 const url = `https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/${tag_name}/${fileName}`
    //                 hero.actions[1].link = url
    //             }
    //         })
    //     })
})
</script> 
<VPHomeHero :name="''" :text="hero.text" :tagline="hero.tagline" :actions="hero.actions"/>
<VPHomeFeatures class="VPHomeFeatures" :features="features" />

