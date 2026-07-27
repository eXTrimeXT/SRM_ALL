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
          label="竞价需求"
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
        <!-- <el-tab-pane
          :label="$t('bidMod.hall')"
          name="competitionHall"
          lazy
        >
          <CompetitionHall :base-info="baseInfo" :is-current-active-tab="activeTab === 'competitionHall'" />
        </el-tab-pane> -->
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import { carVendorHttp } from 'mods@/competitionSupplier/api'
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

      let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.projectId }], 'getProjectInfo')

      const response = await carVendorHttp.order.getProjectInfo(transformParams)
      if (response && response.data && response.data.records.length) {
        const {
          currencyList = [],
          fileConfigList = [],
          auctSouProject = {},
          ...competition
        } = response.data.records[0]
        this.baseInfo = JSON.parse(JSON.stringify({ ...competition, ...auctSouProject }))
        this.currencyList = currencyList.concat()
        this.fileConfigList = fileConfigList.concat()
        console.log('fileConfigList', this.fileConfigList)
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
