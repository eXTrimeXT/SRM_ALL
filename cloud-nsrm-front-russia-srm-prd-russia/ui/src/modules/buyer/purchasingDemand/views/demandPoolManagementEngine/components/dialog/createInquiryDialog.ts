import {
  expression, generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

const createInquiryFetch = ($form: any, $queryEngine: any, values: any, done: any, $confirm: any, $t: any, $http: any, app: any, closeLoading: any) => {
  const data = $form.query('PrRequirementPoolForBuyer').get('data')
  const requirementLineIds = data.selectedRows.map((item: any) => item.requirementLineId)
  $queryEngine.request.baseRequest({
    type: 'PrRequirementPoolForBuyer',
    action: 'createSou',
    query: {
      '*': {}
    },
    payload: [{
      requirementLineIds,
      souType: values.globalSourceType
    }]
  }).then((res: any) => {
    console.log('res:::', res)
    if (res?.data?.length) {
      const { projectId, souNo } = res.data[0]
      if (!projectId || !souNo) return
      $confirm(`${$t('purchaseDemand.addOneItem2Tips2')}${souNo}${$t('purchaseDemand.addOneItem2Tips3')}`, {
        confirmButtonText: $t('common.confirm'),
        cancelButtonText: $t('common.cancel'),
        type: 'warning'
      }).then(() => {
        data.selectedRows = []
        done()
        $queryEngine.state.paginationManagement.refresh()
        const source = data.sourceTypeList.find((item: any) => item.value === values.globalSourceType)
        app.$router.push({
          name: source.componentName,
          params: {
            from: 'demandPoolManagement',
            funName: source.componentName,
            formId: projectId,
            formNo: souNo,
            type: 'edit'
          }
        })
      }).catch(() => {
        data.selectedRows = []
        done()
      })
    }
  }).catch(() => { closeLoading() })
}

export default {
  type: 'void',
  title: i18nExpression('purchaseDemand.createInquiry'), // 创建寻源单据
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'small',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
        if ( type === 'ok') { 
          $self.query('*.createInquiryDialog.form').take().submit(values => {
            $createInquiryFetch($form,$queryEngine,values,done,$confirm,$t,$http,app,closeLoading)   
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
        globalSourceType: {
          type: 'string',
          title: i18nExpression('purchaseDemand.globalSourceTypeTips'), // 请选择寻源方式
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'PrRequirementPoolForBuyer\').get(\'data\').sourceTypeList'),
          'x-component': 'Select',
          ...requiredValidatorSegment
        }
      })
    }
  }
}
export {
  createInquiryFetch
}

