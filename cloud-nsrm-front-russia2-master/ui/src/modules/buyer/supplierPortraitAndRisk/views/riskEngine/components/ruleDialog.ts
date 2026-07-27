import {
  expression,
  i18nExpression,
  generateXindexInOrder,
} from '@meicloud/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'


const initRuleData = ($form:any,$t:any) => {
  console.log('!!! initRuleData')
  const rule1 = [
    {
      ruleColumnA_1: '5',
      ruleColumnA_2: $t('supRisk.veryHigh'), // 很高
      ruleColumnA_3: $t('supRisk.ruleColumnA_3_veryHigh') // 出现的频率很高(或≥ 1 次/半年)；或在大多数情况下会发生；或可以证实多次发生
    },
    {
      ruleColumnA_1: '4',
      ruleColumnA_2: $t('supRisk.high'), // 高
      ruleColumnA_3: $t('supRisk.ruleColumnA_3_high') // 出现的频率较高(或≥ 1 次/半年)；或在大多数情况下很有可能会发生；或可以证实多次发生。
    },
    {
      ruleColumnA_1: '3',
      ruleColumnA_2: $t('supRisk.middle'), // 中
      ruleColumnA_3: $t('supRisk.ruleColumnA_3_middle') //  出现的频率中等(或≥ 1 次/年)；或在某种情况下可能会发生；或被证实曾经发生。
    },
    {
      ruleColumnA_1: '2',
      ruleColumnA_2: $t('supRisk.low'), // 低
      ruleColumnA_3: $t('supRisk.ruleColumnA_3_low') // 出现的频率较小；或一般不太可能发生；或没有被证实发生。
    },
    {
      ruleColumnA_1: '1',
      ruleColumnA_2: $t('supRisk.veryLow'), // 很低
      ruleColumnA_3: $t('supRisk.ruleColumnA_3_veryLow') // 几乎不可能发生，仅可能在非常罕见和例外的情况下发生。
    }
  ]
  const rule2 = [
    {
      ruleColumnB_1: $t('supRisk.quantitativeEvaluation'), // 定量评价
      ruleColumnB_2: $t('supRisk.quantitativeEvaluation_1'), // 财务损失/净利润(财报/内核)
      ruleColumnB_3: '0.3%' + $t('supRisk.under'), // 0.3%以下
      ruleColumnB_4: '0.4%-0.6%',
      ruleColumnB_5: '0.7%-0.9%',
      ruleColumnB_6: '1%-1.5%',
      ruleColumnB_7: '1.6%' + $t('supRisk.over') // 1.6%以上
    },
    {
      ruleColumnB_1: $t('supRisk.quantitativeEvaluation'), // 定量评价
      ruleColumnB_2: $t('supRisk.quantitativeEvaluation_2'), // 财务损失/总资产(财报/内核)
      ruleColumnB_3: '0.03%' + $t('supRisk.under'), // 0.03%以下
      ruleColumnB_4: '0.04%-0.06%',
      ruleColumnB_5: '0.07%-0.09%',
      ruleColumnB_6: '0.1%-0.15%',
      ruleColumnB_7: '0.16%' + $t('supRisk.over') // 0.16%以上
    },
    {
      ruleColumnB_1: $t('supRisk.qualitativeEvaluation'),
      ruleColumnB_2: $t('supRisk.qualitativeEvaluation_1'),
      ruleColumnB_3: $t('supRisk.qualitativeEvaluation_1_veryLow'),
      ruleColumnB_4: $t('supRisk.qualitativeEvaluation_1_low'),
      ruleColumnB_5: $t('supRisk.qualitativeEvaluation_1_middle'),
      ruleColumnB_6: $t('supRisk.qualitativeEvaluation_1_high'),
      ruleColumnB_7: $t('supRisk.qualitativeEvaluation_1_veryHigh')
    },
    {
      ruleColumnB_1: $t('supRisk.qualitativeEvaluation'),
      ruleColumnB_2: $t('supRisk.qualitativeEvaluation_2'),
      ruleColumnB_3: $t('supRisk.qualitativeEvaluation_2_veryLow'),
      ruleColumnB_4: $t('supRisk.qualitativeEvaluation_2_low'),
      ruleColumnB_5: $t('supRisk.qualitativeEvaluation_2_middle'),
      ruleColumnB_6: $t('supRisk.qualitativeEvaluation_2_high'),
      ruleColumnB_7: $t('supRisk.qualitativeEvaluation_2_veryHigh')
    },
    {
      ruleColumnB_1: $t('supRisk.qualitativeEvaluation'),
      ruleColumnB_2: $t('supRisk.qualitativeEvaluation_3'),
      ruleColumnB_3: $t('supRisk.qualitativeEvaluation_3_veryLow'),
      ruleColumnB_4: $t('supRisk.qualitativeEvaluation_3_low'),
      ruleColumnB_5: $t('supRisk.qualitativeEvaluation_3_middle'),
      ruleColumnB_6: $t('supRisk.qualitativeEvaluation_3_high'),
      ruleColumnB_7: $t('supRisk.qualitativeEvaluation_3_veryHigh')
    }
  ]
  const rule3 = [
    {
      ruleColumnC_1: $t('supRisk.level1'),
      ruleColumnC_2: '39-50',
      ruleColumnC_3: $t('supRisk.level1_desc')
    },
    {
      ruleColumnC_1: $t('supRisk.level2'),
      ruleColumnC_2: '26-38',
      ruleColumnC_3: $t('supRisk.level2_desc')
    },
    {
      ruleColumnC_1: $t('supRisk.level3'),
      ruleColumnC_2: '13-25',
      ruleColumnC_3: $t('supRisk.level3_desc')
    },
    {
      ruleColumnC_1: $t('supRisk.level4'),
      ruleColumnC_2: '1-12',
      ruleColumnC_3: $t('supRisk.level4_desc')
    }
  ]
  // console.log('!!!!!!!!!    :',rule1);
  $form.query('table1').take().setValue(rule1)
  $form.query('table2').take().setValue(rule2)
  $form.query('table3').take().setValue(rule3)
}


