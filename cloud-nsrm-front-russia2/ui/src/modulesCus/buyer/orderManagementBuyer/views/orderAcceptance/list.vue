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
          <AuthorityButton
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
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
      tableUrl: '/api-sup-ce/api-ql/CheckOrder/query',
      preArr: [
        {
          prop: 'checkOrderNumber',
          // label: '验收单号'
          label: () => this.$t('contractMod.acceptNumber')
        },
        {
          prop: 'checkOrderStatus',
          // label: '单据状态',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'CHECK_ORDER_STATUS'
        },
        {
          prop: 'orgId',
          label: this.$t('purchaseDemand.businessEntity'),
          type: 'OUorganizationSelector'
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

  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || null
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
        // label: '验收单号',
        label: () => this.$t('contractMod.acceptNumber'),
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
            callback: row => this.editTab('edit', row),
            formattor: () => this.$t('common.edit'),
            show: row => ['WITHDRAW', 'REJECT', 'DRAFT', 'REFUSE'].includes(row.checkOrderStatus) && row.createdId === this.userId
          },
          {
            callback: row => this.editTab('manage', row),
            formattor: () => this.$t('purchaseDemand.manage'),
            // 审批中 (创建人可点管理按钮，进去撤回单据, 审批人进去审批单据)
            show: row => row.checkOrderStatus === 'APPROVING' && [row.createdId, row.approveUserId].includes(this.userId)
          },
          {
            callback: row => this.deleteRow(row),
            formattor: () => this.$t('common.delete'),
            show: row => ['WITHDRAW', 'REJECT', 'DRAFT', 'REFUSE'].includes(row.checkOrderStatus) && row.createdId === this.userId
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (obj) {
      let params = obj || {}
      this.queryParam = transformMQL.listPageData({
        type: 'CheckOrder',
        action: 'query',
        params,
        filterOperator: {
          creationDate: 'between'
        }
      })
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
          // title: '新增订单验收单',
          title:  () => this.$t('cusEntry.supplement20250121.addOrderAcceptance'),
          name: 'orderAcceptanceDetail'
        }
      } else {
        tab = {
          component: orderAcceptanceDetail,
          params: { flag: type, row },
          // title: `订单验收单${row.checkOrderNumber}`,
          title: `${this.$t('cusEntry.supplement20250121.addOrderAcceptance')}${row.checkOrderNumber}`,
          name: 'orderAcceptanceDetail' + row.checkOrderNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    deleteRow (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = transformMQL.save(
          'CheckOrder',
          [{
            'checkOrderId': row.checkOrderId,
            'detailList': [{ $delete: '*' }],
            'attachList': [{ $delete: '*' }]
          }],
          'delete'
        )
        this.$http({
          url: '/api-sup-ce/api-ql/CheckOrder/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
