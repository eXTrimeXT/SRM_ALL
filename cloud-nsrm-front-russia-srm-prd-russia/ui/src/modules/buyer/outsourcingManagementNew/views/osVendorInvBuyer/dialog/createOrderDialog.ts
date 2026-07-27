import { expression, generateXindexInOrder } from '@meicloud/render-engine'

// 提交
export const $createOrder = async ($form: any, values: any, $message: any, $confirm: any, $queryEngine: any, done: any, closeLoading: any) => {
  try {
    const response = await $queryEngine.request.baseRequest({
      type: 'OsVendorInvBuyer',
      action: 'inputPageAnalysisData',
      query: {
        '*': {}
      },
      payload: [{
        ...values
      }]
    })
    done()
    if (response) {
      $message.success('开始同步库存...')
      $queryEngine.state.paginationManagement.refresh()
    }
  } catch {
    closeLoading()
  }
}

// 关闭打开弹窗
export const $openOrCloseDialog = ($form: any, visible: Boolean = true) => {
  $form.query('*.dialog').take().setComponentProps({
    visible
  })
  if (visible) {
    setTimeout(() => {
      $form.query('*.dialog.form').take((field: any) => {
        field.reset()
      })
    })
  }
}

export default {
  type: 'void',
  title: '创建盘点单',
  'x-component': 'RDialog',
  'x-component-props': {
    class: 'create-order-dialog',
    'close-on-click-modal': false,
    size: 'small',
    beforeClose: expression(`(done,type,closeLoading) => {
      if(type === 'ok'){
        $form.query('*.dialog.form').take().submit(values => {
          console.log('values',values)
          $createOrder($form,values,$message,$confirm,$queryEngine,done,closeLoading)
        }).catch(() => {closeLoading()})
      }else{
        done()
      }
    }`)
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
        maxColumns: 1,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        invTaskTitle: {
          type: 'string',
          title: '本次盘点名称',
          required: true,
          'x-decorator': 'FormItem'
        },
        taskRule: {
          type: 'string',
          required: true,
          title: '盘点方式',
          default: 'VENDOR_DIVISION',
          'x-decorator': 'FormItem',
          'x-component': 'Radio.Group',
          enum: [
            // {
            //   label: '盘点所有',
            //   value: 'ALL'
            // },
            {
              label: '按供应商分工规则',
              value: 'VENDOR_DIVISION'
            }
          ]
        }
      })
    }
  }
}
