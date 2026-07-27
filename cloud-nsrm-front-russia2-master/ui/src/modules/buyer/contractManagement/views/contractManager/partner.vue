<template>
  <div
    data-key="payPlan"
    class="pay-plan-wrapper"
  >
    <div>
      <el-button
        v-if="!editable && isFrameworkAgreement && showPlus"
        type="primary"
        style="margin: 0 10px 10px 0"
        class="detail-pbtn"
        @click="addRow"
      >
        {{ $t("common.add") }}
      </el-button>
    </div>
    <template v-if="visible">
      <base-table
        stripe
        :data="payPlans"
        :columns="columns"
        border
        max-height="400"
        :empty-text="$t('common.noData')"
        @deleteOnePaymentPlan="deleteOnePaymentPlan"
        @failure="failure"
        @launchPaymentRequest="launchPaymentRequest"
      >
        <template
          slot="milestoneHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="partnerType"
          slot-scope="scope"
        >
          <el-select
            v-model="payPlans[scope.$index].partnerType"
            :disabled="
              (editable && !!scope.row.sourceId) ||
                illegal == 'view' || editable
            "
            @change="parntererTypeHandler"
          >
<!--            :disabled="isFrameworkAgreement === 'N'"-->
            <el-option
              :label="$t('contractMod.owner')"
              value="甲方"
            />
