<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDayStartSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { DictClass } from '@/library/utils/dict/dict-utils'
import Cselect from '@/library/components/c-select/dict-select'
const $getDictLabel = DictClass.getDictLabel

const schema = defineSchemas({
  PayType: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: {
          immediate: true,
          transformResponse: (res: string) => {
            const data = JSON.parse(res)

            if (data.data?.ref?.PayType) {
              const keys = Object.keys(data.data.ref.PayType ?? {})
              keys.forEach(key => {
                const item = data.data.ref.PayType[key]
                if (item.condFactor) {
                  item.condFactor = item.condFactor.split(',').map((v: string) => parseInt(v))
                }
              })
            }

            return data
          }
        }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          condFactor: {
            type: 'string',
            title: "{{$t('contractMod.condFactor')}}",
            'x-query-engine-query-operator': 'contains'
          },
          payExplain: {
            type: 'string',
            title: "{{$t('contractMod.payExplain')}}",
            'x-query-engine-query-operator': 'contains'
          },
          startDate: {
            title: "{{$t('contractMod.startDate')}}",
            'x-query-engine-query-operator': '>=',
            ...yearMonthDayStartSelectorSegment
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
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query('*.Dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('*.Dialog.form').take((field) => {
                    field.reset()
                  })
                })
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
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          payTypeId: {
            type: 'string',
            'x-hidden': true
          },
          condFactorId: {
            type: 'string',
            'x-hidden': true
          },
          payExplain: {
            type: 'string',
            title: "{{$t('contractMod.payExplain')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          condFactor: {
            type: 'string',
            title: "{{$t('contractMod.condFactor')}}",
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              multiple: true,
              code: 'condFactorList',
              'custom-select-type': 'condFactorList'
            }
          },
          valueRange: {
            type: 'string',
            title: "{{$t('contractMod.valueRange')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'valueRange'
            },
            'x-render-table-column': {
              width: 110
            }
          },
          startDate: {
            ...yearMonthDaySelectorSegment,
            title: "{{$t('contractMod.startDate')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          endDate: {
            ...yearMonthDaySelectorSegment,
            title: "{{$t('contractMod.endDate')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          createdBy: {
            type: 'string',
            title: "{{$t('contractMod.createdBy')}}",
            'x-render-table-column': {
              width: 120
            }
          },
          creationDate: {
            title: "{{$t('contractMod.creationDate')}}",
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 120
            }
          },
          lastUpdatedBy: {
            type: 'string',
            title: "{{$t('contractMod.lastUpdatedBy')}}",
            'x-render-table-column': {
              width: 120
            }
          },
          lastUpdateDate: {
            title: "{{$t('contractMod.lastUpdateDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              minWidth: 150
            }

          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 130,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              management: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $form.query('*.Dialog').take().setComponentProps({ visible: true })

                    setTimeout(() => {
                      $form.query('*.Dialog.form').take(field => {
                        field.setValue({
                          ...row,
                          condFactorId: row.condFactorId.split(',').filter(Boolean).map(i => Number(i))
                        })
                      })
                    });
                  }`)
                }
              }
            }
          }
        })
      },
      Dialog: {
        type: 'void',
        title: i18nExpression('bidMod.bulkMaintainFwAgreement'),
        'x-component': 'RDialog',
        'x-component-props': {
          beforeClose: expression(`(done, type, closeLoading) => {
            if ( type === 'ok') {
              $self.query('*.Dialog.form').take().submit(values => {
                $queryEngine.request.save({
                  ...values,
                  condFactorId: values.condFactorId.join(','),
                  condFactor: values.condFactorId.map(i => $getDictLabel('condFactorList', i)).join(',')
                }).then(() => {
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                }).catch(err => {
                  closeLoading()
                })
              })
            } else {
              done()
            }
      }`)
        },
        properties: {
          form: {
            type: 'object',
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              payExplain: {
                type: 'string',
                title: `{{$t('contractMod.payExplain')}}`,
                required: true,
                'x-decorator': 'FormItem'
              },
              condFactorId: {
                type: 'string',
                title: `{{$t('contractMod.condFactor')}}`,
                required: true,
                'x-component': 'Cselect',
                'x-component-props': {
                  multiple: true,
                  code: 'condFactorList',
                  'custom-select-type': "condFactorList"
                },
                'x-decorator': 'FormItem'
              },
              startDate: {
                title: `{{$t('contractMod.startDate')}}`,
                'x-decorator': 'FormItem',
                ...yearMonthDaySelectorSegment
              },
              endDate: {
                title: `{{$t('contractMod.endDate')}}`,
                'x-decorator': 'FormItem',
                ...yearMonthDaySelectorSegment
              },
              valueRange: {
                type: 'string',
                title: `{{$t('contractMod.valueRange')}}`,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'valueRange'
                },
                'x-decorator': 'FormItem'
              }
            }
          }
        }
      }
    }
  }
})

const scope = {
  $getDictLabel
}

const components = {
  Cselect
}
</script>

<template>
  <RenderEngine
    schemaKey="contractPaymentType"
    :pageAttrs="$attrs"
    class="contractPaymentType"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
::v-deep .contractPaymentType {
  .render-pix-form-item-layout-vertical .render-pix-form-item-label * {
    line-height: 35px
  }
  .render-pix-form-item-layout-vertical .render-pix-form-item-label-content {
    min-height: 35px
  }
}
</style>
