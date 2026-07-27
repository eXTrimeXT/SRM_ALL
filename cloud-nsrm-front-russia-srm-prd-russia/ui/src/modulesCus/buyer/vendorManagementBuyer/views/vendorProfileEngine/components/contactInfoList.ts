import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine";
import {
  feedbackLayoutIsPopover
} from 'lib@/components/render-engine/schema-segments'


export const contactInfoList = {
  contactInfoList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.contactInfo'),
                value: $form.values.extRejectAttribute4,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute4 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-query-engine-skip': true,
    properties: {
      contactInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 250,
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`),
              code: 'POSITION'
            },
            'x-component': 'DictSelect'
          },
          // 联系方式
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
            ...feedbackLayoutIsPopover,
            'x-validator': {
              required: generateCharExpressionByFunction(({ $form }) => {
                return $form.query('state').get('data').userType !== 'OUT'
              }),
              message: i18nExpression('common.requiredField')
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
              'disabled': expression(`$disabled`)
            }
          }
        })
      }
    }
  }
}
