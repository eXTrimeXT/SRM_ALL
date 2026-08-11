<template>
  <el-container class="flex-container black_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="modelConfig:add" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :source="modelConfig.listPage"
      />
    </el-main>
    <el-dialog
      :title="$t('common.copy')"
      :visible.sync="dialogFormVisible"
      :before-close="copyCancel"
    >
      <el-form
        ref="copyForm"
        :model="copyModel"
        :rules="copyModelRules"
      >
        <el-row :gutter="32">
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.menuCode')"
              prop="pageCode"
            >
              <el-input v-model="copyModel.pageCode" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.menuName')"
              prop="pageName"
            >
              <el-input v-model="copyModel.pageName" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="copyCancel">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmAdd"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import modelConfigEdit from './edit.vue'
import { downloadFileLink } from 'lib@/utils/file'
import { modelConfig } from '@/api/modelConfig'

export default {
  name: 'ModelConfig',
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
      modelConfig: modelConfig,
      copyModelRules: {
        dimName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        dimCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        pageCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        pageName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      copyModel: {
        formPageId: '',
        pageCode: '',
        pageName: ''
      },
      name: 'modelConfig',
      tableName: 'blackTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {
        approveStatus: 'APPROVE_STATUS_TYPE'
      },
      filterParams: {},
      tableHeader: [],
      filterConfig: [
        { prop: 'pageName', label: () => this.$t('dataConfMod.menuName') }
      ],
      queryParam: {},
      dialogFormVisible: false
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'pageCode',
        label: () => this.$t('dataConfMod.menuCode')
      },
      {
        prop: 'pageName',
        label: () => this.$t('contractMod.menuName'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            code: 'modelConfig:add',
            callback: row => this.copyHandle(row.formPageId),
            formattor: () => {
              return this.$t('common.copy')
            }
          },
          {
            code: 'modelConfig:edit',
            callback: row => this.editHandle(row),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            code: 'modelConfig:delete',
            callback: row => this.deleteHandle(row),
            formattor: () => {
              return this.$t('common.delete')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    copyCancel () {
      this.dialogFormVisible = false
      this.copyModel = {}
    },
    confirmAdd () {
      this.$refs.copyForm.validate(() => {
        let data = {
          formPageId: this.copyModel.formPageId,
          pageCode: this.copyModel.pageCode,
          pageName: this.copyModel.pageName
        }
        modelConfig.copyModel(data).then(res => {
          this.$message.success(res.message)
          this.dialogFormVisible = false
          this.getQuerydata()
        })
      })
    },
    dolayout () {
      this.$refs[this.gridId].query()
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/black/exportExcelTemplate',
        this.$t('drawingshead.drawingImportTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.creationStartDate = dateList[0]
        params.creationEndDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.queryParam.pageModel = 'MODEL_CONFIG'
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          modelConfig.deleteModel(row.formPageId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: modelConfigEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode,
          tabName: 'modelConfigEdit'
        },
        title: this.$t('dataConfMod.modelConfigAdd'),
        name: 'modelConfigEdit'
      }
      this.$emit('tab-add', tab)
    },
    copyHandle (row) {
      this.dialogFormVisible = true
      this.copyModel = {
        formPageId: row
      }
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: modelConfigEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode,
          tabName: 'modelConfigEdit' + row.formPageId
        },
        title: row.pageName,
        name: 'modelConfigEdit' + row.formPageId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
