<template>
  <el-container
    class="flex-container the-statementBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 基本资料 -->
            <el-collapse-item
              :title="$t('purSettlementMod.basicInfo')"
              name="1"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 供应商名称 -->
                  <el-form-item
                    :label="$t('common.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 供应商编码 -->
                  <el-form-item
                    :label="$t('common.vendorCode')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.vendorCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 采购组织 -->
                  <el-form-item
                    :label="$t('common.orgName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.site"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 结算币种 -->
                  <el-form-item
                    :label="$t('bidMod.currency')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.tel" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 税率 -->
                  <el-form-item
                    :label="$t('contractMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="form.purchaseNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账状态 -->
                  <el-form-item
                    :label="$t('purSettlementMod.statementStatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.deliveryNum" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账开始日期 -->
                  <el-form-item
                    :label="$t('purSettlementMod.statementStartTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.startDate"
                      type="date"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账结束日期 -->
                  <el-form-item
                    :label="$t('purSettlementMod.statementEndTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.endDate"
                      type="date"
                      disabled
                    />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <!-- 付款类型 -->
                  <el-form-item
                    :label="$t('purSettlementMod.payType')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="form.payType"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 付款条件 -->
                  <el-form-item
                    :label="$t('paymentType.paymentTerm')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.payment1"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账含税金 -->
                  <el-form-item
                    :label="$t('accountMod.reconciliationTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.returnQuantity"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账未税金 -->
                  <el-form-item
                    :label="$t('accountMod.reconciledUntaxed')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.auditStatus"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账税额 -->
                  <el-form-item
                    :label="$t('accountMod.reconciliationTax1')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 扣罚金 -->
                  <el-form-item
                    :label="$t('accountMod.deductFine')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 预付总金额 -->
                  <el-form-item
                    :label="$t('accountMod.totalAmountPrepaid')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 对账单号 -->
                  <el-form-item
                    :label="$t('purSettlementMod.statementNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 相关附件 -->
                  <el-form-item
                    :label="$t('accountMod.relevantAttachment')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.contractComments" />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col :span="12">
                  <!-- 需方备注 -->
                  <el-form-item
                    :label="$t('accountMod.buyerComments')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.buyerComments"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 供方备注 -->
                  <el-form-item
                    :label="$t('purSettlementMod.supplierNote')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.vendorComments"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 入库/退货明细 -->
            <el-collapse-item
              :title="$t('accountMod.warehousingAndReturnGoods')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"

                  class="detail-pbtn"
                  @click="addOneContent"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="receptionList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <!-- 序号 -->
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <!-- 入库/退货单号 -->
                <el-table-column
                  align="center"
                  prop="prLine"
                  :label="$t('accountMod.inboundReturnOrderNo')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 项次 -->
                <el-table-column
                  align="center"
                  prop="prNo"
                  :label="$t('accountMod.item')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <!-- 发生日期 -->
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('purSettlementMod.happenDate')"
                  width="160"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.creationDate"
                      type="date"
                    />
                  </template>
                </el-table-column>
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('common.materialName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购订单号 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购订单行号 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 数量 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('bid_mod.quantity')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <!-- 总金额 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('purchaseDemand.totalAmount')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.inspectionQuantity"
                      type="number"
                    />
                  </template>
                </el-table-column>
                <!-- 退货税前金额 -->
                <el-table-column
                  align="center"
                  prop="notaxAmount"
                  :label="$t('accountMod.refundBeforeTax')"
                  width="120"
                />
                <!-- 退货税后金额 -->
                <el-table-column
                  align="center"
                  prop="taxAmount"
                  :label="$t('accountMod.refundAfterTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 单位 -->
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('bid_mod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="receptionComments"
                  :label="$t('common.remark')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 扣罚款明细 -->
            <el-collapse-item
              :title="$t('accountMod.deductionOfFines')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"

                  class="detail-pbtn"
                  @click="addOneContent2"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="receptionList2"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <!-- 扣罚款单号 -->
                <el-table-column
                  align="center"
                  prop="prLine"
                  :label="$t('accountMod.deductionOrderNo')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 扣罚款日期 -->
                <el-table-column
                  align="center"
                  prop="deductionDate"
                  :label="$t('accountMod.deductionDate')"
                  width="160"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.deductionDate"
                      type="date"
                    />
                  </template>
                </el-table-column>
                <!-- 扣罚类型 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('accountMod.deductionType')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 扣罚未税金额 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('accountMod.deductionUntaxed')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 扣罚含税金额 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('accountMod.deductionIncludeTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 扣罚税额 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('accountMod.fineDeductionTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="receptionComments"
                  :label="$t('bid_mod.remark')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent2(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 预付款明细 -->
            <el-collapse-item
              :title="$t('accountMod.advancePaymentDetail')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"

                  class="detail-pbtn"
                  @click="addOneContent3"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="receptionList3"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <!-- 预付发票编号 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('accountMod.prepaidInvoiceNo')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 发票日期 -->
                <el-table-column
                  align="center"
                  prop="invoiceDate"
                  :label="$t('contractMod.invocieDate')"
                  width="160"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.invoiceDate"
                      type="date"
                    />
                  </template>
                </el-table-column>
                <!-- 发票币种 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('accountMod.invoiceCurrency')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购订单号 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 未核销金额 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('accountMod.unwirttenAmount')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 本次核销金额 -->
                <el-table-column
                  align="center"
                  prop="notaxAmount"
                  :label="$t('purSettlementMod.chargeOffAmount')"
                  width="120"
                />
                <!-- GL日期 -->
                <el-table-column
                  align="center"
                  prop="taxAmount"
                  :label="$t('accountMod.GLDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 摘要 -->
                <el-table-column
                  align="center"
                  prop="receptionComments"
                  :label="$t('purSettlementMod.abstract')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 操作 -->
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent3(scope.$index, scope.row)"
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
          <el-button
            type="primary"

            @click="approvalBill"
          >
            {{
              $t("common.cancelled")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="approvalBill"
          >
            {{
              $t("common.pdfPrint")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="saveBill"
          >
            {{
              $t("common.staging")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="submitBill"
          >
            {{
              $t("common.submit")
            }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import {
  getDictItem,
  getRegion
} from '@/api/common'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'StatementBillDetail',
  components: {
    MainHeader,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        projId: '',
        projCode: '',
        templateName: '',
        templateType: '',
        stopTime: '',
        publishRange: '',
        status: 'DRAFT',
        resume: '',
        currency: '',
        taxRate: '',
        showPriceEnabled: 'N',
        remark: '',
        fileuploadId: '',
        fileName: ''
      },
      receptionList: [],
      receptionList2: [],
      receptionList3: [],
      activeDims: ['1', '2', '3', '4'],
      rules: {
        templateName: [
          { required: true, message: this.$t('bidMod.bidMsgList[1]') }
        ], // 请输入项目名称
        templateType: [
          { required: true, message: this.$t('bidMod.bidMsgList[39]') }
        ], // 请选择项目类型
        stopTime: [
          { required: true, message: this.$t('bidMod.bidMsgList[40]') }
        ], // 请选择截止时间
        publishRange: [
          { required: true, message: this.$t('bidMod.bidMsgList[41]') }
        ] // 请选择发布范围
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      isModify: false,
      statusList: [],
      pubRangeList: [],
      projectTypeList: []
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
    // get---
    // 状态
    getDictItem('PROJ_STATUS').then(res => {
      this.statusList = adaptDictData(res.data, 'dict')
    })
    // 项目类型
    getDictItem('PROJECT_TYPE').then(res => {
      this.projectTypeList = adaptDictData(res.data, 'dict')
    })
    // 发布范围
    getDictItem('PUBLISH_RANGE').then(res => {
      this.pubRangeList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    getFormDetail () {
      this.$http({
        url:
          '/api-bid/techDiscuss/techDiscussReply/techDiscussReplyInfo',
        method: 'POST',
        data: this.$attrs.params.row,
        loading: true
      })
        .then(data => {
          this.form = data.data
          // debugger
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.receptionList.splice(index, 1)
    },
    deleteOneContent2 (index, row) {
      this.receptionList2.splice(index, 1)
    },
    deleteOneContent3 (index, row) {
      this.receptionList3.splice(index, 1)
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'statementBillDetail' + this.$attrs.params.row.templateName
        )
      } else {
        this.$emit('tab-remove', 'statementBillDetail')
      }
      this.__setTabTodo('technicalCommunicationList.getQuerydata')
    },
    addOneContent () {
      this.receptionList.push({})
    },
    addOneContent2 () {
      this.receptionList2.push({})
    },
    addOneContent3 () {
      this.receptionList3.push({})
    },
    readOneContent () {},
    copyOneContent () {},
    submitBill () {},
    approvalBill () {},
    saveBill () {
      this.$emit('tab-remove', 'statementBillDetail')
      // return
      // this.$http({
      //   url:
      //     '/api-bid/techDiscuss/techDiscussReply/saveTechDiscussReplyInfo',
      //   method: 'POST',
      //   data: this.form,
      //   loading: true
      // })
      //   .then(data => {
      //     this.$message({
      //       message: this.$t('common.successSave'),
      //       type: 'success'
      //     })
      //     if (this.$attrs.params.flag == 'edit') {
      //       this.$emit(
      //         'tab-remove',
      //         'statementBillDetail' + this.$attrs.params.row.templateName
      //       )
      //     } else {
      //       this.$emit('tab-remove', 'statementBillDetail')
      //     }
      //     this.__setTabTodo('technicalCommunicationList.getQuerydata')
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-statementBillDetail-detail {
  .form-container2 {
    padding: 5px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line {
    margin: 0 0 10px 0;
  }
}
</style>
