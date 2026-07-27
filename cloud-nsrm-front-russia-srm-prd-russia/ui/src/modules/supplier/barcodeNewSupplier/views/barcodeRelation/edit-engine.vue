<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, generateCharFunctionExpression } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import { formGridSegment, requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'

// @ts-ignore
import CreateInnerBoxDialog from './components/createInnerBox'

const { emitTabRemove, app, t } = usePageHelper()

const $attrs: any = useAttrs()

// eslint-disable-next-line no-undef
const props = defineProps({
  showType: {
    type: String,
    default: ''
  },
  row: {
    type: Object,
    default: () => {
      return {}
    }
  },
  flag: {
    type: String,
    default: ''
  }
})

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('TagOuterBox')
  emitTabRemove($attrs.tabName)
}

// 绑定提交
const bindSubmit = ($form: any, $queryEngine: any, $bus: any) => {
  let selects = $form.query('.detailList').take() // 获取表格实时选中值
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!selects.length) {
    return app.$message.warning('请选择内箱条码')
  }

  let payload = selects.map((row: any) => {
    return {
      outerBoxId: $form.values.outerBoxId,
      innerBoxId: row.innerBoxId
    }
  })
  $queryEngine.request.baseRequest({
    'loading': true,
    'type': 'TagInnerBox',
    'action': 'bound',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': payload
  }).then((res: any) => {
    console.log(res, 'res')
    app.$message.success(t('common.success'))
    if (props.flag === 'bind') {
      $bus.$emit('TagManage')
    } else {
      $closePageAndRefreshListPageData($bus)
    }
  })
}

// 过滤参数 去除空参
const setSearchParams = ($form: any) => {
  let params: any = {
    outerBoxId: { eq: $form.values.outerBoxId },
    vendorId: { eq: $form.values.vendorId },
    vendorCode: { eq: $form.values.vendorCode },
    vendorName: { eq: $form.values.vendorName },
    materialCode: { eq: $form.values.materialCode },
    materialId: { eq: $form.values.materialId },
    innerBoxCode: { contains: $form.values.searchInner.innerBoxCode }
  }

  for (let key in params) {
    if (!params[key]?.eq && !params[key]?.contains) {
      delete params[key]
    }
  }

  return params
}

// 搜索
const searchInnerBox = useDebounceFn(($form: any, $queryEngine: any) => {
  if (!$form.values.outerBoxId) {
    return app.$message.warning('请先选择外箱条码')
  }

  $queryEngine.request.baseRequest({
    'type': 'TagInnerBox',
    'action': 'queryUnRel',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      filter: setSearchParams($form),
      page: {
        ...$form.query('TagInnerBox').get('data').pageInfo,
        sort: 'creationDate desc,tagNo desc'
      }
    }
  }).then((res: any) => {
    console.log(res, 'res')
    Object.assign($form.query('TagInnerBox').get('data').pageInfo, {
      pageNum: res.originalData.payload.pageNum,
      pageSize: res.originalData.payload.pageSize,
      total: res.originalData.payload.total
    })
    $form.query('.detailList').take().setValue(res.data)
  })
}, 300)

const selectOuterCodeAfter = (val: any, $form: any, $queryEngine: any) => {
  $form.values.materialName = val?.materialName
  $form.values.materialId = val?.materialId
  $form.values.materialCode = val?.materialCode
  $form.values.vendorId = val?.vendorId
  $form.values.vendorCode = val?.vendorCode
  $form.values.vendorName = val?.vendorName
  $form.values.relationMaterialQuantity = val?.relationMaterialQuantity || 0
  $form.values.leftMaterialQuantity = val?.leftMaterialQuantity || 0
  $form.values.boundInnerBoxQuantity = val?.boundInnerBoxQuantity || 0
  $form.values.outerBoxId = val?.outerBoxId
  $form.values.outerBoxCode = val?.outerBoxCode

  searchInnerBox($form, $queryEngine)
}

const scope = {
  $props: props,
  $attrs,
  app,
  t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  bindSubmit,
  searchInnerBox,
  selectOuterCodeAfter,
  setSearchParams
}

const components = {
  CreateInnerBoxDialog
}

