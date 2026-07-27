<template>
  <el-collapse
    v-model="activeDims"
    class="tab-form-styles"
  >
    <!-- 查询条件信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.searchConditionInfo')"
      name="1"
    >
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      >
        <template #vendorCode="{ scope }">
          <quick-search
            :show-input="scope.vendorCode"
            show-key="companyCode"
            :scope-data="scope"
            name="scc_sup_company_info_display"
            @close-quicksearch="getVendorObj"
          />
        </template>
        <template #bidResult="{ scope }">
          <DictSelect
            v-model="scope.bidResult"
            code="BIDDING_SELECT_STATES"
          />
        </template>
      </form-wrapper>
    </el-collapse-item>
    <!-- 投标汇总信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.bidSumInfo')"
      name="2"
    >
      <div
        v-if="!isGroup"
        style="padding: 3px"
      >
        <el-dropdown
          v-if="
            bidingStatus == 'TENDER_ENDING' ||
              bidingStatus == 'BUSINESS_EVALUATION'
          "
        >
          <el-button type="primary">
            {{ $t("bidMod.nextOrEliminate")
            }}<i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div class="paddingBt">
                <el-button
                  type="primary"
                  @click="toNextRoundConfirm('Y')"
                >
                  {{
                    $t("bidMod.toNextRoundConfirm")
                  }}
                </el-button>
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <el-button
                type="primary"
                @click="toNextRoundConfirm('N')"
              >
                {{
                  $t("bidMod.toEliminateConfirm")
                }}
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown
          v-if="
            bidingStatus == 'TENDER_ENDING' ||
              bidingStatus == 'BUSINESS_EVALUATION'
          "
        >
          <el-button type="primary">
            {{ $t("bidMod.bidAwardOperation")
            }}<i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="winTheBidding">
              <div class="paddingBt">
                <el-button
                  type="primary"
                  @click="winTheBidding"
                >
                  {{
                    $t("bid_mod.winTheBidding")
                  }}
                </el-button>
              </div>
            </el-dropdown-item>
            <el-dropdown-item command="lossTheBidding">
              <el-button
                type="primary"
                @click="lossTheBidding"
              >
                {{
                  $t("bid_mod.lossTheBidding")
                }}
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button
          v-if="
            bidingStatus == 'TENDER_ENDING' ||
              bidingStatus == 'BUSINESS_EVALUATION'
          "
          @click="endEvaluation"
        >
          {{ $t("bidMod.toPublishDialog") }}
        </el-button>
        <el-button
          v-if="bidingStatus == 'PUBLICITY_OF_RESULT'"
          type="primary"
          @click="resultApproval"
        >
          {{ $t("logisticsMod.generateResultApproval") }}
        </el-button>
        <el-button
          class="detail-pbtn"
          type="primary"
          @click="exportEvaExcel"
        >
          {{
            $t("orderMod.excelExport")
          }}
        </el-button>
      </div>
      <el-table
        :data="lgtVendorQuotedSums"
        style="width: 100%"
        border
        :cell-style="cellStyle"
        height="250px"
        @selection-change="checkChanges"
      >
        <el-table-column type="selection" />
        <el-table-column
          align="center"
          type="index"
          :label="$t('purSettlementMod.tabindex')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          :show-overflow-tooltip="true"
          min-width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :show-overflow-tooltip="true"
          :label="$t('common.vendorName')"
          min-width="150"
        />
        <el-table-column
          v-if="allParams.biding.bidingAwardWay != 'COMBINED_DECISION'"
          align="center"
          prop="startAddress"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.startAddress')"
          min-width="150"
        />
        <!-- 目的地 -->
        <el-table-column
          v-if="allParams.biding.bidingAwardWay != 'COMBINED_DECISION'"
          align="center"
          prop="endAddress"
          :show-overflow-tooltip="true"
          :label="$t('contractMod.destination')"
          min-width="150"
        />
        <el-table-column
          align="center"
          prop="sumPrice"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.totalPriceRMB')"
          min-width="150"
        />
        <el-table-column
          align="center"
          prop="rank"
          :show-overflow-tooltip="true"
          :label="$t('bidMod.rank')"
          min-width="100"
        />
        <!-- 决策结果 -->
        <el-table-column
          align="center"
          prop="bidResult"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.decisionResult')"
          :formatter="bidResultFormatter"
          min-width="150"
        />
        <!-- 下轮允许投标 -->
        <el-table-column
          align="center"
          prop="shortlisted"
          :show-overflow-tooltip="true"
          :label="$t('bidMod.nextRoundAllowBid')"
          min-width="150"
        />
        <!-- 是否代理报价 -->
        <el-table-column
          align="center"
          prop="ifProxy"
          :show-overflow-tooltip="true"
          :label="$t('bid_mod.isProxyBidding')"
          :formatter="ifProxyFormattor"
          min-width="150"
        />
      </el-table>
    </el-collapse-item>
    <!-- 投标明细信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.bidDetailInfo')"
      name="3"
    >
      <main-header
        :l-span="18"
        :r-span="6"
      >
        <template slot="left">
          <div
            v-if="
              (bidingStatus == 'TENDER_ENDING' ||
                bidingStatus == 'BUSINESS_EVALUATION') &&
                !isGroup
            "
          >
            <el-button
              type="primary"
              @click="saveTransportDistance"
            >
              {{
                $t("common.save")
              }}
            </el-button>
            <el-button
              v-if="businessModeCode === 'I'"
              @click="adjustment"
            >
              {{
                $t("logisticsMod.bulkDistanceAdjust")
              }}
            </el-button>
            <!-- <el-button v-if="businessModeCode==='I'" @click="adjustment1"
              >批量装载量调整</el-button
            > -->
            <el-button @click="adjustment2">
              {{
                $t("logisticsMod.bulkQuantityAdjust")
              }}
            </el-button>
            <el-button @click="adjustment3">
              {{
                $t("logisticsMod.billQuantityAdjust")
              }}
            </el-button>
            <el-button
              type="primary"
              :disabled="!lgtVendorQuotedLine.length"
              @click="exportEvaExcel"
            >
              {{ $t("orderMod.excelExport") }}
            </el-button>
          </div>
        </template>
        <template slot="right">
          <div
            v-if="
              (bidingStatus == 'TENDER_ENDING' ||
                bidingStatus == 'BUSINESS_EVALUATION') &&
                !isGroup
            "
          >
            <el-form label-width="100px">
              <!-- 贸易术语与进出口 -->
              <el-form-item
                :label="$t('logisticsMod.tradeTermsImpExp')"
                prop="combinationName"
              >
                <el-select
                  v-model="combinationName"
                  clearable
                  @change="tradeTermChange"
                >
                  <el-option
                    v-for="item in tradetermLists"
                    :key="item.tradeTerm"
                    :label="item.combinationName"
                    :value="item.combinationName"
                  />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </template>
      </main-header>
      <el-table
        ref="tableGrid"
        :data="lgtVendorQuotedLine"
        style="width: 100%"
        border
        :cell-style="cellStyles"
        height="300px"
        @selection-change="checkChange"
      >
        <el-table-column
          type="selection"
          fixed="left"
        />
        <el-table-column
          align="center"
          type="index"
          :label="$t('purSettlementMod.tabindex')"
          width="50"
          fixed="left"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          fixed="left"
          :show-overflow-tooltip="true"
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          fixed="left"
          :show-overflow-tooltip="true"
          :label="$t('common.vendorName')"
          width="150"
        />
        <el-table-column
          v-for="(col, key) in innerHeader"
          v-if="col.purchaseVisibleFlag === 'Y' && col.fieldCode != 'number'"
          :key="key"
          align="center"
          :prop="col.fieldCode"
          :label="col.fieldName"
          :min-width="col.width || 100"
          :formatter="row => formattor(row, col)"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          v-for="(col, key) in innerHeader"
          v-if="col.purchaseVisibleFlag === 'Y' && col.fieldCode == 'number'"
          :key="key"
          align="center"
          :prop="col.fieldCode"
          :label="col.fieldName"
          :min-width="col.width || 100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-input
              v-if="isGroup"
              v-model="scope.row.number"
              type="number"
            />
            <span v-else>{{ scope.row.number }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column
          align="center"
          prop="startAddress"
          :show-overflow-tooltip="true"
          label="始发起"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="endAddress"
          :show-overflow-tooltip="true"
          label="目的地"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="expenseItem"
          :show-overflow-tooltip="true"
          label="费用项"
          :formatter="expenseItemFormatter"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="chargeMethod"
          :show-overflow-tooltip="true"
          label="计费方式"
          :formatter="chargeMethodFormatter"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="chargeUnit"
          :show-overflow-tooltip="true"
          :formatter="chargeUnitFormatter"
          label="计费单位"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="transportDistance"
          :show-overflow-tooltip="true"
          label="供方运输距离"
          width="150">
        </el-table-column> -->
        <!-- 运输距离调整 -->
        <el-table-column
          v-if="businessModeCode === 'I'"
          align="center"
          prop="transportDistanceRevision"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.transportDistanceAdjust')"
          width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="isGroup"
              v-model="scope.row.transportDistanceRevision"
            />
            <span v-else>{{ scope.row.transportDistanceRevision }}</span>
          </template>
        </el-table-column>
        <!-- 装载量 -->
        <el-table-column
          v-if="businessModeCode === 'I'"
          align="center"
          prop="loadNumber"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.loadNumber')"
          width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="isGroup"
              v-model="scope.row.loadNumber"
            />
            <span v-else>{{ scope.row.loadNumber }}</span>
          </template>
        </el-table-column>
        <!-- 评标结论 -->
        <el-table-column
          align="center"
          prop="bidResult"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.bidResult')"
          width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="isGroup"
              v-model="scope.row.bidResult"
            />
            <span v-else>{{ scope.row.bidResult }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column
          align="center"
          prop="number"
          :show-overflow-tooltip="true"
          label="调整数量为"
          width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.number" />
          </template>
        </el-table-column> -->
        <!-- <el-table-column
          align="center"
          prop="singleKmCost"
          :show-overflow-tooltip="true"
          label="单公里成本"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="singleDragCost"
          :show-overflow-tooltip="true"
          label="单拖成本"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="expense"
          :show-overflow-tooltip="true"
          label="费用"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="currency"
          :show-overflow-tooltip="true"
          label="币制"
          :formatter="currencyFormatter"
          width="150">
        </el-table-column>
        <el-table-column
          align="center"
          prop="totalAmount"
          :show-overflow-tooltip="true"
          label="总计（人民币）"
          width="150">
        </el-table-column> -->
      </el-table>
      <div style="width: 100%; margin-bottom: 5px">
        <el-pagination
          align="center"
          :current-page="viewIndex"
          :page-sizes="[10, 15, 20, 30]"
          :page-size="viewSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="queryTotal"
          @current-change="changeCurrentIndex"
          @size-change="changeCurrentSize"
        />
      </div>
      <el-form
        ref="form4"
        :model="form"
      >
        <!-- 技术评选结论 -->
        <el-form-item
          prop="technoSelection"
          :label="$t('logisticsMod.techSelectionConclusion')"
        >
          <el-input
            v-model="form.technoSelection"
            type="textarea"
            :rows="2"
            disabled
          />
        </el-form-item>
      </el-form>
    </el-collapse-item>
    <!-- 请输入调整距离 -->
    <srm-dialog
      :visible.sync="transportDistanceVisible"
      :title="$t('logisticsMod.msgAdjustDistance')"
      size="small"
    >
      <el-form
        ref="form"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="transportDistance">
          <el-input
            v-model="form.transportDistance"
            type="number"
            :placeholder="$t('common.pleaseTypeContents')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="transportDistanceConfirm"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="transportDistanceVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 请输入调整装载量 -->
    <srm-dialog
      :visible.sync="transportDistanceVisible1"
      :title="$t('logisticsMod.msgLoadNum')"
      size="small"
    >
      <el-form
        ref="form1"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="loadNumber">
          <el-input
            v-model="form.loadNumber"
            type="number"
            :placeholder="$t('common.pleaseTypeContents')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="transportDistanceConfirm1"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="transportDistanceVisible1 = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 请输入调整数量 -->
    <srm-dialog
      :visible.sync="transportDistanceVisible2"
      :title="$t('logisticsMod.msgAdjustQuantity')"
      size="small"
    >
      <el-form
        ref="form2"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="number">
          <el-input
            v-model="form.number"
            type="number"
            :placeholder="$t('common.pleaseTypeContents')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="transportDistanceConfirm2"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="transportDistanceVisible2 = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 计费数量调整 -->
    <srm-dialog
      :visible.sync="transportDistanceVisible3"
      :title="$t('logisticsMod.billQuantityAdjust')"
      size="middle"
    >
      <el-table
        :data="calculateTable"
        style="width: 100%"
        border
        highlight-current-row
      >
        <!-- 计费方式 -->
        <el-table-column
          align="center"
          prop="chargeMethod"
          :formatter="row => formattor(row, { fieldCode: 'chargeMethod' })"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.chargeMethod')"
        />
        <!-- 计费单位 -->
        <el-table-column
          align="center"
          prop="chargeUnit"
          :formatter="row => formattor(row, { fieldCode: 'chargeUnit' })"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.chargeUnit')"
        />
        <!-- 数量 -->
        <el-table-column
          align="center"
          prop="num"
          :show-overflow-tooltip="true"
          :label="$t('bid_mod.quantity')"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.num" />
          </template>
        </el-table-column>
      </el-table>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="transportDistanceConfirm3"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="transportDistanceVisible3 = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 请输入总结说明 -->
    <srm-dialog
      :visible.sync="summaryDescriptionVisible"
      :title="$t('logisticsMod.msgConclusionDesc')"
      size="middle"
    >
      <el-form
        ref="form3"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="summaryDescription">
          <el-input
            v-model="form.summaryDescription"
            type="textarea"
            :rows="2"
            :placeholder="$t('common.pleaseTypeContents')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="summaryDescriptionConfirm"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="summaryDescriptionVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
  </el-collapse>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { downloadFileLink } from 'lib@/utils/file'
import { adaptDictData } from '@/utils'
import MainHeader from 'lib@/components/Table/MainHeader'
import {
  getDictItem

} from '@/api/common'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'CommercialBiding',
  components: {
    QuickSearch,
    FormWrapper,
    CPagination,
    MainHeader
  },
  props: [
    'allParams',
    'currentRound',
    'scopeBidingId',
    'businessModeCode',
    'bidingStatus',
    'tableHeader',
    'isGroup'
  ],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      preArr: [
        { prop: 'round', label: this.$t('bidMod.preformround') }, // 显示轮次
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'), // 供应商编码
          slot: 'vendorCode',
          type: 'slot'
        },
        { prop: 'rank', label: this.$t('bidMod.rank') }, // 排名
        {
          prop: 'bidResult',
          label: this.$t('logisticsMod.decisionResult'), // 决策结果
          slot: 'bidResult',
          type: 'slot'
        }
      ],
      queryParams: null,
      currentRows: [],
      transportDistanceVisible: false,
      transportDistanceVisible1: false,
      transportDistanceVisible2: false,
      transportDistanceVisible3: false,
      summaryDescriptionVisible: false,
      lgtVendorQuotedSums: [],
      lgtVendorQuotedLines: [],
      tradetermLists: [],
      innerHeader: [],
      wholeArkList: [],
      calculateTable: [],
      importExportMethodList: [],
      selectList: [],
      form: {
        transportDistance: null,
        loadNumber: null,
        number: null,
        technoSelection: null,
        summaryDescription: null
      },
      rules: {
        transportDistance: [
          { required: true, message: this.$t('logisticsMod.msgAdjustDistance') }
        ], // 请输入调整距离
        loadNumber: [
          { required: true, message: this.$t('logisticsMod.msgLoadNum') }
        ], // 请输入调整装载量
        number: [
          { required: true, message: this.$t('logisticsMod.msgAdjustQuantity') }
        ], // 请输入调整数量
        summaryDescription: [
          { required: true, message: this.$t('logisticsMod.msgConclusionDesc') }
        ] // 请输入总结说明
      },
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      tradeTerm: null,
      combinationName: null
    }
  },
  computed: {
    lgtVendorQuotedLine () {
      var list = []
      this.lgtVendorQuotedLines.forEach((item, index) => {
        if (
          (this.viewIndex - 1) * this.viewSize <= index &&
          index < this.viewIndex * this.viewSize
        ) {
          list.push(item)
        }
      })
      return list
    }
  },
  watch: {
    tableHeader: {
      immediate: true,
      handler: function (n, o) {
        if (JSON.stringify(n) !== JSON.stringify(o)) {
          this.innerHeader = n.map(({ fieldCode, ...rest }) => {
            // js 数据库字段转驼峰
            let str = fieldCode
              .toLowerCase()
              .replace(/_(\w)/g, function ($0, $1) {
                return $1.toUpperCase()
              })
            return {
              ...rest,
              fieldCode: str
            }
          })
        }
      },
      deep: true
    }
  },
  mounted () {
    // 整柜/拼柜
    getDictItem('FCL /LCL').then(res => {
      this.wholeArkList = adaptDictData(res.data, 'dict')
    })
    // 进出口方式 IMPORT_EXPORT_METHOD
    getDictItem('EXP/IMP').then(res => {
      this.importExportMethodList = adaptDictData(res.data, 'dict')
    })
    this.getTradetermLists()
  },
  methods: {
    // 单元格的 style 的回调方法
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      if (row.rank == 1) {
        if (column.property == 'sumPrice') {
          if (rowIndex != 0) {
            return 'border-top: 1px solid #88c1f4;color: rgb(54, 206, 40)'
          }
          return 'color: rgb(54, 206, 40)'
        }
        if (rowIndex != 0) {
          return 'border-top: 1px solid #88c1f4'
        }
      }
      if (column.property == 'sumPrice' && row.rank == 2) {
        return 'color:orange'
      }
      if (column.property == 'sumPrice' && row.rank == 3) {
        return 'color:#88c1f4'
      }
      return ''
    },
    // 单元格的 style 的回调方法
    cellStyles ({ row, column, rowIndex, columnIndex }) {
      if (rowIndex == 0 && column.property == 'singleDragCost') {
        return 'color: rgb(54, 206, 40)'
      }
      if (rowIndex == 1 && column.property == 'singleDragCost') {
        return 'color:orange'
      }
      if (rowIndex == 2 && column.property == 'singleDragCost') {
        return 'color:#88c1f4'
      }
      return ''
    },
    expenseItemFormatter (row) {
      return this.$getDictLabel('CHARGE_NAME', row.expenseItem)
    },
    ifProxyFormattor (row) {
      return row.ifProxy == 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    chargeMethodFormatter (row) {
      return this.$getDictLabel('CHARGE_LEVEL', row.chargeMethod)
    },
    chargeUnitFormatter (row) {
      return this.$getDictLabel('SUB_LEVEL', row.chargeUnit)
    },
    currencyFormatter (row) {
      return this.$getDictLabel('currency', row.currency)
    },
    bidResultFormatter (row) {
      return this.$getDictLabel('BIDDING_SELECT_STATES', row.bidResult)
    },
    formattor (row, col) {
      if (col.fieldCode === 'logisticsCategoryCode') {
        return row.logisticsCategoryName
      } else if (col.fieldCode === 'expenseItem') {
        return this.$getDictLabel('CHARGE_NAME', row.expenseItem)
      } else if (col.fieldCode === 'chargeMethod') {
        return this.$getDictLabel('CHARGE_LEVEL', row.chargeMethod)
      } else if (col.fieldCode === 'leg') {
        return this.$getDictLabel('LEG', row.leg)
      } else if (col.fieldCode === 'fromCountry') {
        return row.fromCounty
      } else if (col.fieldCode === 'fromProvince') {
        return row.fromProvince
      } else if (col.fieldCode === 'fromCity') {
        return row.fromCity
      } else if (col.fieldCode === 'fromCounty') {
        return row.fromCounty
      } else if (col.fieldCode === 'toCountry') {
        return row.toCountry
      } else if (col.fieldCode === 'toProvince') {
        return row.toProvince
      } else if (col.fieldCode === 'toCity') {
        return row.toCity
      } else if (col.fieldCode === 'toCounty') {
        return row.toCounty
      } else if (col.fieldCode === 'currency') {
        return this.$getDictLabel('currency', row.currency)
      } else if (col.fieldCode === 'chargeUnit') {
        return this.$getDictLabel('SUB_LEVEL', row.chargeUnit)
      } else if (col.fieldCode === 'wholeArk') {
        return this.$getDictLabelByValue(this.wholeArkList, row.wholeArk)
      } else if (col.fieldCode === 'ifBack') {
        return this.$getDictLabel('YES_OR_NO', row.ifBack)
      } else if (col.fieldCode === 'tradeTerm') {
        return this.$getDictLabel('TRADE_TERM', row.tradeTerm)
      } else if (col.fieldCode === 'importExportMethod') {
        return this.$getDictLabelByValue(
          this.importExportMethodList,
          row.importExportMethod
        )
      } else {
        return row[col.fieldCode]
      }
    },
    getTradetermLists () {
      this.$http({
        url:
          '/api-pd/logistics/tradetermscombination/queryTradeTermsCombinationDto',
        method: 'get',
        params: {},
        loading: true
      })
        .then(data => {
          this.tradetermLists = data.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuerydata (v) {
      let params = {
        round: this.currentRound,
        bidingId: this.scopeBidingId,
        ...v
      }
      this.queryParams = v
      this.$http({
        url: '/api-pd/logistics/biding/queryLgtVendorQuotedSumDto',
        method: 'get',
        params: params,
        loading: true
      })
        .then(data => {
          this.lgtVendorQuotedSums = data.data.lgtVendorQuotedSums
          this.lgtVendorQuotedLines = data.data.lgtVendorQuotedLines || []
          this.queryTotal = this.lgtVendorQuotedLines.length
          this.formatCalculateTable() // 数据处理
          this.form.technoSelection = data.data.technoSelection
          this.$refs.tableGrid.doLayout() // 暂时解决表格错位的问题
        })
        .catch(err => {
          console.log(err)
        })
    },
    formatCalculateTable () {
      let obj = {}
      this.calculateTable = this.lgtVendorQuotedLines.reduce(function (item, next) {
        if (next.chargeMethod && next.chargeUnit) {
          if (obj[next.chargeMethod + '' + next.chargeUnit]) {
            obj[next.chargeMethod + '' + next.chargeUnit] =
                true &&
                item.push({
                  chargeMethod: next.chargeMethod,
                  chargeUnit: next.chargeUnit,
                  num: null
                })
          }
        }
        if (next.chargeMethod && !next.chargeUnit) {
          if (obj[next.chargeMethod]) {
            obj[next.chargeMethod] =
              true &&
              item.push({
                chargeMethod: next.chargeMethod,
                chargeUnit: '',
                num: null
              })
          }
        }
        if (!next.chargeMethod && next.chargeUnit) {
          if (obj[next.chargeUnit]) {
            obj[next.chargeUnit] =
              true &&
              item.push({
                chargeMethod: '',
                chargeUnit: next.chargeUnit,
                num: null
              })
          }
        }
        return item
      },
      [])
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    checkChange (val) {
      this.currentRows = val
    },
    checkChanges (val) {
      this.selectList = val
    },
    adjustment () {
      if (!this.currentRows.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelBatchAdjustRow'), // 请勾选批量调整的行
          type: 'error'
        })
        return
      }
      this.transportDistanceVisible = true
    },
    adjustment1 () {
      if (!this.currentRows.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelBatchAdjustRow'),
          type: 'error'
        })
        return
      }
      this.transportDistanceVisible1 = true
    },
    adjustment2 () {
      if (!this.currentRows.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelBatchAdjustRow'),
          type: 'error'
        })
        return
      }
      this.transportDistanceVisible2 = true
    },
    adjustment3 () {
      this.transportDistanceVisible3 = true
    },
    saveTransportDistance () {
      this.$http({
        url: '/api-pd/logistics/biding/selectionQuotedLineSave',
        method: 'post',
        data: {
          lgtVendorQuotedLines: this.lgtVendorQuotedLines,
          combinationName: this.combinationName
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
          this.$emit('getFormDetail', this.scopeBidingId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    transportDistanceConfirm () {
      this.$refs.form.validate(valid => {
        if (valid) {
          let quotedLineIdList = this.currentRows.map(i => i.quotedLineId)
          this.lgtVendorQuotedLines.map(i => {
            if (quotedLineIdList.indexOf(i.quotedLineId) > -1) {
              i.transportDistanceRevision = this.form.transportDistance
            }
          })
          this.transportDistanceVisible = false
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    transportDistanceConfirm1 () {
      this.$refs.form1.validate(valid => {
        if (valid) {
          let quotedLineIdList = this.currentRows.map(i => i.quotedLineId)
          this.lgtVendorQuotedLines.map(i => {
            if (quotedLineIdList.indexOf(i.quotedLineId) > -1) {
              i.loadNumber = this.form.loadNumber
            }
          })
          this.transportDistanceVisible1 = false
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    transportDistanceConfirm2 () {
      this.$refs.form2.validate(valid => {
        if (valid) {
          let quotedLineIdList = this.currentRows.map(i => i.quotedLineId)
          this.lgtVendorQuotedLines.map(i => {
            if (quotedLineIdList.indexOf(i.quotedLineId) > -1) {
              i.number = this.form.number
            }
          })
          this.transportDistanceVisible2 = false
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    transportDistanceConfirm3 () {
      this.transportDistanceVisible3 = false
      this.calculateTable.forEach(item => {
        this.lgtVendorQuotedLines.forEach(itm => {
          if (
            item.chargeMethod == itm.chargeMethod &&
            item.chargeUnit == itm.chargeUnit
          ) {
            itm.number = item.num
          }
        })
      })
    },
    toNextRoundConfirm (type) {
      if (!this.selectList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelActionLine'), // 请勾选操作的行
          type: 'error'
        })
        return
      }
      this.$http({
        url: '/api-pd/logistics/biding/shortlistedOrEliminated',
        method: 'post',
        data: {
          lgtVendorQuotedSums: this.selectList,
          shortlisted: type
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    winTheBidding () {
      if (!this.selectList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelActionLine'),
          type: 'error'
        })
        return
      }
      this.$http({
        url: '/api-pd/logistics/biding/award',
        method: 'post',
        data: {
          lgtVendorQuotedSums: this.selectList,
          bidResult: 'WIN'
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    lossTheBidding () {
      if (!this.selectList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgSelActionLine'),
          type: 'error'
        })
        return
      }
      this.$http({
        url: '/api-pd/logistics/biding/award',
        method: 'post',
        data: {
          lgtVendorQuotedSums: this.selectList,
          bidResult: 'FAIL'
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    endEvaluation () {
      this.$http({
        url: '/api-pd/logistics/biding/publicResult',
        method: 'get',
        params: {
          bidingId: this.scopeBidingId,
          round: this.currentRound
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
          this.$emit('getFormDetail', this.scopeBidingId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    resultApproval () {
      this.summaryDescriptionVisible = true
    },
    exportEvaExcel () {
      let str = `&round=${this.currentRound}`
      if (this.queryParams && this.queryParams.round) {
        str = ''
        str += `&round=${this.queryParams.round}`
      }
      if (this.queryParams && this.queryParams.vendorId) {
        str += `&vendorId=${this.queryParams.vendorId}`
      }
      downloadFileLink(
        `/api-pd/logistics/biding/quotedSumExport?bidingId=${
          this.scopeBidingId
        }${str}`,
        this.$t('logisticsMod.selectionExportXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    summaryDescriptionConfirm () {
      this.$refs.form3.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/biding/generateResultApprove',
            method: 'get',
            params: {
              bidingId: this.scopeBidingId,
              summaryDescription: this.form.summaryDescription
            },
            loading: true
          })
            .then(res => {
              this.summaryDescriptionVisible = false
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
          return false
        }
      })
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.$nextTick(() => {
        this.tradeTermChange(this.combinationName)
      })
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.$nextTick(() => {
        this.tradeTermChange(this.combinationName)
      })
    },
    tradeTermChange (val) {
      this.$refs.tableGrid.clearSelection()
      let targe = this.tradetermLists.find(i => i.combinationName == val)
      this.tradeTerm = targe.tradeTerm
      let list = []
      this.lgtVendorQuotedLines.forEach(i => {
        i.ifSelected = 'N'
        i.tradeTerm = null
        if (i.importExportMethod == targe.importExportMethod) {
          if (
            targe.legExpenseItemDtos.some(
              j => j.leg == i.leg && j.expenseItem == i.expenseItem
            )
          ) {
            i.tradeTerm = targe.tradeTerm
            i.ifSelected = 'Y'
            list.push(i)
          }
        }
      })
      if (list.length) {
        list.forEach(row => {
          this.$refs.tableGrid.toggleRowSelection(row)
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.tab-form-styles /deep/ {
  .el-table__fixed::before,
  .el-table__fixed-right::before {
    height: 0;
  }
  .main-header {
    padding: 0;
    padding-bottom: 10px;
  }
}
</style>