export default (props:any) => {
  const {
    scope = {}
  } = props || {}
  Object.assign(scope, {
    initRuleData
  })

  return {

      type: 'void',
      title: i18nExpression('supRisk.ruleTitle'),
      
      'x-component': 'RDialog',
      'x-component-props': {
        'close-on-click-modal': false,
        'destroy-on-close': true,
        size: 'large',
        footerButtonList: expression(`(_, { cancelButton,okButton }) => {
          return [
            cancelButton,
          ]
            
          }`),
        beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              
            } else {
              done()
              }
            }
          `)
      },
      properties: {
        collapse: {
          type: 'void',
          'x-component': 'Collapse',
          'x-reactions': expression(`field => {
            console.log('!!!x-reactions')
            console.log('$t',$t)
            console.log('field.visible',field.visible)
            if(field.visible){
              setTimeout(()=>{
                initRuleData($form,$t)
              })
              
            }
             
           }`),
          properties: generateXindexInOrder({
            rule1:{
              type: 'void',
              'x-component': 'CollapseItem',
              'x-component-props': {
                title: `{{$t('supRisk.rule1')}}`,
              },
              'x-query-engine-skip': true,
              'x-read-pretty': expression('$form.readPretty'),
              properties:{
                table1: {
                  type: 'array',
                  'x-component': 'RenderTable',
                  'x-component-props': {
                    class: 'table-view-vxe-table',
                    style: 'flex: 1',
                    preColumns: 'seq',
                    height: '260px',
                    pagination:false,
                  },
                  properties: generateXindexInOrder({
                    'ruleColumnA_1': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnA_1')}}", 
                        width: 150,
                      },
                    },
                    'ruleColumnA_2': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnA_2')}}", 
                        width: 150,
                      },
                    },
                    'ruleColumnA_3': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnA_3')}}", 
                        minWidth: 120,
                      },
                    },
                  })
                }
              }
            },
            rule2:{
              type: 'void',
              'x-component': 'CollapseItem',
              'x-component-props': {
                title: `{{$t('supRisk.rule2')}}`,
              },
              'x-query-engine-skip': true,
              'x-read-pretty': expression('$form.readPretty'),
              properties:{
                table2: {
                  type: 'array',
                  'x-component': 'RenderTable',
                  'x-component-props': {
                    class: 'table-view-vxe-table',
                    style: 'flex: 1',
                    preColumns: 'seq',
                    height: '300px',
                    'showOverflow': false,
                    pagination: false
                  },
                  properties: generateXindexInOrder({
                    'ruleColumnB_1': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_1')}}", 
                        width: 100,
                      },
                    },
                    'ruleColumnB_2': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_2')}}", 
                        width: 100,
                      },
                    },
                    'ruleColumnB_3': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_3')}}", 
                        minWidth: 150,
                      },
                    },
                    'ruleColumnB_4': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_4')}}", 
                        minWidth: 150,
                      },
                    },
                    'ruleColumnB_5': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_5')}}", 
                        minWidth: 150,
                      },
                    },
                    'ruleColumnB_6': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_6')}}", 
                        minWidth: 150,
                      },
                    },
                    'ruleColumnB_7': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnB_7')}}", 
                        minWidth: 150,
                        
                      },

                    },
                    
                  })
                }
              }
            },
            rule3:{
              type: 'void',
              'x-component': 'CollapseItem',
              'x-component-props': {
                title: `{{$t('supRisk.rule3')}}`,
              },
              'x-query-engine-skip': true,
              'x-read-pretty': expression('$form.readPretty'),
              properties:{
                table3: {
                  type: 'array',
                  'x-component': 'RenderTable',
                  'x-component-props': {
                    class: 'table-view-vxe-table',
                    style: 'flex: 1',
                    preColumns: 'seq',
                    'showOverflow': false,
                    height: '420px',
                    pagination: false
                  },
                  properties: generateXindexInOrder({
                    'ruleColumnC_1': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnC_1')}}", 
                        width: 200,
                      },
                    },
                    'ruleColumnC_2': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnC_2')}}", 
                        width: 200,
                      },
                    },
                    'ruleColumnC_3': {
                      type: 'string',
                      'x-render-table-column': {
                        title: "{{$t('supRisk.ruleColumnC_3')}}", 
                        minWidth: 120,
                      },
                    },
                  })
                }
              }
            },
          })
        }
      }
      
    
  }
}
  
