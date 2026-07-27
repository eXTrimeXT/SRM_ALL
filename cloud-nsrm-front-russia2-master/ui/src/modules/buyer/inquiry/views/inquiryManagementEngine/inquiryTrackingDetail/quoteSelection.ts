/**
 * @description 报价评选
 */
import {
  generateXindexInOrder,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { maxNumberOption } from 'lib@/compositionEngine/sourcing/unit'
import PaymentTypeDialogSegment from 'lib@/compositionEngine/sourcing/paymentType'
import LadderPriceDialogSegment from 'lib@/compositionEngine/inquiry/ladderPrice'
import QuoteRecordChartSegment from 'lib@/compositionEngine/sourcing/quoteRecordChart'
import StartNewRoundSegment from './quoteSelection/startNewRound'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import priceComparison from 'lib@/composition/origin/priceComparison/index'

/**
 * 获取并写入评选的筛选条件
 * @param $form
 * @param $queryEngine
 * @param $projectId
 */
const getSearchInfo = async ($form: any, $queryEngine: any, $projectId: any) => {
  const response = await $queryEngine.request.baseRequest({
    action: 'queryInqSelectingSearchInfo',
    query: { '*': {} },
    tree: true,
    payload: [$projectId]
  })

  if (response && Array.isArray(response.data) && response.data.length === 1) {
    // 赋值给表单查询条件 quoteSelectionQuery
    const { items, vendors, currentRound } = response.data[0]

    // 物料编码
    if (items && Object.keys(items).length > 0) {
      $form.query('quoteSelectionQuery.itemCode').take().setDataSource(Object.keys(items).map((key) => {
        return {
          label: items[key],
          value: key
        }
      }))
    }

    // 供应商列表
    if (vendors && Object.keys(vendors).length > 0) {
      $form.query('quoteSelectionQuery.vendorId').take().setDataSource(Object.keys(vendors).map((key) => {
        return {
          label: vendors[key],
          value: key
        }
      }))
    }

    // 轮次
    if (currentRound && !Number.isNaN(Number(currentRound))) {
      $form.query('quoteSelectionQuery.round').take().setDataSource(maxNumberOption(currentRound))
    }
  }
}

/* 表格行点击处理 */
const quoteSelectionTableRowClick = async ($form: any, $queryEngine: any, $arrangeOnSetChartData: any, row: any) => {
  const stateTake = $form.query('quoteSelectionTableState').take()
  console.log(stateTake, 'stateTake')
  if (stateTake.data.currentSouItemId && stateTake.data.currentSouItemId === row.souItemId) {
    return
  }

  // TODO 折线图loading
  // 查询当前物料的历史价格 再设置当前的比价折线图数据
  const response = await $queryEngine.request.baseRequest({
    action: 'queryInqPriceCompareInfos',
    payload: [
      {
        souItemId: row.souItemId,
        round: row.round
      }
    ]
  }).catch(() => {})

  if (response) {
    stateTake.data.currentSouItemId = row.souItemId
    console.log(response, 'quoteSelectionTableRowClick response')
    $arrangeOnSetChartData($form, response.data[0].priceNodes, row.itemDesc)
  }
}

const QuoteSelectionSegment = ({ scope, components }: any) => {
  console.log(scope, components, 'scope')
  Object.assign(scope, {
    BUSINESS_TYPE_ENUM,
    priceComparison
  })
  Object.assign(components, {
    priceComparison
  })
  return {
    InqSouProjectForBuyer: {
      type: 'void',
      'x-query-engine': {
        service: 'sou',
        actions: {
          paginationQuery: {
            immediate: true,
            action: 'listEvaluations',
            // 添加额外查询字段
            transformRequest: generateCharFunctionExpression(({ $form }, data) => {
              data.payload = {
                ...(data.payload || {}),
                filter: {
                  ...(data.payload.filter || {}),
                  projectId: {
                    eq: $form.values.projectId
                  }
                }
              }
              return data
            })
          }
        }
      },
      'x-decorator': 'el-container',
      'x-component': 'QueryEngine',
      'x-decorator-props': {
        class: 'flex-container',
        direction: 'vertical'
      },

      properties: {
        bus: {
          type: 'void',
          'x-component': 'BusEvent',
          'x-component-props': {
            eventName: 'quoteSelectionQuery',
            '@listener': generateCharFunctionExpression(({ $queryEngine }) => {
              $queryEngine.state.paginationManagement.refresh()
            })
          }
        },
        paymentBeforeClose: {
          type: 'void',
          'x-component': 'BusEvent',
          'x-component-props': {
            eventName: 'paymentBeforeClose',
            '@listener': generateCharFunctionExpression(async ({ $queryEngine, $form, $t, $message, $projectId }, { type, paymentList }) => {
              console.log(type, paymentList, 's----')

              const selection = $form.query('quoteSelectionTable')?.take()?.componentProps?.componentInstance?.getCheckboxRecords()
              const orderItemIds = selection.map((item: any) => item.orderItemId)

              const response = await $queryEngine.request.baseRequest({
                type: 'InqSouProjectForBuyer',
                action: 'changePaymentInSelect',
                payload: [{
                  projectId: $projectId,
                  orderItemIds,
                  paymentList
                }],
                query: {}
              }).catch(() => {})
              console.log(response, 'res')
              if (response) {
                $message.success($t('common.success'))
                $queryEngine.state.paginationManagement.refresh()
              }
            })
          }
        },

        // 查询表单
        quoteSelectionQuery: {
          type: 'object',
          'x-query-engine-skip': true,
          'x-component': 'QueryFormByQueryEngine',
          'x-reactions': generateCharReactionExpression(({ $form, $queryEngine, $projectId, $getSearchInfo }) => {
            if (['ORDER_END', 'EVALUATING', 'PRICING', 'PRICE_END', 'PRICE_REJECT'].includes($form.values.header?.extProjectStatus)) {
              // 获取查询条件表单，并编排查询组件数据
              $getSearchInfo($form, $queryEngine, $projectId)
            }
          }),
          properties: generateXindexInOrder({
            // 物料编码
            itemCode: {
              type: 'string',
              title: i18nExpression('bidMod.itemCode'),
              'x-component': 'Select'
            },
            // 轮次
            vendorId: {
              type: 'string',
              title: i18nExpression('bidMod.supplier_price'),
              'x-component': 'Select'
            },
            // 轮次
            round: {
              type: 'string',
              title: i18nExpression('bidMod.bidingRound'),
              'x-component': 'Select'
            }
          })
        },

        // 操作按钮区域
        toolbar: {
          type: 'void',
          'x-component': 'Space',
          'x-component-props': {
            style: 'margin-bottom: 16px'
          },
          properties: {
            // 智能评选
            evaluate: {
              type: 'void',
              title: i18nExpression('bidMod.intelligentEvaluation'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(async ({ $queryEngine, $projectId, $message, $bus }) => {
                  const response = await $queryEngine.request.baseRequest({
                    type: 'InqSouProjectForBuyer',
                    action: 'intelligentSelect',
                    payload: [{ projectId: $projectId }],
                    query: {}
                  }).catch(() => {})

                  if (response) {
                    $message.success(i18nExpression('bidMod.quoteEvaCalc'))  // '报价评选排名计算已完成'
                    $bus.$emit('getTrackingDetail')
                    $queryEngine.state.paginationManagement.refresh()
                  }
                })
              }
            },

            // 入围或淘汰
            changeWinStatus: {
              type: 'void',
              'x-component': 'el-dropdown',
              'x-component-props': {
                '@command': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath, $message, $queryEngine, $t }, type) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('quoteSelectionTable')).take(async field => {
                    const selection = field.componentProps.componentInstance.getCheckboxRecords()
                    if (selection.length === 0) {
                      $message.warning($t('bidMod.msgSelData'))
                      return
                    }

                    const toWin = type === 'win'
                    const orderItemIds = selection.map((item: any) => item.orderItemId)
                    const response = await $queryEngine.request.baseRequest({
                      action: 'changeWinStatus',
                      payload: [
                        {
                          orderItemIds,
                          toWin
                        }
                      ],
                      query: {}
                    }).catch(() => {})

                    if (response) {
                      $message.success(toWin ? $t('bidMod.successShortlist') : $t('bidMod.successEliminate'))
                      $queryEngine.state.paginationManagement.refresh()
                    }
                  })
                })
              },
              'x-content': {
                default: {
                  functional: true,
                  render: generateCharFunctionExpression((h: any) => {
                    return h('el-button',
                      { attrs: { type: 'primary' } },
                      [
                        i18nExpression('bidMod.nextOrEliminate'),  // '入围或淘汰'
                        h('em', { attrs: { class: 'el-icon-arrow-down el-icon--right' } })
                      ]
                    )
                  }, true)
                }
              },
              properties: {
                dropdownMenu: {
                  type: 'void',
                  'x-component': 'el-dropdown-menu',
                  'x-slot': 'dropdown',
                  'x-content': {
                    default: {
                      functional: true,
                      render: generateCharFunctionExpression((h: any) => {
                        return [
                          h('el-dropdown-item', { attrs: { command: 'win' } }, i18nExpression('bidMod.toNextRoundConfirm')),  // '入围下一轮'
                          h('el-dropdown-item', { attrs: { command: 'loss' } }, i18nExpression('bidMod.toEliminateConfirm'))  // '淘汰'
                        ]
                      }, true)
                    }
                  }
                }
              }
            },

            // 决标操作
            changeSelectResult: {
              type: 'void',
              'x-component': 'el-dropdown',
              'x-component-props': {
                '@command': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath, $message, $queryEngine, $t }, type) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('quoteSelectionTable')).take(async field => {
                    const selection = field.componentProps.componentInstance.getCheckboxRecords()
                    if (selection.length === 0) {
                      $message.warning($t('bidMod.msgSelData'))
                      return
                    }

                    const toWin = type === 'win'

                    if (toWin) {
                      // 中标，需要提交中标数量
                      for (const item of selection) {
                        if (!item.winAmount && item.winAmount !== 0) {
                          // 请先填写中标数量
                          $message.warning(`提交行${$t('vendorMod.msgSelBidder')}`)
                          return
                        }
                      }
                    }

                    const selects = selection.map((item: any) => {
                      return {
                        orderItemId: item.orderItemId,
                        winAmount: item.winAmount
                      }
                    })
                    const response = await $queryEngine.request.baseRequest({
                      action: 'changeSelectStatus',
                      payload: [
                        {
                          selects,
                          toWin
                        }
                      ],
                      query: {}
                    }).catch(() => {})

                    if (response) {
                      $message.success($t('common.successSubmit'))
                      $queryEngine.state.paginationManagement.refresh()
                    }
                  })
                })
              },
              'x-content': {
                default: {
                  functional: true,
                  render: generateCharFunctionExpression((h: any) => {
                    return h('el-button',
                      { attrs: { type: 'primary' } },
                      [
                        i18nExpression('bidMod.bidAwardOperation'),  // '决标操作'
                        h('em', { attrs: { class: 'el-icon-arrow-down el-icon--right' } })
                      ]
                    )
                  }, true)
                }
              },
              properties: {
                dropdownMenu: {
                  type: 'void',
                  'x-component': 'el-dropdown-menu',
                  'x-slot': 'dropdown',
                  'x-content': {
                    default: {
                      functional: true,
                      render: generateCharFunctionExpression((h: any) => {
                        return [
                          h('el-dropdown-item', { attrs: { command: 'win' } }, i18nExpression('bid_mod.winTheBidding')),  // '中标'
                          h('el-dropdown-item', { attrs: { command: 'loss' } }, i18nExpression('bid_mod.lossTheBidding'))  // '落标'
                        ]
                      }, true)
                    }
                  }
                }
              }
            },

            // 比价表打印
            priceComparisonExport: {
              type: 'void',
              title: i18nExpression('bidMod.printPrice'),  // '比价表打印'
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                loading: false,
                '@click': generateCharFunctionExpression(async ({ $self, $downloadFileLink, $inqBuyerHttp, $values, $projectId, $message, $t }) => {
                  // mql不支持文件流下载，用回旧接口先
                  $self.setComponentProps({ loading: true })
                  $downloadFileLink(
                    $inqBuyerHttp.select.getExportPriceCompareInfoUrl($projectId),
                    `${$values.header.souNo}_${i18nExpression('bidMod.priceCompareList')}.pdf`  // 比价表
                  ).then(() => {
                    $self.setComponentProps({ loading: false })
                  }).catch(() => {
                    $self.setComponentProps({ loading: false })
                    $message.error($t('components.eio.downloadFail'))
                  })
                })
              }
            },

            // 公式本轮结果
            openResult: {
              type: 'void',
              title: i18nExpression('bidMod.toPublishDialog'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(async ({ $queryEngine, $projectId, $message, $t }) => {
                  const response = await $queryEngine.request.baseRequest({
                    action: 'openResult',
                    payload: [{ projectId: $projectId, souType: 'inq' }],
                    query: {}
                  }).catch(() => {})

                  if (response) {
                    $message.success($t('common.successSubmit'))
                    $queryEngine.state.paginationManagement.refresh()
                  }
                })
              }
            },

            // 发起新一轮
            continueNextRound: {
              type: 'void',
              title: i18nExpression('bidMod.biddingControl.startNewRound'),  // '发起新一轮'
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(({ $form }) => {
                  $form.query('startNewRoundDialog').take(field => {
                    field.setComponentProps({ visible: true })
                  })
                })
              }
            },

            // 生成价格审批单
            toApprovalBill: {
              type: 'void',
              title: i18nExpression('bidMod.toPriceApproval'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(async ({ $queryEngine, $message, $confirm, $t, $router, $projectId }) => {
                  const businessType = BUSINESS_TYPE_ENUM.INQUIRY_LTS
                  if (!businessType) return
                  // '确定生成寻源结果审批单吗？'
                  const confirmSubmitResult = await $confirm(i18nExpression('competition.createPricingApproval'), {
                    confirmButtonText: $t('common.confirm'),
                    cancelButtonText: $t('common.cancel'),
                    type: 'warning'
                  }).catch(() => { /* nothing */ })

                  if (confirmSubmitResult !== 'confirm') return

                  const response = await $queryEngine.request.baseRequest({
                    action: 'createPricingApproval',
                    payload: [{ projectId: $projectId }],
                    query: {}
                  }).catch(() => {})

                  if (response) {
                    $message.success($t('common.successSubmit'))
                    $queryEngine.state.paginationManagement.refresh()
                  }
                  if (!response || !response.data) {
                    return
                  }
                  // '价格审批单创建成功，需要跳转到价格审批单页面吗？'
                  const confirmResult = await $confirm(i18nExpression('competition.createPricingApprovalAndJump'), {
                    // '跳转到价格审批单页面'
                    confirmButtonText: i18nExpression('competition.jumpPricingApproval'),
                    cancelButtonText: $t('common.cancel'),
                    type: 'warning'
                  })

                  if (confirmResult === 'confirm') {
                    // 确认跳转
                    $router.push({
                      name: 'priceApproval',
                      params: {
                        from: 'fromFun',
                        formId: response.data.approvalHeaderId,
                        formNo: response.data.approvalNo,
                        funName: 'priceApproval',
                        sourceType: businessType
                      }
                    })
                  }
                })
              }
            },

            // 批量修改账期
            openBatchPaymentTypeDialog: {
              type: 'void',
              title: i18nExpression('bidMod.BatchModify'),  // '批量修改账期'
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(async ({ $t, $message, $getFieldParentFieldFormPath, $self, $form }) => {
                  let selection: any = []
                  $self.query($getFieldParentFieldFormPath($self, 8).concat('quoteSelectionTable')).take(async field => {
                    selection = field.componentProps.componentInstance.getCheckboxRecords()
                  })

                  if (selection.length === 0) {
                    return $message.warning($t('bidMod.msgSelData'))
                  }

                  // 只触发当前的
                  const state = $self.query($getFieldParentFieldFormPath($self, 8).concat('paymentTypeDialog')).get('data')
                  state.editRow = {
                    paymentList: []
                  }
                  state.selections = selection
                  state.readOnly = false

                  $self.query($getFieldParentFieldFormPath($self, 8).concat('paymentTypeDialog')).take(field => {
                    field.setComponentProps({ visible: true, readOnly: true })
                  })
                })
              }
            },

            // 提交中标数量修改
            changeQuoteQuantity: {
              type: 'void',
              title: i18nExpression('bidMod.submitBidModify'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(({ $queryEngine, $self, $getFieldParentFieldFormPath, $message, $t }) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('quoteSelectionTable')).take(async field => {
                    const payload = []
                    const selection = field.componentProps.componentInstance.getCheckboxRecords()
                    if (selection.length === 0) {
                      $message.warning($t('bidMod.msgSelData'))
                      return
                    }

                    // 中标，需要提交中标数量
                    for (const item of selection) {
                      if (!item.winAmount && item.winAmount !== 0) {
                        // 请先填写中标数量
                        $message.warning(`${i18nExpression('cusEntry.supplement20250121.submissionBank')}${$t('vendorMod.msgSelBidder')}`)  
                        return
                      } else {
                        payload[0] = {
                          orderItemId: item.orderItemId,
                          winAmount: item.winAmount
                        }
                      }
                    }

                    const response = await $queryEngine.request.baseRequest({
                      action: 'changeWinAmount',
                      payload: payload,
                      query: {}
                    }).catch(() => {})

                    if (response) {
                      $message.success($t('common.successSubmit'))
                      $queryEngine.state.paginationManagement.refresh()
                    }
                  })
                })
              }
            }
          }
        },

        quoteSelectionTableState: {
          type: 'void',
          'x-hidden': true,
          'x-data': {
            currentSouItemId: null
          }
        },

        // 评选列表 分页
        quoteSelectionTable: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            preColumns: 'checkbox, seq',
            class: 'table-view-vxe-table',
            openCustomTable: false,
            sortable: false,
            editMode: 'multi-row',
            pagination: true,
            '@cell-click': generateCharFunctionExpression(({ $form, $queryEngine, $arrangeOnSetChartData, $quoteSelectionTableRowClick }, { row }) => {
              console.log('quoteSelectionTable cell-click')
              $quoteSelectionTableRowClick($form, $queryEngine, $arrangeOnSetChartData, row)
            })
          },
          'x-read-pretty': true,
          'x-reactions': generateCharReactionExpression(({ $form, $self, $queryEngine, $quoteSelectionTableRowClick, $arrangeOnSetChartData }) => {
            // 每次数据变动都会触发
            if ($self.value.length > 0) {
              // 设置选中第一行
              $form.query('quoteSelectionTable').take((filed: any) => {
                const vxeTable = filed.componentProps.componentInstance.$refs.vxeTable
                if (!vxeTable.getCurrentRecord()) {
                  // 避免多次触发
                  console.log('quoteSelectionTable x-reactions')
                  $quoteSelectionTableRowClick($form, $queryEngine, $arrangeOnSetChartData, $self.value[0])
                }
                vxeTable.setCurrentRow($self.value[0])
              })
            }
          }),

          properties: generateXindexInOrder({
            // 轮次
            round: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.bidingRound'),
                minWidth: 70
              }
            },
            // 本轮入围情况
            winStatus: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.currentInList'),  // '本轮入围情况'
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'SOU_WIN_STATUS'
              }
            },
            // 评选情况
            selectStatus: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.selectSituation'),
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'SOU_SELECT_STATUS'
              }
            },
            // 业务实体
            orgOuName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.affairsEntity'),
                minWidth: 120
              }
            },
            // 库存组织
            orgInvName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.affairsInventoryOrg'),
                minWidth: 120
              }
            },
            // 供应商编号
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
            // 物料编码
            itemCode: {
              type: 'string',
              // TODO 处理无料号
              'x-render-table-column': {
                title: i18nExpression('bidMod.itemCode'),
                minWidth: 120
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
            // 组合
            itemGroup: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.affairsCombination'),
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
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'unit'
              }
            },
            // 预计数量
            requireQuantity: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.demandQuantity'),
                minWidth: 100
              }
            },
            // 中标数量
            winAmount: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.quotaQuantity'),
                minWidth: 130
              },
              'x-read-pretty': false,
              'x-component': 'Input',
              'x-component-props': {
                '@click.stop': true
              }
            },
            // 目标价(未税)
            notaxTargetPrice: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.notaxTargetPrice'),  // '目标价(未税)'
                minWidth: 100
              }
            },
            // 报价币种
            orderCurrency: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.bidingCurrency2'),
                minWidth: 120
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'currency'
              }
            },
            // 报价税率
            taxKey: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.taxKey'),  // '报价税率'
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'tax'
              }
            },
            // 原币未税单价
            orderNotaxPrice: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.orderNotaxPrice'), // '原币未税单价'
                minWidth: 100
              }
            },
            // 原币含税单价
            orderTaxPrice: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.orderTaxPrice'),  // '原币含税单价'
                minWidth: 100
              }
            },
            // 汇率
            priceTax: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bid_mod.priceTax'),
                minWidth: 100
              }
            },
            // 本币未税价
            standardNotaxPrice: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.standardNotaxPrice'),  // '本币未税价'
                minWidth: 100
              }
            },
            // 本币币种
            standardCurrency: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.appraisLocalCurrency'),
                minWidth: 120
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'currency'
              }
            },
            // 价格得分
            priceScore: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.appraisPricePoints'),
                minWidth: 100
              }
            },
            // 综合得分
            compositeScore: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.synthesisScore'),
                minWidth: 100
              }
            },
            // 排名
            ranking: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.tech_ranking'),
                minWidth: 100
              }
            },

            // 付款条款
            paymentType: {
              type: 'string',
              title: i18nExpression('common.view'),
              'x-render-table-column': {
                title: i18nExpression('paymentType.paymentType'),
                minWidth: 100
              },
              'x-read-pretty': false,
              'x-component': 'RButton',
              'x-component-props': {
                type: 'text',
                '@click': generateCharFunctionExpression(({ $table, $self, $getFieldParentFieldFormPath, $form }) => {
                  // 只触发当前的
                  const row = $table.getRowByIndex($self.index)
                  const state = $self.query($getFieldParentFieldFormPath($self, 3).concat('paymentTypeDialog')).get('data')
                  state.editRow = {
                    paymentList: row.paymentList && Array.isArray(row.paymentList) ? row.paymentList : []
                  }
                  state.readOnly = true
                  $self.query($getFieldParentFieldFormPath($self, 3).concat('paymentTypeDialog')).take(field => {
                    field.setComponentProps({ visible: true })
                  })
                })
              }
            },

            // 是否阶梯价
            isLadder: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.isLadder'),
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'YES_OR_NO'
              }
            },

            // 备注
            remark: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('common.remark'),
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
                // 公式报价
                isFormula: {
                  type: 'void',
                  // '公式报价明细'
                  title: i18nExpression('bidMod.formulaQuoteDetail'),
                  'x-visible': generateCharExpressionByFunction(({ $table, $self }) => {
                    return $table.getRowByIndex($self.index).isFormula === 'Y'
                  }),
                  'x-component-props': {
                    '@click.stop': generateCharFunctionExpression(({ $table, $self }) => {
                      $table.remove($self.index)
                    })
                  }
                },

                // 阶梯价
                isLadder: {
                  type: 'void',
                  // '阶梯价明细'
                  title: i18nExpression('cusEntry.supplement20250211.staircasePriceDetails'),
                  'x-visible': generateCharExpressionByFunction(({ $table, $self }) => {
                    return $table.getRowByIndex($self.index).isLadder === 'Y'
                  }),
                  'x-component-props': {
                    '@click.stop': generateCharFunctionExpression(({ $table, $self }) => {
                      $table.remove($self.index)
                    })
                  }
                },

                // 比价
                priceComparison: {
                  type: 'void',
                  // '比价'
                  title: i18nExpression('bidMod.priceCompare'),
                  'x-component-props': {
                    '@click': generateCharFunctionExpression(({ $values, $emitTabAdd }, { row }) => {
                      // '比价 value'
                      console.log($values, i18nExpression('cusEntry.supplement20250211.compareValue'))
                      $emitTabAdd({
                        component: priceComparison,
                        params: {
                          businessType: BUSINESS_TYPE_ENUM.INQUIRY_LTS,
                          pricingType: $values.header.orderType,
                          row: {
                            id: $values.header.projectId,
                            number: $values.header.souNo,
                            currentRound: row.currentRound
                          }
                        },
                        closable: true,
                        title: `${$values.header.souNo} 比价`,
                        name: `priceComparison-${$values.header.souNo}`
                      })
                    })
                  }
                }
              }
            }
          })
        },

        // 比价折线图
        ...QuoteRecordChartSegment(),

        // 发起新一轮报价 弹窗
        ...StartNewRoundSegment,

        // 查看公式报价 弹窗 TODO 封装

        // 付款条款 弹窗
        ...PaymentTypeDialogSegment({ readonly: false }),

        // 阶梯价 弹窗
        ...LadderPriceDialogSegment()
      }
    }
  }
}

export default QuoteSelectionSegment

export {
  getSearchInfo,
  quoteSelectionTableRowClick
}
