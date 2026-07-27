<template>
  <el-container class="barcoderuleheadEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form ref="form" :model="form" :rules="rules">
          <srm-row :gutter="32">
            <srm-col :span="6">
              <el-form-item prop="ruleName" label="规则名称">
                <el-input v-model="form.ruleName" maxlength="50" show-word-limit />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item prop="ruleDesc" label="规则描述">
                <el-input v-model="form.ruleDesc" maxlength="100" show-word-limit />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item prop="startDate" label="开始日期">
                <el-date-picker
                  v-model="form.startDate"
                  :picker-options="startTiumePickerOptions"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item prop="endDate" label="结束时间">
                <el-date-picker
                  v-model="form.endDate"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :picker-options="endTiumePickerOptions"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item prop="businessBoxType" label="所属箱型业务">
                <dict-select
                  v-model="form.businessBoxType"
                  code="TAG_RULE_TYPE"
                />
              </el-form-item>
            </srm-col>

            <srm-col :span="6">
              <el-form-item prop="categoryName" label="品类信息">
                <QuickSearch
                  :disable="selectCategory"
                  :showInput="form.categoryName"
                  show-key="CATEGORY_NAME"
                  :scope-data="form"
                  name="scc_base_purchase_category4"
                  @close-quicksearch="getCategoryByQuick"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <el-collapse v-model="activeLine">
        <el-collapse-item title="明细" name="1">
          <el-container class="flex-container" style="height: 300px">
            <el-main>
              <div style="padding: 12px 0">
                <!-- <el-button class="detail-pbtn" type="primary"
                :disabled="!selectCategoryFlag"
                @click="addLine"
                  >新增</el-button
                > -->
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  @click="addLine"
                >
                  新增
                </el-button>
              </div>
              <BaseTable
                ref="table"
                :columns="columns"
                :dataSource="dataSource"
                :initialize="false"
                rowKey="ruleLineId"
                border
                @asyncGetRealDataSource="asyncGetRealDataSource"
              >
                <template #fieldEnum="{ scope }">
                  <dict-select
                    v-model="scope.row.fieldEnum"
                    :code="form.businessBoxType === 'INNER_BOX' ? 'INNER_RULE_BASE' : 'OUTER_RULE_BASE'"
                  />
                </template>

                <template #createdBy="{ scope }">
                  <el-input v-model="scope.row.createdBy" :disabled="true" />
                </template>

                <template #creationDate="{ scope }">
                  <el-input v-model="scope.row.creationDate" :disabled="true" />
                </template>
              </BaseTable>
            </el-main>
          </el-container>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            取消
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            确认
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { boxBarCodeRuleApi } from 'modb@/barcodeManagement/api'

export default {
  name: 'BarcoderuleheadEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    CDownloadLink,
    CUploadFile,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      realDataSource: [],
      dataSource: [],
      activeLine: ['1'],
      columns: [
        {
          attrs: {
            prop: 'fieldEnum',
            label: '字段类型',
            formatter: (value) => {
              const code = this.form.businessBoxType === 'INNER_BOX' ? 'INNER_RULE_BASE' : 'OUTER_RULE_BASE'
              return this.$getDictLabel(code, value)
            }
          },
          slot: 'fieldEnum'
        },
        {
          attrs: {
            prop: 'createdBy',
            label: '创建人'
          },
          slot: 'createdBy'
        },
        {
          attrs: {
            prop: 'creationDate',
            label: '创建时间'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            prop: 'operation',
            label: '操作',
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            }
          ]
        }
      ],
      form: {
        ruleName: null,
        ruleDesc: null,
        startDate: null,
        endDate: null,
        business: null,
        isConfigCategory: null,
        categoryId: null,
        categoryName: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        businessBoxType: null,
        businessCodeType: 'BAR_CODE'
      },
      rules: {
        ruleName: [{ required: true, message: '请填写规则名称！' }],
        startDate: [{ required: true, message: '请选择开始日期！' }],
        businessBoxType: [{ required: true, message: '请选择所属箱型业务！' }],
        categoryName: [{ required: true, message: '请选择品类信息！' }]
      },
      readOnly: false,
      selectCategoryFlag: false,
      // 选择品类
      selectCategory: true,
      startTiumePickerOptions: {
        disabledDate: time => {
          const end = new Date(this.form.endDate)
          if (end.getTime()) {
            return time.getTime() > end.getTime()
          } else {
            return false
          }
        }
      },
      endTiumePickerOptions: {
        disabledDate: time => {
          const start = new Date(this.form.startDate)
          return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
        }
      }
    }
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.getDetail()
    }
  },
  methods: {
    /**
     * 获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    async getDetail () {
      await boxBarCodeRuleApi.getById(this.$attrs.params.row.ruleHeadId)
        .then((res) => {
          const { barcodeRuleLineList, ...rest } = res.data
          this.form = rest
          this.dataSource = barcodeRuleLineList
        })
    },
    async save () {
      if (this.realDataSource.length < 1) {
        return this.$message.warning('请至少添加一条明细数据！')
      }
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        const data = {
          ...this.form,
          barcodeRuleLineList: this.realDataSource
        }
        boxBarCodeRuleApi.addOrUpdate(data).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      } else {
        this.__focus_error__(message)
      }
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    /*
    * @Description: 校验表单表格必填项
    * @return: {
    *   flag: true/false,  校验是否通过
    *   message: 返回填写信息
    * }
    */
    async getCheckForm () {
      const formFiled = await this.formValidate('form')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      return { flag: true }
    },
    addLine () {
      console.log(this.formest, 'rest')

      if (!this.form.businessBoxType) {
        this.$message({
          type: 'warning',
          message: '请选择所属箱型业务！',
          duration: 10000,
          showClose: true
        })
        return false
      }
      if (!this.form.categoryName) {
        this.$message({
          type: 'warning',
          message: '请选择品类！',
          duration: 10000,
          showClose: true
        })
        return false
      }
      this.$refs.table.add({})
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('barcoderuleheadList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.barcoderuleheadEdit {
  height: 100%;
  padding-bottom: 50px;
  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
