/**
 * @description 报价跟踪
 */
import {
  generateCharFunctionExpression,
  i18nExpression,
  generateXindexInOrder,
  generateCharExpressionByFunction,
  generateCharReactionExpression
} from '@meicloud/render-engine'
import TargetPriceDetailDialogSegment from './quoteTracking/targetPriceDetailDialog'
import QuoteDetailDialogSegment from './quoteTracking/quoteDetailDialog'
import ProxyQuoteDialogSegment from './quoteTracking/proxyQuoteDialog'

const QuoteTrackingSegment = function (scope: any) {
  return {
    // 报价跟踪顶部区域
    quoteTrackingTop: {
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
        maxColumns: 3,
        columnGap: 32
      },
      properties: {
        // 设定目标价
        targetPrice: {
          type: 'void',
          title: '',
          'x-decorator': 'FormItem',
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form }) => {
              $form.query('targetPriceDetailDialog').take(field => {
                field.setComponentProps({ visible: true })
              })
            })
          },
          'x-content': generateCharExpressionByFunction(({ $values }) => {
            return $values.header?.currentRound === 1 && ['ACCEPT_ORDER', 'ORDER_END'].includes($values.header?.extProjectStatus)
              ? '设定目标价'
              : '查看目标价'
          })
        },
        // 本轮需报价供应商数量
        currentRoundTotalCtn: {
          type: 'string',
          title: i18nExpression('bidMod.inQvendorNums'),
          // 公开报价 && 首轮 不展示
          'x-visible': generateCharExpressionByFunction(({ $values }) => {
            return !($values.header?.currentRound === 1 && $values.header?.publishScope === 'OPEN_TENDER')
          }),
          'x-read-pretty': true,
          'x-decorator': 'FormItem'
        },
        // 已提交报价供应商
        currentRoundQuotedCtn: {
          type: 'string',
          title: i18nExpression('bidMod.inQsubmitNums'),
          'x-read-pretty': true,
          'x-decorator': 'FormItem'
        }
      }
    },

    // 列表数据
    trackingList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'seq',
        class: 'table-view-vxe-table',
        openCustomTable: false,
        sortable: false,
        pagination: false,
        editMode: false
      },
      properties: generateXindexInOrder({
        // 供应商编码
        vendorCode: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.vendorCode'),
            minWidth: 120
          }
        },
        // 供应商名称
        vendorName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.vendorName'),
            minWidth: 150
          }
        },
        // 有效报价
        orderNo: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.validQuote'),
            minWidth: 165
          },
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            disabled: generateCharExpressionByFunction(({ $values }) => {
              return !(
                $values.header?.needEncryptPrice !== 'Y' ||
                // 密封报价 已截止报价才允许看，接受报价中不允许看 ACCEPT_ORDER
                ($values.header?.needEncryptPrice === 'Y' && $values.header?.extProjectStatus !== 'ACCEPT_ORDER')
              )
            }),
            '@click': generateCharFunctionExpression(({ $table, $self, $getFieldParentFieldFormPath }) => {
              $self.query($getFieldParentFieldFormPath($self, 3).concat('quoteDetailDialog')).take((field: any) => {
                console.log(field)
                field.setData({
                  viewRow: $table.getRowByIndex($self.index)
                })
                field.setComponentProps({ visible: true })
              })
            })
          }
        },
        // 总价（未税/元）
        standardNotaxTotalPrice: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quotetotalAmount'),
            minWidth: 120
          },
          'x-content': generateCharExpressionByFunction(({ $values, $self }) => {
            return (
              $values.header?.needEncryptPrice !== 'Y' ||
              // 密封报价 已截止报价才允许看，接受报价中不允许看 ACCEPT_ORDER
              ($values.header?.needEncryptPrice === 'Y' && $values.header?.extProjectStatus !== 'ACCEPT_ORDER')
            ) ? $self.value : ''
          })
        },
        // 报价状态
        orderStatus: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quoteStatus'),
            minWidth: 100
          },
          'x-read-pretty': true,
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'INQ_SOU_ORDER_STATUS'
          }
        },
        // 报价人
        submitFullName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quoteMan'),
            minWidth: 120
          }
        },
        // 报价时间
        submitTime: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quotedTime'),
            minWidth: 160
          }
        },
        // 手机号码
        phone: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.tel'),
            minWidth: 120
          }
        },
        // 邮箱
        email: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('common.email'),
            minWidth: 150
          }
        },
        // 供应商IP
        submitByIp: {
          type: 'string',
          'x-render-table-column': {
            title: '供应商IP',
            minWidth: 120
          },
          // 判断有重复的IP标记红色警告
          'x-reactions': generateCharReactionExpression(({ $values, $table, $self }, field) => {
            if ($values.trackingList.length > 1) {
              if ($values.trackingList.find((item: any, index: number) => item.submitByIp && item.submitByIp === $self.value && $self.index !== index)) {
                field.setComponentProps({
                  style: 'color: red'
                })
              }
            }
          })
        },
        // 撤回原因
        withdrawReason: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.withdrawReason'),
            minWidth: 100
          }
        },
        // 作废原因
        rejectReason: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.cancelDescription'),
            minWidth: 100
          }
        },
        operation: {
          type: 'void',
          'x-render-table-column': {
            title: i18nExpression('common.operation'),
            width: 150,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          properties: {
            // 作废报价
            cancelQuote: {
              type: 'void',
              title: i18nExpression('bidMod.invalidQuotation'),
              // 接收报价中/已截止报价
              'x-visible': generateCharExpressionByFunction(({ $table, $self, $values }) => {
                return $table.getRowByIndex($self.index).orderStatus === 'SUBMISSION' && ['ACCEPT_ORDER', 'ORDER_END'].includes($values.header?.extProjectStatus)
              }),
              'x-component-props': {
                type: 'text',
                '@click': generateCharFunctionExpression(async ({ $values, $queryEngine, $prompt, $t, $message }, { row }) => {
                  const promptResult = await $prompt(
                    '作废原因',
                    '作废报价',
                    {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      inputValidator: (value: any) => !(!value || value.length > 500),
                      inputErrorMessage: '作废原因必填并且长度不能超过500字符！'
                    }
                  )

                  if (!promptResult) {
                    return
                  }

                  if (!promptResult.value) {
                    $message.warning('请输入撤回原因！')
                    return
                  }
                  if (promptResult.value.length > 250) {
                    $message.warning('撤回原因长度最多250个字符！')
                    return
                  }

                  const response = await $queryEngine.request.baseRequest(
                    {
                      type: 'InqSouOrderForBuyer',
                      action: 'cancelOrder',
                      query: { '*': {} },
                      payload: [
                        {
                          projectId: $values.projectId,
                          vendorId: row.vendorId,
                          cancelReason: promptResult.value
                        }
                      ]
                    }
                  ).catch(() => {})

                  if (response) {
                    $message.success('作废成功！')
                    $queryEngine.request.read()
                  }
                })
              }
            },

            // 代理报价
            proxyQuote: {
              type: 'void',
              title: i18nExpression('bid_mod.proxyQuoteHandle'),
              // [未报价 撤回] && 接收报价中 && 允许代理报价
              'x-visible': generateCharExpressionByFunction(({ $table, $self, $values }) => {
                return ['DRAFT', 'WITHDRAW'].includes($table.getRowByIndex($self.index).orderStatus) && $values.header?.extProjectStatus === 'ACCEPT_ORDER' && $values.header?.allowProxyOrder === 'Y'
              }),
              'x-component-props': {
                type: 'text',
                '@click': generateCharFunctionExpression(({ $form, $projectId }, { row }) => {
                  const takeObj: any = $form.query('proxyQuoteDialog').take()
                  takeObj.setComponentProps({ visible: true })
                  takeObj.setData({
                    editRow: {
                      projectId: $projectId,
                      souNo: $form.values.header.souNo,
                      vendorId: row.vendorId
                    }
                  })
                })
              }
            }
          }
        }
      })
    },

    // 设定目标价/目标价查看 弹窗
    ...TargetPriceDetailDialogSegment,

    // 报价详情 弹窗
    ...QuoteDetailDialogSegment,

    // 代理报价 弹窗
    ...ProxyQuoteDialogSegment(scope)
  }
}

export default QuoteTrackingSegment
