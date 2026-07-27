<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="协议编号" prop="agreementCode">
        <el-input v-model="baseForm.agreementCode" :disabled="readonly || isChange" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公司主体" prop="companyName">
        <QuickSearch
          :disabled="readonly || isChange"
          :showInput="baseForm.companyName"
          show-key="organizationName"
          :scope-data="baseForm"
          name="scc_base_organization_invoice"
          dialogLabel="开票主体"
          @close-quicksearch="getCompanyObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="协议名称" prop="agreementName">
        <el-input v-model="baseForm.agreementName" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="供应区域" prop="supplyArea">
        <DictSelect
          v-model="baseForm.supplyArea"
          :disabled="readonly || isChange"
          code="REGION"
          multiple
          collapse-tags
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="mode === 'central'" :init-col="4">
      <el-form-item label="是否默认全组织" prop="defaultAll">
        <el-checkbox
          v-model="baseForm.defaultAll"
          style="width:100%"
          disabled
          true-label="Y"
          false-label="N"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="mode === 'contract'" :init-col="4">
      <el-form-item label="采购组织" prop="buyOrgId">
        <OrganizationSelector
          ref="organizationSelector"
          v-model="baseForm.buyOrgId"
          :parent-id="-1"
          node-type="OU"
          multiple
          :disabled="readonly"
          @select="orgSelect"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="付款条款" prop="payment">
        <DictSelect
          v-model="baseForm.payment"
          :disabled="readonly"
          code="PAYMENT_PROVISION"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="供应商" prop="supName">
        <QuickSearch
          :disabled="readonly || isChange"
          :showInput="baseForm.supName"
          show-key="companyName"
          :scope-data="baseForm"
          name="scc_sup_company_info_display"
          @close-quicksearch="getSupObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="交易方式" prop="trading">
        <DictSelect
          v-model="baseForm.trading"
          :disabled="readonly"
          code="PAY_WAY"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="采购员" prop="buyPersonName">
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.buyPersonName"
          show-key="nickname"
          :scope-data="baseForm"
          name="scc_rbac_user_display"
          @close-quicksearch="getBuyerPersonObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="定价方式" prop="pricingWay">
        <DictSelect
          v-model="baseForm.pricingWay"
          code="PRICING_WAY"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="发票类型" prop="invoiceType">
        <DictSelect
          v-model="baseForm.invoiceType"
          code="EXT_SOU_PURINQ_ORDER_INVOICE_TYPE"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="付款方式" prop="payWay">
        <DictSelect
          v-model="baseForm.payWay"
          code="JC_PAYMENT_WAY"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="有效日期从" prop="effectiveStartDate">
        <el-date-picker
          v-model="baseForm.effectiveStartDate"
          type="datetime"
          :disabled="readonly || isChange"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
          default-time="00:00:01"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="有效日期至" prop="effectiveEndDate">
        <el-date-picker
          v-model="baseForm.effectiveEndDate"
          type="datetime"
          :disabled="readonly || isChange"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
          default-time="23:59:59"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="币种" prop="currencyType">
        <DictSelect
          v-model="baseForm.currencyType"
          code="currency"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="协议类型" prop="agreementType">
        <el-input v-model="baseForm.agreementType" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="最小起订金额" prop="mixAmount">
        <el-input v-model="baseForm.mixAmount" v-input-format="{type:'number',negative:false}" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="协议附件" prop="agreementFileName">
        <SrmCommonFile
          :extra-data="fileInfo"
          :disabled="readonly"
          :default-file="{
            fileId: baseForm.agreementFileId,
            fileName: baseForm.agreementFileName
          }"
          @on-change="({file}) => handleUploadSuccess(file)"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="备注" prop="remark">
        <el-input v-model="baseForm.remark" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    isChange: {
      type: Boolean,
      default: false
    },
    // central:集采协议;contract:合同协议
    mode: {
      type: String,
      default: 'central',
      validator (value) {
        return ['central', 'contract'].includes(value)
      }
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'sou',
        fileFunction: 'centralizedAgree',
        fileType: 'images'
      }
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  },
  methods: {
    // 公司主体；快查
    async getCompanyObj (val, scope) {
      this.baseForm.companyId = val ? val.organizationId : null
      this.baseForm.companyCode = val ? val.organizationCode : null
      this.baseForm.companyName = val ? val.organizationName : null
    },
    // 采购组织选择
    orgSelect (node, value, scope) {
      console.log('node', node)
      if (node.length) {
        this.baseForm.sccSouJcAgreementOrgList = node.map(item => ({
          buyOrgId: item.organizationId,
          buyOrgCode: item.organizationCode,
          buyOrgName: item.organizationName
        }))
      }
    },
    // 供应商快查
    getSupObj (val, scope) {
      this.baseForm.supId = val ? val.companyId : null
      this.baseForm.supCode = val ? val.companyCode : null
      this.baseForm.supName = val ? val.companyName : null
    },
    // 采购员快查
    getBuyerPersonObj (val, scope) {
      this.baseForm.buyPersonId = val ? val.userId : null
      this.baseForm.buyPersonCode = val ? val.username : null
      this.baseForm.buyPersonName = val ? val.nickname : null
    },
    // 协议附件上传
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      this.baseForm.agreementFileId = fileId.toString()
      this.baseForm.agreementFileName = fileName
    }
  }
}
</script>
