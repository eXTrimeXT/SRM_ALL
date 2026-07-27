import CWorkflowButton from 'lib@/components/c-workflow-button'
import CWorkflowMulti from 'lib@/components/c-workflow/multi'

export default {
  components: {
    CWorkflowMulti,
    CWorkflowButton
  },
  data () {
    return {
      workflowParamsInfo: {
        businessType: '',
        businessId: null,
        businessVariables: {},
        integrationMode: null,
        tabDisabled: true,
        version: new Date(),
        workflowActive: false,
        approveStatus: ''
      },
      buttonConfigInfo: {
        cancel: {
          name: this.$t('common.cancel'),
          view: true,
          disabled: false
        },
        close: {
          name: this.$t('common.close'),
          view: true,
          disabled: false
        },
        save: {
          name: this.$t('common.staging'),
          view: true,
          disabled: false,
          code: '' // 按钮权限code
        },
        submit: {
          name: this.$t('common.submit'),
          view: true,
          disabled: false,
          code: '' // 按钮权限code
        },
        withdraw: {
          name: this.$t('common.withdraw'),
          view: false,
          disabled: false,
          code: '' // 按钮权限code
        }
      },
      buttonCustom: {
      },
      activeTabName: 'bizTab',
      queryTodoList: []
    }
  },
  async created () {
    await this.getFlowIntegrationMode()
  },
  computed: {
    // 父类实现此方法，获取业务ID
    workflowBusinessId () {
      return null
    },
    // 父类实现此方法，定义审批Tab是否可以点击
    workflowTabDisabled () {
      return true
    },
    // 是否打开页面自动跳到流程tab页面
    autotTriggerFlowTab () {
      return !this.workflowTabDisabled && this.$attrs.params?.activeWorkflowTab
    }
  },
  watch: {
    workflowBusinessId: {
      async handler () {
        this.workflowParamsInfo.businessId = this.workflowBusinessId
        if (this.$attrs.params?.activeWorkflowTab || !this.workflowTabDisabled) {
          await this.workflowActive()
        }
      },
      immediate: true
    },
    workflowTabDisabled: {
      handler () {
        // console.log('this.workflowTabDisabled===', this.workflowTabDisabled)
        this.workflowParamsInfo.tabDisabled = this.workflowTabDisabled
        this.workflowParamsInfo.version = new Date()
      },
      immediate: true
    }

  },
  methods: {
    // 工作流类型设置
    async getWorkflowBusinessType () {
      this.$message({
        message: this.$t('common.unSetFlow'),
        type: 'error'
      })
    },
    /**
     *
     * 对于需要自定义表单参数的需要实现以下方法, 默认为未设置
     * 业务数据，用于分支，预留分支字段Field01到Field05
     */
    async getWorkflowBusinessVariables () {
      return {}
    },
    /**
     * 获取CWorkflow
     */
    getCWorkflowRefName () {
      this.$message({
        message: this.$t('common.cannotGetRefname'),
        type: 'error'
      })
    },
    /**
     * 获取单据信息
     */
    async getOrderData () {
      return {}
    },

    /**
     * 获取单据附件ID
     */
    async getFileIds () {
      return []
    },

    /**
     * @param type {*} SUBMIT（提交）/SAVE（保存）
     * @param fromMulti 来自multi形式调用 默认Y
     * @returns {Promise<void>}
     */
    async handlerAfter (type, fromMulti = 'Y') {
      if (type === 'SUBMIT') { // 提交审批跳过到审批流程tab
        let orderData = await this.getOrderData() || {}
        let filesIds = await this.getFileIds() || []
        let formBusinessVariables = await this.getWorkflowBusinessVariables()
        let flowDataInfo = {
          businessData: orderData,
          fileuploadIds: filesIds,
          ...this.workflowParamsInfo,
          businessVariables: formBusinessVariables
        }
        if (fromMulti === 'Y') {
          let refName = await this.getCWorkflowRefName()
          this.workflowParamsInfo.businessVariables = formBusinessVariables
          await this.$refs[refName].handlerAfter(type, flowDataInfo) // 执行multiu页面的 handlerAfter 事件
          // 流程是tab的形式时跳到流程tab
          if (this.flowWithTabMode.includes(this.workflowParamsInfo.integrationMode)) {
            this.activeTabName = 'workflowTab'
          }
        } else {
          // 否则返回flowDataInfo
          return flowDataInfo
        }
      }
    },
    // 工作流tab点击
    async workflowView (activeStatus) {
      if (!activeStatus) {
        this.workflowParamsInfo.workflowActive = false
        return
      }
      await this.workflowActive()
    },
    async workflowHandler (operationType) {
      await this.workflowActive()
    },
    async workflowActive () {
      if (!this.workflowBusinessId || this.workflowTabDisabled) {
        return
      }
      if (this.workflowParamsInfo.integrationMode === null) {
        await this.getFlowIntegrationMode()
      }
      // 判断业务单据是否保存
      // 初始化工作流
      this.workflowParamsInfo.businessId = this.workflowBusinessId
      this.workflowParamsInfo.businessVariables = await this.getWorkflowBusinessVariables()
      // console.log('this.workflowParamsInfo.businessVariables===', this.workflowParamsInfo.businessVariables)
      this.workflowParamsInfo.version = new Date()
      this.workflowParamsInfo.workflowActive = true

      // 切换到工作流tab页
      let workflowMode = this.flowWithTabMode.includes(this.workflowParamsInfo.integrationMode)
      // 自动跳转到流程tab
      if (workflowMode && this.autotTriggerFlowTab) {
        this.activeTabName = 'workflowTab'
      }
    },
    async getFlowIntegrationMode () {
      if (this.workflowParamsInfo.integrationMode) return
      this.workflowParamsInfo.businessType = await this.getWorkflowBusinessType()
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: this.workflowParamsInfo.businessType })
      if (res.data) {
        this.workflowParamsInfo.integrationMode = res.data
      }
    }
  }
}
