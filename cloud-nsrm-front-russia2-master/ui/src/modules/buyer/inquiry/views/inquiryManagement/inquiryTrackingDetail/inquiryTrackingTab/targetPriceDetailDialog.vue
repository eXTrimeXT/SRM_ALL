<template>
  <SrmDialog
    :title="status ? $t('bidMod.setTargetPrice') : $t('bidMod.viewTargetPrice')"
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
        min-width="100"
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
        min-width="100"
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

      <!--预计采购量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.requireQuantity')"
        width="100"
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

      <el-table-column
        align="center"
        prop="notaxTargrtPrice"
        :label="$t('bidMod.notaxTargrtPrice')"
        width="130"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.notaxTargetPrice"
            v-input-format="{ type: 'float' }"
            :disabled="!status"
          />
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        v-if="status && targetPriceItemList.length > 0"
        type="primary"
        @click="saveTargetPrice"
      >
        {{ $t("common.submit") }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 设定目标价/目标价查看 弹窗
 */
import { inqBuyerHttp } from '@/modules/buyer/inquiry/api'

export default {
  name: 'TargetPriceDetailDialog',

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    status: {
      type: Boolean,
      default: false
    },
    projectId: {
      type: [String, Number],
      required: true
    }
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

  created () {
    this.getTargetPriceData()
  },

  methods: {
    /* 查询目标价 */
    async getTargetPriceData () {
      const response = await inqBuyerHttp.select.getTargetPrice(this.projectId)
      if (response) {
        this.targetPriceItemList = response.data || []
      }
    },

    /* 保存设定目标价 */
    async saveTargetPrice () {
      // 校验
      for (const item of this.targetPriceItemList) {
        if (!item.notaxTargetPrice) {
          this.$message.warning(this.$t('bidMod.inpTargetPrice'))
          return
        }
      }

      const response = await inqBuyerHttp.select.setTargetPrice(
        this.projectId,
        this.targetPriceItemList.map(item => {
          return {
            notaxTargetPrice: item.notaxTargetPrice,
            souItemId: item.souItemId
          }
        })
      )

      if (response) {
        this.$message.success(this.$t('bidMod.targetPriceSuccess'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
