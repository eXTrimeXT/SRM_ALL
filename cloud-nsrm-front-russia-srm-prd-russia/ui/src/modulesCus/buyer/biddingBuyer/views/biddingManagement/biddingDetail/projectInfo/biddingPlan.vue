<template>
  <div>
    <!--制定招标计划-->
    <el-table
      :data="planList"
      style="width: 100%"
      border
      height="116"
    >
      <el-table-column
        align="center"
        fixed="left"
        prop="planType"
        label="/"
        :formatter="(row, column, val) => val ? $getDictLabel('SOU_BID_PLAN_TYPE', val) : ''"
        width="100"
      />
      <el-table-column
        align="center"
        prop="bidCreationDate"
        :label="$t('cusEntry.bidMod.bidCreationDate')"
        min-width="180"
      />
      <el-table-column
        align="center"
        prop="publishTime"
        :label="$t('cusEntry.bidMod.publishTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.publishTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.publishTime }}</span>
        </template>
      </el-table-column>
      <!-- 收标方式：同时收标 && 招标类型：商务时，不展示技术标截止时间、开技术标时间、技术评标时间 -->
      <el-table-column
        v-if="!techTimeHideFlag"
        align="center"
        prop="techEndTime"
        :label="$t('cusEntry.bidMod.techEndTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.techEndTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
            @change="val => techEndTimeChange(val,scope.row)"
          />
          <span v-else>{{ scope.row.techEndTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!techTimeHideFlag"
        align="center"
        prop="techOpenTime"
        :label="$t('cusEntry.bidMod.techOpenTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.techOpenTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.techOpenTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!techTimeHideFlag"
        align="center"
        prop="techEvaluationTime"
        :label="$t('cusEntry.bidMod.techEvaluationTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.techEvaluationTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.techEvaluationTime }}</span>
        </template>
      </el-table-column>
      <!-- 收标方式：同时收标 && 招标类型：技术+商务时，商务标截止时间取技术标截止时间，隐藏商务标截止时间 -->
      <el-table-column
        v-if="!busEndTimeHideFlag"
        align="center"
        prop="busEndTime"
        :label="$t('cusEntry.bidMod.busEndTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.busEndTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.busEndTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="priceOpenTime"
        :label="$t('cusEntry.bidMod.priceOpenTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.priceOpenTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.priceOpenTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="sumReportTime"
        :label="$t('cusEntry.bidMod.sumReportTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.sumReportTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.sumReportTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="picketageTime"
        :label="$t('cusEntry.bidMod.picketageTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.picketageTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.picketageTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="publishWinLossTime"
        :label="$t('cusEntry.bidMod.publishWinLossTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.publishWinLossTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.publishWinLossTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="completeTime"
        :label="$t('cusEntry.bidMod.completeTime')"
        :render-header="_addStarToColumn"
        min-width="180"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.planType=='PLAN'"
            v-model="scope.row.completeTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="timePickerOptions"
          />
          <span v-else>{{ scope.row.completeTime }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 制定招标计划
 */
export default {
  name: 'BiddingPlan',

  props: {
    biddingBase: {
      type: Object,
      required: true
    },
    detailData: {
      type: Array,
      default: () => []
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      timePickerOptions: {
        disabledDate (time) {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return time.getTime() < nowDate.getTime()
        }
      },
      planList: []
    }
  },

  computed: {
    // 收标方式：同时收标 && 招标类型：技术+商务时，商务标截止时间取技术标截止时间，隐藏商务标截止时间
    busEndTimeHideFlag () {
      return this.biddingBase.extSouMode == 'SAME_TIME' && this.biddingBase.orderType == 'TECHNOLOGY_BUSINESS'
    },
    // 收标方式：同时收标 && 招标类型：商务时，不展示技术标截止时间、开技术标时间、技术评标时间
    techTimeHideFlag () {
      return this.biddingBase.extSouMode == 'SAME_TIME' && this.biddingBase.orderType == 'BUSINESS'
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.planList = val || []
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    techEndTimeChange (val, row) {
      // 收标方式：同时收标 && 招标类型：技术+商务时，商务标截止时间取技术标截止时间，隐藏商务标截止时间 -->
      if (this.busEndTimeHideFlag) {
        row.busEndTime = val
      }
    },
    timeFillIn () {
      // 校验 招标计划时间 是否填完
      let data = this.planList[0]
      let valid = !!data.publishTime && !!data.techEndTime && !!data.techOpenTime && !!data.techEvaluationTime && !!data.busEndTime &&
        !!data.priceOpenTime && !!data.sumReportTime && !!data.picketageTime && !!data.publishWinLossTime && !!data.completeTime
      if (this.techTimeHideFlag) {
        valid = !!data.publishTime && !!data.busEndTime &&
          !!data.priceOpenTime && !!data.sumReportTime && !!data.picketageTime && !!data.publishWinLossTime && !!data.completeTime
      }
      return valid
    },
    timeValidate () {
      // 校验 招标计划时间 后一节点必须晚于前一节点
      let data = this.planList[0]
      let valid = new Date(data.publishTime).getTime() < new Date(data.techEndTime).getTime() &&
        new Date(data.techEndTime).getTime() < new Date(data.techOpenTime).getTime() &&
        new Date(data.techOpenTime).getTime() < new Date(data.techEvaluationTime).getTime() &&
        new Date(data.techEvaluationTime).getTime() < new Date(data.busEndTime).getTime() &&
        new Date(data.busEndTime).getTime() < new Date(data.priceOpenTime).getTime() &&
        new Date(data.priceOpenTime).getTime() < new Date(data.sumReportTime).getTime() &&
        new Date(data.sumReportTime).getTime() < new Date(data.picketageTime).getTime() &&
        new Date(data.picketageTime).getTime() < new Date(data.publishWinLossTime).getTime() &&
        new Date(data.publishWinLossTime).getTime() < new Date(data.completeTime).getTime()

      if (this.techTimeHideFlag) {
        valid = new Date(data.publishTime).getTime() < new Date(data.busEndTime).getTime() &&
          new Date(data.busEndTime).getTime() < new Date(data.priceOpenTime).getTime() &&
          new Date(data.priceOpenTime).getTime() < new Date(data.sumReportTime).getTime() &&
          new Date(data.sumReportTime).getTime() < new Date(data.picketageTime).getTime() &&
          new Date(data.picketageTime).getTime() < new Date(data.publishWinLossTime).getTime() &&
          new Date(data.publishWinLossTime).getTime() < new Date(data.completeTime).getTime()
      } else if (this.busEndTimeHideFlag) {
        valid = new Date(data.publishTime).getTime() < new Date(data.techEndTime).getTime() &&
          new Date(data.techEndTime).getTime() < new Date(data.techOpenTime).getTime() &&
          new Date(data.techOpenTime).getTime() < new Date(data.techEvaluationTime).getTime() &&
          new Date(data.techEvaluationTime).getTime() < new Date(data.priceOpenTime).getTime() &&
          new Date(data.priceOpenTime).getTime() < new Date(data.sumReportTime).getTime() &&
          new Date(data.sumReportTime).getTime() < new Date(data.picketageTime).getTime() &&
          new Date(data.picketageTime).getTime() < new Date(data.publishWinLossTime).getTime() &&
          new Date(data.publishWinLossTime).getTime() < new Date(data.completeTime).getTime()
      }
      return valid
    }
  }
}
</script>
