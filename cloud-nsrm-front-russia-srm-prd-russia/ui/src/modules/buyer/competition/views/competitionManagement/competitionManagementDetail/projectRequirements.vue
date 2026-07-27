<template>
  <div class="project-requirements-wrap">
    <el-form :model="projectRequirementsForm">
      <!--需求简述-->
      <SrmRow>
        <SrmCol :init-col="1">
          <!--需求简述-->
          <el-form-item :label="$t('bidMod.resume')" label-width="80px">
            <el-input
              v-model="requireDesc"
              :placeholder="$t('bidMod.vendorVisible')"
              type="textarea"
              :rows="2"
              :disabled="readonly"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!--按钮操作区域-->
    <div class="table-toolbar-wrap">
      <span class="table-toolbar-tips">{{ $t('bidMod.demandDetail') }}</span>

      <template v-if="!readonly">
        <!--添加-->
        <el-button type="primary" @click="addRow">
          {{ $t('common.new') }}
        </el-button>

        <!--批量维护付款条款-->
        <el-button type="primary" @click="openBatchMaintainPaymentTypeDialog">
          {{ $t('bidMod.bidsBulkMaintenance') }}
        </el-button>

        <!--批量维护税率-->
        <el-button type="primary" @click="openBatchMaintainTaxDialog">
          {{ $t('bidMod.batchAddTaxRate') }}
        </el-button>

        <!--批量维护币种-->
        <el-button type="primary" @click="openBatchMaintainCurrencyDialog">
          {{ $t('bidMod.batchAddCurrency') }}
        </el-button>
      </template>
    </div>

    <!--需求明细-->
    <el-table
      ref="requirementTable"
      :data="requirementLineList"
      border
      @row-click="requirementRowClick"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuId"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <OrganizationSelector
            v-if="scope.row.editable"
            v-model="scope.row.orgOuId"
            :scope="scope"
            node-type="OU"
            :parent-id="-1"
            :clearable="false"
            :disabled="(!!scope.row.orgOuId && scope.row.sourceFromNo) || readonly"
            @select="selectOrg"
          />
          <span v-else>{{ scope.row.orgOuName }}</span>
        </template>
      </el-table-column>

      <!--库存组织-->
      <el-table-column
        align="center"
        prop="orgInvId"
        :label="$t('bid_mod.inv')"
        width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <OrganizationSelector
            v-if="scope.row.editable"
            :ref="`inv_${scope.$index}`"
            v-model="scope.row.orgInvId"
            :scope="scope.row"
            node-type="INV"
            :parent-id="scope.row.orgOuId"
            :clearable="false"
            :disabled="(!!scope.row.orgInvId && scope.row.sourceFromNo) || readonly"
            @select="selectInv"
          />
          <span v-else>{{ scope.row.orgInvName }}</span>
        </template>
      </el-table-column>

      <!--交货地点(仅外协)-->
      <el-table-column
        align="center"
        prop="deliveryPlace"
        :label="$t('bid_mod.deliveryPlace')"
        width="150"
      >
        <template v-slot="scope">
          <ProviceCity
            v-if="scope.row.editable"
            v-model="scope.row.deliveryPlace"
            :disabled="readonly"
          />
          <RenderAsyncText v-else :cell-value="scope.row.deliveryPlace" />
        </template>
      </el-table-column>

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <QuickSearch
            v-if="row.editable"
            :disabled="(!!row.itemCode && row.sourceFromNo) || readonly"
            :show-input="row.itemCode"
            show-key="materialCode"
            :scope-data="row"
            :disabled-select="!row.orgOuId || !row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': row.orgInvId }"
            @close-quicksearch="selectItem"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(row, callback)"
          />
          <span v-else>{{ row.itemCode }}</span>
        </template>
      </el-table-column>

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.itemDesc"
            :disabled="!(!scope.row.itemDesc && scope.row.sourceFromNo) || readonly"
          />
          <span v-else>{{ scope.row.itemDesc }}</span>
        </template>
      </el-table-column>

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.tech_taxRate')"
        width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="scope.row.editable"
            v-model="scope.row.taxKey"
            code="tax"
            :disabled="readonly"
            @change-value="(value, dictItem) => selectTax(value, dictItem, scope.row)"
          />
          <span v-else>{{ $getDictLabel('tax', scope.row.taxKey) }}</span>
        </template>
      </el-table-column>

      <!--币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.allAurrency')"
        width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="scope.row.editable"
            v-model="scope.row.orderCurrency"
            code="currency"
            :disabled="readonly"
            :transform-options="transformOptions"
          />
          <span v-else>{{ $getDictLabel('currency', scope.row.orderCurrency) }}</span>
        </template>
      </el-table-column>

      <!--采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="150"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="scope.row.editable"
            v-model="scope.row.priceType"
            code="PRICE_TYPE"
            :disabled="readonly"
          />
          <span v-else>{{ $getDictLabel('PRICE_TYPE', scope.row.priceType) }}</span>
        </template>
      </el-table-column>

      <!--起拍价-->
      <el-table-column
        align="center"
        prop="startOrderNotaxPrice"
        :label="$t('bidMod.startingPrice')"
        width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.startOrderNotaxPrice"
            v-input-format="{ type: 'float', negative: false, zero: false }"
          />
          <span v-else>{{ scope.row.startOrderNotaxPrice }}</span>
        </template>
      </el-table-column>

      <!--贸易条款-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="scope.row.editable"
            v-model="scope.row.tradeTerm"
            code="trade_clause"
            :disabled="readonly"
          />
          <span v-else>{{ $getDictLabel('trade_clause', scope.row.tradeTerm) }}</span>
        </template>
      </el-table-column>

      <!--质保期(月)-->
      <el-table-column
        align="center"
        prop="warrantyPeriod"
        :label="$t('bid_mod.warrantyPeriod')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.warrantyPeriod"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.warrantyPeriod }}</span>
        </template>
      </el-table-column>

      <!--采购类型-->
      <el-table-column
        align="center"
        prop="purchaseType"
        :label="$t('bid_mod.purchaseType')"
        width="150"
        :formatter="(row, column, value) => $getDictLabel('PURCHASE_TYPE', value)"
      />

      <!--采购申请号-->
      <el-table-column
        align="center"
        prop="sourceFromNo"
        :label="$t('bid_mod.purchaseRequest')"
        width="150"
      />

      <!--采购申请行号-->
      <el-table-column
        align="center"
        prop="sourceFromLineNo"
        :label="$t('bid_mod.purchaseRequestRowNum')"
        width="150"
        show-overflow-tooltip
      />

      <!--需求日期-->
      <el-table-column
        align="center"
        prop="requireDate"
        :label="$t('bidMod.ceeaDemandDate')"
        width="160"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="scope.row.editable"
            v-model="scope.row.requireDate"
            :disabled="readonly"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            :placeholder="$t('bidMod.optionDate')"
          />
          <span v-else>{{ $dayjsParse(scope.row.requireDate) }}</span>
        </template>
      </el-table-column>

      <!--行类型-->
      <el-table-column
        align="center"
        prop="rowType"
        :label="$t('bidMod.itemType')"
        width="160"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="scope.row.editable"
            v-model="scope.row.rowType"
            code="DMAND_LINE_TYPE"
          />
          <span v-else>{{ $getDictLabel('DMAND_LINE_TYPE', scope.row.rowType) }}</span>
        </template>
      </el-table-column>

      <!--预计采购数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.quantity')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.requireQuantity"
            v-input-format="{ type: 'float' }"
            :disabled="(!!scope.row.requireQuantity && scope.row.sourceFromNo) || readonly"
          />
          <span v-else>{{ scope.row.requireQuantity }}</span>
        </template>
      </el-table-column>

      <!--预计采购金额-->
      <el-table-column
        align="center"
        prop="buyAmount"
        :label="$t('bidMod.amount')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.buyAmount"
            v-input-format="{ type: 'float' }"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.buyAmount }}</span>
        </template>
      </el-table-column>

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('bidMod.remark')"
        min-width="150"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.remark"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.remark }}</span>
        </template>
      </el-table-column>

      <!--操作-->
      <el-table-column
        v-if="!readonly"
        fixed="right"
        :label="$t('bidMod.operation')"
        width="70"
      >
        <template v-slot="scope">
          <!--删除-->
          <el-button
            v-if="!scope.row.sourceFromNo"
            type="text"
            @click="deleteRow(scope.$index, scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量维护币种 -->
    <BatchMaintainCurrencyDialog
      v-if="batchMaintainCurrencyDialogVisible"
      :visible.sync="batchMaintainCurrencyDialogVisible"
      :transform-options="transformOptions"
      @save="saveBatchCurrency"
    />

    <!-- 批量维护税率 -->
    <BatchMaintainTaxDialog
      v-if="batchMaintainTaxDialogVisible"
      :visible.sync="batchMaintainTaxDialogVisible"
      @save="saveBatchTax"
    />

    <!--批量维护付款条款-->
    <PaymentTypeDialog
      v-if="batchMaintainPaymentTypeDialogVisible"
      :visible.sync="batchMaintainPaymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
      :edit-row="{ paymentList: purchasePaymentTermList }"
      @savePaymentType="saveBatchPaymentType"
    />
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ProviceCity from 'lib@/components/provice-city'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import BatchMaintainCurrencyDialog from './projectRequirements/batchMaintainCurrencyDialog'
import BatchMaintainTaxDialog from './projectRequirements/batchMaintainTaxDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'ProjectRequirements',

  components: {
    QuickSearch,
    OrganizationSelector,
    ProviceCity,
    RenderAsyncText,
    BatchMaintainCurrencyDialog,
    BatchMaintainTaxDialog,
    PaymentTypeDialog
  },

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    // 可报价币种
    currencyList: {
      type: [Array, Object],
      required: true
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      projectRequirementsForm: {},
      requireDesc: '',
      batchMaintainPaymentTypeDialogVisible: false,
      batchMaintainCurrencyDialogVisible: false,
      batchMaintainTaxDialogVisible: false,
      purchasePaymentTermList: [],
      requirementLineList: [],
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    currencyCodes () {
      return this.currencyList.map(item => item.currencyCode)
    }
  },

  watch: {
    'baseInfo.requireDesc': {
      handler (val) {
        if (typeof val !== 'undefined') {
          this.requireDesc = val
        }
      },
      immediate: true
    },
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getRequireInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询需求详情 */
    async getRequireInfo () {
      if (!this.baseInfo.projectId) {
        return
      }

      const response = await compBuyerHttp.init.getRequireInfo(this.baseInfo.projectId)
      if (response && response.data && Array.isArray(response.data)) {
        this.requirementLineList = response.data.map(item => {
          let DP
          try {
            DP = JSON.parse(item.deliveryPlace)
          } catch (e) { /* noting */ }

          return {
            ...item,
            projectId: this.baseInfo.projectId,
            editable: false,
            deliveryPlace: DP,
            // 默认基本信息中的价格有效期
            priceStartTime: this.baseInfo.priceStartTime,
            priceEndTime: this.baseInfo.priceEndTime
          }
        })

        // 付款条款全部统一
        if (this.requirementLineList.length > 0) {
          this.purchasePaymentTermList = this.requirementLineList[0].paymentList || []
        }
      }
    },

    /* 过滤可以报价的币种 */
    transformOptions (options) {
      return options.filter(item => this.currencyCodes.includes(item.value))
    },

    /* 新增一行物料行 */
    addRow () {
      this.requirementLineList.push({
        projectId: this.baseInfo.projectId,
        editable: true,
        orgOuId: '',
        orgInvId: '',
        orgInvName: '',
        orgInvCode: '',
        categoryId: '',
        categoryName: '',
        itemGroup: '',
        itemCode: '',
        itemDesc: '',
        taxCurrentPrice: '',
        deliveryPlace: '',
        startOrderNotaxPrice: '',
        taxRate: '',
        taxKey: null,
        requireQuantity: '',
        buyAmount: '',
        priceType: 'STANDARD',
        unit: '',
        remark: '',
        requireDate: '',
        rowType: '',
        // 默认基本信息中的价格有效期
        priceStartTime: this.baseInfo.priceStartTime,
        priceEndTime: this.baseInfo.priceEndTime
      })
    },

    /* 删除物料行 */
    deleteRow (index) {
      this.requirementLineList.splice(index, 1)
    },

    /* 批量维护付款条款 */
    // 打开
    openBatchMaintainPaymentTypeDialog () {
      if (this.requirementLineList.length === 0) {
        this.$message.warning('请至少添加一行需求！')
        return
      }
      this.batchMaintainPaymentTypeDialogVisible = true
    },
    // 保存
    saveBatchPaymentType (data) {
      this.purchasePaymentTermList = data
    },
    /* END */

    /* 批量维护税率 */
    // 打开
    openBatchMaintainTaxDialog () {
      if (this.requirementLineList.length === 0) {
        this.$message.warning('请至少添加一行需求！')
        return
      }
      this.batchMaintainTaxDialogVisible = true
    },
    // 保存
    saveBatchTax ({ taxKey, taxRate }) {
      // 批量写入新币种
      this.requirementLineList = this.requirementLineList.map(item => {
        return {
          ...item,
          taxKey,
          taxRate
        }
      })
    },
    /* END */

    /* 批量维护币种 */
    // 打开
    openBatchMaintainCurrencyDialog () {
      if (this.requirementLineList.length === 0) {
        this.$message.warning('请至少添加一行需求！')
        return
      }
      this.batchMaintainCurrencyDialogVisible = true
    },
    // 保存
    saveBatchCurrency (data) {
      // 批量写入新币种
      this.requirementLineList = this.requirementLineList.map(item => {
        return {
          ...item,
          orderCurrency: data
        }
      })
    },
    /* END */

    /* 行点击 */
    requirementRowClick (row) {
      if (this.readonly) {
        return
      }
      row.editable = true
    },

    /* 选择组织 */
    selectOrg (val, id, { row, $index }) {
      if (val && row.orgOuId === val.organizationId) {
        return
      }

      row.orgOuId = val ? val.organizationId : ''
      row.orgOuCode = val ? val.organizationCode : ''
      row.orgOuName = val ? val.organizationName : ''
      this.$refs[`inv_${$index}`].clearOptions()
      row.orgInvId = ''
      row.orgInvCode = ''
      row.orgInvName = ''

      // 重选业务实体，清空物料选择
      if (row.itemId) {
        // 非无料号
        this.selectItem(null, row)
      }
    },

    /* 选择库存组织 */
    selectInv (val, id, row) {
      if (val && row.orgInvId === val.organizationId) {
        return
      }

      row.orgInvId = val ? val.organizationId : ''
      row.orgInvCode = val ? val.organizationCode : ''
      row.orgInvName = val ? val.organizationName : ''

      // 重选库存组织，清空物料选择
      if (row.itemId) {
        // 非无料号
        this.selectItem(null, row)
      }
    },

    /* 选择物料 */
    selectItem (val, row) {
      row.itemId = val ? val.materialId : ''
      row.itemCode = val ? val.materialCode : ''
      row.itemDesc = val ? val.materialName : ''
      row.unit = val ? val.unit : ''
      row.categoryId = val ? val.categoryId : ''
      row.categoryCode = val ? val.categoryCode : ''
      row.categoryName = val ? val.categoryName : ''
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
        callback(null)
      }
    },

    /* 选择税率 */
    selectTax (val, dictItem, row) {
      row.taxRate = dictItem.key
    },

    /* 保存 */
    async saveRequirement (type) {
      // 校验表格必填项
      if (!validateRequiredColumn(
        this.$refs.requirementTable,
        this.requirementLineList,
        {
          validateScope: true,
          tableTitle: '需求明细'
        }
      )) {
        return { status: false }
      }

      // 请批量维护付款条款
      if (this.purchasePaymentTermList.length === 0) {
        this.$message.warning('请批量维护付款条款')
        return { status: false }
      }

      try {
        const response = await compBuyerHttp.init.editRequireInfo({
          projectId: this.baseInfo.projectId,
          // 需求简述
          requireDesc: this.requireDesc,
          // 物料需求信息
          itemList: this.requirementLineList.map(item => {
            return {
              ...item,
              // 冗余付款条款
              paymentList: this.purchasePaymentTermList.concat()
            }
          }),
          // 是否暂存
          isTempSave: type !== 'nextOne'
        })

        if (response) {
          this.$message.success(this.$t('common.successSave'))
          return { status: true, data: response.data }
        } else {
          return { status: false }
        }
      } catch (e) {
        return { status: false }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.project-requirements-wrap {
  .table-toolbar-wrap {
    padding: 0 5px 10px 9px;
    overflow: hidden;
    .table-toolbar-tips {
      padding: 0 11px;
      float: left;
      line-height: 30px;
    }
  }
}
</style>
