<template>
  <el-row>
    <el-col :span="24">
      <el-table
        ref="bidSumTable"
        :data="bidSumData"
        border
      >
        <el-table-column
          align="center"
          prop="reviewItem"
          :label="$t('vendorMod.particulars')"
          minWidth="200"
          show-overflow-tooltip
        />
        <el-table-column
          v-for="(item,index) in bidSumData[0]?.summaryList"
          :key="index"
          :label="item.vendorName"
          :prop="`comment${item.vendorId}`"
          align="center"
          min-width="150"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.summaryList[index].comment"
              :placeholder="$t('contractMod.pleaseFillIn')"
              :disabled="!isWriteBid"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-col>
    <el-col :span="24">
      <BidVendor
        ref="bidVendor"
        :value.sync="baseForm.caOrders"
        :applicantNo="baseForm.applicantNo"
        @tabAdd="tabAdd"
      />
    </el-col>
    <el-col :span="24">
      <div class="header">
        <!-- 招标周期安排 -->
        <span class="title">{{ $t("cusEntry.supplement20250205.tenderCycleSchedule") }}</span>
      </div>
      <el-table
        ref="singleTable"
        :data="tableData"
        :border="true"
      >
        <el-table-column
          type="index"
          :label="$t('components.common.sort')"
          width="60"
        />
        <el-table-column
          prop="date"
          :label="$t('cusEntry.supplement20250205.workContent')"
          minWidth="120"
        >
          <template slot-scope="scope">
            {{ $getDictLabel('CA_TENDER_TIME_TYPE', scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="planTime"
          :label="$t('quality.improvedCompleteDate')"
          :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          minWidth="120"
        />
        <el-table-column
          prop="actualTime"
          :label="$t('cusEntry.bidSuperviseReport.actualAcceptanceBidTime')"
          :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          minWidth="120"
        />
        <el-table-column
          prop="department"
          :label="$t('meeting.department')"
          minWidth="120"
        />
        <el-table-column
          prop="dutyOfficer"
          :label="$t('vendorMod.PersonLiable')"
          minWidth="150"
        />
        <el-table-column
          prop="remark"
          :label="$t('components.eio.headers.remark')"
          minWidth="150"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.remark" :placeholder="$t('contractMod.pleaseFillIn')" :disabled="readonly" />
          </template>
        </el-table-column>
      </el-table>
    </el-col>
    <el-col :span="24">
      <Negotiations
        :form.sync="form"
        :readonly="readonly"
        :table="caNegotiateExtendTable"
        :tableHeader="caNegotiateExtendTableHeader"
      />
    </el-col>
  </el-row>
</template>
<script>
import BidVendor from './businessInfo/bidVendor'
import VendorSituation from './businessInfo/vendorSituation'
import Negotiations from './negotiations'
export default {
  components: {
    BidVendor,
    VendorSituation,
    Negotiations
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
    bidSumData: {
      type: Array,
      default: () => ([])
    },
    caTenderTimes: {
      type: Array,
      default: () => ([])
    },
    isWriteBid: {
      type: Boolean,
      default: false
    },
    caNegotiateExtendTable: {
      type: Array,
      default: () => []
    },
    caNegotiateExtendTableHeader: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
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
        return this.caTenderTimes
      },
      set: function (val) {
        this.$emit('update:form.caTenderTimes', val)
      }
    },
    applicantNoList () {
      let applicantNo = this.baseForm.applicantNo
      if (applicantNo) {
        return applicantNo.toString().split(';')
      }
      return []
    }
  },
  methods: {
    tabAdd (val) {
      this.$emit('tabAdd', val)
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
