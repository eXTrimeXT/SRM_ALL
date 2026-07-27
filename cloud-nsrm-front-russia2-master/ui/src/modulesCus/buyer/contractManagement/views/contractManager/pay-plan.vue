<template>
  <div
    data-key="payPlan"
    class="pay-plan-wrapper"
  >
    <div>
      <el-button
        v-if="!editable"
        type="primary"
        style="margin: 0 10px 10px 0"
        class="detail-pbtn"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </div>
    <template v-if="visible">
      <BaseTable
        stripe
        :data="payPlans"
        :columns="columns"
        border
        :empty-text="$t('common.noData')"
        @deleteOnePaymentPlan="deleteOnePaymentPlan"
        @launchPaymentRequest="launchPaymentRequest"
      >
        <!-- paymentStage -->
        <template
          slot="paymentStageHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="paymentStage"
          slot-scope="scope"
        >
          <!-- {{isBuyer}}{{scope.row.isEdit}} {{editable}} {{scope.row.sourceId}} -->
          <DictSelect
            v-model="payPlans[scope.$index].paymentStage"
            code="PAYMENT_STAGE"
            :disabled="
              (isBuyer ? editable && scope.row.isEdit == 'Y' && !!scope.row.sourceId : true) ||
                illegal == 'view' || editable
            "
            @change="changeHandle"
          />
        </template>
        <!-- payExplain-->
        <template
          slot="payExplainHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="payExplain"
          slot-scope="scope"
        >
          <DictSelect
            v-model="payPlans[scope.$index].payExplain"
            code="payExplain"
            :disabled="
              (isBuyer ? editable && scope.row.isEdit == 'Y' && !!scope.row.sourceId : true) ||
                illegal == 'view' || editable
            "
            custom-select-type="payExplain"
            @change="changeHandle"
          />
        </template>
        <!-- dateNum -->
        <template
          slot="dateNumHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="dateNum"
          slot-scope="scope"
        >
          <DictSelect
            v-model="payPlans[scope.$index].dateNum"
            code="PAYMENT_PERIOD"
            :disabled="
              (isBuyer ? editable && scope.row.isEdit == 'Y' && !!scope.row.sourceId : true) ||
                illegal == 'view' || editable
            "
            @change="changeHandle"
          />
        </template>
        <!-- paymentRatio -->
        <template
          slot="paymentRatioHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="paymentRatio"
          slot-scope="scope"
        >
          <!-- {{scope.row.paidAmount}} -->
          <el-input
            v-model="payPlans[scope.$index].paymentRatio"
            v-input-format="{ type: 'number' }"
            :disabled="(isBuyer ? editable && scope.row.isEdit !== 'Y' : true) || illegal == 'view' || editable"
            @input="setExcludeTaxPayAmount(scope.row)"
            @change="changeHandle"
          />
        </template>
        <!-- stagePaymentAmount -->
        <template
          slot="stagePaymentAmountHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="stagePaymentAmount"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].stagePaymentAmount"
            v-input-format="{ type: 'number' }"
            :disabled="true"
            @change="changeHandle"
          />
        </template>
        <!-- plannedPaymentDate -->
        <template
          slot="plannedPaymentDateHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="plannedPaymentDate"
          slot-scope="scope"
        >
          <el-date-picker
            v-model="payPlans[scope.$index].plannedPaymentDate"
            :disabled="
              (isBuyer ? editable && scope.row.isEdit == 'Y' && !!scope.row.sourceId : true) ||
                illegal == 'view' || editable
            "
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            @change="changeHandle"
          />
        </template>
        <!-- payMethod -->
        <template
          slot="payMethodHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="payMethod"
          slot-scope="scope"
        >
          <DictSelect
            v-model="payPlans[scope.$index].payMethod"
            code="PAYMENT_MODE"
            :disabled="
              (isBuyer ? editable && scope.row.isEdit == 'Y' && !!scope.row.sourceId : true) ||
                illegal == 'view' || editable
            "
            @change="changeHandle"
          />
        </template>
      </BaseTable>
      <!-- <div title="添加" >
        <i class="el-icon-plus plus"></i>
      </div> -->
    </template>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import BaseTable from 'lib@/components/BaseTable'
import DictSelect from '@/library/components/c-select/dict-select'
// import { contract } from '@/service/modules/cm'

// import { contract } from '@/service/modules/cm'

