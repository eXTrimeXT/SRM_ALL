<template>
  <div>
    <!-- 查看定标说明 -->
    <div style="margin: 16px 0;font-size:14px; font-weight:bold">
      {{ $t('cusEntry.bidMod.calibrationDesc') }}
    </div>

    <el-table
      border
      style="width: 100%"
      :data="itemList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="isWin"
        :label="$t('cusEntry.bidMod.isWin')"
        min-width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="winRange"
        :label="$t('cusEntry.bidMod.winRange')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="winReason"
        :label="$t('cusEntry.bidMod.winReason')"
        min-width="200"
        :formatter="(row, column, cellValue) => $getDictLabel('CA_WIN_BID_REASON', cellValue)"
        show-overflow-tooltip
      />
    </el-table>

    <!-- 编制定标结果 -->
    <!-- <div style="margin: 30px 0;font-size:14px; font-weight:bold">
      {{ $t('cusEntry.bidMod.calibrationResult') }}
    </div>

    <FormWrapper
      :form-array="preArr"
      :select-dictionary="selectDictionary"
      @getFormData="getQueryData"
    /> -->

    <!--决标操作-->
    <!-- <el-dropdown style="margin-bottom: 10px" @command="changeSelectStatus">
      <el-button type="primary">
        {{ $t("bidMod.bidAwardOperation") }}
        <em class="el-icon-arrow-down el-icon--right" />
      </el-button>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="WIN" :disabled="readonly">
          {{ $t("bid_mod.winTheBidding") }}
        </el-dropdown-item>
        <el-dropdown-item command="FAIL" :disabled="readonly">
          {{ $t("bid_mod.lossTheBidding") }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown> -->

    <!-- <el-table
      border
      height="180"
      style="width: 100%"
      :data="resultList"
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        fixed="left"
      />
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="winStatus"
        :label="$t('cusEntry.bidMod.isWin')"
        min-width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('cusEntry.bidMod.itemDesc')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extBidSection"
        :label="$t('cusEntry.bidMod.extBidSection')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extQuantity"
        :label="$t('cusEntry.bidMod.extQuantity')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extCurrency"
        :label="$t('cusEntry.bidMod.extCurrency')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extPriceOrFixedNoTax"
        :label="$t('cusEntry.bidMod.extPriceOrFixedNoTax')"
        min-width="160"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('cusEntry.bidMod.unit')"
        min-width="100"
        show-overflow-tooltip
      />
    </el-table> -->
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { judgeManagement } from '@/library/composition/biddingLts/utils'

export default {
  name: 'CalibrationResult',
  components: { FormWrapper },

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
      itemList: [],
      resultList: [],
      selectDictionary: {},
      preArr: [
        {
          prop: 'vendorId',
          label: this.$t('bidMod.vendorName'),
          type: 'select'
        }
      ],
      selectedList: []
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.queryCaResult()
        }
      },
      immediate: true
    }
  },

  methods: {
    // 查看定标说明、供应商名称下拉值、定标结果
    queryCaResult () {
      bidBuyerHttp.calibration.queryCaResult({ projectId: this.biddingBase.projectId }).then(res => {
        if (res && res.data) {
          const { selectionResultList, vendorResultList, orderItemResultList } = res.data
          this.itemList = selectionResultList
          this.resultList = orderItemResultList
          this.selectDictionary = {
            vendorId: vendorResultList.map(item => {
              return {
                id: item.vendorId,
                value: item.vendorId,
                label: item.vendorName
              }
            })
          }
        }
      })
    },
    getQueryData (v) {
      const params = {
        projectId: this.biddingBase.projectId,
        vendorId: v ? v.vendorId : null
      }
      bidBuyerHttp.calibration.getEditSouResult(params).then(res => {
        this.resultList = res.data
      })
    },
    handleSelectionChange (val) {
      this.selectedList = val
    },
    /* 中标 / 落标 */
    async changeSelectStatus (type) {
      // 发起中标
      const response = await bidBuyerHttp.calibration.winOrLoss({
        type,
        projectId: this.biddingBase.projectId,
        orderItemList: this.selectedList
      })
      if (response) {
        this.$message.success(response.message)
        this.getQueryData()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
