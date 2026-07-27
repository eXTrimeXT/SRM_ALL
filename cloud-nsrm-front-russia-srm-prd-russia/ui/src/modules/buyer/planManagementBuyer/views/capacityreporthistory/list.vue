<template>
  <el-container
    class="flex-container capacityreporthistory_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <!-- <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
        </template>
      </main-header> -->
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="capacityreporthistory.list"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CategoryCascader from 'lib@/components/category-cascader'
import { capacityreporthistory } from 'modb@/planManagementBuyer/api/index'

export default {
  name: 'CapacityreporthistoryList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    CategoryCascader
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      capacityreporthistory,
      pageSize: 15,
      gridId: 'list',
      preFormObj: {},
      currentRows: [],
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'materialCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          width: 100
        },
        {
          prop: 'materialName',
          label: this.$t('supplierCapacityReport.materialName'),
          width: 100
        },
        {
          prop: 'vendorCode',
          label: this.$t('supplierCapacityReport.vendorCode'),
          width: 100
        },
        {
          prop: 'vendorName',
          label: this.$t('supplierCapacityReport.vendorName'),
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('supplierCapacityReport.categoryName'),
          width: 100
        },
        {
          prop: 'productionDayNumber',
          label: this.$t('supplierCapacityReport.productionDayNumber'),
          width: 100
        },
        {
          prop: 'monthlyWorkDays',
          label: this.$t('supplierCapacityReport.monthlyWorkDays'),
          width: 100
        },
        {
          prop: 'productionMonthNumber',
          label: this.$t('supplierCapacityReport.productionMonthNumber'),
          width: 100
        },
        {
          prop: 'monthTotalCapacity',
          label: this.$t('supplierCapacityReport.monthTotalCapacity'),
          width: 100
        },
        {
          prop: 'isNot',
          label: this.$t('supplierCapacityReport.isNot'),
          width: 100
        },
        {
          prop: 'startTime',
          label: this.$t('supplierCapacityReport.startTime'),
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          },
          width: 100
        },
        {
          prop: 'endTime',
          label: this.$t('supplierCapacityReport.endTime'),
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          },
          width: 100
        },
        {
          prop: 'createdBy',
          label: this.$t('supplierCapacityReport.createdBy'),
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: this.$t('supplierCapacityReport.lastUpdatedBy'),
          width: 100
        },
        {
          prop: 'lastUpdateDate',
          label: this.$t('supplierCapacityReport.lastUpdateDate'),
          width: 100
        }
      ],

      filterConfig: [],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })

    // 采购
    if (this.$store.getters.userType == 'BUYER') {
      this.filterConfig = [
        {
          prop: 'materialCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },

        {
          prop: 'vendorName',
          label: this.$t('supplierCapacityReport.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          type: 'quicksearch',
          showKey: 'categoryName',
          name: 'scc_base_purchase_category4'
        }
      ]
    }

    // 供应商
    if (this.$store.getters.userType == 'VENDOR') {
      this.filterConfig = [
        {
          prop: 'materialCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_sup_auth_purchase_catalog'
        },

        {
          prop: 'categoryName',
          label: this.$t('supplierCapacityReport.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        }
      ]
    }
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    categoryTreeSelectChange (nodes, value) {
      this.$set(this.queryParam, 'categoryId', nodes.categoryId)
      this.$set(this.queryParam, 'categoryName', nodes.categoryName)
      console.log(this.queryParam, 'this.queryParam')
    },
    getQuerydata (v) {
      // Object.assign(this.queryParam, {
      //   ...params
      // })
      let query = v || this.preFormObj
      this.queryParam = query
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
