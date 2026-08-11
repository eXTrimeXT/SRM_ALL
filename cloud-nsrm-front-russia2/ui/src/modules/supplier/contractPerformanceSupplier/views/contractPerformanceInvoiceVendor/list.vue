<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import ContractPerformanceInvoiceDetail from './edit'
import performInvoice from '@/service/modules/cmPerform/vendor/inv'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
const { emitTabAdd, t: $t, vendor, app } = usePageHelper()

const schema = defineSchemas({
  ListPerInvoice: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ListPerInvoice',
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
          invoiceNo: {
            type: 'string',
            title: i18nExpression('contract_mod.processNum2'), // 合同履约开票单号
            'x-query-engine-query-operator': 'contains'
          },
          buId: {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'select-type': 'input'
            }
          },
          status: {
            type: 'string',
            title: i18nExpression('vendorMod.relegation.documentStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_INVOICE_STATUS'
            }
          },
          createdFullName: {
            type: 'string',
            title: '{{$t(\'common.creator\')}}', // 创建人
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: i18nExpression('common.creationDate'), // 创建日期
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          vendorName: {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              propKey: 'companyId',
              showKey: 'companyName',
              name: 'scc_sup_company_info'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          add: {
            type: 'void',
            title: '{{$t("common.add")}}',
            'x-component': 'RButton',
            'x-hidden': '{{!$vendor()}}',
            'x-component-props': {
              type: 'primary',
              '@click': '{{() => $editTab("add", {})}}'
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
          perInvoiceId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          invoiceNo: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({row}) => $editTab("view", row)')
            },
            'x-render-table-column': {
              title: i18nExpression('contract_mod.processNum2'), // 合同履约开票单号
              minWidth: 130,
              customRender: true
            }
          },
          advanceApplyNumber: {
            type: 'string',
            title: i18nExpression('purSettlementMod.advancePaymentNum'), // 预付款申请单号
            'x-render-table-column': {
              minWidth: 150
            }
          },
          paymentApplyNumber: {
            type: 'string',
            title: i18nExpression('contractMod.paymentApplyNumber'), // 付款申请单号
            'x-render-table-column': {
              width: 110
            }
          },
          outstanding: {
            type: 'string',
            title: i18nExpression('contractMod.unpaidApplicationBalance'), // 未付款申请余额
            'x-query-engine-skip': true,
            'x-component': 'el-progress',
            'x-component-props': {
              style: 'float:right',
              'show-text': false,
              'percentage': '{{$self.value == \'--\' ? 0 : $self.value}}',
              color: `{{() => {
                if ($self.value){
                  return '#1997f2'
                }
                if ($self.value < 100) {
                    return '#1997f2'
                } else if ($self.value == 100) {
                    return '#67c23a'
                }
              }}}`
            },
            'x-reactions': expression(`(field) => {
              const row = $table.getRowByIndex(field.index)
              if (row) {
                field.setValue(
                  row && row.paymentAmount && row.stagePaymentAmount
                    ? $percentage(row.paymentAmount, row.stagePaymentAmount)
                    : '--'
                )
              }
            }`),
            'x-render-table-column': {
              width: 130
            }
          },
          contractName: {
            type: 'string',
            title: '{{$t(\'vendorMod.contractName\')}}',
            'x-render-table-column': {
              width: 100
            }
          },
          milestoneType: {
            type: 'string',
            title: '{{$t(\'contract_mod.processNodeName\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          paymentStage: {
            type: 'string',
            title: '{{$t(\'bidMod.payStage\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_STAGE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          payExplain: {
            type: 'string',
            title: '{{$t(\'route.contractPaymentType\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'payExplain',
              'custom-select-type': 'payExplain'
            },
            'x-render-table-column': {
              width: 130
            }
          },
          payMethod: {
            type: 'string',
            title: '{{$t(\'bidMod.category_paymentMethod\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_MODE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          paymentRatio: {
            type: 'string',
            title: '{{$t(\'contractMod.payRatio\')}}',
            'x-render-table-column': {
              width: 150
            }
          },
          stagePaymentAmount: {
            type: 'string',
            title: '{{$t(\'contractMod.stagePaymentAmount\')}}',
            'x-component-props': {
              style: 'float:right'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          status: {
            type: 'string',
            title: i18nExpression('vendorMod.relegation.documentStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_INVOICE_STATUS'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          buName: {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-render-table-column': {
              width: 150
            }
          },
          vendorName: {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-render-table-column': {
              width: 150
            }
          },
          vendorCode: {
            type: 'string',
            title: '{{$t(\'common.vendorCode\')}}',
            'x-render-table-column': {
              width: 150
            }
          },
          taxAmount: {
            type: 'string',
            title: i18nExpression('purSettlementMod.taxTotalAmount'), // 开票含税金额
            'x-component-props': {
              style: 'float:right'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.creator'), // 创建人
            'x-render-table-column': {
              width: 150
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('common.creationTime'), // 创建时间
            'x-render-table-column': {
              width: 150
            }
          },
          operation: {
            type: 'void',
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              width: 120,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'DRAFT\'].includes($deps[0]) || ([\'REJECTED\'].includes($deps[0]) && !$buyer())'
                ),
                'x-component-props': {
                  '@click': expression('({row}) => $editTab("edit", row)')
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('components.common.delete'), // 删除
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'DRAFT\',\'\',null,undefined].includes($deps[0])'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`
                      ({ row }) => $queryEngine.request.delete(row.perInvoiceId, { type: 'PerInvoice', query: { '*': {} } })
                        .then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })

                  `)
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('components.approvalHead.headers.abandon'), // 废弃
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'REJECTED\'].includes($deps[0]) && $vendor()'
                ),
                'x-component-props': {
                  '@click': expression(`
                  ({ row }) => $confirmDeleteMessage().then(() => {
                      performInvoice.performInvoice.abandon(row.contractHeadId)
                        .then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })
                    })`)
                }
              },
              manage: {
                type: 'void',
                title: i18nExpression('bidMod.management'), // 管理
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'SUPPLIER_SUBMITTED\'].includes($deps[0]) && !$vendor()'
                ),
                'x-component-props': {
                  '@click': expression('({row}) => $editTab("manage", row)')
                }
              }
              // payment: {
              //   type: 'void',
              //   title: i18nExpression('bidMod.management'), // 管理
              //   'x-component': 'TableButton',
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status'],
              //     `['SUPPLIER_SUBMITTED'].includes($deps[0]) && !$vendor()`
              //   ),
              //   'x-component-props': {
              //     type: 'text',
              //     '@click': expression('(row) => $editTab("manage", row)')
              //   }
              // }
            }
          }
        })
      }
    }
  }
})

const $percentage = (paymentAmount, stagePaymentAmount) => {
  const payment = parseFloat(paymentAmount || 0)
  const stagePayment = parseFloat(stagePaymentAmount || 0)
  let aws = ((payment / stagePayment).toFixed(2)) * 100
  aws = aws < 100 ? aws : 100
  if (payment === 0) {
    aws = 0
  }
  return aws
}

const $abandonHandle = (row) => {

}

const $editTab = (type, row) => {
  let name = row.invoiceNo || ''
  let tab = {
    component: ContractPerformanceInvoiceDetail,
    params: {
      flag: type,
      row,
      tabName: $t('cusEntry.supplement20250211.performContractBilling') + name // 履约开票
    },
    title: name ? $t('cusEntry.supplement20250211.performContractBilling') + name : $t('route.newContractPerformanceInvoice'),
    name: $t('cusEntry.supplement20250211.performContractBilling') + name // 履约开票
  }
  emitTabAdd(tab)
}

const scope = {
  $percentage,
  $editTab,
  $vendor: vendor,
  $abandonHandle,
  performInvoice
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformanceInvoiceVendorList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
