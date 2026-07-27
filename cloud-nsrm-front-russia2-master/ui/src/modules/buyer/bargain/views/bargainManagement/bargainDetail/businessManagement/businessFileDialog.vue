<template>
  <SrmDialog
    :title="$t('bidMod.businessAttchDetail')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="businessFileList"
      style="width: 100%"
      border
    >
      <!--资料要求-->
      <el-table-column
        align="center"
        prop="fileRequire"
        :label="$t('bidMod.fileQualify')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--参考附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.refAttachment'),
          prop: 'requireDocId',
          nameProp: 'requireFileName',
          minHeight: ''
        }"
        readonly
      />

      <!--文件类型-->
      <el-table-column
        align="center"
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
        min-width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
      />

      <!--采购商备注-->
      <el-table-column
        align="center"
        prop="requireRemark"
        :label="$t('bidMod.vendorRemark')"
        min-width="90"
        show-overflow-tooltip
      />

      <!--报价附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.finalOffer'),
          prop: 'orderDocId',
          nameProp: 'orderFileName',
          minHeight: ''
        }"
        readonly
      />

      <!--供应商备注-->
      <el-table-column
        align="center"
        prop="orderComments"
        :label="$t('contractMod.vendorRemark')"
        min-width="100"
        show-overflow-tooltip
      />
    </el-table>
    <div style="text-align: right; padding-top: 15px">
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 报价附件
 */

export default {
  name: 'BusinessFileDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      businessFileList: []
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

  mounted () {
    if (this.editRow) {
      this.businessFileList = this.editRow.orderFileList || []
    }
  }
}
</script>
