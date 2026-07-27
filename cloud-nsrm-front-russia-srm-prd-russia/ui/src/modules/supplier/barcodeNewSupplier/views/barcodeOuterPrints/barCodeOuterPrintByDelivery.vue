<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import DetailDialog from './components/detailByDeliveryDialog'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import { commonApi } from 'mod@/common/baseSettingCommon/api'
// @ts-ignore
import { systemUrl } from '@/config/sysConfig'

const { app, t: $t, vendor: $vendor } = usePageHelper()

// 新增
const $addOne = ($form: any) => {
  $form.query('*.detailDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.detailDialog.form').take((field: any) => {
      field.reset()
      const { companyCode: vendorCode, companyId: vendorId, companyName: vendorName } = app.$store.getters.userInfo
      field.setValue({ vendorCode, vendorId, vendorName })
    })
  })
}

// 获取条码生成规则
const $getTagRuleList = ($queryEngine: any, $form: any, val: any, flag: any) => {
  if (flag === 'dialog') {
    $form.query('TagOuterBox').get('data').tagRuleList = []
  } else {
    $form.query('TagOuterBox').get('data').queryTagRuleList = []
  }

  $queryEngine.request.baseRequest({
    'type': 'TagGenerateRuleConfig',
    'lang': 'zh-cn',
    "query": {
      "*": {}
    },
    "payload": [{
      "materialCode": val.materialCode,
      "categoryCode": val.categoryCode,
      "ruleType": 'OUTER'
    }],
    'action': 'listByMaterialAndCategory'
  }).then((res: any) => {
    if (res.data.length) {
      res.data.forEach((item: any) => {
        item.label = item.tagRuleName
        item.value = item.tagGenerateRuleId
        if (item.defaultFlag === 'Y') {
          $form.values.form.tagGenerateRuleId = item.tagGenerateRuleId
          $form.values.form.tagRuleCode = item.tagRuleCode
          $form.values.form.tagRuleName = item.tagRuleName
          $form.values.form.tagType = item.tagType
        }
      })
      if (flag === 'dialog') {
        $form.query('TagOuterBox').get('data').tagRuleList = res.data
      } else {
        $form.query('TagOuterBox').get('data').queryTagRuleList = res.data
      }
    }
  })
}

// 送货单快查
const $getBeliverInfoByQuick = ($queryEngine: any, $form: any, val: any) => {
  val.deliveryLine = val.lineNum || null
  val.generateTotalQuantity = val.generatetotalquantity || 0
  val.generateQuantity = (+val.deliveryQuantity - +val.generateTotalQuantity) ?? null
  $form.query('TagOuterBox').get('data').tagRuleList = []

  const { companyCode: vendorCode, companyId: vendorId, companyName: vendorName } = app.$store.getters.userInfo
  val = { ...val, vendorCode, vendorId, vendorName }

  delete val.status

  commonApi.materialItemGet({ id: val.materialId }).then((res: any) => {
    if (res.data && res.data.materialItem && res.data.materialItem.outboxMinPackagingQuantity) {
      val.maxBoxQuantity = res.data.materialItem.outboxMinPackagingQuantity
      $form.query('*.detailDialog.form.maxBoxQuantity').take().setComponentProps({ disabled: true })
    } else {
      $form.query('*.detailDialog.form.maxBoxQuantity').take().setComponentProps({ disabled: false })
    }
    if (res.data && res.data.materialItem) {
      val.unit = res.data.materialItem.unit
    }

    $form.query('*.detailDialog.form').take((field: any) => {
      field.setValue(val)
    })

    $getTagRuleList($queryEngine, $form, val, 'dialog')

    $getPrintTemplateList($queryEngine, $form, val)
  })
}

// 获取打印模板数据
const $getPrintTemplateList = ($queryEngine: any, $form: any, val: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagTemplateRelation',
    'lang': 'zh-cn',
    "query": {
      "*": {}
    },
    "payload": [{
      "materialCode": val.materialCode,
      "categoryCode": val.categoryCode,
      "type": 'OUTER'
    }],
    'action': 'listByMaterialAndCategory'
  }).then((res: any) => {
    if (res.data.length) {
      res.data.forEach((item: any) => {
        item.label = item.templateName
        item.value = item.templateCode
      })
      $form.query('TagOuterBox').get('data').printTemplateList = res.data
    }
  })
}

