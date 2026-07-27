export * from './multi'

/*

集成方法:

1. 最外层schema配置 schemaKey xxxx

xxxx: {
  type: 'void',
  'x-decorator': 'QueryEngine',
  'x-component': 'el-container',
  'x-component-props': {
    class: 'flex-container',
    direction: 'vertical'
  },
}

注意需要加上 'x-decorator': 'QueryEngine',

2. xxxx 的 properties 属性包裹 SchemaWorkflow

properties: {
  SchemaWorkflow: {
    type: 'void',
    'x-component': 'SchemaWorkflow',
    'x-component-props': {
      'business-id': expression(`attrs.params.row?.perPlanId || null`),
      'business-type': 'PERFORM_PLAN',
      'button-custom': expression(`{
        PASS: {
          name: t('common.toApprove'),
          view: true,
          disabled: false
        },
        REJECT: {
          name: t('orderMod.buyerOrderSynergy.sureRefuse'),
          view: true,
          disabled: false
        }
      }`),
      '@click-handler': expression(`(type) => {
        console.log('click-handler', type)
      }`),
      '@submit-direct': expression(`(type) => {
        console.log('submit-direct', type)
      }`),
      '@confirm': expression(`(type, comment) => {
        console.log('confirm', type)
      }`),
      '@close-tab': expression(`() => {
        emitTabRemove(attrs.tabName)
        $bus.$emit('ModelHead')
      }`)
      '@update-integration-mode': expression(`(integrationMode) => {
        console.log('update-integration-mode', integrationMode)
        updateButtonConfig($form)
      }`)
    }
  }
}

主要配置 props属性 business-id, business-type, button-custom (定制的按钮组配置, 结构参考buttonConfigInfo)
然后配置 事件属性 @click-handler, @submit-direct, @confirm, @close-tab, @update-integration-mode(更新了integrationMode后的回调事件)
如果配置了button-custom, type 就是button-custom的key, 比如 PASS, REJECT, 根据type判断执行了哪个按钮事件

3. SchemaWorkflow 的 properties 属性包裹 原来的业务schema

properties: {
  layout: {
    type: 'void',
    'x-component': 'FormContainer',
    // 'x-decorator': 'QueryEngine',
    'x-reactions': expression(`async () => {
      initButtonConfig($form)
      let row = attrs.params.row
      if (row.perPlanId) {
        $getFormDetail(row.perPlanId, $form)
      }
    }`),
    properties: {
      ...
    }
  }
}

注意要把 'x-decorator': 'QueryEngine' 去掉, 因为最外层schema配置 schemaKey xxxx 已经配置过了, 无需重复配置

调用业务$getFormDetail函数加载表单内容.

另外加载的时候联动触发 initButtonConfig($form), 初始化按钮联动


4. 注意要把原来的业务schema的items配置的按钮事件去掉, SchemaWorkflow 已经通过事件 @click-handler, @submit-direct, @confirm, @close-tab做了处理

去掉:

// items: {
//   type: 'object',
//   properties: {
//     ....
//   }
// }

5. 接下来处理按钮的联动, 参考代码:


const workflowStatus = ref('DRAFT')

const $disabledFlag = computed(() => {
  return !!['view', 'approval', 'manage'].includes(attrs.params.flag)
})

const customUpdateButton = computed(() => (!$disabledFlag.value && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = computed(() => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(workflowStatus.value))
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = !$disabledFlag.value
    componentInstance.buttonConfigInfo.close.view = $disabledFlag.value
    componentInstance.buttonCustom.PASS.view = customUpdateButton.value
    componentInstance.buttonCustom.REJECT.view = customUpdateButton.value
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonCustom.PASS.view = customUpdateButton.value
    componentInstance.buttonCustom.REJECT.view = customUpdateButton.value
  }, 50)
}

上述方法仅供参考, 核心思路是 buttonConfigInfo 和 buttonCustom的 view (是否显示) 和 disabled (是否禁用) 配置
上述的customUpdateButton, viewUpdateButton,disabledUpdateButton 等 如果不用computed绑定, 可以通过其计算属性所依赖的表单项发生变化后手动类似 @change 事件触发 updateButtonConfig 方法

比如 viewUpdateButtonSave 依赖于 contractStatus, 则 contractStatus 发生 @change的时候触发 updateButtonConfig($form)

const viewUpdateButtonSave = ($form: any) => {
  return $form.values.needVendorConfirm != 'Y' &&
    (
      ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
      (attrs.params ? attrs.params.flag === 'add' : null)
    )
}

contractStatus: {
  type: 'string',
  title: i18nExpression('contractMod.status'),
  'x-decorator': 'FormItem',
  'x-component': 'DictSelect',
  'x-component-props': {
    code: 'CONTRACT_STATUS',
    disabled: true,
    '@change': expression(`() => updateButtonConfig($form)`)
  }
},


在业务代码的$getFormDetail函数中, 处理按钮联动

const $getFormDetail = (perPlanId: number, $form: any) => {
  return new Promise(resolve => {
    if (perPlanId) {
      performPlan.performPlan.getPerOrderById(perPlanId).then((res: any) => {
        const detailData = res.data
        ....

        workflowStatus.value = detailData.status

        $form.setValues(detailData)

        updateButtonConfig($form)

        resolve(detailData)
      })
    } else {
      resolve({})
    }
  })
}

使用
workflowStatus.value = detailData.status

updateButtonConfig($form)

6. 最关键一步, 接入工作流:

在 @click-handler, @submit-direct, @confirm 绑定的事件方法中,
根据 type判断执行了哪个按钮的事件,

在 $queryEngine.request.save(form).then((res: any) => {}) 保存数据数据,
然后拿到 业务id perPlanId (workflowBusinessId),
根据 perPlanId 调用 $getFormDetail(perPlanId, $form).then(() => {}) 更新表单数据
接着如果是提交按钮事件, 要接入工作流提交

const curAction = 'approval'
const tabDisabled = curAction ? curAction !== 'approval' : form.status === 'DRAFT'
const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
componentInstance.setWorkflowBusinessId(perPlanId)
componentInstance.setWorkflowTabDisabled(tabDisabled)
componentInstance.setWorkflowBusinessVariables({})
componentInstance.handlerAfter(type.toUpperCase())

关键配置 setWorkflowBusinessId, setWorkflowTabDisabled, setWorkflowBusinessVariables
然后 handlerAfter 触发提交

*/
