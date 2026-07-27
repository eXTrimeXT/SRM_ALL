<template>
  <srm-dialog
    :title="$t('contractMod.createContract')"
    size="large"
    :visible.sync="dialogVisible"
    append-to-body
    destroy-on-close
    :close-on-click-modal="false"
    :before-close="close"
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
          </p>
        </srm-col>
      </srm-row>
    </el-form>

    <vxe-table
      border
      show-overflow
      auto-resize
      sync-resize
      max-height="280"
      min-height="240"
      :scroll-x="{enabled: true}"
      :scroll-y="{enabled: true}"
      :stripe="true"
      :data="displayMaterialItem"
      :checkbox-config="{trigger: 'row', reserve: true}"
      @checkbox-change="handleSelectionChange"
      @checkbox-all="handleSelectionChange"
    >
      <vxe-column
        type="checkbox"
        width="55"
        fixed="left"
      />
      <vxe-column
        align="center"
        type="seq"
        :title="$t('common.sort')"
        width="50"
        fixed="left"
      />

      <!--t 价格类型-->
      <vxe-column
        align="center"
        field="priceType"
        :title="$t('bid_mod.priceType')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ dictFormat(scope.row.priceType, 'DMAND_LINE_TYPE') }}
        </template>
      </vxe-column>

      <!--t 业务实体-->
      <vxe-column
        align="center"
        field="orgOuName"
        :title="$t('bid_mod.businessEntity')"
        min-width="150"
      />

      <!--t 库存组织-->
      <vxe-column
        align="center"
        field="orgInvName"
        :title="$t('bid_mod.inv')"
        min-width="150"
      />

      <!--t 到货地点-->
      <vxe-column
        align="center"
        field="arrivalPlace"
        :title="$t('contractMod.arrivalPlace')"
        min-width="100"
      >
        <template slot-scope="scope">
          <RenderAsyncText :cell-value="scope.row.arrivalPlace" />
        </template>
      </vxe-column>

      <!--t 供应商编码-->
      <vxe-column
        align="center"
        field="vendorCode"
        :title="$t('bidMod.vendorCode')"
        min-width="100"
      />

      <!--t 供应商名称-->
      <vxe-column
        align="center"
        field="vendorName"
        :title="$t('common.vendorName')"
        min-width="150"
      />

      <!--t 物料编码-->
      <vxe-column
        align="center"
        field="itemCode"
        :title="$t('common.materialCode')"
        min-width="100"
      />

      <!--t 物料名称-->
      <vxe-column
        align="center"
        field="itemDesc"
        :title="$t('common.materialName')"
        min-width="150"
      />

      <!--t 品类-->
      <vxe-column
        align="center"
        field="categoryName"
        :title="$t('common.category')"
        min-width="100"
      />

      <!--t 单位-->
      <vxe-column
        align="center"
        field="unit"
        :title="$t('bid_mod.unit')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ dictFormat(scope.row.unit, 'unit') }}
        </template>
      </vxe-column>

      <!--t 含税单价-->
      <vxe-column
        align="center"
        field="orderTaxPrice"
        :title="$t('contractMod.taxedPrice')"
        min-width="100"
      />

      <!--t 币种-->
      <vxe-column
        align="center"
        field="orderCurrency"
        :title="$t('contractMod.currencyCode')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ dictFormat(scope.row.orderCurrency, 'currency') }}
        </template>
      </vxe-column>

      <!--t 税率-->
      <vxe-column
        align="center"
        field="taxKey"
        :title="$t('contractMod.taxRate')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ dictFormat(scope.row.taxKey, 'tax') }}
        </template>
      </vxe-column>

      <!--t 未税单价-->
      <vxe-column
        align="center"
        field="orderNotaxPrice"
        :title="$t('contractMod.notaxPrice')"
        min-width="100"
      />

      <!--t 付款条款-->
      <vxe-column
        align="center"
        field="paymentList"
        :title="$t('paymentType.paymentType')"
        width="100"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openPaymentTypeDialog(scope.row)"
          >
            {{ $t("common.view") }}
          </el-button>
        </template>
      </vxe-column>

      <!--t 价格有效期自-->
      <vxe-column
        align="center"
        field="priceStartTime"
        :title="$t('bid_mod.defaultPriceValidFrom')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ parseTime(scope.row.priceStartTime) }}
        </template>
      </vxe-column>

      <!--t 价格有效期至-->
      <vxe-column
        align="center"
        field="priceEndTime"
        :title="$t('contractMod.price_endDate')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{ parseTime(scope.row.priceEndTime) }}
        </template>
      </vxe-column>
    </vxe-table>

    <CPagination
      :total="pageInfo.total"
      :page-num="pageInfo.currentPage"
      :page-size="pageInfo.pageSize"
      @current-change="changeCurrentIndex"
      @size-change="changeCurrentSize"
    />
    <div slot="footer">
      <div style="text-align:right;">
        <!--b 取消-->
        <el-button
          type="ghost"
          @click="close"
        >
          {{ $t("common.cancel") }}
        </el-button>
        <!--b 确定-->
        <el-button
          type="primary"
          @click="createContract"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </div>

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
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import OrganizationSelector from 'lib@/components/organization-selector'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'CreateContractDialog',
  components: {
    QuickSearch,
    CPagination,
    RenderAsyncText,
    OrganizationSelector,
    PaymentTypeDialog
  },
  props: {
    visible: Boolean,
    editRow: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      filterForm: {
        approvalId: null,
        itemId: null,
        vendorId: null,
        orgId: null,
        type: null
      },
      pageInfo: {
        currentPage: 1,
        pageSize: 15,
        total: 0
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
    dialogVisible: {
      handler (newValue) {
        if (newValue) {
          const { approvalId, approvalNo } = this.editRow
          this.filterForm.approvalNo = approvalNo
          this.filterForm.approvalId = approvalId
          this.getDisplayItemList()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    parseTime (val) {
      return parseTimeYMD(val)
    },
    dictFormat (val, code) {
      return this.$getDictLabel(code, val)
    },
    changeCurrentIndex (currentNum) {
      this.pageInfo.currentPage = currentNum
      this.getDisplayItemList()
    },
    changeCurrentSize (currentSize) {
      this.pageInfo.pageSize = currentSize
      this.pageInfo.currentPage = 1
      this.getDisplayItemList()
    },
    getDisplayItemList () {
      let filter = {}
      for (let key in this.filterForm) {
        if (this.filterForm[key]) {
          filter[key] = {
            eq: this.filterForm[key]
          }
        }
      }
      let transformParams = transformMQL.save(
        'PriceApprovalItem',
        {
          filter: {
            ...filter
          },
          page: {
            pageNum: this.pageInfo.currentPage,
            pageSize: this.pageInfo.pageSize
          }
        },
        'query', {
          '*': {},
          'paymentList': {
            '*': {}
          }
        }
      )
      this.$http({
        url: '/api-sou/api-ql/PriceApprovalItem/query',
        method: 'POST',
        data: transformParams,
        loading: true
      }).then(response => {
        if (response && response.data.records) {
          this.displayMaterialItem = response.data.records || []
          this.pageInfo.total = response.data.total
        }
      })
    },
    /* 创建合同方式 */
    clearFilterForm () {
      this.filterForm.itemId = null
      this.filterForm.vendorId = null
      this.filterForm.orgId = null
    },
    /* 冗余供应商信息 */
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    /* 勾选行 */
    handleSelectionChange (selected) {
      this.selectionItem = selected.records
    },

    /* 查看付款条款 */
    openPaymentTypeDialog (row) {
      this.viewRow = {
        paymentList: row.paymentList || []
      }
      this.paymentTypeDialogVisible = true
    },

    createContract () {
      if (this.selectionItem.length === 0) {
        return this.$message.warning(this.$t('purchaseDemand.selectData'))
      }
      let approvalItemIdList = this.selectionItem.map(item => ({
        approvalItemId: item.approvalItemId
      }))
      let transformParams = transformMQL.save('PriceApprovalForBuyer', approvalItemIdList, 'generateContractInfo')
      this.$http({
        url: '/api-sou/api-ql/PriceApprovalForBuyer/generateContractInfo',
        method: 'POST',
        data: transformParams,
        loading: true
      })
        .then(() => {
          this.close()
          this.$message.success(this.$t('common.success'))
          this.$router.push('/contractManagement/contractMaintainList')
        })
    },
    /* 冗余物料编码信息 */
    getItemObj (val, scope) {
      scope.itemId = val ? val.materialId : ''
      scope.itemCode = val ? val.materialCode : ''
      scope.itemName = val ? val.materialName : ''
    },
    close () {
      this.$emit('close')
    }
  }
}
</script>
