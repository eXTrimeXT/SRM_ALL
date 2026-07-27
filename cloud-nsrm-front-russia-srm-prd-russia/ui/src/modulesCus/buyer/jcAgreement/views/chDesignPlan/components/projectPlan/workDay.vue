<template>
  <div class="wrapper">
    <div v-if="!readonly" class="btns mb-10">
      <el-button type="primary" @click="addNew">
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
        prop="dataSta"
        label="数据统计"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.dataSta" :disabled="readonly" />
          <span v-else>{{ row.dataSta }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="reqSta"
        label="需求分析"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.reqSta" :disabled="readonly" />
          <span v-else>{{ row.reqSta }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="supDev"
        label="供方资源开发"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.supDev" :disabled="readonly" />
          <span v-else>{{ row.supDev }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="planWrite"
        label="策划方案编写"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.planWrite" :disabled="readonly" />
          <span v-else>{{ row.planWrite }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="inqPro"
        label="询比价环节"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.inqPro" :disabled="readonly" />
          <span v-else>{{ row.inqPro }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="facApl"
        label="定厂申请"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.facApl" :disabled="readonly" />
          <span v-else>{{ row.facApl }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="proSign"
        label="方案签批"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.proSign" :disabled="readonly" />
          <span v-else>{{ row.proSign }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="conSign"
        label="合同签署"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.conSign" :disabled="readonly" />
          <span v-else>{{ row.conSign }}</span>
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
</style>
