<template>
  <el-container
    class="flex-container the-quick-demo"
    direction="vertical"
  >
    <el-main>
      <div style="padding:15px">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          :show-message="false"
          class="form-incontainer"
          :rules="rules"
        >
          <el-row>
            <el-col :span="8">
              <el-form-item
                :label="$t('bidMod.quickSearchName')"
                :label-width="formLabelWidth"
                prop="name"
              >
                <el-input
                  v-model="form.name"
                  :disabled="isDisabled"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item
                :label="$t('bidMod.queryMatchOperator')"
                :label-width="formLabelWidth"
                prop="queryMatchOperator"
              >
                <el-select
                  v-model="form.queryMatchOperator"
                  clearable
                >
                  <el-option
                    :label="$t('bidMod.allMatch')"
                    value="allMatch"
                  />
                  <el-option
                    :label="$t('bidMod.leftMatch')"
                    value="leftMatch"
                  />
                  <el-option
                    :label="$t('bidMod.rightMatch')"
                    value="rightMatch"
                  />
                  <el-option
                    :label="$t('bidMod.equalMatch')"
                    value="equalMatch"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item
                :label="$t('bidMod.queryModule')"
                :label-width="formLabelWidth"
                prop="queryModule"
              >
                <CDictSelect
                  v-model="form.queryModule"
                  code="MODULE_DIVISION"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('bidMod.quickSearchQueryTable')"
                :label-width="formLabelWidth"
                prop="queryTable"
              >
                <DictSelect
                  v-model="form.queryTable"
                  :code="form.queryModule"
                  custom-select-type="MODULE_TABLE_NAME"
                  @change="() => changeTableName(null)"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('bidMod.queryMaxSize')"
                :label-width="formLabelWidth"
                prop="queryMaxSize"
              >
                <el-input
                  v-model="form.queryMaxSize"
                  type="number"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('bidMod.valueAttr')"
                :label-width="formLabelWidth"
                prop="valueAttr"
              >
                <el-select
                  v-model="form.valueAttr"
                  placeholder
                  filterable
                  clearable
                  allow-create
                  default-first-option
                >
                  <el-option
                    v-for="item in tableColumn"
                    :key="item.columnName"
                    :label="item.columnName"
                    :value="item.columnName"
                  />
                </el-select>
                <span style="font-size: 12px;color:red">
                  <el-tooltip
                    effect="dark"
                    placement="top-start"
                    :content="$t('quickSearch.valueAttrTipDes')"
                  >
                    <em class="el-icon-warning">{{ $t('quickSearch.valueAttrTip') }}</em>
                  </el-tooltip>
                </span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item
                :label="$t('quickSearch.companyViewFlag')"
                prop="companyViewFlag"
                :label-width="formLabelWidth"
              >
                <dict-select
                  v-model="form.companyViewFlag"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('bidMod.quickSearchDescription')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.description"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item
                :label="$t('quickSearch.popSelectMode')"
                :label-width="formLabelWidth"
                prop="selectMode"
              >
                <el-select
                  v-model="form.selectMode"
                  clearable
                >
                  <el-option
                    :label="$t('quickSearch.reflect')"
                    value="REFLECT"
                  />
                  <el-option
                    label="SQL"
                    value="SQL"
                  />
                  <el-option
                    label="URL"
                    value="URL"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectClassName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectClassName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectMethodName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectMethodName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectPropertyName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectPropertyName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectMode === 'URL'"
              :span="24"
            >
              <el-form-item
                :label="$t('quickSearch.popHttpUrl')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.httpUrl"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row
            v-if="!form.selectMode || form.selectMode === 'SQL'"
            type="flex"
          >
            <el-col>
              <el-form-item
                :label="$t('bidMod.dialogQueryLanguage')"
                :label-width="formLabelWidth"
                prop="dialogQueryLanguage"
              >
                <el-input
                  v-model="form.dialogQueryLanguage"
                  type="textarea"
                  :showWordLimit="true"
                  :maxlength="4000"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item
                :label="$t('quickSearch.selectInputMode')"
                :label-width="formLabelWidth"
                prop="selectInputMode"
              >
                <el-select
                  v-model="form.selectInputMode"
                  clearable
                >
                  <el-option
                    :label="$t('quickSearch.reflect')"
                    value="REFLECT"
                  />
                  <el-option
                    label="SQL"
                    value="SQL"
                  />
                  <el-option
                    label="URL"
                    value="URL"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectInputMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectInputClassName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectInputClassName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectInputMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectInputMethodName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectInputMethodName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectInputMode === 'REFLECT'"
              :span="8"
            >
              <el-form-item
                :label="$t('quickSearch.reflectInputPropertyName')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.reflectInputPropertyName"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="form.selectInputMode === 'URL'"
              :span="24"
            >
              <el-form-item
                :label="$t('appRegister.inputHttpUrl')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.inputHttpUrl"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row
            v-if="!form.selectInputMode || form.selectInputMode === 'SQL'"
            type="flex"
          >
            <el-col>
              <el-form-item
                :label="$t('bidMod.queryLanguage')"
                :label-width="formLabelWidth"
                prop="queryLanguage"
              >
                <el-input
                  v-model="form.queryLanguage"
                  type="textarea"
                  :showWordLimit="true"
                  :maxlength="4000"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="!form.selectMode || form.selectMode === 'SQL'">
            <el-col :span="24">
              <!-- SQL查询语句 -->
              <div class="useDes">
                {{ $t('reportSetting.sqlUseDes') }}：<br>
                <span class="red">{{ $t('reportSetting.sqlUseDesPlaceHolder') }}</span>
                <span class="red">select * from base_dict where vendor_id = ${user.userId}</span>
              </div>
            </el-col>
            <el-col
              :span="24"
              class="placeholderItem"
            >
              <el-tag
                v-for="(placeholderItem, placeholderKey) in placeholderConfigArray"
                :key="placeholderKey"
              >
                {{ placeholderItem.label }}
              </el-tag>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <MainHeader :l-span="24">
        <template slot="left">
          {{ $t("bidMod.quickSearchCfg") }}
          <span class="red">{{ $t('quickSearch.comDes') }}</span>
        </template>
      </MainHeader>
      <MainHeader :l-span="24">
        <template slot="left">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addColumn()"
          >
            <!-- 新增字段 -->
            {{ $t('reportSetting.addColumn') }} </el-button>
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="removeColumn()"
          >
            <!-- 删除字段 -->
            {{ $t('reportSetting.removeColumn') }} </el-button>
        </template>
      </MainHeader>
      <TableEdit
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :show-filter-bar="showFilterBar === 1"
        :checkbox="true"
        :multi-commit="true"
        url="/admin/application/list"
        :auto-query="false"
        style="overflow:hidden;min-height:400px;margin-bottom: 55px;"
        @check-change="selectionChange"
      />
      <CToolbar>
        <template slot="right">
          <el-button
            type="primary"

            @click="resetOne"
          >
            {{ $t("common.reset") }}
          </el-button>
          <el-button
            type="primary"

            @click="submitOne"
          >
            {{ $t("common.submit") }}
          </el-button>
          <el-button
            v-if="showLanguageBtn"
            type="primary"

            @click="getQuickSearchLanguage"
          >
            <!-- 获取中文语言配置 -->
            {{ $t("bidMod.getZhConf") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
/* eslint-disable no-template-curly-in-string */
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import { transform } from 'lib@/utils/getQuickSearchLanguage'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableEdit from 'lib@/components/Table/TableEdit'
import CToolbar from 'lib@/components/c-toolbar'
import CDictSelect from '@/library/components/c-select/dict-select'
import { globalToolAPI } from 'modb@/basicSetting/api/basicSetting'

let seed = 0

function generateTemplateId () {
  const index = ++seed
  return `custom_file_id_${index}`
}
export default {
  name: 'QuickSearchConfigDetail',
  components: {
    CDictSelect,
    MainHeader,
    TableEdit,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      placeholderConfigArray: [
        { type: 'success', label: '${user.userId}' },
        { type: 'success', label: '${user.userName}' },
        { type: 'success', label: '${user.enabled}' },
        { type: 'success', label: '${user.nickname}' },
        { type: 'success', label: '${user.companyId}' },
        { type: 'success', label: '${user.companyCode}' },
        { type: 'success', label: '${user.companyName}' },
        { type: 'success', label: '${user.userType}' }
      ],
      form: {
        name: '',
        queryMatchOperator: 'allMatch',
        description: '',
        queryTable: '',
        queryMaxSize: '15',
        valueAttr: '',
        companyViewFlag: 'N',
        queryModule: '',
        queryLanguage: '',
        dialogQueryLanguage: '',
        quicksearchConfigId: '',
        selectMode: '',
        reflectClassName: '',
        reflectMethodName: '',
        reflectPropertyName: '',
        selectInputMode: '',
        reflectInputClassName: '',
        reflectInputMethodName: '',
        reflectInputPropertyName: '',
        inputHttpUrl: '',
        httpUrl: ''
      },
      resetForm: {},
      rules: {
        name: [{ required: true, trigger: 'change' }],
        queryMatchOperator: [{ required: true, change: 'change' }],
        queryTable: [{ required: true, trigger: 'change' }],
        queryModule: [{ required: true, trigger: 'change' }],
        queryMaxSize: [{ required: true, trigger: 'change' }],
        valueAttr: [{ required: true, change: 'change' }],
        queryLanguage: [{ required: true, trigger: 'change' }],
        dialogQueryLanguage: [{ required: true, trigger: 'change' }]
      },
      tableColumn: [],
      queryModuleList: [],
      isDisabled: this.$attrs.params.flag === 'edit',
      formLabelWidth: '120px',
      gridId: 'confList',
      pageSize: 15,
      dataCount: 0,
      queryTotal: -1,
      all: -1,
      tableData: [],
      showDialog: false,
      tableHeader: [],
      showFilterBar: 1,
      selectList: [],
      currentRow: ''
    }
  },
  computed: {
    showLanguageBtn () {
      return this.$route.query.showLanguageBtn === 'Y'
    }
  },
  created () {
    if (this.$attrs.params.flag === 'edit') {
      for (let i in this.form) {
        this.form[i] = this.$attrs.params.row[i] || ''
      }
      this.getDetail()
    }
    let _this = this
    this.tableHeader = [
      {
        prop: 'alias',
        label: _this.$t('bidMod.alias'),
        width: 60
      },
      {
        prop: 'attrOrder',
        label: _this.$t('bidMod.attrOrder'),
        width: 80
      },
      {
        prop: 'attr',
        label: _this.$t('bidMod.attr'),
        width: 180
      },
      {
        prop: 'title',
        label: _this.$t('bidMod.title'),
        width: 250
      },
      {
        prop: 'queryItemEnabled',
        label: _this.$t('bidMod.queryItemEnabled'),
        type: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        width: 70
      },
      {
        prop: 'displayItemEnabled',
        label: _this.$t('bidMod.displayItemEnabled'),
        type: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        width: 70
      },
      {
        prop: 'queryMatchOperator',
        label: _this.$t('bidMod.queryMatchOperator'),
        type: 'select',
        options: {
          queryMatchOperator: [
            { value: 'allMatch', label: this.$t('bidMod.allMatch') },
            { value: 'equalMatch', label: this.$t('bidMod.equalMatch') },
            { value: 'leftMatch', label: this.$t('bidMod.leftMatch') },
            { value: 'rightMatch', label: this.$t('bidMod.rightMatch') }
          ]
        }
      },
      {
        prop: 'dataType',
        label: _this.$t('dataConfMod.dbFieldType')
      },
      {
        prop: 'columnWidth',
        label: _this.$t('bidMod.columnWidth')
      },
      {
        prop: 'componentType',
        label: _this.$t('dataConfMod.componentType'),
        type: 'select',
        options: {
          componentType: [
            { value: 'DATE', label: this.$t('dataConfMod.time') }, // 时间
            { value: 'DOWNLOAD', label: this.$t('common.download') }, // 下载
            { value: 'DICTIONARY', label: this.$t('dataConfMod.dictionary') } // 字典
          ]
        }
      },
      {
        prop: 'componentProperty',
        label: _this.$t('dataConfMod.componentProperty')
      }
    ]

    // 模块下拉列表
    getDictItem('MODULE_DIVISION').then(res => {
      this.queryModuleList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    generateTemplateId () {
      return generateTemplateId()
    },
    getQuickSearchLanguage () {
      const { queryTable, description, name } = this.form
      transform(queryTable, description, name)
    },
    changeTableName (action) {
      globalToolAPI.listColumns({ module: this.form.queryModule, tableName: this.form.queryTable }).then(data => {
        if (!action) {
          // 查询表结构,  更新列表
          for (let i = 0; i < data.data.length; i++) {
            const item = data.data[i]
            item.$index = this.generateTemplateId()
            item.attr = item.columnName
            item.alias = 't'
            item.title = item.desc
          }
          this.$refs[this.gridId].tableData = data.data
        }
        this.tableColumn = data.data
        this.$nextTick(() => {
          this.$refs[this.gridId].tbDoLayout()
        })
      })
    },
    // 选择项变化
    selectionChange (selection) {
      this.selectList = selection
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    submitOne () {
      this.$refs.form.validate(valid => {
        if (valid) {
          let params = Object.assign({}, this.form)
          params.attrConfigs = this.$refs[this.gridId].tableData || []
          this.$http({
            url: '/api-base/quicksearch/quicksearchConfig/save',
            method: 'POST',
            data: params
          })
            .then(data => {
              if (data) {
                this.$message({
                  message: this.$t('common.successSave'), // "保存成功",
                  type: 'success'
                })
                if (this.$attrs.params.flag == 'edit') {
                  sessionStorage.removeItem('QS_' + this.form.name)
                  this.$emit(
                    'tab-remove',
                    'quickSearchConfigDetail' + this.$attrs.params.row.name
                  )
                } else {
                  this.$emit('tab-remove', 'quickSearchConfigDetail')
                }
                this.__setTabTodo('quickSearchConfigList.getQuerydata')
              }
            })
        } else {
          return false
        }
      })
    },
    addColumn () {
      this.$refs[this.gridId].tableData.push({
        $index: this.generateTemplateId()
      })
    },
    removeColumn () {
      for (let i = 0; i < this.selectList.length; i++) {
        for (let j = 0; j < this.$refs[this.gridId].tableData.length; j++) {
          if (this.selectList[i].$index === this.$refs[this.gridId].tableData[j].$index) {
            this.$refs[this.gridId].tableData.splice(j, 1)
            break
          }
        }
      }
    },
    resetOne () {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.$refs[this.gridId].tableData = []
    },
    // 详情
    getDetail () {
      this.$http({
        url: '/api-base/quicksearch/quicksearchConfig/getDetail',
        method: 'POST',
        data: { name: this.$attrs.params.row.name },
        loading: true
      })
        .then(data => {
          const list = data.data.attrConfigs || []
          for (let i = 0; i < list.length; i++) {
            const item = list[i]
            item.$index = this.generateTemplateId()
          }
          this.$refs[this.gridId].tableData = list // 列表
        })
        .then(() => {
          this.changeTableName('detail')
        })
    }
  }
}
</script>
<style scoped lang="scss">
.placeholderItem{
  padding: 10px 0;
  .el-tag{
    margin-right: 10px;
  }
}
.useDes{
  font-size: 12px;
  line-height: 20px;
  padding: 8px;
  background: #cdd5da;
  border-radius: 4px;
}
.red{color: red;}
</style>
