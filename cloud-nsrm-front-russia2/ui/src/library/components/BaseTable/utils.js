export function isPromise (obj) {
  return (
    !!obj &&
    (typeof obj === 'object' || typeof obj === 'function') &&
    typeof obj.then === 'function'
  )
}

export const EDITABLE_KEY = '__edit_key__'
export const ADD_KEY = '__add_key__'
export const UPDATE_KEY = '__update_key__'
