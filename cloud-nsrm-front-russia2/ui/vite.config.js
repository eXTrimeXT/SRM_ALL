import { defineConfig } from 'vite'

import {
  loadEnvAndProcessEnvConfig,
  resolveRootPathToViteAlias,
  presetMeiCloud,
  resolveRootPath
} from '@meicloud/vite'

import packageJson from './package.json'
import { transformVueJsonEditor } from './scripts/transform-vue-json-editor'
import { buildModeAddPolyfillsPlugin } from './scripts/polyfills'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnvAndProcessEnvConfig(mode)
  console.log('OPEN_APP_PREFIX-', env.VUE_APP_OPEN_APP_PREFIX)
  return {
    envPrefix: 'VUE_', // 标识前缀 | vite 默认 VITE_
    server: {
      host: '0.0.0.0'
    },
    // 应用前缀开关设计，产品目前开启，项目上可按需开启或关闭 ui\.env 文件设置 VUE_APP_OPEN_APP_PREFIX
    base: env.VUE_APP_OPEN_APP_PREFIX == 'Y' ? '/srm/' : '',
    resolve: {
      dedupe: ['vue'],
      extensions: ['.vue', '.js', '.ts', '.tsx', '.jsx', '.json', '.mjs'],
      alias: {
        /**
         * 这个组件需要用到完整版的 vue, 需要用到了 template 编译
         * src/modules/contractManagement/views/contractManager/parser/index.js
         */
        vue: 'vue/dist/vue.esm.js',
        '@': resolveRootPathToViteAlias('src'),
        'mod@': resolveRootPathToViteAlias('src/modules'),
        'mods@': resolveRootPathToViteAlias('src/modules/supplier'), // supplier
        'modb@': resolveRootPathToViteAlias('src/modules/buyer'), // buyer
        'assets@': resolveRootPathToViteAlias('src/assets'),
        'comps@': resolveRootPathToViteAlias('src/components'),
        'lib@': resolveRootPathToViteAlias('src/library'),
        'modc@': resolveRootPathToViteAlias('src/modulesCus'),
        'modcarb@': resolveRootPathToViteAlias('src/modulesCar/buyer'),
        'modcb@': resolveRootPathToViteAlias('src/modulesCus/buyer'),
        'modcs@': resolveRootPathToViteAlias('src/modulesCus/supplier'),
        'modcc@': resolveRootPathToViteAlias('src/modulesCus/common')
      }
    },
    define: {
      'process.env.npm_package_version': JSON.stringify(packageJson.dependencies.jsencrypt)
    },
    esbuild: {
      minifyIdentifiers: false,
      drop: mode !== 'development' ? ['debugger'] : [],
      pure: ['console.log', 'console.warn', 'console.debug', 'console.trace']
    },
    build: {
      // sourcemap: false,
      // commonjsOptions: {
      //   sourceMap: false,
      // },
      cssMinify: false,
      minify: 'esbuild',
      terserOptions: undefined,
      rollupOptions: {
        // 临时 fix，详情可跟踪：https://github.com/vuejs/devtools/issues/1932
        external: ['@vue/devtools-api']
      },
      target: 'esnext'
    },
    optimizeDeps: {
      include: [
        'vue-json-editor/assets/jsoneditor.js',
        '@vueuse/core',
        '@meicloud/render-engine',
        '@meicloud/render-pix',
        '@meicloud/element-ui',
        '@meicloud/render-pix/dist/esm/all-style',
        '@meicloud/element-ui/lib/locale',
        'vxe-table',
        resolveRootPath('src/service/index.js'),
        resolveRootPath('src/lang/index.js')
      ]
    },
    plugins: [
      transformVueJsonEditor(),
      buildModeAddPolyfillsPlugin(),
      ...presetMeiCloud({
        env,
        https: false,
        legacy: false,
        unocss: false,
        pages: false,
        pageLayout: false,
        autoComponents: false,
        chunkSplits: false,
        svgIcon: {
          iconDirs: [resolveRootPath('src/icons/svg')],
          symbolId: 'icon-[name]'
        }
        // proxy: {
        //   '/ide': 'https://ide-sit1.meicloud.com',
        //   // /egg html 转PDF代理
        //   '/egg': env.VUE_APP_BASE_URL,
        //   // ide 审批流本地调试代理[[ 需要将 https 设置成false
        //   '^/ide-flow/.*': {
        //     target: 'http://bpm-dev-stable.meicloud.com',
        //     changeOrigin: true,
        //     rewrite: (path) => path.replace(/^\/ide-flow/, 'mflow')
        //   },
        //   '/mflow': 'http://bpm-dev-stable.meicloud.com',
        //   '/question': {
        //     target: 'https://yunying.gwm.cn',
        //     changeOrigin: true,
        //     rewrite: (path) => path.replace(/^\/question/, '')
        //   },
        //   // ide 审批流本地调试代理]]
        //   // /oasis|/public-access|/ihr-corehr 都是审批流
        //   ...'/cloud-srm|/srm/cloud-srm|/oasis|/public-access|/ihr-corehr|/lcdp|/api-cm-v2'
        //     .split('|')
        //     .reduce((acc, urlKey) => ({ ...acc, [urlKey]: env.VUE_APP_BASE_URL }), {})
        //   // 1、联调后端本地电脑，直接放开注释修改IP
        //   // '/cloud-srm': 'http://localhost:9005', // 开发IP 端口
        //   // 2、美的网络下单点可以直接调线上ipass：单点登录 + 调试后端本地
        //   // '/flow/api/cloud-srm': 'http://10.18.4.29:37001',
        //   // 3、VPN+现场联调后端本地电脑: 放开注释 -> 修改本地ipass启动IP端口
        //   // '/ssc/workflow/cloud-srm': 'http://10.18.4.29:37001'
        // }
      })
    ]
  }
})
