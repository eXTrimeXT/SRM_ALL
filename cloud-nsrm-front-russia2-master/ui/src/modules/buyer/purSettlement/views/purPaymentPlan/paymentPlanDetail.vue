<template>
  <el-container
    class="flex-container the-paymentPlanDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="paymentPlanHead"
          :disabled="isDisabled"
          :model="paymentPlanHead"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('purSettlementMod.paymentPlanInfo')"
              name="1"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.paymentPlanNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="paymentPlanHead.paymentPlanNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.creationDate')"
                    :label-width="formLabelWidth"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="paymentPlanHead.creationDate"
                      disabled
                      :format="$formatDatePicker"
                      type="date"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.fullPathId')"
                    :label-width="formLabelWidth"
                  >
                    <OrganizationSelectTree
                      v-model="paymentPlanHead.organizationId"
                      :scope="paymentPlanHead"
                      :placeholder="$t('common.pleaseSelect')"
                      :parent-id="-1"
                      node-type="OU"
                      @select="addOrgHandle"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.currency')"
                    :label-width="formLabelWidth"
                  >
                    <DictSelect
                      v-model="paymentPlanHead.currency"
                      code="currency"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.planPaymentDate')"
                    :label-width="formLabelWidth"
                    prop="planPaymentDate"
                  >
                    <el-date-picker
                      v-model="paymentPlanHead.planPaymentDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.planPaymentAmountNoTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="paymentPlanHead.planPaymentAmountNoTax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.paymentPlanDetail')"
              name="2"
            >
              <p style="margin: 0 0 10px 0;">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="openOneContent"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="paymentPlanLines"
                style="width: 100%"
                border
                max-height="700px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="sourceType"
                  :label="$t('purSettlementMod.sourceType')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <span>{{ sourceTypeList2obj[scope.row.sourceType] }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="sourceNumber"
                  :label="$t('purSettlementMod.sourceNumber')"
                  width="170"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="payStage"
                  :label="$t('purSettlementMod.payStage')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="payType"
                  :label="$t('purSettlementMod.payType')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="payMethod"
                  :label="$t('purSettlementMod.paymentMethod')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <span>{{ payMethodList2obj[scope.row.payMethod] }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="termOfPayment"
                  :label="$t('purSettlementMod.termOfPayment')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <span>{{ payTermsList2obj[scope.row.termOfPayment] }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="vendorCode"
                  :label="$t('purSettlementMod.vendorCode')"
                  width="120"
                />
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('purSettlementMod.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="taxRate"
                  :label="$t('purSettlementMod.taxRate2')"
                  width="90"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                  width="120"
                />
                <el-table-column
                  align="center"
                  prop="unpaidAmountNoTax"
                  :label="$t('purSettlementMod.unpaidAmountNoTax')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="planPaymentAmountNoTax"
                  :label="$t('purSettlementMod.planPaymentAmountNoTax')"
                  width="170"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.planPaymentAmountNoTax"
                      v-input-format="{ type: 'float' }"
                      @input="setAllAmount"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="actualPaymentAmountNoTax"
                  :label="$t('purSettlementMod.actualPaymentAmountNoTax')"
                  width="170"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.actualPaymentAmountNoTax"
                      v-input-format="{ type: 'float' }"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paymentApplyNumber"
                  :label="$t('purSettlementMod.paymentApplyNumber')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('purSettlementMod.remark')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" />
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
              <srm-dialog
                :title="$t('purSettlementMod.paymentPlanDetail')"
                :visible.sync="dialogFormVisible"
                size="large"
                :close-on-click-modal="false"
              >
                <div>
                  <div class="form-container2">
                    <el-form
                      ref="paymentPlanLine"
                      :model="paymentPlanLine"
                      label-width="80px"
                      label-position="top"
                      class="form-incontainer"
                    >
                      <el-row type="flex">
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.sourceType')"
                            :label-width="formLabelWidth"
                          >
                            <DictSelect
                              v-model="paymentPlanLine.sourceType"
                              code="PAYMENT_SCHEDULES_SOURCE_TYPE"
                              clearable
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.sourceNumber')"
                            :label-width="formLabelWidth"
                          >
                            <el-input v-model="paymentPlanLine.sourceNumber" />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.vendorName')"
                            :label-width="formLabelWidth"
                          >
                            <!-- <el-input v-model="paymentPlanLine.vendorName" /> -->
                            <QuickSearch
                              :show-input="paymentPlanLine.vendorName"
                              show-key="companyName"
                              :scope-data="paymentPlanLine"
                              name="scc_sup_company_info"
                              @close-quicksearch="getVendorObj"
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-button
                            type="primary"
                            style="margin-top:23px"
                            @click="addOneContent('view')"
                          >
                            {{ $t("common.search") }}
                          </el-button>
                        </el-col>
                      </el-row>
                    </el-form>
                  </div>
                  <el-table
                    ref="parentOrgTable"
                    :data="displayPaymentPlanLines"
                    style="width: 100%"
                    border
                    max-height="350px"
                    @selection-change="handleSelectionChange"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                    />
                    <el-table-column
                      align="center"
                      type="index"
                      :label="$t('purSettlementMod.tabindex')"
                      width="60"
                    />
                    <el-table-column
                      align="center"
                      prop="sourceType"
                      :label="$t('purSettlementMod.sourceType')"
                      width="100"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <span>{{
                          sourceTypeList2obj[scope.row.sourceType]
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      align="center"
                      prop="sourceNumber"
                      :label="$t('purSettlementMod.sourceNumber')"
                      width="170"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="payStage"
                      :label="$t('purSettlementMod.payStage')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="payType"
                      :label="$t('purSettlementMod.payType')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="payMethod"
                      :label="$t('purSettlementMod.paymentMethod')"
                      width="100"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <span>{{
                          payMethodList2obj[scope.row.payMethod]
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      align="center"
                      prop="termOfPayment"
                      :label="$t('purSettlementMod.termOfPayment')"
                      width="100"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <span>{{
                          payTermsList2obj[scope.row.termOfPayment]
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      align="center"
                      prop="vendorCode"
                      :label="$t('purSettlementMod.vendorCode')"
                      width="120"
                    />
                    <el-table-column
                      align="center"
                      prop="vendorName"
                      :label="$t('purSettlementMod.vendorName')"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="taxRate"
                      :label="$t('purSettlementMod.taxRate2')"
                      width="90"
                    />
                    <el-table-column
                      align="center"
                      prop="totalAmountNoTax"
                      :label="$t('purSettlementMod.totalAmountNoTax')"
                      width="120"
                    />
                    <el-table-column
                      align="center"
                      prop="unpaidAmountNoTax"
                      :label="$t('purSettlementMod.unpaidAmountNoTax')"
                      width="150"
                    />
                  </el-table>
                  <el-row type="flex">
                    <el-col>
                      <CPagination
                        ref="queryPagination"
                        style="margin: 3px"
                        class="c-query-table-pagination"
                        :total="queryTotal"
                        :page-num="viewIndex"
                        :page-size="viewSize"
                        @current-change="changeCurrentIndex"
                        @size-change="changeCurrentSize"
                      />
                    </el-col>
                  </el-row>
                </div>
                <div
                  slot="footer"
                  class="dialog-footer"
                >
                  <el-button @click="dialogFormVisible = false">
                    {{ $t("common.cancel") }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="addOneItem"
                  >
                    {{ $t("common.confirm") }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar v-if="!isDisabled">
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
          <AuthorityButton
            code="ps:paymentPlanHead:rejectPaymentPlan"
            type="primary"
            @click="rejectcBill"
          >
            {{ $t("common.toRefuse") }}
          </AuthorityButton>
          <AuthorityButton
            code="ps:paymentPlanHead:passPaymentPlan"
            type="primary"
            @click="approvalBill"
          >
            {{ $t("common.toApprove") }}
          </AuthorityButton>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'PaymentPlanDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelectTree,
    CPagination
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      paymentPlanLine: {
        sourceType: '',
        sourceNumber: '',
        vendorName: ''
      },
      paymentPlanHead: {
        creationDate: '',
        fullPathId: '',
        planPaymentDate: '',
        planPaymentAmountNoTax: '',
        paymentPlanNumber: '',
        currency: '',
        organizationId: null,
        organizationName: '',
        organizationCode: ''
      },
      dialogFormVisible: false,
      mulSelection: [],
      paymentPlanLines: [],
      displayPaymentPlanLines: [],
      activeDims: ['1', '2', '3', '4'],
      rules: {
        paymentPlanNumber: [
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
      isDisabled: this.$attrs.params.flag == 'readOnly',
      formLabelWidth: '120px',
      isModify: false,
      sourceTypeList2obj: {},
      payTermsList2obj: {},
      payMethodList2obj: {}
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
    }
    if (
      this.$attrs.params.flag == 'edit' ||
      this.$attrs.params.flag == 'readOnly'
    ) {
      setTimeout(() => {
        this.getFormDetail()
      }, 1111)
    }
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-sup-ce/ps/paymentPlanHead/getPaymentPlanById',
        method: 'GET',
        params: { paymentPlanHeadId: this.$attrs.params.row.paymentPlanHeadId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.paymentPlanHead = data.data.paymentPlanHead
            this.paymentPlanLines = data.data.paymentPlanLines
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.paymentPlanLines.splice(index, 1)
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    // 选择组织
    addOrgHandle (node, instanceId) {
      const { organizationCode, organizationName, organizationId } = node
      this.paymentPlanHead.organizationId = organizationId
      this.paymentPlanHead.organizationCode = organizationCode
      this.paymentPlanHead.organizationName = organizationName
    },
    handleSelectionChange (selection) {
      this.mulSelection = selection
    },
    setAllAmount () {
      let payRatioArr = this.paymentPlanLines.map(
        v => v.planPaymentAmountNoTax
      )
      let payRatioTotal = payRatioArr.reduce((p, n) => Number(p) + Number(n))
      this.paymentPlanHead.planPaymentAmountNoTax = payRatioTotal
    },
    addOneItem () {
      this.mulSelection.map(v => {
        v.planPaymentAmountNoTax = v.unpaidAmountNoTax
      })
      this.paymentPlanLines = this.paymentPlanLines.concat(this.mulSelection)
      this.dialogFormVisible = false
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.addOneContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.addOneContent()
    },
    addOneContent (val) {
      if (val === 'view') {
        this.viewIndex = 1
      }
      if (!this.paymentPlanLine.sourceType) {
        this.$message({
          message: this.$t('purSettlementMod.selSourceTypeFirst'),
          type: 'info'
        })
        return
      }
      let params = Object.assign(
        { pageSize: this.viewSize, pageNum: this.viewIndex },
        this.paymentPlanLine
      )
      this.$http({
        url: '/api-sup-ce/ps/paymentPlanHead/listPaymentPlanLinePage',
        method: 'POST',
        data: params,
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.displayPaymentPlanLines = data.data.list
            this.queryTotal = data.data.total
            // this.dialogFormVisible =true;
            let sourceNumberList = this.paymentPlanLines.map(
              v => v.sourceNumber
            )
            if (sourceNumberList.length) {
              sourceNumberList.forEach(item => {
                const selection = data.data.list.find(
                  i => i.sourceNumber === item
                )
                if (selection) {
                  setTimeout(() => {
                    if (this.$refs.parentOrgTable) {
                      this.$refs.parentOrgTable.toggleRowSelection(
                        selection,
                        true
                      )
                    }
                  }, 100)
                }
              })
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    openOneContent () {
      this.dialogFormVisible = true
    },
    rejectcBill () {
      this.$http({
        url: '/api-sup-ce/ps/paymentPlanHead/rejectPaymentPlan',
        method: 'POST',
        data: {
          paymentPlanHead: this.paymentPlanHead,
          paymentPlanLines: this.paymentPlanLines
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'paymentPlanDetail' + this.$attrs.params.row.paymentPlanNumber
            )
          } else {
            this.$emit('tab-remove', 'paymentPlanDetail')
          }
          this.__setTabTodo('paymentPlanList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    approvalBill () {
      this.$http({
        url: '/api-sup-ce/ps/paymentPlanHead/passPaymentPlan',
        method: 'POST',
        data: {
          paymentPlanHead: this.paymentPlanHead,
          paymentPlanLines: this.paymentPlanLines
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'paymentPlanDetail' + this.$attrs.params.row.paymentPlanNumber
            )
          } else {
            this.$emit('tab-remove', 'paymentPlanDetail')
          }
          this.__setTabTodo('paymentPlanList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    submitBill () {
      this.$http({
        url: '/api-sup-ce/ps/paymentPlanHead/submitPaymentPlan',
        method: 'POST',
        data: {
          paymentPlanHead: this.paymentPlanHead,
          paymentPlanLines: this.paymentPlanLines
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'paymentPlanDetail' + this.$attrs.params.row.paymentPlanNumber
            )
          } else {
            this.$emit('tab-remove', 'paymentPlanDetail')
          }
          this.__setTabTodo('paymentPlanList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    saveBill () {
      let url = '/api-sup-ce/ps/paymentPlanHead/savePaymentPlan'
      if (this.paymentPlanHead.paymentPlanHeadId) {
        url = '/api-sup-ce/ps/paymentPlanHead/updatePaymentPlan'
      }
      this.$http({
        url: url,
        method: 'POST',
        data: {
          paymentPlanHead: this.paymentPlanHead,
          paymentPlanLines: this.paymentPlanLines
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'paymentPlanDetail' + this.$attrs.params.row.paymentPlanNumber
            )
          } else {
            this.$emit('tab-remove', 'paymentPlanDetail')
          }
          this.__setTabTodo('paymentPlanList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
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
