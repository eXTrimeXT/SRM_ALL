<template>
  <srm-dialog
    :visible.sync="visibleDialog"
    :title="$t('purSettlementMod.detailSelect')"
    size="large"
    destroy-on-close
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-form
      ref="queryForm"
      :model="queryForm"
      label-width="80px"
      label-position="top"
      class="form-incontainer"
    >
      <srm-row>
        <!-- 合同名称 -->
        <srm-col :init-col="3">
          <el-form-item :label="$t('vendorMod.contractName')">
            <el-input v-model="queryForm.contractName" />
          </el-form-item>
        </srm-col>
        <!-- 合同履约开票单号 -->
        <srm-col :init-col="3">
          <el-form-item :label="$t('contract_mod.processNum2')">
            <el-input v-model="queryForm.invoiceNo" />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <div style="text-align: right;margin-top: 24px;">
            <el-button type="primary" @click="getListData(queryForm.contractName, queryForm.invoiceNo)">
              {{ $t('common.search') }}
            </el-button>
            <el-button type="primary" @click="addContentInfo">
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </srm-col>
      </srm-row>
    </el-form>
    <el-table
      :data="displayItemTable"
      style="width: 100%"
      border
      height="250px"
      highlight-current-row
      @selection-change="handleItemSelection"
      @row-dblclick="handleItemDBClick"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column
        align="center"
        type="index"
        :label="$t('contractMod.tabindex')"
        width="60"
      />
      <!-- 合同名称 -->
      <el-table-column
        align="center"
        prop="contractName"
        :label="$t('vendorMod.contractName')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 里程碑名称 -->
      <el-table-column
        align="center"
        prop="milestoneType"
        :label="$t('contract_mod.processNodeName')"
        width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          {{ $getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType) }}
        </template>
      </el-table-column>
      <!-- 付款阶段 -->
      <el-table-column
        align="center"
        prop="paymentStage"
        :label="$t('bidMod.payStage')"
        width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          {{ $getDictLabel('PAYMENT_STAGE', scope.row.paymentStage) }}
        </template>
      </el-table-column>
      <!-- 合同履约开票单号 -->
      <el-table-column
        align="center"
        prop="invoiceNo"
        :label="$t('contract_mod.processNum2')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 供应商名称 -->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 供应商编号 -->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('supplierRating.supplierCode')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 组织 -->
      <el-table-column
        align="center"
        prop="invName"
        :label="$t('vendorMod.organization')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 物料名称 -->
      <el-table-column
        align="center"
        prop="materialName"
        :label="$t('vendorMod.materialName')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 物料编码 -->
      <el-table-column
        align="center"
        prop="materialCode"
        :label="$t('vendorMod.materialCode')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 应付含税金额 -->
      <el-table-column
        align="center"
        prop="payableTaxedAmount"
        :label="$t('contract_mod.payableTax')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 开票含税金额 -->
      <el-table-column
        align="center"
        prop="invoicedTaxedAmount"
        :label="$t('contract_mod.payableTax2')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 代付款金额 -->
      <el-table-column
        align="center"
        prop="stayPaymentAmount"
        :label="$t('contract_mod.paymentAmount')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 已付款金额 -->
      <el-table-column
        align="center"
        prop="alreadyPaymentAmount"
        :label="$t('contract_mod.amountPaid')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 创建人 -->
      <el-table-column
        align="center"
        prop="createdBy"
        :label="$t('sourcingBuyer.createdFullName')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 创建时间 -->
      <el-table-column
        align="center"
        prop="creationDate"
        :label="$t('sourcingBuyer.creationDate')"
        width="150"
        show-overflow-tooltip
      />
    </el-table>
    <srm-row type="flex">
      <srm-col>
        <CPagination
          ref="queryPagination"
          style="margin:5px"
          class="c-query-table-pagination"
          :total="pageInfo.total"
          :page-num="pageInfo.pageNum"
          :page-size="pageInfo.pageSize"
          @current-change="changeCurrentIndex"
          @size-change="changeCurrentSize"
        />
      </srm-col>
    </srm-row>
  </srm-dialog>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'DetailSelectDialog',
  components: {
    QuickSearch,
    CPagination
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    pageInfo: {
      type: Object,
      default: () => {
        return {
          total: 0,
          pageNum: 1,
          pageSize: 15
        }
      }
    },
    queryForm: {
      type: Object,
      default: () => {
        return {
          contractName: '',
          invoiceNo: ''
        }
      }
    },
    displayItemTable: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data () {
    return {
      visibleDialog: false,
      multipleSelection: []
      // displayItemTable: []
    }
  },
  watch: {
    visible (sign) {
      this.visibleDialog = sign
      if (sign) this.getListData()
    }
  },
  methods: {
    // 物料编码
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    // 查询
    getListData () {
      this.$emit('getListData', this.queryForm)
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.pageInfo.pageNum = currentNum
      this.getListData()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.pageInfo.pageSize = currentSize
      this.getListData()
    },
    // 添加明细
    addContentInfo () {
      this.$emit('addContentInfo', this.multipleSelection)
    },
    // 明细选择
    handleItemSelection (selects) {
      this.multipleSelection = selects
    },
    // 明细双击
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addContentInfo()
    }
  }
}
</script>
