<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'

import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  generateCharFunctionExpression,
  i18nExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore

import DetailDialog from './tagManageComponents/dialog/outerBox'

// @ts-ignore
import { getDictItem } from '@/api/common'
// @ts-ignore
import { adaptDictData } from '@/utils'

import { bus } from 'lib@/components/render-engine/components/bus'

// @ts-ignore
import barcodeRelation from 'mods@/barcodeNewSupplier/views/barcodeRelation/edit-engine.vue'

// @ts-ignore
import { systemUrl } from '@/config/sysConfig'

const { emitTabAdd, t: $t, app, vendor } = usePageHelper()
const $attrs: any = useAttrs()

// 获取内外箱模板
const innerAndOutTemplate = (async () => {
  let inner = await getDictItem('TAG_PRINT_TEMPLATEP_INNER')
  let outer = await getDictItem('TAG_PRINT_TEMPLATEP_OUTER')

  return {
    inner: adaptDictData(inner.data, 'dict'),
    outer: adaptDictData(outer.data, 'dict')
  }
})()

// 作废
const $abandonOne = (rows: any, $queryEngine: any, $message: any, $form: any, $bus: any) => {
  if (rows[0].boundInnerBoxQuantity > 0) return $message.warning($t('buyerDeliveryOrder.prompt5'))

  let params = rows.map((item: any) => {
    return {
      outerBoxId: item.outerBoxId
    }
  })
  $queryEngine.request
    .baseRequest({
      type: 'TagOuterBox',
      lang: 'zh-cn',
      loading: true,
      payload: params,
      action: 'disable'
    })
    .then((res: any) => {
      $message.success($t('common.success'))
      $bus.$emit('TagManage')
    })
}

// 预览
const $review = ($form: any, $self: any, $queryEngine: any, done: any) => {
  $self.query('*.detailDialog.form').take().submit((values: any) => {
    $queryEngine.request.baseRequest({
      'type': 'TagOuterBoxView',
      'lang': 'zh-cn',
      "query": {
        "*": {}
      },
      "payload": [{ ...values, type: 'DELIVERY_NOTE' }],
      'action': 'view'
    }).then((res: any) => {
      console.log('res', res)
      $printRowsSingle(res.data, $form.values.form.templatePath, 'outerBoxId')
    })
  })
}

const $printRowsSingle = async (rows: any, path: any, key: any) => {
  const ids = rows.map((item: any) => item[key]).join(',')
  const params = encodeURIComponent(`ids=${ids}`)
  bus.$emit('TagManage')
  $openPrint(path, params)
}

// 批量打印内箱
const $batchPrintInner = async ($self: any, $message: any, $queryEngine:any) => {
  const rows = $self
    .query('TagInnertable')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (!rows.length) {
    return $message.warning($t('buyerDeliveryOrder.prompt6'))
  }
  let templatePathList = rows.map(item => item.templatePath)
  let setTemplatePath = [...new Set(templatePathList)]
  if (setTemplatePath.length != 1) {
    return app.$message.warning($t('buyerDeliveryOrder.prompt7'))
  }

  let printParams = rows.map((item:any) => {
    return {
      innerBoxId: item.innerBoxId
    }
  })
  if (vendor()) {
    let res = await $queryEngine.request
      .baseRequest({
        type: 'TagInnerBox',
        action: 'print',
        lang: 'zh-cn',
        query: {
          '*': {}
        },
        payload: printParams
      })
    if (res.data) {
      $printRows(rows, 'inner', 'innerBoxId', setTemplatePath[0])
    }
  } else {
    $printRows(rows, 'inner', 'innerBoxId', setTemplatePath[0])
  }
}

// 批量打印外箱
const $batchPrintOuter = async ($self: any, $message: any, $queryEngine: any) => {
  const rows = $self
    .query('TagOutertable')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (!rows.length) {
    return $message.warning($t('buyerDeliveryOrder.prompt6'))
  }
  let templatePathList = rows.map(item => item.templatePath)
  let setTemplatePath = [...new Set(templatePathList)]
  if (setTemplatePath.length != 1) {
    return app.$message.warning($t('buyerDeliveryOrder.prompt8'))
  }
  let printParams = rows.map((item: any) => {
    return {
      outerBoxId: item.outerBoxId
    }
  })
  if (vendor()) {
    let res = await $queryEngine.request
      .baseRequest({
        type: 'TagOuterBox',
        action: 'print',
        lang: 'zh-cn',
        query: {
          '*': {}
        },
        payload: printParams
      })
    if (res.data) $printRows(rows, 'outer', 'outerBoxId', setTemplatePath[0])
  } else {
    $printRows(rows, 'outer', 'outerBoxId', setTemplatePath[0])
  }
}

