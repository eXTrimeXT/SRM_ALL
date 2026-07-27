<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <ApprovalProcess
        :business-id="workflowBusinessId"
        :business-type="bpmBusinessType"
        :approval-status="form.status"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <el-collapse-item title="调整申请单" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="附件信息" name="2">
              <FileInfo
                ref="fileInfo"
                :value="form.adjustAttList"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <Market
              v-if="form.adjustType == '2'"
              ref="market"
              style="margin-bottom: 20px"
              :form.sync="form"
              :readonly="disabledFlag"
              @fileSuccess="fileSuccess"
            />
          </el-collapse>
        </el-form>
      </ApprovalProcess>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import FileInfo from './components/fileInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { centralHttp, priceAdjustApply } from 'modcb@/jcAgreement/api'
import Market from './market'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'CentralizedAgreeDetail',
  components: {
    BaseInfo,
    CToolbar,
    Market,
    FileInfo,
    ApprovalProcess
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      adjustId: null,
      colValue: ['1', '2'],
      form: {
        jcCode: null,
        designId: null,
        adjustCode: null,
        adjustName: null,
        executeDateStart: null,
        executeDateEnd: null,
        adjustId: null,
        companyId: null,
        companyCode: null,
        companyName: null,
        agreementCode: null,
        agreementName: null,
        supplyArea: null,
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
        pricingWay: null,
        invoiceType: null,
        payWay: null,
        effectiveStartDate: null,
        effectiveEndDate: null,
        currencyType: null,
        agreementType: '集采协议',
        mixAmount: 0,
        agreementFileId: null,
        agreementFileName: null,
        introduce: null,
        sccSouJcAgreementOrgList: [],
        sccSouJcAgreementInfoList: [],
        vendorList: [],
        agreeList: [],
        adjustAttList: [] // 附件
      },
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
      bpmBusinessType: 'PRICE_ADJUST_APPLY', // 审批流程编码
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: { // 中标通知对应审批流状态
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'SUBMIT', // 已提交
        APPROVED: 'PASS', // 审批通过
        REJECTED: 'REJECT', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      }
    }
  },
  computed: {
    workflowBusinessId () { // 工作流单据ID
      return this.form ? this.form.adjustId : null
    },
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    }
  },
  watch: {
    'form.jcCode': {
      handler (val) {
        if (!val) {
          return false
        }

        // 请求协议详情表
        const obj = { 'pageNum': 1, 'pageSize': 10000, '__page': 1, '__pagesize': 10000, 'projectCode': val }
        this.$http({
          url: '/api-sou/price/adjustment/apply/getAgreementPageList',
          method: 'POST',
          data: obj,
          loading: true
        }).then((res) => {
          console.log(res, 'agreeList')
          this.$set(this.form, 'agreeList', res.data?.list)
        })
      },
      deep: true
    }
  },
  mounted () {
    const { adjustId } = this.urlParams.row
    if (adjustId) {
      this.adjustId = adjustId
      this.form = this.urlParams.row
      // this.getFormDetail()
    }
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let params = this.initParams()
      params.status = 'DRAFT'
      const validForm = await this.validBill()
      if (!validForm) {
        this.__focus_error__()
        return false
      }
      // TODO 协议信息的校验
      const response = await priceAdjustApply.save(params)
      this.form = response.data || {}
      this.adjustId = this.form.adjustId
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveBill('SAVE')
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    fileSuccess () {
      // 请求协议详情表
      const obj = { 'pageNum': 1, 'pageSize': 10000, '__page': 1, '__pagesize': 10000, 'projectCode': this.form.jcCode }
      this.$http({
        url: '/api-sou/price/adjustment/apply/getAgreementPageList',
        method: 'POST',
        data: obj,
        loading: true
      }).then((res) => {
        console.log(res, 'agreeList')
        this.form.agreeList = res.data?.list
      })
    },
    async getFormDetail () {
      this.form = this.urlParams.row
      // const response = await centralHttp.getJcAgreementInfo({
      //   adjustId: this.adjustId
      // })
      // if (response.data) {
      //   this.form = response.data
      //   const { sccSouJcAgreementOrgList = [] } = this.form
      //   this.form.buyOrgId = sccSouJcAgreementOrgList.map(item => item.buyOrgId)
      // }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      params.adjustAttList = this.$refs.fileInfo.getParams()
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
      params.status = 'DRAFT'
      if (type === 'SUBMIT') {
        params.status = 'SUBMIT'
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
        }

        // TODO 协议信息的校验
      }
      const response = await priceAdjustApply.save(params)
      if (response.data) {
        this.form = response.data
        this.adjustId = this.form.adjustId
        console.log(this.form, 'form')
        this.$message.success(this.$t('common.successSave'))

        if (type === 'SUBMIT') {
          // if (this.form?.adjustType == '2'){
          //   let tab = {
          //     component: Market,
          //     params: {
          //       flag: 'edit',
          //       row: this.form,
          //       tabName: 'market'
          //     },
          //     title: '市场行情',
          //     name: 'market'
          //   }
          //   this.$emit('tab-add', tab)
          // }
          this.back()
        }
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
