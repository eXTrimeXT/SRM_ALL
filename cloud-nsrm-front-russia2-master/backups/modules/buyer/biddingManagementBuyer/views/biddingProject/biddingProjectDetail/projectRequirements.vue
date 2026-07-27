<template>
  <!-- 项目需求 -->
  <el-form :model="projectRequirementsForm">
    <srm-row style="padding-top: 10px">
      <srm-col :init-col="1">
        <!-- 需求简述 -->
        <el-form-item
          :label="$t('bidMod.bondDesc2')"
          label-width="80px"
        >
          <el-input
            v-model="bidingBaseInfo.requireDesc"
            type="textarea"
            :rows="2"
            :disabled="readonly"
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!-- 报价类型 -->
        <el-form-item
          :label="$t('bidMod.pricingType')"
          label-width="80px"
        >
          <DictSelect
            v-model="bidingBaseInfo.pricingType"
            code="REQUIREMENT_PRICING_TYPE"
            :disabled="readonly || isNotManual"
            @change="setPricingType"
          />
        </el-form-item>
      </srm-col>
    </srm-row>

    <div style="padding: 10px; overflow: hidden">
      <!-- 需求明细 -->
      <span style="padding: 0 11px; float: left; line-height: 24px">{{ $t("bidMod.demandDetail") }}</span>
      <!--添加-->
      <el-button
        style="float: left; margin: 0 5px"
        type="primary"
        class="detail-pbtn"
        :disabled="readonly"
        @click="addBidRequirementLineList"
      >
        {{ $t("common.new") }}
      </el-button>
    </div>

    <!-- 表格 -->
    <vxe-table
      ref="xTable"
      border
      show-overflow="tooltip"
      keep-source
      size="mini"
      align="center"
      max-height="500"
      :data="bidRequirementLineList"
      :valid-config="{ showMessage: false }"
      :edit-rules="vxeTableValidRules"
      :edit-config="{
        trigger: 'click',
        mode: 'row',
        autoClear: false,
        enabled: !readonly
      }"
      @edit-actived="vxeTableEditActived"
    >
      <!--序号-->
      <vxe-column
        type="seq"
        width="60"
      />

      <!--业务实体-->
      <vxe-column
        field="orgOuId"
        :title="$t('bid_mod.businessEntity')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <OrganizationSelector
            v-model="scope.row.orgOuId"
            :scope="scope"
            node-type="OU"
            :parent-id="-1"
            :clearable="false"
            :placeholder="$t('common.pleaseSelect')"
            :disabled="(!!scope.row.orgOuId && !!scope.row.purchaseRequestNum)"
            @select="orgOuIdChange"
          />
        </template>
        <template #default="{ row }">
          {{ row.orgOuName }}
        </template>
      </vxe-column>

      <!--库存组织-->
      <vxe-column
        field="orgInvId"
        :title="$t('bid_mod.inv')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <OrganizationSelector
            :ref="`inv_${$rowIndex}`"
            v-model="row.orgInvId"
            :scope="row"
            node-type="INV"
            :clearable="false"
            :placeholder="$t('common.pleaseSelect')"
            :parent-id="row.orgOuId"
            :disabled="(!!row.orgInvId && !!row.purchaseRequestNum)"
            @select="orgInvIdChange"
          />
        </template>
        <template #default="{ row }">
          {{ row.orgInvName }}
        </template>
      </vxe-column>

      <!--组合-->
      <vxe-column
        v-if="bidingBaseInfo.bidingAwardWay === 'COMBINED_DECISION'"
        field="itemGroup"
        :title="$t('bidMod.itemGroup')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.itemGroup" />
        </template>
        <template #default="{ row }">
          {{ row.itemGroup }}
        </template>
      </vxe-column>

      <!--是否无料号寻源-->
      <vxe-column
        field="isNoCodeItem"
        :title="$t('bidMod.biddingManagementBuyer.isNoCodeItem')"
        width="130"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <el-checkbox
            v-model="row.isNoCodeItem"
            :disabled="!!row.purchaseRequestNum"
            true-label="Y"
            false-label="N"
            @change="isNoCodeItemChange(row)"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.isNoCodeItem) }}
        </template>
      </vxe-column>

      <!--物料编码-->
      <vxe-column
        field="targetNum"
        :title="$t('bidMod.targetNum')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <QuickSearch
            v-if="row.isNoCodeItem !== 'Y'"
            :disabled="!!row.purchaseRequestNum"
            :show-input="row.targetNum"
            show-key="materialCode"
            :scope-data="row"
            :disabled-select="!row.orgOuId || !row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': row.orgInvId }"
            @close-quicksearch="targetNumChange"
            @before-open="(value, callback) => targetNumQuickSearchBeforeOpen(row, callback)"
          />
        </template>
        <template #default="{ row }">
          {{ row.isNoCodeItem !== 'Y' ? row.targetNum : '' }}
        </template>
      </vxe-column>

      <!--物料名称-->
      <vxe-column
        field="targetDesc"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow="tooltip"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-if="row.isNoCodeItem === 'Y'"
            v-model="row.targetDesc"
            maxlength="100"
          />
          <template v-else>
            {{ row.targetDesc }}
          </template>
        </template>
        <template #default="{ row }">
          {{ row.targetDesc }}
        </template>
      </vxe-column>

      <!--单位-->
      <vxe-column
        field="unit"
        :title="$t('bidMod.unit')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <dict-select
            v-if="row.isNoCodeItem === 'Y'"
            v-model="row.unit"
            code="unit"
          />
          <span v-else>{{ $getDictLabel('unit', row.unit) }}</span>
        </template>
        <template #default="{ row }">
          <span>{{ $getDictLabel('unit', row.unit) }}</span>
        </template>
      </vxe-column>

      <!--采购分类-->
      <vxe-column
        field="categoryName"
        :title="$t('bidMod.purcategoryName')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <QuickSearch
            v-if="row.isNoCodeItem === 'Y'"
            :show-input="row.categoryName"
            show-key="categoryName"
            :scope-data="row"
            :table-index="$rowIndex"
            clearable
            name="scc_base_purchase_category2"
            @close-quicksearch="categoryNameChange"
          />
          <span v-else>{{ row.categoryName }}</span>
        </template>
        <template #default="{ row }">
          {{ row.categoryName }}
        </template>
      </vxe-column>

      <!--预计采购数量-->
      <vxe-column
        field="quantity"
        :title="$t('bidMod.quantity')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-model="row.quantity"
            v-input-format="{ type: 'number' }"
            :disabled="!!row.quantity && !!row.purchaseRequestNum"
          />
        </template>
        <template #default="{ row }">
          {{ row.quantity }}
        </template>
      </vxe-column>

      <!--预计采购金额-->
      <vxe-column
        field="amount"
        :title="$t('bidMod.amount')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.amount" v-input-format="{ type: 'float' }" />
        </template>
        <template #default="{ row }">
          {{ row.amount }}
        </template>
      </vxe-column>

      <!--t 选择公式-->
      <vxe-column
        field="materialFormulaRelateId"
        :title="$t('bidMod.biddingManagementBuyer.formulaId')"
        width="125"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-select
            v-model="row.materialFormulaRelateId"
            clearable
            @change="formulaChange(row)"
          >
            <el-option
              v-for="(item, index) in (row.formulaValueList || [])"
              :key="item.relateId + index"
              :label="item.formulaName"
              :value="item.relateId"
            />
          </el-select>
        </template>
        <template #default="{ row }">
          {{ row.formulaName }}
        </template>
      </vxe-column>

      <!--t 公式值-->
      <vxe-column
        field="formulaValue"
        :title="$t('bid_mod.formulaValue')"
        width="150"
        show-overflow="tooltip"
      />

      <!--t 是否阶梯报价-->
      <vxe-column
        field="isLadder"
        :title="$t('bidMod.isLadder')"
        width="115"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-checkbox
            v-model="scope.row.isLadder"
            true-label="Y"
            false-label="N"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.isLadder) }}
        </template>
      </vxe-column>

      <!--t 阶梯价报价-->
      <vxe-column
        field="ladderPrices"
        :title="$t('bidMod.ladderQuote')"
        width="110"
      >
        <template #default="scope">
          <el-button
            type="text"
            :disabled="scope.row.isLadder !== 'Y'"
            @click="openLadderPrice(scope)"
          >
            {{ $t('bidMod.ladderPrice') }}
          </el-button>
        </template>
      </vxe-column>

      <!--模板报价-->
      <vxe-column
        field="isTemplate"
        :title="$t('templatePrice.label')"
        width="110"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <QuickSearch
            ref="tempQuickSearch"
            :btn-title="row.quoteTempName || $t('templatePrice.select')"
            name="sou_quote_temp"
            :scope-data="row"
            btn-type="text"
            show-button
            :confirm-auto-close="false"
            :pre-query-data="{ 't.temp_status': 'VALID' }"
            @close-quicksearch="selectQuoteTemplate"
          />
        </template>
        <template #default="{ row }">
          {{ row.quoteTempName }}
        </template>
      </vxe-column>

      <!--定价开始时间-->
      <vxe-column
        field="priceStartTime"
        :title="$t('bidMod.priceStartTime')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-date-picker
            v-model="row.priceStartTime"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ row.priceStartTime | parseTimeFilters }}
        </template>
      </vxe-column>

      <!--定价结束时间-->
      <vxe-column
        field="priceEndTime"
        :title="$t('bidMod.priceEndTime')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-date-picker
            v-model="row.priceEndTime"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ row.priceEndTime | parseTimeFilters }}
        </template>
      </vxe-column>

      <!--需求日期-->
      <vxe-column
        field="demandDate"
        :title="$t('bidMod.ceeaDemandDate')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-date-picker
            v-model="row.demandDate"
            :disabled="!!row.demandDate && !!row.purchaseRequestNum"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ row.demandDate | parseTimeFilters }}
        </template>
      </vxe-column>

      <!--交货地点(仅外协)-->
      <vxe-column
        field="deliveryPlace"
        :title="$t('bid_mod.deliveryPlace')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <ProviceCity
            v-model="row.deliveryPlace"
          />
        </template>
        <template #default="{ row }">
          <RenderAsyncText :cell-value="row.deliveryPlace" />
        </template>
      </vxe-column>

      <!--价格类型-->
      <vxe-column
        field="priceType"
        :title="$t('bid_mod.priceType')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <dict-select
            v-model="row.priceType"
            code="PRICE_TYPE"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('PRICE_TYPE', row.priceType) }}
        </template>
      </vxe-column>

      <!--采购类型-->
      <vxe-column
        field="purchaseType"
        :title="$t('bid_mod.purchaseType')"
        width="150"
        :formatter="({ cellValue }) => $getDictLabel('PURCHASE_TYPE', cellValue)"
      />

      <!--贸易条款-->
      <vxe-column
        field="tradeTerm"
        :title="$t('bid_mod.tradeTerm')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <dict-select
            v-model="row.tradeTerm"
            code="trade_clause"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('trade_clause', row.tradeTerm) }}
        </template>
      </vxe-column>

      <!--运输方式-->
      <vxe-column
        field="transportType"
        :title="$t('bid_mod.transportType')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <dict-select
            v-model="row.transportType"
            code="TRANSF_TYPE"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('TRANSF_TYPE', row.transportType) }}
        </template>
      </vxe-column>

      <!--是否显示需求量-->
      <vxe-column
        field="showRequireNum"
        :title="$t('bid_mod.showRequireNum')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <dict-select
            v-model="row.showRequireNum"
            code="YES_OR_NO"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.showRequireNum) }}
        </template>
      </vxe-column>

      <!--质保期(月)-->
      <vxe-column
        field="warrantyPeriod"
        :title="$t('bid_mod.warrantyPeriod')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.warrantyPeriod" />
        </template>
        <template #default="{ row }">
          {{ row.warrantyPeriod }}
        </template>
      </vxe-column>

      <!--采购申请号-->
      <vxe-column
        field="purchaseRequestNum"
        :title="$t('bid_mod.purchaseRequest')"
        width="150"
      />

      <!--采购申请行号-->
      <vxe-column
        field="purchaseRequestRowNum"
        :title="$t('bid_mod.purchaseRequestRowNum')"
        width="150"
      />

      <!--指定供应商名称-->
      <vxe-column
        field="awardedSupplierName"
        :title="$t('bid_mod.awardedSupplierName')"
        width="150"
      />

      <!--备注-->
      <vxe-column
        field="comments"
        :title="$t('bidMod.remark')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.comments" />
        </template>
        <template #default="{ row }">
          {{ row.comments }}
        </template>
      </vxe-column>

      <!--操作-->
      <vxe-column
        width="140"
        :title="$t('bidMod.operation')"
        fixed="right"
        :visible="!readonly || pricingType.isModelPricing"
      >
        <template #default="{ row, $rowIndex }">
          <!--删除-->
          <el-button
            v-if="!row.purchaseRequestNum && !readonly"
            type="text"
            @click="handleDeleteRow(row)"
          >
            {{ $t('common.delete') }}
          </el-button>

          <!-- 模型报价 -->
          <el-button
            v-if="pricingType.isModelPricing"
            type="text"
            :disabled="false"
            @click="openModelQuoteDialog($rowIndex, row)"
          >
            {{ $t('bid_mod.modelQuoteTitle') }}
          </el-button>
        </template>
      </vxe-column>
    </vxe-table>

    <!-- 模型报价 -->
    <ModelQuoteDialog
      v-if="modelQuoteDialogVisible"
      :visible.sync="modelQuoteDialogVisible"
      :source-line="editRow"
      :is-read-only-by-buyer="readonly"
      @saveModelQuoteData="saveModelQuoteData"
    />

    <!--阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDetailVisible"
      :visible.sync="ladderPriceDetailVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :edit-row="editRow"
      :readonly="readonly"
      @save-set="saveLadderPrices"
    />
  </el-form>
