<template>
  <el-container
    class="formpageEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <FormPageEdit
          ref="formPage"
          v-model="form"
          :page-model="pageModel"
          @changeTableName="tableName => changeTableName(tableName, configType)"
        />
        <el-tabs
          v-model="configType"
          type="card"
        >
          <el-tab-pane
            v-if="pageModel === 'COMMON' || pageModel === 'REPORT'"
            :label="$t('reportSetting.queryMent')"
            name="QUERY"
          >
            <el-card class="box-card" shadow="never">
              <div
                slot="header"
                class="clearfix"
              >
                <span>{{ $t('reportSetting.querySetting') }}</span>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                  @click="editDetailItem(selectGlobalInfo)"
                >
                  {{ $t('reportSetting.editDetail') }} </el-button>
              </div>
              <div>
                <FormQueryEdit v-model="selectGlobalInfo" />
              </div>
            </el-card>

            <el-row
              :gutter="32"
              style="margin-top: 5px;margin-bottom: 50px;"
            >
              <el-col :span="24">
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="addAttribute()"
                >
                  {{ $t('bidMod.affairsIncreased') }}
                </el-button>
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="deleteSelect()"
                >
                  {{ $t('reportSetting.deleteSelected') }} </el-button>
                <BaseTableBind
                  ref="selectAttribute"
                  v-model="selectAttributeDataList"
                  :columns="selectAttributeColumns"
                  :initialize="false"
                  row-key="formAttributeId"
                  selection
                  border
                  :empty-text="$t('reportSetting.addTip')"
                  style="height:400px;"
                  @selection-change="selectionChange"
                >
                  <template #columnName="{ scope }">
                    <el-input v-model="scope.row.columnName" />
                  </template>
                  <template #propertyName="{ scope }">
                    <el-input v-model="scope.row.propertyName" />
                  </template>
                  <template #propertyDescribe="{ scope }">
                    <el-input v-model="scope.row.propertyDescribe" />
                  </template>
                  <template #sort="{ scope }">
                    <el-input v-model="scope.row.sort" />
                  </template>
                  <template #languageCode="{ scope }">
                    <el-input v-model="scope.row.languageCode" />
                  </template>
                  <template #parentAttributeId="{ scope }">
                    <DictSelect
                      v-model="scope.row.parentAttributeId"
                      :dict-class="dictClass"
                      code="SELECT_FORM_ATTRIBUTE_ID"
                    />
                  </template>
                  <template #parentSlotName="{ scope }">
                    <el-input v-model="scope.row.parentSlotName" />
                  </template>
                  <template #componentType="{ scope }">
                    <DictSelect
                      v-model="scope.row.componentType"
                      code="FORM_COMPONENT_TYPE"
                      @change="componentType => changeComponentType(scope.row, componentType)"
                    />
                  </template>
                  <template #elementTag="{ scope }">
                    <el-input v-model="scope.row.elementTag" />
                  </template>
                  <template #notNull="{ scope }">
                    <el-switch
                      v-model="scope.row.notNull"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #viewable="{ scope }">
                    <el-switch
                      v-model="scope.row.viewable"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #modelBind="{ scope }">
                    <DictSelect
                      v-model="scope.row.modelBind"
                      code="YES_OR_NO"
                    />
                  </template>
                  <template #modelType="{ scope }">
                    <el-input v-model="scope.row.modelType" />
                  </template>
                  <template #maxLength="{ scope }">
                    <el-input-number v-model="scope.row.maxLength" />
                  </template>
                  <template #minLength="{ scope }">
                    <el-input-number v-model="scope.row.minLength" />
                  </template>
                  <template #placeholder="{ scope }">
                    <el-input v-model="scope.row.placeholder" />
                  </template>
                  <template #dataType="{ scope }">
                    <el-input v-model="scope.row.dataType" />
                  </template>
                  <template #dictCode="{ scope }">
                    <el-input v-model="scope.row.dictCode" />
                  </template>
                  <template #formSpan="{ scope }">
                    <el-input-number v-model="scope.row.formSpan" />
                  </template>
                </BaseTableBind>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('reportSetting.functionHeadMent')"
            name="MAIN_HEADER"
          >
            <el-card class="box-card" shadow="never">
              <div
                slot="header"
                class="clearfix"
              >
                <span>{{ $t('reportSetting.functionGlobalSetting') }}</span>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                  @click="editDetailItem(headerGlobalInfo)"
                >
                  {{ $t('reportSetting.editDetail') }} </el-button>
              </div>
              <div>
                <FormQueryEdit v-model="headerGlobalInfo" />
              </div>
            </el-card>

            <el-row
              :gutter="32"
              style="margin-top: 5px;margin-bottom: 50px;"
            >
              <el-col :span="24">
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="addAttribute()"
                >
                  {{ $t('bidMod.affairsIncreased') }}
                </el-button>
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="deleteSelect()"
                >
                  {{ $t('reportSetting.deleteSelected') }}
                </el-button>
                <BaseTableBind
                  ref="headerAttribute"
                  v-model="headerAttributeDataList"
                  :columns="headerAttributeColumns"
                  :initialize="false"
                  row-key="formAttributeId"
                  selection
                  border
                  :empty-text="$t('reportSetting.addTip')"
                  style="height:400px;"
                  @selection-change="selectionChange"
                >
                  <template #columnName="{ scope }">
                    <el-input v-model="scope.row.columnName" />
                  </template>
                  <template #propertyName="{ scope }">
                    <el-input v-model="scope.row.propertyName" />
                  </template>
                  <template #propertyDescribe="{ scope }">
                    <el-input v-model="scope.row.propertyDescribe" />
                  </template>
                  <template #sort="{ scope }">
                    <el-input v-model="scope.row.sort" />
                  </template>
                  <template #languageCode="{ scope }">
                    <el-input v-model="scope.row.languageCode" />
                  </template>
                  <template #parentAttributeId="{ scope }">
                    <DictSelect
                      v-model="scope.row.parentAttributeId"
                      :dict-class="dictClass"
                      code="TABLE_FORM_ATTRIBUTE_ID"
                    />
                  </template>
                  <template #parentSlotName="{ scope }">
                    <el-input v-model="scope.row.parentSlotName" />
                  </template>
                  <template #componentType="{ scope }">
                    <DictSelect
                      v-model="scope.row.componentType"
                      code="FORM_COMPONENT_TYPE"
                      @change="componentType => changeComponentType(scope.row, componentType)"
                    />
                  </template>
                  <template #elementTag="{ scope }">
                    <el-input v-model="scope.row.elementTag" />
                  </template>
                  <template #notNull="{ scope }">
                    <el-switch
                      v-model="scope.row.notNull"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #viewable="{ scope }">
                    <el-switch
                      v-model="scope.row.viewable"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #modelBind="{ scope }">
                    <DictSelect
                      v-model="scope.row.modelBind"
                      code="YES_OR_NO"
                    />
                  </template>
                  <template #modelType="{ scope }">
                    <el-input v-model="scope.row.modelType" />
                  </template>
                  <template #maxLength="{ scope }">
                    <el-input-number v-model="scope.row.maxLength" />
                  </template>
                  <template #minLength="{ scope }">
                    <el-input-number v-model="scope.row.minLength" />
                  </template>
                  <template #placeholder="{ scope }">
                    <el-input v-model="scope.row.placeholder" />
                  </template>
                  <template #dataType="{ scope }">
                    <el-input v-model="scope.row.dataType" />
                  </template>
                  <template #dictCode="{ scope }">
                    <el-input v-model="scope.row.dictCode" />
                  </template>
                  <template #formSpan="{ scope }">
                    <el-input-number v-model="scope.row.formSpan" />
                  </template>
                </BaseTableBind>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('reportSetting.tableMent')"
            name="TABLE"
          >
            <el-card class="box-card" shadow="never">
              <div
                slot="header"
                class="clearfix"
              >
                <span>{{ $t('reportSetting.tableGlobalSetting') }}</span>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                  @click="editDetailItem(tableGlobalInfo)"
                >
                  {{ $t('reportSetting.editDetail') }}
                </el-button>
              </div>
              <div>
                <FormQueryEdit v-model="tableGlobalInfo" />
              </div>
            </el-card>

            <el-row
              :gutter="32"
              style="margin-top: 5px;margin-bottom: 50px;"
            >
              <el-col :span="24">
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="addAttribute()"
                >
                  {{ $t('bidMod.affairsIncreased') }}
                </el-button>
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="deleteSelect()"
                >
                  {{ $t('reportSetting.deleteSelected') }} </el-button>
                <BaseTableBind
                  ref="tableAttribute"
                  v-model="tableAttributeDataList"
                  :columns="tableAttributeColumns"
                  :initialize="false"
                  row-key="formAttributeId"
                  selection
                  border
                  :empty-text="$t('reportSetting.addTip')"
                  style="height:400px;"
                  @selection-change="selectionChange"
                >
                  <template #columnName="{ scope }">
                    <el-input v-model="scope.row.columnName" />
                  </template>
                  <template #propertyName="{ scope }">
                    <el-input v-model="scope.row.propertyName" />
                  </template>
                  <template #propertyDescribe="{ scope }">
                    <el-input v-model="scope.row.propertyDescribe" />
                  </template>
                  <template #sort="{ scope }">
                    <el-input v-model="scope.row.sort" />
                  </template>
                  <template #languageCode="{ scope }">
                    <el-input v-model="scope.row.languageCode" />
                  </template>
                  <template #parentAttributeId="{ scope }">
                    <DictSelect
                      v-model="scope.row.parentAttributeId"
                      :dict-class="dictClass"
                      code="TABLE_FORM_ATTRIBUTE_ID"
                    />
                  </template>
                  <template #parentSlotName="{ scope }">
                    <el-input v-model="scope.row.parentSlotName" />
                  </template>
                  <template #componentType="{ scope }">
                    <DictSelect
                      v-model="scope.row.componentType"
                      code="FORM_COMPONENT_TYPE"
                      @change="componentType => changeComponentType(scope.row, componentType)"
                    />
                  </template>
                  <template #elementTag="{ scope }">
                    <el-input v-model="scope.row.elementTag" />
                  </template>
                  <template #notNull="{ scope }">
                    <el-switch
                      v-model="scope.row.notNull"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #viewable="{ scope }">
                    <el-switch
                      v-model="scope.row.viewable"
                      active-color="#13ce66"
                      inactive-color="#dcdfe6"
                    />
                  </template>
                  <template #modelBind="{ scope }">
                    <DictSelect
                      v-model="scope.row.modelBind"
                      code="YES_OR_NO"
                    />
                  </template>
                  <template #modelType="{ scope }">
                    <el-input v-model="scope.row.modelType" />
                  </template>
                  <template #maxLength="{ scope }">
                    <el-input-number v-model="scope.row.maxLength" />
                  </template>
                  <template #minLength="{ scope }">
                    <el-input-number v-model="scope.row.minLength" />
                  </template>
                  <template #placeholder="{ scope }">
                    <el-input v-model="scope.row.placeholder" />
                  </template>
                  <template #dataType="{ scope }">
                    <el-input v-model="scope.row.dataType" />
                  </template>
                  <template #dictCode="{ scope }">
                    <el-input v-model="scope.row.dictCode" />
                  </template>
                  <template #formSpan="{ scope }">
                    <el-input-number v-model="scope.row.formSpan" />
                  </template>
                </BaseTableBind>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            v-if="openMethod"
            :label="$t('reportSetting.functionMent')"
            name="METHOD"
          >
            <el-row :gutter="32">
              <el-col :span="24">
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="addMethod()"
                >
                  {{ $t('bidMod.affairsIncreased') }}
                </el-button>
                <TableView
                  ref="methodView"
                  :table-header="methodAttributeColumns"
                  row-key="formLineId"
                  :page-enabled="false"
                />
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="addInfo"
          >
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>

    <FormApiEdit
      :visible="api.visible"
      :total-config="api.totalConfig"
      @confirm="saveConfig"
      @cancel="api.visible = false"
    />
    <FormMethodEdit
      :visible="formMethod.visible"
      :type="formMethod.type"
      :form-method-config="formMethod.data"
      @confirm="saveFormMethod"
      @cancel="formMethod.visible = false"
    />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import EditOperation from '@/library/mixins/edit-operation'
