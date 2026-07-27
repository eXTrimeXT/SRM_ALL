<template>
  <el-row>
    <el-col v-if="tableHeader.length" :span="24">
      <div class="header">
        <span class="title">价格综合分析</span>
        <el-col :span="24">
          <el-form-item label="报价与预算差异分析" prop="budgetPriceDiff">
            <el-input v-model="baseForm.budgetPriceDiff" type="textarea" placeholder="请填写报价与预算差异分析" :disabled="readonly" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="前期采购情况" prop="previousPurchase">
            <el-input v-model="baseForm.previousPurchase" type="textarea" placeholder="请填写前期采购情况" :disabled="readonly" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="厂家报价差异分析" prop="manufacturerAnalysis">
            <el-input v-model="baseForm.manufacturerAnalysis" type="textarea" placeholder="请填写厂家报价差异分析" :disabled="readonly" />
          </el-form-item>
        </el-col>
      </div>
      <div class="header">
        <span class="title">报价过程情况</span>
        <el-button
          v-if="!readonly"
          type="primary"
          style="margin-left:10px"
          @click="handleAdd(null)"
        >
          新增
        </el-button>
      </div>
      <el-table
        ref="singleTable"
        :data="tableData"
        :border="true"
      >
        <el-table-column
          type="index"
          width="60"
          label="序号"
        />
        <el-table-column
          v-for="item in tableHeader"
          :key="item.label"
          :prop="item.value"
          :label="item.label"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row[item.value]" :disabled="readonly" />
          </template>
        </el-table-column>
        <el-table-column
          v-if="!readonly"
          fixed="right"
          label="操作"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleDelete(scope.$index)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>
</template>
<script>
export default {
  components: {
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    table: {
      type: Array,
      default: () => ([])
    },
    tableHeader: {
      type: Array,
      default: () => ([])
    }
  },
  data () {
    return {
      // tableData: []
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    },
    tableData: {
      get: function () {
        return this.table
      },
      set: function (val) {
        this.$emit('update:caNegotiateExtendTable', val)
      }
    }
  },
  watch: {
    table: {
      immediate: true,
      handler (newValue) {
        if (!newValue.length) {
          let list = ['首次报价（万元）']
          list.map(item => {
            this.handleAdd(item)
          })
        }
      }
    }
  },
  // mounted () {
  //   if (!this.table.length) {
  //     let list = ['首次报价（万元）', '第一次谈判后报价（万元）（谈判时间以及领导）', '降幅（万元）', '第二次谈判后报价（万元）（谈判时间以及领导）']
  //     list.map(item => {
  //       this.handleAdd(item)
  //     })
  //   }
  // },
  methods: {
    handleDelete (i) {
      this.tableData.splice(i, 1)
    },
    handleAdd (str) {
      let child = {}
      let index = 0
      for (let item of this.tableHeader) {
        child[item.value] = ''
        str && index == 0 && (child[item.value] = str)
        index++
      }
      this.tableData.push(child)
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.header {
  margin:10px 0;
  .title {
    font-weight:bold;
  }
}
</style>
