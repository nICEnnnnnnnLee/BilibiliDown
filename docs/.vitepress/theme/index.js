import DefaultTheme from 'vitepress/theme'
import NotFound from './NotFound.vue'
import Layout from './Layout.vue'

import { watch, h } from 'vue'
import { withBase, useData } from 'vitepress'

export default {
    ...DefaultTheme,
    // this is a Vue 3 functional component
    Layout,
    NotFound,
    enhanceApp({ app, router, siteData }) {
        // app is the Vue 3 app instance from `createApp()`.
        // router is VitePress' custom router. `siteData` is
        // a `ref` of current site-level metadata.

        // 以下没有必要，因为已经做到了
        // watch(() => router.route.path, (to, from) => {
        //     const fullPath = `${window.location.origin}${withBase(to)}`
        //     // console.log('路由发生了变化', fullPath);
        //     // 触发百度的pv统计
        //     if (typeof _hmt != "undefined") {
        //         if (to.path) {
        //             _hmt.push(["_trackPageview", fullPath]);
        //             console.log("上报百度统计", fullPath);
        //         }
        //     }
        // })
    }

}