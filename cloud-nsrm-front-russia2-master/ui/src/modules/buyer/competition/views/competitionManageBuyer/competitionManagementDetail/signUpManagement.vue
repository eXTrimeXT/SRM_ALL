<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="cur-quote-deadline">
        <DynamicCutoffTime :label="$t('bidMod.competitionLts.signUpEndTime')" :deadline-time="baseInfo.signUpEndTime" />
      </div>

      <p style="margin-top: 0">
        <!--立即结束报名-->
        <el-button
          v-if="baseInfo.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP"
          type="primary"
          @click="stopSignUp"
        >
          {{ $t('bidMod.biddingManagementBuyer.changeSignUpEndTime') }}
        </el-button>

        <!--刷新-->
        <el-button class="detail-pbtn" @click="() => $refs.applyManageTable.query()">
          {{ $t('common.refresh') }}
        </el-button>
      </p>

      <TableView
        ref="applyManageTable"
        :table-data="signUpTableData"
        :table-header="signUpTableHeader"
        :pre-query-data="transformQueryParams"
        auto-query
        :adeptMeiQl="true"
        :sortable="false"
        :url="tableViewUrl"
      />

      <!--报名管理-->
      <EntryManagementDialog
        v-if="entryManagementDialogVisible"
        :visible.sync="entryManagementDialogVisible"
        :edit-row="editRow"
        :project-id="baseInfo.projectId"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名管理
 */
import { carBuyerHttp } from 'modb@/competition/api'
import { SOU_SIGN_UP_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import TableView from 'lib@/components/Table/TableView'
import EntryManagementDialog from './signUpManagement/entryManagementDialog'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'SignUpManagement',

  components: {
    TableView,
    EntryManagementDialog,
    DynamicCutoffTime
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => ({})
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      tableViewUrl: carBuyerHttp.signUp.listPageUrl,
      signUpTableHeader: [
        // '轮次'
        {
          prop: 'joinRound',
          label: this.$t('bidMod.bidingRound'),
          width: 100
        },
        // 供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.supplierCode'),
          width: 120
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('bidMod.vendorName'),
          minWidth: 150
        },
        // 联系人
        {
          prop: 'linkmanName',
          label: this.$t('bidMod.contactMan'),
          width: 120
        },
        // 电话
        {
          prop: 'phone',
          label: this.$t('bidMod.registPhone'),
          width: 120
        },
        // 邮箱
        {
          prop: 'email',
          label: this.$t('components.approvalHead.headers.email'),
          width: 120
        },
        // 参与状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.Participate'),
          width: 110,
          formattor: val => this.$getDictLabel('SOU_SIGN_UP_STATUS', val)
        },
        // 报名资料
        {
          prop: 'signUp',
          label: this.$t('bidMod.applicationChecklist'),
          width: 110,
          showType: 'buttons',
          btnStyle: 'text',
          buttons: [
            // 审查
            {
              // 确认中 已报名
              show: row => [SOU_SIGN_UP_STATUS_ENUM.CONFIRM_ING, SOU_SIGN_UP_STATUS_ENUM.SIGN_UP_DONE].includes(row.signUpStatus),
              callback: row => this.openEntryManagementDialog(row),
              formattor: () => this.$t('bidMod.investigate')
            }
          ]
        },
        // 回复时间
        // 报名时间
        {
          prop: 'signUpTime',
          label: this.$t('sourcingBuyer.signUpTime'),
          width: 140
        },
        {
          prop: 'signUpRejectReason',
          label: this.$t('vendorMod.rejectReason'),
          width: 140,
          showType: 'input',
          editable: (row) => row.signUpStatus === 'CONFIRM_ING', // 报名状态 确认中
          hidden: this.baseInfo.publishScope === 'INVITE_TENDER' // 邀请
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 120,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          // hidden: this.baseInfo.publishScope === 'INVITE_TENDER',
          buttons: [
            {
              show: row => row.signUpStatus === 'CONFIRM_ING',
              callback: row => this.confirmSignUp(row, true),
              formattor: () => this.$t('components.common.confirm')
            },
            {
              show: row => row.signUpStatus === 'CONFIRM_ING',
              callback: row => this.confirmSignUp(row, false),
              formattor: () => this.$t('components.approvalHead.headers.refuse')
            }
          ]
        }
      ],
      signUpTableData: [],
      entryManagementDialogVisible: false,
      editRow: null,
      SOU_AUCT_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    transformQueryParams () {
      return transformMQL.listPageData({
        type: 'AuctSouProjectForBuyer',
        action: 'listVendorSignUp',
        params:
          {
            projectId: this.baseInfo.projectId
          }

      })
    }
  },

  watch: {
    isActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          if (this.$refs.applyManageTable) {
            this.$refs.applyManageTable.query()
          }
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 审查报名信息 */
    openEntryManagementDialog (row) {
      this.editRow = row
      this.entryManagementDialogVisible = true
    },

    /* 立即结束报名 */
    async stopSignUp () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        stopNow: true
      }], 'changeSignUpEndTime')
      const response = await carBuyerHttp.signUp.changeSignUpEndTime(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$refs.applyManageTable.query()
        // 更新节点
        this.$emit('refresh-process')
        // 更新基础数据
        this.$emit('refresh')
      }
    },

    /* 竞价范围-公开 - 确认、驳回 */
    async confirmSignUp (row, toPass) {
      const { vendorId, signUpRejectReason } = row
      if (!toPass && !signUpRejectReason) {
        // '请填写驳回原因'
        this.$message.warning(this.$t('purSettlementMod.pleaseFillReasonForRejection'))
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        toPass,
        vendorMap: {
          [vendorId]: toPass ? '' : signUpRejectReason
        }
      }], 'confirmSignUp')
      const response = await carBuyerHttp.signUp.confirmSignUp(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.$refs.applyManageTable.query()
        // 更新节点
        this.$emit('refresh-process')
        // 更新基础数据
        this.$emit('refresh')
      }
    }
  }
}
</script>
