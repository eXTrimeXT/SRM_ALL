<!-- 合同履行计划 -->
<template>
  <i-mini-table
    :columns="columns"
    :data="data"
    border
    :summary-method="getSummaries"
    show-summary
    @selection-change="selectionChangeHandler"
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'
import Big from 'big.js'
import CPeopleSelector from '@/library/components/c-people-selector'

const IPersonSelector = {
  props: ['value', 'row', 'disabled'],
  data () {
    return {
      visible: false
    }
  },
  components: { CPeopleSelector },
  render (h) {
    const listeners = {
      'on-confirm': (data) => {
        if (!data) {
          return
        }
        const [user = {}] = data
        this.row.nodePersonName = user.nickname || ''
        this.row.nodePersonId = user.userId || ''
        this.row.nodePersonBy = user.username || ''
      },
      'update:visible': (value) => (this.visible = value)
    }
    return (
      <div>
        <el-input value={this.value} disabled={this.disabled}>
          <div slot="append">
            <el-button
              icon="el-icon-search"
              disabled={this.disabled}
              size="medium"
              onClick={() => (this.visible = true)}
            />
          </div>
        </el-input>
        <c-people-selector on={{ ...listeners }} visible={this.visible} multiSelect={false} />
      </div>
    )
  }
}

export default {
  name: 'IPerformInvoice',
  components: {
    IMiniTable
  },
  props: ['data', 'mode', 'store', 'includeTaxAmount'],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceBillEdit',
        fileType: 'images'
      },
      columns: [
        { prop: 'index', type: 'index' },
        {
          prop: 'milestoneType',
          label: this.$t('contract_mod.processNodeName'),
          width: 150,
          formatter: (...args) => {
            return this.store.getLabel('MILESTONE_SCHEDULE', args[2])
          }
        },
        {
          prop: 'nodePersonName',
          label: this.$t('common.nodeLeader'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            return (
              <IPersonSelector
                value={scope.row.nodePersonName}
                row={scope.row}
                disabled={this.disabled}
              />
            )
          }
        },
        {
          prop: 'invoicePlanStatus',
          label: this.$t('components.stratProcess.headers.docStatusValue'),
          formatter: (...args) => this.store.getLabel('MILESTONE_STATE', args[2])
        },
        // 计划开票比例（%）
        {
          prop: 'planInvoiceProportion',
          label: this.$t('cusEntry.supplement20250205.plannedInvoicingRatioPercentage'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            const input = (rate) => {
              if (!rate) {
                scope.row.planInvoiceAmount = 0
                return
              }
              // 计算 planInvoiceAmount 计划开票金额
              scope.row.planInvoiceAmount = new Big(this.includeTaxAmount)
                .times(rate)
                .div(100)
                .valueOf()
            }
            return (
              <el-input
                onInput={input}
                v-model={scope.row.planInvoiceProportion}
                disabled={this.disabled}
              />
            )
          }
        },
        { prop: 'planInvoiceAmount', label: this.$t('contract_mod.planInvoiceAmount'), width: 150 },
        {
          prop: 'planInvoiceCompleteDate',
          label: this.$t('contract_mod.planInvoiceCompleteDate'),
          width: 180,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            return (
              <el-date-picker
                v-model={scope.row.planInvoiceCompleteDate}
                type="date"
                format={this.$formatDatePicker}
                disabled={this.disabled}
                value-format="yyyy-MM-dd"
              />
            )
          }
        },
        { prop: 'actualInvoiceDate', label: this.$t('contract_mod.actualInvoiceDate'), width: 180, dataType: 'dateTime' },
        { prop: 'actualInvoiceAmount', label: this.$t('contract_mod.actualInvoiceAmount'), width: 150 },
        { prop: 'invoiceApplyNo', label: this.$t('contract_mod.invoiceApplyNo'), width: 150 },
        { prop: 'paymentDays', label: this.$t('bidMod.paymentDays'), width: 150 },
        { prop: 'palnPaymentDate', label: this.$t('contract_mod.palnPaymentDate'), width: 150, dataType: 'dateTime' },
        { prop: 'paymentApplyNo', label: this.$t('contractMod.paymentApplyNumber'), width: 150 },
        { prop: 'actualPaymentDate', label: this.$t('contract_mod.actualPaymentDate'), width: 150, dataType: 'dateTime' },
        { prop: 'fineAmount', label: this.$t('cusEntry.supplement20250205.penaltyAmount') }, // 罚款金额
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          render: (h, scope) => {
            // 交付
            if (scope.row.planStatus === 'COMPLETE') {
              return (
                <el-button type="text" onClick={() => this.handOver(scope.row)}>
                  {this.$t('cusEntry.supplement20250205.invoiceApplication')}
                </el-button>
              )
            }
            return null
          }
        }
      ]
    }
  },
  computed: {
    disabled () {
      return ['handOver', 'approved', 'view'].includes(this.mode)
    }
  },
  watch: {},
  created () {},
  mounted () {},
  methods: {
    getSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = this.$t('other.key13')
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        const listenerKeys = [
          'planInvoiceProportion',
          'planInvoiceAmount',
          'actualInvoiceAmount',
          'fineAmount'
        ]
        if (!values.every((value) => isNaN(value)) && listenerKeys.includes(column.property)) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
        } else {
          sums[index] = ''
        }
      })

      return sums
    },
    handOver (row) {
      this.$emit('handover', row)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
