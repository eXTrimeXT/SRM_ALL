<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      >
        <template #orgName="{ scope }">
          <OrganizationSelector
            ref="ouSelector"
            v-model="scope.organizationName"
            :parent-id="-1"
            node-type="OU"
            :placeholder="$t('common.pleaseSelect')"
            @select="(val) => selectHandler(val, scope)"
          />
        </template>
        <template #reviewYear="{ scope }">
          <el-date-picker
            v-model="scope.reviewYear"
            type="year"
            value-format="yyyy"
            :placeholder="$t('supplierRating.selectYear')"
          />
        </template>
      </FormWrapper>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        url="/api-pef/perf/levellinescore/listPageByParam"
        @afterQuery="afterQueryData"
      />
      <srm-dialog
        :visible.sync="detailViewVisible"
        :title="$t('route.graderRating')"
        :close-on-click-modal="false"
        size="large"
      >
        <el-table
          :data="graderRatingData"
          style="width: 100%"
        >
          <el-table-column
            type="index"
            width="50"
          />
          <el-table-column
            prop="projectName"
            :label="$t('supplierRating.projectName')"
            width="140"
          />
          <el-table-column
            prop="companyName"
            :label="$t('supplierRating.supplierName')"
            width="140"
          />
          <el-table-column
            prop="organizationName"
            :label="$t('supplierRating.entity')"
          />
          <el-table-column
            prop="perStartMonth"
            :label="$t('supplierRating.perfStartMonth')"
            width="140"
          />
          <el-table-column
            prop="perEndMonth"
            :label="$t('supplierRating.perfEndMonth')"
            width="140"
          />
          <el-table-column
            prop="categoryName"
            :label="$t('supplierRating.category')"
          />
          <el-table-column
            prop="scoreAttribute1"
            :label="$t('supplierRating.averageScore')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute2"
            :label="$t('supplierRating.averageCostScore')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute3"
            :label="$t('supplierRating.averageDeliveryScore')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute4"
            :label="$t('supplierRating.averageServiceScore')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute5"
            :label="$t('supplierRating.averageTechnicalScore')"
            width="140"
          />
          <el-table-column
            prop="score"
            :label="$t('supplierRating.meanCompositeScore')"
            width="140"
          />
          <el-table-column
            prop="levelName"
            :label="$t('supplierRating.performanceRating')"
          >
            <template slot-scope="scope">
              {{ $getDictLabel('VENDOR_LEVEL', scope.row.levelName) }}
            </template>
          </el-table-column>
          <el-table-column
            prop="rank"
            :label="$t('perfMod.rankAll')"
          />
        </el-table>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import OrganizationSelector from 'lib@/components/organization-selector'
import ExportExcel from 'lib@/components/export-excel'
import { hierarchicalRatingApi } from 'modb@/vendorHierarchicalManagement/api'

