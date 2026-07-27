<template>
  <div class="price-control">
    <!--信息行-->
    <el-form label-position="left" disabled>
      <SrmRow>
        <!--需投标的供应商数量-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('cusEntry.supplement20250211.supplierCountToQuote')">
            <el-input v-model="priceControlInfo.inviteCount" />
          </el-form-item>
        </SrmCol>

        <!--已提交投标供应商-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.submitSupplierCount1')">
            <el-input v-model="priceControlInfo.orderCount" />
          </el-form-item>
        </SrmCol>

        <!--投标截止时间-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.enrollEndDatetime')">
            <el-date-picker
              v-model="priceControlInfo.orderEndTime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!--按钮区域-->
    <div style="padding: 3px; margin-bottom: 8px">
      <!--发起报价 报名截止-->
      <el-button
        v-if="projectStatus === SOU_PROJECT_STATUS_ENUM.SIGN_UP_END"
        type="primary"
        :disabled="readOnly"
        @click="startComp"
      >
        {{ $t('bidMod.launchQuotation') }}
      </el-button>

      <!--调整截至时间-->
      <el-button
        v-if="adjustmentDeadlineShow"
        type="primary"
        :disabled="readOnly"
        @click="adjustmentDeadlineDialogVisible = true"
      >
        {{ $t('bidMod.adjustmentDeadline') }}
      </el-button>

      <!--立即截止报价-->
      <el-button
        v-if="projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER"
        type="primary"
        :disabled="readOnly"
        @click="stopComp"
      >
        <!-- 立即截止报价 -->
        {{ $t("cusEntry.supplement20250211.immediateDeadlinePrice") }}
      </el-button>

      <!--刷新-->
      <el-button :disabled="readOnly" @click="getPriceControlInfo">
        {{ $t('common.refresh') }}
      </el-button>
    </div>

    <!--表格-->
    <el-table
      :data="quoteDetailList"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
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

      <!--联系人-->
      <el-table-column
        align="center"
        prop="linkmanName"
        :label="$t('bidMod.linkMan')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--手机号码-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.tel')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        width="180"
        show-overflow-tooltip
      />

      <!--报价状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.orderStatus1')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('SOU_ORDER_STATUS', value)"
      />

      <!--提交人-->
      <el-table-column
        align="center"
        prop="submitBy"
        :label="$t('bidMod.lastUpdatedBy2')"
        width="100"
        show-overflow-tooltip
      />

      <!--提交时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.lastUpdateDate2')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
    </el-table>

    <!--调整截至时间-->
    <AdjustmentDeadlineDialog
      v-if="adjustmentDeadlineDialogVisible"
      :visible.sync="adjustmentDeadlineDialogVisible"
      :project-id="projectId"
      @updateParentData="updateParentData"
    />
  </div>
</template>

<script>
/**
 * 报价控制
 */
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import AdjustmentDeadlineDialog from './priceControl/adjustmentDeadlineDialog'

export default {
  name: 'PriceControl',

  components: { AdjustmentDeadlineDialog },

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
      priceControlInfo: {
        inviteCount: '',
        orderCount: '',
        orderEndTime: ''
      },
      readOnly: false,
      adjustmentDeadlineDialogVisible: false,
      quoteDetailList: [],
      SOU_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    adjustmentDeadlineShow () {
      return (new Date().getTime() < new Date(this.priceControlInfo.orderEndTime).getTime()) &&
        this.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getPriceControlInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询详情 */
    async getPriceControlInfo () {
      const response = await compBuyerHttp.control.getOrders(this.projectId)
      if (response && response.data) {
        const { orderInfos = [], currentRound = {} } = response.data

        this.quoteDetailList = orderInfos

        for (let key in this.priceControlInfo) {
          if (currentRound[key] || currentRound[key] === 0) {
            this.priceControlInfo[key] = currentRound[key]
          }
        }
      }
    },

    /* 发起报价 */
    async startComp () {
      const response = await compBuyerHttp.control.changeOrderStartTime({
        projectId: this.projectId,
        startNow: true
      })
      if (response) {
        this.$message.success(this.$t('bidMod.InitiationBidSuccessful'))
        this.updateParentData()
      }
    },

    /* 立即结束报价 */
    async stopComp () {
      const response = await compBuyerHttp.control.changeOrderEndTime({
        projectId: this.projectId,
        endNow: true
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.updateParentData()
      }
    },

    /* 更新父组件信息 */
    updateParentData () {
      this.getPriceControlInfo()
      // 更新节点
      this.$emit('updateProcessNode')
      // 更新基础数据
      this.$emit('fetchBaseInfo')
    }
  }
}
</script>
