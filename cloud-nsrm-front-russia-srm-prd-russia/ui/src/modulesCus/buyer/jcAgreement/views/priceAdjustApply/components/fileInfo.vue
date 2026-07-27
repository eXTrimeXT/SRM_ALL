<template>
  <div class="file-wrapper">
    <div class="btns">
      <el-button v-if="!readonly" type="primary" @click="addRows">
        新增
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        width="60"
        label="序号"
      />
      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          align: 'left',
          label: $t('bidMod.fileName'),
          prop: 'fileId',
          nameProp: 'fileName'
        }"
        :readonly="readonly"
        @on-change="filesChange"
      />

      <!--备注-->
      <el-table-column
        prop="remake"
        :label="$t('bidMod.remark')"
        min-width="100"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.remake" />
          <span v-else>{{ scope.row.remake }}</span>
        </template>
      </el-table-column>

      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="80"
      >
        <template v-slot="{ $index }">
          <el-button type="text" @click="deleteRows($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
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
    }
  },
  data () {
    return {
      tableData: []
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      }
    }
  },
  methods: {
    addRows () {
      this.tableData.push({
        fileId: '',
        fileName: '',
        remake: ''
      })
    },
    /* 内部查看文件变更 */
    filesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].fileId = fileId
      this.tableData[$index].fileName = fileName
    },
    deleteRows (index) {
      this.tableData.splice(index, 1)
    },
    getParams () {
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
