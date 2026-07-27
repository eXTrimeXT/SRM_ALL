<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

// @ts-ignore
import { setRepeatData, throttle } from 'lib@/utils/util'
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import { bus } from 'lib@/components/render-engine/components/bus'

const { emitTabAdd, emitTabRemove, t: $t, app, vendor } = usePageHelper()

const $attrs: any = useAttrs()

const $saveBill = throttle(
  async (type: any, $form: any, $queryEngine: any, $message: any, $bus: any,) => {
    const values = $form.values
    if (type === 'SAVE') {
      $submitData(type, values, $form, $queryEngine)
    } else if (type === 'SUBMIT') {
      $form
        .validate()
        .then(() => {
          let tips = '提交审批后，供应商还是正式黑名单状态，审批通过后，供应商进入合格供应商，原来的组织及品类更新为原来的合格状态，不需重新引入。'
          app.$confirm(tips, '是否确认提交', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            $submitData(type, values, $form, $queryEngine)
          })
        })
        .catch((err: any) => {
          console.log(err, 'err')
        })
    }
  },
  300,
)

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any) => {
  const form = toJS($values)
  console.log(form)
  if ($attrs.params.flag === 'add') form.approveStatus = 'DRAFT'
  return $queryEngine.request
    .baseRequest({
      type: 'BlackRescind',
      lang: 'zh-cn',
      loading: true,
      tree: true,
      payload: [form],
      query: { '*': {}, blackRescindCompanyList: { '*': {} }, fileUploads: { '*': {} } },
      action: 'save'
    })
    .then((res: any) => {
      if (res.data && res.data.length > 0) {
        const datas = res.data[0]
        $form.values.rescindId = datas.rescindId
        app.$message.success($t('common.successSave'))
        if (type === 'SUBMIT') {
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId(datas.rescindId || '')
          componentInstance.setWorkflowTabDisabled(false)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            $cancel()
          })
          setTimeout(() => {
            $form.readPretty = true
            componentInstance.buttonConfigInfo.save.view = false
            componentInstance.buttonConfigInfo.submit.view = false
          }, 100)
        } else {
          // $form.setValues(datas)
          $queryEngine.request.read()
        }
        bus.$emit('BlackRescind')
      }
    })
}
const $cancel = () => {
  // @ts-ignore
  emitTabRemove($attrs.tabName)
  bus.$emit('BlackRescind')
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
    componentInstance.setWorkflowBusinessId($form.values.rescindId)
    componentInstance.setWorkflowTabDisabled(
      $form.query('state').get('data').orderStatus === 'DRAFT',
    )
  }, 50)
}

