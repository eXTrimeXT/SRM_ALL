<template>
  <el-container
    class="flex-container-aside the_dict_wrapper"
    style="min-width:900px"
  >
    <el-aside
      width="50%"
      style="padding: 0px;padding-right:11px;"
    >
      <el-container
        class="flex-container-notab"
        direction="vertical"
      >
        <el-main>
          <FormWrapper
            :form-array="preArr"
            :col-length="colSpan"
            form-label-width="70px"
            @getFormData="getQuerydata"
          />

          <MainHeader
            :l-span="24"
            :r-span="0"
          >
            <template slot="left">
              <AuthorityButton
                type="primary"
                code="base:dictionaryMaintenance:add"
                @click="editTab('add')"
              >
                {{ $t('common.add') }}
              </AuthorityButton>
              <AuthorityButton
                type="primary"
                plain
                :disabled="!currentRow && currentHeaderRows.length == 0"
                code="base:dictionaryMaintenance:edit"
                @click="editTab"
              >
                {{ $t('common.edit') }}
              </AuthorityButton>
              <AuthorityButton
                type="primary"
                plain
                code="base:dictionaryMaintenance:delete"
                @click="batchDelete"
              >
                {{ $t('common.delete') }}
              </AuthorityButton>
              <el-tooltip effect="dark" :content="$t('common.checkExportTooltip')" placement="top">
                <ExportDirect
                  exprot-url="/api-base/dict/base-dict/leftExportExcelByModel"
                  :filter-params="dictHeaderExportParam"
                  requst-type="POST"
                  type="default"
                  code="base:dictionaryMaintenance:exportOne2"
                  :btn-text="$t('common.checkExport')"
                  :filename="$t('dataConfMod.dictHeaderFile') + '.xlsx'"
                />
              </el-tooltip>
              <MImport
                ref="import1"
                :title="$t('common.import')"
                up-load-url="/api-base/dict/base-dict/leftImportExcel"
                :extra-data="extraData"
                code="base:dictionaryMaintenance:importOne2"
                type="default"
                @downloadTemplate="dictHeaderDownload"
                @handleSuccess="dictHeaderhandleSuccess"
              />
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
            url="/api-base/dict/base-dict/queryPageByConditions"
            url-for-count="/isales-main/mstQuicksearchConfig/queryCount"
          />
          <!--      弹框区域-->
          <srm-dialog
            :title="$t('bidMod.dictManage')"
            :visible.sync="dialogFormVisible"
            size="middle"
            :close-on-click-modal="false"
          >
            <el-form
              ref="form"
              :model="form"
              class="form-incontainer"
              :rules="rules"
              label-width="80px"
              label-position="top"
            >
              <srm-row>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.dictCode')"
                    :label-width="formLabelWidth"
                    prop="dictCode"
                  >
                    <el-input
                      v-model="form.dictCode"
                      :disabled="isedited"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.dictlanguage')"
                    :label-width="formLabelWidth"
                    prop="language"
                  >
                    <el-select
                      v-model="form.language"
                      :disabled="isedited"
                      @change="(val) => languageChange(val,form)"
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
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.dictName')"
                    :label-width="formLabelWidth"
                    prop="dictName"
                  >
                    <el-input v-model="form.dictName" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.dictdescription')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.description" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.activeDate')"
                    :label-width="formLabelWidth"
                    prop="activeDate"
                  >
                    <el-date-picker
                      v-model="form.activeDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('common.pleaseSelectDate')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.inactiveDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.inactiveDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('common.pleaseSelectDate')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('bidMod.dictRole')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.dictRole">
                      <el-option
                        v-for="item in dictRoleList"
                        :key="item.roleId"
                        :label="item.roleName"
                        :value="'' + item.roleId"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <div
              slot="footer"
              class="dialog-footer"
            >
              <el-button @click="dialogFormVisible = false">
                {{ $t('common.cancel') }}
              </el-button>
              <el-button
                type="primary"
                @click="addOneDict"
              >
                {{ $t('common.confirm') }}
              </el-button>
            </div>
          </srm-dialog>
        </el-main>
      </el-container>
    </el-aside>
    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container-notab"
      direction="vertical"
    >
      <el-main>
        <FormWrapper
          :form-array="preArr2"
          :col-length="colSpan"
          form-label-width="70px"
          @getFormData="getQuerydata2"
        />

        <MainHeader
          :l-span="24"
          :r-span="0"
        >
          <template slot="left">
            <AuthorityButton
              type="primary"
              :disabled="!currentRow"
              code="base:dictionaryMaintenance:add2"
              @click="editTab2('add')"
            >
              {{ $t('common.add') }}
            </AuthorityButton>
            <AuthorityButton
              type="primary"
              :disabled="!currentRow2"
              plain
              code="base:dictionaryMaintenance:editTab2"
              @click="editTab2"
            >
              {{ $t('common.edit') }}
            </AuthorityButton>
            <ExportDirect
              :disabled="currentHeaderRows.length == 0"
              exprot-url="/api-base/dict/base-dict-item/rightExportExcel"
              :filter-params="dictHeaderExportParam"
              requst-type="POST"
              type="default"
              code="dictionaryMaintenance:dictItemExport"
              :btn-text="$t('common.export')"
              :filename="$t('dataConfMod.dictExItemFile') + '.xlsx'"
            />
            <MImport
              ref="import1"
              style="display: inline-block;margin-right: 10px;"
              :title="$t('common.import')"
              up-load-url="/api-base/dict/base-dict-item/rightImportExcel"
              :extra-data="extraData"
              type="default"
              code="dictionaryMaintenance:dictItemImport"
              @downloadTemplate="dictItemDownload"
              @handleSuccess="dictItemhandleSuccess"
            />
          </template>
        </MainHeader>
        <el-table
          :data="tableData2"
          border
          style="width: 100%"
          height="100%"
          highlight-current-row
          @current-change="handleCurrentChange2"
        >
          <el-table-column
            type="index"
            width="35"
          />
          <el-table-column
            prop="dictItemCode"
            :label="$t('bidMod.dictItemCode')"
            align="center"
            width="120"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            prop="itemLanguage"
            :label="$t('bidMod.itemLanguage')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <span>{{ $getLabelByValue(langList, scope.row.itemLanguage) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="dictItemName"
            :label="$t('bidMod.dictItemName')"
            align="center"
            width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            prop="itemDescription"
            :label="$t('bidMod.itemDescription')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            prop="dictItemNo"
            :label="$t('bidMod.dictItemNo')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            prop="dictItemMark"
            :label="$t('bidMod.dictItemMark')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            prop="activeDate"
            :label="$t('bidMod.activeDate')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              {{$parseTime(scope.row.activeDate)}}
            </template>
          </el-table-column>
          <el-table-column
            prop="inactiveDate"
            :label="$t('bidMod.inactiveDate')"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              {{$parseTime(scope.row.inactiveDate)}}
            </template>
          </el-table-column>
        </el-table>
        <!--      弹框区域-->
        <srm-dialog
          :title="$t('bidMod.dictItemManage')"
          :visible.sync="dialogFormVisible2"
          size="middle"
          :close-on-click-modal="false"
          top="2vh"
        >
          <el-form
            ref="form2"
            :model="form2"
            :rules="rules2"
            class="form-incontainer"
            label-width="80px"
            label-position="top"
          >
            <srm-row>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictCode')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form2.dictCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictlanguage')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="form2.language"
                    disabled
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
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictName')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form2.dictName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictdescription')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form2.description"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictItemCode')"
                  :label-width="formLabelWidth"
                  prop="dictItemCode"
                >
                  <el-input v-model="form2.dictItemCode" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.itemLanguage')"
                  :label-width="formLabelWidth"
                  prop="itemLanguage"
                >
                  <el-select
                    v-model="form2.itemLanguage"
                    disabled
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
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictItemName')"
                  :label-width="formLabelWidth"
                  prop="dictItemName"
                >
                  <el-input v-model="form2.dictItemName" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.itemDescription')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form2.itemDescription" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictItemNo')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form2.dictItemNo"
                    type="number"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.dictItemMark')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form2.dictItemMark" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.activeDate')"
                  :label-width="formLabelWidth"
                  prop="activeDate"
                >
                  <el-date-picker
                    v-model="form2.activeDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('bidMod.inactiveDate')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="form2.inactiveDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <div
            slot="footer"
            class="dialog-footer"
          >
            <el-button @click="dialogFormVisible2 = false">
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              type="primary"
              @click="addOneDictItem"
            >
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </srm-dialog>
      </el-main>
    </el-container>
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

