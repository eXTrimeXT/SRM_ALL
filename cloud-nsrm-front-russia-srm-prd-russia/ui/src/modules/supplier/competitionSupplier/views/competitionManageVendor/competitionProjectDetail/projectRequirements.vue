<template>
  <div class="project-requirements">
    <MainHeader
      :l-span="22"
      :r-span="2"
    >
      <template slot="left">
        <span style="padding-right: 11px">{{ $t('bidMod.demandDetail') }}</span>
      </template>
    </MainHeader>

    <el-table
      ref="requirementTable"
      :data="requirementLineList"
      border
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--业务实体-->
      <el-table-column
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        width="150"
        show-overflow-tooltip
      />

      <!--是否无料号-->
      <el-table-column
        prop="noCodeItem"
        label="无料号寻源"
        width="150"
        :formatter=" (row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--物料编码-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="150"
      />

      <!--物料名称-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        :formatter=" (row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--采购品类-->
      <el-table-column
        prop="categoryName"
        :label="$t('vendorMod.category')"
        min-width="150"
        show-overflow-tooltip
      />

      <!-- 需求数量 -->
      <el-table-column
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity2')"
        min-width="200"
      />

      <!--交货日期-->
      <el-table-column
        prop="requireDate"
        :label="$t('contractMod.deliveryDate')"
        width="160"
        show-overflow-tooltip
        :formatter=" (row, column, cellValue) => $dayjsParse(cellValue)"
      />

      <!--起拍价-->
      <el-table-column
        align="right"
        prop="orderStartPrice"
        :label="$t('bidMod.startingPrice')"
        width="100"
      />

      <!--采购申请号-->
      <el-table-column
        prop="sourceFromNo"
        :label="$t('bid_mod.purchaseRequest')"
        width="150"
      />

      <!--采购申请行号-->
      <el-table-column
        prop="sourceFromLineNo"
        :label="$t('bid_mod.purchaseRequestRowNum')"
        width="150"
        show-overflow-tooltip
      />

      <!--价格有效期从-->
      <el-table-column
        prop="priceStartTime"
        label="价格有效期从"
        min-width="150"
        show-overflow-tooltip
      />

      <!--价格有效期至-->
      <el-table-column
        prop="priceEndTime"
        label="价格有效期至"
        min-width="150"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        :formatter=" (row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
      />

      <!--贸易条款(月)-->
      <el-table-column
        prop="tradeTerm"
        label="贸易条款(月)"
        width="150"
        :formatter=" (row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
      />

      <!--保质期-->
      <el-table-column
        prop="warrantyPeriod"
        label="保质期"
        width="150"
      />

      <!--备注-->
      <el-table-column
        prop="remark"
        :label="$t('bidMod.remark')"
        min-width="150"
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import MainHeader from 'lib@/components/Table/MainHeader'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import { transformMQL } from 'lib@/utils/util'

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

      let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.attrsParamsRow.projectId }], 'getRequireInfo')

      const response = await carVendorHttp.order.getRequireInfo(transformParams)
      if (response && response.data) {
        this.requirementLineList = (response.data.records || []).map(item => {
          return {
            ...item,
            ...item.auctSouItem
          }
        })
      }
    }
  }
}
</script>
