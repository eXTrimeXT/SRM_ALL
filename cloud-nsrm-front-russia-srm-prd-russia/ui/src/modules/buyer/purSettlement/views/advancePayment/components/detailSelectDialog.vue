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
        <!-- 订单号 -->
        <srm-col :init-col="3">
          <el-form-item :label="$t('orderMod.buyerOrderSynergy.orderNumber2')">
            <el-input v-model="queryForm.orderNumber" />
          </el-form-item>
        </srm-col>
        <!-- 物料编码 -->
        <srm-col :init-col="3">
          <el-form-item :label="$t('common.materialName')">
            <QuickSearch
              :show-input="queryForm.materialName"
              show-key="materialName"
              prop-key="materialCode"
              :scope-data="queryForm"
              name="scc_base_material_item_display"
              @close-quicksearch="getItemObj"
            />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <div style="text-align: right;margin-top: 24px;">
            <el-button type="primary" @click="queryContent">
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
      <!-- 采购订单号 -->
      <el-table-column
        align="center"
        prop="orderNumber"
        :label="$t('purSettlementMod.orderNumber')"
        width="150"
        show-overflow-tooltip
      />
      <!-- 订单行号 -->
      <el-table-column
        align="center"
        prop="orderDetailLineNum"
        :label="$t('orderMod.orderLineNum')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 物料编码 -->
      <el-table-column
        align="center"
        prop="materialCode"
        :label="$t('common.materialCode')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 物料名称 -->
      <el-table-column
        align="center"
        prop="materialName"
        :label="$t('common.materialName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!-- 含税金额 -->
      <el-table-column
        align="center"
        prop="amountIncludingTax"
        :label="$t('contractMod.amount2')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 未申请付款金额 -->
      <el-table-column
        align="center"
        prop="paymentAmountAppliedN"
        :label="$t('purSettlementMod.paymentAmountAppliedN')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 单位 -->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('dataConfMod.unit')"
        width="80"
        show-overflow-tooltip
      />
      <!-- 订单数量 -->
      <el-table-column
        align="center"
        prop="orderNum"
        :label="$t('orderMod.buyerOrderSynergy.orderNum')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 未税单价 -->
      <el-table-column
        align="center"
        prop="unitNoTaxPrice"
        :label="$t('purSettlementMod.unitPriceNoTax')"
        width="100"
        show-overflow-tooltip
      />
      <!-- 税率 -->
      <el-table-column
        align="center"
        prop="taxRate"
        :label="$t('bidMod.taxRate')"
        width="100"
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
    queryForm: {
      type: Object,
      default: () => {
        return {
          orderNumber: '',
          materialId: '',
          materialCode: '',
          materialName: ''
        }
      }
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
      if (sign) {
        // this.batchMaintainForm = {}
      }
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
    queryContent () {
      this.$emit('queryContent', this.queryForm)
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.pageInfo.pageNum = currentNum
      this.queryContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.pageInfo.pageSize = currentSize
      this.queryContent()
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