export default {
  name: 'DictionaryMaintenance',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportDirect
  },
  provide () {
    return { tableName: this.name, defaultTableHeader: this.tableHeader }
  },
  data () {
    return {
      name: 'dictionaryTableList',
      extraData: {
        fileModular: 'base',
        fileFunction: 'dictionaryMaintenance',
        fileType: 'excel'
      },
      description: '',
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      currentHeaderRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      // 右边的字段
      tableData2: [],
      colSpan: 2,
      dialogFormVisible2: false,
      currentRow2: '',

      form2: {
        dictId: '',
        dictItemId: '',
        dictItemCode: '',
        dictItemName: '',
        itemDescription: '',
        itemLanguage: '',
        itemLanguageName: '',
        activeDate: '',
        inactiveDate: '',
        dictItemNo: '',
        dictItemMark: ''
      },
      form: {
        dictId: '',
        dictCode: '',
        dictName: '',
        description: '',
        language: '',
        languageName: '',
        activeDate: '',
        inactiveDate: '',
        dictRole: '',
        dictRoleName: ''
      },
      rules: {
        dictCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }], // '请输入字典编码'
        dictName: [{ required: true, message: this.$t('bidMod.msgDictName') }], // '请输入字典名称'
        language: [{ required: true, message: this.$t('bidMod.msgDictlanguage') }], // '请输入字典语言'
        activeDate: [{ required: true, message: this.$t('bidMod.msgActiveDate') }] // '请输入生效日期'
      },
      rules2: {
        dictItemCode: [{ required: true, message: this.$t('bidMod.msgDictItemCode') }], // '请输入字典条目编码'
        dictItemName: [{ required: true, message: this.$t('bidMod.msgDictItemName') }], // '请输入字典条目名称'
        itemLanguage: [{ required: true, message: this.$t('bidMod.msgItemLanguage') }], // '请输入字典条目语言'
        activeDate: [{ required: true, message: this.$t('bidMod.msgActiveDate') }] // '请输入条目生效日期'
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      langList: [],
      dictRoleList: [],
      preArr: [
        {
          prop: 'dictCode',
          label: () => this.$t('bidMod.dictCode') // '字典编码'
        },
        {
          prop: 'dictName',
          label: () => this.$t('bidMod.dictName') // '字典名称'
        },
        {
          prop: 'description',
          label: () => this.$t('bidMod.dictdescription') // '字典描述'
        },
        {
          prop: 'language',
          label: () => this.$t('bidMod.dictlanguage'),
          type: 'select', // '字典语言'
          options: [
            { label: this.$t('common.zh'), value: 'zh_CN' }, // '中文'
            { label: this.$t('common.ru'), value: 'ru_RU' } // '俄文'
          ]
        }
      ],
      preArr2: [
        {
          prop: 'dictItemCode',
          label: () => this.$t('bidMod.dictItemCode') // '条目编码'
        },
        {
          prop: 'dictItemName',
          label: () => this.$t('bidMod.dictItemName') // '条目名称'
        },
        {
          prop: 'itemDescription',
          label: () => this.$t('bidMod.itemDescription') // '条目描述'
        },
        {
          prop: 'itemLanguage',
          label: () => this.$t('bidMod.itemLanguage'),
          type: 'select', // '条目语言'
          options: [
            { label: this.$t('common.zh'), value: 'zh_CN' }, // '中文'
            { label: this.$t('common.ru'), value: 'ru_RU' } // '俄文'
          ]
        }
      ],
      isedited: false,
      queryParam: {},
      queryItemParam: {},
      querydictId: '',
      dictHeaderExportParam: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'dictCode', label: _this.$t('bidMod.dictCode'), width: 150 },
      {
        prop: 'language',
        label: _this.$t('bidMod.dictlanguage'),
        width: 100,
        formattor (val) {
          return _this.$getLabelByValue(_this.langList, val)
        }
      },
      { prop: 'dictName', label: _this.$t('bidMod.dictName'), width: 150 },
      {
        prop: 'description',
        label: _this.$t('bidMod.dictdescription'),
        width: 100
      },
      {
        prop: 'activeDate',
        label: _this.$t('bidMod.activeDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'inactiveDate',
        label: _this.$t('bidMod.inactiveDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'dictRoleName',
        label: _this.$t('bidMod.dictRoleName'),
        width: 100
      }
    ]
    this.getLanguage()
    this.getdictRoleList()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    languageChange (val, data) {
      let row = this.langList.find(i => (i.language == val))
      if (row) {
        data.languageName = row.languageName
      } else {
        data.languageName = ''
      }
    },
    // 字典头信息导入模板下载
    dictHeaderDownload () {
      // 字典头信息_模板导出.xlsx
      downloadFileLink(
        '/api-base/dict/base-dict/leftImportExcelTemplate',
        parseTime(new Date()) + this.$t('dataConfMod.dictHeaderImportFile') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 字典头信息导入成功
    dictHeaderhandleSuccess () {
      this.getQuerydata()
    },
    dictItemDownload () {
      downloadFileLink(
        '/api-base/dict/base-dict-item/rightImportExcelTemplate',
        parseTime(new Date()) + this.$t('dataConfMod.dictExItemImportFile') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    dictItemhandleSuccess () {
      this.getQuerydata2()
    },
    reset () {
      // 重置所有条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 批量删除
    batchDelete () {
      let idArr = this.dictHeaderExportParam
      if (idArr.length === 0) return this.$message.error(this.$t('dataConfMod.checkHeaderDictionary'))
      this.$confirm(this.$t('dataConfMod.sureDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/dict/base-dict/batchDelete',
            method: 'POST',
            data: idArr,
            loading: true
          })
            .then(() => {
              this.$message.success(this.$t('components.approvalHead.tips.approvalCompletion'))
              this.getQuerydata()
            })
        })
    },
    reset2 () {
      // 重置所有条件
      for (let i in this.form2) {
        this.form2[i] = ''
      }
    },
    addOneDict () {
      if (this.form.dictRole) {
        let dictRow = this.dictRoleList.filter(v => v.roleId == this.form.dictRole)[0]
        this.form.dictRoleName = dictRow['roleName'] || ''
      }
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-base/dict/base-dict/saveOrUpdateDict',
            method: 'POST',
            data: this.form
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
    addOneDictItem () {
      this.$refs.form2.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-base/dict/base-dict-item/saveOrUpdateDictItem',
            method: 'POST',
            data: {
              dictId: this.form2.dictId,
              dictItemId: this.form2.dictItemId,
              dictItemCode: this.form2.dictItemCode,
              dictItemName: this.form2.dictItemName,
              dictItemNo: this.form2.dictItemNo,
              itemLanguage: this.form2.itemLanguage,
              itemLanguageName: this.form2.itemLanguageName,
              itemDescription: this.form2.itemDescription,
              activeDate: this.form2.activeDate,
              inactiveDate: this.form2.inactiveDate,
              dictItemMark: this.form2.dictItemMark
            }
          })
            .then(() => {
              this.dialogFormVisible2 = false
              this.$message({
                message: this.$t('common.successSave'), // '保存成功',
                type: 'success'
              })
              this.reset2()
              this.getQuerydata2()
            })
        } else {
          return false
        }
      })
    },
    queryDictItem () {
      if (!this.querydictId) {
        this.$message.warning(this.$t('dataConfMod.selectDict'))
        return false
      }
      let params = Object.assign(
        { dictId: this.querydictId || '', pageNum: 1, pageSize: 10000 },
        this.queryItemParam
      )
      this.$http({
        url: '/api-base/dict/base-dict-item/queryPageByConditions',
        method: 'POST',
        data: params
      })
        .then(data => {
          this.tableData2 = data.data ? data.data.list : []
        })
    },
    getLanguage () {
      // 获取language
      this.$http({
        url: '/api-base/dict/base-dict-language/listAll',
        method: 'POST',
        data: {}
      })
        .then(data => {
          this.langList = data.data
        })
    },
    getdictRoleList () {
      // 获取管理角色
      this.$http({
        url: '/api-rbac/role/role/listSelect',
        method: 'GET',
        params: {}
      })
        .then(data => {
          this.dictRoleList = data.data
        })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.tableData2 = [] // 清理右边的条目数据
      })
    },
    // 编辑tab
    editTab (type) {
      if (type == 'add') {
        // 新增
        this.isedited = false
        for (let i in this.form) {
          this.form[i] = ''
        }
        this.form.activeDate = parseTime(new Date(), '{y}-{m}-{d}', true)
      } else {
        // 修改
        if (this.currentHeaderRows.length > 1) {
          this.$message.error(this.$t('dataConfMod.onlyEditOneData'))
          return
        }
        this.isedited = true
        let editRow = this.currentRow || this.currentHeaderRows[0]
        for (let i in this.form) {
          this.form[i] = editRow[i]
        }
      }
      this.dialogFormVisible = true
    },
    handleCurrentChange (val) {
      this.currentRow = val
      if (val && val.dictId) {
        this.querydictId = val.dictId
        this.queryDictItem()
      }
    },
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.dictHeaderExportParam = rowArr.map(i => i.dictId)
    },
    handleCurrentChange2 (val) {
      this.currentRow2 = val
    },
    // youbian
    editTab2 (type) {
      if (type == 'add') {
        // 新增
        for (let i in this.form2) {
          this.form2[i] = ''
        }
      } else {
        // 修改
        for (let i in this.form2) {
          this.form2[i] = this.currentRow2[i]
        }
      }
      this.form2.dictId = this.currentRow.dictId
      this.form2.dictCode = this.currentRow.dictCode
      this.form2.dictName = this.currentRow.dictName
      this.form2.description = this.currentRow.description
      this.form2.language = this.currentRow.language
      this.form2.languageName = this.currentRow.languageName
      this.form2.itemLanguage = this.currentRow.language
      this.form2.itemLanguageName = this.currentRow.languageName
      this.dialogFormVisible2 = true
    },
    getQuerydata2 (v) {
      this.queryItemParam = v
      this.queryDictItem()
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
