module.exports = {
  root: true,
  env: {
    browser: true,
    es6: true,
    node: true
  },
  'extends': [
    'eslint:recommended',
    'plugin:vue/recommended',
    'plugin:@typescript-eslint/eslint-recommended',
    '@vue/standard',
    './.eslintrc-auto-import.json'
  ],
  "plugins": [
    "vue"
  ],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'quotes': ['error', 'single'],
    'semi': ['error', 'never'],
    'eqeqeq': 'off',
    "comma-dangle": ["error", "never"],
    'no-extra-semi': 'error',
    'vue/no-unused-vars': 'off',
    'vue/html-end-tags': 'error',
    'vue/require-default-prop': 'off',
    'vue/require-prop-types': 'error',
    'vue/attributes-order': 'error',
    'vue/order-in-components': 'error',
    'vue/html-quotes': ['error', 'double'],
    'vue/arrow-spacing': ['error', { 'before': true, 'after': true }],
    'vue/block-spacing': ['error', 'always'],
    'vue/component-name-in-template-casing': ['error', 'PascalCase', { // PascalCase | kebab-case
      'registeredComponentsOnly': true,
      'ignores': []
    }],
    // "vue/max-attributes-per-line": 'off',
    "vue/max-attributes-per-line": ["error", {
      "singleline": {
        "max": 4
      },
      "multiline": {
        "max": 1
      }
    }],
    "vue/html-self-closing": ["warn", {
      "html": {
        "void": "never",
        "normal": "always",
        "component": "always"
      },
      "svg": "always",
      "math": "always"
    }],
    "vue/html-closing-bracket-newline": ["error", {
      "singleline": "never",
      "multiline": "always"
    }],
    // "vue/html-indent": 'off',
    'no-tabs': ["error", { allowIndentationTabs: true }],
    'no-irregular-whitespace': ["error", { skipTemplates: true, skipComments: true }],
    'handle-callback-err': ['error', '^(err|error)$'],
    'template-curly-spacing': 'off',
    "indent": ["error", 2],
    "space-before-function-paren": ["error", {
      "anonymous": "always",
      "named": "always",
      "asyncArrow": "always"
    }],
    // "vue/no-unused-components": ["never", {
    //   "ignoreWhenBindingPresent": true
    // }],
    // 标签属性驼峰/非驼峰设置
    "vue/attribute-hyphenation": 'off',
    // 事件驼峰/非驼峰设置
    "vue/v-on-event-hyphenation": 'off',
    "no-unused-vars": 'off',
    "vue/no-unused-components": 'off'
  },
  parserOptions: {
    parser: 'babel-eslint'
  }
}
