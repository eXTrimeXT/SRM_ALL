<template>
  <!-- 项目需求 -->
  <el-form :model="projectRequirementsForm">
    <SrmRow style="padding-top: 10px">
      <SrmCol :init-col="1">
        <!-- 需求简述 -->
        <el-form-item :label="$t('bidMod.bondDesc2')" label-width="80px">
          <el-input
            v-model="biddingBaseInfo.requireDesc"
            type="textarea"
            :rows="2"
            :disabled="readonly"
          />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!-- 投标类型 -->
        <el-form-item :label="$t('bidMod.typeOfTender')" label-width="80px">
          <DictSelect
            v-model="biddingBaseInfo.orderType"
            code="SOU_ORDER_TYPE"
            :disabled="readonly"
            :transform-options="orderTypeTransformOptions"
            @change="orderTypeChange"
          />
        </el-form-item>
      </SrmCol>

      <!-- 报价类型为料费分离的时候启用报价模板 -->
      <SrmCol v-if="pricingType.isSeparation" :init-col="3">
        <el-form-item
          label="报价模板"
          prop="quoteTempName"
          label-width="80px"
        >
          <QuickSearch
            :show-input="biddingBaseInfo.quoteTempName"
            show-key="tempName"
            :scope-data="biddingBaseInfo"
            :disabled="readonly"
            clearable
            name="scc_sou_quote_temp"
            @close-quicksearch="quoteTempNameChange"
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <div style="padding: 10px; overflow: hidden">
      <!-- 需求明细 -->
      <span style="padding: 0 11px; float: left; line-height: 24px">{{ $t("bidMod.demandDetail") }}</span>
      <!--添加-->
      <el-button
        style="float: left; margin: 0 5px"
        type="primary"
        :disabled="readonly"
        @click="addRow"
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
      align="left"
      max-height="500"
      :data="itemList"
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
      <vxe-column type="seq" width="60" />

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
            :disabled="!!scope.row.orgOuId && scope.row.sourceFromType && scope.row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
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
            :disabled="!!row.orgInvId && row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
            @select="orgInvIdChange"
          />
        </template>
        <template #default="{ row }">
          {{ row.orgInvName }}
        </template>
      </vxe-column>

      <!--组合-->
      <vxe-column
        v-if="biddingBase.orderWay === SOU_ORDER_WAY_ENUM.COMBINED"
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

      <!--交货地点(仅外协)-->
      <vxe-column
        field="deliveryPlace"
        :title="$t('bid_mod.deliveryPlace')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <ProviceCity v-model="row.deliveryPlace" />
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
          <DictSelect v-model="row.priceType" code="PRICE_TYPE" />
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
      >
        <template #edit="{ row }">
          <DictSelect v-model="row.purchaseType" code="PURCHASE_TYPE" />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('PURCHASE_TYPE', row.purchaseType) }}
        </template>
      </vxe-column>

      <!--贸易条款-->
      <vxe-column
        field="tradeTerm"
        :title="$t('bid_mod.tradeTerm')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <DictSelect v-model="row.tradeTerm" code="trade_clause" />
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
          <DictSelect v-model="row.transportType" code="TRANSF_TYPE" />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('TRANSF_TYPE', row.transportType) }}
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
          <el-input v-model="row.warrantyPeriod" v-input-format="{ type: 'float' }" />
        </template>
        <template #default="{ row }">
          {{ row.warrantyPeriod }}
        </template>
      </vxe-column>

      <!--采购申请号-->
      <vxe-column
        field="sourceFromNo"
        :title="$t('bid_mod.purchaseRequest')"
        width="150"
      />

      <!--采购申请行号-->
      <vxe-column
        field="sourceFromLineNo"
        :title="$t('bid_mod.purchaseRequestRowNum')"
        width="150"
      />

      <!--是否无料号寻源-->
      <vxe-column
        field="noCodeItem"
        :title="$t('bidMod.biddingManagementBuyer.isNoCodeItem')"
        width="130"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <el-checkbox
            v-model="row.noCodeItem"
            :disabled="row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
            true-label="Y"
            false-label="N"
            @change="noCodeItemChange(row)"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.noCodeItem) }}
        </template>
      </vxe-column>

      <!--物料编码-->
      <vxe-column
        field="itemCode"
        :title="$t('bidMod.targetNum')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <QuickSearch
            v-if="row.noCodeItem !== 'Y'"
            :disabled="row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
            :show-input="row.itemCode"
            show-key="materialCode"
            :scope-data="row"
            :disabled-select="!row.orgOuId || !row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': row.orgInvId }"
            @close-quicksearch="itemCodeChange"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(row, callback)"
          />
        </template>
        <template #default="{ row }">
          {{ row.noCodeItem !== 'Y' ? row.itemCode : '' }}
        </template>
      </vxe-column>

      <!--物料名称-->
      <vxe-column
        field="itemDesc"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-if="row.noCodeItem === 'Y'"
            v-model="row.itemDesc"
            maxlength="100"
          />
          <span v-else>{{ row.itemDesc }}</span>
        </template>
        <template #default="{ row }">
          {{ row.itemDesc }}
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
          <DictSelect
            v-if="row.noCodeItem === 'Y'"
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
            v-if="row.noCodeItem === 'Y'"
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
        field="requireQuantity"
        :title="$t('bidMod.quantity')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-model="row.requireQuantity"
            v-input-format="{ type: 'number' }"
            :disabled="!!row.requireQuantity && row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
          />
        </template>
        <template #default="{ row }">
          {{ row.requireQuantity }}
        </template>
      </vxe-column>

      <!--预计采购金额-->
      <vxe-column
        field="buyAmount"
        :title="$t('bidMod.amount')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.buyAmount" v-input-format="{ type: 'float' }" />
        </template>
        <template #default="{ row }">
          {{ row.buyAmount }}
        </template>
      </vxe-column>

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
            :disabled="!pricingType.isSimplePricing"
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
        field="ladderList"
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

      <!--t 选择公式-->
      <vxe-column
        field="materialFormulaRelateId"
        :title="$t('bidMod.biddingManagementBuyer.formulaId')"
        width="125"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-select
            v-if="pricingType.isFormulaPricing"
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
        :formatter="({ cellValue }) => pricingType.isFormulaPricing ? cellValue : ''"
      />

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
            :picker-options="cannotLessCurrentTimeOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ $dayjsParse(row.priceStartTime) }}
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
            :picker-options="cannotLessCurrentTimeOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ $dayjsParse(row.priceEndTime) }}
        </template>
      </vxe-column>

      <!--需求日期-->
      <vxe-column
        field="requireDate"
        :title="$t('bidMod.ceeaDemandDate')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-date-picker
            v-model="row.requireDate"
            :disabled="!!row.requireDate && row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="cannotLessCurrentTimeOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>
        <template #default="{ row }">
          {{ $dayjsParse(row.requireDate) }}
        </template>
      </vxe-column>

      <!--技术文件-->
      <vxe-column
        field="itemFiles"
        :title="$t('bidMod.technicalDocuments.title')"
        width="150"
      >
        <template #default="{ row, $rowIndex }">
          <el-button type="text" @click="openTechnicalDocumentsDialog(row, $rowIndex)">
            {{ readonly ? $t('common.view') : $t('common.select') }}
          </el-button>
        </template>
      </vxe-column>

      <!--备注-->
      <vxe-column
        field="remark"
        :title="$t('bidMod.remark')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input v-model="row.remark" />
        </template>
        <template #default="{ row }">
          {{ row.remark }}
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
            v-if="!readonly && !(row.sourceFromType && row.sourceFromType !== SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE)"
            type="text"
            @click="deleteRow(row)"
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
      :source-line="sourceLine"
      :readonly="readonly"
      @saveModelQuoteData="saveModelQuoteData"
    />

    <!--阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDetailVisible"
      :visible.sync="ladderPriceDetailVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
      :edit-row="editRow"
      :readonly="readonly"
      @save-set="saveLadderPrices"
    />

    <!--技术文件-->
    <TechnicalDocumentsDialog
      v-if="technicalDocumentsDialogVisible"
      :visible.sync="technicalDocumentsDialogVisible"
      :readonly="readonly"
      :params="editRow"
      :detail-data="editRow.detailData"
      @confirm="technicalDocumentsConfirm"
    />
  </el-form>
