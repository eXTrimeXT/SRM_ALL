const fs = require('fs-extra')
const AdmZip = require('adm-zip')
const ora = require('ora')
const chalk = require('chalk')

const spinner = ora(chalk.green('正在压缩打包文件...'))
spinner.start()

fs.emptyDir('../deploy_temp_supplier')
  .then(() => {
    console.log(chalk.green('\nempty deploy_temp_supplier success!'))

    const zip = new AdmZip()

    zip.addLocalFolder('./dist')
    zip.writeZip('../deploy_temp_supplier/dist.zip')

    spinner.stop()

    console.log(chalk.green('✅✅✅✅✅✅压缩完成！✅✅✅✅✅✅'))
  })
  .catch(err => {
    console.error(err)
  })
