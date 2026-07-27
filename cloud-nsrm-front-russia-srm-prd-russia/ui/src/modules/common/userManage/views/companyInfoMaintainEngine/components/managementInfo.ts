import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const managementInfoList = {
  // 管理体系信息
  managementInfoList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.managementSystemInfo'),
    },
    'x-visible': expression(`$form.query('state').get('data').overseasRelation !== 'PERSONAL'`),
    'x-query-engine-skip': true,
    properties: {
      managementInfo: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 1,
          columnGap: 32,
          rowGap: 0
        },
        'x-query-engine-skip': true,
        properties: {
          // 是否通过ISO9001质量体系认证(如是请上传附件)
          'ifIsoQuality': {
            type: 'string',
            default: 'N',
            'x-decorator': 'FormItem',
            'x-component': 'Radio.Group',
            enum: [
              {
                label: i18nExpression('common.yes'),
                value: 'Y'
              },
              {
                label: i18nExpression('common.no'),
                value: 'N'
              }
            ],
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change': expression(`$managementChange($self.value, 'ISO9001质量体系认证', $form)`)
              // '@change':expression(`() => {
              //    console.log($self.value)
              // }`)
            },
            title: i18nExpression(`vendorMod.msgIfPass1`)
          },
          // 是否通过ISO14001环境体系认证(如是请上传附件)
          'ifIsoEnviron': {
            type: 'string',
            default: 'N',
            'x-decorator': 'FormItem',
            'x-component': 'Radio.Group',
            enum: [
              {
                label: i18nExpression('common.yes'),
                value: 'Y'
              },
              {
                label: i18nExpression('common.no'),
                value: 'N'
              }
            ],
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change':expression(`$managementChange($self.value, 'ISO14001环境体系认证', $form)`)
            },
            title: i18nExpression(`vendorMod.msgIfPass2`)
          },
          // 是否通过OHSAS18000职业、健康安全体系认证(如是请上传附件)
          'ifOhsasSafe': {
            type: 'string',
            default: 'N',
            'x-decorator': 'FormItem',
            'x-component': 'Radio.Group',
            enum: [
              {
                label: i18nExpression('common.yes'),
                value: 'Y'
              },
              {
                label: i18nExpression('common.no'),
                value: 'N'
              }
            ],
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change':expression(`$managementChange($self.value, 'OHSAS18000职业、健康安全体系认证', $form)`)
            },
            title: i18nExpression(`vendorMod.msgIfPass3`)
          },
          // 其他认证情况(如是请上传附件)
          'otherAuthSit': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            title: i18nExpression(`vendorMod.msgIfPass4`)
          }
        }
      },
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
                 $self.query('managementAttaches')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
                 console.log($self.query('managementAttaches').take().value)
              }`)
            }
          }
        }
      },
      managementAttaches: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 200,
          pagination: false,
          sortable: false,
          primaryKey: 'managementAttachId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 证件要求
          documentInspection: {
            type: 'string',
            title: i18nExpression('vendorMod.certificateRequirements'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          fileuploadId:{
            type: 'string',
            'x-hidden': true
          },
          // 认证类型
          authType: {
            type: 'string',
            title: i18nExpression('vendorMod.authType'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$table.getRowByIndex($self.index)?.fileuploadId`),
                fileName: expression('$self.value')
              },
              'validate-options': {
                accept: ['jpg', 'png', 'jpeg']
              },
              readonly:false,
              '@on-change':expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)
            }
          },
          // 认证描述
          authDescription: {
            type: 'string',
            title: i18nExpression('vendorMod.authDesc'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 认证编号
          authNum: {
            type: 'string',
            title: i18nExpression('vendorMod.authNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change': expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)
            }
          },
          // 认证时间
          authDate: {
            type: 'date',
            default: null,
            title: i18nExpression('vendorMod.authDate'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              style: 'width:120px',
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 认证机构
          authOrg: {
            type: 'string',
            title: i18nExpression('vendorMod.authOrg'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 证件有效期至
          endDate: {
            type: 'date',
            title: i18nExpression('vendorMod.certUntil'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              style: 'width:120px',
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
                'x-reactions': expression(`async (feild) => {
                  const row = $table.getRowByIndex($self.index)
                  if (row?.documentInspection) {
                    feild.visible = false
                  } else {
                    feild.visible = true
                  }
                }`),
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
  },

}
