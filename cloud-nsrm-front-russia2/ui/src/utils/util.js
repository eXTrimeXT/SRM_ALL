import Vue from 'vue'

export function proxyProp (prop) {
  // 使用Proxy可以拦截对象的动态生成的属性
  return new Proxy(prop, {
    set (target, key, value) {
      if (prop[key] !== value) {
        let _set = Vue.set
        _set.call(Vue, prop, key, value)
      }
      return Reflect.set(target, key, value)
    }
  })
}

export function deepProxyProp (prop) {
  return deepProxy(prop, function (cbType, { target, key, value }) {
    console.log(cbType, target, key, value)
    if (target[key] !== value) {
      let _set = Vue.set
      _set.call(Vue, target, key, value)
    }
  })
}

/**
 * 对象、数组变化监听(增删改)
 * @param {Object} obj
 * @param {Function} cb
 * @return {Proxy}
 */
function deepProxy (obj, cb) {
  if (typeof obj === 'object' && !!obj) {
    for (let key in obj) {
      if (typeof obj[key] === 'object') {
        obj[key] = deepProxy(obj[key], cb)
      }
    }
  }

  if (!obj) {
    return
  }

  return new Proxy(obj, {
    /**
     * @param {Object, Array} target 设置值的对象
     * @param {String} key 属性
     * @param {any} value 值
     * @param {Object} receiver this
     */
    set: function (target, key, value, receiver) {
      if (typeof value === 'object') {
        value = deepProxy(value, cb)
      }

      let cbType = target[key] == undefined ? 'create' : 'modify'

      // 排除数组修改length回调
      if (!(Array.isArray(target) && key === 'length')) {
        cb(cbType, { target, key, value })
      }
      return Reflect.set(target, key, value, receiver)
    },
    deleteProperty (target, key) {
      // eslint-disable-next-line standard/no-callback-literal
      cb('delete', { target, key })
      return Reflect.deleteProperty(target, key)
    }
  })
}

// 寻找某个符合条件的父组件
export function findComponentUpwardByProp (vm, prop) {
  let res = null
  let parent = vm.$parent
  while (parent) {
    if (parent[prop] !== undefined) {
      res = parent
      break
    }
    parent = parent.$parent
  }
  return res
}

export const formatNumber = (number, fitBit) => {
  if (!fitBit) {
    fitBit = 2
  }
  number = parseFloat(number).toFixed(fitBit).toString().split('.')
  // console.log("第一次处理后的num",num);
  number[0] = number[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), '$1,')
  // console.log("第一次处理后的num",num[0]);
  if (number && number.length > 1) {
    number[1] = number[1].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), '$1,')
  }
  return number.join('.')
}

// 递归删除指定字段，跟 lodash omit 类似，区别在于深度剔除
export const deppOmit = (obj, keys) => {
  if (obj === null || obj === undefined) {
    return obj
  }

  if (Array.isArray(obj)) {
    return obj.map((item) => deppOmit(item, keys))
  }

  if (typeof obj === 'object') {
    return Object.keys(obj).reduce((acc, key) => {
      if (keys.includes(key)) {
        return acc
      }
      acc[key] = deppOmit(obj[key], keys)

      return acc
    }, {})
  }

  return obj
}
