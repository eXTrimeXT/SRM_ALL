<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="quickSearchConfig:add"
            @click="addTab"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <ExportExcel
            :filter-params="queryParam"
            :line-array="lineArray"
            type="plain"
            multiple
            timeout="1000000"
            export-mode="front"
            code="quickSearchConfig:export"
          />
          <MImport
            ref="import"
            type="default"
            code="quickSearchConfig:import"
            style="display: inline-block; margin-left: 10px"
            :title="iModal.title"
            :extra-data="extraData"
            :up-load-url="iModal.upLoadUrl"
            :show-success-deal="true"
            @downloadTemplate="downloadTemplate"
          />
          <el-button
            v-if="showLanguageBtn"
            type="default"
            @click="getQuickSearchLanguage"
          >
            <!-- 获取中文语言配置 -->
            {{ $t("bidMod.getZhConf") }}
          </el-button>
          <el-button
            v-if="showLanguageBtn"
            type="default"
            @click="getQuickSearchLanguage('en')"
          >
            <!-- 获取英文语言配置 -->
            {{ $t("bidMod.getEnConf") }}
          </el-button>
        </template>
      </MainHeader>
      <el-container style="padding-left: 0px;min-height:0;" direction="vertical">
        <div style="height:100%;display:flex;flex:1;flex-direction:column;min-height:0;">
          <TableView
            :ref="gridId"
            :table-header="tableHeader"
            :check-change="handleCurrentChange"
            :page-size="pageSize"
            :pre-query-data="queryParam"
            :open-custom-table="true"
            :com-active="$attrs['changeTab']"
            url="/api-base/quicksearch/quicksearchConfig/listPage"
          />
        </div>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import quickSearchConfigDetail from './quickSearchConfigDetail'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { getAllQuickSearchZhLanguage } from 'lib@/utils/getQuickSearchLanguage'
import TableView from 'lib@/components/Table/TableView'
import { downloadFileLink } from 'lib@/utils/file'
import MImport from '@/library/components/import'
import ExportExcel from '@/library/components/export-excel'

