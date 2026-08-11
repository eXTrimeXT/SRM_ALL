<template>
  <el-container class="flex-container the_inquiryVendorBiddingDetail_wrapper" direction="vertical">
    <el-main>
      <el-tabs
        v-model="activeTab"
        type="border-card"
      >
        <!--项目信息-->
        <el-tab-pane
          :label="$t('bidMod.projectInformation')"
          name="projectInformation"
          lazy
        >
          <ProjectInformation
            :base-info="baseInfo"
            :currency-list="currencyList"
            :file-config-list="fileConfigList"
          />
        </el-tab-pane>

        <!--项目需求-->
        <el-tab-pane
          :label="$t('bidMod.projectRequire')"
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

        <!--竞价大厅-->
        <el-tab-pane
          :label="$t('bidMod.hall')"
          name="competitionHall"
          lazy
        >
          <CompetitionHall :base-info="baseInfo" :is-current-active-tab="activeTab === 'competitionHall'" />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import ProjectInformation from './competitionProjectDetail/projectInformation'
import ProjectRequirements from './competitionProjectDetail/projectRequirements'
import ApplyInfo from './competitionProjectDetail/applyInfo'
import QuoteInfo from './competitionProjectDetail/quoteInfo'
import CompetitionHall from './competitionProjectDetail/competitionHall'

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
      baseInfo: {},
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

      const response = await compVendorHttp.order.getProjectInfo(this.projectId)
      if (response && response.data) {
        const {
          currencyList = [],
          fileConfigList = [],
          ...competition
        } = response.data
        this.baseInfo = JSON.parse(JSON.stringify(competition))
        this.currencyList = currencyList.concat()
        this.fileConfigList = fileConfigList.concat()
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