const schema = defineSchemas({
  // 基本信息
  TagInnerBox: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-component-props': {
      class: 'the-barcodeRelation-detail',
      direction: 'vertical'
    },
    'x-data': {
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0
      }
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`async () => {
            return await new Promise((res) => {
              setTimeout(() => {
                console.log('ready=>',$form, $values)

                // 增加一个外来控制头部表单只读
                if($props.showType === 'readOnly'){
                  $form.readPretty = true
                  $form.query('outerBoxCode').take().setComponentProps({'readPretty':true})
                }

                let row =  $attrs?.params?.row || $props.row
                console.log('!!!row',row)
                // 点击行绑定进来赋值
                if ($attrs?.params?.flag === 'bind' || $props.flag === 'bind' ) {
                  $values.vendorName = row.vendorName
                  $values.vendorId = row.vendorId
                  $values.vendorCode = row.vendorCode
                  $values.outerBoxCode =  row.outerBoxCode
                  $values.outerBoxId =  row.outerBoxId
                  $values.materialName =  row.materialName
                  $values.materialId =  row.materialId
                  $values.materialCode =  row.materialCode
                  $values.relationMaterialQuantity =  row.relationMaterialQuantity || 0
                  $values.leftMaterialQuantity =  row.leftMaterialQuantity || 0
                  $values.boundInnerBoxQuantity =  row.boundInnerBoxQuantity || 0
                }
                if($props.flag === 'bind'){
                  $bus.$emit('searchInnerBox')
                }
              })
            })  
          }`)
        },
        save: {
          cascadeDeletion: true
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.cancel'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $bus.$emit('TagManage')
              $closePageAndRefreshListPageData($bus)
            }`)
          }
        },
        bindSubmit: {
          type: 'void',
          'x-content': '绑定提交',
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              bindSubmit($form, $queryEngine, $bus)
            }`)
          }
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'closeBarcodeRelationDialog',
          '@listener': expression(`() => {
            $form.query('createInnerBoxDialog').take().setComponentProps({ visible: false })
            searchInnerBox($form, $queryEngine)
          }`)
        }
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-reactions': expression(`() => {
          
        }`),
        properties: generateXindexInOrder({
          // 可绑定内箱的外箱标签
          bindForm: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: '可绑定内箱的外箱条码'
            },
            'x-query-engine-skip': true,
            // 'x-read-pretty': true,
            properties: {
              layout: {
                type: 'void',
                ...formGridSegment,
                properties: {
                  // 外箱条码
                  outerBoxCode: {
                    type: 'string',
                    title: '外箱条码',
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      readPretty: `{{$props.showType === 'readOnly'}}`,
                      showKey: 'outerBoxCode',
                      showInput: '{{$values.outerBoxCode}}',
                      'scope-data': '{{$values}}',
                      name: expression(`$attrs.params?.type === 'MATERIAL' ? 'scc_sc_tag_outer_box_relation_vendor_m' : 'scc_sc_tag_outer_box_relation_vendor'`), // DELIVERY_NOTE 送货单，MATERIAL 物料
                      '@close-quicksearch': expression(`async (val) => {
                        selectOuterCodeAfter(val, $form, $queryEngine)
                      }`)
                    }
                  },
                  // 供应商名称
                  vendorName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 物料名称
                  materialName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('purchaseDemand.itemName'),
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 关联物料数量
                  relationMaterialQuantity: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '关联物料数量',
                    'x-decorator-props': {
                      tooltip: '即1个外箱可装的物料数量，贴1个外箱标签'
                    },
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 待绑定内箱物料数量
                  leftMaterialQuantity: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '待绑定内箱物料数量',
                    'x-decorator-props': {
                      tooltip: '待绑定内箱物料数量：外箱关联物料数量-已绑定内箱累计关联物料数量'
                    },
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 已绑定内箱数
                  boundInnerBoxQuantity: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '已绑定内箱数',
                    'x-decorator-props': {
                      tooltip: '1个外箱已绑定的内箱数量'
                    },
                    'x-component-props': {
                      disabled: true
                    }
                  }
                }
              }
            }
          },
          // 内箱条码
          deliveryOrderInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: '内箱条码'
            },
            'x-read-pretty': false,
            properties: {
              searchInner: {
                type: 'object',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                  layout: 'horizontal'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                  minColumns: 1,
                  columnGap: 32,
                  rowGap: 0
                },
                'x-reactions': expression(`() => {
                  if ($values.outerBoxId && ($attrs?.params?.flag === 'bind')) { 
                    searchInnerBox($form, $queryEngine)
                  }
                }`),
                properties: generateXindexInOrder({
                  // 内箱条码
                  innerBoxCode: {
                    type: 'string',
                    title: '内箱条码',
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 1
                    }
                  },
                  innerSearchBtn: {
                    type: 'void',
                    'x-component': 'RButton',
                    'x-content': i18nExpression('common.search'),
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 2
                    },
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {
                        searchInnerBox($form, $queryEngine)
                      }`)
                    }
                  }
                })
              },
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                // 'x-reactions': expression(`field => {
                //   field.visible = !$values.detailList?.length && $props.flag === 'bind'
                // }`),
                'x-component-props': {
                  style: 'margin-bottom: 16px'
                },
                properties: {
                  add: {
                    type: 'void',
                    title: `{{$t('orderMod.createdInnerBox')}}`,
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {
                        $form.query('createInnerBoxDialog').take().setComponentProps({ visible: true })
                        setTimeout(()=>{
                          $form.query('CreateInnerBoxDialogWrap').take().setComponentProps({
                            row: $values
                          })
                        })
                      }`)
                    }
                  }
                }
              },
              TagInnerBox: {
                type: 'void',
                'x-query-engine': {
                  service: 'sup-ce',
                  actions: {
                    paginationQuery: {
                      // immediate: true,
                      action: 'queryUnRel',
                      transformRequest: expression(`(data, headers) => {
                        console.log(data, headers,'data, headers')
                        data.query = {
                          '*': {}
                        }

                        data.payload.filter = setSearchParams($form)
                        data.payload.page.sort = 'creationDate desc,tagNo desc'

                        // data.payload = {
                        //   filter: setSearchParams($form),
                        //   page: {
                        //     ...$form.query('TagInnerBox').get('data').pageInfo,
                        //     sort: 'creationDate desc,tagNo desc'
                        //   }
                        // }

                        return data
                      }`),
                      onSuccess: expression(`async (res) => {
                        console.log(res,'success')

                        Object.assign($form.query('TagInnerBox').get('data').pageInfo, {
                          // pageNum: res.originalData.payload.pageNum,
                          // pageSize: res.originalData.payload.pageSize,
                          total: res.originalData.payload.total
                        })
                      }`)
                    }
                  }
                },
                'x-component': 'QueryEngine',
                'x-query-engine-skip': true,
                properties: {
                  bus: {
                    type: 'void',
                    'x-component': 'BusEvent',
                    'x-component-props': {
                      eventName: 'searchInnerBox',
                      '@listener': expression(`() => {
                        console.log('get bus')
                        searchInnerBox($form, $queryEngine)
                      }`)
                    }
                  },
                  detailList: {
                    type: 'array',
                    'x-component': 'RenderTable',
                    'x-component-props': {
                      height: 300,
                      class: 'table-view-vxe-table',
                      preColumns: 'seq, checkbox',
                      // pagination: true,
                      sortable: false,
                      // 联表主键的 key
                      primaryKey: 'innerBoxId',
                      // 启用级联删除的储值行为
                      cascadeDeletion: true,
                      pagination: `{{$form.query('TagInnerBox').get('data').pageInfo}}`
                    },
                    'x-query-engine-skip': true,
                    // 'x-query-engine-relation': 'detailList:*',
                    properties: generateXindexInOrder({
                      // 内箱条码
                      innerBoxCode: {
                        type: 'string',
                        title: '内箱条码',
                        'x-render-table-column': {
                          minWidth: 100
                        },
                        'x-query-engine-query-operator': 'contains'
                      },
                      // 物料名称
                      materialName: {
                        type: 'string',
                        title: '物料名称',
                        'x-render-table-column': {
                          minWidth: 100
                        }
                      },
                      // 物料编码
                      materialCode: {
                        type: 'string',
                        title: '物料编码',
                        'x-render-table-column': {
                          minWidth: 100
                        }
                      },
                      // 关联物料数量
                      relationMaterialQuantity: {
                        type: 'string',
                        title: '关联物料数量',
                        'x-render-table-column': {
                          minWidth: 160,
                          icon: 'el-icon-question',
                          // 即1个内箱可装的物料数量，贴1个内箱标签
                          description: '即1个内箱可装的物料数量，贴1个内箱标签'
                        }
                      },
                      // 生成内箱条码数量
                      tagNo: {
                        type: 'string',
                        title: '生成内箱条码数量',
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      // 条码生成规则
                      tagRuleName: {
                        type: 'string',
                        title: '条码生成规则',
                        'x-render-table-column': {
                          minWidth: 160
                        }
                      },
                      // 条码样式
                      tagType: {
                        type: 'string',
                        title: '条码样式',
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'TAG_TYPE'
                        },
                        'x-render-table-column': {
                          minWidth: 100
                        }
                      },
                      // 创建日期
                      creationDate: {
                        type: 'string',
                        title: '创建日期',
                        'x-render-table-column': {
                          minWidth: 100
                        }
                      },
                      // 创建人
                      createdBy: {
                        type: 'string',
                        title: '创建人',
                        'x-render-table-column': {
                          minWidth: 100
                        }
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
                        ],
                        'x-render-table-column': {
                          minWidth: 100
                        }
                      }
                    })
                  }
                }
              }
            }
          }
        })
      }
    }
  },
  createInnerBoxDialog: {
    type: 'void',
    title: "{{$t('orderMod.createInnerBox')}}",
    'x-decorator': 'QueryEngine',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'tagmanage-barcodeRelation-dialog',
      'close-on-click-modal': false,
      'destroy-on-close': true,
      footer: false,
      beforeClose: expression(`(done, type) => {
        if ( type === 'ok') {
          done()
          
        } else {
          done()
          }
        }
      `)
    },
    properties: {
      CreateInnerBoxDialogWrap: {
        type: 'void',
        'x-component': 'CreateInnerBoxDialog',
        'x-component-props': {}
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
    schemaKey="TagInnerBoxDetail"
  />
</template>

<style lang="scss">
.the-barcodeRelation-detail {
  .render-pix-form-item-label {
    .render-pix-form-item-label-tooltip {
      height: 20px !important;
      padding-bottom: 2px;
    }
  }
  .off-cursor {
    cursor: pointer;
  }
  .search-po {
    float: right;
  }
}
.select-barcode-grid {
  .render-pix-form-item-layout-vertical {
    display: flex;
  }
}
</style>
