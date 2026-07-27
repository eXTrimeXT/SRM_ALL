const path = require('path')
const fs = require('fs')
const TerserPlugin = require('terser-webpack-plugin')
const CompressionWebpackPlugin = require('compression-webpack-plugin')

const resolve = dir => {
  return path.join(__dirname, dir)
}

console.log(' 代理地址：', process.env.VUE_APP_BASE_URL)
console.log('NODE_ENV', process.env.NODE_ENV)

module.exports = {
  // eslint-loader将lint错误输出为编辑警告
  lintOnSave: false,
  runtimeCompiler: true,
  // 生产环境 source map 测试环境 控制台显示报错信息
  productionSourceMap: process.env.NODE_ENV === 'production',
  // 部署应用包时的基本 URL
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  devServer: {
    disableHostCheck: true,
    host: 'localhost',
    port: '8099',
    overlay: {
      warnings: false,
      errors: true
    },
    open: false,
    proxy: {
      // 本地单点联调网关
      '/flow/api/cloud-srm': {
        target: 'http://10.18.4.29:37001',
        changeOrigin: true
      },
      // 联调后端本地电脑，直接放开注释修改IP
      // '/cloud-srm': {
      //   target: 'http://10.74.157.69:9005', // 开发IP 端口
      //   changeOrigin: true
      // },
      // /oasis|/public-access|/ihr-corehr 都是审批流
      '/cloud-srm|/flow/cloud-srm/|/oasis|/public-access|/ihr-corehr|ide-flow|/lcdp': {
        target: 'http://srm-dev.meicloud.com',
        changeOrigin: true
      },
      '/egg': {
        target: 'http://srm-dev.meicloud.com',
        changeOrigin: true,
        // 是否验证ssl证书
        secure: false
      }
    }
  },

  // webpack 配置
  configureWebpack: config => {
    // 忽略打包xe-utils
    config.externals = {
      'xe-utils': 'XEUtils'
    }

    if (process.env.NODE_ENV === 'production') {
      // 为生产环境修改配置
      config.mode = 'production'

      // 将每个依赖包打包成单独的js文件
      config.optimization = {
        ...config.optimization,
        // 压缩JS代码
        minimize: true,
        minimizer: [
          new TerserPlugin({
            sourceMap: false,
            terserOptions: {
              warnings: false,
              sourceMap: false,
              compress: {
                drop_debugger: false,
                drop_console: true,
                // 移除console
                pure_funcs: ['console.log']
              },
              format: {
                comments: false
              }
            },
            extractComments: {
              condition: /^\**!|@preserve|@license|@cc_on/i,
              // filename: 'extracted-comments-build.txt',
              filename: (fileData) => {
                // The "fileData" contains object with "filename", "basename", "query" and "hash"
                return `${fileData.filename}_${fileData.hash}.LICENSE.txt`
              },
              banner: (licenseFile) => {
                return `License information can be found in ${licenseFile}`
              }
            }
          })
        ],
        // 公共代码抽离
        splitChunks: {
          // 缓存策略
          cacheGroups: {
            vendor: {
              chunks: 'all',
              test: /node_modules/,
              name: 'vendor',
              minChunks: 1,
              maxInitialRequests: 5,
              minSize: 0,
              priority: 100
            },
            common: {
              chunks: 'all',
              test: /[\\/]src[\\/]js[\\/]/,
              name: 'common',
              minChunks: 2,
              maxInitialRequests: 5,
              minSize: 0,
              priority: 60
            },
            runtimeChunk: {
              name: 'manifest'
            }
          }
        }
      }

      // 配置webpack 压缩
      config.plugins.push(
        new CompressionWebpackPlugin({
          test: /\.js$|\.html$|\.css$/,
          // 超过4kb压缩
          threshold: 4096
        })
      )
    } else {
      // 为开发环境修改配置
      config.mode = 'development'
      config.devtool = 'source-map'
    }
  },
  // webpack配置
  chainWebpack: config => {
    const files = fs.readdirSync(path.resolve(__dirname, './dll'))
    files.forEach((file, index) => {
      if (/.*\.dll.js/.test(file)) {
        config.plugin('AddAssetHtmlWebpackPlugin' + index).use(require('add-asset-html-webpack-plugin'), [{
          filepath: path.resolve(__dirname, 'dll', file)
        }])
      }
      if (/.*\.manifest.json/.test(file)) {
        config.plugin('DllReferencePlugin' + index).use(require('webpack/lib/DllReferencePlugin'), [{
          context: __dirname,
          manifest: path.resolve(__dirname, 'dll', file)
        }])
      }
    })

    // 添加路径别名
    config.resolve.alias
      .set('@', resolve('src'))
      .set('mod@', resolve('src/modules'))
      .set('assets@', resolve('src/assets'))
      .set('comps@', resolve('src/components'))
      .set('lib@', resolve('src/library'))
      .set('modc@', resolve('src/modulesCus'))

    // 添加svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  },
  css: {
    loaderOptions: {
      sass: {
        data: '@import "@/styles/variables.scss";'
      }
    }
  }
}
