<template>
  <!-- 物料明细选择 -->
  <srm-dialog
    :visible.sync="visibleDialog"
    :title="$t('purchaseDemand.materialDetailSelect')"
    size="large"
    destroy-on-close
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-form
      v-if="visibleDialog"
      ref="queryForm"
      label-width="60px"
      label-position="left"
      :inline="true"
      :model="formParams"
    >
      <srm-row>
        <srm-col :initCol="3">
          <!-- 物料编码 -->
          <el-form-item :label="$t('purchaseDemand.itemCode')">
            <el-input v-model="formParams.materialCode" />
          </el-form-item>
        </srm-col>
        <!-- 物料小类 -->
        <srm-col :initCol="3">
          <el-form-item :label="$t('purchaseDemand.materialCateSub')">
            <QuickSearch
              :pre-query-data="materialParam"
              :show-input="formParams.categoryName"
              show-key="categoryName"
              :scope-data="formParams"
              name="scc_base_purchase_category4"
              @before-open="beforeOpenMaterial"
              @close-quicksearch="getCategoryNameObj"
            />
          </el-form-item>
        </srm-col>

        <srm-col :initCol="3">
          <div style="text-align: right;">
            <el-button type="primary" @click="queryContent">
              {{ $t('common.search') }}
            </el-button>
            <el-button type="primary" @click="addOneContent">
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
      height="345px"
      highlight-current-row
      @row-dblclick="handleItemDBClick"
      @selection-change="handleItemSelection"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column
        align="center"
        type="index"
        :label="$t('contractMod.tabindex')"
        width="60"
      />
      <!-- 物料编码 -->
      <el-table-column
        align="center"
        prop="materialCode"
        :label="$t('purchaseDemand.itemCode')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <!-- 物料名称 -->
      <el-table-column
        align="center"
        prop="materialName"
        :label="$t('purchaseDemand.itemName')"
        min-width="150"
        :show-overflow-tooltip="true"
      />
      <!-- 物料小类 -->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('purchaseDemand.materialCateSub')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <!-- 单位 -->
      <el-table-column
        align="center"
        prop="unitName"
        :label="$t('purchaseDemand.unitCode')"
        width="120"
      />
      <!-- 品类全称 -->
      <el-table-column
        align="center"
        prop="categoryFullName"
        :label="$t('purchaseDemand.categoryFullName')"
        min-width="150"
        :show-overflow-tooltip="true"
      />
    </el-table>

    <srm-row>
      <srm-col :initCol="1">
        <CPagination
          ref="queryPagination"
          style="margin: 5px"
          class="c-query-table-pagination"
          :total="pageInfo.pageTotal"
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
  name: 'BatchMaintainDialog',
  components: {
    QuickSearch,
    CPagination
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    requirementHead: {
      type: Object,
      default: () => {}
    },
    queryForm: {
      type: Object,
      default: () => {
        return {
          categoryCode: null,
          categoryId: null,
          categoryName: null,
          materialCode: null,
          materialId: null,
          materialName: null,
          orgId: null,
          organizationId: null,
          organizationName: null
        }
      }
    },
    pageInfo: {
      type: Object,
      default: () => {
        return {
          pageTotal: 0,
          pageNum: 1,
          pageSize: 15
        }
      }
    },
    displayItemTable: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      visibleDialog: false,
      materialParam: {},
      multipleSelection: [],
      viewIndex: 1,
      viewSize: 15,
      formParams: {}
    }
  },
  watch: {
    visible (sign) {
      this.visibleDialog = sign
    },
    queryForm (val) {
      this.formParams = val
    }
  },
  methods: {
    // 物料小类弹窗
    beforeOpenMaterial () {
      this.materialParam['t.STRUCT'] = this.requirementHead.categoryId
    },
    // 物料小类
    getCategoryNameObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    // 查询
    queryContent (argParams = {}) {
      this.formParams = { ...this.formParams, ...argParams }
      this.$emit('queryContent', this.formParams)
    },
    addOneContent () {
      this.$emit('addOneContent', this.multipleSelection)
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addOneContent()
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.queryContent({ pageNum: currentNum })
      this.$emit('changeCurrentIndex', currentNum)
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.queryContent({ pageSize: currentSize })
      this.$emit('changeCurrentSize', currentSize)
    }
  }
}
</script>
