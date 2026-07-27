<template>
  <el-table
    :data="fileData"
    border
    stripe
    style="margin-top: 10px"
  >
    <el-table-column
      type="index"
      width="60"
      :label="$t('common.sort')"
    />

    <!--材料类型-->
    <el-table-column prop="materialType" :label="$t('meeting.materialType')" />

    <!--附件名称-->
    <SrmCommonFile
      type="table-column"
      :table-column-options="{
        label: $t('vendorMod.attachmentName'),
        prop: 'fileuploadId',
        nameProp: 'materialMould'
      }"
      readonly
    />
  </el-table>
</template>

<script>
/**
 * 材料模板
 */

export default {
  name: 'MeetTopicsDetailMaterialTemplate',

  props: {
    detailData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      fileData: []
    }
  },

  watch: {
    detailData: {
      handler (val) {
        if (val && Array.isArray(val)) {
          this.fileData = val.concat()
        }
      },
      immediate: true,
      deep: true
    }
  }
}
</script>
