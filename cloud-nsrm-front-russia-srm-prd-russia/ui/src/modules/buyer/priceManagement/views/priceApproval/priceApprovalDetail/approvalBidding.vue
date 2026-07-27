<template>
  <div class="approval-bidding">
    <!--中标行信息-->
    <div class="approval-bidding-header">
      <span style="padding: 0 11px">{{ $t("bidMod.wonBidRowInfo") }}</span>
      <!--b 添加-->
      <el-button
        v-if="!sourceTypeBoolean"
        type="primary"
        class="detail-pbtn"
        @click="addOneBidItem"
      >
        {{ $t("common.new") }}
      </el-button>
    </div>

    <!--信息表格-->
    <el-table
      :data="approvalBiddingItemTable"
      style="width: 100%;"
      border
      max-height="251px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
        fixed="left"
      />

      <!--t 价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="120"
      >
        <template v-slot="scope">
          <dict-select
            v-model="scope.row.priceType"
            code="DMAND_LINE_TYPE"
            :disabled="sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuId"
        :label="$t('bid_mod.businessEntity')"
        width="150"
      >
        <template v-slot="scope">
          <OrganizationSelector
            ref="organizationSelector"
            v-model="scope.row.orgOuId"
            :parent-id="-1"
            :placeholder="$t('common.pleaseSelect')"
            :disabled="sourceTypeBoolean"
            node-type="OU"
            :clearable="false"
            :scope="scope.row"
            :table-index="scope.$index"
            @select="setOrgObj"
          />
        </template>
      </el-table-column>

      <!--t 库存组织-->
      <el-table-column
        align="center"
        prop="orgInvId"
        :label="$t('bid_mod.inv')"
        width="150"
      >
        <template v-slot="scope">
          <OrganizationSelector
            :ref="`inv_${scope.$index}`"
            v-model="scope.row.orgInvId"
            :parent-id="scope.row.orgOuId || -1"
            :placeholder="$t('common.pleaseSelect')"
            :disabled="sourceTypeBoolean"
            node-type="INV"
            :clearable="false"
            :scope="scope.row"
            :table-index="scope.$index"
            @select="setOrganizationObj"
          />
        </template>
      </el-table-column>

      <!--t 到货地点-->
      <el-table-column
        align="center"
        prop="arrivalPlace"
        :label="$t('contractMod.arrivalPlace')"
        width="150"
      >
        <template v-slot="scope">
          <ProviceCity
            v-model="scope.row.arrivalPlace"
            :disabled="pageFlag.isReadonly || pageFlag.isApproval"
          />
        </template>
      </el-table-column>

      <!--t 供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        width="150"
      >
        <template v-slot="scope">
          <QuickSearch
            :disabled="sourceTypeBoolean"
            :show-input="scope.row.vendorCode"
            show-key="companyCode"
            :table-index="scope.$index"
            :scope-data="scope.row"
            name="scc_sup_company_info"
            @close-quicksearch="setVendorObj"
          />
        </template>
      </el-table-column>

      <!--t 供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="150"
      >
        <template v-slot="scope">
          <QuickSearch
            :disabled="(scope.row.isNoCodeItem === 'N' || !scope.row.isNoCodeItem) && sourceTypeBoolean"
            :show-input="scope.row.itemId ? scope.row.itemCode : ''"
            show-key="materialCode"
            :table-index="scope.$index"
            :scope-data="scope.row"
            :disabled-select="!scope.row.orgOuId || !scope.row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': scope.row.orgInvId }"
            @close-quicksearch="setItemObj"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(scope.row, callback)"
          />
        </template>
      </el-table-column>

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
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

      <!--t 最小起订量-->
      <el-table-column
        align="center"
        prop="minOrderQuantity"
        :label="$t('dataConfMod.orderQuantityMinimum')"
        width="100"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.minOrderQuantity"
            v-input-format="{ type: 'float' }"
            :disabled="sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 需求数量-->
      <el-table-column
        align="center"
        prop="needNum"
        :label="$t('bidMod.demandQuantity2')"
        width="100"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.needNum"
            v-input-format="{ type: 'float' }"
            :disabled="sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 中标数量-->
      <el-table-column
        align="center"
        prop="quotaQuantity"
        :label="$t('bidMod.quotaQuantity')"
        width="100"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.quotaQuantity"
            v-input-format="{ type: 'float' }"
            :disabled="sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bid_mod.unit')"
        width="60"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--t 原币含税单价-->
      <el-table-column
        align="center"
        prop="originalTaxPrice"
        label="原币含税单价"
        width="100"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.originalTaxPrice"
            v-input-format="{ type: 'float' }"
            disabled
          />
        </template>
      </el-table-column>

      <!--t 币种-->
      <el-table-column
        align="center"
        prop="originalCurrency"
        :label="$t('bidMod.allAurrency')"
        width="120"
      >
        <template v-slot="scope">
          <dict-select
            v-model="scope.row.originalCurrency"
            :disabled="sourceTypeBoolean"
            code="currency"
            @change-value="(value, dictItem) => setCurrency(dictItem, scope.row)"
          />
        </template>
      </el-table-column>

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxRate2')"
        width="100"
      >
        <template v-slot="scope">
          <dict-select
            v-model="scope.row.taxKey"
            :disabled="sourceTypeBoolean"
            code="tax"
          />
        </template>
      </el-table-column>

      <!--t 原币未税单价-->
      <el-table-column
        align="center"
        prop="originalNotaxPrice"
        label="原币未税单价"
        width="110"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.originalNotaxPrice"
            v-input-format="{ type: 'float' }"
            :disabled="scope.row.isLadder === 'Y' || sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 汇率 -->
      <el-table-column
        v-if="!pageFlag.isAdd"
        align="center"
        prop="exchangeRate"
        :label="$t('contractMod.exchangeRate')"
        width="110"
      />

      <!--手工创建才需要-->
      <!--t 是否阶梯报价-->
      <el-table-column
        align="center"
        prop="isLadder"
        :label="$t('bidMod.isLadder')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.isLadder"
            :disabled="sourceTypeBoolean"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>

      <!--t 阶梯价报价-->
      <el-table-column
        align="center"
        :label="$t('bidMod.ladderQuote')"
        width="110"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button
            type="primary"
            :disabled="scope.row.isLadder !== 'Y'"
            @click="ladderClick(scope.$index, scope.row)"
          >
            {{ $t('bidMod.ladderPrice') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 付款条款-->
      <el-table-column
        align="center"
        prop="paymentList"
        :label="$t('paymentType.paymentType')"
        width="100"
      >
        <template v-slot="{ $index, row }">
          <el-button
            type="text"
            @click="openPaymentTypeDialog($index, row)"
          >
            {{ $t("common.view") }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 供货周期(自然天)-->
      <el-table-column
        align="center"
        prop="lAndT"
        :label="$t('bidMod.deliveryCycleDays')"
        width="140"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.lAndT"
            :disabled="sourceTypeBoolean"
          />
        </template>
      </el-table-column>

      <!--t 价格执行有效期自-->
      <el-table-column
        align="center"
        prop="startTime"
        :label="$t('bid_mod.priceStartTime')"
        width="170"
      >
        <template v-slot="scope">
          <el-date-picker
            v-model="scope.row.startTime"
            :disabled="sourceTypeBoolean"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
        </template>
      </el-table-column>

      <!--t 价格执行有效期至-->
      <el-table-column
        align="center"
        prop="endTime"
        :label="$t('bid_mod.priceEndTime')"
        width="170"
      >
        <template v-slot="scope">
          <el-date-picker
            v-model="scope.row.endTime"
            :disabled="sourceTypeBoolean"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
        </template>
      </el-table-column>

      <!--t 备注-->
      <el-table-column
        align="center"
        prop="comments"
        :label="$t('common.remark')"
        min-width="150"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.comments"
            :disabled="pageFlag.isReadonly || pageFlag.isApproval"
          />
        </template>
      </el-table-column>

      <!--贸易术语-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bidMod.tradeTerm')"
        min-width="150"
      >
        <template v-slot="scope">
          <dict-select
            v-model="scope.row.tradeTerm"
            code="trade_clause"
            :disabled="!pageType.isHandMake || pageFlag.isReadonly || pageFlag.isApproval"
          />
        </template>
      </el-table-column>

      <!--t 操作-->
      <el-table-column
        align="center"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="70"
        fixed="right"
      >
        <template v-slot="scope">
          <!--b 删除-->
          <el-button
            :disabled="sourceTypeBoolean"
            type="text"
            @click="deleteBidItem(scope.$index, scope.row)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <CPagination
      :total="approvalBiddingItemData.length"
      :page-num="currentPage"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="pageType.isHandMake ? 'PRICE' : approvalHeader.sourceType"
      :edit-row="editRow"
      :readonly="sourceTypeBoolean"
      @savePaymentType="savePaymentType"
    />

    <!--阶梯价-->
    <ladder-price-dialog
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :edit-row="editRow"
      :is-read-only="sourceTypeBoolean"
      @saveLadderPrices="saveLadderPrices"
    />
  </div>
</template>

<script>
/**
 * 中标行信息
 */
import ProviceCity from 'lib@/components/provice-city'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import ladderPriceDialog from './ladderPrice'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'ApprovalBidding',

  components: {
    ProviceCity,
    OrganizationSelector,
    QuickSearch,
    CPagination,
    ladderPriceDialog,
    PaymentTypeDialog
  },

  props: {
    pageFlag: Object,
    pageType: Object,
    approvalHeader: Object,
    approvalHeaderIdAdd: String,
    approvalBiddingItem: Array,
    attrsParamsRow: Object,
    getApprovalHeaderId: [Object, String, Number]
  },

  data () {
    return {
      currentPage: 1,
      pageSize: 15,
      paymentTypeDialogVisible: false,
      ladderPriceDialogVisible: false,
      editRow: null,
      editIndex: ''
    }
  },

  computed: {
    approvalBiddingItemData: {
      get: function () {
        return this.approvalBiddingItem
      },
      set: function (val) {
        this.$emit('update:approvalBiddingItem', val)
      }
    },
    /* 前端分页表格 */
    approvalBiddingItemTable () {
      // 这里是浅拷贝
      return this.approvalBiddingItemData.slice(
        (this.currentPage - 1) * this.pageSize,
        this.currentPage * this.pageSize
      )
    },
    // 是否禁用 如果存在寻源方式，代表不是手工创建的
    sourceTypeBoolean () {
      return !this.pageType.isHandMake || this.pageFlag.isApproval || this.pageFlag.isReadonly
    }
  },

  methods: {
    /* 自定义编排options */
    taxTransformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          // 用key覆盖value
          value: item.key
        }
      })
    },

    /* 添加一行中标行信息 */
    addOneBidItem () {
      this.approvalBiddingItemData.push({
        // 价格类型默认值改成标准
        priceType: 'STANDARD',
        paymentList: []
      })
      if (this.approvalBiddingItemData.length === 0) {
        this.handleCurrentChange(1)
      }
    },

    /* 冗余业务实体信息 */
    setOrgObj (node, _value, scope, index) {
      if (node && scope.orgOuId === node.organizationId) {
        return
      }

      scope.orgOuId = node ? node.organizationId : ''
      scope.orgCode = node ? node.organizationCode : ''
      scope.orgName = node ? node.organizationName : ''
      this.$refs[`inv_${index}`].clearOptions()
      scope.orgInvId = ''
      scope.orgInvCode = ''
      scope.orgInvName = ''

      // 重选库存组织，清空物料选择
      if (scope.isNoCodeItem !== 'Y' && scope.itemId) {
        // 非无料号
        this.setItemObj(null, scope, index)
      } else {
        this.itemsDataRenderUpdate(index, scope)
      }
    },

    /* 冗余库存组织信息 */
    setOrganizationObj (node, _value, scope, index) {
      if (node && scope.orgInvId === node.organizationId) {
        return
      }

      scope.orgInvId = node ? node.organizationId : ''
      scope.organizationCode = node ? node.organizationCode : ''
      scope.organizationName = node ? node.organizationName : ''

      // 重选库存组织，清空物料选择
      if (scope.isNoCodeItem !== 'Y' && scope.itemId) {
        // 非无料号
        this.setItemObj(null, scope, index)
      } else {
        this.itemsDataRenderUpdate(index, scope)
      }
    },

    /* 冗余供应商信息 */
    setVendorObj (val, scope, index) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      scope.linkMan = val ? val.legalPerson : ''
      this.itemsDataRenderUpdate(index, scope)
    },

    /* 冗余物料信息 */
    setItemObj (val, row, index) {
      const rowItemObj = {
        itemId: val ? val.materialId : '',
        itemCode: val ? val.materialCode : '',
        itemDesc: val ? val.materialName : '',
        unit: val ? val.unit : '',
        categoryId: val ? val.categoryId : '',
        categoryCode: val ? val.categoryCode : '',
        categoryName: val ? val.categoryName : ''
      }
      // 如果是简易询价的物料号寻源
      if (this.pageType.isInquiry && row.isNoCodeItem === 'Y' && row.sourceRequirementItemId) {
        // 覆盖表格中同一个无料号来源的物料
        this.approvalBiddingItemData.forEach((item, itemIndex) => {
          if (row.sourceRequirementItemId === item.sourceRequirementItemId && item.isNoCodeItem === 'Y') {
            this.approvalBiddingItemData.splice(itemIndex, 1, {
              ...item,
              ...rowItemObj
            })
          }
        })
      } else {
        row = {
          ...row,
          ...rowItemObj
        }
        this.itemsDataRenderUpdate(index, row)
      }
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
        callback(null)
      }
    },

    /* 冗余币种信息 */
    setCurrency (dictItem, row) {
      row.currencyId = dictItem ? dictItem.id : null
      row.currencyName = dictItem ? dictItem.label : null
    },

    /* 查看付款方式 */
    openPaymentTypeDialog (index, row) {
      this.editIndex = index
      this.editRow = {
        paymentList: row.paymentList || []
      }
      this.paymentTypeDialogVisible = true
    },

    /* 保存付款条款 */
    savePaymentType (val) {
      this.approvalBiddingItemTable[this.editIndex].paymentList = val
    },

    /* 删除一行 */
    deleteBidItem (index) {
      // (当前页码 - 1) * 每页大小 + index
      this.approvalBiddingItemData.splice(this.calcTableItemIndex(index), 1)

      // 当删除整一页的内容时触发，更改当前页
      const listLength = this.approvalBiddingItemData.length
      if (listLength && listLength % this.pageSize === 0) {
        // 判断是最后一页就往前走一页
        if (this.currentPage * this.pageSize === listLength + this.pageSize) {
          this.currentPage = this.currentPage - 1
          this.handleCurrentChange(this.currentPage)
        }
      }
    },

    /* 计算得出分页表格数据源序号 */
    calcTableItemIndex (index) {
      return (this.currentPage - 1) * this.pageSize + index
    },

    /* 当前页变更 */
    handleCurrentChange (val) {
      this.currentPage = val
    },

    /* 页码变更 */
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 触发数组渲染更新 */
    itemsDataRenderUpdate (index, row) {
      this.approvalBiddingItemData.splice(this.calcTableItemIndex(index), 1, row)
    },

    /* 打开阶梯报价 */
    ladderClick (index, row) {
      if (!row.needNum) {
        this.$message.warning('请先输入需求数量')
        return
      }
      this.editIndex = index
      this.editRow = row
      this.ladderPriceDialogVisible = true
    },

    /* 保存阶梯报价 */
    saveLadderPrices (data) {
      this.approvalBiddingItemTable[this.editIndex].ladderPriceType = data.ladderType
      this.approvalBiddingItemTable[this.editIndex].ladderPriceList = data.ladderPriceList
    },

    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        if (this.approvalBiddingItemData.length === 0) {
          // 中标行信息
          this.$message.warning(this.$t('bidMod.msgWonBidRowInfo'))
          resolve(false)
          return
        }
        let resolveStatus = true
        for (const i of this.approvalBiddingItemData) {
          if (!i.vendorCode) {
            // 供应商编码
            this.$message.warning(this.$t('bidMod.msgVendorCode'))
            resolveStatus = false
            return
          }
          if (!i.itemCode) {
            // 物料编码
            this.$message.warning(this.$t('bidMod.msgItemInfo'))
            resolveStatus = false
            return
          }
          if (!i.taxKey) {
            // 税率
            this.$message.warning(this.$t('bidMod.msgSelTaxRate'))
            resolveStatus = false
            return
          }
          if (!i.categoryName) {
            // 品类
            this.$message.warning(this.$t('bidMod.msgEnterCate'))
            resolveStatus = false
            return
          }
          if (!i.originalCurrency) {
            // 币种
            this.$message.warning(this.$t('vendorMod.msgCurrencyCode'))
            resolveStatus = false
            return
          }
          if (!i.quotaQuantity && this.pageType.isHandMake) {
            // 中标数量必填
            this.$message.warning(this.$t('vendorMod.msgSelBidder'))
            resolveStatus = false
            return
          }
        }
        resolve(resolveStatus)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.approval-bidding-header {
  margin: 15px 0 10px;
}

.import-btn {
  display: inline-block;
  margin: 0 15px;
  :deep(.el-button) {
    min-width: 56px;
    height: 24px;
    line-height: 22px;
    font-size: 12px;
    border-radius: 2px;
    padding: 1px 14px;
  }
}
</style>
