<template>
  <el-container
    class="formpageEdit"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-position="top"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-collapse-item
            :title="$t('datapermission.baseSetting')"
            name="base"
            :accordion="false"
          >
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item
                  prop="pageCode"
                  :label="$t('dataConfMod.pageCode')"
                >
                  <el-input v-model="form.pageCode" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="pageName"
                  :label="$t('dataConfMod.pageName')"
                >
                  <el-input v-model="form.pageName" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="pageModel"
                  :label="$t('dataConfMod.pageModel')"
                >
                  <DictSelect
                    v-model="form.pageModel"
                    code="FORM_PAGE_MODEL"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="pageDescribe"
                  :label="$t('dataConfMod.pageDescribe')"
                >
                  <el-input v-model="form.pageDescribe" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-checkbox-group v-model="formType">
                  <el-checkbox
                    v-for="formTypeOption in formTypeOptions"
                    :key="formTypeOption"
                    :label="formTypeOption"
                  >
                    {{ formTypeOption }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="bizModule"
                  :label="$t('dataConfMod.bizModule')"
                >
                  <DictSelect
                    v-model="form.bizModule"
                    code="MODULE_DIVISION"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="tableName"
                  :label="$t('dataConfMod.tableName')"
                >
                  <DictSelect
                    v-model="form.tableName"
                    :code="form.bizModule"
                    custom-select-type="MODULE_TABLE_NAME"
                    @change="changeTableName"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('dataConfMod.functionSetting')"
            name="method"
            :accordion="false"
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
          </el-collapse-item>
          <el-collapse-item
            :title="$t('dataConfMod.attrSetting')"
            name="attribute"
            :accordion="false"
          >
            <el-row :gutter="32">
              <el-col :span="24">
                <el-button
                  type="primary"
                  :disabled="readOnly"

                  style="margin-bottom: 5px;"
                  @click="clearSelect('selectAttribute')"
                >
                  {{ $t('dataConfMod.rootNode') }}
                </el-button>
                <TableView
                  ref="attributeView"
                  :table-header="tableAttributeColumns"
                  :row-dblclick="rowDblclick"

                  row-key="formAttributeId"
                  :expand-all="true"
                  :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                />
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('dataConfMod.attrSetting')"
            name="attribute"
            :accordion="false"
            style="height:600px;"
          >
            <el-row :gutter="32">
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
                <div v-if="!currentRow">
                  {{ $t('dataConfMod.currRootNode') }}
                </div>
                <div v-else>
                  <span>{{ $t('dataConfMod.selectNodeDes') }}</span>
                  <span>{{ currentRow.propertyDescribe }}</span>
                  <span>{{ $t('dataConfMod.nodeAttrName') }}</span>
                  <span>{{ currentRow.propertyName }}</span>
                </div>
                <BaseTableSimple
                  ref="selectAttribute"
                  :data-source="selectAttributeDataList"
                  :columns="selectAttributeColumns"
                  columns-name="selectAttributeColumns"
                  :initialize="false"
                  row-key="formAttributeId"
                  selection
                  border
                  :empty-text="$t('reportSetting.addTip')"
                  style="height:500px;"
                  @asyncGetRealDataSource="dataList => asyncFreshDataSource(dataList)"
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
                    <el-input v-model="scope.row.parentAttributeId" />
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
                      inactive-color="#ff4949"
                    />
                  </template>
                  <template #viewable="{ scope }">
                    <el-switch
                      v-model="scope.row.viewable"
                      active-color="#13ce66"
                      inactive-color="#ff4949"
                    />
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
                </BaseTableSimple>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-form>
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
import BaseTableSimple from '@/library/components/BaseTable/BaseTableSimple'
import camelCase from 'lodash/camelCase'
import TableView from 'lib@/components/Table/TableView'
import createTreeClass from '@/utils/tree-utils'
import FormApiEdit from 'modb@/basicSetting/views/formPage/components/FormApiEdit'
import FormMethodEdit from 'modb@/basicSetting/views/formPage/components/FormMethodEdit'

export default {
  name: 'FormPageEdit',
  components: {
    TableView,
    CToolbar,
    BaseTableSimple,
    FormApiEdit,
    FormMethodEdit
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
      formTypeOptions: [this.$t('components.common.search'), this.$t('dataConfMod.table')],  // ['查询', '表格']
      formType: [],
      activeDims: ['base', 'method', 'attribute', 'query', 'table'],
      selectAttributeDataList: [],
      currentRow: null,
      defaultAttributePlaceArray: ['select', 'table'],
      defaultFormGlobal: {
        attributePlace: 'select',
        formSize: 'small',
        formSpan: 6,
        formGutter: 2,
        formRef: 'formRef',
        formDisabled: false,
        formEditable: true,
        formModel: 'formData',
        formRules: 'rules',
        labelWidth: '100',
        labelPosition: 'top',
        apiType: null,
        httpMethod: null,
        apiUrl: null,
        apiDataModule: null,
        apiSqlEditable: null,
        apiSql: null
      },
      form: {
        formPageId: null,
        pageCode: null,
        pageName: null,
        pageModel: null,
        pageDescribe: null,
        bizModule: null,
        tableName: null,
        attributeDataList: [],
        methodList: []
      },
      methodAttributeColumns: [],
      globalAttributeId: 0,
      totalAttributeDataList: [],
      treeAttributeDataList: [],
      waitAttributeDataList: [],
      selectionData: [],
      selectAttributeColumns: [],
      tableAttributeColumns: [],
      rules: {},
      readOnly: false,
      api: {
        visible: false,
        totalConfig: {},
        row: null
      },
      methodDataList: [],
      formMethod: {
        visible: false,
        type: null,
        data: {}
      }
    }
  },
  created () {
    let columns = [
      { align: 'center', prop: 'formAttributeId', label: () => this.$t('reportMod.formAttributeId'), editable: true, minWidth: '180' },
      { align: 'center', prop: 'columnName', label: () => this.$t('reportSetting.columnName'), editable: true, slot: 'columnName', minWidth: '180' },
      { align: 'center', prop: 'propertyName', label: () => this.$t('reportSetting.propertyName'), editable: true, slot: 'propertyName', minWidth: '180' },
      { align: 'center', prop: 'propertyDescribe', label: () => this.$t('reportSetting.propertyDescribe'), editable: true, slot: 'propertyDescribe', minWidth: '180' },
      { align: 'center', prop: 'sort', label: () => this.$t('components.viewConfig.seq'), editable: true, slot: 'sort', minWidth: '180' },
      { align: 'center', prop: 'languageCode', label: () => this.$t('components.viewConfig.languageCode'), editable: true, slot: 'languageCode', minWidth: '180' },
      { align: 'center', prop: 'parentAttributeId', label: () => this.$t('reportMod.parentAttributeId'), editable: true, slot: 'parentAttributeId', minWidth: '180' },
      { align: 'center', prop: 'parentAttributeId', label: () => this.$t('reportMod.parentAttributeId'), editable: true, slot: 'parentAttributeId', minWidth: '180' },
      { align: 'center', prop: 'parentSlotName', label: () => this.$t('reportMod.parentSlotName'), editable: true, slot: 'parentSlotName', minWidth: '180' },
      { align: 'center', prop: 'componentType', label: () => this.$t('dataConfMod.componentType'), editable: true, slot: 'componentType', minWidth: '180' },
      { align: 'center', prop: 'elementTag', label: () => this.$t('reportSetting.elementTag'), editable: true, slot: 'elementTag', minWidth: '180' },
      { align: 'center', prop: 'notNull', label: () => this.$t('dataConfMod.isRequested'), editable: true, slot: 'notNull', minWidth: '180' },
      { align: 'center', prop: 'viewable', label: () => this.$t('reportMod.viewable'), editable: true, slot: 'viewable', minWidth: '180' },
      { align: 'center', prop: 'isModel', label: () => this.$t('reportMod.isModel'), editable: true, slot: 'isModel', minWidth: '180' },
      { align: 'center', prop: 'maxLength', label: () => this.$t('reportSetting.maxLength'), editable: true, slot: 'maxLength', minWidth: '180' },
      { align: 'center', prop: 'minLength', label: () => this.$t('reportSetting.minLength'), editable: true, slot: 'minLength', minWidth: '180' },
      { align: 'center', prop: 'placeholder', label: () => this.$t('reportSetting.placeholder'), editable: true, slot: 'placeholder', minWidth: '180' },
      { align: 'center', prop: 'dataType', label: () => this.$t('reportSetting.dataType'), editable: true, slot: 'dataType', minWidth: '180' },
      { align: 'center', prop: 'dictCode', label: () => this.$t('reportSetting.dictCode'), editable: true, slot: 'dictCode', minWidth: '180' },
      { align: 'center', prop: 'formSpan', label: () => this.$t('reportSetting.formSpan'), editable: true, slot: 'formSpan', minWidth: '180' }
    ]
    this.methodAttributeColumns = [
      { align: 'center', prop: 'formLineId', label: () => this.$t('reportSetting.formLineId'), editable: true, minWidth: '180' },
      { align: 'center', prop: 'methodName', label: () => this.$t('reportSetting.methodName'), editable: true, slot: 'methodName', minWidth: '180' },
      { align: 'center', prop: 'methodDescribe', label: () => this.$t('reportSetting.methodDescribe'), editable: true, slot: 'methodDescribe', minWidth: '180' },
      { align: 'center', prop: 'methodParams', label: () => this.$t('reportSetting.methodParams'), editable: true, slot: 'methodParams', minWidth: '180' },
      { align: 'center', prop: 'methodBody', label: () => this.$t('reportSetting.methodBody'), editable: true, slot: 'methodBody', minWidth: '180' },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '220',
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
    }
    this.selectAttributeColumns.push({ align: 'center',
      prop: 'tableOperations',
      label: () => this.$t('common.operation'),
      minWidth: '180',
      operations: [
        {
          event: 'editDetailItem',
          name: () => this.$t('common.edit'),
          func: this.editDetailItem
        },
        {
          event: 'deleteItem',
          name: () => this.$t('common.delete'),
          func: this.deleteItem
        }
      ]
    })
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'view') {
      formPageAPI.getDetail(row.formPageId).then(res => {
        let formTemp = res.data
        if (!formTemp.formAttributeList) {
          formTemp.formAttributeList = []
        }

        this.totalAttributeDataList = formTemp.formAttributeList

        this.methodDataList = formTemp.methodList
        this.$refs.methodView.tableData = this.methodDataList

        this.form = formTemp
        this.freshTableView()
      })
    }
  },
  methods: {
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
      this.$refs.methodView.tableData = this.methodDataList
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
      this.$refs.methodView.tableData = this.methodDataList
    },
    freshTableView () {
      const treeClass = createTreeClass('formAttributeId', 'parentAttributeId')
      this.treeAttributeDataList = treeClass.buildTree(this.totalAttributeDataList)
      this.$refs.attributeView.tableData = this.treeAttributeDataList
    },
    rowDblclick (val) {
      this.currentRow = val

      this.updateEditData()
      this.resetEditableList()
      this.freshTableView()
    },
    updateEditData () {
      const waitMap = {}
      for (let i = 0; i < this.waitAttributeDataList.length; i++) {
        const waitItem = this.waitAttributeDataList[i]
        waitMap[waitItem.formAttributeId] = waitItem
      }

      for (let i = 0; i < this.totalAttributeDataList.length; i++) {
        const formAttributeId = this.totalAttributeDataList[i].formAttributeId
        if (waitMap[formAttributeId]) {
          this.totalAttributeDataList[i] = waitMap[formAttributeId]
        }
      }
    },
    deleteItem (rowObj) {
      this.updateEditData()
      const removeFormAttributeId = rowObj.row.formAttributeId
      this.totalAttributeDataList = this.totalAttributeDataList.filter(item => item.formAttributeId !== removeFormAttributeId)
      this.freshTableView()
      this.resetEditableList()
    },
    deleteSelect () {
      this.updateEditData()
      const removeFormAttributeIdMap = {}
      for (let i = 0; i < this.selectionData.length; i++) {
        removeFormAttributeIdMap[this.selectionData[i].formAttributeId] = 1
      }

      this.totalAttributeDataList = this.totalAttributeDataList.filter(item => removeFormAttributeIdMap[item.formAttributeId] !== 1)
      this.freshTableView()
      this.resetEditableList()
    },
    addAttribute () {
      this.updateEditData()

      let parentAttributeId = -1
      if (this.currentRow) {
        parentAttributeId = this.currentRow.formAttributeId
      }
      this.globalAttributeId = this.globalAttributeId + 1
      this.totalAttributeDataList.push({
        formAttributeId: this.globalAttributeId,
        parentAttributeId: parentAttributeId
      })
      this.freshTableView()
      this.resetEditableList()
    },
    clearSelect () {
      this.currentRow = null
      this.updateEditData()
      this.resetEditableList()
    },
    resetEditableList () {
      let parentAttributeId = -1
      if (this.currentRow) {
        parentAttributeId = this.currentRow.formAttributeId
      }
      let list = this.totalAttributeDataList.filter(item => item.parentAttributeId === parentAttributeId)
      const tableList = []
      if (list) {
        for (let i = 0; i < list.length; i++) {
          const { children, ...rowItem } = list[i]
          tableList.push(rowItem)
        }
      }
      this.selectAttributeDataList = tableList
    },
    asyncFreshDataSource (dataList) {
      this.waitAttributeDataList = dataList
    },
    selectionChange (selection) {
      this.selectionData = selection
    },
    changeComponentType (row, componentType) {
      let label = this.$getDictLabel('FORM_COMPONENT_TYPE', componentType)
      this.$set(row, 'elementTag', label)
    },
    editDetailItem (rowData) {
      const formApi = rowData.row.formApi
      if (formApi) {
        let sql = formApi.apiSql
        if (sql) {
          // eslint-disable-next-line no-useless-escape
          sql = sql.replace(/\n/g, '').replace(/\t/g, '').replace(/\"/g, '"')
        }
        rowData.row.formApi.apiSql = sql
      } else {
        this.$set(rowData.row, 'formApi', {})
      }
      this.api.totalConfig = rowData.row
      this.api.row = rowData.row

      this.api.visible = true
    },
    saveConfig (totalConfig) {
      this.$set(this.api.row, 'formApi', totalConfig.formApi)
      this.$set(this.api.row, 'eventList', totalConfig.eventList)
      this.$set(this.api.row, 'propList', totalConfig.propList)
      this.api.visible = false
    },
    addInfo () {
      if (!this.apiConfig.updateApi) {
        this.$message.error($t('reportSetting.unSettingFun'))
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error(this.$t('reportSetting.unSettingTable'))
        return
      }
      this.updateEditData()
      this.$refs.form.validate(result => {
        if (result) {
          this.form.formAttributeList = this.totalAttributeDataList
          this.form.methodList = this.methodDataList
          this.apiConfig.addApi(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        }
      })
    },
    changeTableName (tableName) {
      this.updateEditData()
      globalToolAPI.listColumns({ module: this.form.bizModule, tableName: tableName }).then(res => {
        const columnList = res.data
        const rowArray = []

        let parentAttributeId = -1
        if (this.currentRow) {
          parentAttributeId = this.currentRow.formAttributeId
        }
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
          rowArray.push(rowItem)
        }

        this.totalAttributeDataList.push(...rowArray)
        // this.freshTableView()
        this.resetEditableList()
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
