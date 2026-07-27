<template>
  <!-- 项目需求 -->
  <el-form :model="projectRequirementsForm">
    <SrmRow style="padding-top: 10px">
      <SrmCol :init-col="1">
        <!-- 需求简述 -->
        <el-form-item :label="$t('bidMod.bondDesc2')" label-width="80px">
          <el-input
            v-model="bargainBaseInfo.requireDesc"
            type="textarea"
            :rows="2"
            :disabled="readOnly"
          />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!-- 报价类型 -->
        <el-form-item :label="$t('bidMod.pricingType')" label-width="80px">
          <DictSelect
            v-model="bargainBaseInfo.pricingType"
            code="BRG_PRICING_TYPE"
            :disabled="readOnly || isNotManual"
            @change="setPricingType"
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
        class="detail-pbtn"
        :disabled="readOnly"
        @click="addBrgRequirementLineList"
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
      align="center"
      max-height="500"
      :data="brgRequirementLineList"
      :valid-config="{ showMessage: false }"
      :edit-rules="vxeTableValidRules"
      :edit-config="{
        trigger: 'click',
        mode: 'row',
        autoClear: false,
        enabled: !readOnly
      }"
      @edit-actived="vxeTableEditActived"
    >
      <!--操作-->
      <vxe-column
        width="140"
        :title="$t('bidMod.operation')"
        fixed="left"
        :visible="!readOnly || pricingType.isTemplatePricing"
      >
        <template #default="{ row, $rowIndex }">
          <!--删除-->
          <el-button
            v-if="!row.purchaseRequestNum && !readOnly"
            type="text"
            @click="handleDeleteRow(row)"
          >
            {{ $t('common.delete') }}
          </el-button>

          <!-- 模型报价 -->
          <el-button
            v-if="pricingType.isTemplatePricing"
            type="text"
            :disabled="false"
            @click="openModelQuoteDialog($rowIndex, row)"
          >
            {{ $t('bid_mod.modelQuoteTitle') }}
          </el-button>
        </template>
      </vxe-column>

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
        v-if="bargainBaseInfo.bargainAwardWay === 'COMBINED'"
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

      <!--是否显示需求量-->
      <vxe-column
        field="showRequireNum"
        :title="$t('bid_mod.showRequireNum')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <DictSelect v-model="row.showRequireNum" code="YES_OR_NO" />
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
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-if="row.isNoCodeItem === 'Y'"
            v-model="row.targetDesc"
            maxlength="100"
          />
          <span v-else>{{ row.targetDesc }}</span>
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
          <DictSelect
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

      <!--t 选择公式-->
      <vxe-column
        field="materialFormulaRelateId"
        :title="$t('bidMod.biddingManagementBuyer.formulaId')"
        width="125"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-select
            v-if="pricingType.isFormulPricing"
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
        :formatter="({ cellValue }) => pricingType.isFormulPricing ? cellValue : ''"
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
          <el-input
            v-model="row.amount"
            v-input-format="{ type: 'float' }"
          />
        </template>
        <template #default="{ row }">
          {{ row.amount }}
        </template>
      </vxe-column>

      <!--技术文件-->
      <vxe-column
        field="graphFileId"
        :title="$t('bidMod.technicalDocuments.title')"
        width="150"
      >
        <template #default="{ row, $rowIndex }">
          <el-button type="text" @click="openTechnicalDocumentsDialog(row, $rowIndex)">
            {{ readOnly ? $t('common.view') : $t('common.select') }}
          </el-button>
        </template>
      </vxe-column>

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
    </vxe-table>

    <!-- 模型报价 -->
    <ModelQuoteDialog
      v-if="modelQuoteDialogVisible"
      :visible.sync="modelQuoteDialogVisible"
      :source-line="sourceLine"
      :is-read-only-by-buyer="readOnly"
      @saveModelQuoteData="saveModelQuoteData"
    />

    <!--技术文件-->
    <TechnicalDocumentsDialog
      v-if="technicalDocumentsDialogVisible"
      :visible.sync="technicalDocumentsDialogVisible"
      :readonly="readOnly"
      :params="editRow"
      :detail-data="editRow.detailData"
      @confirm="technicalDocumentsConfirm"
    />
  </el-form>
</template>

<script>
import { parseTime } from '@/utils'
import { getFormulaValuePreconditions, souProjectSourceFromManual } from 'lib@/composition/origin/composition'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ModelQuoteDialog from 'lib@/composition/bargain/modelQuote/modelQuoteDialog'
import ProviceCity from 'lib@/components/provice-city'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'

