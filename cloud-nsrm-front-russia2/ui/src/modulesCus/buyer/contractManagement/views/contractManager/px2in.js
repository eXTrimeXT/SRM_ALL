const unit = 0.010416666666667
const unit2 = 96

function fixed (s, n) {
  var str = s + ' '
  var pat = /e/i
  var res = str.match(pat)
  if (res == 'e' || res == 'E') {
    return s
  }
  s = 1 * s
  var s_ori = s
  var flag = 0
  if (s < 0) {
    s = -1 * s
    flag = 1
  }
  var val = Math.pow(0.1, n)
  if (s < 10 * val) {
    return s_ori
  }
  if (str.length > n + 1) {
    s = s.toFixed(n)
    s = s.replace(/0+$/, '')
    s = s.replace(/\.$/, '')
  }
  if (flag == 1) {
    s = -1 * s
  }
  return s
}

function lfixsci (s, n) {
  var tp = s
  var tpStr = new String(tp)
  if (tpStr.indexOf('E') != -1 || tpStr.indexOf('e') != -1) {
    var regExp = /^(\d+.\d+)[Ee]([\+\-]?\d+)$/
    var result = regExp.exec(tp)
    var num = ''
    var power = ''
    if (result != null) {
      num = result[1]
      power = result[2]
      var res = fixed(num, n)
      if (res == 10) {
        res = 1
        power = 1 * power + 1
        power = '+' + power
      }
      res += 'e'
      res += power
      return res
    } else {
      return s
    }
  } else {
    return s
  }
}
function lfixed (s, n) {
  var str = s + ' '
  var pat = /e/i
  var res = str.match(pat)
  if (res == 'e' || res == 'E') {
    s = lfixsci(s, n)
    return s
  }
  s = 1 * s
  var s_ori = s
  var flag = 0
  if (s < 0) {
    s = -1 * s
    flag = 1
  }
  if (str.length > n + 1) {
    s = s.toFixed(n)
    s = s.replace(/0+$/, '')
    s = s.replace(/\.$/, '')
  }
  if (flag == 1) {
    s = -1 * s
  }
  return s
}

function lfix (s) {
  var s_ori = s
  if (s < 0) s = -1 * s
  var tpStr = new String(s)
  if (tpStr.indexOf('E') != -1 || tpStr.indexOf('e') != -1) {
    s = lfixsci(s, 6)
    return s
  }
  s = 1 * s
  if (s > 100000000) {
    s = s.toExponential(8)
    s = lfixsci(s, 6)
    return s
  }
  if (s > 0.1) {
    s = lfixed(s, 6)
  } else if (s > 0.01) {
    s = lfixed(s, 8)
  } else if (s < 0.01 && s > 0.0001) {
    s = lfixed(s, 10)
  } else if (s < 0.0001 && s > 0.000001) {
    s = lfixed(s, 12)
  } else if (s < 0.000001 && s > 0.00000001) {
    s = lfixed(s, 14)
  } else if (s < 0.00000001 && s > 0.0000000001) {
    s = lfixed(s, 16)
  } else if (s < 0.0000000001 && s > 0.000000000001) {
    s = lfixed(s, 18)
  } else if (s < 0.000000000001 && s > 0.00000000000001) {
    s = lfixed(s, 20)
  } else if (s < 0.00000000000001 && s > 0.0000000000000001) {
    s = lfixed(s, 22)
  } else if (s < 0.0000000000000001 && s > 0.000000000000000001) {
    s = lfixed(s, 24)
  } else if (s < 0.000000000000000001 && s > 0.00000000000000000001) {
    s = lfixed(s, 26)
  } else if (s < 0.00000000000000000001 && s > 0.0000000000000000000001) {
    s = lfixed(s, 28)
  } else if (s < 0.0000000000000000000001 && s > 0.000000000000000000000001) {
    s = lfixed(s, 30)
  }
  if (s_ori < 0) s = -1 * s
  return s
}

export function px2in (px) {
  return lfix(px * unit)
}

export function in2px (_in) {
  return lfix(_in * unit2)
}
