<template>
  <SrmDialog
    :title="$t('cusEntry.inq.recentPurchase')"
    :visible.sync="recentPurchaseVisible"
    :close-on-click-modal="false"
    size="xLarge"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQueryData"
    />
    <el-table
      ref="recentTable"
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
        type="index"
        align="center"
        :label="$t('cusEntry.inq.index')"
        width="50"
      />
      <el-table-column
        prop="orgName"
        align="center"
        :label="$t('cusEntry.inq.orgName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="requirementHeadNum"
        align="center"
        :label="$t('cusEntry.inq.requirementHeadNum')"
        min-width="120"
        show-overflow-tooltip
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
        prop="materialCode"
        align="center"
        :label="$t('cusEntry.inq.materialCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="materialName"
        align="center"
        :label="$t('cusEntry.inq.materialName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        prop="categoryName"
        align="center"
        :label="$t('cusEntry.inq.categoryName')"
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
        align="center"
        prop="requirementQuantity"
        :label="$t('cusEntry.inq.quantity')"
        width="100"
        show-overflow-tooltip
      />
      <el-table-column
        prop="unitCode"
        align="center"
        :label="$t('cusEntry.inq.baseMeasurmentUnit')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />
      <el-table-column
        prop="brand"
        align="center"
        :label="$t('cusEntry.inq.brand')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('cusEntry.inq.winSupplier')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="notaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        min-width="100"
      />
      <!--t 未税总价-->
      <el-table-column
        align="center"
        prop="totalAmount"
        :label="$t('cusEntry.bidMod.orderNotaxAmount')"
        min-width="100"
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
        prop="ceeaPrType"
        align="center"
        :label="$t('cusEntry.inq.applyType')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.ceeaPrType ? $getDictLabel('application_form_type', row.ceeaPrType) : ''
        }"
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
        prop="applyBy"
        align="center"
        :label="$t('cusEntry.inq.buyerUsername')"
        min-width="120"
        show-overflow-tooltip
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
        @click="recentPurchaseVisible = false"
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
          prop: 'requirementHeadNum',
          label: this.$t('cusEntry.inq.requirementHeadNum')
        },
        {
          prop: 'materialCode',
          label: this.$t('cusEntry.inq.materialCode')
        },
        {
          prop: 'applyBy',
          label: this.$t('cusEntry.inq.buyerUsername')
        }
        // {
        //   prop: 'orgId',
        //   label: this.$t('cusEntry.inq.orgDeptName'),
        //   type: 'quicksearch',
        //   name: 'scc_base_organization',
        //   preQueryData: { 't.organization_type_code': 'OU' },
        //   showKey: 'organizationName',
        //   propKey: 'organizationId'
        // }
      ],
      tableData: [],
      selectRows: [],
      queryParams: {},
      pagination: {
        total: 0,
        pageSize: 15,
        pageNum: 1
      }
    }
  },
  computed: {
    recentPurchaseVisible: {
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
      inqBuyerHttp.price.recentPurchase(queryParams).then(res => {
        this.tableData = res.data.list
        this.pagination.total = res.data.total
      })
    },
    /* 确认 */
    confirm () {
      if (this.selectRows.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectRowsMsg'))
        return false
      }
      this.recentPurchaseVisible = false
      this.$emit('setMaterialDetail', { selectRows: this.selectRows, type: 'PURCHASE_REQ' })
      /* 清空勾选状态 */
      this.$refs.recentTable.clearSelection()
    },
    /* 勾选数据 */
    handleSelectionChange (rows) {
      this.selectRows = rows
    }
  }
}
</script>
