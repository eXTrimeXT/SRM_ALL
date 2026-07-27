/* eslint-disable quotes */
import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  formGridSegment, requiredValidatorSegment, yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.receiptInfo') // 单据信息
  },
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout1: {
      type: 'void',
      ...formGridSegment,
      properties: {
        // 主键id
        approvalId: {
          type: 'string',
          'x-hidden': true
        },
        // 标题
        approvalName: {
          type: 'string',
          title: i18nExpression('bidMod.title'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            maxlength: 50,
            'show-word-limit': true,
            class: 'limitInput',
            disabled: expression(`$self.query('.sourceFromType').take()?.value !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`)
          },
          ...requiredValidatorSegment
        },
        // 寻源方式
        sourceFromType: {
          type: 'string',
          title: i18nExpression('bidMod.sourceType'),
          default: 'hand_make',
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PRICE_APPROVAL_FROM_TYPE',
            disabled: true
          }
        },
        // 寻源单号
        sourceFromNo: {
          type: 'string',
          title: i18nExpression('bidMod.businessNo'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        // 创建人
        createdFullName: {
          type: 'string',
          title: i18nExpression('bidMod.bidingCreatedBy'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        // 创建时间
        creationDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          },
          title: i18nExpression('common.creationDate'),
          'x-decorator': 'FormItem',
        },
        // 状态
        approvalStatus: {
          type: 'string',
          title: i18nExpression('bidMod.status'),
          default: 'DRAFT',
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'SOU_PRICE_APPROVAL_STATUS',
            disabled: true
          }
        },
        orderWay: {
          type: 'string',
          title: i18nExpression('bidMod.bidingAwardWay'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'SOU_ORDER_WAY',
            disabled: true
          }
        },
        // 是否更新至价格库
        isSyncToPriceLibrary: {
          type: 'string',
          title: i18nExpression('bidMod.ifUpdateToPriceLib'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO',
            disabled: expression('$form.values.sourceFromType === PRICE_APPROVAL_FROM_TYPE_ENUM.INQ')
          },
          ...requiredValidatorSegment
        },
        // 币种
        standardCurrency: {
          type: 'string',
          title: i18nExpression('bidMod.allAurrency'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'currency',
            disabled: expression(`$form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`)
          },
          ...requiredValidatorSegment
        },
        // 价格精度
        pricePrecision: {
          type: 'string',
          title: i18nExpression('bid_mod.pricePrecision'),
          'x-decorator': 'FormItem',
          'x-component': 'Select',
          enum: [
            { id: 0, label: '0', value: 0 },
            { id: 1, label: '1', value: 1 },
            { id: 2, label: '2', value: 2 },
            { id: 3, label: '3', value: 3 },
            { id: 4, label: '4', value: 4 },
            { id: 5, label: '5', value: 5 },
            { id: 6, label: '6', value: 6 }
          ],
          'x-component-props': {
            disabled: expression(`$form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`)
          },
          ...requiredValidatorSegment
        },
        // 汇率类型
        exchangeRateType: {
          type: 'string',
          title: i18nExpression('bid_mod.exchangeRateType'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'EXCHANGE_RATE_TYPE',
            disabled: expression(`![PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE,PRICE_APPROVAL_FROM_TYPE_ENUM.AUCT].includes($form.values.sourceFromType)`)
          },
          ...requiredValidatorSegment
        },
        // 币种转换日期
        exchangeRateDate: {
          type: 'string',
          title: i18nExpression('bid_mod.currencyChangeDate'),
          'x-decorator': 'FormItem',
          'x-component': 'DatePicker',
          'x-component-props': {
            type: 'date',
            format: 'yyyy-MM-dd',
            'value-format': 'yyyy-MM-dd',
            disabled: expression(`![PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE,PRICE_APPROVAL_FROM_TYPE_ENUM.AUCT].includes($form.values.sourceFromType)`)
          },
          ...requiredValidatorSegment
        },
        // 中标未税总金额 后端计算得出 未税单价 * 中标数量
        notaxBidAmount: {
          type: 'string',
          title: i18nExpression('bidMod.notaxBidAmount'),
          'x-decorator': 'FormItem',
          'x-component': 'InputNumber',
          'x-component-props': {
            min: 0,
            disabled: true
          }
        },
        // 价格审批号
        approvalNo: {
          type: 'string',
          title: i18nExpression('bidMod.approvalNo'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        }
      }
    },
    layout2: {
      type: 'void',
      ...formGridSegment,
      properties: {
        // 需求概述
        demandSummary: {
          type: 'string',
          title: i18nExpression('bidMod.requiremenOverview'),
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 5 }
          }
        },
        // 说明
        description: {
          type: 'string',
          title: i18nExpression('vendorMod.operationMemo'),
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 5 }
          }
        }
      }
    }
  }
}
