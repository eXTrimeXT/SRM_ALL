import {
  expression,
  changeFieldVisibleByDeps,
  i18nExpression
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
    title: i18nExpression('priceModel.costElement.baseInfo')
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        companyName: {
          type: 'string',
          title: i18nExpression('vendorMod.relegation.relegationVendor'),
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info2',
            '@close-quicksearch': expression(`(val, scope) => {
              $getCompany(val,$values,$form)

            }`)
          },
          ...requiredValidatorSegment
        },
        demotionType: {
          type: 'string',
          title: i18nExpression('vendorMod.relegation.relegationType'), //
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DEMOTION_TYPE',
            '@change-value': expression(`(val, element) =>{
              $setDemotionType($values,val,$form)
            }`)
          },
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment
        },
        demotionName: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('vendorMod.relegation.estimateName'),
          'x-component-props': {
            disabled: true
          }
        },
        createdBy: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.relegation.creator'),
          'x-component-props': {
            disabled: true
          }
        },
        creationDate: {
          ...yearMonthDaySelectorSegment,
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.relegation.creationTime'),
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          }
        },
        demotionDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.relegation.effectTimeDowngrade'),
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          },
        },
        reviewUserNicknames: {
          type: 'string',
          title: i18nExpression('vendorMod.relegation.assessor'),
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'nickname',
            propKey: 'nickname',
            multiSelect: true,
            'name': 'scc_rbac_user_display',
            '@close-quicksearch': expression(`(val, scope) => {
              $getUserDemandObj(val,$values,$form)

            }
            `)
          },
        },
        companyDemotionId: {
          type: 'void',
          'x-decorator': 'FormItem',
          'x-hidden': true,
          'x-query-engine-skip': true
        },
        companyId: {
          type: 'void',
          'x-decorator': 'FormItem',
          'x-hidden': true,
          'x-query-engine-skip': true
        },
        rules:{
          type: 'void',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 0,
            style:{
              'grid-column': 'span 4'
            }
          },
          'x-component': 'el-tooltip',
          'x-component-props':{
            // style:{
            //   'font-size': '14px',
            //   'color': 'black',
            //   'font-weight': '400'
            // },
            placement:'right',
            effect:'white',
//             content:
//             `流程发起人的操作要求：
// 需要根据升降级的具体原因，选择采购部门或采购以外部门人员评审。

// （1）绿牌供应商降级黄牌——升降级类型选择“降级至黄牌”，单据提交审批通过后，品类预警状态从“绿牌”降级到“黄牌”。

// （2）绿牌供应商降级红牌——升降级类型选择“降级至红牌”，单据提交审批通过后，品类预警状态从“绿牌”降级到“红牌”。

// （3）黄牌供应商降级红牌——将降级类型选择“降级至红牌”，单据提交审批通过后，品类预警状态从“黄牌”降级到“红牌”。

// （4）黄牌供应商升级绿牌——将降级类型选择“黄牌改善升级”，单据提交审批通过后，品类预警状态从“黄牌”升级到“绿牌”。

// （5）红牌供应商升级黄牌——将降级类型选择“红牌改善升级”，单据提交审批通过后，品类预警状态从“红牌”升级到“黄牌”。
// `,
            content:
            `${i18nExpression('cusEntry.supplement20250205.processInitiatorRequirements')}
              ${i18nExpression('vendorMod.relegation.operationalInfo.0')}

              ${i18nExpression('vendorMod.relegation.operationalInfo.1')}

              ${i18nExpression('vendorMod.relegation.operationalInfo.2')}

              ${i18nExpression('vendorMod.relegation.operationalInfo.3')}

              ${i18nExpression('vendorMod.relegation.operationalInfo.4')}

              ${i18nExpression('vendorMod.relegation.operationalInfo.5')}
              `,
            'popper-class':'demotiontooltip'
          },
          properties:{
            ruleBtn:{
              type: 'void',
              title: i18nExpression('relegationEntity.key21'),
              'x-component': 'TableButton',
              'x-component-props':{
                type: 'text',
                // style:{
                //   color: '#0077ff!important',
                //   'font-size': '12px',
                //   'font-weight': '400'
                // }
              },
            }
          }

        },
        demotionDesrc: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 0,
            style:{
              'grid-column': 'span 4'
            }
          },
          title: i18nExpression('vendorMod.relegation.relegationReasons'),
          'x-component-props': {
            type: 'textarea',
            autosize: expression(`{ minRows: 2, maxRows: 5 }`),
            // rows: 2,
            maxlength: '500',
            showWordLimit: true
          },
          ...requiredValidatorSegment
        },
        drafterOpinion: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 0,
            style:{
              'grid-column': 'span 4'
            }
          },
          title: i18nExpression('vendorMod.relegation.drafterOpinion'),
          'x-component-props': {
            type: 'textarea',
            autosize: expression(`{ minRows: 2, maxRows: 5 }`),
            // rows: 2,
            maxlength: '500',
            showWordLimit: true
          }
        },
        status: {
          type: 'string',
          default: 'DRAFT',
          'x-decorator': 'FormItem',
          'x-hidden': true,
        },


      }
    }
  }
}
