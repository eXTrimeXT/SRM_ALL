<template>
  <div class="business-standard-ctrl">
    <el-table
      :data="businessItemList"
      border
      height="343px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
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

      <!--报价详情-->
      <el-table-column
        align="center"
        prop="orderNo"
        :label="$t('bidMod.bidDetail1')"
        min-width="170"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openQuoteDetailDialog(row)">
            {{ row.orderNo }}
          </el-button>
        </template>
      </el-table-column>

      <!--报价状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.quoteStatus')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('SOU_ORDER_STATUS', value)"
      />

      <!--提交时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.quoteSubmitDate')"
        width="150"
        show-overflow-tooltip
      />

      <!--商务附件-->
      <el-table-column
        align="center"
        :label="$t('bidMod.businessAttachment')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openBusinessFileDialog(row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--作废原因-->
      <el-table-column
        align="center"
        prop="rejectReason"
        :label="$t('bidMod.rejectReason')"
        width="120"
        show-overflow-tooltip
      />

      <el-table-column
        align="center"
        :label="$t('bidMod.operation')"
        width="120"
      >
        <template v-slot="{ row }">
          <!--作废报价 非查看 && 未作废 && [接受报价中、报价截止]-->
          <!-- <el-button
            v-if="
              !pageFlag.isView &&
                row.orderStatus !== SOU_ORDER_STATUS_ENUM.CANCEL &&
                [SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER, SOU_PROJECT_STATUS_ENUM.ORDER_END].includes(projectStatus)
            "
            type="text"
            @click="cancelOrder(row)"
          >
            {{ $t('bidMod.cancelBiding1') }}
          </el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <!--报价详情-->
    <QuoteDetailDialog
      v-if="quoteDetailDialogVisible"
      :visible.sync="quoteDetailDialogVisible"
      :project-id="projectId"
      :view-row="viewRow"
    />

    <!--商务标附件详情-->
    <BusinessFileDialog
      v-if="businessFileDialogVisible"
      :visible.sync="businessFileDialogVisible"
      :view-row="viewRow"
    />
  </div>
</template>

<script>
/**
 * 商务标管理
 */
import { SOU_PROJECT_STATUS_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import QuoteDetailDialog from './businessStandardCtrl/quoteDetailDialog'
import BusinessFileDialog from './businessStandardCtrl/businessFileDialog'

export default {
  name: 'BusinessStandardCtrl',

  inject: ['pageFlag'],

  components: {
    QuoteDetailDialog,
    BusinessFileDialog
  },

  props: {
    projectId: {
      type: [Number, String],
      default: ''
    },
    projectStatus: {
      type: String,
      default: ''
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      businessItemList: [],
      quoteDetailDialogVisible: false,
      businessFileDialogVisible: false,
      viewRow: null,
      SOU_PROJECT_STATUS_ENUM,
      SOU_ORDER_STATUS_ENUM
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getBusinessOrder()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询列表数据 */
    async getBusinessOrder () {
      const response = await compBuyerHttp.control.getOrders(this.projectId)
      if (response && response.data) {
        const { orderInfos = [] } = response.data

        this.businessItemList = orderInfos
      }
    },

    /* 作废报价 */
    async cancelOrder (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.enterScrapQuotation'),
        this.$t('bidMod.abandonedQuotation'),
        {
          confirmButtonText: this.$t('bidMod.determine'),
          cancelButtonText: this.$t('bidMod.cancel')
        }
      )

      if (!promptResult) {
        return
      }

      const response = await this.$api.comp.buyer.CM.cancelQuote({
        projectId: this.projectId,
        orderHeadId: row.orderHeadId,
        rejectReason: promptResult.value
      })

      if (response) {
        this.$message.success(this.$t('bidMod.abandonedQuotaSuccess'))
        // 更新列表数据
        await this.getBusinessOrder()
      }
    },

    /* 打开报价详情 */
    openQuoteDetailDialog (row) {
      this.viewRow = row
      this.quoteDetailDialogVisible = true
    },

    /* 打开商务附件 */
    openBusinessFileDialog (row) {
      this.viewRow = row
      this.businessFileDialogVisible = true
    }
  }
}
</script>