import { globalToolAPI, formPageAPI } from 'modb@/basicSetting/api/basicSetting'
import camelCase from 'lodash/camelCase'
import TableView from 'lib@/components/Table/TableView'
import FormApiEdit from 'modb@/basicSetting/views/formPage/components/FormApiEdit'
import FormMethodEdit from 'modb@/basicSetting/views/formPage/components/FormMethodEdit'
import FormPageEdit from 'modb@/basicSetting/views/formPage/components/FormPageEdit'
import FormQueryEdit from 'modb@/basicSetting/views/formPage/components/FormQueryEdit'
import { createDictClass } from '@/library/utils/dict/dict-utils'

import BaseTableBind from '@/library/components/BaseTable/BaseTableBind'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from '@/library/components/BaseTable/utils'
const dictClass = createDictClass()
export default {
  name: 'EditReport',
  components: {
    TableView,
    CToolbar,
    FormApiEdit,
    FormMethodEdit,
    FormPageEdit,
    FormQueryEdit,
    BaseTableBind
  },
  mixins: [tabTodoMixin, EditOperation],
  data () {
    return {
      // 文件上传配置信息
      configInfo: {
        listName: 'formPageList'
      },
      apiConfig: {
        updateApi: formPageAPI.saveOrUpdate,
        addApi: formPageAPI.saveOrUpdate,
        updateTempApi: formPageAPI.saveOrUpdate,
        addTempApi: formPageAPI.saveOrUpdate
      },
      pageModel: null,
      form: {
        formPageId: null,
        pageCode: null,
        pageName: null,
        pageModel: null,
        pageDescribe: null,
        bizModule: null,
        tableName: null,
        needCheck: null,
        attributeDataList: [],
        methodList: []
      },
      dictClass: dictClass,

      configType: 'QUERY',
      globalAttributeId: 2,

      selectGlobalInfo: {
        formAttributeId: 1,
        parentAttributeId: -1
      },
      selectAttributeDataList: [],
      selectAttributeColumns: [],
      selectSelectionData: [],

      tableGlobalInfo: {
        formAttributeId: 2,
        parentAttributeId: -1
      },
      tableAttributeDataList: [],
      tableAttributeColumns: [],
      tableSelectionData: [],

      headerGlobalInfo: {
        formAttributeId: 2,
        parentAttributeId: -1
      },
      headerAttributeDataList: [],
      headerAttributeColumns: [],
      headerSelectionData: [],

      methodDataList: [],
      methodAttributeColumns: [],
      rules: {},
      readOnly: false,
      api: {
        visible: false,
        totalConfig: {},
        row: null
      },
      formMethod: {
        visible: false,
        type: null,
        data: {}
      }
    }
  },
  computed: {
    openMethod () {
      return this.pageModel !== 'SCENE_ATTACHMENT'
    }
  },
  watch: {
    selectAttributeDataList: {
      handler () {
        const dictionary = []
        dictionary.push({
          id: this.selectGlobalInfo.formAttributeId.toString(),
          value: this.selectGlobalInfo.formAttributeId.toString(),
          label: this.selectGlobalInfo.propertyDescribe ? this.selectGlobalInfo.propertyDescribe : this.selectGlobalInfo.elementTag,
          disabled: false
        })
        for (let i = 0; i < this.selectAttributeDataList.length; i++) {
          const item = this.selectAttributeDataList[i]
          dictionary.push({
            id: item.formAttributeId.toString(),
            value: item.formAttributeId.toString(),
            label: item.propertyDescribe,
            disabled: false
          })
        }
        this.dictClass.setDictionary('SELECT_FORM_ATTRIBUTE_ID', dictionary)
      },
      deep: true
    },
    tableAttributeDataList: {
      handler () {
        const dictionary = []
        dictionary.push({
          id: this.tableGlobalInfo.formAttributeId.toString(),
          value: this.tableGlobalInfo.formAttributeId.toString(),
          label: this.tableGlobalInfo.propertyDescribe ? this.tableGlobalInfo.propertyDescribe : this.tableGlobalInfo.elementTag,
          disabled: false
        })
        for (let i = 0; i < this.tableAttributeDataList.length; i++) {
          const item = this.tableAttributeDataList[i]
          dictionary.push({
            id: item.formAttributeId.toString(),
            value: item.formAttributeId.toString(),
            label: item.propertyDescribe,
            disabled: false
          })
        }
        this.dictClass.setDictionary('TABLE_FORM_ATTRIBUTE_ID', dictionary)
      },
      deep: true
    },
    headerAttributeDataList: {
      handler () {
        const dictionary = []
        dictionary.push({
          id: this.headerGlobalInfo.formAttributeId.toString(),
          value: this.headerGlobalInfo.formAttributeId.toString(),
          label: this.headerGlobalInfo.propertyDescribe ? this.headerGlobalInfo.propertyDescribe : this.headerGlobalInfo.elementTag,
          disabled: false
        })
        for (let i = 0; i < this.headerAttributeDataList.length; i++) {
          const item = this.headerAttributeDataList[i]
          dictionary.push({
            id: item.formAttributeId.toString(),
            value: item.formAttributeId.toString(),
            label: item.propertyDescribe,
            disabled: false
          })
        }
        this.dictClass.setDictionary('HEADER_FORM_ATTRIBUTE_ID', dictionary)
      },
      deep: true
    }
  },
  created () {
    let columns = [
      { align: 'center', prop: 'columnName', label: this.$t('reportSetting.columnName'), editable: true, slot: 'columnName', minWidth: '180' },
      { align: 'center', prop: 'propertyName', label: this.$t('reportSetting.propertyName'), editable: true, slot: 'propertyName', minWidth: '180' },
      { align: 'center', prop: 'propertyDescribe', label: this.$t('reportSetting.propertyDescribe'), editable: true, slot: 'propertyDescribe', minWidth: '180' },
      { align: 'center', prop: 'sort', label: this.$t('components.viewConfig.seq'), editable: true, slot: 'sort', minWidth: '180' },
      { align: 'center', prop: 'languageCode', label: () => this.$t('components.viewConfig.languageCode'), editable: true, slot: 'languageCode', minWidth: '180' },
      { align: 'center', prop: 'componentType', label: this.$t('dataConfMod.componentType'), editable: true, slot: 'componentType', minWidth: '180' },
      { align: 'center', prop: 'elementTag', label: this.$t('reportSetting.elementTag'), editable: true, slot: 'elementTag', minWidth: '180' },
      { align: 'center', prop: 'notNull', label: this.$t('dataConfMod.isRequested'), editable: true, slot: 'notNull', minWidth: '180' },
      { align: 'center', prop: 'viewable', label: this.$t('contractMod.WhetherToDisplay'), editable: true, slot: 'viewable', minWidth: '180' },
      { align: 'center', prop: 'maxLength', label: this.$t('reportSetting.maxLength'), editable: true, slot: 'maxLength', minWidth: '180' },
      { align: 'center', prop: 'minLength', label: this.$t('reportSetting.minLength'), editable: true, slot: 'minLength', minWidth: '180' },
      { align: 'center', prop: 'placeholder', label: this.$t('reportSetting.placeholder'), editable: true, slot: 'placeholder', minWidth: '180' },
      { align: 'center', prop: 'dataType', label: this.$t('reportSetting.dataType'), editable: true, slot: 'dataType', minWidth: '180' },
      { align: 'center', prop: 'dictCode', label: this.$t('reportSetting.dictCode'), editable: true, slot: 'dictCode', minWidth: '180' },
      { align: 'center', prop: 'formSpan', label: this.$t('reportSetting.formSpan'), editable: true, slot: 'formSpan', minWidth: '180' },
      { align: 'center',
        prop: 'tableOperations',
        label: this.$t('components.viewConfig.operation'),
        minWidth: '180',
        operations: [
          {
            event: 'editDetailItem',
            name: this.$t('common.edit'),
            func: rowData => this.editDetailItem(rowData.row)
          },
          {
            event: 'deleteItem',
            name: this.$t('common.delete'),
            func: this.deleteItem
          }
        ]
      }
    ]
    this.methodAttributeColumns = [
      { align: 'center', prop: 'formLineId', label: this.$t('reportSetting.formLineId'), editable: true, minWidth: '180' },
      { align: 'center', prop: 'methodName', label: this.$t('reportSetting.methodName'), editable: true, slot: 'methodName', minWidth: '180' },
      { align: 'center', prop: 'methodDescribe', label: this.$t('reportSetting.methodDescribe'), editable: true, slot: 'methodDescribe', minWidth: '180' },
      { align: 'center', prop: 'methodParams', label: this.$t('reportSetting.methodParams'), editable: true, slot: 'methodParams', minWidth: '180' },
      { align: 'center', prop: 'methodBody', label: this.$t('reportSetting.methodBody'), editable: true, slot: 'methodBody', minWidth: '180' },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '220',
        // fixed: "right",
        showType: 'buttons',
        buttons: [
          {
            callback: (row, scope) => this.viewMethod(row, scope),
            formattor: () =>
              '<i class="el-icon-tickets"></i> ' +
              this.$t('common.view') // 查看
          },
          {
            callback: (row, scope) => this.editMethod(row, scope),
            formattor: () =>
              '<i class="el-icon-edit"></i> ' + this.$t('common.edit') // 修改
          },
          {
            callback: (row, scope) => this.deleteMethod(row, scope),
            formattor: () =>
              '<i class="el-icon-delete"></i> ' + this.$t('common.delete') // 删除
          }
        ]
      }
    ]

    for (let i = 0; i < columns.length; i++) {
      this.selectAttributeColumns.push(Object.assign({}, columns[i]))
      this.tableAttributeColumns.push(Object.assign({}, columns[i]))
      this.headerAttributeColumns.push(Object.assign({}, columns[i]))
    }
  },
  mounted () {
    const { flag, row, pageModel, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'add' || flag === 'edit') {
      this.form.pageModel = pageModel
      this.pageModel = pageModel
    }
    if (pageModel === 'SCENE_ATTACHMENT') {
      if (flag === 'add') {
        this.form.bizModule = 'BASE'
        this.loadColumns('scc_base_scene_file', this.tableGlobalInfo.formAttributeId, this.tableAttributeDataList)
      }
      this.configType = 'MAIN_HEADER'
    }
    if (flag === 'edit' || flag === 'view') {
      formPageAPI.getDetail(row.formPageId).then(res => {
        let formTemp = res.data
        if (!formTemp.formAttributeList) {
          formTemp.formAttributeList = []
        }

        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          attrItem[EDITABLE_KEY] = false
          attrItem[ADD_KEY] = false
          attrItem[UPDATE_KEY] = false

          if (attrItem.componentType === 'MAIN_HEADER' && attrItem.parentAttributeId === -1) {
            this.headerGlobalInfo = attrItem
          } else if (attrItem.componentType === 'FORM_WRAPPER' && attrItem.parentAttributeId === -1) {
            this.selectGlobalInfo = attrItem
          } else if (attrItem.componentType === 'TABLE_VIEW' && attrItem.parentAttributeId === -1) {
            this.tableGlobalInfo = attrItem
          }
        }
        const selectFormAttributeId = this.selectGlobalInfo ? this.selectGlobalInfo.formAttributeId : null
        const tableFormAttributeId = this.tableGlobalInfo ? this.tableGlobalInfo.formAttributeId : null
        const headerFormAttributeId = this.headerGlobalInfo ? this.headerGlobalInfo.formAttributeId : null

        let waitList = []
        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          if (attrItem.parentAttributeId === selectFormAttributeId) {
            this.selectAttributeDataList.push(attrItem)
          } else if (attrItem.parentAttributeId === tableFormAttributeId) {
            this.tableAttributeDataList.push(attrItem)
          } else if (attrItem.parentAttributeId === headerFormAttributeId) {
            this.headerAttributeDataList.push(attrItem)
          } else {
            waitList.push(attrItem)
          }
        }
        while (waitList.length > 0) {
          const tempList = []
          for (let i = 0; i < waitList.length; i++) {
            const attrItem = waitList[i]
            let existSelect = false
            for (let j = 0; j < this.selectAttributeDataList.length; j++) {
              const selectItem = this.selectAttributeDataList[j]
              if (selectItem.formAttributeId === attrItem.parentAttributeId) {
                existSelect = true
                break
              }
            }
            if (existSelect) {
              this.selectAttributeDataList.push(attrItem)
              continue
            }

            let existTable = false
            for (let j = 0; j < this.tableAttributeDataList.length; j++) {
              const tableItem = this.tableAttributeDataList[j]
              if (tableItem.formAttributeId === attrItem.parentAttributeId) {
                existTable = true
                break
              }
            }
            if (existTable) {
              this.tableAttributeDataList.push(attrItem)
              continue
            }

            let existHeader = false
            for (let j = 0; j < this.headerAttributeDataList.length; j++) {
              const headerItem = this.headerAttributeDataList[j]
              if (headerItem.formAttributeId === attrItem.parentAttributeId) {
                existHeader = true
                break
              }
            }
            if (existHeader) {
              this.headerAttributeDataList.push(attrItem)
              continue
            } else {
              tempList.push(attrItem)
            }
          }
          if (tempList.length === waitList.length) {
            break
          }
          waitList = tempList
        }

        this.methodDataList = formTemp.methodList

        this.setMethodTableData(this.methodDataList)

        this.form = formTemp
      })
    }
  },
  methods: {
    setMethodTableData (tableData) {
      if (this.openMethod) {
        this.$refs.methodView.tableData = tableData
      }
    },
    addMethod () {
      this.formMethod.visible = true
      this.formMethod.type = 'add'
      this.formMethod.data = {
        index: null,
        formLineId: null,
        lineType: 'METHOD',
        methodName: null,
        methodDescribe: null,
        methodParams: null,
        methodBody: null
      }
    },
    saveFormMethod (methodData, type) {
      if (type === 'edit') {
        this.methodDataList[methodData.index].methodName = methodData.methodName
        this.methodDataList[methodData.index].methodDescribe = methodData.methodDescribe
        this.methodDataList[methodData.index].methodParams = methodData.methodParams
        this.methodDataList[methodData.index].methodBody = methodData.methodBody
      } else {
        this.methodDataList.push(methodData)
      }
      this.setMethodTableData(this.methodDataList)
      this.formMethod.visible = false
    },
    viewMethod (row, scope) {
      this.formMethod.type = 'view'
      this.formMethod.data = Object.assign({}, row, { index: scope.$index })
      this.formMethod.visible = true
    },
    editMethod (row, scope) {
      this.formMethod.type = 'edit'
      this.formMethod.data = Object.assign({}, row, { index: scope.$index })
      this.formMethod.visible = true
    },
    deleteMethod (row, scope) {
      this.methodDataList.splice(scope.$index, 1)
      this.setMethodTableData(this.methodDataList)
    },
    deleteItem (rowObj) {
      if (this.configType === 'QUERY') {
        const removeFormAttributeId = rowObj.row.formAttributeId
        this.selectAttributeDataList = this.selectAttributeDataList.filter(item => item.formAttributeId !== removeFormAttributeId)
      } else if (this.configType === 'TABLE') {
        const removeFormAttributeId = rowObj.row.formAttributeId
        this.tableAttributeDataList = this.tableAttributeDataList.filter(item => item.formAttributeId !== removeFormAttributeId)
      } else if (this.configType === 'MAIN_HEADER') {
        const removeFormAttributeId = rowObj.row.formAttributeId
        this.headerAttributeDataList = this.headerAttributeDataList.filter(item => item.formAttributeId !== removeFormAttributeId)
      }
    },
    deleteSelect () {
      if (this.configType === 'QUERY') {
        const removeFormAttributeIdMap = {}
        for (let i = 0; i < this.selectSelectionData.length; i++) {
          removeFormAttributeIdMap[this.selectSelectionData[i].formAttributeId] = 1
        }

        this.selectAttributeDataList = this.selectAttributeDataList.filter(item => removeFormAttributeIdMap[item.formAttributeId] !== 1)
      } else if (this.configType === 'TABLE') {
        const removeFormAttributeIdMap = {}
        for (let i = 0; i < this.tableSelectionData.length; i++) {
          removeFormAttributeIdMap[this.tableSelectionData[i].formAttributeId] = 1
        }

        this.tableAttributeDataList = this.tableAttributeDataList.filter(item => removeFormAttributeIdMap[item.formAttributeId] !== 1)
      } else if (this.configType === 'MAIN_HEADER') {
        const removeFormAttributeIdMap = {}
        for (let i = 0; i < this.headerSelectionData.length; i++) {
          removeFormAttributeIdMap[this.headerSelectionData[i].formAttributeId] = 1
        }

        this.headerAttributeDataList = this.headerAttributeDataList.filter(item => removeFormAttributeIdMap[item.formAttributeId] !== 1)
      }
    },
    addAttribute () {
      this.globalAttributeId = this.globalAttributeId + 1

      let parentAttributeId = -1
      const dataItem = {}
      if (this.configType === 'QUERY') {
        parentAttributeId = this.selectGlobalInfo.formAttributeId

        for (let i = 0; i < this.selectAttributeColumns.length; i++) {
          dataItem[this.selectAttributeColumns[i].prop] = null
        }
      } else if (this.configType === 'TABLE') {
        parentAttributeId = this.tableGlobalInfo.formAttributeId

        for (let i = 0; i < this.tableAttributeColumns.length; i++) {
          dataItem[this.tableAttributeColumns[i].prop] = null
        }
      } else if (this.configType === 'MAIN_HEADER') {
        parentAttributeId = this.headerGlobalInfo.formAttributeId

        for (let i = 0; i < this.headerAttributeColumns.length; i++) {
          dataItem[this.headerAttributeColumns[i].prop] = null
        }
      }

      dataItem.formAttributeId = this.globalAttributeId
      dataItem.parentAttributeId = parentAttributeId

      if (this.configType === 'QUERY') {
        this.$refs.selectAttribute.pushRow(dataItem)
      } else if (this.configType === 'TABLE') {
        this.$refs.tableAttribute.pushRow(dataItem)
      } else if (this.configType === 'MAIN_HEADER') {
        this.$refs.headerAttribute.pushRow(dataItem)
      }
    },
    selectionChange (selection) {
      if (this.configType === 'QUERY') {
        this.selectSelectionData = selection
      } else if (this.configType === 'TABLE') {
        this.tableSelectionData = selection
      } else if (this.configType === 'MAIN_HEADER') {
        this.headerSelectionData = selection
      }
    },
    changeComponentType (row, componentType) {
      let label = this.$getDictLabel('FORM_COMPONENT_TYPE', componentType)
      this.$set(row, 'elementTag', label)
    },
    editDetailItem (rowData) {
      const formApi = rowData.formApi
      if (formApi) {
        let sql = formApi.apiSql
        if (sql) {
          // eslint-disable-next-line no-useless-escape
          sql = sql.replace(/\n/g, '').replace(/\t/g, '').replace(/\"/g, '"')
        }
        rowData.formApi.apiSql = sql
      } else {
        this.$set(rowData, 'formApi', {})
      }
      this.api.totalConfig = rowData
      this.api.row = rowData

      this.api.visible = true
    },
    saveConfig (totalConfig) {
      this.$set(this.api.row, 'formApi', totalConfig.formApi)
      this.$set(this.api.row, 'eventList', totalConfig.eventList)
      this.$set(this.api.row, 'propList', totalConfig.propList)
      this.api.visible = false
    },
    async addInfo () {
      if (!this.apiConfig.updateApi) {
        this.$message.error(this.$t('reportSetting.unSettingFun'))
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error(this.$t('reportSetting.unSettingTable'))
        return
      }
      let formValidate = await this.$refs.formPage.validate()
      if (formValidate) {
        const tempList = []
        tempList.push(this.selectGlobalInfo)
        tempList.push(...this.selectAttributeDataList)
        tempList.push(this.tableGlobalInfo)
        tempList.push(...this.tableAttributeDataList)
        tempList.push(this.headerGlobalInfo)
        tempList.push(...this.headerAttributeDataList)
        this.form.formAttributeList = tempList
        this.form.methodList = this.methodDataList
        this.apiConfig.addApi(this.form).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      }
    },
    changeTableName (tableName, configType) {
      if (!tableName) {
        return
      }
      if (configType === 'QUERY') {
        this.loadColumns(tableName, this.selectGlobalInfo.formAttributeId, this.selectAttributeDataList)
      } else if (configType === 'TABLE') {
        this.loadColumns(tableName, this.tableGlobalInfo.formAttributeId, this.tableAttributeDataList)
      } else if (configType === 'MAIN_HEADER') {
        this.loadColumns(tableName, this.headerGlobalInfo.formAttributeId, this.headerAttributeDataList)
      }
    },
    loadColumns (tableName, parentAttributeId, dataList) {
      globalToolAPI.listColumns({ module: this.form.bizModule, tableName: tableName }).then(res => {
        const columnList = res.data
        const rowArray = []

        for (let i = 0; i < columnList.length; i++) {
          const columnItem = columnList[i]
          this.globalAttributeId = this.globalAttributeId + 1
          const rowItem = {
            formAttributeId: this.globalAttributeId,
            parentAttributeId: parentAttributeId,
            columnName: columnItem.columnName,
            propertyName: camelCase(columnItem.columnName.toLowerCase()),
            propertyDescribe: columnItem.desc,
            sort: i + 1,
            componentType: null
          }
          rowItem[EDITABLE_KEY] = false
          rowItem[ADD_KEY] = false
          rowItem[UPDATE_KEY] = false
          rowArray.push(rowItem)
        }

        dataList.push(...rowArray)
      })
    }
  }
}
</script>
<style scoped lang="scss">
.formpageEdit {
  height: 100%;

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
