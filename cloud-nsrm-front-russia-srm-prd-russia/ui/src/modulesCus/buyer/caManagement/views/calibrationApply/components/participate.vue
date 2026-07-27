<template>
  <el-row>
    <el-col v-if="isShow" :span="24">
      <el-form-item label="供应商资质要求" prop="vendorFlairAdjure">
        <el-input v-model="baseForm.vendorFlairAdjure" type="textarea" :disabled="readonly" placeholder="具备发泡设备相关机械设备资质，注册资金≥1000万。" />
      </el-form-item>
    </el-col>
    <el-col v-if="isShow" :span="24">
      <el-form-item label="业绩要求" prop="vendorBizAdjure">
        <el-input v-model="baseForm.vendorBizAdjure" type="textarea" :disabled="readonly" placeholder="具备与国内知名汽车座椅企业合作的发泡生产线开孔破泡机设计、制造、安装、调试经验，此类项目数量至少3个项目。" />
      </el-form-item>
    </el-col>
    <el-col :span="24">
      <BidVendor
        ref="bidVendor"
        :value.sync="baseForm.caOrders"
        :applicantNo="baseForm.applicantNo"
        @tabAdd="tabAdd"
      />
    </el-col>
    <el-col v-if="isShow" :span="24">
      <div class="header">
        <span class="title">招标周期安排</span>
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
          prop="date"
          label="工作内容"
        >
          <template slot-scope="scope">
            {{ $getDictLabel('CA_TENDER_TIME_TYPE', scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="planTime"
          label="计划完成时间"
        />
        <el-table-column
          prop="actualTime"
          label="实际时间"
        />
        <el-table-column
          prop="department"
          label="责任部门"
        />
        <el-table-column
          prop="dutyOfficer"
          label="责任人"
          width="280"
        />
        <el-table-column
          prop="remark"
          label="备注"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.remark" placeholder="请输入内容" :disabled="readonly" />
          </template>
        </el-table-column>
      </el-table>
    </el-col>
    <el-col>
      <VendorSituation
        v-if="baseForm.extSouProcess !== 'INQUIRY'"
        ref="vendorSituation"
        class="mt-10"
        :value.sync="baseForm.caSuppliers"
        :isWrite="isWrite"
        :readonly="readonly"
        :applicantNo="baseForm.applicantNo"
        :extScoreRule="baseForm.extScoreRule"
      />
    </el-col>
    <el-col>
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
    caTenderTimes: {
      type: Array,
      default: () => ([])
    },
    isShow: {
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
    isWrite () { // BMP回调标志是否填写 Y是可以填写，N不可以填写
      return this.form.ifWrite === 'N'
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