// 编辑
const $editOne = ($queryEngine: any, $form: any, row: any) => {
  $setFormValue($queryEngine, $form, row)
}

// 详情
const $readOne = ($queryEngine: any, $form: any, row: any) => {
  $form.readPretty = true
  $setFormValue($queryEngine, $form, row)
}

const $setFormValue = ($queryEngine: any, $form: any, row: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagOuterBox',
    'lang': 'zh-cn',
    'payload': [row.outerBoxId],
    'query': { '*': {} },
    'action': 'read'
  }).then((res: any) => {
    const val = res.data[0] || {}
    $form.query('*.detailDialog').take().setComponentProps({ visible: true })
    $getTagRuleList($queryEngine, $form, val, 'dialog')
    setTimeout(() => {
      $form.query('*.detailDialog.form').take((field: any) => {
        field.setValue(val)
      })
    })
  }).catch(() => {
    $form.readPretty = false
  })
}

// 批量作废
const $batchAbandon = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('TagOuterBox.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.warning('请选择作废的行数据')
  }

  const i = rows.filter((row: any) => row.boundFlag === 'Y')
  if (i.length) {
    const sequence = i.map((item: any) => item.sequence).join()
    return $message.warning(`序号${sequence}已绑定内箱，不可作废`)
  }

  const j = rows.filter((row: any) => row.status === 'N')
  if (j.length) {
    const sequence = j.map((item: any) => item.sequence).join()
    return $message.warning(`序号${sequence}已作废，不可再作废`)
  }

  const k = rows.filter((row: any) => ['DELIVERED', 'SUBMITTED', 'REJECTED', 'CLOSE'].includes(row.deliveryNoteStatus))
  if (k.length) {
    const sequence = k.map((item: any) => item.sequence).join()
    return $message.warning(`送货状态为拟定或已取消时才可以作废，序号${sequence}不可作废`)
  }

  $confirm('确定批量作废所选条码?', {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $abandonOne(rows, $queryEngine, $message)
  })
}

