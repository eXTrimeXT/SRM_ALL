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
            ref="selector"
            :key="scope.organizationTypeCode"
            v-model="scope.organizationId"
            :parent-id="-1"
            :placeholder="$t('common.pleaseSelect')"
            :node-type="scope.organizationTypeCode"
            :scope="scope"
            @select="selectHandle"
          />
        </template>
        <!-- 品类 -->
        <template #categoryId="{ scope }">
          <CCategorySelect
            v-model="scope.categoryName"
            :scope="scope"
            :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
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
            :dict-class="dictClass"
          />
        </template>
        <!-- 结束月份 -->
        <template #endMonth="{ scope }">
          <DictSelect
            v-model="scope.endMonth"
            code="MONTH"
            :dict-class="dictClass"
          />
        </template>
      </FormWrapper>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :source="purchaseAnalysisApi.queryCostReductionDetail"
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
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { purchaseAnalysisApi } from 'modb@/orderManagementBuyer/api'
import { geti18n } from '@/main'
const i18n = geti18n()
const dictClass = createDictClass({
  'MONTH': [
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
  'SEASON': [
    { id: 1, value: 1, label: i18n.t('time.quarterList[0]') },
    { id: 2, value: 2, label: i18n.t('time.quarterList[1]') },
    { id: 3, value: 3, label: i18n.t('time.quarterList[2]') },
    { id: 4, value: 4, label: i18n.t('time.quarterList[3]') }
  ]
}, false)

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
    const { type } = this.$attrs.params
    const dateMap = [
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.create'),
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.warehousing')
    ]
    const name = dateMap[type - 1]
    return {
      dictClass: dictClass,
      tableName: 'moreInfoDetail',
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
      ],
      queryParam: {},
      preFormObj: {},
      tableHeader: [
        {
          prop: 'companyName',
          label: this.$t('common.vendorName'), // 供应商名称
          minWidth: 180
        },
        {
          prop: 'organizationName',
          label: this.$t('vendorMod.cooOrg'), // 合作组织
          minWidth: 180
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName'), // 品类名称
          minWidth: 180
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'), // 物料名称
          minWidth: 180
        },
        {
          prop: 'materialUpAmount',
          label: this.$t('reportMod.materialIncreaseAmount'), // 物料上涨金额
          minWidth: 150
        },
        {
          prop: 'materialCrAmount',
          label: this.$t('reportMod.materialReduceAmount'), // 物料降本金额
          minWidth: 150
        },
        {
          prop: 'materialCrRate',
          label: this.$t('reportMod.materialReduceRate'), // 物料降本率
          minWidth: 150
        },
        {
          prop: 'categoryCrAmount',
          label: this.$t('reportMod.cateReduceAmount'), // 品类降本金额
          minWidth: 150
        },
        {
          prop: 'categoryReachRate',
          label: this.$t('reportMod.categoryReachRate'), // 品类达成率
          minWidth: 150
        },
        {
          prop: 'confirmTime',
          label: this.$t('orderMod.warehouseTime'), // 入库时间
          minWidth: 150,
          dataType: 'dateTime'
        }
      ]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    const { type, name, row, ...queryParams } = this.$attrs.params
    const { season, year, ...restParams } = queryParams
    const seasonMap = [['01', '03'], ['04', '06'], ['07', '09'], ['10', '12']]
    // 设置预制查询参数
    const preParams = {
      type,
      ...restParams,
      ...queryParams
    }
    if (type === 5) {
      preParams.categoryId = row.categoryId || ''
    }
    this.preFormObj = preParams
    this.queryParam = preParams
  },
  mounted () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
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
