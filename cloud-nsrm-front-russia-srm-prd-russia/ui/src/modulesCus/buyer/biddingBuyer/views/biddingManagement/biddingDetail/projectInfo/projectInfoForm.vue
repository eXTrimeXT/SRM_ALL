<template>
  <SrmRow>
    <!--招标项目编号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidingNumCla')" prop="extProjectNo">
        <el-input v-model="biddingBaseInfo.extProjectNo" disabled />
      </el-form-item>
    </SrmCol>
    <!--项目名称-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="biddingBaseInfo.souName" />
      </el-form-item>
    </SrmCol>
    <!--招标地点-->
    <!-- <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.souPlace')" prop="orderSite">
        <el-input v-model="biddingBaseInfo.orderSite" />
      </el-form-item>
    </SrmCol> -->
    <!-- 板块 -->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.common.plate')" prop="extOrgBuName">
        <el-input v-model="biddingBaseInfo.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <!-- 公司 -->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.common.company')" prop="extOrgOuName">
        <el-input v-model="biddingBaseInfo.extOrgOuName" disabled />
      </el-form-item>
    </SrmCol>
    <!-- 招标流程 -->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.biddingSettings.bidProcess')" prop="extSouProcess">
        <DictSelect
          v-model="biddingBaseInfo.extSouProcess"
          code="SOU_BID_PROCCESS"
          :disabled="!biddingBaseInfo.mergeFlag"
          @change="extSouProcessChange"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.biddingType')" prop="publishScope">
        <DictSelect
          v-model="biddingBaseInfo.publishScope"
          code="SOU_PUBLISH_SCOPE"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.bidMode')" prop="extSouMode">
        <DictSelect
          v-model="biddingBaseInfo.extSouMode"
          code="SOU_BID_SOU_MODE"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.evaluateMethod')" prop="extScoreRule">
        <DictSelect
          v-model="biddingBaseInfo.extScoreRule"
          code="SOU_BID_SCORE_RULE"
          :disabled="biddingBaseInfo.extSouProcess == 'INQUIRY'"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.investNum')" prop="extInvestNo">
        <el-input v-model="biddingBaseInfo.extInvestNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('common.category')" prop="extCategoryName">
        <el-input v-model="biddingBaseInfo.extCategoryName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.techResponer')" prop="extTechPrincipal">
        <el-input v-model="biddingBaseInfo.extTechPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item label="联系方式" prop="extTechPrincipalTel">
        <el-input v-model="biddingBaseInfo.extTechPrincipalTel" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.budget')" prop="extBudget">
        <el-input v-model="biddingBaseInfo.extBudget" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidingType')" prop="orderType">
        <DictSelect
          v-model="biddingBaseInfo.orderType"
          code="BID_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.scaleQuantity')" prop="extScaleQuantity">
        <el-input v-model="biddingBaseInfo.extScaleQuantity" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.applicant')" prop="extApplicant">
        <el-input v-model="biddingBaseInfo.extApplicant" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.applicationDep')" prop="extApplicantDepart">
        <el-input v-model="biddingBaseInfo.extApplicantDepart" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.bidderOrNot')" prop="extAssignEvaluator">
        <dict-select
          v-model="biddingBaseInfo.extAssignEvaluator"
          code="YES_OR_NO"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('common.creationDate')" prop="creationDate">
        <el-date-picker
          v-model="biddingBaseInfo.creationDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('bidMod.datePicker')"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.updateDate')" prop="lastUpdateDate">
        <el-date-picker
          v-model="biddingBaseInfo.lastUpdateDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('bidMod.datePicker')"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="3">
      <el-form-item label="招标书审批人" prop="approveFullName">
        <QuickSearch
          :show-input="biddingBaseInfo.approveFullName"
          show-key="username"
          :scope-data="biddingBaseInfo"
          name="scc_rbac_user_display"
          :disabled="readOnly"
          @close-quicksearch="getUserObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="biddingBaseInfo.projectStatus==='ABANDON'" :init-col="1">
      <el-form-item label="废弃说明" prop="cancelReason">
        <el-input v-model="biddingBaseInfo.cancelReason" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="biddingBaseInfo.mergeFlag" :init-col="1" style="margin: 10px 0;">
      <span style="color: #161C24">合并申请单号：</span>
      <span
        v-for="(item,index) in applicantNoList"
        :key="index"
        style="cursor: pointer; color: #0077ff; margin-right: 5px;"
        @click="applyNoClick(item,index)"
      >
        {{ item }}
      </span>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 项目信息
 */
import QuickSearch from 'lib@/components/QuickSearch'
import purchaseApplicationDetail2 from 'modcb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'

export default {
  name: 'ProjectInfo',
  components: {
    QuickSearch
  },
  props: {
    biddingBase: {
      type: Object,
      required: true
    },
    processList: {
      type: Array,
      default: () => []
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    },
    applicantNoList () {
      if (this.biddingBaseInfo.applicantNo) {
        return this.biddingBaseInfo.applicantNo.toString().split(';')
      }
      return []
    }
  },

  methods: {
    // 招标流程选择 询比价招标类型，评分规则默认合理低价法，不可修改。
    extSouProcessChange (val) {
      this.biddingBaseInfo.extScoreRule = val == 'INQUIRY' ? 'LOW_PRICE' : ''
    },
    // 选择招标书审批人
    getUserObj (val, scope) {
      scope.approveUserId = val ? val.userId : ''
      scope.approveUserName = val ? val.username : ''
      scope.approveFullName = val ? val.nickname : ''
    },
    // 跳转至招标申请单
    applyNoClick (item, index) {
      if (this.biddingBaseInfo.applicantId) {
        let curId = this.biddingBaseInfo.applicantId.toString().split(';')[index]
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
    },
    /* 获取流程配置 */
    processConfigIdChange (val) {
      if (val) {
        // 设置左边菜单节点信息
        this.$emit('set-menu-config', val, 'add')

        const processData = this.processList.find(item => item.processConfigId === val)
        console.log(processData)

        if (processData) {
          this.$set(this.biddingBaseInfo, 'publishScope', processData.publishScope)
          this.$set(this.biddingBaseInfo, 'bargainType', processData.bargainType)
          this.$set(this.biddingBaseInfo, 'scoreRuleType', processData.scoreRuleType)

          this.$emit('clear-data', val)
        }
      }
    }
  }
}
</script>
