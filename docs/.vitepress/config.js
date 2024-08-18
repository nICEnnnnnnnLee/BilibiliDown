import { version } from '../../package.json'
export default {
    base: '/BilibiliDown/',
    cleanUrls: 'without-subfolders',
    lang: 'zh-CN',
    title: 'BilibiliDown文档',
    titleTemplate: false,
    description: 'BilibiliDown 帮助文档',
    head: [
        ['script', { type: 'text/javascript' },
            `var _hmt = _hmt || [];
        (function() {
          var hm = document.createElement("script");
          hm.src = "//hm.baidu.com/hm.js?11f780e4e4ccd4e99b101eac776e93e4";
          var s = document.getElementsByTagName("script")[0];
          s.parentNode.insertBefore(hm, s);
        })();`
        ],
        ['script', { type: 'text/javascript' },
            `window.smartlook||(function(d) {
          var o=smartlook=function(){ o.api.push(arguments)},h=d.getElementsByTagName('head')[0];
          var c=d.createElement('script');o.api=new Array();c.async=true;c.type='text/javascript';
          c.charset='utf-8';c.src='https://web-sdk.smartlook.com/recorder.js';h.appendChild(c);
          })(document);
          smartlook('init', '19ece82e2fc254b6b9cb148c545244ca1fc2ade2', { region: 'eu' });`
        ]
    ],
    ignoreDeadLinks: true,
    lastUpdated: true,
    markdown: {
        // theme: 'material-palenight',
        lineNumbers: false
    },
    outDir: './.vitepress/dist',

    themeConfig: {
        siteTitle: 'BilibiliDown',
        logo: 'favicon.svg',
        nav: nav(),
        sidebar: {
            '/guide/': sidebarGuide(),
            '/config/': sidebarConfig()
        },
        aside: true,
        footer: {
            message: 'Released under the Apache 2.0 License.',
            copyright: 'Copyright © 2019-present nICEnnnnnnnLee'
        },
        socialLinks: [
            { icon: 'github', link: 'https://github.com/nICEnnnnnnnLee/BilibiliDown' },
        ],
        outlineTitle: '导航',
        editLink: {
            pattern: 'https://github.com/nICEnnnnnnnLee/BilibiliDown/edit/docs/docs/:path',
            text: '编辑该页面'
        },
        // lastUpdatedText: '上次更新: ',
        lastUpdated: {
            text: '最后更新于',
            formatOptions: {
                dateStyle: 'short',
                timeStyle: 'medium'
            }
        },
        returnToTopLabel: '回到顶部',
        sidebarMenuLabel: '菜单',
        darkModeSwitchLabel: '主题',
        lightModeSwitchTitle: '切换到浅色模式',
        darkModeSwitchTitle: '切换到深色模式',
        docFooter: {
            prev: '上一篇',
            next: '下一篇'
        },
        search: {
            provider: 'local',
            options: {
                translations: {
                    button: {
                        buttonText: '搜索文档',
                        buttonAriaLabel: '搜索文档'
                    },
                    modal: {
                        displayDetails: '显示详情',
                        backButtonTitle: '后退',
                        noResultsText: '无法找到相关结果',
                        resetButtonTitle: '清除查询条件',
                        footer: {
                            selectText: '选择',
                            navigateText: '切换',
                            closeText: '关闭',
                            // selectKeyAriaLabel: string
                            // navigateUpKeyAriaLabel: string
                            // navigateDownKeyAriaLabel: string
                            // closeKeyAriaLabel: string
                        }
                    }
                }
            }
        }
    }
}

function nav() {
    return [
        { text: '快速开始', link: '/guide/quick-start/what-is-BilibiliDown', activeMatch: '/guide/' },
        { text: '配置', link: '/config/app', activeMatch: '/config/' },
        { text: 'v' + version, link: 'https://github.com/nICEnnnnnnnLee/BilibiliDown' }
    ]
}

function sidebarGuide() {
    return [
        {
            text: '快速开始',
            collapsible: true,
            items: [
                { text: 'BilibiliDown是什么?', link: '/guide/quick-start/what-is-BilibiliDown' },
                { text: '下载程序', link: '/guide/quick-start/download' },
                { text: '设置ffmpeg', link: '/guide/quick-start/ffmpeg' },
                { text: '运行程序(exe)', link: '/guide/quick-start/run_win' },
                { text: '运行程序(脚本)', link: '/guide/quick-start/run' },
                { text: '更新程序', link: '/guide/quick-start/update' },
            ]
        },
        {
            text: '基础使用',
            collapsible: true,
            collapsed: true,
            items: [
                { text: '典型案例', link: '/guide/basic/examples' },
                { text: '支持链接', link: '/guide/basic/surports' },
                { text: '使用技巧', link: '/guide/basic/tips' },
                { text: '补充说明', link: '/guide/basic/additional-remarks' },
            ]
        },
        {
            text: '进阶使用',
            collapsible: true,
            collapsed: true,
            items: [
                { text: '文件结构说明', link: '/guide/advanced/dir-list' },
                { text: '设置视频格式(MP4/FLV)', link: '/guide/advanced/media-type-format' },
                { text: '设置视频编码优先级', link: '/guide/advanced/custom-media-codecs' },
                { text: '保存当前下载中的任务', link: '/guide/advanced/save-downloading-tasks' },
                { text: '自定义下载保存路径', link: '/guide/advanced/custom-file-path' },
                { text: '自定义文件名', link: '/guide/advanced/custom-file-name' },
                { text: '自定义背景图', link: '/guide/advanced/custom-background-img' },
                { text: '自定义任务完成提示音', link: '/guide/advanced/custom-beep-sound' },
                { text: '自定义解析器', link: '/guide/advanced/custom-parsers' },
                { text: '自定义消息推送器', link: '/guide/advanced/custom-pushers' },
                { text: '通过配置一键批量下载', link: '/guide/advanced/quick-batch-download' },
                { text: '周期性一键批量下载', link: '/guide/advanced/quick-batch-download-rbr' },
            ]
        },
        {
            text: '常见问题',
            collapsible: true,
            collapsed: true,
            items: [
                { text: '怎么关闭打开的标签页？', link: '/guide/frequently-asked/how-to-close-tab' },
                { text: '一段时间后，下载面板一片红，如何解决？', link: '/guide/frequently-asked/why-task-fail-after-hours' },
                { text: '批量下载的清晰度不符合预期，如何解决？', link: '/guide/frequently-asked/how-to-redownload-videos' },
                { text: '视频一直下载失败，如何解决？', link: '/guide/frequently-asked/what-to-do-if-download-fail' },
                { text: '为什么总是弹出已经下载？', link: '/guide/frequently-asked/why-always-prompt' },
                { text: '为什么下载的视频同分辨率比别的工具下的体积要小？', link: '/guide/frequently-asked/why-video-size-smaller-than-others' },
            ]
        },
    ]
}

function sidebarConfig() {
    return [
        {
            text: '配置',
            // collapsible: true,
            items: [
                { text: 'app.config', link: '/config/app' },
                { text: 'batchDownload.config', link: '/config/batchDownload' },
                { text: 'cookies.config', link: '/config/cookies' },
                { text: 'fingerprint.config', link: '/config/fingerprint' },
                { text: 'repo.config', link: '/config/repo' },
                { text: 'tasks.config', link: '/config/tasks' },
                { text: 'JVM参数', link: '/config/jvm_args' },
            ]
        }
    ]
}