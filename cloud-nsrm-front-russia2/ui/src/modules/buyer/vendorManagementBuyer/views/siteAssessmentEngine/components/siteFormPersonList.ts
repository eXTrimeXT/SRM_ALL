import {
  expression,
  methodExpression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";



export const siteFormPersonList = {
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
      add: {
        type: 'void',
        title: i18nExpression('common.add'),
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`),
          type: 'primary',
          '@click': expression(`() => {
           $self.query('siteFormPersonList')
             .take(field => {
               field.componentProps.componentInstance.addRow()
           })
          }`)
        }
      }
    }
  },
  siteFormPersonList: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      // 如果都没有标记，那么默认使用 id 作为联表主键的 key
      primaryKey: 'siteFormPersonId',
      // 启用级联删除的储值行为
      cascadeDeletion: true,
      preColumns: 'seq',
      editMode: true,
      maxHeight: 400,
      pagination: false,
      sortable: false
    },
    'x-query-engine-skip': true,
    properties: generateXindexInOrder({
      personType: {
        type: 'string',
        title: i18nExpression('vendorMod.personType'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'PERSON_TYPE',
          disabled: expression(`$disabledAdd($form)`)
        },
      },
      userAccount: {
        type: 'string',
        title: i18nExpression('vendorMod.userAccount'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'QuickSearchWrapper',
        'x-component-props': {
          'name': expression(`$table.getRowByIndex($self.index).personType == 'GRAND_JURY' ? 'scc_rbac_user_vendor_display' : 'scc_rbac_user_display'`),
          showKey: 'username',
          disabled: expression(`$disabledAdd($form)`),
          '@close-quicksearch': expression(`(val) => {
              $table.getRowByIndex($self.index).userAccount = val ? val.username : ''
              $table.getRowByIndex($self.index).userName = val ? val.nickname : ''
              $table.getRowByIndex($self.index).userId = val ? val.userId : ''
              $table.getRowByIndex($self.index).userTel = val ? val.phone : ''
              $table.getRowByIndex($self.index).userEmail = val ? val.email : ''
           }`)
        }
      },
      userName: {
        type: 'string',
        title: i18nExpression('vendorMod.userName2'),
        'x-render-table-column': {
          minWidth: 100
        },
        'x-read-pretty': true
      },
      userTel: {
        type: 'string',
        title: i18nExpression('vendorMod.mobilePhone'),
        'x-render-table-column': {
          minWidth: 100
        },
        'x-read-pretty': true
      },
      userEmail: {
        type: 'string',
        title: i18nExpression('vendorMod.emailAddress'),
        'x-render-table-column': {
          minWidth: 100
        },
        'x-read-pretty': true
      },
      userPost: {
        type: 'string',
        title: i18nExpression('bidMod.position'), // 岗位
        'x-render-table-column': {
          minWidth: 100
        },
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`)
        }
      },
      reviewModelId: {
        type: 'number',
        default: null,
        title: i18nExpression('vendorMod.reviewModelId'), // 评审模板
        'x-render-table-column': {
          minWidth: 100
        },
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`)
        },
        'x-component': 'Select',
        'x-reactions': [
          expression(`(field) => {
            siteReviewModel.listAll().then(res => {
              let attr = []
              res.data.forEach(datas => {
                if (datas.approveStatus == 'ENABLE') {
                  attr.push(datas)
                }
              })
              let datas = []
              attr.forEach(resData => {
                const objs = {
                  key:resData.reviewModelId,
                  label:resData.reviewModelName,
                  value:resData.reviewModelId
                }
                datas.push(objs)
              })
              $self.dataSource = datas
            })
          }`),
        ]
      },
      onSiteFlag: {
        type: 'string',
        title: i18nExpression('vendorMod.onSiteFlag'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'Checkbox',
        'x-component-props': {
          'true-label': "Y",
          'false-label': "N",
          disabled: expression(`$disabledAdd($form)`)
        },
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
              disabled: expression(`$disabledAdd($form)`),
              type: 'text',
              '@click': expression(`({ row }) => {
                  $table.remove($self.index)
              }`)
            }
          }
        }}
    })
  }
}
