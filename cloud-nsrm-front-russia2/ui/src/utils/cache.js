export const createCaches = (fn, prop) => {
    let caches = {}

    let cb = function _cache (params) {
        let key = params[prop]

        if (key === '' || key == null) {
            return Promise.reject(new Error(`params.${prop} is required`))
        }

        if (!caches[key]) {
            caches[key] = fn.call(this, params).catch(e => {
                delete caches[key]
                return Promise.reject(e)
            })
        }

        return caches[key]
    }

    cb.clear = function (key) {
        (key === '' || key == null) ? (caches = {}) : (delete caches[key])
    }

    return cb
}
