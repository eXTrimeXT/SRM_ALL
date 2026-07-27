import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.companyBaseInfo2') // 基础信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        materialHeadNum: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('outsourceMaterialHead.materialHeadNum') // 委外用料单号
        },
        status: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OS_MATERIAL_ORDER_STATUS'
          }
        },
        lastUpdatedFullName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.updatePeople') // 更新人
        },
        lastUpdateDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('common.lastUpdateDate'), // 更新日期
          ...yearMonthDaySelectorSegment
        },
        orderNumber: { // 采购订单号
          type: 'string',
          'x-hidden': true
        },
        orderDetailRow: { // 采购订单行号
          type: 'string',
          'x-hidden': true
        },
        orderNumberAndOrderDetailRow: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: `{{$t('purSettlementMod.orderNumber') +
            '|' + $t('vendorMod.relegation.lineNumber')}}`, // 采购订单号|行号
          'x-reactions': expression(`(field) => {
              $self.value = ($form.values.orderNumber || '') + '|' + ($form.values.orderDetailRow  || '')
          }`),
          'x-query-engine-skip': true
        },
        materialCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialCode') // 物料编码
        },
        materialName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialName') // 物料名称
        },
        materialUnit: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.unit') // 单位
        },
        orderDetailQuantity: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('outsourceMaterialHead.orderNum') // 订单数量
        },
        orgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect')
          }
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.organizationId'), // 库存组织
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'node-type': 'INV',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            'parent-id': expression('$form.values.orgId || -1')
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendorName'), // 供应商名称
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info_all'
          }
        },
        bomVersion: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('outsourceMaterialHead.bomVersion') // BOM版本
        },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.buyerOrderSynergy.comments'), // 备注
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 4 },
            maxlength: 80,
            showWordLimit: true
          }
        }
      }
    }
  }
}
