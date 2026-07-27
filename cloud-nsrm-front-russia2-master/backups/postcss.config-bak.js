module.exports = {
  // vant ui 默认单位是px, postcss-pxtorem转化px成rem
  plugins: {
    autoprefixer: {
      overrideBrowserslist: [
        'Android >= 4.0',
        'iOS >= 8',
        'Chrome > 31',
        'ff > 31',
        'ie >= 10'
      ]
    },
    'postcss-pxtorem': {
      rootValue: 37.5, // 结果为：设计稿元素尺寸/rootValue 37.5
      propList: ['*']
    }
  }
}