// @ts-ignore
const scope = {
  $attrs,
  app,
  emitTabRemove,
  $saveBill,
  updateButtonConfig,
  initButtonConfig
}
// @ts-ignore
const components = {

  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      isSrmCompany: false,
      viewUpdateButton: true,
      orderStatus: 'DRAFT'
    }
  },
  // 基本信息
  BlackRescind: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          tree: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)

            return $attrs.params.row.rescindId || $form.values.rescindId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.rescindId || $form.values.rescindId || '']
            data.query = {
              '*': {},
              blackRescindCompanyList: {'*': {}},
              fileUploads: {'*': {}}
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            let detailData = res.data[0]
            $form.query('state').get('data').orderStatus = detailData.approveStatus

            $form.query('state').get('data').viewUpdateButton = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(detailData.approveStatus) || $attrs.params.flag === 'add'
            updateButtonConfig($form)

            $form.setValues({
              ...detailData
            })
            // 附件
            setTimeout(() => {
              $form.query('fileUploads').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            },100)
           
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),
          onSuccess: expression(`(res) => {

          }`),
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$form.values.blackId || null'),
          'business-type': 'BlackRescind',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-read-pretty': expression('$form.readPretty'),
            properties: generateXindexInOrder({
            // 表单
              baseInfo: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('black.blackSecureDetail')
                },
                'x-query-engine-skip': true,
                properties: {
                  layout: {
                    type: 'void',
                    ...formGridSegment,
                    properties: {
                      rescindName: {
                        type: 'string',
                        title: i18nExpression('vendorMod.relegation.billName'),
                        'x-decorator': 'FormItem',
                        'x-validator': {
                          required: true
                        }
                      },
                      approveStatus: {
                        type: 'string',
                        title: i18nExpression('vendorMod.relegation.documentStatus'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'APPROVE_STATUS_TYPE',
                          disabled: true
                        },
                        'x-decorator': 'FormItem'
                      },
                      createdBy: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creator'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      creationDate: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creationTime'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      rescindContent: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('vendorMod.relegation.sketch'),
                        'x-component-props': {
                          type: 'textarea',
                          autosize: expression('{ minRows: 3, maxRows: 4}')
                        },
                        'x-decorator-props': {
                          gridSpan: 4
                        },
                        'x-validator': {
                          required: true
                        }
                      }
                    }
                  }
                }
              },
              // 解除供应商范围
              rangeList: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('black.rangeList')
                },
                'x-query-engine-skip': true,
                properties: {
                  add: {
                    type: 'void',
                    'x-hidden': '{{$form.readPretty}}',
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      style: 'margin:0 0 15px 0',
                      'show-key': 'username',
                      'name': 'scc_sup_company_info_is_black',
                      multiSelect: true,
                      showButton: true,
                      btnTitle: `{{$t('bidMod.addVendor')}}`,
                      '@close-quicksearch': expression(`(val)=>{
                      let companyNameList = []
                      try {
                        val.forEach(e => {
                          companyNameList.push(e.companyName)
                        })
                      } catch (err) {
                        companyNameList.push(val.companyName)
                      }
                      console.log(companyNameList, 'companyNameList')
                      $queryEngine.request.baseRequest({
                          type: 'BlackCompany',
                          lang: 'zh-cn',
                          loading: true,
                          tree: true,
                          "query": {
                            "*": {}
                          },
                          "payload": {
                            "filter": {
                              "companyName": {
                                "in": companyNameList
                              }
                            },
                            page: {
                              pageNum: 1,
                              pageSize: 15,
                              sort: "lastUpdateDate desc"
                            }
                          },
                          action: 'query'
                        }).then((res) => {
                          let datas = []
                          if (res.data) {
                            res.data.forEach((e) => {
                              if (datas.length<1) {
                                datas.push(e)
                              }
                              // 去重
                              let bol = 1
                              datas.forEach((eD) => {
                                if (e.companyName == eD.companyName) {
                                  bol = 0
                                }
                              })
                              if (bol) {
                                  datas.push(e)
                              }
                            })
                          }
                          if (val) {
                            // 插入经办人跟截止时间
                            val.forEach((eVal) => {
                              const eDatas = datas.find(item => item.companyName == eVal.companyName)
                              console.log(eDatas)
                              eVal.endDate = eDatas?.endDate
                              eVal.agent = eDatas?.createdFullName
                            })
                          }
                          $form.query('.blackRescindCompanyList').take().value = val
                        })
                    }`)
                    }
                  },
                  blackRescindCompanyList: {
                    type: 'array',
                    'x-component': 'RenderTable',
                    'x-component-props': {
                    // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                      primaryKey: 'rescindCompanyId',
                      // 启用级联删除的储值行为
                      cascadeDeletion: true,
                      preColumns: 'seq',
                      class: 'table-view-vxe-table',
                      editMode: false,
                      pagination: false,
                      sortable: false,
                      height: '250px'
                    },
                    'x-query-engine-skip': true,
                    properties: generateXindexInOrder({
                      companyId: {
                        type: 'number',
                        default: null,
                        'x-hidden': true
                      },
                      companyName: {
                        type: 'string',
                        title: i18nExpression('vendorMod.vendorName'), // 供应商名称
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      companyCode: {
                        type: 'string',
                        title: i18nExpression('vendorMod.vendorCode'), // 供应商编码
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      lcCode: {
                        type: 'string',
                        title: i18nExpression('统一信用代码'), // 统一信用代码
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      legalPerson: {
                        type: 'string',
                        title: i18nExpression('法人代表'), // 法人代表
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      supplierType: {
                        type: 'string',
                        title: i18nExpression('供应商类型'), // 供应商类型
                        'x-component': 'DictSelect',
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-component-props': {
                          code: 'SUPPLIER_TYPE'
                        }
                      },
                      companyType: {
                        type: 'string',
                        title: i18nExpression('企业性质'), // 企业性质
                        'x-component': 'DictSelect',
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-component-props': {
                          code: 'COMPANY_NATURE'
                        }
                      },
                      agent: {
                        type: 'string',
                        title: i18nExpression('经办人'), // 经办人
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      endDate: {
                        title: i18nExpression('黑名单截止日期'), // 黑名单截止日期
                        ...yearMonthDaySelectorSegment,
                        'x-render-table-column': {
                          minWidth: 150
                        }
                      },
                      operation: {
                        type: 'void',
                        title: "{{$t('common.operation')}}",
                        'x-render-table-column': {
                          width: 150,
                          fixed: 'right'
                        },
                        'x-component': 'RenderTableButtonList',
                        properties: {
                          delete: {
                            type: 'void',
                            title: "{{$t('common.delete')}}",
                            'x-component-props': {
                              disabled: expression(`$form.readPretty`),
                              type: 'text',
                              '@click': expression(`({ row }) => {
                                $table.remove($self.index)
                            }`)
                            }
                          }
                        }
                      }
                    })
                  }
                }
              },
              // 附件
              fileUploadsCollapse: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('sourcingBuyer.attach')
                },
                'x-query-engine-skip': true,
                properties: {
                  fileUploads: {
                    type: 'array',
                    'x-query-engine-relation': 'fileUploads:*',
                    'x-component': 'FileDynamic',
                    'x-component-props': {
                      'scene-module-code': 'SCENE_BLACK_RESCIND_ATTACHMENT',
                      'business-id': '{{$attrs.params.row.rescindId || $form.values.rescindId}}',
                      'editable': '{{!$readOnly}}',
                      'need-init': true
                    }
                  }
                }
              }
            })
          }
        }
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
    schemaKey="BlackDetail"
  />
</template>
