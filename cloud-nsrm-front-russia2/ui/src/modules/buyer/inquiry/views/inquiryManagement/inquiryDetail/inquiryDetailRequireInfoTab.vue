<template>
  <div class="inquiry-detail-require-info-tab">
    <el-form
      ref="form"
      :model="headerData"
      label-width="82px"
      label-position="right"
      :rules="rules"
      :disabled="readonly"
    >
      <SrmRow>
        <SrmCol :init-col="3">
          <!--报价类型-->
          <el-form-item
            :label="$t('bidMod.pricingType')"
            prop="orderType"
          >
            <DictSelect
              v-model="headerData.orderType"
              code="SOU_ORDER_TYPE"
              clearable
              :transform-options="orderTypeTransformOptions"
              @change="orderTypeChange"
            />
          </el-form-item>
        </SrmCol>
        <!-- 报价类型为料费分离的时候启用报价模板 -->
        <SrmCol v-if="isSeparation" :init-col="3">
          <el-form-item
            :label="$t('route.quoteTemplateList')"
            prop="quoteTempName"
          >
            <QuickSearch
              :show-input="headerData.quoteTempName"
              show-key="tempName"
              :scope-data="headerData"
              :disabled="readonly"
              clearable
              name="scc_sou_quote_temp"
              @close-quicksearch="quoteTempNameChange"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <div v-if="!readonly">
      <span style="padding: 11px;">{{ $t("bidMod.requireInfoLine") }}</span>
      <!--新增物料-->
      <el-button
        type="primary"
        :disabled="!headerData.orderType"
        @click="addOneItem"
      >
        {{ $t("bidMod.addItem") }}
      </el-button>

      <!--TODO 导入物料等待接口-->
      <MImport
        v-if="false"
        ref="import"
        type="default"
        style="display: inline-block; margin-left: 10px"
        :title="$t('components.eio.importTitle')"
        :extra-data="extraData"
        up-load-url="/api-inq/inquiry/header/inqItem/importExcel"
        :show-success-deal="true"
        @downloadTemplate="downloadTemplate"
        @handleSuccess="itemImportSuccess"
      />
    </div>

    <!-- 表格 -->
    <vxe-table
      ref="xTable"
      border
      show-overflow="tooltip"
      keep-source
      align="center"
      max-height="385"
      :data="itemsTableData"
      :valid-config="{ showMessage: false }"
      :edit-rules="vxeTableValidRules"
      :edit-config="{
        trigger: 'click',
        mode: 'row',
        autoClear: false,
        enabled: !readonly
      }"
      style="margin-top: 15px"
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
            :value="scope.row.orgOuId"
            :scope="scope.row"
            :table-index="scope.$rowIndex"
            node-type="OU"
            :parent-id="-1"
            :clearable="false"
            :placeholder="$t('common.pleaseSelect')"
            :disabled="(!!scope.row.orgOuId && !!scope.row.sourceFromLineId)"
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
        <template #edit="scope">
          <OrganizationSelector
            :ref="`inv_${scope.$rowIndex}`"
            v-model="scope.row.orgInvId"
            :scope="scope.row"
            :table-index="scope.$rowIndex"
            node-type="INV"
            :clearable="false"
            :placeholder="$t('common.pleaseSelect')"
            :parent-id="scope.row.orgOuId"
            :disabled="(!!scope.row.orgInvId && !!scope.row.sourceFromLineId)"
            @select="orgInvIdChange"
          />
        </template>
        <template #default="{ row }">
          {{ row.orgInvName }}
        </template>
      </vxe-column>

      <!--是否无料号寻源-->
      <vxe-column
        field="noCodeItem"
        :title="$t('bidMod.biddingManagementBuyer.isNoCodeItem')"
        width="130"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-checkbox
            v-model="scope.row.noCodeItem"
            :disabled="!!scope.row.sourceFromLineId"
            true-label="Y"
            false-label="N"
            @change="noCodeItemChange(scope)"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.noCodeItem) }}
        </template>
      </vxe-column>

      <!--t 物料编码-->
      <vxe-column
        field="itemCode"
        :title="$t('bidMod.itemCode')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <QuickSearch
            v-if="scope.row.noCodeItem !== 'Y'"
            :show-input="scope.row.itemCode"
            show-key="materialCode"
            :scope-data="scope.row"
            :disabled-select="!scope.row.orgOuId || !scope.row.orgInvId"
            name="scc_base_material_item_inv_enable"
            :disabled="!!scope.row.sourceFromLineId"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': scope.row.orgInvId }"
            @close-quicksearch="itemCodeChange"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(scope.row, callback)"
          />
        </template>
        <template #default="{ row }">
          {{ row.noCodeItem !== 'Y' ? row.itemCode : '' }}
        </template>
      </vxe-column>

      <!--物料名称-->
      <vxe-column
        field="itemDesc"
        :title="$t('bidMod.itemDesc')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-if="scope.row.noCodeItem === 'Y'"
            v-model="scope.row.itemDesc"
            maxlength="100"
            :disabled="!!scope.row.sourceFromLineId"
          />
          <span v-else>{{ scope.row.itemDesc }}</span>
        </template>
        <template #default="{ row }">
          {{ row.itemDesc }}
        </template>
      </vxe-column>

      <!--物料分类-->
      <vxe-column
        field="categoryName"
        :title="$t('bidMod.categoryName')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <QuickSearch
            v-if="scope.row.noCodeItem === 'Y'"
            :show-input="scope.row.categoryName"
            show-key="categoryName"
            :scope-data="scope.row"
            clearable
            name="scc_base_purchase_category2"
            @close-quicksearch="categoryNameChange"
          />
          <span v-else>{{ scope.row.categoryName }}</span>
        </template>
        <template #default="{ row }">
          {{ row.categoryName }}
        </template>
      </vxe-column>

      <!--组合-->
      <vxe-column
        field="itemGroup"
        :title="$t('bidMod.itemGroup')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.itemGroup"
            maxlength="30"
            :disabled="!!scope.row.sourceFromLineId"
          />
        </template>
        <template #default="{ row }">
          {{ row.itemGroup }}
        </template>
      </vxe-column>

      <!--行类型-->
      <vxe-column
        field="itemType"
        :title="$t('bidMod.itemType')"
        width="120"
        :edit-render="{}"
      >
        <template #edit="scope">
          <DictSelect
            v-model="scope.row.itemType"
            code="DMAND_LINE_TYPE"
            clearable
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('DMAND_LINE_TYPE', row.itemType) }}
        </template>
      </vxe-column>

      <!--t 预计数量-->
      <vxe-column
        field="requireQuantity"
        :title="$t('bidMod.demandQuantity')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.requireQuantity"
            v-input-format="{ type: 'number' }"
            :disabled="!!scope.row.sourceFromLineId"
          />
        </template>
        <template #default="{ row }">
          {{ row.requireQuantity }}
        </template>
      </vxe-column>

      <!--t 单位-->
      <vxe-column
        field="unit"
        :title="$t('bidMod.unit')"
        width="90"
        :edit-render="{}"
      >
        <template #edit="scope">
          <DictSelect
            v-if="scope.row.noCodeItem === 'Y'"
            v-model="scope.row.unit"
            clearable
            code="unit"
          />
          <span v-else>{{ $getDictLabel('unit', scope.row.unit) }}</span>
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('unit', row.unit) }}
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
            :disabled="!isNormal"
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
        <template #edit="scope">
          <el-select
            v-model="scope.row.materialFormulaRelateId"
            clearable
            :disabled="!isFormula"
            @change="formulaChange(scope.row)"
          >
            <el-option
              v-for="(item, index) in (scope.row.formulaValueList || [])"
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
        width="125"
      />

      <!--模板报价-->
      <vxe-column
        field="templatePrice"
        :title="$t('templatePrice.label')"
        width="110"
      >
        <template #default="scope">
          <QuickSearch
            v-if="!readonly"
            ref="tempAttrQuickSearch"
            :btn-title="$t('templatePrice.select')"
            name="sou_quote_temp"
            btn-type="text"
            show-button
            multi-select
            :confirm-auto-close="false"
            :pre-query-data="{ 't.temp_status': 'VALID' }"
            @close-quicksearch="selectQuoteTemplate"
          />
          <!--已选显示模板名称，未选显示选择模板-->
        </template>
      </vxe-column>

      <!--t 定价开始日期-->
      <vxe-column
        field="priceStartTime"
        :title="$t('bidMod.fixedPriceBegin')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-date-picker
            v-model="scope.row.priceStartTime"
            type="date"
            :format="$formatDatePicker"
            :picker-options="cannotLessCurrentTimeOptions"
          />
        </template>
        <template #default="{ row }">
          {{ $parseTime(row.priceStartTime) }}
        </template>
      </vxe-column>

      <!--定价结束日期-->
      <vxe-column
        field="priceEndTime"
        :title="$t('bidMod.fixedPriceEnd')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-date-picker
            v-model="scope.row.priceEndTime"
            type="date"
            :format="$formatDatePicker"
            :picker-options="cannotLessCurrentTimeOptions"
          />
        </template>
        <template #default="{ row }">
          {{ $parseTime(row.priceEndTime) }}
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
        :title="$t('common.remark')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.remark"
            maxlength="300"
          />
        </template>
        <template #default="{ row }">
          {{ row.remark }}
        </template>
      </vxe-column>

      <!--操作-->
      <vxe-column
        width="60"
        :title="$t('common.operation')"
        fixed="right"
        :visible="!readonly"
      >
        <template #default="{ row, $rowIndex }">
          <!--删除-->
          <el-button
            v-if="!row.sourceFromLineId"
            type="text"
            @click="deleteRow(row, $rowIndex)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </vxe-column>
    </vxe-table>

    <!--阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDetailVisible"
      :visible.sync="ladderPriceDetailVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
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
  </div>
