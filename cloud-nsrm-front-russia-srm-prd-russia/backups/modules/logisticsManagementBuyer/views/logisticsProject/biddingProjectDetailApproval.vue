<template>
  <el-collapse
    v-model="activeDims"
    class="tab-form-style"
  >
    <el-collapse-item
      :title="$t('bidMod.projectInformation')"
      name="1"
    >
      <el-form
        ref="form"
        :model="lgtBiding"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
        disabled
      >
        <el-row>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.bidingNum')"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="lgtBiding.bidingNum"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.bidingName')"
              :label-width="formLabelWidth"
              prop="bidingName"
            >
              <el-input v-model="lgtBiding.bidingName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('logisticsMod.businessMode')"
              :label-width="formLabelWidth"
              prop="businessModeCode"
            >
              <DictSelect
                v-model="lgtBiding.businessModeCode"
                code="BUSINESS_MODE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.transportType')"
              :label-width="formLabelWidth"
              prop="transportModeCode"
            >
              <DictSelect
                v-model="lgtBiding.transportModeCode"
                code="TRANSPORT_MODE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.unit')"
              :label-width="formLabelWidth"
              prop="unitCode"
            >
              <DictSelect
                v-model="lgtBiding.unitCode"
                code="SUB_LEVEL"
                @change="changeUnit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('logisticsMod.projectTotal')"
              :label-width="formLabelWidth"
              prop="projectTotal"
            >
              <el-input
                v-model="lgtBiding.projectTotal"
                type="number"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.ceeaDemandDate')"
              :label-width="formLabelWidth"
              prop="demandDate"
            >
              <el-date-picker
                v-model="lgtBiding.demandDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.budgetAmount')"
              :label-width="formLabelWidth"
              prop="budgetAmount"
            >
              <el-input
                v-model="lgtBiding.budgetAmount"
                v-input-format="{ type: 'float' }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidFrom')"
              :label-width="formLabelWidth"
              prop="priceTimeStart"
            >
              <el-date-picker
                v-model="lgtBiding.priceTimeStart"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidTo')"
              :label-width="formLabelWidth"
              prop="priceTimeEnd"
            >
              <el-date-picker
                v-model="lgtBiding.priceTimeEnd"
                type="date"
                value-format="yyyy-MM-dd"
                :picker-options="endTiumePickerOptions2"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('common.remark')"
              :label-width="formLabelWidth"
              prop="comments"
            >
              <el-input
                v-model="lgtBiding.comments"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item
              :label="$t('logisticsMod.summaryDescription')"
              :label-width="formLabelWidth"
              prop="summaryDescription"
            >
              <el-input
                v-model="lgtBiding.summaryDescription"
                type="textarea"
                :rows="2"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-collapse-item>
    <!-- 报价汇总信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.quotaSumInfo')"
      name="2"
    >
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <el-table
        :data="lgtVendorQuotedSums"
        style="width: 100%"
        border
        height="250px"
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('purSettlementMod.tabindex')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          show-overflow-tooltip
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('common.vendorName')"
          show-overflow-tooltip
          width="180"
        />
        <el-table-column
          align="center"
          prop="startAddress"
          show-overflow-tooltip
          :label="$t('logisticsMod.startAddress')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="endAddress"
          show-overflow-tooltip
          :label="$t('contractMod.destination')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="sumPrice"
          show-overflow-tooltip
          :label="$t('logisticsMod.totalPriceRMB')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="bidResult"
          show-overflow-tooltip
          :formatter="bidResultFormatter"
          :label="$t('logisticsMod.decisionResult')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="ifProxy"
          show-overflow-tooltip
          :formatter="formattor"
          :label="$t('bid_mod.isProxyBidding')"
          width="150"
        />
      </el-table>
    </el-collapse-item>
    <!-- 报价明细信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.quotaDetailInfo')"
      name="3"
    >
      <el-table
        :data="lgtVendorQuotedLines"
        style="width: 100%"
        border
        height="250px"
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('purSettlementMod.tabindex')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          show-overflow-tooltip
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('common.vendorName')"
          show-overflow-tooltip
          width="180"
        />
        <el-table-column
          align="center"
          prop="startAddress"
          show-overflow-tooltip
          :label="$t('logisticsMod.startAddress')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="endAddress"
          show-overflow-tooltip
          :label="$t('contractMod.destination')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="leg"
          show-overflow-tooltip
          label="leg"
          width="150"
        />
        <el-table-column
          align="center"
          prop="expenseItem"
          show-overflow-tooltip
          :formatter="expenseItemFormatter"
          :label="$t('logisticsMod.expenseItem')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="chargeMethod"
          show-overflow-tooltip
          :formatter="chargeMethodFormatter"
          :label="$t('logisticsMod.chargeMethod')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="chargeUnit"
          show-overflow-tooltip
          :formatter="chargeUnitFormatter"
          :label="$t('logisticsMod.chargeUnit')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="number"
          show-overflow-tooltip
          :label="$t('bid_mod.quantity')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="maxCost"
          show-overflow-tooltip
          :label="$t('logisticsMod.maxCost')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="minCost"
          show-overflow-tooltip
          :label="$t('logisticsMod.minCost')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="expense"
          show-overflow-tooltip
          :label="$t('bid_mod.costType')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="currency"
          :formatter="currencyFormatter"
          show-overflow-tooltip
          :label="$t('bid_mod.currencyName')"
          width="220"
        />
        <el-table-column
          align="center"
          prop="totalAmount"
          show-overflow-tooltip
          :label="$t('logisticsMod.totalPriceRMB')"
          width="150"
        />
      </el-table>
    </el-collapse-item>
    <!-- 技术标信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.techBidInfo')"
      name="4"
    >
      <shipTableClumn
        ref="shipTableClumnId"
        :table-header="tableHeader"
        operate-flag-type="vendorOperateFlag"
        visible-flag-type="vendorVisibleFlag"
        :schedule-form="scheduleForm"
        :transport-flag="transportFlag"
        :is-read-only="true"
      >
        <template slot="header">
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('common.vendorCode')"
            :show-overflow-tooltip="true"
            width="150"
          />
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            :show-overflow-tooltip="true"
            width="150"
          />
        </template>
      </shipTableClumn>
      <!-- 000 -->
      <span>{{ $t("logisticsMod.techSelectionConclusion") }}</span>
      <el-input
        v-model="allParams.biding.technoSelection"
        type="textarea"
        :rows="2"
        disabled
      />
    </el-collapse-item>
  </el-collapse>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'BiddingProjectDetailInfo',
  components: {
    QuickSearch,
    shipTableClumn,
    FormWrapper
  },
  props: [
    'lgtBiding',
    'allParams',
    'lgtVendorQuotedSums',
    'currentRound',
    'lgtVendorQuotedLines',
    'scheduleForm',
    'tableHeader'
  ],
  data () {
    return {
      formLabelWidth: '120px',
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      preArr: [{ prop: 'round', label: this.$t('bidMod.bidingRound') }]
    }
  },
  computed: {
    transportFlag () {
      // 陆运、铁运
      if (
        ['LAND_TRANSPORT', 'RAILWAY_TRANSPORT'].includes(
          this.allParams.biding.transportModeCode
        )
      ) {
        return true
      } else {
        return false
      }
    }
  },
  watch: {},
  mounted () {
  },
  methods: {
    expenseItemFormatter (row) {
      return this.$getDictLabel('CHARGE_NAME', row.expenseItem)
    },
    chargeMethodFormatter (row) {
      return this.$getDictLabel('CHARGE_LEVEL', row.chargeMethod)
    },
    chargeUnitFormatter (row) {
      return this.$getDictLabel('SUB_LEVEL', row.chargeUnit)
    },
    currencyFormatter (row) {
      return this.$getDictLabel('currency', row.currency)
    },
    bidResultFormatter (row) {
      return this.$getDictLabel('BIDDING_SELECT_STATES', row.bidResult)
    },
    wholeArkFormatter (row) {
      return this.$getDictLabel('PAYMENT_MODE', row.wholeArk)
    },
    formattor (row) {
      return row.ifProxy == 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    getQuerydata (v) {
      let round = this.currentRound
      if (v.round) {
        round = v.round
      }
      this.$emit('getApprovalDetails', round)
    }
  }
}
</script>
<style scoped></style>