export default {
  name: 'HierarchicalResults',
  components: {
    FormWrapper,
    TableView,
    MainHeader,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      detailViewVisible: false,
      graderRatingData: [],
      gridId: 'hierarchicalResults',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      pageSize: 15,
      username: '',
      preArr: [
        {
          prop: 'orgName',
          label: () => this.$t('supplierRating.entity'),
          type: 'slot',
          slot: 'orgName'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('supplierRating.heading'),
          type: 'catSelect'
        },
        {
          prop: 'reviewYear',
          label: () => this.$t('supplierRating.assessmentYear'),
          type: 'slot',
          slot: 'reviewYear'
        },
        {
          prop: 'companyName',
          label: () => this.$t('supplierRating.supplierName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'companyCode',
          label: () => this.$t('supplierRating.supplierCode'),
          type: 'quicksearch',
          showKey: 'companyCode',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'version',
          label: () => this.$t('perfMod.version')
        },
        {
          prop: 'createdBy',
          label: () => this.$t('supplierRating.creator')
        },
        {
          prop: 'vendorLevel',
          label: () => this.$t('supplierRating.classificationSuppliers'),
          type: 'dict',
          code: 'VENDOR_LEVEL'
        },

        {
          prop: 'status',
          label: () => this.$t('supplierRating.status'),
          type: 'dict',
          code: 'VALID_STATUS'
        }
      ]
    }
  },
  created () {
    this.username = this.$store.getters.username || ''
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        label: _this.$t('supplierRating.entity'),
        type: 'OUorganizationSelector',
        width: 120
      },
      {
        prop: 'reviewYear',
        label: _this.$t('supplierRating.assessmentYear'),
        width: 120
      },
      {
        prop: 'version',
        label: _this.$t('perfMod.version'),
        width: 120
      },
      { prop: 'categoryFullName', label: _this.$t('supplierRating.heading'), width: 240 },

      { prop: 'companyCode', label: _this.$t('supplierRating.vendorCode'), width: 140 },
      { prop: 'companyName', label: _this.$t('supplierRating.supplierName'), width: 140 },
      {
        prop: 'vendorLevel',
        label: _this.$t('supplierRating.classificationSuppliers'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL' // 字典code
      },
      {
        prop: 'dataSource',
        label: _this.$t('vendorMod.dataSources'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'LEVEL_DATA_SOURCE' // 字典code
      },
      {
        prop: 'systemSourceResult',
        label: _this.$t('supplierRating.sourceResults'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL' // 字典code
      },
      {
        prop: 'perFormance',
        label: _this.$t('supplierRating.detailsReview'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.perFormanceDeatil(row),
        formattor () {
          return _this.$t('dataConfMod.detail') // '编辑'
        }
      },
      {
        prop: 'status',
        label: _this.$t('supplierRating.significantCondition'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VALID_STATUS' // 字典code
      },
      {
        prop: 'currentStatus',
        label: _this.$t('supplierRating.currentDemand'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'IS_VALID' // 字典code
      },
      {
        prop: 'orderNo',
        label: _this.$t('supplierRating.classificationNumber'),
        width: 120
      },
      {
        prop: 'projectName',
        label: _this.$t('supplierRating.gradedName'),
        width: 120
      },

      {
        prop: 'auditCode',
        label: _this.$t('supplierRating.approvalNumber'),
        width: 120
      },
      // {
      //   prop: "auditStatus",
      //   label: "审批状态",
      //   formattor (val) {
      //     return _this.$getDictLabelByValue(_this.LEVEL_APPROVAL_STATUS, val);
      //   }
      // },
      {
        prop: 'createdBy',
        label: _this.$t('supplierRating.creator'),
        width: 120
      },
      {
        prop: 'creationDate',
        label: _this.$t('supplierRating.creationDate'),
        width: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'auditBy',
        label: _this.$t('supplierRating.approver'),
        width: 120
      },
      {
        prop: 'auditDate',
        label: _this.$t('supplierRating.approvalTime'),
        width: 120,
        dataType: 'dateTime'
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  activated () {
    this.dolayout()
  },

  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    perFormanceDeatil (row) {
      let data = {
        reviewYear: row.reviewYear || '',
        orgId: row.orgId || '',
        categoryId: row.categoryId || '',
        companyId: row.companyId || '',
        levelHeadId: row.levelHeadId || ''
      }
      hierarchicalRatingApi.listPageOverallScoreByParam(data).then((res) => {
        this.graderRatingData = res.data.list || []
      })
      this.detailViewVisible = true
    },
    afterQueryData (data) {
      console.log('[data]', data)
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.queryParam.approvalStatus = 'APPROVAL'
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      console.log('[tableData]', this.tableData)
    },
    selectHandler (val, scope) {
      scope.orgName = val.organizationName || ''
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'REQUIREMENT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    async getWorkflowBusinessVariables () {
      // 定义流程变量，如果没有可以不添加
      return {
        Amount: this.requirementHead.ceeaTotalBudget
      }
    }
  }
}
</script>
<style lang="scss" scoped></style>
