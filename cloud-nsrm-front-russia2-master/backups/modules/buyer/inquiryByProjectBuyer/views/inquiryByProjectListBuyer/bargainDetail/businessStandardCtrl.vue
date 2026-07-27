<template>
  <div>
    <p style="margin: 10px 0;">
      <!-- 商务开标 项目状态为投标截止 or 技术评标 -->
      <el-button
        v-if="isBusinessBargain"
        type="primary"
        class="detail-pbtn"
        :disabled="isDisabledTable"
        @click="openBusinessBargain"
      >
        {{ $t("bidMod.openBusinessBiding") }}
      </el-button>

      <!--报价解密 密封报价才需要解密-->
      <el-button
        v-if="isNeedEncryptPrice"
        type="primary"
        class="detail-pbtn"
        :disabled="isDisabledTable"
        @click="decryptBrg"
      >
        报价解密
      </el-button>
    </p>

    <el-table
      :data="businessItemList"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />
      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="60"
        show-overflow-tooltip
      />

      <!--供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--供应商IP-->
      <el-table-column
        align="center"
        prop="quotationIp"
        label="供应商IP"
        width="120"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span :style="{ color: row.ipWarn ? 'red' : '' }">{{ row.quotationIp }}</span>
        </template>
      </el-table-column>

      <!--报价详情-->
      <el-table-column
        align="center"
        prop="brgDetail"
        :label="$t('bidMod.bidDetail1')"
        width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!--(密封报价 && 已解密) || 非密封报价-->
          <el-button
            v-if="(isNeedEncryptPrice && isDecryptFlag) || !isNeedEncryptPrice"
            type="text"
            @click="openBargainDetailDialog(scope.row)"
          >
            {{ scope.row.brgOrderNum }}
          </el-button>
        </template>
      </el-table-column>

      <!--提交日期-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.submitDate')"
        width="150"
        show-overflow-tooltip
      />

      <!--作废原因-->
      <el-table-column
        align="center"
        prop="rejectReason"
        :label="$t('bidMod.rejectReason')"
        width="120"
        show-overflow-tooltip
      />

      <!--商务附件-->
      <el-table-column
        align="center"
        :label="$t('bidMod.businessAttch')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!-- 查看 -->
          <el-button
            type="text"
            @click="openBusinessFileDialog(scope.row)"
          >
            {{ $t("common.view") }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        :label="$t('bidMod.operation')"
        width="130"
        fixed="right"
      >
        <template slot-scope="scope">
          <!-- 作废投标 非查看 && 未作废 && [接受投标中、投标截止、技术评标、商务评标] -->
          <el-button
            v-if="
              !isViewOrApproval &&
                scope.row.orderStatus !== 'INVALID' &&
                ['ACCEPT_BRG', 'TENDER_ENDING', 'TECHNICAL_EVALUATION', 'BUSINESS_EVALUATION'].includes(bargainStatus)
            "
            type="text"
            @click="cancelBargain(scope.row)"
          >
            {{ $t("bidMod.cancelBiding1") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--商务附件-->
    <business-file-dialog
      :visible.sync="businessFileDialogVisible"
      :edit-row="editRow"
    />

    <!--报价详情-->
    <bargain-detail-dialog
      :visible.sync="bargainDetailDialogVisible"
      :edit-row="editRow"
    />
  </div>
</template>

<script>
/**
 * 商务标管理
 */
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import businessFileDialog from './businessStandardCtrl/businessFileDialog'
import bargainDetailDialog from './businessStandardCtrl/bargainDetailDialog'

export default {
  name: 'BusinessStandardCtrl',
  components: {
    businessFileDialog,
    bargainDetailDialog
  },
  props: {
    scopeBargainId: {
      // 招标ID
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
    auditStatus: {
      // 审批状态
      type: String,
      default () {
        return ''
      }
    },
    isViewOrApproval: {
      type: Boolean,
      required: true
    },
    // 投标基础信息
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    },
    // 投标基础信息
    projectInformationData: {
      type: Object,
      default () {
        return {}
      }
    },
    isActiveMenu: {
      type: Boolean
    }
  },
  data () {
    return {
      businessItemList: [],
      editRow: null,
      businessFileDialogVisible: false,
      bargainDetailDialogVisible: false
    }
  },
  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },
    isDisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bargainStatus === 'DRAW_UP' &&
        ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    },
    // 是否显示商务开标
    isBusinessBargain () {
      return !this.isDisabledTable &&
        ['TECHNICAL_EVALUATION', 'TENDER_ENDING'].includes(this.bargainBase.bargainStatus)
    },
    // 是否显示密封报价
    isNeedEncryptPrice () {
      // 需要解密、未解密，在工作小组中且有解密权限
      return this.bargainBase.needEncryptPrice === 'Y' &&
        !this.isDecryptFlag &&
        (this.projectInformationData.groupList || []).find(item => item.userId === this.userId && item.canDecrypt === 'Y')
    },
    // 是否已解密报价
    isDecryptFlag () {
      return this.bargainBase.decryptFlag === 'Y'
    }
  },
  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getBusinessOrders()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询数据 */
    getBusinessOrders () {
      this.$api.brg.inquiryByProject.queryBusinessOrders(this.scopeBargainId).then(data => {
        if (data && data.data) {
          this.businessItemList = judgeListRepeatValueWarnTag((data.data || []), 'quotationIp', 'ipWarn')
        }
      })
    },

    /* 商务开标 */
    openBusinessBargain () {
      this.$confirm('确定发起商务开标吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.brg.inquiryByProject.openBrg(this.scopeBargainId).then(() => {
          this.$message.success(this.$t('common.successSubmit'))

          // 更新节点信息
          this.$emit('fetchParentNodeData')
          // 更新招标基础数据
          this.$emit('fetchBaseInfo')
        })
      })
    },

    /* 解密报价 */
    decryptBrg () {
      this.$api.brg.inquiryByProject.decryptBrg(this.scopeBargainId).then(() => {
        this.$t('common.successSubmit')

        // 更新招标基础数据
        this.$emit('fetchBaseInfo')
      })
    },

    /* 打开投标详情弹窗 */
    openBargainDetailDialog (row) {
      this.editRow = row
      this.bargainDetailDialogVisible = true
    },

    /* 打开商务附件弹窗 */
    openBusinessFileDialog (row) {
      this.editRow = row
      this.businessFileDialogVisible = true
    },

    /* 作废投标 */
    cancelBargain (row) {
      this.$prompt(this.$t('bidMod.enterScrapQuotation'), this.$t('bidMod.cancelBiding1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length === 0),
        inputErrorMessage: this.$t('logisticsMod.msgInvalidReason')
      }).then(({ value }) => {
        this.$api.brg.inquiryByProject.withdrawOrder({
          orderHeadId: row.orderHeadId,
          rejectReason: value
        }).then(() => {
          this.$message.success(this.$t('bidMod.abandonedQuotaSuccess'))
          this.getBusinessOrders()
        })
      })
    },

    /* 驳回投标 */
    delQuote (row) {
      let query = {}
      query.brgVendorId = row.brgVendorId
      query.round = row.round
      this.$api.brg.inquiryByProject.removeOrderInfo(query,).then(() => {
        this.$message.success(this.$t('bidMod.successDelQuote'))
        this.getBusinessOrders(this.scopeBargainId)
      })
    }
  }
}
</script>
