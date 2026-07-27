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
            code="base:purchaseBaseSetting:add"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-base/purchase/purchaseCurrency/importExcel"
            :extra-data="extraData"
            code="currencySetting:import"
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
        customTableKey="purchaseBaseSettingExchangeCurrencySetting"
        url="/api-base/purchase/purchaseCurrency/listPage"
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
            <!-- 币种名称 -->
            <el-form-item
              :label="$t('dataConfMod.currencyName')"
              prop="currencyName"
            >
              <el-input v-model="purchaseModel.purchaseform.currencyName" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 币种编码 -->
            <el-form-item
              :label="$t('dataConfMod.currencyCode')"
              prop="currencyCode"
            >
              <el-input
                v-model="purchaseModel.purchaseform.currencyCode"
                :disabled="curOpt==='edit'"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 小数点位数 -->
            <el-form-item
              :label="$t('dataConfMod.decimalPoint')"
              prop="decimalPoint"
            >
              <el-input v-model="purchaseModel.purchaseform.decimalPoint" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 语言 -->
            <el-form-item
              :label="$t('common.language')"
              prop="language"
            >
              <el-select
                v-model="purchaseModel.purchaseform.language"
              >
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
              prop="currencySort"
            >
              <el-input v-model="purchaseModel.purchaseform.currencySort" />
            </el-form-item>
          </srm-col>
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
        </srm-row>
        <srm-row>
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
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
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
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { downloadFileLink } from 'lib@/utils/file'
import { purchaseBaseSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'CurrencySetting',
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
        fileFunction: 'currencySetting',
        fileType: 'excel'
      },
      pageSize: 15,
      gridId: 'currencyList',
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
      dialogTitle: this.$t('dataConfMod.addCurrency'), // '新增币种',
      tableSelection: [],
      langList: [],
      purchaseModel: {
        purchaseform: {
          currencyName: '', // 币种名称
          currencyCode: '', // 币种编码
          language: '', // 语言
          languageName: '', // 语言名称
          enabled: 'Y', // 是否启用
          defaultShow: 'Y', // 是否显示
          decimalPoint: '', // 小数位数
          currencySort: '' // 排序
        },
        rules: {
          currencyName: [{ required: true, message: this.$t('dataConfMod.msgCurrencyName') }], // '请输入币种名称'
          currencyCode: [{ required: true, message: this.$t('dataConfMod.msgCurrencyCode') }], // '请输入币种编码'
          language: [{ required: true, message: this.$t('dataConfMod.msgLanguage') }], // '请选择语言类型'
          decimalPoint: [{ required: true, message: this.$t('dataConfMod.msgDecimalPoint') }]// '请输入小数点位数'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      { prop: 'currencyName',
        label: () => this.$t('dataConfMod.currencyName')// '币种名称'
      },
      { prop: 'currencyCode',
        label: () => this.$t('dataConfMod.currencyCode')// '币种编码'
      },
      { prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' }, // '是'
          { label: this.$t('common.no'), value: 'N' }// '否'
        ]
      }
    ]
    this.tableHeader = [
      {
        prop: 'currencyName',
        label: () => this.$t('dataConfMod.currencyName')// '币种名称'
      },
      {
        prop: 'currencyCode',
        label: () => this.$t('dataConfMod.currencyCode')// '币种编码'
      },
      {
        prop: 'decimalPoint',
        label: () => this.$t('dataConfMod.decimalPoint'), // '小数点位数'
        width: '105'
      },
      {
        prop: 'currencySort',
        label: () => this.$t('common.sort')// '序号'
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
          let textVal = val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
          return '<span>' + textVal + '</span>'
        }
      },
      {
        prop: 'defaultShow',
        label: () => this.$t('dataConfMod.defaultShow'), // '是否默认显示',
        minWidth: '120px',
        formattor (val) {
          let textVal = val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
          return '<span>' + textVal + '</span>'
        }
      },
      { prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime')// '更新时间'
      },
      { prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople')// '更新人'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        code: 'base:purchaseBaseSetting:edit',
        callback: function (row) {
          this.editDetail(row)
        }.bind(this),
        formattor () {
          return _this.$t('common.edit')// '编辑'
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
        '/api-base/purchase/purchaseCurrency/importExcelTemplate',
        this.$t('dataConfMod.purchaseCurrencyTemplate')
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
      // 获取语言
      this.$http({
        url: '/api-base/dict/base-dict-language/listAll',
        method: 'POST',
        data: {}
      }).then(data => {
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
        this.dialogTitle = this.$t('dataConfMod.addCurrency')// '新增币种'
        let formObj = this.purchaseModel.purchaseform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.editCurrency')// '编辑币种'
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
      if (opt === 'add') { // 新增
        delete submitData.currencyId
      }
      purchaseBaseSetting.currencySaveOrUpdate(submitData).then(res => {
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
      this.purchaseModel.purchaseform.currencyName = row.currencyName
      this.purchaseModel.purchaseform.currencyCode = row.currencyCode
      this.purchaseModel.purchaseform.language = row.language
      this.purchaseModel.purchaseform.enabled = row.enabled
      this.purchaseModel.purchaseform.defaultShow = row.defaultShow
      this.purchaseModel.purchaseform.currencySort = row.currencySort
      this.purchaseModel.purchaseform.decimalPoint = row.decimalPoint
      this.purchaseModel.purchaseform.currencyId = row.currencyId
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
