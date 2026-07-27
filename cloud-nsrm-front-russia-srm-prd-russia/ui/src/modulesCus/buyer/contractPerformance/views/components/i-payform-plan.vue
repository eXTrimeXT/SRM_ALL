<!-- 合同履行计划 -->
<template>
  <IMiniTable
    :columns="columns"
    :data="dataSource"
    border
    @selection-change="selectionChange"
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'
import CPeopleSelector from '@/library/components/c-people-selector'
import { isEqual } from 'lodash'

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
      dataSource: [],
      columns: [
        { prop: 'index', type: 'index', label: '节点', width: 60 },
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
          label: this.$t('节点负责人'),
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
          label: '付款阶段',
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
          label: '付款条件',
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
          label: '付款方式',
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
                  onChange={(val) => this.payMethodChange(val, scope.row)}
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
        {
          prop: 'stagePaymentAmount',
          label: '阶段付款金额',
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.stagePaymentAmount
            } else {
              return (
                <el-input
                  v-model={scope.row.stagePaymentAmount}
                  disabled={this.disabled}
                  v-input-format={{ type: 'float', digits: 2, negative: false }}
                />
              )
            }
          }
        },
        {
          prop: 'palnPaymentDate',
          label: this.$t('计划开始时间'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.palnPaymentDate
            } else {
              return (
                <el-date-picker
                  v-model={scope.row.palnPaymentDate}
                  type="date"
                  disabled={this.disabled}
                  value-format="yyyy-MM-dd"
                />
              )
            }
          }
        },
        {
          prop: 'planInvoiceCompleteDate',
          label: this.$t('计划结束时间'),
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            return (
              <el-date-picker
                v-model={scope.row.planInvoiceCompleteDate}
                type="date"
                disabled={this.disabled}
                value-format="yyyy-MM-dd"
              />
            )
          }
        },
        {
          prop: 'extAcceptanceDate',
          label: '承兑期限',
          width: 150,
          render: (h, scope) => {
            return (
              <el-date-picker
                v-model={scope.row.extAcceptanceDate}
                type="date"
                disabled={this.disabled}
                value-format="yyyy-MM-dd"
              />
            )
          }
        },
        {
          prop: 'extAcceptanceRatio',
          label: '承兑比例%',
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.extAcceptanceRatio
            } else {
              return (
                <el-input
                  v-model={scope.row.extAcceptanceRatio}
                  disabled={this.disabled || ['HONOUR', 'WIRE_TRANSFER'].includes(scope.row.payMethod)}
                  onChange={(val) => this.extAcceptanceRatioChange(val, scope.row)}
                  v-input-format={{ type: 'float', digits: 2, negative: false }}
                />
              )
            }
          }
        },
        {
          prop: 'extWireTransferRatio',
          label: '电汇比例%',
          width: 150,
          renderHeader: this._addStarToColumn,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.extWireTransferRatio
            } else {
              return (
                <el-input
                  v-model={scope.row.extWireTransferRatio}
                  disabled={this.disabled || ['HONOUR', 'WIRE_TRANSFER'].includes(scope.row.payMethod)}
                  onChange={(val) => this.extWireTransferRatioChange(val, scope.row)}
                  v-input-format={{ type: 'float', digits: 2, negative: false }}
                />
              )
            }
          }
        },
        {
          prop: 'invoiceSpeed',
          label: this.$t('开票进度'),
          width: 150,
          render: (h, scope) => {
            return (
              <div>{this.invoiceSpeed(scope.row.stagePaymentAmount, scope.row.invoicedAmount)}</div>
            )
          }
        },
        {
          prop: 'paymentSpeed',
          label: this.$t('付款进度'),
          width: 150,
          render: (h, scope) => {
            return (
              <div>{this.paymentSpeed(scope.row.paymentAmount, scope.row.stagePaymentAmount)}</div>
            )
          }
        },
        {
          prop: 'paymentAmount',
          label: this.$t('未付款申请余额'),
          width: 150,
          render: (h, scope) => {
            return (
              <el-progress text-inside={true} stroke-width={26} percentage={this.percentage(scope.row.paymentAmount, scope.row.stagePaymentAmount)} color={this.customColorMethod}></el-progress>
            )
          }
        },
        {
          prop: 'invoiceNo',
          label: this.$t('开票单号'),
          width: 150,
          render: (h, scope) => {
            return (
              <div>{scope.row.invoiceNo}</div>
            )
          }
        },
        {
          prop: 'advanceApplyNumber',
          label: this.$t('预付款申请单号'),
          width: 150,
          render: (h, scope) => {
            return (
              <div>{scope.row.advanceApplyNumber}</div>
            )
          }
        },
        {
          prop: 'paymentApplyNumber',
          label: '付款申请单号',
          width: 150,
          render: (h, scope) => {
            return (
              <div>{scope.row.paymentApplyNumber}</div>
            )
          }
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          fixed: 'right',
          width: 130,
          render: (h, scope) => {
            let text = ''
            text = '付款申请'
            let btn =
              <el-button type="text" disabled={this.disabled} onClick={() => this.$emit('delete', scope)}>删除</el-button>
            let bol = false
            let invoiceSpeed = this.invoiceSpeed(scope.row.stagePaymentAmount, scope.row.invoicedAmount)
            let paymentSpeed = this.paymentSpeed(scope.row.paymentAmount, scope.row.stagePaymentAmount)
            if (['已开票', '开票中'].includes(invoiceSpeed) && ['付款申请中', '待付款申请'].includes(paymentSpeed)) {
              bol = true
            }
            // this.disabled ||
            return (
              <div>
                <el-button type="text" disabled={!bol} onClick={() => this.payment(scope.row)}>
                  { text }
                </el-button>
                {btn}
              </div>
            )
          }
        }
      ]
    }
  },
  computed: {

  },
  watch: {
    data: {
      handler () {
        if (isEqual(this.data, this.dataSource)) {
          return
        }
        console.log('data')
        this.dataSource = this.data
      }
    },

    dataSource: {
      handler () {
        this.$emit('change', this.dataSource)
      },
      deep: true
    }
  },
  created () {},
  mounted () {
  },
  methods: {
    extAcceptanceRatioChange (val, row) {
      if (val) {
        row.extWireTransferRatio = 100 - val
      } else {
        row.extWireTransferRatio = null
      }
    },
    extWireTransferRatioChange (val, row) {
      if (val) {
        row.extAcceptanceRatio = 100 - val
      } else {
        row.extAcceptanceRatio = null
      }
    },
    payMethodChange (val, row) {
      if (val) {
        if (val === 'WIRE_TRANSFER') { // 电汇
          row.extAcceptanceRatio = 0
          row.extWireTransferRatio = 100
        } else if (val === 'HONOUR') { // 承兑
          row.extAcceptanceRatio = 100
          row.extWireTransferRatio = 0
        } else {
          row.extAcceptanceRatio = null
          row.extWireTransferRatio = null
        }
      } else {
        row.extAcceptanceRatio = null
        row.extWireTransferRatio = null
      }
    },
    advanceApply (id) {
      this.$emit('advanceApply', id)
    },
    paymentApply (id) {
      this.$emit('paymentApply', id)
    },
    paymentSpeed (paymentAmount, invoicedAmount) {
      const payment = parseFloat(paymentAmount || 0) // 已付款金额
      const invoiced = parseFloat(invoicedAmount || 0) // 已开票金额
      if (payment < invoiced && payment != 0) {
        return '付款申请中'
      } else if (payment == invoiced && payment != 0) {
        return '已付款申请'
      } else {
        return '待付款申请'
      }
    },
    invoiceSpeed (stagePaymentAmount, invoicedAmount) {
      const invoiced = parseFloat(invoicedAmount || 0)
      const stagePayment = parseFloat(stagePaymentAmount || 0)
      if (invoiced == 0) {
        return '待开票'
      } else if (invoiced == stagePayment || invoiced > stagePayment) {
        return '已开票'
      } else {
        return '开票中'
      }
    },
    percentage (paymentAmount, stagePaymentAmount) {
      const payment = parseFloat(paymentAmount || 0)
      const stagePayment = parseFloat(stagePaymentAmount || 0)
      let aws = Math.ceil((payment / stagePayment) * 100)
      aws = aws < 100 ? aws : 100
      if (payment === 0) {
        aws = 0
      }
      return aws
    },
    customColorMethod (percentage) {
      if (percentage < 100) {
        return '#1997f2'
      } else if (percentage == 100) {
        return '#67c23a'
      }
    },
    payment (row) {
      this.$emit('payment', row)
    },
    changeType (val, row) {
      let obj = (this.milestoneTypeList || []).find(item => item.value === val)
      row.performTemplLineId = obj ? obj.id : null
      row.perPlanMilestoneId = obj ? obj.perPlanMilestoneId : null
    },
    selectionChange (row) {
      this.$emit('selection-change', row)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
