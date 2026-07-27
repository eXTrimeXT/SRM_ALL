<template>
  <div class="wrapper">
    <div class="btns">
      <el-button v-if="!readonly" type="primary" @click="add">
        新增
      </el-button>
      <el-button v-if="!readonly && form.requirementHeadNo" type="ghost" @click="getVendorByRequire">
        从寻源需求单号获取
      </el-button>
    </div>
    <el-table
      class="mg-10"
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        width="80"
      />

      <el-table-column
        prop="vendorCode"
        label="供应商编码"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <QuickSearch
            v-if="!readonly"
            :show-input="scope.row.vendorCode"
            show-key="companyCode"
            :scope-data="scope.row"
            name="scc_sup_company_info_display_tz"
            @close-quicksearch="getVendorObj"
          />
          <span v-else>{{ scope.row.vendorCode }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="vendorName"
        label="供应商名称"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="contactName"
        label="联系人"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="phone"
        label="电话"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="vendorAttribute"
        label="供应商属性"
        minWidth="230"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="!readonly"
            v-model="scope.row.vendorAttribute"
            multiple
            code="SOU_RECOMM_VENDOR_NATRUE"
          />
          <template v-if="readonly && scope.row.vendorAttribute">
            <span v-for="(item,index) in scope.row.vendorAttribute" :key="index">{{ $getDictLabel('SOU_RECOMM_VENDOR_NATRUE',item) }};</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
        fixed="right"
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
        // 联系电话
        scope.phone = val ? val.ceeacontactmethod : ''
        // 联系人
        scope.contactName = val ? val.contactname : ''
      } else {
        this.$message.warning('供应商重复，请重新选择')
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
