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
          <!-- 新增 -->
          <AuthorityButton
            code="base:materialCategoryMaintain:addNew"
            type="primary"
            @click="addNew"
          >
            {{ $t("common.add") }}
          </AuthorityButton>

          <m-import
            ref="import"
            style="display: inline-block;margin: 0 10px;"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-base/organization/category-business/listPageByParam"
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
        :model="configVendorModel.configForm"
        :rules="configVendorModel.rules"
      >
        <el-row :gutter="50">
          <el-col :span="12">
            <!-- 物料小类 -->
            <el-form-item
              :label="$t('dataConfMod.categoryLittle')"
              prop="categoryName"
            >
              <quick-search
                :show-input="configVendorModel.configForm.categoryName"
                show-key="categoryName"
                :scope-data="configVendorModel.configForm"
                name="scc_base_purchase_category2"
                @close-quicksearch="getCategoryObj"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 业务小类 -->
            <el-form-item
              :label="$t('dataConfMod.businessLittleType')"
              prop="businessLittleType"
            >
              <DictSelect
                v-model="configVendorModel.configForm.businessLittleType"
                code="BUSSINESS_LITTLE_TYPE"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 生效日期  -->
            <el-form-item :label="$t('dataConfMod.effectDate')">
              <el-date-picker
                v-model="configVendorModel.configForm.startDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 失效日期 -->
            <el-form-item :label="$t('dataConfMod.endDate')">
              <!-- 选择日期 -->
              <el-date-picker
                v-model="configVendorModel.configForm.endDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="comfirmSave"
        >
          <!-- 确 定 -->
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime, adaptDictData } from '@/utils'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'MaterialCategoryMaintain',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'categoryDivision',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('components.eio.importTitle'), // Excel导入
        upLoadUrl:
          '/api-base/organization/category-business/importExcel'
      },
      pageSize: 15,
      gridId: 'byVendorList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.categoryLittle') // 物料小类
        },
        {
          prop: 'businessLittleType',
          label: () => this.$t('dataConfMod.businessLittleType'), //  业务小类
          type: 'dict',
          code: 'BUSSINESS_LITTLE_TYPE'
        }
      ], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      curOpt: 'add',
      dialogTitle: this.$t('dataConfMod.addSetting'), // '新增配置'
      tableSelection: [],
      configVendorModel: {
        configForm: {
          categoryName: '',
          categoryCode: '',
          categoryId: '',
          businessLittleType: '',
          categoryBusinessId: null,
          startDate: '',
          endDate: ''
        },
        rules: {
          categoryName: [
            { required: true, message: this.$t('dataConfMod.msgCateSelected') }
          ],
          businessLittleType: [
            {
              required: true,
              message: this.$t('dataConfMod.msgBusinessSelected')
            }
          ]
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'categoryName',
        minWidth: '150',
        label: () => this.$t('dataConfMod.categoryLittle') // 物料小类
      },
      {
        prop: 'businessLittleType',
        minWidth: '150',
        label: () => this.$t('dataConfMod.businessLittleType'), // 业务小类
        dataType: 'dict',
        code: 'BUSSINESS_LITTLE_TYPE'
      },
      {
        prop: 'startDate',
        width: 100,
        label: () => this.$t('dataConfMod.effectDate') // '生效日期'
      },
      {
        prop: 'endDate',
        width: 100,
        label: () => this.$t('dataConfMod.endDate') // '失效日期'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        width: 120,
        label: () => this.$t('common.updatePeople') // '更新人'
      },
      {
        prop: 'lastUpdateDate',
        width: 150,
        label: () => this.$t('common.updateTime') // '更新时间'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 100,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              _this.editDetail(row)
            },
            code: 'base:materialCategoryMaintain:editDetail',
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            }
            // show: row=>(row.status === "APPROVED")    //'已经注册'
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            code: 'base:materialCategoryMaintain:delRowData',
            formattor (val) {
              return _this.$t('common.delete') // 删除
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
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      // 物料小类业务小类维护导入模板.xlsx
      downloadFileLink(
        '/api-base/organization/category-business/importModelDownload',
        this.$t('dataConfMod.cateBusinessImpXLSX')
      ).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      let params = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      data.vendorId = val ? val.companyId : null
      data.vendorCode = val ? val.companyCode : ''
      data.vendorName = val ? val.companyName : ''
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('dataConfMod.addSetting') // '新增配置'
        let formObj = this.configVendorModel.configForm
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
        this.configVendorModel.configForm.startDate = parseTime(
          new Date(),
          '{y}-{m}-{d}'
        )
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.editSetting') // '编辑配置'
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
      let submitData = this.configVendorModel.configForm
      if (opt === 'add') {
        // 新增
        delete submitData.configVendorId
      }
      this.$api.base.basicSetting.siteConfigVendorSaveOrUpdate(submitData).then(res => {
        if (res) {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata() // 重新查询数据
          this.dialogFormVisible = false
        }
      })
    },
    getBusinessSmallObj (val, scope) {
      scope.businessLittleType = val ? val.dictItemCode : ''
    },
    delRowData (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/organization/category-business/delete',
            method: 'GET',
            params: { id: row.categoryBusinessId },
            loading: true
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    editDetail (row) {
      this.curOpt = 'edit'
      for (let i in this.configVendorModel.configForm) {
        this.configVendorModel.configForm[i] = row[i]
      }
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.$refs.orgform.validate(valid => {
        if (valid) {
          // this.saveOrUpdateHandle(this.curOpt)
          let url = '/api-base/organization/category-business/add'
          if (this.configVendorModel.configForm.categoryBusinessId) {
            url = '/api-base/organization/category-business/modify'
          }
          let submitData = this.configVendorModel.configForm
          this.$http({
            url: url,
            method: 'POST',
            data: submitData,
            loading: true
          }).then(res => {
            this.dialogFormVisible = false
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
