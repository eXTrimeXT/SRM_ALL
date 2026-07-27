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
name2code.set('行号', 'lineNumber')
name2code.set('来源单号', 'sourceNumber')
name2code.set('来源单行号', 'sourceLineNumber')
name2code.set('物料编码', 'materialCode')
name2code.set('物料名称', 'materialName')
name2code.set('品类名称', 'categoryName')
name2code.set('含税单价', 'taxedPrice')
name2code.set('未税单价', 'untaxedPrice')
name2code.set('数量', 'contractQuantity')
name2code.set('含税金额', 'amount')
name2code.set('单位', 'unitName')
name2code.set('业务实体', 'buName')
name2code.set('库存组织', 'invName')
name2code.set('交货地点', 'tradingLocations')
name2code.set('税率', 'taxRate')
name2code.set('未税金额', 'unAmount')
name2code.set('价格有效期从', 'startDate')
name2code.set('价格有效期至', 'endDate')
name2code.set('规格型号', 'specification')
name2code.set('原产地', 'placeOfOrigin')
name2code.set('是否含安装调试业务', 'isInstallDebug')
name2code.set('保质期(月)', 'shelfLife')
name2code.set('行备注', 'lineRemark')
name2code.set('项目编号', 'itemNumber')
name2code.set('项目名称', 'itemName')
name2code.set('任务编号', 'taskNumber')
name2code.set('任务名称', 'taskName')
name2code.set('制造商', 'manufacturer')
name2code.set('发运地', 'shipFrom')
name2code.set('目的地', 'destination')
name2code.set('是否危险化学品', 'isDangerChemistry')
name2code.set('税额', 'taxQuota')

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
