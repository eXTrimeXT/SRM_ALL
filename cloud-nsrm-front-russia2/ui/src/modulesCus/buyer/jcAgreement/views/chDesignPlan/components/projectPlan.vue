<template>
  <el-collapse v-model="colVal">
    <!-- 基础信息 -->
    <el-collapse-item
      v-if="!approvalFlag"
      :title="$t('common.baseInfo')"
      name="1"
    >
      <SrmRow>
        <SrmCol :init-col="4">
          <!-- 使用单位金额分析 -->
          <el-form-item :label="$t('cusEntry.supplement20250121.unitFlag')" prop="unitFlag">
            <DictSelect v-model="baseForm.unitFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- 供方采购金额分析 -->
          <el-form-item :label="$t('cusEntry.supplement20250121.buyFlag')" prop="buyFlag">
            <DictSelect v-model="baseForm.buyFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- 按物资品类分析 -->
          <el-form-item :label="$t('cusEntry.supplement20250121.categoryFlag')" prop="categoryFlag">
            <DictSelect v-model="baseForm.categoryFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-collapse-item>
    <!-- 工作日程 -->
    <el-collapse-item :title="$t('cusEntry.supplement20250121.workDay')" name="2">
      <WorkDay
        ref="workDay"
        :value.sync="baseForm.workList"
        :readonly="readonly"
      />
    </el-collapse-item>
    <!-- 使用单位金额分析 -->
    <el-collapse-item v-if="baseForm.unitFlag === 'Y'" :title="$t('cusEntry.supplement20250121.unitFlag')" name="3">
      <OrgAmount
        ref="orgAmount"
        :value.sync="baseForm.unitList"
        readonly
      />
    </el-collapse-item>
    <!-- 供方采购金额分析 -->
    <el-collapse-item v-if="baseForm.buyFlag === 'Y'" :title="$t('cusEntry.supplement20250121.buyFlag')" name="4">
      <VendorAmount
        ref="vendorAmount"
        :value.sync="baseForm.supplyList"
        readonly
      />
    </el-collapse-item>
    <!-- 按物资品类分析 -->
    <el-collapse-item v-if="baseForm.categoryFlag === 'Y'" :title="$t('cusEntry.supplement20250121.categoryFlag')" name="5">
      <CatAmount
        ref="catAmount"
        :value.sync="baseForm.categoryList"
        readonly
      />
    </el-collapse-item>
    <!-- 招标策略及目标设定 -->
    <el-collapse-item :title="$t('cusEntry.supplement20250121.biddingStrategyTargetSetting')" name="6">
      <!-- <GasOil
        ref="gasOil"
        :value.sync="baseForm.strategyList"
        :readonly="readonly"
      /> -->
      <OtherItem
        ref="otherItem"
        :value.sync="baseForm.otherList"
        :readonly="readonly"
        class="mt-10"
      />
    </el-collapse-item>
    <!-- 招标供方履历 -->
    <el-collapse-item :title="$t('cusEntry.supplement20250121.biddingVendorResume')" name="7">
      <BidTarget
        ref="bidTarget"
        :value.sync="baseForm.settingList"
        :readonly="readonly"
      />
    </el-collapse-item>
  </el-collapse>
</template>
<script>
import WorkDay from './projectPlan/workDay'
import OrgAmount from './projectPlan/orgAmount'
import CatAmount from './projectPlan/catAmount'
import VendorAmount from './projectPlan/vendorAmount'
import GasOil from './projectPlan/gasOil'
import OtherItem from './projectPlan/otherItem'
import BidTarget from './projectPlan/bidTarget'

export default {
  name: 'ProjectPlan',
  components: {
    WorkDay,
    OrgAmount,
    CatAmount,
    VendorAmount,
    GasOil,
    OtherItem,
    BidTarget
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    approvalFlag: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      colVal: ['1', '2', '3', '4', '5', '6', '7'],
      workDayList: [],
      orgAmountList: [],
      catAmountList: [],
      gasOilList: [],
      otherItemList: [],
      bidTargetList: []
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  }

}
</script>
<style scoped lang="scss">
.mt-10 {
  margin-top: 10px;
}
</style>
