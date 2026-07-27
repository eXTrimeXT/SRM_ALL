import {
  expression, 
  generateXindexInOrder, 
  i18nExpression, 
  generateCharExpressionByFunction,
  generateCharFunctionExpression
} from '@meicloud/render-engine'
import {
  editTableFormItemValid,
  feedbackLayoutIsPopover
} from "lib@/components/render-engine";

export const contactData = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      contactDataBefore: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 400,
          pagination: false,
          sortable: false
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
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
            }
          },
          // 邮箱
          email: {
            type: 'string',
            title: i18nExpression('vendorMod.email'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
            }
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
              'disabled': true
            }
          },
          socialSecurityCertificateFileId: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 100
            },
            'x-visible': generateCharExpressionByFunction(({ $form }) => {
              return $form.query('state').get('data').userType !== 'PERSONAL'
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
              'readonly': true
            },
            // ...feedbackLayoutIsPopover,
            // 'x-validator': {
            //   required: generateCharExpressionByFunction(({ $form }) => {
            //     return $form.query('state').get('data').userType !== 'OUT'
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
              'disabled': true
            }
          }
        })
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      toolbar: {
        type: 'void',
        'x-visible': expression('!$form.readPretty'),
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $self.query('contactInfoChanges')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      contactInfoChanges: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          primaryKey: 'contactChangeId',
          cascadeDeletion: true,
          pagination: false,
          sortable: false
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.contactName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 性别
          ceeaGender: {
            type: 'string',
            title: i18nExpression('vendorMod.sex'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaGender || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER',
              class: '',
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 部门
          ceeaDeptName: {
            type: 'string',
            title: i18nExpression('vendorMod.department'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDeptName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.position || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'POSITION',
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 联系方式
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaContactMethod || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField'),
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return '请输入格式正确的电话号码'
                }
              }`)
            }
          },
          // 邮箱
          email: {
            type: 'string',
            title: i18nExpression('vendorMod.email'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.email || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField'),
              validator: expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)
            }
          },
          // 默认联系人
          ceeaDefaultContact: {
            type: 'string',
            title: i18nExpression('dataConfMod.isDefault'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDefaultContact || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$form.readPretty`),
              '@change': expression(`() => {
                const row = $table.getRowByIndex($self.index)
                if(row.ceeaDefaultContact == 'Y'){
                  let data = $form.query('contactInfoChanges').get('value')
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
              return $form.query('state').get('data').userType !== 'PERSONAL'
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
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).socialSecurityCertificateFileId = fileId
                $table.getRowByIndex($self.index).socialSecurityCertificateFileName = fileName
              }`)
            },
            // ...feedbackLayoutIsPopover,
            // 'x-validator': {
            //   required: generateCharExpressionByFunction(({ $form }) => {
            //     return $form.query('state').get('data').userType !== 'OUT'
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaComments || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-visible': expression('!$form.readPretty'),
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
                  'disabled': expression(`$form.readPretty`),
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
