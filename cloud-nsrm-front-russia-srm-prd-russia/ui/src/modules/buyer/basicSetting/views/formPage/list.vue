<template>
  <el-container
    class="flex-container formpage_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="queryScene"
        :form-array="filterConfig"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            v-if="pageModel === 'COMMON'"
            type="primary"
            code="formPageSceneFileCol:add"
            @click="addHandle(formPageEditCommon)"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            v-if="pageModel === 'REPORT'"
            type="primary"
            @click="addHandle(formPageEditReport, 'REPORT')"
          >
            {{ $t('common.add') }}{{ $t('route.reportSetting') }}
          </AuthorityButton>
          <AuthorityButton
            v-if="pageModel === 'SCENE_ATTACHMENT'"
            type="primary"
            @click="addHandle(formPageEditReport, 'SCENE_ATTACHMENT')"
          >
            {{ $t('common.add') }}{{ $t('dataConfMod.configuringScenario') }}
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
        :source="formPageAPI.list"
        :com-active="$attrs['changeTab']"
      />
    </el-main>

    <el-dialog
      :title="$t('common.copy')"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="copyForm"
        :model="copyModel"
        :rules="copyModelRules"
      >
        <el-row :gutter="32">
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.pageCode')"
              prop="pageCode"
            >
              <el-input v-model="copyModel.pageCode" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.pageName')"
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
import TableOperation from '@/library/mixins/table-operation'
import FormPageEditCommon from './edit.vue'
import FormPageEditReport from './edit_report'
import { formPageAPI } from 'modb@/basicSetting/api/basicSetting'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass()

export default {
  name: 'FormPageList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin, TableOperation],
  provide () {
    return { context: this }
  },
  data () {
    return {
      formPageEditReport: FormPageEditReport,
      formPageEditCommon: FormPageEditCommon,
      dictClass: dictClass,
      componentName: 'formPage',
      componentConfig: {
        add: FormPageEditCommon,
        edit: FormPageEditCommon,
        view: FormPageEditCommon
      },
      operationFunction: {
        deleteInfo: formPageAPI.delete
      },
      pageModel: 'COMMON',
      formPageAPI: formPageAPI,
      tableHeader: [
        {
          prop: 'pageCode',
          label: this.$t('dataConfMod.pageCode')
        },
        {
          prop: 'pageModel',
          label: this.$t('dataConfMod.pageModel'),
          dataType: 'dict',
          code: 'FORM_PAGE_MODEL',
          hidden: this.pageModel !== 'COMMON'
        },
        {
          prop: 'pageName',
          label: this.$t('dataConfMod.pageName')
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.copyHandle(row),
              code: 'formPageSceneFileCol:add',
              formattor: () => {
                return this.$t('common.copy')
              }
            },
            {
              callback: row => this.editHandle(row, row.formPageId),
              code: 'formPageSceneFileCol:edit',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row.formPageId),
              code: 'formPageSceneFileCol:delete',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'pageCode',
          label: this.$t('dataConfMod.pageCode'),
          type: 'custom-dict',
          code: 'ATTACHMENT_TEMPLATE_SCENE',
          customSelectType: 'SECOND_DICT'
        },
        { prop: 'pageName', label: this.$t('dataConfMod.pageName') }
      ],
      preFormObj: { pageCode: null },
      queryParam: {},
      textInfo: 'test',

      copyModelRules: {
        dimName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        dimCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      copyModel: {
        formPageId: '',
        pageCode: '',
        pageName: ''
      },
      dialogFormVisible: false
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'sceneFileManagement'
        ) {
          this.preFormObj.pageCode = this.$route.params.pageCode // 流程标题
          this.$nextTick(() => {
            this.$refs.queryScene.query()
          })
          if (this.$route.params.pageCode) {
            formPageAPI.list({ pageCode: this.$route.params.pageCode }).then(
              res => {
                if (res.data && res.data.list && res.data.list.length > 0) {
                  const formPageDetail = res.data.list[0]
                  this.editHandle(formPageDetail, formPageDetail.formPageId)
                }
              }
            )
          }
        }
      }
    }
  },
  created () {
    this.pageModel = (this.$attrs.params || {}).pageModel || ''
  },
  methods: {
    getQueryData (params) {
      this.queryParam = params || this.queryParam

      if (this.pageModel !== 'COMMON') {
        this.queryParam.pageModel = this.pageModel
      }

      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (addComponent, pageModel) {
      if (!this.componentConfig.add && !addComponent) {
        this.$message.error(this.$t('dataConfMod.notConfiguredComponent'))
        return
      }
      this.mode = 'add'
      const tab = {
        component: addComponent || this.componentConfig.add,
        params: {
          flag: this.mode,
          pageModel: pageModel
        },
        closable: true,
        title: this.$t(this.componentName + '.table.add'),
        name: this.componentName + 'Add' + new Date().getTime()
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row, id) {
      const editComponent = this.pageModel === 'REPORT' || this.pageModel === 'SCENE_ATTACHMENT' ? this.formPageEditReport : this.formPageEditCommon
      if (!this.componentConfig.edit && !editComponent) {
        this.$message.error(this.$t('dataConfMod.notConfiguredModified'))
        return
      }
      this.mode = 'edit'
      const tab = {
        component: editComponent || this.componentConfig.edit,
        params: {
          row: row,
          flag: this.mode,
          pageModel: this.pageModel
        },
        title: this.$t(this.componentName + '.table.edit') + ':' + id,
        name: this.componentName + 'Edit' + id
      }
      this.$emit('tab-add', tab)
    },
    copyHandle (row) {
      this.dialogFormVisible = true
      this.copyModel = {
        formPageId: row.formPageId
      }
    },
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
        formPageAPI.copyPageForm(data).then(res => {
          this.$message.success(res.message)
          this.dialogFormVisible = false
          this.getQueryData()
        })
      })
    }
  }
}
</script>
