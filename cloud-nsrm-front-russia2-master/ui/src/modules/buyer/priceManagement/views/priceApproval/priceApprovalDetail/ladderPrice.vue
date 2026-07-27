<template>
  <srm-dialog
    :title="$t('bidMod.ladderPrice')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div class="ladder-price-form-row">
      <div class="form-col">
        <!--物料编码-->
        <span class="label">{{ $t('bidMod.itemCode') }}:</span>
        <span class="value">{{ targetNumRevealFilter(ladderPriceForm.itemCode) }}</span>
      </div>
      <div class="form-col">
        <!--物料名称-->
        <span class="label">{{ $t('bidMod.itemName') }}:</span>
        <span class="value">{{ ladderPriceForm.itemName }}</span>
      </div>
      <div class="form-col">
        <!--预计数量-->
        <span class="label">{{ $t('bidMod.demandQuantity') }}:</span>
        <span class="value">{{ ladderPriceForm.needNum }}</span>
      </div>
    </div>
    <div class="ladder-price-form-row">
      <div class="form-col">
        <!--阶梯价类型-->
        <span class="label">{{ $t('bidMod.ladderType') }}:</span>
        <span class="value">
          <el-radio
            v-model="ladderPriceForm.ladderType"
            label="standard"
            :disabled="isReadOnly"
          >
            {{ $t('bidMod.standardladderPrice') }}
          </el-radio>
          <el-radio
            v-model="ladderPriceForm.ladderType"
            label="sum"
            :disabled="isReadOnly"
          >
            {{ $t('bidMod.sumladderPrice') }}
          </el-radio>
        </span>
      </div>
    </div>

    <p v-if="!isReadOnly">
      <el-button
        type="primary"
        @click="addLadderPrice"
      >
        {{ $t("common.new") }}
      </el-button>
    </p>

    <el-table
      :data="ladderPriceTable"
      style="width: 100%"
      border
      height="155px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--t 数量从（>=）-->
      <el-table-column
        align="center"
        prop="beginQuantity"
        :label="$t('bidMod.beginQuantity')"
        width="200"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="!isReadOnly"
            v-model="scope.row.beginQuantity"
            v-input-format="{ type: 'float' }"
          />
          <span v-else>{{ scope.row.beginQuantity }}</span>
        </template>
      </el-table-column>

      <!--t 数量至（<）-->
      <el-table-column
        align="center"
        prop="endQuantity"
        :label="$t('bidMod.endQuantity')"
        width="200"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="!isReadOnly"
            v-model="scope.row.endQuantity"
            v-input-format="{ type: 'float' }"
          />
          <span v-else>{{ scope.row.endQuantity }}</span>
        </template>
      </el-table-column>

      <!--未税单价-->
      <el-table-column
        align="center"
        prop="price"
        :label="$t('bidMod.quotenotaxPrice2')"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-if="!isReadOnly"
            v-model.number="scope.row.price"
            type="number"
            :disabled="isReadOnly"
          />
          <span v-else>{{ scope.row.price }}</span>
        </template>
      </el-table-column>

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--t 操作-->
      <el-table-column
        align="center"
        :label="$t('bidMod.operation')"
      >
        <template slot-scope="{ $index }">
          <el-button
            type="text"
            :disabled="isReadOnly"
            @click="handleDelLadder($index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        v-if="!isReadOnly"
        type="primary"
        @click="saveLadderItems"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 阶梯报价
 */
import { targetNumReveal } from 'lib@/composition/origin/composition'

