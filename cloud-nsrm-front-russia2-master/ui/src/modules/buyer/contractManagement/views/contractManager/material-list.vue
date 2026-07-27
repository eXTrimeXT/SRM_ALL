<template>
  <div
    data-key="materialList"
    class="material-list-wrappers"
  >
    <div>
      <el-button
        v-if="editable && showPlus"
        type="primary"
        style="margin: 0 10px 10px 0"
        class="detail-pbtn"
        @click="addRow"
      >
        {{ $t("common.add") }}
      </el-button>
      <slot name="btns" />
    </div>
    <BaseTable
      stripe
      :data="materialEditableRows.slice((page - 1) * pageSize, page * pageSize)"
      :columns="columns"
      border
      :empty-text="$t('contractMod.clickToNewData')"
      max-height="400"
      @deleteItem="deleteItem"
      @selection-change="handleSelectionChange"
    >
      <template #ceeaOuId="scope">
        <span v-if="scope.row.sourceNumber">{{ scope.row.ceeaOuName }}</span>
        <QuickSearch
          :show-input="scope.row.ceeaOuName"
          show-key="ouGroupName"
          :disabled="!!scope.row.buId || !editable"
          :scope-data="scope.row"
          name="ceea_base_ou_group"
          @close-quicksearch="getOuGroup"
        />
      </template>
      <!-- 业务实体 -->
      <template #buId="scope">
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          scope.row.buName
        }}</span>
        <OrganizationSelector
          v-else
          :ref="'ou_' + scope.$index"
          v-model="scope.row.buId"
          :disabled="
            (editable ? false : !!scope.row.ceeaOuId) || illegal == 'view'
          "
          :jump-login="jumpLogin"
          :placeholder="$t('common.pleaseSelect')"
          :parent-id="-1"
          :scope="scope"
          node-type="OU"
          @select="buHandler"
        />
      </template>
      <!-- 库存组织 -->
      <template
        slot="invIdHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template #invId="scope">
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          scope.row.invName
        }}</span>
        <OrganizationSelector
          v-else
          :ref="'inv_' + scope.$index"
          v-model="scope.row.invId"
          :disabled="
            !editable || !showPlus
          "
          :jump-login="jumpLogin"
          :parent-id="buId"
          :placeholder="$t('common.pleaseSelect')"
          :scope="scope"
          node-type="INV"
          @select="invHandler"
        />
      </template>
      <template
        slot="tradingLocationsHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template #tradingLocations="scope">
        <DictSelect
          v-model="scope.row.tradingLocations"
          style="width: 100%"
          :disabled="!editable || !showPlus"
          :code="scope.row.invId"
          :custom-select-type="scope.row.invId ? 'RECEIVE_ADDRESS' : ''"
          @change-value="(val, element) => changeSiteInfo(scope.row, element)"
        />
      </template>
      <template
        slot="materialCodeHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template
        slot="materialCode"
        slot-scope="scope"
      >
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          scope.row.materialCode
        }}</span>
        <QuickSearch
          v-else
          :disabled="(!editable || !showPlus) || !(scope.row.invId)"
          :show-input="scope.row.materialCode"
          show-key="materialCode"
          :pre-query-data="{ 'o.ORGANIZATION_ID': scope.row.invId }"
          :scope-data="scope"
          name="scc_base_material_item_contract"
          @close-quicksearch="getItemObj"
        />
      </template>
      <!-- 含税单价 -->
      <template
        slot="taxedPrice"
        slot-scope="scope"
      >
        <!--        v-if="scope.row.sourceNumber || !editable"-->
        <span>{{ scope.row.taxedPrice }}</span>
        <!--        <el-input-->
        <!--          v-else-->
        <!--          v-model="scope.row.taxedPrice"-->
        <!--          :disabled="!editable || !showPlus"-->
        <!--          @input="inputHandle(scope)"-->
        <!--          @change="inputChangeHandle(scope)"-->
        <!--        />-->
      </template>
      <!-- 未税单价 -->
      <template
        slot="untaxedPriceHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template
        slot="untaxedPrice"
        slot-scope="scope"
      >
        <span v-if="scope.row.sourceNumber || !editable">{{
          scope.row.untaxedPrice
        }}</span>
        <el-input
          v-else
          v-model="scope.row.untaxedPrice"
          :disabled="!editable || !showPlus"
          @input="inputHandle(scope)"
          @change="inputChangeHandle(scope)"
        />
      </template>
      <!-- 数量 -->
      <template
        slot="contractQuantityHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template
        slot="contractQuantity"
        slot-scope="scope"
      >
        <span v-if="scope.row.sourceNumber || !editable">{{
          scope.row.contractQuantity
        }}</span>
        <el-input
          v-else
          v-model="scope.row.contractQuantity"
          :disabled="!editable || !showPlus"
          @change="inputChangeHandle(scope)"
          @input="inputHandle(scope)"
        />
      </template>
      <template
        slot="taxRateHeader"
        slot-scope="scope"
      >
        <i class="toRequired">*</i>{{ scope.column.label }}
      </template>
      <template #taxRate="scope">
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          $getDictLabel("tax", scope.row.taxKey)
        }}</span>
        <DictSelect
          v-else
          v-model="scope.row.taxKey"
          code="tax"
          :disabled="!editable || !showPlus"
          @change="taxHandler(scope)"
        />
      </template>

      <template #ceeaInitAmount="scope">
        <el-input
          v-model="scope.row.ceeaInitAmount"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaInitAmount"
          :disabled="!editable || !showPlus"
        />
      </template>
      <!--单位-->
      <template #unitName="scope">
        {{ $getDictLabel('unit', scope.row.unitName) }}
      </template>
      <template #ceeaInitNumber="scope">
        <el-input
          v-model="scope.row.ceeaInitNumber"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaInitNumber"
          :disabled="!editable || !showPlus"
        />
      </template>
      <template #ceeaUsedAmount="scope">
        <el-input
          v-model="scope.row.ceeaUsedAmount"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaUsedAmount"
          :disabled="!editable || !showPlus"
        />
      </template>
      <template #ceeaUsedNumber="scope">
        <el-input
          v-model="scope.row.ceeaUsedNumber"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaUsedNumber"
          :disabled="!editable || !showPlus"
        />
      </template>

      <template #startDate="scope">
        <span v-if="scope.row.sourceNumber">{{ scope.row.startDate }}</span>
        <el-date-picker
          v-else
          v-model="scope.row.startDate"
          type="date"
          :disabled="!editable || !showPlus"
          value-format="yyyy-MM-dd"
          :format="$formatDatePicker"
          :picker-options="pickerOptions"
          :placeholder="$t('vendorMod.datePicker')"
        />
      </template>
      <template #endDate="scope">
        <span v-if="scope.row.sourceNumber">{{ scope.row.endDate }}</span>
        <el-date-picker
          v-else
          v-model="scope.row.endDate"
          type="date"
          :disabled="!editable || !showPlus"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          :placeholder="$t('vendorMod.datePicker')"
        />
      </template>
      <template #specification="scope">
        <el-input
          v-model="scope.row.specification"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #manufacturer="scope">
        <el-input
          v-model="scope.row.manufacturer"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #isDangerChemistry="scope">
        <DictSelect
          v-model="scope.row.isDangerChemistry"
          code="YES_OR_NO"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #placeOfOrigin="scope">
        <el-input
          v-model="scope.row.placeOfOrigin"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #isInstallDebug="scope">
        <el-select
          v-model="scope.row.isInstallDebug"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        >
          <el-option
            :id="1"
            :label="$t('common.yes')"
            value="Y"
          />
          <el-option
            :id="2"
            :label="$t('common.no')"
            value="N"
          />
        </el-select>
      </template>
      <template #shelfLife="scope">
        <el-input
          v-model="scope.row.shelfLife"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #lineRemark="scope">
        <el-input
          v-model="scope.row.lineRemark"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #itemNumber="scope">
        <el-input
          v-model="scope.row.itemNumber"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #itemName="scope">
        <el-input
          v-model="scope.row.itemName"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #taskNumber="scope">
        <el-input
          v-model="scope.row.taskNumber"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #taskName="scope">
        <el-input
          v-model="scope.row.taskName"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #shipFrom="scope">
        <el-input
          v-model="scope.row.shipFrom"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #destination="scope">
        <el-input
          v-model="scope.row.destination"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
      <template #tradeTerm="scope">
        <DictSelect
          v-model="scope.row.tradeTerm"
          code="trade_clause"
          :disabled="!editable || !!scope.row.sourceId || !showPlus"
        />
      </template>
    </BaseTable>
    <el-pagination
      align="center"
      :current-page="page"
      :page-sizes="[10]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="materialEditableRows.length"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({ 'tax': [] })

