<template>
  <el-container
    class="flex-container the_onlineInvoice_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="pm:supOnlineInvoice:add"
            type="primary"
            @click="openDialog"
          >
            {{ $t("purSettlementMod.newOnlineInvoice") }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/invoice/onlineInvoice/listPage"
      />
      <!-- 弹框区域-->
      <srm-dialog
        :title="$t('purSettlementMod.newOnlineInvoice')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <div>
          <el-form
            ref="filterForm"
            :model="filterForm"
            label-width="80px"
            label-position="top"
            class="form-incontainer the_filter_form"
          >
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('quota.org')"
                  :label-width="formLabelWidth"
                >
                  <organization-selector
                    ref="organizationSelector"
                    v-model="filterForm.orgId"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :limit="false"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.inv')"
                  :label-width="formLabelWidth"
                >
                  <organization-selector
                    ref="organizationSelector"
                    v-model="filterForm.organizationId"
                    :parent-id="filterForm.orgId"
                    node-type="INV"
                    :placeholder="$t('common.pleaseSelect')"
                    :limit="false"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('contractMod.tradingLocations')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.organizationSite" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('common.vendor')"
                  :label-width="formLabelWidth"
                >
                  <quick-search
                    :show-input="filterForm.vendorCode"
                    show-key="companyCode"
                    :scope-data="filterForm"
                    name="scc_sup_company_info"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('purchaseDemand.vendorSite')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="filterForm.ceeaCostType"
                    code="BUSINESS_TYPE"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('supRisk.material')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.materialParam" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purSettlementMod.invoiceDateFrom')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker v-model="filterForm.startDate" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purSettlementMod.invoiceDateTo')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker v-model="filterForm.endDate" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('contractMod.categoryName')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.categoryName" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('contractMod.contractCode')"
                  :label-width="formLabelWidth"
                >
                  <quick-search
                    :show-input="filterForm.contractNo"
                    show-key="contractCode"
                    :scope-data="filterForm"
                    name="scc_contract_head"
                    @close-quicksearch="getcontractObj"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purSettlementMod.orderNumber')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.orderNumber" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purSettlementMod.invoiceNoticeNumber')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.invoiceNoticeNumber" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('bidMod.bidingNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.projectNum" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bidMod.bidingName')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="filterForm.projectName" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purSettlementMod.ifService')"
                  :label-width="formLabelWidth"
                >
                  <el-checkbox
                    v-model="filterForm.ifService"
                    true-label="Y"
                    false-label="N"
                  />
                </el-form-item>
              </el-col>
              <!-- <el-col>
                    <p style="padding:8px">
                      <el-button type="primary" @click="queryItemList">{{$t("common.search") }}</el-button>
                      <el-button @click="resetForm">{{$t("common.reset") }}</el-button>
                      <el-button type="primary" @click="openNewTab">{{$t("common.confirm")}}</el-button>
                    </p>
                  </el-col> -->
            </el-row>
            <el-row type="flex">
              <el-col>
                <p style="padding: 5px; float: right; margin-right: 20px">
                  <el-button
                    type="primary"
                    @click="queryItemList"
                  >
                    {{
                      $t("common.search")
                    }}
                  </el-button>
                  <el-button @click="resetForm">
                    {{
                      $t("common.reset")
                    }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="openNewTab"
                  >
                    {{
                      $t("common.confirm")
                    }}
                  </el-button>
                </p>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <el-table
          :data="displayMaterialItem"
          style="width: 100%"
          border
          height="345px"
          highlight-current-row
          @selection-change="handleSelectionChange2"
        >
          <el-table-column
            type="selection"
            width="55"
            fixed="left"
          />
          <el-table-column
            sortable
            align="center"
            type="index"
            width="50"
            :label="$t('common.sort')"
          />
          <el-table-column
            sortable
            align="center"
            prop="invoiceNoticeNumber"
            :label="$t('purSettlementMod.invoiceNoticeNumber')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="invoiceRow"
            :label="$t('purSettlementMod.lineNum')"
            width="130"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="orderNumber"
            :label="$t('purSettlementMod.orderNumber')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="lineNum"
            :label="$t('purSettlementMod.orderLineNumber')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="categoryName"
            :label="$t('contractMod.categoryName')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="itemCode"
            :label="$t('common.materialCode')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="itemName"
            :label="$t('common.materialName')"
            min-width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="receiveNum"
            :label="$t('orderMod.receiveNum')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="notInvoiceQuantity"
            :label="$t('purSettlementMod.notInvoiceQuantity')"
            width="130"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="invoiceQuantity"
            :label="$t('purSettlementMod.invoiceQuantity')"
            width="130"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.invoiceQuantity"
                v-input-format="{ type: 'float' }"
                @change="setRowAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            sortable
            align="center"
            prop="unitPriceExcludingTax"
            :label="$t('purSettlementMod.unitPriceExcludingTax')"
            width="130"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="noTaxAmount"
            :label="$t('purSettlementMod.noTaxAmount')"
            width="130"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="unitPriceContainingTax"
            :label="$t('quota.taxPrice')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="taxRate"
            :label="$t('bid_mod.taxRate')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="currencyName"
            :label="$t('quota.currency')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="ceeaExchangeRate"
            :label="$t('bid_mod.priceTax')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="contractNo"
            :label="$t('contractMod.contractCode')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="receiveOrderNo"
            :label="$t('orderMod.receiveOrderNo')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="receiveOrderLineNo"
            :label="$t('orderMod.receiveOrderLineNo')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="orgName"
            :label="$t('quota.org')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="organizationName"
            :label="$t('bid_mod.inv')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="ceeaCostType"
            :label="$t('purchaseDemand.vendorSite')"
            width="130"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            min-width="150"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="organizationSite"
            :label="$t('contractMod.tradingLocations')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="projectNum"
            :label="$t('contractMod.itemNumber')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="projectName"
            :label="$t('contractMod.itemName')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="taskNum"
            :label="$t('contractMod.taskNumber')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            sortable
            align="center"
            prop="taskName"
            :label="$t('contractMod.taskName')"
            width="100"
            show-overflow-tooltip
          />
        </el-table>
        <c-pagination
          style="margin:3px"
          :total="parentOrgTableDataPage.total"
          :page-num="parentOrgTableDataPage.pageNum"
          :page-size="parentOrgTableDataPage.pageSize"
          @current-change="parentDataCurrentChange"
          @size-change="parentDataSizeChange"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import supOnlineInvoiceDetail from './supOnlineInvoiceDetail'
import { parseTime } from '@/utils'

export default {
  name: 'SupOnlineInvoiceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPagination,
    OrganizationSelector,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preFormObj: {},
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'supOnlineInvoiceList',
      gridId: 'supOnlineInvoiceList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'onlineInvoiceNum',
          label: () => this.$t('purSettlementMod.onlineInvoiceNum')
        },
        {
          prop: 'startDate',
          label: () => this.$t('paymentType.paymentDateDueFrom'),
          type: 'date'
        },
        {
          prop: 'endDate',
          label: () => this.$t('paymentType.paymentDateDueTo'),
          type: 'date'
        },
        {
          prop: 'orgIds',
          label: () => this.$t('quota.org'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'costType',
          label: () => this.$t('purchaseDemand.vendorSite'),
          type: 'dict',
          code: 'BUSINESS_TYPE'

        },
        {
          prop: 'invoiceStatus',
          label: () => this.$t('purSettlementMod.invoiceStatus'),
          type: 'dict',
          code: 'INVOICE_STATUS'
        },
        {
          prop: 'importStatus',
          label: () => this.$t('purSettlementMod.importStatus'),
          type: 'dict',
          code: 'INVOICE_IMPORT_STATUS'
        },
        {
          prop: 'businessType',
          label: () => this.$t('dataConfMod.businessType'),
          type: 'dict',
          code: 'BUSINESS_TYPE'
        },
        {
          prop: 'taxInvoiceNum',
          label: () => this.$t('purSettlementMod.taxationInvoiceNum')
        },
        { prop: 'boeNo', label: () => this.$t('purSettlementMod.boeNo') },
        {
          prop: 'payMethod',
          label: () => this.$t('paymentType.paymentWay'),
          type: 'dict',
          code: 'PAYMENT_WAY'
        }
      ],
      queryParam: {},
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      selectionItem: [],
      displayMaterialItem: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      dialogFormVisible: false,
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      filterForm: {
        orgId: null,
        organizationId: null,
        materialParam: null,
        orgName: null,
        categoryCode: null,
        orderNumber: null,
        startDate: null,
        endDate: null,
        ifService: 'N'
      },
      formLabelWidth: '120px'
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'supOnlineInvoice'
        ) {
          // 供应商 工作台跳转
          this.queryParam.invoiceStatus = this.$route.params.invoiceStatus
          // this.firstLoad = false;
          this.preFormObj = Object.assign(
            {},
            { invoiceStatus: this.$route.params.invoiceStatus }
          )
        }
      }
    }
  },
  created () {
    if (this.curRole === 'VENDOR') {
      this.preArr[4] = {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        disabled: true,
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display'
      }
    }
    this.rolePermissions = this.userInfo.rolePermissions[0]
      ? this.userInfo.rolePermissions[0].roleCode
      : null // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    let _this = this
    this.tableHeader = [
      {
        prop: 'onlineInvoiceNum',
        label: _this.$t('purSettlementMod.onlineInvoiceNum'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'accountPayableDealine',
        label: _this.$t('paymentType.paymentDateDue'),
        width: 130,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'invoiceStatus',
        label: _this.$t('purSettlementMod.invoiceStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_STATUS'

      },
      {
        prop: 'importStatus',
        label: _this.$t('purSettlementMod.importStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_IMPORT_STATUS'

      },
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 },
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 },
      {
        prop: 'vendorName',
        label: _this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'costTypeName',
        label: _this.$t('purchaseDemand.vendorSite'),
        width: 120
        /* formattor (val) {
            return _this.$getDictLabelByValue(_this.costTypeList, val)
          } */
      },
      {
        prop: 'taxInvoiceNum',
        label: _this.$t('purSettlementMod.taxationInvoiceNum'),
        width: 120
      },
      { prop: 'boeNo', label: _this.$t('purSettlementMod.boeNo'), width: 120 },
      {
        prop: 'businessType',
        label: _this.$t('bidMod.businessType'),
        minWidth: 120,
        dataType: 'dict',
        code: 'BUSINESS_TYPE'

      },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('common.creationTime'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      { prop: 'comment', label: _this.$t('common.remark'), minWidth: 150 },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit')
            },
            code: 'pm:supOnlineInvoice:edit',
            show: row => ['DRAFT', 'REJECTED'].includes(row.invoiceStatus)
          },
          {
            callback: function (row) {
              this.vendorAbandon(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.cancelled')
            },
            code: 'pm:supOnlineInvoice:abandon',
            show: row =>
              ['DRAFT', 'REJECTED'].includes(row.invoiceStatus) &&
              _this.curRole === 'VENDOR'
          }
        ]
      }
    ]

    this.$nextTick(() => {
      // this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      let query = v || this.preFormObj
      this.queryParam = query
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 采购商审核
    buyerApproval (row) {
      this.$http({
        url: '/api-sup-ce/invoice/invoiceNotice/confirm',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 采购商退回
    buyerReject (row) {
      this.$emit('tab-add', {
        component: supOnlineInvoiceDetail,
        params: {
          flag: type,
          onlineInvoiceId: row.onlineInvoiceId,
          tabName: 'supOnlineInvoiceDetail' + row.onlineInvoiceNum
        },
        title: row.onlineInvoiceNum,
        name: 'supOnlineInvoiceDetail' + row.onlineInvoiceNum
      })
    },
    // 删除
    delRowData (row) {
      let onlineInvoiceId = row.onlineInvoiceId
      this.$api.pur.invoiceNoticeDel({ onlineInvoiceId }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    setRowAmount (row) {
      if (row.notInvoiceQuantity > 0) {
        if (row.invoiceQuantity > row.notInvoiceQuantity) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[0]')
          )
        }
      } else {
        if (
          row.invoiceQuantity < row.notInvoiceQuantity ||
          row.invoiceQuantity > 0
        ) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[0]')
          )
        }
      }
      row.noTaxAmount = Number(
        Number(row.invoiceQuantity) * Number(row.unitPriceExcludingTax || 0)
      ).toFixed(2)
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: supOnlineInvoiceDetail,
          params: {
            flag: 'add',
            tabName: 'supOnlineInvoiceDetail'
          },
          title: this.$t('purSettlementMod.newOnlineInvoice'),
          name: 'supOnlineInvoiceDetail'
        }
      } else {
        // 修改
        tab = {
          component: supOnlineInvoiceDetail,
          params: {
            flag: type,
            onlineInvoiceId: row.onlineInvoiceId,
            tabName: 'supOnlineInvoiceDetail' + row.onlineInvoiceNum
          },
          title: row.onlineInvoiceNum,
          name: 'supOnlineInvoiceDetail' + row.onlineInvoiceNum
        }
      }
      this.$emit('tab-add', tab)
    },
    openDialog () {
      this.parentOrgQueryForm = {
        pageNum: 1,
        pageSize: 10
      }
      this.resetForm()
      this.queryItemList()
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url:
          '/api-sup-ce/invoice/onlineInvoice/listPageInvoiceNoticeDetail',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogFormVisible = true
      })
    },
    // 【作废】
    vendorAbandon (row) {
      this.$http({
        url: '/api-sup-ce/invoice/onlineInvoice/vendorAbandon',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    openNewTab () {
      if (this.selectionItem.length === 0) {
        this.$message.error(this.$t('contractMod.msgSelData'))
        return
      }
      for (let row of this.selectionItem) {
        if (row.notInvoiceQuantity > 0) {
          if (row.invoiceQuantity > row.notInvoiceQuantity) {
            return this.$message.warning(
              this.$t('purSettlementMod.invoiceMsg[0]')
            )
          }
        } else {
          if (
            row.invoiceQuantity < row.notInvoiceQuantity ||
            row.invoiceQuantity > 0
          ) {
            return this.$message.warning(
              this.$t('purSettlementMod.invoiceMsg[0]')
            )
          }
        }
      }
      if (this.selectionItem.length > 1) {
        let typeArr = this.selectionItem.map(v => v.type)
        // let newArr = Array.from(new Set(typeArr));
        if (typeArr.includes('RECEIVE') && typeArr.includes('RETURN')) {
          let txnIdArr = this.selectionItem
            .filter(v => v.type === 'RECEIVE')
            .map(x => x.txnId)
          let ptxnIdArr = this.selectionItem
            .filter(v => v.type === 'RETURN')
            .map(x => x.parentTxnId)
          for (let i = 0; i < ptxnIdArr.length; i++) {
            if (!txnIdArr.includes(ptxnIdArr[i])) {
              this.$message.warning(this.$t('purSettlementMod.invoiceMsg[1]'))
              return
            }
          }
        }
      }
      for (let i = 1; i < this.selectionItem.length; i++) {
        if (this.selectionItem[0].orgId !== this.selectionItem[i].orgId) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[2]')
          )
        }
        if (this.selectionItem[0].vendorId !== this.selectionItem[i].vendorId) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[3]')
          )
        }
        if (
          this.selectionItem[0].ceeaCostType !==
          this.selectionItem[i].ceeaCostType
        ) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[4]')
          )
        }
        if (
          this.selectionItem[0].currencyCode !==
          this.selectionItem[i].currencyCode
        ) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[5]')
          )
        }
        if (this.selectionItem[0].taxKey !== this.selectionItem[i].taxKey) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[6]')
          )
        }
        if (
          this.selectionItem[0].projectNum !== this.selectionItem[i].projectNum
        ) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[7]')
          )
        }
        if (
          this.selectionItem[0].contractNo !== this.selectionItem[i].contractNo
        ) {
          return this.$message.warning(
            this.$t('purSettlementMod.invoiceMsg[8]')
          )
        }
      }
      // 新增
      this.dialogFormVisible = false
      this.$emit('tab-add', {
        component: supOnlineInvoiceDetail,
        params: {
          flag: 'add',
          tabName: 'supOnlineInvoiceDetail',
          tabList: this.selectionItem
        },
        title: this.$t('purSettlementMod.newOnlineInvoice'),
        name: 'supOnlineInvoiceDetail'
      })
      // this.invoicePunishes = this.invoicePunishes.concat(this.selectionItem);
    },
    handleSelectionChange2 (selection) {
      this.selectionItem = selection
    },
    resetForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = null
      }
      this.filterForm.ifService = 'N'
    },
    getcontractObj (val, scope) {
      scope.contractNo = val.contractCode
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    }
  }
}
</script>
<style scoped lang="scss">
.the_onlineInvoice_wrapper {
  .the_filter_form {
    .el-form-item {
      margin-bottom: 5px;
    }
  }
}
</style>
