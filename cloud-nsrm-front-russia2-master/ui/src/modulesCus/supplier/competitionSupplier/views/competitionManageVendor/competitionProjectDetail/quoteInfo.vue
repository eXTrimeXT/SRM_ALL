<template>
  <div class="quote-info">
    <el-table
      ref="requirementTable"
      :data="orderLineList"
      border
      @row-click="requirementRowClick"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!-- 物资名称 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.materialName')"
        prop="itemDesc"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 组合 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.combination')"
        prop="itemGroup"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 所属单位 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.belongCompany')"
        prop="affiliatedUnit"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 单位 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.unit')"
        prop="unit"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 月约产量 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.monthProduct')"
        prop="monthlyProduction"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 计量单位 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.measurementUnit')"
        prop="meteringUnit"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 起拍价格 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.startPrice')"
        prop="startPrice"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 梯次价格 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.cascadePrice')"
        prop="echelonPrice"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 本次报价 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.thisQuote')"
        prop="orderNowPrice"
        min-width="130"
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 报价明细
 */
import { carVendorHttp } from 'modcs@/competitionSupplier/api'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'QuoteInfo',

  inject: ['attrsParamsRow'],

  components: { FormWrapper },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* noting */ }
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      orderLineList: [],
      itemDesc: ''
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getOrderDetails()
        }
      },
      immediate: true
    }
  },

  methods: {
    async getOrderDetails () {
      if (!this.attrsParamsRow.projectId) {
        return
      }
      // let transfromParams = transformMQL.save('AuctSouOrderForVendor', {
      //   filter: {
      //     projectId: {
      //       eq: this.attrsParamsRow.projectId
      //     },
      //     itemDesc: {
      //       contains: this.itemDesc
      //     }
      //   },
      //   page: {
      //     sort: 'lastUpdateDate desc',
      //     pageNum: 1,
      //     pageSize: 1000
      //   }
      // }, 'listVendorOrderItemHis')
      const response = await carVendorHttp.order.getOrderDetails({ projectId: this.attrsParamsRow.projectId })
      if (response && response.data) {
        const {
          orderDetails = []
        } = response.data
        this.orderLineList = orderDetails
      }
    }
  }
}
</script>
