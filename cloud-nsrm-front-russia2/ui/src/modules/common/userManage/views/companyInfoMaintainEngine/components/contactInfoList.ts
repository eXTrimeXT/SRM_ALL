import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  editTableFormItemValid,
  feedbackLayoutIsPopover
} from 'lib@/components/render-engine/schema-segments'


export const contactInfoList = {
  contactInfoList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.contactInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component-props': {
              type: 'primary',
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@click': expression(`() => {
                 $self.query('.contactInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      contactInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'contactInfoId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 姓名
          contactName: {
            type: 'string',
            title: i18nExpression('vendorMod.nickname'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
          },
          // 性别
          ceeaGender: {
            type: 'string',
            title: i18nExpression('vendorMod.sex'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER',
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 部门
          ceeaDeptName: {
            type: 'string',
            title: i18nExpression('vendorMod.department'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 职位
          position: {
            type: 'string',
            title: i18nExpression('dataConfMod.position'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 联系方式
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-validator': {
              message: i18nExpression('vendorMod.correctPhoneNumber'), // 请输入格式正确的电话号码
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)
            },
            ...feedbackLayoutIsPopover
          },
          // 邮箱
          email: {
            type: 'string',
            title: i18nExpression('vendorMod.email'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-validator': {
              message: i18nExpression('vendorMod.correctEmail'), // 请输入格式正确的邮箱地址
              validator: expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return $t('vendorMod.correctEmail')
                }
              }`)
            },
            ...feedbackLayoutIsPopover
          },
          // 默认联系人
          ceeaDefaultContact: {
            type: 'string',
            title: i18nExpression('dataConfMod.isDefault'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 备注
          ceeaComments: {
            type: 'string',
            title: i18nExpression('dataConfMod.remark'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  'disabled': expression(`$form.query('state').get('data').$disabled`),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)
                }
              }
            }}
        })
      }
    }
  }
}
