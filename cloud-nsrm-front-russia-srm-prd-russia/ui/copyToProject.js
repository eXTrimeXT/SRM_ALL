const fse = require('fs-extra')
const chalk = require('chalk')

// 执行脚本之前设置对应的源文件目录和对应的目标文件目录后执行
// npm run copyToProject

const sourceDir = 'D:/SRM_PRO/srm-prod-copy/ui/src'
const destDir = 'D:/out_project/xi-long/xi-long-front-code/ui/src'
fse.copy(sourceDir, destDir, function (err) {
  if (err) {
    console.error(err)
  } else {
    console.log(chalk.green('✅✅✅✅✅✅复制完成！！✅✅✅✅✅✅'))
  }
})
