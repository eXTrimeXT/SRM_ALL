<template>
<!--  <el-container class="flex-container" direction="vertical">-->
<!--    <el-main>-->
      <el-form ref="form" :model="form" :rules="formRules">
        <BaseInfo
          ref="baseInfo"
          :form.sync="form"
          :readonly="disabledFlag"
        />
        <el-collapse v-model="colValue">
          <!-- 协议明细 -->
          <el-collapse-item :title="$t('cusEntry.supplement20250121.agreeInfo')" name="2">
            <AgreeInfo
              ref="agreeInfo"
              :form="form"
              :value="form.agreeList"
              :readonly="disabledFlag"
              @fileSuccess="fileSuccess"
            />
          </el-collapse-item>
          <!-- 供应商明细 -->
          <el-collapse-item :title="$t('cusEntry.supplement20250121.vendorInfo')" name="2">
            <VendorInfo
              ref="vendorInfo"
              :value="form.vendorList"
              :readonly="disabledFlag"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
<!--    </el-main>-->
<!--    <CToolbar>-->
<!--      <template slot="right">-->
<!--        <el-button type="ghost" @click="back">-->
<!--          {{ $t('bidMod.cancel') }}-->
<!--        </el-button>-->
<!--        <template v-if="!disabledFlag">-->
<!--          <el-button type="primary" @click="saveBill('SAVE')">-->
<!--            {{ $t('bidMod.temporaryStorage') }}-->
<!--          </el-button>-->
<!--          <el-button type="primary" @click="saveBill('SUBMIT')">-->
<!--            {{ $t('problemManagement.submit') }}-->
<!--          </el-button>-->
<!--        </template>-->
<!--      </template>-->
<!--    </CToolbar>-->
<!--  </el-container>-->
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/market/baseInfo'
import AgreeInfo from './components/market/agreeInfo'
import VendorInfo from './components/market/vendorInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { centralHttp } from 'modcb@/jcAgreement/api'

export default {
  name: 'PriceAdjustApplyMarket',
  components: {
    BaseInfo,
    AgreeInfo,
    VendorInfo,
    CToolbar
  },
  props: {
    form: {
      type: Object,
      default: () => {}
    }
  },
  watch: {
    form: {
      handler (val) {
        console.log(val, 'val')
      },
      deep: true
    }
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      adjustId: null,
      colValue: ['1', '2'],
      formRules: {
        companyName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        agreementName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        agreementCode: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        supplyArea: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        buyOrgId: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        payment: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        supName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        trading: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        buyPersonName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        pricingWay: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        invoiceType: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        payWay: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        effectiveStartDate: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        effectiveEndDate: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        currencyType: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        agreementType: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        mixAmount: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        agreementFileName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        remark: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      agreeList: []
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    }
  },
  mounted () {
    // const { adjustId } = this.urlParams.row
    // if (adjustId) {
    //   this.adjustId = adjustId
    //   this.getFormDetail()
    // }
  },
  methods: {
    // formChange (val) {
    //   console.log(val, 'val')
    //   this.form.remark = val
    // },
    fileSuccess () {
      this.$emit('fileSuccess')
    },
    async getFormDetail () {
      const response = await centralHttp.getJcAgreementInfo({
        adjustId: this.adjustId
      })
      if (response.data) {
        this.form = response.data
        const { sccSouJcAgreementOrgList = [] } = this.form
        this.form.buyOrgId = sccSouJcAgreementOrgList.map(item => item.buyOrgId)
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      console.log('params', params)
      return params
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        resolve(validForm)
      })
    },
    async saveBill (type) {
      let params = this.initParams()
      if (type === 'SUBMIT') {
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
        }

        // TODO 协议信息的校验
      }
      const response = await centralHttp.saveOrUpdateJcAgreement(params)
      if (response.data) {
        this.form = response.data
        this.adjustId = this.form.adjustId
        this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail()
        if (type === 'SUBMIT') {
          this.back()
        }
      }
    },
    async saveChangeBill () {
      let params = this.initParams()
      const validForm = await this.validBill()
      if (!validForm) {
        this.__focus_error__()
      }
      // TODO 协议信息的校验
      const response = await centralHttp.changeJcAgreement(params)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.back()
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('PriceAdjustApplyList.getQueryData')
    }
  }
}
</script>
