<template>
  <el-collapse v-model="colVal">
    <el-collapse-item
      v-if="!approvalFlag"
      title="基础信息"
      name="1"
    >
      <SrmRow>
        <SrmCol :init-col="4">
          <el-form-item label="使用单位金额分析" prop="unitFlag">
            <DictSelect v-model="baseForm.unitFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="供方采购金额分析" prop="buyFlag">
            <DictSelect v-model="baseForm.buyFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="按物资品类分析" prop="categoryFlag">
            <DictSelect v-model="baseForm.categoryFlag" code="YES_OR_NO" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-collapse-item>
    <el-collapse-item title="工作日程" name="2">
      <WorkDay
        ref="workDay"
        :value.sync="baseForm.workList"
        :readonly="readonly"
      />
    </el-collapse-item>
    <el-collapse-item v-if="baseForm.unitFlag === 'Y'" title="使用单位金额分析" name="3">
      <OrgAmount
        ref="orgAmount"
        :value.sync="baseForm.unitList"
        readonly
      />
    </el-collapse-item>
    <el-collapse-item v-if="baseForm.buyFlag === 'Y'" title="供方采购金额分析" name="4">
      <VendorAmount
        ref="vendorAmount"
        :value.sync="baseForm.supplyList"
        readonly
      />
    </el-collapse-item>
    <el-collapse-item v-if="baseForm.categoryFlag === 'Y'" title="按物资品类分析" name="5">
      <CatAmount
        ref="catAmount"
        :value.sync="baseForm.categoryList"
        readonly
      />
    </el-collapse-item>
    <el-collapse-item title="招标策略及目标设定" name="6">
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
    <el-collapse-item title="招标供方履历" name="7">
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
