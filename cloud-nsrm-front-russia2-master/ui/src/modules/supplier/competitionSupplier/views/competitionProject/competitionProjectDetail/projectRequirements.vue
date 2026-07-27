<template>
  <div class="project-requirements">
    <SrmRow>
      <SrmCol :init-col="1">
        <div style="padding-bottom: 5px">
          {{ $t('bidMod.resume') }}
        </div>
        <el-input
          v-model="baseInfo.requireDesc"
          type="textarea"
          :rows="2"
          disabled
        />
      </SrmCol>
    </SrmRow>

    <MainHeader
      :l-span="22"
      :r-span="2"
    >
      <template slot="left">
        <span style="padding-right: 11px">{{ $t('bidMod.demandDetail') }}</span>
      </template>
    </MainHeader>

    <el-table
      :data="requirementLineList"
      border
      height="345px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('bidMod.sequence')"
        width="65"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.procurementClass')"
        width="150"
        show-overflow-tooltip
      />

      <!--交货地点(仅外协)-->
      <el-table-column
        align="center"
        prop="deliveryPlace"
        :label="$t('bid_mod.deliveryPlace')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
        </template>
      </el-table-column>

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, colunm, value) => $getDictLabel('PRICE_TYPE', value)"
      />

      <!--需求数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.appraisRequired')"
        width="110"
        show-overflow-tooltip
      />

      <!--贸易条款-->
      <el-table-column
        align="center"
        show-overflow-tooltip
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        width="150"
        :formatter="(row, colunm, value) => $getDictLabel('trade_clause', value)"
      />

      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="100"
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import MainHeader from 'lib@/components/Table/MainHeader'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'

export default {
  name: 'ProjectRequirements',

  inject: ['attrsParamsRow'],

  components: {
    MainHeader,
    RenderAsyncText
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      requirementLineList: []
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getRequireInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取详情 */
    async getRequireInfo () {
      if (!this.attrsParamsRow.projectId) {
        return
      }

      const response = await compVendorHttp.order.getRequireInfo(this.attrsParamsRow.projectId)
      if (response && response.data) {
        this.requirementLineList = (response.data || []).map(item => {
          let DP
          try {
            DP = JSON.parse(item.deliveryPlace)
          } catch (e) { /* noting */ }

          return {
            ...item,
            deliveryPlace: DP
          }
        })
      }
    }
  }
}
</script>
