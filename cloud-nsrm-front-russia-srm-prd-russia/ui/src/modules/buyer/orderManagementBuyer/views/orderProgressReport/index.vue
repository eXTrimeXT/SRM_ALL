<template>
  <el-container
    class="flex-container-notab the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <!-- <main-header>
        <template slot="left">
        </template>
      </main-header> -->
      <el-container ddirection="vertical" style="min-height: 0;" class="tablePd">
        <el-main style="flex-grow: 1;display: flex;flex-direction: column">
          <el-table
            ref="mtTable"
            v-loading="loading"
            stripe
            border
            height="100%"
            :data="materialModle.tableData"

            style="height: 100%;"
            @selection-change="handleSelectionChange"
            @cell-click="cellClick"
          >
            <!-- 采购需求 -->
            <el-table-column
              prop="fullPathId"
              label="采购需求"
              show-overflow-tooltip
              align="center"
            >
              <!-- 事业部 -->
              <!-- <el-table-column
                prop="fullPathId"
                label="事业部"
                align="center"
                show-overflow-tooltip
                min-width="100"
              /> -->
              <!-- 需求工厂 -->
              <el-table-column
                prop="cfsaReceivedFactoryName"
                label="需求工厂"
                align="center"
                show-overflow-tooltip
                min-width="180"
              />
              <!-- 来源系统 -->
              <el-table-column
                prop="requirementSource"
                label="来源系统"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 需求创建人 -->
              <el-table-column
                prop="createdFullName"
                label="需求创建人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 需求单号 -->
              <el-table-column
                prop="requirementHeadNum"
                label="需求单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goRequirementDetail(scope.row)"
                  >
                    {{scope.row.requirementHeadNum}}
                  </el-button> -->
                  {{ scope.row.requirementHeadNum }}
                </template>
              </el-table-column>
              <!-- 需求日期 -->
              <el-table-column
                prop="requirementDate"
                label="需求日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 需求物料编号 -->
              <el-table-column
                prop="itemCode"
                label="需求物料编号"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 需求物料名称 -->
              <el-table-column
                prop="itemDesc"
                label="需求物料名称"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 采购分类 -->
              <el-table-column
                prop="categoryName"
                label="采购分类"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 需求数量 -->
              <el-table-column
                prop="requirementQuantity"
                label="需求数量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 采购单位 -->
              <el-table-column
                prop="unitName"
                label="采购单位"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 采购订单 -->
            <el-table-column
              prop="fullPathId"
              label="采购订单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 订单创建人 -->
              <el-table-column
                prop="orderCreatedBy"
                label="订单创建人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 采购订单号 -->
              <el-table-column
                prop="orderNumber"
                label="采购订单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goOrderDetail(scope.row)"
                  >
                    {{scope.row.orderNumber}}
                  </el-button> -->
                  {{ scope.row.orderNumber }}
                </template>
              </el-table-column>
              <!-- 订单类型 -->
              <el-table-column
                prop="orderTypeName"
                label="订单类型"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 付款条件 -->
              <el-table-column
                prop="termOfPaymentName"
                label="付款条件"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 制单日期 -->
              <el-table-column
                prop="orderDetailCreationDate"
                label="制单日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 订单需求日期 -->
              <el-table-column
                prop="orderRequirementDate"
                label="订单需求日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 供应商编码 -->
              <el-table-column
                prop="vendorCode"
                label="供应商编码"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 供应商名称 -->
              <el-table-column
                prop="vendorName"
                label="供应商名称"
                align="center"
                show-overflow-tooltip
                min-width="200"
              />
              <!-- 采购数量 -->
              <el-table-column
                prop="orderNum"
                label="采购数量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 订单含税价 -->
              <el-table-column
                prop="unitPriceContainingTax"
                label="订单含税价"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 订单含税金额 -->
              <el-table-column
                prop="amountContainingTax"
                label="订单含税金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 送货通知单 -->
            <el-table-column
              prop="fullPathId"
              label="送货通知单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 送货通知单号 -->
              <el-table-column
                prop="deliveryNoticeNum"
                label="送货通知单号"
                align="center"
                show-overflow-tooltip
                min-width="120"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goDeliveryNoticeDetail(scope.row)"
                  >
                    {{scope.row.deliveryNoticeNum}}
                  </el-button> -->
                  {{ scope.row.deliveryNoticeNum }}
                </template>
              </el-table-column>
              <!-- 制单人 -->
              <el-table-column
                prop="deliveryNoticeCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 制单时间 -->
              <el-table-column
                prop="deliveryNoticeCreationDate"
                label="制单时间"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 送货通知单数量 -->
              <el-table-column
                prop="noticeSum"
                label="送货通知单数量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 进厂通知单 -->
            <el-table-column
              prop="fullPathId"
              label="进厂通知单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 进厂通知单 -->
              <el-table-column
                prop="factoryNoticeNum"
                label="进厂通知单"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goFactoryNoticeDetail(scope.row)"
                  >
                    {{scope.row.factoryNoticeNum}}
                  </el-button> -->
                  {{ scope.row.factoryNoticeNum }}
                </template>
              </el-table-column>
              <!-- 制单人 -->
              <el-table-column
                prop="factoryNoticeDetailCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 制单时间 -->
              <el-table-column
                prop="factoryNoticeDetailCreationDate"
                label="制单时间"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 进厂通知单数量 -->
              <el-table-column
                prop="deliveryQuantity"
                label="进厂通知单数量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 入库单入库单 -->
            <el-table-column
              prop="fullPathId"
              label="入库单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 入库单号 -->
              <el-table-column
                prop="warehouseReceiptNumber"
                label="入库单号"
                align="center"
                show-overflow-tooltip
                min-width="120"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goWarehouseReceiptDetail(scope.row)"
                  >
                    {{scope.row.warehouseReceiptNumber}}
                  </el-button> -->
                  {{ scope.row.warehouseReceiptNumber }}
                </template>
              </el-table-column>
              <!-- 入库日期 -->
              <el-table-column
                prop="receiptCreationDate"
                label="入库日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 订单入库量 -->
              <el-table-column
                prop="warehouseReceiptQuantity"
                label="订单入库量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 入库金额 -->
              <el-table-column
                prop="warehouseReceiptAmount"
                label="入库金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 运费 -->
              <el-table-column
                prop="orderAmount"
                label="运费"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>

            <!-- 退货单退货单 -->
            <el-table-column
              prop="fullPathId"
              label="退货单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 制单人 -->
              <el-table-column
                prop="returnCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 退货单号 -->
              <el-table-column
                prop="returnOrderNumber"
                label="退货单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goreturnNumDetail(scope.row)"
                  >
                    {{scope.row.returnOrderNumber}}
                  </el-button> -->
                  {{ scope.row.returnOrderNumber }}
                </template>
              </el-table-column>
              <!-- 退货日期 -->
              <el-table-column
                prop="returnDate"
                label="退货日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 订单退货量 -->
              <el-table-column
                prop="returnNum"
                label="订单退货量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 退货金额 -->
              <el-table-column
                prop="returnAmount"
                label="退货金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 结算单 -->
            <el-table-column
              prop="fullPathId"
              label="结算单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 制单人 -->
              <el-table-column
                prop="statementCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 结算开始日期 -->
              <el-table-column
                prop="statementStartTime"
                label="结算开始日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 结算结束日期 -->
              <el-table-column
                prop="statementEndTime"
                label="结算结束日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 结算单号 -->
              <el-table-column
                prop="statementNumber"
                label="结算单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goStatementNumberDetail(scope.row)"
                  >
                    {{scope.row.statementNumber}}
                  </el-button> -->
                  {{ scope.row.statementNumber }}
                </template>
              </el-table-column>
              <!-- 结算数量 -->
              <el-table-column
                prop="statementReceiptQuantity"
                label="结算数量"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 结算金额 -->
              <el-table-column
                prop="statementReceiptAmount"
                label="结算金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 发票单 -->
            <el-table-column
              prop="fullPathId"
              label="发票单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 发票校验日期 -->
              <el-table-column
                prop="cfsaInvocieDate"
                label="发票校验日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 是否发票校验 -->
              <el-table-column
                prop="invoiceCheckName"
                label="是否发票校验"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 发票单号 -->
              <el-table-column
                prop="invoiceNoticeNumber"
                label="发票单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goInvoiceNoticeNumberDetail(scope.row)"
                  >
                    {{scope.row.invoiceNoticeNumber}}
                  </el-button> -->
                  {{ scope.row.invoiceNoticeNumber }}
                </template>
              </el-table-column>
              <!-- 制单人 -->
              <el-table-column
                prop="invoiceNoticeCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 发票总金额 -->
              <el-table-column
                prop="invoiceTotalAmount"
                label="发票总金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
            <!-- 付款申请单 -->
            <el-table-column
              prop="fullPathId"
              label="付款申请单"
              show-overflow-tooltip
              align="center"
            >
              <!-- 付款单号 -->
              <el-table-column
                prop="paymentApplyNumber"
                label="付款单号"
                align="center"
                show-overflow-tooltip
                min-width="160"
              >
                <template slot-scope="scope">
                  <!-- <el-button
                    type="text"
                    @click="goPaymentApplyNumberDetail(scope.row)"
                  >
                    {{scope.row.paymentApplyNumber}}
                  </el-button> -->
                  {{ scope.row.paymentApplyNumber }}
                </template>
              </el-table-column>
              <!-- 制单人 -->
              <el-table-column
                prop="paymentApplyCreatedBy"
                label="制单人"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 付款日期 -->
              <el-table-column
                prop="erpPaymentDate"
                label="付款日期"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 付款金额 -->
              <el-table-column
                prop="cfsaTotalAmountTax"
                label="付款金额"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 是否已付款 -->
              <el-table-column
                prop="isPayName"
                label="是否已付款"
                align="center"
                show-overflow-tooltip
                min-width="120"
              />
            </el-table-column>
          </el-table>
        </el-main>
        <el-footer class="page-bar">
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            :preQueryData="queryParam"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import CCategorySelect from 'lib@/components/c-category-select'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'OrderProgressReport',
  components: {
    OrganizationSelectTree,
    QuickSearch,
    CPagination,
    MainHeader,
    FormWrapper,
    CCategorySelect,
    ExportExcel
  },
  data () {
    return {
      gridId: 'list',
      loading: false,
      pageSize: 15,
      currentRow: null,
      tableHeader: [],
      queryParam: {},
      queryForm: [],
      purUnit: [],
      filterParams: {},
      orderTypeList: [],
      materialModle: {
        tableData: [],
        rules: {
          fullPathId: { type: 'string', required: true },
          vendorName: { type: 'string', required: true },
          categoryName: { type: 'string', required: true },
          warnBrandType: { required: true },
          startMonth: { type: 'string', required: true },
          endMonth: { type: 'string', required: true }
        }
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      selections: [],
      headerMonthList: []
    }
  },
  created () {
    this.queryForm = [
      {
        prop: 'cfsaReceivedFactoryId',
        label: '需求工厂', // '需求工厂'
        type: 'OUorganizationSelector'
      },
      { prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
        type: 'quicksearch',
        showKey: 'companyCode',
        name: 'scc_sup_company_info_display_buyer'
      },
      { prop: 'categoryName',
        label: '采购分类', // '采购分类'
        type: 'catSelect',
        showKey: 'categoryName'
      },
      {
        prop: 'statementStartTime',
        label: '需求日期从',
        type: 'date'
      },
      {
        prop: 'statementEndTime',
        label: '需求日期至',
        type: 'date'
      },
      { prop: 'orderType',
        label: '订单类型', // '订单类型'
        type: 'select',
        options: this.orderTypeList
      },
      { prop: 'requirementHeadNum',
        label: '需求单号'
      },
      { prop: 'orderNumber',
        label: '订单号'
      }
    ]
    const companyId = this.$store.getters.user.companyId
    this.fatchDictData()
    this.getQuerydata() //  查询数据
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    cellClick (row) {
      this.$refs.mtTable.toggleRowSelection(row, true)
    },
    // 入库单号
    goWarehouseReceiptDetail (row) {
      this.$router.push({
        name: 'orderStorage',
        params: {
          from: 'fromFun',
          funName: 'orderStorage',
          warehouseReceiptNumber: row.warehouseReceiptNumber // 业务单据ID

        }
      })
    },

    // 需求详情
    goRequirementDetail (row) {
      this.$router.push({
        name: 'applicationAndAudit',
        params: {
          from: 'fromFun',
          funName: 'applicationAndAudit',
          fdFormInstanceId: row.requirementHeadId, // 业务单据ID
          fdSubject: row.trialNumber
        }
      })
    },
    // 订单详情
    goOrderDetail (row) {
      this.$router.push({
        name: 'buyerPurchaseOrder',
        params: {
          from: 'fromFun',
          funName: 'buyerPurchaseOrder',
          fdFormInstanceId: row.orderId, // 业务单据ID
          fdSubject: row.orderNumber
        }
      })
    },
    // 送货通知单详情
    goDeliveryNoticeDetail (row) {
      this.$router.push({
        name: 'buyerDeliveryNotice',
        params: {
          from: 'fromFun',
          funName: 'buyerDeliveryNotice',
          fdFormInstanceId: row.deliveryNoticeId, // 业务单据ID
          fdSubject: row.deliveryNoticeNum
        }
      })
    },
    // 进厂通知单详情
    goFactoryNoticeDetail (row) {
      this.$router.push({
        name: 'IncomingNotice',
        params: {
          from: 'fromFun',
          funName: 'IncomingNotice',
          fdFormInstanceId: row.factoryNoticeId, // 业务单据ID
          fdSubject: row.factoryNoticeNum
        }
      })
    },
    // 退货通知单详情
    goreturnNumDetail (row) {
      this.$router.push({
        name: 'returnStorage',
        params: {
          from: 'fromFun',
          funName: 'returnStorage',
          fdFormInstanceId: row.returnNoticeId, // 业务单据ID
          fdSubject: row.returnNoticeNumber,
          returnOrderNumber: row.returnOrderNumber
        }
      })
    },
    // 结算单详情
    goStatementNumberDetail (row) {
      this.$router.push({
        name: 'purStatementBill',
        params: {
          from: 'fromFun',
          funName: 'purStatementBill',
          fdFormInstanceId: row.statementHeadId, // 业务单据ID
          fdSubject: row.statementNumber,
          statementHeadId: row.statementHeadId
        }
      })
    },
    // 发票单详情
    goInvoiceNoticeNumberDetail (row) {
      this.$router.push({
        name: 'purInvoice',
        params: {
          from: 'fromFun',
          funName: 'purInvoice',
          fdFormInstanceId: row.invoiceNoticeId, // 业务单据ID
          fdSubject: row.invoiceNoticeNumber,
          orderId: row.invoiceNoticeId
        }
      })
    },
    // 付款申请单详情
    goPaymentApplyNumberDetail (row) {
      this.$router.push({
        name: 'purPaymentApply',
        params: {
          from: 'fromFun',
          funName: 'purPaymentApply',
          fdFormInstanceId: row.paymentApplyHeadId, // 业务单据ID
          fdSubject: row.paymentApplyNumber,
          paymentApplyHeadId: row.paymentApplyHeadId
        }
      })
    },
    getQuerydata (v) {
      this.queryParam = v || {}
        this.pageInfo.pageNum = 1
        // this.loading = true
        this.$nextTick(() => this.fatchListData(this.queryParam))
    },
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'ORDER_TYPE' } // 订单类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [ORDER_TYPE, CFSA_MAINTAIN_WARN_BRAND_TYPE] = res.data
        this.orderTypeList = adaptDictData(
          ORDER_TYPE.ORDER_TYPE,
          'dict'
        )
        this.queryForm[6].options = this.orderTypeList
      })
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let res = {
        'code': '0',
        'data': {
          'pageNum': 1,
          'pageSize': 15,
          'endRow': 15,
          'hasNextPage': true,
          'hasPreviousPage': true,
          'isFirstPage': false,
          'isLastPage': false,
          'list': [
            {
              'amountContainingTax': 80,
              'categoryId': 504895102431232,
              'categoryName': '劳保防护用品',
              'cfsaInvocieDate': '2021-02-03 00:00:00',
              'cfsaReceivedFactoryCode': '2060',
              'cfsaReceivedFactoryId': 505211268259840,
              'cfsaReceivedFactoryName': '黑龙江省青冈长林肉类食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 280,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-04',
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '021198',
              'invoiceNoticeDetailId': 541390958063616,
              'invoiceNoticeId': 541390957944832,
              'invoiceNoticeNumber': 'IN21020300028',
              'invoiceTotalAmount': 80,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '98000000000004',
              'itemDesc': '手套',
              'itemId': 534010924511232,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '021198',
              'orderDetailCreationDate': '2021-02-03 16:21:34',
              'orderDetailId': 541384821071872,
              'orderId': 541384689991680,
              'orderNum': 100,
              'orderNumber': 'PO21020300038',
              'orderProgressId': 282191551502360,
              'orderRequirementDate': '2021-02-03',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2060',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '021198',
              'paymentApplyHeadId': 541627931893760,
              'paymentApplyNumber': 'PA21020400002',
              'receiptCreationDate': '2021-02-03 16:36:54',
              'requirementDate': '2021-02-03',
              'requirementHeadId': 541341315653632,
              'requirementHeadNum': '1000002232',
              'requirementLineId': 541341315710976,
              'requirementQuantity': 100,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '021198',
              'statementDetailId': 541390229139456,
              'statementEndTime': '2021-02-03',
              'statementHeadId': 541390228967424,
              'statementNumber': 'SA21020300019',
              'statementReceiptAmount': 80,
              'statementReceiptQuantity': 100,
              'statementStartTime': '2021-02-03',
              'tenantId': null,
              'termOfPayment': 'Z001',
              'termOfPaymentName': '现款现货',
              'unitCode': 'A23',
              'unitName': '双',
              'unitPriceContainingTax': 0.8,
              'userId': null,
              'vendorCode': 'S0007490',
              'vendorId': 534355879579648,
              'vendorName': '青冈县青冈镇联营生资商场',
              'version': null,
              'warehouseReceiptAmount': 80,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541388586508289,
              'warehouseReceiptNumber': '5000002730',
              'warehouseReceiptQuantity': 100
            },
            {
              'amountContainingTax': 80,
              'categoryId': 504895102431232,
              'categoryName': '劳保防护用品',
              'cfsaInvocieDate': '2021-02-03 00:00:00',
              'cfsaReceivedFactoryCode': '2060',
              'cfsaReceivedFactoryId': 505211268259840,
              'cfsaReceivedFactoryName': '黑龙江省青冈长林肉类食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 280,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-04',
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '021198',
              'invoiceNoticeDetailId': 541390958063616,
              'invoiceNoticeId': 541390957944832,
              'invoiceNoticeNumber': 'IN21020300028',
              'invoiceTotalAmount': 80,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '98000000000004',
              'itemDesc': '手套',
              'itemId': 534010924511232,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '021198',
              'orderDetailCreationDate': '2021-02-03 16:21:34',
              'orderDetailId': 541384821071872,
              'orderId': 541384689991680,
              'orderNum': 100,
              'orderNumber': 'PO21020300038',
              'orderProgressId': 282191551502361,
              'orderRequirementDate': '2021-02-03',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2060',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '021198',
              'paymentApplyHeadId': 541633276895232,
              'paymentApplyNumber': 'PA21020400003',
              'receiptCreationDate': '2021-02-03 16:36:54',
              'requirementDate': '2021-02-03',
              'requirementHeadId': 541341315653632,
              'requirementHeadNum': '1000002232',
              'requirementLineId': 541341315710976,
              'requirementQuantity': 100,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '021198',
              'statementDetailId': 541390229139456,
              'statementEndTime': '2021-02-03',
              'statementHeadId': 541390228967424,
              'statementNumber': 'SA21020300019',
              'statementReceiptAmount': 80,
              'statementReceiptQuantity': 100,
              'statementStartTime': '2021-02-03',
              'tenantId': null,
              'termOfPayment': 'Z001',
              'termOfPaymentName': '现款现货',
              'unitCode': 'A23',
              'unitName': '双',
              'unitPriceContainingTax': 0.8,
              'userId': null,
              'vendorCode': 'S0007490',
              'vendorId': 534355879579648,
              'vendorName': '青冈县青冈镇联营生资商场',
              'version': null,
              'warehouseReceiptAmount': 80,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541388586508289,
              'warehouseReceiptNumber': '5000002730',
              'warehouseReceiptQuantity': 100
            },
            {
              'amountContainingTax': 80,
              'categoryId': 504895102431232,
              'categoryName': '劳保防护用品',
              'cfsaInvocieDate': '2021-02-03 00:00:00',
              'cfsaReceivedFactoryCode': '2060',
              'cfsaReceivedFactoryId': 505211268259840,
              'cfsaReceivedFactoryName': '黑龙江省青冈长林肉类食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 280,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-04',
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '021198',
              'invoiceNoticeDetailId': 541390958063616,
              'invoiceNoticeId': 541390957944832,
              'invoiceNoticeNumber': 'IN21020300028',
              'invoiceTotalAmount': 80,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '98000000000004',
              'itemDesc': '手套',
              'itemId': 534010924511232,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '021198',
              'orderDetailCreationDate': '2021-02-03 16:21:34',
              'orderDetailId': 541384821071872,
              'orderId': 541384689991680,
              'orderNum': 100,
              'orderNumber': 'PO21020300038',
              'orderProgressId': 282191551502362,
              'orderRequirementDate': '2021-02-03',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2060',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '021198',
              'paymentApplyHeadId': 541641763803136,
              'paymentApplyNumber': 'PA21020400004',
              'receiptCreationDate': '2021-02-03 16:36:54',
              'requirementDate': '2021-02-03',
              'requirementHeadId': 541341315653632,
              'requirementHeadNum': '1000002232',
              'requirementLineId': 541341315710976,
              'requirementQuantity': 100,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '021198',
              'statementDetailId': 541390229139456,
              'statementEndTime': '2021-02-03',
              'statementHeadId': 541390228967424,
              'statementNumber': 'SA21020300019',
              'statementReceiptAmount': 80,
              'statementReceiptQuantity': 100,
              'statementStartTime': '2021-02-03',
              'tenantId': null,
              'termOfPayment': 'Z001',
              'termOfPaymentName': '现款现货',
              'unitCode': 'A23',
              'unitName': '双',
              'unitPriceContainingTax': 0.8,
              'userId': null,
              'vendorCode': 'S0007490',
              'vendorId': 534355879579648,
              'vendorName': '青冈县青冈镇联营生资商场',
              'version': null,
              'warehouseReceiptAmount': 80,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541388586508289,
              'warehouseReceiptNumber': '5000002730',
              'warehouseReceiptQuantity': 100
            },
            {
              'amountContainingTax': 19000,
              'categoryId': 504895119470592,
              'categoryName': '鲜头蹄类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2380',
              'cfsaReceivedFactoryId': 505211268710400,
              'cfsaReceivedFactoryName': '四川古川藏优食谷食品科技有限公司工厂',
              'cfsaRequirementLineNum': 1,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': 1,
              'deliveryNoticeCreatedBy': '028358',
              'deliveryNoticeCreationDate': '2021-02-03 13:47:13',
              'deliveryNoticeDetailId': 541346886868992,
              'deliveryNoticeId': 541346886770688,
              'deliveryNoticeNum': 'DN21020300004',
              'deliveryQuantity': 1000,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '028358',
              'factoryNoticeDetailCreationDate': '2021-02-03 13:47:51',
              'factoryNoticeDetailId': 541347044085760,
              'factoryNoticeId': 541347043860480,
              'factoryNoticeNum': 'EN21020300015',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40408070000010',
              'itemDesc': '鲜带筋短猪脚',
              'itemId': 534030766129152,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': 1000,
              'orderAmount': null,
              'orderCreatedBy': '028358',
              'orderDetailCreationDate': '2021-02-03 13:46:21',
              'orderDetailId': 541346672992256,
              'orderId': 541346503323648,
              'orderNum': 1000,
              'orderNumber': 'PO21020300023',
              'orderProgressId': 282191551502364,
              'orderRequirementDate': '2021-02-05',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2380',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': '2021-02-03 13:51:03',
              'requirementDate': '2021-02-05',
              'requirementHeadId': 541345796329472,
              'requirementHeadNum': 'PR21020300004',
              'requirementLineId': 541345796440064,
              'requirementQuantity': 1000,
              'requirementSource': 'SRM',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 19,
              'userId': null,
              'vendorCode': 'S0007443',
              'vendorId': 534355757445120,
              'vendorName': '绵阳市四汇勋洋贸易有限公司',
              'version': null,
              'warehouseReceiptAmount': 19000,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541347827724289,
              'warehouseReceiptNumber': '5000002674',
              'warehouseReceiptQuantity': 1000
            },
            {
              'amountContainingTax': 230,
              'categoryId': 504895100735488,
              'categoryName': '猪类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2370',
              'cfsaReceivedFactoryId': 505211268694016,
              'cfsaReceivedFactoryName': '黑龙江道台府食品有限公司工厂',
              'cfsaRequirementLineNum': 1,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': 1,
              'deliveryNoticeCreatedBy': '026480',
              'deliveryNoticeCreationDate': '2021-02-03 15:33:46',
              'deliveryNoticeDetailId': 541373072363520,
              'deliveryNoticeId': 541373072199680,
              'deliveryNoticeNum': 'DN21020300011',
              'deliveryQuantity': 10,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '026480',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:34:30',
              'factoryNoticeDetailId': 541373253660672,
              'factoryNoticeId': 541373253541888,
              'factoryNoticeNum': 'EN21020300021',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '94506000000063',
              'itemDesc': '脊皮',
              'itemId': 534029025857536,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': 10,
              'orderAmount': null,
              'orderCreatedBy': '026480',
              'orderDetailCreationDate': '2021-02-03 14:48:59',
              'orderDetailId': 541362066853888,
              'orderId': 541362010324992,
              'orderNum': 10,
              'orderNumber': 'PO21020300029',
              'orderProgressId': 282191551502365,
              'orderRequirementDate': '2021-02-15',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2370',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': '2021-02-03 15:40:43',
              'requirementDate': '2021-02-15',
              'requirementHeadId': 541357530140672,
              'requirementHeadNum': 'PR21020300005',
              'requirementLineId': 541357530275840,
              'requirementQuantity': 10,
              'requirementSource': 'SRM',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': 'Z005',
              'termOfPaymentName': '30天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 23,
              'userId': null,
              'vendorCode': 'S0007173',
              'vendorId': 533979015147520,
              'vendorName': '哈尔滨帕沸特商贸有限公司',
              'version': null,
              'warehouseReceiptAmount': 230,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541374779232257,
              'warehouseReceiptNumber': '5000002718',
              'warehouseReceiptQuantity': 10
            },
            {
              'amountContainingTax': 13000,
              'categoryId': 504895117877248,
              'categoryName': '香肠类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2380',
              'cfsaReceivedFactoryId': 505211268710400,
              'cfsaReceivedFactoryName': '四川古川藏优食谷食品科技有限公司工厂',
              'cfsaRequirementLineNum': 1,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': 1,
              'deliveryNoticeCreatedBy': '028358',
              'deliveryNoticeCreationDate': '2021-02-03 14:57:23',
              'deliveryNoticeDetailId': 541364132827136,
              'deliveryNoticeId': 541364132708352,
              'deliveryNoticeNum': 'DN21020300008',
              'deliveryQuantity': 1000,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '028358',
              'factoryNoticeDetailCreationDate': '2021-02-03 14:58:06',
              'factoryNoticeDetailId': 541364308500480,
              'factoryNoticeId': 541364308373504,
              'factoryNoticeNum': 'EN21020300019',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40303010200056',
              'itemDesc': '麻辣小香肠100G*2',
              'itemId': 534029859659776,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': 1000,
              'orderAmount': null,
              'orderCreatedBy': '028358',
              'orderDetailCreationDate': '2021-02-03 14:55:35',
              'orderDetailId': 541363687526400,
              'orderId': 541363369439232,
              'orderNum': 1000,
              'orderNumber': 'PO21020300031',
              'orderProgressId': 282191551504384,
              'orderRequirementDate': '2021-02-10',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2380',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': '2021-02-03 16:17:30',
              'requirementDate': '2021-02-10',
              'requirementHeadId': 541359455686656,
              'requirementHeadNum': 'PR21020300006',
              'requirementLineId': 541359455809536,
              'requirementQuantity': 1000,
              'requirementSource': 'SRM',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': 'Z005',
              'termOfPaymentName': '30天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 13,
              'userId': null,
              'vendorCode': 'S0007021',
              'vendorId': 533978632302592,
              'vendorName': '四川杨大爷食品科技有限公司',
              'version': null,
              'warehouseReceiptAmount': 13000,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541383820083201,
              'warehouseReceiptNumber': '5000002726',
              'warehouseReceiptQuantity': 1000
            },
            {
              'amountContainingTax': null,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2060',
              'cfsaReceivedFactoryId': 505211268259840,
              'cfsaReceivedFactoryName': '黑龙江省青冈长林肉类食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '',
              'divisionId': null,
              'divisionName': '',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 534010219536384,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '',
              'orderDetailCreationDate': null,
              'orderDetailId': null,
              'orderId': null,
              'orderNum': null,
              'orderNumber': '',
              'orderProgressId': 282191551504385,
              'orderRequirementDate': '2021-02-03',
              'orderType': '',
              'orderTypeName': '',
              'orgCode': '',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': null,
              'requirementDate': '2021-02-03',
              'requirementHeadId': 541367630123008,
              'requirementHeadNum': '1000002233',
              'requirementLineId': 541367630139392,
              'requirementQuantity': 10,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': '',
              'termOfPaymentName': '',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': null,
              'userId': null,
              'vendorCode': '',
              'vendorId': null,
              'vendorName': '',
              'version': null,
              'warehouseReceiptAmount': null,
              'warehouseReceiptCreatedBy': '',
              'warehouseReceiptId': null,
              'warehouseReceiptNumber': '',
              'warehouseReceiptQuantity': null
            },
            {
              'amountContainingTax': 30,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': '2021-02-04 00:00:00',
              'cfsaReceivedFactoryCode': '2070',
              'cfsaReceivedFactoryId': 505211268272128,
              'cfsaReceivedFactoryName': '绵阳长林食品股份有限公司屠宰工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 30,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': 1,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-04',
              'factoryNoticeDetailCreatedBy': '020547',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:32:56',
              'factoryNoticeDetailId': 541372867264512,
              'factoryNoticeId': 541372867080192,
              'factoryNoticeNum': 'EN21020300020',
              'invoiceNoticeCreatedBy': '020547',
              'invoiceNoticeDetailId': 541640676651008,
              'invoiceNoticeId': 541640676528128,
              'invoiceNoticeNumber': 'IN21020400011',
              'invoiceTotalAmount': 30,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 537133354487808,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '020547',
              'orderDetailCreationDate': '2021-02-03 15:31:56',
              'orderDetailId': 541372622630912,
              'orderId': 541372431441920,
              'orderNum': 1,
              'orderNumber': 'PO21020300032',
              'orderProgressId': 282191551504386,
              'orderRequirementDate': '2021-02-04',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2070',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '020547',
              'paymentApplyHeadId': 541706836099072,
              'paymentApplyNumber': 'PA21020400012',
              'receiptCreationDate': '2021-02-03 15:40:47',
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541370060111872,
              'requirementHeadNum': '1000002236',
              'requirementLineId': 541370060124160,
              'requirementQuantity': 1,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '020547',
              'statementDetailId': 541631644389376,
              'statementEndTime': '2021-02-28',
              'statementHeadId': 541631642730496,
              'statementNumber': 'SA21020400001',
              'statementReceiptAmount': 30,
              'statementReceiptQuantity': 1,
              'statementStartTime': '2021-02-04',
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 30,
              'userId': null,
              'vendorCode': 'S0007680',
              'vendorId': 537131887755264,
              'vendorName': '成都市荣申包装设计有限责任公司',
              'version': null,
              'warehouseReceiptAmount': 30,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541374795915265,
              'warehouseReceiptNumber': '5000002719',
              'warehouseReceiptQuantity': 1
            },
            {
              'amountContainingTax': 30,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': '2021-02-04 00:00:00',
              'cfsaReceivedFactoryCode': '2070',
              'cfsaReceivedFactoryId': 505211268272128,
              'cfsaReceivedFactoryName': '绵阳长林食品股份有限公司屠宰工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 30,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': 1,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-04',
              'factoryNoticeDetailCreatedBy': '020547',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:32:56',
              'factoryNoticeDetailId': 541372867264512,
              'factoryNoticeId': 541372867080192,
              'factoryNoticeNum': 'EN21020300020',
              'invoiceNoticeCreatedBy': '020547',
              'invoiceNoticeDetailId': 541640676651008,
              'invoiceNoticeId': 541640676528128,
              'invoiceNoticeNumber': 'IN21020400011',
              'invoiceTotalAmount': 30,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 537133354487808,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '020547',
              'orderDetailCreationDate': '2021-02-03 15:31:56',
              'orderDetailId': 541372622630912,
              'orderId': 541372431441920,
              'orderNum': 1,
              'orderNumber': 'PO21020300032',
              'orderProgressId': 282191551504387,
              'orderRequirementDate': '2021-02-04',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2070',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '020547',
              'paymentApplyHeadId': 541706836099072,
              'paymentApplyNumber': 'PA21020400012',
              'receiptCreationDate': '2021-02-03 15:40:47',
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541370060111872,
              'requirementHeadNum': '1000002236',
              'requirementLineId': 541370060124160,
              'requirementQuantity': 1,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '020547',
              'statementDetailId': 541639801200640,
              'statementEndTime': '2021-02-28',
              'statementHeadId': 541639801032704,
              'statementNumber': 'SA21020400008',
              'statementReceiptAmount': 30,
              'statementReceiptQuantity': 1,
              'statementStartTime': '2021-02-01',
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 30,
              'userId': null,
              'vendorCode': 'S0007680',
              'vendorId': 537131887755264,
              'vendorName': '成都市荣申包装设计有限责任公司',
              'version': null,
              'warehouseReceiptAmount': 30,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541374795915265,
              'warehouseReceiptNumber': '5000002719',
              'warehouseReceiptQuantity': 1
            },
            {
              'amountContainingTax': 30,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': '2021-02-04 00:00:00',
              'cfsaReceivedFactoryCode': '2070',
              'cfsaReceivedFactoryId': 505211268272128,
              'cfsaReceivedFactoryName': '绵阳长林食品股份有限公司屠宰工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': 1,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '020547',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:32:56',
              'factoryNoticeDetailId': 541372867264512,
              'factoryNoticeId': 541372867080192,
              'factoryNoticeNum': 'EN21020300020',
              'invoiceNoticeCreatedBy': '020547',
              'invoiceNoticeDetailId': 541634446991360,
              'invoiceNoticeId': 541634446860288,
              'invoiceNoticeNumber': 'IN21020400005',
              'invoiceTotalAmount': 30,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 537133354487808,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '020547',
              'orderDetailCreationDate': '2021-02-03 15:31:56',
              'orderDetailId': 541372622630912,
              'orderId': 541372431441920,
              'orderNum': 1,
              'orderNumber': 'PO21020300032',
              'orderProgressId': 282191551504388,
              'orderRequirementDate': '2021-02-04',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2070',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': '2021-02-03 15:40:47',
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541370060111872,
              'requirementHeadNum': '1000002236',
              'requirementLineId': 541370060124160,
              'requirementQuantity': 1,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '020547',
              'statementDetailId': 541631644389376,
              'statementEndTime': '2021-02-28',
              'statementHeadId': 541631642730496,
              'statementNumber': 'SA21020400001',
              'statementReceiptAmount': 30,
              'statementReceiptQuantity': 1,
              'statementStartTime': '2021-02-04',
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 30,
              'userId': null,
              'vendorCode': 'S0007680',
              'vendorId': 537131887755264,
              'vendorName': '成都市荣申包装设计有限责任公司',
              'version': null,
              'warehouseReceiptAmount': 30,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541374795915265,
              'warehouseReceiptNumber': '5000002719',
              'warehouseReceiptQuantity': 1
            },
            {
              'amountContainingTax': 30,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': '2021-02-04 00:00:00',
              'cfsaReceivedFactoryCode': '2070',
              'cfsaReceivedFactoryId': 505211268272128,
              'cfsaReceivedFactoryName': '绵阳长林食品股份有限公司屠宰工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': 1,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '020547',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:32:56',
              'factoryNoticeDetailId': 541372867264512,
              'factoryNoticeId': 541372867080192,
              'factoryNoticeNum': 'EN21020300020',
              'invoiceNoticeCreatedBy': '020547',
              'invoiceNoticeDetailId': 541634446991360,
              'invoiceNoticeId': 541634446860288,
              'invoiceNoticeNumber': 'IN21020400005',
              'invoiceTotalAmount': 30,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 537133354487808,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '020547',
              'orderDetailCreationDate': '2021-02-03 15:31:56',
              'orderDetailId': 541372622630912,
              'orderId': 541372431441920,
              'orderNum': 1,
              'orderNumber': 'PO21020300032',
              'orderProgressId': 282191551504389,
              'orderRequirementDate': '2021-02-04',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2070',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': '2021-02-03 15:40:47',
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541370060111872,
              'requirementHeadNum': '1000002236',
              'requirementLineId': 541370060124160,
              'requirementQuantity': 1,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '020547',
              'statementDetailId': 541639801200640,
              'statementEndTime': '2021-02-28',
              'statementHeadId': 541639801032704,
              'statementNumber': 'SA21020400008',
              'statementReceiptAmount': 30,
              'statementReceiptQuantity': 1,
              'statementStartTime': '2021-02-01',
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 30,
              'userId': null,
              'vendorCode': 'S0007680',
              'vendorId': 537131887755264,
              'vendorName': '成都市荣申包装设计有限责任公司',
              'version': null,
              'warehouseReceiptAmount': 30,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541374795915265,
              'warehouseReceiptNumber': '5000002719',
              'warehouseReceiptQuantity': 1
            },
            {
              'amountContainingTax': 30,
              'categoryId': 504895119085568,
              'categoryName': '冻油脂类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2070',
              'cfsaReceivedFactoryId': 505211268272128,
              'cfsaReceivedFactoryName': '绵阳长林食品股份有限公司屠宰工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': 1,
              'deliveryNoticeCreatedBy': 'admin',
              'deliveryNoticeCreationDate': '2021-02-03 15:33:36',
              'deliveryNoticeDetailId': 541373031833600,
              'deliveryNoticeId': 541373031510016,
              'deliveryNoticeNum': 'DN21020300010',
              'deliveryQuantity': null,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '40407040000010',
              'itemDesc': '吊挂板油1*25',
              'itemId': 537133354487808,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': 1,
              'orderAmount': null,
              'orderCreatedBy': '020547',
              'orderDetailCreationDate': '2021-02-03 15:31:56',
              'orderDetailId': 541372622630912,
              'orderId': 541372431441920,
              'orderNum': 1,
              'orderNumber': 'PO21020300032',
              'orderProgressId': 282191551504390,
              'orderRequirementDate': '2021-02-04',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2070',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': null,
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541370060111872,
              'requirementHeadNum': '1000002236',
              'requirementLineId': 541370060124160,
              'requirementQuantity': 1,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': 'Z002',
              'termOfPaymentName': '3天账期',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': 30,
              'userId': null,
              'vendorCode': 'S0007680',
              'vendorId': 537131887755264,
              'vendorName': '成都市荣申包装设计有限责任公司',
              'version': null,
              'warehouseReceiptAmount': null,
              'warehouseReceiptCreatedBy': '',
              'warehouseReceiptId': null,
              'warehouseReceiptNumber': '',
              'warehouseReceiptQuantity': null
            },
            {
              'amountContainingTax': null,
              'categoryId': 504895100735488,
              'categoryName': '猪类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2370',
              'cfsaReceivedFactoryId': 505211268694016,
              'cfsaReceivedFactoryName': '黑龙江道台府食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '',
              'divisionId': null,
              'divisionName': '',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '94506000000062',
              'itemDesc': '猪手(生)',
              'itemId': 534029011587072,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '',
              'orderDetailCreationDate': null,
              'orderDetailId': null,
              'orderId': null,
              'orderNum': null,
              'orderNumber': '',
              'orderProgressId': 282191551504391,
              'orderRequirementDate': '2021-02-04',
              'orderType': '',
              'orderTypeName': '',
              'orgCode': '',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': null,
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541372205514752,
              'requirementHeadNum': '1000002237',
              'requirementLineId': 541372205527040,
              'requirementQuantity': 32,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': '',
              'termOfPaymentName': '',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': null,
              'userId': null,
              'vendorCode': '',
              'vendorId': null,
              'vendorName': '',
              'version': null,
              'warehouseReceiptAmount': null,
              'warehouseReceiptCreatedBy': '',
              'warehouseReceiptId': null,
              'warehouseReceiptNumber': '',
              'warehouseReceiptQuantity': null
            },
            {
              'amountContainingTax': null,
              'categoryId': 504895100735488,
              'categoryName': '猪类',
              'cfsaInvocieDate': null,
              'cfsaReceivedFactoryCode': '2370',
              'cfsaReceivedFactoryId': 505211268694016,
              'cfsaReceivedFactoryName': '黑龙江道台府食品有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': null,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': null,
              'divisionCode': '',
              'divisionId': null,
              'divisionName': '',
              'erpPaymentDate': null,
              'factoryNoticeDetailCreatedBy': '',
              'factoryNoticeDetailCreationDate': null,
              'factoryNoticeDetailId': null,
              'factoryNoticeId': null,
              'factoryNoticeNum': '',
              'invoiceNoticeCreatedBy': '',
              'invoiceNoticeDetailId': null,
              'invoiceNoticeId': null,
              'invoiceNoticeNumber': '',
              'invoiceTotalAmount': null,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '94506000000062',
              'itemDesc': '猪手(生)',
              'itemId': 534029011587072,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '',
              'orderDetailCreationDate': null,
              'orderDetailId': null,
              'orderId': null,
              'orderNum': null,
              'orderNumber': '',
              'orderProgressId': 282191551504392,
              'orderRequirementDate': '2021-02-04',
              'orderType': '',
              'orderTypeName': '',
              'orgCode': '',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '',
              'paymentApplyHeadId': null,
              'paymentApplyNumber': '',
              'receiptCreationDate': null,
              'requirementDate': '2021-02-04',
              'requirementHeadId': 541376324956160,
              'requirementHeadNum': '1000002238',
              'requirementLineId': 541376324968448,
              'requirementQuantity': 35,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '',
              'statementDetailId': null,
              'statementEndTime': null,
              'statementHeadId': null,
              'statementNumber': '',
              'statementReceiptAmount': null,
              'statementReceiptQuantity': null,
              'statementStartTime': null,
              'tenantId': null,
              'termOfPayment': '',
              'termOfPaymentName': '',
              'unitCode': 'KG',
              'unitName': 'KG',
              'unitPriceContainingTax': null,
              'userId': null,
              'vendorCode': '',
              'vendorId': null,
              'vendorName': '',
              'version': null,
              'warehouseReceiptAmount': null,
              'warehouseReceiptCreatedBy': '',
              'warehouseReceiptId': null,
              'warehouseReceiptNumber': '',
              'warehouseReceiptQuantity': null
            },
            {
              'amountContainingTax': 2430000,
              'categoryId': 504895102382080,
              'categoryName': '罐盖',
              'cfsaInvocieDate': '2021-02-03 00:00:00',
              'cfsaReceivedFactoryCode': '2120',
              'cfsaReceivedFactoryId': 505211268370432,
              'cfsaReceivedFactoryName': '莆田市莆罐食品工业有限公司工厂',
              'cfsaRequirementLineNum': 10,
              'cfsaTotalAmountTax': 2455000,
              'createdBy': '系统管理员',
              'createdByIp': '127.0.0.1',
              'createdFullName': '系统管理员',
              'createdId': -1,
              'creationDate': '2021-04-09 16:01:50',
              'deliveryLineNum': null,
              'deliveryNoticeCreatedBy': '',
              'deliveryNoticeCreationDate': null,
              'deliveryNoticeDetailId': null,
              'deliveryNoticeId': null,
              'deliveryNoticeNum': '',
              'deliveryQuantity': 9720000,
              'divisionCode': '00003000',
              'divisionId': 505211259285504,
              'divisionName': '食品事业部',
              'erpPaymentDate': '2021-02-03',
              'factoryNoticeDetailCreatedBy': '021010',
              'factoryNoticeDetailCreationDate': '2021-02-03 15:53:25',
              'factoryNoticeDetailId': 541377900392448,
              'factoryNoticeId': 541377900158976,
              'factoryNoticeNum': 'EN21020300022',
              'invoiceNoticeCreatedBy': '021010',
              'invoiceNoticeDetailId': 541389435068416,
              'invoiceNoticeId': 541389434949632,
              'invoiceNoticeNumber': 'IN21020300019',
              'invoiceTotalAmount': 2430000,
              'isNeedTotal': null,
              'isPayCode': '',
              'isPayName': '',
              'itemCode': '94718000200031',
              'itemDesc': '315#普通盖(喜福临鱼)',
              'itemId': 534042426310656,
              'lastUpdateDate': '2021-04-09 16:01:50',
              'lastUpdatedBy': '',
              'lastUpdatedByIp': '',
              'lastUpdatedId': null,
              'noticeSum': null,
              'orderAmount': null,
              'orderCreatedBy': '021010',
              'orderDetailCreationDate': '2021-02-03 15:52:24',
              'orderDetailId': 541377652744192,
              'orderId': 541377563164672,
              'orderNum': 9720000,
              'orderNumber': 'PO21020300034',
              'orderProgressId': 282191551504393,
              'orderRequirementDate': '2021-02-03',
              'orderType': 'Z001',
              'orderTypeName': '标准订单',
              'orgCode': '2120',
              'pageNum': null,
              'pageSize': null,
              'paymentApplyCreatedBy': '021010',
              'paymentApplyHeadId': 541390266212352,
              'paymentApplyNumber': 'PA21020300029',
              'receiptCreationDate': '2021-02-03 16:06:25',
              'requirementDate': '2021-02-03',
              'requirementHeadId': 541377206308864,
              'requirementHeadNum': '1000002240',
              'requirementLineId': 541377206321152,
              'requirementQuantity': 9720000,
              'requirementSource': 'SAP',
              'returnAmount': null,
              'returnCreatedBy': '',
              'returnDate': null,
              'returnDetailId': null,
              'returnNum': null,
              'returnOrderId': null,
              'returnOrderNumber': '',
              'searchUrl': '',
              'statementCreatedBy': '021010',
              'statementDetailId': 541381959327744,
              'statementEndTime': '2021-02-03',
              'statementHeadId': 541381959114752,
              'statementNumber': 'SA21020300015',
              'statementReceiptAmount': 2430000,
              'statementReceiptQuantity': 9720000,
              'statementStartTime': '2021-02-03',
              'tenantId': null,
              'termOfPayment': 'Z007',
              'termOfPaymentName': '60天账期',
              'unitCode': 'EA',
              'unitName': '个',
              'unitPriceContainingTax': 0.25,
              'userId': null,
              'vendorCode': 'S0007339',
              'vendorId': 534355482472448,
              'vendorName': '福建贤和包装有限公司',
              'version': null,
              'warehouseReceiptAmount': 2430000,
              'warehouseReceiptCreatedBy': '系统管理员',
              'warehouseReceiptId': 541381098504193,
              'warehouseReceiptNumber': '5000002723',
              'warehouseReceiptQuantity': 9720000
            }
          ],
          'navigateFirstPage': 1,
          'navigateLastPage': 1,
          'navigatePages': 1,
          'navigatepageNums': [
            1
          ],
          'nextPage': 1,
          'pages': 1,
          'prePage': 0,
          'size': 15,
          'startRow': 0,
          'total': 15
        },
        'errorMsgTrace': '',
        'message': '操作成功'
      }
      this.pageInfo.total = res.data.total
      this.pageInfo.pageNum = res.data.pageNum
      this.pageInfo.pageSize = res.data.pageSize
      this.materialModle.tableData = res.data.list
    },
    handleSelectionChange (val) {
      this.selections = val
    },

    handleCurrentChange (num) {
      this.pageInfo.pageNum = num
      this.loading = true
      this.fatchListData(this.queryParam)
    },
    handleSizeChange (size) {
      this.pageInfo.pageNum = 1
      this.pageInfo.pageSize = size
      this.loading = true
      this.fatchListData(this.queryParam)
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm{
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table{
    height: 100%;
  }
}
  .the_material_wrapper {

  }
  .download-link-wrap{
    .download-link-item{
      color:#1890ff;
    }
    .close-icon{
      font-weight: bold;
      cursor: pointer;
    }
  }
  .toRequired{
    color: #ff4949;
    padding-right: 2px;
  }
</style>
