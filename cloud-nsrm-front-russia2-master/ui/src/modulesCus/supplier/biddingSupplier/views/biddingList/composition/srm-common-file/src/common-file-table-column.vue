<template>
  <!--附件上传列-->
  <el-table-column v-bind="tableColumnOptionsDefault">
    <!--自定义表头-->
    <template
      v-if="uploadTips"
      #header
    >
      <span>{{ tableColumnOptionsDefault.label }}</span>
      <el-tooltip
        v-if="uploadTips"
        effect="dark"
        :content="uploadTips"
        placement="top"
        class="table-column-tooltip"
      >
        <em class="el-icon-warning tip-icon" />
      </el-tooltip>
    </template>

    <template v-slot="{ row, $index }">
      <!--非只读 非禁用 调用上传组件-->
      <CommonFileUpload
        v-if="!readonly && !fileDisabled"
        v-bind="$props"
        :default-file="{
          fileId: row[tableColumnOptionsDefault.prop],
          fileName: row[tableColumnOptionsDefault.nameProp]
        }"
        @on-change="value => $emit('on-change', { ...value, $index })"
        @on-standby="value => $emit('on-standby', { ...value, $index })"
        @on-success="value => $emit('on-success', { ...value, $index })"
        @on-error="value => $emit('on-error', { ...value, $index })"
        @on-progress="value => $emit('on-progress', { ...value, $index })"
        @on-exceed="value => $emit('on-exceed', { ...value, $index })"
      />

      <!--只读 禁用 调用列表组件-->
      <CommonFileList
        v-else
        v-bind="$props"
        :default-file="{
          fileId: row[tableColumnOptionsDefault.prop],
          fileName: row[tableColumnOptionsDefault.nameProp]
        }"
      />
    </template>
  </el-table-column>
</template>

<script>
/**
 * 表格模式
 */
import { propsMixin, uploadPropsMixin, listPropsMixin, tableColumnPropsMixin } from './util'
import CommonFileUpload from './common-file-upload'
import CommonFileList from './common-file-list'

export default {
  name: 'CommonFileTableColumn',

  components: {
    CommonFileUpload,
    CommonFileList
  },

  mixins: [propsMixin, uploadPropsMixin, listPropsMixin, tableColumnPropsMixin],

  computed: {
    // 表格配置混入默认配置
    tableColumnOptionsDefault () {
      return {
        label: this.$t('components.upload.fileUpload'),
        prop: this.fileKeyOptions.idKey,
        nameProp: this.fileKeyOptions.nameKey,
        minWidth: '150',
        align: 'left',
        ...(this.tableColumnOptions || {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
// 表格列头
.table-column-tooltip {
  color: #96999C;
  margin-left: 4px;
}
</style>
