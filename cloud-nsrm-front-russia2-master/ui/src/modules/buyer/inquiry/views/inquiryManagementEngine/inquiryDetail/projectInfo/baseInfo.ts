/**
 * @description 项目信息
 */
import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction,
  generateCharFunctionExpression
} from '@meicloud/render-engine'
import {
  yearMonthDayHourMinuteSecondSelectorSegment,
  requiredValidatorSegment,
  cannotLessCurrentTimeSegment
} from 'lib@/components/render-engine/schema-segments'

const ProjectInfoFormSegment: Record<any, any> = {
  projectInfoForm: {
    type: 'void',
    'x-component': 'FormGrid',
    'x-component-props': {
      minColumns: 1,
      maxColumns: 3,
      columnGap: 32,
      rowGap: 0
    },
    properties: {
      // 询价单号
      souNo: {
        type: 'string',
        title: i18nExpression('bidMod.inquiryNo'),
        'x-query-engine-skip': true,
        default: '',
        'x-decorator': 'FormItem',
        'x-component': 'Input',
        'x-component-props': {
          disabled: true
        }
      },
      // 询价标题
      souName: {
        type: 'string',
        title: i18nExpression('bidMod.inquiryTitle'),
        'x-decorator': 'FormItem',
        'x-component': 'Input',
        'x-component-props': {
          maxlength: '80',
          showWordLimit: true
        },
        ...requiredValidatorSegment
      },

      // 报价方式
      orderWay: {
        type: 'string',
        title: i18nExpression('bidMod.quoteRule'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_ORDER_WAY',
          clearable: true
        },
        'x-disabled': generateCharExpressionByFunction(({ $form, $enum }, $readonly) => {
          return $readonly || $form.values?.scoreRuleType === $enum.SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
        }),
        ...requiredValidatorSegment
      },

      // 预计报价开始时间
      orderStartTime: {
        title: i18nExpression('bidMod.beginQuote'),
        'x-decorator': 'FormItem',
        ...yearMonthDayHourMinuteSecondSelectorSegment,
        default: '',
        'x-component-props': {
          ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
          ...cannotLessCurrentTimeSegment,
          formatter: expression(`({ cellValue, row, column }) => {
            parseTime(row.orderStartTime, '{y}-{m}-{d} {h}:{i}:{s}')
         }`)
        },
        'x-validator': {
          required: true,
          triggerType: 'onBlur',
          validator: generateCharFunctionExpression(({ $dayjs, $values }) => {
            const { orderStartTime, orderEndTime } = $values
            if (
              orderStartTime &&
              orderEndTime &&
              ($dayjs(orderStartTime).unix() > $dayjs(orderEndTime).unix())
            ) {
              return i18nExpression('cusEntry.supplement20250211.quoteStartTimeNotGreaterThanEndTime')  // '报价开始时间不能大于结束时间!'
            }
          })
        }
      },
      // 报价结束时间
      orderEndTime: {
        title: i18nExpression('bidMod.deadline'),
        'x-decorator': 'FormItem',
        ...yearMonthDayHourMinuteSecondSelectorSegment,
        default: '',
        'x-component-props': {
          ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
          pickerOptions: {
            disabledDate: generateCharFunctionExpression(({ $self, $dayjs }, time) => {
              const orderStartTime = $self.query('.orderStartTime').get('value')
              const [nowDate, startDate, valueDate] = [
                $dayjs().hour(0).minute(0).second(0).unix(),
                $dayjs(orderStartTime).hour(0).minute(0).second(0).unix(),
                $dayjs(time).unix()
              ]
              return (valueDate < startDate) || (valueDate < nowDate)
            })
          },
          formatter: expression(`({ cellValue, row, column }) => {
            parseTime(row.orderEndTime, '{y}-{m}-{d} {h}:{i}:{s}')
          }`)
        },
        'x-validator': {
          required: true,
          triggerType: 'onBlur',
          validator: generateCharFunctionExpression(({ $values, $dayjs }) => {
            const { orderStartTime, orderEndTime } = $values
            if (
              orderStartTime &&
              orderEndTime &&
              ($dayjs(orderStartTime).unix() > $dayjs(orderEndTime).unix())
            ) {
              return i18nExpression('cusEntry.supplement20250211.quoteEndTimeNotLessThanStartTime')  // '报价结束时间不能小于报价开始时间!'
            }
          })
        }
      },
      // 询价类型
      'inqSouProject.inquiryType': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryType'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_INQUIRY_TYPE',
          clearable: true
        },
        ...requiredValidatorSegment
      },
      // 邀标类型
      publishScope: {
        type: 'string',
        title: '邀标类型',
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_PUBLISH_SCOPE',
          clearable: true
        },
        ...requiredValidatorSegment
      },
      // 发起人
      createdFullName: {
        type: 'string',
        title: i18nExpression('bidMod.createdBy'),
        'x-decorator': 'FormItem',
        'x-component': 'Input',
        'x-component-props': {
          disabled: true
        }
      },
      // 创建时间
      creationDate: {
        title: i18nExpression('bidMod.creationDate'),
        'x-decorator': 'FormItem',
        ...yearMonthDayHourMinuteSecondSelectorSegment,
        'x-component-props': {
          ...yearMonthDayHourMinuteSecondSelectorSegment,
          disabled: true
        }
      },
      // 单据状态
      extProjectStatus: {
        type: 'string',
        title: i18nExpression('bidMod.billstatus'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_PROJECT_STATUS',
          disabled: true
        }
      },
      // 备注
      remark: {
        type: 'string',
        title: i18nExpression('bidMod.remark'),
        'x-decorator': 'FormItem',
        'x-decorator-props': {
          gridSpan: 3
        },
        'x-component': 'Input.TextArea',
        'x-component-props': {
          maxlength: '500',
          showWordLimit: true,
          rows: 2
        }
      },
      // 单据来源
      sourceFromType: {
        type: 'string',
        default: 'HAND_MAKE',
        'x-hidden': true
      }
    }
  }
}

export default ProjectInfoFormSegment
