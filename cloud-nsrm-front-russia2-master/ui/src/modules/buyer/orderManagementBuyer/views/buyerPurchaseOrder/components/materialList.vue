<template>
  <!-- 选择父级菜单弹框 -->
  <srm-dialog
    :visible.sync="visible"
    :title="$t('orderMod.buyerOrderSynergy.title')"
    size="large"
    :show-close="false"
    :destroy-on-close="true"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <el-form
        :inline="true"
        :model="form"
      >
        <el-form-item
          :label="$t('orderMod.buyerOrderSynergy.materialName')"
          prop="itemDesc"
        >
          <QuickSearch
            :show-input="form.itemDesc"
            show-key="materialName"
            :scope-data="form"
            name="scc_base_material_item_display"
            @close-quicksearch="getItemObj1"
          />
        </el-form-item>
        <el-form-item
          :label="$t('orderMod.buyerOrderSynergy.materialCode')"
          prop="itemCode"
        >
          <QuickSearch
            :show-input="form.itemCode"
            show-key="materialCode"
            :scope-data="form"
            name="scc_base_material_item_display"
            @close-quicksearch="getItemObj"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="searchParentOrg(false, $event)"
          >
            {{
              $t("common.search")
            }}
          </el-button>
          <el-button @click="clear">
            {{ $t("common.clear") }}
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 列表 -->
      <div class="porg-table">
        <el-table
          v-if="visible"
          ref="parentOrgTable"
          border
          :data="parentOrgTableData"
          tooltip-effect="dark"
          style="width: 100%"
          max-height="300px"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('orderMod.buyerOrderSynergy.vendorName')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="organizationName"
            :label="$t('orderMod.buyerOrderSynergy.organizationName2')"
            width="150"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('orderMod.buyerOrderSynergy.categoryName')"
            width="100"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            prop="itemCode"
            :label="$t('orderMod.buyerOrderSynergy.materialCode')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="itemDesc"
            :label="$t('orderMod.buyerOrderSynergy.materialName')"
            show-overflow-tooltip
            width="100"
          />
          <el-table-column
            align="center"
            prop="notaxPrice"
            :label="$t('orderMod.buyerOrderSynergy.untaxedPrice')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="taxPrice"
            :label="$t('orderMod.buyerOrderSynergy.taxPrice')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="taxRate"
            :label="$t('orderMod.buyerOrderSynergy.taxRate')"
            :formatter="formatTax"
            width="150"
          />
          <el-table-column
            align="center"
            prop="unit"
            :label="$t('orderMod.buyerOrderSynergy.unit')"
            width="100"
            :formatter="formatUnit"
          />
          <el-table-column
            align="center"
            prop="currency"
            :label="$t('orderMod.buyerOrderSynergy.currency')"
            width="150"
            :formatter="formatCurrency"
          />
        </el-table>
        <CPagination
          :total="parentOrgTableDataPage.total"
          :page-num="parentOrgTableDataPage.pageNum"
          :page-size="parentOrgTableDataPage.pageSize"
          @current-change="parentDataCurrentChange"
          @size-change="parentDataSizeChange"
        />
      </div>
    </div>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="cancleHandle">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="comfirmSelect"
      >
        {{
          $t("common.confirm")
        }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'
import {
  getDictItemList,
  getAllPurUnit,
  getAllPurCurrency
} from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import { purchaseOrderApi } from 'modb@/orderManagementBuyer/api'

export default {
  name: 'MaterialList',
  components: { CPagination, QuickSearch },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    queryParams: {
      type: Object,
      default: {}
    }
  },
  data () {
    return {
      selection: null,
      contractStatus: [],
      parentOrgTableData: [],
      orgTypeList: [],
      unitList: [],
      currencyList: [],
      form: {},
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      deliveryLevelOpts: [],
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    visible (oldValue, newValue) {
      if (!newValue) {
        this.searchParentOrg(false)
      }
    }
  },
  created () {
    // this.$nextTick(() => {
    //   this.searchParentOrg(true);
    // });
  },
  mounted () {
    const dictionaryCodes = [{ dictCode: 'CONTRACT_STATUS' }]
    getDictItemList(dictionaryCodes).then(res => {
      const [CONTRACT_STATUS] = res.data
      this.contractStatus = adaptDictData(CONTRACT_STATUS.CONTRACT_STATUS)
    })
    // 获取所有单位
    getAllPurUnit().then(res => {
      this.unitList = adaptDictData(res.data, 'unit')
    })
    getAllPurCurrency().then(res => {
      this.currencyList = adaptDictData(res.data, 'currency')
    })
  },
  methods: {
    formatUnit (row, column, cellValue, index) {
      const dict = this.unitList.find(i => i.value === cellValue)
      return dict ? dict.label : cellValue
    },
    formatTax (row, column, cellValue, index) {
      this.$getDictLabel('tax', cellValue)
      return this.$getDictLabel('tax', cellValue)
    },
    formatCurrency (row, column, cellValue, index) {
      const dict = this.currencyList.find(i => i.value === cellValue)
      return dict ? dict.label : cellValue
    },
    clear () {
      this.form = {}
    },
    getItemObj (val, form) {
      this.form.itemCode = val.materialCode
      console.log(val, form)
    },
    getItemObj1 (val, form) {
      this.form.itemDesc = val.materialName
      console.log(val, form)
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.searchParentOrg()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.searchParentOrg()
    },
    searchParentOrg (isFirst = false) {
      const data = isFirst
        ? { pageNum: 1, pageSize: 10 }
        : { ...this.parentOrgQueryForm, ...this.form }
        purchaseOrderApi.listPagePriceLibrary({
          ...data,
          ...this.queryParams
        })
        .then(data => {
          const { list, pageNum = 0, pageSize = 0, total } = data.data
          console.log(list)
          this.parentOrgTableData = list
          this.parentOrgTableDataPage = { pageNum, pageSize, total }
        })
    },
    comfirmSelect () {
      this.$emit('on-ok', this.selection)
    },
    handleSelectionChange (selection) {
      console.log(selection)
      this.selection = selection
    },
    cancleHandle () {
      this.$emit('on-cancle')
    }
  }
}
</script>
