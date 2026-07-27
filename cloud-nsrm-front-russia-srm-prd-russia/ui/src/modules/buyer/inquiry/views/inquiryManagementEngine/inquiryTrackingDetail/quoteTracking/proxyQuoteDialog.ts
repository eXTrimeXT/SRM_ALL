/**
 * @description 代理报价 引入供应商报价页内容
 */
import {
  generateCharExpressionByFunction,
  generateCharFunctionExpression,
  i18nExpression
} from '@meicloud/render-engine'
import DetailHeaderSegment from 'lib@/compositionEngine/inquiry/quoteDetail/detailHeader'
import ItemInfoSegment from 'lib@/compositionEngine/inquiry/quoteDetail/itemInfo'

const ProxyQuoteDialogSegment = function (scope: any) {
  return {
    proxyQuoteDialog: {
      type: 'object',
      title: i18nExpression('bid_mod.proxyQuoteHandle'),
      'x-component': 'RDialog',
      'x-component-props': {
        class: 'dialogMain',
        size: 'xLarge',
        top: '2vh',
        cancelButtonProps: false,
        okButtonText: i18nExpression('common.submit'),
        beforeClose: generateCharFunctionExpression(({ $form, $self, $message, $queryEngine, $projectId }, done, type) => {
          if (!type || type === 'cancel') {
            done()
            return
          }

          done()

          // $form.query($self.address.concat('targetPriceItemList')).take(async (field: any) => {
          //   for (const item of field.value) {
          //     if (!item.notaxTargetPrice) {
          //       $message.warning('请输入目标价!')
          //       return
          //     }
          //   }
          //
          //   const response = await $queryEngine.request.baseRequest({
          //     type: 'InqSouProject',
          //     action: 'setTargetPrice',
          //     payload: [
          //       {
          //         projectId: $projectId,
          //         itemList: field.value
          //       }
          //     ]
          //   }).catch(() => {})
          //
          //   if (response) {
          //     $message.success('目标价保存成功!')
          //     $queryEngine.request.read()
          //
          //     done()
          //   }
          // })
        }),
        '@opened': generateCharFunctionExpression(async ({ $form, $self, $queryEngine }) => {
          console.log($self.data)
          // 查询报价页详情数据
          const response = await $queryEngine.request.baseRequest({
            type: 'InqSouProjectForBuyer',
            action: 'queryInqOrderItemsForOrderForBuyer',
            query: { '*': {} },
            payload: [
              {
                ...$self.data.editRow
              }
            ],
            tree: true
          }).catch(() => {})

          console.log(response)

          // const takeObj: any = $form.query($self.address.concat('targetPriceItemList')).take()

          // console.log(response)
          //
          // if (response) {
          //   takeObj.setValue((response.data || []).concat())
          // }
        })
      },

      'x-data': {
        editRow: null
      },

      properties: {
        // 详情头
        ...DetailHeaderSegment(),

        // 代理报价
        proxyQuoteForm: {
          type: 'void',
          'x-decorator': 'FormLayout',
          'x-decorator-props': {
            colon: false,
            layout: 'horizontal',
            feedbackLayout: 'terse'
          },
          'x-component': 'FormGrid',
          'x-component-props': {
            minColumns: 1,
            maxColumns: 4,
            columnGap: 32
          },
          properties: {
            proxyDocId: {
              type: 'string',
              title: '代理报价授权证明',
              'x-decorator': 'FormItem',
              'x-component': 'SrmCommonFile',
              'x-component-props': {
                readonly: false,
                defaultFile: {
                  fileId: generateCharExpressionByFunction(({ $self }) => $self.value),
                  fileName: generateCharExpressionByFunction(({ $self, $values }) => {
                    return $self.value ? $values.proxyQuoteDialog?.proxyFileName : ''
                  })
                },
                '@on-change': generateCharFunctionExpression(({ $self, $values }, { file }) => {
                  const { fileId = '', fileName = '' } = file || {}
                  $self.value = fileId.toString()
                  $values.proxyQuoteDialog.proxyFileName = fileName
                })
              }
            }
          }
        },

        // 物料列表
        projectInfoCollapse: {
          type: 'void',
          'x-component': 'Collapse',
          'x-component-props': {
            defaultOpenPanelCount: 1
          },
          properties: {
            // 物料信息 共用
            itemInfo: {
              type: 'void',
              'x-component': 'CollapseItem',
              'x-component-props': {
                title: i18nExpression('bidMod.itemInfo')
              },
              properties: {
                ...ItemInfoSegment(scope)
              }
            }
          }
        }
      }
    }
  }
}

export default ProxyQuoteDialogSegment
