<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <component
        :is="formWrapperConfig.component"
        :key="formWrapperConfig.prop"
        v-bind="formWrapperConfig.componentProperty"
        v-on="formWrapperConfig.listeners"
      >
        <template
          v-for="item in formSlots"
          #[item.prop]="{ scope }"
        >
          <component
            :is="item.component"
            :key="item.prop"
            v-model="scope[item.prop]"
            v-bind="item.componentProperty"
            v-on="item.listeners"
          />
        </template>
      </component>

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <export-excel
            :page-url="requestUrl"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :timeout="10000000"
          />
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="tableFormGlobal.pageSize"
        :open-custom-table="true"
        :pre-query-data="queryParam"
        :url="requestUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'modb@/basicSetting/views/formPage/components/FormWrapperConfig'
import DynamicForm from './mixins/dynamic-mixins'

export default {
  name: 'FormPageDynamic',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoMixin, tabTodoWatch, DynamicForm],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'tableList',

      queryParam: {},
      formConfig: [],
      dataList: [],
      tableHeader: [],
      defaultTableHeader: [],
      requestUrl: null,

      dictCodes: {},
      columnSlots: [],
      dateFilter: [],
      formQuery: {},
      valueAttr: '',
      defaultValues: {},
      dataLimit: {},
      testValue: 'testvaluesss==ss'
    }
  },
  computed: {
  },
  updated () {
    this.defaultTableHeader = this.tableHeader // 动态表头才在updated中这样写
  },
  created () {
    this.defaultTableHeader = this.tableHeader
  },
  mounted () {
    this.queryDynamicConfig()
  },
  activated () {
    this.doLayout()
  },
  methods: {
    getPageCode () {
      let pageCode = null
      if (this.$attrs && this.$attrs.params) {
        pageCode = this.$attrs.params.pageCode
      }
      if (!pageCode) {
        pageCode = this.$route.params.pageCode
      }
      return pageCode
    },
    doLayout () {
      if (
        this.$refs.table &&
        this.$refs.table.doLayout &&
        typeof this.$refs.table.doLayout === 'function'
      ) {
        this.$refs.table.doLayout()
      }
    }
  }
}
</script>
