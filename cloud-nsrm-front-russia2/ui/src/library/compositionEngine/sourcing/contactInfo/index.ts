/**
 * @description 采购商联系方式组件
 */
import { expression, i18nExpression } from '@meicloud/render-engine'
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'

interface ContactInfoProps {
  // 是否设置默认联系方式
  setDefault?: boolean;
  // 只读
  readonly?: boolean
}

export default function (props?: ContactInfoProps): Record<any, any> {
  const {
    setDefault = true,
    readonly
  } = props || {}

  // 设置默认值
  const getDefaultByExpression = (name: string) => {
    return !setDefault ? undefined : expression(`!$form.values.projectId ? $getCurrentUserInfo().${name} : ''`)
  }

  return {
    contactInfoForm: {
      type: 'void',
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 3,
        columnGap: 32,
        rowGap: 0
      },
      'x-query-engine-skip': true,
      'x-read-pretty': expression(`$readonly || ${readonly}`),
      properties: {
        linkman: {
          type: 'string',
          title: i18nExpression('bidMod.linkman'),
          default: getDefaultByExpression('nickname'),
          'x-decorator': 'FormItem',
          'x-component': 'Input',
          'x-component-props': {
            maxlength: '80',
            'show-word-limit': true
          },
          ...requiredValidatorSegment
        },
        tel: {
          type: 'string',
          title: i18nExpression('bidMod.tel'),
          default: getDefaultByExpression('phone'),
          'x-decorator': 'FormItem',
          'x-component': 'Input',
          'x-component-props': {
            maxlength: '20',
            'show-word-limit': true
          },
          'x-validator': {
            triggerType: 'onBlur',
            format: 'phone'
          }
        },
        email: {
          type: 'string',
          title: i18nExpression('bidMod.email'),
          default: getDefaultByExpression('email'),
          'x-decorator': 'FormItem',
          'x-component': 'Input',
          'x-component-props': {
            maxlength: '80',
            'show-word-limit': true
          },
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            format: 'email'
          }
        }
      }
    }
  }
}
