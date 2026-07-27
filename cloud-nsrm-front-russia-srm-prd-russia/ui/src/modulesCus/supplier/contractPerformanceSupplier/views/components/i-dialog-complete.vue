<template>
  <srm-dialog
    v-if="visible"
    title="完成情况"
    :visible.sync="visible"
    width="600px"
    @close="$emit('update:show',false)"
  >
    <p class="title">
      合同验收情况
    </p>
    <el-table :data="tableData1" border stripe>
      <el-table-column prop="" label="合同验收单号" />
      <el-table-column prop="milestoneType" label="合同验收里程碑">
        <template slot-scope="scope">
          {{ store.getLabel('MILESTONE_SCHEDULE',scope.row.milestoneType) }}
        </template>
      </el-table-column>
      <el-table-column prop="nodePersonName" label="节点责任人" />
      <el-table-column prop="perSituation" label="开展情况" />
    </el-table>
    <p class="title">
      合同履约开票情况
    </p>
    <el-table :data="tableData2" border stripe>
      <el-table-column prop="" label="合同履约开票单号" />
      <el-table-column prop="milestoneType" label="开票里程碑">
        <template slot-scope="scope">
          {{ store.getLabel('MILESTONE_SCHEDULE',scope.row.milestoneType) }}
        </template>
      </el-table-column>
      <el-table-column prop="nodePersonName" label="节点责任人" />
      <el-table-column prop="perSituation" label="开展情况" />
    </el-table>
  </srm-dialog>
</template>
<script>
export default {
  name: 'IDialogComplete',
  props: ['visible', 'store', 'show', 'id'],
  data () {
    return {
      visible: this.show,
      tableData1: [],
      tableData2: []
    }
  },
  watch: {
    show (nVal) {
      this.visible = nVal
      if (nVal) {
        if (!this.id) return
        this.initTable()
      }
    }
  },
  methods: {
    async initTable () {
      const res = await this.$api.cmPerform.buyer.main.performOrder.getPerOrderById(this.id)
      const { perOrderPlanList, perOrderInPlanList } = res.data
      this.tableData1 = perOrderPlanList
      this.tableData2 = perOrderInPlanList
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
