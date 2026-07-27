<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewExtractRisk')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!-- 投标供应商 -->
    <div style="font-weight:bold; margin-bottom: 10px;">
      {{ $t('cusEntry.bidMod.bidVendor') }}
    </div>
    <el-table
      border
      :data="tenderVendorList"
      max-height="180"
      style="width: 100%"
      :cell-style="cellStyle"
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
        :label="$t('common.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 本次投标供应商 -->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('cusEntry.bidMod.currentBidVendor')"
        min-width="120"
        show-overflow-tooltip
      />
    </el-table>
    <!-- 专家自身及亲友履历 -->
    <div style="font-weight:bold; margin: 24px 0px 10px 0;">
      {{ $t('cusEntry.bidMod.resumes') }}
    </div>
    <el-table
      border
      :data="expertRiskList"
      max-height="180"
      style="width: 100%"
      :cell-style="cellStyle"
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
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('cusEntry.bidMod.resumes1')"
        min-width="120"
        show-overflow-tooltip
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 抽取风险
 */
export default {
  name: 'ExtractRiskDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    }
  },

  data () {
    return {
      tenderVendorList: [],
      expertRiskList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.getExtractRisk()
  },

  methods: {
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      if (row.riskFlag == 'Y' && column.property == 'vendorName') {
        return {
          'color': 'red'
        }
      }
    },
    /* 查询数据 */
    getExtractRisk () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getExtractRisk?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.tenderVendorList = res.data.tenderVendorList
          this.expertRiskList = res.data.expertRiskList
        }
      })
    }
  }
}
</script>
