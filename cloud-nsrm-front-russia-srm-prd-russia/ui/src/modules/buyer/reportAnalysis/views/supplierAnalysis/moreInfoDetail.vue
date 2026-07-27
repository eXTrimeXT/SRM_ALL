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
            :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
            name="scc_base_material_item"
            @close-quicksearch="getMaterialCode"
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
        <!-- 季度 -->
        <template #season="{ scope }">
          <DictSelect
            v-model="scope.season"
            code="SEASON"
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
        :source="purchaseAnalysisApi.querySupplierDetailList"
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
  'SEASON': [
    { id: 1, value: 1, label: i18n.t('time.quarterList[0]') }, // 第一季度
    { id: 2, value: 2, label: i18n.t('time.quarterList[1]') }, // 第二季度
    { id: 3, value: 3, label: i18n.t('time.quarterList[2]') }, // 第三季度
    { id: 4, value: 4, label: i18n.t('time.quarterList[3]') }, // 第四季度
    { id: 5, value: 0, label: i18n.t('reportMod.allYear') } // 全年
  ] }, false)
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
          prop: 'season',
          label: this.$t('time.quarter'), // 季度
          slot: 'season',
          type: 'slot'
        },
        {
          prop: 'year',
          label: this.$t('dataConfMod.year'), // 年份
          slot: 'year',
          type: 'slot'
        }
      ],
      queryParam: {},
      preFormObj: {},
      tableHeader: [
        {
          prop: 'companyName',
          label: this.$t('common.vendorName'), // 供应商名称
          width: 180
        },
        {
          prop: 'companyRegisteredDate',
          label: this.$t('reportMod.registerDate'), // 注册时间
          width: 150
        },
        {
          prop: 'startDate',
          label: this.$t('reportMod.supplierEffectiveTime'), // 供方生效时间
          width: 150
        },
        {
          prop: 'endDate',
          label: this.$t('reportMod.expirationTime'), // 失效时间
          width: 150
        },
        {
          prop: 'dataSources',
          label: this.$t('vendorMod.dataSources'), // 数据来源
          width: 150,
          dataType: 'dict', // 数据类型为字典
          code: 'DATA_SOURCE' // 字典code
        },
        {
          prop: 'organizationName',
          label: this.$t('vendorMod.cooOrg'), // 合作组织
          width: 100
        },
        {
          prop: 'cooperationYear',
          label: this.$t('reportMod.cooperationYear'), // 合作年限
          width: 150
        },
        {
          prop: 'orderAmount',
          label: this.$t('reportMod.cumulativePurAmount'), // 累计采购金额
          width: 120
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName'), // 品类名称
          width: 120
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'), // 物料名称
          width: 120
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'), // 物料编码
          width: 120
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
      if (!this.queryParam.season && this.queryParam.season !== 0) {
        this.$message({
          message: this.$t('time.msgSelQuarter'), // 请选择季度
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
