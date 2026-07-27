<template>
  <el-container
    class="flex-container-notab the_expertDatabase_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader :l-span="24">
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="inq:priceCatalog:openDialog"
            @click="openDialog"
          >
            {{ $t('bidMod.bulkMaintainFwAgreement') }}
          </AuthorityButton>

          <!--自定义导出-->
          <ExportExcel
            page-url="/api-inq/price/priceLibrary/listPage"
            export-mode="front"
            type="default"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :timeout="10000000"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        big-data
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="handleCurrentChange"
        checkbox
        :pre-query-data="queryParam"
        row-key="priceLibraryId"
        reserve-selection
        url="/api-inq/price/priceLibrary/listPage"
      >
        <!-- <template #ceeaArrivalPlace="{ scope }">
          <render-async-text :cell-value="scope.row.ceeaArrivalPlace" />
        </template> -->
      </TableView>

      <!-- 阶梯价 -->
      <LadderPriceDialog
        v-if="ladderPriceDialogVisible"
        :visible.sync="ladderPriceDialogVisible"
        :view-row="editRow"
      />

      <!--付款条款-->
      <PaymentTypeDialog
        v-if="paymentTypeDialogVisible"
        :visible.sync="paymentTypeDialogVisible"
        :edit-row="{ paymentList: globalApprovalBiddingItemPaymentTermList }"
        readonly
        business-type="PRICE"
      />

      <!--合同选择-->
      <srm-dialog
        :title="$t('contractMod.selContract')"
        class="cat-selector-dialog"
        :visible.sync="sumFormVisible"
        :append-to-body="true"
        size="large"
        @close="dialogClose"
      >
        <el-form
          ref="form"
          :model="queryForm"
          label-width="80px"
        >
          <srm-row>
            <srm-col
              :xs="7"
              :sm="7"
              :md="7"
              :lg="7"
              :xl="7"
            >
              <el-form-item :label="$t('contractMod.contractName')">
                <el-input
                  v-model="queryForm.contractName"
                  @keyup.native.enter="queryContractData"
                />
              </el-form-item>
            </srm-col>
            <srm-col
              :xs="7"
              :sm="7"
              :md="7"
              :lg="7"
              :xl="7"
            >
              <el-form-item :label="$t('contractMod.contractNo_1')">
                <el-input
                  v-model="queryForm.contractNo"
                  @keyup.native.enter="queryContractData"
                />
              </el-form-item>
            </srm-col>
            <srm-col
              :xs="7"
              :sm="7"
              :md="7"
              :lg="7"
              :xl="7"
            >
              <el-form-item :label="$t('common.vendor')">
                <el-input
                  v-model="queryForm.vendorName"
                  @keyup.native.enter="queryContractData"
                />
              </el-form-item>
            </srm-col>
            <srm-col
              :xs="3"
              :sm="3"
              :md="3"
              :lg="3"
              :xl="3"
              style="text-align: right"
            >
              <el-button
                type="primary"
                @click="queryContractData"
              >
                {{ $t('common.search') }}
              </el-button>
            </srm-col>
          </srm-row>
        </el-form>

        <el-table
          ref="catSelector"
          style="width: 100%"
          max-height="260px"
          border
          highlight-current-row
          :data="contractDataList"
          @row-dblclick="getLineData"
        >
          <el-table-column
            prop="contractName"
            :label="$t('contractMod.contractName')"
          />
          <el-table-column
            prop="contractNo"
            :label="$t('contractMod.contractNo_1')"
          />
          <el-table-column
            prop="contractCode"
            :label="$t('contractMod.contractCode')"
          />
          <el-table-column
            prop="vendorName"
            :label="$t('common.vendor')"
          />
        </el-table>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="cancleSelector">
            {{ $t('common.cancel') }}
          </el-button>
        </div>
      </srm-dialog>

      <!--批量维护框架协议-->
      <BulkMaintainFwAgreementDialog
        v-if="bulkMaintainFwAgreementDialogVisible"
        :visible.sync="bulkMaintainFwAgreementDialogVisible"
        :current-rows="currentRows"
        @saveSuccess="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import ExportExcel from 'lib@/components/export-excel'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPriceDialog from './ladderPriceDialog'
import BulkMaintainFwAgreementDialog from './bulkMaintainFwAgreementDialog.vue'
import { priceManagementApi } from 'modb@/priceManagement/api'

