<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-table
        border
        :data="calibrateResultList"
        max-height="250"
      >
        <el-table-column
          type="index"
          align="center"
          width="50"
          fixed="left"
        />
        <!--物资名称-->
        <el-table-column
          align="center"
          :label="$t('cusEntry.competition.materialName')"
          prop="itemDesc"
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
        <!-- 月约产量 -->
        <el-table-column
          align="center"
          :label="$t('cusEntry.competition.monthProduct')"
          prop="monthlyProduction"
          min-width="120"
          show-overflow-tooltip
        />
        <!--中标供应商-->
        <el-table-column
          align="center"
          prop="winVendorName"
          :label="$t('cusEntry.competition.bidSupplier')"
          min-width="120"
        />
        <!--单价(元)-->
        <el-table-column
          align="center"
          prop="winVendorPrice"
          min-width="120"
          :label="$t('cusEntry.competition.price')"
        />
        <!--月总金额-->
        <el-table-column
          align="center"
          prop="monthTotalAmount"
          min-width="120"
          :label="$t('cusEntry.competition.monthAmount')"
        />
        <!--中标备注-->
        <el-table-column
          align="center"
          prop="selectRemark"
          min-width="120"
          :label="$t('cusEntry.competition.bidRemark')"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.selectRemark" :disabled="!editable" />
          </template>
        </el-table-column>
        <!--流标-->
        <el-table-column
          align="center"
          prop="selectStatus"
          min-width="120"
          :label="$t('cusEntry.competition.failBid')"
        >
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.selectStatus"
              true-label="WIN"
              false-label="FAIL"
              :disabled="!editable"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'EditCalibrateResult',
  props: {
    calibrateResultList: {
      type: Array,
      default () {
        return []
      }
    },
    editable: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {}
  },
  watch: {
    calibrateResultList: {
      immediate: true,
      deep: true,
      handler (newValue) {
        this.$emit('update:calibrateResultList', newValue)
      }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>