export default {
  name: 'PayPlan',
  components: { QuickSearch, BaseTable, DictSelect },
  model: {
    prop: 'payPlans',
    event: 'change'
  },
  props: {
    illegal: String,
    isBuyer: {},
    payPlans: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    },
    contractType: {},
    fields: {
      type: Array,
      default: () => []
    },
    context: {
      default: () => {}
    },
    showPlus: {
      type: Boolean,
      default: true
    },
    showLaunchPaymentRequest: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      payModeList: [],
      milestoneStatus: [],
      columns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.paymentPeriod'),
            prop: 'paymentPeriod'
          }
        },
        {
          attrs: {
            align: 'center',
            width: '130',
            label: (t) => t.$t('contractMod.paymentStage'),
            prop: 'paymentStage'
          },
          slot: 'paymentStage',
          headerSlot: 'paymentStageHeader'
        },
        {
          attrs: {
            align: 'center',
            width: '150',
            label: (t) => t.$t('contractMod.payExplain'),
            prop: 'payExplain'
          },
          slot: 'payExplain',
          headerSlot: 'payExplainHeader'
        },
        {
          attrs: {
            align: 'center',
            width: '130',
            label: (t) => t.$t('contractMod.dateNum'),
            prop: 'dateNum'
          },
          slot: 'dateNum',
          headerSlot: 'dateNumHeader'
        },
        {
          attrs: {
            align: 'center',
            // showOverflowTooltip: true,
            minWidth: '100',
            label: (t) => t.$t('contractMod.paymentRatio'),
            prop: 'paymentRatio'
          },
          headerSlot: 'paymentRatioHeader',
          slot: 'paymentRatio'
        },
        {
          slot: 'stagePaymentAmount',
          headerSlot: 'stagePaymentAmountHeader',
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('cusEntry.contractMod.stagePaymentAmount'),
            prop: 'stagePaymentAmount'
          }
        },
        {
          slot: 'plannedPaymentDate',
          headerSlot: 'plannedPaymentDateHeader',
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.plannedPaymentDate'),
            prop: 'plannedPaymentDate'
          }
        },
        {
          attrs: {
            align: 'center',
            width: '140',
            label: (t) => t.$t('contractMod.paymentMethod'),
            prop: 'paymentMethod'
          },
          slot: 'payMethod',
          headerSlot: 'payMethodHeader'
        }
      ]
    }
  },
  computed: {
    editable () {
      if (!this.isBuyer) return true
      return !this.showPlus || this.contractType === 'SUPPLEMENTAL_AGREEMENT'
    }
  },
  created () {
    if (this.showLaunchPaymentRequest) {
      this.columns.push({
        attrs: {
          align: 'center',
          label: () => this.$t('common.operation'),
          fixed: 'right',
          width: 130
        },
        operations: [
          {
            key: 'deleteAction',
            event: 'launchPaymentRequest',
            name: this.$t('contractMod.initiatePaymentRequest'),
            attrs: { type: 'text' }
          }
        ]
      })
    } else {
      this.columns.push({
        attrs: {
          align: 'center',
          label: () => this.$t('common.operation'),
          fixed: 'right'
        },
        operations: [
          {
            key: 'deleteAction',
            event: 'deleteOnePaymentPlan',
            show: (scope) => {
              if (this.showPlus && this.isBuyer) {
                if (this.contractType === 'MIAN_CONTRACT_ADD') {
                  return true
                }
                if (this.contractType === 'MIAN_CONTRACT_ALTER') {
                  return !scope.row.sourceId
                }
                if (this.contractType === 'SUPPLEMENTAL_AGREEMENT') {
                  return false
                }
              }
              return false
            },
            name: this.$t('common.delete'),
            attrs: { type: 'text' }
          }
        ]
      })
    }
  },
  mounted () {
  },
  methods: {
    launchPaymentRequest (scope) {
      this.$emit('launchPaymentRequest', scope)
    },
    changeHandle () {
      this.$emit('change', this.payPlans)
    },
    addRow () {
      this.payPlans.push({
        paymentPeriod: this.payPlans.length - -1 || 1
        // payStage: this.payPlans.length - -1 || 1
      })
    },
    deleteOnePaymentPlan ({ row, $index }) {
      this.payPlans.splice($index, 1)
      this.changeHandle()
    },
    getPaymentTypeObj (val, scope) {
      this.payPlans[scope.$index].payTypeId = val ? val.payTypeId : ''
      this.payPlans[scope.$index].payType = val ? val.payType : ''
      this.payPlans[scope.$index].payExplain = val ? val.payExplain + '' : ''

      this.$nextTick(() => {
        this.$set(this.payPlans, scope.$index, this.payPlans[scope.$index])
      })
      this.changeHandle()
    },
    setExcludeTaxPayAmount (row) {
      const form = this.context.mergeForm
      if (row.paymentRatio && form.includeTaxAmount) {
        let paidAmount = row.paidAmount
        let stagePaymentAmount = (form.includeTaxAmount * row.paymentRatio) / 100
        if (stagePaymentAmount < paidAmount) {
          // 不能小于对应阶段的已付金额
          this.$message.warning(this.$t('contractMod.msgNoLessThan'))
          row.stagePaymentAmount = ''
        } else {
          row.stagePaymentAmount = stagePaymentAmount
        }
      } else {
        row.excludeTaxPayAmount = ''
      }
      this.changeHandle()
    }
  }
}
</script>
<style>
/* .pay-plan-wrapper .el-table th {
  background-color: #fff !important;
} */
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
.plus-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-top: none;
}
.plus {
  font-size: 18px;
  font-weight: bold;
}
.pay-plan-td {
  text-align: center;
}
</style>
