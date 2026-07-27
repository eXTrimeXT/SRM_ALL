// 查看内箱条码
import { generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('orderMod.innerBoxBarcode'),
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'xLarge',
    footer: true,
    okButtonProps: false,
    cancelText: i18nExpression('common.close'),
    beforeClose: expression(`(done, type) => {
      console.log(done,type,'done type')
      if ( type === 'ok') {
        console.log('type = ok')
      }
      done()
    }`)
  },
  properties: {
    innerBarTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        height: 300,
        primaryKey: 'innerBoxId'
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        // 内箱条码
        innerBoxCode: {
          type: 'string',
          title: i18nExpression('orderMod.innerBoxBarcode'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料名称
        materialName: {
          type: 'string',
          title: i18nExpression('orderMod.materialName'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料编码
        materialCode: {
          type: 'string',
          title: i18nExpression('orderMod.materialCode'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 品类名称
        categoryName: {
          type: 'string',
          title: i18nExpression('common.categoryName'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 品类编码
        categoryCode: {
          type: 'string',
          title: i18nExpression('common.categoryCode'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 供应商编码
        vendorCode: {
          type: 'string',
          title: i18nExpression('common.vendorCode'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 单位
        unit: {
          type: 'string',
          title: i18nExpression('orderMod.unit'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 关联物料数量
        relationMaterialQuantity: {
          type: 'string',
          title: i18nExpression('hierarchical.associated'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 生成内箱条码数量
        generateTagQuantity: {
          type: 'string',
          title: i18nExpression('hierarchical.Generatenumber'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料数量
        materialQuantity: {
          type: 'string',
          title: i18nExpression('hierarchical.quantitynumber'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码生成规则
        tagRuleName: {
          type: 'string',
          title: i18nExpression('hierarchical.Barcodegenerationrules'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码样式
        tagType: {
          type: 'string',
          title: i18nExpression('hierarchical.Barcodestyle'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 打印次数
        printCount: {
          type: 'string',
          title: i18nExpression('hierarchical.printsNumber'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码状态
        status: {
          type: 'string',
          title: i18nExpression('hierarchical.Barcodestatus'),
          'x-component': 'Select',
          enum: [
            {
              label: i18nExpression('common.active'),
              value: 'Y'
            },
            {
              label: i18nExpression('common.inactive'),
              value: 'N'
            }
          ],
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 绑定状态
        boundFlag: {
          type: 'string',
          title: i18nExpression('hierarchical.Bindingstatus'),
          'x-component': 'Select',
          enum: [
            {
              label: i18nExpression('buyerDeliveryOrder.bound'),
              value: 'Y'
            },
            {
              label: i18nExpression('buyerDeliveryOrder.unbound'),
              value: 'N'
            }
          ],
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 打印状态
        printedFlag: {
          type: 'string',
          title: i18nExpression('hierarchical.Printstatus'),
          'x-component': 'Select',
          enum: [
            {
              label: i18nExpression('buyerDeliveryOrder.printed'),
              value: 'Y'
            },
            {
              label: i18nExpression('buyerDeliveryOrder.unprinted'),
              value: 'N'
            }
          ],
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 创建日期
        creationDate: {
          title: i18nExpression('hierarchical.Dateofcreation'),
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.creationDate, '{y}-{m}-{d}')
            }`)
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 创建人
        createdFullName: {
          type: 'string',
          title: i18nExpression('common.creator'),
          'x-render-table-column': {
            minWidth: 120
          }
        }
      })
    }
  }
}
