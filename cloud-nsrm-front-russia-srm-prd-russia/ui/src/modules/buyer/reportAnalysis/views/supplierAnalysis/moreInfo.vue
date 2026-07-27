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
    return {
      dictClass: dictClass,
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
      tableHeader: []
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    const { type, name, ...queryParams } = this.$attrs.params
    // 获取列表接口
    const sourceArray = [
      'querySupplierCooperation',
      'queryPurchaseAmount',
      'queryPurchaseAmount',
      'querySupplierGrade',
      'queryCategorySupplier'
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
      ]
    }
    // 设置列表表头
    const tableHeaderArray = [
      [
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
          prop: 'cooperationYear',
          label: this.$t('reportMod.cooperationYear'), // v
          width: 150
        },
        {
          prop: 'dataSources',
          label: this.$t('basicPrice.dataSource'), // 数据来源
          width: 150,
          dataType: 'dict', // 数据类型为字典
          code: 'DATA_SOURCE' // 字典code

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
          prop: 'no',
          label: this.$t('bidMod.rank'), // 排名
          width: 80
        },
        {
          prop: 'companyName',
          label: this.$t('common.vendorName') // 供应商名称
        },
        {
          prop: 'organizationName',
          label: this.$t('vendorMod.cooOrg') // 合作组织
        },
        {
          prop: 'categoryName',
          label: this.$t('vendorMod.cooCategory') // 合作品类
        },
        {
          prop: 'orderAmount',
          label: this.$t('reportMod.cumulativePurAmount'), // 累计采购金额
          width: 120
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
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
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank'), // 排名
          width: 80
        },
        {
          prop: 'companyName',
          label: this.$t('common.vendorName') // 供应商名称
        },
        {
          prop: 'organizationName',
          label: this.$t('vendorMod.cooOrg') // 合作组织
        },
        {
          prop: 'categoryName',
          label: this.$t('vendorMod.cooCategory') // 合作品类
        },
        {
          prop: 'orderAmount',
          label: this.$t('reportMod.cumulativePurAmount'), // 累计采购金额
          width: 100
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
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
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank'), // 排名
          width: 80
        },
        {
          prop: 'companyName',
          label: this.$t('common.vendorName'), // 供应商名称
          minWidth: 180
        },
        {
          prop: 'score',
          label: this.$t('perfMod.perScore'), // 绩效得分
          width: 100
        },
        {
          prop: 'perStartMonth',
          label: this.$t('perfMod.perStartMonth'), // 绩效开始月份
          width: 150
        },
        {
          prop: 'perEndMonth',
          label: this.$t('perfMod.perEndMonth'), // 绩效结束月份
          width: 150
        },
        {
          prop: 'indicatorLineDes',
          label: this.$t('perfMod.indicatorScoreTip'), // 指标评分值
          width: 120
        },
        {
          prop: 'categoryName',
          label: this.$t('perfMod.categoryId'), // 采购品类
          width: 100
        },
        {
          prop: 'organizationName',
          label: this.$t('vendorMod.cooOrg'), // 合作组织
          minWidth: 180
        },
        {
          prop: 'evalutionDate',
          label: this.$t('perfMod.evalutionDate'), // 评分时间
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
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
      ],
      [
        {
          prop: 'no',
          label: this.$t('bidMod.rank') // 排名
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName'), // 品类名称
          width: 100
        },
        // {
        //   prop: "materialName",
        //   label: "物料名称",
        // },
        {
          prop: 'companyName',
          label: this.$t('reportMod.cooperteSupplierName'), // 合作供应商名称
          minWidth: 180
        },
        {
          prop: 'cooperationVendorNum',
          label: this.$t('reportMod.cooperationVendorNum'), // 合作供应商数
          minWidth: 150
        },
        {
          prop: 'belongRange',
          label: this.$t('reportMod.categoryRange'), // 品类所在区间
          minWidth: 180
        },
        {
          prop: 'belongRangePercent',
          label: this.$t('reportMod.belongRangePercent'), // 所在区间占比
          minWidth: 180,
          formattor: val => {
            return Number(val * 100).toFixed(1) + '%'
          }
        },
        {
          prop: 'outVendorNum',
          label: this.$t('reportMod.outVendorNum'), // 退出供应商数
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
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
