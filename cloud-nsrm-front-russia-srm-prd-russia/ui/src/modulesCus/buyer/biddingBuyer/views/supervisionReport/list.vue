<template>
  <el-container
    class="flex-container supervisionReportList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :table-header="tableHeader"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'SupervisionReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        orderStatus: 'SOU_ORDER_STATUS',
        requirementPlanType: 'PR_SOU_REQUIREMENT_FROM',
        extSouProcess: 'SOU_BID_PROCCESS',
        extVendorAttr: 'SOU_RECOMM_VENDOR_NATRUE',
        extIsNewVendor: 'YES_OR_NO',
        isWin: 'YES_OR_NO'
      },
      tableUrl: '/api-sou/extReportBid/bidSupervise/listPage',
      gridId: 'list',
      pageSize: 15,
      queryParam: {},
      filterParams: {},
      tableData: [],
      preArr: [
        {
          prop: 'requirementPlanType',
          label: this.$t('cusEntry.bidSuperviseReport.requirementPlanType'),
          type: 'dict',
          code: 'PR_SOU_REQUIREMENT_FROM'
        },
        {
          prop: 'extOrgBuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName')
        },
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName')
        },
        {
          prop: 'companyShortCode',
          label: '公司代码'
        },
        {
          prop: 'year',
          label: '年'
        },
        {
          prop: 'month',
          label: '月'
        },
        {
          prop: 'extOrgBuCode',
          label: '板块代码'
        },
        {
          prop: 'extProjectNo',
          label: this.$t('cusEntry.bidSuperviseReport.extProjectNo')
        },
        {
          prop: 'souName',
          label: this.$t('cusEntry.bidSuperviseReport.souName')
        },
        {
          prop: 'extSouProcess',
          label: this.$t('cusEntry.bidSuperviseReport.extSouProcess'),
          type: 'dict',
          code: 'SOU_BID_PROCCESS'
        },
        {
          prop: 'itemDesc',
          label: this.$t('cusEntry.bidSuperviseReport.extCategoryName')
        },
        {
          prop: 'souPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.souPrincipal')
        },
        {
          prop: 'vendorPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal')
        },
        {
          prop: 'leaderPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.leaderPrincipal')
        },
        {
          prop: 'extTechPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.extTechPrincipal')
        },
        {
          prop: 'totalBudgetFrom',
          label: '预算金额（≥N万元）'
        },
        {
          prop: 'totalBudgetTo',
          label: '预算金额（≤N万元）'
        },
        {
          prop: 'vendorName',
          label: this.$t('cusEntry.bidSuperviseReport.vendorName')
        },
        {
          prop: 'linkmanName',
          label: this.$t('cusEntry.bidSuperviseReport.linkmanName')
        },
        {
          prop: 'phone',
          label: this.$t('cusEntry.bidSuperviseReport.phone')
        },
        {
          prop: 'extIsNewVendor',
          label: this.$t('cusEntry.bidSuperviseReport.extIsNewVendor'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'techSocreFrom',
          label: '技术得分（≥N）'
        },
        {
          prop: 'techSocreTo',
          label: '技术得分（≤N）'
        },
        {
          prop: 'comprehensiveScoreFrom',
          label: '综合得分（≥N）'
        },
        {
          prop: 'comprehensiveScoreTo',
          label: '综合得分（≤N）'
        },
        {
          prop: 'isWin',
          label: this.$t('cusEntry.bidSuperviseReport.isWin'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'extVendorAttr',
          label: this.$t('cusEntry.bidSuperviseReport.extVendorAttr'),
          type: 'dict',
          code: 'SOU_RECOMM_VENDOR_NATRUE'
        }
      ],
      tableHeader: [
        {
          prop: 'requirementPlanType',
          label: this.$t('cusEntry.bidSuperviseReport.requirementPlanType'),
          minWidth: 150,
          dataType: 'dict',
          code: 'PR_SOU_REQUIREMENT_FROM'
        },
        {
          prop: 'extOrgBuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
          minWidth: 150
        },
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          minWidth: 150
        },
        {
          prop: 'companyShortCode',
          label: '公司代码',
          minWidth: 150
        },
        {
          prop: 'year',
          label: '年',
          minWidth: 100
        },
        {
          prop: 'month',
          label: '月',
          minWidth: 100
        },
        {
          prop: 'extOrgBuCode',
          label: '板块代码',
          minWidth: 150
        },
        {
          prop: 'extProjectNo',
          label: this.$t('cusEntry.bidSuperviseReport.extProjectNo'),
          minWidth: 150
        },
        {
          prop: 'completeMonth',
          label: '完成月份',
          minWidth: 150
        },
        {
          prop: 'souName',
          label: this.$t('cusEntry.bidSuperviseReport.souName'),
          minWidth: 150
        },
        {
          prop: 'extSouProcess',
          label: this.$t('cusEntry.bidSuperviseReport.extSouProcess'),
          minWidth: 150,
          dataType: 'dict',
          code: 'SOU_BID_PROCCESS'
        },
        {
          prop: 'classification',
          label: this.$t('cusEntry.bidSuperviseReport.classification'),
          minWidth: 150
        },
        {
          prop: 'itemDesc',
          label: this.$t('cusEntry.bidSuperviseReport.extCategoryName'),
          minWidth: 150
        },
        {
          prop: 'souPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
          minWidth: 150
        },
        {
          prop: 'vendorPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal'),
          minWidth: 150
        },
        {
          prop: 'leaderPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.leaderPrincipal'),
          minWidth: 150
        },
        {
          prop: 'extTechPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.extTechPrincipal'),
          minWidth: 150
        },
        {
          prop: 'extScaleQuantity',
          label: this.$t('cusEntry.bidSuperviseReport.extScaleQuantity'),
          minWidth: 150
        },
        {
          prop: 'totalBudget',
          label: this.$t('cusEntry.bidSuperviseReport.totalBudget'),
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('cusEntry.bidSuperviseReport.vendorName'),
          minWidth: 200
        },
        {
          prop: 'linkmanName',
          label: this.$t('cusEntry.bidSuperviseReport.linkmanName'),
          minWidth: 150
        },
        {
          prop: 'phone',
          label: this.$t('cusEntry.bidSuperviseReport.phone'),
          minWidth: 150
        },
        {
          prop: 'extIsNewVendor',
          label: this.$t('cusEntry.bidSuperviseReport.extIsNewVendor'),
          minWidth: 150,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'quotedPriceWithTaxFirst',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxFirst'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxSecond',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxSecond'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxThird',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxThird'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxFour',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxFour'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxFive',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxFive'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxSix',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxSix'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxSeven',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxSeven'),
          minWidth: 160
        },
        {
          prop: 'quotedPriceWithTaxEight',
          label: this.$t('cusEntry.bidSuperviseReport.quotedPriceWithTaxEight'),
          minWidth: 160
        },
        {
          prop: 'techSocre',
          label: this.$t('cusEntry.bidSuperviseReport.techSocre'),
          minWidth: 150
        },
        {
          prop: 'comprehensiveScore',
          label: this.$t('cusEntry.bidSuperviseReport.comprehensiveScore'),
          minWidth: 150
        },
        {
          prop: 'isWin',
          label: this.$t('cusEntry.bidSuperviseReport.isWin'),
          minWidth: 150,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'caPrice',
          label: this.$t('cusEntry.bidSuperviseReport.caPrice'),
          minWidth: 160
        },
        {
          prop: 'noticePrice',
          label: this.$t('cusEntry.bidSuperviseReport.noticePrice'),
          minWidth: 160
        },
        {
          prop: 'extVendorAttrName',
          label: this.$t('cusEntry.bidSuperviseReport.extVendorAttr'),
          minWidth: 150         
        },
        {
          prop: 'orderStatus',
          label: this.$t('cusEntry.bidSuperviseReport.orderstaus'),
          dataType: 'dict',
          code: 'SOU_ORDER_STATUS'
        }
      ]
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
