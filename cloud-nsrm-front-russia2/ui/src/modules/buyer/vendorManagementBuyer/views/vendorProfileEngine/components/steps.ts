import {
  expression
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
      dependencies: ['status'],
      fulfill: {
        state: {
          // TODO 优化判断
          // @ts-ignore
          'component[1].active': expression(`
            ['DRAFT', ''].includes($deps[0])
            ? 0
            : ['SUBMITTED'].includes($deps[0])
            ? 1
            : ['APPROVED'].includes($deps[0])
            ? 3
            : 2
          `)
        }
      }
    },
    properties: {
      step1: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.finishCompanyInfo')`)
        }
      },
      step2: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('common.successSubmit')`)
        }
      },
      step3: {
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.approvalSuccess')`)
        }
      }
    }
  }
}
