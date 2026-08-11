<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item :label="this.$t('cusEntry.supplement20250205.bidNoticeNo')" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 板块 -->
      <el-form-item :label="$t('cusEntry.common.plate')" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 公司 -->
      <el-form-item :label="$t('components.organization.COMPANY')" prop="extOrgOuName">
        <el-input v-model="baseForm.extOrgOuName" disabled />
        <!-- <OrganizationSelector
          v-model="baseForm.extOrgOuId"
          :scope="baseForm"
          node-type="OU"
          :parent-id="-1"
          :placeholder="$t('common.pleaseSelect')"
          disabled
          @select="orgSelect"
        /> -->
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 需求部门 -->
      <el-form-item :label="$t('purchaseDemand.requirementDepartment')" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 需求人 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.reqUserName')" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 招标技术专家 -->
      <el-form-item :label="$t('cusEntry.supplement20250205.techPrincipal')" prop="extTechPrincipal">
        <el-input v-model="baseForm.extTechPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 联系电话 -->
      <el-form-item :label="$t('orderMod.buyerOrderSynergy.linkPhone')" prop="extTechPhone">
        <el-input v-model="baseForm.extTechPhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 单据状态 -->
      <el-form-item :label="$t('bidMod.billstatus')" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="SOU_CA_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 招标单号 -->
      <el-form-item :label="$t('logisticsMod.bidingCode')" prop="souNo">
        <el-input v-model="baseForm.souNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 项目名称 -->
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="baseForm.souName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 招标项目编号 -->
      <el-form-item :label="$t('bidMod.bidingNumCla')" prop="extProjectNo">
        <el-input v-model="baseForm.extProjectNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 创建人 -->
      <el-form-item :label="$t('common.createdFullName')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 创建日期 -->
      <el-form-item :label="$t('common.creationDate')" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 最后更新日期 -->
      <el-form-item :label="$t('common.lastUpdateDate2')" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 原中/落标通知单号 -->
      <el-form-item :label="$t('cusEntry.supplement20250205.oldBidNoticeNo')" prop="originalBidNoticeNo">
        <el-input v-model="baseForm.originalBidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 废弃类型 -->
      <el-form-item :label="$t('bidMod.billType')" prop="discardType" :rules="{required: true,message: $t('common.requiredField')}">
        <DictSelect
          v-model="baseForm.discardType"
          code="SOU_ATN_TYPE"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 备注 -->
      <el-form-item :label="$t('common.remark')" prop="remark">
        <el-input v-model="baseForm.remark" disabled :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 中落标取消原因 -->
      <el-form-item :label="$t('cusEntry.supplement20250205.abandonReason')" prop="abandonReason" :rules="{required: true,message: $t('common.requiredField')}">
        <el-input v-model="baseForm.abandonReason" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.status==='ABANDONED'" :init-col="1">
      <!-- 废弃说明 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.reasonDesc')" prop="discardReason">
        <el-input v-model="baseForm.discardReason" disabled />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'

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
    approvalFlag: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {

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
    },
    applicantNoList () {
      let applicantNo = this.baseForm.applicantNo
      if (applicantNo) {
        return applicantNo.toString().split(';')
      }
      return []
    }
  },
  methods: {
    orgSelect (node, value, scope) {
      this.form.extOrgOuId = node ? node.organizationId : null
      this.form.extOrgOuCode = node ? node.organizationCode : null
      this.form.extOrgOuName = node ? node.organizationName : null
    },
    /* 申请单号单击 */
    applyNoClick (item, index) {
      if (this.form.applicantId) {
        let curId = this.form.applicantId.toString().split(';')[index]
        const row = {
          requirementHeadNum: item,
          requirementHeadId: curId
        }
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
          params: {
            flag: 'approveNumber',
            row: row,
            showType: 'readOnly',
            tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
            activeWorkflowTab: false
          },
          title: row.requirementHeadNum,
          name: 'purchaseApplicationDetail' + row.requirementHeadNum
        })
      }
    }
  }
}
</script>
