<template>
  <el-container class="flex-container boxtagconfig_list_wrapper" direction="vertical">
    <el-main>
      <form-wrapper :formArray="filterConfig" @getFormData="getQuerydata">
        <template #fieldTypeCode="{ scope }">
          <el-select v-model="scope.fieldTypeCode">
            <el-option v-for="item in fieldTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </template>
        <template #isMust="{ scope }">
          <dict-select v-model="scope.isMust" code="YES_OR_NO" />
        </template>
        <template #businessBoxType="{ scope }">
          <dict-select v-model="scope.businessBoxType" code="TAG_RULE_TYPE" />
        </template>
      </form-wrapper>
      <main-header :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/boxtagconfig/listPage"
      />
    </el-main>
    <!-- 弹窗 -->
    <srm-dialog :title="dialogTitle" size="large" :visible.sync="visible" destroy-on-close>
      <div class="boxtagconfigEdit">
        <el-form ref="form" :model="form" :rules="rules">
          <srm-row :gutter="32">
            <srm-col :span="8">
              <el-form-item prop="categoryName" label="品类">
                <quick-search
                  :disable="isReadOnly"
                  :showInput="form.categoryName"
                  show-key="CATEGORY_NAME"
                  :scope-data="form"
                  name="scc_base_purchase_category"
                  @close-quicksearch="getCategoryByQuick"
                />
              </el-form-item>
            </srm-col>

            <srm-col :span="8">
              <el-form-item prop="fieldTypeCode" label="字段类型">
                <el-select v-model="form.fieldTypeCode">
                  <el-option
                    v-for="item in fieldTypeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </srm-col>

            <srm-col :span="8">
              <el-form-item prop="fieldCode" label="拓展字段">
                <dict-select
                  v-model="form.fieldCode"
                  code="BOXCODE_EXTENDS_FIELD"
                  @change-value="selectField"
                />
              </el-form-item>
            </srm-col>

            <srm-col :span="8">
              <el-form-item prop="isMust" label="是否必须">
                <dict-select v-model="form.isMust" code="YES_OR_NO" />
              </el-form-item>
            </srm-col>

            <srm-col :span="8">
              <el-form-item prop="businessBoxType" label="所属箱型业务">
                <dict-select v-model="form.businessBoxType" code="TAG_RULE_TYPE" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <template #footer class="dialog-footer">
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="confirm">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
// 引入组件
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { boxtagconfigApi } from 'modb@/barcodeManagement/api'

export default {
  name: 'BoxtagconfigList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile,
    CDownloadLink,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      currentRows: [],
      visible: false,
      mode: 'add',
      isReadOnly: false,
      dialogTitle: '详情',
      form: {
        id: 'ID',
        categoryId: '品类ID',
        categoryName: '品类名称',
        categoryCode: '品类编码',
        fieldTypeCode: '字段类型编码',
        fieldTypeName: '字段类型名称（文本、数字）',
        fieldName: '字段名字',
        fieldCode: '字段编码',
        isMust: '是否必须',
        // fieldLength: "字段长度",
        businessBoxType: '所属箱型业务',
        createdId: '创建人id',
        createdBy: '创建人名称',
        creationDate: '创建日期',
        createdByIp: '创建ip',
        lastUpdatedId: '更新人id',
        lastUpdatedBy: '更新人',
        lastUpdateDate: '更新时间',
        lastUpdatedByIp: '更新ip',
        version: '版本号',
        tenantId: 'TENANT_ID'
      },
      rules: {
      },
      tableHeader: [
        {
          prop: 'categoryName',
          label: '品类名称',
          width: 100
        },
        {
          prop: 'categoryCode',
          label: '品类编码',
          width: 100
        },
        {
          prop: 'fieldTypeCode',
          label: '字段类型',
          width: 100,
          formattor: val => {
            const dict = this.fieldTypeOptions.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'fieldName',
          label: '字段名字',
          width: 100
        },
        {
          prop: 'fieldCode',
          label: '字段编码',
          width: 100
        },
        {
          prop: 'isMust',
          label: '是否必须',
          width: 100,
          formattor: val => this.$getDictLabel('YES_OR_NO', val)
        },
        {
          prop: 'businessBoxType',
          label: '所属箱型业务',
          width: 120,
          formattor: val => this.$getDictLabel('TAG_RULE_TYPE', val)
        },
        {
          prop: 'createdUserName',
          label: '创建人名称',
          width: 120
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 100
        },
        {
          prop: 'lastUpdatedUserName',
          label: '更新人',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'categoryCode', label: '品类编码' },
        {
          prop: 'fieldTypeCode',
          label: '字段类型',
          type: 'slot',
          slot: 'fieldTypeCode'
        },
        { prop: 'fieldCode', label: '字段编码' },
        {
          prop: 'isMust',
          label: '是否必须',
          type: 'slot',
          slot: 'isMust'
        },
        {
          prop: 'businessBoxType',
          label: '所属箱型业务',
          type: 'slot',
          slot: 'businessBoxType'
        }
      ],
      queryParam: {},

      // 字段类型下拉属性
      fieldTypeOptions: [
        { value: 'text', label: '文本' },
        { value: 'number', label: '数字' },
        { value: 'dateTime', label: '时间' },
        { value: 'date', label: '日期' }
      ]
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    /**
     * 選擇字段
     */
    selectField (val, scope) {
      this.form.fieldName = scope.label
      this.form.fieldCode = val
    },
    /**
     * 获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$refs.form.validate(result => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          const { id, ...rest } = this.form
          console.log(rest)
          if (rest.categoryName.length == 0) {
            this.$message({
              type: 'warning',
              message: '请选择品类！',
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (rest.fieldTypeCode.length == 0) {
            this.$message({
              type: 'warning',
              message: '请选择字段类型！',
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (rest.fieldCode.length == 0) {
            this.$message({
              type: 'warning',
              message: '请选择拓展字段！',
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (rest.isMust.length == 0) {
            this.$message({
              type: 'warning',
              message: '请选择是否必须！',
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (rest.businessBoxType.length == 0) {
            this.$message({
              type: 'warning',
              message: '请选择所属箱型业务！',
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (flag === 'add') {
            boxtagconfigApi.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
              this.getQuerydata()
            })
          } else if (flag === 'edit') {
            boxtagconfigApi.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
              this.getQuerydata()
            })
          }
        }
      })
    },

    getQuerydata (params) {
      this.queryParam = params
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
          boxtagconfigApi.delete(row.id).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => { })
    },
    addHandle (row) {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = '装箱条码标签配置新增'
      this.visible = true
      this.mode = 'add'
    },
    editHandle (row) {
      this.form = row
      this.dialogTitle = '装箱条码标签配置编辑'
      this.visible = true
      this.mode = 'edit'
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
