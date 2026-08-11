<template>
  <el-container
    class="flex-container the-penaltyDeductionOrderDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
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
              <!-- 供应商名称 -->
              <el-form-item
                :label="$t('common.vendorName')"
                :label-width="formLabelWidth"
              >
                <quick-search
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info"
                  @close-quicksearch="getVendorObj"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚项类型 -->
              <el-form-item
                :label="$t('accountMod.deductionType1')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.tel" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚单据类型 -->
              <el-form-item
                :label="$t('accountMod.deductionBillType')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.vendorName" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 来源单据号 -->
              <el-form-item
                :label="$t('accountMod.sourceBillNum')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.sourceNum" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 扣罚款税率 -->

              <el-form-item
                :label="$t('accountMod.deductionTaxRate')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.purchaseNum" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚款税前金额 -->
              <el-form-item
                :label="$t('accountMod.deductionNoTaxAmount')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.deliveryNum" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚款税后金额 -->
              <el-form-item
                :label="$t('accountMod.deductionTaxAmount')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.paidAmount" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚款税额 -->
              <el-form-item
                :label="$t('accountMod.fineDeductionTax')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.totalAmount"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 币种 -->
              <el-form-item
                :label="$t('bid_mod.currencyName')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="form.currency"
                  code="currency"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚款日期 -->
              <el-form-item
                :label="$t('accountMod.deductionDate')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.stopTime"
                  type="date"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚款附件 -->
              <el-form-item
                :label="$t('accountMod.deductionFineAttach')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.returnQuantity" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('qualitySynergy.creationDate')"
                :label-width="formLabelWidth"
                prop="creationDate"
              >
                <el-date-picker
                  v-model="form.stopTime"
                  type="date"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('contractMod.createdBy')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.createdUserName" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.lastUpdateDate')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.lastUpdateDate"
                  type="date"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 扣罚单据号 -->
              <el-form-item
                :label="$t('accountMod.deductionDocNum')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.returnQuantity" />
              </el-form-item>
            </el-col>
            <el-col><p /></el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 扣罚事由 -->
              <el-form-item
                :label="$t('accountMod.deductionReason')"
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
          <el-row type="flex">
            <el-col>
              <!-- 供应商申述附件 -->
              <el-form-item
                :label="$t('accountMod.supplierAppealAttach')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.debateFile" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 结案类型 -->
              <el-form-item
                :label="$t('accountMod.closeCaseType')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.finishType" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 结案时间 -->
              <el-form-item
                :label="$t('accountMod.closeCaseDate')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.finishDate"
                  type="date"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 创建人 -->
              <el-form-item
                :label="$t('common.creator')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.returnQuantity" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 申述事由 -->
              <el-form-item
                :label="$t('accountMod.representationReason')"
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
        </el-form>
      </div>
      <div class="the_bottom_btns">
        <el-button
          type="primary"

          @click="approvalBill"
        >
          {{
            $t("accountMod.closeCase")
          }}
        </el-button>
        <el-button
          type="primary"

          @click="submitBill"
        >
          {{
            $t("common.publish")
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
      </div>
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'

export default {
  name: 'PenaltyDeductionOrderDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        vendorId: '',
        vendorCode: '',
        vendorName: '',
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
    // 状态
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
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'penaltyDeductionOrderDetail' + this.$attrs.params.row.templateName
        )
      } else {
        this.$emit('tab-remove', 'penaltyDeductionOrderDetail')
      }
      this.__setTabTodo('technicalCommunicationList.getQuerydata')
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    readOneContent () {},
    copyOneContent () {},
    submitBill () {},
    approvalBill () {},
    saveBill () {
      this.$emit('tab-remove', 'penaltyDeductionOrderDetail')
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
      //         'penaltyDeductionOrderDetail' +
      //           this.$attrs.params.row.templateName
      //       )
      //     } else {
      //       this.$emit('tab-remove', 'penaltyDeductionOrderDetail')
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
.the-penaltyDeductionOrderDetail-detail {
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
  .the_bottom_btns {
    padding-right: 33px;
  }
  .the_bottom_btns .el-button {
    float: right;
    margin: 5px;
  }
}
</style>
