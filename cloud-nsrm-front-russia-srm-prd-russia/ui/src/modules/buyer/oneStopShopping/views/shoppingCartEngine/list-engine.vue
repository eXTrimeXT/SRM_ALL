<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, dataTimeSelectorSegment, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import { bus } from 'lib@/components/render-engine/components/bus'
import sumDialog from './components/sumDialog'
import rejectDialog from './components/rejectDialog'
import batchDialog from './components/batchDialog'
import { shoppingCartApi } from 'modb@/oneStopShopping/api'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $ceeaSetSummaryAndNoticeUser = ($form: any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let currentRowsCheck = selects.find(v => v.status !== 'DRAFT')

  if (currentRowsCheck) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg11'))
  }

  if (selects.length < 1) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg12'))
  } else {
    $form.query('sumDialog').take().setComponentProps({ visible: true })
  }
}

const $Batch_maintenance = ($form: any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let currentRowsCheck = selects.find(v => v.status === 'APPLIED')
  if (currentRowsCheck) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg13'))
  }
  if (selects.length < 1) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg14'))
  } else {
    $form.query('batchDialog').take().setComponentProps({ visible: true })
  }
}

const $createProject = ($form:any, $queryEngine:any) => {
  let ids = []
  let arrCurrentRows = []
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  selects.forEach(item => {
    let obj = {}
    obj.purchaseType = item.purchaseType // 采购类型
    obj.requirementDate = item.requirementDate // 需求时间
    obj.requirementNum = item.requirementNum // 数量
    obj.summaryNickname = item.summaryNickname // 汇总人昵称
    // obj.noticeNickname = item.noticeNickname // 通知人昵称
    obj.status = item.status // 状态
    arrCurrentRows.push(obj)
  })
  let purchaseType = true
  let requirementDate = true
  let requirementNum = true
  let summaryNickname = true
  let summaryNicknameWrong = true
  // let noticeNickname = true
  let status = true
  let appled = arrCurrentRows.find(v => v.status == 'APPLIED') // 您勾选的行已生成申请单”，不能重复创建
  arrCurrentRows.forEach(item => {
    if (!item.purchaseType) {
      purchaseType = false
    }
    if (!item.requirementDate) {
      requirementDate = false
    }
    if (!item.requirementNum) {
      requirementNum = false
    }
    if (!item.summaryNickname) {
      summaryNickname = false
    } else {
      if (item.summaryNickname !== app.$store.getters.userInfo.nickname) {
        summaryNicknameWrong = false
      }
    }
    // if (!item.noticeNickname) {
    //   noticeNickname = false
    // }
    if (item.status != 'SUBMITTED') {
      status = false
    }
  })

  if (
    purchaseType &&
    requirementDate &&
    requirementNum &&
    summaryNickname &&
    summaryNicknameWrong &&
    // noticeNickname &&
    status
  ) {
    selects.forEach(elm => {
      ids.push(elm.shopCartId)
    })

    shoppingCartApi.ceeaValidRequiredInfo(ids).then(res => {
      if (res.data == $t('oneStopShopping.createProjectMsg1')) {
        app.$message.success($t('oneStopShopping.createProjectMsg1'))
      } else if (res.data == $t('oneStopShopping.createProjectMsg2')) {
        app.$message.success($t('oneStopShopping.createProjectMsg2'))
      } else if (res.data == $t('oneStopShopping.createProjectMsg3')) {
        app.$message.success($t('oneStopShopping.createProjectMsg3'))
      } else if (!res.data) {
        $queryEngine.request.baseRequest({
          loading: true,
          action: "createRequirements",
          type: 'ShopCart',
          payload: ids,
          query: {
            "*": {}
          }
        }).then((res) => {
          if (res.data) {
            if (res.data.length > 0) {
              app.$message.success($t('common.success'))
              bus.$emit('ShopCart')

              app.$confirm($t('oneStopShopping.createProjectMsg4'), '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                app.$router.push({
                  name: 'purchaseApplication',
                  params: { autoQuery: true }
                })
              }).catch((err) => {

              })
            } else {
              app.$message.warning($t('oneStopShopping.createProjectMsg5'))
              bus.$emit('ShopCart')
            }
          }
        }).catch(() => {
        })
      }
    })
  } else if (!purchaseType) {
    app.$message.error($t('oneStopShopping.createProjectMsg1'))
  } else if (!requirementDate) {
    app.$message.error($t('oneStopShopping.createProjectMsg2'))
  } else if (!requirementNum) {
    app.$message.error($t('oneStopShopping.createProjectMsg3'))
  } else if (!summaryNickname) {
    app.$message.error($t('oneStopShopping.createProjectMsg6'))
  } else if (!summaryNicknameWrong) {
    app.$message.error($t('oneStopShopping.createProjectMsg7'))
  } else if (appled) {
    app.$message.error($t('oneStopShopping.createProjectMsg9'))
  } else if (!status) {
    app.$message.error($t('oneStopShopping.createProjectMsg10'))
  }
}

