<template>
  <!-- 阶梯价 -->
  <SrmDialog
    :title="$t('bidMod.ladderPrice')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div class="header">
      <div class="header-row">
        <div class="header-item">
          {{ $t('common.materialCode') }}：
          <span class="header-value">{{ row.materialCode }}</span>
        </div>
        <div class="header-item">
          {{ $t('common.materialName') }}：
          <span class="header-value">{{ row.materialName }}</span>
        </div>
        <div class="header-item">
          {{ $t('bidMod.taxRate') }}：
          <span class="header-value">{{ row.taxRate }}%</span>
        </div>
      </div>
    </div>
    <div class="table">
      <div v-if="!readonly" class="btn">
        <el-button type="primary" @click="add">
          {{ $t('common.add') }}
        </el-button>
      </div>
      <el-table
        class="mt-10"
        border
        stripe
        :data="tableData"
      >
        <!-- 序号 -->
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="80"
        />
        <!-- 数量从 -->
        <el-table-column
          prop="moreNum"
          :label="$t('cusEntry.supplement20250121.moreNum')"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.moreNum" v-input-format="{type:'integer',negative:false}" :disabled="readonly" />
          </template>
        </el-table-column>
        <!-- 数量至 -->
        <el-table-column
          prop="lessNum"
          :label="$t('cusEntry.supplement20250121.lessNum')"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.lessNum" v-input-format="{type:'integer',negative:false}" :disabled="readonly" />
          </template>
        </el-table-column>
        <!-- 单位 -->
        <el-table-column
          prop="unit"
          :label="$t('dataConfMod.unit')"
        >
          <template v-slot="scope">
            <DictSelect
              v-model="scope.row.unit"
              code="unit"
              :disabled="readonly"
            />
          </template>
        </el-table-column>
        <!-- 未税单价 -->
        <el-table-column
          prop="priceTax"
          :label="$t('orderMod.untaxedPrice')"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.priceTax" v-input-format="{type: 'float', digits: 2, negative: false}" :disabled="readonly" @change="val => getRatePrice(val,scope)" />
          </template>
        </el-table-column>
        <!-- 含税单价 -->
        <el-table-column
          prop="ratePrice"
          :label="$t('bidMod.unitPrice_price')"
          :render-header="_addStarToColumn"
        />
        <!-- 参考价 -->
        <el-table-column
          prop="referPrice"
          :label="$t('cusEntry.sup.extReferencePrice')"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.referPrice" v-input-format="{type: 'float', digits: 4, negative: false}" :disabled="readonly" />
          </template>
        </el-table-column>

        <el-table-column
          v-if="!readonly"
          prop="operation"
          :label="$t('common.operation')"
          width="100"
        >
          <template v-slot="scope">
            <el-button type="text" @click="deleteRow(scope)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>

export default {
  name: 'LadderDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    row: {
      type: Object,
      default () {
        return {}
      }
    },
    value: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      tableData: []
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
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleConfirm () {
      // TODO 需要加校验
      this.$emit('confirm', this.tableData)
    },
    add () {
      this.tableData.push({
        moreNum: null,
        lessNum: null,
        unit: null,
        priceTax: null,
        ratePrice: null,
        referPrice: null
      })
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    getRatePrice (val, scope) {
      let taxRate = this.row.taxRate
      let taxNum = taxRate / 100
      let ratePrice = scope.row.priceTax * (1 + taxNum)
      ratePrice = Math.round(ratePrice * 10000) / 10000
      scope.row.ratePrice = ratePrice
      this.tableData[scope.$index] = scope.row
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  .header-row {
    display: flex;
    align-items: center;
    .header-item {
      & + .header-item {
        margin-left: 20px;
      }
    }
  }
}

.table {
  margin-top: 10px;
}

.mt-10 {
  margin-top: 10px;
}

</style>
