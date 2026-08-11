<template>
  <SrmRow>
    <!-- <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="定标申请单" prop="caNo">
        <el-input v-model="baseForm.caNo" disabled />
      </el-form-item>
    </SrmCol> -->
    <SrmCol :init-col="4">
      <!-- <el-form-item label="板块" prop="extOrgBuName"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="公司" prop="extOrgOuName"> -->
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
      <!-- <el-form-item label="需求部门" prop="demandDepartmentName"> -->
      <el-form-item :label="$t('purchaseDemand.requirementDepartment')" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <!-- <SrmCol :init-col="4">
      <el-form-item label="合同经办人" prop="contractOperatorNickname">
        <QuickSearch
          :show-input="baseForm.contractOperatorNickname"
          :scope-data="baseForm"
          show-key="nickname"
          name="scc_rbac_user_display"
          :disabled="readonly"
          @close-quicksearch="getUserObj"
        />
      </el-form-item>
    </SrmCol> -->
    <SrmCol :init-col="4">
      <!-- <el-form-item label="创建人" prop="createdFullName"> -->
      <el-form-item :label="$t('common.creator')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="第一层级审批人" prop="approvalNickname"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.firstLevelApprover')" prop="approvalNickname">
        <QuickSearch
          :show-input="baseForm.approvalNickname"
          :scope-data="baseForm"
          show-key="nickname"
          name="scc_rbac_user_display"
          :disabled="readonly"
          @close-quicksearch="getUserObj1"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="报价轮次" prop="caRound"> -->
      <el-form-item :label="$t('bidMod.preformround')" prop="caRound">
        <DictSelect
          v-model="baseForm.caRound"
          code="NPM_CA_PRICE_ROUND"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.extSouProcess !== 'INQUIRY'" :init-col="4">
      <!-- <el-form-item label="技术评分结果"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.technicalScoreResult')">
        <el-button style="width:100%" type="ghost" @click="viewScoreDetail">
          <!-- 查看 -->
          {{ $t("common.view") }}
        </el-button>
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="备注" prop="remark"> -->
      <el-form-item :label="$t('components.eio.headers.remark')" prop="remark">
        <el-input
          v-model="baseForm.remark"
          :disabled="readonly"
          :maxlength="3000"
          :autosize="{ minRows: 2.5, maxRows: 6}"
          type="textarea"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import TechScoreDetail from 'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/techManagement/techScoreDetail'
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
    },
    flag: {
      type: String,
      default: ''
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
    }
  },
  methods: {
    orgSelect (node, value, scope) {
      this.baseForm.extOrgOuId = node ? node.organizationId : null
      this.baseForm.extOrgOuCode = node ? node.organizationCode : null
      this.baseForm.extOrgOuName = node ? node.organizationName : null
    },
    getUserObj (val) {
      this.baseForm.contractOperatorUserId = val ? val.userId : null
      this.baseForm.contractOperatorUsername = val ? val.username : null
      this.baseForm.contractOperatorNickname = val ? val.nickname : null
    },
    getUserObj1 (val) {
      this.baseForm.approvalUserId = val ? val.userId : null
      this.baseForm.approvalUserName = val ? val.username : null
      this.baseForm.approvalNickname = val ? val.nickname : null
    },
    viewScoreDetail () {
      if (!this.baseForm.projectId) return
      if (this.flag === 'approval') {
        // const str = encodeURI(`from=fromFun&funName=bidTechScoreDetail&formId=${this.baseForm.projectId}&groupId=${this.baseForm.groupId}&souName=${this.baseForm.souName}`)
        // const encodeStr = btoa(str)
        // const pathname = window.location.pathname
        // const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
        // window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
        this.$router.push({
          name: 'flowTaskView',
          query: {
            from: 'fromFun',
            funName: 'bidTechScoreDetail',
            formId: this.baseForm.projectId,
            groupId: this.baseForm.groupId,
            souName: this.baseForm.souName,
            status: '1' // 待审批状态，2：起草，1：执行中
          }
        })
      } else {
        let tab = {
          component: TechScoreDetail,
          params: {
            editRow: {
              projectId: this.baseForm.projectId,
              groupId: this.baseForm.groupId,
              souName: this.baseForm.souName
            },
            tabName: `TechScoreDetail${this.baseForm.projectId}${this.baseForm.groupId}`
          },
          title: this.baseForm.souName + '-' + this.$t('cusEntry.bidMod.techScoreDetail'), // 评分详情
          name: `TechScoreDetail${this.baseForm.projectId}${this.baseForm.groupId}`
        }
        this.$emit('tab-add', tab)
      }
    }
  }
}
</script>
