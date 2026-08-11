<template>
  <el-container
    class="flex-container-notab the_buyerPlanOrders_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <!-- <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">

        </template>
      </main-header> -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/pss/member/dict/list"
        url-for-count="/isales-main/mstQuicksearchConfig/queryCount"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'

export default {
  name: 'BuyerPlanOrders',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [
          { required: true, message: this.$t('bidMod.msgDictCode') }
        ], // 请输入字典编码
        vendorCompanyName: [
          { required: true, message: this.$t('bidMod.msgDictName') }
        ] // 请输入字典名称
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'itemCode', label: this.$t('common.materialCode') }, // 物料编码
        { prop: 'itemName', label: this.$t('common.materialName') }, // 物料名称
        { prop: 'status', label: this.$t('common.status'), type: 'select' }, // 状态
        {
          prop: 'planMonth',
          label: this.$t('planMod.planMonth'),
          type: 'month'
        } // 计划月
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      { prop: 'planMonth', label: this.$t('planMod.planMonth'), width: 100 }, // 计划月
      { prop: 'versionNum', label: this.$t('dataConfMod.version'), width: 100 }, // 版本号
      { prop: 'itemCode', label: this.$t('common.materialCode'), width: 100 }, // 物料编码
      { prop: 'itemName', label: this.$t('common.materialName'), width: 100 }, // 物料名称
      { prop: 'purOrg', label: this.$t('common.orgName'), width: 100 }, // 采购组织
      {
        prop: 'demandDate',
        label: this.$t('bidMod.ceeaDemandDate'),
        width: 100,
        dataType: 'dateTime'
      }, //   需求日期
      {
        prop: 'demandAmount',
        label: this.$t('bidMod.demandQuantity2'),
        width: 100
      }, // 需求数量
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        dataType: 'dateTime'
      }
    ]
    this.$nextTick(() => {
      // this.getQuerydata()
      let listdata = []
      for (let i = 1; i < 5; i++) {
        listdata.push({
          planMonth: '20200' + (3 + i),
          versionNum: '2020020' + i,
          itemCode: this.$t('common.materialCode') + i,  // '物料编码'
          itemName: this.$t('common.materialName') + i,  // '物料名称'
          demandAmount: '10' + i,
          purOrg: this.$t('cusEntry.supplement20250211.oouuRefrigeratorDivisionShundeFactory') + i,  // 'OOUU_冰箱事业部_顺德工厂'
          demandDate: '2020-07' + i,
          creationDate: '2020-03' + i
        })
      }
      this.$refs[this.gridId].tableData = listdata
    })
  },
  methods: {
    getQuerydata (v) {
      // debugger
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">
.the_buyerPlanOrders_wrapper {
}
</style>
