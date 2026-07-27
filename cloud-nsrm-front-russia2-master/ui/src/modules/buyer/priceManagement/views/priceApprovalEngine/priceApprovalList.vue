<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { onActivated } from 'vue-demi'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import PriceApprovalDetail from './priceApprovalDetail'
import CreateContractDialog from './priceApprovalList/createContractDialog'

const { emitTabAdd, t: $t, app } = usePageHelper()

onActivated(() => {
  const { from, funName, formId, formNo } = app.$route.params
  if (from === 'fromFun' && funName === 'priceApproval') {
    if (formId) {
      const approvalId = Number(formId)
      const row = {
        ...app.$route.params,
        approvalId,
        approvalNo: formNo
      }
      $editTab('edit', row)
    }
  }
})

const $addOne = () => {
  $editTab('add', {})
}

const $editTab = (flag:string, row:Object) => {
  const commonTab = {
    component: PriceApprovalDetail,
    params: {
      flag,
      row,
      tabName: `priceApprovalDetail${row.approvalNo}`
    },
    title: row.approvalNo,
    name: `priceApprovalDetail${row.approvalNo}`
  }

  const map = new Map([
    [
      'add',
      {
        component: PriceApprovalDetail,
        params: {
          flag,
          row,
          tabName: 'priceApprovalDetail'
        },
        title: $t('bidMod.inquiryapproval'),
        name: 'priceApprovalDetail'
      }
    ],
    [
      'edit',
      commonTab
    ],
    [
      'approval',
      commonTab
    ],
    [
      'readonly',
      commonTab
    ]
  ])

  const tab = map.get(flag)
  emitTabAdd(tab)
}

// 创建合同弹窗
const $openCreateContractDialog = (row, $form, $queryEngine, $message) => {
  const xData = $form.query('PriceApprovalForBuyer').get('data')
  xData.visible = true
  xData.row = row
}

// 废弃
const $abandonedApprovalFlow = (row, $form, $queryEngine, $message) => {

}

// 删除
const $deleteApprovalFlow = (row, $form, $queryEngine, $message) => {
  const { approvalId } = row
  $queryEngine.request.baseRequest({
    type: 'PriceApprovalForBuyer',
    action: 'removePriceApproval',
    payload: [{
      approvalId
    }],
    query: {
      '*': {}
    },
    loading: true
  }).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $addOne,
  $editTab,
  $openCreateContractDialog,
  $deleteApprovalFlow
}

const components = {
  CreateContractDialog
}

