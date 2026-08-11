<template>
  <div class="wrapper">
    <div class="header">
      <span class="red">*</span>
      <!-- 工作履历全称 -->
      {{ $t("cusEntry.supplement20250205.workHistoryFullName") }}
      <!-- <span class="red">（请各专家如实填写工作履历及亲属工作信息，后期如有因隐瞒信息导致的评标审计问题，涉及专家需承担追溯后果。）</span> -->
      <span class="red">{{ $t("cusEntry.supplement20250205.disclosureNotice") }}</span>
    </div>
    <div class="btns mg-10">
      <el-button v-if="!readonly" type="primary" @click="add">
        <!-- 新增 -->
        {{ $t("common.add") }}
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
    >
      <!-- <el-table-column
        type="index"
        label="序号"
        width="60"
      /> -->
      <el-table-column
        type="index"
        :label="$t('components.common.sort')"
        width="60"
      />

      <!-- <el-table-column
        prop="workUnit"
        label="工作单位名称（必须填写单位全称）"
      > -->
      <el-table-column
        prop="workUnit"
        :label="$t('cusEntry.supplement20250205.workUnitName')"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.workUnit" />
          <span v-else>{{ scope.row.workUnit }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="entryDate"
        label="工作时间从"
      > -->
      <el-table-column
        prop="entryDate"
        :label="$t('cusEntry.supplement20250205.workStartDateFrom')"
      >

      <!-- 开始时间 -->
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.entryDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            :placeholder="$t('components.common.startTime')"
            :picker-options="{
              disabledDate: time => {
                return scope.row.quitDate ? time.getTime() > new Date(scope.row.entryDate).getTime() : false
              }
            }"
          />
          <span v-else>{{ scope.row.entryDate }}</span>
        </template>
      </el-table-column>
      <!-- 工作时间到 -->
      <el-table-column
        prop="quitDate"
        :label="$t('cusEntry.supplement20250205.workEndDate')"
      >
      <!-- 结束时间 -->
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.quitDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            :placeholder="$t('components.common.endTime')"
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
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
      > -->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('components.headers.operation')"
        width="100"
      >
        <template v-slot="scope">
          <el-button type="text" @click="deleteRow(scope)">
            <!-- 删除 -->
           {{ $t("components.common.delete") }}
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
