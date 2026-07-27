import { resolveRootPath } from '@meicloud/vite'
import type { Plugin } from 'vite'

const defaultPolyfills = ['es.global-this']

/**
 * 编译的时候动态加载 polyfill
 */
export const buildModeAddPolyfillsPlugin = (polyfills = defaultPolyfills): Plugin => {
  return {
    name: 'build-mode-add-polyfills',
    enforce: 'pre',
    apply: 'build',
    transform (code, id) {
      if (/\/src\/main\.(ts|js)$/.test(id)) {
        // 项目用的是 core-js@v2
        const polyfillsText = polyfills
          .map(polyfill => `import '${`core-js/modules/${polyfill}`}'`)
          .join('\n')

        return {
          code: polyfillsText + '\n' + code,
          map: null
        }
      }
    }
  }
}
