<template>
  <el-dialog
    :title="title"
    :width="width"
    :visible.sync="visible"
    append-to-body
    @close="$emit('update:show',false)"
  >
    <el-table
      :data="tableData"
      border
      stripe
      @row-dblclick="rowClick"
    >
      <el-table-column type="index" label="序号" width="100px" />
      <el-table-column prop="groupName" label="组名称" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click.stop="handleDelete(scope)">
            删除常用组
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
export default {
  name: 'CommonUser',

  props: {
    show: {
      type: Boolean,
      default: false
    },
    groupType: {
      type: String,
      default: ''
    }
  },

  data () {
    return {
      visible: this.show,
      title: '选择常用组',
      width: '600px',
      tableData: []
    }
  },

  watch: {
    show (nVal) {
      this.visible = nVal
      if (nVal) {
        this.initTable()
      }
    }
  },

  methods: {
    initTable () {
      this.$http({
        url: '/api-inq/inq/meetGroup/listPage',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 100,
          groupType: this.groupType
        }
      }).then(res => {
        this.tableData = res.data.list || []
      })
    },

    rowClick (row) {
      this.$http({
        url: '/api-inq/inq/meetGroup/listGroupMember',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 100,
          groupId: row.groupId,
          groupType: this.groupType
        }
      }).then(res => {
        this.$emit('row-click', res.data.list || [], row)
      })
    },

    handleDelete (scope) {
      if (!scope.row.groupId) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-inq/inq/meetGroup/delete',
          method: 'POST',
          params: { groupId: scope.row.groupId }
        }).then((res) => {
          this.$message.success(res.message)
          this.tableData.splice(scope.$index, 1)
        })
      })
    }
  }
}
</script>
