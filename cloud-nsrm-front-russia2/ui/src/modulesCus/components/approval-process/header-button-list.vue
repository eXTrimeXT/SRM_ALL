<template>
  <div class="srm-header-btn-group">
    <el-button
      v-if="hasPermission('pass')"
      type="primary"
      @click="approvalHandler('pass')"
    >
      {{ $t('cusEntry.approval.pass') }}
    </el-button>
    <el-button
      v-if="hasPermission('return')"
      type="primary"
      @click="approvalHandler('return')"
    >
      {{ $t('cusEntry.approval.return') }}
    </el-button>
    <el-button
      v-if="hasPermission('returnDirect')"
      type="primary"
      @click="approvalHandler('returnDirect')"
    >
      {{ $t('cusEntry.approval.returnDirect') }}
    </el-button>
    <el-button
      v-if="hasPermission('returnPre')"
      type="primary"
      @click="approvalHandler('returnPre')"
    >
      {{ $t('cusEntry.approval.returnPre') }}
    </el-button>
    <QuickSearch
      v-if="hasPermission('transfer')"
      ref="userNode"
      style="margin: 0px 7px;"
      :show-button="true"
      name="scc_rbac_user_display"
      :btn-title="$t('cusEntry.approval.transfer')"
      @close-quicksearch="getUserNode"
    />
    <el-button
      v-if="hasPermission('end')"
      type="primary"
      @click="approvalHandler('end')"
    >
      {{ $t('cusEntry.approval.end') }}
    </el-button>
  </div>
</template>

<script>
import { approvalApi } from './api'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'ButtonList',
  components: {
    QuickSearch
  },
  props: {
    // 操作前置配置
    operationPreOptions: {
      type: Object,
      default: () => ({})
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
    // 流程节点信息
    flowNodeList: {
      type: Array,
      default: () => ([])
    },
    // 审批节点信息
    approveRecordList: {
      type: Array,
      default: () => ([])
    }
  },
  data () {
    return {
      username: this.$store.getters.userInfo.username,
      actionsMap: new Map([
        ['pass', this.$t('cusEntry.approval.pass')], // 通过
        ['return', this.$t('cusEntry.approval.return')], // 退回
        ['returnDirect', this.$t('cusEntry.approval.returnDirect')], // 退回直达
        ['returnPre', this.$t('cusEntry.approval.returnPre')], // 退回招标专家
        ['transfer', this.$t('cusEntry.approval.transfer')], // 转办
        ['end', this.$t('cusEntry.approval.end')] // 结束
      ]),
      show: false,
      isPassFlag: true,
      errorMes: ''
    }
  },
  created() {
    this.$nextTick(() => {
      const obj = this.approveRecordList.find(item => item.actionName === '正在办理' && item.activityName === '经办人清稿')
      if (obj) {
        this.$http({
          url: `/api-file/edit/onlyoffice/api/checkHasClear?contractHeadId=${this.businessId}`,
          method: 'GET',
          loading: true
        }).then(res => {
          if (res && res.code + '' === '0') {
            if (res.data && res.data.isSubmit === 'N') {
              this.isPassFlag = false
              this.errorMes = res.data.errorMsg
            }
          } else {
            this.isPassFlag = false
          }
        }).catch(err => {
          this.isPassFlag = false
        })
      } else {
        this.isPassFlag = true
      }
    })
  },
  methods: {
    // 判断是否移动端打开
    isMobileDevice () {
      let mobile = false
      if (navigator.userAgent.match(/(iPhone|iPod|Android|ios|iPad)/i)) {
        mobile = true
      }
      return mobile
    },
    // 审批操作
    async approvalHandler (type, targetNode) {
      if (this.isPassFlag) {
        // 获取审批操作的前置配置
        const preConfigKeys = Object.keys(this.operationPreOptions)
        if (preConfigKeys.includes(type)) {
          // 配置类型是否与操作类型匹配
          const preResult = await this.operationPreOptions[type]()
          if (!preResult) return
        }
        this.show = true
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
            businessId: this.businessId,
            businessType: this.businessType,
            taskInstId: this.flowNodeList.find(item => item.taskStatus == 1)?.executor.find(itm => itm.userId === this.$store.getters.userInfo.username)?.taskInstId,
            commentInfo: {
              actionName: this.actionsMap.get(type),
              commentMsg: value
            },
            targetUser: targetNode
          }
          const optType = ['return', 'returnDirect', 'returnPre'].includes(type) ? 'return' : type
          const res = await approvalApi[optType](submitParams)
          if (res.code == 0) {
            this.$message.success(this.$t('cusEntry.common.operationSuccess'))
          }
          // 重新获取节点信息
          const { data } = await approvalApi.nextStep({ businessId: this.businessId, businessType: this.businessType })
          this.$emit('update:flowNodeList', data)
          this.$emit('get-flow-node-list', data)
          // 判断下个节点审批人是否含有当前用户
          // const nextNodeList = data.filter(item => item.taskStatus == 1).map(itm => itm.executorId)
          // 更新审批记录
          this.$emit('update-approval-record')
          // 操作成功发布事件
          this.$emit('approval-handler-callback', type)
          window.close()
        }).catch((err) => {
          if (err === 'cancel') {
            this.$message({
              type: 'info',
              message: this.$t('common.cancel')
            })
          }
        })
      } else {
        this.$message.error(this.errorMes)
      }
    },
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
    // 转办
    getUserNode (node) {
      this.approvalHandler('transfer', node.username)
    }
  }
}
</script>

<style lang="scss">
.srm-header-btn-group {
  top: 0px;
  position: sticky;
  display: flex;
  justify-content: flex-end;
  height: 30px;
  box-sizing: border-box;
  background-color: #ffffff;
  box-shadow: 0 -1px 2px 0 rgba(182, 182, 182, 0.5);
  padding-bottom: 2px;
  z-index: 999;
}
.srm-approval-prompt {
  width: 350px !important;
}
</style>
