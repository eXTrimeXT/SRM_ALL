<template>
  <div
    data-key="materialList"
    class="material-list-wrappers"
  >
    <!-- <span>editable:{{editable}}</span>
    <span>showPlus:{{showPlus}}</span>
    <span>isBuyer:{{isBuyer}}</span> -->
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
      <slot name="btns"></slot>
    </div>
    <base-table
      stripe
      :data="materialEditableRows.slice((page - 1) * pageSize, page * pageSize)"
      :columns="columns"
      border
      :empty-text="$t('contractMod.clickToNewData')"
      max-height="400"
      @deleteItem="deleteItem"
      @selection-change="handleSelectionChange"
    >
      <!-- <template #ceeaOuNumber="scope">
        <show-ou-detail
          :text="scope.row.ceeaOuNumber"
          :ouId="scope.row.ceeaOuId"
        />
      </template> -->
      <template #ceeaOuId="scope">
        <span v-if="scope.row.sourceNumber">{{ scope.row.ceeaOuName }}</span>
        <quick-search
          :show-input="scope.row.ceeaOuName"
          show-key="ouGroupName"
          :disabled="!!scope.row.buId"
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
        <organization-selector
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
      <template #invId="scope">
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          scope.row.invName
        }}</span>
        <organization-selector
          v-else
          :ref="'inv_' + scope.$index"
          v-model="scope.row.invId"
          :disabled="
            (editable ? false : !!scope.row.ceeaOuId) || illegal == 'view'
          "
          :jump-login="jumpLogin"
          :parent-id="scope.row.buId"
          :placeholder="$t('common.pleaseSelect')"
          :scope="scope"
          node-type="INV"
          @select="invHandler"
        />
      </template>
      <template #tradingLocations="scope">
        <DictSelect
          v-if="!scope.row.sourceNumber && !scope.row.sourceId"
          v-model="scope.row.tradingLocations"
          :disabled="!editable || illegal == 'view'"
          :code="scope.row.invId"
          :custom-select-type="scope.row.invId ? 'RECEIVE_ADDRESS' : ''"
          @change-value="(val, element) => changeSiteInfo(scope.row, element)"
        />
      </template>
      <template
        slot="materialCode"
        slot-scope="scope"
      >
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          scope.row.materialCode
        }}</span>
        <quick-search
          v-else
          :disabled="!editable || illegal == 'view'"
          :show-input="scope.row.materialCode"
          show-key="materialCode"
          :scope-data="scope"
          name="scc_base_material_item_display"
          @close-quicksearch="getItemObj"
        />
      </template>
      <!-- 含税单价 -->
      <template
        slot="taxedPrice"
        slot-scope="scope"
      >
        <span v-if="scope.row.sourceNumber || !editable">{{
          scope.row.taxedPrice
        }}</span>
        <el-input
          v-else
          v-model="scope.row.taxedPrice"
          :disabled="!editable || illegal == 'view'"
          @input="inputHandle(scope)"
          @change="inputChangeHandle(scope)"
        />
      </template>
      <!-- 数量 -->
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
          :disabled="!editable || illegal == 'view'"
          @change="inputChangeHandle(scope)"
          @input="inputHandle(scope)"
        />
      </template>
      <template #taxRate="scope">
        <span v-if="scope.row.sourceNumber || scope.row.sourceId">{{
          $getDictLabel("tax", scope.row.taxKey)
        }}</span>
        <DictSelect
          v-else
          v-model="scope.row.taxKey"
          code="tax"
          :disabled="!editable || illegal == 'view'"
          @change="taxHandler(scope)"
        />
      </template>

      <template #ceeaInitAmount="scope">
        <el-input
          v-model="scope.row.ceeaInitAmount"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaInitAmount"
          :disabled="!editable || illegal == 'view'"
        />
      </template>
      <template #ceeaInitNumber="scope">
        <el-input
          v-model="scope.row.ceeaInitNumber"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaInitNumber"
          :disabled="!editable || illegal == 'view'"
        />
      </template>
      <template #ceeaUsedAmount="scope">
        <el-input
          v-model="scope.row.ceeaUsedAmount"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaUsedAmount"
          :disabled="!editable || illegal == 'view'"
        />
      </template>
      <template #ceeaUsedNumber="scope">
        <el-input
          v-model="scope.row.ceeaUsedNumber"
          v-input-format="{ type: 'float' }"
          :material-id="scope.row.ceeaUsedNumber"
          :disabled="!editable || illegal == 'view'"
        />
      </template>

      <template #startDate="scope">
        <span v-if="scope.row.sourceNumber">{{ scope.row.startDate }}</span>
        <el-date-picker
          v-else
          v-model="scope.row.startDate"
          type="date"
          :disabled="!editable || illegal == 'view'"
          value-format="yyyy-MM-dd"
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
          :disabled="!editable || illegal == 'view'"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          :placeholder="$t('vendorMod.datePicker')"
        />
      </template>
      <template #specification="scope">
        <el-input
          v-model="scope.row.specification"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #manufacturer="scope">
        <el-input
          v-model="scope.row.manufacturer"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #isDangerChemistry="scope">
        <el-input
          v-model="scope.row.isDangerChemistry"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #placeOfOrigin="scope">
        <el-input
          v-model="scope.row.placeOfOrigin"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #isInstallDebug="scope">
        <el-select
          v-model="scope.row.isInstallDebug"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
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
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #lineRemark="scope">
        <el-input
          v-model="scope.row.lineRemark"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #shipFrom="scope">
        <el-input
          v-model="scope.row.shipFrom"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #destination="scope">
        <el-input
          v-model="scope.row.destination"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
      <template #tradeTerm="scope">
        <el-input
          v-model="scope.row.tradeTerm"
          :disabled="!editable || !!scope.row.sourceId || illegal == 'view'"
        />
      </template>
    </base-table>
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
import ShowOuDetail from 'mod@/biddingManagementBuyer/views/biddingProject/show-ou-detail'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({ 'tax': [] })

