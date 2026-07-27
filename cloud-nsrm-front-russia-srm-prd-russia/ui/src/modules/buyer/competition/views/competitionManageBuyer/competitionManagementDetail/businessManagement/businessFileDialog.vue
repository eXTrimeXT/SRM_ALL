<template>
  <SrmDialog
    :title="$t('bidMod.attachedDetails')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="viewRow?.orderFileList || []"
      style="width: 100%"
      border
    >
      <!--文件类型-->
      <el-table-column
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
      />

      <!--报价附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.finalOffer'),
          prop: 'orderDocId',
          nameProp: 'orderFileName'
        }"
        readonly
      />

      <!--备注-->
      <el-table-column
        prop="orderRemark"
        :label="$t('bidMod.remark')"
        show-overflow-tooltip
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 商务附件
 */

export default {
  name: 'BusinessFileDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    viewRow: {
      type: Object,
      required: true
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
  }
}
</script>
