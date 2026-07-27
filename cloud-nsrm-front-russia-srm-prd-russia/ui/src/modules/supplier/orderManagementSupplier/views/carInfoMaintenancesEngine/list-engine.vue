<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  changeFieldVisibleByDeps,
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  generateCharFunctionExpression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  buttonListItemVisibleByPermission,
  formGridSegment,
  inputLimitSegment
} from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { formatDate } from "@vueuse/core"

const $openDialog = ($form: any, $message: any) => {
  $form.query('CarInfoVendor').get('data').dialogType = 'create'
  $form.query('carInfoDialog').take().title = $t('carInfoMaintenance.add') // 新增车辆预约
  $form.query('*.carInfoDialog.carInfoForm').take((field) => {
    field.reset()
  })
  $form.query('carInfoDialog').take().setComponentProps({ visible: true })
}

const $changeOne = ($form: any, row: any) => {
  $form.query('CarInfoVendor').get('data').dialogType = 'change'
  $form.query('carInfoDialog').take().title = $t('carInfoMaintenance.update') // 修改车辆预约
  $form.query('carInfoDialog').take().setComponentProps({ visible: true })
  console.log('$form.values', $form.values)
  setTimeout(() => {
    $form.values.carInfoForm.carInfoId = row.carInfoId
    $form.values.carInfoForm.licensePlate = row.licensePlate
    $form.values.carInfoForm.carType = row.carType
    $form.values.carInfoForm.effectiveDate = row.effectiveDate.substring(0, 10)
    $form.values.carInfoForm.expirationDate = row.expirationDate.substring(0, 10)
  })
}

// 批量删除
const $invalid = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('CarInfoVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.warning($t('contractMod.msgSelData'))
  }
  const submitStatus = ['EFFECTIVE']
  if (rows.some((i) => submitStatus.findIndex((j) => j === i.status) === -1)) {
    return $message.warning($t('orderMod.msgVendorOrder[23]'))
  }

  $confirm('确认失效', {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $queryEngine.request.baseRequest({
      type: 'CarInfoVendor',
      action: 'invalid',
      payload: rows,
      query: {}
    }).then(() => {
      $message.success($t('carInfoMaintenance.prompt1')) // 失效成功!

      $queryEngine.state.paginationManagement.refresh()
    }).catch(() => {})
  })
}

// 批量提交
const $batchSubmit = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('CarInfoVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.warning($t('contractMod.msgSelData'))
  }

  const submitStatus = ['CREATE']
  if (rows.some((i) => submitStatus.findIndex((j) => j === i.status) === -1)) {
    return $message.warning($t('orderMod.msgVendorOrder[25]'))
  }
  // 确认提交
  $confirm($t('carInfoMaintenance.prompt2'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $queryEngine.request.baseRequest({
      type: 'CarInfoVendor',
      action: 'submitBatch',
      payload: rows,
      query: {}
    }).then(() => {
      $message.success($t('carInfoMaintenance.prompt3')) // 提交成功!

      $queryEngine.state.paginationManagement.refresh()
    }).catch(() => {})
  })
}

const schema = defineSchemas({
  CarInfoVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-data': {
      dialogType: 'create',
      queryData: {},
      selectedData: []
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'CarInfoVendor',
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
          status: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_INFO_STATUS'
            }
          },
          carType: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.carType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_TYPE'
            }
          },
          licensePlate: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.licensePlate')}}",
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'), // 创建通知单
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('po:carInfoVendor:add'),
              '@click': expression(`() => {
                $openDialog($form, $message)
              }`)
            }
          },
          invalid: {
            type: 'void',
            title: i18nExpression('common.inactive'), // 创建通知单
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              // disabled: expression('$form.readPretty ? undefined : true'),
              ...buttonListItemVisibleByPermission('po:carInfoVendor:add'),
              '@click': expression(`() => {
                $invalid($self, $queryEngine,$message,$confirm)
              }`)
            }
          },
          submit: {
            type: 'void',
            title: i18nExpression('common.submit'), // 提交车辆预约
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              // disabled: expression('$form.readPretty ? undefined : true'),
              ...buttonListItemVisibleByPermission('po:carInfoVendor:add'),
              '@click': expression(`() => {
                $batchSubmit($self, $queryEngine,$message,$confirm)
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
          openCustomTable: true,
          checkbox: true
        },
        properties: generateXindexInOrder({
          carInfoId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            'x-query-engine-sort': 'desc',
            title: "{{$t('orderMod.buyerOrderSynergy.lastUpdateDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment
          },
          status: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_INFO_STATUS'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          licensePlate: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $changeOne($form, row)`)
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.licensePlate'),
              minWidth: 120,
              customRender: true
            }
          },
          carType: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.carType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_TYPE'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          effectiveDate: {
            title: "{{$t('orderMod.buyerOrderSynergy.effectiveDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment
          },
          expirationDate: {
            title: "{{$t('orderMod.buyerOrderSynergy.expirationDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment
          }
        })
      },
      carInfoDialog: {
        type: 'void',
        title: i18nExpression('carInfoMaintenance.vehicleReservation'), // 车辆预约
        'x-component': 'RDialog',
        'x-component-props': {
          class: 'dialogMain',
          size: 'middle',
          // appendToBody: true,
          // closeOnClickModal: false,
          // okButtonText: i18nExpression('common.submit'),
          // footer: true,
          beforeClose: expression(`(done, type) => {
            if (!type || type === 'cancel') {
              done()
              return
            }
            let action = 'create'
            if ($form.query('CarInfoVendor').get('data').dialogType === 'change') {
              action = 'change'
            }
            $queryEngine.request.baseRequest({
              type: 'CarInfoVendor',
              action: action,
              loading: true,
              payload: [
                {
                  ...$form.values.carInfoForm
                }
              ],
              query: {}
            }).then(() => {
              if (action === 'create') {
                $message.success($t('carInfoMaintenance.prompt4')) // 车辆信息创建成功!
              } else {
                $message.success($t('carInfoMaintenance.prompt5')) // 车辆信息修改成功!
              }
              done()
              $queryEngine.state.paginationManagement.refresh()
            }).catch(() => {})
          }`)
        },
        properties: {
          carInfoForm: {
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
              licensePlate: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('orderMod.buyerOrderSynergy.licensePlate'), // 车牌号码
                required: true,
                'x-component-props': {
                  ...inputLimitSegment('text', '20') // 文字长度控制
                }
              },
              carType: {
                type: 'string',
                required: true,
                title: i18nExpression('orderMod.buyerOrderSynergy.carType'), // 车辆类型
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'CAR_TYPE'
                },
                'x-decorator': 'FormItem'
              },
              effectiveDate: {
                type: 'string',
                required: true,
                'x-decorator': 'FormItem',
                'x-component': 'DatePicker',
                title: i18nExpression('orderMod.buyerOrderSynergy.effectiveDate'), // 创建时间
                'x-component-props': {
                  ...yearMonthDaySelectorSegment
                }
              },
              expirationDate: {
                type: 'string',
                required: true,
                'x-decorator': 'FormItem',
                'x-component': 'DatePicker',
                title: i18nExpression('orderMod.buyerOrderSynergy.expirationDate'), // 创建时间
                'x-component-props': {
                  ...yearMonthDaySelectorSegment
                }
              }
            }
          }
        }
      }
    }
  }
})

const { emitTabAdd, t: $t } = usePageHelper()

const scope = {
  $openDialog,
  $batchSubmit,
  $invalid,
  $changeOne,
  $t
}
</script>

<template>
  <RenderEngine schemaKey="carInfoMaintenancesVenter" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
