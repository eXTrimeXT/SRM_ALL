import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'

const assignFetch = ($form: any, $queryEngine: any, values: any, done: any, $message: any, $t: any, closeLoading:any) => {
  const data = $form.query('PrRequirementPoolForBuyer').get('data')
  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': data.requirementLineIds,
      'ceeaStrategyUserId': values.ceeaStrategyUserId,
      'ceeaPerformUserId': values.ceeaPerformUserId
    }],
    'action': 'batchAssign'
  }).then((res: any) => {
    $message.success($t('common.success'))
    data.requirementLineIds = []
    done()
    $queryEngine.state.paginationManagement.refresh()
  }).catch(() => {
    closeLoading()
  })
}

export default {
  type: 'void',
  title: i18nExpression('purchaseDemand.assignBuyer'), // 分配采购员
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
      if ( type === 'ok') { 
        $self.query('*.assignDialog.form').take().submit(values => {
          $assignFetch($form,$queryEngine,values,done,$message,$t,closeLoading)         
        }).catch(() => {
          closeLoading()
        })
      } else {
        done()
      }
    }}}`
  },
  properties: {
    form: {
      type: 'object',
      'x-query-engine-skip': true,
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 2,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        ceeaStrategyUserId: {
          type: 'string',
          title: i18nExpression('purchaseDemand.ceeaStrategyUser'), // 寻源策略
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'PrRequirementPoolForBuyer\').get(\'data\').strategyList.map(item => ({ label: item.personInChargeNickname, value: item.personInChargeUserId, ...item }))'),
          'x-component': 'Select'
        },
        ceeaPerformUserId: {
          type: 'string',
          title: i18nExpression('purchaseDemand.performUserNickname'), // 订单履行
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'PrRequirementPoolForBuyer\').get(\'data\').carryOutList.map(item => ({ label: item.personInChargeNickname, value: item.personInChargeUserId, ...item }))'),
          'x-component': 'Select'
        }
      })
    }
  }
}
export {
  assignFetch
}
