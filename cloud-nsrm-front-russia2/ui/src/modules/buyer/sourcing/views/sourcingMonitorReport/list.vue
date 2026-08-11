<template>
  <el-container class="flex-container the_sourcingMonitorReport_wrapper" direction="vertical">
    <el-main>
      <div class="status-list-block">
        <ul class="status-list">
          <li v-for="i in statusList" :key="i.value" :class="currentStage === i.value ? 'status-active' : null" @click="getList(i.value)">
            {{ `${i.label}（${i.count}）` }}<i />
          </li>
        </ul>
      </div>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <ExportExcel
            v-loading
            page-url="/api-sup/req/monitor/list"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('sourcingBuyer.exportData')"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/req/monitor/list"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import sourcingApplicationDetail from '../sourcingApplicationBuyer/sourcingApplicationDetail'
import { sourcing } from 'modb@/sourcing/api'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'SourcingMonitorReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    CCategorySelect,
    QuickSearch
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      statusList: [
        {
          value: 'REQHEAD',
          // '寻源发布'
          label: this.$t('cusEntry.supplement20250211.sourceRelease'),
          count: 0
        },
        {
          value: 'REVIEW',
          // '资质审查'
          label: this.$t('route.quaOfReview'),
          count: 0
        },
        {
          value: 'SITEFORM',
          // '现场评审'
          label: this.$t('route.siteAssessmentV'),
          count: 0
        },
        {
          value: 'QUASAMPLE',
          // '送样申请'
          label: this.$t('cusEntry.supplement20250211.javascriptsampleApplication'),
          count: 0
        },
        {
          value: 'QUBSAMPLE',
          // '样品确认'
          label: this.$t('route.sampleConfirmed'),
          count: 0
        },
        {
          value: 'MATERIALTRIAL',
          // '物料试用'
          label: this.$t('route.materialTrial'),
          count: 0
        },
        {
          value: 'TAKEEFFECT',
          // '供方生效'
          label: this.$t('route.vendorEffect'),
          count: 0
        }
      ],
      currentStage: 'REQHEAD',
      dictCodes: {
        quaReviewType: 'QUA_REVIEW_TYPE'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 业务实体
        {
          prop: 'orgId',
          label: this.$t('sourcingBuyer.orgName'),
          type: 'OUorganizationSelector'
        },
        // 库存组织
        {
          prop: 'organizationId',
          label: this.$t('sourcingBuyer.organizationName'),
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        // 寻源单号
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        },
        // 物料品类
        {
          prop: 'categoryId',
          label: this.$t('sourcingBuyer.categoryType'),
          type: 'catSelect',
          showKey: 'categoryId'
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        // 引入场景
        {
          prop: 'quaReviewType',
          // '引入场景'
          label: this.$t('vendorMod.supplierEffective.introducingScenarios'),
          type: 'dict',
          code: 'QUA_REVIEW_TYPE'
        },
        // 创建时间
        {
          prop: 'dateList',
          label: this.$t('sourcingBuyer.creationDate'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      // 业务实体
      {
        prop: 'orgName',
        label: this.$t('sourcingBuyer.orgName'),
        minWidth: 100
      },
      // 库存组织
      {
        prop: 'organizationName',
        label: this.$t('sourcingBuyer.organizationName'),
        minWidth: 100
      },
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      // 供应商名称
      {
        prop: 'vendorName',
        // '供应商名称'
        label: this.$t('common.companyName'),
        minWidth: 130
      },
      // 物料品类
      {
        prop: 'categoryName',
        label: this.$t('sourcingBuyer.categoryType'),
        minWidth: 100
      },
      // 引入场景
      {
        prop: 'quaReviewType',
        // '引入场景'
        label: this.$t('vendorMod.supplierEffective.introducingScenarios'),
        minWidth: 100,
        dataType: 'dict',
        code: 'QUA_REVIEW_TYPE'
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('sourcingBuyer.creationDate'),
        minWidth: 100,
        dataType: 'dateTime'
      },
      // 寻源发布
      {
        prop: 'reqHeadStatus',
        // '寻源发布'
        label: this.$t('cusEntry.supplement20250211.sourceRelease'),
        minWidth: 100
      },
      // 资质审查
      {
        prop: 'reviewFormStatus',
        // '资质审查'
        label: this.$t('route.quaOfReview'),
        minWidth: 100
      },
      // 现场评审
      {
        prop: 'siteFormStatus',
        // '现场评审'
        label: this.$t('route.siteAssessmentV'),
        minWidth: 100
      },
      // 送样申请
      {
        prop: 'quaSampleStatus',
        // '送样申请'
        label: this.$t('cusEntry.supplement20250211.javascriptsampleApplication'),
        minWidth: 100
      },
      // 样品确认
      {
        prop: 'qubSampleStatus',
        // '样品确认'
        label: this.$t('route.sampleConfirmed'),
        minWidth: 100
      },
      // 物料试用
      {
        prop: 'materialTrialStatus',
        // '物料试用'
        label: this.$t('route.materialTrial'),
        minWidth: 100
      },
      // 供方生效
      {
        prop: 'takeEffectStatus',
        // '供方生效'
        label: this.$t('route.vendorEffect'),
        minWidth: 100
      },
      // 供方引入总耗时
      {
        prop: 'timeConsuming',
        // '供方引入总耗时'
        label: this.$t('cusEntry.supplement20250211.supplierIntroductionTotalTimeConsumption'),
        minWidth: 140
      }
    ]
    this.$nextTick(() => {
      this.getReportCount()
      this.getQuerydata()
    })
  },
  methods: {
    async getReportCount () {
      let res = await sourcing.getMonitorCount()
      if (res.data) {
        let data = res.data
        this.statusList[0].count = data.reqHeadCount
        this.statusList[1].count = data.reviewFormCount
        this.statusList[2].count = data.siteFormCount
        this.statusList[3].count = data.quaSampleCount
        this.statusList[4].count = data.qubSampleCount
        this.statusList[5].count = data.materialTrialCount
        this.statusList[6].count = data.takeEffectCount
      }
    },
    getQuerydata (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'dateList', fromProp: 'creationDateFrom', toProp: 'creationDateTo' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.queryParam.stage = this.currentStage
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getList (status) {
      this.currentStage = status
      this.queryParam.stage = this.currentStage
      this.$refs[this.gridId].query()
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'view',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'sourcingApplicationDetail' + row.reqHeadNo
      })
    }
  }
}
</script>

<style lang="scss" scoped>

@import "modb@/sourcing/views/requirementMonitorReport/style/mixin.scss";
.the_sourcingMonitorReport_wrapper {
  @include arrowArocess();
}
</style>
