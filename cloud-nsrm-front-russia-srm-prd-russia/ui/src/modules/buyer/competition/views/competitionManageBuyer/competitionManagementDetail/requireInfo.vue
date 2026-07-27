<template>
  <div class="project-requirements-wrap">
    <!--按钮操作区域-->
    <div class="table-toolbar-wrap">
      <template v-if="!readonly">
        <!--添加-->
        <el-button type="primary" @click="addRow">
          {{ $t('common.new') }}
        </el-button>

        <!--批量维护付款条款-->
        <el-button type="primary" @click="openBatchMaintainPaymentTypeDialog">
          {{ $t('bidMod.bidsBulkMaintenance') }}
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
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--业务实体-->
      <el-table-column
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
            :disabled="readonly || !!scope.row.sourceFromNo"
            @select="selectOrg"
          />
          <span v-else>{{ scope.row.orgOuName }}</span>
        </template>
      </el-table-column>

      <!--库存组织-->
      <el-table-column
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
            :disabled="readonly || !!scope.row.sourceFromNo"
            @select="selectInv"
          />
          <span v-else>{{ scope.row.orgInvName }}</span>
        </template>
      </el-table-column>

      <!--无料号寻源-->
      <el-table-column
        prop="noCodeItem"
        label="无料号寻源"
        width="150"
      >
        <template v-slot="scope">
          <el-checkbox
            v-if="scope.row.editable"
            v-model="scope.row.noCodeItem"
            :disabled="readonly || !!scope.row.sourceFromNo"
            true-label="Y"
            false-label="N"
            @change="noCodeItemChange(scope)"
          />
          <span v-else>{{ $getDictLabel('YES_OR_NO', scope.row.noCodeItem) }}</span>
        </template>
      </el-table-column>

      <!--物料编码-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <QuickSearch
            v-if="scope.row.editable && scope.row.noCodeItem !== 'Y'"
            :disabled="readonly || !!scope.row.sourceFromNo"
            :show-input="scope.row.itemCode"
            show-key="materialCode"
            :scope-data="scope.row"
            :disabled-select="!scope.row.orgOuId || !scope.row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': scope.row.orgInvId }"
            @close-quicksearch="selectItem"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(scope.row, callback)"
          />
          <span v-else>{{ scope.row.itemCode }}</span>
        </template>
      </el-table-column>

      <!--物料名称-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable && scope.row.noCodeItem === 'Y'"
            v-model="scope.row.itemDesc"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.itemDesc }}</span>
        </template>
      </el-table-column>

      <!--单位-->
      <el-table-column
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
      >
        <template v-slot="scope">
          <dict-select v-if="scope.row.editable && scope.row.noCodeItem === 'Y'" v-model="scope.row.unit" code="unit" />
          <span v-else>{{ $getDictLabel('unit', scope.row.unit) }}</span>
        </template>
      </el-table-column>

      <!-- 采购品类 -->
      <el-table-column
        prop="categoryName"
        :label="$t('vendorMod.category')"
        min-width="200"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <CCategorySelect
            v-if="scope.row.editable"
            v-model="scope.row.categoryName"
            :disabled="readonly || !!scope.row.itemCode || !!scope.row.sourceFromNo"
            :scope="scope.row"
            showKey="categoryName"
            :placeholder="$t('vendorMod.msgCategoryNormalizer')"
            @select="comfirmSelect"
          />
          <span v-else>{{ scope.row.categoryName }}</span>
        </template>
      </el-table-column>

      <!--需求数量-->
      <el-table-column
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity2')"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.requireQuantity"
            :disabled="readonly || !!scope.row.sourceFromNo"
          />
          <span v-else>{{ scope.row.requireQuantity }}</span>
        </template>
      </el-table-column>

      <!--交货日期-->
      <el-table-column
        prop="requireDate"
        :label="$t('contractMod.deliveryDate')"
        width="160"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="scope.row.editable"
            v-model="scope.row.requireDate"
            :disabled="readonly || !!scope.row.sourceFromNo"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            :placeholder="$t('bidMod.optionDate')"
          />
          <span v-else>{{ $dayjsParse(scope.row.requireDate) }}</span>
        </template>
      </el-table-column>

      <!--起拍价-->
      <el-table-column
        prop="orderStartPrice"
        :label="$t('bidMod.startingPrice')"
        width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.orderStartPrice"
            v-input-format="{ type: 'float', negative: false, zero: false }"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.orderStartPrice }}</span>
        </template>
      </el-table-column>

      <!--流标价-->
      <!-- <el-table-column
        prop="orderNoBidPrice"
        label="流标价"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-if="scope.row.editable"
            v-model="scope.row.orderNoBidPrice"
            v-input-format="{ type: 'float', negative: false, zero: false }"
            :disabled="(!!scope.row.orderNoBidPrice) || readonly"
          />
          <span v-else>{{ scope.row.orderNoBidPrice }}</span>
        </template>
      </el-table-column> -->

      <!--采购申请号-->
      <el-table-column
        prop="sourceFromNo"
        :label="$t('bid_mod.purchaseRequest')"
        width="150"
      />

      <!--采购申请行号-->
      <el-table-column
        prop="sourceFromLineNo"
        :label="$t('bid_mod.purchaseRequestRowNum')"
        width="150"
        show-overflow-tooltip
      />

      <!--价格有效期从-->
      <el-table-column
        prop="priceStartTime"
        label="价格有效期从"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="scope.row.editable"
            v-model="scope.row.priceStartTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.optionDate')"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.priceStartTime }}</span>
        </template>
      </el-table-column>

      <!--价格有效期至-->
      <el-table-column
        prop="priceEndTime"
        label="价格有效期至"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="scope.row.editable"
            v-model="scope.row.priceEndTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.optionDate')"
            :disabled="readonly"
          />
          <span v-else>{{ scope.row.priceEndTime }}</span>
        </template>
      </el-table-column>

      <!--价格类型-->
      <el-table-column
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
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

      <!--贸易条款(月)-->
      <el-table-column
        prop="tradeTerm"
        label="贸易条款(月)"
        width="150"
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

      <!--保质期-->
      <el-table-column
        prop="warrantyPeriod"
        label="保质期"
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

      <!--备注-->
      <el-table-column
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
import { carBuyerHttp } from 'modb@/competition/api'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import CCategorySelect from 'lib@/components/c-category-select'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ProjectRequirements',

  components: {
    QuickSearch,
    OrganizationSelector,
    RenderAsyncText,
    PaymentTypeDialog,
    CCategorySelect
  },

  props: {
    baseInfo: {
      type: Object,
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
      batchMaintainPaymentTypeDialogVisible: false,
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

  computed:{
    isDemandPool(){
      return !!(this.baseInfo.sourceFromType === 'PURCHASE_REQ')
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.listRequireInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    /* 查询需求详情 */
    async listRequireInfo () {
      if (!this.baseInfo.projectId) {
        return
      }

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: this.baseInfo.projectId }], 'listRequireInfo')
      const response = await carBuyerHttp.init.listRequireInfo(transformParams)
      if (response && response.data && Array.isArray(response.data.records)) {
        this.requirementLineList = response.data.records.map(item => {
          return {
            ...item,
            ...item.auctSouItem,
            projectId: this.baseInfo.projectId,
            editable: false
            // 默认基本信息中的价格有效期
            // priceStartTime: this.baseInfo.priceStartTime,
            // priceEndTime: this.baseInfo.priceEndTime
          }
        })

        // 付款条款全部统一
        if (this.requirementLineList.length > 0) {
          this.purchasePaymentTermList = this.requirementLineList[0].paymentList || []
        }
      }
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
        orderStartPrice: '',
        exchangeRateType: '',
        taxKey: null,
        requireQuantity: '',
        buyAmount: '',
        priceType: 'STANDARD',
        purchaseType: '',
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

    /* 是否无料号寻源勾选 */
    noCodeItemChange ({ row }) {
      // 无料号，清空原有选的料号相关数据
      row.itemId = ''
      row.itemCode = ''
      row.itemDesc = ''
      row.unit = ''
      row.categoryId = ''
      row.categoryCode = ''
      row.categoryName = ''
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
      row.exchangeRateType = dictItem.key
    },

    /* 保存 */
    async saveRequirement (type) {
      // 校验表格必填项
      if (!validateRequiredColumn(
        this.$refs.requirementTable,
        this.requirementLineList,
        {
          validateScope: false,
          tableTitle: '需求明细',
          excludeProperty: ['itemCode']
        }
      )) {
        return { status: false }
      }

      for (let i = 0; i < this.requirementLineList.length; i++) {
        let item = this.requirementLineList[i]
        if (item.noCodeItem !== 'Y' && !item.itemCode) {
          this.$message.warning(`第${i + 1}行的物料编码不能为空`)
          return { status: false }
        }
      }

      // 请批量维护付款条款
      if (this.purchasePaymentTermList.length === 0) {
        this.$message.warning('请批量维护付款条款')
        return { status: false }
      }

      try {
        let params = {
          projectId: this.baseInfo.projectId,
          // 物料需求信息
          itemList: this.requirementLineList.map(item => {
            return {
              ...item,
              auctSouItem: {
                orderStartPrice: item.orderStartPrice,
                orderNoBidPrice: item.orderNoBidPrice,
                priceType: item.priceType,
                tradeTerm: item.tradeTerm,
                warrantyPeriod: item.warrantyPeriod
              },
              // 冗余付款条款
              paymentList: this.purchasePaymentTermList.concat()
            }
          }),
          // 是否暂存
          tempSave: type !== 'nextOne'
        }
        let transformParams = transformMQL.save('AuctSouProjectForBuyer', [params], 'editRequireInfo')
        const response = await carBuyerHttp.init.editRequireInfo(transformParams)

        if (response) {
          this.$message.success(this.$t('common.successSave'))
          return { status: true, data: response.data.records[0] }
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
    padding-bottom: 10px;
  }
}
</style>
