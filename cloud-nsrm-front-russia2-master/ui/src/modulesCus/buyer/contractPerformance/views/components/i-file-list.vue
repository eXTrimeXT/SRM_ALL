<template>
  <srm-dialog
    v-if="visible"
    title="里程碑节点附件信息"
    :visible.sync="visible"
    width="600px"
    @close="$emit('update:show',false)"
    v-on="$listeners"
  >
    <el-table :data="tableData" border stripe>
      <el-table-column type="index" width="60" :label="$t('contractMod.order')" />
      <el-table-column prop="milestoneType" :label="$t('dataConfMod.fileSourceName')">
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.fileId,
              fileName: scope.row.fileName
            }"
            :readonly="true"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createdFullName" :label="$t('dataConfMod.createdBy')" />
      <el-table-column prop="creationDate" :label="$t('common.creationTime')">
        <template slot-scope="scope">
          {{$parseTime(scope.row.creationDate)}}
        </template>
      </el-table-column>
    </el-table>
  </srm-dialog>
</template>
<script>
export default {
  name: 'IFileList',
  components: { },
  props: ['show', 'id'],
  data () {
    return {
      visible: this.show,
      tableData: []
    }
  },
  watch: {
    show (nVal) {
      console.log(nVal, 'nval')
      this.visible = nVal
      if (nVal) {
        if (!this.id) return
        this.initTable()
      }
    }
  },
  methods: {
    async initTable () {
      const res = await this.$api.cmPerform.buyer.main.performAcceptance.getByPerPlanMilestoneId(this.id)
      const { perAcceptanceAttList } = res.data
      this.tableData = perAcceptanceAttList
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