// 打印
const $printRows = async (rows: any, type: any, key: any, templatePath: any) => {
  const ids = rows.map((item: any) => item[key]).join(',')
  const params = encodeURIComponent(`ids=${ids}`)
  bus.$emit('TagManage')
  $openPrint(templatePath, params)
}

// 打开pdf
const $openPrint = (pdfName: any, params: any) => {
  const xml = encodeURIComponent(pdfName)
  const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

// 解绑函数
const unbindRequest = (payload: any, $queryEngine: any, $form, $message, $bus) => {
  $queryEngine.request
    .baseRequest({
      loading: true,
      type: 'TagOuterBox',
      action: 'unbound',
      lang: 'zh-cn',
      query: {
        '*': {}
      },
      payload: payload
    })
    .then(() => {
      app.$message.success($t('common.success'))
      bus.$emit('TagManage')
    })
}

// 批量解绑
const batchUnbind = ($form: any, $queryEngine: any, $message, $bus) => {
  let selects = $form
    .query('TagOutertable')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.length < 1) {
    return app.$message.warning($t('barcodeManageNew.selectUnbindData'))
  }

  if (selects.some((item: any) => item.boundFlag === 'N')) {
    return app.$message.warning($t('barcodeManageNew.selectBindData'))
  }

  let payload = selects.map((row: any) => {
    return { outerBoxId: row.outerBoxId }
  })

  unbindRequest(payload, $queryEngine, $form, $message, $bus)
}

// 打开绑定内箱弹窗
const $bindInnerBox = (row, $form: any) => {
  $form.query('barcodeRelationDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('barcodeRelation').take().setComponentProps({
      flag: 'bind',
      showType: 'readOnly',
      row: row
    })
  })
}

// 创建外箱
const $addOne = ($form: any, $queryEngine: any) => {
  $form.query('TagOuterBox').get('data').detailDialogStep = 1
  $form.query('TagOuterBox').get('data').currentDeliveryNoteDetail = {}
  $form.query('*.detailDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('deliveryOrderTable').take().setComponentProps({ loading: true })
  })
  // 获取送货单行明细
  $queryEngine.request
    .baseRequest({
      type: 'DeliveryNoteVendor',
      lang: 'zh-cn',
      query: {
        "fileUploads": {
          "*": {}
        },
        "*": {},
        "detailList": {
          "orderDetailId": {
            "orderId": {
              "*": {}
            },
            "*": {}
          },
          "deliveryNoticeDetailId": {
            "deliveryNoticeId": {
              "*": {}
            },
            "*": {}
          },
          "fileUploads": {
            "*": {}
          },
          "*": {}
        }
      },
      payload: {
        filter: {
          deliveryNoteId: $attrs.params.row.deliveryNoteId
        }
      },
      action: 'query'
    })
    .then((res: any) => {
      let data = res.originalData?.ref?.DeliveryNoteDetailVendor || {}
      let orderDetailNode = res.originalData?.ref?.OrderDetailVendor || {}
      let orderNode = res.originalData?.ref?.OrderVendor || {}
      let DeliveryNoteVendor = res.originalData?.ref?.DeliveryNoteVendor || {}

      let list = []
      for (let k in data) {
        let deliveryNoteResult = data[k]
        let vendorName = DeliveryNoteVendor[deliveryNoteResult.deliveryNoteId].vendorName
        let orderDetailResult = orderDetailNode[deliveryNoteResult.orderDetailId]
        let orderNodeResult = orderNode[orderDetailResult.orderId]

        let all = {
          deliveryNoteDetailId: deliveryNoteResult.deliveryNoteDetailId,
          lineNum: deliveryNoteResult.lineNum,
          deliveryNumber: $attrs.params.row.deliveryNumber,
          deliveryLine: deliveryNoteResult.lineNum,
          orderDetailId: deliveryNoteResult.orderDetailId,
          orderNumber: orderNodeResult.orderNumber,
          orderLineNum: orderDetailResult.lineNum,
          deliveryQuantity: deliveryNoteResult.deliveryQuantity,
          deliveryNoticeNumber: deliveryNoteResult.deliveryNoticeNumber,
          deliveryNoticeLineNum: deliveryNoteResult.deliveryNoticeLineNum,
          materialCode: orderDetailResult.materialCode,
          materialName: orderDetailResult.materialName,
          materialId: orderDetailResult.materialId,
          categoryCode: orderDetailResult.categoryCode,
          categoryName: orderDetailResult.categoryName,
          categoryId: orderDetailResult.categoryId,
          vendorName: vendorName,
          vendorId: orderNodeResult.vendorId,
          vendorCode: orderNodeResult.vendorCode,
          unit: orderDetailResult.unit,
          type: 'DELIVERY_NOTE'

        }
        console.log('!!!!!!!!!  集成的送货单明细行数据  :', all)
        list.push(all)
      }
      $form.query('TagOuterBox').get('data').DeliveryNoteCache = list
      setTimeout(() => {
        $form.query('deliveryOrderTable').take().setValue(list)
        $form.query('*.detailDialog.form').take((field: any) => {
          field.reset()
        })
        $form.query('deliveryOrderTable').take().setComponentProps({ loading: false })
      })
    })
}

