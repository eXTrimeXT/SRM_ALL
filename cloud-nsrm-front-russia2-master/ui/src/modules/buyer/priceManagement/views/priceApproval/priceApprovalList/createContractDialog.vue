<template>
  <srm-dialog
    :title="$t('contractMod.createContract')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    size="large"
  >
    <el-form
      ref="filterForm"
      :model="filterForm"
      label-width="100px"
      label-position="top"
      class="form-incontainer"
    >
      <srm-row>
        <srm-col>
          <!--f 价格审批单号-->
          <el-form-item :label="$t('bidMod.approvalNo')">
            <el-input
              v-model="filterForm.approvalNo"
              disabled
            />
          </el-form-item>
        </srm-col>
        <srm-col v-if="filterForm.type === 'SEPERATELYSIGNING'">
          <!--f 业务实体-->
          <el-form-item
            :label="$t('bid_mod.businessEntity')"
          >
            <OrganizationSelector
              ref="organizationSelector"
              v-model="filterForm.orgId"
              :parent-id="-1"
              node-type="OU"
            />
          </el-form-item>
        </srm-col>
        <srm-col>
          <!--f 供应商-->
          <el-form-item :label="$t('common.vendor')">
            <QuickSearch
              :show-input="filterForm.vendorName"
              show-key="companyName"
              :scope-data="filterForm"
              name="scc_sup_company_info"
              @close-quicksearch="getVendorObj"
            />
          </el-form-item>
        </srm-col>

        <srm-col>
          <!--f 物料编码-->
          <el-form-item :label="$t('common.materialCode')">
            <QuickSearch
              :show-input="filterForm.itemCode"
              show-key="materialCode"
              :scope-data="filterForm"
              name="scc_base_material_item_display"
              @close-quicksearch="getItemObj"
            />
          </el-form-item>
        </srm-col>
        <srm-col>
          <p style="margin-top: 20px">
            <!--b 查询-->
            <el-button
              type="primary"
              @click="getDisplayItemList"
            >
              {{ $t("common.search") }}
            </el-button>
            <!--b 确定-->
            <el-button
              type="primary"
              @click="createContract"
            >
              {{ $t("common.confirm") }}
            </el-button>
          </p>
        </srm-col>
      </srm-row>
    </el-form>

    <el-table
      :data="displayMaterialItem"
      style="width: 100%"
      border
      height="345px"
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        fixed="left"
      />
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--t 价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
      />

      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 到货地点-->
      <el-table-column
        align="center"
        prop="arrivalPlace"
        :label="$t('contractMod.arrivalPlace')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <RenderAsyncText :cell-value="scope.row.arrivalPlace" />
        </template>
      </el-table-column>

      <!--t 供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('common.materialCode')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('common.materialName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 品类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('common.category')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bid_mod.unit')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="originalTaxPrice"
        :label="$t('contractMod.taxedPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 币种-->
      <el-table-column
        align="center"
        prop="originalCurrency"
        :label="$t('contractMod.currencyCode')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('currency', cellValue)"
      />

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('contractMod.taxRate')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('tax', cellValue)"
      />

      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="originalNotaxPrice"
        :label="$t('contractMod.notaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 付款条款-->
      <el-table-column
        align="center"
        prop="paymentList"
        :label="$t('paymentType.paymentType')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openPaymentTypeDialog(scope.row)"
          >
            {{ $t("common.view") }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 价格有效期自-->
      <el-table-column
        align="center"
        prop="startTime"
        :label="$t('bid_mod.defaultPriceValidFrom')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => parseTime(cellValue)"
      />

      <!--t 价格有效期至-->
      <el-table-column
        align="center"
        prop="endTime"
        :label="$t('contractMod.price_endDate')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => parseTime(cellValue)"
      />
    </el-table>

    <!--d 付款条款查看--->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      business-type="PRICE"
      :edit-row="viewRow"
      readonly
    />
  </srm-dialog>
</template>

<script>
/**
 * 创建合同弹窗
 */
import QuickSearch from 'lib@/components/QuickSearch'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import OrganizationSelector from 'lib@/components/organization-selector'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'CreateContractDialog',
  components: {
    QuickSearch,
    RenderAsyncText,
    OrganizationSelector,
    PaymentTypeDialog
  },
  props: {
    visible: Boolean,
    methodsOpts: Array,
    editRow: Object
  },
  data () {
    return {
      filterForm: {
        approvalHeaderId: null,
        itemId: null,
        vendorId: null,
        orgId: null,
        type: null,
        ceeaIfVirtual: 'N'
      },
      displayMaterialItem: [],
      selectionItem: [],
      paymentTypeDialogVisible: false,
      viewRow: null
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    editRow: {
      handler (newValue) {
        if (newValue) {
          this.filterForm.approvalNo = newValue.approvalNo
          this.filterForm.approvalHeaderId = newValue.approvalHeaderId
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.getDisplayItemList()
  },
  methods: {
    parseTime (val) {
      return this.$parseTime(val)
    },

    getDisplayItemList () {
      this.$http({
        url: '/api-inq/price/approval/getItemVoByParam',
        method: 'POST',
        data: this.filterForm,
        loading: true
      }).then(data => {
        this.displayMaterialItem = data.data
      })
    },
    /* 创建合同方式 */
    clearFilterForm () {
      this.filterForm.itemId = null
      this.filterForm.vendorId = null
      this.filterForm.orgId = null
      this.filterForm.ceeaIfVirtual = null
    },
    /* 冗余供应商信息 */
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    /* 勾选行 */
    handleSelectionChange (selection) {
      this.selectionItem = selection
    },

    /* 查看付款条款 */
    openPaymentTypeDialog (row) {
      this.viewRow = {
        paymentList: row.paymentList || []
      }
      this.paymentTypeDialogVisible = true
    },

    createContract () {
      if (!this.filterForm.ceeaIfVirtual) {
        this.$message.error(this.$t('contractMod.ifVirtualRequire'))
      }
      if (this.selectionItem.length === 0) {
        return this.$message.warning(this.$t('purchaseDemand.selectData'))
      }
      this.$http({
        url:
          '/api-inq/price/approval/genContractByApprovalBidingItemVOs',
        method: 'POST',
        data: this.selectionItem.map(i => ({
          ...i,
          ceeaIfVirtual: this.filterForm.ceeaIfVirtual
        })),
        loading: true
      })
        .then(() => {
          this.dialogVisible = false
          this.$message.success(this.$t('common.success'))
          this.$router.push('/contractManagement/contractMaintainList')
        })
    },
    /* 冗余物料编码信息 */
    getItemObj (val, scope) {
      scope.itemId = val ? val.materialId : ''
      scope.itemCode = val ? val.materialCode : ''
      scope.itemName = val ? val.materialName : ''
    }
  }
}
</script>
