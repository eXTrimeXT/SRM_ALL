<template>
<!-- 完成情况 -->
  <srm-dialog
    :title="$t('cusEntry.supplement20250205.completionStatus')"
    :visible.sync = "visible"
    @close="$emit('update:show',false)"
    v-if="visible"
    width="600px"
  >
    <p class="title">
      <!-- 合同验收情况 -->
      {{ $t("cusEntry.supplement20250205.contractAcceptanceStatus") }}
    </p>
    <el-table :data='tableData1' border stripe>
      <!-- 合同验收单号 -->
      <el-table-column prop="" :label="$t('bid_mod.perAcceptanceNo')" />
      <!-- 合同验收里程碑 -->
      <el-table-column prop="milestoneType" :label="$t('cusEntry.supplement20250205.contractAcceptanceMilestones')" >
        <template slot-scope="scope">
          {{store.getLabel('MILESTONE_SCHEDULE',scope.row.milestoneType)}}
        </template>
      </el-table-column>
      <!-- 节点责任人 -->
      <el-table-column prop="nodePersonName" :label="$t('contract_mod.nodePerson')" />
      <!-- 开展情况 -->
      <el-table-column prop="perSituation" :label="$t('cusEntry.supplement20250205.implementationStatus')" />
    </el-table>
    <p class="title">
      <!-- 合同履约开票情况 -->
      {{ $t("cusEntry.supplement20250205.contractPerformanceInvoicingStatus") }}
    </p>
    <el-table :data='tableData2' border stripe>
      <el-table-column prop="" :label="$t('contract_mod.processNum2')" />
      <el-table-column prop="milestoneType" :label="$t('cusEntry.supplement20250205.invoicingMilestones')" >
        <template slot-scope="scope">
          {{store.getLabel('MILESTONE_SCHEDULE',scope.row.milestoneType)}}
        </template>
      </el-table-column>
      <!-- 节点责任人 -->
      <el-table-column prop="nodePersonName" :label="$t('contract_mod.nodePerson')" />
      <!-- 开展情况 -->
      <el-table-column prop="perSituation" :label="$t('cusEntry.supplement20250205.implementationStatus')" />
    </el-table>
  </srm-dialog>
</template>
<script>
export default {
  name:'IDialogComplete',
  props:['visible','store','show','id'],
  watch:{
    show(nVal){
      this.visible = nVal;
      if(nVal){
        if(!this.id) return;
        this.initTable();
      }
    }
  },
  data(){
    return {
      visible:this.show,
      tableData1:[],
      tableData2:[],
    }
  },
  methods:{
    async initTable(){
      const res = await this.$api.cmPerform.buyer.main.performOrder.getPerOrderById(this.id);
      const {perOrderPlanList,perOrderInPlanList} = res.data;
      this.tableData1 = perOrderPlanList;
      this.tableData2 = perOrderInPlanList;
    }
  }
}
</script>
<style lang="scss" scoped>
.title {
  font-weight:bold;
  font-size: 12px;
}
</style>
