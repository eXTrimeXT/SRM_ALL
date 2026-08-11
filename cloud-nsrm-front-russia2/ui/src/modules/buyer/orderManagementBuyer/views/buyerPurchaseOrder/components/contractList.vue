<template>
  <!-- 选择父级菜单弹框 -->
  <srm-dialog
    :title="$t('orderMod.buyerOrderSynergy.getcontractline')"
    size="large"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <el-form
        :inline="true"
        :model="form"
      >
        <el-form-item
          :label="$t('orderMod.buyerOrderSynergy.categoryName')"
          prop="categoryName"
        >
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item
          :label="$t('orderMod.buyerOrderSynergy.materialCode')"
          prop="materialCode"
        >
          <QuickSearch
            :show-input="form.materialCode"
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
            prop="lineNumber"
            :label="$t('orderMod.buyerOrderSynergy.contractlineNumber')"
            width="130"
          />
          <el-table-column
            align="center"
            prop="contractNo"
            :label="$t('orderMod.buyerOrderSynergy.contractNo')"
            width="120"
          />
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('orderMod.buyerOrderSynergy.vendorName')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="organizationCode"
            :label="$t('orderMod.buyerOrderSynergy.organizationCode')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="organizationName"
            :label="$t('orderMod.buyerOrderSynergy.organizationName2')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="contractStatus"
            :label="$t('orderMod.buyerOrderSynergy.contractStatus')"
            :formatter="formatContractStatus"
            width="100"
          />
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('orderMod.buyerOrderSynergy.categoryName')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="materialCode"
            :label="$t('orderMod.buyerOrderSynergy.materialCode')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="materialName"
            :label="$t('orderMod.buyerOrderSynergy.materialName')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="amount"
            :label="$t('orderMod.buyerOrderSynergy.amount')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="untaxedPrice"
            :label="$t('orderMod.buyerOrderSynergy.untaxedPrice')"
            width="150"
          />
          <el-table-column
            align="center"
            prop="unitName"
            :label="$t('orderMod.buyerOrderSynergy.unit')"
            width="100"
            :formatter="formatUnit"
          />
          <el-table-column
            align="center"
            prop="currencyName"
            :label="$t('orderMod.buyerOrderSynergy.currencyName')"
            width="150"
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
import { getDictItemList, getAllPurUnit } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import { purchaseOrderApi } from 'modb@/orderManagementBuyer/api'

export default {
  name: 'ContractList',
  components: { CPagination, QuickSearch },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
      type: Array,
      default: () => []
    },
    queryParams: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      selection: null,
      contractStatus: [],
      parentOrgTableData: [],
      orgTypeList: [],
      unitList: [],
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
        this.searchParentOrg()
      }
    }
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
  },
  methods: {
    formatContractStatus (row, column, cellValue, index) {
      const dict = this.contractStatus.find(i => i.value === cellValue)
      return dict ? dict.label : cellValue
    },
    formatUnit (row, column, cellValue, index) {
      const dict = this.unitList.find(i => i.value === cellValue)
      return dict ? dict.label : cellValue
    },
    clear () {
      this.form = {}
    },
    getItemObj (val, form) {
      this.form.materialCode = val.materialCode
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
        purchaseOrderApi.queryContractMaterialPage({
          ...data,
          ...this.queryParams,
          contractStatus: 'ARCHIVED'
        })
        .then(data => {
          const { list, pageNum = 0, pageSize = 0, total } = data.data
          console.log(list)
          this.parentOrgTableData = list
          this.parentOrgTableDataPage = { pageNum, pageSize, total }
          if (this.id.length) {
            this.id.forEach(item => {
              const selection = list.find(i => i.contractMaterialId === item)
              if (selection) {
                setTimeout(() => {
                  if (this.$refs.parentOrgTable) {
                    this.$refs.parentOrgTable.toggleRowSelection(
                      selection,
                      true
                    )
                  }
                }, 100)
              }
            })
          }
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
