<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  ViewModel,
  i18nExpression, changeFieldVisibleByDeps, queryFieldValueExpression, queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  requiredValidatorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import {usePageHelper} from 'lib@/components/composables/usePageHelper'
import vendorProfileDetailReadEngine from './vendorProfileDetailReadEngine.vue'
import quaOfReviewDetail from 'modcb@/vendorManagementBuyer/views/quaOfReviewEngine/quaOfReviewDetail'

const {emitTabAdd, app} = usePageHelper()

const schema = defineSchemas({
  state: {
    'x-data': {
      companyId: null
    }
  },
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-component-props': {
      '@activated': expression(`() => {
        app.$nextTick(() => {
          $form.query('query').take(field => {
            const { from, funName, formId, formNo, taskIndex, row } = app.$route.params
            // 区分供应商注册和附件审批待办跳转
            if (from === 'fromFun' && funName === 'vendorProfile') {
              let flag = 'view'
              if (row.configCode === 'ONESELF_REGISTER_SUBMITTED') {
                // configCode: "ONESELF_REGISTER_SUBMITTED"是供应商注册
                flag = 'passRegister'
              } else {
                // configCode: "GYSKTBBH"是附件审批
                flag = taskIndex === 1 ? 'startFileApproval' : 'view'
              }
              // 待办跳转
              let tab = {
                component: vendorProfileDetailReadEngine,
                params: {
                  flag,
                  companyId: formId,
                  tabName: 'vendorProfileDetailReadEngine' + formNo
                },
                title: formNo,
                name: 'vendorProfileDetailReadEngine' + formNo
              }
              emitTabAdd(tab)
            }
          })
        })
      }`)
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        queryPotentialSupplier: {
          immediate: true,
          method: 'paginationQuery',
          transformRequest: expression(`(data, header) => {
            if (data.payload?.filter) {
              const {
                abnormalDirectory,
                registeredCapitalEnd,
                registeredCapital,
                ...otherFilter
              } = data.payload.filter
              if (abnormalDirectory) {
                data.payload.filter = {
                  ...otherFilter,
                  '$or': {
                    isBacklist: 'Y',
                    keySupervisionFlag: 'Y',
                    categoryLimitFlag: 'Y',
                    timeLimitFlag: 'Y',
                    positionLimitFlag: 'Y'
                  }
                }
              }
              if (registeredCapital && registeredCapitalEnd) {
                const from = registeredCapital.gt
                const to = registeredCapitalEnd.eq
                delete data.payload.filter.registeredCapital
                delete data.payload.filter.registeredCapitalEnd
                data.payload.filter.registeredCapital = {
                  between: [from, to]
                }
              } else if (registeredCapitalEnd) {
                const to = registeredCapitalEnd.eq
                delete data.payload.filter.registeredCapitalEnd
                data.payload.filter.registeredCapital = {
                  le: to
                }
              }
            }
            return data
          }`),
          onSuccess: expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.companyId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)
        },
        approve: {
          autoFormatResult: false
        },
        reject: {
          autoFormatResult: false
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'green',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          immediateQueryForm: true
        },
        properties: generateXindexInOrder({
          companyName: {
            type: 'string',
            'x-query-engine-query-operator': 'contains',
            title: i18nExpression('common.vendorName') // 供应商名称
            // 'x-component': 'QuickSearchWrapper',
            // 'x-component-props': {
            //   showKey: 'companyName',
            //   propKey: 'companyName',
            //   name: 'scc_sup_company_info_all'
            // }
          },
          companyCode: {
            type: 'string',
            'x-query-engine-query-operator': 'contains',
            title: i18nExpression('common.vendorCode') // 供应商编码
          },
          contactName: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.contactName'),
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'contactInfos'
          },
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.contactPhone'),
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'contactInfos'
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 纳税人识别号
            'x-query-engine-query-operator': 'contains'
          },
          isBacklist: {
            type: 'string',
            title: i18nExpression('vendorMod.isBacklist'), // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          overseasRelation: {
            type: 'string',
            title: i18nExpression('vendorMod.overseasRelation'), // 企业分类
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION'
            }
          },
          companyType: {
            type: 'string',
            title: i18nExpression('vendorMod.companyType'), // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE_NEW'
            }
          },
          pjCompanyStatus: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.companyStatus'), // 供应商状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PJ_COMPANY_STATUS'
            }
          },
          dataSources: {
            type: 'string',
            title: i18nExpression('vendorMod.dataSources'), // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
            }
          },
          status: {
            title: i18nExpression('vendorMod.registerStatus'), // 注册状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_LIST_STATUS_vendorProfileList'
            }
          },
          domesticAndForeignRelations: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.domesticAndForeignRelations'), // 境内外关系
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW'
            }
          },
          approvedDate: {
            title: i18nExpression('cusEntry.vendorMod.enterDate'),
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          companyCreationDate: {
            title: i18nExpression('cusEntry.vendorMod.creationDate'),
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          categoryId: {
            type: 'string',
            title: i18nExpression('vendorMod.categoryName'),
            'x-component': 'QuickSearchWrapper',
            'x-query-engine-query-operator': 'eq',
            'x-component-props': {
              showKey: 'categoryName',
              name: 'scc_base_purchase_category4'
            },
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'cateJournalList'
          },
          keySupervisionFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.isKeySupervision'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          registCurrency: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.registeredCurrency'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'currency'
            }
          },
          registeredCapital: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.registeredAmountFrom'),
            'x-query-engine-query-operator': 'gt'
          },
          registeredCapitalEnd: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.registeredAmountEnd')
          },
          infoCompleteFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.infoCompleteFlag'), // 信息是否完善
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          companyAddress: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.address'),
            'x-query-engine-query-operator': 'contains'
          },
          extLegalOpinion: {
            type: 'string',
            title: i18nExpression('cusEntry.supplement20250218.lgNodePassed'),  // 'LG节点是否通过'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          extFinancialOpinion: {
            type: 'string',
            title: i18nExpression('cusEntry.supplement20250218.fdNodePassed'),  // 'FD节点是否通过'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          extSecurityAndRiskOpinion: {
            type: 'string',
            title: i18nExpression('cusEntry.supplement20250218.secNodePassed'), // 'SEC节点是否通过'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          abnormalDirectory: {
            type: 'string',
            'x-hidden': true
          },
          potentialSuppliers: {
            type: 'string',
            'x-hidden': true
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup/api-ql/CompanyInfo/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('CompanyInfo.table', 'data.columns'),
              dictCodes: {
                extIsQualifiedFileUpload: 'YES_OR_NO',
                extIsQualifiedStatus: 'QUALIFIED_UPLOAD_STATUS',
                isBacklist: 'YES_OR_NO',
                keySupervisionFlag: 'YES_OR_NO',
                status: 'SUPPLIER_LIST_STATUS',
                dataSources: 'DATA_SOURCE',
                positionLimitFlag: 'YES_OR_NO',
                categoryLimitFlag: 'YES_OR_NO',
                timeLimitFlag: 'YES_OR_NO',
                supplierType: 'SUPPLIER_TYPE',
                infoCompleteFlag: 'YES_OR_NO',
              }
            }
          },
          buttonGroup: {
            type: 'void',
            'x-component': 'el-button-group',
            properties: {
              all: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.all'),  // 全部
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('query').take().invoke('resetQuery')
                  }`)
                }
              },
              potentialSuppliers: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.potentialSuppliers'),  // 潜在供应商
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'POTENTIAL_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              quasiSupplier: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.quasiSupplier'),  // 准供应商
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'QUASI_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              officialSupplier: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.officialSupplier'),  // 正式供应商
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'OFFICIAL_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              sluggishSupplier: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.sluggishSupplier'),  // 呆滞供应商
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'SLUGGISH_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              invalidupSplier: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.invalidupSplier'),  // 无效供应商
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'INVALID_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              abnormalDirectory: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.abnormalDirectory'),  // 异常名录
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').abnormalDirectory = 'Y'
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true,
          editMode: true
        },
        properties: generateXindexInOrder({
          allowClearWithoutSealFlag: {
            type: 'string',
            'x-hidden': true
          },
          allowBidWithoutSealFlag: {
            type: 'string',
            'x-hidden': true
          },
          allowQuotationWithoutSealFlag: {
            type: 'string',
            'x-hidden': true
          },
          createdId: {
            type: 'string',
            'x-hidden': true
          },
          companyCode: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('common.vendorCode'), // 供应商编码
            'x-render-table-column': {
              width: 120
            }
          },
          companyName: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let companyId = row.companyId
                let tab = {
                  component: vendorProfileDetailReadEngine,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorProfileDetailReadEngine' + row.companyName
                  },
                  title: row.companyName,
                  name: 'vendorProfileDetailReadEngine' + row.companyName
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // 供应商名称
              minWidth: 150,
              customRender: true
            }
          },
          lcCode: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('vendorMod.lcCode'), // 纳税人识别号
            'x-render-table-column': {
              minWidth: 150
            }
          },
          extIsQualifiedFileUpload: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.extIsQualifiedFileUpload'), // 资质附件是否上传
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          extIsQualifiedStatus: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.extIsQualifiedStatus'), // 资质附件审批状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUALIFIED_UPLOAD_STATUS'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          isBacklist: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('vendorMod.isBacklist'), // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          keySupervisionFlag: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.isKeySupervision'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          positionLimitFlag: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.ifLimitUnit'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          categoryLimitFlag: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.ifLimitCategory'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          timeLimitFlag: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.ifLimitTime'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          contactName: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.contactName'),
            'x-render-table-column': {
              width: 120
            }
            // 'x-query-engine-relation': 'contactInfos'
          },
          ceeaContactMethod: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.contactPhone'),
            'x-render-table-column': {
              width: 120
            }
            // 'x-query-engine-relation': 'contactInfos'
          },
          pjCompanyStatus: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.companyStatus'), // 供应商状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PJ_COMPANY_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          status: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('vendorMod.registerStatus'), // 审批状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_LIST_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          gscpStatus: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('cusEntry.vendorMod.gscpStatus'),
            'x-render-table-column': {
              width: 100
            }
          },
          approvedDate: {
            'x-read-pretty': true,
            title: i18nExpression('vendorMod.permitDate'), // 准入日期
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.approvedDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130
            }
          },
          dataSources: {
            type: 'string',
            'x-read-pretty': true,
            title: i18nExpression('vendorMod.dataSources'), // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          infoCompleteFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.infoCompleteFlag'),
            'x-read-pretty': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          companyId: {
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': 'true',
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 200,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              passRegister: {
                type: 'void',
                title: i18nExpression('common.toApprove'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.companyCode'],
                  '!$deps[0]'
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'passRegister',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              refuse: {
                type: 'void',
                title: i18nExpression('purchaseDemand.refuse'), // 驳回
                'x-reactions': changeFieldVisibleByDeps(
                  ['.companyCode'],
                  '!$deps[0]'
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    app.$prompt('', $t('vendorMod.rejectReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      inputType: 'textarea'
                    }).then(({ value }) => {
                      let values = {
                        flowRemark: value,
                        companyId: row.companyId
                      }
                      $queryEngine.request.save(values, { query: { '*':{} }, action: 'reject' }).then((res) => {
                        $message.success($t('bidMod.toRefuseSuccess'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                    })
                  }`)
                }
              },
              startFileApproval: {
                type: 'void',
                title: i18nExpression('cusEntry.supplement20250211.submitFileApprove'), // 发起附件审批
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(row.extIsQualifiedStatus)
                }`),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'startFileApproval',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              rejectFile: {
                type: 'void',
                title: i18nExpression('cusEntry.supplement20250211.rejectFile'), // 附件驳回
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(row.extIsQualifiedStatus)
                }`),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    app.$prompt('', $t('vendorMod.rejectReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      inputType: 'textarea',
                      inputValidator: value => !!value,
                      inputErrorMessage: $t('common.pleaseInput')
                    }).then(({ value }) => {
                      let values = {
                        extRejectQualificationReason: value,
                        companyId: row.companyId
                      }
                      app.$http({
                        url: '/api-sup/pj/companyInfo/rejectQualificationReason',
                        method: 'POST',
                        data: { ...values },
                        loading: true
                      }).then(res => {
                        $message.success($t('bidMod.toRefuseSuccess'))
                        $queryEngine.state.paginationManagement.refresh()
                      }).catch(() => {})
                    })
                  }`)
                }
              },
              approval: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['APPROVING'].includes(row.extIsQualifiedStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'approval',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              view: {
                type: 'void',
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  name: 'scc_npm_inspect_vendor_history',
                  showButton: true,
                  btnTitle: i18nExpression('cusEntry.vendorMod.checkHistory'), // 考察历史,
                  btnType: 'text',
                  'pre-query-data': '{{{\'t.vendor_id\': $table.getRowByIndex($self.index).companyId}}}'
                }
              },
              createdQuaofReview: {
                type: 'void',
                title: i18nExpression('vendorMod.createQua'), // 创建资质审查
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.isBacklist'],
                  '[\'APPROVED\'].includes($deps[0]) && [\'N\'].includes($deps[1])'
                ),
                'x-component-props': {
                  type: 'text',
                  ...buttonListItemVisibleByPermission('sup:vendorpRroList:createQua'),
                  '@click': expression(`({ row }) => {
                    let tab = {
                      component: quaOfReviewDetail,
                      params: {
                        flag: 'add',
                        row,
                        tabName: 'quaOfReviewDetail'
                      },
                      title: () => $t('vendorMod.addQua'), // '资质审查新增',
                      name: 'quaOfReviewDetail'
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              // bidTag: {
              //   type: 'void',
              //   title: i18nExpression('cusEntry.common.bidTag'),
              //   'x-component-props': {
              //     type: 'text',
              //     ...buttonListItemVisibleByPermission('sup:vendorpRroList:bidTag'),
              //     '@click': expression(`({row}) => {
              //       const companyId = row.companyId
              //       $form.query('state').get('data').companyId = companyId
              //       $openBidTag($form)
              //     }`)
              //   },
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.pjCompanyStatus'],
              //     '[\'QUASI_SUPPLIER\',\'OFFICIAL_SUPPLIER\'].includes($deps[0])'
              //   )
              // },
              // covenantCertification: {
              //   type: 'void',
              //   title: i18nExpression('cusEntry.common.covenantCertification'),
              //   'x-component-props': {
              //     type: 'text',
              //     ...buttonListItemVisibleByPermission('sup:vendorpRroList:covenantCertification'),
              //     '@click': expression(`({row}) => {
              //       const companyId = row.companyId
              //       $form.query('state').get('data').companyId = companyId
              //       $openCovenantCertification($form, row)
              //     }`)
              //   },
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.pjCompanyStatus'],
              //     '[\'QUASI_SUPPLIER\',\'OFFICIAL_SUPPLIER\'].includes($deps[0])'
              //   )
              // },
              abnormalRegister: {
                type: 'void',
                title: i18nExpression('cusEntry.common.abnormalRegister'),
                'x-component-props': {
                  type: 'text',
                  ...buttonListItemVisibleByPermission('sup:vendorpRroList:abnormalRegister'),
                  '@click': expression(`({row}) => {
                    const companyId = row.companyId
                    $form.query('state').get('data').companyId = companyId
                    $openAbnormalRegister($form)
                  }`)
                }
              }
              // createdQuestionnaire: {
              //   type: 'void',
              //   title: i18nExpression('quest.createdQuestionnaire'), // 创建调查表
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status', '.isBacklist'],
              //     '[\'APPROVED\', \'SUBMITTED\'].includes($deps[0]) && [\'N\'].includes($deps[1])'
              //   ),
              //   'x-component-props': {
              //     type: 'text',
              //     '@click': expression(`({ row }) => {
              //       app.$router.push('/vendorManagement/questManagement')
              //     }`)
              //   }
              // }
            }
          }
        })
      },
      /* 竞价登记 */
      bidTag: {
        type: 'void',
        'x-component': 'RDialog',
        title: i18nExpression('cusEntry.vendorMod.bidTag'),
        'x-component-props': {
          size: 'middle',
          footer: true,
          beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              const value = $form.query('biddingFlag').get('value')
              if (value) {
                $queryEngine.request.update({ companyId, biddingFlag: value}).then((res) => {
                  $message.success($t('cusEntry.tipMessage.tagSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
        }`)
        },
        properties: {
          biddingFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.isCompetition'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          }
        }
      },
      /* 契约验证 */
      contractVerificationDialog: {
        type: 'void',
        'x-component': 'RDialog',
        title: i18nExpression('cusEntry.vendorMod.covenantCertification'),
        'x-component-props': {
          size: 'middle',
          footer: true,
          beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              const values = $form.query('contractVerificationForm').get('value')
              const {
                allowClearWithoutSealFlag,
                allowBidWithoutSealFlag,
                allowQuotationWithoutSealFlag
              } = values
              if (allowClearWithoutSealFlag || allowBidWithoutSealFlag || allowQuotationWithoutSealFlag) {
                $queryEngine.request.update({ companyId, ...values}).then((res) => {
                  $message.success($t('cusEntry.tipMessage.covenantCertificationSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
        }`)
        },
        properties: {
          contractVerificationForm: {
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 3,
              columnGap: 32,
              rowGap: 0
            },
            'x-query-engine-skip': true,
            properties: {
              allowClearWithoutSealFlag: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.allowClearWithoutSealFlag'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                }
              },
              allowBidWithoutSealFlag: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.allowBidWithoutSealFlag'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                }
              },
              allowQuotationWithoutSealFlag: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.allowQuotationWithoutSealFlag'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                }
              }
            }
          }
        }
      },
      /* 异常登记 */
      abnormalRegisterDialog: {
        type: 'void',
        'x-component': 'RDialog',
        title: i18nExpression('cusEntry.vendorMod.abnormalRegister'),
        'x-component-props': {
          size: 'middle',
          footer: true,
          beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              console.log($form.query('abnormalRegisterForm').get('value'))
              const { keySupervisionFlag, npmCompanyExceptionInfos } = $form.query('abnormalRegisterForm').get('value') || {}
              if (keySupervisionFlag) {
                const payLoad = {
                  companyId,
                  keySupervisionFlag,
                  npmCompanyExceptionInfos: [{exceptionRemark: npmCompanyExceptionInfos}]
                }
                $queryEngine.request.save(payLoad, { action: 'saveExceptionInfo' }).then((res) => {
                  $message.success($t('cusEntry.tipMessage.abnormalRegisterSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
          }`)
        },
        properties: {
          abnormalRegisterForm: {
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 3,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              keySupervisionFlag: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.isKeySupervision'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                },
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment
              },
              npmCompanyExceptionInfos: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.keySupervisionRemark'),
                'x-decorator': 'FormItem'
              }
            }
          }
        }
      }
    }
  }
})
/* 竞价登记 */
const $openBidTag = ($form: ViewModel) => {
  $form.query('bidTag').take().setComponentProps({
    visible: true
  })
}
/* 契约验证 */
const $openCovenantCertification = ($form: ViewModel, row) => {
  $form.query('contractVerificationDialog').take().setComponentProps({
    visible: true
  })
  setTimeout(() => {
    $form.query('contractVerificationForm').take().value = row
  })
}
/* 异常登记 */
const $openAbnormalRegister = ($form: ViewModel) => {
  $form.query('abnormalRegisterDialog').take().setComponentProps({
    visible: true
  })
}
const scope = {
  emitTabAdd,
  app,
  i18nExpression,
  vendorProfileDetailReadEngine,
  quaOfReviewDetail,
  $openBidTag,
  $openCovenantCertification,
  $openAbnormalRegister
}

const components = {}

</script>

<template>
  <RenderEngine
    schemaKey="vendorProfileList"
    class="contractPaymentType"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
