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
          label: '到期月份',
          type: 'month'
        },
        {
          prop: 'projectName',
          label: '项目名称'
        },
        {
          prop: 'headPerson',
          label: '负责人',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'num',
          label: '项目轮次'
        },
        // {
        //   prop: 'round',
        //   label: '项目轮次至'
        // },
        {
          prop: 'contractDate',
          label: '合同起始日期',
          type: 'daterange'
        },
        {
          prop: 'projectTotalMoney',
          label: '项目总金额从'
        },
        {
          prop: 'projectTotalMoneyEnd',
          label: '项目总金额至'
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
        label: '到期月份',
        minWidth: 120
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 100
      },
      {
        prop: 'num',
        label: '现运行项目轮数',
        minWidth: 130
      },
      {
        prop: 'contractStartDate',
        label: '合同起始日期',
        minWidth: 130
      },
      {
        prop: 'contractEndDate',
        label: '合同终止日期',
        minWidth: 130
      },
      {
        prop: 'delayReason',
        label: '延期原因',
        minWidth: 150
      },
      {
        prop: 'headPerson',
        label: '负责人',
        minWidth: 130
      },
      {
        prop: 'projectTotalMoney',
        label: '项目总金额（万元）',
        minWidth: 150
      },
      {
        label: '主项目',
        align: 'center',
        width: 150,
        children: [
          {
            prop: 'mainPlanStatus',
            label: '项目策划方案状态'
          },
          {
            prop: 'mainIsXbj',
            label: '询比价'
          },
          {
            prop: 'mainIsSq',
            label: '定厂定价申请'
          }
        ]
      },
      {
        label: '询比价调价',
        width: 130,
        align: 'center',
        children: [
          {
            prop: 'xIsXbj',
            label: '询比价'
          },
          {
            prop: 'xIsSq',
            label: '调价申请'
          },
          {
            prop: 'xNum',
            label: '第几次'
          }
        ]
      },
      {
        label: '市场行情调价',
        width: 130,
        align: 'center',
        children: [

          {
            prop: 'sIsSq',
            label: '调价申请'
          },
          {
            prop: 'sNum',
            label: '第几次'
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
