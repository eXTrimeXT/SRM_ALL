import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction,
  generateCharFunctionExpression,
} from "@meicloud/render-engine";
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
        'x-visible': expression(`!$disabled`),
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
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'POSITION',
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
              required: true,
              message: i18nExpression('请输入格式正确的电话号码'),
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return '请输入格式正确的电话号码'
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
              required: true,
              message: i18nExpression('请输入格式正确的邮箱地址'),
              validator: expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
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
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change': expression(`() => {
                const row = $table.getRowByIndex($self.index)
                console.log(row)
                if(row.ceeaDefaultContact == 'Y'){
                  let data = $form.query('contactInfos').get('value')
                  let index = 0;
                  for(let item of data){
                    if(index != $self.index){
                      item.ceeaDefaultContact = ''
                    }
                    index++
                  }
                }
              }`)
            }
          },
          socialSecurityCertificateFileId: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 100
            },
            'x-visible': generateCharExpressionByFunction(({ $form }) => {
              return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
            }),
            title: i18nExpression('cusEntry.vendorMod.socialSecurityCertificate'),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId`),
                fileName: expression('$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName')
              },
              '@on-change': expression(`(file) => {
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileId = fileId.toString()
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileName = fileName
                } else {
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileId = null
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileName = null
                }
              }`),
              readonly: expression(`$disabled`)
            },
            // ...feedbackLayoutIsPopover,
            // 'x-validator': {
            //   required: generateCharExpressionByFunction(({ $form }) => {
            //     return $form.query('state').get('data').overseasRelation !== 'OUT'
            //   }),
            //   message: i18nExpression('common.requiredField')
            // }
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
              width: 60,
              fixed: 'right'
            },
            'x-visible': expression(`!$disabled`),
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
