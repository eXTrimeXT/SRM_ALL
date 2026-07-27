<template>
  <el-container
    class="flex-container the_OrderAcceptanceList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
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
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :adeptMeiQl="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import orderAcceptanceDetail from './detail'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'OrderAcceptanceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      filterParams: {},
      queryParam: {},
      tableHeader: [],
      tableData: [],
      tableUrl: '/api-sup-ce/api-ql/CheckOrderVendor/query',
      preArr: [
        {
          prop: 'checkOrderNumber',
          label: '验收单号'
        },
        {
          prop: 'checkOrderStatus',
          label: '单据状态',
          type: 'dict',
          filterItem: () => ['DRAFT', 'APPROVING', 'WITHDRAW', 'REJECT'],
          code: 'CHECK_ORDER_STATUS'
        },
        {
          prop: 'orgName',
          label: this.$t('purchaseDemand.businessEntity')
        },
        {
          prop: 'createdFullName',
          label: this.$t('purchaseDemand.createdBy1')
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          type: 'daterange'
        }
      ],
      dictCodes: {
        checkOrderStatus: 'CHECK_ORDER_STATUS'
      }
    }
  },

  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        label: () => this.$t('oneStopShopping.businessEntity'),
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'),
        minWidth: 150
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'checkOrderNumber',
        label: '验收单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      {
        prop: 'checkOrderStatus',
        label: () => this.$t('bidMod.billstatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'CHECK_ORDER_STATUS'
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('common.creator'),
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : '',
        minWidth: 100
      },
      {
        prop: 'approveComment',
        label: () => this.$t('orderMod.refuseReason'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('manage', row),
            formattor: () => this.$t('purchaseDemand.manage'),
            // 待供应商确认
            show: row => row.checkOrderStatus === 'APPROVED'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      // 过滤拟定状态单据、其他供应商单据
      let params = {}
      params.vendorId = { eq: this.$store.getters.user.companyId }
      params.checkOrderStatus = { notIn: ['DRAFT', 'APPROVING', 'WITHDRAW', 'REJECT'] }
      const { checkOrderNumber, checkOrderStatus, orgName, createdFullName, creationDate } = v || {}
      if (checkOrderNumber) {
        params.checkOrderNumber = { contains: checkOrderNumber }
      }
      if (checkOrderStatus) {
        params.checkOrderStatus = { eq: checkOrderStatus }
      }
      if (orgName) {
        params.orgName = { contains: orgName }
      }
      if (createdFullName) {
        params.createdFullName = { contains: createdFullName }
      }
      if (creationDate) {
        params.creationDate = { between: creationDate }
      }

      this.queryParam = {
        type: 'CheckOrderVendor',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }

      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        tab = {
          component: orderAcceptanceDetail,
          params: { flag: type },
          title: '新增订单验收单',
          name: 'orderAcceptanceDetail'
        }
      } else {
        tab = {
          component: orderAcceptanceDetail,
          params: { flag: type, row },
          title: `订单验收单${row.checkOrderNumber}`,
          name: 'orderAcceptanceDetail' + row.checkOrderNumber
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss"></style>
