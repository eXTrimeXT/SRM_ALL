/* eslint-disable quotes */
export const transformVueJsonEditor = () => ({
  name: 'transform-vue-json-editor',

  transform (src, id) {
    if (/vue-json-editor\/.*\.vue$/.test(id)) {
      // cjs to esm
      return {
        code: src.replace(
          `"./assets/jsoneditor"`,
          `"/node_modules/.local--deps/jsoneditor.js"`,
        ),
        map: null
      }
    }
  }
})
