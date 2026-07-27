<template>
  <el-container
    class="flex-container-notab the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="exportOne"
          >
            导出
          </el-button>
          <el-button
            type="primary"
            @click="exportOne"
          >
            提交
          </el-button>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
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
import quotationPricesDetail from '../quotationPrices/quotationPricesDetail'

export default {
  name: 'QuotationPricesList',
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
      preArr: [
        {
          prop: 'inquiryOrderNum',
          label: '询价单号'
        },
        {
          prop: 'itemCode',
          label: '物料编码'
        },
        {
          prop: 'ioStatus',
          label: '单据状态',
          type: 'select',
          options: []
        },
        {
          prop: 'inquiryStatus',
          label: '询价状态',
          type: 'select',
          options: []
        },
        {
          prop: 'purOrg',
          label: '采购组织',
          type: 'select',
          options: []
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'biddingNum',
        label: '报价单号',
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => {
          this.currentRow = row
          this.editTab()
        },
        formattor: val => val || '--'
      },
      {
        prop: 'ioStatus',
        label: '单据状态',
        width: 100
      },
      {
        prop: 'itemCode',
        label: '物料编码',
        width: 100
      },
      {
        prop: 'itemName',
        label: '物料名称',
        width: 100
      },
      {
        prop: 'purOrg',
        label: '采购组织',
        width: 200
      },
      {
        prop: 'lineStatus',
        label: '行状态',
        width: 100
      },
      {
        prop: 'biddingResult',
        label: '询报价结果',
        width: 100
      }
    ]

    this.$nextTick(() => {
      const listdata = []
      for (let i = 1; i < 5; i++) {
        listdata.push({
          biddingNum: 'BRDCAST00' + i,
          ioStatus: '状态' + i,
          itemCode: 'MAT00' + i,
          itemName: '物料00' + i,
          purOrg: 'OOUU_冰箱事业部_顺德工厂' + i,
          biddingEndDate: '2020-3-' + i,
          publishDate: '2020-8-' + i
        })
      }
      this.$refs[this.gridId].tableData = listdata
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    exportOne () {},
    imoportOne () {},
    deleteOne () {},
    editTab () {
      // 编辑tab
      const tab = {
        component: quotationPricesDetail,
        params: {
          flag: 'edit',
          row: this.currentRow
        },
        title: this.currentRow.biddingNum,
        name: 'quotationPricesDetail' + this.currentRow.biddingNum
      }
      this.$emit('tab-add', tab)
    },
    dateExchange () {},
    cancelOne () {}
  }
}
</script>
