export const flatten = obj => {
    const result = {}

    const recurse = (src, prop) => {
        const toString = Object.prototype.toString
        if (toString.call(src) == '[object Object]') {
            let isEmpty = true
            for (const p in src) {
                isEmpty = false
                recurse(src[p], prop ? prop + '.' + p : p)
            }
            if (isEmpty && prop) {
                result[prop] = {}
            }
        } else if (toString.call(src) == '[object Array]') {
            const len = src.length
            if (len > 0) {
                src.forEach(function (item, index) {
                    recurse(item, prop ? prop + '.[' + index + ']' : index)
                })
            } else {
                result[prop] = []
            }
        } else {
            result[prop] = src
        }
    }
    recurse(obj, '')

    return result
}

export const unflatten = function (data) {
    if (Object(data) !== data || Array.isArray(data)) return data
    const regex = /\.?([^.\[\]]+)|\[(\d+)\]/g
        const resultholder = {}
    for (const p in data) {
        let cur = resultholder; let prop = ''; let m
        while (m = regex.exec(p)) {
            cur = cur[prop] || (cur[prop] = (m[2] ? [] : {}))
            prop = m[2] || m[1]
        }
        cur[prop] = data[p]
    }
    return resultholder[''] || resultholder
}

export const unflatten2 = function (data) {
    if (Object(data) !== data || Array.isArray(data)) return data
    const result = {}
    let cur, prop, idx, last, temp
    for (const p in data) {
        cur = result, prop = '', last = 0
        do {
            idx = p.indexOf('.', last)
            temp = p.substring(last, idx !== -1 ? idx : undefined)
            cur = cur[prop] || (cur[prop] = (!isNaN(parseInt(temp)) ? [] : {}))
            prop = temp
            last = idx + 1
        } while (idx >= 0)
        cur[prop] = data[p]
    }
    return result['']
}
