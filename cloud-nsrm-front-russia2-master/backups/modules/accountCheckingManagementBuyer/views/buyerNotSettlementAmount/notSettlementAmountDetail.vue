<template>
  <el-container class="the-notSettlementAmountDetail-detail" direction="vertical">
    <el-main>
      <main-header>
        <!-- 对账单详情 -->
        <template slot="left">
          <h2>
            {{ $t("accountMod.statementDetail") + unsettledOrderNumber }}
          </h2>
        </template>
        <template slot="right">
          <el-button type="primary" @click="print">
            {{
              $t("common.pdfPrint")
            }}
          </el-button>
          <el-button v-if="showComfirmAndRefuse" type="primary" @click="comfirm">
            {{ $t("purchaseDemand.confirm") }}
          </el-button>
          <el-button v-if="showComfirmAndRefuse" type="primary" @click="refuse">
            {{ $t("common.toRefuse") }}
          </el-button>
          <el-button v-if="showFinish" type="primary" @click="finish">
            {{ $t("common.finish") }}
          </el-button>
          <el-button type="primary" @click="backTo">
            {{
              $t("common.backTo")
            }}
          </el-button>
        </template>
      </main-header>
      <div class="form-container">
        <!-- 新建对账单 -->
        <el-steps :active="activeStep" :process-status="isCancal">
          {{ $t("accountMod.newStatement") }}
          <!-- 新建对账单 -->
          <el-step :title="$t('accountMod.newStatement')" />
          <!-- 供方提交 -->
          <el-step :title="$t('accountMod.supplierSubmit')" />
          <!-- 采购方审核 -->
          <el-step :title="$t('accountMod.purchaseReview')" />
          <!-- 供方上传对账单确认函 -->
          <el-step :title="$t('accountMod.supplierUploadLetter')" />
          <!-- 采购方确认完成 -->
          <el-step :title="$t('accountMod.buyerConfirmFinish')" />
        </el-steps>
      </div>
      <div class="form-container">
        <p class="sub_header">
          <span>{{ $t("accountMod.searchCodition") }}</span>
        </p>
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-row type="flex">
            <el-col>
              <!-- 采购组织 -->
              <el-form-item
                prop="organizationId"
                :label="$t('common.orgName')"
                :label-width="formLabelWidth"
              >
                <organization-select-tree
                  v-model="form.organizationId"
                  disabled
                  @select="treeselectChange"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 供货方 -->
              <el-form-item :label="$t('accountMod.vendor')" :label-width="formLabelWidth">
                <el-input v-model="form.vendorName" disabled />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 币种 -->
              <el-form-item
                prop="rfqSettlementCurrency"
                :label="$t('bid_mod.currencyName')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="form.rfqSettlementCurrency"
                  code="currency"
                  :disabled="!showSaveAndSubmit"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 起始时间 -->
              <el-form-item
                prop="startDate"
                :label="$t('componentDoc.stratTime')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.startDate"
                  :disabled="!showSaveAndSubmit"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 截止时间 -->
              <el-form-item
                prop="endDate"
                :label="$t('bidMod.stopTime')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.endDate"
                  :disabled="!showSaveAndSubmit"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="form-container">
        <p class="sub_header">
          <span>{{ $t("accountMod.statementDetail1") }}</span>
          <span style="padding: 0 11px;font-weight: bolder;">{{
            $t("accountMod.excludeTaxSummary") + totalNum
          }}</span>
        </p>
        <el-tabs v-model="tabActive" type="border-card">
          <!-- 对账单明细 -->
          <el-tab-pane :label="$t('accountMod.statementDetail1')" name="billListTab">
            <el-table
              ref="table"
              :data="selectedTableData"
              style="width: 100%"
              border
              height="222px"
            >
              <el-table-column align="center" type="index" width="50" />
              <!-- 单据类型 -->
              <el-table-column
                align="center"
                prop="billType"
                :label="$t('bidMod.billType')"
                :formatter="billTypeFormatter"
                width="100"
              />
              <!-- 单据编码 -->
              <el-table-column
                align="center"
                prop="billNumber"
                :label="$t('bidMod.billCode')"
                width="120"
              />
              <!-- 业务日期 -->
              <el-table-column
                align="center"
                prop="businessDate"
                :label="$t('accountMod.businessDate')"
                width="100"
                :formatter="dateFormatter"
              />
              <!-- 订单编号 -->
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('logisticsMod.orderNum')"
                width="120"
              />
              <!-- 未开票 -->
              <el-table-column
                align="center"
                prop="notInvoiced"
                :label="$t('accountMod.notInvoiced')"
                width="100"
                :formatter="notInvoicedFormatter"
              />
              <!-- 未对账 -->
              <el-table-column
                align="center"
                prop="unreconciled"
                :label="$t('accountMod.unreconciled')"
                width="100"
                :formatter="unreconciledFormatter"
              />
              <!-- 物料编码 -->
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="100"
              />
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="materialName"
                :label="$t('common.materialName')"
                width="120"
              />
              <!-- 单位 -->
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bid_mod.unit')"
                :formatter="unitFormatter"
                width="100"
              />
              <!-- 单价 -->
              <el-table-column
                align="right"
                prop="unitPriceExcludingTax"
                :label="$t('accountMod.unitPrice')"
                width="100"
              />
              <!-- 数量 -->
              <el-table-column
                align="right"
                prop="unsettledNum"
                :label="$t('bid_mod.quantity')"
                width="100"
              />
              <!-- 金额 -->
              <el-table-column
                align="right"
                prop="price"
                :label="$t('orderMod.buyerOrderSynergy.amount')"
                width="100"
              />
            </el-table>
          </el-tab-pane>
          <!-- 物料汇总 -->
          <el-tab-pane :label="$t('accountMod.materialSummary')" name="itemListTab">
            <el-table :data="tableData2" style="width: 100%" border height="222px">
              <el-table-column align="center" type="index" width="50" />
              <!-- 物料编码 -->
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="100"
              />
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="materialName"
                :label="$t('common.materialName')"
              />
              <!-- 单位 -->
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bid_mod.unit')"
                width="100"
                :formatter="unitFormatter"
              />
              <!-- 数量 -->
              <el-table-column
                align="right"
                prop="unsettledNum"
                :label="$t('bid_mod.quantity')"
                width="100"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="form-container">
        <p class="sub_header">
          <!-- 罚扣款明细 -->
          <span>{{ $t("accountMod.fineDeductionDetail") }}</span>
          <span style="padding: 0 11px;font-weight: bolder;">{{
            $t("accountMod.fineDeductionSummary") + totalNum2
          }}</span>
        </p>
        <el-table :data="tableData3" style="width: 100%" border height="222px">
          <el-table-column align="center" type="index" width="50" />
          <!-- 罚扣款编码 -->
          <el-table-column
            align="center"
            prop="penaltyNumber"
            :label="$t('accountMod.penaltyNumber')"
            width="120"
          />
          <!-- 罚扣款类型 -->
          <el-table-column
            align="center"
            prop="penaltyType"
            :label="$t('accountMod.penaltyType')"
            :formatter="penaltyTypeFormatter"
          />
          <!-- 罚扣描述 -->
          <el-table-column
            align="center"
            prop="penaltyCommons"
            :label="$t('accountMod.penaltyCommons')"
          />
          <!-- 采购组织 -->
          <el-table-column
            align="center"
            prop="organizationName"
            :label="$t('common.orgName')"
            width="120"
          />
          <!-- 罚扣款金额 -->
          <el-table-column
            align="right"
            prop="penaltyAmount"
            :label="$t('accountMod.penaltyAmount1')"
            width="100"
          />
          <!-- 扣款日期 -->
          <el-table-column
            align="right"
            prop="penaltyTime"
            :label="$t('accountMod.penaltyTime')"
            :formatter="dateFormatter"
            width="100"
          />
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'NotSettlementAmountDetail',
  components: {
    MainHeader,
    OrganizationSelectTree,
    CPagination
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      unsettledOrderNumber: '',
      dialogVisible: false,
      fileRelationId: null,
      fileName: null,
      form: {},
      total: 0,
      currentPage: 1,
      pageSize: 15,
      isCancal: 'process',
      tabActive: 'billListTab',
      form2: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      },
      rules: {
        endDate: [
          { required: true, message: this.$t('bidMod.bidMsgList[40]') }
        ], // 请选择截止时间
        startDate: [
          { required: true, message: this.$t('accountMod.msgList[6]') }
        ], // 请选择开始时间
        rfqSettlementCurrency: [
          { required: true, message: this.$t('vendorMod.msgCurrencyCode') }
        ], // 请选择币种
        fullPathId: [
          { required: true, message: this.$t('bidMod.bidMsgList[23]') }
        ] // 请选择采购组织
      },
      isDisabled: this.$attrs.params.flag === 'edit',
      formLabelWidth: '120px',
      totalNum: 0,
      totalNum2: 0,
      tableData: [],
      selectedTableData: [],
      tableData2: [],
      tableData3: [],
      unsettledDetails: [],
      isModify: false,
      penaltyType: [],
      showSaveAndSubmit: false,
      showUpload: false,
      showObsolete: false,
      showQuery: false,
      showRecall: false,
      detailListVisible: false,
      showComfirmAndRefuse: false,
      showFinish: false
    }
  },
  created () {
    this.activeStep = this.getActiveStep()
    if (this.$attrs.params.flag === 'edit') {
      this.isModify = true
      this.form = this.$attrs.params.row
      this.unsettledOrderNumber = this.$attrs.params.row.unsettledOrderNumber
      this.reQuery()
    } else {
      this.isModify = false
      const { companyCode, companyName, companyId } =
        this.$store.getters.userInfo || {}
      this.form = {
        venderId: companyId,
        vendorName: companyName,
        vendorCode: companyCode
      }
    }
  },
  methods: {
    unitFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('unit', cellValue) : cellValue
    },
    dateFormatter (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    notInvoicedFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('NOT_INVOICED', cellValue) : cellValue
    },
    unreconciledFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('UNRECONCILED', cellValue) : cellValue
    },
    billTypeFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('UNSETTLED_BILL_TYPE', cellValue) : cellValue
    },
    penaltyTypeFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('PENALTY_TYPE', cellValue) : cellValue
    },
    getActiveStep (params) {
      let isCancal = 'process'
      let step = 1
      const { flag, row = {} } = this.$attrs.params
      const { status } = params || row

      // 已提交状态显示【审核通过】 和 【驳回】 按钮
      this.showComfirmAndRefuse = status === 'SUBMIT'
      // SURE状态 显示 完成按钮
      this.showFinish = status === 'SURE'

      if (flag === 'add') {
        step = 1
      } else {
        if (['REFUSE', 'CREATE'].includes(status)) {
          step = 2
        } else if (status === 'CANCAL') {
          // 作废
          isCancal = 'error'
          step = 2
        } else if (status === 'SUBMIT') {
          step = 3
        } else if (['COMFIRM', 'SURE'].includes(status)) {
          step = 4
        } else if (status === 'FINISH') {
          step = 5
        }
      }
      this.isCancal = isCancal
      return step - 1
    },
    queryUnsettledOrder () {
      const data = { unsettledOrderId: this.form.unsettledOrderId }
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/listPage',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.form = res.data.list[0]
        this.activeStep = this.getActiveStep(this.form)
      })
    },
    reQuery () {
      // 重新查询表单信息
      const data = {
        unsettledOrderId: this.$attrs.params.row.unsettledOrderId
      }
      // 查询物料数量统计
      this.$http({
        url:
          '/api-sup-ce/reconciliation/unsettledDetail/materialCountList',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.tableData2 = res.data
      })
      // 分页未结算数量账单明细
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledDetail/listPage',
        method: 'POST',
        data: { ...data, pageSize: 99999, pageNum: 1 },
        loading: true
      }).then(res => {
        this.selectedTableData = res.data.list
        let totalNum = 0
        res.data.list.forEach(i => {
          totalNum += i.price
        })
        this.totalNum = totalNum
      })
      // 查询所有未结算数量账单罚扣款
      this.$http({
        url:
          '/api-sup-ce/reconciliation/unsettledPenalty/findUnsettledList',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.tableData3 = res.data
        let totalNum = 0
        res.data.forEach(i => {
          totalNum += i.penaltyAmount
        })
        this.totalNum2 = totalNum
      })
    },
    comfirm () {
      // 审核通过之后重新查询表单信息
      const data = [this.form.unsettledOrderId]
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/comfirmBatch',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.queryUnsettledOrder()
      })
    },
    finish () {
      // 完成之后重新查询表单信息
      const data = [this.form.unsettledOrderId]
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/finishBatch',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.queryUnsettledOrder()
      })
    },
    refuse () {
      // 驳回之后重新查询表单信息
      const data = [this.form.unsettledOrderId]
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/refuseBatch',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.queryUnsettledOrder()
      })
    },
    openUploadModal () {
      this.dialogVisible = true
    },
    uplaod () {
      if (!this.fileRelationId || !this.fileName) {
        this.$message({
          type: 'error',
          message: this.$t('accountMod.msgList[7]') // 请上传对账单确认函
        })
        return
      }
      const data = {
        unsettledOrderId: this.form.unsettledOrderId,
        fileRelationId: this.fileRelationId,
        fileName: this.fileName
      }
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/sure',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.queryUnsettledOrder()
      })
    },
    treeselectChange (node, instanceId, scope) {
      const { organizationCode, organizationName, organizationId } = node
      this.queryParams = { organizationId }
      this.form.organizationCode = organizationCode
      this.form.organizationName = organizationName
      this.form.organizationId = organizationId
    },
    saveOne () {
      const data = {
        unsettledOrder: { ...this.form },
        unsettledDetails: this.selectedTableData.map(i => i.unsettledDetailId),
        unsettledPenaltys: this.tableData3.map(i => i.penaltyId)
      }
      if (!data.unsettledDetails.length) {
        this.$message({
          type: 'error',
          message: this.$t('accountMod.msgList[8]') // 请至少选择一个对账单明细
        })
        return
      }
      if (this.isModify) {
        this.$http({
          url: '/api-sup-ce/reconciliation/unsettledOrder/update',
          method: 'POST',
          data,
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.$http({
          url: '/api-sup-ce/reconciliation/unsettledOrder/save',
          method: 'POST',
          data,
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      }
    },
    submitOne () {
      const data = [this.form.unsettledOrderId]
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/submitBatch',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.queryUnsettledOrder()
      })
    },
    print () { },
    backTo () {
      if (this.$attrs.params.flag === 'edit') {
        this.$emit(
          'tab-remove',
          'notSettlementAmountDetail' +
          this.$attrs.params.row.unsettledOrderNumber
        )
      } else {
        this.$emit('tab-remove', 'notSettlementAmountDetail')
      }
      // this.__setTabTodo('inquiryOrdersList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-notSettlementAmountDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }

  .upload {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .download-template {
    color: #2079ff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>
