<template>
  <!-- 报名管理 -->
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main style="padding-top: 10px;height: 400px;">
      <div style="padding: 5px 5px 8px 0">
        <!-- 发起投标 项目状态：接受报名中 -->
        <el-button
          type="primary"
          class="detail-pbtn"
          :disabled="bargainStatus !== 'ACCEPT_SIGNUP'"
          @click="changeSignUpEndTime(true)"
        >
          {{ $t('bidMod.biddingManagementBuyer.changeSignUpEndTime') }}
        </el-button>

        <el-button
          class="detail-pbtn"
          @click="getQueryData"
        >
          {{ $t('common.refresh') }}
        </el-button>
      </div>

      <TableView
        ref="applyTable"
        style="padding: 0"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-brg/signUpManagement/management/querySignUpInfos"
      />

      <!-- 审查报名资料 -->
      <entry-management-dialog
        :visible.sync="entryManagementDialogVisible"
        :edit-row="editRow"
        :bargain-id="scopeBargainId"
      />

      <srm-dialog
        :title="$t('bidMod.adjustDeadline')"
        size="middle"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
      >
        <srm-row style="margin: 11px 0">
          <srm-col
            :span="6"
            style="line-height: 32px;"
          >
            {{ $t('bidMod.biddingManagementBuyer.newSignUpEndTime') }}
          </srm-col>
          <srm-col :span="18">
            <el-date-picker
              v-model="singUpEndTime"
              type="datetime"
              value-format="timestamp"
              :placeholder="$t('bidMod.datePicker')"
            />
          </srm-col>
        </srm-row>
        <template #footer>
          <el-button
            @click="dialogVisible = false"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="changeSignUpEndTime()"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名管理
 */
import TableView from 'lib@/components/Table/TableView'
import entryManagementDialog from './applyManage/entryManagementDialog'

export default {
  name: 'ApplyManage',
  components: {
    entryManagementDialog,
    TableView
  },
  props: {
    // 招标ID
    scopeBargainId: {
      type: [Number, String],
      default () {
        return ''
      }
    },
    bargainStatus: {
      // 招标状态
      type: String,
      default () {
        return ''
      }
    },
    pageFlag: {
      type: Object,
      required: true
    },
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
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
        // 参与状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.participateStatus'),
          width: 110,
          formattor: val => this.$getDictLabel('BRG_SIGN_UP_STATUS', val)
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
          formattor: val => val ? val.substr(0, 10) : ''
        },
        // 驳回原因
        {
          prop: 'rejectReason',
          label: this.$t('bidMod.rejectReason1'),
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
          width: 110,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 确认
            {
              // 邀标类型：公开招标 && 报名状态：确认中
              show: row => (this.pageFlag.isEdit || this.pageFlag.isAdd) &&
                this.bargainBase.bargainScope === 'OPEN_TENDER' &&
                row.signUpStatus === 'CONFIRM_ING',
              callback: row => this.confirmVendor(row),
              formattor: () => this.$t('common.confirm')
            },
            // 驳回
            {
              show: row => (this.pageFlag.isEdit || this.pageFlag.isAdd) &&
                row.signUpStatus === 'CONFIRM_ING',
              callback: row => this.rejectOneVendor(row),
              formattor: () => this.$t('common.toRefuse')
            }
          ]
        }
      ],
      tableData: [],
      pageSize: 15,
      entryManagementDialogVisible: false,
      editRow: null,
      dialogVisible: false,
      singUpEndTime: ''
    }
  },
  computed: {
    queryParam () {
      return { bargainId: this.scopeBargainId }
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
    changeSignUpEndTime (isStopNow) {
      let params = {
        bargainId: this.scopeBargainId
      }
      if (isStopNow) {
        // 立即结束
        params = {
          ...params,
          stopNow: true
        }
      } else {
        if (!this.singUpEndTime) {
          this.$message.warning(this.$t('bidMod.biddingManagementBuyer.warningSelectEndTime'))
          return
        }
        params = {
          ...params,
          endTime: this.singUpEndTime
        }
      }
      this.$api.brg.inquiryByProject.changeSignUpEndTime(params).then(() => {
        if (!isStopNow) {
          this.dialogVisible = false
        }
        this.$message.success(this.$t('common.successSubmit'))
        // 更新节点
        this.$emit('updateProcessNode')
        // 更新基础数据
        this.$emit('fetchBaseInfo')
      })
    },

    /* 驳回 */
    rejectOneVendor (row) {
      this.$prompt(this.$t('bidMod.rejectReason1'), this.$t('bidMod.rejectReason1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length > 200),
        inputErrorMessage: this.$t('bidMod.biddingManagementBuyer.rejectReason')
      }).then(({ value }) => {
        this.submitConfirmOrReject({
          ...row,
          rejectReason: value
        }, 'reject')
      })
    },

    /* 确认 审批报名 */
    confirmVendor (row) {
      this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmVendorTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // 发起确认
        this.submitConfirmOrReject(row, 'confirm')
      })
    },

    /* 发起 确认 or 驳回 */
    submitConfirmOrReject (row, type) {
      let param = {
        bargainId: this.scopeBargainId,
        vendorId: row.vendorId,
        toConfirm: true
      }
      if (type === 'reject') {
        param = {
          ...param,
          toConfirm: false,
          rejectReason: row.rejectReason
        }
      }
      this.$api.brg.inquiryByProject.confirmVendorSignUpInfo(param).then(() => {
        const message = type === 'confirm' ? this.$t('common.successSubmit') : this.$t('bidMod.toRefuseSuccess')
        this.$message.success(message)
        this.getQueryData()
      })
    }
  }
}
</script>

<style scoped lang="scss">
.labelWithValue {
  font-size: 14px;
  padding-left: 100px;
  position: relative;
  color: #333;
  line-height: 22px;
  span {
    line-height: 22px;
    position: absolute;
    left: 0;
    width: 100px;
    text-align: right;
    color: #999;
  }
}
</style>
