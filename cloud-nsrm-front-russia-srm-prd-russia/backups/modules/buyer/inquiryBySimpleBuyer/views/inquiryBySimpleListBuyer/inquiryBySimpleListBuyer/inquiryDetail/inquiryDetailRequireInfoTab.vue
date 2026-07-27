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
      <srm-row>
        <srm-col :init-col="3">
          <!--报价类型 非手工创建的暂不允许改变-->
          <el-form-item
            :label="$t('bidMod.pricingType')"
            prop="quoteType"
          >
            <DictSelect
              v-model="headerData.quoteType"
              code="INQ_QUOTE_TYPE"
              clearable
              :disabled="isNotManual"
              @change="quoteTypeChange"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>

    <div v-if="!readonly">
      <span style="padding: 11px;">{{ $t("bidMod.requireInfoLine") }}</span>
      <!--新增物料-->
      <el-button
        type="primary"
        :disabled="!headerData.quoteType"
        @click="addOneItem"
      >
        {{ $t("bidMod.addItem") }}
      </el-button>

      <!--模板报价暂不支持导入-->
      <MImport
        v-if="!isTemplate"
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
            :disabled="!!scope.row.orgOuId && scope.row.purchaseReqLineId"
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
            :disabled="!!scope.row.orgInvId && scope.row.purchaseReqLineId"
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
        title="是否无料号寻源"
        width="130"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-checkbox
            v-model="scope.row.noCodeItem"
            :disabled="scope.row.purchaseReqLineId"
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
            :disabled="scope.row.purchaseReqLineId"
            :pre-query-data="{ 'scboa.ORGANIZATION_ID': scope.row.orgInvId }"
            @close-quicksearch="itemCodeChange"
            @before-open="(value, callback) => itemCodeQuickSearchBeforeOpen(scope.row, callback)"
          />
        </template>
        <template #default="{ row }">
          {{ row.noCodeItem !== 'Y' ? row.itemCode : '' }}
        </template>
      </vxe-column>

      <!--物料描述-->
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
            :disabled="scope.row.purchaseReqLineId"
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
          <el-input v-model="scope.row.itemGroup" maxlength="30" />
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
        field="demandQuantity"
        :title="$t('bidMod.demandQuantity')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.demandQuantity"
            v-input-format="{ type: 'number' }"
            :disabled="scope.row.purchaseReqLineId"
          />
        </template>
        <template #default="{ row }">
          {{ row.demandQuantity }}
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

      <!--t 选择公式-->
      <vxe-column
        field="materialFormulaRelateId"
        title="选择公式"
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

      <!--t 定价开始日期-->
      <vxe-column
        field="fixedPriceBegin"
        :title="$t('bidMod.fixedPriceBegin')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-date-picker
            v-model="scope.row.fixedPriceBegin"
            type="date"
            :picker-options="pickerOptions"
          />
        </template>
        <template #default="{ row }">
          {{ $dayjsParse(row.fixedPriceBegin) }}
        </template>
      </vxe-column>

      <!--定价结束日期-->
      <vxe-column
        field="fixedPriceEnd"
        :title="$t('bidMod.fixedPriceEnd')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-date-picker
            v-model="scope.row.fixedPriceEnd"
            type="date"
            :picker-options="pickerOptions"
          />
        </template>
        <template #default="{ row }">
          {{ $dayjsParse(row.fixedPriceEnd) }}
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
          <!--删除 手工创建的才允许删-->
          <el-button
            v-if="!row.purchaseReqLineId"
            type="text"
            @click="handleDelClick(row)"
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
      business-type="INQUIRY"
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
import { getFormulaValuePreconditions, souProjectSourceFromManual } from 'lib@/composition/origin/composition'
import { QUOTE_TYPE_MAGIC } from '@/library/composition/inquiryBySimple/utils'
import { downloadFileLink } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
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
    formInquiryId: {
      type: [String, Number],
      required: true
    },
    pickerOptions: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      ladderType: 'standard',
      // 文件上传配置信息
      rules: { quoteType: [{ required: true, message: '请选择报价类型' }] },
      ladderPriceDetailVisible: false,
      editRow: null,
      editIndex: '',
      // 校验配置
      vxeTableValidRules: {
        orgOuId: [{ required: true }],
        orgInvId: [{ required: true }],
        demandQuantity: [{ required: true }],
        fixedPriceBegin: [{ required: true }],
        fixedPriceEnd: [{ required: true }]
      },
      itemsTableData: [],
      technicalDocumentsDialogVisible: false,
      technicalDocumentsParams: {
        materialCode: '',
        detailData: []
      }
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
      return this.headerData.quoteType === QUOTE_TYPE_MAGIC.FORMULA
    },
    // 普通报价
    isNormal () {
      return this.headerData.quoteType === QUOTE_TYPE_MAGIC.NORMAL
    },
    // 模板报价
    isTemplate () {
      return this.headerData.quoteType === QUOTE_TYPE_MAGIC.TEMPLATE
    },

    extraData () {
      return {
        fileModular: 'inq',
        fileFunction: 'item',
        fileType: 'excel',
        inquiryId: this.formInquiryId,
        quoteType: this.headerData.quoteType
      }
    },

    // 非手工创建的单据
    isNotManual () {
      return !souProjectSourceFromManual(this.headerData.sourceFrom)
    }
  },

  watch: {
    items: {
      handler (val) {
        this.itemsTableData = (val || []).concat([])
        this.$nextTick(() => {
          this.quoteTypeChange(this.headerData.quoteType, false)
        })
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 改变报价类型 */
    quoteTypeChange (val, clear = true) {
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
      if (val === QUOTE_TYPE_MAGIC.NORMAL) {
        // 清空公式相关 和模型报价相关
        reloadItem = {
          materialFormulaRelateId: '',
          formulaId: '',
          formulaValue: '',
          formulaName: '',
          formulaValueList: [],
          // 模板数据
          isTemplate: 'N',
          quoteTempId: '',
          quoteTempName: ''
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 显示阶梯价列
        toggleLadder('showColumn')
        // 隐藏模板选择列
        toggleTemplate('hideColumn')
      }

      // 公式报价
      if (val === QUOTE_TYPE_MAGIC.FORMULA) {
        // 清空物料编码等数据
        reloadItem = {
          noCodeItem: 'N',
          itemId: '',
          itemCode: '',
          itemDesc: '',
          unit: '',
          categoryId: '',
          categoryName: '',
          isLadder: 'N',
          ladderPrices: [],
          // 模板数据
          isTemplate: 'N',
          quoteTempId: '',
          quoteTempName: ''
        }

        // 显示公式列
        toggleFormula('showColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 隐藏模板选择列
        toggleTemplate('hideColumn')
      }

      // 报价模板
      if (val === QUOTE_TYPE_MAGIC.TEMPLATE) {
        // 清空公式和阶梯价相关
        reloadItem = {
          isFormula: 'N',
          materialFormulaRelateId: '',
          formulaId: '',
          formulaValue: '',
          formulaName: '',
          formulaValueList: [],
          isLadder: 'N',
          ladderPrices: [],
          // 模板数据
          isTemplate: 'Y'
        }

        // 隐藏公式列
        toggleFormula('hideColumn')
        // 隐藏阶梯价列
        toggleLadder('hideColumn')
        // 显示模板选择列
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

    /* 选择分类 */
    categoryNameChange (value, row) {
      row.categoryCode = value.categoryCode || ''
      row.categoryName = value.categoryName || ''
      row.categoryId = value.categoryId || ''

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 新增一行物料 */
    addOneItem () {
      this.$refs.xTable.insertAt({
        itemId: '',
        itemCode: '',
        itemDesc: '',
        unit: '',
        categoryId: '',
        categoryName: '',
        inquiryId: this.formInquiryId,
        ladderPrices: [],
        ladderType: '',
        // 默认标准类型
        itemType: 'STANDARD',
        organizationId: this.headerData.organizationId,
        // 模板报价
        quoteTempId: '',
        quoteTempName: ''
      }, -1)
    },

    /* 选择一个物料 */
    itemCodeChange (val, row) {
      const { fullData } = this.$refs.xTable.getTableData()
      if (val && fullData.map(v => v.itemId).includes(val.materialId)) {
        this.$message.warning('该物料已存在,请勿重复添加!')
        return
      }
      row.itemId = val ? val.materialId : ''
      row.itemCode = val ? val.materialCode : ''
      row.itemDesc = val ? val.materialName : ''
      row.unit = val ? val.unit : ''
      row.categoryId = val ? val.categoryId : ''
      row.categoryName = val ? val.categoryName : ''

      // 处理公式报价
      this.rowHandelFormulaValue(row)
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
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
      if (!row.isSetFormulaValueList && this.isFormula) {
        // 单行公式查询处理
        this.rowHandelFormulaValue(row, false, false)
      }
    },

    /* 阶梯报价 START */
    /* 打开 */
    openLadderPrice ({ $rowIndex, row }) {
      if (!row.demandQuantity) {
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
      const response = await this.$http({
        url: `/api-bid/buyer/quote-temp/detail/${val.tempId}`,
        method: 'GET',
        loading: true
      })

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

    /* 删除一行 */
    handleDelClick (row) {
      this.$refs.xTable.remove(row)
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
        businessId: row.inquiryItemId || '',
        detailData: row.inqItemFiles || []
      }
      this.technicalDocumentsDialogVisible = true
    },
    // 确认
    technicalDocumentsConfirm (value) {
      const { fullData } = this.$refs.xTable.getTableData()
      fullData[this.editIndex].inqItemFiles = value || []
    },
    /* 技术文件 END */

    /* 导入物料 START */
    // 下载模板
    downloadTemplate () {
      downloadFileLink(
        '/api-file/files-anon/file/fileupload/downloadTemplate/INQITEM_IMPORT',
        '物料导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 导入成功
    itemImportSuccess () {
      this.$emit('getFormDetail', this.formInquiryId)
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
          this.$message.warning('请先录入物料信息!')
          resolve(false)
          return
        }

        const warningMessage = (index, message) => {
          this.$message.warning(`第${index + 1}行 ${message}`)
          resolve(false)
        }

        for (const [index, item] of fullData.entries()) {
          if (!item.orgOuId) {
            warningMessage(index, '请选择业务实体!')
            return
          }
          if (!item.orgInvId) {
            warningMessage(index, '请选择库存组织!')
            return
          }
          if (!item.itemType) {
            warningMessage(index, '请选择行类型!')
            return
          }
          if (!item.demandQuantity) {
            warningMessage(index, '请录入预计采购量!')
            return
          }
          // 阶梯报价
          if (item.isLadder === 'Y') {
            if (!item.ladderPrices || (item.ladderPrices && item.ladderPrices.length === 0)) {
              warningMessage(index, '请录入阶梯报价!')
              return
            }
          }
          if (!item.fixedPriceBegin) {
            warningMessage(index, '请选择定价开始日期!')
            return
          }
          if (!item.fixedPriceEnd) {
            warningMessage(index, '请选择定价结束日期!')
            return
          }
          if (item.fixedPriceBegin > item.fixedPriceEnd) {
            warningMessage(index, '定价开始时间不能大于结束时间!')
            return
          }
          // 公式报价
          if (this.isFormula && !item.materialFormulaRelateId) {
            warningMessage(index, '公式报价下存在物料没有维护公式')
            return
          }
          // 组合报价，必须填组合
          if (this.headerData.quoteRule === 'QUOTE_BY_SUPPLIER' && !item.itemGroup) {
            warningMessage(index, '组合报价方式，必须输入组合')
            return
          }
          // 模版报价，选择模版
          if (this.isTemplate && !item.quoteTempId) {
            warningMessage(index, '请选择一个报价模版')
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
