<template>
  <div class="wrapper">
    <div class="btns mb-10">
      <MImport
        v-if="!readonly"
        ref="import"
        :title="$t('common.import')"
        :up-load-url="upLoadUrl"
        type="primary"
        :disabled="form.designId == null"
        :extra-post-data="extraPostData"
        :extra-data="extraPostData"
        @beforeUpload="(value, callback) => beforeUpload(value, callback)"
        @downloadTemplate="downloadTemplate"
        @handleSuccess="handleSuccess"
      />
      <el-button @click="handleExport">
        {{ $t('common.export') }}
      </el-button>
    </div>
    <el-table
      ref="graderTable"
      element-loading-background="rgba(0, 0, 0, 0.4)"
      stripe
      border
      height="300px"
      :data="tableData"
      style="height: 100%"
      highlight-current-row
      @row-click="rowClick"
    >
      <el-table-column
        prop="agreementId"
        label="ID"
        min-width="100"
      />
      <!-- 供货范围 -->
      <el-table-column
        prop="area"
        :label="$t('cusEntry.centralizedPurchase.supplyScope')"
        min-width="120"
      />
      <!-- 物资编码 -->
      <el-table-column
        prop="itemCode"
        :label="$t('common.materialCode')"
        min-width="120"
      />
      <!-- 物资名称 -->
      <el-table-column
        prop="itemDesc"
        :label="$t('common.materialName')"
        min-width="120"
      />
      <!-- 规格型号 -->
      <el-table-column
        prop="model"
        :label="$t('common.specification')"
        min-width="120"
      />
      <!-- 计量单位 -->
      <el-table-column
        prop="unit"
        :label="$t('cusEntry.competition.measurementUnit')"
        min-width="120"
      />
      <!-- 品牌 -->
      <el-table-column
        prop="brand"
        :label="$t('dataConfMod.band')"
        min-width="120"
      />
      <!-- 备注 -->
      <el-table-column
        prop="remark"
        :label="$t('common.remark')"
        min-width="120"
      />
      <!-- 中标供应商 -->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.wonBidVendor')"
        min-width="120"
      />
      <!-- 含税价格 -->
      <el-table-column
        prop="taxPrice"
        :label="$t('purchaseDemand.taxPrice')"
        min-width="120"
      />
      <!-- 未税价格 -->
      <el-table-column
        prop="notaxPrice"
        :label="$t('contractMod.notaxPrice')"
        min-width="120"
      />
    </el-table>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'AgreeInfo',
  components: {
    BaseTable,
    MImport,
    ExportExcel
  },
  props: {
    form: {
      type: Object,
      default: () => {}
    },
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      tableData: [],
      upLoadUrl: '/api-sou/price/adjustment/apply/importAgreementInfo',
      extraPostData: {}
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {},
  methods: {
    rowClick (row) {
      console.log(row, 'row')
      // 请求供应商表
      const obj = {"pageNum":1,"pageSize":10000,"__page":1,"__pagesize":10000,"projectId": row?.projectId, "vendorId": row?.vendorId}
      this.$http({
        url: `/api-sou/price/adjustment/apply/souVendorList`,
        method: 'POST',
        data: obj,
        loading: true
      }).then((res) => {
        this.$set(this.form, 'vendorList', res.data?.list)
      })
    },
    beforeUpload (value, callback) {
      this.extraPostData.projectCode = this.form?.jcCode
    },
    handleSuccess () {
      this.$emit('fileSuccess')
    },
    downloadTemplate () {
      downloadFileLink(
        `/api-sou/price/adjustment/apply/downloadAgreementInfoTemplate`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleExport () {
      let href = `/api-sou/price/adjustment/apply/getAgreementUpload?projectCode=${this.form?.jcCode}`
      // 协议详情导出
      downloadFileLink(href, `${this.$t('cusEntry.supplement20250121.agreeDetailExport')}.xlsx`).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    }
  }

}
</script>
<style lang="scss" scoped>
.mb-10 {
  margin-bottom: 10px;
}
</style>
