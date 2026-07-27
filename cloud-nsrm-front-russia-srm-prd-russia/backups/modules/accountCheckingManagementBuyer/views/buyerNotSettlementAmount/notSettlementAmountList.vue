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
            @click="comfirm"
          >
            {{
              $t("purchaseDemand.confirm")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="refuse"
          >
            {{
              $t("common.toRefuse")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="finish"
          >
            {{
              $t("common.finish")
            }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="checkChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
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
import { parseTime } from '@/utils'

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
      statusList: [],
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
        callback: row => {
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
      { prop: 'organizationName', label: this.$t('common.orgName'), width: 200 }, // 采购组织
      {
        prop: 'startDate',
        label: this.$t('dataConfMod.startDay'), // 开始日期
        width: 100,
        formattor: cellValue =>
          cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'endDate',
        label: this.$t('accountMod.deadline'), // 截止日期
        width: 100,
        formattor: cellValue =>
          cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'rfqSettlementCurrency',
        label: this.$t('bid_mod.currencyName'), // 币种
        width: 100,
        dataType: 'dict',
        code: 'currency'

      },
      { prop: 'refuseReason', label: this.$t('bidMod.rejectReason1'), width: 100 }, //   驳回原因
      { prop: 'fileName', label: this.$t('bidMod.attachment'), width: 100 }, // 附件
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: cellValue =>
          cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (rows) {
      this.currentRows = rows
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    comfirm () {
      // 审核通过之后重新查询表单信息
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[0]') // 请选择要审核通过的对账单！
        })
}
      const submitStaus = ['SUMBIT']
      if (data.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[1]') // 只有状态为已提交的才能审核通过！
        })
        return
      }
      const params = data.map(i => i.unsettledOrderId)
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/comfirmBatch',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    finish () {
      // 完成之后重新查询表单信息
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[2]') // 请选择要完成的对账单！
        })
}
      const submitStaus = ['SURE']
      if (data.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[3]') // 只有状态为已确认的才能完成！
        })
        return
      }
      const params = data.map(i => i.unsettledOrderId)
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/finishBatch',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    refuse () {
      // 驳回之后重新查询表单信息
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[4]') // 请选择要驳回的对账单！
        })
}
      const submitStaus = ['SUMBIT']
      if (data.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('accountMod.msgList[5]') // 只有状态为已提交的才能驳回！
        })
        return
      }
      const params = data.map(i => i.unsettledOrderId)
      this.$http({
        url: '/api-sup-ce/reconciliation/unsettledOrder/refuseBatch',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
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
          name:
            'notSettlementAmountDetail' + this.currentRow.unsettledOrderNumber
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
