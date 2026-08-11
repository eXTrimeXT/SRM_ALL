// @ts-ignore
import { flowAPI } from '@/service/modules/base'
// @ts-ignore
/*
 接入步骤:
 1. 引入useWorkflow依赖
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { useWorkflow } from 'lib@/components/composables/useWorkflow'

const {app, emitTabRemove, t: $t, emitTabAdd, $http } = usePageHelper()
let attrs: any = useAttrs()
const { getDefaultWorkflowParamsInfo, getFlowIntegrationMode, submitWorkflow } = useWorkflow(app, $t, $http)

 2. defineSchemas 增加

 workflow: {
  type: 'void',
  'x-data': getDefaultWorkflowParamsInfo('PERFORM_PLAN')
 }

 4. 页面初始化加载时 'x-reactions': expression(`async () => getFlowIntegrationMode($form)`),  获取默认的integrationMode

 5. 提交审批时候调用代码
    submitWorkflow($form, workflowBusinessId, getWorkflowTabDisabled, getBusinessVariables).then(() => {
      emitTabRemove(attrs.tabName)
    })

  6. 参考例子: @/modules/contractPerformance/views/contractPerformancePlan/edit-engine.vue
*/
export const useWorkflow = (app: any, $t: any, $http: any) => {
  const $message = app.$message
  const $confirm = app.$confirm

  const getDefaultWorkflowParamsInfo = (businessType = '', businessVariables = {}, businessData = {}, options = {}) => {
    let defaultConfig = {
      businessType: businessType,
      businessId: null,
      businessVariables: businessVariables,
      businessData: businessData, // orderData
      integrationMode: null,
      tabDisabled: true,
      fileuploadIds: [], // fileIds
      version: new Date(),
      workflowActive: false
    }
    return { ...defaultConfig, ...options }
  }

  const getFlowIntegrationMode = ($form: any, schemaDataKey = '.workflow') => {
    return new Promise(resolve => {
      const workflowData = $form.query(schemaDataKey).take().data || {}
      console.log('getFlowIntegrationMode', workflowData)
      let integrationMode = workflowData.integrationMode
      if (integrationMode) {
        resolve(null)
        return
      }
      const businessType = workflowData.businessType
      flowAPI.getFlowIntegrationMode({ businessType: businessType }).then((res: any) => {
        if (res.data) {
          workflowData.integrationMode = res.data
          resolve(res.data)
          // $form.query(schemaDataKey).take().setData(curData)
        } else {
          resolve(null)
        }
      })
    })
  }

  const workflowActive = (workflowData: any, workflowBusinessId: string, getWorkflowTabDisabled: () => boolean, getBusinessVariables: () => any, $form: any) => {
    return new Promise(resolve => {
      if (!workflowBusinessId || getWorkflowTabDisabled()) {
        resolve(null)
        return
      }

      const handleWorkflowData = () => {
        // 判断业务单据是否保存
        // 初始化工作流
        workflowData.businessId = workflowBusinessId
        workflowData.businessVariables = getBusinessVariables()
        workflowData.version = new Date()
        workflowData.workflowActive = true

        // 切换到工作流tab页
        // let workflowMode = flowWithTabMode.includes(this.workflowParamsInfo.integrationMode)
        // // 自动跳转到流程tab
        // if (workflowMode && autotTriggerFlowTab) {
        //   activeTabName = 'workflowTab'
        // }
      }

      if (workflowData.integrationMode === null) {
        getFlowIntegrationMode($form).then(() => {
          handleWorkflowData()
          resolve(null)
        })
      } else {
        handleWorkflowData()
        resolve(null)
      }
    })
  }

  const submitEngine = (flowDataInfo: any) => {
    return new Promise((resolve, reject) => {
      $confirm($t('common.sureSubmit'), {
        confirmButtonText: $t('common.confirm') as string,
        cancelButtonText: $t('common.cancel') as string,
        type: 'warning'
      }).then(() => {
        $http({
          url: '/api-base/flow/event/submitEngine',
          method: 'POST',
          data: flowDataInfo,
          loading: true
        }).then((res: any) => {
          console.log('[submitEngine]', res)
          $message({
            type: 'success',
            message: $t('components.approvalHead.tips.approvalCompletion')
          })
          resolve(null)
        })
      }).catch((e: any) => {
        console.error('submitEngine submit error', e)
        if (e.code && e.message) {
          $message.error(e.message)
        }
        reject(null)
      })
    })
  }

  const submitWorkflow = ($form: any, workflowBusinessId: string, getWorkflowTabDisabled: () => boolean, getBusinessVariables: () => {}, schemaDataKey = '.workflow') => {
    return new Promise((resolve, reject) => {
      const workflowData = $form.query(schemaDataKey).take().data || {}
      const integrationMode = workflowData.integrationMode
      const businessData = workflowData.businessData || {}
      const fileuploadIds = workflowData.fileuploadIds || []

      const flowDataInfo = {
        ...workflowData,
        businessId: workflowBusinessId,
        businessData: businessData,
        fileuploadIds: fileuploadIds
      }

      const WORK_FLOW_MODE = {
        PRODUCT: 'Product',
        IFRAME: 'Iframe',
        SELF: 'Self',
        PUSH: 'Push',
        NONE: 'None',
        IDEFLOW: 'IdeFlow',
        IDESDK: 'IdeSdk'
      }

      console.log('flowDataInfo', flowDataInfo)

      // const flowWithTabMode = ['Product', 'IdeFlow']

      let handler: any = () => {
        return new Promise((resolve, reject) => resolve(null))
      }

      // step2: 根据工作模式，分别处理
      switch (integrationMode) {
        // 产品工作流模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.PRODUCT:
          handler = workflowActive(workflowData, workflowBusinessId, getWorkflowTabDisabled, getBusinessVariables, $form)
          break
        // IDE工作流
        case WORK_FLOW_MODE.IDEFLOW:
          handler = workflowActive(workflowData, workflowBusinessId, getWorkflowTabDisabled, getBusinessVariables, $form)
          break
        // iframe嵌入页面模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.IFRAME:
          handler = workflowActive(workflowData, workflowBusinessId, getWorkflowTabDisabled, getBusinessVariables, $form)
          break
        // 自带页面模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.SELF:
          handler = workflowActive(workflowData, workflowBusinessId, getWorkflowTabDisabled, getBusinessVariables, $form)
          break
        // 无页面推送模式，填写提交人意见后调用submitEngine
        case WORK_FLOW_MODE.PUSH:
          handler = submitEngine(flowDataInfo)
          break
        // 无工作流，直接调用提交数据接口，无需执行
        case WORK_FLOW_MODE.NONE:
          handler = submitEngine(flowDataInfo)
          break
        // IDESDK推送模式
        case WORK_FLOW_MODE.IDESDK:
          handler = submitEngine(flowDataInfo)
          break
        default:
      }

      handler.then(() => {
        // 流程是tab的形式时跳到流程tab
        // if (flowWithTabMode.includes(integrationMode)) {
        //   activeTabName = 'workflowTab'
        // }

        // emitTabRemove(tabName)
        resolve(null)
      })
    })
  }

  return {
    getDefaultWorkflowParamsInfo,
    getFlowIntegrationMode,
    submitEngine,
    workflowActive,
    submitWorkflow
  }
}
