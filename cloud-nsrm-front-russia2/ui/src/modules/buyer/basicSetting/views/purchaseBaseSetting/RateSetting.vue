<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="base:RateSetting:add"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-base/purchase/purchaseTax/importExcel"
            :extra-data="extraData"
            code="rateSetting:import"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        customTableKey="purchaseBaseSettingRateSetting"
        url="/api-base/purchase/purchaseTax/listPage"
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
        :model="purchaseModel.purchaseform"
        :rules="purchaseModel.rules"
      >
        <srm-row>
          <srm-col :init-col="2">
            <!-- 税率名称 -->
            <el-form-item
              :label="$t('dataConfMod.taxName')"
              prop="taxName"
            >
              <el-input v-model="purchaseModel.purchaseform.taxName" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 税率值 -->
            <el-form-item
              :label="$t('dataConfMod.taxCode')"
              prop="taxCode"
            >
              <el-input
                v-model="purchaseModel.purchaseform.taxCode"
                type="number"
                :disabled="curOpt === 'edit'"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 税率编码 -->
            <el-form-item
              :label="$t('dataConfMod.taxKey')"
              prop="taxKey"
            >
              <el-input
                v-model="purchaseModel.purchaseform.taxKey"
                :disabled="curOpt === 'edit'"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 语言 -->
            <el-form-item
              :label="$t('common.language')"
              prop="language"
            >
              <el-select v-model="purchaseModel.purchaseform.language">
                <el-option
                  v-for="item in langList"
                  :key="item.language"
                  :label="item.languageName"
                  :value="item.language"
                />
              </el-select>
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 序号 -->
            <el-form-item
              :label="$t('common.sort')"
              prop="taxSort"
            >
              <el-input v-model="purchaseModel.purchaseform.taxSort" />
            </el-form-item>
          </srm-col>
        </srm-row>
        <srm-row>
          <srm-col :init-col="2">
            <!-- 是否默认显示 -->
            <el-form-item
              :label="$t('dataConfMod.defaultShow')"
              prop="defaultShow"
            >
              <el-checkbox
                v-model="purchaseModel.purchaseform.defaultShow"
                width="100%"
                true-label="Y"
                false-label="N"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 是否启用 -->
            <el-form-item
              :label="$t('dataConfMod.enabledUse')"
              prop="enabled"
            >
              <el-checkbox
                v-model="purchaseModel.purchaseform.enabled"
                width="100%"
                true-label="Y"
                false-label="N"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
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
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { purchaseBaseSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'RateSetting',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  data () {
    return {
      extraData: {
        fileModular: 'base',
        fileFunction: 'rateSetting',
        fileType: 'excel'
      },
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
      dialogTitle: this.$t('dataConfMod.addRate'), // '新增税率'
      tableSelection: [],
      langList: [],
      purchaseModel: {
        purchaseform: {
          taxName: '', // 税率名称
          taxCode: '', // 税率值
          taxKey: '', // 税率编码
          language: '', // 语言
          languageName: '', // 语言名称
          enabled: 'Y', // 是否启用
          defaultShow: 'Y', // 是否显示
          taxSort: '' // 排序
        },
        rules: {
          taxName: [
            { required: true, message: this.$t('dataConfMod.msgTaxName') }
          ], // '请输入税率名称'
          taxKey: [
            { required: true, message: this.$t('dataConfMod.msgTaxKey') }
          ], // '请输入税率值'
          taxCode: [
            { required: true, message: this.$t('dataConfMod.msgTaxCode') }
          ], // '请输入税率编码'
          language: [
            { required: true, message: this.$t('dataConfMod.msgLanguage') }
          ] // '请输请选中语言'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'taxName',
        label: () => this.$t('dataConfMod.taxName') // '税率名称'
      },
      {
        prop: 'taxKey',
        label: () => this.$t('dataConfMod.taxKey') // '税率编码'
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' }, // '是'
          { label: this.$t('common.no'), value: 'N' } // '否'
        ]
      }
    ]
    this.tableHeader = [
      {
        prop: 'taxName',
        label: () => this.$t('dataConfMod.taxName') // '税率名称',
        // desc: "税率名称"
      },
      {
        prop: 'taxKey',
        label: () => this.$t('dataConfMod.taxKey') // '税率编码'
      },
      {
        prop: 'taxCode',
        label: () => this.$t('dataConfMod.taxCode') // '税率值'
      },
      {
        prop: 'language',
        label: () => this.$t('common.language'), // '字典语言'
        formattor (val) {
          return _this.$getLabelByValue(_this.langList, val)
        }
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        formattor (val) {
          let textVal =
            val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
          return '<span>' + textVal + '</span>'
        }
      },
      {
        prop: 'defaultShow',
        label: () => this.$t('dataConfMod.defaultShow'), // '是否默认显示'
        minWidth: '120px',
        formattor (val) {
          let textVal = val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
          return '<span>' + textVal + '</span>'
        }
      },
      {
        prop: 'taxSort',
        label: () => this.$t('dataConfMod.taxSort') // '序号'
      },
      {
        prop: 'lastUpdateDate',
        dataType: 'dateTime',
        label: () => this.$t('common.updateTime') // '更新时间'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople') // '更新人'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        code: 'base:RateSetting:edit',
        callback: function (row) {
          this.editDetail(row)
        }.bind(this),
        formattor () {
          return _this.$t('common.edit') // '编辑'
        }
      }
    ]
    this.getLanguage()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    downloadTemplate () {
      downloadFileLink(
        '/api-base/purchase/purchaseTax/importExcelTemplate',
        this.$t('dataConfMod.taxImportTemplate')
      ).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getLanguage () {
      // 获取语言列表
      this.$http({
        url: '/api-base/dict/base-dict-language/listAll',
        method: 'POST',
        data: {}
      })
        .then(data => {
          this.langList = data.data
        })
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('dataConfMod.addRate') // '新增税率'
        let formObj = this.purchaseModel.purchaseform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.editRate') // '编辑税率'
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
      let submitData = this.purchaseModel.purchaseform
      if (opt === 'add') {
        // 新增
        delete submitData.taxId
      }
      purchaseBaseSetting.rateSaveOrUpdate(submitData).then(res => {
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
    editDetail (row) {
      this.curOpt = 'edit'
      this.purchaseModel.purchaseform.taxName = row.taxName
      this.purchaseModel.purchaseform.taxCode = row.taxCode
      this.purchaseModel.purchaseform.language = row.language
      this.purchaseModel.purchaseform.enabled = row.enabled
      this.purchaseModel.purchaseform.defaultShow = row.defaultShow
      this.purchaseModel.purchaseform.taxSort = row.taxSort
      this.purchaseModel.purchaseform.taxId = row.taxId
      this.purchaseModel.purchaseform.taxKey = row.taxKey
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
