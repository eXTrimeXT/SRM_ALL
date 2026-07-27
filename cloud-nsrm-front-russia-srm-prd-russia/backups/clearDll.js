const fs = require('fs-extra')
const chalk = require('chalk')

fs.emptyDir('./dll')
  .then(() => {
    console.log(chalk.green('\nempty old dll success!'))
  })
  .catch(err => {
    console.error(err)
  })
