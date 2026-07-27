import { defineComponent, ref, watch, computed } from 'vue-demi'
// import { Message } from '@meicloud/element-ui'
// @ts-ignore
import CWorkflow from '@/library/components/c-workflow/multi.vue'
import {
  h,
  RecursionField,
  useFieldSchema,
  observer,
  useField,
  useAutoMountInstanceToField
} from '@meicloud/render-engine'
// @ts-ignore
import { flowAPI } from '@/service/modules/base'
import { useSetupContext } from '../../../composables/useSetupContext'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

type IWorkflowParamsInfo = {
  businessType: string
  businessId: number | null
  businessVariables: Record<string, any>
  integrationMode: string
  tabDisabled: boolean
  version: Date
  workflowActive: boolean
  approveStatus: string
}

export const SchemaWorkflow = observer(
  defineComponent({
    name: 'SchemaWorkflow',
    inheritAttrs: false,
    setup(props, { attrs, slots,listeners, expose, emit }) {
      const field = useField()
      const fieldSchema = useFieldSchema()
      const { t: $t } = usePageHelper()

      const refName = field.value.componentProps['ref-name'] || 'workflowMulti'
      const businessId = field.value.componentProps['business-id'] || null
      const businessType = field.value.componentProps['business-type'] || ''
      // const clickHandler = listeners['click-handler'] || ((type: string) => {})
      // const submitDirect = listeners['submit-direct']|| ((type: string) => {})
      // const confirmHandler = listeners['confirm-handler'] || ((type: string, comment: any) => {})
      // const closeTab = listeners['close-tab'] || (() => {})

      // debugger
      const { currentInstance } = useSetupContext()

      useAutoMountInstanceToField()

      const workflowParamsInfo = ref<IWorkflowParamsInfo>({
        businessType: businessType,
        businessId: businessId,
        businessVariables: {},
        integrationMode: '',
        tabDisabled: true,
        version: new Date(),
        workflowActive: false,
        approveStatus: ''
      })

      const buttonConfigInfo = ref<Record<string, any>>({
        cancel: {
          name: $t('common.cancel'),
          view: true,
          disabled: false
        },
        close: {
          name: $t('common.close'),
          view: true,
          disabled: false
        },
        save: {
          name: $t('common.staging'),
          view: true,
          disabled: false
        },
        submit: {
          name: $t('common.submit'),
          view: true,
          disabled: false
        },
        withdraw: {
          name: $t('common.withdraw'),
          view: false,
          disabled: false,
          code: '' // 按钮权限code
        }
      })

      const buttonCustom = ref({})

      if (field.value.componentProps['button-custom']) {
        buttonCustom.value = field.value.componentProps['button-custom']
      }

      const activeTabName = ref('bizTab')

      const queryTodoList = ref([])

      const flowWithTabMode = ref(['Product', 'IdeFlow'])

      const _businessId = ref<number | null>(null)

      _businessId.value = businessId

      // 父类实现此方法，获取业务ID
      const workflowBusinessId = computed(() => _businessId || null)

      const setWorkflowBusinessId = (newBusinessId: number | null) => {
        _businessId.value = newBusinessId
      }

      // 父类实现此方法
      const _tabDisabled = ref(true)

      // 父类实现此方法，定义审批Tab是否可以点击
      const workflowTabDisabled = computed(() => !!_tabDisabled.value)

      const setWorkflowTabDisabled = (newTabDisabled: boolean) => {
        _tabDisabled.value = newTabDisabled
      }

      /**
       *
       * 对于需要自定义表单参数的需要实现以下方法, 默认为未设置
       * 业务数据，用于分支，预留分支字段Field01到Field05
       */
      const workflowBusinessVariables = ref({})

      const setWorkflowBusinessVariables = (businessVariables: any) => {
        workflowBusinessVariables.value = businessVariables
        workflowParamsInfo.value.businessVariables = businessVariables
      }

      // 设置表单状态
      const setWorkflowApproveStatus = (status: any) => {
        // console.log(status)
        workflowParamsInfo.value.approveStatus = status
      }
      // 手动设置表单模板编码
      const setWorkflowBusinessType = (type: string) => {
        workflowParamsInfo.value.businessType = type
        getFlowIntegrationMode()
      }

      // 是否打开页面自动跳到流程tab页面
      const autotTriggerFlowTab = computed(() => {
        return !workflowTabDisabled.value && (attrs.params && attrs.params.activeWorkflowTab)
      })

      // // 工作流类型设置
      // const getWorkflowBusinessType = () => {
      //   return new Promise<string>(resolve => {
      //     Message({
      //       message: '未设置工作流类型',
      //       type: 'error'
      //     })
      //     resolve('')
      //   })
      // }
      const setWorkflowIntegrationMode = async () => {
        if (workflowParamsInfo.value.integrationMode) return
        let res = await flowAPI.getFlowIntegrationMode({ businessType: workflowParamsInfo.value.businessType })
        if (res.data) {
          workflowParamsInfo.value.integrationMode = res.data
        }
      }

      // 获取集成模式
      const getFlowIntegrationMode = async () => {
        if (workflowParamsInfo.value.integrationMode || !workflowParamsInfo.value.businessType) return
        // workflowParamsInfo.value.businessType = await getWorkflowBusinessType() || ''
        // console.log(workflowParamsInfo.value.businessType)
        let res = await flowAPI.getFlowIntegrationMode({ businessType: workflowParamsInfo.value.businessType })
        if (res.data) {
          workflowParamsInfo.value.integrationMode = res.data
          // console.log('workflowParamsInfo.value.integrationMode', workflowParamsInfo.value )
          emit('update-integration-mode', res.data)
        }
      }

      /**
       * 获取单据信息
       */
      const getOrderData = () => {
        return new Promise<Record<string,any>>(resolve => {
          resolve({})
        })
      }

      /**
       * 获取单据附件ID
       */
      const getFileIds = () => {
        return new Promise<any[]>(resolve => {
          resolve([])
        })
      }

      const workflowActive = async () => {
        // console.log('workflowActive')
        if (!workflowBusinessId.value || !workflowTabDisabled.value) {
          return
        }
        if (workflowParamsInfo.value.integrationMode === null) {
          await getFlowIntegrationMode()
        }
        // 判断业务单据是否保存
        // 初始化工作流
        workflowParamsInfo.value.businessId = workflowBusinessId.value
        workflowParamsInfo.value.businessVariables = workflowBusinessVariables.value
        // console.log('this.workflowParamsInfo.businessVariables===', workflowParamsInfo.value.businessVariables)
        workflowParamsInfo.value.version = new Date()
        workflowParamsInfo.value.workflowActive = true
        // 切换到工作流tab页
        let workflowMode = flowWithTabMode.value.includes(workflowParamsInfo.value.integrationMode || '')
        // 自动跳转到流程tab
        if (workflowMode && autotTriggerFlowTab.value) {
          activeTabName.value = 'workflowTab'
        }
      }

      /**
       *
       * @param {*} type SUBMIT（提交）/SAVE（保存）
       */
      const handlerAfter = async (type: string, callback: () => void) => {
        // console.log('handlerAfter', workflowTabDisabled)
        if (type === 'SUBMIT') { // 提交审批跳过到审批流程tab
          let orderData = await getOrderData() || {}
          let filesIds = await getFileIds() || []
          let flowDataInfo = {
            businessData: orderData,
            fileuploadIds: filesIds,
            ...workflowParamsInfo.value
          }
          // console.log('flowDataInfo', flowDataInfo, workflowBusinessId, workflowTabDisabled, workflowBusinessVariables)

          const $vm: any = currentInstance?.$refs[refName]

          await $vm?.handlerAfter(type, flowDataInfo, callback) // 执行multiu页面的 handlerAfter 事件
          // 流程是tab的形式时跳到流程tab
          if (flowWithTabMode.value.includes(workflowParamsInfo.value.integrationMode || '')) {
            activeTabName.value = 'workflowTab'
          }
        }
      }

      // 工作流tab点击
      const workflowView = async (activeStatus: any) => {
        // console.log('workflowView')
        if (!activeStatus) {
          workflowParamsInfo.value.workflowActive = false
          return
        }
        await workflowActive()
      }


      const workflowHandler = async (operationType: any, callback: () => void) => {
        // console.log('workflowHandler')
        await workflowActive()
        callback && callback()
      }

      watch(() => workflowBusinessId.value, (val) => {
        // console.log('workflowBusinessId')
        workflowParamsInfo.value.businessId = workflowBusinessId.value
        if (attrs.params && attrs.params.activeWorkflowTab || !workflowTabDisabled.value) {
          workflowActive()
        }
      }, { immediate: true })

      watch(() => workflowTabDisabled.value, (val) => {
        // console.log(workflowTabDisabled)
        workflowParamsInfo.value.tabDisabled = workflowTabDisabled.value
        workflowParamsInfo.value.version = new Date()
      }, { immediate: true })

      expose({
        handlerAfter,
        setWorkflowBusinessId,
        setWorkflowTabDisabled,
        setWorkflowBusinessVariables,
        setWorkflowApproveStatus,
        getFlowIntegrationMode,
        setWorkflowIntegrationMode,
        setWorkflowBusinessType,
        buttonCustom,
        buttonConfigInfo,
        workflowParamsInfo
      })

      getFlowIntegrationMode()

      return () => {
        return h(
          CWorkflow,
          {
            ref: refName,
            props: {
              ...attrs,
              value: activeTabName.value,
              'fun-params': workflowParamsInfo.value,
              'button-config-info': buttonConfigInfo.value,
              'button-custom': buttonCustom.value
            },
            on: {
              ...listeners,
              'change': (val: string) => activeTabName.value = val,
              'tab-click': workflowView,
              'workflow-handler': workflowHandler,
              // 'click-handler': clickHandler,
              // 'submit-direct': submitDirect,
              // 'confirm': confirmHandler,
              // 'close-tab': closeTab
            },
          },
          {
            buttonOne: () => h(
              RecursionField,
              {
                props: {
                  schema: fieldSchema.value.items,
                  basePath:field.value.address,
                  onlyRenderProperties: true,
                },
              },
              {},
            ),
            default: () =>
              h(
                RecursionField,
                {
                  props: {
                    schema: fieldSchema.value,
                    basePath:field.value.address,
                    onlyRenderProperties: true,
                  },
                },
                {}
              ),
              ...slots
          }
        )
      }
    }
  })
)
