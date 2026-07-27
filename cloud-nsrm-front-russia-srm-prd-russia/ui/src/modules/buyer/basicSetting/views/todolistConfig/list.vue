<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />

      <MainHeader>
        <template slot="left">
          <el-button type="primary" @click="addTab">
            {{ $t("common.add") }}
          </el-button>

          <ExportExcel
            :filter-params="queryParam"
            :title="$t('components.eio.customExport')"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :page-url="tableViewUrl"
            timeout="1000000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        ref="tableView"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import todolistConfigHttp from 'modb@/basicSetting/api/todolistConfig'
import todolistDetail from './edit'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'

const formattorConfigType = (type) => {
  const map = new Map([
    ['WAIT_FORM', '单据待办'],
    ['DONE_FORM', '单据已办'],
    ['MY_START', '我启动'],
    ['WAIT_FLOW', '待审批-流程'],
    ['WAIT_READ', '待阅'],
    ['READED', '已阅']
  ])

  return map.get(type) || type
}

const formattorConfigFrom = (type) => {
  const map = new Map([
    ['PRODUCT', '产品'],
    ['PROJECT', '项目']
  ])

  return map.get(type) || type
}

export default {
  name: 'TodolistConfigList',

  components: {
    MImport,
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableHeader: [
        // 唯一编码
        {
          prop: 'uniqueCode',
          label: this.$t('todolistConfig.uniqueCode'),
          minWidth: 150
        },
        // 配置编码
        {
          prop: 'configCode',
          label: this.$t('todolistConfig.configCode'),
          minWidth: 150
        },
        // 配置名称
        {
          prop: 'configName',
          label: this.$t('todolistConfig.configName'),
          minWidth: 150
        },
        // 配置类型
        {
          prop: 'configType',
          label: () => this.$t('todolistConfig.configType'),
          minWidth: 150,
          formattor: val => formattorConfigType(val)
        },
        // 配置来源
        {
          prop: 'configFrom',
          label: () => this.$t('todolistConfig.configFrom'),
          minWidth: 150,
          // 没有字典
          formattor: val => formattorConfigFrom(val)
        },
        // 配置版本
        {
          prop: 'configVersion',
          label: () => this.$t('todolistConfig.configVersion'),
          minWidth: 150
        },
        // 配置状态
        {
          prop: 'configStatus',
          label: () => this.$t('todolistConfig.configStatus'),
          minWidth: 150,
          formattor: val => this.$getDictLabel('YES_OR_NO', val)
        },
        // 角色编码
        {
          prop: 'roleCode',
          label: () => this.$t('todolistConfig.roleCode'),
          minWidth: 150
        },
        // 配置维度
        {
          prop: 'dimension',
          label: () => this.$t('todolistConfig.dimension'),
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
            // 编辑
            {
              callback: row => this.editTab(row),
              formattor: () => this.$t('common.edit')
            }
          ]
        }
      ],
      queryParam: {},
      dictCodes: {
        configStatus: 'YES_OR_NO'
      },
      tableData: [],
      preArr: [
        // '配置编码'
        {
          prop: 'configCodeLike',
          label: () => this.$t('todolistConfig.configCode')
        },
        // '配置名称'
        {
          prop: 'configNameLike',
          label: () => this.$t('todolistConfig.configName')
        },
        // '配置状态'
        {
          prop: 'configStatus',
          label: () => this.$t('todolistConfig.configStatus'),
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ],
      tableViewUrl: todolistConfigHttp.listPageUrl
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (params = {}) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs.tableView.query()
      })
    },

    /* 新增 */
    addTab () {
      this.$emit('tab-add', {
        component: todolistDetail,
        ctrlHeight: true,
        params: {
          flag: 'add',
          tabName: 'todolistDetail'
        },
        title: () => this.$t('dataConfMod.addtodoQuery'),
        name: 'todolistDetail'
      })
    },

    /* 编辑 */
    editTab (row) {
      let uniqueCode = row.uniqueCode
      this.$emit('tab-add', {
        component: todolistDetail,
        ctrlHeight: true,
        params: {
          flag: 'edit',
          row: row,
          tabName: `todolistDetail${uniqueCode}`
        },
        title: uniqueCode,
        name: `todolistDetail${uniqueCode}`
      })
    }
  }
}
</script>
