<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-tabs
        v-model="activeTabName"
        :class="{'order-and-flow-tab-srm-env': isSrmEnv, 'order-and-flow-tab-bpm-env': !isSrmEnv}"
        :before-leave="beforeLeave"
        @tab-click="tabClick"
      >
        <el-tab-pane
          name="order"
          class="srm-approval-process-order-tab"
          :label="$t('cusEntry.approval.orderInfo')"
        >
          <!--头部操作栏-->
          <HeaderButtonList
            v-if="showHeaderBtn"
            :business-id="businessId"
            :business-type="businessType"
            :flow-node-list.sync="flowNodeList"
            :operation-pre-options="operationPreOptions"
            :approve-record-list.sync="approvalRecords"
            @approval-handler-callback="type => $emit('approval-handler-callback', type)"
            @update-approval-record="getApprovalRecords"
            @get-flow-node-list="data => $emit('get-flow-node-list', data)"
          />
          <div v-if="businessType === 'SOU_CA'||businessType === 'RCOMMVENDOR'||businessType === 'SOU_TN'" class="srm-approval-process-title">
            <span v-if="!!businessType">{{$getDictLabel('BPM2_ZBUSINESS_TYPE', businessType)}}</span>
            <span v-if="!!projectName">-{{projectName}}</span>
            <span v-if="!!round">-{{$getDictLabel('NPM_CA_PRICE_ROUND', round)}}</span>
          </div>
          <!--审批单据内容区域-->
          <div class="srm-approval-process-container">
            <slot />
          </div>
          <!--审批记录-->
          <ApprovalRecordList
            v-if="showApprovalRecord"
            :show-operation="false"
            :approval-records.sync="approvalRecords"
          />
          <!--底部操作栏-->
          <FooterButtonList
            v-if="showOrderTabFooterBtn"
            :approval-status="approvalStatus"
            :show-save-and-next-step="showSaveAndNextStep"
            :status-map="statusMap"
            :readonly="readonly"
          >
            <slot name="custom" />
            <el-button
              v-if="showRecallBtn && btnPermissionConfig.recall"
              type="primary"
              @click="approvalHandler('recall')"
            >
              {{ $t('cusEntry.approval.recall') }}
            </el-button>
            <el-button
              v-if="showAbandonBtn && btnPermissionConfig.abandon && hasPermission('abandon')"
              type="primary"
              @click="approvalHandler('abandon')"
            >
              {{ $t('cusEntry.approval.abandon') }}
            </el-button>
            <el-button
              v-if="showSaveAndNextStep"
              type="primary"
              @click="approvalHandler('save')"
            >
              {{ $t('cusEntry.approval.save') }}
            </el-button>
            <el-button
              v-if="showSaveAndNextStep"
              type="primary"
              @click="approvalHandler('nextStep')"
            >
              {{ $t('cusEntry.approval.nextStep') }}
            </el-button>
          </FooterButtonList>
        </el-tab-pane>
        <el-tab-pane
          v-if="showApprovalNodeTab"
          :label="$t('cusEntry.approval.approvalNodes')"
          name="approval"
          :disabled="approvalTabDisabled"
        >
          <!--审批记录-->
          <ApprovalRecordList
            :editable="showSaveAndNextStep"
            :approval-records.sync="approvalRecords"
          >
            <p>{{ $t('cusEntry.approval.approvalRecord') }}</p>
          </ApprovalRecordList>
          <!--底部操作栏-->
          <FooterButtonList
            v-if="showSaveAndNextStep"
            :show-save-and-next-step="showSaveAndNextStep"
            :business-id="businessId"
            :business-type="businessType"
            :approval-records="approvalRecords"
            :approval-status="approvalStatus"
            @approval-handler-callback="type => $emit('approval-handler-callback', type)"
          />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import HeaderButtonList from './header-button-list'
