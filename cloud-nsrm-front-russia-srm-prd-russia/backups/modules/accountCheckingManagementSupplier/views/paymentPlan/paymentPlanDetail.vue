<template>
  <el-container
    class="flex-container the-paymentPlanDetail-detail"
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
            <!-- 付款计划维护 -->
            <el-collapse-item
              :title="$t('purSettlementMod.paymentPlanInfo')"
              name="1"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 付款计划单号 -->
                  <el-form-item
                    :label="$t('purSettlementMod.paymentPlanNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.linkMan"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 单据日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.orderDate')"
                    :label-width="formLabelWidth"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="form.stopTime"
                      type="date"
                      :placeholder="$t('bidMod.datePicker')"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 付款类型 -->
                  <el-form-item
                    :label="$t('purSettlementMod.payType')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.paymentType" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 采购组织 -->
                  <el-form-item
                    :label="$t('common.orgName')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.purorg" />
                  </el-form-item>
                </el-col>

                <!-- 计划付款日期 -->
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.planPaymentDate')"
                    :label-width="formLabelWidth"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="form.stopTime"
                      type="date"
                      :placeholder="$t('bidMod.datePicker')"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 币种 -->
                  <el-form-item
                    :label="$t('contractMod.currencyCode')"
                    :label-width="formLabelWidth"
                  >
                    <DictSelect
                      v-model="form.currency"
                      code="currency"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 税率 -->
                  <el-form-item
                    :label="$t('contractMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <DictSelect
                      v-model="form.taxRate"
                      code="currency"
                    />
                  </el-form-item>
                </el-col>

                <!-- 未税总金额 -->
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.totalAmountNoTax2')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.teamleader" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 含税总金额 -->
                  <el-form-item
                    :label="$t('contractMod.totalAmountTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.teamleader" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 总税额 -->
                  <el-form-item
                    :label="$t('accountMod.totalTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.teamleader" />
                  </el-form-item>
                </el-col>
                <el-col :span="12" />
                <el-col>
                  <!-- 备注 -->
                  <el-form-item
                    :label="$t('contractMod.remark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.contractComments"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 付款计划明细 -->
            <el-collapse-item
              :title="$t('purSettlementMod.paymentPlanDetail')"
              name="2"
            >
              <p style="margin: 0 0 8px 0">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addOneContent"
                >
                  {{ $t("common.add") }}
                </el-button>
                <!-- <el-button type="primary"  @click="deleteOneContent">{{$t('common.delete')}}</el-button> -->
                <!-- <el-button type="primary"  @click="readOneContent">内容预览</el-button> -->
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="copyOneContent"
                >
                  {{ $t("accountMod.viewDetail") }}
                </el-button>
              </p>
              <el-table
                :data="receptionList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <!-- 项次 -->
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('accountMod.item')"
                  width="60"
                />
                <!-- 来源类型 -->
                <el-table-column
                  align="center"
                  prop="sourceType"
                  :label="$t('contractMod.sourceType')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.sourceType" />
                  </template>
                </el-table-column>
                <!-- 来源单号  -->
                <el-table-column
                  align="center"
                  prop="sourceNum"
                  :label="$t('contractMod.sourceNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 合同付款期数 -->
                <el-table-column
                  align="center"
                  prop="paymentQuote"
                  :label="$t('accountMod.contractPaymentPeriod')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentQuote" />
                  </template>
                </el-table-column>
                <!-- 付款类型 -->
                <el-table-column
                  align="center"
                  prop="paymentType"
                  :label="$t('contractMod.payType')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentType" />
                  </template>
                </el-table-column>
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="specification"
                  :label="$t('common.vendorCode')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <quick-search
                      :show-input="scope.row.vendorCode"
                      show-key="companyCode"
                      :scope-data="form"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </template>
                </el-table-column>
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('common.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 总金额（未税） -->
                <el-table-column
                  align="center"
                  prop="totalAmount"
                  :label="$t('contractMod.totalAmountNoTax')"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.totalAmount" />
                  </template>
                </el-table-column>
                <!-- 未付金额（未税） -->
                <el-table-column
                  align="center"
                  prop="totalAmount2"
                  :label="$t('purSettlementMod.unpaidAmountNoTax')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.totalAmount2" />
                  </template>
                </el-table-column>
                <!-- 本次计划付款金额（未税） -->
                <el-table-column
                  align="center"
                  prop="totalAmount3"
                  :label="$t('purSettlementMod.planPaymentAmountNoTax')"
                  width="170"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.totalAmount3" />
                  </template>
                </el-table-column>
                <!-- 本次实际付款金额（未税） -->
                <el-table-column
                  align="center"
                  prop="totalAmount4"
                  :label="$t('purSettlementMod.actualPaymentAmountNoTax')"
                  width="170"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.totalAmount4" />
                  </template>
                </el-table-column>
                <!-- 付款申请单号（SRM） -->
                <el-table-column
                  align="center"
                  prop="applicationNum"
                  :label="$t('purSettlementMod.paymentApplyNumber')"
                  width="150"
                />
                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="comments"
                  :label="$t('common.remark')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comments" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
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
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
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
          <el-button
            type="primary"

            @click="submitBill"
          >
            {{
              $t("accountMod.review")
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
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'PaymentPlanDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
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
        vendorName: '',
        vendorCode: '',
        vendorId: '',
        status: '',
        tel: '',
        linkMan: '',
        currency: '',
        taxRate: '',
        showPriceEnabled: 'N',
        remark: '',
        fileuploadId: '',
        fileName: ''
      },
      receptionList: [],
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
      isModify: false
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
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
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'paymentPlanDetail' + this.$attrs.params.row.templateName
        )
      } else {
        this.$emit('tab-remove', 'paymentPlanDetail')
      }
      this.__setTabTodo('paymentPlanList.getQuerydata')
    },
    addOneContent () {
      this.receptionList.push({
        sourceNum: '',
        sourceType: '',
        paymentQuote: '',
        paymentType: '',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        totalAmount: '',
        totalAmount2: '',
        totalAmount3: '',
        totalAmount4: '',
        applicationNum: '',
        comments: ''
      })
    },
    readOneContent () {},
    copyOneContent () {},
    submitBill () {},
    saveBill () {
      this.$emit('tab-remove', 'paymentPlanDetail')
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
      //         'paymentPlanDetail' + this.$attrs.params.row.templateName
      //       )
      //     } else {
      //       this.$emit('tab-remove', 'paymentPlanDetail')
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
.the-paymentPlanDetail-detail {
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
}
</style>
