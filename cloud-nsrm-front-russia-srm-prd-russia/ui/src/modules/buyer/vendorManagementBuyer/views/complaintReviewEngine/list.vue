<!-- eslint-disable quotes -->
<script setup lang='ts'>
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, queryFieldStatePropertyExpression,
  queryFieldValueExpression } from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import ComplaintinfoEdit from './edit'

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      records: []
    }
  },
  ComplaintInfo: {
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
          immediate: true
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ComplaintInfo',
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
          complaintNo: {
            type: 'string',
            title: "{{$t('vendorMod.complaintInfoId')}}",
            'x-query-engine-query-operator': 'contains'
          },
          orgId: {
            type: 'string',
            title: "{{$t('supplierRating.entity')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU'
            }
          },
          supplierName: {
            type: 'string',
            title: "{{$t('common.vendor')}}",
            'x-query-engine-query-operator': 'contains'
          },
          complaintType: {
            type: 'string',
            title: "{{$t('vendorMod.complaintType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_TYPE'
            }
          },
          complaintStatus: {
            type: 'string',
            title: "{{$t('vendorMod.complaintStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_STATUS'
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('vendorMod.creatTime2')}}",
            'x-component': 'DatePicker',
            'x-query-engine-query-operator': 'between',
            'x-component-props': {
              type: 'daterange',
              'value-format': 'yyyy-MM-dd'
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
          add: {
            type: 'void',
            title: "{{$t('vendorMod.setPersonLiable')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                setPersonLiable($form,$message)
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/ComplaintInfo/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('ComplaintInfo.table', 'data.columns'),
              dictCodes: {
                complaintType: 'COMPLAINT_TYPE',
                complaintStatus: 'COMPLAINT_STATUS'
              }
            }
          },
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq, checkbox',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          complaintInfoId: {
            type: 'number',
            'x-hidden': false,
            'x-query-engine-primary-key': true
          },
          complaintNo: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.complaintInfoId')}}",
              minWidth: 130,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('view',row,'readonly')
              }`)
            }
          },
          complaintStatus: {
            type: 'string',
            title: "{{$t('vendorMod.complaintStatus')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_STATUS'
            }
          },
          orgName: {
            type: 'string',
            title: "{{$t('vendorMod.ceeaOrgName')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('vendorMod.categoryName')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          complaintType: {
            type: 'string',
            title: "{{$t('vendorMod.complaintType')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_TYPE'
            }
          },
          authUserName: {
            type: 'string',
            title: "{{$t('vendorMod.PersonLiable')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          supplierName: {
            type: 'string',
            title: "{{$t('common.vendor')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          complaintUserName: {
            type: 'string',
            title: "{{$t('vendorMod.complaintUserName')}}",
            'x-render-table-column': {
              minWidth: 130
            }
          },
          supplierCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('barcodeManageNew.creationDate')}}",
            'x-render-table-column': {
              minWidth: 160
            }
          },
          lastAnswerDate: {
            type: 'string',
            title: "{{$t('vendorMod.lastAnswerDate')}}",
            'x-render-table-column': {
              minWidth: 160
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              fixed: 'right',
              width: 120
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.view')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('view',row)
                  }`)
                }
              }
            }
          }
        })
      },
      dialog: {
        type: 'void',
        title: '指派责任人',
        'x-component': 'RDialog',
        'x-component-props': {
          class: 'the-dialog',
          size: 'small',
          'close-on-click-modal': false,
          beforeClose: expression(`(done,type) => {
            if(type === 'ok'){
              $self.query('*.dialog.form').take().submit(values => {
                console.log('values:::',values)
                return $setPerson($form,values,$queryEngine,$message,$bus,done)
              })
            }else{
              done()
            }
          }`)
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
              maxColumns: 1,
              columnGap: 32,
              rowGap: 0
            },
            properties: generateXindexInOrder({
              authUserId: {
                type: 'string',
                'x-hidden': true
              },
              authUserName: {
                type: 'string',
                title: "{{$t('qualitySynergy.responsible')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  name: 'scc_rbac_user_display',
                  showKey: 'username',
                  propKey: 'username',
                  '@close-quicksearch': expression(`(val) => {
                    const instance = $self.query('*.dialog.form').take()
                    instance.value.authUserName = val ? val.nickname : null
                    instance.value.authUserId = val ? val.userId : null
                  }`)
                }
              }
            })
          }
        }
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $editTab = (type:string, row:Object, flag:string) => {
  let name, title
  name = 'complaintinfoEdit' + row.complaintNo
  let readonly = !!(flag === 'readonly')
  title = t('route.complaintReview')
  let tab = {
    component: ComplaintinfoEdit,
    params: {
      flag: type,
      row,
      tabName: name,
      readonly
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const setPersonLiable = ($form, $message) => {
  const rows = $form.query('*.table').take().componentProps.componentInstance.getCheckboxRecords()
  console.log('rows:::', rows)
  if (!rows.length) {
    $message.warning(t('oneStopShopping.createProjectMsg15'))
    return
  }
  $form.query('state').get('data').records = rows || []
  $form.query('*.dialog.form').take((field) => field.reset())
  $form.query('*.dialog').take().setComponentProps({ visible: true })
}

const $setPerson = ($form, values, $queryEngine, $message, $bus, done) => {
  done()
  let records = $form.query('state').get('data').records
  let ids = []
  for (let item of records) {
    item.complaintInfoId && ids.push(item.complaintInfoId)
  }
  const payload = [
    {
      ...values,
      ids
    }
  ]
  return $queryEngine.request.baseRequest({
    type: 'ComplaintInfo',
    action: 'setPersonLiable',
    payload,
    query: {
      '*': {}
    }
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $editTab,
  setPersonLiable,
  $setPerson
}

</script>
<template>
  <RenderEngine schemaKey="complaintReviewList" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
<style lang="scss">
.the-dialog {
  .render-pix-form-item-feedback-layout-loose {
    margin-bottom: 20px !important;
  }
}
</style>