export default {
  name: 'MaterialList',
  components: {
    BaseTable,
    QuickSearch,
    OrganizationSelector
  },
  props: {
    illegal: String,
    startDate: {},
    endDate: {},
    contractType: {},
    jumpLogin: {
      type: Boolean,
      default: true
    },
    isBuyer: {},
    showPlus: {
      type: Boolean,
      default: true
    },
    value: {
      type: Array,
      default: () => []
    },
    buId: {
      type: String,
      default: ''
    },
    // 是否框架协议
    isFrameworkAgreement: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      dictClass: dictClass,
      selections: [],
      page: 1,
      pageSize: 10,
      width: 206,
      pickerOptions: {
        disabledDate: time => {
          const startDate = new Date(this.startDate)
          startDate.setHours(0)
          startDate.setMinutes(0)
          startDate.setSeconds(0)
          startDate.setMilliseconds(0)
          const endDate = new Date(this.endDate)
          endDate.setHours(0)
          endDate.setMinutes(0)
          endDate.setSeconds(0)
          endDate.setMilliseconds(0)
          return (
            time.getTime() >= endDate.getTime() ||
            time.getTime() < startDate.getTime()
          )
        }
      },
      columns: [
        {
          slot: 'invId',
          headerSlot: 'invIdHeader',
          attrs: {
            minWidth: '180',
            align: 'center',
            prop: 'invId',
            label: context => context.$t('contractMod.invId')
          }
        },
        {
          slot: 'tradingLocations',
          headerSlot: 'tradingLocationsHeader',
          attrs: {
            minWidth: '300',
            align: 'center',
            prop: 'tradingLocations',
            label: context => context.$t('contractMod.tradingLocations')
          }
        },
        {
          slot: 'materialCode',
          headerSlot: 'materialCodeHeader',
          attrs: {
            minWidth: '140',
            align: 'center',
            prop: 'materialCode',
            label: context => context.$t('contractMod.materialCode')
          }
        },
        {
          attrs: {
            minWidth: '140',
            align: 'center',
            prop: 'materialName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.materialName')
          }
        },
        {
          attrs: {
            minWidth: '140',
            align: 'center',
            prop: 'categoryName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.categoryName')
          }
        },
        { // 未税单价
          slot: 'untaxedPrice',
          headerSlot: 'untaxedPriceHeader',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'untaxedPrice',
            label: context => context.$t('bid_mod.untaxedPrice')
          }
        },
        {
          slot: 'taxedPrice',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxedPrice',
            label: context => context.$t('bid_mod.taxUnitPrice')
          }
        },
        {
          slot: 'contractQuantity',
          headerSlot: 'contractQuantityHeader',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'contractQuantity',
            label: context => context.$t('contractMod.contractQuantity')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'amount',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.amount2')
          }
        },
        {
          slot: 'unitName', // ceeaInitAmount
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.unitName')
          }
        },
        {
          headerSlot: 'taxRateHeader',
          slot: 'taxRate', // taxRate 用于 计算值 taxKey 用于唯一值
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxRate',
            label: context => context.$t('contractMod.taxRate')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unAmount',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.unAmount')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxQuota',
            label: context => context.$t('contractMod.taxQuota')
          }
        },
        {
          slot: 'ceeaUsedAmount',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'ceeaUsedAmount',
            label: t => t.$t('contractMod.usedAmount')
          }
        },
        {
          slot: 'ceeaUsedNumber',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'ceeaUsedNumber',
            label: t => t.$t('contractMod.usedNumber')
          }
        },
        {
          slot: 'startDate',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'startDate',
            label: t => t.$t('bid_mod.priceStartTime')
          }
        },
        {
          slot: 'endDate',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'endDate',
            label: t => t.$t('bid_mod.priceEndTime')
          }
        },
        {
          slot: 'specification',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'specification',
            label: context => context.$t('contractMod.specification')
          }
        },
        {
          slot: 'manufacturer',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'manufacturer',
            label: context => context.$t('contractMod.manufacturer')
          }
        },
        {
          slot: 'isDangerChemistry',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'isDangerChemistry',
            label: context => context.$t('contractMod.isDangerChemistry')
          }
        },
        {
          slot: 'placeOfOrigin',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'placeOfOrigin',
            label: context => context.$t('contractMod.placeOfOrigin')
          }
        },
        {
          slot: 'isInstallDebug',
          attrs: {
            minWidth: '140',
            align: 'center',
            prop: 'isInstallDebug',
            label: context => context.$t('contractMod.isInstallDebug')
          }
        },
        {
          slot: 'shelfLife',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'shelfLife',
            label: context => context.$t('contractMod.shelfLife')
          }
        },
        {
          slot: 'lineRemark',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'lineRemark',
            label: context => context.$t('contractMod.lineRemark')
          }
        },
        {// 项目编号
          slot: 'itemNumber',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'itemNumber',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.itemNumber')
          }
        },
        {// 项目名称
          slot: 'itemName',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'itemName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.itemName')
          }
        },
        {// 任务编号
          slot: 'taskNumber',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taskNumber',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.taskNumber')
          }
        },
        {// 任务名称
          slot: 'taskName',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taskName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.taskName')
          }
        },
        {
          slot: 'shipFrom',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'shipFrom',
            label: context => context.$t('contractMod.shipFrom')
          }
        },
        {
          slot: 'destination',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'destination',
            label: context => context.$t('contractMod.destination')
          }
        },
        // 贸易方术语
        {
          slot: 'tradeTerm',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'tradeTerm',
            label: context => context.$t('bidMod.tradeTerm')
          }
        },
        // 来源单号
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'sourceNumber',
            label: context => context.$t('contractMod.sourceNumber')
          }
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right'
          },
          operations: [
            {
              key: 'deleteAction',
              event: 'deleteItem',
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
              name: this.$t('common.delete'),
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },
  computed: {
    materialEditableRows () {
      return this.value.filter(i => {
        if (i.handleMark === undefined || i.handleMark === 0) {
          return true
        }
        return false
      })
    },
    editable () {
      console.log(this.isBuyer)
      console.log(this.contractType)
      console.log(this.showPlus)
      if (!this.isBuyer) return false
      return (
        !this.showPlus ||
        this.contractType === 'MIAN_CONTRACT_ADD' ||
        this.contractType === 'MIAN_CONTRACT_ALTER'
      ) // 补充协议 合同变更
    }
  },
  watch: {
    value: {
      handler (newList, oldList) {
        this.update()
        this.$emit('change', this.value)
      },
      deep: true,
      immediate: true
    }
  },
  created () {},
  methods: {
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element.receiver)
      this.$set(row, 'receiveTelephone', element.receiverPhone)
      this.$set(row, 'tradingLocations', element.siteName)
    },
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
      this.page = 1
      this.pageSize = val
    },
    handleCurrentChange (val) {
      console.log(`当前页: ${val}`)
      this.page = val
    },
    handleSelectionChange (rows) {
      this.selections = rows
      this.$emit('select', rows)
    },
    getOuGroup (value, scope) {
      scope.ceeaOuName = value.ouGroupName || ''
      scope.ceeaOuNumber = value.ouGroupCode || ''
      scope.ceeaOuId = value.ouGroupId || ''
      console.log(scope)
    },
    taxHandler (scope) {
      const value = scope.row.taxKey // key值
      const dRowObj = this.dictClass.getDictDetail('tax', value)
      scope.row.taxRate = dRowObj.key // 计算值
      this.inputHandle(scope)
    },
    buHandler (node, value, scope) {
      this.$refs[`inv_${scope.$index}`].clearOptions()
      scope.row.invCode = null
      scope.row.invName = null
      scope.row.invFullPathId = null
      scope.row.invId = null
      if (value) {
        const {
          organizationId,
          organizationCode,
          organizationName,
          fullPathId
        } = node
        scope.row.buId = organizationId
        scope.row.buCode = organizationCode
        scope.row.buName = organizationName
        scope.row.buFullPathId = fullPathId
      } else {
        scope.row.buCode = null
        scope.row.buName = null
        scope.row.buFullPathId = null
        scope.row.buId = null
      }
    },
    invHandler (node, value, scope) {
      if (value) {
        const { organizationCode, organizationName, fullPathId } = node
        scope.row.invCode = organizationCode
        scope.row.invName = organizationName
        scope.row.invFullPathId = fullPathId
      } else {
        scope.row.invCode = null
        scope.row.invName = null
        scope.row.invFullPathId = null
      }
      this.$set(this.value, scope.$index, this.value[scope.$index])
    },
    getItemObj (val, scope) {
      const obj = {}
      obj.materialId = val ? val.materialId : ''
      obj.materialCode = val ? val.materialCode : ''
      obj.materialName = val ? val.materialName : ''
      obj.categoryName = val ? val.categoryName : ''
      obj.categoryId = val ? val.categoryId : ''
      obj.categoryCode = val ? val.categoryCode : ''
      obj.specification = val ? val.specification : ''
      obj.unitCode = val ? val.unit : ''
      obj.unitName = val ? val.unitName : ''
      scope.row = Object.assign(scope.row, obj)
      console.log(obj)
      this.$set(this.value, scope.$index, this.value[scope.$index])
    },
    update () {
      console.log('update material list')
      this.$emit('update:value', this.value)
    },
    addRow () {
      this.value.push({
        specification: null,
        startDate: null,
        endDate: null,
        ceeaOuId: null,
        ceeaOuName: null,
        ceeaOuNumber: null,
        invId: null,
        invName: null,
        invCode: null,
        buId: null,
        buName: null,
        buCode: null,
        handleMark: 0,
        ceeaUsedNumber: 0,
        ceeaUsedAmount: 0
      })
    },
    deleteItem (scope) {
      scope.row.handleMark = 1
      console.log(this.materialEditableRows)
      // FIXME: hack 触发数组更新
      this.value.splice(0, 0)
      if (
        this.materialEditableRows.slice(
          (page - 1) * pageSize,
          page * pageSize
        ) === 0
      ) {
        this.handleCurrentChange(1)
      }
      // this.value.splice(scope.$index, 1);
    },
    inputChangeHandle (scope) {
      const { contractQuantity, taxedPrice } = scope.row
      if (taxedPrice) {
        scope.row.taxedPrice = Number(taxedPrice).toFixed(2)
      }
      // 数量需要支持小数 暂时注释
      // if (contractQuantity) {
      //   scope.row.contractQuantity = Math.round(contractQuantity);
      // }
      this.$emit('change', this.value)
    },
    inputHandle (scope) {
      console.log(scope.row)
      const { contractQuantity, taxedPrice, taxRate, untaxedPrice } = scope.row
      if (untaxedPrice && contractQuantity) {
        const unAmount =
          parseFloat(untaxedPrice) * parseFloat(contractQuantity)
        scope.row.unAmount = unAmount.toFixed(2)
        if (taxRate) {
          const amount = Number((unAmount * (1 + taxRate / 100)).toFixed(2))
          const num = Number(contractQuantity)
          scope.row.amount = amount
          scope.row.taxQuota = Number(amount - unAmount).toFixed(2)
          scope.row.taxedPrice = amount / num
        }
        this.$emit('change', this.value)
        return false
      }

      if (taxedPrice && contractQuantity) {
        const amount = parseFloat(taxedPrice) * parseFloat(contractQuantity)
        scope.row.amount = amount.toFixed(2)
        if (taxRate) {
          const unAmount = Number((amount / (1 + taxRate / 100)).toFixed(2))
          const num = Number(contractQuantity)
          scope.row.unAmount = unAmount
          scope.row.taxQuota = Number(amount - unAmount).toFixed(2)
          scope.row.untaxedPrice = unAmount / num
        }
      }
      // console.log(this.materialEditableRows);
      this.$emit('change', this.value)
    }
  }
}
</script>
<style scoped>
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
.material-td {
  text-align: center;
}
:deep(.el-pagination) {
  margin-top:10px;
}
</style>
