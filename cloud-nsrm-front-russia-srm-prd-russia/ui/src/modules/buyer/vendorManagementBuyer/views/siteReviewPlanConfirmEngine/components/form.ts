import {expression, generateXindexInOrder, i18nExpression} from "@meicloud/render-engine";

import {
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const formMain = {
  type: 'void',
  ...formGridSegment,
  'x-reactions': expression(`() => {
    setTimeout(() => {
      const { datas } = $attrs.params
      $self.form.setValues(datas)
    })
  }`),
  properties: {
    planName: {
        type: 'string',
        title: i18nExpression('vendorMod.planName2'), // 关联计划名称
        'x-decorator': 'FormItem',
        'x-component': 'QuickSearchWrapper',
        'x-component-props': {
          readPretty: '{{$form.readPretty}}',
          showKey: 'planName',
          propKey: 'planName',
          'read-pretty': '{{$form.readPretty}}',
          'name': 'scc_sup_site_review_plan',
          '@close-quicksearch': expression(`(val, scope) => {
              console.log(val)
              console.log($values)

              $values.siteReviewPlanId =  val?.siteReviewPlanId
              $values.vendorName = val ? val.vendorName : ''
              $values.orgName = val ? val.orgName : ''
              $values.categoryName = val ? val.categoryName : ''
              $values.planType = val ? val.planType : ''
              const id = val ? val.siteReviewPlanId : ''
              console.log($form.query('.visitingAddress').take())
              supCommonApi.findCategory(val.vendorId).then(res => {
                const data = res.data.companyInfo
                let obj = [{
                  addressDetail: data.companyAddress,
                  city: data.companyCity,
                  country: data.companyCountry,
                  province: data.companyProvince
                }]

                $form.query('.tableAddress').take().setValue(obj)
              })
          }`)
        },
        'x-validator': {
          required: true
        }
    },
    vendorName: {
      type: 'string',
      title: i18nExpression('vendorMod.vendorName'), // 供应商名称
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    orgName: {
      type: 'string',
      title: i18nExpression('vendorMod.orgName'), // 供应商名称
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    planType: {
      type: 'string',
      title: i18nExpression('vendorMod.planType'), // 计划类型
      'x-component': 'DictSelect',
      'x-component-props': {
        code: 'planType',
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    planSetOutTime: {
      type: 'string',
      title: i18nExpression('vendorMod.planSetOutTime'), // 计划出发时间
      ...yearMonthDaySelectorSegment,
      'x-component-props': {
        disabled: '{{$form.readPretty}}'
      },
      'x-decorator': 'FormItem'
    },
    planVisitTime: {
      type: 'string',
      title: i18nExpression('vendorMod.planVisitTime'), // 计划到访时间
      ...yearMonthDaySelectorSegment,
      'x-component-props': {
        disabled: '{{$form.readPretty}}'
      },
      'x-decorator': 'FormItem'
    },
    visitDays: {
      type: 'string',
      title: i18nExpression('vendorMod.visitDays'), // 计划类型
      'x-component-props': {
        disabled: '{{$form.readPretty}}'
      },
      'x-decorator': 'FormItem'
    }
  }
}
