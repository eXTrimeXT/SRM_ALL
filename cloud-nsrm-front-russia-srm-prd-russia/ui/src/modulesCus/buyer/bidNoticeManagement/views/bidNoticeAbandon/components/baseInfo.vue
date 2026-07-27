<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="中/落标通知单号" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="板块" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公司" prop="extOrgOuName">
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
      <el-form-item label="需求部门" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="需求人" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="招标技术负责人" prop="extTechPrincipal">
        <el-input v-model="baseForm.extTechPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="联系电话" prop="extTechPhone">
        <el-input v-model="baseForm.extTechPhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="单据状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="SOU_CA_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="招标单号" prop="souNo">
        <el-input v-model="baseForm.souNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目名称" prop="souName">
        <el-input v-model="baseForm.souName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="招标项目编号" prop="extProjectNo">
        <el-input v-model="baseForm.extProjectNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建人" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="创建日期" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="最后更新日期" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="原中/落标通知单号" prop="originalBidNoticeNo">
        <el-input v-model="baseForm.originalBidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="废弃类型" prop="discardType" :rules="{required: true,message: '请填写'}">
        <DictSelect
          v-model="baseForm.discardType"
          code="SOU_ATN_TYPE"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="applicantNoList.length > 1" :init-col="1">
      <span style="font-size:14px;">合并申请单号：</span>
      <el-button v-for="(item,index) in applicantNoList" :key="index" type="text" @click="applyNoClick(item,index)">
        {{ item }}
      </el-button>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="备注" prop="remark">
        <el-input v-model="baseForm.remark" disabled :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="中落标取消原因" prop="abandonReason" :rules="{required: true,message: '请填写'}">
        <el-input v-model="baseForm.abandonReason" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.status==='ABANDONED'" :init-col="1">
      <el-form-item label="废弃说明" prop="discardReason">
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
