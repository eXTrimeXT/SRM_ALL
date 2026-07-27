/**
 * @description 评分规则
 */
import {
  i18nExpression,
  generateXindexInOrder,
  generateCharExpressionByFunction,
  generateCharReactionExpression
} from '@meicloud/render-engine'
import { requiredValidatorSegment } from 'lib@/components/render-engine'

const ScoreRuleSegment: Record<any, any> = {
  scoreRuleForm: {
    type: 'void',
    'x-decorator': 'FormLayout',
    'x-decorator-props': {
      colon: false,
      layout: 'vertical'
    },
    'x-component': 'FormGrid',
    'x-component-props': {
      minColumns: 1,
      maxColumns: 3,
      columnGap: 32,
      rowGap: 0,
      colWrap: true
    },
    properties: {
      // 评分规则
      scoreRuleType: {
        type: 'string',
        title: i18nExpression('bidMod.inquiryRule'),
        'x-decorator': 'FormItem',
        'x-decorator-props': {
          gridSpan: 1
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_SCORE_RULE_TYPE',
          filterItem: ['COMPOSITE_PRICE'],
          clearable: true
        },
        ...requiredValidatorSegment
      },
      // 评分细则
      scoreTemplateId: {
        type: 'string',
        title: i18nExpression('bidMod.evalRuleList'),
        // 选了综合评分才显示
        'x-visible': generateCharExpressionByFunction(({ $form, $enum }) => {
          return $form.values.scoreRuleType === $enum.SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
        }),
        'x-decorator': 'FormItem',
        'x-decorator-props': {
          gridSpan: 1
        },
        'x-component': 'Select',
        'x-reactions': [
          {
            dependencies: ['scoreRuleType'],
            when: generateCharExpressionByFunction(({ $deps, $enum }) => $deps[0] === $enum.SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE),
            fulfill: {
              state: {
                visible: true
              }
            },
            otherwise: {
              state: {
                visible: false,
                // 清空值
                value: ''
              }
            }
          },
          // 切换到当前才显示
          generateCharReactionExpression(async ({ $scoreRuleHttp, $self, $enum }) => {
            if (!$self.visible) {
              return
            }

            const response = await $scoreRuleHttp.listPage({
              pageNum: 1,
              pageSize: 999,
              souType: $enum.SOU_TYPE_ENUM.INQ,
              status: 'VALID'
            })
            if (response) {
              $self.dataSource = response.data.list || []
            }
          })
        ]
      }
    }
  },
  // 评分细则列表
  scoreRuleDetail: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      preColumns: 'seq',
      class: 'table-view-vxe-table',
      editMode: false,
      sortable: false,
      openCustomTable: false
    },
    // 选了综合评分才显示
    'x-reactions': {
      dependencies: ['scoreRuleType'],
      when: generateCharExpressionByFunction(({ $deps, $enum }) => $deps[0] === $enum.SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE),
      fulfill: {
        state: {
          value: [],
          visible: true
        }
      },
      otherwise: {
        state: {
          visible: false
        }
      }
    },
    properties: generateXindexInOrder({
      // 纬度
      dimension: {
        type: 'string',
        title: i18nExpression('bidMod.dimension'),
        'x-render-table-column': {
          minWidth: 150
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_SCORE_RULE_DIMENSION'
        }
      },
      // 评分项
      scoreItem: {
        type: 'string',
        title: i18nExpression('bidMod.scoreItem'),
        'x-render-table-column': {
          minWidth: 200
        }
      },
      // 取值来源
      scoreSource: {
        type: 'string',
        title: i18nExpression('bidMod.scoreSource'),
        'x-render-table-column': {
          minWidth: 150
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_SOURCE_RULE_SOURCE'
        }
      },
      // 权重（%）
      scoreWeight: {
        type: 'string',
        title: i18nExpression('bidMod.scoreWeight'),
        'x-render-table-column': {
          width: 120
        }
      },
      // 满分值
      totalScore: {
        type: 'string',
        title: i18nExpression('bidMod.fullScore'),
        'x-render-table-column': {
          width: 100
        }
      }
    })
  }
}

export default ScoreRuleSegment
