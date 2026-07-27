import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";
import {editTableFormItemValid} from "lib@/components/render-engine";



export const financeInfoList = {
  financeInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.financeInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              disabled: expression(`$form.query('state').get('data').$disabled || $form.query('.orgCategorys').take().value.length == 0`),
              '@click': expression(`() => {
                 $self.query('financeInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      financeInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'financeInfoId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 引入组织
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-hidden': true
          },
          orgId: {
            type: 'string',
            title: i18nExpression('vendorMod.ceeaOrgName2'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'Select',
            'x-component-props': {
              'disabled': expression(`$disabled`),
              '@change': expression(`(val) => {
                  const orgCategorys = $form.query('.orgCategorys').take().value
                  let datas = []
                  orgCategorys.forEach(resData => {
                    const objs = {
                      key:resData.orgId,
                      label:resData.orgName,
                      value:resData.orgId
                    }
                    datas.push(objs)
                  })
                  let dictItem = datas.find(i => i.orgId === val) || {}
                  let row = $table.getRowByIndex($self.index)
                  row.orgCode = dictItem.orgCode
                  row.orgName = dictItem.orgName
               }`)
            },
            'x-reactions': [
              expression(`(field) => {
                const orgCategorys = $form.query('.orgCategorys').take().value
                let datas = []
                orgCategorys.forEach(resData => {
                  const objs = {
                    key:resData.orgId,
                    label:resData.orgName,
                    value:resData.orgId
                  }
                  datas.push(objs)
                })
                // 去重
                let attrId = []
                let attr = []
                datas.forEach((dataE, index) => {
                  if (!attrId.includes(dataE.value) || index == 0) {
                    attrId.push(dataE.value)
                    attr.push(dataE)
                  }
                })
                $self.dataSource = attr
              }`)
            ],
            ...editTableFormItemValid
          },
          // 工厂代码
          factoryCode: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryCode'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$disabled`),
              maxlength: "50",
              'show-word-limit': true,
              '@onKeyUp': "value=value.replace(/[^\w\\/]/ig,'')"
            }
          },
          // 结算币种
          clearCurrency: {
            type: 'string',
            title: i18nExpression('vendorMod.clearCurrency'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BID_TENDER_CURRENCY',
              'disabled': expression(`$disabled`)
            },
            ...editTableFormItemValid
          },
          // 付款方式
          paymentMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentMethod'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_METHOD',
              'disabled': expression(`$disabled`)
            },
            ...editTableFormItemValid
          },
          // 付款账期
          paymentTerms: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentTerms'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_TERMS',
              'disabled': expression(`$disabled`)
            },
            ...editTableFormItemValid
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
                  'disabled': expression(`$disabled`),
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
