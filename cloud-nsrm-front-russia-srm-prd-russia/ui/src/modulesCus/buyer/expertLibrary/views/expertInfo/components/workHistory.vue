<template>
  <div class="wrapper">
    <div class="header">
      <span class="red">*</span>
      工作履历全称
      <span class="red">（请各专家如实填写工作履历及亲属工作信息，后期如有因隐瞒信息导致的评标审计问题，涉及专家需承担追溯后果。）</span>
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
        prop="workUnit"
        label="工作单位名称（必须填写单位全称）"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.workUnit" />
          <span v-else>{{ scope.row.workUnit }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="entryDate"
        label="工作时间从"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.entryDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="开始时间"
            :picker-options="{
              disabledDate: time => {
                return scope.row.quitDate ? time.getTime() > new Date(scope.row.entryDate).getTime() : false
              }
            }"
          />
          <span v-else>{{ scope.row.entryDate }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="quitDate"
        label="工作时间到"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.quitDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="结束时间"
            :picker-options="{
              disabledDate: time => {
                return scope.row.entryDate ? time.getTime() < new Date(scope.row.entryDate).getTime() : false
              }
            }"
          />
          <span v-else>{{ scope.row.quitDate }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="workDay"
        label="工作时间"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.workDay"
            type="daterange"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            @change="(val) => workDayChange(scope.row,val)"
          />
          <template v-else>
            <span>{{ scope.row.entryDate }}</span>
            <span> - </span>
            <span>{{ scope.row.quitDate }}</span>
          </template>
        </template>
      </el-table-column> -->

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
    },
    workDayChange (row, val) {
      row.entryDate = val[0]
      row.quitDate = val[1]
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
