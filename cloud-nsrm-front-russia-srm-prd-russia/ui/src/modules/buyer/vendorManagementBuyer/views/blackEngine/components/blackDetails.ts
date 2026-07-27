import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('black.blacklistSupplierDetails')
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
           field.visible = !$form.readPretty
       }`),
      properties: {
        addSrmCompany: {
          type: 'void',
          title: '{{$t(\'black.SRMSystemSupplierImport\')}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $form.query('state').get('data').isSrmCompany = true
              $form.query('addDialog').take().setComponentProps({ visible: true })
              $clearFormField($form)
             }`)
          }
        },
        addOtherCompany: {
          type: 'void',
          title: '{{$t(\'black.NSRMSystemSupplierImport\')}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $form.query('state').get('data').isSrmCompany = false
              $form.query('addDialog').take().setComponentProps({ visible: true })
              $clearFormField($form)
             }`)
          }
        },
        importExcel: {
          type: 'void',
          'x-component': 'ImportExcel',
          'x-component-props': {
            title: '{{$t(\'common.excelImport\')}}',
            type: 'default',
            extraData: {
              fileModular: 'sup',
              fileFunction: 'blackCompany',
              fileType: 'excel'
            },
            upLoadUrl: '/api-sup/sup/black/importBlackCompanyExcel',
            downloadTemplateOptions: {
              downloadUrl: '/api-sup/sup/black/exportBlackCompanyExcelTemplate',
              fileName: '{{$t(\'logisticsMod.importTemplateXLSX\')}}'
            },
            '@handleSuccess': expression(`(value) => {
              let list = value.data || []
              $addCompanyByImportExcel($form,list)
            }`)
          }
        }

      }
    },
    blackCompanyList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: expression('$form.readPretty ? \'seq\' : \'seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'blackCompanyId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      'x-query-engine-relation': 'blackCompanyList:*',
      properties: generateXindexInOrder({
        // 来源detailList表
        blackCompanyId: {
          type: 'string',
          'x-hidden': true
        },
        companyName: {
          type: 'string',
          title: '{{$t(\'common.vendorName\')}}',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        socialCreditCode: {
          type: 'string',
          title: '{{$t(\'vendorMod.lcCode\')}}',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        companyCreationDate: {
          type: 'string',
          title: '{{$t(\'vendorMod.establishDate\')}}',
          'x-render-table-column': {
            minWidth: 120
          }
        },

        legalPerson: {
          type: 'string',
          title: '{{$t(\'vendorMod.legalPerson\')}}',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        registeredCapital: {
          type: 'string',
          title: '{{$t(\'vendorMod.registeredCapital\')}}',
          'x-render-table-column': {
            minWidth: 120
          }
        },
        companyCountry: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'country'
          },
          'x-render-table-column': {
            title: '{{$t(\'components.address.country\')}}',
            minWidth: 100
          }
        },
        companyProvince: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PROVINCE',
            'custom-select-type': 'PROVINCE'
          },
          'x-render-table-column': {
            title: '{{$t(\'vendorMod.province\')}}',
            minWidth: 100
          }
        },
        companyCity: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: expression('$table.getRowByIndex($self.index)?.companyProvince'),
            'custom-select-type': 'CITY'
          },
          'x-render-table-column': {
            title: '{{$t(\'vendorMod.city\')}}',
            minWidth: 100
          }
        },
        companyType: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'COMPANY_NATURE'
          },
          'x-render-table-column': {
            title: '{{$t(\'vendorMod.companyType\')}}',
            minWidth: 100
          }
        },

        operation: {
          type: 'void',
          title: '{{$t(\'common.operation\')}}',
          'x-render-table-column': {
            width: 60,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty
            }`),
          properties: {
            delete: {
              type: 'void',
              title: '{{$t(\'common.delete\')}}',
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                       ({ rowIndex }) => {
                          $table.remove(rowIndex)
                       }
                   `)
              }
            }
          }
        }
      })
    }
  }
}
