// 密码相关校验和处理方法
// 1 containDigit: 'Y',
// 2 containLowerLetter: 'Y',
// 3 containUpperLetter: 'Y',
// 4 containSpecialLetter: 'Y',
// 正则校验
export const validPatrnObj = {
  // 数字
  containDigit: /[0-9]/,
  // 小写
  containLowerLetter: /[a-z]/,
  // 大写
  containUpperLetter: /[A-Z]/,
  // 特殊字符
  // `
  // containSpecialLetter: /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
  containSpecialLetter: /[`~!@#$%^&*()_\-+=?:"{},.\\/;'\\[\]]/im
}
export const validPatrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,./;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im

// 密码规则校验
export const notContainMap = {
  'username-total': '账号',
  'username-desc': '账号倒序'
}

export const notContainFn = (notContainObj) => {
  let str = ''
  let notContain = notContainObj
  let keysArr = Object.keys(notContain)
  keysArr.forEach((key, index) => {
    if (notContain[key].total == 'Y') {
      str += notContainMap[key + '-total'] + '、'
    }
    if (notContain[key].desc == 'Y') {
      str += notContainMap[key + '-desc'] + '、'
    }
  })
  return str ? str.slice(0, -1) : str
}
