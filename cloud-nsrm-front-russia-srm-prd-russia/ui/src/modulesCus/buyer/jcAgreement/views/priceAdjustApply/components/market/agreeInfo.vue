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
      <!-- 导出 -->
<!--      <ExportExcel-->
<!--        :filter-params="{'designId': form.designId}"-->
<!--        pageUrl="/api-sou/design/plan/getReqSupInfoList"-->
<!--        :disabled="form.designId == null"-->
<!--        :tableHeader="tableHeader"-->
<!--        export-mode="front"-->
<!--      />-->
      <el-button @click="handleExport">
        {{ $t('common.export') }}
      </el-button>
    </div>
<!--    <BaseTable-->
<!--      stripe-->
<!--      index-->
<!--      :data="tableData"-->
<!--      :columns="tableColumns"-->
<!--      :empty-text="$t('components.noData')"-->
<!--      border-->
<!--      max-height="250px"-->
<!--      @rowClick="rowClick"-->
<!--    />-->
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
      <el-table-column
        prop="area"
        label="供货范围"
        min-width="120"
      />
      <el-table-column
        prop="itemCode"
        label="物资编码"
        min-width="120"
      />
      <el-table-column
        prop="itemDesc"
        label="物资名称"
        min-width="120"
      />
      <el-table-column
        prop="model"
        label="规格型号"
        min-width="120"
      />
      <el-table-column
        prop="unit"
        label="计量单位"
        min-width="120"
      />
      <el-table-column
        prop="brand"
        label="品牌"
        min-width="120"
      />
      <el-table-column
        prop="remark"
        label="备注"
        min-width="120"
      />
      <el-table-column
        prop="vendorName"
        label="中标供应商"
        min-width="120"
      />
      <el-table-column
        prop="taxPrice"
        label="含税价格"
        min-width="120"
      />
      <el-table-column
        prop="notaxPrice"
        label="未税价格"
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
      tableColumns: [],
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
  created () {
    this.tableHeader = [
      {
        prop: 'area',
        label: '供货范围',
        width: 150
      },
      {
        prop: 'itemCode',
        label: '物资编码',
        width: 150
      },
      {
        prop: 'itemDesc',
        label: '物资名称',
        width: 150
      },
      {
        prop: 'model',
        label: '规格型号',
        width: 150
      },
      {
        prop: 'unit',
        label: '计量单位',
        width: 150
      },
      {
        prop: 'brand',
        label: '品牌',
        width: 150
      },
      {
        prop: 'remark',
        label: '备注',
        width: 150
      },
      {
        prop: 'vendorName',
        label: '中标供应商',
        width: 150
      },
      {
        prop: 'taxPrice',
        label: '含税价格',
        width: 150
      },
      {
        prop: 'notaxPrice',
        label: '未税价格',
        width: 150
      }
    ],
    this.tableColumns = [
      {
        attrs: {
          prop: 'agreementId',
          label: 'ID',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'area',
          label: '供货范围',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'itemCode',
          label: '物资编码',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'itemDesc',
          label: '物资名称',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'model',
          label: '规格型号',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'unit',
          label: '计量单位',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'brand',
          label: '品牌',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'remark',
          label: '备注',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'vendorName',
          label: '中标供应商',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'taxPrice',
          label: '含税价格',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'notaxPrice',
          label: '未税价格',
          minWidth: 120
        }
      }
    ]
  },
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
      downloadFileLink(href, '协议详情导出.xlsx').catch(() => {
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
