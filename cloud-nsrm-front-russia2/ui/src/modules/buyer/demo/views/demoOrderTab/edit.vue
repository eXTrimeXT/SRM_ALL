<template>
  <el-container
    class="demoorderEdit"
    direction="vertical"
  >
    <el-main class="mb-40">
      <div class="form-container">
        <!-- :disabled="IS_READ_ONLY || !BUYER_EDIT" -->
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                :label="$t('components.organization.organizationCode')"
                prop="organizationId"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.organizationId"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  :scope="form"
                  @select="selectHandler"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorId"
                :label="$t('common.vendor')"
              >
                <QuickSearch
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info5"
                  @close-quicksearch="getVendorObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorCode"
                :label="$t('supplierRating.supplierCode')"
              >
                <el-input
                  v-model="form.vendorCode"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderStatus"
                :label="$t('orderMod.buyerOrderSynergy.orderStatus')"
              >
                <DictSelect
                  v-model="form.orderStatus"
                  code="ORDER_STATUS"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderNumber"
                :label="$t('orderMod.buyerOrderSynergy.orderNumber2')"
              >
                <el-input
                  v-model="form.orderNumber"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderAmount"
                :label="$t('dataConfMod.orderAmount')"
              >
                <el-input
                  v-model="form.orderAmount"
                  v-input-format="{ type: 'float' }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="rfqSettlementCurrency"
                :label="$t('dataConfMod.settingGuide.step3.2')"
              >
                <DictSelect
                  v-model="form.rfqSettlementCurrency"
                  code="currency"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="termOfPayment"
                :label="$t('route.contractPaymentType')"
              >
                <DictSelect
                  v-model="form.termOfPayment"
                  code="PAYMENT_TERMS"
                  @change="val => termOfPaymentChange(val,form)"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="paymentMethod"
                :label="$t('bidMod.category_paymentMethod')"
              >
                <DictSelect
                  v-model="form.paymentMethod"
                  code="PAYMENT_WAY"
                  @change="val => paymentMethodChange(val,form)"
                />
              </el-form-item>
            </el-col>
            <!-- 付款方式 为电汇的时候显示税率 -->
            <el-col
              v-if="form.paymentMethod=='WIRE_TRANSFER'"
              :span="6"
            >
              <el-form-item
                prop="taxKey"
                :label="$t('dataConfMod.settingGuide.step3.4')"
              >
                <dict-select
                  v-model="form.taxKey"
                  code="tax"
                  @change-value="getRaxRateObj"
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.termOfPayment=='WIRE_TRANSFER'"
              :span="6"
            >
              <el-form-item
                prop="taxKey"
                :label="$t('dataConfMod.taxKey')"
              >
                <el-input
                  v-model="form.taxKey"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="deliveryLevel"
                :label="$t('key9')"
              >
                <DictSelect
                  v-model="form.deliveryLevel"
                  :disabled="form.paymentMethod=='WIRE_TRANSFER'"
                  code="DELIVERY_LEVEL"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderType"
                :label="$t('purchaseDemand.orderType')"
              >
                <DictSelect
                  v-model="form.orderType"
                  code="ORDER_TYPE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="jitOrder"
                :label="$t('jit')"
              >
                <DictSelect
                  v-model="form.jitOrder"
                  code="JIT_ORDER"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="buyerName"
                :label="$t('purchaseDemand.buyerName')"
              >
                <QuickSearch
                  :show-input="form.buyerName"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getBuyerObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="tel"
                :label="$t('orderMod.buyerOrderSynergy.tel')"
              >
                <el-input
                  v-model="form.tel"
                  maxlength="11"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="purchaseResponse"
                :label="$t('key8')"
              >
                <el-input v-model="form.purchaseResponse" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="approveStatus"
                :label="$t('common.approvalStatus')"
              >
                <DictSelect
                  v-model="form.approveStatus"
                  code="APPROVE_STATUS"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="comments"
                :label="$t('bidMod.appraisRemark')"
              >
                <el-input
                  v-model="form.comments"
                  type="textarea"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse v-model="activeSec">
        <el-collapse-item
          :title="$t('vendorMod.vendorConfirm')"
          name="1"
        >
          <el-form
            :model="form"
            :rules="rules"
          >
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item
                  prop="comfirmBy"
                  :label="$t('key7')"
                >
                  <el-input v-model="form.comfirmBy" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="comfirmTime"
                  :label="$t('bidMod.confirmeDatetime')"
                >
                  <el-date-picker
                    v-model="form.comfirmTime"
                    :format="$formatDatePickerTime"
                    type="datetime"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="refuseId"
                  :label="$t('orderMod.refuseId')"
                >
                  <el-input v-model="form.refuseId" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="refuseBy"
                  :label="$t('orderMod.refuseBy')"
                >
                  <el-input v-model="form.refuseBy" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="refuseTime"
                  :label="$t('key6')"
                >
                  <el-date-picker v-model="form.refuseTime" :format="$formatDatePickerTime" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="refuseReason"
                  :label="$t('contractMod.refusedReason')"
                >
                  <el-input v-model="form.refuseReason" />
                </el-form-item>
              </el-col>

              <el-col :span="6">
                <el-form-item
                  prop="responseStatus"
                  :label="$t('key5')"
                >
                  <DictSelect
                    v-model="form.responseStatus"
                    code="RESPONSE_STATUS"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button
            v-if="BUYER_EDIT"
            type="primary"
            @click="save"
          >
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import OrganizationSelector from 'lib@/components/organization-selector'
import { validatePhone } from '@/utils/validate'
import { demoOrderApi } from 'modb@/demo/api'
export default {
  name: 'DemoorderEdit',
  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeSec: ['1', '2'],
      userType: this.$store.getters.userType,
      form: {
        organizationId: null,
        fullPathId: null,
        vendorId: null,
        orderStatus: null,
        orderNumber: null,
        orderAmount: null,
        rfqSettlementCurrency: null,
        termOfPayment: null,
        paymentMethod: null,
        deliveryLevel: null,
        buyerName: null,
        tel: null,
        taxRate: null,
        taxKey: null,
        comments: null,
        submittedId: null,
        submittedBy: null,
        submittedTime: null,
        comfirmId: null,
        comfirmBy: null,
        comfirmTime: null,
        refuseId: null,
        refuseBy: null,
        refuseTime: null,
        refuseReason: null,
        createdId: null,
        createdBy: null,
        creationDate: null,
        createdByIp: null,
        lastUpdateDate: null,
        lastUpdatedId: null,
        lastUpdatedBy: null,
        lastUpdatedByIp: null,
        tenantId: null,
        version: null,
        organizationCode: null,
        organizationName: null,
        vendorCode: null,
        vendorName: null,
        companyCode: null,
        responseStatus: null,
        sourceSystem: null,
        purchaseResponse: null,
        orderType: null,
        jitOrder: null,
        cbpmInstanceId: null,
        approveStatus: null,
        demoOrderDetailList: []
      },
      rules: {
        tel: [
          {
            required: true,
            message: this.$t('cusEntry.supplement20250211.phoneNumberNotEmpty')  // '手机号码不能为空！'
          }, 
          {
            trigger: 'blur',
            validator: (rule, value, callback) => {
              const telValidate = validatePhone(value)
              if (!telValidate) {
                callback(
                  // '手机号码不正确'
                  this.$message({ type: 'warning', message: this.$t('cusEntry.supplement20250211.incorrectPhoneNumber') })
                )
              } else {
                callback()
              }
            }
          }
        ]
      },
      readOnly: false
    }
  },
  computed: {
    IS_BUYER () {
      return this.userType === 'BUYER'
    },
    IS_VENDOR () {
      return this.userType === 'VENDOR'
    },
    BUYER_EDIT () { // 采购商可编辑判断
      const state = this.form.orderStatus || 'CREATE' // 单据状态
      let approvedStatus = this.form.approvedStatus // 审批状态
      if (this.IS_BUYER &&
        (['CREATE', 'BUYER_WITHDRAWED', 'VENDOR_REJECTED'].includes(state) ||
          (approvedStatus === 'REJECTED' && (state === 'BUYER_SUBMITTED' || state === 'SUPPLIER_CONFIRMED')))
      ) {
        return true // 采购商可编辑
      }
      return false
    },
    VENDOR_EDIT () { // 供应商可编辑判断
      const state = this.form.orderStatus || 'CREATE' // 单据状态
      if (this.IS_VENDOR &&
        ['PUBLISHED', 'VENDOR_WITHDRAWED', 'BUYER_REJECTED'].includes(state)) {
        return true
      }
      return false
    },
    IS_READ_ONLY () {
      const { flag, readOnly = false } = this.$attrs.params
      if (flag !== 'view' || !readOnly) { // 查看 (根据情况可调整)
        return false
      }
      return true
    }
  },
  watch: {},
  created () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly || flag === 'view'
    if (flag === 'edit' || flag === 'view') {
      this.form = row
    }
  },
  mounted () {},
  methods: {
    // 下拉选择以后通过查询接口返回数据以后赋值其他字段
    async termOfPaymentChange (val, formData) {
      console.log(val)
      console.log(this.form.paymentMethod)
      const { data } = await this.$api.cm.buyer.main.modelListByType(val)
      if (data) {
        formData.a = data.a
        formData.b = data.aa
        // todu
      } else {
        // todo
      }
    },
    paymentMethodChange (val, formData) {
      if (val === 'WIRE_TRANSFER') {
        this.form.deliveryLevel = ''
      }
    },
    // 业务实体选择回调
    selectHandler (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    // 关闭供应商快查回调
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    // 关闭采购员快查
    getBuyerObj (val, scope) {
      scope.buyerName = val ? val.nickname : ''
    },
    // 选择税率回调
    getRaxRateObj (val, dictItem) {
      this.form.taxRate = dictItem.key // 税率值
      // const dRowObj = this.dictClass.getDictDetail('tax', val)
      // console.log(dRowObj)
    },
    // 保存
    save () {
      this.$refs.form.validate((result) => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { demoOrderId, ...rest } = this.form
          if (flag === 'add') {
            demoOrderApi.add(rest).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            demoOrderApi.update(this.form).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        } else {
          return false
        }
      })
    },
    // 取消编辑，关闭tab页
    cancelBill () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('demoorderList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.demoorderEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
.demoorderEdit .form-container {
}
</style>