</template>

<script>
import { parseTime } from '@/utils'
import { getFormulaValuePreconditions, souProjectSourceFromManual } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { PRICING_TYPE_MAGIC } from '@/library/composition/biddingManagement/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ModelQuoteDialog from 'lib@/composition/biddingManagement/modelQuote/modelQuoteDialog'
import ProviceCity from '@/library/components/provice-city'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'
import { getRequireInfoByBidingId, formula, priceTemplateApi } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'ProjectRequirements',

  components: {
    ProviceCity,
    OrganizationSelector,
    RenderAsyncText,
    ModelQuoteDialog,
    QuickSearch,
    LadderPriceDetail
  },

  filters: {
    // 时间格式化
    parseTimeFilters (val) {
      return val ? parseTime(val, '{y}-{m}-{d}') : ''
    }
  },

  props: {
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    bidingBase: {
      type: Object,
      default: () => { /* noting */ }
    },
    isActiveMenu: {
      type: Boolean,
      default: false
    },
    pricingType: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      projectRequirementsForm: {},
      bidRequirementLineList: [],
      editRow: null,
      editIndex: '',
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
      modelQuoteDialogVisible: false,
      // 校验配置
      vxeTableValidRules: {
        orgOuId: [{ required: true }],
        orgInvId: [{ required: true }],
        targetNum: [{ required: true }],
        tradeTerm: [{ required: true }],
        transportType: [{ required: true }],
        showRequireNum: [{ required: true }],
        warrantyPeriod: [{ required: true }],
        priceStartTime: [{ required: true }],
        priceEndTime: [{ required: true }],
        demandDate: [{ required: true }],
        quantity: [{ required: true }],
        amount: [{ required: true }]
      },
      ladderPriceDetailVisible: false,
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    bidingBaseInfo: {
      get: function () {
        return this.bidingBase
      },
      set: function (val) {
        this.$emit('update:bidingBase', val)
      }
    },

    // 非手工创建的单据
    isNotManual () {
      return !souProjectSourceFromManual(this.bidingBaseInfo.sourceFrom)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getProjectRequirementsData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取项目需求数据 */
    async getProjectRequirementsData (bidingId) {
      const id = bidingId || this.scopeBidingId
      const response = await getRequireInfoByBidingId(id)
      if (response && response.data) {
        this.bidRequirementLineList = response.data.map(
          ({ deliveryPlace, ...rest }) => {
            let d = null
            if (deliveryPlace) {
              try {
                d = JSON.parse(deliveryPlace)
              } catch (e) {
                console.log(e)
              }
            }
            return {
              ...rest,
              bidingId: id,
              editable: false,
              materialEditable: false,
              deliveryPlace: d
            }
          }
        )
        this.$nextTick(() => {
          // load一遍
          this.setPricingType(this.bidingBaseInfo.pricingType, false)
        })
      }
    },

    /* 选择报价类型 */
    setPricingType (val, clear = true) {
      // 公式相关列
      const toggleFormula = type => {
        this.$refs.xTable[type]('materialFormulaRelateId')
        this.$refs.xTable[type]('formulaValue')
      }

      // 阶梯价相关列
      const toggleLadder = type => {
        this.$refs.xTable[type]('isLadder')
        this.$refs.xTable[type]('ladderPrices')
      }

      // 报价模板相关列
      const toggleTemplate = type => {
        this.$refs.xTable[type]('isTemplate')
      }

      let reloadItem

      // 普通报价
      if (val === PRICING_TYPE_MAGIC.SIMPLE_PRICING) {
        // 清空公式值 模型报价
        reloadItem = {
          formulaId: '',
          materialFormulaRelateId: '',
          formulaValue: '',
          formulaValueList: [],
          modelPriceTemplateList: [],
          // 模板数据
          isTemplate: 'N',
          quoteTempId: '',
          quoteTempName: ''
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 显示阶梯价列
        toggleLadder('showColumn')
        // 隐藏报价模板列
        toggleTemplate('hideColumn')
      }

      // 公式报价
      if (val === PRICING_TYPE_MAGIC.FORMULA_PRICING) {
        reloadItem = {
          // 清空阶梯价
          isLadder: 'N',
          ladderPrices: [],
          // 清空模型报价
          modelPriceTemplateList: [],
          // 模板数据
          isTemplate: 'N',
          quoteTempId: '',
          quoteTempName: ''
        }

        // 显示公式列
        toggleFormula('showColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 隐藏报价模板列
        toggleTemplate('hideColumn')
      }

      // 模型报价
      if (val === PRICING_TYPE_MAGIC.MODEL_PRICING) {
        // 清空公式值 阶梯价
        reloadItem = {
          formulaId: '',
          materialFormulaRelateId: '',
          formulaValue: '',
          formulaValueList: [],
          isLadder: 'N',
          ladderPrices: [],
          // 模板数据
          isTemplate: 'N',
          quoteTempId: '',
          quoteTempName: ''
        }
        // 隐藏公式列
        toggleFormula('hideColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 隐藏报价模板列
        toggleTemplate('hideColumn')
      }

      // 报价模板
      if (val === PRICING_TYPE_MAGIC.TEMPLATE_PRICING) {
        // 清空公式值 模型报价 阶梯价
        reloadItem = {
          formulaId: '',
          materialFormulaRelateId: '',
          formulaValue: '',
          formulaValueList: [],
          // 清空模型报价
          modelPriceTemplateList: [],
          isLadder: 'N',
          ladderPrices: [],
          isTemplate: 'Y'
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 显示报价模板列
        toggleTemplate('showColumn')
      }

      // 重置数据
      if (clear) {
        const { fullData } = this.$refs.xTable.getTableData()
        this.$refs.xTable.reloadData(fullData.map(item => {
          return {
            ...item,
            ...reloadItem
          }
        }))
      }
    },

    /* 是否无料号寻源勾选 */
    isNoCodeItemChange (row) {
      if (row.isNoCodeItem === 'Y' && row.targetNum) {
        // 无料号，清空原有选的料号相关数据
        row.targetNum = ''
        row.targetDesc = ''
        row.categoryName = ''
        row.materialFormulaRelateId = ''
        row.formulaId = ''
        row.formulaName = ''
        row.formulaValueList = []
      }
    },

    /* 选择公式值 */
    formulaChange (row) {
      if (!row.materialFormulaRelateId) {
        // 清空选择
        row.formulaId = ''
        row.formulaValue = ''
        row.formulaName = ''
        row.materialFormulaRelateId = ''
      } else {
        // 查找对应的公式值
        const formulaValue = row.formulaValueList.find(item => item.relateId === row.materialFormulaRelateId)
        if (formulaValue) {
          row.formulaValue = formulaValue.formulaValue
          row.formulaName = formulaValue.formulaName
          row.formulaId = formulaValue.formulaId
          row.materialFormulaRelateId = formulaValue.relateId
        }
      }
    },

    /* 判断某一行数据是否满足查询公式列表的条件，符合条件返回行参数 */
    getFormulaValuePreconditions (row) {
      // 必须是公式报价
      if (!this.pricingType.isFormulPricing || !row) {
        return false
      }

      const {
        orgOuId,
        isNoCodeItem,
        categoryId,
        targetId
      } = row

      // 业务实体必选
      if (!orgOuId) {
        return false
      }
      const param = { orgOuId }

      // 无料号寻源，需要选品类
      if (isNoCodeItem === 'Y' && categoryId) {
        return {
          ...param,
          categoryId,
          mapKey: 'categoryRelateMap',
          valueKey: categoryId.toString()
        }
      }

      // 非无料号寻源，需要选物料编码
      if ((isNoCodeItem === 'N' || !isNoCodeItem) && targetId) {
        return {
          ...param,
          materialId: targetId,
          mapKey: 'materialRelateMap',
          valueKey: targetId.toString()
        }
      }
      return false
    },

    /**
     * 处理单行数据的公式
     * @param row 行数据
     * @param isClear 是否清空公式相关参数
     * @param isOneSet 公式列表只有一个时候是否自动选中
     * @returns {Promise<void>}
     */
    async rowHandelFormulaValue (row, isClear = true, isOneSet = true) {
      // 是否清空公式值
      if (isClear) {
        row.materialFormulaRelateId = ''
        row.formulaId = ''
        row.formulaValue = ''
        row.formulaName = ''
        row.formulaValueList = []
      }
      // 获取公式报价参数
      const params = getFormulaValuePreconditions({
        orgOuId: row.orgOuId,
        noCodeItem: row.isNoCodeItem,
        categoryId: row.categoryId,
        itemId: row.targetId
      }, this.pricingType.isFormulPricing)

      if (params) {
        // 查询公式
        const { data: formulaValueData } = await formula.getMaterialFormulaRelateInfos([params])

        if (formulaValueData && formulaValueData[params.mapKey][params.valueKey]) {
          row.formulaValueList = formulaValueData[params.mapKey][params.valueKey]
          // 记录查询过公式了
          row.isSetFormulaValueList = true
          // 局部刷新行数据
          await this.$refs.xTable.reloadRow(row, row, 'formulaValueList')

          if (isOneSet && row.formulaValueList.length === 1) {
            // 只有一个公式，自动选中
            row.materialFormulaRelateId = row.formulaValueList[0].relateId
            this.formulaChange(row)
          }
        } else {
          // 没有公式，提示需要维护公式
          this.$message.warning(this.$t('bidMod.biddingManagementBuyer.warningNoFormulaId'))
        }
      }
    },

    /* 表格单元格触发编辑 */
    vxeTableEditActived ({ row }) {
      // 判断是否查询公式
      if (!row.isSetFormulaValueList && this.pricingType.isFormulPricing) {
        // 单行公式查询处理
        this.rowHandelFormulaValue(row, false, false)
      }
    },

    /* 删除物料行 */
    handleDeleteRow (row) {
      this.$refs.xTable.remove(row)
    },

    /* 选择业务实体 */
    orgOuIdChange (node, _id, { row, $rowIndex }) {
      if (node && row.orgOuId === node.organizationId) {
        return
      }

      row.orgOuId = node ? node.organizationId : ''
      row.orgOuCode = node ? node.organizationCode : ''
      row.orgOuName = node ? node.organizationName : ''
      this.$refs[`inv_${$rowIndex}`].clearOptions()

      row.orgInvId = ''
      row.orgInvCode = ''
      row.orgInvName = ''

      // 重选业务实体，清空物料选择
      if (row.isNoCodeItem !== 'Y' && row.targetId) {
        // 非无料号
        this.targetNumChange(null, row)
      } else {
        // 处理公式报价
        this.rowHandelFormulaValue(row)
      }
    },

    /* 库存组织 */
    orgInvIdChange (node, _id, row) {
      if (node && row.orgInvId === node.organizationId) {
        return
      }

      row.orgInvId = node ? node.organizationId : ''
      row.orgInvCode = node ? node.organizationCode : ''
      row.orgInvName = node ? node.organizationName : ''

      // 重选库存组织，清空物料选择
      if (row.isNoCodeItem !== 'Y' && row.targetId) {
        // 非无料号
        this.targetNumChange(null, row)
      }
    },

    /* 选择一个物料 */
    targetNumChange (val, row) {
      row.targetId = val ? val.materialId : ''
      row.targetNum = val ? val.materialCode : ''
      row.targetDesc = val ? val.materialName : ''
      row.unit = val ? val.unit : ''
      row.categoryId = val ? val.categoryId : ''
      row.categoryCode = val ? val.categoryCode : ''
      row.categoryName = val ? val.categoryName : ''

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    targetNumQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
        callback(null)
      }
    },

    /* 选择分类 */
    categoryNameChange (value, row) {
      row.categoryCode = value.categoryCode || ''
      row.categoryName = value.categoryName || ''
      row.categoryId = value.categoryId || ''

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 模型报价 START */
    /* 打开模型报价弹窗 */
    openModelQuoteDialog (index, row) {
      this.editIndex = index
      this.editRow = {
        modelQuoteLines: row.modelPriceTemplateList || []
      }
      this.modelQuoteDialogVisible = true
    },
    /* 保存模型报价 */
    saveModelQuoteData (data) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].modelPriceTemplateList = data
    },
    /* 模型报价 END */

    /* 阶梯报价 START */
    /* 打开 */
    openLadderPrice ({ $rowIndex, row }) {
      if (!row.quantity) {
        this.$message.warning('请输入预计采购数量!')
        return
      }

      this.editIndex = $rowIndex
      this.editRow = row
      this.ladderPriceDetailVisible = true
    },
    /* 保存 */
    saveLadderPrices (data) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].ladderType = data.ladderType
      fullData[this.editIndex].ladderPrices = data.ladderPrices
    },
    /* 阶梯报价 END */

    /* 选择一个报价模板 */
    async selectQuoteTemplate (val, row) {
      // 校验所选的模板，必须设定了一个总价属性
      const response = await priceTemplateApi.getDetail(val.tempId)

      if (response) {
        const { tempLineList = [] } = response.data
        const isTotalItem = tempLineList.find(item => item.isTotal === 'Y')
        if (isTotalItem) {
          // 所选模板存在总价属性，合法
          const { tempId = '', tempName = '' } = val || {}
          row.quoteTempId = tempId
          row.quoteTempName = tempName
          // 手动关闭快查
          this.$refs.tempQuickSearch.cancelDialog()
        } else {
          // 所选模板不存在总价属性，不合法，提示重新选择
          this.$message.warning(`模板 [${val.tempName}] 没有指定总价属性，请重新选择`)
        }
      }
    },

    /* 新增一行物料行 */
    addBidRequirementLineList () {
      this.$refs.xTable.insertAt({
        bidingId: this.scopeBidingId,
        editable: true,
        materialEditable: false,
        orgOuId: '',
        orgInvId: '',
        orgInvName: '',
        orgInvCode: '',
        categoryId: '',
        categoryName: '',
        itemGroup: '',
        targetNum: '',
        targetDesc: '',
        taxCurrentPrice: '',
        deliveryPlace: '',
        taxRate: '',
        quantity: '',
        amount: '',
        priceType: 'STANDARD',
        purchaseType: '',
        unit: '',
        comments: '',
        isLadder: 'N',
        // 带入项目信息中的价格有效期
        priceStartTime: this.bidingBaseInfo.priceStartTime || '',
        priceEndTime: this.bidingBaseInfo.priceEndTime || '',
        demandDate: null,
        formulaValue: null,
        // 模板报价
        quoteTempId: '',
        quoteTempName: ''
      }, -1)
    },

    /* 保存项目需求信息 */
    saveRequirement (type) {
      const { fullData } = this.$refs.xTable.getTableData()

      if (fullData.length === 0) {
        // 请先录入需求明细
        this.$message.warning(this.$t('bidMod.bidMsgList[27]'))
        return
      }

      const warningMessage = (index, message) => this.$message.warning(`第${index + 1}行 ${message}`)

      for (let [index, item] of fullData.entries()) {
        if (item.orgOuId && !item.orgInvId) {
          // 维护了业务实体，库存组织必须维护！
          warningMessage(index, this.$t('bidMod.bidMsgList[38]'))
          return
        }
        if (!item.targetDesc) {
          // 请先录入物料名称!
          warningMessage(index, this.$t('bidMod.bidMsgList[28]'))
          return
        }
        if (!item.unit) {
          // 请先录入单位!
          warningMessage(index, this.$t('bidMod.bidMsgList[29]'))
          return
        }
        if (!item.categoryName) {
          // 请录入采购分类!
          warningMessage(index, this.$t('bidMod.bidMsgList[30]'))
          return
        }
        // 是否进价格库
        if (this.bidingBaseInfo.isSyncToPriceLibrary === 'Y' && (!item.priceStartTime || !item.priceEndTime)) {
          // 请录入定价开始时间和结束时间!
          warningMessage(index, this.$t('bidMod.bidMsgList[31]'))
          return
        }
        // 组合决标
        if (this.bidingBaseInfo.bidingAwardWay === 'COMBINED_DECISION') {
          if (!item.itemGroup) {
            // 组合决标的决标方式，请输入组合！
            warningMessage(index, this.$t('bidMod.bidMsgList[32]'))
            return
          }
        }
        // 模版报价，选择模版
        if (this.pricingType.isTemplatePricing && !item.quoteTempId) {
          warningMessage(index, this.$t('请选择一个报价模版'))
          return
        }
      }

      this.$http({
        url: '/api-bid/bidInitiating/biding/tempSaveOrSubmitRequireInfo',
        method: 'POST',
        data: {
          bidingId: this.scopeBidingId,
          requireDesc: this.bidingBaseInfo.requireDesc,
          pricingType: this.bidingBaseInfo.pricingType,
          requirementLineList: fullData,
          // 是否是暂存
          isTempSave: type !== 'nextOne'
        },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))

        // 查询
        this.getProjectRequirementsData()

        // 下一步保存触发
        if (type === 'nextOne') {
          // 保存后下一步操作
          this.$emit('fetchBaseInfo')
          this.$emit('saveNextTodo')
        } else {
          // 暂存触发 更新节点
          this.$emit('updateProcessNode')
        }
      })
    }
  }
}
</script>
