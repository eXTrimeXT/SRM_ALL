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
            code="base:UnitSetting:add"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-base/purchase/purchaseUnit/importExcel"
            :extra-data="extraData"
            code="unitSetting:import"
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
        customTableKey="purchaseBaseSettingUnitSetting"
        url="/api-base/purchase/purchaseUnit/listPage"
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
            <!-- 单位名称 -->
            <el-form-item
              :label="$t('dataConfMod.unitName')"
              prop="unitName"
            >
              <el-input v-model="purchaseModel.purchaseform.unitName" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 单位编码 -->
            <el-form-item
              :label="$t('dataConfMod.unitCode')"
              prop="unitCode"
            >
              <el-input
                v-model="purchaseModel.purchaseform.unitCode"
                :disabled="curOpt==='edit'"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 序号 -->
            <el-form-item
              :label="$t('common.sort')"
              prop="unitSort"
            >
              <el-input v-model="purchaseModel.purchaseform.unitSort" />
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
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { purchaseBaseSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'UnitSetting',
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
        fileFunction: 'unitSetting',
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
      dialogTitle: this.$t('dataConfMod.addUnit'), // '新增单位',
      tableSelection: [],
      langList: [],
      purchaseModel: {
        purchaseform: {
          unitName: '', // 单位名称
          unitCode: '', // 单位编码
          language: '', // 语言
          languageName: '', // 语言名称
          enabled: 'Y', // 是否启用
          defaultShow: 'Y', // 是否显示
          unitSort: '' // 排序
        },
        rules: {
          unitName: [{ required: true, message: this.$t('dataConfMod.msgUnitName') }], // '请输入单位名称'
          unitCode: [{ required: true, message: this.$t('dataConfMod.msgUnitCode') }], // '请输入单位编码'
          language: [{ required: true, message: this.$t('dataConfMod.msgLanguage') }]// '请输请选中语言'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      { prop: 'unitName',
        label: () => this.$t('dataConfMod.unitName')// '单位名称'
      },
      { prop: 'unitCode',
        label: () => this.$t('dataConfMod.unitCode')// '单位编码'
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
        prop: 'unitName',
        label: () => this.$t('dataConfMod.unitName'), // '单位名称',
        minWidth: '120'
      },
      {
        prop: 'unitCode',
        label: () => this.$t('dataConfMod.unitCode'), // '单位编码',
        minWidth: '120'
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用',
        width: '100',
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
      {
        prop: 'unitSort',
        label: () => this.$t('common.sort'), // '序号',
        minWidth: '80'
      },
      {
        prop: 'language',
        label: () => this.$t('common.language'), // '字典语言',
        formattor (val) {
          return _this.$getLabelByValue(_this.langList, val)
        }
      },
      { prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // '更新时间',
        minWidth: '160',
        dataType: 'dateTime'
      },
      { prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople'), // '更新人',
        minWidth: '160'
      },
      {
        label: () => this.$t('common.operation'), // '操作',
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        code: 'base:UnitSetting:edit',
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
        '/api-base/purchase/purchaseUnit/importExcelTemplate',
        this.$t('dataConfMod.purchaseUnitTemplate')
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
        this.dialogTitle = this.$t('dataConfMod.addUnit')// '新增单位'
        let formObj = this.purchaseModel.purchaseform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.editUnit')// '编辑单位'
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
        delete submitData.unitId
      }
      purchaseBaseSetting.unitSaveOrUpdate(submitData).then(res => {
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
      this.purchaseModel.purchaseform.unitName = row.unitName
      this.purchaseModel.purchaseform.unitCode = row.unitCode
      this.purchaseModel.purchaseform.language = row.language
      this.purchaseModel.purchaseform.enabled = row.enabled
      this.purchaseModel.purchaseform.defaultShow = row.defaultShow
      this.purchaseModel.purchaseform.unitSort = row.unitSort
      this.purchaseModel.purchaseform.unitId = row.unitId
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
