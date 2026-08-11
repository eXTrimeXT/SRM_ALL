<template>
  <el-container
    class="flex-container-notab the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        url="/api-sup/bda/bdaState/listPageByParam"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-form
        ref="orgform"
        :model="submitModel.submitform"
        :rules="submitModel.rules"
      >
        <el-row :gutter="50">
          <el-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.businessType')"
              prop="businessType"
            >
              <DictSelect
                v-model="submitModel.submitform.businessType"
                code="BUSINESS_TYPE"
                @change="buTypeChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 品类状态 -->
            <el-form-item
              :label="$t('dataConfMod.categoryStatus')"
              prop="categoryStatus"
            >
              <DictSelect
                v-model="submitModel.submitform.categoryStatus"
                code="CATEGORY_STATUS"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 组织状态 -->
            <el-form-item
              :label="$t('dataConfMod.orgStatus')"
              prop="orgStatus"
            >
              <DictSelect
                v-model="submitModel.submitform.orgStatus"
                code="ORG_STATUS"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 是否可创建 -->
            <el-form-item
              :label="$t('dataConfMod.isAllowCreate')"
              prop="isAllow"
            >
              <DictSelect
                v-model="submitModel.submitform.isAllow"
                code="YES_OR_NO"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="handleCancel">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="comfirmSave"
        >
          <!-- 确 定 -->
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'RateSetting',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      submitLoading: false,
      pageSize: 15,
      gridId: 'RateList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      curOpt: 'add',
      dialogTitle: this.$t('common.add'), // '新增',
      tableSelection: [],
      langList: [],
      submitModel: {
        submitform: {
          businessType: '', // 业务类型
          businessTypeName: '', // 业务类型名称
          categoryStatus: '', // 品类状态
          orgStatus: '', // 组织状态
          isAllow: '' // 是否可创建
        },
        rules: {
          businessType: [{ required: true, message: this.$t('dataConfMod.msgBusinessType') }], // '请选择业务类型'
          categoryStatus: [{ required: true, message: this.$t('dataConfMod.msgCategoryStatus') }], // '请选择品类状态'
          orgStatus: [{ required: true, message: this.$t('dataConfMod.msgOrgStatus') }] // '请选择组织状态'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'businessType',
        label: () => this.$t('dataConfMod.businessType'), // '单据类型'
        type: 'dict',
        code: 'BUSINESS_TYPE'
      },
      {
        prop: 'orgStatus',
        label: () => this.$t('dataConfMod.orgStatus'), // '组织状态'
        type: 'dict',
        code: 'ORG_STATUS'
      },
      {
        prop: 'categoryStatus',
        label: () => this.$t('dataConfMod.categoryStatus'), // '品类状态'
        type: 'dict',
        code: 'CATEGORY_STATUS'
      },
      {
        prop: 'isAllow',
        label: () => this.$t('dataConfMod.isAllowCreate'), // '是否可创建'
        type: 'dict',
        code: 'YES_OR_NO'
      }
    ]
    this.tableHeader = [
      {
        prop: 'businessType',
        label: () => this.$t('dataConfMod.businessType'), // '单据类型'
        dataType: 'dict',
        code: 'BUSINESS_TYPE'
      },
      {
        prop: 'orgStatus',
        label: () => this.$t('dataConfMod.orgStatus'), // '组织状态'
        dataType: 'dict',
        code: 'ORG_STATUS'
      },
      {
        prop: 'categoryStatus',
        label: () => this.$t('dataConfMod.categoryStatus'), // '品类状态'
        dataType: 'dict',
        code: 'CATEGORY_STATUS'
      },
      {
        prop: 'isAllow',
        label: () => this.$t('dataConfMod.isAllowCreate'), // '是否可创建'
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime') // '更新时间'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople') // '更新人'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '160',
        fixed: 'right',
        editType: 'none',
        btnStyle: 'text',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editDetail(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // '编辑'
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            }
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
      let params = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 切换业务类型
    buTypeChange (val) {
      if (val) {
        let row = this.busTypeList.find(item => {
          return item.value === val
        })
        if (row) {
          this.submitModel.submitform.businessTypeName = row.label
        }
      }
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('common.add') // '新增'
        let formObj = this.submitModel.submitform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
      } else {
        // 修改
        this.dialogTitle = this.$t('common.edit') // '编辑'
      }
      this.dialogFormVisible = true
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    saveData () {
      this.saveOrUpdateHandle(this.curOpt)
    },
    // 新增编辑组织数据
    saveOrUpdateHandle (opt) {
      // 表单验证处理
      this.$refs.orgform.validate(async (valid) => {
        if (!valid) {
          return false
        }
        let submitData = this.submitModel.submitform
        this.submitLoading = true
        if (opt === 'add') {
          // 新增
          delete submitData.stateControlId
        }
        let res = await this.$api.base.basicSetting.saveOrUpdateBdaData(submitData)
        if (res) {
          // 返回数据处理
          this.$message.success(res.message)
          this.getQuerydata() // 重新查询数据
          this.submitLoading = false
          this.dialogFormVisible = false
          this.$refs.orgform.resetFields()
        }
      })
    },
    handleCancel () {
      this.dialogFormVisible = false
      this.$refs.orgform.resetFields()
    },
    editDetail (row) {
      this.curOpt = 'edit'
      this.submitModel.submitform.businessType = row.businessType
      this.submitModel.submitform.categoryStatus = row.categoryStatus
      this.submitModel.submitform.orgStatus = row.orgStatus
      this.submitModel.submitform.isAllow = row.isAllow
      this.submitModel.submitform.stateControlId = row.stateControlId
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    delRowData (row) {
      let stateControlId = row.stateControlId
      // 当前操将永久删除这条数据，确认删除这条数据？
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.base.basicSetting.bdaStateDel({ stateControlId }).then(res => {
            if (res) {
              this.getQuerydata()
            }
          })
        })
        .catch(() => {})
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
<style scoped lang="scss"></style>
