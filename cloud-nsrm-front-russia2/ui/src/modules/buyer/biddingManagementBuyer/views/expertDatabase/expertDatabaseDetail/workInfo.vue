<template>
  <div class="work-info" style="margin-bottom: 20px">
    <h3>{{ $t('dataConfMod.workInfo') }}</h3>

    <p v-if="!isReadonly">
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      :data="workRecordListData"
      style="width: 100%;"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--工作单位-->
      <el-table-column
        align="center"
        prop="workUnit"
        :label="$t('dataConfMod.workUnit')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.workUnit" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--岗位-->
      <el-table-column
        align="center"
        prop="position"
        :label="$t('components.orgPositionSel.position')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.position" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--入职时间-->
      <el-table-column
        align="center"
        prop="entryDate"
        :label="$t('dataConfMod.entryDate')"
        width="170"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-date-picker
            v-model="row.entryDate"
            type="date"
            :format="$formatDatePicker"
            :placeholder="$t('bidMod.datePicker')"
            :disabled="isReadonly"
          />
        </template>
      </el-table-column>

      <!--离职日期-->
      <el-table-column
        align="center"
        prop="quitDate"
        :label="$t('dataConfMod.quitDate')"
        width="170"
      >
        <template v-slot="{ row }">
          <el-date-picker
            v-model="row.quitDate"
            type="date"
            :format="$formatDatePicker"
            :placeholder="$t('bidMod.datePicker')"
            :disabled="isReadonly"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="!isReadonly"
        fixed="right"
        align="center"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="scope">
          <!--删除-->
          <el-button
            type="text"
            @click="deleteRow(scope.$index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 工作信息
 */
export default {
  name: 'WorkInfo',
  props: {
    workRecordList: {
      type: [Array, Object],
      required: true
    },
    isReadonly: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    workRecordListData: {
      get: function () {
        return this.workRecordList
      },
      set: function (val) {
        this.$emit('update:workRecordList', val)
      }
    }
  },
  methods: {
    /* 新增行 */
    addRow () {
      this.workRecordListData.push({
        workUnit: '', // 工作单位
        position: '', // 岗位
        entryDate: '', // 入职日期
        quitDate: '' // 离职日期
      })
    },

    /* 删除行 */
    deleteRow (index) {
      this.workRecordListData.splice(index, 1)
    }
  }
}
</script>
