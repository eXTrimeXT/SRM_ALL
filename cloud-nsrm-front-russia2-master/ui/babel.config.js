module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset',
    [
      '@babel/preset-env',
      {
        'useBuiltIns': 'entry',
        'corejs': 3
      }
    ]
  ],
  compact: false,
  env: {
    development: {
      sourceMaps: true,
      retainLines: true,
      plugins: ['dynamic-import-node']
    }
  }
}
