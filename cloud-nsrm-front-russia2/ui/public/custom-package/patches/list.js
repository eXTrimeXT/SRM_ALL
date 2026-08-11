// TODO 多版本
// TODO ts
// TODO 微模块
/**
 * scope 可以是 hooks
 */
export default {
  components: {},
  scope: () => {
    return {
      $professionOptions: [
        {
          label: '选项1',
          value: 1,
        },
        {
          label: '选项2',
          value: 2,
        },
      ],
    }
  },
  patches: {
    add: {
      profession: {
        __field__: true,
        type: 'string',
        title: "{{$t('home.profession')}}",
        enum: '{{$professionOptions}}',
        'x-decorator': 'FormItem',
        'x-component': 'Select',
      },
    },
    update: {
      name: {
        __field__: true,
        'x-component-props': {
          placeholder: '请输入',
        },
      },
    },
    remove: {
      age: true,
    },
  },
}
