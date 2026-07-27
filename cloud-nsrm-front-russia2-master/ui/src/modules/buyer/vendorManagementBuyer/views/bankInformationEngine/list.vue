<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import {RenderEngine} from 'lib@/components/render-engine'
import { DictClass } from 'lib@/utils/dict/dict-utils'
import Cselect from 'lib@/components/c-select/dict-select'
import {
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
const $getDictLabel = DictClass.getDictLabel

const schema = defineSchemas({
  BranchBank: {
    type: 'void',
    'x-query-engine': {
      service: 'base',
      actions: {
        paginationQuery: {
          immediate: true
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
          bankNum: {
            type: 'string',
            title: i18nExpression('components.bank.bankCode'),
            'x-query-engine-query-operator': 'contains'
          },
          bankName: {
            type: 'string',
            title: i18nExpression('components.bank.bankName'),
            'x-query-engine-query-operator': 'contains'
          },
          attr1: {
            type: 'string',
            title: i18nExpression('components.bank.isActiveOrNot'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          branchBankNum: {
            type: 'string',
            title: i18nExpression('components.bank.unionCode'),
            'x-query-engine-query-operator': 'contains'
          },
          branchBankName: {
            type: 'string',
            title: i18nExpression('components.bank.branchBankName'),
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('bankInformation:add'),
              '@click': expression(`() => {
                $form.query('*.Dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('*.Dialog.form').take((field) => {
                    field.reset()
                  })
                })
              }`)
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              code: 'bankInformation:import',
              title: i18nExpression('common.import'),
              type: 'default',
              extraData: {
                fileModular: 'base',
                fileFunction: 'branchBankExcel',
                fileType: 'excel'
              },
              upLoadUrl: '/api-base/organization/erp-branch-bank/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-base/organization/erp-branch-bank/importExcelTemplate',
                fileName: expression(`$t('vendorMod.vendorImportTemplateXLXS')`)
              },
              '@handleSuccess': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
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
          branchBankId: {
            type: 'string',
            'x-hidden': true
          },
          bankNum: {
            type: 'string',
            title: i18nExpression('components.bank.bankCode'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          bankName: {
            type: 'string',
            title: i18nExpression('components.bank.bankName'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          branchBankNum: {
            type: 'string',
            title: i18nExpression('components.bank.unionCode'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          branchBankName: {
            type: 'string',
            title: i18nExpression('components.bank.branchBankName'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          attr1: {
            type: 'string',
            title: i18nExpression('components.bank.isActiveOrNot'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
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
                  ...buttonListItemVisibleByPermission('bankInformation:edit'),
                  '@click': expression(`({ row }) => {
                    $form.query('*.Dialog').take().setComponentProps({ visible: true })
                    console.log(row, 'row')
                    setTimeout(() => {
                      $form.query('*.Dialog.form').take(field => {
                        field.value = JSON.parse(JSON.stringify(row))
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
        title: i18nExpression('vendorMod.bankInfo'),
        'x-component': 'RDialog',
        'x-component-props': {
          'close-on-click-modal': false,
          beforeClose: expression(`(done, type, closeLoading) => {
            if ( type === 'ok') {
              $self.query('*.Dialog.form').take().submit(values => {
                $queryEngine.request.save( values, { loading: true }).then(() => {
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                })
              }).catch(() => {
                closeLoading()
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
              bankNum: {
                type: 'string',
                title: i18nExpression('components.bank.bankCode'),
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('common.requiredField')
                }
              },
              bankName: {
                type: 'string',
                title: i18nExpression('components.bank.bankName'),
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('common.requiredField')
                }
              },
              branchBankNum: {
                type: 'string',
                title: i18nExpression('components.bank.unionCode'),
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('common.requiredField')
                }
              },
              branchBankName: {
                type: 'string',
                title: i18nExpression('components.bank.branchBankName'),
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('common.requiredField')
                }
              },
              attr1: {
                type: 'string',
                title: i18nExpression('components.bank.isActiveOrNot'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                },
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('common.requiredField')
                }
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