export default {
  name: 'PriceCatalog',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    RenderAsyncText,
    ExportExcel,
    PaymentTypeDialog,
    LadderPriceDialog,
    BulkMaintainFwAgreementDialog
  },

  data () {
    return {
      queryForm: {
        priceLibraryList: [],
        ceeaOrganizationName: '',
        ceeaOrgName: '',
        vendorName: '',
        contractNo: '',
        contractName: ''
      },
      paymentTypeDialogVisible: false,
      globalApprovalBiddingItemPaymentTermList: [],
      contractDataList: [],
      queryParam: {},
      tableHeader: [],
      tableData: [],
      currentRows: [],
      sumFormVisible: false,
      sumForm: {
        contractHeadId: '',
        contractName: '',
        contractCode: ''
      },
      dialogFormVisible: false,
      preArr: [
        // 供应商名称
        {
          prop: 'vendorName',
          label: () => this.$t('bidMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        // 物料编码
        {
          prop: 'itemCode',
          label: () => this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 品类
        {
          prop: 'categoryCode',
          label: () => this.$t('common.category'),
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryCode',
          name: 'scc_base_purchase_category2'
        },
        // 价格审批单号
        { prop: 'approvalNo', label: () => this.$t('bidMod.approvalNo') },
        // 业务实体
        {
          prop: 'ceeaOrgCode',
          label: () => this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector',
          customProps: {
            id: 'organizationId',
            value: 'organizationCode',
            label: 'organizationName'
          }
        },
        // 库存组织
        {
          prop: 'ceeaOrganizationCode',
          label: () => this.$t('bid_mod.inv'),
          type: 'INVorganizationSelector',
          customProps: {
            id: 'organizationId',
            value: 'organizationCode',
            label: 'organizationName'
          }
        },
        // 合同编码
        {
          prop: 'contractCode',
          label: () => this.$t('contractMod.contractCode'),
          type: 'quicksearch',
          showKey: 'contractCode',
          propKey: 'contractCode',
          name: 'scc_contract_head'
        },
        {
          prop: 'priceStatus',
          label: () => this.$t('contractMod.priceStatus'),
          type: 'dict',
          code: 'PRICE_STATUS'
        }
      ],
      dictCodes: {
        unit: 'unit',
        currencyCode: 'currency',
        taxRate: 'tax',
        ceeaAllocationType: 'CeeaQuotaAllocationType',
        isLadder: 'YES_OR_NO',
        priceType: 'DMAND_LINE_TYPE',
        priceStatus: 'PRICE_STATUS'
      },
      ladderPriceDialogVisible: false,
      editRow: null,
      bulkMaintainFwAgreementDialogVisible: false
    }
  },

  created () {
    this.tableHeader = [
      // 价格库编号
      {
        prop: 'priceLibraryNo',
        label: this.$t('bidMod.priceLibraryNo'),
        minWidth: 140
      },
      // 价格审批单号
      {
        prop: 'approvalNo',
        label: this.$t('bidMod.approvalNo'),
        minWidth: 140
      },
      // 物料编码
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode'),
        minWidth: 120
      },
      // 物料名称
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemDesc'),
        minWidth: 150
      },
      // 品类
      {
        prop: 'categoryName',
        label: this.$t('common.category'),
        minWidth: 100
      },
      // 合同编码
      {
        prop: 'contractCode',
        label: this.$t('contractMod.contractCode'),
        minWidth: 150
      },
      // 合同名称
      {
        prop: 'contractName',
        label: this.$t('contractMod.contractName'),
        minWidth: 150
      },
      // 单位
      {
        prop: 'unit',
        label: this.$t('bidMod.unit'),
        minWidth: 70,
        formattor: val => this.$getDictLabel('unit', val)
      },
      // 供应商编码
      {
        prop: 'vendorCode',
        label: this.$t('bidMod.vendorCode'),
        minWidth: 120
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: this.$t('bidMod.vendorName'),
        minWidth: 150
      },
      // 业务实体
      {
        prop: 'ceeaOrgName',
        label: this.$t('bid_mod.businessEntity'),
        minWidth: 150
      },
      // 库存组织
      {
        prop: 'ceeaOrganizationName',
        label: this.$t('bid_mod.inv'),
        minWidth: 150
      },
      // 到货地点
      // {
      //   prop: 'ceeaArrivalPlace',
      //   label: this.$t('contractMod.arrivalPlace'),
      //   minWidth: 150,
      //   slot: 'ceeaArrivalPlace',
      //   showType: 'slot'
      // },
      // 价格类型
      {
        prop: 'priceType',
        label: this.$t('bid_mod.priceType'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('DMAND_LINE_TYPE', val)
      },
      // 未税单价
      {
        prop: 'notaxPrice',
        label: this.$t('bid_mod.untaxedPrice'),
        minWidth: 100
      },
      // 税率
      {
        prop: 'taxRate',
        align: 'right',
        label: this.$t('bidMod.taxRate'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('tax', val)
      },
      // 含税单价
      {
        prop: 'taxPrice',
        label: this.$t('bid_mod.taxUnitPrice'),
        minWidth: 100
      },
      // 结算币种
      {
        prop: 'currencyCode',
        label: this.$t('bidMod.currency'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('currency', val)
      },
      // 汇率
      {
        prop: 'exchangeRate',
        label: this.$t('contractMod.exchangeRate'),
        minWidth: 100
      },
      // 价格状态
      {
        prop: 'priceStatus',
        label: this.$t('contractMod.priceStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('PRICE_STATUS', val)
      },
      // 价格执行有效期自
      {
        prop: 'effectiveDate',
        label: this.$t('bid_mod.priceStartTime'),
        minWidth: 150,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 价格执行有效期至
      {
        prop: 'expirationDate',
        label: this.$t('bid_mod.priceEndTime'),
        minWidth: 150,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 是否阶梯报价
      {
        prop: 'isLadder',
        label: this.$t('bidMod.isLadder'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        show: row => row.isLadder === 'Y',
        formattor: val => this.$getDictLabel('YES_OR_NO', val),
        callback: row => this.openLadderPriceDialog(row)
      },
      // 付款条款
      {
        prop: 'paymentProvision',
        label: this.$t('paymentType.paymentType'),
        minWidth: 100,
        showType: 'button',
        btnStyle: 'text',
        formattor: () => this.$t('common.view'),
        callback: row => this.openPaymentTypeDialog(row)
      },
      // 供货周期(自然天)
      {
        prop: 'ceeaLt',
        label: this.$t('bidMod.deliveryCycleDays'),
        minWidth: 140
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        minWidth: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 更新时间
      {
        prop: 'lastUpdateDate',
        label: this.$t('bidMod.updateTime'),
        minWidth: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 贸易术语
      {
        prop: 'tradeTerm',
        label: this.$t('bidMod.tradeTerm'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('trade_clause', val)
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    cancleSelector () {
      this.sumFormVisible = false
    },

    dialogClose () {
      this.sumFormVisible = false
    },

    queryContractData () {
      this.queryForm.priceLibraryList = this.currentRows
      priceManagementApi.getOnShelvesContractList(this.queryForm).then((res) => {
        this.contractDataList = res.data
        this.sumFormVisible = true
      })
    },

    getLineData (row = {}) {
      this.sumForm.contractHeadId = row.contractHeadId
      this.currentRows.forEach((item) => {
        item.contractCode = row.contractCode
        item.contractName = row.contractName
        item.contractNo = row.contractNo
        item.vendorName = row.vendorName
      })
      this.comfirmSum()
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog (row) {
      this.globalApprovalBiddingItemPaymentTermList = row.priceLibraryPaymentTermList
      this.paymentTypeDialogVisible = true
    },

    /* 查询列表数据 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    // 打开批量维护框架协议弹框
    openDialog () {
      if (this.currentRows.length <= 0) {
        this.$message.warning(this.$t('oneStopShopping.createProjectMsg14'))
        return
      }
      if (this.currentRows.length > 1) {
        for (let i = 1; i < this.currentRows.length; i++) {
          if (this.currentRows[i].vendorId !== this.currentRows[0].vendorId) {
            this.$message.warning(this.$t('bidMod.selSameVendor'))
            return
          }
        }
      }

      this.bulkMaintainFwAgreementDialogVisible = true
    },

    // 上架选择合同编号后提交
    async comfirmSum () {
      const setData = {
        priceLibraryList: [...this.currentRows],
        contractHeadId: this.sumForm.contractHeadId
      }

      await priceManagementApi.putOnShelves(setData).then((res) => {
        if (!res.data) {
          this.$message.success(this.$t('bidMod.onShelfSucc'))
        }
        this.sumForm.contractCode = ''
        this.sumForm.contractName = ''
        this.sumForm.contractHeadId = ''
        this.sumFormVisible = false
        this.getQueryData()
      })
    },

    handleCurrentChange (val) {
      this.currentRows = val
    },

    /* 打开阶梯价弹窗 */
    openLadderPriceDialog (row) {
      this.editRow = row
      this.ladderPriceDialogVisible = true
    }
  }
}
</script>
