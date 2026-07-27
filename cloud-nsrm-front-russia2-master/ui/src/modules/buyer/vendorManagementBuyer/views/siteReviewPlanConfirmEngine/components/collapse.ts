import {
  changeFieldVisibleByDeps,
  expression,
  generateXindexInOrder,
  i18nExpression,
  queryFieldValueExpression
} from "@meicloud/render-engine";

import {
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const collapseMain = {
  type: 'void',
  'x-component': 'Collapse',
    properties: generateXindexInOrder({
      // 工作小组人员
      workingGroupStaff: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.workingGroupStaff')
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
                'x-hidden': '{{$form.readPretty}}',
                title: i18nExpression('common.add'),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression('({ rowIndex }) => $form.query(".tableStaff").take().componentProps.componentInstance.addRow("unshift")')
                }
              }
            }
          },
          tableStaff: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              class: 'table-view-vxe-table',
              editMode: true,
              pagination: false,
              sortable: false
            },
            'x-query-engine-skip': true,
            'x-query-engine-relation': 'siteReviewPlanConfirmPersons:*',
            properties: generateXindexInOrder({
              userAccount: {
                type: 'string',
                title: i18nExpression('vendorMod.userAccount'), // 成员账号
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  'show-input': expression(`$self.value`),
                  'read-pretty': '{{$form.readPretty}}',
                  'show-key': 'username',
                  'name': 'scc_rbac_user_display',
                  '@close-quicksearch': expression(`(val, scope) => {
                    const row = $table.getRowByIndex($self.index)
                    row.userAccount = val ? val.username : ''
                    row.userName = val ? val.nickname : ''
                    row.userId = val ? val.userId : ''
                    row.userTel = val ? val.phone : ''
                    row.userEmail = val ? val.email : ''
                  }`)
                }
              },
              userName: {
                type: 'string',
                title: i18nExpression('vendorMod.userName2'), // 成员名称
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  // 跳过行内编辑
                  disabled: true
                }
              },
              userTel: {
                type: 'string',
                title: i18nExpression('vendorMod.mobilePhone'), // 手机号码
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  // 跳过行内编辑
                  disabled: true
                }
              },
              userEmail: {
                type: 'string',
                title: i18nExpression('vendorMod.emailAddress'), // 电子邮箱
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  // 跳过行内编辑
                  disabled: true
                }
              },
              userPost: {
                type: 'string',
                title: i18nExpression('bidMod.position'), // 岗位
                'x-component-props': {
                  disabled: '{{$form.readPretty}}'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              onSiteFlag: {
                type: 'string',
                title: i18nExpression('vendorMod.onSiteFlag'), // 是否到现场
                'x-component': 'Checkbox',
                'x-component-props': {
                  'true-label': 'Y',
                  'false-label': 'N',
                  disabled: '{{$form.readPretty}}'
                },
                'x-render-table-column': {
                  minWidth: 100
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
                    'x-hidden': '{{$form.readPretty}}',
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => {
                        $table.remove($self.index)
                      }`)
                    }
                  }
                }
              }
            })
          }
        }
      },
      // 到访地址
      visitingAddress: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: expression('$t("vendorMod.visitingAddress")')
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
                'x-hidden': '{{$form.readPretty}}',
                title: i18nExpression('common.add'),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression('({ rowIndex }) => $form.query(".tableAddress").take().componentProps.componentInstance.addRow("unshift")')
                }
              }
            }
          },
          tableAddress: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              class: 'table-view-vxe-table',
              editMode: true,
              pagination: false,
              sortable: false
            },
            'x-query-engine-skip': true,
            'x-query-engine-relation': 'siteReviewPlanConfirmAddress:*',
            properties: generateXindexInOrder({
              country: {
                type: 'string',
                title: i18nExpression('components.address.country'), // 国家
                'x-component': 'DictSelect',
                'x-component-props': {
                  disabled: '{{$form.readPretty}}',
                  code: 'country',
                  filterable: true,
                  '@change-value': expression(`(_, node) => {
                    const row = $table.getRowByIndex($self.index)
                    // 选择国外就清理省市区，并且禁用
                      if (row.country !== 'CN') {
                        row.province = null
                        row.city = null
                      }
                  }`)
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              province: {
                type: 'string',
                title: i18nExpression('components.address.area'), // 省
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PROVINCE',
                  'custom-select-type': 'PROVINCE',
                  disabled: `{{ $table.getRowByIndex($self.index).country !== 'CN' ||  $form.readPretty}}`
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              city: {
                type: 'string',
                title: i18nExpression('components.address.city'), // 城市
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: `{{ $table.getRowByIndex($self.index).province }}`,
                  'custom-select-type': 'CITY',
                  disabled: `{{ $table.getRowByIndex($self.index).country !== 'CN' ||  $form.readPretty}}`
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              addressDetail: {
                type: 'string',
                title: i18nExpression('components.address.detailAddress'), // 详细地址
                'x-component-props': {
                  disabled: '{{$form.readPretty}}'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              postCode: {
                type: 'string',
                title: i18nExpression('components.address.postalCode'), // 邮政编码
                'x-component-props': {
                  disabled: '{{$form.readPretty}}'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              siteComment: {
                type: 'string',
                title: i18nExpression('components.address.remark'), // 地址备注
                'x-component-props': {
                  disabled: '{{$form.readPretty}}'
                },
                'x-render-table-column': {
                  minWidth: 100
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
                    'x-hidden': '{{$form.readPretty}}',
                    title: "{{$t('common.delete')}}",
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => {
                        $table.remove($self.index)
                      }`)
                    }
                  }
                }
              }
            })
          }
        }
      }
    })
}
