<!-- 内外箱关联 -->
<script setup lang="ts">
// @ts-ignore
import { defineSchemas, generateXindexInOrder, expression, i18nExpression, changeFieldVisibleByDeps } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import editEngine from './edit-engine'
// @ts-ignore
import { getDictItem } from '@/api/common'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import viewInnerBoxDialog from 'lib@/compositionEngine/barcodeNewSupplier/barcodeRelation/viewInnerBoxDialog'
// @ts-ignore
import { systemUrl } from '@/config/sysConfig'
const { app, emitTabAdd, t, confirmMessage, vendor: $vendor } = usePageHelper()

// 获取内外箱模板
const innerAndOutTemplate = (async () => {
  let inner = await getDictItem('TAG_PRINT_TEMPLATEP_INNER')
  let outer = await getDictItem('TAG_PRINT_TEMPLATEP_OUTER')

  return {
    inner: adaptDictData(inner.data, 'dict'),
    outer: adaptDictData(outer.data, 'dict')
  }
})()

// 查看内箱
const viewInnerBox = (row: any, $form: any, $queryEngine: any) => {
  $form.query('.innerBarDialog').take().setComponentProps({
    visible: true
  })

  $queryEngine.request.baseRequest({
    'type': 'TagInnerBox',
    'action': 'query',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      filter: {
        outerBoxId: { eq: row.outerBoxId }
      }
    }
  }).then((res: any) => {
    $form.query('*.innerBarDialog.innerBarTable').take().setValue(res.data)
  })
}

