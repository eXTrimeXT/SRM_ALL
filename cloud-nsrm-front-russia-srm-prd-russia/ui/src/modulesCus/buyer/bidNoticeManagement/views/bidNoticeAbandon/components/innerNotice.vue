<template>
  <div class="file-wrapper">
    <el-table
      border
      stripe
      :data="tableData"
      :column="tableColumns"
    >
      <el-table-column
        type="index"
        width="60"
        label="序号"
      />

      <el-table-column
        label="板块"
        prop="extOrgBuName"
        showshowOverflowTooltip
      />

      <el-table-column
        label="公司"
        prop="extOrgOuName"
        showshowOverflowTooltip
      />

      <el-table-column
        label="需求部门"
        prop="demandDepartmentName"
        showshowOverflowTooltip
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          align: 'left',
          label: '通知书附件',
          prop: 'attachmentId',
          nameProp: 'attachmentName'
        }"
        :readonly="readonly"
        @on-change="filesChange"
      />

      <!--是否发送-->
      <el-table-column
        prop="isSend"
        label="是否发送"
        min-width="100"
        :formatter="(row,column,cellValue) => $getDictLabel('YES_OR_NO',cellValue)"
      />
    </el-table>
  </div>
</template>
<script>
export default {
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      tableColumns: []
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    },
    showSend () {
      return this.form.status === 'APPROVED'
    }
  },
  methods: {
    addRows () {
      this.tableData.push({
        souDocId: '',
        souFileName: ''
      })
    },
    /* 内部查看文件变更 */
    filesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].attachmentId = fileId
      this.tableData[$index].attachmentName = fileName
    },
    deleteRows (index) {
      this.tableData.splice(index, 1)
    },
    getParamsData () {
      return this.tableData
    }
  }
}
</script>
<style lang="scss" scoped>
.btns {
  margin-bottom: 10px;
}
</style>
