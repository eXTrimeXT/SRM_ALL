const path = require("path");
const webpack = require("webpack");
const TerserPlugin = require("terser-webpack-plugin");
//读取package.json里的依赖，normalize.css除外，打包会报错
const package = require("../ui/package.json");
let dependencies = Object.keys(package.dependencies) || [];
//如果使用了chrome的vue-devtool，那打包的时候把vue也排除掉，因为压缩过的vue是不能使用vue-devtool的
dependencies =
  dependencies.length > 0
    ? dependencies.filter(item => !["vue", "normalize.css"].includes(item))
    : [];

module.exports = {
  entry: {
    vendor: dependencies
  },
  resolve: {
    extensions: [".js", ".vue", ".json"],
    alias: {
      vue$: "vue/dist/vue.esm.js"
    }
  },
  output: {
    filename: "[name]_[hash:6].dll.js",
    path: path.resolve(__dirname, "./dll"),
    library: "[name]_[hash:6]" // 把文件里的内容通过全局变量暴露出来，变量的名字叫vendors
  },
  optimization: {
    minimize: true,
    minimizer: [
      new TerserPlugin({
        terserOptions: {
          format: {
            comments: false,
          },
        },
        extractComments: {
          condition: /^\**!|@preserve|@license|@cc_on/i,
          // filename: "extracted-comments.txt",
          filename: (fileData) => {
            // The "fileData" contains object with "filename", "basename", "query" and "hash"
            return `${fileData.filename}_${fileData.hash}.LICENSE.txt`
          },
          banner: (licenseFile) => {
            return `License information can be found in ${licenseFile}`;
          },
        },
      }),
    ],
  },
  plugins: [
    new webpack.DllPlugin({
      name: "[name]_[hash:6]", // 对library这个库进行dllplugin的分析
      path: path.resolve(__dirname, "./dll/[name]_[hash].manifest.json")
    })
  ]
};
