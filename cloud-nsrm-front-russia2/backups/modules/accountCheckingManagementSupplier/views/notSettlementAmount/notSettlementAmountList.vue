<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t('accountMod.createStatement')
            }}
          </el-button>
          <el-button
            type="primary"
            @click="recall"
          >
            {{
              $t('bidMod.withdraw')
            }}
          </el-button>
          <el-button
            type="primary"
            @click="obsolete"
          >
            {{
              $t('common.cancelled')
            }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="checkChange"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/reconciliation/unsettledOrder/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import notSettlementAmountDetail from './notSettlementAmountDetail'
import { parseTime, adaptDictData } from '@/utils'

export default {
  name: 'NotSettlementAmountList',
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
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'), // 采购组织
          type: 'OUorganizationSelector'
        },
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'UNSETTLED_ORDER_STATUS'
        }, // 状态
        {
          prop: 'startDateStr',
          label: this.$t('quota.startDate'),
          type: 'date'
        }, // 起始日期
        {
          prop: 'endDateStr',
          label: this.$t('accountMod.deadline'),
          type: 'date'
        }, // 截止日期
        {
          prop: 'rfqSettlementCurrency',
          label: this.$t('bid_mod.currencyName'),
          type: 'dict',
          code: 'currency'
        }, // 币种
        {
          prop: 'unsettledOrderNumber',
          label: this.$t('purSettlementMod.statementNumber')
        } // 对账单号
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'unsettledOrderNumber',
        label: this.$t('purSettlementMod.statementNumber'), // 对账单号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.currentRow = row
          this.editTab()
        },
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'status',
        label: this.$t('common.status'), // 状态
        width: 100,
        dataType: 'dict',
        code: 'UNSETTLED_ORDER_STATUS'
      },
      {
        prop: 'organizationName',
        label: this.$t('common.orgName'),
        width: 200
      },
      {
        prop: 'startDate',
        label: this.$t('dataConfMod.startDay'), // 开始日期
        width: 100,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'endDate',
        label: this.$t('accountMod.deadline'), // 截止日期
        width: 100,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'rfqSettlementCurrency',
        label: this.$t('bid_mod.currencyName'), // 币种
        width: 100,
        dataType: 'dict',
        code: 'currency'
      },
      {
        prop: 'refuseReason',
        label: this.$t('bidMod.rejectReason1'),
        width: 100
      }, //   驳回原因
      { prop: 'fileName', label: this.$t('bidMod.attachment'), width: 100 },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
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
    checkChange (rows) {
      this.currentRows = rows
    },
    obsolete () {
      // 作废之后重新查询表单信息
      const data = this.currentRows
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[9]') // 请选择要作废的对账单！
        })
      }
      const submitStaus = ['CREATE']
      if (data.some((i) => submitStaus.findIndex((j) => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: 'c'
        })
        return
      }
      const params = data.map((i) => i.unsettledOrderId)
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/cancalBatch',
        method: 'POST',
        data: params,
        loading: true
      }).then((res) => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    recall () {
      // 撤回之后重新查询表单信息
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[11]') // 请选择要撤回的对账单！
        })
}
      const submitStaus = ['SURE', 'SUBMIT']
      if (data.some((i) => submitStaus.findIndex((j) => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[12]') // 只有状态为已提交或者已确认的才能撤回！
        })
        return
      }
      const params = { rollBack: [], unSubmit: [] }
      data.forEach((i) => {
        if (i.status === 'SUBMIT') {
          params.rollBack.push(i.unsettledOrderId)
        }
        if (i.status === 'SURE') {
          params.unSubmit.push(i.unsettledOrderId)
        }
      })
      this.statusBack('SURE', params.rollBack)
      this.statusBack('SUBMIT', params.unSubmit)
    },
    statusBack (status, data) {
      const url =
        status === 'SURE'
          ? '/api-sup-ce/reconciliation/unsettledOrder/rollBackBatch'
          : '/api-sup-ce/reconciliation/unsettledOrder/unSubmitBatch'
      this.$http({
        url,
        method: 'POST',
        data,
        loading: true
      }).then((res) => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    editTab (type) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: notSettlementAmountDetail,
          params: { flag: 'add' },
          title: this.$t('accountMod.addStatement'), // 新增对账单
          name: 'notSettlementAmountDetail'
        }
      } else {
        // 修改
        tab = {
          component: notSettlementAmountDetail,
          params: {
            flag: 'edit',
            row: this.currentRow
          },
          title: this.currentRow.unsettledOrderNumber,
          name: 'notSettlementAmountDetail' + this.currentRow.unsettledOrderNumber
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the_quotationPrices_wrapper {
}
</style>