const $toPrev = $form => {
  $form.query('TagOuterBox').get('data').detailDialogStep = 1
}

// 设置创建外箱表单
const $setDeliveryForm = async ($message, $form, $queryEngine, $values) => {
  let row = $form.query('TagOuterBox').get('data').currentDeliveryNoteDetail
  if (!row.deliveryNoteDetailId) return $message.warning($t('buyerDeliveryOrder.prompt9'))
  let params = {
    ...row,
    deliveryNumber: $attrs.params.row.deliveryNumber,
    deliveryLine: row.lineNum,
    deliveryQuantity: row.deliveryQuantity || 0,
    generateTotalQuantity: row.generateTotalQuantity || 0,
    maxBoxQuantity: undefined
  }

  await $getTotalQuantity($queryEngine, $form, params)
  await $getMaxBoxQuantity($queryEngine, $form, params)
  await $getMaterialByQuick($queryEngine, $form, params)
  $setBoundInnerBoxFlag($form, params, $values)
  await $getPrintTemplateList($queryEngine, $form, params)
  $form.query('*.detailDialog.form').take((field: any) => {
    field.setValue({
      ...params
    })
  })
  $form.query('TagOuterBox').get('data').detailDialogStep = 2
}

const $setBoundInnerBoxFlag = ($form:any, params:any, $values:any) => {
  let current = $values.DeliveryTable.find((item:any) => {
    return item.deliveryLine == params.deliveryLine
  })
  if (current?.boundInnerBoxFlag) {
    params.hasBoundInnerBoxFlag = true
    params.boundInnerBoxFlag = current.boundInnerBoxFlag
  }
}

// 获取已生成数量
const $getTotalQuantity = async ($queryEngine: any, $form: any, params: any) => {
  let totalQuantityList = await $queryEngine.request.baseRequest({
    type: 'TagOuterBox',
    lang: 'zh-cn',
    loading: true,
    query: {
      '*': {}
    },
    payload: [
      {
        deliveryNumber: $attrs.params.row.deliveryNumber,
        deliveryLine: params.deliveryLine
      }
    ],
    action: 'queryGenerateTotalQuantity'
  })
  if (totalQuantityList.data && totalQuantityList.data.length) {
    let data = totalQuantityList.data[0]
    params.generateTotalQuantity = data.generateTotalQuantity || 0
    params.generateQuantity = (+params.deliveryQuantity - +data.generateTotalQuantity) ?? null
  }
  return params
}

// 获取外箱最大包装量
const $getMaxBoxQuantity = async ($queryEngine: any, $form: any, params: any) => {
  let res = await app.$http({
    url: '/api-base/material/materialItem/ceeaGet',
    method: 'GET',
    params: { id: params.materialId },
    loading: true
  })
  if (res.data) {
    let data = res.data || {}
    params.maxBoxQuantity = data?.materialItem?.outboxMinPackagingQuantity || undefined
    if (params.maxBoxQuantity > 0) params.maxBoxQuantityDisabled = true
  }
}

// 获取打印模板数据
const $getPrintTemplateList = ($queryEngine: any, $form: any, val: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagTemplateRelation',
    'lang': 'zh-cn',
    loading: true,
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
      console.log('!!!!!!!!! getPrintTemplateList    :', res.data)
    }
  })
}

// 物料快查
const $getMaterialByQuick = async ($queryEngine: any, $form: any, params: any) => {
  // val.maxBoxQuantity = val.minimumPackagingQuantity || null
  $form.query('TagOuterBox').get('data').tagRuleList = []
  let res = await $queryEngine.request
    .baseRequest({
      type: 'TagGenerateRuleConfig',
      lang: 'zh-cn',
      loading: true,
      query: {
        '*': {}
      },
      payload: [
        {
          materialCode: params.materialCode,
          categoryCode: params.categoryCode,
          ruleType: 'OUTER'
        }
      ],
      action: 'listByMaterialAndCategory'
    })

  if (res.data && res.data.length) {
    res.data.forEach((item: any) => {
      item.label = item.tagRuleName
      item.value = item.tagGenerateRuleId
      if (item.defaultFlag === 'Y') {
        params.tagGenerateRuleId = item.tagGenerateRuleId
        params.tagRuleCode = item.tagRuleCode
        params.tagRuleName = item.tagRuleName
        params.tagType = item.tagType
      }
    })
    $form.query('TagOuterBox').get('data').tagRuleList = res.data
  }
  return params
}

// 获取内箱条码列表
const $getInnerBox = ($queryEngine, row: any, $message, $form, $bus) => {
  $form.query('tagContainer').get('data').currentOuterBox = row
  $form.query('TagInnertable').take().setComponentProps({ loading: true })
  $bus.$emit('TagInnerBox')
}

