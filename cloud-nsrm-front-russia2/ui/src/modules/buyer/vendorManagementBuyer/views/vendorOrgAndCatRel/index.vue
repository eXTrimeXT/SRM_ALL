<template>
  <el-container
    class="flex-container-notab the_vendorOrgAndCatRel_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <!-- <main-header>
        <template slot="left">
        </template>
      </main-header> -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup/vendorOrgCategory/listAllByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
export default {
  name: 'VendorOrgAndCatRel',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'vendorOrgAndCatRel',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'vendorOrgAndCatRel',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      queryForm: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('vendorMod.cooCategory'), // '合作品类'
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'categoryServiceStatus',
          label: () => this.$t('vendorMod.catServiceStatus'), // '品类状态'
          type: 'dict', // 字典类型
          code: 'CATEGORY_STATUS' // 字典code
        },
        {
          prop: 'orgId',
          label: () => this.$t('vendorMod.cooOrg'), // '合作组织'
          type: 'OUorganizationSelector',
          placeholder: () => this.$t('common.msgSelectOrg') // '请选择组织'
        },
        {
          prop: 'orgServiceStatus',
          label: () => this.$t('vendorMod.orgServiceStatus'), // '组织状态'
          type: 'dict', // 字典类型
          code: 'ORG_STATUS' // 字典code
        }
      ],
      queryParam: {},
      approveStatus: [],
      catStatus: [],
      orgStatus: []
    }
  },
  created () {
    let _this = this
    _this.tableHeader = [
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        width: 100
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        width: 150
      },
      {
        prop: 'orgName',
        label: () => this.$t('vendorMod.cooOrg'), // '合作组织'
        width: 120
      },
      {
        prop: 'orgServiceStatus',
        label: () => this.$t('vendorMod.orgServiceStatus'), // '组织状态'
        width: 80,
        dataType: 'dict', // 数据类型为字典
        code: 'ORG_STATUS' // 字典code
      },
      {
        prop: 'orgStartDate',
        label: () => this.$t('vendorMod.orgStartDate'), // '组织生效日期'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'orgEndDate',
        label: () => this.$t('vendorMod.orgEndDate'), // '组织失效日期'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'categoryName',
        label: () => this.$t('vendorMod.cooCategory'), // '合作品类'
        width: 100
      },
      {
        prop: 'categoryFullName',
        label: () => this.$t('vendorMod.cooCategoryFullName'), // '品类全称'
        width: 100
      },
      {
        prop: 'categoryServiceStatus',
        label: () => this.$t('vendorMod.catServiceStatus'), // '品类状态'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'CATEGORY_STATUS' // 字典code
      },
      {
        prop: 'categoryStartDate',
        label: () => this.$t('vendorMod.categoryStartDate'), // '品类生效日期'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'categoryEndDate',
        label: () => this.$t('vendorMod.categoryEndDate'), // '品类失效日期'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorOrgAndCatRel_wrapper {
}
</style>