// 解绑函数
const unbindRequest = (payload: any, $queryEngine: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagOuterBox',
    'action': 'unbound',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': payload
  }).then(() => {
    app.$message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 批量解绑
const batchUnbind = ($form: any, $queryEngine: any) => {
  let selects = $form.query('.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (selects.length < 1) {
    return app.$message.warning(t('barcodeManageNew.selectUnbindData'))
  }

  if (selects.some((item: any) => item.boundFlag === 'N')) {
    return app.$message.warning(t('barcodeManageNew.selectBindData'))
  }

  let payload = selects.map((row: any) => {
    return { outerBoxId: row.outerBoxId }
  })

  unbindRequest(payload, $queryEngine)
}

// 解绑
const unbindBox = async (row: any, $queryEngine: any) => {
  // 确定解绑该行数据
  const isConfirm = await confirmMessage(t('barcodeManageNew.sureUnbindData'))
  if (isConfirm !== 'confirm') return
  unbindRequest([{ outerBoxId: row.outerBoxId }], $queryEngine)
}

// 绑定
const bindBox = async (row: any) => {
  emitTabAdd({
    component: editEngine,
    params: {
      flag: 'bind',
      row: row,
      type: 'DELIVERY_NOTE'
    },
    title: t('barcodeManageNew.bindinnerBox') + row.outerBoxCode,
    name: 'editEngine'
  })
}

// 打开pdf
const openPrint = (pdfName: any, params: any) => {
  let origin = systemUrl
  const xml = encodeURIComponent(pdfName)
  const url = `${origin}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

const printInnerBoxByOuterId = async (id:string, $queryEngine: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagInnerBox',
    'action': 'query',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      filter: {
        outerBoxId: { eq: id }
      }
    }
  }).then((res: any) => {
    let data = res.data || []
    let templatePathList = data.map(item => item.templatePath)
    let setTemplatePath = [...new Set(templatePathList)]
    if (setTemplatePath.length == 1) {
      printInnerOrOuterBox('inner', id, $queryEngine, setTemplatePath[0])
    } else {
      // 内箱存在多个模板，无法打印
      app.$message.warning(t('cusEntry.supplement20250211.message4'))
    }
  })
}

// 打印内箱或者外箱条码
const printInnerOrOuterBox = async (type: string, id: string, $queryEngine: any, templatePath: any) => {
  let params = encodeURIComponent(`ids=${id}`)
  if (type === 'inner') params = encodeURIComponent(`outerBoxId=${id}`)

  if ($vendor()) {
    $queryEngine.request.baseRequest({
      'type': type === 'outer' ? 'TagOuterBox' : 'TagInnerBox',
      'lang': 'zh-cn',
      'query': {
        '*': {}
      },
      'payload': [{ outerBoxId: id }],
      'action': 'print'
    }).then((res: any) => {
      if (res.data) openPrint(templatePath, params)
    })
  } else {
    openPrint(templatePath, params)
  }
}

const scope = {
  t,
  emitTabAdd,
  editEngine,
  viewInnerBox,
  unbindBox,
  bindBox,
  innerAndOutTemplate,
  batchUnbind,
  $vendor,
  printInnerBoxByOuterId,
  printInnerOrOuterBox
}

const components = {

}

const schema = defineSchemas({
  TagOuterBox: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            console.log('transformRequest=>', data, headers)

            data.query['*'] = {}

            // 添加过滤条件 去除废弃
            if (!data.payload?.filter) {
              data.payload.filter = {
                status: {eq: 'Y'},
                type: { 'eq': 'DELIVERY_NOTE' },
              }
            } else {
              data.payload.filter.status = {eq: 'Y'}
              data.payload.filter.type = {eq: 'DELIVERY_NOTE'}
            }

            return data
          }`),
          onSuccess: expression(`async (res) => {
            console.log(res,'qqqqqq')
          }`)
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
      innerBarDialog: {
        'x-decorator': 'QueryEngine',
        ...viewInnerBoxDialog
      },
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'TagOuterBox',
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
          // 外箱条码
          outerBoxCode: {
            type: 'string',
            title: i18nExpression('orderMod.outerBoxBarcode'),
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'outerBoxCode',
              propKey: 'outerBoxId',
              name: 'scc_sc_tag_outer_box_vendor',
              '@close-quicksearch': expression(`(val) => {
                console.log(val,'val')
                $self.value = val ? val.outerBoxCode : ''
              }`)
            }
          },
          // 物料名称
          materialId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.itemName'),
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialId',
              name: 'purchase_catalog_material',
              '@close-quicksearch': expression(`(val) => {
                $self.value = val ? val.materialId : ''
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
              name: 'purchase_catalog_category',
              '@close-quicksearch': expression(`(val) => {
                $self.value = val ? val.categoryId : ''
              }`)
            }
          },
          // 送货单号
          deliveryNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber')
          },
          // 绑定状态
          boundFlag: {
            type: 'string',
            title: i18nExpression('orderMod.bindingState'),
            'x-component': 'Select',
            enum: [
              {
                label: i18nExpression('buyerDeliveryOrder.bound'), // 已绑定
                value: 'Y'
              },
              {
                label: i18nExpression('orderMod.unbound'), // 未绑定
                value: 'N'
              }
            ]
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
          bindInner: {
            type: 'void',
            'x-content': i18nExpression('barcodeManageNew.bindinnerBox'),
            'x-component': 'AuthorityButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                emitTabAdd({
                  component: editEngine,
                  params: {
                    flag: 'add',
                    row: {},
                    type:'DELIVERY_NOTE'
                  },
                  title: t('cusEntry.supplement20250211.newBinding'), // 新增绑定
                  name: 'editEngine'
                })
              }`)
            }
          },
          batchUnbind: {
            type: 'void',
            'x-content': i18nExpression('orderMod.unbind'),
            'x-component': 'AuthorityButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                batchUnbind($form, $queryEngine)
                // $form.query('.table').take().componentProps.componentInstance.addRow('unshift')
              }`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq, checkbox',
          openCustomTable: true,
          dblclickEditable: true,
          editMode: false
        },
        properties: generateXindexInOrder({
          outerBoxId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          innerBoxId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'tagInnerBoxList'
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          // 外箱条码
          outerBoxCode: {
            type: 'string',
            'x-query-engine-relation': 'TagOuterBoxView',
            'x-render-table-column': {
              title: i18nExpression('orderMod.outerBoxBarcode'),
              minWidth: 150
            }
          },
          // 内箱条码
          innerBoxCode: {
            type: 'string',
            'x-component': 'Button',
            'x-content': '{{$t("common.view")}}',
            'x-component-props': {
              type: 'text',
              '@click': expression(`() => {
                let row = $table.getRowByIndex($self.index)
                viewInnerBox(row, $form, $queryEngine)
              }`)
            },
            'x-render-table-column': {
              minWidth: 150,
              title: i18nExpression('orderMod.innerBoxBarcode'),
              customRender: true
            }
          },
          // 送货单号
          deliveryNumber: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.deliveryNumber'),
              minWidth: 150
            }
          },
          // 送货单行号
          deliveryLine: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.deliveryLineNum'),
              minWidth: 150
            }
          },
          // 物料名称
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.materialName'),
              minWidth: 150
            }
          },
          // 物料编码
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'),
              minWidth: 150
            }
          },
          // 单位
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.unit'),
              minWidth: 150
            }
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'),
              minWidth: 150
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'),
              minWidth: 150
            }
          },
          // 关联物料数量
          relationMaterialQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('hierarchical.associated'),
              minWidth: 150,
              icon: 'el-icon-question',
              // 即1个外箱可装的物料数量，贴1个外箱标签
              description: i18nExpression('cusEntry.supplement20250211.relationMaterialQuantityTip')
            }
          },
          // 已绑定内箱数量
          boundInnerBoxQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.boundInnerBoxQuantity2'),
              minWidth: 150,
              icon: 'el-icon-question',
              // 1个外箱已绑定的内箱数量
              description: i18nExpression('buyerDeliveryOrder.prompt17')
            }
          },
          // 待绑定内箱物料数量
          leftMaterialQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.leftMaterialQuantity'),
              minWidth: 150,
              icon: 'el-icon-question',
              // 待绑定内箱物料数量：外箱关联物料数量-已绑定内箱累计关联物料数量
              description: i18nExpression('cusEntry.supplement20250211.leftMaterialQuantityTip')
            }
          },
          // 创建日期
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.creationDate'),
              minWidth: 150
            }
          },
          // 创建人
          createdBy: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.creator'),
              minWidth: 150
            }
          },
          // 绑定状态
          boundFlag: {
            type: 'string',
            'x-component': 'Select',
            enum: [
              {
                label: i18nExpression('buyerDeliveryOrder.bound'), // 已绑定
                value: 'Y'
              },
              {
                label: i18nExpression('orderMod.unbound'), // 未绑定
                value: 'N'
              }
            ],
            'x-render-table-column': {
              title: i18nExpression('orderMod.bindingState'),
              minWidth: 150
            }
          },
          // 是否绑定内箱条码
          boundInnerBoxFlag: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.supplement20250211.boundInnerBoxFlag'),
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              minWidth: 250,
              fixed: 'right'
            },
            'x-component': 'Space',
            properties: {
              // 解绑
              unBind: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('orderMod.unbind'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                      let row = $table.getRowByIndex($self.index)
                      unbindBox(row, $queryEngine)
                    }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.boundFlag', '.leftMaterialQuantity'],
                  `
                    $deps[0] === 'Y'
                  `
                )
              },
              // 绑定
              bind: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('cusEntry.supplement20250211.bindVariable'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    let row = $table.getRowByIndex($self.index)
                    bindBox(row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.boundFlag', '.leftMaterialQuantity', '.boundInnerBoxFlag'],
                  `
                  $deps[2] === 'Y' && ($deps[0] === 'N' || ($deps[0] === 'Y' && $deps[1] > 0))
                  `
                )
              },
              // 打印外箱条码
              printOutBar: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('cusEntry.supplement20250211.printOuterBoxBarcode'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    let row = $table.getRowByIndex($self.index)
                    printInnerOrOuterBox('outer', row.outerBoxId,$queryEngine,row.templatePath)
                  }`)
                }
              },
              // 打印内箱条码
              printInnerBar: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('cusEntry.supplement20250211.printInnerBar'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    let row = $table.getRowByIndex($self.index)
                    printInnerBoxByOuterId(row.outerBoxId,$queryEngine)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.boundFlag'],
                  `
                  $deps[0] === 'Y'
                  `
                )
              }
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :components="components"
    :scope="scope"
    schemaKey="TagOuterBox"
  />
</template>
