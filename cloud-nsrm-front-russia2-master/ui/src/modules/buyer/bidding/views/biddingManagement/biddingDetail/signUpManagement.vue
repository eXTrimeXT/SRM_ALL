<template>
  <!-- 报名管理 -->
  <el-container class="flex-container" direction="vertical">
    <el-main style="padding-top: 10px;height: 400px;">
      <div style="padding: 5px 5px 8px 0">
        <!--立即结束报名-->
        <el-button
          type="primary"
          :disabled="projectStatus !== SOU_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP"
          @click="changeSignUpEndTime(true)"
        >
          {{ $t('bidMod.biddingManagementBuyer.changeSignUpEndTime') }}
        </el-button>

        <el-button @click="getQueryData">
          {{ $t('common.refresh') }}
        </el-button>
      </div>

      <TableView
        ref="applyTable"
        style="padding: 0"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :url="tableViewUrl"
      />

      <!-- 审查报名资料 -->
      <ExaminationSignUpDialog
        v-if="entryManagementDialogVisible"
        :visible.sync="entryManagementDialogVisible"
        :edit-row="editRow"
        :project-id="biddingBase.projectId"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名管理
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import {
  SOU_PROJECT_STATUS_ENUM,
  SOU_PUBLISH_SCOPE_ENUM,
  SOU_SIGN_UP_STATUS_ENUM
} from 'lib@/composition/origin/enum'
import ExaminationSignUpDialog from './signUpManagement/examinationSignUpDialog.vue'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'SignUpManagement',

  components: {
    ExaminationSignUpDialog,
    TableView
  },

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    pageFlag: {
      type: Object,
      required: true
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      tableViewUrl: bidBuyerHttp.signUp.page,
      tableHeader: [
        // 供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),
          width: 120
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          minWidth: 150
        },
        // 联系人
        {
          prop: 'linkManName',
          label: this.$t('bidMod.contactMan'),
          width: 120
        },
        // 电话
        {
          prop: 'phone',
          label: this.$t('bidMod.phone'),
          width: 120
        },
        // 邮箱
        {
          prop: 'email',
          label: this.$t('bidMod.email2'),
          width: 120
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          width: 110,
          formattor: val => this.$getDictLabel('SOU_SIGN_UP_STATUS', val)
        },
        // 报名资料
        {
          prop: 'inquiryNo',
          label: this.$t('bidMod.registrationInfo'),
          width: 110,
          showType: 'button',
          btnStyle: 'text',
          formattor: () => this.$t('bidMod.review'),
          callback: row => this.openEntryManagementDialog(row)
        },
        // 回复时间
        {
          prop: 'replyDatetime',
          label: this.$t('bidMod.replyDatetime'),
          width: 100,
          formattor: val => this.$parseTime(val)
        },
        // 驳回原因
        {
          prop: 'signUpRejectReason',
          label: this.$t('bidMod.rejectReason1'),
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 110,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 确认
            {
              // 邀标类型：公开招标 && 报名状态：确认中
              show: row => (this.pageFlag.isEdit || this.pageFlag.isAdd) &&
                this.biddingBase.publishScope === SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER &&
                row.signUpStatus === SOU_SIGN_UP_STATUS_ENUM.CONFIRM_ING,
              callback: row => this.confirmVendor(row),
              formattor: () => this.$t('common.confirm')
            },
            // 驳回
            {
              show: row => (this.pageFlag.isEdit || this.pageFlag.isAdd) &&
                row.signUpStatus === SOU_SIGN_UP_STATUS_ENUM.CONFIRM_ING,
              callback: row => this.rejectOneVendor(row),
              formattor: () => this.$t('common.toRefuse')
            }
          ]
        }
      ],
      tableData: [],
      entryManagementDialogVisible: false,
      editRow: null,
      signUpEndTime: '',
      SOU_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    queryParam () {
      return { projectId: this.biddingBase.projectId }
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.$nextTick(() => {
            this.getQueryData()
          })
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询列表数据 */
    getQueryData () {
      this.$refs.applyTable.query()
    },

    /* 查看报名信息 */
    openEntryManagementDialog (row) {
      this.editRow = row
      this.entryManagementDialogVisible = true
    },

    /* 立即结束报名 / 调整报名时间 */
    async changeSignUpEndTime (isStopNow) {
      let params = {
        projectId: this.biddingBase.projectId
      }
      if (isStopNow) {
        // 立即结束
        params = {
          ...params,
          stopNow: true
        }
      } else {
        if (!this.signUpEndTime) {
          this.$message.warning(this.$t('bidMod.biddingManagementBuyer.warningSelectEndTime'))
          return
        }
        params = {
          ...params,
          signUpEndTime: this.signUpEndTime
        }
      }
      const response = await bidBuyerHttp.signUp.changeSignUpEndTime(params)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新节点
        this.$emit('refresh-process')
        // 更新基础数据
        this.$emit('refresh')
      }
    },

    /* 驳回 */
    async rejectOneVendor (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.rejectReason1'),
        this.$t('bidMod.rejectReason1'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputValidator: value => !(!value || value.length > 200),
          inputErrorMessage: this.$t('bidMod.biddingManagementBuyer.rejectReason')
        }
      )

      if (!promptResult) {
        return
      }

      await this.submitConfirmOrReject(
        {
          ...row,
          rejectReason: promptResult.value
        },
        'reject'
      )
    },

    /* 确认 审批报名 */
    async confirmVendor (row) {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmVendorTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      // 发起确认
      await this.submitConfirmOrReject(row, 'confirm')
    },

    /* 发起 确认 or 驳回 */
    async submitConfirmOrReject (row, type) {
      let param = {
        projectId: this.biddingBase.projectId,
        vendorId: row.vendorId,
        toPass: true
      }
      if (type === 'reject') {
        param = {
          ...param,
          toPass: false,
          rejectReason: row.rejectReason
        }
      }

      const response = await bidBuyerHttp.signUp.confirmSignUp(param)
      if (response) {
        this.$message.success(type === 'confirm' ? this.$t('common.successSubmit') : this.$t('bidMod.toRefuseSuccess'))
        await this.getQueryData()
      }
    }
  }
}
</script>
