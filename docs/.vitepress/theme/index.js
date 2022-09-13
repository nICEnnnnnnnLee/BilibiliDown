import DefaultTheme from 'vitepress/theme'
import NotFound from './NotFound.vue'
import Layout from './Layout.vue'

import { watch } from 'vue'
import { withBase } from 'vitepress'

export default {
    ...DefaultTheme,
    Layout,
    NotFound,
    enhanceApp({ app, router, siteData }) {
        // app is the Vue 3 app instance from `createApp()`.
        // router is VitePress' custom router. `siteData` is
        // a `ref` of current site-level metadata.

        // 以下没有必要，因为已经做到了
        watch(() => router.route.path, (to, from) => {
            // 触发百度的pv统计
            if (typeof _hmt != "undefined") {
                if (to) {
                    _hmt.push(["_trackPageview", to]);
                    console.log("上报百度统计", to);
                }
            }
        })
    }

}