<template>
  <el-container
    class="flex-container-notab the_message_wrapper"
    style="min-width:900px"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="70px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="24"
        :r-span="0"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            page-url="/api-base/syscode/systemCode/listPage"
            :filter-params="queryParam"
          />
          <MImport
            ref="import1"
            :title="$t('common.import')"
            up-load-url="/api-base/syscode/systemCode/import"
            :extra-data="extraData"
            code="messageManagement:import"
            type="default"
            @downloadTemplate="dictHeaderDownload"
            @handleSuccess="dictHeaderhandleSuccess"
          />
          <AuthorityButton
            code="messageManagement:translation"
            type="default"
            @click="translationFn"
          >
            {{ $t('messageMaintenance.platformTranslation') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="checkChangeChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        url="/api-base/syscode/systemCode/listPage"
      />
      <!--弹框区域-->
      <srm-dialog
        :title="$t('messageMaintenance.editMessage')"
        :visible.sync="dialogFormVisible"
        size="large"
        :close-on-click-modal="false"
      >
        <el-form
          v-if="dialogFormVisible"
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('messageMaintenance.messageCode')"
                :label-width="formLabelWidth"
                prop="code"
              >
                <el-input
                  v-model="form.code"
                  :disabled="true"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('messageMaintenance.busModule')"
                :label-width="formLabelWidth"
                prop="module"
              >
                <DictSelect
                  v-model="form.module"
                  code="MODULE_SRM"
                  :disabled="isEdit"
                  @change="(val) => moduleChange(val,form)"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('messageMaintenance.messageType')"
                :label-width="formLabelWidth"
                prop="messageType"
              >
                <DictSelect
                  v-model="form.messageType"
                  code="SYSTEM_CODE_TYPE_SRM"
                  :disabled="isEdit"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div class="message-table">
          <div style="margin-bottom:8px;">
            <AuthorityButton
              type="primary"
              @click="addMessageItem"
            >
              {{ $t('common.add') }}
            </AuthorityButton>
          </div>
          <el-table
            :data="languageList"
            style="width: 100%"
            border
            highlight-current-row
          >
            <el-table-column prop="dictItemCode" :label="$t('messageMaintenance.languageType')">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.dictItemCode"
                  :disabled="scope.row.dictItemCode =='zh_CN'"
                  @change="(val) => editMessagelanguageChange(val,scope.row)"
                >
                  <el-option
                    v-for="item in langList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="message" :label="$t('messageMaintenance.messageDes')" />
            <el-table-column prop="customMessage" :label="$t('messageMaintenance.busMessageDes')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.customMessage" />
              </template>
            </el-table-column>
            <el-table-column
              prop="opration"
              width="100"
              :label="$t('common.operation')"
            >
              <template slot-scope="scope">
                <el-button v-if="scope.row.dictItemCode !='zh_CN'" type="text" @click="deleteMessage(scope)">
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmEdit"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
      <!-- 平台翻译 -->
      <!-- 选择翻译语言 -->
      <srm-dialog
        :title="$t('messageMaintenance.translationDialog')"
        :visible.sync="translationDialogFormVisible"
        size="small"
        :close-on-click-modal="false"
      >
        <div>
          <el-form
            v-if="translationDialogFormVisible"
            ref="form"
            :model="translationForm"
            class="form-incontainer"
            :rules="translationRule"
            label-width="80px"
            label-position="left"
          >
            <srm-row>
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('messageMaintenance.translationOrigineLang')"
                  prop="sourceLanguage"
                >
                  <el-select
                    v-model="translationForm.sourceLanguage"
                    clearable
                  >
                    <el-option
                      v-for="item in langList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('messageMaintenance.translationToLang')"
                  prop="targetLanguage"
                >
                  <el-select
                    v-model="translationForm.targetLanguage"
                    clearable
                  >
                    <el-option
                      v-for="item in langList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </div>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="translationDialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmTranslation"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportDirect from 'lib@/components/export-direct'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'MessageManagement',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportDirect,
    ExportExcel
  },
  provide () {
    return { tableName: this.name, defaultTableHeader: this.tableHeader }
  },
  data () {
    return {
      name: 'messageTableList',
      extraData: {
        fileModular: 'base',
        fileFunction: 'messageMaintenance',
        fileType: 'excel'
      },
      dictCodes: {
        module: 'MODULE_SRM',
        messageType: 'SYSTEM_CODE_TYPE_SRM',
        status: 'MESSAGE_STATUE'
      },
      pageSize: 15,
      gridId: 'messageList',
      currentRow: null,
      currentHeaderRows: [],
      tableHeader: [],
      tableData: [],
      form: {
        code: '',
        module: '',
        messageType: ''
      },
      rules: {
        code: [{ required: true, message: this.$t('common.pleaseInput') }],
        module: [{ required: true, message: this.$t('common.pleaseSelect') }],
        messageType: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      langList: [],
      preArr: [
        {
          prop: 'code',
          label: () => this.$t('messageMaintenance.messageCode') // '消息编码'
        },
        {
          prop: 'message',
          label: () => this.$t('messageMaintenance.messageZh') // '中文描述'
        },
        {
          prop: 'systemCodeType',
          type: 'dict',
          code: 'SYSTEM_CODE_TYPE_SRM',
          label: () => this.$t('messageMaintenance.messageType') // '消息类型'
        },
        {
          prop: 'moduleType',
          label: () => this.$t('messageMaintenance.busModule'),
          type: 'dict',
          code: 'MODULE_SRM'
        },
        {
          prop: 'status',
          type: 'dict',
          code: 'MESSAGE_STATUE',
          label: () => this.$t('messageMaintenance.status') // '状态'
        },
        {
          prop: 'daterange',
          label: () => this.$t('messageMaintenance.asyncDataTime'),
          type: 'daterange'
        }
      ],
      isEdit: false,
      queryParam: {},
      messageExportParam: [],
      languageList: [],
      dynamicTableHeader: [],
      translationDialogFormVisible: false,
      translationForm: {
        sourceLanguage: 'zh_CN',
        targetLanguage: 'en_US'
      },
      translationRule: {
        sourceLanguage: [{ required: true, message: this.$t('common.pleaseSelect') }],
        targetLanguage: [{ required: true, message: this.$t('common.pleaseSelect') }]
      }
    }
  },
  async created () {
    this.dynamicTableHeader = await this.getTableDynamicHeader()
    await this.getLanguage()
    let _this = this
    this.tableHeader = [
      // 消息编码
      {
        prop: 'code',
        label: _this.$t('messageMaintenance.messageCode'),
        width: 150
      },
      // 模块
      {
        prop: 'module',
        label: _this.$t('messageMaintenance.busModule'),
        width: 100,
        dataType: 'dict',
        code: 'MODULE_SRM'
      },
      // 消息类型
      {
        prop: 'messageType',
        label: _this.$t('messageMaintenance.messageType'),
        width: 150,
        dataType: 'dict',
        code: 'SYSTEM_CODE_TYPE_SRM'
      },
      ...this.dynamicTableHeader,
      // 同步时间
      {
        prop: 'syncDate',
        label: _this.$t('messageMaintenance.asyncDataTime'),
        width: 100,
        dataType: 'dateTime'
      },
      // 状态
      {
        prop: 'status',
        label: _this.$t('messageMaintenance.status'),
        dataType: 'dict',
        code: 'MESSAGE_STATUE',
        width: 100
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: () => {
              return this.$t('common.edit')
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
    // 获取动态表头部分
    async getTableDynamicHeader () {
      try {
        const { data = [] } = await this.$http({
          url: '/api-base/syscode/dynamic/tableColumn',
          method: 'GET'
        })
        return data.map(i => ({
          prop: i.itemKey,
          label: i.dictItemName,
          width: 120
        }))
      } catch (err) {
        return []
      }
    },
    // 获取language
    async getLanguage () {
      const { data = [] } = await this.$http({
        url: '/api-base/dict/base-dict-language/listAll',
        method: 'POST'
      })
      this.langList = data.map(i => ({
        label: i.languageName,
        value: i.language
      }))
    },
    moduleChange (val, data) {

    },
    languageChange (val, data) {
      let row = this.langList.find(i => (i.value == val))
      if (row) {
        data.languageName = row.label
      } else {
        data.languageName = ''
      }
    },
    editMessagelanguageChange (val, data) {
      if (val) {
        let curLangArr = this.languageList.filter(i => i.dictItemCode == val)
        if (curLangArr.length > 1) {
          data.dictItemCode = ''
          return this.$message.warning(this.$t('cusEntry.supplement20250211.languageTypeRowMaintenance')) // '每个语言类型只能维护一行'
        }
        let row = this.langList.find(i => (i.value == val))
        if (row) {
          data.dictItemName = row.label
        } else {
          data.dictItemName = ''
        }
      } else {
        data.dictItemName = ''
      }
    },
    async translationFn () {
      let idArr = this.messageExportParam
      if (idArr.length > 0) {
        this.translationForm = {
          sourceLanguage: '',
          targetLanguage: ''
        }
        this.translationDialogFormVisible = true
      } else {
        this.$message.error(this.$t('messageMaintenance.selectTranslationItem'))
      }
    },
    // 确定翻译
    async comfirmTranslation () {
      let idArr = this.messageExportParam
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = await this.$http({
            url: '/api-base/syscode/i18n/translation',
            method: 'POST',
            returnDirectly: true,
            loading: true,
            data: {
              ...this.translationForm,
              idList: idArr
            }
          })
          if (res) {
            let messType = res.code == '0' ? 'success' : 'error'
            this.$message({
              type: messType,
              message: res.data.message
            })
          }
        } else {
          this.$message.error(this.$t('messageMaintenance.selectTranslationLang'))
        }
      })
    },
    // 消息提示导入模板下载
    dictHeaderDownload () {
      // 消息提示_模板导出
      downloadFileLink(
        '/api-base/syscode/importExcelTemplate',
        parseTime(new Date()) + this.$t('messageMaintenance.messageExportTemplate') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 导入成功
    dictHeaderhandleSuccess () {
      this.getQuerydata()
    },
    reset () {
      // 重置所有条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 弹框确认
    comfirmEdit () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-base/syscode/systemCode/modify',
            method: 'POST',
            data: {
              ...this.form,
              languageList: this.languageList
            }
          })
            .then(() => {
              this.dialogFormVisible = false
              this.$message({
                message: this.$t('common.successSave'), // '保存成功'
                type: 'success'
              })
              this.reset()
              this.getQuerydata()
            })
        } else {
          return false
        }
      })
    },

    getQuerydata (v = {}) {
      let { daterange = null, ...rest } = v
      this.queryParam = { ...rest }
      if (daterange) {
        this.queryParam = {
          ...rest,
          startTime: daterange[0],
          endTime: daterange[1]
        }
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    async editTab (type, row) {
      if (type == 'add') {
        // 新增
        this.isEdit = true
        for (let i in this.form) {
          this.form[i] = ''
        }
        this.languageList = []
      } else {
        // 修改
        let codeId = row.id
        const { data } = await this.$http({
          url: `/api-base/syscode/systemCode/detail?codeId=${codeId}`,
          method: 'GET'
        })
        const { languageList = [], ...rest } = data
        this.isEdit = true
        this.form = { ...rest }
        this.languageList = languageList
      }
      this.dialogFormVisible = true
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.messageExportParam = rowArr.map(i => i.id)
    },
    addMessageItem () {
      this.languageList.push({
        dictItemCode: '',
        dictItemName: '',
        message: '',
        customMessage: ''
      })
    },
    deleteMessage (scope) {
      let { row, $index } = scope
      this.languageList.splice($index, 1)
    }
  }
}
</script>
<style scoped lang="scss">
.the_dict_wrapper {
  border-top: 1px solid #eee;
  :deep(aside) {
    line-height: 16px !important;
  }
}
</style>
