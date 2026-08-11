import { generateSchemaPage } from '../helper.js'

export default generateSchemaPage({
  name: 'LoginPage',
  schema: {
    username: {
      type: 'string',
      title: "{{$t('login.username')}}",
      'x-decorator': 'FormItem',
    },
    password: {
      type: 'string',
      title: "{{$t('login.password')}}",
      'x-component': 'Password',
      'x-decorator': 'FormItem',
    },
    confirm_password: {
      type: 'string',
      title: "{{$t('login.confirmPassword')}}",
      'x-component': 'Password',
      'x-decorator': 'FormItem',
      'x-reactions': [
        {
          dependencies: ['.password'],
          fulfill: {
            state: {
              selfErrors:
                '{{$deps[0] && $self.value && $self.value !== $deps[0] ? "确认密码不匹配" : ""}}',
            },
          },
        },
      ],
    },
  },
})
