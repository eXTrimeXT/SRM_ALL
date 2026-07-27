<template>
  <div class="wrapper">
    <div class="header">
      <span class="red">*</span>
      亲属工作单位全称
      <span class="red">（请各专家如实填写工作履历及亲属工作信息，后期如有因隐瞒信息导致的评标审计问题，涉及专家需承担追溯后果。亲属为第一关联关系人，包含父母、配偶、子女、兄弟姐妹。）</span>
    </div>
    <div class="btns mg-10">
      <el-button v-if="!readonly" type="primary" @click="add">
        新增
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        width="60"
      />

      <el-table-column
        prop="relativeType"
        label="与本人的亲属关系"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.relativeType" />
          <span v-else>{{ scope.row.relativeType }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="workUnit"
        label="工作单位名称（必须填写单位全称）"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.workUnit" />
          <span v-else>{{ scope.row.workUnit }}</span>
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
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  methods: {
    add () {
      this.tableData.push({})
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.mg-10 {
  margin: 10px 0;
}
</style>
