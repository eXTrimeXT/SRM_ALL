/* eslint-disable quotes */
import { expression } from '@meicloud/render-engine'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'

export default {
  catalogId: {
    type: 'string',
    'x-hidden': true
  },
  vendorId: {
    type: 'string',
    'x-hidden': true
  },
  vendorName: {
    type: 'string',
    title: "{{$t('orderMod.buyerOrderSynergy.vendorName')}}",
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      readPretty: expression(`$readOnly`),
      showKey: 'companyName',
      propKey: 'companyName',
      name: 'scc_sup_company_info2',
      '@close-quicksearch': expression(`(val) => {
        $values.vendorId = val ? val.companyId : null
        $values.vendorCode = val ? val.companyCode : null
        $values.vendorName = val ? val.companyName : null
      }`)
    },
    ...requiredValidatorSegment
  },
  vendorCode: {
    type: 'string',
    title: "{{$t('common.vendorCode')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  purchaseOrgId: {
    type: 'string',
    title: "{{$t('bidMod.businessEntity')}}",
    'x-decorator': 'FormItem',
    'x-component': 'OrganizationSelector',
    'x-component-props': {
      'read-pretty': expression(`$readOnly`),
      'parent-id': -1,
      'node-type': 'OU',
      '@select': expression(`(val) => {
        $values.purchaseOrgId = val ? val.organizationId : null
        $values.purchaseOrgCode = val ? val.organizationCode : null
        $values.purchaseOrgName = val ? val.organizationName : null
        $values.invId = null
        $values.invCode = null
        $values.invName = null
      }`)
    },
    ...requiredValidatorSegment
  },
  purchaseOrgCode: {
    type: 'string',
    'x-hidden': true
  },
  purchaseOrgName: {
    type: 'string',
    'x-hidden': true
  },
  invId: {
    type: 'string',
    title: "{{$t('purchaseDemand.invOrg')}}",
    'x-decorator': 'FormItem',
    'x-component': 'OrganizationSelector',
    'x-component-props': {
      'read-pretty': expression(`$readOnly`),
      'node-type': 'INV',
      'parent-id': expression(`$form.query('purchaseOrgId').get('value')`),
      '@select': expression(`(val) => {
        $values.invId = val ? val.organizationId : null
        $values.invCode = val ? val.organizationCode : null
        $values.invName = val ? val.organizationName : null
      }`)
    },
    ...requiredValidatorSegment
  },
  invCode: {
    type: 'string',
    'x-hidden': true
  },
  invName: {
    type: 'string',
    'x-hidden': true
  },
  materialName: {
    type: 'string',
    title: "{{$t('purSettlementMod.materialId')}}",
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      readPretty: expression(`$readOnly`),
      'show-key': 'materialName',
      name: 'scc_base_material_item',
      '@close-quicksearch': expression(`(val) => {
        $values.materialCode = val ? val.materialCode : null
        $values.materialName = val ? val.materialName : null
        $values.materialId = val ? val.materialId : null
        $values.categoryId = val ? val.categoryId : null
        $values.categoryName = val ? val.categoryName : null
        $values.categoryCode = val ? val.categoryCode : null
        $values.categoryFullName = val ? val.categoryFullName : null
      }`)
    },
    ...requiredValidatorSegment
  },
  materialCode: {
    type: 'string',
    title: "{{$t('mould.itemNumber')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  materialId: {
    type: 'string',
    'x-hidden': true
  },
  categoryName: {
    type: 'string',
    title: "{{$t('orderMod.categoryName')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  categoryId: {
    type: 'string',
    'x-hidden': true
  },
  categoryCode: {
    type: 'string',
    'x-hidden': true
  },
  categoryFullName: {
    type: 'string',
    'x-hidden': true
  },
  catalogStatus: {
    type: 'string',
    title: "{{$t('priceFormula.formulaStatus')}}",
    'x-decorator': 'FormItem',
    default: 'DRAFT', // 默认为拟定
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'CATALOG_STATUS',
      disabled: true
    }
  },
  createdFullName: {
    type: 'string',
    title: "{{$t('dataConfMod.createdBy')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  creationDate: {
    ...yearMonthDaySelectorSegment,
    title: "{{$t('common.creationTime')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      ...yearMonthDaySelectorSegment['x-component-props'],
      disabled: true
    }
  },
  dataSource: {
    type: 'string',
    title: "{{$t('basicPrice.dataSource')}}",
    'x-decorator': 'FormItem',
    default: 'MANUAL_CREATE', // 默认为手工新增
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'PURCHASE_DATA_SOURCE',
      disabled: true
    }
  },
  startDate: {
    ...yearMonthDaySelectorSegment,
    title: "{{$t('common.effectTime')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      ...yearMonthDaySelectorSegment['x-component-props'],
      disabled: true,
      'picker-options': expression(`cannotLessCurrentTimeOptions`)
    }
  },
  endDate: {
    ...yearMonthDaySelectorSegment,
    title: "{{$t('dataConfMod.endDateTime')}}",
    'x-decorator': 'FormItem',
    'x-component-props': {
      ...yearMonthDaySelectorSegment['x-component-props'],
      disabled: true,
      'picker-options': expression(`cannotLessCurrentTimeOptions`)
    }
  }
}
