<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 中/落标通知单号 -->
      <el-form-item :label="this.$t('cusEntry.supplement20250205.bidNoticeNo')" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 板块 -->
      <el-form-item :label="this.$t('cusEntry.common.plate')" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 公司 -->
      <el-form-item :label="this.$t('components.organization.COMPANY')" prop="extOrgOuName">
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
      <el-form-item :label="this.$t('purchaseDemand.requirementDepartment')" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 需求人 -->
      <el-form-item :label="this.$t('cusEntry.supplement20250121.reqUserName')" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 招标技术专家 -->
      <el-form-item :label="this.$t('cusEntry.supplement20250205.techPrincipal')" prop="extTechPrincipal">
        <el-input v-model="baseForm.extTechPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 联系电话 -->
      <el-form-item :label="this.$t('orderMod.buyerOrderSynergy.linkPhone')" prop="extTechPhone">
        <el-input v-model="baseForm.extTechPhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 单据状态 -->
      <el-form-item :label="this.$t('bidMod.billstatus')" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="SOU_TN_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 招标单号 -->
      <el-form-item :label="this.$t('logisticsMod.bidingCode')" prop="souNo">
        <el-input v-model="baseForm.souNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 项目名称 -->
      <el-form-item :label="this.$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="baseForm.souName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 招标项目编号 -->
      <el-form-item :label="this.$t('bidMod.bidingNumCla')" prop="extProjectNo">
        <el-input v-model="baseForm.extProjectNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 创建人 -->
      <el-form-item :label="this.$t('common.createdFullName')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <!-- 创建日期 -->
      <el-form-item :label="this.$t('common.creationDate')" prop="creationDate">
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
      <el-form-item :label="this.$t('common.lastUpdateDate2')" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <!-- <SrmCol v-if="applicantNoList.length > 1" :init-col="1">
      <span style="font-size:14px;">合并申请单号：</span>
      <el-button v-for="(item,index) in applicantNoList" :key="index" type="text" @click="applyNoClick(item,index)">
        {{ item }}
      </el-button>
    </SrmCol> -->
    <SrmCol :init-col="4">
      <el-form-item>
        <!-- 查看定标申请 -->
        <el-button
          type="text"
          @click="readFixedBid"
        >
          {{ $t('cusEntry.supplement20250205.readFixedBid') }}
        </el-button>
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item>
        <!-- 查看招标流程 -->
        <el-button
          type="text"
          @click="readBidProcess"
        >
          {{ $t('cusEntry.supplement20250205.readBidProcess') }}
        </el-button>
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 备注 -->
      <el-form-item :label="this.$t('common.remark')" prop="remark">
        <el-input v-model="baseForm.remark" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.status==='ABANDONED'" :init-col="1">
      <!-- 废弃说明 -->
      <el-form-item :label="this.$t('cusEntry.supplement20250121.reasonDesc')" prop="discardReason">
        <el-input v-model="baseForm.discardReason" disabled />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'
import CalibrationApplyDetail from '@/modulesCus/buyer/caManagement/views/calibrationApply/edit'
import biddingDetail from '@/modulesCus/buyer/biddingBuyer/views/biddingManagement/biddingDetail'
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
    // 是否第三方跳转
    isThirdParty: {
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
    // 查看定标申请
    readFixedBid () {
      // if (this.isThirdParty) {
      //   const str = encodeURI(`from=fromFun&funName=SOU_CA&formId=${this.baseForm.caId}`)
      //   const encodeStr = btoa(str)
      //   const pathname = window.location.pathname
      //   const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
      //   window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      // } else {
      //   const row = this.baseForm
      //   this.$emit('tab-add', {
      //     component: CalibrationApplyDetail,
      //     params: {
      //       flag: 'view',
      //       row,
      //       tabName: 'calibrationApply' + row.caNo
      //     },
      //     title: this.$t('cusEntry.supplement20250205.readFixedBid') + (row.caNo || ''),
      //     name: 'calibrationApply' + row.caNo
      //   })
      // }
      this.$router.push({
        name: 'calibrationApply',
        params: {
          from: 'bidNotice',
          row: this.baseForm,
          flag: 'view'
        }
      })
    },
    // 查看招标流程
    readBidProcess () {
      // if (this.isThirdParty) {
      //   const str = encodeURI(`from=fromFun&funName=BID_PROCESS&formId=${this.baseForm.projectId}`)
      //   const encodeStr = btoa(str)
      //   const pathname = window.location.pathname
      //   const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
      //   window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      // } else {
      //   const row = this.baseForm
      //   this.$emit('tab-add', {
      //     component: biddingDetail,
      //     params: {
      //       flag: 'edit',
      //       row: row,
      //       tabName: `biddingDetail${row.extProjectNo}`
      //     },
      //     title: row.extProjectNo || row.souNo,
      //     name: `biddingDetail${row.extProjectNo}`
      //   })
      // }
      this.$router.push({
        name: 'biddingManagementNew',
        params: {
          from: 'bidNotice',
          row: this.baseForm,
          flag: 'edit'
        }
      })
    },
    orgSelect (node, value, scope) {
      this.form.extOrgOuId = node ? node.organizationId : null
      this.form.extOrgOuCode = node ? node.organizationCode : null
      this.form.extOrgOuName = node ? node.organizationName : null
    },
    /** 申请单号单击 */
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