</template>

<script>
import { bidBuyerHttp } from 'modb@/bidding/api'
import {
  SOU_ORDER_TYPE_ENUM,
  SOU_SOURCE_FROM_TYPE_ENUM,
  SOU_ORDER_WAY_ENUM,
  BUSINESS_TYPE_ENUM
} from 'lib@/composition/origin/enum'
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import { getFormulaValuePreconditions } from 'lib@/composition/origin/composition'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ModelQuoteDialog from 'lib@/composition/biddingLts/modelQuote/modelQuoteDialog'
import ProviceCity from 'lib@/components/provice-city'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'

export default {
  name: 'RequireInfo',

  components: {
    ProviceCity,
    OrganizationSelector,
    RenderAsyncText,
    ModelQuoteDialog,
    QuickSearch,
    TechnicalDocumentsDialog,
    LadderPriceDetail
  },

  mixins: [cannotLessCurrentTime],

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
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
      itemList: [],
      sourceLine: null,
      sourceLineIndex: '',
      modelQuoteDialogVisible: false,
      // 校验配置
      vxeTableValidRules: {
        orgOuId: [{ required: true }],
        orgInvId: [{ required: true }],
        itemGroup: [{ required: true }],
        // itemCode: [{ required: true }],
        itemDesc: [{ required: true }],
        categoryName: [{ required: true }],
        unit: [{ required: true }],
        tradeTerm: [{ required: true }],
        transportType: [{ required: true }],
        warrantyPeriod: [{ required: true }],
        priceStartTime: [{ required: true }],
        priceEndTime: [{ required: true }],
        requireDate: [{ required: true }],
        requireQuantity: [{ required: true }],
        buyAmount: [{ required: true }]
      },
      technicalDocumentsDialogVisible: false,
      editRow: null,
      editIndex: '',
      SOU_ORDER_WAY_ENUM,
      SOU_SOURCE_FROM_TYPE_ENUM,
      ladderPriceDetailVisible: false,
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
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
    async getProjectRequirementsData () {
      const response = await bidBuyerHttp.init.getRequireInfo(this.biddingBaseInfo.projectId)

      if (response && response.data) {
        this.itemList = response.data.map(item => {
          return {
            ...item,
            editable: false
          }
        })
        this.$nextTick(() => {
          this.orderTypeChange(this.biddingBaseInfo.orderType, false)
        })
      }
    },

    /* 报价类型过滤 */
    orderTypeTransformOptions (options) {
      // 过滤模板报价
      return options.filter(item => ![SOU_ORDER_TYPE_ENUM.TEMPLATE].includes(item.value))
    },

    /* 选择报价模板 */
    quoteTempNameChange (value) {
      this.biddingBaseInfo.quoteTempId = value ? value.tempId : null
      this.biddingBaseInfo.quoteTempName = value ? value.tempName : null
    },

    /* 选择报价类型 */
    orderTypeChange (val, clear = true) {
      this.$refs.xTable.resetColumn()
      // 公式相关列
      const toggleFormula = type => {
        this.$refs.xTable[type]('materialFormulaRelateId')
        this.$refs.xTable[type]('formulaValue')
      }
      // 阶梯价相关列
      const toggleLadder = type => {
        this.$refs.xTable[type]('isLadder')
        this.$refs.xTable[type]('ladderList')
      }

      let reloadItem

      // 普通报价
      if (val === SOU_ORDER_TYPE_ENUM.SIMPLE) {
        // 清空公式相关 和模型报价相关
        reloadItem = {
          materialFormulaRelateId: '',
          formulaId: '',
          formulaValue: '',
          formulaName: '',
          formulaValueList: []
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 显示阶梯价列
        toggleLadder('showColumn')
      }

      // 公式报价
      if (val === SOU_ORDER_TYPE_ENUM.FORMULA) {
        // 清空物料编码等数据
        reloadItem = {
          noCodeItem: 'N',
          itemId: '',
          itemCode: '',
          itemDesc: '',
          unit: '',
          categoryId: '',
          categoryCode: '',
          categoryName: '',
          isLadder: 'N',
          ladderList: []
        }

        // 显示公式列
        toggleFormula('showColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
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
    noCodeItemChange (row) {
      if (row.noCodeItem === 'Y' && row.itemCode) {
        // 无料号，清空原有选的料号相关数据
        row.itemCode = ''
        row.itemDesc = ''
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
      if (!this.pricingType.isFormulaPricing || !row) {
        return false
      }

      const {
        orgOuId,
        noCodeItem,
        categoryId,
        itemId
      } = row

      // 业务实体必选
      if (!orgOuId) {
        return false
      }
      const param = { orgOuId }

      // 无料号寻源，需要选品类
      if (noCodeItem === 'Y' && categoryId) {
        return {
          ...param,
          categoryId,
          mapKey: 'categoryRelateMap',
          valueKey: categoryId.toString()
        }
      }

      // 非无料号寻源，需要选物料编码
      if ((noCodeItem === 'N' || !noCodeItem) && itemId) {
        return {
          ...param,
          materialId: itemId,
          mapKey: 'materialRelateMap',
          valueKey: itemId.toString()
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
        noCodeItem: row.noCodeItem,
        categoryId: row.categoryId,
        itemId: row.itemId
      }, this.pricingType.isFormulaPricing)

      if (params) {
        // 查询公式
        const { data: formulaValueData } = await bidBuyerHttp.formula.getMaterialFormulaRelateInfos([params])

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
      if (!row.isSetFormulaValueList && this.pricingType.isFormulaPricing) {
        // 单行公式查询处理
        this.rowHandelFormulaValue(row, false, false)
      }
    },

    /* 阶梯报价 START */
    /* 打开 */
    openLadderPrice ({ $rowIndex, row }) {
      if (!row.requireQuantity) {
        this.$message.warning('请输入预计采购数量!')
        return
      }
      this.editIndex = $rowIndex
      this.editRow = {
        ...row,
        ladderPrices: row.ladderList
      }
      this.ladderPriceDetailVisible = true
    },
    /* 保存 */
    saveLadderPrices (data) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].ladderType = data.ladderType
      fullData[this.editIndex].ladderList = data.ladderPrices
    },
    /* 阶梯报价 END */

    /* 删除物料行 */
    deleteRow (row) {
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
      if (row.noCodeItem !== 'Y' && row.itemId) {
        // 非无料号
        this.itemCodeChange(null, row)
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
      if (row.noCodeItem !== 'Y' && row.itemId) {
        // 非无料号
        this.itemCodeChange(null, row)
      }
    },

    /* 选择一个物料 */
    itemCodeChange (val, row) {
      const {
        materialId = '',
        materialCode = '',
        materialName = '',
        unit = '',
        categoryId = '',
        categoryCode = '',
        categoryName = ''
      } = val || {}
      row.itemId = materialId
      row.itemCode = materialCode
      row.itemDesc = materialName
      row.unit = unit
      row.categoryId = categoryId
      row.categoryCode = categoryCode
      row.categoryName = categoryName

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row = {}, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
        callback(null)
      }
    },

    /* 选择分类 */
    categoryNameChange (value, row) {
      const {
        categoryCode = '',
        categoryName = '',
        categoryId = ''
      } = value || {}
      row.categoryCode = categoryCode
      row.categoryName = categoryName
      row.categoryId = categoryId

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 模型报价 START */
    /* 打开模型报价弹窗 */
    openModelQuoteDialog (index, row) {
      this.sourceLineIndex = index
      this.sourceLine = {
        modelQuoteLines: row.modelPriceTemplateList || []
      }
      this.modelQuoteDialogVisible = true
    },
    /* 保存模型报价 */
    saveModelQuoteData (data) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.sourceLineIndex].modelPriceTemplateList = data
    },
    /* 模型报价 END */

    /* 新增一行物料行 */
    addRow () {
      this.$refs.xTable.insertAt({
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
        deliveryPlace: '',
        taxRate: '',
        requireQuantity: '',
        buyAmount: '',
        priceType: 'STANDARD',
        purchaseType: '',
        unit: '',
        remark: '',
        // 带入项目信息中的价格有效期
        priceStartTime: this.biddingBase.priceStartTime || '',
        priceEndTime: this.biddingBase.priceEndTime || '',
        requireDate: null,
        formulaValue: null
      }, -1)
    },

    /* 技术文件 */
    // 打开弹窗
    openTechnicalDocumentsDialog (row, $index) {
      this.editIndex = $index
      // 判断是否是无料号，无料号不能从物料图纸库选择
      this.editRow = {
        materialCode: row.noCodeItem === 'Y' ? '' : row.itemCode,
        businessId: row.souItemId || '',
        detailData: row.itemFiles || []
      }
      this.technicalDocumentsDialogVisible = true
    },
    // 确认
    technicalDocumentsConfirm (value) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].itemFiles = (value || []).concat()
      fullData[this.editIndex].bidItemFiles = []
    },
    /* 技术文件 END */

    /* 校验表格数据 */
    validateTableData () {
      const { fullData } = this.$refs.xTable.getTableData()

      if (fullData.length === 0) {
        // 请先录入需求明细
        this.$message.warning(this.$t('bidMod.bidMsgList[27]'))
        return { status: false }
      }

      const warningMessage = (index, message) => {
        this.$message.warning(`第${index + 1}行 ${message}`)
      }

      for (const [index, item] of fullData.entries()) {
        if (item.orgOuId && !item.orgInvId) {
          // 维护了业务实体，库存组织必须维护！
          warningMessage(index, this.$t('bidMod.bidMsgList[38]'))
          return { status: false }
        }
        if (!item.itemCode && item.noCodeItem !== 'Y') {
          warningMessage(index, '请选择物料编码!')
          return { status: false }
        }
        if (!item.itemDesc) {
          // 请先录入物料名称!
          warningMessage(index, this.$t('bidMod.bidMsgList[28]'))
          return { status: false }
        }
        if (!item.unit) {
          // 请先录入单位!
          warningMessage(index, this.$t('bidMod.bidMsgList[29]'))
          return { status: false }
        }
        if (!item.categoryName) {
          // 请录入采购分类!
          warningMessage(index, this.$t('bidMod.bidMsgList[30]'))
          return { status: false }
        }
        // 是否进价格库
        if (this.biddingBase.isSyncToPriceLibrary === 'Y' && (!item.priceStartTime || !item.priceEndTime)) {
          // 请录入定价开始时间和结束时间!
          warningMessage(index, this.$t('bidMod.bidMsgList[31]'))
          return { status: false }
        }
        console.log(this.biddingBase.orderWay, SOU_ORDER_WAY_ENUM.COMBINED, 'this.biddingBase.orderWay')
        // 组合决标
        if (this.biddingBase.orderWay === SOU_ORDER_WAY_ENUM.COMBINED) {
          if (!item.itemGroup) {
            // 组合决标的决标方式，请输入组合！
            warningMessage(index, this.$t('bidMod.bidMsgList[32]'))
            return { status: false }
          }
        }
      }

      return {
        status: true,
        data: fullData
      }
    },

    /* 保存项目需求信息 */
    async saveRequirement (type) {
      const validateResult = this.validateTableData()

      if (!validateResult.status) {
        return
      }

      const response = await bidBuyerHttp.init.editRequireInfo({
        projectId: this.biddingBaseInfo.projectId,
        requireDesc: this.biddingBaseInfo.requireDesc,
        orderType: this.biddingBaseInfo.orderType,
        quoteTempName: this.biddingBaseInfo.quoteTempName,
        quoteTempId: this.biddingBaseInfo.quoteTempId,
        itemList: validateResult.data,
        // 是否是暂存
        isTempSave: type !== 'nextOne'
      })

      if (response) {
        this.$message.success(this.$t('common.success'))

        // 查询
        await this.getProjectRequirementsData()

        // 发起保存成功回调
        this.$emit('temp-save-success', type)
      }
    }
  }
}
</script>
