<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  i18nExpression,
  action,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import {
  buttonListItemVisibleByPermission,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

// @ts-ignore
import { contractManagement } from 'modb@/contractManagement/api/index'
// @ts-ignore
import contractInformation from './edit-engine.vue'
import { useDebounceFn } from '@vueuse/core'

const generateAddOneEventExpression = (type: string) => expression(`() => {
  $addOne('${type}', $self.query('ContractHead.table').take().componentProps.componentInstance.getCheckboxRecords())
}`)

const schema = defineSchemas({
  ContractHead: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ContractHead',
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
          contractNo: {
            type: 'string',
            title: "{{$t('contractMod.contractNo')}}",
            'x-query-engine-query-operator': 'contains'
          },
          sourceNumber: {
            type: 'string',
            title: "{{$t('bidMod.approvalNo')}}",
            'x-query-engine-query-operator': 'contains'
          },
          contractType: {
            type: 'string',
            title: "{{$t('contractMod.operationType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_TYPE'
            }
          },
          contractStatus: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.contractStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_STATUS'
            }
          },
          buId: {
            type: 'string',
            title: "{{$t('contractMod.buId')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: false
            }
          },
          contractClass: {
            type: 'string',
            title: "{{$t('contractMod.contractType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            }
          },
          contractName: {
            type: 'string',
            title: "{{$t('contractMod.contractName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          vendorId: {
            type: 'string',
            title: "{{$t('contractMod.vendorName')}}",
            'x-hidden': `{{ $vendor() }}`,
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_display_buyer'
            }
          },
          frameworkAgreementCode: {
            type: 'string',
            title: "{{$t('contractMod.frameworkAgreementCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          frameworkAgreementName: {
            type: 'string',
            title: "{{$t('contractMod.frameworkAgreementName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          createdBy: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 签署方式
          formal: {
            type: 'string',
            title: "{{$t('contractMod.signingMethod')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_FORM2'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('contractMod.addContract')}}",
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('cm:contractManager:add'),
              '@click': expression(`() => $addOne('MIAN_CONTRACT_ADD')`)
            }
          },
          // 合同变更
          alter: {
            type: 'void',
            title: "{{$t('contractMod.contractChange')}}",
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              ...buttonListItemVisibleByPermission('cm:contractManager:alter'),
              '@click': generateAddOneEventExpression('MIAN_CONTRACT_ALTER')
            }
          },
          // 补充协议
          contractChange2: {
            type: 'void',
            title: "{{$t('contractMod.contractChange2')}}",
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              '@click': generateAddOneEventExpression('SUPPLEMENTAL_AGREEMENT')
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              title: i18nExpression('common.excelImport'),
              type: 'default',
              extraData: {
                fileModular: 'cm',
                fileFunction: 'contractMaintainList',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cm/contract/contractHead/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cm/contract/contractHead/importModelDownload',
                fileName: expression(`$t('contractMod.contractManageImp')`)
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ContractHead')
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-cm/api-ql/ContractHead/query", // meiql 接口
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ContractHead.table', 'data.columns'),
              dictCodes: {
                contractStatus: 'CONTRACT_STATUS',
                contractType: 'CONTRACT_TYPE',
                contractClass: 'ELEM_CONTRACT_TYPE',
                formal: 'CONTRACT_FORM2'
              }
            }
          },
          bulkMaintainFwAgreement: {
            type: 'void',
            title: "{{$t('bidMod.bulkMaintainFwAgreement')}}",
            'x-visible': expression('$buyer()'),
            'x-component-props': {
              '@click': expression(`() => {
                const rows = $self.query('ContractHead.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()

                if (!rows.length) {
                  $message.error($t('contractMod.msgSelData'))
                  return
                }

                for (let i = 0; i < rows.length; i += 1) {
                  const item = rows[i]
                  if (item.contractStatus != 'ARCHIVED') {
                    // 请选择已归档的数据
                    $message.error($t('bidMod.selSameVendor2'))
                    return
                  }

                  // 选择的数据必须是同一个供应商
                  if (i > 0 && item.vendorId !== rows[0].vendorId) {
                    $message.error($t('bidMod.selSameVendor'))
                    return
                  }
                }

                $form.query('bulkMaintainFwAgreementDialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $reactiveAction(() => {
                    const queryDataField = $form.query('bulkMaintainFwAgreementDialog.queryData').take()
                    queryDataField.value.vendorName = rows[0].vendorName
                    queryDataField.data.vendorId = rows[0].vendorId
                    queryDataField.data.globalcontractIds = rows.map(item => item.contractHeadId)
                  })
                })
              }`)
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
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          contractHeadId: {
            type: 'string',
            'x-hidden': true
          },
          vendorId: {
            type: 'string',
            'x-hidden': true
          },
          contractNo: {
            type: 'string',
            title: i18nExpression('contractMod.contractNo'),
            'x-render-table-column': {
              minWidth: 200
            }
          },
          contractName: {
            type: 'string',
            'x-component': 'RenderTableLink',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $readOne(row,'view')`)
            },
            'x-render-table-column': {
              title: i18nExpression('contractMod.contractName'),
              minWidth: 160,
              customRender: true
            }
          },
          sourceNumber: {
            type: 'string',
            title: "{{$t('bidMod.approvalNo')}}",
            'x-render-table-column': {
              width: 170
            }
          },
          contractStatus: {
            type: 'string',
            title: "{{$t('common.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          contractType: {
            type: 'string',
            title: "{{$t('contractMod.operationType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          contractClass: {
            type: 'string',
            title: "{{$t('contractMod.contractType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          // 签署方式
          formal: {
            type: 'string',
            title: "{{$t('contractMod.signingMethod')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_FORM2'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          // 业务实体
          buName: {
            type: 'string',
            title: "{{$t('contractMod.buId')}}",
            'x-render-table-column': {
              width: 150
            }
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 130
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          // 框架协议编号
          frameworkAgreementCode: {
            type: 'string',
            title: "{{$t('contractMod.frameworkAgreementCode')}}",
            'x-render-table-column': {
              width: 150
            }
          },
          // 框架协议名称
          frameworkAgreementName: {
            type: 'string',
            title: "{{$t('contractMod.frameworkAgreementName')}}",
            'x-render-table-column': {
              width: 150
            }
          },
          // 原合同号
          contractOldCode: {
            type: 'string',
            'x-component': 'RenderTableLink',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => {
                $queryEngine.request.query({
                  contractNo: {
                    eq: row.contractOldCode
                  }
                }).then(res => {
                  if (res.data[0]) {
                    $readOne(res.data[0])
                  }
                })
              }`)
            },
            'x-render-table-column': {
              title: "{{$t('contractMod.contractOldCode')}}",
              minWidth: 150,
              customRender: true
            }
          },
          contractChangeCode: {
            type: 'string',
            title: "{{$t('contractMod.contractChangeCode')}}",
            'x-render-table-column': {
              width: 150
            }

          },
          contractAgreementCode: {
            type: 'string',
            title: "{{$t('contractMod.annexId')}}",
            'x-render-table-column': {
              width: 150
            }

          },
          modelName: {
            type: 'string',
            title: "{{$t('contractMod.templHeadId')}}",
            'x-render-table-column': {
              width: 150
            }
          },
          effectiveDateFrom: {
            title: "{{$t('contractMod.constartDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.effectiveDateFrom, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130
            }
          },
          contractTerminationCode: {
            type: 'string',
            title: "{{$t('contractMod.terminationId')}}",
            'x-render-table-column': {
              width: 150
            }

          },
          endDate: {
            title: "{{$t('qualitySynergy.endDate2')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          createdUserName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-skip': true,
            'x-render-table-column': {
              width: 100
            }
          },
          creationDate: {
            title: "{{$t('common.creationTime')}}",
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          lastUpdatedBy: {
            type: 'string',
            'x-hidden': true
          },
          lastUpdatedUserName: {
            type: 'string',
            title: "{{$t('contractMod.lastUpdatedBy')}}",
            'x-query-engine-skip': true,
            'x-render-table-column': {
              width: 130
            }
          },
          lastUpdateDate: {
            title: "{{$t('contractMod.lastUpdateDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          vendorConfirmDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.vendorConfirmDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('contractMod.vendorConfirmDate')}}",
            'x-render-table-column': {
              width: 140
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 170,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              management: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    console.log(row.contractType, 'contractType')
                    if (row.contractType == 'TERMINATION') {
                      $goToTermination(row, 2)
                    } else {
                      $editOne(row)
                    }
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$detectContractInformationRightByRow($table.getRowByIndex($self.index))`
                )
              },
              // 合同终止编辑
              stopEdit: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-component-props': {
                  '@click': expression(`({ row }) => $goToTermination(row, 1)`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus', '.contractType'],
                  `(
                      (
                        // 这部分逻辑可以提取出去
                        $buyer() &&
                        (
                          ['DRAFT', 'REJECTED', 'REFUSED', 'WITHDRAW'].includes($deps[0]) &&
                          $deps[1] == 'TERMINATION'
                        ) &&
                        $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))
                      ) ||
                      ($vendor() && $deps[0] === 'SUPPLIER_CONFIRMING')
                    ) &&
                    $deps[1] === 'TERMINATION'
                  `
                )
              },
              publish: {
                type: 'void',
                title: "{{$t('common.publish')}}",
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    $queryEngine.request.save(row.contractHeadId, { customizeAction: 'publish' })
                      .then(() => {
                        $message.success($t('common.successPublish'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && $deps[0] === 'UNPUBLISHED' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))`
                )
              },
              // 终止合同
              terminationContract: {
                type: 'void',
                title: i18nExpression('contractMod.terminationContract'),
                'x-component-props': {
                  '@click': expression(`({ row }) => $goToTermination(row, 2)`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && ['ARCHIVED'].includes($deps[0])`
                )
              },
              // 发布签章
              releaseSignPlatform: {
                type: 'void',
                title: i18nExpression('contractMod.releaseSignPlatform'),
                'x-component-props': {
                  '@click': expression(`({ row }) => $readOne(row)`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && ['APPROVAL'].includes($deps[0]) &&
                      ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes($table.getRowByIndex($self.index).contractType) &&
                      ['ELECTRONIC_CONTRACT'].includes($table.getRowByIndex($self.index).formal)`
                )
              },
              // 审批
              approve: {
                type: 'void',
                title: i18nExpression('common.approve'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => $readOne(row)`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && ['UNDER_REVIEW', 'SUPPLIER_CONFIRMED'].includes($deps[0])`
                )
              },
              // 拒绝
              approvalRefuse: {
                type: 'void',
                title: i18nExpression('bidMod.approvalRefuse'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $prompt($t('contractMod.msgRefuseReason'), $t('oneStopShopping.refusedReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel')
                    }).then(({ value }) => {
                      return $queryEngine.request.save({
                          contractHeadId: row.contractHeadId,
                          approvalAdvice: value,
                          contractStatus: 'REFUSED',
                        }).then(() => {
                          $queryEngine.state.paginationManagement.refresh()
                        })
                    }).then(() => {
                      $message.success($t('common.success'))
                    }).catch((err) => {
                      console.error('approvalRefuse', err)
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && ['UNDER_REVIEW'].includes($deps[0])`
                )
              },
              // [供应商已确认]  归档
              archive: {
                type: 'void',
                title: i18nExpression('contractMod.archive'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $form.query('contractFilingDialog').take().setComponentProps({ visible: true })
                    setTimeout(() => {
                      const field = $form.query('contractFilingDialog.fileInfo').take()

                      // 重置
                      field.data = {}
                      field.data.contractHeadId = row.contractHeadId
                      field.data.fileuploadId = ''
                      field.data.fileSourceName = ''
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `$buyer() && $deps[0] === 'UN_ARCHIVED' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))`
                )
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `['DRAFT', 'ABANDONED'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`
                    ({ row }) => $queryEngine.request.delete(row.contractHeadId)
                        .then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })

                  `)
                }
              },
              // 废弃
              abandon: {
                type: 'void',
                title: i18nExpression('common.abandon'),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    return $queryEngine.request.save({
                      contractHeadId: row.contractHeadId,
                      contractStatus: 'ABANDONED',
                    }).then(() => {
                      $message.success($t('common.successAbandon'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.contractStatus'],
                  `['REJECTED', 'REFUSED'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))`
                )
              }
            }
          }
        })
      }
    }
  },

  // 批量维护框架协议
  bulkMaintainFwAgreementDialog: {
    type: 'void',
    title: i18nExpression('bidMod.bulkMaintainFwAgreement'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: false
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      type: 'ContractHead',
      transformRequest: expression(`(data, headers) => {
        data.query.vendorId = {}
        data.query.ceeaIfVirtual = {}

        return data
      }`)
    },
    properties: {
      queryData: {
        type: 'object',
        default: {},
        'x-query-engine-skip': true,
        // 使用 data 冗余属性而不是直接定义 field 的方式，减少 createField 开销
        'x-data': {
          vendorId: undefined,
          globalcontractIds: []
        },
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'horizontal'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 4,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          vendorId: {
            type: 'string',
            'x-hidden': true
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendor'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true
            }
          },
          isFrameworkAgreement: {
            type: 'string',
            title: i18nExpression('contractMod.status'),
            default: 'Y',
            'x-decorator': 'FormItem',
            'x-component': 'Checkbox',
            'x-component-props': {
              disabled: true,
              trueLabel: "Y",
              falseLabel: "N"
            }
          }
        }
      },
      dialogTable: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          performanceMode: true,
          pagination: false,
          // 后面考虑内置然后通过媒体查询的方式做 size 响应式变化
          maxHeight: '45vh'
        },
        'x-reactions': expression(`(field) => {
          const queryDataField = field.query('bulkMaintainFwAgreementDialog.queryData').take()

          $effect(() => {
            if (queryDataField.value.vendorName) {
              $queryEngine.state.paginationManagement.configState.value.pageSize = 999
              $queryEngine.state.paginationManagement.queryParams.value = {
                vendorId: { eq: queryDataField.data.vendorId },
                isFrameworkAgreement: { eq: queryDataField.value.isFrameworkAgreement },
                vendorName: { eq: queryDataField.value.vendorName },
                contractStatus: { eq: 'ARCHIVED' },
              }

              $queryEngine.state.paginationManagement.refresh()
            }
          }, [queryDataField.data.vendorId])
        }`),
        properties: generateXindexInOrder({
          vendorId: {
            type: 'string',
            'x-hidden': true
          },
          // 合同编码
          contractCode: {
            type: 'string',
            title: i18nExpression('contractMod.contractCode'),
            'x-render-table-column': {
              //
            }
          },
          contractName: {
            type: 'string',
            title: i18nExpression('contractMod.contractName'),
            'x-render-table-column': {
              //
            }
          },
          contractHeadId: {
            type: 'string'
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              performanceMode: false,
              sortable: false,
              width: 60
            },
            properties: {
              save: {
                type: 'void',
                title: i18nExpression('common.save'),
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                  const queryDataField = $form.query('bulkMaintainFwAgreementDialog.queryData').take()
                     $queryEngine.request.save({
                        contractHeadId: row.contractHeadId,
                        contractCode: row.contractCode,
                        contractName: row.contractName,
                        contractIds: $form.query('bulkMaintainFwAgreementDialog.queryData')
                          .take().data.globalcontractIds,
                        vendorId: queryDataField.data.vendorId
                      }, { customizeAction: 'bulkMaintenanceFramework' }).then(() => {
                        $message.success($t('common.success'))
                        $closed()

                        $bus.$emit('ContractHead')
                      })
                    }`)
                }
              }
            }
          }
        })
      }
    }
  },

  // 双方盖章协议附件上传
  contractFilingDialog: {
    type: 'void',
    title: i18nExpression('contractMod.contractFiling'),
    'x-component': 'RDialog',
    'x-component-props': {
      beforeClose: expression(`(done, type, closeLoading) => {
        if (type !== 'ok') {
          done()
          return
        }

        const fieldData = $self.query('contractFilingDialog.fileInfo').get('data')

        if (!fieldData.fileuploadId) {
          $message.error($t('contractMod.msgUploadFile'))
          closeLoading()
          return
        }

        $contractManagement.contract
          .paperArchiveConfirm(fieldData)
          .then((data) => {
            $message.success($t('contractMod.archiveConfirmSuccess'))

            done()

            $bus.$emit('ContractHead')
          })
          .catch((err) => {
            console.log(err)
          })
      }`)
    },
    properties: {
      fileInfo: {
        type: 'object',
        title: i18nExpression('contractMod.bothPartiesUpload'),
        'x-decorator': 'FormItem',
        'x-component': 'SrmCommonFile',
        'x-component-props': {
          extraData: {
            fileModular: 'cm',
            fileFunction: 'contractManager',
            fileType: 'excel/word'
          },
          readonly: false,
          defaultFile: {
            fileId: expression('$self.data && $self.data.fileuploadId'),
            fileName: expression('$self.data && $self.data.fileSourceName')
          },
          '@on-change': expression(`({ file }) => {
            const { fileId = '', fileName = '', fileType = '' } = file || {}
            $self.data.fileuploadId = fileId.toString()
            $self.data.fileSourceName = fileName
            $self.data.fileType = fileType
          }`)
        }
      }
    }
  }
})

const { emitTabAdd, createdUserIsCurrentUserByRow, buyer, vendor, t, app } = usePageHelper()

const $goToTermination = (row: any, num = 1) => {
  let str = num == 1 ? 'edit' : 'termination'

  emitTabAdd({
    component: contractInformation,
    params: {
      termination: true,
      flag: str,
      row: row,
      isReadOnly: false,
      contractType: 'TERMINATION'
    },
    title: row.contractName,
    name: 'termination' + row.contractName
  })
}

const $detectContractInformationRightByRow = (row: any) => {
  return (
    (
      (
        ['DRAFT', 'REJECTED', 'REFUSED', 'WITHDRAW', 'SUPPLIER_REJECTED'].includes(row.contractStatus) &&
        buyer() &&
        createdUserIsCurrentUserByRow(row)
      ) ||
      (vendor() && row.contractStatus === 'SUPPLIER_CONFIRMING')
    ) &&
    row.contractType !== 'TERMINATION'
  )
}

const $readOne = (row: any, types) => {
  let flag = types == 'view' ? 'view' : 'edit'
  if (row.contractType == 'TERMINATION') {
    emitTabAdd({
      component: contractInformation,
      params: {
        termination: true,
        flag: flag,
        row: row,
        isReadOnly: true,
        contractType: 'TERMINATION'
      },
      title: row.contractName,
      name: 'contractInformation' + row.contractName
    })

    return
  }

  emitTabAdd({
    component: contractInformation,
    params: {
      flag: flag,
      row: row,
      isReadOnly: !$detectContractInformationRightByRow(row)
    },
    title: row.contractName,
    name: 'contractInformation' + row.contractName
  })
}

const $editOne = (row: any) => {
  emitTabAdd({
    component: contractInformation,
    params: {
      flag: 'edit',
      row: row,
      isReadOnly: false
    },
    contractType: row.contractType,
    title: row.contractName,
    name: 'contractInformation' + row.contractName
  })
}

const $addOne = useDebounceFn(async (contractType: string, selectedRows = []) => {
  let rowId = null
  let contractOldCode = null
  let mainContractNo = null
  let isReadOnly = false
  // 如果不是创建合同的时候校验
  if (contractType !== 'MIAN_CONTRACT_ADD') {
    const name =
      contractType === 'MIAN_CONTRACT_ALTER'
        ? t('contractMod.changeInContract')
        : t('contractMod.supplementalAgreement')

    if (!selectedRows.length) {
      app.$message.warning(t('contractMod.msgContractManage[0]') + `${name}`) // 请选择要以哪个合同为基础创建
      return
    }

    if (selectedRows.length > 1) {
      app.$message.warning(
        t('contractMod.msgContractManage[1]') +
        `${name}` +
        t('contractMod.msgContractManage[2]')
      ) // 只能选择一个合同作为创建${name}的基础

      return
    }

    if (selectedRows[0].contractStatus !== 'ARCHIVED') {
      app.$message.warning(t('contractMod.msgContractManage[3]') + `${name}`) // 只能已归档的合同才能创建
      return
    }

    rowId = selectedRows[0].contractHeadId
    contractOldCode = selectedRows[0].contractCode
    mainContractNo = selectedRows[0].contractNo
    let dataV = await contractManagement.changePreCheck({ ceeaContractOldId: rowId })
    if (dataV.code !== '0') {
      return false
    }
  }

  emitTabAdd({
    component: contractInformation,
    params: {
      flag: 'add',
      rowId,
      contractType,
      contractOldCode,
      mainContractNo,
      isReadOnly: isReadOnly,
      row: selectedRows
    },
    title: t('contractMod.createContract'), // 创建合同
    name: 'contractInformation'
  })
}, 216)

const scope = {
  $buyer: buyer,
  $vendor: vendor,
  $goToTermination,
  $contractManagement: contractManagement,
  $addOne,
  $readOne,
  $editOne,
  $prompt: app.$prompt,
  $detectContractInformationRightByRow,
  $createdUserIsCurrentUserByRow: createdUserIsCurrentUserByRow,
  $reactiveAction: action
}
</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="schema" :scope="scope" schemaKey="ContractHead" />
</template>