const $submitGet = ($form:any, $queryEngine:any) => {
  let ids = []
  let arrCurrentRows = []
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  selects.forEach(item => {
    let obj = {}

    obj.summaryNickname = item.summaryNickname // 汇总人昵称
    // obj.noticeNickname = item.noticeNickname // 通知人昵称
    obj.status = item.status // 状态
    obj.purchaseType = item.purchaseType // 采购类型
    obj.requirementDate = item.requirementDate // 需求时间
    obj.requirementNum = item.requirementNum // 数量
    arrCurrentRows.push(obj)
  })

  let summaryNickname = true
  // let noticeNickname = true
  let status = true
  let purchaseType = true
  let requirementDate = true
  let requirementNum = true
  arrCurrentRows.forEach(item => {
    if (!item.summaryNickname) {
      summaryNickname = false
    }
    // if (!item.noticeNickname) {
    //   noticeNickname = false
    // }
    if (item.status != 'SUBMITTED') {
      status = false
    }
    if (!item.purchaseType) {
      purchaseType = false
    }
    if (!item.requirementDate) {
      requirementDate = false
    }
    if (!item.requirementNum) {
      requirementNum = false
    }
  })

  if (
    summaryNickname &&
    // noticeNickname &&
    !status &&
    purchaseType &&
    requirementDate &&
    requirementNum
  ) {
    selects.forEach(elm => {
      ids.push(elm.shopCartId)
    })
    console.log('ids', ids)
    // 先保存
    let shopCarts = [...selects]
    $queryEngine.request.baseRequest({
      loading: true,
      action: "update",
      type: 'ShopCart',
      payload: shopCarts,
      query: {
        "*": {}
      }
    }).then((res) => {
    }).catch(() => {
    })

    $queryEngine.request.baseRequest({
      loading: true,
      action: "submit",
      type: 'ShopCart',
      payload: shopCarts,
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')
    }).catch(() => {
    })
  } else if (!summaryNickname) {
    app.$message.error($t('oneStopShopping.createProjectMsg6'))
  } else if (!purchaseType || !requirementDate || !requirementNum) {
    app.$message.error($t('oneStopShopping.submitGetMsg1'))
  } else if (status) {
    app.$message.error($t('oneStopShopping.submitGetMsg2'))
  }
}

const $keep = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (!selects.length) return app.$message.warning('请勾选数据')
  if (selects.some(item => item.status != 'DRAFT')) {
    return app.$message.warning('请勾选未提交的数据')
  }

  $queryEngine.request.baseRequest({
    loading: true,
    action: "update",
    type: 'ShopCart',
    payload: selects,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  }).catch(() => {
  })
}

const $deleteByIds = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let delIds = true
  selects.forEach(item => {
    if (item.status !== 'DRAFT') {
      delIds = false
    }
  })
  let ids = []
  selects.forEach(elm => {
    ids.push(elm.shopCartId)
  })
  if (!delIds || ids.length === 0) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg11'))
  }

  $queryEngine.request.baseRequest({
    loading: true,
    action: "delete",
    type: 'ShopCart',
    payload: ids,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  }).catch(() => {
  })
}

const $submitOut = ($form:any, $queryEngine:any) => {
  let userId = app.$store.getters.userInfo.userId
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let currentRowsCheck = selects.find(v => v.status !== 'SUBMITTED')
  let userIdCheck = selects.find(v => v.summaryUserId !== userId)

  if (currentRowsCheck) {
    return app.$message.warning($t('oneStopShopping.submitOutMsg1'))
  }
  if (userIdCheck) {
    return app.$message.warning($t('oneStopShopping.submitOutMsg2'))
  }

  if (selects.length < 1) {
    return app.$message.warning($t('components.userSelection.selectData'))
  }

  $form.query('rejectDialog').take().setComponentProps({ visible: true })
}

