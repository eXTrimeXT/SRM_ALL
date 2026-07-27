<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left" />
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { designPlanHttp } from 'modcb@/jcAgreement/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'

export default {
  name: 'JcProgressReportList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: '/api-sou/design/plan/schedule/dp/schedule/getSchedulePageList',
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'expirationMonth',
          label: this.$t('cusEntry.supplement20250121.expirationMonth'), // '到期月份'
          type: 'month'
        },
        {
          prop: 'projectName',
          label: this.$t('bidMod.bidingName') // '项目名称'
        },
        {
          prop: 'headPerson',
          label: this.$t('dataConfMod.principal'), // '负责人'
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'num',
          label: this.$t('cusEntry.supplement20250121.runRound'), // '项目轮次
        },
        {
          prop: 'contractDate',
          label: this.$t('cusEntry.supplement20250121.contractStartDate'), // '合同起始日期'
          type: 'daterange'
        },
        {
          prop: 'projectTotalMoney',
          label: this.$t('cusEntry.supplement20250121.projectTotalMoneyStart'), // '项目总金额从
        },
        {
          prop: 'projectTotalMoneyEnd',
          label: this.$t('cusEntry.supplement20250121.projectTotalMoneyEnd'), // '项目总金额至
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'expirationMonth',
        label: this.$t('cusEntry.supplement20250121.expirationMonth'), // '到期月份'
        minWidth: 120
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'), // '项目名称'
        minWidth: 100
      },
      {
        prop: 'num',
        label: this.$t('cusEntry.supplement20250121.runRound'), // '现运行项目轮数'
        minWidth: 130
      },
      {
        prop: 'contractStartDate',
        label: this.$t('cusEntry.supplement20250121.contractStartDate'), // '合同起始日期'
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'contractEndDate',
        label: this.$t('cusEntry.supplement20250121.contractEndDate'), // '合同终止日期'
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'delayReason',
        label: this.$t('cusEntry.supplement20250121.delayReason'), // '延期原因'
        minWidth: 150
      },
      {
        prop: 'headPerson',
        label: this.$t('dataConfMod.principal'), // '负责人'
        minWidth: 130
      },
      {
        prop: 'projectTotalMoney',
        label: this.$t('cusEntry.supplement20250121.projectTotalMoney'), // '项目总金额
        minWidth: 150
      },
      {
        label: this.$t('cusEntry.supplement20250121.mainProject'), //'主项目'
        align: 'center',
        width: 150,
        children: [
          {
            prop: 'mainPlanStatus',
            label: this.$t('cusEntry.supplement20250121.mainPlanStatus'), //'项目策划方案状态
          },
          {
            prop: 'mainIsXbj',
            label: this.$t('purchaseDemand.priceComparison'), //'询比价
          },
          {
            prop: 'mainIsSq',
            label: this.$t('cusEntry.supplement20250121.mainIsSq'), //'定厂定价申请
          }
        ]
      },
      {
        label: this.$t('cusEntry.supplement20250121.inquiryChange'), //'询比价调价'
        width: 130,
        align: 'center',
        children: [
          {
            prop: 'xIsXbj',
            label: this.$t('purchaseDemand.priceComparison'), //'询比价
          },
          {
            prop: 'xIsSq',
            label: this.$t('cusEntry.supplement20250121.priceAdjustApply'), //'调价申请
          },
          {
            prop: 'xNum',
            label: this.$t('cusEntry.supplement20250121.times'), //'第几次
          }
        ]
      },
      {
        label: this.$t('cusEntry.supplement20250121.priceAdjustment'), //'市场行情调价'
        width: 130,
        align: 'center',
        children: [

          {
            prop: 'sIsSq',
            label: this.$t('cusEntry.supplement20250121.priceAdjustApply'), //'调价申请
          },
          {
            prop: 'sNum',
            label: this.$t('cusEntry.supplement20250121.times'), //'第几次
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      let transformParams = {}
      const { contractDate, ...rest } = params
      if (contractDate && contractDate.length) {
        transformParams.contractStartDate = contractDate[0]
        transformParams.contractEndDate = contractDate[1]
      }
      this.queryParam = {
        ...rest,
        ...transformParams
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    }
  }
}
</script>