</template>

<script>
/**
 * 需求信息
 */
import { formulaHttp } from 'modb@/inquiry/api'
import { getFormulaValuePreconditions } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { downloadFileLink } from 'lib@/utils/file'
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import QuickSearch from 'lib@/components/QuickSearch.vue'
import OrganizationSelector from 'lib@/components/organization-selector/index.vue'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog/index.vue'
import MImport from 'lib@/components/import'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'

export default {
  name: 'InquiryDetailRequireInfoTab',

  components: {
    QuickSearch,
    OrganizationSelector,
    LadderPriceDetail,
    TechnicalDocumentsDialog,
    MImport
  },

  mixins: [cannotLessCurrentTime],

  props: {
    isCurrentActiveTab: {
      type: Boolean,
      required: true
    },
    items: {
      type: [Array, Object],
      required: true
    },
    header: {
      type: Object,
      required: true
    },
    formProjectId: {
      type: [String, Number],
      required: true
    },
    readonly: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      rules: {
        orderType: [{ required: true, message: this.$t('bidMod.chooseQuoteType') }],
        quoteTempName: [{ required: true, message: this.$t('bidMod.chooseQuoteTemplate') }]
      },
      ladderPriceDetailVisible: false,
      editRow: null,
      editIndex: '',
      // 校验配置
      vxeTableValidRules: {
        orgOuId: [{ required: true }],
        orgInvId: [{ required: true }],
        requireQuantity: [{ required: true }],
        priceStartTime: [{ required: true }],
        priceEndTime: [{ required: true }],
        itemType: [{ required: true }],
        // itemCode: [{ required: true }],
        itemDesc: [{ required: true }],
        categoryName: [{ required: true }],
        unit: [{ required: true }]
      },
      itemsTableData: [],
      technicalDocumentsDialogVisible: false,
      technicalDocumentsParams: {
        materialCode: '',
        detailData: []
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },
    // 公式报价
    isFormula () {
      return this.headerData.orderType === SOU_ORDER_TYPE_ENUM.FORMULA
    },
    // 普通报价
    isNormal () {
      return this.headerData.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE
    },
    // 料费分离
    isSeparation () {
      return this.headerData.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
    },

    extraData () {
      return {
        fileModular: 'inq',
        fileFunction: 'item',
        fileType: 'excel',
        projectId: this.formProjectId,
        orderType: this.headerData.orderType
      }
    }
  },

  watch: {
    items: {
      handler (val) {
        this.itemsTableData = JSON.parse(JSON.stringify(val || []))
        this.$nextTick(() => {
          this.orderTypeChange(this.headerData.orderType, false)
        })
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 报价类型过滤 */
    orderTypeTransformOptions (options) {
      // 过滤模板报价
      return options.filter(item => item.value !== SOU_ORDER_TYPE_ENUM.TEMPLATE)
    },

    /* 改变报价类型 */
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

      // 报价模板相关列
      const toggleTemplate = type => {
        this.$refs.xTable[type]('templatePrice')
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
          formulaValueList: [],
          // FIXME [ORION] 模板数据
          templatePrice: ''
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 显示阶梯价列
        toggleLadder('showColumn')
        // 隐藏模板选择列
        toggleTemplate('hideColumn')
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
          ladderList: [],
          // FIXME [ORION] 模板数据
          templatePrice: ''
        }

        // 显示公式列
        toggleFormula('showColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 隐藏模板选择列
        toggleTemplate('hideColumn')
      }

      // 报价模板
      if (val === SOU_ORDER_TYPE_ENUM.TEMPLATE) {
        // 清空公式和阶梯价相关
        reloadItem = {
          materialFormulaRelateId: '',
          formulaId: '',
          formulaValue: '',
          formulaName: '',
          formulaValueList: [],
          isLadder: 'N',
          ladderList: []
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 显示模板选择列
        toggleTemplate('showColumn')
      }

      // 料费分离
      if (val === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION) {
        // 清空公式和阶梯价相关
        reloadItem = {
          materialFormulaRelateId: '',
          formulaId: '',
          formulaValue: '',
          formulaName: '',
          formulaValueList: [],
          isLadder: 'N',
          ladderList: []
        }
        // 隐藏公式列
        toggleFormula('hideColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 隐藏模板选择列
        toggleTemplate('hideColumn')
      }

      // 不是料费分离，清空报价模板信息
      if (val !== SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION) {
        this.headerData.quoteTempId = null
        this.headerData.quoteTempName = null
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

    /* 选择分类 */
    categoryNameChange (value, row) {
      row.categoryCode = value.categoryCode || ''
      row.categoryName = value.categoryName || ''
      row.categoryId = value.categoryId || ''

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 选择报价模板 */
    quoteTempNameChange (value) {
      this.headerData.quoteTempId = value ? value.tempId : null
      this.headerData.quoteTempName = value ? value.tempName : null
    },

    /* 新增一行物料 */
    addOneItem () {
      const columnArray = this.$refs.xTable.getColumns()
      let keys = {
        itemId: '',
        itemCode: '',
        itemDesc: '',
        categoryId: '',
        categoryCode: '',
        categoryName: '',
        projectId: this.formProjectId,
        ladderList: [],
        // 默认标准类型
        itemType: 'STANDARD',
        organizationId: this.headerData.organizationId
      }
      // 添加未写入字段，设置初始值
      for (const column of columnArray) {
        if (column.field && !keys[column.field]) {
          keys[column.field] = null
        }
      }
      this.itemsTableData.push({ ...keys })

      // this.$refs.xTable.insertAt({
      //   itemId: '',
      //   itemCode: '',
      //   itemDesc: '',
      //   unit: '',
      //   categoryId: '',
      //   categoryName: '',
      //   projectId: this.formProjectId,
      //   ladderList: [],
      //   ladderType: '',
      //   priceStartTime: '',
      //   priceEndTime: '',
      //   // 默认标准类型
      //   itemType: 'STANDARD',
      //   organizationId: this.headerData.organizationId
      // }, -1)
    },

    /* 选择一个物料 */
    itemCodeChange (val, row) {
      const { fullData } = this.$refs.xTable.getTableData()
      if (val && fullData.map(v => v.itemId).includes(val.materialId)) {
        this.$message.warning(this.$t('bidMod.itemRepeatMsg'))
        return
      }

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
      row.categoryName = categoryName
      row.categoryCode = categoryCode

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning(this.$t('bidMod.selectOuAndOrgMsg'))
        callback(null)
      }
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
      }, this.isFormula)

      if (params) {
        // 查询公式
        const { data: formulaValueData } = await formulaHttp.getMaterialFormulaRelateInfos([params])

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
      if (!row.isSetFormulaValueList && this.isFormula) {
        // 单行公式查询处理
        this.rowHandelFormulaValue(row, false, false)
      }
    },

    /* 阶梯报价 START */
    /* 打开 */
    openLadderPrice ({ $rowIndex, row }) {
      if (!row.requireQuantity) {
        this.$message.warning(this.$t('bidMod.requireQuantityMsg'))
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

    /* 模型报价 START */
    // 选择一个报价模板
    selectQuoteTemplate () {},
    /* 模型报价 END */

    /* 删除一行 */
    deleteRow (row, index) {
      console.log(row, index, 'row deleteRow')

      this.itemsTableData.splice(index, 1)
    },

    /* 选择业务实体 */
    orgOuIdChange (val, _id, row, index) {
      if (val && row.orgOuId === val.organizationId) {
        return
      }

      row.orgOuId = val ? val.organizationId : ''
      row.orgOuCode = val ? val.organizationCode : ''
      row.orgOuName = val ? val.organizationName : ''
      this.$refs[`inv_${index}`].clearOptions()
      row.orgInvId = ''
      row.orgInvCode = ''
      row.orgInvName = ''

      // 重选库存组织，清空物料选择
      if (row.noCodeItem !== 'Y' && row.itemId) {
        // 非无料号
        this.itemCodeChange(null, row)
      } else {
        // 处理公式报价
        this.rowHandelFormulaValue(row)
      }
    },

    /* 选择库存组织 */
    orgInvIdChange (val, _id, row) {
      if (val && row.orgInvId === val.organizationId) {
        return
      }

      row.orgInvId = val ? val.organizationId : ''
      row.orgInvCode = val ? val.organizationCode : ''
      row.orgInvName = val ? val.organizationName : ''

      // 重选库存组织，清空物料选择
      if (row.noCodeItem !== 'Y' && row.itemId) {
        // 非无料号
        this.itemCodeChange(null, row)
      }
    },

    /* 选择公式值 */
    formulaChange (row) {
      if (!row.materialFormulaRelateId) {
        // 清空选择
        row.formulaValue = ''
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

    /* 是否无料号寻源勾选 */
    noCodeItemChange ({ row }) {
      // 无料号，清空原有选的料号相关数据
      row.itemId = ''
      row.itemCode = ''
      row.itemDesc = ''
      row.categoryName = ''
      row.formulaId = ''
      row.materialFormulaRelateId = ''
      row.formulaValue = ''
      row.formulaValueList = []
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
      fullData[this.editIndex].itemFiles = value || []
    },
    /* 技术文件 END */

    /* 导入物料 START */
    // 下载模板
    downloadTemplate () {
      downloadFileLink(
        '/api-file/files-anon/file/fileupload/downloadTemplate/INQITEM_IMPORT',
        this.$t('bidMod.itemImportTemp') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 导入成功
    itemImportSuccess () {
      this.$emit('refresh', this.formProjectId)
    },
    /* END */

    /* 校验 */
    validateForm () {
      return new Promise(async resolve => {
        const valid = await this.$refs.form.validate().catch(() => this.__focus_error__())

        if (!valid) {
          resolve(false)
          return
        }

        const { fullData } = this.$refs.xTable.getTableData()

        if (fullData.length === 0) {
          this.$message.warning(this.$t('bidMod.inpItemInfo'))
          resolve(false)
          return
        }

        const warningMessage = (index, message) => {
          this.$message.warning(this.$t('bidMod.warningMessage', { index: index + 1, message }))
          resolve(false)
        }

        for (const [index, item] of fullData.entries()) {
          if (!item.orgOuId) {
            warningMessage(index, this.$t('purchaseDemand.orgIdTips'))
            return
          }
          if (!item.orgInvId) {
            warningMessage(index, this.$t('purchaseDemand.organizationIdTips'))
            return
          }
          if (!item.itemCode && item.noCodeItem !== 'Y') {
            warningMessage(index, this.$t('dataConfMod.msgInputItemCode'))
            return
          }
          if (!item.itemDesc) {
            warningMessage(index, this.$t('bidMod.inpItemDesc'))
            return
          }
          if (!item.categoryCode) {
            warningMessage(index, this.$t('bidMod.inpCategory'))
            return
          }
          if (!item.unit) {
            warningMessage(index, this.$t('logisticsMod.msgPurchaseApply[3]'))
            return
          }
          if (!item.itemType) {
            warningMessage(index, this.$t('bidMod.inpItemType'))
            return
          }
          if (!item.requireQuantity) {
            warningMessage(index, this.$t('bidMod.requireQuantityMsg'))
            return
          }
          // 阶梯报价
          if (item.isLadder === 'Y') {
            if (!item.ladderList || (item.ladderList && item.ladderList.length === 0)) {
              warningMessage(index, this.$t('bidMod.inpLadderQuote'))
              return
            }
          }
          if (!item.priceStartTime) {
            warningMessage(index, this.$t('bidMod.inpPrceStartTime'))
            return
          }
          if (!item.priceEndTime) {
            warningMessage(index, this.$t('bidMod.inpPriceEndTime'))
            return
          }
          if (item.priceStartTime > item.priceEndTime) {
            warningMessage(index, this.$t('bidMod.priceStartLessEnd'))
            return
          }
          // 公式报价
          if (this.isFormula && !item.materialFormulaRelateId) {
            warningMessage(index, this.$t('bidMod.formulaMsg'))
            return
          }
          // 组合报价，必须填组合
          if (this.headerData.orderWay === 'COMBINED' && !item.itemGroup) {
            warningMessage(index, this.$t('bidMod.orderWayMsg'))
            return
          }
        }

        // 通过校验。更新items数据
        this.$emit('update:items', fullData)
        resolve(true)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.inquiry-detail-require-info-tab {
  .required-column-label {
    color: red;
    margin-right: 5px;
  }
}
</style>
