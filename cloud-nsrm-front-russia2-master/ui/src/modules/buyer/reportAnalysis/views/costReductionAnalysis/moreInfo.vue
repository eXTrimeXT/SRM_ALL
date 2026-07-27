<template>
  <el-container
    class="flex-container the_more_info_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        :pre-form-obj.sync="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <!-- 组织类型 -->
        <template #organizationTypeCode="{ scope }">
          <DictSelect
            v-model="scope.organizationTypeCode"
            code="ORG_TYPE"
            custom-select-type="ORG_TYPE"
          />
        </template>
        <!-- 组织 -->
        <template #organizationId="{ scope }">
          <OrganizationSelector
            :key="scope.organizationTypeCode"
            ref="selector"
            v-model="scope.organizationId"
            :parent-id="-1"
            :node-type="scope.organizationTypeCode"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope"
            @select="selectHandle"
          />
        </template>
        <!-- 品类 -->
        <template #categoryId="{ scope }">
          <CCategorySelect
            v-model="scope.categoryName"
            :scope="scope"
            show-key="categoryName"
            @select="comfirmSelect"
          />
        </template>
        <!-- 物料 -->
        <template #materialCode="{ scope }">
          <QuickSearch
            :show-input="scope.materialCode"
            show-key="materialCode"
            :scope-data="scope"
            name="scc_base_material_item"
            @close-quicksearch="getMaterialCode"
          />
        </template>
        <!-- 品类类型 -->
        <template #level="{ scope }">
          <DictSelect
            v-model="scope.level"
            code="CATEGORY"
            custom-select-type="CATEGORY"
          />
        </template>
        <!-- 年份 -->
        <template #year="{ scope }">
          <el-date-picker
            v-model="scope.year"
            class="year"
            type="year"
            value-format="yyyy"
          />
        </template>
        <!-- 开始月份 -->
        <template #startMonth="{ scope }">
          <DictSelect
            v-model="scope.startMonth"
            code="MONTH"
            :dict-class="store"
          />
        </template>
        <!-- 结束月份 -->
        <template #endMonth="{ scope }">
          <DictSelect
            v-model="scope.endMonth"
            code="MONTH"
            :dict-class="store"
          />
        </template>
      </FormWrapper>

      <TableView
        :ref="gridId"
        :row-index="false"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :source="source"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import moreInfoDetail from './moreInfoDetail.vue'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { purchaseAnalysisApi } from 'modb@/orderManagementBuyer/api'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'MoreInfo',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector,
    QuickSearch,
    CCategorySelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      store: createDictClass({ 'MONTH': [] }),
      MONTH: [
        { id: 1, value: 1, label: i18n.t('reportMod.January') },
        { id: 2, value: 2, label: i18n.t('reportMod.February') },
        { id: 3, value: 3, label: i18n.t('reportMod.March') },
        { id: 4, value: 4, label: i18n.t('reportMod.April') },
        { id: 5, value: 5, label: i18n.t('reportMod.May') },
        { id: 6, value: 6, label: i18n.t('reportMod.June') },
        { id: 7, value: 7, label: i18n.t('reportMod.July') },
        { id: 8, value: 8, label: i18n.t('reportMod.Augest') },
        { id: 9, value: 9, label: i18n.t('reportMod.September') },
        { id: 10, value: 10, label: i18n.t('reportMod.October') },
        { id: 11, value: 11, label: i18n.t('reportMod.November') },
        { id: 12, value: 12, label: i18n.t('reportMod.December') }
      ],
      tableName: 'moreInfo',
      defaultTableHeader: [],
      filterParams: {},
      pageSize: 15,
      gridId: 'list',
      formLabelWidth: '100px',
      source: null,
      preArr: [
        {
          prop: 'organizationTypeCode',
          label: this.$t('dataConfMod.orgType'), // 组织类型
          slot: 'organizationTypeCode',
          type: 'slot'
        },
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'), // 采购组织
          slot: 'organizationId',
          type: 'slot'
        }
      ],
      queryParam: {},
      preFormObj: {},
      tableHeader: []
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.store.setDictionary('MONTH', this.MONTH)

    const { type, name, ...queryParams } = this.$attrs.params
    // 获取列表接口
    const sourceArray = [
      'queryMonthsDetail',
      'queryYearCumulativeDetail',
      'queryCategorRateDetail',
      'queryCategorAmountDetail',
      'queryCategorUpAmountDetail'
    ]
    const sourceName = sourceArray[type - 1]
    this.source = purchaseAnalysisApi[sourceName]
    // 设置查询条件
    if (type === 5) {
      this.preArr = [
        {
          prop: 'organizationTypeCode',
          label: this.$t('dataConfMod.orgType'), // 组织类型
          slot: 'organizationTypeCode',
          type: 'slot'
        },
        {
          prop: 'categoryId',
          label: this.$t('common.category'), // 品类
          type: 'slot',
          slot: 'categoryId'
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'), // 物料编码
          slot: 'materialCode',
          type: 'slot'
        },
        {
          prop: 'level',
          label: this.$t('reportMod.categoryType'), // 品类类型
          slot: 'level',
          type: 'slot'
        },
        {
          prop: 'year',
          label: this.$t('dataConfMod.year'), // 年份
          slot: 'year',
          type: 'slot'
        },
        {
          prop: 'startMonth',
          label: this.$t('components.startMonth'), // 开始月份
          slot: 'startMonth',
          type: 'slot'
        },
        {
          prop: 'endMonth',
          label: this.$t('components.endMonth'), // 结束月份
          slot: 'endMonth',
          type: 'slot'
        }
      ]
    } else {
      this.preArr = [
        {
          prop: 'organizationTypeCode',
          label: this.$t('dataConfMod.orgType'), // 组织类型
          slot: 'organizationTypeCode',
          type: 'slot'
        },
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'), // 采购组织
          slot: 'organizationId',
          type: 'slot'
        },
        {
          prop: 'level',
          label: this.$t('reportMod.categoryType'), // 品类类型
          slot: 'level',
          type: 'slot'
        },
        {
          prop: 'year',
          label: this.$t('dataConfMod.year'), // 年份
          slot: 'year',
          type: 'slot'
        },
        {
          prop: 'startMonth',
          label: this.$t('components.startMonth'), // 开始月份
          slot: 'startMonth',
          type: 'slot'
        },
        {
          prop: 'endMonth',
          label: this.$t('components.endMonth'), // 结束月份
          slot: 'endMonth',
          type: 'slot'
        }
      ]
    }
    // 设置列表表头
    const tableHeaderArray = [
      [
        {
          prop: 'ym',
          dataType: 'dateTime',
          label: this.$t('orderMod.warehouseTime') // 入库时间
        },
        {
          prop: 'amount',
          label: this.$t('reportMod.actualPurAmount') // 实际采购金额
        },
        {
          prop: 'sumAmount',
          label: this.$t('reportMod.actualCapitalReduction') // 实际降本金额
        },
        {
          prop: 'targetAmount',
          label: this.$t('reportMod.targetCaptialReduction') // 目标降本金额
        },
        {
          prop: 'reachRate',
          label: this.$t('reportMod.reachRateMonth') // 月降本达成率
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 160,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'ym',
          label: this.$t('orderMod.warehouseTime') // 入库时间
        },
        {
          prop: 'amount',
          label: this.$t('reportMod.sumCapitalReduction') // 累计降本金额
        },
        {
          prop: 'rate',
          label: this.$t('reportMod.sumCostReductionRate') // 累计降本率
        },
        {
          prop: 'targetRate',
          label: this.$t('reportMod.annualTargetReductionRate') // 年度目标降本率
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank'), // 排名
          width: 80
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName') // 品类名称
        },
        {
          prop: 'categoryCrAmount',
          label: this.$t('reportMod.cateReduceAmount') // 品类降本金额
        },
        {
          prop: 'rate',
          label: this.$t('reportMod.cateReduceRate') // 品类降本率
        },
        {
          prop: 'categoryReachRate',
          label: this.$t('reportMod.categoryReachRate') // 品类达成率
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 190,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank'), // 排名
          width: 80
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName') // 品类名称
        },
        {
          prop: 'categoryCrAmount',
          label: this.$t('reportMod.cateReduceAmount') // 品类降本金额
        },
        {
          prop: 'belongRange',
          label: this.$t('reportMod.belongRange') // 所在区间
        },
        {
          prop: 'amountRate',
          label: this.$t('reportMod.capitalReductionProportion') // 降本金额占比
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 200,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank') // 排名
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName') // 品类名称
        },
        {
          prop: 'upAmount',
          label: this.$t('reportMod.cateIncreaseAmount') // 品类上涨金额
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 180,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ]
    ]
    this.tableHeader = tableHeaderArray[type - 1]
    // 设置预制查询参数
    this.preFormObj = queryParams
    this.queryParam = queryParams
    // 设置表格名称
    this.tableName = `moreInfo_${type}`
  },
  mounted () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
    console.log(this.$attrs.params)
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    detailView (row) {
      const { type, name, ...queryParams } = this.$attrs.params
      this.$emit('tab-add', {
        title: `${name}` + this.$t('dataConfMod.detail'),
        component: moreInfoDetail,
        name: `moreInfoDetail_${type}`,
        params: { ...this.filterParams, ...queryParams, type, row }
      })
    },
    selectHandle (node, value, scope) {
      const { fullPathId } = node
      scope.fullPathId = fullPathId
    },
    getMaterialCode (node, scope) {
      const { materialCode } = node
      scope.materialCode = materialCode
    },
    comfirmSelect (node, scope) {
      const { categoryId, categoryName } = node
      scope.categoryId = categoryId
      scope.categoryName = categoryName
    },
    getQuerydata (v) {
      this.queryParam = { ...this.queryParam, ...v }
      if (!this.queryParam.organizationTypeCode) {
        this.$message({
          message: this.$t('dataConfMod.msgOrgTypeName'), // 请选择组织类型
          type: 'error'
        })
        return
      }
      if (!this.queryParam.level) {
        this.$message({
          message: this.$t('reportMod.msgSelCateType'), // 请选择品类类型
          type: 'error'
        })
        return
      }
      if (!this.queryParam.year) {
        this.$message({
          message: this.$t('time.msgSelYear'), // 请选择年份
          type: 'error'
        })
        return
      }
      if (!this.queryParam.startMonth) {
        this.$message({
          message: this.$t('reportMod.msgSelStartMonth'), // 请选择开始月份
          type: 'error'
        })
        return
      }
      if (!this.queryParam.endMonth) {
        this.$message({
          message: this.$t('reportMod.msgSelEndMonth'), // 请选择结束月份
          type: 'error'
        })
        return
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_more_info_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