import FooterButtonList from './footer-button-list'
import ApprovalRecordList from './approval-record-list'
import { approvalApi } from './api'
export default {
  name: 'ApprovalProcess',
  components: {
    HeaderButtonList,
    FooterButtonList,
    ApprovalRecordList
  },
  props: {
    // 业务单据ID
    businessId: {
      type: [Number, String],
      default: null
    },
    // 流程类型/流程模板
    businessType: {
      type: String,
      default: ''
    },
    // 操作前置配置
    operationPreOptions: {
      type: Object,
      default: () => ({})
    },
    // 是否显示单据页底部操作栏
    showOrderTabFooterBtn: {
      type: Boolean,
      default: true
    },
    // 是否显示审批节点页底部操作栏
    showApprovalTabFooterBtn: {
      type: Boolean,
      default: true
    },
    // 审批状态 (DRAFT、SUBMITTED、APPROVED、REJECTED、WITHDRAW、ABANDONED)
    approvalStatus: {
      type: String,
      default: 'DRAFT'
    },
    projectName: {
      type: String,
      default: ''
    },
    round: {
      type: String,
      default: ''
    },
    // 审批状态的映射关系
    statusMap: {
      type: Object,
      default: () => ({
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'SUBMITTED', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      })
    },
    // tab页签切换前置钩子
    beforeLeave: {
      type: Function,
      default: () => true
    },
    // 审批页签是否可编辑
    approvalTabDisabled: {
      type: Boolean,
      default: true
    },
    // 展示按钮配置
    showButtonConfig: {
      type: Object,
      default: () => ({})
    },
    // 是否只读
    readonly: {
      type: Boolean,
      default: false
    },
    // 显示审批页签配置
    showTabConfig: {
      type: Object,
      default: () => ({})
    },
    // 是否显示单据页审批记录
    showApprovalTabRecord: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      isSrmEnv: false, // 是否SRM环境
      activeTabName: 'order',
      approvalRecords: [],
      flowNodeList: [],
      promoter: ['申请人', '人工任务'],
      btnPermissionConfig: {
        recall: true,
        abandon: true
      },
      actionsMap: new Map([
        ['pass', '通过'],
        ['return', '退回'],
        ['returnDirect', '退回直达'],
        ['returnPre', '退回招标负责人'],
        ['transfer', '转办'],
        ['end', '结束'],
        ['abandon', '废弃']
      ])
    }
  },
  computed: {
    // 控制撤回按钮的显示, 已提交状态显示
    showRecallBtn () {
      // 获取自定义展示按钮配置
      const preConfigKeys = Object.keys(this.showButtonConfig)
      return preConfigKeys.includes('recall') ? this.showButtonConfig.recall : this.approvalStatus === this.statusMap.SUBMITTED
    },
    // 控制废弃按钮的显示, 已驳回、已撤回状态下显示
    showAbandonBtn () {
      const {
        REJECTED,
        WITHDRAW
      } = this.statusMap
      // 获取自定义展示按钮配置
      const preConfigKeys = Object.keys(this.showButtonConfig)
      return preConfigKeys.includes('end') ? this.showButtonConfig.end : [REJECTED, WITHDRAW].includes(this.approvalStatus)
    },
    // 控制暂存、下一步按钮显示
    showSaveAndNextStep () {
      const {
        DRAFT,
        REJECTED,
        WITHDRAW
      } = this.statusMap
      const preConfigKeys = Object.keys(this.showButtonConfig)
      return preConfigKeys.includes('saveAndNextStep') ? this.showButtonConfig.saveAndNextStep : [DRAFT, REJECTED, WITHDRAW].includes(this.approvalStatus) && !this.readonly
    },
    // 控制是否显示头部按钮
    showHeaderBtn () {
      return this.approvalStatus === this.statusMap.SUBMITTED
    },
    // 控制是否展示审批节点页签
    showApprovalNodeTab () {
      const {
        REJECTED,
        WITHDRAW,
        DRAFT
      } = this.statusMap
      // 获取自定义展示页签配置
      const tabConfigKeys = Object.keys(this.showTabConfig)
      return tabConfigKeys.includes('approval') ? this.showTabConfig.approval : [REJECTED, WITHDRAW, DRAFT].includes(this.approvalStatus)
    },
    // 控制是否显示审批记录
    showApprovalRecord () {
      return this.showApprovalTabRecord && this.statusMap.DRAFT !== this.approvalStatus
    },
    // 预执行触发参数
    predictParams () {
      return JSON.parse(JSON.stringify({ businessId: this.businessId, businessType: this.businessType, approvalStatus: this.approvalStatus }))
    }
  },
  watch: {
    // 监听状态获取审批记录
    approvalStatus: {
      immediate: true,
      handler (newValue) {
        if (newValue && this.statusMap.DRAFT !== newValue) {
          this.getApprovalRecords()
        }
      }
    },
    // 监听节点记录控制撤回、显示
    flowNodeList: {
      immediate: true,
      handler (newValue, oldValue) {
        if (JSON.stringify(newValue) != JSON.stringify(oldValue)) {
          const currentUser = this.$store.getters.userInfo.username
          const promoter = this.flowNodeList.find(item => this.promoter.includes(item.activityName))?.executorId
          this.btnPermissionConfig.recall = currentUser == promoter
          this.btnPermissionConfig.abandon = currentUser == promoter
        }
      }
    },
    predictParams: {
      immediate: true,
      deep: true,
      handler (newValue, oldValue) {
        if (JSON.stringify(newValue) !== JSON.stringify(oldValue) && [this.statusMap.SUBMITTED, this.statusMap.REJECTED, this.statusMap.WITHDRAW].includes(this.approvalStatus)) {
          this.getFlowNodes()
        }
      }
    },
    $route: {
      immediate: true,
      deep: true,
      handler (to, from) {
        console.log(to)
        // 借助链接地址解决样式兼容问题
        const query = to.params.id ? this.query(to.params.id) : {}
        if (query.from === 'fromFun') {
          this.isSrmEnv = false
        } else {
          this.isSrmEnv = true
        }
      }
    }
  },
  methods: {
    // 当前用户是否有相应的按钮操作权限
    hasPermission (type) {
      let result = []
      const curUser = this.$store.getters.userInfo.username
      // 判断当前用户是否在正在办理节点
      const isCurHandler = this.flowNodeList.find(node => node.taskStatus == 1)?.executor.filter(item => item.isEnd == 0).map(itm => itm.userId).includes(curUser)
      if (isCurHandler) {
        // 获取当前用户可操作的节点权限
        const currentNode = this.flowNodeList.find(item => item.taskStatus == 1)
        result = currentNode.commentMenus || []
      }
      return result.includes(this.actionsMap.get(type))
    },
    // 判断是否移动端打开
    isMobileDevice () {
      let mobile = false
      if (navigator.userAgent.match(/(iPhone|iPod|Android|ios|iPad)/i)) {
        mobile = true
      }
      return mobile
    },
    // 解析路由参数
    query (params) {
      const atobId = atob(params.replaceAll('-', '+').replaceAll('_', '/').replaceAll('.', '='))
      const query = {}
      const keyValueArr = atobId.split('&')
      keyValueArr.forEach(keyValue => {
        const [key, value] = keyValue.split('=')
        const decodedKey = decodeURIComponent(key)
        const decodedValue = decodeURIComponent(value)
        query[decodedKey] = isNaN(decodedValue) ? decodedValue : Number(decodedValue)
      })
      return query
    },
    // 撤回、废弃、暂存、下一步
    async approvalHandler (type) {
      // 获取审批操作的前置配置
      const preConfigKeys = Object.keys(this.operationPreOptions)
      if (preConfigKeys.includes(type)) {
        // 配置类型是否与操作类型匹配
        const preResult = await this.operationPreOptions[type]()
        if (!preResult) return
      }
      let nodeResult = null
      switch (type) {
      // 下一步
      case 'nextStep':
        // 获取流程节点信息
        nodeResult = await approvalApi.nextStep({ businessId: this.businessId, businessType: this.businessType })
        this.approvalRecords = nodeResult.data.map(item => ({
          ...item,
          createUserName: item.executor?.map(itm => itm.userName).join(),
          createUser: item.executor?.map(itm => itm.userId).join()
        }))
        this.activeTabName = 'approval'
        // 操作成功触发事件
        this.$emit('approval-handler-callback', type)
        break
      // 撤回
      case 'recall':
        // 操作弹窗
        this.$prompt('', this.$t('cusEntry.approval.approvalMessage'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          showClose: false,
          closeOnClickModal: false,
          inputPlaceholder: this.$t('cusEntry.approval.pleaseEnterApprovalMessage'),
          inputType: 'textarea',
          customClass: this.isMobileDevice() ? 'srm-approval-prompt' : null
        }).then(async ({ value }) => {
          const submitParams = {
            dataId: this.businessId,
            bussinessType: this.businessType,
            commentmsg: value
          }
          const res = await approvalApi[type](submitParams)
          if (res.code == 0) {
            this.$message.success(this.$t('cusEntry.common.operationSuccess'))
          }
          // 重新获取节点信息
          const { data } = await approvalApi.nextStep({ businessId: this.businessId, businessType: this.businessType })
          this.$emit('update:flowNodeList', data)
          // 更新审批记录
          this.getApprovalRecords()
          // 操作成功发布事件
          this.$emit('approval-handler-callback', type)
        }).catch((err) => {
          if (err === 'cancel') {
            this.$message({
              type: 'info',
              message: '取消操作'
            })
          }
        })
        break
      // 废弃
      case 'abandon':
        this.$prompt('', this.$t('cusEntry.approval.approvalMessage'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          showClose: false,
          closeOnClickModal: false,
          inputPlaceholder: this.$t('cusEntry.approval.pleaseEnterApprovalMessage'),
          inputType: 'textarea',
          customClass: this.isMobileDevice() ? 'srm-approval-prompt' : null
        }).then(async ({ value }) => {
          const submitParams = {
            businessId: this.businessId,
            businessType: this.businessType,
            taskInstId: this.flowNodeList.find(item => item.taskStatus == 1)?.executor.find(itm => itm.userId === this.$store.getters.userInfo.username)?.taskInstId,
            commentInfo: {
              actionName: this.actionsMap.get(type),
              commentMsg: value
            }
          }
          const res = await approvalApi.abandon(submitParams)
          this.$message.success(this.$t('cusEntry.common.operationSuccess'))
          // 操作成功发布事件
          this.$emit('approval-handler-callback', type)
        }).catch((err) => {
          if (err === 'cancel') {
            this.$message({
              type: 'info',
              message: '取消操作'
            })
          }
        })
        break
      case 'save':
        // 操作成功发布事件
        this.$emit('approval-handler-callback', type)
        break
      default:
        break
      }
    },
    // 标签页选中
    tabClick (instance) {
      this.$emit('tab-click', instance)
    },
    // 获取审批记录
    getApprovalRecords () {
      if (!this.getFlowNode && !this.showApprovalTabRecord) {
        return
      }
      const submitParams = {
        businessType: this.businessType,
        businessId: this.businessId
      }
      approvalApi.getApprovalRecord(submitParams).then(res => {
        if (res.data) {
          this.approvalRecords = res.data
        }
      })
    },
    // 获取流程节点信息
    getFlowNodes () {
      if (!this.businessId || !this.businessType) {
        return false
      }
      const submitParams = {
        businessId: this.businessId,
        businessType: this.businessType
      }
      approvalApi.nextStep(submitParams).then(res => {
        if (res.data) {
          this.flowNodeList = res.data
          this.$emit('get-flow-node-list', res.data)
        }
      })
    }
  }
}
</script>

<style lang="scss">
// .app-main .el-container .el-main {
//   overflow-y: hidden !important;
// }
.srm-approval-process-title{
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 10px;
  font-weight: 700;
  font-size:20px
}
.order-and-flow-tab-srm-env {
  >.el-tabs__content{
    height:calc(100vh - 195px) !important;
    overflow-y: auto;
  }
}
.order-and-flow-tab-bpm-env {
  >.el-tabs__content{
    height:calc(100vh - 72px) !important;
    overflow-y: auto;
  }
}
.srm-approval-prompt {
  width: 350px !important;
}
</style>
