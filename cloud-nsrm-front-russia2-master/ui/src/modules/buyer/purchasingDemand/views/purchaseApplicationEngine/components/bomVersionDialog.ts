import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { deepClone } from '@/utils'

const viewVersion = (row:any, app:any, $form:any) => {
  $form.query('*.bomVersionDialog').take().setComponentProps({ visible: false })
  app.$router.push({
    name: 'outsourcingBomNew',
    params: {
      from: 'order',
      bomHeadId: row.bomHeadId
    }
  })
}

export default {
  type: 'void',
  'x-decorator': 'QueryEngine',
  title: i18nExpression('purchaseApplication.bomVersionTitle'), // BOM版本选择
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type, closeLoading) => { 
        if ( type === 'ok') { 
          const row = $form.query('*.bomVersionDialog.*.bomVersionList').take()
          .componentProps
          .componentInstance
          .getRadioRecord()
          console.log(row,112)
          if (!row) {
            $message.warning($t('common.msgSelectData'))
            closeLoading()
          } else {
            $selBomVersion($form,done)
          }
          
        } else {
            done()
        }
    }}}`
  },
  properties: {
    layout: {
      type: 'void',
      properties: {
        bomVersionList: {
          type: 'array',
          'x-query-engine-skip': true,
          'x-component': 'RenderTable',
          'x-component-props': {
            height: '450px',
            editMode: true,
            preColumns: 'radio,seq',
            pagination: false,
            sortable: false,
            'radio-config': { trigger: 'row' },
            '@cell-dblclick': expression(`() => {
                  $selBomVersion($form)
            }`)
          },
          'x-read-pretty': true,
          properties: generateXindexInOrder({
            materialCode: {
              type: 'string',
              title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
              'x-render-table-column': {
                minWidth: 120
              }
            },
            materialName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.itemName'), // 物料名称
              'x-render-table-column': {
                minWidth: 120
              }
            },
            versionCode: {
              type: 'string',
              'x-component': 'TableButton',
              'x-component-props': {
                type: 'text',
                disabled: false,
                '@click': expression('({ row }) => $viewVersion(row, app, $form)')
              },
              'x-render-table-column': {
                minWidth: 150,
                title: i18nExpression('dataConfMod.version'), // 版本号
                customRender: true
              }
            },
            bomDetailDescription: {
              type: 'string',
              title: i18nExpression('purchaseApplication.bomDetailDescription'), // BOM说明
              'x-render-table-column': {
                minWidth: 120
              }
            },
            createdFullName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.createdBy1'), // 创建人
              'x-render-table-column': {
                minWidth: 120
              }
            },
            creationDate: {
              ...yearMonthDaySelectorSegment,
              'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)
              },
              'x-render-table-column': {
                title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
                minWidth: 120
              }
            }
          })
        }
      }
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomVersionListPageNum'),
        pageSize: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomVersionListPageSize'),
        total: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomVersionListTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
                $form.query('PrRequirementForBuyer').get('data').bomVersionListPageNum = num
                $getBomVersionList($form)
              }`),
        '@size-change': expression(`(size) => {
                $form.query('PrRequirementForBuyer').get('data').bomVersionListPageSize = size
                $getBomVersionList($form)
              }`)
      }
    }

  }
}
export {
  viewVersion
}
