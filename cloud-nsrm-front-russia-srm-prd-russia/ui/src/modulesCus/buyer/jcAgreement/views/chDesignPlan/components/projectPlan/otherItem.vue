<template>
  <div class="wrapper">
    <div class="btns mb-10">
      <span class="title">其他</span>
      <el-button v-if="!readonly" type="primary" @click="addNew">
        {{ $t('common.add') }}
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
      <el-table-column
        prop="bidArea"
        label="招标区域"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.bidArea" :disabled="readonly" />
          <span v-else>{{ row.bidArea }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="sup"
        label="供应商"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.sup" :disabled="readonly" />
          <span v-else>{{ row.sup }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="proMon"
        label="年采购额"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.proMon" :disabled="readonly" />
          <span v-else>{{ row.proMon }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="amoPro"
        label="金额占比"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.amoPro" :disabled="readonly" />
          <span v-else>{{ row.amoPro }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="curPol"
        label="现行政策"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.curPol" :disabled="readonly" />
          <span v-else>{{ row.curPol }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="selSup"
        label="拟询价供方"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.selSup" :disabled="readonly" />
          <span v-else>{{ row.selSup }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="bidStr"
        label="招标策略"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.bidStr" :disabled="readonly" />
          <span v-else>{{ row.bidStr }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="tarSet"
        label="目标设定"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.tarSet" :disabled="readonly" />
          <span v-else>{{ row.tarSet }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
      >
        <template v-slot="{row,$index}">
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:value'],
  data () {
    return {

    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (value) {
        this.$emit('update:value', value)
      }
    }
  },
  methods: {
    addNew () {
      this.tableData.push({})
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.mb-10 {
  margin-bottom: 10px;
}
.btns {
  .title {
    margin-right:20px;
  }
}
</style>
