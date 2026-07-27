/**
 * @description 修改报价结束时间
 */
import { i18nExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { yearMonthDayHourMinuteSecondSelectorSegment } from 'lib@/components/render-engine/schema-segments'

const EditQuoteTimeSegment: Record<any, any> = {
  editQuoteTimeDialog: {
    type: 'void',
    title: '选择新的报价结束时间',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'dialogMain',
      size: 'small',
      appendToBody: true,
      closeOnClickModal: false,
      okButtonText: i18nExpression('common.submit'),
      beforeClose: generateCharFunctionExpression(({ $form, $self, $message, $queryEngine, $projectId }, done, type) => {
        if (!type || type === 'cancel') {
          done()
          return
        }

        $form.query($self.address.concat('editQuoteTimeForm')).take(async (field: any) => {
          const { orderEndTime = '' } = field.value || {}

          $form.validate(field.address.concat('*'))
            .then(async () => {
              const response = await $queryEngine.request.baseRequest(
                {
                  action: 'changeOrderEndTime',
                  query: { '*': {} },
                  payload: [
                    {
                      // 询价单ID
                      projectId: $projectId,
                      // 是否立即结束(Y/N)
                      endNow: false,
                      orderEndTime
                    }
                  ]
                }
              ).catch(() => {})

              if (response) {
                $message.success('修改报价截止时间成功!')
                $queryEngine.request.read()

                done()
              }
            })
        })
      })
    },

    'x-data': {
      // 用于弹窗交互
      viewRow: null
    },

    properties: {
      editQuoteTimeForm: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          colon: false,
          layout: 'vertical',
          feedbackLayout: 'terse'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          minColumns: 1,
          maxColumns: 1,
          columnGap: 32,
          rowGap: 0,
          colWrap: true
        },
        properties: {
          // 报价结束时间
          orderEndTime: {
            title: i18nExpression('bidMod.bidingEndDatetime1'),
            'x-decorator': 'FormItem',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              pickerOptions: {
                disabledDate: generateCharFunctionExpression(({ $dayjs, $values }, time) => {
                  const [nowDate, valueDate] = [
                    $dayjs().hour(0).minute(0).second(0).unix(),
                    $dayjs(time).unix()
                  ]
                  if ($values.orderStartTime) {
                    return valueDate < $dayjs($values.orderStartTime).unix()
                  }
                  return valueDate < nowDate
                })
              }
            },
            'x-validator': {
              required: true,
              triggerType: 'onBlur',
              validator: generateCharFunctionExpression(({ $dayjs, $self, $values }) => {
                if ($dayjs($self.value).unix() <= $dayjs($values.orderStartTime).unix()) {
                  return '报价截止时间必须大于报价开始时间！'
                }
              })
            }
          }
        }
      }
    }
  }
}

export default EditQuoteTimeSegment