const schema = defineSchemas({
  PriceApprovalForBuyer: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      row: {},
      visible: false
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      actions: {
        pagePriceApprovals: {
          immediate: true,
          method: 'paginationQuery'
          // transformRequest: expression(`({ data, headers }) => {
          //   return data
          // }`)
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'PriceApprovalForBuyer',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          // 价格审批单号
          approvalNo: {
            type: 'string',
            title: i18nExpression('bidMod.approvalNo'),
            'x-query-engine-query-operator': 'contains'
          },
          // 标题
          approvalName: {
            type: 'string',
            title: i18nExpression('bidMod.title'),
            'x-query-engine-query-operator': 'contains'
          },
          // 审核状态
          approvalStatus: {
            type: 'string',
            title: i18nExpression('bidMod.auditStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_PRICE_APPROVAL_STATUS'
            }
          },
          // 寻源单号
          sourceFromNo: {
            type: 'string',
            title: i18nExpression('bidMod.businessNo'),
            'x-query-engine-query-operator': 'contains'
          },
          // 创建时间
          creationDate: {
            type: 'date',
            title: i18nExpression('bidMod.creationDate'),
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'daterange',
              'format': 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd'
            },
            'x-query-engine-query-operator': 'between'
          },
          // 创建人
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.creator'),
            'x-query-engine-query-operator': 'contains'
          },
          // 物料编码
          itemCode: {
            type: 'string',
            title: i18nExpression('bidMod.itemCode'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialCode',
              name: 'scc_base_material_item'
            },
            'x-query-engine-relation': 'itemList',
            'x-query-engine-relation-strict': true
          },
          // 供应商名称
          vendorId: {
            type: 'string',
            title: i18nExpression('bidMod.vendorName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'vendorId',
              name: 'scc_sup_company_info_display_buyer'
            },
            'x-query-engine-relation': 'itemList',
            'x-query-engine-relation-strict': true
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          // 新增
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => $addOne()`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          // 单据ID - 主键
          approvalId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          isSyncToPriceLibrary: {
            type: 'string',
            'x-hidden': true
          },
          // t 价格审批单号
          approvalNo: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.approvalNo'),
              minWidth: 150,
              customRender: true
            },
            'x-query-engine-sort': 'desc',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('readonly',row)
              }`)
            }
          },
          // t 标题
          approvalName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.title'),
              minWidth: 150
            }
          },
          // t 寻源方式
          sourceFromType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.sourceType'),
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PRICE_APPROVAL_FROM_TYPE'
            }
          },
          // t 寻源单号
          sourceFromNo: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.businessNo'),
              minWidth: 150
            }
          },
          // t 审核状态
          approvalStatus: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.auditStatus'),
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_PRICE_APPROVAL_STATUS'
            }
          },
          // t 创建人
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.bidingCreatedBy'),
              minWidth: 100
            }
          },
          // t 创建时间
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                    parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.creationDate'),
              minWidth: 150
            }
          },
          // 操作
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 130,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              // 审批
              approval: {
                type: 'void',
                title: i18nExpression('bidMod.doApproval'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approvalStatus'],
                  `$deps[0] === 'SUBMITTED'` // 审批中
                ),
                'x-component-props': {
                  '@click': expression(`({row}) => $editTab('approval',row)`)
                }
              },
              // 创建合同
              createContract: {
                type: 'void',
                title: i18nExpression('contractMod.createContract'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approvalStatus'],
                  `$deps[0] === 'APPROVED' && $table.getRowByIndex($self.index).isSyncToPriceLibrary === 'N'` // 已审批 && 不更新到价格库
                ),
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    console.log('row',row)
                    $openCreateContractDialog(row,$form,$queryEngine,$message)
                  }`)
                }
              },
              // b 编辑
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approvalStatus'],
                  `$deps[0] === 'DRAFT'` // 拟定
                ),
                'x-component-props': {
                  '@click': expression(`({row}) => $editTab('edit',row)`)
                }
              },
              // b 废弃
              // abandon: {
              //   type: 'void',
              //   title: i18nExpression('common.abandon'),
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.approvalStatus'],
              //     `['WITHDRAW','RESULT_REJECTED'].includes($deps[0])`
              //   ),
              //   'x-component-props': {
              //     '@click': expression(`({row}) => $abandonedApprovalFlow(row, $form, $queryEngine, $message)`)
              //   }
              // },
              // b 删除
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approvalStatus'],
                  `['DRAFT', 'WITHDRAW', 'ABANDONED'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                  // 删除该行
                    title: i18nExpression('common.delRow')
                  },
                  '@click': expression(`({row}) => $deleteApprovalFlow(row, $form, $queryEngine, $message)`)
                }
              }
            }
          }
        })
      }
    }
  },
  CreateContractDialog: {
    type: 'void',
    'x-component': CreateContractDialog,
    'x-component-props': {
      'visible': expression(`$form.query('PriceApprovalForBuyer').get('data').visible`),
      'editRow': expression(`$form.query('PriceApprovalForBuyer').get('data').row`),
      '@close': expression(`() => {
        $form.query('PriceApprovalForBuyer').get('data').visible = false
      }`)
    }
  }
})
</script>
<template>
  <RenderEngine :schema="schema" :scope="scope" :components="components" schemaKey="priceApprovalList" />
</template>
