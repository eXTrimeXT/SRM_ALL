<template>
  <div class="srm-footer-btn-group">
    <slot>
      <template v-if="[statusMap.DRAFT, statusMap.REJECTED, statusMap.WITHDRAW].includes(approvalStatus) && !readonly || showSaveAndNextStep">
        <!-- <el-button
          type="primary"
          @click="approvalHandler('save')"
        >
          {{ $t('cusEntry.approval.save') }}
        </el-button> -->
        <el-button
          type="primary"
          @click="approvalHandler('submit')"
        >
          {{ $t('cusEntry.approval.submit') }}
        </el-button>
      </template>
    </slot>
  </div>
</template>

<script>
import { approvalApi } from './api'
export default {
  name: 'ButtonList',
  props: {
    // 操作前置配置
    operationPreOptions: {
      type: Object,
      default: () => ({})
    },
    // 审批状态
    approvalStatus: {
      type: String,
      default: 'DRAFT'
    },
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
    // 审批记录数据
    approvalRecords: {
      type: Array,
      default: () => ([])
    },
    // 是否使用组件者自定义
    showSaveAndNextStep: {
      type: Boolean,
      default: false
    },
    // 是否只读
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      actionsMap: new Map([
        ['submit', this.$t('common.submit')],
        ['resubmit', this.$t('common.reSubmit')]
      ]),
      activityName: '人工任务'
    }
  },
  methods: {
    // 暂存、提交
    async approvalHandler (type) {
      // 获取审批操作的前置配置
      const preConfigKeys = Object.keys(this.operationPreOptions)
      if (preConfigKeys.includes(type)) {
        // 配置类型是否与操作类型匹配
        const preResult = await this.operationPreOptions[type]()
        if (!preResult) return
      }
      // 构造流程参数
      let processVars = {}
      this.approvalRecords.forEach(item => {
        processVars[item.activityDefId] = item.createUser ? item.createUser.replace(/,/g, ' ') : ''
      })
      const submitParams = {
        taskInstId: this.approvalRecords.find(item => item.taskStatus == 1)?.executor.find(itm => itm.userId === this.$store.getters.userInfo.username)?.taskInstId,
        processVars,
        businessType: this.businessType,
        businessId: this.businessId,
        commentInfo: {
          actionName: this.statusMap.WITHDRAW === this.approvalStatus || this.approvalRecords.find(item => item.taskStatus == 1).activityName === this.activityName ? this.actionsMap.get('resubmit') : this.actionsMap.get('submit')
        }
      }
      let submitRes = null
      // 校验必填标识
      let validRequired = true
      const target = this.approvalRecords.filter(item => item.executor)
      target.some(item => {
        if (!item.createUserName) {
          validRequired = false
          return true
        }
      })
      switch (type) {
      // 暂存
      case 'save':
        break
      // 提交
      case 'submit':
        // 校验所有节点必填
        if (!validRequired) {
          this.$message.warning(this.$t('cusEntry.tipMessage.required'))
          return
        }
        submitRes = await approvalApi.submit(submitParams)
        if (submitRes.code == 0) {
          this.$message.success(this.$t('common.successSubmit'))
        }
        break
      default:
        break
      }
      // 操作成功发布事件
      this.$emit('approval-handler-callback', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.srm-footer-btn-group {
  bottom: 0px;
  right: 16px;
  padding-right: 24px;
  position: fixed;
  display: flex;
  justify-content: flex-end;
  height: 30px;
  box-sizing: border-box;
  background-color: #ffffff;
  width: calc(100% - 222px);
  padding-bottom: 2px;
  z-index: 999;
}
</style>
