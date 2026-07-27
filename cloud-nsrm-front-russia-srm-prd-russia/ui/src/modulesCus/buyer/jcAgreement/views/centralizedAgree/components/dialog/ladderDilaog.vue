<template>
  <SrmDialog
    title="阶梯价"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div class="header">
      <div class="header-row">
        <div class="header-item">
          物料编码：
          <span class="header-value">{{ row.materialCode }}</span>
        </div>
        <div class="header-item">
          物料名称：
          <span class="header-value">{{ row.materialName }}</span>
        </div>
        <div class="header-item">
          税率：
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
        <el-table-column
          type="index"
          label="序号"
          width="80"
        />

        <el-table-column
          prop="moreNum"
          label="数量从"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.moreNum" v-input-format="{type:'integer',negative:false}" :disabled="readonly" />
          </template>
        </el-table-column>

        <el-table-column
          prop="lessNum"
          label="数量至"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.lessNum" v-input-format="{type:'integer',negative:false}" :disabled="readonly" />
          </template>
        </el-table-column>

        <el-table-column
          prop="unit"
          label="单位"
        >
          <template v-slot="scope">
            <DictSelect
              v-model="scope.row.unit"
              code="unit"
              :disabled="readonly"
            />
          </template>
        </el-table-column>

        <el-table-column
          prop="priceTax"
          label="未税单价"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.priceTax" v-input-format="{type: 'float', digits: 2, negative: false}" :disabled="readonly" @change="val => getRatePrice(val,scope)" />
          </template>
        </el-table-column>

        <el-table-column
          prop="ratePrice"
          label="含税单价"
          :render-header="_addStarToColumn"
        />

        <el-table-column
          prop="referPrice"
          label="参考价"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.referPrice" v-input-format="{type: 'float', digits: 4, negative: false}" :disabled="readonly" />
          </template>
        </el-table-column>

        <el-table-column
          v-if="!readonly"
          prop="operation"
          label="操作"
          width="100"
        >
          <template v-slot="scope">
            <el-button type="text" @click="deleteRow(scope)">
              删除
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
