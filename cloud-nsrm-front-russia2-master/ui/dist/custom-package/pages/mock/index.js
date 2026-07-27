import { generateSchemaPage, generateXindexInOrder } from '../helper.js'

const getOrgList = () => (
  `{{
    (field) => {
      $http({
        url: '/api-cm/plugins/cm-car-enhance/org/list',
        method: 'GET',
        loading: true
      })
        .then((res) => {
          field.dataSource = (res.data || []).map(item => ({ label: item.organizationName, value: item.organizationId }))
        })
    }
  }}`
)

export const yearMonthDaySelectorSegment = {
  type: 'date',
  default: null,
  'x-component-props': {
    placeholder: '请选择',
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd'
  }
}

export default generateSchemaPage({
  name: 'ContractHeadCar',
  schema: {
    ContractHeadCar: {
      type: 'void',
      'x-query-engine': {
        actions: {
          paginationQuery: { immediate: true },
          update: true,
          delete: true,
          create: true
        }
      },
      'x-decorator': 'QueryEngine',
      'x-component': 'PageContainer',
      properties: {
        query: {
          type: 'object',
          'x-query-engine-skip': true,
          'x-component': 'QueryFormByQueryEngine',
          'x-component-props': {
            labelCol: 9
          },
          properties: generateXindexInOrder({
            code: {
              type: 'string',
              title: "编码",
              'x-query-engine-query-operator': 'contains'
            },
            name: {
              type: 'string',
              title: "名称",
              'x-query-engine-query-operator': 'contains'
            },
            orgId: {
              type: 'string',
              title: "所属组织",
              'x-component': 'Select',
              'x-reactions': getOrgList()
            },
            startDate: {
              title: '生效时间',
              'x-query-engine-query-operator': '>=',
              type: 'date',
              default: null,
              'x-component-props': {
                placeholder: '生效时间',
                format: 'yyyy-MM-dd',
                'value-format': 'yyyy-MM-dd',
                '@change': `{{
                    (date) => {
                    if (date && date.indexOf('00:00:00') < 0) {
                      $self.value = date + ' 00:00:00'
                    }
                  }
                }}`
              }
            },
            endDate: {
              title: '失效时间',
              'x-query-engine-query-operator': '<=',
              type: 'date',
              default: null,
              'x-component-props': {
                placeholder: '失效时间',
                format: 'yyyy-MM-dd',
                'value-format': 'yyyy-MM-dd',
                '@change': `{{
                  (date) => {
                    if (date && date.indexOf('00:00:00') < 0) {
                      $self.value = date + ' 00:00:00'
                    }
                  }
                }}`
              }
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
              title: "新增",
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': `{{() => {
                  $form.query('*.Dialog').take().setComponentProps({ visible: true })
                  setTimeout(() => {
                    $form.query('*.Dialog.form').take((field) => {
                      field.reset()
                    })
                  })
                }}}`
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
            preColumns: 'seq',
            openCustomTable: true
          },
          properties: generateXindexInOrder({
            id: {
              type: 'string',
              title: "ID",
              'x-render-table-column': {
                width: 200
              },
              'x-hidden': true,
              'x-query-engine-primary-key': true
            },
            code: {
              type: 'string',
              title: "编码",
              'x-render-table-column': {
                width: 200
              }
            },
            name: {
              type: 'string',
              title: "名称",
              'x-render-table-column': {
                width: 200
              }
            },
            orgId: {
              type: 'string',
              title: "所属组织",
              'x-render-table-column': {
                width: 200
              }
            },
            startDate: {
              type: 'string',
              title: "生效时间",
              'x-render-table-column': {
                minWidth: 200
              }

            },
            endDate: {
              type: 'string',
              title: "失效时间",
              'x-render-table-column': {
                minWidth: 200
              }
            }
          })
        },
        Dialog: {
          type: 'void',
          title: '新增汽车合同',
          'x-component': 'RDialog',
          'x-component-props': {
            beforeClose: `{{(done, type) => {
              if ( type === 'ok') {
                $self.query('*.Dialog.form').take().submit(values => {
                  $queryEngine.request.create({
                    ...values,
                  }).then(() => {
                    $queryEngine.state.paginationManagement.refresh()
                    done()
                  })
                })
              } else {
                done()
              }
            }}}`
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
                id: {
                  type: 'string',
                  title: 'ID',
                  'x-hidden': true,
                  'x-decorator': 'FormItem'
                },
                code: {
                  type: 'string',
                  title: '编码',
                  'x-decorator': 'FormItem'
                },
                name: {
                  type: 'string',
                  title: '名称',
                  'x-decorator': 'FormItem'
                },
                orgId: {
                  type: 'string',
                  title: `所属组织`,
                  'x-decorator': 'FormItem',
                  'x-component': 'Select',
                  'x-reactions': getOrgList()
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
              }
            }
          }
        }
      }
    }
  }
})
