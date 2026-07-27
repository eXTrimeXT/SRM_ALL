<template>
  <!-- 招标文件下载 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250205.downloadBidFile')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-button type="primary" style="margin-bottom: 12px;">
      {{ $t('common.download') }}
    </el-button>
    <el-table
      border
      :data="fileList"
      max-height="200"
      style="width: 100%"
      highlight-current-row
      @selection-change="selectionChange"
    >
      <el-table-column
        align="center"
        type="selection"
        fixed="left"
        width="55"
      />
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <!-- 归档文件 -->
      <el-table-column
        align="center"
        prop="orderDocId"
        :label="$t('cusEntry.supplement20250205.archiveFile')"
        min-width="120"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.orderDocId,
              fileName: scope.row.orderFileName
            }"
            readonly
          />
        </template>
      </el-table-column>
      <!-- 归档时间 -->
      <el-table-column
        align="center"
        prop="orderTime"
        :label="$t('cusEntry.supplement20250205.archiveTime')"
        min-width="120"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 归档文件
 */
export default {
  name: 'ArchivedFileDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },

  data () {
    return {
      fileList: [],
      selectedList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.getBusOrderFile()
  },

  methods: {
    selectionChange (val) {
      this.selectedList = val
    },
    /* 查询数据 */
    getBusOrderFile () {}
  }
}
</script>