export default {
  name: 'LadderPrice',
  props: {
    visible: Boolean,
    editRow: Object,
    isReadOnly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      ladderPriceForm: {
        itemCode: '',
        itemName: '',
        needNum: '',
        ladderType: 'standard'
      },
      ladderPriceTable: []
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
    editRow: {
      handler (newValue) {
        if (newValue) {
          this.ladderPriceForm = {
            itemCode: newValue.itemCode || '',
            itemName: newValue.itemDesc || '',
            needNum: newValue.needNum || '',
            ladderType: newValue.ladderType || 'standard'
          }
          this.ladderPriceTable = (newValue.ladderPriceList || []).map(item => {
            return {
              ...item,
              unit: newValue.unit
            }
          })
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /* 物料编码格式化 */
    targetNumRevealFilter (value) {
      return targetNumReveal(value)
    },

    /* 新增 */
    addLadderPrice () {
      for (const i of this.ladderPriceTable) {
        if (i.beginQuantity === 0 || i.endQuantity === 0) {
          // 数量不为0！
          this.$message.warning(this.$t('cusEntry.supplement20250211.quantityNotZero'))
          return
        }
        if (!i.beginQuantity || !i.endQuantity) {
          // '请输入[数量从]和[数量至]!'
          this.$message.warning(this.$t('bidMod.common.ladderMsg4'))
          return
        }
        if (Number(i.beginQuantity) > Number(i.endQuantity)) {
          // '[数量从]不能大于[数量至]!'
          this.$message.warning(this.$t('bidMod.common.ladderMsg5'))
          return
        }
      }
      this.ladderPriceTable.push({
        beginQuantity: this.ladderPriceTable.length ? this.ladderPriceTable[this.ladderPriceTable.length - 1].endQuantity : '',
        endQuantity: '',
        unit: this.editRow.unit
      })
    },
    /* 删除 */
    handleDelLadder (index) {
      this.ladderPriceTable.splice(index, 1)
    },
    /* 保存 */
    saveLadderItems () {
      if (!this.ladderPriceForm.ladderType) {
        // '请选择阶梯价类型!'
        this.$message.warning(this.$t('bidMod.common.ladderMsg1'))
        return
      }
      if (this.ladderPriceTable.length === 0) {
        // '请设置阶梯报价!'
        this.$message.warning(this.$t('bidMod.common.ladderMsg2'))
        return
      }
      for (let x = 0; x < this.ladderPriceTable.length; x++) {
        if (this.ladderPriceTable[x].beginQuantity === 0 || this.ladderPriceTable[x].endQuantity === 0) {
          // 数量不为0！
          this.$message.warning(this.$t('cusEntry.supplement20250211.quantityNotZero'))
          return
        }
        if (
          !this.ladderPriceTable[x].beginQuantity ||
          // 最后一行的至允许为空
          (!this.ladderPriceTable[x].endQuantity && x !== (this.ladderPriceTable.length - 1))
        ) {
          // '请输入[数量从]和[数量至]!'
          this.$message.warning(this.$t('bidMod.common.ladderMsg4'))
          return
        }
        if (
          this.ladderPriceTable[x].beginQuantity &&
          this.ladderPriceTable[x].endQuantity &&
          Number(this.ladderPriceTable[x].beginQuantity) > Number(this.ladderPriceTable[x].endQuantity)
        ) {
          // '[数量从]不能大于[数量至]!'
          this.$message.warning(this.$t('bidMod.common.ladderMsg5'))
          return
        }

        if (this.ladderPriceTable[x - 1] && this.ladderPriceTable[x].beginQuantity !== this.ladderPriceTable[x - 1].endQuantity) {
          // '阶梯价不允许断层报价，接下来的一层只能从上一层的终点数量开始算!'
          this.$message.warning(this.$t('bidMod.common.ladderMsg6'))
          return
        }
        if (!this.ladderPriceTable[x].price) {
          // '请输入未税单价！'
          this.$message.warning(this.$t('bidMod.inpOrderNotaxPrice'))
          return
        }
      }
      if (
        this.ladderPriceTable[this.ladderPriceTable.length - 1].endQuantity &&
        (
          Number(this.ladderPriceForm.needNum) <
          Number(this.ladderPriceTable[0].beginQuantity) ||
          Number(this.ladderPriceForm.needNum) >
          Number(this.ladderPriceTable[this.ladderPriceTable.length - 1].endQuantity)
        )
      ) {
        // '阶梯报价未包含预计采购量区间!'
        this.$message.warning(this.$t('bidMod.common.ladderMsg7'))
        return
      }
      this.ladderPriceTable = this.ladderPriceTable.map(item => {
        return {
          ...item,
          ladderType: this.ladderType
        }
      })
      // 返回保存
      this.$emit('saveLadderPrices', {
        ladderType: this.ladderPriceForm.ladderType,
        ladderPriceList: this.ladderPriceTable
      })
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.ladder-price-form-row {
  display: flex;
  margin-bottom: 10px;
  .form-col:not(:last-child) {
    margin-right: 30px;
  }
  .form-col {
    height: 30px;
    line-height: 30px;
    .label {
      padding-right: 15px;
    }
  }
}
</style>
