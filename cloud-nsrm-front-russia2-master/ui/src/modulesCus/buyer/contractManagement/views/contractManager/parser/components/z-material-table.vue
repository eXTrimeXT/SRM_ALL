<template>
  <div
    :data-key="code"
    class="material-list-wrapper"
  >
    <table
      cellspacing="0"
      cellpadding="0"
      width="100%"
      border="1"
    >
      <tr class="firstRow">
        <th
          v-for="item in fields"
          :key="item.field"
          scope="col"
          valign="top"
          style="word-break: break-all;"
        >
          {{ item.name }}
        </th>
      </tr>
      <tr
        v-for="(item, index) in tableData"
        :key="index"
      >
        <td
          v-for="field in fields"
          :key="field.field"
          class="pay-plan-td"
          valign="top"
          style="word-break: break-all;text-align: center;"
        >
          {{ item ? transfrom(item[field.field]) : "" }}
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import minix from './minix'

const GenNonDuplicateID = randomLength => {
  return Number(
    Math.random()
      .toString()
      .substr(3, randomLength) + Date.now()
  ).toString(36)
}

const name2code = new Map()
name2code.set(this.$t('vendorMod.relegation.lineNumber'), 'lineNumber')  // '行号'
name2code.set(this.$t('orderMod.buyerOrderSynergy.externalNum'), 'sourceNumber')  // '来源单号'
name2code.set(this.$t('orderMod.buyerOrderSynergy.externalRowNum'), 'sourceLineNumber') // '来源单行号'
name2code.set(this.$t('common.materialCode'), 'materialCode') // '物料编码'
name2code.set(this.$t('common.materialName'), 'materialName') // '物料名称'
name2code.set(this.$t('components.category.categoryName'), 'categoryName') // '品类名称'
name2code.set(this.$t('bid_mod.taxUnitPrice'), 'taxedPrice') // '含税单价'
name2code.set(this.$t('bid_mod.untaxedPrice'), 'untaxedPrice')  // '未税单价'
name2code.set(this.$t('bid_mod.quantity'), 'contractQuantity')  // '数量'
name2code.set(this.$t('perfMod.taxAmount'), 'amount')  // '含税金额'
name2code.set(this.$t('dataConfMod.settingGuide.step3.3'), 'unitName') // '单位'
name2code.set(this.$t('components.organization.ORG'), 'buName')  // '业务实体'
name2code.set(this.$t('components.organization.INV'), 'invName')  // '库存组织'
name2code.set(this.$t('bid_mod.tradingLocations'), 'tradingLocations')  // '交货地点'
name2code.set(this.$t('components.ocr.commodityTaxRate'), 'taxRate')  // '税率'
name2code.set(this.$t('contractMod.unAmount'), 'unAmount')  // '未税金额'
name2code.set(this.$t('contractMod.price_startDate'), 'startDate')  // '价格有效期从'
name2code.set(this.$t('dataConfMod.priceExpirationDate'), 'endDate')  // '价格有效期至'
name2code.set(this.$t('vendorMod.specification'), 'specification')  // '规格型号'
name2code.set(this.$t('contractMod.placeOfOrigin'), 'placeOfOrigin')  // '原产地'
name2code.set(this.$t('contractMod.isInstallDebug'), 'isInstallDebug')  // '是否含安装调试业务'
name2code.set(this.$t('contractMod.shelfLife'), 'shelfLife')  // '保质期(月)'
name2code.set(this.$t('contractMod.lineRemark'), 'lineRemark')  // '行备注'
name2code.set(this.$t('bidMod.bidingNum'), 'itemNumber')  // '项目编号'
name2code.set(this.$t('bidMod.bidingName'), 'itemName')  // '项目名称'
name2code.set(this.$t('contractMod.taskNumber'), 'taskNumber')  // '任务编号'
name2code.set(this.$t('dataConfMod.strategyName'), 'taskName')  // '任务名称'
name2code.set(this.$t('contractMod.manufacturer'), 'manufacturer')  // '制造商'
name2code.set(this.$t('contractMod.shipFrom'), 'shipFrom')  // '发运地'
name2code.set(this.$t('contractMod.destination'), 'destination')  // '目的地'
name2code.set(this.$t('contractMod.isDangerChemistry'), 'isDangerChemistry')  // '是否危险化学品'
name2code.set(this.$t('contractMod.taxQuota'), 'taxQuota')  // '税额'

export default {
  name: 'ZTable',
  components: { BaseTable },
  mixins: [minix],
  data () {
    return {
      fields: []
    }
  },
  computed: {
    tableData () {
      console.log(this.context.materialEditableRows)
      // return this.context.materialListData;
      return this.context.materialEditableRows
    }
  },
  created () {
    const { elemRanges } = this.componentInfo
    this.fields = elemRanges.map(({ elemValue }, index) => ({
      name: elemValue,
      field: name2code.get(elemValue)
    }))
    console.log('fields', this.fields)
  },
  methods: {
    transfrom (value) {
      if (['N', 'Y'].includes(value)) {
        const map = {
          N: '是',
          Y: '否'
        }
        return map[value]
      }
      return value
    }
  }
}
</script>
<style>
.material-list-wrapper .el-table th {
  background-color: #fff !important;
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
.material-td {
  text-align: center;
}
</style>
