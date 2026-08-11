<template>
  <el-container
    class="orderStorageEdit"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.oldStorage')"
          name="1"
        >
          <el-table
            ref="table"
            :data="table1"
            stripe
            border
            highlight-current-row
          >
            <el-table-column
              type="index"
              align="center"
              fixed
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.status')"
              prop="warehouseReceiptStatus"
              :formatter="formatterStatus"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptId')"
              prop="warehouseReceiptId"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.categoryName')"
              prop="categoryName"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.materialCode')"
              prop="materialCode"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryQuantity')"
              prop="deliveryQuantity"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
              prop="warehouseReceiptQuantity"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber')"
              prop="warehouseReceiptNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptRowNum')"
              prop="warehouseReceiptRowNum"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.organizationName')"
              prop="organizationName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
              prop="orderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
              prop="vendorCode"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.vendorName')"
              prop="vendorName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
              prop="deliveryNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.contactNumber')"
              prop="contactNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.receivedFactory')"
              prop="receivedFactory"
            />
            <el-table-column
              width="120"
              :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
              prop="inventoryPlace"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')"
              prop="returnOrderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryDate')"
              prop="deliveryDate"
              :formatter="formatterDate"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.confirmTime')"
              prop="confirmTime"
              :formatter="formatterDate"
            />
          </el-table>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.reversalStorage')"
          name="2"
        >
          <el-table
            ref="table"
            :data="table2"
            stripe
            border
            highlight-current-row
          >
            <el-table-column
              type="index"
              align="center"
              fixed
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.status')"
              prop="warehouseReceiptStatus"
              :formatter="formatterStatus"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptId')"
              prop="warehouseReceiptId"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.categoryName')"
              prop="categoryName"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.materialCode')"
              prop="materialCode"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryQuantity')"
              prop="deliveryQuantity"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
              prop="warehouseReceiptQuantity"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber')"
              prop="warehouseReceiptNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptRowNum')"
              prop="warehouseReceiptRowNum"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.organizationName')"
              prop="organizationName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
              prop="orderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
              prop="vendorCode"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.vendorName')"
              prop="vendorName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
              prop="deliveryNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.contactNumber')"
              prop="contactNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.receivedFactory')"
              prop="receivedFactory"
            />
            <el-table-column
              width="120"
              :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
              prop="inventoryPlace"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')"
              prop="returnOrderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryDate')"
              prop="deliveryDate"
              :formatter="formatterDate"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.confirmTime')"
              prop="confirmTime"
              :formatter="formatterDate"
            />
          </el-table>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.newStorage')"
          name="3"
        >
          <el-table
            ref="table"
            :data="table3"
            stripe
            border
            highlight-current-row
          >
            <el-table-column
              type="index"
              align="center"
              fixed
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.status')"
              prop="warehouseReceiptStatus"
              :formatter="formatterStatus"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptId')"
              prop="warehouseReceiptId"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.categoryName')"
              prop="categoryName"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.materialCode')"
              prop="materialCode"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryQuantity')"
              prop="deliveryQuantity"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
              prop="warehouseReceiptQuantity"
            >
              <template slot-scope="scope">
                <el-input v-model.number="scope.row.warehouseReceiptQuantity" />
              </template>
            </el-table-column>
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber')"
              prop="warehouseReceiptNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptRowNum')"
              prop="warehouseReceiptRowNum"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.organizationName')"
              prop="organizationName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
              prop="orderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
              prop="vendorCode"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.vendorName')"
              prop="vendorName"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
              prop="deliveryNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.contactNumber')"
              prop="contactNumber"
            />
            <el-table-column
              width="130"
              :label="$t('orderMod.buyerOrderSynergy.receivedFactory')"
              prop="receivedFactory"
            />
            <el-table-column
              width="120"
              :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
              prop="inventoryPlace"
            />
            <el-table-column
              width="150"
              :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')"
              prop="returnOrderNumber"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.deliveryDate')"
              prop="deliveryDate"
              :formatter="formatterDate"
            />
            <el-table-column
              width="100"
              :label="$t('orderMod.buyerOrderSynergy.confirmTime')"
              prop="confirmTime"
              :formatter="formatterDate"
            />
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="back">
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="submit"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime, adaptDictData } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import { getDictItemList } from '@/api/common'
import { orderStorageApi } from 'modb@/orderManagementBuyer/api'

export default {
  name: 'OrderStorageEdit',
  components: { CToolbar },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      table1: [],
      table2: [],
      table3: [],
      orderStatusOpts: []
    }
  },
  created () {
    const { row } = this.$attrs.params
    this.table1 = row
    this.table2 = row.map(i => ({
      ...i,
      warehouseReceiptQuantity: i.warehouseReceiptQuantity
        ? i.warehouseReceiptQuantity * -1
        : null
    }))
    this.table3 = row.map(i => ({ ...i, warehouseReceiptQuantity: null }))
  },
  mounted () {
    // 字典信息查询
    const dictionaryCodes = [{ dictCode: 'RECEIVED_STATUS' }]
    getDictItemList(dictionaryCodes).then(res => {
      const [RECEIVED_STATUS] = res.data
      this.orderStatusOpts = adaptDictData(RECEIVED_STATUS.RECEIVED_STATUS)
    })
  },
  methods: {
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    formatterStatus (row, column, cellValue, index) {
      return this.getLabel(this.orderStatusOpts, cellValue)
    },
    formatterDate (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    back () {
      this.$emit('tab-remove', 'orderStorageEdit')
      this.__setTabTodo('orderStorageList.getQuerydata')
    },
    submit () {
      if (this.table3.some(i => !i.warehouseReceiptQuantity)) {
        this.$message({
          type: 'error',
          message: this.$t('orderMod.buyerOrderSynergy.storageReversalMsg1')
        })
        return
      }
      const data = {
        originalList: this.table1,
        writeOffList: this.table2,
        newList: this.table3
      }
      orderStorageApi.writeOff(data).then(res => {
        this.$message({ type: 'success', message: res.message })
        this.back()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.orderStorageEdit {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