const $clearOuterBox = $form => {
  $form.query('TagOutertable').take()?.setValue([])
}
const $clearInnerBox = $form => {
  $form.query('TagInnertable').take()?.setValue([])
}

// 获取外箱条码列表
const $getOuterBox = ($queryEngine:any, $message, $form, $bus) => {
  let row = $form.query('tagContainer').get('data').currentDeliveryLine
  if (!row) return
  $form.query('tagContainer').get('data').showInnerBox = row.boundInnerBoxFlag == 'Y'
  $form.query('TagOutertable').take().setComponentProps({ loading: true })
  //

  // $bus.$emit('TagOutertable')
  bus.$emit('TagOutertable' + $attrs.params.row.deliveryNoteId)
}

// @ts-ignore
const scope = {
  app,
  $t,
  $attrs,
  $getOuterBox,
  $getInnerBox,
  $addOne,
  $printRows,
  $openPrint,
  $review,
  $getMaterialByQuick,
  $setDeliveryForm,
  $toPrev,
  $batchPrintOuter,
  $bindInnerBox,
  batchUnbind,
  $clearOuterBox,
  $clearInnerBox,
  $abandonOne,
  innerAndOutTemplate,
  $vendor: vendor,
  unbindRequest,
  $setBoundInnerBoxFlag,
  $batchPrintInner,
  $getPrintTemplateList,
  // @ts-ignore
  $status: $attrs.params?.status
}
const components = {
  DetailDialog,
  barcodeRelation
}
// @ts-ignore
const schema = defineSchemas({
  barcodeRelationDialog: {
    type: 'void',
    title: "{{$t('orderMod.bindInnerBox')}}",
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'tagmanage-barcodeRelation-dialog',
      'close-on-click-modal': false,
      destroyOnClose: true,
      size: 'large',
      footer: false,
      beforeClose: expression(`(done, type) => {
        if ( type === 'ok') {
          done()
          $clearInnerBox($form)
        } else {
          done()
          }
        }
      `)
    },
    properties: {
      barcodeRelation: {
        type: 'void',
        'x-component': 'barcodeRelation',
        'x-component-props': {}
      }
    }
  },
  TagOuterBox: {
    type: 'void',
    'x-data': {
      DeliveryNoteCache: [],
      currentDeliveryNoteDetail: {}, // 创建外箱弹窗选择的送货单明细行
      detailDialogStep: 1,
      readonly: false,
      tagRuleList: [],
      printTemplateList: [],
      okBtnLoading: false
    },
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container-aside tag-manage-container',
      direction: 'row'
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'queryAll',
          immediate: true,
          ready: expression(`() => {

            return $attrs.params.row.deliveryNumber
          }`),
          transformRequest: expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              let materialCode = $form.query('materialCodeQuickSearch').take().value || undefined
              let obj = {
                "deliveryNumber": $attrs.params.row.deliveryNumber
              }
              if(materialCode){
                obj.materialCode = materialCode
              }
              data.payload = [obj]
              return data
            }`),
          onSuccess: expression(`(res) => {
            $form.query('DeliveryTable').take().setComponentProps({ loading: false })
            setTimeout(()=>{
              // 重新选中左侧DeliveryTable选中的送货单行
              let row = $form.query('tagContainer').get('data').currentDeliveryLine

              if(row){
                let current = $values.DeliveryTable.find((item)=>{
                  return item.deliveryLine == row.deliveryLine
                })
                $form.query('DeliveryTable').take().componentProps.componentInstance.setCurrentRow(current)

                $getOuterBox({},$message,$form,$bus)
                }else{
                  // 清空外箱条码
                  $clearOuterBox($form)
                  // 清空内箱条码
                  $clearInnerBox($form)
                }

                // $bus.$emit('TagOutertable')
            },300)




          }`)
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'TagManage',
          '@listener': expression(`() => {
            $form.query('barcodeRelationDialog').take().setComponentProps({ visible: false })
            $form.query('DeliveryTable').take().setComponentProps({ loading: true })
            console.log('eventName $queryEngine',$queryEngine)
            $queryEngine.state.paginationManagement.refresh()

          }`)
        }
      },

      tagContainer: {
        type: 'void',
        'x-decorator': 'el-aside',
        'x-decorator-props': {
          style: {
            'padding-top': '0',
            width: '50%'
          }
        },
        'x-component': 'HTMLElement',
        'x-component-props': {
          style: {
            height: '100%'
          }
        },
        'x-data': {
          showInnerBox: false,
          currentDeliveryLine: {}, // 左侧DeliveryTable选中的送货单行号
          currentOuterBox: {}
        },
        properties: {
          buttonArea: {
            type: 'void',

            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              class: 'buttonArea',
              style: {
                'display': 'flex',
                'justify-content': 'space-between',
                'margin-bottom': '16px'
              }
            },

            properties: {
              add: {
                type: 'void',
                title: i18nExpression('buyerDeliveryOrder.createOuterBox'), // 创建外箱条码
                'x-component': 'RButton',
                'x-component-props': {
                  style: {
                    display: `{{($status === 'CREATE' && $vendor())?'block':'none'}}`,
                    height: '28px'
                  },
                  type: 'primary',
                  '@click': expression(`() => $addOne($form,$queryEngine)`)
                }
              },
              quickSearchWrap: {
                type: 'void',
                // 'x-decorator': 'div',
                // 'x-decorator-props': {
                //   style: {
                //     display: 'flex'
                //   }
                // },
                'x-component': 'FormGrid',
                'x-component-props': {
                  maxColumns: 2,
                  columnGap: 8,
                  rowGap: 0,
                  style: {
                    display: 'flex',
                    gap: '0px 8px !important'

                  }
                },
                properties: {
                  lable: {
                    type: 'void',
                    'x-decorator': 'span',
                    'x-decorator-props': {
                      style: {
                        'font-size': '12px'
                      }
                    },
                    'x-content': $t('buyerDeliveryOrder.materialName')
                  },
                  materialCodeQuickSearch: {
                    type: 'string',
                    title: $t('buyerDeliveryOrder.materialName'), // 物料名称
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      width: '200px',
                      showKey: 'materialName',
                      propKey: 'materialCode',
                      name: `{{$vendor()?'purchase_catalog_material':'scc_base_material_item'}}`,
                      '@close-quicksearch': expression(`
                      ()=>{
                        $form.query('DeliveryTable').take().setComponentProps({ loading: true })
                        $queryEngine.state.paginationManagement.refresh()
                      }


                    `)
                    }
                  }

                }
              }

            }
          },

          DeliveryTable: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              loading: true,
              class: 'table-view-vxe-table',
              style: 'flex: 1;height:92%',
              preColumns: 'seq',
              pagination: false,
              '@current-change': expression(`({ row }) => {

                $form.query('tagContainer').get('data').currentDeliveryLine = row
                $getOuterBox($queryEngine,$message,$form,$bus)
                $clearInnerBox($form)
              }`)
            },
            properties: generateXindexInOrder({
              // deliveryNoteId: { // 单据ID - 主键
              //   type: 'string',
              //   'x-hidden': true,
              //   'x-query-engine-primary-key': true
              // },
              deliveryNumber: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.deliveryNumber')}}", // 送货单号
                  minWidth: 120
                },
                'x-query-engine-primary-key': true
              },
              deliveryLine: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.deliveryLine')}}", // 送货单行号
                  minWidth: 120
                }
              },
              materialName: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.materialName')}}", // 物料名称
                  minWidth: 120
                }
              },
              materialCode: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.materialCode')}}", // 物料编码
                  minWidth: 120
                }
              },
              unit: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.unit')}}", // 单位
                  minWidth: 120
                }
              },
              deliveryQuantity: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.deliveryQuantity')}}", // 送货数量
                  minWidth: 120
                }
              },
              boxedMaterialQuantity: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.boxedMaterialQuantity')}}", // 已装箱物料数量
                  minWidth: 120,
                  'title-prefix': { content: `①${$t('buyerDeliveryOrder.prompt10')}
②${$t('buyerDeliveryOrder.prompt11')}` }
                }
              },
              generatedOutBoxTagQuantity: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.generatedOutBoxTagQuantity')}}", // 已生成外箱条码数量
                  minWidth: 120,
                  'title-prefix': { content: $t('buyerDeliveryOrder.prompt12') }
                }
              },
              boundInnerBoxFlag: {
                type: 'string',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                },
                'x-render-table-column': {
                  title: "{{$t('orderMod.boundInnerBoxFlag')}}", // 是否绑定内箱
                  minWidth: 120
                }
              },
              boundInnerBoxQuantity: {
                type: 'string',
                'x-render-table-column': {
                  title: "{{$t('orderMod.boundInnerBoxQuantity')}}", // 累计已绑定内箱数量
                  minWidth: 120,
                  'title-prefix': { content: $t('buyerDeliveryOrder.prompt13') }
                }
              }
            })
          }
        }
      },
      rightContainer: {
        type: 'void',
        'x-decorator': 'el-container',
        'x-decorator-props': {
          class: 'flex-container',
          direction: 'vertical'
        },
        'x-component': 'HTMLElement',
        'x-component-props': {
          style: {
            height: '100%'
          }
        },
        properties: {
          TagOuterBox: {
            type: 'void',
            'x-decorator': 'QueryEngine',
            'x-query-engine': {
              service: 'sup-ce',
              actions: {
                paginationQuery: {
                  transformRequest: expression(`(data, headers) => {
                      data.type = 'TagOuterBox'
                      data.query = {
                        '*': {}
                      }
                      data.payload.filter = {
                        "deliveryNumber": {
                            "eq": $form.query('tagContainer').get('data').currentDeliveryLine.deliveryNumber
                          },
                          "deliveryLine": {
                            "eq": $form.query('tagContainer').get('data').currentDeliveryLine.deliveryLine
                          },
                      }
                      data.payload.page.sort = 'creationDate desc,tagNo desc'
                      return data
                    }`),
                  onSuccess: expression(`(res) => {
                      $form.query('TagOutertable').take().setComponentProps({ loading: false })

                      setTimeout(()=>{
                        console.log('!!!TagOuterBox $queryEngine',$queryEngine)
                        // 重新选中之前的外箱条码
                        let row = $form.query('tagContainer').get('data').currentOuterBox
                        if(row){
                          let current = $values.TagOutertable.find((item)=>{
                            return item.outerBoxId == row.outerBoxId
                          })
                          $form.query('TagOutertable').take().componentProps.componentInstance.setCurrentRow(current)
                          $getInnerBox($queryEngine,row,$message,$form,$bus)
                        }
                      },300)


                    }`)
                }
              }
            },
            properties: {
              // 事件总线
              bus: {
                type: 'void',
                'x-component': 'BusEvent',
                'x-component-props': {
                  // eventName: 'TagOutertable',
                  eventName: `{{'TagOutertable' + $attrs.params.row.deliveryNoteId}}`,
                  '@listener': expression(`() => {
                    $queryEngine.state.paginationManagement.refresh()


                  }`)
                }
              },
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-component-props': {
                  style: 'margin-bottom: 16px;height:28px;'
                },
                properties: {
                  print: {
                    type: 'void',
                    'x-content': i18nExpression('buyerDeliveryOrder.batchPrintOuterBox'), // 批量打印
                    'x-reactions': expression(`field => {
                        let flag = $values.TagOutertable && $values.TagOutertable.length
                        field.visible = flag && ['CREATE','DELIVERED'].includes($status)
                      }`),
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {$batchPrintOuter($self,$message,$queryEngine)}`)
                    }
                  },
                  batchUnbind: {
                    type: 'void',
                    'x-content': i18nExpression('buyerDeliveryOrder.batchUnbinding'),
                    'x-component': 'RButton',
                    'x-reactions': expression(`field => {
                        let flag = $values.TagOutertable && $values.TagOutertable.length
                        field.visible = ['CREATE','CANCELLED'].includes($status)  && $vendor() && flag
                      }`),
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {
                        console.log('解绑')
                        batchUnbind($form, $queryEngine,$message,$bus)
                      }`)
                    }
                  }
                }
              },
              TagOutertable: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  style: {
                    flex: 1,
                    height: `{{$form.query('tagContainer').get('data').showInnerBox? '42%': '92%'}}`
                  },
                  preColumns: 'checkbox,seq',
                  '@current-change': expression(`({ row }) => {
                    $getInnerBox($queryEngine,row,$message,$form,$bus)
                  }`)
                },
                properties: generateXindexInOrder({
                  outerBoxId: {
                    type: 'number',
                    'x-hidden': true,
                    'x-query-engine-primary-key': true
                  },
                  outerBoxCode: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.outerBoxCode')}}", // 外箱条码
                      minWidth: 120
                    }

                  },
                  relationMaterialQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.relationMaterialQuantity')}}", // 关联物料数量
                      minWidth: 120
                    }
                  },
                  tagNo: {
                    type: 'string',
                    'x-query-engine-sort': 'desc',
                    'x-render-table-column': {
                      title: $t('buyerDeliveryOrder.prompt14'), // 生成外箱条码数量
                      minWidth: 160,
                      'title-prefix': { content: $t('buyerDeliveryOrder.prompt15') } // 本次生成外箱条码时生成的条码张数，以及对应第几张
                    }
                  },
                  generateQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.generateQuantity')}}", // 本次生成数量
                      minWidth: 120,
                      'title-prefix': { content: '该送货单明细行本次生成外箱条码的物料数量' }
                    }
                  },
                  // 已绑定内箱数量
                  boundInnerBoxQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.boundInnerBoxQuantity2')}}", // 已绑定内箱数量
                      minWidth: 120,
                      'title-prefix': { content: $t('buyerDeliveryOrder.prompt17') }
                    }
                  },
                  // 待绑定内箱物料数量
                  leftMaterialQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.leftMaterialQuantity')}}", // 待绑定内箱物料数量
                      minWidth: 120,
                      'title-prefix': { content: $t('buyerDeliveryOrder.prompt18') }
                    }
                  },
                  boundFlag: {
                    type: 'string',
                    'x-component': 'Select',
                    enum: [
                      {
                        label: $t('buyerDeliveryOrder.bound'),
                        value: 'Y'
                      },
                      {
                        label: $t('buyerDeliveryOrder.unbound'),
                        value: 'N'
                      }
                    ],
                    'x-render-table-column': {
                      title: "{{$t('orderMod.boundFlag')}}", // 绑定状态
                      minWidth: 120
                    }
                  },
                  deliveryNoteStatus: {
                    type: 'string',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'DELIVERY_NOTE_STATUS'
                    },
                    'x-render-table-column': {
                      title: $t('buyerDeliveryOrder.deliveryStatus'), // 送货状态
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
                      title: $t('buyerDeliveryOrder.barcodeStatus'), // 条码状态
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
                      title: $t('buyerDeliveryOrder.barcodePrinting'), // 条码打印
                      minWidth: 120
                    }
                  },
                  creationDate: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('common.creationDate')}}", // 创建日期
                      minWidth: 120
                    },
                    'x-query-engine-sort': 'desc'
                  },
                  operation: {
                    type: 'void',
                    'x-visible': '{{!$readOnly  && $vendor()}}',
                    'x-render-table-column': {
                      title: i18nExpression('common.operation'),
                      width: 160,
                      fixed: 'right',
                      sortable: false
                    },
                    properties: {
                      layout: {
                        type: 'void',
                        'x-component': 'Space',
                        properties: {
                          unbind: {
                            type: 'void',
                            title: `{{$t('orderMod.unbind')}}`, // 解绑
                            'x-component': 'TableButton',
                            'x-reactions': changeFieldVisibleByDeps(
                              ['.boundFlag'],
                              `$deps[0] === 'Y'  && ['CREATE','CANCELLED'].includes($status)`,
                            ),
                            'x-component-props': {
                              showPopconfirm: true,
                              title: $t('buyerDeliveryOrder.prompt19'),
                              '@confirm': expression(
                                '({ row }) => unbindRequest([{outerBoxId:row.outerBoxId}], $queryEngine, $form,$message,$bus)',
                              )
                            }
                          },
                          abandon: {
                            type: 'void',
                            title: "{{$t('orderMod.abandon')}}", // '作废'
                            'x-component': 'TableButton',
                            'x-reactions': changeFieldVisibleByDeps(
                              ['.boundFlag', '.status'],
                              `$deps[0] !== 'Y' && $deps[1] !== 'N'   && ['CREATE'].includes($status)`,
                            ),
                            'x-component-props': {
                              showPopconfirm: true,
                              title: $t('buyerDeliveryOrder.prompt20'),
                              '@confirm': expression(
                                '({ row }) => $abandonOne([row], $queryEngine, $message,$form,$bus)',
                              )
                            }
                          },
                          bindInnerBox: {
                            type: 'void',
                            title: "{{$t('orderMod.bindInnerBox')}}", // 绑定内箱
                            'x-component': 'TableButton',
                            'x-reactions': changeFieldVisibleByDeps(
                              ['.leftMaterialQuantity', '.status'],
                              `$deps[0] > 0 &&  $deps[1] === 'Y'  && ['CREATE'].includes($status) && $form.query('tagContainer').get('data').showInnerBox`,
                            ),
                            'x-component-props': {
                              type: 'text',
                              '@click': expression('({ row }) => $bindInnerBox(row,$form)')
                            }
                          }
                        }

                      } }

                  }
                })
              }
            }
          },
          TagInnerBox: {
            type: 'void',
            'x-decorator': 'QueryEngine',
            'x-decorator-props': {
              style: {
                display: `{{$form.query('tagContainer').get('data').showInnerBox?'block':'none'}}`
              }
            },
            'x-query-engine': {
              service: 'sup-ce',
              actions: {
                paginationQuery: {
                  transformRequest: expression(`(data, headers) => {
                    data.type = 'TagInnerBox'
                      data.query = {
                        '*': {}
                      }
                      data.payload.filter = {
                        "outerBoxId": {
                          "eq": $form.query('tagContainer').get('data').currentOuterBox.outerBoxId
                        },
                      }
                    data.payload.page.sort = 'creationDate desc,tagNo desc'
                      return data
                    }`),
                  onSuccess: expression(`(res) => {
                      $form.query('TagInnertable').take().setComponentProps({ loading: false })
                    }`)
                }
              }
            },
            properties: {
              // 事件总线
              bus: {
                type: 'void',
                'x-component': 'BusEvent',
                'x-component-props': {
                  eventName: 'TagInnerBox',
                  '@listener': expression(`() => {
                    setTimeout(()=>{
                      let current = $form.query('tagContainer').get('data').currentOuterBox || {}
                      if(current.outerBoxId){
                        $queryEngine.state.paginationManagement.refresh()
                      }
                    })

                  }`)
                }
              },
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-component-props': {
                  style: 'margin-bottom: 16px;height:28px;'
                },
                properties: {
                  printInner: {
                    type: 'void',
                    'x-reactions': expression(`field => {
                        let flag = $values.TagInnertable && $values.TagInnertable.length
                        field.visible = flag && ['CREATE','DELIVERED'].includes($status)
                      }`),
                    title: i18nExpression('buyerDeliveryOrder.batchPrintInnerBox'), // 批量打印内箱
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {$batchPrintInner($self,$message,$queryEngine)}`)
                    }
                  }
                }
              },
              TagInnertable: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  style: 'flex: 1;height:42%',
                  preColumns: 'checkbox,seq'
                },

                properties: generateXindexInOrder({
                  innerBoxCode: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.innerBoxCode')}}", // 内箱条码
                      minWidth: 120
                    }

                  },
                  materialName: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.materialName')}}", // 物料名称
                      minWidth: 120
                    }
                  },
                  materialCode: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.materialCode')}}", // 物料编码
                      minWidth: 120
                    }
                  },
                  categoryName: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.categoryName')}}", // 品类名称
                      minWidth: 120
                    }
                  },
                  categoryCode: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.categoryCode')}}", // 品类编码
                      minWidth: 120
                    }
                  },
                  vendorCode: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.vendorCode')}}", // 供应商编码
                      minWidth: 120
                    }
                  },
                  vendorName: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.vendorName')}}", // 供应商名称
                      minWidth: 120
                    }
                  },
                  unit: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.unit')}}", // 单位
                      minWidth: 120
                    }
                  },
                  relationMaterialQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.relationMaterialQuantity')}}", // 关联物料数
                      minWidth: 120,
                      'title-prefix': { content: $t('buyerDeliveryOrder.prompt21') }
                    }
                  },
                  tagNo: {
                    type: 'string',
                    'x-query-engine-sort': 'desc',
                    'x-render-table-column': {
                      title: $t('buyerDeliveryOrder.innerBoxBarcodesNum'), // 生成内箱条码数量
                      minWidth: 160,
                      'title-prefix': { content: $t('buyerDeliveryOrder.prompt22') } // 本次生成内箱条码时生成的条码张数，以及对应第几张
                    }
                  },
                  materialQuantity: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.materialQuantity')}}", // 物料数量
                      minWidth: 120
                    }
                  },
                  tagRuleName: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.tagRuleName')}}", // 条码生成规则
                      minWidth: 120
                    }
                  },
                  tagType: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.tagType')}}", // 条码样式
                      minWidth: 120
                    }
                  },
                  printCount: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('orderMod.printCount')}}", // 打印次数
                      minWidth: 120
                    }
                  },
                  status: {
                    type: 'string',
                    'x-component': 'Select',
                    enum: [
                      {
                        label: $t('buyerDeliveryOrder.takeEffect'),
                        value: 'Y'
                      },
                      {
                        label: $t('buyerDeliveryOrder.abandon'),
                        value: 'N'
                      }
                    ],
                    'x-render-table-column': {
                      title: "{{$t('orderMod.tagStatus')}}", // 条码状态
                      minWidth: 120
                    }
                  },
                  boundFlag: {
                    type: 'string',
                    'x-component': 'Select',
                    enum: [
                      {
                        label: $t('buyerDeliveryOrder.bound'),
                        value: 'Y'
                      },
                      {
                        label: $t('buyerDeliveryOrder.unbound'),
                        value: 'N'
                      }
                    ],
                    'x-render-table-column': {
                      title: "{{$t('orderMod.boundFlag')}}", // 绑定状态
                      minWidth: 120
                    }
                  },

                  printedFlag: {
                    type: 'string',
                    'x-component': 'Select',
                    enum: [
                      {
                        label: $t('buyerDeliveryOrder.printed'),
                        value: 'Y'
                      },
                      {
                        label: $t('buyerDeliveryOrder.unprinted'),
                        value: 'N'
                      }
                    ],
                    'x-render-table-column': {
                      title: "{{$t('orderMod.printedFlag')}}", // 打印状态
                      minWidth: 120
                    }
                  },
                  creationDate: {
                    type: 'string',
                    'x-render-table-column': {
                      title: "{{$t('common.creationDate')}}", // 创建日期
                      minWidth: 120
                    },
                    'x-query-engine-sort': 'desc'
                  }
                })
              }
            }
          }
        }
      },
      detailDialog: {
        ...DetailDialog
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="TagOuterBox"
  />
</template>
<style lang="scss" >
.tag-manage-container{
  .vxe-cell--title {
    order: 1
  }

  .vxe-cell-help-icon {
    order: 2
  }

  .vxe-cell--sort {
    order: 3
  }

  .buttonArea{
    .the_quick_search .quick-search-btn{
      height: 22px;
      top: 4px;
    }
  }
}
.tagmanage-barcodeRelation-dialog {

  .render-form-container__fixed-footer {
    position: relative;
  }
  .render-form-container__fixed-footer-content {
    position: absolute;
  }
}
</style>
