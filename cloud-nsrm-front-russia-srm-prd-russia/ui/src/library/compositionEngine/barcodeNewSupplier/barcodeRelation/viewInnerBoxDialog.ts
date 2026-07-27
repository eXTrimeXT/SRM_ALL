// 查看内箱条码
import { generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'

export default {
  type: 'void',
  title: '内箱条码',
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
          title: '内箱条码',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料名称
        materialName: {
          type: 'string',
          title: '物料名称',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料编码
        materialCode: {
          type: 'string',
          title: '物料编码',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 品类名称
        categoryName: {
          type: 'string',
          title: '品类名称',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 品类编码
        categoryCode: {
          type: 'string',
          title: '品类编码',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 供应商编码
        vendorCode: {
          type: 'string',
          title: '供应商编码',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 单位
        unit: {
          type: 'string',
          title: '单位',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 关联物料数量
        relationMaterialQuantity: {
          type: 'string',
          title: '关联物料数量',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 生成内箱条码数量
        generateTagQuantity: {
          type: 'string',
          title: '生成内箱条码数量',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 物料数量
        materialQuantity: {
          type: 'string',
          title: '物料数量',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码生成规则
        tagRuleName: {
          type: 'string',
          title: '条码生成规则',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码样式
        tagType: {
          type: 'string',
          title: '条码样式',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 打印次数
        printCount: {
          type: 'string',
          title: '打印次数',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 条码状态
        status: {
          type: 'string',
          title: '条码状态',
          'x-component': 'Select',
          enum: [
            {
              label: '生效',
              value: 'Y'
            },
            {
              label: '失效',
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
          title: '绑定状态',
          'x-component': 'Select',
          enum: [
            {
              label: '已绑定',
              value: 'Y'
            },
            {
              label: '未绑定',
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
          title: '打印状态',
          'x-component': 'Select',
          enum: [
            {
              label: '已打印',
              value: 'Y'
            },
            {
              label: '未打印',
              value: 'N'
            }
          ],
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 创建日期
        creationDate: {
          type: 'string',
          title: '创建日期',
          'x-component-props': {
            format: 'yyyy-MM-dd'
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 创建人
        createdFullName: {
          type: 'string',
          title: '创建人',
          'x-render-table-column': {
            minWidth: 120
          }
        }
      })
    }
  }
}
