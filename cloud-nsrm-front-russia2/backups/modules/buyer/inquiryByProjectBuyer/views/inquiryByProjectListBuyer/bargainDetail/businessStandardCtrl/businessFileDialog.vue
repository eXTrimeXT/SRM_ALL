<template>
  <srm-dialog
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
        prop="reqInfo"
        :label="$t('bidMod.fileQualify')"
      />

      <!--参考附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.refAttachment'),
          prop: 'reqDocId',
          nameProp: 'reqFileName',
          minHeight: ''
        }"
        readonly
      />

      <!--文件类型-->
      <el-table-column
        align="center"
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
        :formatter="(row, column, cellValue) => $getDictLabel('REFERENCE_FILE_TYPE', cellValue)"
      />

      <!--采购商备注-->
      <el-table-column
        align="center"
        prop="reqComments"
        :label="$t('bidMod.vendorRemark')"
      />

      <!--报价附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.finalOffer'),
          prop: 'vendorDocId',
          nameProp: 'vendorFileName',
          minHeight: ''
        }"
        readonly
      />

      <!--供应商备注-->
      <el-table-column
        align="center"
        prop="vendorComments"
        :label="$t('contractMod.vendorRemark')"
      />
    </el-table>
    <div style="text-align: right; padding-top: 15px">
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 报价附件
 */

export default {
  name: 'BusinessFileDialog',

  props: {
    visible: {
      type: Boolean
    },
    editRow: {
      type: Object
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
  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal && this.editRow) {
          this.getBusinessFile()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询商务附件 */
    getBusinessFile () {
      this.$api.brg.inquiryByProject.getOrderFiles(this.editRow.orderHeadId).then(res => {
        if (res && res.data) {
          this.businessFileList = res.data
        }
      })
    }
  }
}
</script>
