
export const endWith = (src, end) => {
  if (!end || end.length > src.length) {
    return false
  }

  return src.substring(src.length - end.length) === end
}

export const startWith = (src, start) => {
  if (!start || !src || start.length > src.length) {
    return false
  }

  return src.substr(0, start.length) === start
}

export const countLength = (str) => {
  if (str == null) return 0
  if (typeof str !== 'string') {
    str += ''
  }
  return str.replace(/[^\x00-\xff]/g, '01').length
}

// 去左空格;
export const ltrim = (str) => {
  if (!str) {
    return str
  }
  return str.replace(/(^\s*)/g, '')
}
// 去右空格;
export const rtrim = (str) => {
  if (!str) {
    return str
  }
  return str.replace(/(\s*$)/g, '')
}
// 去左右空格;
export const trim = (str) => {
  if (!str) {
    return str
  }
  return str.replace(/(^\s*)|(\s*$)/g, '')
}

export const guid = () => {
  const guidStr = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    // var r = Math.random() * 16 | 0
    var r = window.crypto.getRandomValues(new Uint8Array(1)) * 0.001 * 16 | 0
    var v = c == 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
  return 'U' + guidStr.replace(/-/g, '')
}

export default {
  endWith,
  startWith,
  countLength,
  trim,
  ltrim,
  rtrim,
  guid
}
