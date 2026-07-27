<template>
  <el-table
    :data="outerFileList"
    style="width: 100%"
    border
    height="133px"
  >
    <el-table-column
      align="center"
      type="index"
      width="50"
    />

    <!--t 附件名称-->
    <SrmCommonFile
      type="table-column"
      :table-column-options="{
        label: $t('bidMod.fileName'),
        prop: 'fileRelationId',
        nameProp: 'fileName'
      }"
      readonly
    />

    <!--t 备注-->
    <el-table-column
      align="center"
      prop="remark"
      :label="$t('bidMod.remark')"
      min-width="150"
    />
  </el-table>
</template>

<script>
/**
 * 查看附件
 */
export default {
  name: 'FileList',

  props: {
    outerFileList: Array
  }
}
</script>