export default {
  name: 'ProjectRequirements',

  components: {
    ProviceCity,
    OrganizationSelector,
    RenderAsyncText,
    ModelQuoteDialog,
    QuickSearch,
    TechnicalDocumentsDialog
  },

  filters: {
    // 时间格式化
    parseTimeFilters (val) {
      return val ? parseTime(val, '{y}-{m}-{d}') : ''
    }
  },

  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    },
    isActiveMenu: {
      type: Boolean
    },
    pricingType: {
      type: Object,
      required: true
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      projectRequirementsForm: {},
      brgRequirementLineList: [],
      sourceLine: null,
      sourceLineIndex: '',
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
      technicalDocumentsDialogVisible: false,
      editRow: null,
      editIndex: ''
    }
  },

  computed: {
    bargainBaseInfo: {
      get: function () {
        return this.bargainBase
      },
      set: function (val) {
        this.$emit('update:bargainBase', val)
      }
    },

    // 非手工创建的单据
    isNotManual () {
      return !souProjectSourceFromManual(this.bargainBaseInfo.sourceFrom)
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
    async getProjectRequirementsData (bargainId) {
      const id = bargainId || this.scopeBargainId
      const response = await this.$api.brg.getRequireInfoByBargainId(id)
      if (response && response.data) {
        this.brgRequirementLineList = response.data.map(
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
              bargainId: id,
              editable: false,
              materialEditable: false,
              deliveryPlace: d
            }
          }
        )
      }
    },

    /* 选择报价类型 */
    setPricingType (val) {
      if (val !== 'FORMULA_PURCHASER') {
        // 清空公式值
        this.$refs.xTable.reloadData(this.$refs.xTable.getTableData().fullData.map(item => {
          return {
            ...item,
            formulaId: '',
            materialFormulaRelateId: '',
            formulaValue: '',
            formulaValueList: []
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
        const { data: formulaValueData } = await this.$api.bid.formula.getMaterialFormulaRelateInfos([params])

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
    orgOuIdChange (node, id, { row, $rowIndex }) {
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
    orgInvIdChange (node, id, row) {
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
    addBrgRequirementLineList () {
      this.$refs.xTable.insertAt({
        bargainId: this.scopeBargainId,
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
        // 带入项目信息中的价格有效期
        priceStartTime: this.bargainBaseInfo.priceStartTime || '',
        priceEndTime: this.bargainBaseInfo.priceEndTime || '',
        demandDate: null,
        formulaValue: null
      }, -1)
    },

    /* 技术文件 */
    // 打开弹窗
    openTechnicalDocumentsDialog (row, $index) {
      this.editIndex = $index
      // 判断是否是无料号，无料号不能从物料图纸库选择
      this.editRow = {
        materialCode: row.isNoCodeItem === 'Y' ? '' : row.targetNum,
        businessId: row.requirementLineId || '',
        detailData: row.brgItemFiles || []
      }
      this.technicalDocumentsDialogVisible = true
    },
    // 确认
    technicalDocumentsConfirm (value) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].brgItemFiles = (value || []).concat()
    },
    /* 技术文件 END */

    /* 保存项目需求信息 */
    saveRequirement (type) {
      const { fullData } = this.$refs.xTable.getTableData()

      if (fullData.length === 0) {
        // 请先录入需求明细
        this.$message.warning(this.$t('bidMod.bidMsgList[27]'))
        return
      }

      const warningMessage = (index, message) => {
        this.$message.warning(`第${index + 1}行 ${message}`)
      }

      for (const [index, item] of fullData.entries()) {
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
        if (this.bargainBaseInfo.isSyncToPriceLibrary === 'Y' && (!item.priceStartTime || !item.priceEndTime)) {
          // 请录入定价开始时间和结束时间!
          warningMessage(index, this.$t('bidMod.bidMsgList[31]'))
          return
        }
        // 组合决标
        if (this.bargainBaseInfo.bargainAwardWay === 'COMBINED_DECISION') {
          if (!item.itemGroup) {
            // 组合决标的决标方式，请输入组合！
            warningMessage(index, this.$t('bidMod.bidMsgList[32]'))
            return
          }
        }
      }

      this.$api.brg.inquiryByProject.tempSaveOrSubmitRequireInfo({
        bargainId: this.scopeBargainId,
        requireDesc: this.bargainBaseInfo.requireDesc,
        pricingType: this.bargainBaseInfo.pricingType,
        requirementLineList: fullData,
        // 是否是暂存
        isTempSave: type !== 'nextOne'
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