export default {
  name: 'QuickSearchConfigList',
  components: {
    MImport,
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'quickSearchConfig',
      tableHeader: [],
      queryParam: {},
      iModal: {
        // Excel导入
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-base/quicksearch/quicksearchConfig/importExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'quickSearch',
        fileType: 'excel'
      },
      pageSize: 15,
      dataCount: 0,
      queryTotal: -1,
      pageIndex: 1,
      currentRow: null,
      tableData: [],
      preArr: [
        {
          prop: 'name',
          label: () => this.$t('bidMod.quickSearchName') // '名称'
        },
        {
          prop: 'description',
          label: () => this.$t('bidMod.quickSearchDescription') // '描述'
        },
        {
          prop: 'queryTable',
          label: () => this.$t('bidMod.quickSearchQueryTable') // '查询表'
        }
      ],
      lineArray: []
    }
  },
  computed: {
    showLanguageBtn () {
      return this.$route.query.showLanguageBtn === 'Y'
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'name',
        label: this.$t('bidMod.quickSearchName'),
        minWidth: 150
      },
      {
        prop: 'description',
        label: this.$t('bidMod.quickSearchDescription'),
        minWidth: 150
      },
      {
        prop: 'queryTable',
        label: this.$t('bidMod.quickSearchQueryTable'),
        minWidth: 150
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // '更新时间',
        minWidth: 150
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            code: 'quickSearchConfig:edit',
            callback: row => this.editTab(row),
            formattor: () => {
              return this.$t('common.edit')
            }
          }
        ]
      }
    ]
    const lineList = []
    lineList.push({
      tableHeader: [
        { prop: 'name', label: () => this.$t('quickSearch.name'), width: 150 },
        { prop: 'description', label: () => this.$t('quickSearch.description'), width: 150 },
        { prop: 'queryMode', label: () => this.$t('quickSearch.queryMode'), width: 150 },
        { prop: 'codeAttr', label: () => this.$t('quickSearch.codeAttr'), width: 150 },
        { prop: 'queryMatchOperator', label: () => this.$t('quickSearch.queryMatchOperator'), width: 150 },
        { prop: 'queryDatasource', label: () => this.$t('quickSearch.queryDatasource'), width: 150 },
        { prop: 'valueAttr', label: () => this.$t('quickSearch.valueAttr'), width: 150 },

        { prop: 'queryModule', label: () => this.$t('quickSearch.queryModule'), width: 150 },
        { prop: 'queryTable', label: () => this.$t('quickSearch.queryTable'), width: 150 },
        { prop: 'queryLanguage', label: () => this.$t('quickSearch.queryLanguage'), width: 150 },
        { prop: 'queryLanguageType', label: () => this.$t('quickSearch.queryLanguageType'), width: 150 },
        { prop: 'queryLanguageCustom', label: () => this.$t('quickSearch.queryLanguageCustom'), width: 150 },
        { prop: 'queryMaxSize', label: () => this.$t('quickSearch.queryMaxSize'), width: 150 },

        { prop: 'selectInputMode', label: () => this.$t('quickSearch.selectInputMode'), width: 150 },
        { prop: 'reflectInputMethodName', label: () => this.$t('quickSearch.reflectInputMethodName'), width: 150 },
        { prop: 'reflectInputClassName', label: () => this.$t('quickSearch.reflectInputClassName'), width: 150 },
        { prop: 'reflectInputPropertyName', label: () => this.$t('quickSearch.reflectInputPropertyName'), width: 150 },

        { prop: 'dialogQueryType', label: () => this.$t('quickSearch.dialogQueryType'), width: 150 },
        { prop: 'dialogEnabled', label: () => this.$t('quickSearch.dialogEnabled'), width: 150 },
        { prop: 'dialogQueryTable', label: () => this.$t('quickSearch.dialogQueryTable'), width: 150 },
        { prop: 'dialogQueryLanguage', label: () => this.$t('quickSearch.dialogQueryLanguage'), width: 150 },
        { prop: 'dialogQueryMaxSize', label: () => this.$t('quickSearch.dialogQueryMaxSize'), width: 150 },
        { prop: 'dialogQueryLanguageCustom', label: () => this.$t('quickSearch.dialogQueryLanguageCustom'), width: 150 },

        { prop: 'selectMode', label: () => this.$t('quickSearch.selectMode'), width: 150 },
        { prop: 'reflectClassName', label: () => this.$t('quickSearch.reflectClassName'), width: 150 },
        { prop: 'reflectMethodName', label: () => this.$t('quickSearch.reflectMethodName'), width: 150 },
        { prop: 'reflectPropertyName', label: () => this.$t('quickSearch.reflectPropertyName'), width: 150 },

        { prop: 'errorMsg', label: () => this.$t('quickSearch.errorMsg'), width: 150 }
      ],
      dictCodes: {},
      exportType: 'ALL',
      pageUrl: '/api-base/quicksearch/quicksearchConfig/listPage',
      fileName: this.$t('quickSearch.ecportFileName')
    })
    lineList.push({
      tableHeader: [
        { prop: 'quicksearchName', label: () => this.$t('quickSearch.quicksearchName'), width: 150 },
        { prop: 'alias', label: () => this.$t('quickSearch.alias'), width: 150 },
        { prop: 'attr', label: () => this.$t('quickSearch.attr'), width: 150 },
        { prop: 'title', label: () => this.$t('quickSearch.title'), width: 150 },
        { prop: 'componentType', label: () => this.$t('quickSearch.componentType'), width: 150 },
        { prop: 'dataType', label: () => this.$t('quickSearch.dataType'), width: 150 },
        { prop: 'queryItemEnabled', label: () => this.$t('quickSearch.queryItemEnabled'), width: 150 },
        { prop: 'codeList', label: () => this.$t('quickSearch.codeList'), width: 150 },
        { prop: 'displayItemEnabled', label: () => this.$t('quickSearch.displayItemEnabled'), width: 150 },
        { prop: 'queryMatchOperator', label: () => this.$t('quickSearch.queryMatchOperator'), width: 150 },
        { prop: 'componentProperty', label: () => this.$t('quickSearch.componentProperty'), width: 150 },
        { prop: 'javaType', label: () => this.$t('quickSearch.javaType'), width: 150 },
        { prop: 'isDialogAttr', label: () => this.$t('quickSearch.isDialogAttr'), width: 150 },
        { prop: 'columnWidth', label: () => this.$t('quickSearch.columnWidth'), width: 150 },
        { prop: 'attrOrder', label: () => this.$t('quickSearch.attrOrder'), width: 150 },
        { prop: 'errorMsg', label: () => this.$t('quickSearch.errorMsg'), width: 150 }
      ],
      dictCodes: {},
      exportType: 'ALL',
      pageUrl: '/api-base/quicksearch/quicksearchConfig/listPageExport',
      fileName: this.$t('quickSearch.lineExportFile')
    })
    this.lineArray = lineList
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuickSearchLanguage (type) {
      getAllQuickSearchZhLanguage(type)
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-file/files-anon/file/fileupload/downloadTemplate/QUICKSEARCH_IMPORT',
        this.$t('quickSearch.importTemp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 新增
    addTab () {
      this.$emit('tab-add', {
        component: quickSearchConfigDetail,
        params: { flag: 'add' },
        title: () => this.$t('bidMod.addQuickSearch'), // '快速查询新增',
        name: 'quickSearchConfigDetail'
      })
    },
    editTab (row) {
      this.$emit('tab-add', {
        component: quickSearchConfigDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.name,
        name: 'quickSearchConfigDetail' + row.name
      })
    },

    deleteOne (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let rmList = [{ id: row.quicksearchConfigId }]
          this.$http({
            url:
              '/api-base/quicksearch/quicksearchConfig/removeConfig',
            method: 'POST',
            data: { params: JSON.stringify(rmList) },
            loading: true
          })
            .then(() => {
              this.$message({
                message: this.$t('common.successDelete'), // '删除成功',
                type: 'success'
              })
              this.getQuerydata()
            })
        })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
