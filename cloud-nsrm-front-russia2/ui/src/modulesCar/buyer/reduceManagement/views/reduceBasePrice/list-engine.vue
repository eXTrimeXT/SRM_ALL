<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction,
  generateCharReactionExpression,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $submitData = ($form:any, $queryEngine:any, $table:any, $index:any, row:any) => {
  $form.validate().then(() => {
    $queryEngine.request.save(row, { query: { '*': {} }, loading: true }).then((res) => {
      if (res.data.length) app.$message.success($t('common.success'))
      $table.cancelEditRow($index)
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch(err => {
    app.$message.warning($t('common.pleasefinishRequired'))
  })
}

const $approve = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT') || !selects.length) return app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认提交审批'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return row.basicPriceId
    })
    app.$http({
      url: '/api-cost/reduce/basic/price/submitApproval',
      method: 'POST',
      data: { basicPriceIds: payload },
      loading: true
    }).then((res: any) => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const $getFirstDraft = ($form:any, $queryEngine:any, $values:any, done:any, $bus:any) => {
  $form.query('*.Dialog.form').take().submit(values => {
    app.$http({
      url: '/api-cost/reduce/basic/price/generate',
      method: 'POST',
      data: { ...values },
      loading: true
    }).then((res) => {
      app.$message.success($t('数据生成成功，请前往“配置中心-导出中心”进行下载'))
      $bus.$emit('ReduceBasePrice')
      done()
    }).catch(() => {
      done()
    })
  })
}

const $delete = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT')) app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认删除'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return { basicPriceId: row.basicPriceId }
    })

    $queryEngine.request['delete'](payload, { loading: true }).then((res: any) => {
      app.$message.success($t('common.successDelete'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const schema = defineSchemas({
  ReduceBasicPrice: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'cost',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          transformResponse: (res: string) => {
            const data = JSON.parse(res)
            if (data.data?.ref?.ReduceBasicPrice) {
              const keys = Object.keys(data.data.ref.ReduceBasicPrice ?? {})
              keys.forEach(key => {
                const item = data.data.ref.ReduceBasicPrice[key]
                item.reduceYear = String(item.reduceYear)
              })
            }
            return data
          },
          onSuccess: expression(`(res) => {
            console.log('success',$form.query('ReduceBasicPrice.table').take())
          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ReduceBasePrice',
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
          materialCode: {
            type: 'string',
            title: "{{$t('reduce.materialCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 业务实体
          orgId: {
            type: 'string',
            title: "{{$t('common.orgId')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              '@select': expression(`(node) => {
                  if (!$form.values.query.invOrgId) return
                  $form.values.query.invOrgId = null
              }`)
            }
          },
          // 库存组织
          invOrgId: {
            type: 'string',
            title: "{{$t('common.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              disabled: expression('!$form.values.query.orgId'),
              'parent-id': expression('$form.values.query.orgId')
            }
          },
          carCode: {
            type: 'string',
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_cost_car',
              'preQueryData': expression(`{'t.car_level': 2}`),
              showKey: 'carCode',
              propKey: 'carCode'
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('reduce.approveStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            }
          },
          'reduceYear': {
            type: 'string',
            title: "{{$t('reduce.reduceYear')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              'value-format': 'yyyy'
            }
          }

        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          getFirstDraft: {
            type: 'void',
            title: "{{$t('reduce.getFirstDraft')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:getFirstDraft'),
              visible: expression('false'),
              '@click': expression(`() => {
                $form.query('Dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('form').take().reset()
                })
              }`)
            }
          },
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:add'),
              '@click': expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{approveStatus: 'DRAFT'})
              }`)
            }
          },
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:delete'),
              '@click': expression(`() => {
                $delete($form,$queryEngine)
              }`)
            }
          },

          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:export'),
              pageUrl: "/api-cost/api-ql/ReduceBasicPrice/query",
              ...exportExcelSegment,
              meiqlKey: "ReduceBasicPrice", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ReduceBasicPrice.table', 'data.columns'),
              dictCodes: {
                approveStatus: 'APPROVE_STATUS'
              }
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:import'),
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cost/reduce/basic/price/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cost/reduce/basic/price/exportExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ReduceBasePrice')
              }`)
            }
          },
          approve: {
            type: 'void',
            title: "{{$t('reduce.approve')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:approve'),
              '@click': expression(`() => {
                $approve($form,$queryEngine)
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
          preColumns: 'checkbox,seq',
          openCustomTable: true,
          editMode: 'multi-row'
        },
        properties: generateXindexInOrder({
          basicPriceId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          reduceYear: {
            'x-render-table-column': {
              title: i18nExpression('reduce.reduceYear'),
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-decorator': 'FormItem',
            ...editTableFormItemValid,
            type: 'date',
            default: null,
            'x-component-props': {
              type: 'year',
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy',
              'value-format': 'yyyy'
            }
          },
          orgId: {
            type: 'string',
            ...editTableFormItemValid,
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              '@select': expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                if (val && row.orgId === organizationId) {
                  // 避免重复执行
                  return
                }
                row.orgId = organizationId
                row.orgCode = organizationCode
                row.orgName = organizationName
                console.log('!!!!!!!!!  row  :', row)
                // 清空库存组织
                row.invOrgId = ''
                row.invOrgCode = ''
                row.invOrgName = ''

              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('common.orgId')}}",
              minWidth: 160
            }
          },
          invOrgId: {
            type: 'string',
            ...editTableFormItemValid,
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('bid_mod.inv')}}",
              multiple: false,
              // disabled: expression('!$form.values.query.orgId'),
              parentId: expression(`$table.getRowByIndex($self.index)?.orgId || '' `),
              '@select': expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}
                if (val && row.invOrgId === organizationId) {
                  // 避免重复执行
                  return
                }
                row.invOrgId = organizationId
                row.invOrgCode = organizationCode
                row.invOrgName = organizationName
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('common.invOrg')}}",
              minWidth: 160
            }
          },
          carCode: {
            type: 'string',
            ...editTableFormItemValid,
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'carCode',
              propKey: 'carCode',
              'name': 'scc_cost_car',
              'preQueryData': expression(`{'t.car_level': 2}`),
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.carId = val ? val.carId : ''
                row.carCode = val ? val.carCode : ''
                row.carName = val ? val.carName : ''
                row.parentCarId = val ? val.parentCarId : ''
                row.parentCarCode = val ? val.parentCarCode : ''
                row.parentCarName = val ? val.parentCarName : ''
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          materialCode: {
            type: 'string',
            ...editTableFormItemValid,
            title: "{{$t('reduce.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_cost_reduce_material',
              showKey: 'materialCode',
              propKey: 'materialCode',
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.materialName = val ? val.materialName : ''
                row.materialId = val ? val.materialId : ''
                row.materialCode = val ? val.materialCode : ''

                // 带出其他值
                row.categoryName = val ? val.categoryName : ''
                row.categodryId = val ? val.categodryId : ''
                row.categodryCode = val ? val.categodryCode : ''

                row.priceType = val ? val.priceType : ''
                row.referBasicPointPrice = val ? val.referBasicPointPrice : ''
                row.effectiveDate = val ? val.effectiveDate : ''
                row.expirationDate = val ? val.expirationDate : ''
                // 装配系数
                row.assembleCoefficient = val ? val.assembleCoefficient : ''

              }`)
            },
            'x-reactions': expression(`(field) => {
              let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.materialName')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          vendorCode: {
            type: 'string',
            ...editTableFormItemValid,
            title: "{{$t('reduce.vendorCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              showKey: 'companyCode',
              propKey: 'companyCode',
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.vendorName = val ? val.companyName : ''
                row.vendorId = val ? val.companyId : ''
                row.vendorCode = val ? val.companyCode : ''
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('reduce.vendorCode')}}",
              minWidth: 150
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.vendorName')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.categoryName')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          priceType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PRICE_TYPE'
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('reduce.priceType')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          referBasicPointPrice: {
            type: 'string',
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('reduce.referBasicPointPrice')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          effectiveDate: {
            title: "{{ $t('reduce.effectiveDate') }}",
            ...yearMonthDaySelectorSegment,
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              width: 150,
              skipEditable: true
            }
          },
          expirationDate: {
            title: "{{ $t('reduce.expirationDate') }}",
            ...yearMonthDaySelectorSegment,
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              width: 150,
              skipEditable: true
            }
          },
          assembleCoefficient: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.assembleCoefficient')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)
          },
          confirBasicPointPrice: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.confirBasicPointPrice')}}",
              minWidth: 120
            }
          },
          approveTime: {
            title: "{{ $t('reduce.approveTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 160,
              skipEditable: true
            }
          },

          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120,
              skipEditable: true
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150,
              skipEditable: true
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.updatePeople')}}",
              width: 120,
              skipEditable: true

            }
          },
          lastUpdateDate: {
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: "{{$t('common.updateTime')}}",
              skipEditable: true,
              width: 120
            }
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.approveStatus')}}",
              skipEditable: true,
              minWidth: 100
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            'x-render-table-column': {
              fixed: 'right',
              width: 120
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT'].includes($deps[0]) && !$table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  // ...buttonListItemVisibleByPermission('reduce:reduceBasePrice:edit'),
                  '@click': expression(`({rowIndex}) => {
                    $table.editRowByIndex(rowIndex)
                  }`)
                }
              },
              cancel: {
                type: 'void',
                title: "{{$t('common.cancel')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  [`.approveStatus`],
                  `$table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)
                }
              },
              save: {
                type: 'void',
                title: "{{$t('common.save')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT'].includes($deps[0]) && $table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)
                }
              }
            }
          }
        })
      },
      Dialog: {
        type: 'void',
        title: i18nExpression('reduce.getFirstDraft'),
        'x-component': 'RDialog',
        'x-component-props': {
          'close-on-click-modal': false,
          destroyOnClose: true,
          // size: 'large',
          footerButtonList: expression(`(_, { cancelButton,okButton }) => {
          return [
            cancelButton,
            {
              ...okButton,
              text: '生成数据',
              type:'primary',
            },
          ]
          }`),
          beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              $getFirstDraft($form,$queryEngine,$values,done,$bus)
            } else {
              done()
              }
            }
          `)
        },
        properties: {
          form: {
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              reduceYear: {
                'x-decorator': 'FormItem',
                title: "{{$t('reduce.reduceYear')}}",
                'x-component': 'DatePicker',
                'x-component-props': {
                  type: 'year',
                  'value-format': 'yyyy'
                },
                ...requiredValidatorSegment
              },
              carCode: {
                type: 'string',
                title: "{{$t('reduce.carCode')}}",
                'x-decorator': 'FormItem',
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  showKey: 'carCode',
                  propKey: 'carCode',
                  'name': 'scc_cost_car',
                  'preQueryData': expression(`{'t.car_level': 2}`),
                  '@close-quicksearch': expression(`(val, scope) => {
                    $values.form.carId = val ? val.carId : ''
                    $values.form.carCode = val ? val.carCode : ''
                    $values.form.carName = val ? val.carName : ''
                  }`)
                },
                ...requiredValidatorSegment
              },
              // 业务实体
              orgId: {
                type: 'string',
                title: "{{$t('common.orgId')}}",
                'x-decorator': 'FormItem',
                'x-component': 'OrganizationSelector',
                'x-component-props': {
                  'parent-id': -1,
                  'node-type': 'OU',
                  'select-type': 'input',
                  placeholder: "{{$t('common.pleaseSelect')}}",
                  multiple: false,
                  '@select': expression(`(node, val) => {
                    const row = $form.values.form
                    const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                    if (node && row.orgId === organizationId) {
                      // 避免重复执行
                      return
                    }
                    row.orgId = organizationId
                    row.orgCode = organizationCode
                    row.orgName = organizationName
                    // 清空库存组织
                    row.invOrgId = ''
                    row.invOrgCode = ''
                    row.invOrgName = ''

                  }`)
                }
              },
              // 库存组织
              invOrgId: {
                type: 'string',
                title: "{{$t('common.invOrg')}}",
                'x-decorator': 'FormItem',
                'x-component': 'OrganizationSelector',
                'x-component-props': {
                  'node-type': 'INV',
                  'select-type': 'input',
                  placeholder: "{{$t('common.pleaseSelect')}}",
                  multiple: false,
                  disabled: expression('!$form.values?.form.orgId'),
                  parentId: expression('$form.values?.form.orgId || -1'),
                  '@select': expression(`(node, val) => {

                      const row = $form.values.form
                      const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                      if (node && row.orgInvId === organizationId) {
                        // 避免重复执行
                        return
                      }

                      row.invOrgId = organizationId
                      row.invOrgCode = organizationCode
                      row.invOrgName = organizationName

                  }`)
                }
              }

            }
          }
        }
      }
    }
  }
})

// @ts-ignore
const components = {
}

const scope = {
  $delete,
  $getFirstDraft,
  $approve,
  $submitData
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceBasePrice"
  />
</template>
