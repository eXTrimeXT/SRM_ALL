<template>
  <!-- 物料明细选择(采购申请)-->
  <srm-dialog
    :title="$t('purchaseDemand.materialDetailSelect1')"
    size="large"
    :visible.sync="dialogFormVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <div>
      <el-form
        ref="filterForm"
        :model="filterForm"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <srm-row>
          <srm-col :initCol="3">
            <!-- 物料名称 -->
            <el-form-item :label="$t('purchaseDemand.itemName')">
              <QuickSearch
                :show-input="filterForm.materialName"
                show-key="materialCode"
                :scope-data="filterForm"
                name="scc_base_material_item"
                @close-quicksearch="getMaterialByQuick"
              />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <el-form-item :label="$t('bidMod.categoryName')" :label-width="formLabelWidth">
              <el-input v-model="filterForm.categoryKey" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <el-form-item
              :label="$t('purchaseDemand.purRequisitionNum')"
              :label-width="formLabelWidth"
            >
              <el-input v-model="filterForm.requirementHeadNum" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <el-form-item
              :label="$t('purchaseDemand.requestDateFrom')"
              :label-width="formLabelWidth"
            >
              <el-date-picker v-model="filterForm.startDate" value-format="yyyy-MM-dd" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <el-form-item :label="$t('purchaseDemand.requestDateTo')" :label-width="formLabelWidth">
              <el-date-picker v-model="filterForm.endDate" value-format="yyyy-MM-dd" />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <p>
        <el-button type="primary" @click="queryItemList">
          {{ $t('common.search') }}
        </el-button>
        <el-button @click="resetFilterForm">
          {{ $t('common.reset') }}
        </el-button>
        <el-button type="primary" @click="addNewOne">
          {{ $t('common.confirm') }}
        </el-button>
      </p>
    </div>
    <el-table
      :data="displayMaterialItem"
      style="width: 100%"
      border
      height="250px"
      highlight-current-row
      @selection-change="handleSelectionChange"
      @row-dblclick="handleItemDBClick2"
    >
      <el-table-column type="selection" width="55" fixed="left" />
      <el-table-column align="center" type="index" width="60" :label="$t('contractMod.tabindex')" />
      <el-table-column
        align="center"
        prop="orgName"
        :label="$t('purchaseDemand.businessEntity')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="organizationName"
        :label="$t('purchaseDemand.invOrg')"
        min-width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="receiptPlace"
        :label="$t('purchaseDemand.ceeaDeliveryPlace')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('purchaseDemand.materialCateSub')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="materialCode"
        :label="$t('purchaseDemand.itemCode')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="materialName"
        :label="$t('purchaseDemand.itemName')"
        min-width="150"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="unitCode"
        :label="$t('orderMod.buyerOrderSynergy.unit')"
        width="60"
        :show-overflow-tooltip="true"
      />
      <!-- 需求数量 -->
      <el-table-column
        align="center"
        prop="requirementQuantity"
        :label="$t('orderMod.buyerOrderSynergy.applicationQuantity')"
        width="80"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="requirementDate"
        :label="$t('purchaseDemand.requirementDate')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="requirementHeadNum"
        :label="$t('purchaseDemand.purRequisitionNum')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="rowNum"
        :label="$t('purchaseDemand.rowNum')"
        width="80"
        :show-overflow-tooltip="true"
      />
    </el-table>
    <srm-row>
      <srm-col :initCol="1">
        <CPagination
          ref="queryPagination2"
          style="margin: 5px"
          class="c-query-table-pagination"
          :total="queryTotal"
          :page-num="viewIndex"
          :page-size="viewSize"
          @current-change="changeCurrentIndex2"
          @size-change="changeCurrentSize2"
        />
      </srm-col>
    </srm-row>
  </srm-dialog>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'PurchaseDetailSelect',
  components: {
    QuickSearch,
    CPagination
  },
  props: {
    form: {
      type: Object,
      default: () => {
        return {}
      }
    },
    filterForm: {
      type: Object,
      default: () => {
        return {}
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      dialogFormVisible: false,
      queryTotal: 0,
      viewSize: 10,
      viewIndex: 1,
      selection: [],
      formLabelWidth: '120px',
      displayMaterialItem: []
    }
  },
  watch: {
    visible (sign) {
      this.dialogFormVisible = sign
    }
  },
  methods: {
    // 改变 currentNum
    changeCurrentIndex2 (currentNum) {
      this.viewIndex = currentNum
      this.queryItemList()
    },
    // 改变 currentSize
    changeCurrentSize2 (currentSize) {
      this.viewSize = currentSize
      this.queryItemList()
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    handleItemDBClick2 (val) {
      this.selection = [val]
      this.addNewOne()
    },
    queryItemList () {
      const data = {
        pageSize: this.viewSize,
        pageNum: this.viewIndex,
        ...this.filterForm,
        receiveContact: this.form.receiveContact,
        receiveTelephone: this.form.receiveTelephone,
        receiveAddress: this.form.receiveAddress
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/listPageForOrder',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem = res.data.list
        this.queryTotal = res.data.total
        this.dialogFormVisible = true
      })
    },
    resetFilterForm () {
      Object.assign(this.filterForm, {
        materialCode: '',
        materialId: '',
        materialName: '',
        categoryKey: '',
        requirementHeadNum: '',
        startDate: '',
        endDate: ''
      })
    },
    addNewOne () {
      this.$emit('addNewOne', this.selection)
    },
    // 物料编码
    getMaterialByQuick (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    }
  }
}
</script>

<style></style>
