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
      <!-- 预付款 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.advancePayment')"
        prop="advanceCharge"
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
    </el-table>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { carVendorHttp } from 'modcs@/competitionSupplier/api'
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

      // let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.attrsParamsRow.projectId }], 'getRequireInfo')

      const response = await carVendorHttp.order.getRequireInfo(this.attrsParamsRow.projectId)
      if (response && response.data) {
        this.requirementLineList = response.data || []
      }
    }
  }
}
</script>