const $rejectSubmit = ($form: any, $queryEngine: any, done:any) => {
  $form.query('rejectForm').take().submit(values => {
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()

    let ids = []
    selects.forEach(item => {
      ids.push(item.shopCartId)
    })
    let submitData = {
      ids,
      returnReason: values.returnReason
    }

    $queryEngine.request.baseRequest({
      loading: true,
      action: "withdraw",
      type: 'ShopCart',
      payload: [submitData],
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')

      done()
    }).catch(() => {
    })
  })
}

const $batchSubmit = ($form: any, $queryEngine: any, done:any) => {
  $form.query('batchUpdateForm').take().submit(values => {
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()

    let ids = []
    selects.forEach(item => {
      ids.push(item.shopCartId)
    })
    let submitData = {
      ids,
      purchaseType: values.purchaseType,
      requirementDate: values.requirementDate
    }

    $queryEngine.request.baseRequest({
      loading: true,
      action: "batchUpdate",
      type: 'ShopCart',
      payload: [submitData],
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')

      done()
    }).catch(() => {
    })
  })
}

const $confirmSum = ($form:any, $queryEngine:any, done:any) => {
  $form.query('sumForm').take().submit(values => {
    console.log('values', values)
    let submitData = {}
    let ids = []
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()

    selects.forEach(elm => {
      ids.push(elm.shopCartId)
    })
    submitData = {
      ...values,
      ids: ids
    }

    $queryEngine.request.baseRequest({
      loading: true,
      action: "setSummaryAndNoticeUser",
      type: 'ShopCart',
      payload: [submitData],
      query: {
        "*": {}
      }
    }).then((res) => {
      console.log('res', res)
      if (res.data && res.data.length) {
        let result = res.data[0]
        app.$message.success($t('common.success'))
        ids.forEach(item => {
          let tableData = $form.values.table || []
          let index = tableData.findIndex(data => data.shopCartId == item)
          tableData[index].summaryEmpNo = result.summaryEmpNo
          tableData[index].summaryNickname = result.summaryNickname
          tableData[index].summaryUserId = result.summaryUserId
        })
      }

      // bus.$emit('ShopCart')

      done()
    }).catch(() => {
    })
  })
}

