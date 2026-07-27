<template>
  <div class="business-standard-ctrl">
    <p style="margin: 10px 0;">
      <!-- 商务开标 项目状态为投标截止 or 技术评标 -->
      <el-button
        v-if="isBusinessBidding"
        type="primary"
        :disabled="readonly"
        @click="businessOpen"
      >
        {{ $t("bidMod.openBusinessBiding") }}
      </el-button>

      <!--报价解密 密封报价才需要解密-->
      <el-button
        v-if="isNeedEncryptPrice"
        type="primary"
        :disabled="readonly"
        @click="decryptPrice"
      >
        报价解密
      </el-button>

      <!--刷新-->
      <el-button @click="getBusinessOrders">
        {{ $t('common.refresh') }}
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
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--轮次-->
      <el-table-column
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="60"
        show-overflow-tooltip
      />

      <!--供应商编码-->
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--供应商IP-->
      <el-table-column
        prop="submitByIp"
        label="供应商IP"
        width="120"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span :style="{ color: row.ipWarn ? 'red' : '' }">{{ row.submitByIp }}</span>
        </template>
      </el-table-column>

      <!--投标详情-->
      <el-table-column
        prop="orderNo"
        :label="$t('bidMod.bidDetail')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <!--(密封报价 && 已解密) || 非密封报价-->
          <el-button
            v-if="(isNeedEncryptPrice && isPriceDecrypt) || !isNeedEncryptPrice"
            type="text"
            @click="openBiddingDetailDialog(row)"
          >
            {{ row.orderNo }}
          </el-button>
        </template>
      </el-table-column>

      <!--提交日期-->
      <el-table-column
        prop="submitTime"
        :label="$t('bidMod.submitDate')"
        width="150"
        show-overflow-tooltip
      />

      <!--作废原因-->
      <el-table-column
        prop="rejectReason"
        :label="$t('bidMod.rejectReason')"
        width="120"
        show-overflow-tooltip
      />

      <!--商务附件-->
      <el-table-column
        :label="$t('bidMod.businessAttch')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <!-- 查看 -->
          <el-button type="text" @click="openBusinessFileDialog(row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        :label="$t('bidMod.operation')"
        width="130"
        fixed="right"
      >
        <template v-slot="{ row }">
          <!-- 作废投标 非查看 && 已报价 && [接受投标中、投标截止] -->
          <el-button
            v-if="
              !readonly &&
                row.orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION &&
                [SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER, SOU_PROJECT_STATUS_ENUM.ORDER_END].includes(projectStatus)
            "
            type="text"
            @click="cancelOrder(row)"
          >
            {{ $t("bidMod.cancelBiding1") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--商务附件-->
    <BusinessFileDialog
      v-if="businessFileDialogVisible"
      :visible.sync="businessFileDialogVisible"
      :edit-row="editRow"
    />

    <!--报价详情-->
    <BiddingDetailDialog
      v-if="biddingDetailDialogVisible"
      :visible.sync="biddingDetailDialogVisible"
      :bidding-base="biddingBase"
      :edit-row="editRow"
    />
  </div>
</template>

<script>
/**
 * 商务标管理
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import { SOU_PROJECT_STATUS_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement } from '@/library/composition/biddingLts/utils'
import BusinessFileDialog from './businessManagement/businessFileDialog'
import BiddingDetailDialog from './businessManagement/biddingDetailDialog'

export default {
  name: 'BusinessManagement',

  components: {
    BusinessFileDialog,
    BiddingDetailDialog
  },

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    createApprovalStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    // 投标基础信息
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    // 投标基础信息
    projectInfoData: {
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
      currentRound: {
        priceDecrypt: ''
      },
      businessItemList: [],
      editRow: null,
      businessFileDialogVisible: false,
      biddingDetailDialogVisible: false,
      SOU_ORDER_STATUS_ENUM,
      SOU_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    },
    // 是否显示商务开标
    isBusinessBidding () {
      return !this.readonly &&
        // 技术评标 || 报价截止
        [SOU_PROJECT_STATUS_ENUM.TECH_EVAL, SOU_PROJECT_STATUS_ENUM.ORDER_END].includes(this.biddingBase.projectStatus)
    },
    // 是否显示密封报价
    isNeedEncryptPrice () {
      // 需要解密、未解密，在工作小组中且有解密权限
      return this.biddingBase.needEncryptPrice === 'Y' &&
        !this.isPriceDecrypt &&
        (this.projectInfoData.groupList || []).find(item => item.userId === this.userId && item.operateAuth === 'SOU_DECRYPT_PRICE')
    },
    // 是否已解密报价
    isPriceDecrypt () {
      return this.currentRound.priceDecrypt === 'Y'
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
    async getBusinessOrders () {
      const response = await bidBuyerHttp.control.orders(this.biddingBase.projectId)
      if (response) {
        const {
          currentRound = {},
          orderInfos = []
        } = response.data || {}
        this.currentRound = currentRound
        this.businessItemList = judgeListRepeatValueWarnTag((orderInfos || []), 'submitByIp', 'ipWarn')
      }
    },

    /* 商务开标 */
    async businessOpen () {
      const confirmResult = await this.$confirm('确定发起商务开标吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.businessOpen(this.biddingBase.projectId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))

        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
      }
    },

    /* 解密报价 */
    async decryptPrice () {
      const response = await bidBuyerHttp.control.decryptPrice(this.biddingBase.projectId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getBusinessOrders()

        // 更新招标基础数据
        this.$emit('refresh')
      }
    },

    /* 打开投标详情弹窗 */
    openBiddingDetailDialog (row) {
      this.editRow = row
      this.biddingDetailDialogVisible = true
    },

    /* 打开商务附件弹窗 */
    openBusinessFileDialog (row) {
      this.editRow = row
      this.businessFileDialogVisible = true
    },

    /* 作废投标 */
    async cancelOrder (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.enterScrapQuotation'),
        this.$t('bidMod.cancelBiding1'),
        {
          confirmButtonText: this.$t('bidMod.determine'),
          cancelButtonText: this.$t('bidMod.cancel'),
          inputValidator: value => !(!value || value.length === 0),
          inputErrorMessage: this.$t('logisticsMod.msgInvalidReason')
        }
      )

      if (!promptResult) {
        return
      }

      const response = await bidBuyerHttp.order.cancelOrder({
        projectId: this.biddingBase.projectId,
        vendorId: row.vendorId,
        cancelReason: promptResult.value
      })

      if (response) {
        // 废弃报价成功
        this.$message.success(this.$t('bidMod.abandonedQuotaSuccess'))
        await this.getBusinessOrders()
      }
    }
  }
}
</script>
