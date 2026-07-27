<template>
  <SrmDialog
    :title="$t('cusEntry.inq.inquiryList')"
    :visible.sync="inquiryVisible"
    :close-on-click-modal="false"
    size="xLarge"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQueryData"
    />
    <el-table
      ref="inquiryTable"
      border
      :data="tableData"
      max-height="350"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        align="center"
        width="50"
      />
      <el-table-column
        prop="useDeptName"
        align="center"
        :label="$t('cusEntry.inq.useDeptName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extOrderCount"
        align="center"
        :label="$t('cusEntry.inq.extOrderCount')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="souNo"
        align="center"
        :label="$t('bidMod.inquiryNo')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="itemCode"
        align="center"
        :label="$t('cusEntry.inq.materialCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="itemDesc"
        align="center"
        :label="$t('cusEntry.inq.materialName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extMaterialModel"
        align="center"
        :label="$t('cusEntry.inq.extMaterialModel')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="unit"
        align="center"
        :label="$t('cusEntry.inq.baseMeasurmentUnit')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.unit ? $getDictLabel('unit', row.unit) : ''
        }"
      />
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('cusEntry.inq.quantity')"
        width="100"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extBrand"
        align="center"
        :label="$t('cusEntry.inq.brand')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="standardNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        min-width="100"
      />
      <!--t 未税总价-->
      <el-table-column
        align="center"
        prop="standardNotaxTotalPrice"
        :label="$t('cusEntry.bidMod.orderNotaxAmount')"
        min-width="100"
      />
      <el-table-column
        prop="extAreaCode"
        align="center"
        :label="$t('cusEntry.inq.extAreaName')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.extAreaCode ? $getDictLabel('REGION', row.extAreaCode) : ''
        }"
      />
      <el-table-column
        prop="latestMinNotaxPrice"
        align="center"
        :label="$t('cusEntry.inq.latestMinNotaxPrice')"
        min-width="140"
        show-overflow-tooltip
      />
      <el-table-column
        prop="priceFloatScale"
        align="center"
        :label="$t('cusEntry.inq.priceFloatScale')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="latestMinVendorName"
        align="center"
        :label="$t('cusEntry.inq.latestMinVendorName')"
        min-width="140"
        show-overflow-tooltip
      />
      <el-table-column
        prop="latestMinBrand"
        align="center"
        :label="$t('cusEntry.inq.latestMinBrand')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extWinReason"
        align="center"
        :label="$t('cusEntry.inq.extWinReason')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="specialPaymentRemark"
        align="center"
        :label="$t('cusEntry.inq.specialPaymentRemark')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="orderRemark"
        align="center"
        :label="$t('cusEntry.inq.suppilerRemark')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extLeadTime"
        align="center"
        :label="$t('cusEntry.inq.extLeadTime')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="buyerNickname"
        align="center"
        :label="$t('cusEntry.inq.buyerUsername')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="applyType"
        align="center"
        :label="$t('cusEntry.inq.applyType')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.applyType ? $getDictLabel('application_form_type', row.applyType) : ''
        }"
      />
      <el-table-column
        prop="extWarrantyPeriod"
        align="center"
        :label="$t('cusEntry.inq.extWarrantyPeriod')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extPredictPrice"
        align="center"
        :label="$t('cusEntry.inq.extPredictPrice')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="extPredictAmount"
        align="center"
        :label="$t('cusEntry.inq.extPredictAmount')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="sourceFromType"
        align="center"
        :label="$t('cusEntry.inq.sourceFromType')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.sourceFromType ? $getDictLabel('EXT_SOU_FIX_PRICE_LINE_FROM_TYPE', row.sourceFromType) : ''
        }"
      />
      <el-table-column
        prop="extBuyType"
        align="center"
        :label="$t('cusEntry.inq.extBuyType')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.extBuyType ? $getDictLabel('PR_BUY_TYPE', row.extBuyType) : ''
        }"
      />
    </el-table>
    <CPagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @current-change="paginationCurrentChange"
      @size-change="paginationSizeChange"
    />
    <div slot="footer">
      <el-button
        @click="inquiryVisible = false"
      >
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import CPagination from 'lib@/components/c-pagination'
export default {
  name: 'InquiryDialog',
  components: {
    FormWrapper,
    CPagination
  },
  props: {
    /* 弹窗显隐控制 */
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      preArr: [
        {
          prop: 'souNo',
          label: this.$t('bidMod.inquiryNo')
        },
        {
          prop: 'itemCode',
          label: this.$t('cusEntry.inq.materialCode')
        },
        {
          prop: 'buyerUsername',
          label: this.$t('cusEntry.inq.buyerUsername')
        },
        {
          prop: 'orgOuId',
          label: this.$t('cusEntry.inq.orgDeptName'),
          type: 'quicksearch',
          name: 'scc_base_organization',
          preQueryData: { 't.organization_type_code': 'OU' },
          showKey: 'organizationName',
          propKey: 'organizationId'
        }
      ],
      tableData: [],
      selectRows: [],
      pagination: {
        total: 0,
        pageSize: 15,
        pageNum: 1
      },
      queryParams: {}
    }
  },
  computed: {
    inquiryVisible: {
      get () {
        return this.visible
      },
      set (value) {
        this.$emit('update:visible', value)
      }
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler (newValue) {
        if (newValue) {
          this.getQueryData()
        }
      }
    }
  },
  methods: {
    /* 页码变更 */
    paginationCurrentChange (pageNum) {
      this.pagination.pageNum = pageNum
      this.getQueryData(this.queryParams)
    },
    /* 页条数变更 */
    paginationSizeChange (pageSize) {
      this.pagination.pageSize = pageSize
      this.getQueryData(this.queryParams)
    },
    /* 查询 */
    getQueryData (params = {}) {
      this.queryParams = params
      const {
        pageSize,
        pageNum
      } = this.pagination
      const queryParams = {
        pageNum,
        pageSize,
        ...params
      }
      inqBuyerHttp.price.inquiry(queryParams).then(res => {
        if (res.data) {
          this.tableData = res.data.list
          this.pagination.total = res.data.total
        }
      })
    },
    /* 确认 */
    confirm () {
      if (this.selectRows.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectRowsMsg'))
        return false
      }
      this.inquiryVisible = false
      this.$emit('setMaterialDetail', { selectRows: this.selectRows, type: 'INQ' })
      /* 清空勾选状态 */
      this.$refs.inquiryTable.clearSelection()
    },
    /* 勾选数据 */
    handleSelectionChange (rows) {
      this.selectRows = rows
    }
  }
}
</script>
