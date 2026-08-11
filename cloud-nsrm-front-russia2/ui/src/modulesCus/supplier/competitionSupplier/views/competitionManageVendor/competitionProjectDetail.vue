<template>
  <el-container class="flex-container the_inquiryVendorBiddingDetail_wrapper" direction="vertical">
    <el-main>
      <el-tabs
        v-model="activeTab"
        type="border-card"
      >
        <!--基础信息-->
        <el-tab-pane
          :label="$t('vendorMod.companyBaseInfo2')"
          name="projectInformation"
          lazy
        >
          <ProjectInformation
            :base-info="baseInfo"
            :currency-list="currencyList"
            :file-config-list="fileConfigList"
          />
        </el-tab-pane>

        <!--竞价需求-->
        
        <el-tab-pane
          :label="$t('competition.requireInfo')"
          name="projectRequirements"
          lazy
        >
          <ProjectRequirements :base-info="baseInfo" :is-current-active-tab="activeTab === 'projectRequirements'" />
        </el-tab-pane>

        <!--报名信息-->
        <el-tab-pane
          :label="$t('vendorMod.inforRegistration')"
          name="applyInfo"
          lazy
        >
          <ApplyInfo :is-current-active-tab="activeTab === 'applyInfo'" />
        </el-tab-pane>

        <!--报价明细-->
        <el-tab-pane
          :label="$t('bidMod.quoteDetails')"
          name="quoteInfo"
          lazy
        >
          <QuoteInfo :base-info="baseInfo" :is-current-active-tab="activeTab === 'quoteInfo'" />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import { carVendorHttp } from 'modcs@/competitionSupplier/api'
import ProjectInformation from './competitionProjectDetail/projectInformation'
import ProjectRequirements from './competitionProjectDetail/projectRequirements'
import ApplyInfo from './competitionProjectDetail/applyInfo'
import QuoteInfo from './competitionProjectDetail/quoteInfo'
import CompetitionHall from './competitionProjectDetail/competitionHall'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'CompetitionProjectDetail',

  provide () {
    return {
      attrsParamsRow: {
        projectId: (this.$attrs.params.row || {}).projectId,
        vendorId: (this.$attrs.params.row || {}).vendorId
      }
    }
  },

  components: {
    ProjectInformation,
    ProjectRequirements,
    ApplyInfo,
    QuoteInfo,
    CompetitionHall
  },

  data () {
    return {
      activeTab: 'projectInformation',
      projectId: '',
      baseInfo: {
        auctSouProject: {}
      },
      currencyList: [],
      fileConfigList: []
    }
  },

  created () {
    this.projectId = this.$attrs.params.row.projectId
    if (this.$attrs.params.type === 'viewResults') {
      // 报价结果
      this.activeTab = 'quoteInfo'
    }

    this.getProjectInfo()
  },

  methods: {
    /* 查询单据详情 */
    async getProjectInfo () {
      if (!this.projectId) {
        return
      }
      // let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.projectId }], 'getProjectInfo')
      const response = await carVendorHttp.order.getProjectInfo(this.projectId)
      if (response && response.data) {
        const {
          currencyList = [],
          compSouProject = {},
          souFileList = [],
          ...competition
        } = response.data
        /* 解构获取相应数据构造前端数据 */
        const {
          souRules,
          orderNum,
          quoteCap,
          publicRules
        } = competition
        const {
          bondAmount,
          bondMethod,
          bondEndTime,
          bankAccountNum,
          bankAccountName,
          bankBranchName,
          bondDesc
        } = compSouProject
        const auctSouProject = { souRules, orderNum, quoteCap, publicRules, bondAmount, bondMethod, bondEndTime, bankAccountNum, bankAccountName, bankBranchName, bondDesc }
        this.baseInfo = Object.assign({}, competition)
        this.baseInfo.auctSouProject = Object.assign({}, this.baseInfo.auctSouProject, auctSouProject)
        this.currencyList = currencyList.concat()
        this.fileConfigList = souFileList.filter(item => item.fileType === 'OUTER')
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_inquiryVendorBiddingDetail_wrapper) {
  .el-form .el-form-item {
    margin-bottom: 0 !important;
  }
  .the_display_content {
    padding: 11px;
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
    .the_display_footer {
      text-align: center !important;
    }
  }
}
</style>
