<template>
  <SrmRow>
    <SrmCol v-if="mode === 'expertApply'" :init-col="4">
      <el-form-item label="数据来源" prop="applyFromType">
        <DictSelect
          v-model="baseForm.applyFromType"
          code="EXT_SOU_EXPERT_APPLY_FROM_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="申请编号" prop="expertApplyNo">
        <el-input v-model="baseForm.expertApplyNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="申请人" prop="applyByNickname">
        <el-input v-model="baseForm.applyByNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="单据状态" prop="applyStatus">
        <DictSelect
          v-model="baseForm.applyStatus"
          code="EXT_SOU_EXPERT_APPLY_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="工号" prop="applyBy">
        <QuickSearch
          v-if="mode === 'expertApply'"
          :disabled="readonly"
          :showInput="baseForm.applyBy"
          show-key="ceeaEmpNo"
          :scope-data="baseForm"
          name="scc_rbac_user_display"
          @close-quicksearch="getExpertByQuick"
        />
        <el-input v-else v-model="baseForm.applyBy" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="姓名" prop="applyByNickname">
        <el-input v-model="baseForm.applyByNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="最高学历" prop="highestDegree">
        <DictSelect v-model="baseForm.highestDegree" code="EXT_SOU_EXPERT_EDUCATION" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 取学历表 -->
      <el-form-item label="毕业时间" prop="studyDateTo">
        <el-date-picker
          v-model="baseForm.studyDateTo"
          type="date"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="性别" prop="sex">
        <DictSelect
          v-model="baseForm.sex"
          code="EXT_SOU_EXPERT_SEX"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="板块" prop="buName">
        <el-input v-model="baseForm.buName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="所属公司" prop="orgOuId">
        <OrganizationSelector
          ref="organizationSelector"
          v-model="baseForm.orgOuId"
          :parent-id="-1"
          node-type="OU"
          :placeholder="$t('common.pleaseSelect')"
          :disabled="readonly"
          @select="selectHandler"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="部门/科室" prop="departmentName">
        <el-select
          v-model="baseForm.departmentName"
          filterable
          :disabled="readonly"
          @change="setUserObj"
        >
          <el-option
            v-for="item in bumenList"
            :key="item.organizationId"
            :label="item.organizationName"
            :value="item.organizationName"
          />
        </el-select>
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="职务" prop="job">
        <el-input v-model="baseForm.job" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="序列等级" prop="jobRank">
        <el-input v-model="baseForm.jobRank" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="在职状态" prop="jobStatus">
        <DictSelect
          v-model="baseForm.jobStatus"
          code="EXT_SOU_EXPERT_JOB_STATUS"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model="baseForm.phone" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="入厂时间" prop="hireDate">
        <el-date-picker
          v-model="baseForm.hireDate"
          type="date"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 取学历表 -->
      <el-form-item label="毕业院校" prop="studyCollege">
        <el-input v-model="baseForm.studyCollege" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="所学专业" prop="major">
        <el-input v-model="baseForm.major" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="申报等级" prop="applyLevel">
        <DictSelect
          v-model="baseForm.applyLevel"
          code="EXT_SOU_EXPERT_LEVEL"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="mode === 'expertUpgrade'" :init-col="1">
      <el-form-item label="申请说明" prop="upgradeReason">
        <el-input v-model="baseForm.upgradeReason" type="textarea" :autosize="{minRows:3,maxRows:5}" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { expInfoHttp } from '../../../api'
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
    mode: { // 自主申请 expertInfo  升级申请 expertUpgrade  绿色通道 expertApply 专家变更 CHANGE
      type: String,
      default: 'expertInfo'
    }
  },
  data () {
    return {
      bumenList: []
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
    setUserObj () {
      const val = this.bumenList.find(v => v.organizationName === this.baseForm.departmentName) || {}
      this.baseForm.departmentId = val.organizationId
      this.baseForm.departmentName = val.organizationName
    },
    async selectHandler (node, value, scope) {
      this.baseForm.orgOuId = node ? node.organizationId : null
      this.baseForm.orgOuCode = node ? node.organizationCode : null
      this.baseForm.orgOuName = node ? node.organizationName : null
      // 切换的时候清空申请部门
      this.baseForm.departmentId = null
      this.baseForm.departmentName = null
      // 板块
      if (node) {
        this.bumenListFun()
        const { data } = await expInfoHttp.getBuOrganizationByOuOrgCode({ organizationCode: this.baseForm.orgOuCode })
        this.baseForm.buName = data.organizationName
        this.baseForm.buId = data.organizationId
        this.baseForm.buCode = data.organizationCode
      } else {
        this.bumenList = []
        this.baseForm.buId = null
        this.baseForm.buCode = null
        this.baseForm.buName = null
      }
    },
    bumenListFun () {
      const saveData = {
        parentId: this.form.orgOuId,
        type: 'DEP'
      }
      this.$http({
        url: '/api-base/orgQuery/getSubOrgs',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.bumenList = res.data || []
      })
    },
    async getExpertByQuick (val, scope) {
      this.baseForm.applyById = val ? val.userId : null
      this.baseForm.applyBy = val ? val.username : null
      this.baseForm.applyByNickname = val ? val.nickname : null
      this.baseForm.applyByCode = val ? val.ceeaEmpNo : null // 工号
      this.baseForm.phone = val ? val.phone : null
      if (val && val.username) {
        const response = await expInfoHttp.getHrUserInfo({ personnelNo: val.username })
        let result = response.data || {}
        this.baseForm.highestDegree = result.diploma || null // 最高学历
        this.baseForm.sex = result.sex?.toString() || null // 性别
        this.baseForm.job = result.dutyName || null // 职务
        this.baseForm.jobRank = result.rankName || null // 序列等级
        this.baseForm.jobStatus = result.state?.toString() || null // 在职状态
        this.baseForm.hireDate = result.admissionDate || null // 入厂时间
        this.baseForm.studyDateTo = result.graduateTime || null // 毕业时间
        this.baseForm.studyCollege = result.graduateSchool || null // 毕业院校
        this.baseForm.major = result.professional || null // 所学专业

        const { data } = await expInfoHttp.getHrUserOrgnizationByUsername({ username: val.username })
        this.baseForm.orgOuId = data.ouOrganization?.organizationId // 所属单位
        this.baseForm.orgOuCode = data.ouOrganization?.organizationCode
        this.baseForm.orgOuName = data.ouOrganization?.organizationName
        this.baseForm.departmentId = data.departmentOrganization?.organizationId // 科室/部门
        this.baseForm.departmentName = data.departmentOrganization?.organizationName
        this.baseForm.buId = data.buOrganization?.organizationId // 板块
        this.baseForm.buCode = data.buOrganization?.organizationCode
        this.baseForm.buName = data.buOrganization?.organizationName
        this.baseForm.orgOuId && this.bumenListFun()
      } else {
        this.baseForm.highestDegree = null // 最高学历
        this.baseForm.sex = null // 性别
        this.baseForm.job = null // 职务
        this.baseForm.jobRank = null // 序列等级
        this.baseForm.jobStatus = null // 在职状态
        this.baseForm.hireDate = null // 入厂时间
        this.baseForm.studyDateTo = null // 毕业时间
        this.baseForm.studyCollege = null // 毕业院校
        this.baseForm.major = null // 所学专业
        this.baseForm.buId = null // 板块
        this.baseForm.buCode = null
        this.baseForm.buName = null
        this.baseForm.orgOuId = null // 所属单位
        this.baseForm.orgOuCode = null
        this.baseForm.orgOuName = null
        this.baseForm.departmentId = null // 科室/部门
        this.baseForm.departmentName = null
      }
    }
  }
}
</script>
