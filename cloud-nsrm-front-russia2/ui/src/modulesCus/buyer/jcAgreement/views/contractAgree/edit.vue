<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue">
          <!-- 协议管理 -->
          <el-collapse-item :title="$t('cusEntry.supplement20250121.protocolManage')" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="disabledFlag"
              :isChange="isChange"
              mode="contract"
            />
          </el-collapse-item>
          <!-- 协议信息 -->
          <el-collapse-item :title="$t('cusEntry.supplement20250121.protocolInfo')" name="2">
            <AgreeInfo
              ref="agreeInfo"
              :value.sync="form.sccSouJcAgreementInfoList"
              :readonly="disabledFlag"
              :agreementId="agreementId"
              :isChange="isChange"
              :agreement-status="form.agreementStatus"
              mode="contract"
              @fileSuccess="fileSuccess"
              @refresh="getFormDetail"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <template v-if="!disabledFlag">
          <!-- 非协议变更 -->
          <el-button v-if="urlParams.flag !== 'change'" type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button v-if="urlParams.flag !== 'change'" type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
          <!-- 协议变更 -->
          <el-button v-if="urlParams.flag === 'change'" type="primary" @click="saveChangeBill">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
        <template v-if="disabledFlag">
          <!-- 变更记录 -->
          <el-button type="primary" @click="getChangeRecords">
            {{ $t('vendorMod.changeRecord') }}
          </el-button>
        </template>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from 'modcb@/jcAgreement/views/centralizedAgree/components/baseInfo'
import AgreeInfo from 'modcb@/jcAgreement/views/centralizedAgree/components/agreeInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { centralHttp } from 'modcb@/jcAgreement/api'
import VersionRecord from 'modcb@/jcAgreement/views/centralizedAgree/versionRecord'
import { isNull } from '@/utils'

export default {
  name: 'ContractAgreeDetail',
  components: {
    BaseInfo,
    AgreeInfo,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      agreementId: null,
      colValue: ['1', '2'],
      form: {
        defaultAll: 'N',
        agreementId: null,
        companyId: null,
        companyCode: null,
        companyName: null,
        agreementCode: null,
        agreementName: null,
        supplyArea: [],
        buyOrgId: null,
        buyOrgCode: null,
        buyOrgName: null,
        payment: null,
        supId: null,
        supCode: null,
        supName: null,
        trading: null,
        buyPersonId: null,
        buyPersonCode: null,
        buyPersonName: null,
        pricingWay: '1',
        invoiceType: null,
        payWay: null,
        effectiveStartDate: null,
        effectiveEndDate: null,
        currencyType: 'RMB',
        agreementType: this.$t('cusEntry.inq.contractAgreeExportHeader'), // 合同协议
        mixAmount: 0,
        agreementFileId: null,
        agreementFileName: null,
        remark: null,
        sccSouJcAgreementOrgList: [],
        sccSouJcAgreementInfoList: []
      },
      formRules: {
        companyName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        agreementName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        agreementCode: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        supplyArea: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        buyOrgId: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        defaultAll: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
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
        agreementFileName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    isChange () {
      return this.urlParams.flag === 'change'
    }
  },
  mounted () {
    const { agreementId } = this.urlParams.row
    if (agreementId) {
      this.agreementId = agreementId
      this.getFormDetail()
    }
  },
  methods: {
    fileSuccess () {
      this.getFormDetail()
    },
    async getFormDetail () {
      const response = await centralHttp.getJcAgreementInfo({
        agreementId: this.agreementId
      })
      if (response.data) {
        const { sccSouJcAgreementInfoList = [], ...rest } = response.data
        const materialIds = sccSouJcAgreementInfoList.map(item => item.materialId)
        const res = await this.$http({
          url: '/api-base/material/materialItem/ext/multilingual',
          method: 'POST',
          data: { materialIds, language: this.$i18n.locale },
          loading: true
        })
        const infoList = sccSouJcAgreementInfoList.map(item => {
          const data = res.data.find(it => it.materialId === item.materialId)
          return {
            ...item,
            materialNameShow: data?.materialName,
            extMaterialModelShow: data?.extMaterialModel
          }
        })
        
        this.form = { ...rest, sccSouJcAgreementInfoList: infoList }
        const { sccSouJcAgreementOrgList = [], supplyArea } = this.form
        this.form.buyOrgId = sccSouJcAgreementOrgList.map(item => item.buyOrgId)
        if (supplyArea) {
          this.form.supplyArea = supplyArea.split(',')
        } else {
          this.form.supplyArea = []
        }
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      if (params.supplyArea && Array.isArray(params.supplyArea)) {
        params.supplyArea = params.supplyArea.join(',')
      }
      if (params.sccSouJcAgreementInfoList && params.sccSouJcAgreementInfoList.length) {
        params.sccSouJcAgreementInfoList.forEach((item, index) => { item.materialLine = index + 1 })
      }
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
          return
        }
        // 协议信息的校验
        let valid = false
        let vaildErr = ''
        this.form.sccSouJcAgreementInfoList.some(item => {
          if (!item.materialCode || !item.extCurrency || isNull(item.taxRate) || isNull(item.priceTax) || isNull(item.referencePrice) || isNull(item.leadTime) || isNull(item.startNum) || !item.extIsPrepaid) {
            valid = true
            // 请维护表格必填项
            vaildErr = this.$t('cusEntry.supplement20250121.tableRequirdTips')
            return true
          }
          if (item.extIsPrepaid === 'Y' && isNull(item.extPrepaidRatio)) {
            valid = true
            // 是否预付为是时，预付比例必填
            vaildErr = this.$t('cusEntry.supplement20250121.prepaidRatioTips')
            return true
          }
        })
        if (valid) {
          return this.__jump_error__('agreeInfo', null, vaildErr)
        }
      }
      const response = await centralHttp.saveOrUpdateJcAgreement(params)
      if (response.data) {
        this.form = response.data
        this.agreementId = this.form.agreementId
        this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail()
        if (type === 'SUBMIT') {
          this.changeStatus('EXECUTE', this.agreementId).then(() => {
            this.back()
          })
        }
      }
    },
    changeStatus (status, agreementId) {
      return new Promise((resolve) => {
        centralHttp.changeStatus({
          agreementId,
          operationType: status
        }).then(response => {
          if (response) {
            resolve()
          }
        })
      })
    },
    async saveChangeBill () {
      let params = this.initParams()
      const validForm = await this.validBill()
      if (!validForm) {
        this.__focus_error__()
        return
      }
      // TODO 协议信息的校验
      const response = await centralHttp.changeJcAgreement(params)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.back()
      }
    },
    getChangeRecords () {
      const tab = {
        component: VersionRecord,
        params: {
          row: {
            agreementId: this.agreementId,
            tabName: 'versionRecord' + this.agreementId
          }
        },
        title: this.$t('cusEntry.supplement20250121.versionRecord'), // 版本记录
        name: 'versionRecord' + this.agreementId
      }
      this.$emit('tab-add', tab)
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('ContractAgreeList.getQueryData')
    }
  }
}
</script>
