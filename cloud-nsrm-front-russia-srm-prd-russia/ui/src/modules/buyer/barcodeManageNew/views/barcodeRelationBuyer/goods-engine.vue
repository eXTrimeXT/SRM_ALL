<!-- 内外箱关联 -->
<script setup lang="ts">
// @ts-ignore
import { defineSchemas, generateXindexInOrder, expression, i18nExpression, changeFieldVisibleByDeps } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import viewInnerBoxDialog from 'lib@/compositionEngine/barcodeNewSupplier/barcodeRelation/viewInnerBoxDialog'

// @ts-ignore
import { getDictItem } from '@/api/common'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import { systemUrl } from '@/config/sysConfig'

const { emitTabAdd, t,vendor: $vendor } = usePageHelper()

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

// 打开pdf
const openPrint = (pdfName: any, params: any) => {
  const xml = encodeURIComponent(pdfName)
  const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

const printInnerBoxByOuterId = async (id:string,$queryEngine: any)=>{
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
    let templatePathList = data.map(item=>item.templatePath)
    let setTemplatePath = [...new Set(templatePathList)]
    if(setTemplatePath.length == 1){
      printInnerOrOuterBox('inner', id,$queryEngine,setTemplatePath[0])
    }else{
      app.$message.warning(t('内箱存在多个模板，无法打印'))
    }
  })
}

// 打印内箱或者外箱条码
const printInnerOrOuterBox = async (type: string, id: string, $queryEngine: any,templatePath:any) => {
  let params = encodeURIComponent(`ids=${id}`)
  if(type === 'inner') params = encodeURIComponent(`outerBoxId=${id}`)
  openPrint(templatePath, params)
}


const scope = {
  t,
  emitTabAdd,
  innerAndOutTemplate,
  printInnerOrOuterBox,
  printInnerBoxByOuterId,
  viewInnerBox
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

            data.query = {
              '*': {}
            }

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
          }`)
        }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      innerBarDialog: {
        'x-decorator': 'QueryEngine',
        ...viewInnerBoxDialog
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
           // 外箱标签
          outerBoxCode: {
            type: 'string',
            title: '外箱条码',
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'outerBoxCode',
              propKey: 'outerBoxId',
              name: 'scc_sc_tag_outer_box_1',
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
              name: 'scc_base_material_item',
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
              name: 'scc_base_purchase_category2',
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
            title: '绑定状态',
            'x-component': 'Select',
            enum: [
              {
                label: '已绑定',
                value: 'Y'
              },
              {
                label: '未绑定',
                value: 'N'
              }
            ]
          },
          // 供应商名称
          vendorId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all',
              '@close-quicksearch': expression(`(val) => {
                $self.value = val ? val.companyId : ''
              }`)
            }
          }
        })
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          openCustomTable: true,
          dblclickEditable: true,
          editMode: false,
          style: 'margin-top: 28px'
        },
        properties: generateXindexInOrder({
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
              title: '关联物料数量',
              minWidth: 150,
              icon: 'el-icon-question',
              // 即1个外箱可装的物料数量，贴1个外箱标签
              description: '即1个外箱可装的物料数量，贴1个外箱标签'
            }
          },
          // 已绑定内箱数量
          boundInnerBoxQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '已绑定内箱数量',
              minWidth: 150,
              icon: 'el-icon-question',
              // 1个外箱已绑定的内箱数量
              description: '1个外箱已绑定的内箱数量'
            }
          },
          // 待绑定内箱物料数量
          leftMaterialQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '待绑定内箱物料数量',
              minWidth: 150,
              icon: 'el-icon-question',
              // 关联物料数量-已绑定内箱数量*内箱关联物料数量
              description: '关联物料数量-已绑定内箱数量*内箱关联物料数量'
            }
          },
          // 创建日期
          creationDate: {
            type: 'string',
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
                label: '已绑定',
                value: 'Y'
              },
              {
                label: '未绑定',
                value: 'N'
              }
            ],
            'x-render-table-column': {
              title: i18nExpression('orderMod.bindingState'),
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
               // 打印外箱条码
              printOutBar: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': '打印外箱条码',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    console.log('打印外箱条码')
                    let row = $table.getRowByIndex($self.index)
                    printInnerOrOuterBox('outer', row.outerBoxId,$queryEngine,row.templatePath)
                  }`)
                }
              },
              // 打印内箱条码
              printInnerBar: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': '打印内箱条码',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    console.log('打印内箱条码')
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
