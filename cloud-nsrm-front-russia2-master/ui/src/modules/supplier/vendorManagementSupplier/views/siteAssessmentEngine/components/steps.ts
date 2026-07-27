import {
  expression,
  i18nExpression
} from "@meicloud/render-engine"

export const Steps = {
  steps: {
    type: 'void',
    'x-decorator': 'div',
    'x-decorator-props': {
      class: 'stepDiv'
    },
    'x-component': 'Steps',
    'x-component-props': {
      alignCenter: true,
      finishStatus: 'success'
    },
    'x-reactions': {
      dependencies: ['approveStatus'],
      fulfill: {
        state: {
          // TODO 优化判断
          // @ts-ignore
          'component[1].active': expression(`
            ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])
            ? 0
            : ['PUBLISH'].includes($deps[0])
            ? 1
            : ['SUBMITTED'].includes($deps[0])
            ? 2
            : ['APPROVED'].includes($deps[0])
            ? 3 : 0
          `)
        }
      }
    },
    properties: {
      step1: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: i18nExpression('common.fillOutReview') // 填写现场评审
        }
      },
      step2: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: i18nExpression('common.staffReview') // 工作人员评审
        }
      },
      step3: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: i18nExpression('common.reviewReport') // 评审报告提交
        }
      },
      step4: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: i18nExpression('vendorMod.relsultApproval') // 结果审批
        }
      },
    }
  }
}
