/**
 * @description 目标价
 */
import { i18nExpression, generateXindexInOrder, generateCharFunctionExpression } from '@meicloud/render-engine'

const TargetPriceDetailDialogSegment: Record<any, any> = {
  targetPriceDetailDialog: {
    type: 'void',
    title: '目标价',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'dialogMain',
      size: 'large',
      footer: true,
      cancelText: i18nExpression('common.cancel'),
      okText: i18nExpression('common.submit'),
      beforeClose: generateCharFunctionExpression(({ $form, $self, $message, $queryEngine, $projectId }, done, type) => {
        if (!type || type === 'cancel') {
          done()
          return
        }

        $form.query($self.address.concat('targetPriceItemList')).take(async (field: any) => {
          for (const item of field.value) {
            if (!item.notaxTargetPrice) {
              $message.warning('请输入目标价!')
              return
            }
          }

          const response = await $queryEngine.request.baseRequest({
            type: 'InqSouProjectForBuyer',
            action: 'setTargetPrice',
            payload: field.value,
            query: {}
          }).catch(() => {})

          if (response) {
            $message.success('目标价保存成功!')
            $queryEngine.request.read()

            field.value = []
            done()
          }
        })
      }),
      '@opened': generateCharFunctionExpression(async ({ $form, $self, $queryEngine, $projectId }) => {
        const takeObj: any = $form.query($self.address.concat('targetPriceItemList')).take()

        // 查询目标价
        const response = await $queryEngine.request.baseRequest({
          type: 'InqSouProjectForBuyer',
          action: 'getTargetPrice',
          query: { '*': {} },
          payload: [$projectId],
          tree: true
        }).catch(() => {})

        if (response) {
          takeObj.setValue((response.data || []).concat())
        }
      })
    },
    properties: {
      // 表格
      targetPriceItemList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: false,
          sortable: false,
          editMode: true,
          pagination: false
        },
        'x-read-pretty': true,
        properties: generateXindexInOrder({
          // 物料编码
          itemCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.itemCode'),
              minWidth: 100
            }
          },
          // 物料名称
          itemDesc: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.itemName'),
              minWidth: 150
            }
          },
          // 采购分类
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.purcategoryName'),
              minWidth: 100
            }
          },
          // 行类型
          itemType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.itemType'),
              minWidth: 100
            },
            'x-read-pretty': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DMAND_LINE_TYPE'
            }
          },
          // 预计采购量
          requireQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '预计采购量',
              minWidth: 100
            }
          },
          // 单位
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.unit'),
              minWidth: 100
            },
            'x-read-pretty': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'unit'
            }
          },
          // 目标价（未税）
          notaxTargetPrice: {
            type: 'string',
            'x-render-table-column': {
              title: '目标价（未税）',
              minWidth: 130
            },
            'x-read-pretty': false,
            // TODO 输入数字
            'x-component': 'Input',
            'x-component-props': {
              // TODO 查看模式禁用
              disabled: false
            }
          }
        })
      }
    }
  }
}

export default TargetPriceDetailDialogSegment
