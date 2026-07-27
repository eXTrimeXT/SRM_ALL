const path = require('path')

// cjs to esm
require('esbuild')
  .build({
    entryPoints: ['vue-json-editor/assets/jsoneditor.js'],
    bundle: true,
    format: 'esm',
    charset: 'utf8',
    sourcemap: false,
    logLevel: 'error',
    outfile: path.resolve(process.cwd(), 'node_modules', '.local--deps', 'jsoneditor.js')
  })
  .catch(() => process.exit(1))
