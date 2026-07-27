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
            default: null,
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
              'disabled': expression(`$disabled`),
              '@change':expression(`$managementChange($self.value, 'ISO9001质量体系认证', $form)`)
              // '@change':expression(`() => {
              //    console.log($self.value)
              // }`)
            },
            title: i18nExpression(`vendorMod.msgIfPass1`)
          },
          // 是否通过ISO14001环境体系认证(如是请上传附件)
          'ifIsoEnviron': {
            type: 'string',
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
              'disabled': expression(`$disabled`),
              '@change':expression(`$managementChange($self.value, 'ISO14001环境体系认证', $form)`)
            },
            title: i18nExpression(`vendorMod.msgIfPass2`)
          },
          // 是否通过OHSAS18000职业、健康安全体系认证(如是请上传附件)
          'ifOhsasSafe': {
            type: 'string',
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
              'disabled': expression(`$disabled`),
              '@change':expression(`$managementChange($self.value, 'OHSAS18000职业、健康安全体系认证', $form)`)
            },
            title: i18nExpression(`vendorMod.msgIfPass3`)
          },
          // 其他认证情况(如是请上传附件)
          'otherAuthSit': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            title: i18nExpression(`vendorMod.msgIfPass4`)
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
          sortable: false
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
              'disabled': expression(`$disabled`)
            }
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
              'disabled': expression(`$disabled`),
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`),
              '@change': expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)
            }
          },
          // 认证时间
          authDate: {
            type: 'date',
            title: i18nExpression('vendorMod.authDate'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
            }
          },
          // 证件有效期至
          endDate: {
            type: 'date',
            title: i18nExpression('vendorMod.certUntil'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          }
        })
      }
    }
  }

}
