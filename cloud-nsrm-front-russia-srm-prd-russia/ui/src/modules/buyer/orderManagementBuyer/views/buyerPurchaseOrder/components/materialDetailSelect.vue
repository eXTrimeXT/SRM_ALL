<template>
  <!-- 物料新增 - 物料明细选择 -->
  <srm-dialog
    :title="$t('purchaseDemand.materialDetailSelect')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    v-bind="$attrs"
    class="purchase_order_material_dialog"
    v-on="$listeners"
  >
    <div class="detail-add">
      <el-form
        ref="queryForm"
        :model="queryForm"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <srm-row>
          <srm-col :initCol="3">
            <el-form-item :label="$t('purchaseDemand.itemName')">
              <QuickSearch
                :show-input="queryForm.materialName"
                show-key="materialCode"
                :scope-data="queryForm"
                name="scc_base_material_item"
                @close-quicksearch="getMaterialByQuick"
              />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3">
            <el-form-item
              :label="$t('purchaseDemand.materialCategory')"
              :label-width="formLabelWidth2"
            >
              <el-input
                v-model="queryForm.inputLevel"
                :placeholder="$t('common.pleaseTypeContents')"
                class="input-with-select"
                clearable
              >
                <el-select
                  slot="prepend"
                  v-model="queryForm.selectLevel"
                  style="width: 75px;"
                  :placeholder="$t('common.pleaseSelect')"
                  clearable
                >
                  <el-option :id="1" :label="$t('purchaseDemand.bigCategory')" value="1" />
                  <el-option :id="2" :label="$t('purchaseDemand.midCategory')" value="2" />
                  <el-option :id="3" :label="$t('purchaseDemand.smallCategory')" value="3" />
                </el-select>
              </el-input>
            </el-form-item>
          </srm-col>
          <srm-col :initCol="3" style="margin-top:26px;text-align:right;">
            <el-button type="primary" @click="queryContent">
              {{ $t('common.search') }}
            </el-button>
            <el-button type="primary" @click="addOneContent">
              {{ $t('common.save') }}
            </el-button>
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
        <el-table-column type="index" width="60" :label="$t('contractMod.tabindex')" />
        <el-table-column
          align="center"
          prop="organizationName"
          :label="$t('purchaseDemand.invOrg')"
          width="150"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="bigCategoryName"
          :label="$t('purchaseDemand.materialCate')"
          width="120"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="middleCategoryName"
          :label="$t('purchaseDemand.midCategoryName')"
          width="120"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="categoryName"
          :label="$t('purchaseDemand.materialCateSub')"
          width="120"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="materialCode"
          :label="$t('purchaseDemand.itemCode')"
          width="120"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="materialName"
          :label="$t('purchaseDemand.itemName')"
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
            :total="queryTotal"
            :page-num="viewIndex"
            :page-size="viewSize"
            @current-change="changeCurrentIndex"
            @size-change="changeCurrentSize"
          />
        </srm-col>
      </srm-row>
    </div>
  </srm-dialog>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'MaterialDetailSelect',
  components: {
    QuickSearch,
    CPagination
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => {
        return {}
      }
    },
    queryForm: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data () {
    return {
      dialogVisible: false,
      queryTotal: 0,
      viewSize: 10,
      viewIndex: 1,
      multipleSelection: [],
      displayItemTable: [],
      formLabelWidth2: '120px'
    }
  },
  watch: {
    visible (sign) {
      this.dialogVisible = sign
    }
  },
  methods: {
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addOneContent()
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.queryContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.queryContent()
    },
    // 物料编码
    getMaterialByQuick (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    addOneContent () {
      this.$emit('addOneContent', this.multipleSelection)
    },
    queryContent () {
      if (!this.queryForm.organizationId) {
        this.$message.warning(this.$t('dataConfMod.msgPSelectOrgza'))
        return
      }
      const params = {
        level: this.queryForm.selectLevel,
        param: this.queryForm.inputLevel
      }
      this.$http({
        url: '/api-base/purchase/purchaseCategory/queryCategoryByType',
        method: 'GET',
        params: { ...params, enabled: 'Y' },
        loading: true
      }).then(res => {
        const categoryRata = res.data.filter(v => v.categoryName !== '服务类')

        this.$http({
          url: '/api-base/material/materialItem/listForOrder',
          method: 'POST',
          data: {
            ifSample: this.form.ifSample,
            ceeaOrgId: this.form.ceeaOrgId,
            vendorId: this.form.vendorId,
            receiveContact: this.form.receiveContact,
            receiveTelephone: this.form.receiveTelephone,
            receiveAddress: this.form.receiveAddress,
            purchaseCategories: categoryRata,
            materialCode: this.queryForm.materialCode,
            materialName: this.queryForm.materialName,
            categoryName: this.queryForm.inputLevel,
            isManual: this.queryForm.isManual,
            organizationId: this.queryForm.organizationId,
            pageSize: this.viewSize,
            pageNum: this.viewIndex,
            purchaseType: this.form.orderType
          },
          loading: true
        }).then(data => {
          if (data && data.data) {
            this.displayItemTable = data.data.list
            this.queryTotal = data.data.total
            this.dialogVisible = true
          }
        })
      })
    }
  }
}
</script>

<style>
.purchase_order_material_dialog .el-input-group__prepend {
  background-color:#fff;
}
</style>
