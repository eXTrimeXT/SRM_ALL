<template>
  <el-container
    class="flex-container questSupplier_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left" />
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/quest/questSupplier/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import questSupplierResultView from '../questSupplier/questSupplierResultView'
import { parseTime, adaptDictData } from '@/utils'

export default {
  name: 'QuestSupplierForExternalList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'questSupplierForExternalList',
      tableName: 'questSupplierTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      curRole: this.$store.getters.userType, // vendor buyer
      tableHeader: [
        {
          prop: 'questNo',
          label: () => this.$t('quest.questNo'), // '调查表编号'
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.questSupplierResultView(row)
          }.bind(this)
        },
        {
          prop: 'questName',
          label: () => this.$t('quest.questName'), // '调查表名称'
          width: 150
        },
        {
          prop: 'approvalStatus',
          label: () => this.$t('quest.approvalStatus'), // '调查表状态'
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'QUEST_SUPPLIER_APPROVE_STATUS' // 字典code
        },
        {
          prop: 'companyCode',
          label: () => this.$t('quest.companyCode'), // '供应商编码'
          width: 150
        },
        {
          prop: 'companyName',
          label: () => this.$t('quest.companyName'), // '供应商名称'
          minWidth: 150
        },
        {
          prop: 'questTemplateOrgName',
          label: () => this.$t('quest.questTemplateOrgName'), // '业务组织'
          width: 200
        },
        {
          prop: 'questTemplateCode',
          label: () => this.$t('quest.questTemplateCode'), // '调查模板编码'
          width: 100
        },
        {
          prop: 'questTemplateName',
          label: () => this.$t('quest.questTemplateName'), // '调查表模板名称'
          minWidth: 150
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('quest.createdFullName'), // '创建人'
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('quest.creationDate'), // '创建时间'
          width: 150
        }
      ],
      filterConfig: [
        { prop: 'questNoForQuery', label: '调查表编号' },
        { prop: 'questTemplateOrgId', label: '业务组织', type: 'OUorganizationSelector' }
      ],
      queryParam: {},
      approveStatusList: [] // 审批状态
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params = {}) {
      const { extraQueryParams } = this.$attrs.params
      this.queryParam = { ...params, ...extraQueryParams, orgCondition: 'Y' }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    questSupplierResultView (row) {
      this.$emit('tab-add', {
        component: questSupplierResultView,
        params: {
          flag: 'view',
          row: row,
          tabName: 'questSupplierResultView' + row.questSupId
        },
        title: '调查表' + row.questNo,
        name: 'questSupplierResultView' + row.questSupId
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
