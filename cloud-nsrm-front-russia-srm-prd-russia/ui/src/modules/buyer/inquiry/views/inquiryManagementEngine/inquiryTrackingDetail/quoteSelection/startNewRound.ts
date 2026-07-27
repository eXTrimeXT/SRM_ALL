/**
 * @description 发起新一轮
 */

import { i18nExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { cannotLessCurrentTimeSegment, yearMonthDayHourMinuteSecondSelectorSegment } from 'lib@/components/render-engine'

const StartNewRoundSegment: Record<any, any> = {
  startNewRoundDialog: {
    type: 'object',
    title: '发起新一轮报价',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'dialogMain',
      size: 'middle',
      // size: 'xLarge',
      appendToBody: true,
      closeOnClickModal: false,
      okButtonText: i18nExpression('common.submit'),
      beforeClose: generateCharFunctionExpression(async ({ $form, $self, $message, $queryEngine, $projectId, $bus }, done, type) => {
        if (!type || type === 'cancel') {
          done()
          await $self.reset()
          return
        }

        $form.validate($self.address.concat('startNewRoundForm').concat('*'))
          .then(async () => {
            const response = await $queryEngine.request.baseRequest({
              type: 'InqSouProjectForBuyer',
              action: 'startNewRound',
              payload: [
                {
                  projectId: $projectId,
                  newVendors: [],
                  startNow: false,
                  ...$self.value.startNewRoundForm
                }
              ],
              query: {}
            }).catch(() => {})

            if (response) {
              $message.success('发起新一轮报价成功!')
              $bus.$emit('getTrackingDetail')

              done()
              await $self.reset()
            }
          })
          .catch(() => {})
      })
      // TODO 待实现
      // '@opened': generateCharFunctionExpression(async ({ $form, $self, $queryEngine, $projectId }) => {
      //   const takeObj: any = $form.query($self.address.concat('targetPriceItemList')).take()
      //
      //   // 查询物料列表
      //   const response = await $queryEngine.request.baseRequest({
      //     type: 'InqSouProjectForBuyer',
      //     action: 'getTargetPrice',
      //     query: { '*': {} },
      //     payload: [$projectId],
      //     tree: true
      //   }).catch(() => {})
      //
      //   if (response) {
      //     takeObj.setValue((response.data || []).concat())
      //   }
      // })
    },
    properties: {
      startNewRoundForm: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          colon: false,
          feedbackLayout: 'terse'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          minColumns: 1,
          maxColumns: 2,
          columnGap: 32,
          rowGap: 0,
          colWrap: true
        },
        properties: {
          // 新一轮报价开始时间
          orderStartTime: {
            title: '新一轮报价开始时间',
            'x-decorator': 'FormItem',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            default: '',
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              ...cannotLessCurrentTimeSegment
            },
            'x-validator': {
              required: true,
              triggerType: 'onBlur',
              validator: generateCharFunctionExpression(({ $dayjs, $values }) => {
                const { orderStartTime, orderEndTime } = $values.startNewRoundDialog.startNewRoundForm
                if (
                  orderStartTime &&
                  orderEndTime &&
                  ($dayjs(orderStartTime).unix() > $dayjs(orderEndTime).unix())
                ) {
                  return '报价开始时间不能大于结束时间!'
                }
              })
            }
          },
          // 新一轮报价结束时间
          orderEndTime: {
            title: '新一轮报价结束时间',
            'x-decorator': 'FormItem',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            default: '',
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              ...cannotLessCurrentTimeSegment
            },
            'x-validator': {
              required: true,
              triggerType: 'onBlur',
              validator: generateCharFunctionExpression(({ $dayjs, $values }) => {
                const { orderStartTime, orderEndTime } = $values.startNewRoundDialog.startNewRoundForm
                if (
                  orderStartTime &&
                  orderEndTime &&
                  ($dayjs(orderStartTime).unix() > $dayjs(orderEndTime).unix())
                ) {
                  return '报价开始时间不能大于结束时间!'
                }
              })
            }
          }
        }
      }
    }
  }
}

export default StartNewRoundSegment
