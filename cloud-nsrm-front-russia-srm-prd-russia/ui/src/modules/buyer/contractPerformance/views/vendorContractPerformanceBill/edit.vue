<template>
  <el-container
    class="vendorContractPerformanceBillEdit"
    direction="vertical"
  >
    <el-main>
      <el-tabs
        v-model="activeName"
        type="border-card"
      >
        <el-tab-pane :label="$t('priceModel.costElement.baseInfo')">
          <div class="form-container">
            <el-form
              ref="form"
              :model="form"
            >
              <el-row :gutter="32">
                <el-col :span="8">
                  <el-form-item
                    :label="$t('合同序号')"
                    prop="contractNo"
                    :rules="[{ required: true, message: $t('contract_mod.required') }]"
                  >
                    <quick-search
                      :show-input="form.contractNo"
                      show-key="contractNo"
                      :scope-data="form"
                      name="queryPerformContract"
                      :disabled="!['DRAFT', 'REJECTED'].includes(form.status) && mode !== 'add'"
                      @close-quicksearch="writeBackContract"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
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
                <el-col :span="8">
                  <el-form-item
                    :label="$t('contract_mod.contractType')"
                    prop="contractClass"
                  >
                    <dict-select
                      v-model="form.contractClass"
                      disabled
                      code="ELEM_CONTRACT_TYPE"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('bid_mod.businessEntity')"
                    prop="buName"
                  >
                    <el-input
                      v-model="form.buName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('状态')"
                    prop="status"
                  >
                    <dict-select
                      v-model="form.status"
                      disabled
                      code="CONTRACT_STATE"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('履约单号')"
                    prop="perOrderNo"
                  >
                    <el-input
                      v-model="form.perOrderNo"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="contractCreatedFullName"
                  >
                    <el-input
                      v-model="form.contractCreatedFullName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('合同总金额（含税）')"
                    prop="includeTaxAmount"
                  >
                    <el-input
                      v-model="form.includeTaxAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('币种')"
                    prop="currencyName"
                  >
                    <el-input
                      v-model="form.currencyName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('common.creationTime')"
                    prop="contractCreationDate"
                  >
                    <el-date-picker
                      v-model="form.contractCreationDate"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <div class="title">
              {{ $t('orderMod.buyerOrderSynergy.orderDetails') }}
            </div>
            <i-order-detail :data="perOrderDetailList" />
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('contract_mod.contractImplementatPlan')">
          <i-perform-plan
            :data="perOrderPlanList"
            :store="store"
            :mode="mode"
            @payment-change="paymentChangeHandler"
            @handover="handover"
          />
        </el-tab-pane>
        <el-tab-pane :label="$t('contract_mod.contractPerformancePlan')">
          <div class="form-container">
            <el-form
              ref="form"
              :model="form"
            >
              <el-row :gutter="32">
                <el-col :span="8">
                  <el-form-item
                    :label="$t('合同编号')"
                    prop="contractNo"
                  >
                    <el-input
                      v-model="form.contractNo"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
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
                <el-col :span="8">
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="contractCreatedFullName"
                  >
                    <el-input
                      v-model="form.contractCreatedFullName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('合同总金额（含税）')"
                    prop="includeTaxAmount"
                  >
                    <el-input
                      v-model="form.includeTaxAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :label="$t('币种')"
                    prop="currencyName"
                  >
                    <el-input
                      v-model="form.currencyName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <i-perform-invoice
              :data="perOrderInPlanList"
              :mode="mode"
              :store="store"
              :include-tax-amount="form.includeTaxAmount"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="cancel"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="['TOBEAPPROVED'].includes(form.status)"
            type="primary"
            :disabled="readOnly"
            @click="approve"
          >
            {{ $t("通过") }}
          </el-button>
          <el-button
            v-if="['TOBEAPPROVED'].includes(form.status)"
            type="primary"
            :disabled="readOnly"
            @click="reject"
          >
            {{ $t("驳回") }}
          </el-button>
          <el-button
            v-if="['REJECTED', 'DRAFT'].includes(form.status) || mode === 'add'"
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="form.perOrderId && ['REJECTED', 'DRAFT'].includes(form.status)"
            type="primary"
            :disabled="readOnly"
            @click="submit"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
    <srm-dialog
      :title="$t('交付')"
      size="large"
      :visible.sync="payPageVisible"
      :close-on-click-modal="false"
    >
      <i-pay-page
        v-if="payPageVisible"
        ref="payPlan"
        :data="deliveryOrderData"
        :mode="mode"
        :disabled="iPayPageDisabled"
      />
      <template #footer>
        <el-button @click="payPageVisible = false">
          {{
            $t("common.cancel")
          }}
        </el-button>
        <template v-if="isEditable">
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="saveOrderPlanPay"
          >
            {{
              $t("common.staging")
            }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="submitOrderPlanPay"
          >
            {{
              $t("common.submit")
            }}
          </el-button>
        </template>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import IPerformPlan from './components/i-perform-plan.vue'
import IPayPage from './components/i-pay-page.vue'
import IPerformInvoice from './components/i-perform-invoice.vue'
import IOrderDetail from './components/i-order-detail.vue'

export default {
  name: 'VendorContractPerformanceBillEdit',
  components: {
    CToolbar,
    QuickSearch,
    IPerformPlan,
    IPayPage,
    IPerformInvoice,
    IOrderDetail
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      isEditable: false,
      iPayPageDisabled: false,
      activeName: '0',
      readOnly: false,
      form: {
        contractNo: ''
      },
      // 合同物料明细行
      perOrderDetailList: [],
      // 履行计划
      perOrderPlanList: [],
      // 履行开票计划
      perOrderInPlanList: [],
      // 履行计划id 用于查询交付页面详情
      perOrderPlanId: null,
      payPageVisible: false,
      // 交货单详情信息
      deliveryOrderData: {
        approverCode: '',
        approverName: '',
        approverId: '',
        // 物料明细行
        perDelivOrderDetaList: [{}],
        // 自定义配置字段行
        perDelivOrderConfList: [],
        // 附件行
        perDelivOrderAttList: []
      },
      // 当前选中的履行计划行，用于开票计划列表
      selectionPlanLines: [],
      mode: ''
    }
  },
  computed: {},
  watch: {},
  created () {
    const { row, flag } = this.$attrs.params
    this.mode = flag
    if (this.mode === 'handOver') this.activeName = '1'
    this.readOnly = flag === 'view'
    if (row.perOrderId) {
      this.initFormData(row.perOrderId)
    }
  },
  methods: {
    paymentChangeHandler () {
      const selections = this.perOrderPlanList.filter(
        (i) => i.paymentNode === 'Y' && i.isSelected === 'Y'
      )
      console.log('[paymentChangeHandler]', selections)
      this.perOrderInPlanList = JSON.parse(JSON.stringify(selections))
    },
    async handover (row, disabled) {
      this.iPayPageDisabled = disabled
      this.isEditable = ['REJECTED', 'UNFINISHED_WORK'].includes(row.planStatus)
      const { data } = await this.$api.cmPerform.buyer.main.performOrder.getPerDelivOrder(row.perOrderPlanId)
      const defaultData = {
        approverCode: '',
        approverName: '',
        approverId: '',
        // 物料明细行
        perDelivOrderDetaList: [{}],
        // 自定义配置字段行
        perDelivOrderConfList: [],
        // 附件行
        perDelivOrderAttList: []
      }
      this.deliveryOrderData = data || defaultData
      // 展示交付节点详情
      this.payPageVisible = true
    },
     async writeBackContract (data) {
      this.form.contractNo = data.contractNo
      if (!data || !data.contractClass) return
      const res = await this.$api.cmPerform.buyer.main.performOrder.getPerOrderByContractNo(data.contractNo)
      const { perOrderDetailList, perOrderPlanList, perOrderInPlanList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perOrderInPlanList = perOrderInPlanList
      const list = []
      let flag = false
      perOrderPlanList.forEach((item) => {
        if (item.isSelected === 'Y') {
          if (item.planStatus === 'COMPLETE') {
            list.push({ ...item, show: true })
          } else {
            if (!flag) {
              list.push({ ...item, show: true })
              flag = true
            } else {
              list.push({ ...item, show: false })
            }
          }
        } else {
          list.push({ ...item, show: false })
        }
      })
      this.perOrderPlanList = list
      this.perOrderDetailList = perOrderDetailList
    },
    async initFormData (id) {
      const res = await this.$api.cmPerform.buyer.main.performOrder.getPerOrderById(id)
      const { perOrderDetailList, perOrderPlanList, perOrderInPlanList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perOrderInPlanList = perOrderInPlanList
      const list = []
      let flag = false
      perOrderPlanList.forEach((item) => {
        if (item.isSelected === 'Y') {
          if (item.planStatus === 'COMPLETE') {
            list.push({ ...item, show: true })
          } else {
            if (!flag) {
              list.push({ ...item, show: true })
              flag = true
            } else {
              list.push({ ...item, show: false })
            }
          }
        } else {
          list.push({ ...item, show: false })
        }
      })
      this.perOrderPlanList = list
      console.log(this.perOrderPlanList)
      this.perOrderDetailList = perOrderDetailList
    },
    validate () {
      return new Promise((rs) => {
        this.$refs.form.validate((flag) => rs(flag))
      })
    },
    async saveOrderPlanPay () {
      const flag = await this.$refs.payPlan.validate()
      if (!flag) return
      const data = this.deliveryOrderData
      console.log('[deliveryOrderData]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.addOrUpdatePerDelivOrder(data)
      this.$message.success(res.message)
      this.payPageVisible = false
      this.refresh()
    },
    async submitOrderPlanPay () {
      const flag = await this.$refs.payPlan.validate()
      if (!flag) return
      const data = this.deliveryOrderData
      console.log('[deliveryOrderData]', data)
      let submitMethod = 'purchaseSubmitPerDelivOrder'
      if (this.$store.getters.userInfo.userType === 'VENDOR') {
        submitMethod = 'vendorSubmitPerDelivOrder'
      }
      console.log('[submitMethod]', submitMethod)
      const res = await this.$api.cmPerform.buyer.main.performOrder[submitMethod](data)
      this.$message.success(res.message)
      this.payPageVisible = false
      this.refresh()
    },
    async save () {
      const flag = await this.validate()
      if (!flag) return
      const data = {
        ...this.form,
        perOrderInPlanList: this.perOrderInPlanList,
        perOrderPlanList: this.perOrderPlanList,
        perOrderDetailList: this.perOrderDetailList
      }
      console.log('[save]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.saveOrUpdatePerOrder(data)
      this.$message.success(res.message)
      this.refresh()
    },
    async submit () {
      const flag = await this.validate()
      if (!flag) return
      const _flag = await this.billValidate()
      if (_flag) return
      const data = {
        ...this.form,
        perOrderInPlanList: this.perOrderInPlanList,
        perOrderPlanList: this.perOrderPlanList,
        perOrderDetailList: this.perOrderDetailList
      }
      console.log('[submit]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.submitPerOrder(data)
      this.$message.success(res.message)
      this.cancel()
    },
    billValidate () {
      return new Promise((rs) => {
        let checkFields = [
          {
            key: 'nodePersonName',
            message: this.$t('contract_mod.nodePersonNameRequired'),
            activeName: '1'
          },
          {
            key: 'planStartDate',
            message: this.$t('contract_mod.planTimeRequired'),
            activeName: '1'
          },
          {
            key: 'planEndDate',
            message: this.$t('contract_mod.planTimeRequired'),
            activeName: '1'
          }
        ]
        let isBreak = false
        for (let i = 0; i < this.perOrderPlanList.length; i++) {
          const errorIndex = checkFields.findIndex(j => !this.perOrderPlanList[i][j.key])
          if (errorIndex > -1) {
            isBreak = true
            this.$message.error(checkFields[errorIndex].message)
            this.activeName = checkFields[errorIndex].activeName
            rs(isBreak)
            break
          }
        }
        if (isBreak) return
        checkFields = [
          {
            key: 'nodePersonName',
            message: this.$t('contract_mod.planTimeRequired'),
            activeName: '2'
          },
          {
            key: 'planInvoiceProportion',
            message: this.$t('contract_mod.planInvoiceProportionRequired'),
            activeName: '2'
          }
        ]
        for (let i = 0; i < this.perOrderInPlanList.length; i++) {
          const errorIndex = checkFields.findIndex(j => !this.perOrderInPlanList[i][j.key])
          if (errorIndex > -1) {
            isBreak = true
            this.$message.error(checkFields[errorIndex].message)
            this.activeName = checkFields[errorIndex].activeName
            rs(isBreak)
            break
          }
        }
        if (!this.perOrderInPlanList.length) {
          isBreak = true
          this.$message.error(this.$t('contract_mod.leastOnePaymentNode'))
        }
        rs(isBreak)
      })
    },
    async approve () {
      const flag = await this.validate()
      if (!flag) return
      const data = {
        ...this.form,
        perOrderInPlanList: this.perOrderInPlanList,
        perOrderPlanList: this.perOrderPlanList,
        perOrderDetailList: this.perOrderDetailList
      }
      console.log('[approve]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.approvePerOrder(data)
      this.$message.success(res.message)
      this.cancel()
    },
    async reject () {
      const flag = await this.validate()
      if (!flag) return
      const data = {
        ...this.form,
        perOrderInPlanList: this.perOrderInPlanList,
        perOrderPlanList: this.perOrderPlanList,
        perOrderDetailList: this.perOrderDetailList
      }
      console.log('[reject]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.rejectedPerOrder(data)
      this.$message.success(res.message)
      this.cancel()
    },
    async handoverApprove () {
      const flag = await this.$refs.payPlan.validate()
      if (!flag) return
      const data = this.deliveryOrderData
      console.log('[handoverApprove]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.approveDelivOrder(data)
      this.$message.success(res.message)
      this.payPageVisible = false
      this.refresh()
    },
    async handoverReject () {
      const flag = await this.$refs.payPlan.validate()
      if (!flag) return
      const data = this.deliveryOrderData
      console.log('[handoverReject]', data)
      const res = await this.$api.cmPerform.buyer.main.performOrder.rejectedDelivOrder(data)
      this.$message.success(res.message)
      this.payPageVisible = false
      this.refresh()
    },
    refresh () {
      const { row } = this.$attrs.params
      this.initFormData(row.perOrderId)
    },
    cancel () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('vendorContractPerformanceBillList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.vendorContractPerformanceBillEdit {
  .title {
    margin: 10px 0;
    font-size: 14px;
    font-weight: bolder;
    color: #606266;
  }
  .form-container {
    padding-bottom: 20px;
  }
}
</style>