const $detailOne = (type: string, row: any) => {

}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.tagTemplateRelationId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
    }
  },
  ShopCart: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {

          }`)
        }
      }
    },
    properties: {
      sumDialog: {
        ...sumDialog
      },
      rejectDialog: {
        ...rejectDialog
      },
      batchDialog: {
        ...batchDialog
      },
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ShopCart',
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
          materialId: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              showKey: 'materialCode',
              propKey: 'materialId'
            }
          },
          status: {
            type: 'string',
            title: "{{$t('common.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SHOP_CART_STATUS'
            }
          },
          orgIds: {
            type: 'string',
            title: "{{$t('quota.org')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: true,
              '@select': expression(`(node) => {
                  if (!$form.values.query.organizationIds) return
                  $form.values.query.organizationIds = null
              }`)
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryName'
            }
          },
          organizationIds: {
            type: 'string',
            title: "{{$t('purchaseDemand.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: true,
              disabled: expression('!$form.values.query.orgIds'),
              'parent-id': expression('$form.values.query.orgIds')
            },
            'x-query-engine-query-operator': 'in'
          },
          summaryNickname: {
            type: 'string',
            title: "{{$t('oneStopShopping.summaryUser')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // noticeNickname: {
          //   type: 'string',
          //   title: "{{$t('oneStopShopping.noticeUser')}}",
          //   'x-query-engine-query-operator': 'contains',
          // },
          createdUserName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: "{{$t('common.creationTime')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          purchaseType: {
            type: 'string',
            title: "{{$t('purchaseDemand.purchaseType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
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
          ceeaSetSummaryAndNoticeUser: {
            type: 'void',
            title: "{{$t('oneStopShopping.setSummaryAndNoticeUser')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $ceeaSetSummaryAndNoticeUser($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:ceeaSetSummaryAndNoticeUser')
            }
          },
          submitGet: {
            type: 'void',
            title: "{{$t('oneStopShopping.submitGet')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $submitGet($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:submit_get')
            }
          },
          createProject: {
            type: 'void',
            title: "{{$t('oneStopShopping.createProject')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $createProject($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:createProjectDetail')
            }
          },
          submitOut: {
            type: 'void',
            title: "{{$t('oneStopShopping.submitOut')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $submitOut($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:submit_out')
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/ShopCart/query",
              ...exportExcelSegment,
              meiqlKey: "ShopCart", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ShopCart.table', 'data.columns'),
              dictCodes: {
                status: 'SHOP_CART_STATUS',
                purchaseType: 'PURCHASE_TYPE'
              }
            }
          },
          Batch_maintenance: {
            type: 'void',
            title: "{{$t('vendorMod.batchMaintain')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $Batch_maintenance($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:Batch_maintenance')
            }
          },
          keep: {
            type: 'void',
            title: "{{$t('common.save')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $keep($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:save')
            }
          },
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $deleteByIds($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:ceeaDeleteByIds')
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
          preColumns: 'seq, checkbox',
          openCustomTable: true,
          editMode: 'multi-row'
        },
        properties: generateXindexInOrder({
          shopCartId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SHOP_CART_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('common.status')}}",
              minWidth: 100
            }
          },
          requirementHeadNum: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementHeadNum')}}",
              minWidth: 120
            }
          },
          returnReason: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.returnReason')}}",
              minWidth: 120
            }
          },
          summaryNickname: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('oneStopShopping.summaryUser')}}",
              minWidth: 120
            }
          },
          // noticeNickname: {
          //   type: 'string',
          //   'x-render-table-column': {
          //     title: "{{$t('oneStopShopping.noticeUser')}}",
          //     minWidth: 120,
          //   },
          // },
          purchaseType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE',
              style: {
                width: '120px'
              }

            },
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.purchaseType')}}",
              minWidth: 150
            },
            'x-reactions': {
              dependencies: ['.status'],
              fulfill: {
                state: {
                  disabled: expression(`$deps[0] === 'APPLIED'`)
                }
              }
            }
          },
          requirementDate: {
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementDate')}}",
              minWidth: 150
            },
            'x-reactions': {
              dependencies: ['.status'],
              fulfill: {
                state: {
                  disabled: expression(`$deps[0] === 'APPLIED'`)
                }
              }
            },
            type: 'date',
            default: null,
            'x-component-props': {
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd',
              style: {
                width: '120px'
              }
            }
          },
          requirementNum: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementQuantity')}}",
              minWidth: 120
            },
            'x-reactions': {
              dependencies: ['.status'],
              fulfill: {
                state: {
                  disabled: expression(`$deps[0] === 'APPLIED'`)
                }
              }
            }
          },
          ifCatalog: {
            type: 'string',
            title: "{{$t('purchaseDemand.ceeaIfCatalogMaterial')}}",
            'x-component': 'Select',
            enum: [
              {
                label: "{{$t('common.yes')}}",
                value: 'Y'
              },
              {
                label: "{{$t('common.no')}}",
                value: 'N'
              }
            ]
          },

          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.materialCode')}}",
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('materialMainData.materialDesc')}}",
              minWidth: 120
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.category')}}",
              minWidth: 120
            }
          },
          specification: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.specification')}}",
              minWidth: 120
            }
          },
          unitName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('dataConfMod.unit')}}",
              minWidth: 120
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.businessEntity')}}",
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.invOrg')}}",
              minWidth: 120
            }
          },
          contractNo: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.contractNum')}}",
              minWidth: 120
            }
          },
          unitPrice: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('oneStopShopping.unitPrice')}}",
              minWidth: 120
            }
          },
          currencyName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.currency')}}",
              minWidth: 120
            }
          },
          supplierCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.vendorCode')}}",
              minWidth: 120
            }
          },
          supplierName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.vendorName')}}",
              minWidth: 120
            }
          },

          createdUserName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          }

        })
      }
    }
  }
})

// @ts-ignore
const components = {
}

const scope = {
  $delete,
  $ceeaSetSummaryAndNoticeUser,
  $confirmSum,
  $submitGet,
  $submitOut,
  $rejectSubmit,
  $keep,
  $deleteByIds,
  $Batch_maintenance,
  $batchSubmit,
  $createProject

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="ShopCartList"
  />
</template>
