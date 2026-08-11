/* eslint-disable quotes */
import { expression, i18nExpression } from "@meicloud/render-engine"

import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

// 禁用
export const sourceTypeBoolean = `$form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE || $form.readPretty`

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('bidMod.wonBidRowInfo')
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
        field.visible = !$form.readPretty && $form.values.sourceFromType === PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE
      }`),
      properties: {
        add: {
          type: 'void',
          title: i18nExpression('common.add'),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => $addOneBidItem($form)`)
          }
        }
        // 处理虚拟物料
        // dealVisiualItem: {
        //   type: 'void',
        //   title: i18nExpression('bidMod.dealVisitualItem'),
        //   'x-visible': expression(sourceTypeBoolean),
        //   'x-component-props': {
        //     type: 'ghost',
        //     '@click': expression(`() => $dealVisiualItem($form,$self,$queryEngine,$message)`)
        //   }
        // }
      }
    },
    itemList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        height: '450px',
        sortable: false,
        pagination: {
          static: true,
          pageSize: 10,
          layout: 'total, prev, pager, next, sizes, jumper',
          pageSizes: [10, 30, 50]
        },
        checkboxConfig: {
          trigger: 'default'
        },
        static: true,
        dblclickEditable: expression('!$form.readPretty'),
        preColumns: expression(`($form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE && !$form.readPretty) ? 'checkbox,seq' : 'seq'`),
        // preColumns: 'checkbox,seq',
        // 联表主键的 key
        primaryKey: 'approvalItemId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'itemList:*',
      properties: {
        approvalItemId: {
          type: 'string',
          'x-hidden': true,
          'x-query-engine-primary-key': true
        },
        // 价格类型
        priceType: {
          type: 'string',
          title: i18nExpression('bid_mod.priceType'),
          'x-render-table-column': {
            width: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DMAND_LINE_TYPE',
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 业务实体
        orgOuId: {
          type: 'string',
          title: i18nExpression('bid_mod.businessEntity'),
          'x-render-table-column': {
            width: 150,
            static: false
          },
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            'read-pretty': expression(`$form.readPretty || !$self.editable`),
            'parent-id': -1,
            'nodet-type': 'OU',
            'clearable': false,
            'disabled': expression(sourceTypeBoolean),
            '@select': expression(`(node) => setOrgObj($form,$table,$self,node)`)
          }
        },
        // 库存组织
        orgInvId: {
          type: 'string',
          title: i18nExpression('bid_mod.inv'),
          'x-render-table-column': {
            width: 150,
            static: false
          },
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            'read-pretty': expression(`$form.readPretty || !$self.editable`),
            'parent-id': expression(`$table.getRowByIndex($self.index).orgOuId`),
            'node-type': 'INV',
            'clearable': false,
            'disabled': expression(sourceTypeBoolean),
            '@select': expression(`(node) => setOrganizationObj($form,$table,$self,node)`)
          }
        },
        // 到货地点
        arrivalPlace: {
          type: 'string',
          title: i18nExpression('contractMod.arrivalPlace'),
          'x-render-table-column': {
            width: 150,
            static: false
          },
          'x-component': expression(`($form.readPretty || !$self.editable) ? 'RenderAsyncText' : 'ProviceCity'`),
          'x-component-props': {
            cellValue: expression(`$self.value`)
          }
        },
        // 供应商编码
        vendorCode: {
          type: 'string',
          title: i18nExpression('common.vendorCode'),
          'x-render-table-column': {
            width: 150
          },
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            'read-pretty': expression(`$form.readPretty`),
            disabled: expression(sourceTypeBoolean),
            'show-key': 'companyCode',
            name: 'scc_sup_company_info',
            '@close-quicksearch': expression(`(val) => setVendorObj($form,$table,$self,val)`)
          }
        },
        // 供应商名称
        vendorName: {
          type: 'string',
          title: i18nExpression('bidMod.vendorName'),
          'x-render-table-column': {
            width: 150,
            static: true
          }
        },
        // 物料编码
        itemCode: {
          type: 'string',
          title: i18nExpression('bidMod.itemCode'),
          'x-render-table-column': {
            width: 150
          },
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            'read-pretty': expression(`$form.readPretty`),
            'show-key': 'materialCode',
            name: 'scc_base_material_item_inv_enable',
            disabled: expression(`$table.getRowByIndex($self.index).noCodeItem !== 'Y' && $form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`),
            'disabled-select': expression(`!$table.getRowByIndex($self.index).orgOuId || !$table.getRowByIndex($self.index).orgInvId`),
            'pre-query-data': expression(`{
              'scboa.ORGANIZATION_ID':$table.getRowByIndex($self.index).orgInvId
            }`),
            '@close-quicksearch': expression(`(val) => setItemObj($form,$table,$self,val)`),
            '@before-open': expression(`(value,callback) => itemCodeQuickSearchBeforeOpen(value,callback,$table,$self,$message)`)
          }
        },
        // 物料名称
        itemDesc: {
          type: 'string',
          title: i18nExpression('bidMod.itemDesc'),
          'x-render-table-column': {
            minWidth: 150,
            static: true
          }
        },
        // 品类
        categoryName: {
          type: 'string',
          title: i18nExpression('common.category'),
          'x-render-table-column': {
            width: 100,
            static: true
          }
        },
        // 最小起订量
        moq: {
          type: 'string',
          title: i18nExpression('dataConfMod.orderQuantityMinimum'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'InputNumber',
          'x-component-props': {
            min: 0,
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 需求数量
        needNum: {
          type: 'string',
          title: i18nExpression('bidMod.demandQuantity2'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'InputNumber',
          'x-component-props': {
            min: 0,
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 中标数量
        winNum: {
          type: 'string',
          title: i18nExpression('bidMod.quotaQuantity'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'InputNumber',
          'x-component-props': {
            min: 0,
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 单位
        unit: {
          type: 'string',
          title: i18nExpression('bid_mod.unit'),
          'x-render-table-column': {
            width: 60,
            static: true
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'unit'
          }
        },
        // 原币含税单价
        orderTaxPrice: {
          type: 'string',
          title: i18nExpression('bidMod.orderTaxPrice'),
          'x-render-table-column': {
            width: 100,
            static: true
          }
        },
        // 币种
        orderCurrency: {
          type: 'string',
          title: i18nExpression('bidMod.allAurrency'),
          'x-render-table-column': {
            width: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'currency',
            disabled: expression(sourceTypeBoolean),
            '@change-value': expression(`(value,dictItem) => setCurrency($form,$table,$self,value,dictItem)`)
          }
        },
        // 税率
        taxKey: {
          type: 'string',
          title: i18nExpression('bidMod.taxRate2'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax',
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 原币未税单价 ORDER_NOTAX_PRICE
        orderNotaxPrice: {
          type: 'string',
          title: i18nExpression('bidMod.orderNotaxPrice'),
          'x-render-table-column': {
            width: 110
          },
          'x-component': 'InputNumber',
          'x-component-props': {
            disabled: expression(`$table.getRowByIndex($self.index).ladderPrice ==='Y' || $form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`)
          }
        },
        // 汇率
        exchangeRate: {
          type: 'string',
          title: i18nExpression('contractMod.exchangeRate'),
          'x-render-table-column': {
            width: 110
          },
          'x-visible': expression(`!$attrs.params.flag === 'add'`),
          'x-read-pretty': true
        },
        // 是否阶梯报价
        ladderPrice: {
          type: 'string',
          title: i18nExpression('bidMod.isLadder'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'Checkbox',
          'x-component-props': {
            disabled: expression(sourceTypeBoolean),
            'true-label': 'Y',
            'false-label': 'N'
          }
        },
        // 阶梯价报价
        ladderQuote: {
          type: 'string',
          'x-query-engine-skip': true,
          title: i18nExpression('bidMod.ladderQuote'),
          'x-render-table-column': {
            width: 110
          },
          'x-component': 'TableButton',
          'x-component-props': {
            type: 'text',
            disabled: expression(`$table.getRowByIndex($self.index).ladderPrice !== 'Y'`),
            '@click': expression(`({row}) => {
              ladderClick($form,$table,$self,$message)
            }`)
          }
        },
        // 付款条款
        paymentList: {
          type: 'string',
          'x-query-engine-skip': true,
          title: i18nExpression('paymentType.paymentType'),
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'TableButton',
          'x-component-props': {
            disabled: false,
            type: 'text',
            '@click': expression(`() => {
              openPaymentTypeDialog($form,$table,$self,$message)
            }`)
          }
        },
        // 供货周期(自然天)
        leadTime: {
          type: 'string',
          title: i18nExpression('bidMod.deliveryCycleDays'),
          'x-render-table-column': {
            width: 140
          },
          'x-component': 'InputNumber',
          'x-component-props': {
            min: 0,
            disabled: expression(sourceTypeBoolean)
          }
        },
        // 价格执行有效期自
        priceStartTime: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.creationDate, '{y}-{m}-{d}')
            }`),
            disabled: expression(sourceTypeBoolean)
          },
          title: i18nExpression('bid_mod.priceStartTime'),
          'x-render-table-column': {
            width: 170
          }
        },
        // 价格执行有效期至
        priceEndTime: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.creationDate, '{y}-{m}-{d}')
            }`),
            disabled: expression(sourceTypeBoolean)
          },
          title: i18nExpression('bid_mod.priceEndTime'),
          'x-render-table-column': {
            width: 170
          }
        },
        // 备注
        remark: {
          type: 'string',
          title: i18nExpression('common.remark'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 贸易术语
        tradeTerm: {
          type: 'string',
          title: i18nExpression('bidMod.tradeTerm'),
          'x-render-table-column': {
            minWidth: 150
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'trade_clause',
            disabled: expression(`$form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`)
          }
        },
        // 操作
        operation: {
          type: 'void',
          title: i18nExpression('bidMod.operation'),
          'x-render-table-column': {
            width: 70,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
            field.visible = !$form.readPretty && $form.values.sourceFromType === PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE
          }`),
          properties: {
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                '@click': expression(`({rowIndex}) => {
                  deleteBidItem($form,$table,$self,$message,rowIndex)
                }`)
              }
            }
          }
        }
      }
    }
  }
}
