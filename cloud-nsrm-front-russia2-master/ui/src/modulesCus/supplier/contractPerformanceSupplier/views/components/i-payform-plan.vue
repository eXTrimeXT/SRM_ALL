<!-- 合同履行计划 -->
<template>
  <IMiniTable
    :columns="columns"
    :data="data"
    border
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'
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
  name: 'IPayformPlan',
  components: {
    IMiniTable
  },
  props: ['data', 'mode', 'store', 'disabled', 'milestoneTypeList', 'readPretty'],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceBillEdit',
        fileType: 'images'
      },
      columns: [
        // { prop: 'index', type: 'index', label: '节点', width: 50 },
        { prop: 'index', type: 'index', label: () => this.$t('components.processTable.headers.fdNodeName'), width: 50 },
        {
          prop: 'milestoneType',
          label: this.$t('contract_mod.processNodeName'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.milestoneTypeList) {
              if (this.readPretty == true) {
                let name = ''
                this.milestoneTypeList.map(item => {
                  if (item.value == scope.row.milestoneType) {
                    name = item.label
                  }
                })
                return (
                  name
                )
              } else {
                return (
                  <el-select filterable v-model={scope.row.milestoneType} disabled={this.disabled} onChange={(val) => this.changeType(val, scope.row)}>
                    {this.milestoneTypeList.map(item => (
                      <el-option key={item.value} value={item.value} label={item.label}></el-option>
                    ))}
                  </el-select>
                )
              }
            }
          }
        },
        {
          prop: 'nodePersonName',
          // label: this.$t('节点负责人'),
          label: this.$t('common.nodeLeader'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.nodePersonName
            } else {
              return (
                <IPersonSelector
                  value={scope.row.nodePersonName}
                  row={scope.row}
                  disabled={this.disabled}
                />
              )
            }
          }
        },
        {
          prop: 'paymentStage',
          // label: '付款阶段',
          label: () => this.$t('bidMod.payStage'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return this.$getDictLabel('PAYMENT_STAGE', scope.row.paymentStage)
            } else {
              return (
                <dict-select
                  v-model={scope.row.paymentStage}
                  code="PAYMENT_STAGE"
                  disabled={this.disabled}
                />
              )
            }
          }
        },
        {
          prop: 'payExplain',
          // label: '付款条件',
          label: () => this.$t('route.contractPaymentType'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return this.$getDictLabel('payExplain', scope.row.payExplain)
            } else {
              return (
                <dict-select
                  v-model={scope.row.payExplain}
                  code="payExplain"
                  custom-select-type="payExplain"
                  disabled={this.disabled}
                />
              )
            }
          }
        },
        {
          prop: 'payMethod',
          // label: '付款方式',
          label: () => this.$t('vendorMod.paymentMethod'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return this.$getDictLabel('PAYMENT_MODE', scope.row.payMethod)
            } else {
              return (
                <dict-select
                  v-model={scope.row.payMethod}
                  code="PAYMENT_MODE"
                  disabled={this.disabled}
                />
              )
            }
          }
        },
        // {
        //   prop: 'paymentRatio',
        //   label: '付款比例（%）',
        //   width: 150,
        //   renderHeader: this._addStarToColumn,
        //   render: (h, scope) => {
        //     if (this.readPretty == true) {
        //       return scope.row.paymentRatio
        //     } else {
        //       return (
        //         <el-input
        //           v-model={scope.row.paymentRatio}
        //           disabled={this.disabled}
        //           onChange={() => this.$emit('setAmount', scope.row)}
        //           v-input-format={{ type: 'float', digits: 2, negative: false, zero: false }}
        //         />
        //       )
        //     }
        //   }
        // },
        // { prop: 'stagePaymentAmount', label: '阶段付款金额' },
        { prop: 'stagePaymentAmount', label: () => this.$t('contractMod.stagePaymentAmount') },
        {
          prop: 'palnPaymentDate',
          // label: this.$t('计划开始时间'),
          label: this.$t('vendorMod.planStartDate'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return this.$parseTime(scope.row.palnPaymentDate)
            } else {
              return (
                <el-date-picker
                  v-model={scope.row.palnPaymentDate}
                  type="date"
                  disabled={this.disabled}
                  format={this.$formatDatePicker}
                  value-format="yyyy-MM-dd"
                />
              )
            }
          }
        },
        {
          prop: 'planInvoiceCompleteDate',
          // label: this.$t('计划结束时间'),
          label: this.$t('perfMod.planEndDate'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return $parseTime(scope.row.planInvoiceCompleteDate)
            } else {
              return (
                <el-date-picker
                  v-model={scope.row.planInvoiceCompleteDate}
                  type="date"
                  disabled={this.disabled}
                  format={this.$formatDatePicker}
                  value-format="yyyy-MM-dd"
                />
              )
            }
          }
        },
        { prop: 'extAcceptanceDate', label: () => this.$t('cusEntry.supplement20250205.acceptancePeriod'), width: 120 },  // 承兑期限
        { prop: 'extAcceptanceRatio', label: () => this.$t('cusEntry.supplement20250205.acceptanceRatioPercentage'), width: 130 },   // 承兑比例%
        { prop: 'extWireTransferRatio', label: () => this.$t('cusEntry.supplement20250205.telegraphicTransferRatioPercentage'), width: 130 },  // 电汇比例%
        { prop: 'paymentNode', label: () => this.$t('cusEntry.supplement20250205.paymentNode') },  // 付款节点
        { prop: 'paymentApplyNo', label: () => this.$t('contractMod.paymentApplyNumber') },  // 付款申请单号
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          fixed: 'right',
          render: (h, scope) => {
            let text = ''
            if (scope.row.payFlag === 'Y') {
              text = this.$t('purSettlementMod.payment')
            }
            let btn =
              <el-button type="text" disabled={this.disabled} onClick={() => this.$emit('delete', scope)}>{ this.$t("components.common.delete") }</el-button>
            if (text) {
              return (
                <div>
                  <el-button type="text" disabled={this.disabled} onClick={() => this.handOver(scope.row)}>
                    { text }
                  </el-button>
                  {btn}
                </div>
              )
            }
            return btn
          }
        }
      ]
    }
  },
  computed: {

  },
  watch: {},
  created () {},
  mounted () {
  },
  methods: {
    handOver (row) {
      this.$emit('handover', row)
    },
    changeType (val, row) {
      let obj = this.milestoneTypeList.find(item => item.value === val)
      row.performTemplLineId = obj ? obj.id : null
    }
  }
}
</script>
<style scoped lang="scss">
</style>
