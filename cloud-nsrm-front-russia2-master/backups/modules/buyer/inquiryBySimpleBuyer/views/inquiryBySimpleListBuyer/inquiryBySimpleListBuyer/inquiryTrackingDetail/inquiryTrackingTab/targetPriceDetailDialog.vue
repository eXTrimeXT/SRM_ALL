<template>
  <srm-dialog
    :title="status ? '设定目标价' : '查看目标价'"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="targetPriceItemList"
      style="width: 100%"
      border
      height="200"
      highlight-current-row
    >
      <el-table-column align="center" type="index" width="50" />
      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="100"
        :formatter="(row, column, value) => row.noCodeItem === 'Y' ? '' : value"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--t 采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 行类型-->
      <el-table-column
        align="center"
        prop="itemType"
        :label="$t('bidMod.itemType')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('DMAND_LINE_TYPE', cellValue)"
      />

      <el-table-column
        align="center"
        prop="demandQuantity"
        label="预计采购量"
        width="90"
        show-overflow-tooltip
      />
      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <el-table-column align="center" prop="notaxTargrtPrice" label="目标价（未税）" width="130">
        <template slot-scope="scope">
          <el-input v-model="scope.row.notaxTargetPrice" :disabled="!status" type="number" />
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button v-if="status" type="primary" @click="saveTargetPrice">
        {{ $t("common.submit") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 设定目标价/目标价查看 弹窗
 */

export default {
  name: 'TargetPriceDetailDialog',
  props: {
    visible: Boolean,
    status: Boolean,
    inquiryId: [String, Number]
  },
  data () {
    return {
      targetPriceItemList: [],
      priceDisabled: false
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    dialogVisible: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue) {
          this.getTargetPriceData()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询目标价 */
    getTargetPriceData () {
      this.$api.inq.inquiryBySimple.getTargetPriceData(this.inquiryId).then(data => {
        this.targetPriceItemList = data.data || []
      })
    },
    /* 保存 */
    saveTargetPrice () {
      const data = []
      for (const item of this.targetPriceItemList) {
        if (!item.notaxTargetPrice) {
          this.$message.warning('请输入目标价!')
          return
        }
        data.push({
          inquiryItemId: item.inquiryItemId,
          notaxTargetPrice: item.notaxTargetPrice
        })
      }
      this.$api.inq.inquiryBySimple.saveTargetPrice(data, this.inquiryId).then(() => {
        this.$message.success('目标价保存成功!')
        this.dialogVisible = false
        this.$emit('saveTargetPriceSuccess')
      })
    }
  }
}
</script>
