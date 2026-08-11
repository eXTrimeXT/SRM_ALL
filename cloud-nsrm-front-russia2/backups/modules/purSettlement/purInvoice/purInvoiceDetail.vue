<template>
  <el-container
    class="the-purInvoice-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="invoiceNoticeForm"
          :model="form"
          :rules="formRules"
          label-position="top"
          class="form-incontainer"
          :disabled="curOpt!=='add' && curOpt!=='edit'"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('purSettlementMod.invoiceNoticeInfo')"
              name="1"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item :label="$t('purSettlementMod.invoiceNoticeNumber')">
                    <el-input
                      v-model="form.invoiceNoticeNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purSettlementMod.paymentPlanStatus')">
                    <el-select
                      v-model="form.invoiceNoticeStatus"
                      disabled
                    >
                      <el-option
                        v-for="item in invoiceStatus"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.fullPathId')"
                    prop="orgId"
                  >
                    <organization-select-tree
                      v-model="form.orgId"
                      :scope="form"
                      @select="addOrgHandle"
                      @change="changeOrgHandle"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.vendorName')"
                    prop="vendorName"
                  >
                    <el-input
                      v-model="form.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.taxRate2')"
                    prop="taxKey"
                  >
                    <dict-select
                      v-model="form.taxKey"
                      code="tax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('quota.currency')"
                    prop="currency"
                  >
                    <dict-select
                      v-model="form.currency"
                      code="currency"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.statementTotalAmount')"
                    prop="statementTotalAmount"
                  >
                    <el-input
                      v-model="form.statementTotalAmount"
                      type="number"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.invoiceTotalAmount')"
                    prop="invoiceTotalAmount"
                  >
                    <el-input
                      v-model="form.invoiceTotalAmount"
                      type="number"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="18">
                  <el-form-item
                    :label="$t('purSettlementMod.sourceNumber')"
                    prop="sourceNumber"
                  >
                    <el-select
                      v-model="sourceNumber"
                      class="the_order_select"
                      clearable
                      filterable
                      reserve-keyword
                      remote
                      multiple
                      popper-class="order-select"
                      :placeholder="$t('purSettlementMod.selNum')"
                      :remote-method="querySearchAsync"
                      :loading="loading"
                      :no-data-text="$t('components.noData')"
                      :loading-text="$t('components.loading')"
                      @change="inputSelect"
                      @focus="orderInputfocus"
                      @remove-tag="removeTagHandel"
                      @clear="clearHandel"
                    >
                      <i
                        slot="prefix"
                        class="el-icon-search"
                      />
                      <!-- <el-option class="option-item" value="" disabled
                        v-if="selectOrderData.length > 0"
                      >
                        <el-row type="flex" class="select-row">
                          <el-col class="border">{{$t("purSettlementMod.statementNumber")}}</el-col>
                          <el-col class="border">{{$t("purSettlementMod.statementNumber")}}</el-col>
                        </el-row>
                      </el-option> -->
                      <el-option
                        v-for="(item, index) in selectOrderData"
                        :key="index"
                        class="option-item"
                        :value="item.statementNumber"
                        :label="item.statementNumber "
                      >
                        <!-- <el-row type="flex" class="select-row">
                          <el-col class="border" :title="item.statementNumber">{{item.statementNumber}}</el-col>
                          <el-col class="border" :title="item.statementHeadId">{{item.statementHeadId}}</el-col>
                        </el-row> -->
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="18">
                  <el-form-item
                    :label="$t('common.remark')"
                    prop="remark"
                  >
                    <el-input
                      v-model="form.remark"
                      type="textarea"
                      :rows="3"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.receiptDetail')"
              name="2"
            >
              <el-table
                :data="invoiceNoticeReceipts"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                />
                <el-table-column
                  align="center"
                  show-overflow-tooltip
                  prop="materialName"
                  :label="$t('common.materialName')"
                />
                <el-table-column
                  :formatter="formatUnit"
                  align="center"
                  prop="unit"
                  :label="$t('bidMod.unit')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptQuantity"
                  :label="$t('bid_mod.quantity')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                />
                <el-table-column
                  align="center"
                  prop="actualInvoiceAmount"
                  :label="$t('purSettlementMod.actualInvoiceAmount')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.actualInvoiceAmount"
                      type="number"
                      @change="actualInvoiceAmountChange"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.returnDetail')"
              name="3"
            >
              <el-table
                :data="invoiceNoticeReturns"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                />
                <el-table-column
                  align="center"
                  show-overflow-tooltip
                  prop="materialName"
                  :label="$t('common.materialName')"
                />
                <el-table-column
                  :formatter="formatUnit"
                  align="center"
                  prop="unit"
                  :label="$t('bidMod.unit')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="returnNum"
                  :label="$t('bid_mod.quantity')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="110"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.actualInvoiceAmount')"
                />
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.invoiceTaxControls')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  @click="addInvoiceTaxControls"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="invoiceTaxControls"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  prop="invoiceNumber"
                  :label="$t('purSettlementMod.invoiceTaxControlsNum')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.invoiceNumber" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="invoiceDate"
                  :label="$t('purSettlementMod.invoiceDate')"
                  width="160"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.invoiceDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="amountNoTax"
                  :label="$t('contractMod.excludeTaxPayAmount')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.amountNoTax"
                      type="number"
                      @change="amountNoTaxChange"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="buyerName"
                  :label="$t('purSettlementMod.buyerName')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.buyerName" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="sellerName"
                  :label="$t('purSettlementMod.sellerName')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.sellerName" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="fileUploadName"
                  :label="$t('quota.fileupload')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileUploadId,
                        fileName: scope.row.fileUploadName
                      }"
                      :readonly="!(curOpt==='add' || curOpt==='edit')"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="100"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="delInvoiceTaxControls(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button @click="backTo()">
            {{ curOpt ==='view' ? $t("common.close") : $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="curRole==='VENDOR' && curOpt!=='view'"
            type="primary"
            :disabled="curOpt==='view'"
            @click="saveData"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curRole==='VENDOR' && curOpt!=='view'"
            type="primary"
            :disabled="curOpt==='view'"
            @click="submitData"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            v-if="curRole==='BUYER' && curOpt==='approve' && (rolePermissions==='AccountSpecialist'|| rolePermissions==='Buyer')"
            type="primary"
            @click="approveHandle"
          >
            {{ $t("purchaseDemand.confirm") }}
          </el-button>
          <el-button
            v-if="curRole==='BUYER' && curOpt==='approve' && (rolePermissions==='AccountSpecialist'|| rolePermissions==='Buyer')"
            type="primary"
            @click="rejectedHandle"
          >
            {{ $t("purchaseDemand.refuse") }}
          </el-button>
        </template>
      </c-toolbar>
      <!-- 驳回弹框 -->
      <!-- 驳回说明 -->
      <srm-dialog
        :title="$t('vendorMod.refuseMemo')"
        :visible.sync="rejectedDialog"
        width="450px"
        size="small"
      >
        <el-form
          ref="rejectedForm"
          :model="rejectedModel.rejectedForm"
          :rules="rejectedModel.rules"
        >
          <el-form-item
            :label="$t('contractMod.rejectReason')"
            prop="rejectReason"
          >
            <el-input
              v-model="rejectedModel.rejectedForm.rejectReason"
              type="textarea"
            />
            <el-button @click="rejectedDialog = false">
              {{ $t('common.cancel') }}
            </el-button>
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="rejectedComfirm"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import Big from 'big.js'

import {
  getDictItemList,
  getAllPurUnit
} from '@/api/common'

export default {
  name: 'PurInvoiceDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    OrganizationSelectTree
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      curOrderId: '',
      form: {
        fullPathId: '',
        orgId: null,
        orgCode: '',
        orgName: '',
        taxRate: '',
        taxKey: '',
        vendorCode: '',
        vendorName: '',
        vendorId: '',
        currency: '',
        statementTotalAmount: null,
        invoiceTotalAmount: null,
        remark: '',
        sourceNumber: '',
        invoiceNoticeStatus: 'DRAFT'
      },
      formRules: {
        fullPathId: [{ required: true, message: this.$t('perfMod.selectOrg') }], // 请选择采购组织
        sourceNumber: [{ required: true, message: this.$t('purSettlementMod.selNum') }]// 请选择单据
      },
      loading: false,
      sourceNumber: [], // 来源单号ID
      tempSourceNumber: [], // 来源单号临时
      orgList: [{
        organizationId: '6803628383141888',
        organizationCode: '10102',
        organizationName: '生活电器事业部'
      }], // 合作组织下拉
      vendorOptions: [], // 供应商下拉
      unitList: [], // 单位
      invoiceStatus: [], // 状态
      invoiceNoticeReceipts: [], // 入库明细
      invoiceNoticeReturns: [], // 退货明细
      invoiceTaxControls: [],
      curOpt: 'add',
      isModify: false,
      activeDims: ['1', '2', '3', '4'],
      selectOrderData: [],
      rejectedDialog: false,
      rejectedModel: { // 驳回信息
        rejectedForm: {
          rejectReason: ''
        },
        rules: {
          rejectReason: [{ required: true, message: this.$t('bidMod.msgRejectReason') }]// 请输入驳回原因
        }
      }
    }
  },
  computed: {
    actualInvoiceAmount () {
      let actAmount = 0
      let returnAmount = 0
      this.invoiceNoticeReceipts.forEach(item => {
        let actualInvoiceAmount = item.actualInvoiceAmount ? item.actualInvoiceAmount : 0
        actAmount += parseFloat(actualInvoiceAmount)
      })

      // console.log('act',actAmount)
      if (this.invoiceNoticeReturns.length > 0) {
        this.invoiceNoticeReturns.forEach(item => {
          let totalAmountNoTax = item.totalAmountNoTax ? item.totalAmountNoTax : 0
          returnAmount += parseFloat(totalAmountNoTax)
        })
      }
      // console.log('ret',returnAmount)
      return (actAmount - returnAmount).toFixed(4)
    },
    amountNoTax () {
      let amount = 0
      this.invoiceTaxControls.forEach(item => {
        let amountNoTax = item.amountNoTax ? item.amountNoTax : 0
        amount += Number(amountNoTax)
      })
      return amount
    }
  },
  created () {
    this.fatchDictData() // 查字典
    this.curOpt = this.$attrs.params.flag
    this.rolePermissions = this.userInfo.rolePermissions[0].roleCode // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    if (this.curOpt == 'add') {
      this.form.vendorCode = this.userInfo.companyCode
      this.form.vendorName = this.userInfo.companyName
      this.form.vendorId = this.userInfo.companyId
    } else { // 编辑 查看
      this.curOrderId = this.$attrs.params.orderId
      this.getOrderFormDetail() // 查询
    }
  },
  methods: {
    formatUnit (row, column, cellValue, index) {
      const dict = this.unitList.find(i => i.value === cellValue)
      return dict ? dict.label : cellValue
    },
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'INVOICE_NOTICE_STATUS' } // 审批状态
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [INVOICE_NOTICE_STATUS] = res.data
        this.invoiceStatus = adaptDictData(
          INVOICE_NOTICE_STATUS.INVOICE_NOTICE_STATUS,
          'dict'
        )
      })
      getAllPurUnit().then(res => {
        this.unitList = adaptDictData(res.data, 'unit')
      })
    },
    // 选择组织
    addOrgHandle (node, instanceId, scope) {
      const { organizationCode, organizationName, organizationId } = node
      this.form.orgCode = organizationCode
      this.form.orgName = organizationName
      this.form.orgId = organizationId
      this.form.fullPathId = instanceId
      this.sourceNumber = []
      this.form.sourceNumber = ''
      // 查询带出税率和币种
      this.$api.sup_ce.orderManagement.getByCompanyIdAndOrgId({
        orgId: organizationId, // 组织ID
        companyId: this.form.vendorId // 公司ID
      }).then(res => {
        const {
          taxKey,
          taxRate,
          clearCurrency
        } = res.data
        this.form = {
          ...this.form,
          taxRate: taxRate,
          taxKey: taxKey,
          currency: clearCurrency
        }
        // const row1 = this.currencyList.find(item => {
        //   return clearCurrency === item.value;
        // });
        // if (row1) {
        //   this.form.currencyName = row1.label;
        // }
      })
      // 查询有效单据
      this.querySearchAsync()
    },
    changeOrgHandle (val) {
      if (val === null) {
        // console.log(val)
        // this.form.vendorCode = ''
        // this.form.vendorId = null
        // this.form.vendorName = ''
        // this.options = []
      }
    },
    // 查询对账单信息
    getgetStatemen (val) {
      let statementHeadId = val
      this.$api.pur.getStatementById({ statementHeadId }).then(res => {
        if (res) {
          let receiptList = this.adaptResData(res.data.receiptList, statementHeadId, 'receipt') // 入库明细
          let returnList = this.adaptResData(res.data.returnList, statementHeadId) // 退货明细
          if (receiptList && receiptList.length > 0) {
            this.invoiceNoticeReceipts = [...this.invoiceNoticeReceipts, ...receiptList] // 入库单
          }
          if (returnList && returnList.length > 0) {
            this.invoiceNoticeReturns = [...this.invoiceNoticeReturns, ...returnList] // 退货单
          }
          this.form.statementTotalAmount = this.actualInvoiceAmount
          // console.log('actualInvoiceAmount',this.actualInvoiceAmount)
        }
      })
    },
    // 数据处理
    adaptResData (data, orderId, type) {
      let arr = []
      data.forEach(item => {
        arr.push({
          actualInvoiceAmount: type === 'receipt' ? item.totalAmountNoTax : null, // 入库的开始默认等于对账单 可修改
          materialCode: item.materialCode,
          materialId: item.materialId,
          materialName: item.materialName,
          sourceNumber: '',
          statementHeadId: orderId, // 对账单ID
          totalAmountNoTax: item.totalAmountNoTax,
          unit: item.unit,
          unitPriceNoTax: item.unitPriceNoTax,
          warehouseReceiptQuantity: item.warehouseReceiptQuantity, // 数量
          returnNum: item.returnNum // 数量
        })
      })
      return arr
    },
     // 单据异步查询 , cb
    querySearchAsync (queryVal) {
      let query = {}
      let results = []
      query.statementNumber = queryVal
      query.vendorId = this.form.vendorId
      query.fullPathId = this.form.fullPathId
      if (this.curOpt == 'add') {
        query.invoiceNoticeId = null
      } else {
        query.invoiceNoticeId = this.curOrderId
      }
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        this.loading = true
        this.$api.pur.listStatementHeadPage(query).then(res => {
          this.loading = false
          results = res.data
          this.selectOrderData = results
          // console.log('selectOrderData')
          // console.log(this.selectOrderData)
        })
      }, 1000 * Math.random())
    },
    // focus
    orderInputfocus () {
      if (this.selectOrderData.length == 0) {
        if (this.form.fullPathId) {
          this.querySearchAsync()
        } else {
          this.$message({
            message: this.$t('perfMod.selectOrg'),
            type: 'warning'
          })
        }
      }
    },
    // 选择单据 执行顺序 1
    inputSelect (item) {
      const last = arr => arr[arr.length - 1]
      this.form.sourceNumber = this.sourceNumber.toString()
      if (item.length > 0) {
        if (item.length > this.tempSourceNumber.length) { // 增量
          let lastStatementNumber = last(item)
          if (lastStatementNumber) {
            const row = this.selectOrderData.find(elm => { return elm.statementNumber === lastStatementNumber })
            if (row) {
              let statementHeadId = row.statementHeadId
              this.getgetStatemen(statementHeadId) // 查询单据详情
            }
          }
        }
      }

      this.tempSourceNumber = this.sourceNumber // 临时存放
    },
    // 删除的时候先执行 change 再执行 remove-tag
    removeTagHandel (val) {
      if (val) {
        let statementNumber = val // 单号
        const row = this.selectOrderData.find(elm => { return elm.statementNumber === statementNumber })
        if (row) {
          let statementHeadId = row.statementHeadId
          let noticeReceiptId = null
          let noticeReturnId = null

          // 入库单
          if (this.invoiceNoticeReceipts.length > 0) {
            for (let j = 0; j < this.invoiceNoticeReceipts.length; j++) {
              if (this.invoiceNoticeReceipts[j].statementHeadId == statementHeadId) {
                if (!noticeReceiptId) {
                  noticeReceiptId = this.invoiceNoticeReceipts[j].noticeReceiptId
                }
                delete this.invoiceNoticeReceipts[j]
              }
            }
            this.invoiceNoticeReceipts = this.invoiceNoticeReceipts.filter(j => !!j)
          }
          // 退货单
          if (this.invoiceNoticeReturns.length > 0) {
            for (let i = 0; i < this.invoiceNoticeReturns.length; i++) {
              if (this.invoiceNoticeReturns[i].statementHeadId == statementHeadId) {
                if (!noticeReturnId) {
                  noticeReturnId = this.invoiceNoticeReturns[i].noticeReturnId
                }
                delete this.invoiceNoticeReturns[i]
              }
            }
            this.invoiceNoticeReturns = this.invoiceNoticeReturns.filter(i => !!i)
          }
          if (this.curOpt === 'edit') {
            // 删除单据 入库单、退货单 相关明细
            if (noticeReceiptId || noticeReturnId) {
              // 单个删除
              let invoiceNoticeQueryDTOs = [
                {
                  invoiceNoticeId: this.curOrderId,
                  statementHeadId: statementHeadId
                }
              ]
              this.$api.pur.batchDeleteByStatementHeadId(invoiceNoticeQueryDTOs).then(res => {})
            }
          }
          this.form.statementTotalAmount = this.actualInvoiceAmount
          this.form.sourceNumber = this.sourceNumber.toString()
        }
      }
    },
    // 清空所有单据
    clearHandel () {
      let invoiceNoticeQueryDTOs = []
      this.invoiceNoticeReceipts.forEach(elm => {
        let noticeReceiptId = elm.noticeReceiptId
        let statementHeadId = elm.statementHeadId
        if (noticeReceiptId) {
          invoiceNoticeQueryDTOs.push({
            invoiceNoticeId: this.curOrderId, // 单据头ID
            statementHeadId: statementHeadId // 对账单ID
          })
        }
      })
      this.$api.pur.batchDeleteByStatementHeadId(invoiceNoticeQueryDTOs).then(res => {})
      this.form.statementTotalAmount = null
      this.invoiceNoticeReceipts = []
      this.invoiceNoticeReturns = []
      this.tempSourceNumber = []
      this.form.sourceNumber = ''
    },
    // 实际数量变化
    actualInvoiceAmountChange (val) {
      this.form.statementTotalAmount = this.actualInvoiceAmount
    },
    amountNoTaxChange (val) {
      this.form.invoiceTotalAmount = this.amountNoTax
    },
    // 查询单据详情
    getOrderFormDetail () {
      let invoiceNoticeId = this.curOrderId
      this.$api.pur.getInvoiceNoticeSaveDTO({ invoiceNoticeId }).then(res => {
        this.form = res.data.invoiceNotice // 头信息
        this.invoiceNoticeReceipts = res.data.invoiceNoticeReceipts// 入库明细
        this.invoiceNoticeReturns = res.data.invoiceNoticeReturns// 退货明细
        this.invoiceTaxControls = res.data.invoiceTaxControls // 税控发票信息
        this.sourceNumber = res.data.invoiceNotice.sourceNumber ? (res.data.invoiceNotice.sourceNumber).split(',') : []
        this.tempSourceNumber = res.data.invoiceNotice.sourceNumber ? (res.data.invoiceNotice.sourceNumber).split(',') : []
        if (this.curOpt === 'edit') {
          this.querySearchAsync() // 查询有效单号
        }
      })
    },
    // 新增税控明细
    addInvoiceTaxControls () {
      this.invoiceTaxControls.push({
        invoiceNumber: '',
        invoiceDate: '',
        amountNoTax: null,
        buyerName: '',
        sellerName: '',
        fileUploadId: null,
        fileUploadName: ''
      })
    },
    // 删除税控
    delInvoiceTaxControls (index, row) {
      this.invoiceTaxControls.splice(index, 1)
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileUploadId = fileId.toString()
      row.fileUploadName = fileName
    },
    confirmBill () {},
    cancelBill () {},
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 暂存
    saveData () {
      this.dataHandle('save')
    },
    dataHandle (type) {
      let submitData = {}
      submitData = {
        invoiceNotice: this.form,
        invoiceNoticeReceipts: this.invoiceNoticeReceipts, // 入库明细
        invoiceNoticeReturns: this.invoiceNoticeReturns, // 退货明细
        invoiceTaxControls: this.invoiceTaxControls // 税控发票信息
      }
      if (type === 'save') { // 暂存
        this.$api.pur.saveTemporary(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.curOrderId = res.data // 返回单据号
            this.curOpt = 'edit'
            this.getOrderFormDetail() // 查询单据数据
          }
        })
      } else { // 提交
        this.$api.pur.invoiceNoticeSubmit(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('purInvoiceList.getQuerydata')
          }
        })
      }
    },
    // 提交
    submitData () {
      let _this = this
      _this.$refs.invoiceNoticeForm.validate(valid => {
        if (!valid) {
          _this.$message({
            message: _this.$t('common.pleasefinishRequired'),
            type: 'warning'
          })
          return false
        } else {
          if (_this.invoiceTaxControls.length < 1) {
            _this.$message({
              message: _$t('purSettlementMod.editInvoiceTaxControlsInfo'),
              type: 'warning'
            })
            return false
          } else {
            _this.dataHandle('submit')
          }
        }
      })
    },
    // 驳回操作
    rejectedHandle () {
      this.rejectedDialog = true
    },
    // 确认驳回
    rejectedComfirm () {
      this.$refs.rejectedForm.validate(valid => {
        let _this = this
        if (valid) {
          let parame = {}
          parame.invoiceNoticeId = this.curOrderId
          parame.rejectReason = _this.rejectedModel.rejectedForm.rejectReason
          if (this.rolePermissions === 'AccountSpecialist') { // 财务专员
            this.$api.pur.financeReject(parame).then(res => {
              _this.$message({
                message: res.message,
                type: 'success'
              })
              _this.rejectedDialog = false
              _this.backTo()
            })
          } else if (this.rolePermissions === 'Buyer') { // 财务采购员
            this.$api.pur.buyerReject(parame).then(res => {
              _this.$message({
                message: res.message,
                type: 'success'
              })
              _this.rejectedDialog = false
              _this.backTo()
            })
          }
        }
      })
    },
    // 财务审批通过
    approveHandle () {
      let invoiceNoticeId = this.curOrderId
      if (this.rolePermissions === 'AccountSpecialist') { // 财务终审
        this.$api.pur.financeApprove({ invoiceNoticeId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.backTo()
        })
      } else if (this.rolePermissions === 'Buyer') { // 采购初审批
        this.$api.pur.buyerFirstReview({ invoiceNoticeId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.backTo()
        })
      }
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purInvoiceList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-purInvoice-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
<style>
.order-select .el-select-dropdown__list {
  padding: 0;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item {
  border-bottom: 1px solid #ddd;
}
.order-select .el-select-dropdown__item.option-item.is-disabled{
  background: #f5f5f5;

}
.order-select .el-select-dropdown__item.option-item.is-disabled .border {
  /* color: #1890ff !important; */
  font-weight: bold;
  color: #666 !important;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item .name {
  text-overflow: ellipsis;
  overflow: hidden;
  line-height: 26px !important;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item .fullName {
  font-size: 12px;
}
.order-select.el-select-dropdown.is-multiple .el-select-dropdown__item.selected::after{
  top: 0 !important;
  right: 15px !important;
}
.btn_line {
  margin-bottom: 10px;
}
</style>
