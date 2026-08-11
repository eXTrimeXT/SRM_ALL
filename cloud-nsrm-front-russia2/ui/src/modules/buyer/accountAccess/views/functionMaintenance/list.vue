<template>
  <el-container class="flex-container-notab the_functionMaintenance_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="rbac:functionMaintenance:add"
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-rbac/function/listPage"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="{}"
            :filter-params="queryParam"
            :title="$t('components.eio.customExport')"
            code=""
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :auto-query="false"
        url="/api-rbac/function/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { functionApi } from 'modb@/accountAccess/api'
import ExportExcel from 'lib@/components/export-excel'
import edit from './edit'

export default {
  name: 'FunctionMaintenance',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },

  data () {
    return {
      gridId: 'functionList',
      curOpt: 'add',
      pageSize: 15,
      tableData: [],
      tableHeader: [],
      queryParam: {},
      preArr: [
        {
          prop: 'functionCode',
          label: () => this.$t('dataConfMod.functionCode') // '功能编码'
        },
        {
          prop: 'functionName',
          label: () => this.$t('dataConfMod.functionName') // '功能名称'
        },
        {
          prop: 'functionAddress',
          label: () => this.$t('dataConfMod.functionAddress') // '功能地址'
        },
        {
          prop: 'functionDesc',
          label: () => this.$t('dataConfMod.functionDesc') // '功能描述'
        },
        {
          prop: 'startDate',
          label: () => this.$t('dataConfMod.startDate'), // '生效日期'
          type: 'date'
        }
      ]
    }
  },

  created () {
    this.tableHeader = [
      {
        prop: 'functionCode',
        label: () => this.$t('dataConfMod.functionCode'), // '功能编码'
        width: 150
      },
      {
        prop: 'functionName',
        label: () => this.$t('dataConfMod.functionName'), // '功能名称'
        width: 150
      },
      {
        prop: 'functionAddress',
        label: () => this.$t('dataConfMod.functionAddress'), // '功能地址'
        width: 150
      },
      {
        prop: 'functionDesc',
        label: () => this.$t('dataConfMod.functionDesc') // '功能描述'
      },
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'), // '生效日期'
        width: 150,
        editType: 'none',
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'), // '失效日期'
        editType: 'none',
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // '编辑'
          {
            callback: row => this.editOne(row, 'edit'),
            code: 'rbac:functionMaintenance:edit',
            formattor: () => this.$t('common.edit')
          }
        ]
      }
    ]
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
    // 新增
    addOne () {
      this.curOpt = 'add'
      let tab = {
        component: edit,
        ctrlHeight: true,
        params: {
          flag: 'add',
          tabName: 'functionMaintenanceEdit'
        },
        title: this.$t('dataConfMod.addFuntion'),
        name: 'functionMaintenanceEdit'
      }
      this.$emit('tab-add', tab)
    },

    // 编辑
    editOne (row, flag) {
      this.curOpt = flag
      let tab = {
        component: edit,
        ctrlHeight: true,
        params: {
          flag: flag,
          row,
          tabName: 'functionMaintenanceEdit' + row.functionCode + flag
        },
        title: row.functionName,
        name: 'functionMaintenanceEdit' + row.functionCode + flag
      }
      this.$emit('tab-add', tab)
    },

    // 删除数据
    deleteOneItem (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let id = row.functionId
        functionApi.functionDel({ id }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
      })
    },
  }
}
</script>
