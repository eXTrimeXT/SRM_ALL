/**
 * @description 付款条款
 */
import {
  generateCharFunctionExpression,
  i18nExpression,
  generateXindexInOrder,
  expression
} from '@meicloud/render-engine'
import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

interface PaymentTypeProps {
  readonly: boolean
}

export default function (props?: PaymentTypeProps): Record<any, any> {
  const { readonly = false } = props || {}
  console.log(props, 'props')

  const selfReadonly = (flag?: boolean) => expression(`${flag ? '!' : ''}(${readonly} || $readonly)`)

  return {
    paymentTypeDialog: {
      type: 'void',
      title: i18nExpression('paymentType.paymentType'),
      'x-component': 'RDialog',
      'x-decorator': 'QueryEngine',
      'x-reactions': generateCharFunctionExpression(({ $self }) => {
        if ($self) $self.setComponentProps({ footer: !$self.data.readOnly })
      }),
      'x-component-props': {
        class: 'dialogMain',
        size: 'large',
        appendToBody: true,
        closeOnClickModal: false,
        okButtonProps: generateCharFunctionExpression(({ $readonly }) => $readonly ? false : {}),
        okButtonText: i18nExpression('common.submit'),
        beforeClose: generateCharFunctionExpression(({ $form, $self, $bus, $message }, done, type) => {
          if (!type || type === 'cancel') {
            done()
            return
          }

          // 只找当前子级别的
          $form.query($self.address.concat('paymentTypeTable')).take((field: any) => {
            if (field.value.length) {
              try {
                const totalPaymentProportion = field.value.reduce((total: any, item: any) => {
                  return total + Number(item.paymentProportion)
                }, 0)
                if (totalPaymentProportion !== 100) {
                  $message.warning('付款比例相加必须等于100')
                  return
                }
              } catch (e) {
                $message.warning('付款比例相加必须等于100')
                return
              }
            }

            if ($self.data.editIndex === -1) {
              $self.data.selections.forEach((row: any) => {
                row.paymentList = field.value.concat()
                $bus.$emit('paymentBeforeClose', { type, paymentList: row.paymentList })
              })
            } else {
              $form.values.orderItemList[$self.data.editIndex].paymentList = field.value.concat()
            }
            console.log($form.values, '$form.values')

            done()
          })
        }),

        // 打开后赋值
        '@opened': generateCharFunctionExpression(({ $form, $self, $getFieldParentFieldFormPath }) => {
          console.log($form.query('paymentTypeDialog').get('data'), 'paymentTypeDialog')
          const state = $self.data
          console.log(state, 'state')
          if (state.editRow) {
            const {
              paymentList
              // ...queryParams
            } = state.editRow || {}

            // 存在列表
            if (paymentList && Array.isArray(paymentList)) {
              $form.query($self.address.concat('paymentTypeTable')).take((field: any) => {
                field.value = paymentList.concat()
              })
              // return
            }

            // // 根据业务类型净化入参
            // const params = getQueryParamsByBusinessType(this.businessType, queryParams)
            // if (params && Object.values(params).length === 0) {
            //   return
            // }
            //
            // const response = await this.$api.utils.common(
            //   getApiByBusinessType(this.businessType),
            //   { queryParams: params }
            // )
            //
            // if (response && response.data && Array.isArray(response.data)) {
            //   this.paymentTypeTableData = response.data
            // }
          }
        })
      },

      // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
      'x-data': {
        // 用于弹窗交互
        editIndex: -1,
        editRow: null,
        selections: [],
        readOnly: false
      },

      properties: {
        // 操作栏
        toolbar: {
          type: 'void',
          'x-component': 'Space',
          'x-component-props': {
            style: 'margin-bottom: 16px'
          },
          'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
            const data = $form.query('paymentTypeDialog').get('data')
            field.visible = !data.readOnly
          }),
          properties: {
            // 新增
            add: {
              type: 'void',
              title: i18nExpression('common.add'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('paymentTypeTable'))
                    .take(field => {
                      field.componentProps.componentInstance.addRow()
                    })
                })
              }
            }
          }
        },

        // 表格
        paymentTypeTable: {
          type: 'array',
          'x-decorator': 'FormItem',
          'x-component': 'RenderTable',
          'x-component-props': {
            preColumns: 'seq',
            class: 'table-view-vxe-table',
            openCustomTable: false,
            sortable: false,
            editMode: true,
            pagination: false
          },
          properties: generateXindexInOrder({
            // 付款账期
            paymentPeriod: {
              type: 'string',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.readPretty = data.readOnly
              }),
              'x-render-table-column': {
                title: i18nExpression('paymentType.paymentDay1'),
                minWidth: 150
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PAYMENT_PERIOD'
              },
              ...editTableFormItemValid
            },
            // 付款条件
            paymentCondition: {
              type: 'string',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.readPretty = data.readOnly
              }),
              'x-render-table-column': {
                title: i18nExpression('contractMod.payExplain'),
                minWidth: 150
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'payExplain',
                customSelectType: 'payExplain',
                '@change-value': generateCharFunctionExpression(({ $table, $self }, _, dictItem) => {
                  console.log(dictItem, 'dictItem')
                  const row = $table.getRowByIndex($self.index)
                  // 冗余名称
                  row.paymentCondition = dictItem.label
                  row.paymentConditionId = dictItem.id
                })
              },
              ...editTableFormItemValid
            },
            // 付款方式
            paymentMode: {
              type: 'string',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.readPretty = data.readOnly
              }),
              'x-render-table-column': {
                title: i18nExpression('paymentType.paymentWay'),
                minWidth: 150
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PAYMENT_MODE'
              },
              ...editTableFormItemValid
            },
            // 付款比例
            paymentProportion: {
              type: 'string',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.readPretty = data.readOnly
              }),
              'x-render-table-column': {
                title: i18nExpression('bidMod.payRatio'),
                minWidth: 100
              },
              exclusiveMaximum: 100,
              minimum: 0,
              'x-component': 'Input',
              ...editTableFormItemValid
            },
            // 付款阶段
            paymentPhase: {
              type: 'string',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.readPretty = data.readOnly
              }),
              'x-render-table-column': {
                title: i18nExpression('bidMod.payStage'),
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PAYMENT_STAGE'
              },
              ...editTableFormItemValid
            },
            operation: {
              type: 'void',
              'x-render-table-column': {
                title: i18nExpression('common.operation'),
                width: 150,
                fixed: 'right'
              },
              'x-component': 'RenderTableButtonList',
              'x-reactions': generateCharFunctionExpression(({ $form }, field) => {
                const data = $form.query('paymentTypeDialog').get('data')
                field.visible = !data.readOnly
              }),
              properties: {
                // comment
                delete: {
                  type: 'void',
                  title: i18nExpression('common.delete'),
                  'x-component-props': {
                    '@click': generateCharFunctionExpression(({ $table, $self }) => {
                      $table.remove($self.index)
                    })
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