// 作废
const $abandonOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      outerBoxId: item.outerBoxId
    }
  })
  $queryEngine.request.baseRequest({
    'type': 'TagOuterBox',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'disable'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 打开pdf
const $openPrint = (pdfName: any, params: any) => {
  let origin = systemUrl
  const xml = encodeURIComponent(pdfName)
  const url = `${origin}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

// 批量打印
const $batchPrint = async ($self: any, $form: any, $message: any, $queryEngine: any) => {
  const rows = $self.query('TagOuterBox.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.warning('请选择打印的行数据')
  }
  const j = rows.filter((row: any) => row.status === 'N')
  if (j.length) {
    const sequence = j.map((item: any) => item.sequence).join()
    return $message.warning(`序号${sequence}已作废，不可打印`)
  }

  let arr = rows.map((item:any) => item.templatePath)
  if (Array.from(new Set(arr)).length > 1) {
    return $message.warning('所选数据存在多个模板，无法批量打印')
  }

  $print(rows, $form, $queryEngine)
}

// 打印
const $print = (rows: any, $form: any, $queryEngine: any) => {
  if ($vendor()) {
    const params = rows.map((item: any) => {
      return { outerBoxId: item.outerBoxId }
    })
    $queryEngine.request.baseRequest({
      'type': 'TagOuterBox',
      'lang': 'zh-cn',
      "query": {
        "*": {}
      },
      "payload": params,
      'action': 'print'
    }).then((res: any) => {
      $queryEngine.state.paginationManagement.refresh()
      $toPrint(rows)
    })
  } else {
    $toPrint(rows)
  }
}

const $toPrint = (rows: any) => {
  const pdfName = rows[0].templatePath || ''
  const ids = rows.map((item: any) => item.outerBoxId).join(',')

  const params = encodeURIComponent(`ids=${ids}`)
  $openPrint(pdfName, params)
}

// 预览
const $review = ($form: any, $self: any, $queryEngine: any, done: any) => {
  $self.query('*.detailDialog.form').take().submit((values: any) => {
    if (values.outerBoxId) {
      $toPrint([values])
    } else {
      $queryEngine.request.baseRequest({
        'type': 'TagOuterBoxView',
        'lang': 'zh-cn',
        "query": {
          "*": {}
        },
        "payload": [{ ...values, type: 'DELIVERY_NOTE' }],
        'action': 'view',
        'loading': true
      }).then((res: any) => {
        $toPrint(res.data)
      })
    }
  })
}

const $save = ($form: any, $queryEngine: any, values: any, done: any) => {
  let requestType = $form.values.form.outerBoxId ? 'update' : 'save'
  $queryEngine.request[requestType]({
    ...values, type: 'DELIVERY_NOTE'
  }, { loading: true }).then(() => {
    $queryEngine.state.paginationManagement.refresh()
    done()
  })
}

// @ts-ignore
const scope = {
  $addOne,
  $vendor,
  $save,
  $getPrintTemplateList,
  $readOne,
  $editOne,
  $batchAbandon,
  $abandonOne,
  $batchPrint,
  $print,
  $getBeliverInfoByQuick,
  $getTagRuleList,
  $review
}
// @ts-ignore
const components = {
  DetailDialog
}

// @ts-ignore
const schema = defineSchemas({
  TagOuterBox: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'PageContainer',
    'x-component-props': {
      class: 'the-barcodeOuterPrints-detail'
    },
    'x-data': {
      tagRuleList: [],
      queryTagRuleList: [],
      printTemplateList: []
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              data.payload.filter = {
                type: { 'eq': 'DELIVERY_NOTE' },
                ...data.payload.filter

            }
              return data
            }`),
          onSuccess: expression(`(res) => {
            res.data.forEach((item,index) =>{
              item.sequence = index + 1
            })
          }`)
        }
      }
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          materialName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialName',
              name: `{{$vendor()?'purchase_catalog_material':'scc_base_material_item'}}`,
              '@close-quicksearch': expression(`(val, scope) => {
                  $getTagRuleList($queryEngine,$form,val,'query')
               }`)
            }
          },
          // 品类名称
          categoryId: {
            type: 'string',
            title: i18nExpression('common.categoryName'),
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'categoryName', // 显示值
              propKey: 'categoryId', // 取值
              name: `{{$vendor()?'purchase_catalog_category':'scc_base_purchase_category2'}}`,
              '@close-quicksearch': expression(`(val) => {
                $self.value = val ? val.categoryId : ''
              }`)
            }
          },
          deliveryNumber: {
            type: 'string',
            title: '送货单号', // 送货单号,
            'x-query-engine-query-operator': 'contains'
          },
          tagGenerateRuleId: {
            type: 'string',
            title: '条码规则', // 条码规则
            enum: expression('$form.query(\'TagOuterBox\').get(\'data\').queryTagRuleList'),
            'x-component': 'Select'
          },
          status: {
            type: 'string',
            title: '条码状态', // 条码状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_STATUS'
            }
          },
          boundFlag: {
            type: 'string',
            title: '绑定状态', // 绑定状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BOUND_STATUS'
            }
          },
          vendorId: {
            type: 'string',
            'x-hidden': '{{$vendor()}}',
            title: i18nExpression('common.vendorName'), // '供应商名称'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
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
            'x-hidden': '{{!$vendor()}}',
            title: i18nExpression('创建外箱条码'), // 创建外箱条码
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => $addOne($form)`)
            }
          },
          print: {
            type: 'void',
            title: i18nExpression('批量打印'), // 批量打印
            'x-component': 'RButton',
            'x-component-props': {
              type: `{{$vendor() ? 'default' : 'primary'}}`,
              '@click': expression(`() => {$batchPrint($self,$form,$message,$queryEngine)}`)
            }
          },
          abandon: {
            type: 'void',
            'x-hidden': '{{!$vendor()}}',
            title: i18nExpression('批量作废'), // 批量作废
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchAbandon($self, $queryEngine,$message,$confirm)
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
          outerBoxId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          outerBoxCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $readOne($queryEngine,$form, row)`)
            },
            'x-render-table-column': {
              title: '外箱条码', // 外箱条码
              minWidth: 120,
              customRender: true
            },
            'x-query-engine-sort': 'asc'
          },
          deliveryNumber: {
            type: 'string',
            'x-render-table-column': {
              title: '送货单号', // 送货单号
              minWidth: 120
            }
          },
          deliveryLine: {
            type: 'string',
            'x-render-table-column': {
              title: '送货单行号', // 送货单行号
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemName'), // 物料名称
              minWidth: 120
            }
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
              minWidth: 120
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: '品类名称', // 品类名称
              minWidth: 120
            }
          },
          categoryCode: {
            type: 'string',
            'x-render-table-column': {
              title: '品类编码', // 品类编码
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
              minWidth: 120
            }
          },
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.unitCode'), // 单位
              minWidth: 120
            }
          },
          relationMaterialQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '关联物料数量', // 关联物料数量
              minWidth: 160,
              'title-prefix': { content: '即1个外箱可装的物料数量，贴1个外箱标签' } // 即1个外箱可装的物料数量，贴1个外箱标签

            }
          },
          generateQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '本次生成数量', // 本次生成数量
              minWidth: 160,
              'title-prefix': { content: '本次生成外箱条码的物料数量' } // 本次生成外箱条码的物料数量
            }
          },
          tagNo: {
            type: 'string',
            'x-render-table-column': {
              title: '生成外箱条码数量', // 生成外箱条码数量
              minWidth: 160,
              'title-prefix': { content: '本次生成外箱条码时生成的条码张数，以及对应第几张' } // 本次生成外箱条码时生成的条码张数，以及对应第几张
            },
            'x-query-engine-sort': 'asc'
          },
          generateTotalQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '该送货明细行累计已生成数量', // 该送货明细行累计已生成数量
              minWidth: 160
            }
          },
          deliveryQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '送货数量', // 送货数量
              minWidth: 120
            }
          },
          tagRuleName: {
            type: 'string',
            'x-render-table-column': {
              title: '条码生成规则', // 条码生成规则
              minWidth: 120
            }
          },
          tagType: {
            type: 'string',
            'x-render-table-column': {
              title: '条码样式', // 条码样式
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_TYPE'
            }
          },
          boundInnerBoxFlag: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: '是否绑定内箱条码', // 是否绑定内箱条码
              minWidth: 120
            }
          },
          boundFlag: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BOUND_STATUS'
            },
            'x-render-table-column': {
              title: '绑定状态', // 绑定状态
              minWidth: 120
            },
            'x-query-engine-skip': true
          },
          deliveryNoteStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTE_STATUS'
            },
            'x-render-table-column': {
              title: '送货状态', // 送货状态
              minWidth: 120
            },
            'x-query-engine-skip': true
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_STATUS'
            },
            'x-render-table-column': {
              title: '条码状态', // 条码状态
              minWidth: 120
            }
          },
          printedFlag: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PRINT_STATUS'
            },
            'x-render-table-column': {
              title: '打印状态', // 打印状态
              minWidth: 120
            }
          },
          printCount: {
            type: 'string',
            'x-render-table-column': {
              title: '打印次数', // 打印次数
              minWidth: 120
            }
          },

          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.createdBy1'), // 创建人
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 120,
              fixed: 'right',
              sortable: false
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  print: {
                    type: 'void',
                    title: '打印', // 打印
                    'x-component': 'TableButton',
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => $print([row], $form,$queryEngine)`)
                    },
                    'x-reactions': changeFieldVisibleByDeps(
                      // 废弃状态下不展示按钮
                      ['.status'],
                      `$deps[0] === 'Y'`
                    )
                  },
                  abandon: {
                    type: 'void',
                    title: '作废', // 作废
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status', '.boundFlag', '.deliveryNoteStatus'],
                      `$deps[0] === 'Y' && $deps[1] !== 'Y' && ['CREATE', 'CANCELLED'].includes($deps[2]) && $vendor()`
                    ),
                    'x-component-props': {
                      title: '确认作废该行数据',
                      showPopconfirm: true,
                      '@confirm': expression('({ row }) => $abandonOne([row], $queryEngine, $message)')
                    }
                  }
                  // edit: {
                  //   type: 'void',
                  //   title: '编辑', // 编辑
                  //   'x-component': 'TableButton',
                  //   'x-reactions': changeFieldVisibleByDeps(
                  //     // 废弃状态下不展示按钮
                  //     ['.status'],
                  //     `$deps[0] === 'Y'`
                  //   ),
                  //   'x-component-props': {
                  //     type: 'text',
                  //     '@click': expression(`({ row }) => $editOne($queryEngine,$form,row)`)
                  //   }
                  // }
                }
              }
            }
          }
        })
      },
      detailDialog: {
        ...DetailDialog
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="TagOuterBox" />
</template>
