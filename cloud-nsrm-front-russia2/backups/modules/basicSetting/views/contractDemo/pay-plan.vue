<template>
  <div class="pay-plan-wrapper">
    <template v-if="visible">
      <base-table
        stripe
        :data="payPlans"
        :columns="columns"
        border
        empty-text="点击下方加号新增数据"
        @deleteOnePaymentPlan="deleteOnePaymentPlan"
      >
        <!-- milestone -->
        <template
          slot="milestoneHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="milestone"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].milestone"
            @change="changeHandle"
          />
        </template>
        <!-- milestoneExplain -->
        <template
          slot="milestoneExplainHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="milestoneExplain"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].milestoneExplain"
            @change="changeHandle"
          />
        </template>
        <!-- milestoneDate -->
        <template
          slot="milestoneDateHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="milestoneDate"
          slot-scope="scope"
        >
          <el-date-picker
            v-model="payPlans[scope.$index].milestoneDate"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            @change="changeHandle"
          />
        </template>
        <!-- payType -->
        <template
          slot="payType"
          slot-scope="scope"
        >
          <quick-search
            :pre-query-data="{ 't.PAY_TYPE_STATUS': 'EFFECTIVE' }"
            :show-input="payPlans[scope.$index].payType"
            show-key="payType"
            :scope-data="scope"
            name="scc_contract_templ_pay_type_display"
            @close-quicksearch="getPaymentTypeObj"
          />
        </template>
        <!-- payRatio -->
        <template
          slot="payRatio"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].payRatio"
            v-input-format="{ type: 'number' }"
            @input="setExcludeTaxPayAmount(scope.row)"
            @change="changeHandle"
          />
        </template>
        <!-- payMethod -->
        <template
          slot="payMethod"
          slot-scope="scope"
        >
          <el-select
            v-model="payPlans[scope.$index].payMethod"
            @change="changeHandle"
          >
            <el-option
              v-for="item in payModeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </template>
      </base-table>
      <div
        title="添加"
        class="plus-wrapper"
        @click="addRow"
      >
        <i class="el-icon-plus plus" />
      </div>
    </template>
    <table
      v-else
      cellspacing="0"
      cellpadding="0"
      width="100%"
      border="1"
    >
      <tr class="firstRow">
        <th
          v-for="item in fields"
          :key="item"
          valign="top"
          style="word-break: break-all;"
          scope="col"
        >
          {{ $t(`contractMod.${item}`) }}
        </th>
      </tr>
      <tr
        v-for="(item, index) in payPlans"
        :key="index"
      >
        <td
          v-for="field in fields"
          :key="field"
          class="pay-plan-td"
          valign="top"
          style="word-break: break-all;"
        >
          {{ item ? item[field] : "" }}
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import BaseTable from 'lib@/components/BaseTable'
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'

export default {
  name: 'PayPlan',
  components: { QuickSearch, BaseTable },
  model: {
    prop: 'payPlans',
    event: 'change'
  },
  props: {
    payPlans: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    },
    fields: {
      type: Array,
      default: () => []
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
            label: t => t.$t('contractMod.milestoneStage'),
            prop: 'milestoneStage'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.milestone'),
            prop: 'milestone'
          },
          slot: 'milestone',
          headerSlot: 'milestoneHeader'
        },
        {
          attrs: {
            align: 'center',
            width: '120',
            label: t => t.$t('contractMod.milestoneExplain'),
            prop: 'milestoneExplain'
          },
          slot: 'milestoneExplain',
          headerSlot: 'milestoneExplainHeader'
        },
        {
          attrs: {
            align: 'center',
            width: '150',
            label: t => t.$t('contractMod.milestoneDate'),
            prop: 'milestoneDate'
          },
          slot: 'milestoneDate',
          headerSlot: 'milestoneDateHeader'
        },
        {
          // 转换为对应中文 此处原本值为里程碑状态字典code
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.milestoneStatus'),
            prop: 'milestoneStatus',
            formatter: (row, column, cellValue, index) =>
              this.getLabel('milestoneStatus', cellValue)
          }
        },
        {
          attrs: {
            align: 'center',
            width: '130',
            label: t => t.$t('contractMod.payType'),
            prop: 'payType'
          },
          slot: 'payType'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.payStage'),
            prop: 'payStage'
          }
        },
        {
          attrs: {
            align: 'center',
            showOverflowTooltip: true,
            minWidth: '100',
            label: t => t.$t('contractMod.payExplain'),
            prop: 'payExplain'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.payStatus'),
            prop: 'payStatus'
          }
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('contractMod.payRatio'),
            prop: 'payRatio',
            width: '120'
          },
          slot: 'payRatio'
        },
        {
          attrs: {
            align: 'center',
            width: '140',
            label: t => t.$t('contractMod.paymentMethod'),
            prop: 'paymentMethod'
          },
          slot: 'payMethod'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.paidAmount'),
            prop: 'paidAmount'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('contractMod.excludeTaxPayAmount'),
            prop: 'excludeTaxPayAmount'
          }
        },
        {
          attrs: {
            align: 'center',
            label: '操作',
            fixed: 'right'
          },
          operations: [
            {
              key: 'deleteAction',
              event: 'deleteOnePaymentPlan',
              name: '删除',
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },
  mounted () {
    // 里程碑类型
    getDictItem('MILESTONE_STATUS').then(res => {
      this.milestoneStatus = adaptDictData(res.data, 'dict')
    })
    // 发布范围
    getDictItem('PAYMENT_MODE').then(res => {
      this.payModeList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    getLabel (feild, value) {
      if (feild === 'milestoneStatus' && this.milestoneStatus.length) {
        const target = this.milestoneStatus.find(i => i.value == value)
        return target ? target.label : value
      }
    },
    changeHandle () {
      this.$emit('change', this.payPlans)
    },
    addRow () {
      this.payPlans.push({
        milestoneStage: this.payPlans.length - -1 || 1,
        payStage: this.payPlans.length - -1 || 1
      })
    },
    deleteOnePaymentPlan ({ row, $index }) {
      if (row.payPlanId) {
        this.$http({
          url: '/api-cm/contract/payPlan/batchDelete',
          method: 'POST',
          data: [row.payPlanId],
          loading: true
        })
          .then(data => {
            this.payPlans.splice($index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.payPlans.splice($index, 1)
      }
      this.changeHandle()
    },
    getPaymentTypeObj (val, scope) {
      this.payPlans[scope.$index].payTypeId = val ? val.payTypeId : ''
      this.payPlans[scope.$index].payType = val ? val.payType : ''
      this.payPlans[scope.$index].payExplain = val ? val.payExplain : ''

      this.$nextTick(() => {
        this.$set(this.payPlans, scope.$index, this.payPlans[scope.$index])
      })
      this.changeHandle()
    },
    setExcludeTaxPayAmount (row) {
      // if (row.payRatio && this.contractHead.excludeTaxAmount) {
      //   row.excludeTaxPayAmount =
      //     (this.contractHead.excludeTaxAmount * row.payRatio) / 100;
      // } else {
      //   row.excludeTaxPayAmount = "";
      // }
      this.changeHandle()
    }
  }
}
</script>
<style scoped>
.pay-plan-wrapper /deep/ .el-table th {
  background-color: #fff !important;
}
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
