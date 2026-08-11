import dialogAttributes from './dialog.js'

export default {
  components: {},
  scope: () => {},
  patches: {
    add: {
      newDialog: dialogAttributes,
      ContractHead: {
        toolbar: {
          openDialog: {
            __field__: true,
            type: 'void',
            title: "【新增】按钮",
            'x-component-props': {
              'className': 'patches',
              '@click': "{{() => $form.query('newDialog').take().setComponentProps({ visible: true })}}"
            }
          }
        },
        query: {
          lastUpdatedBy: {
            __field__: true,
            type: 'string',
            title: "【新增】修改人名称",
            'x-query-engine-skip': true,
            'x-decorator-props': {
              'class': 'patches'
            }
          }
        }
      }
    },
    update: {
      ContractHead: {
        query: {
          contractType: {
            __field__: true,
            title: "【联动】操作类型",
            'x-decorator-props': {
              'class': 'patches'
            },
            'x-index': 3,
            'x-reactions': `{{
              (field) => {
                console.log('==contractType==', field)
                const contractTypeField = field.query('query.contractType').take()
                const contractStatusField = field.query('query.contractStatus').take()
                $effect(() => {
                  contractStatusField?.setComponentProps({ disabled: contractTypeField.value === 'TERMINATION' })
                }, [contractTypeField.value])
              }
            }}`
          },
          createdBy: {
            __field__: true,
            title: "【修改】创建人",
            'x-decorator-props': {
              'class': 'patches'
            }
          }
        },
        table: {
          contractHeadId: {
            __field__: true,
            'x-hidden': false,
            'title': '【新增】合同ID',
            'x-render-table-column': {
              minWidth: 150,
              'headerStyle': 'color:red;'
            }
          },
          contractNo: {
            __field__: true,
            type: 'string',
            'x-render-table-column': {
              'title': '【修改】合同编号',
              'headerStyle': 'color:red;',
              customRender: true
            },
            'x-component': 'RenderTableLink',
            'x-component-props': {
              type: 'text',
              '@click': `{{
                ({ row }) => $message.success($t('点击链接成功'))
              }}`
            },
          },
        }
      }
    },
    remove: {
      'ContractHead.table.sourceNumber': true
    },
  },
}