export default {
  name: 'MaterialList',
  components: {
    BaseTable,
    ShowOuDetail,
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
          attrs: {
            type: 'selection',
            width: '50',
            align: 'center'
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'sourceNumber',
            label: context => context.$t('contractMod.sourceNumber')
          }
        },
        // ou组编号
        // {
        //   slot: "ceeaOuNumber",
        //   attrs: {
        //     minWidth: "180",
        //     align: "center",
        //     prop: "ceeaOuNumber",
        //     label: context => context.$t("bid_mod.ouNumber")
        //   }
        // },
        // ou组名称
        // {
        //   slot: "ceeaOuId",
        //   attrs: {
        //     minWidth: "180",
        //     align: "center",
        //     prop: "ceeaOuId",
        //     label: context => context.$t("bid_mod.ouName")
        //   }
        // },
        // o业务实体
        {
          slot: 'buId',
          attrs: {
            minWidth: '180',
            align: 'center',
            prop: 'buId',
            label: context => context.$t('contractMod.buId')
          }
        },
        {
          slot: 'invId',
          attrs: {
            minWidth: '180',
            align: 'center',
            prop: 'invId',
            label: context => context.$t('contractMod.invId')
          }
        },
        {
          slot: 'tradingLocations',
          attrs: {
            minWidth: '140',
            align: 'center',
            prop: 'tradingLocations',
            label: context => context.$t('contractMod.tradingLocations')
          }
        },
        {
          slot: 'materialCode',
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
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.unitName')
          }
        },
        {
          slot: 'taxRate',
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
          slot: 'ceeaInitAmount',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'ceeaInitAmount',
            label: t => t.$t('contractMod.initAmount')
          }
        },
        {
          slot: 'ceeaInitNumber',
          attrs: {
            minWidth: '150',
            align: 'center',
            prop: 'ceeaInitNumber',
            label: t => t.$t('contractMod.initNumber')
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
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'itemNumber',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.itemNumber')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'itemName',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.itemName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taskNumber',
            showOverflowTooltip: true,
            label: context => context.$t('contractMod.taskNumber')
          }
        },
        {
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
        {
          slot: 'tradeTerm',
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'tradeTerm',
            label: context => context.$t('bidMod.tradeTerm')
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
      if (!this.isBuyer) return true
      return (
        !this.showPlus ||
        this.contractType === 'MIAN_CONTRACT_ADD' ||
        this.contractType === 'SUPPLEMENTAL_AGREEMENT' ||
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
  mounted () {
  },
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
      const { contractQuantity, taxedPrice, taxRate } = scope.row
      if (taxedPrice && contractQuantity) {
        const amount = parseFloat(taxedPrice) * parseFloat(contractQuantity)
        scope.row.amount = amount.toFixed(2)
        if (taxRate) {
          const unAmount = Number((amount / (1 + taxRate / 100)).toFixed(2))
          scope.row.unAmount = unAmount
          scope.row.taxQuota = Number(amount - unAmount).toFixed(2)
        }
      }
      console.log('[inputHandle]')
      this.$emit('change', this.value)
    }
  }
}
</script>
<style>
/* .material-list-wrapper .el-table th {
  background-color: #fff !important;
} */
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
</style>
