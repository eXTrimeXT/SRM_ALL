<template>
  <div class="wrapper">
    <el-table
      class="mg-10"
      border
      stripe
      :data="tableData"
    >
      <!-- <el-table-column
        type="index"
        label="序号"
        width="80"
      /> -->
      <el-table-column
        type="index"
        :label="$t('components.common.sort')"
        width="80"
      />

      <!-- <el-table-column
        prop="vendorCode"
        label="供应商编码"
        minWidth="130"
      /> -->
      <el-table-column
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        minWidth="130"
      />

      <!-- <el-table-column
        prop="vendorName"
        label="供应商名称"
        minWidth="130"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="vendorName"
        :label="$t('common.companyName')"
        minWidth="130"
        showOverflowTooltip
      />

      <!-- <el-table-column
        prop="contactName"
        label="联系人"
        minWidth="130"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="contactName"
        :label="$t('vendorMod.contactPerson')"
        minWidth="130"
        showOverflowTooltip
      />

      <!-- <el-table-column
        prop="phone"
        label="电话"
        minWidth="130"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="phone"
        :label="$t('common.phone')"
        minWidth="130"
        showOverflowTooltip
      />

      <!-- <el-table-column
        prop="vendorAttribute"
        label="供应商属性"
        minWidth="230"
      > -->
      <el-table-column
        prop="vendorAttribute"
        :label="$t('cusEntry.supplement20250121.supplierAttributes')"
        minWidth="230"
      >
        <template v-slot="scope">
          <template v-if="scope.row.vendorAttribute">
            <span v-for="(item,index) in scope.row.vendorAttribute.split(';')" :key="index">{{ $getDictLabel('SOU_RECOMM_VENDOR_NATRUE',item) }};</span>
          </template>
        </template>
      </el-table-column>
      <template v-if="form.status !== 'DRAFT'">
        <!-- <el-table-column
          prop="sourceDescription"
          label="来源说明"
          minWidth="130"
          showOverflowTooltip
        /> -->
        <el-table-column
          prop="sourceDescription"
          :label="$t('cusEntry.supplement20250205.sourceDescription')"
          minWidth="130"
          showOverflowTooltip
        />

        <!-- <el-table-column
          prop="feedbackStatus"
          label="反馈状态"
          minWidth="130"
        > -->
        <el-table-column
          prop="feedbackStatus"
          :label="$t('dashboard.loopMode')"
          minWidth="130"
        >
          <template v-slot="scope">
            <span>{{ $getDictLabel('VENDOR_FEEDBACK_STATUS',scope.row.feedbackStatus) }}</span>
          </template>
        </el-table-column>

        <!-- <el-table-column
          prop="rejectDescription"
          label="驳回说明"
          minWidth="180"
          showOverflowTooltip
        /> -->
        <el-table-column
          prop="rejectDescription"
          :label="$t('vendorMod.refuseMemo')"
          minWidth="180"
          showOverflowTooltip
        />

        <!-- <el-table-column
          prop="isSelected"
          label="是否入围"
          minWidth="130"
          :render-header="_addStarToColumn"
        > -->
        <el-table-column
          prop="isSelected"
          :label="$t('cusEntry.supplement20250205.isShortlisted')"
          minWidth="130"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <DictSelect v-if="!readonly" v-model="scope.row.isSelected" code="YES_OR_NO" />
            <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isSelected) }}</span>
          </template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import { commuNoticeBuyerHttp } from 'modcb@/preBidCommunicate/api'
import { transformMQL } from 'lib@/utils/util'

export default {
  components: {
    QuickSearch
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    form: {
      type: Object,
      default: () => {}
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
      this.tableData.push({
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        contactName: null,
        phone: null,
        vendorAttribute: null
      })
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    getVendorObj (val, scope) {
      console.log('val', val)
      let vendorIdList = this.tableData.map(item => item.vendorId)
      if (!vendorIdList.includes(val.companyId)) {
        scope.vendorId = val ? val.companyId : ''
        scope.vendorCode = val ? val.companyCode : ''
        scope.vendorName = val ? val.companyName : ''
        scope.phone = val ? val.extOfficePhone : ''
      // 联系人待确认
      } else {
        // this.$message.warning('供应商重复，请重新选择')
        this.$message.warning(this.$t('cusEntry.supplement20250205.duplicateSupplier'))
        scope.vendorCode = null
      }
    },
    async getVendorByRequire () {
      // 根据申请单号查询所有报名成功供应商，并去重
      let params = [{
        requirementHeadId: this.form.requirementHeadId
      }]
      let transformParams = transformMQL.save('PreBidNoticeBuyer', params, 'getVendorList')
      const response = await commuNoticeBuyerHttp.getVendorList(transformParams)
      let vendorIdList = this.tableData.map(item => item.vendorId)
      let records = response.data.records || []
      if (records.length) {
        for (let item of records) {
          if (!vendorIdList.includes(item.vendorId)) {
            this.tableData.push({
              vendorId: item.vendorId,
              vendorCode: item.vendorCode,
              vendorName: item.vendorName,
              contactName: item.applyContactName,
              phone: item.applyPhone,
              vendorAttribute: []
            })
          }
        }
      }
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