<!--            :disabled="isFrameworkAgreement === 'N'"-->
            <el-option
              :label="$t('contractMod.partyB')"
              value="乙方"
            />
            <el-option
              :label="$t('contractMod.partyC')"
              value="丙方"
            />
          </el-select>
        </template>
        <template
          slot="partnerName"
          slot-scope="scope"
        >
          <!-- {{editable}} -->
          <!-- && scope.row.partnerType === '甲方'-->
          <el-autocomplete
            v-if="scope.row.partnerType === '乙方'"
            v-model="payPlans[scope.$index].partnerName"
            style="width: 100%;"
            :disabled="
              (editable && !!scope.row.sourceId) ||
                illegal == 'view' || editable
            "
            :fetch-suggestions="querySearch"
            @change="changeHandle"
          />
          <span v-else-if="payPlans[scope.$index].partnerName != '' && payPlans[scope.$index].partnerName != null">
            {{ payPlans[scope.$index].partnerName }}
          </span>
          <organization-selector
            v-else
            :ref="'ou_' + scope.$index"
            v-model="scope.row.ouId"
            :disabled="editable || !!scope.row.sourceId"
            :parent-id="-1"
            :scope="scope"
            :placeholder="$t('common.pleaseSelect')"
            node-type="COMPANY"
            @select="buHandler"
          />
        </template>
        <!-- 代表人 -->
        <template
          slot="milestoneExplainHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="contactName"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].contactName"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 联系电话 -->
        <template
          slot="milestoneDateHeader"
          slot-scope="scope"
        >
          <i class="toRequired">*</i>{{ scope.column.label }}
        </template>
        <template
          slot="phone"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].phone"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 地址信息 -->
        <template
          slot="address"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].address"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 传真 -->
        <template
          slot="fax"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].fax"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 开户银行 -->
        <template
          slot="bankName"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].bankName"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 银行账号 -->
        <template
          slot="bankAccount"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].bankAccount"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 邮编 -->
        <template
          slot="postCode"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].postCode"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
        <!-- 纳税人识别号 -->
        <template
          slot="taxPayer"
          slot-scope="scope"
        >
          <el-input
            v-model="payPlans[scope.$index].taxPayer"
            :disabled="(editable && !!scope.row.sourceId) || illegal == 'view' || editable"
            @change="changeHandle"
          />
        </template>
      </base-table>
    </template>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import BaseTable from 'lib@/components/BaseTable'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'PayPlan',
  components: { QuickSearch, BaseTable, OrganizationSelector },
  model: {
    prop: 'payPlans',
    event: 'change'
  },
  props: {
    illegal: String,
    materialListData: {},
    isBuyer: {},
    payPlans: {
      type: Array,
      default: () => []
    },
    contractType: {},
    visible: {
      type: Boolean,
      default: false
    },
    fields: {
      type: Array,
      default: () => []
    },
    context: {
      default: () => {}
    },
    vendorName: {
      type: String,
      default: ''
    },
    showPlus: {
      type: Boolean,
      default: true
    },
    // 是否框架协议
    isFrameworkAgreement: {
      type: String,
      default: ''
    },
    showLaunchPaymentRequest: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      milestoneStatus: [],
      parntererNameList: [],
      columns: [
        {
          attrs: {
            type: 'index',
            align: 'center',
            minWidth: '100'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('contractMod.partnerType'), // 伙伴类型
            prop: 'partnerType'
          },
          slot: 'partnerType'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: this.$t('contractMod.partnerName'), // 伙伴名称
            prop: 'partnerName'
          },
          slot: 'partnerName'
        },
        {
          attrs: {
            align: 'center',
            width: '120',
            label: this.$t('contractMod.represent'), // 代表人
            prop: 'contactName'
          },
          slot: 'contactName'
        },
        {
          attrs: {
            align: 'center',
            width: '150',
            label: this.$t('contractMod.mobileNumber'), // 联系电话
            prop: 'phone'
          },
          slot: 'phone'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: this.$t('components.address.addressInfo'), // 地址信息
            prop: 'address'
          },
          slot: 'address'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: '传真', // 传真
            prop: 'fax'
          },
          slot: 'fax'
        },
        {
          attrs: {
            align: 'center',
            showOverflowTooltip: true,
            minWidth: '150',
            label: this.$t('contractMod.openingBank'), // 开户行
            prop: 'bankName'
          },
          slot: 'bankName'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: this.$t('contractMod.bankAccount'), // 银行账号
            prop: 'bankAccount'
          },
          slot: 'bankAccount'
        },
        {
          slot: 'postCode',
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('contractMod.postcode'), // 邮编
            prop: 'postCode'
          }
        },
        {
          attrs: {
            align: 'center',
            showOverflowTooltip: true,
            minWidth: '100',
            label: this.$t('dataConfMod.taxPayer'), // 纳税人识别号
            prop: 'taxPayer'
          },
          slot: 'taxPayer'
        }
      ]
    }
  },
  computed: {
    // MIAN_CONTRACT_ADD 合同新增
    // SUPPLEMENTAL_AGREEMENT 补充协议
    // MIAN_CONTRACT_ALTER 合同变更
    editable () {
      console.log(this.contractType)
      if (!this.isBuyer) return true
      return (
        !this.showPlus || this.contractType === 'SUPPLEMENTAL_AGREEMENT'
      )
    }
  },
  created () {
    if (this.showLaunchPaymentRequest) {
      this.columns.push({
        attrs: {
          align: 'center',
          label: this.$t('common.operation'), // 操作
          fixed: 'right',
          width: 130
        },
        operations: [
          {
            key: 'deleteAction',
            event: 'launchPaymentRequest',
            name: this.$t('contractMod.initiatePaymentRequest'), // 发起付款申请
            attrs: { type: 'text' }
          }
        ]
      })
    } else {
      this.columns.push({
        attrs: {
          align: 'center',
          label: this.$t('common.operation'), // 操作
          fixed: 'right'
        },
        operations: [
          {
            key: 'deleteAction',
            event: 'deleteOnePaymentPlan',
            show: scope => {
              if (!this.showPlus || !this.isBuyer) {
                return false
              }
              if (this.contractType === 'MIAN_CONTRACT_ADD') {
                return true
              }
              if (this.contractType === 'MIAN_CONTRACT_ALTER') {
                return !scope.row.sourceId
              }
              if (this.contractType === 'SUPPLEMENTAL_AGREEMENT') {
                return false
              }
            },
            name: this.$t('common.delete'), // 删除
            attrs: { type: 'text' }
          },
          {
            key: 'failure',
            show: scope => {
              if (!this.showPlus || !this.isBuyer) {
                return false
              }
              if (this.contractType === 'MIAN_CONTRACT_ADD') {
                // 新增
                return false
              }
              if (this.contractType === 'MIAN_CONTRACT_ALTER') {
                // 变更
                return (
                  scope.row.sourceId &&
                  (scope.row.partnerType === '丙方' ||
                    scope.row.partnerType === '甲方')
                ) //
              }
              if (this.contractType === 'SUPPLEMENTAL_AGREEMENT') {
                // 补充协议
                return false
              }
            },
            event: 'failure',
            name: this.$t('common.inactive'), // 失效
            attrs: { type: 'text' }
          }
        ]
      })
    }
  },
  mounted () {
  },
  methods: {
    transformData (data) {
      const alreadyExist = this.payPlans
        .filter(i => i.partnerType === '甲方')
        .map(i => i.partnerName)
      return data.filter(i => !alreadyExist.includes(i.organizationName))
    },
    buHandler (node, value, scope) {
      scope.row.partnerName = node ? node.organizationName : null
      scope.row.ouId = node ? node.organizationId : null
      scope.row.ouCode = node ? node.organizationCode : null
      scope.row.ouName = node ? node.organizationName : null
    },
    querySearch (queryString, cb) {
      var parntererNameList = this.parntererNameList
      var results = queryString
        ? parntererNameList.filter(item => item.value.indexOf(queryString) > -1)
        : parntererNameList
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    parntererTypeHandler (value) {
      switch (value) {
        case '甲方':
          // 从物料清单中去重筛选出可选业务实体
          const list = this.materialListData
            .filter(i => i.buId)
            .map(i => ({ address: i.buName, value: i.buName }))
          this.parntererNameList = list
          break
        case '乙方':
          const vendorName = this.vendorName
          this.parntererNameList = vendorName
            ? [{ address: vendorName, value: vendorName }]
            : []
          break
        case '丙方':
          this.parntererNameList = []
          break
        default:
          this.parntererNameList = []
      }
      this.changeHandle()
    },
    launchPaymentRequest (scope) {
      this.$emit('launchPaymentRequest', scope)
    },
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
        checkAll: this.payPlans.length - -1 || 1,
        ouId: null,
        ouCode: null,
        ouName: null,
        partnerName: null
      })
    },
    failure ({ row, $index }) {
      row.enable = 'N'
      this.changeHandle()
    },
    deleteOnePaymentPlan ({ row, $index }) {
      this.payPlans.splice($index, 1)
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
      const form = this.context.mergeForm
      if (row.payRatio && form.excludeTaxAmount) {
        row.excludeTaxPayAmount = (form.excludeTaxAmount * row.payRatio) / 100
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
