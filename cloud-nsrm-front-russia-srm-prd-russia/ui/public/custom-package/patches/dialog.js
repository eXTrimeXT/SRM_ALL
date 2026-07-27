export default {
  __field__: true,
  type: 'void',
  title: "动态添加的 Dialog",
  'x-read-only': false,
  'x-read-pretty': false,
  'x-component': 'RDialog',
  'x-component-props': {
    beforeClose: `{{(done, type) => {
      if (type !== 'ok') {
        $message.info('取消成功')
        return done()
      }

      $form.validate('a').then(() => {
        $message.success('操作成功')
        done()
      })
    }}}`,
  },
  properties: {
    a: {
      type: 'string',
      title: '请输入任意字符',
      'x-component': 'Input',
      'x-decorator': 'FormItem',
      'x-validator': {
        required: true,
        message: '必填项',
      },
    }
  }
}