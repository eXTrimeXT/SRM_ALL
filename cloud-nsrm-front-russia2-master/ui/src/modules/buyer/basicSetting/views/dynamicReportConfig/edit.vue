<template>
  <el-container
    class="flex-container dynamic-config-edit"
    direction="vertical"
  >
    <el-main>
      <el-collapse v-model="activeCollapse">
        <!-- SQL配置 -->
        <el-collapse-item
          :title="$t('dataConfMod.sqlSetting')"
          name="1"
        >
          <div
            class="form-container"
            style="border: none;"
          >
            <el-form
              ref="reportForm"
              :model="form"
              label-width="80px"
              :show-message="false"
              class="form-fill-style"
              label-position="top"
              :rules="rules"
            >
              <el-row :gutter="32">
                <!-- 编码 -->
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.reportCode')"
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
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.reportName')"
                    :label-width="formLabelWidth"
                    prop="nickName"
                  >
                    <el-input
                      v-model="form.nickName"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="description"
                    :label="$t('bidMod.quickSearchDescription')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.description"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="false"
                  :span="6"
                >
                  <el-form-item
                    :label="$t('reportSetting.userRole')"
                    :label-width="formLabelWidth"
                    prop="roleCodeList"
                  >
                    <CRoleSelector
                      v-model="form.roleCodeList"
                      multiple
                      collapse-tags
                      value-key="roleCode"
                      :placeholder="$t('common.pleaseSelect')"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('bidMod.queryModule')"
                    :label-width="formLabelWidth"
                    prop="queryModule"
                  >
                    <dict-select
                      v-model="form.queryModule"
                      :dict-class="dictClass"
                      code="MODULE_DIVISION"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('bidMod.valueAttr')"
                    :label-width="formLabelWidth"
                    prop="valueAttr"
                  >
                    <dict-select
                      v-model="form.valueAttr"
                      :dict-class="dictClass"
                      filterable
                      clearable
                      code="VALUE_ATTRS"
                      :disabled="isDisabledPk"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- icon路径 -->
                  <el-form-item
                    :label="$t('dataConfMod.iconPath')"
                    :label-width="formLabelWidth"
                    prop="icon"
                  >
                    <el-input
                      v-model="form.icon"
                      disabled
                    >
                      <el-button
                        slot="append"
                        icon="el-icon-search"
                        @click="getIconList"
                      />
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <!-- SQL查询语句 -->
                  <el-form-item
                    :label="$t('dataConfMod.querySql')"
                    :label-width="formLabelWidth"
                    prop="querySql"
                  >
                    <el-input
                      v-model="form.querySql"
                      type="textarea"
                      :showWordLimit="true"
                      :maxlength="4000"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <!-- SQL查询语句 -->
                  <div class="useDes">
                    {{ $t('reportSetting.sqlUseDes') }}<br>
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
        </el-collapse-item>
        <!-- 报表字段配置 -->
        <el-collapse-item
          :title="$t('dataConfMod.reportFieldConf')"
          name="2"
        >
          <div class="btn-group">
            <el-button
              type="primary"
              :disabled="queryTablesDisabled"
              class="detail-pbtn"
              @click="getTables('enter')"
            >
              <!-- 查询字段 -->
              {{ $t("dataConfMod.queryField") }}
            </el-button>
            <el-button
              type="primary"
              :disabled="queryTablesDisabled"
              class="detail-pbtn"
              @click="addColumn()"
            >
              <!-- 新增字段 -->
              {{ $t('reportSetting.addColumn') }}
            </el-button>
            <el-button
              type="primary"
              :disabled="queryTablesDisabled"
              class="detail-pbtn"
              @click="removeColumn()"
            >
              <!-- 删除字段 -->
              {{ $t('reportSetting.removeColumn') }}
            </el-button>
          </div>
          <div style="height: 400px;">
            <el-table
              :data="dataSource"
              use-virtual
              :row-height="37"
              border
              :empty-text="$t('common.noData')"
              max-height="400px"
              @row-dblclick="rowClick"
              @selection-change="checkChange"
            >
              <el-table-column
                type="selection"
                align="center"
              />
              <el-table-column
                sortable
                width="80px"
                align="center"
                :label="$t('bidMod.alias')"
                prop="alias"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.editable"
                    v-model="scope.row.alias"
                  />
                  <span v-else>{{ scope.row.alias }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                width="80px"
                align="center"
                :label="$t('bidMod.attrOrder')"
                prop="attrOrder"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.editable"
                    v-model="scope.row.attrOrder"
                  />
                  <span v-else>{{ scope.row.attrOrder }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                align="center"
                :label="$t('bidMod.attr')"
                prop="attr"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.editable"
                    v-model="scope.row.attr"
                  />
                  <span v-else>{{ scope.row.attr }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                align="center"
                :label="$t('bidMod.title')"
                prop="title"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.editable"
                    v-model="scope.row.title"
                  />
                  <span v-else>{{ scope.row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                width="90px"
                align="center"
                :label="$t('bidMod.queryItemEnabled')"
                prop="queryItemEnabled"
              >
                <template slot-scope="scope">
                  <el-switch
                    v-if="scope.row.editable"
                    v-model="scope.row.queryItemEnabled"
                    active-value="Y"
                    inactive-value="N"
                  />
                  <span v-else>{{
                    scope.row.queryItemEnabled === "Y"
                      ? $t("common.yes")
                      : $t("common.no")
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                width="90px"
                align="center"
                :label="$t('bidMod.displayItemEnabled')"
                prop="displayItemEnabled"
              >
                <template slot-scope="scope">
                  <el-switch
                    v-if="scope.row.editable"
                    v-model="scope.row.displayItemEnabled"
                    active-value="Y"
                    inactive-value="N"
                  />
                  <span v-else>{{
                    scope.row.displayItemEnabled === "Y"
                      ? $t("common.yes")
                      : $t("common.no")
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                align="center"
                :label="$t('bidMod.queryMatchOperator')"
                prop="queryMatchOperator"
              >
                <template slot-scope="scope">
                  <dict-select
                    v-if="scope.row.editable"
                    v-model="scope.row.queryMatchOperator"
                    :dict-class="dictClass"
                    code="QUERY_MATCH_OPERATOR"
                  />
                  <span v-else>{{
                    dictClass.getDictLabel(
                      "QUERY_MATCH_OPERATOR",
                      scope.row.queryMatchOperator
                    )
                  }}</span>
                </template>
              </el-table-column>
              <!-- 数据库字段类型 -->
              <el-table-column
                sortable
                width="130px"
                align="center"
                :label="$t('dataConfMod.dbFieldType')"
                prop="dataType"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.editable"
                    v-model="scope.row.dataType"
                  />
                  <span v-else>{{ scope.row.dataType }}</span>
                </template>
              </el-table-column>
              <!-- 组件类型 -->
              <el-table-column
                sortable
                align="center"
                :label="$t('dataConfMod.componentType')"
                prop="componentType"
              >
                <template slot-scope="scope">
                  <dict-select
                    v-if="scope.row.editable"
                    v-model="scope.row.componentType"
                    :dict-class="dictClass"
                    code="COMPONENT_TYPE"
                  />
                  <span v-else>{{
                    dictClass.getDictLabel("COMPONENT_TYPE", scope.row.componentType)
                  }}</span>
                </template>
              </el-table-column>
              <!-- 组件属性 -->
              <el-table-column
                sortable
                align="center"
                :label="$t('dataConfMod.componentProperty')"
                prop="componentProperty"
              >
                <template slot-scope="scope">
                  <el-popover
                    v-if="!scope.row.editable"
                    placement="top"
                    trigger="hover"
                  >
                    <code>{{
                      scope.row.componentProperty
                        ? stringfiyProps(scope.row.componentProperty)
                        : "{}"
                    }}</code>
                    <el-button
                      slot="reference"
                      type="text"
                      @click="editComponentProps(scope)"
                    >
                      <!-- 查看 -->
                      {{ $t("common.view") }}
                    </el-button>
                  </el-popover>
                  <el-button
                    v-else
                    type="text"
                    @click="editComponentProps(scope)"
                  >
                    <!-- 编辑 -->
                    {{ $t("common.edit") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button

            @click="cancel"
          >
            {{ $t("common.cancel") }}
          </el-button>
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
        </template>
      </CToolbar>
    </el-main>
    <!-- 组件属性编辑 -->
    <srm-dialog
      :title="$t('dataConfMod.editCompProp')"
      :visible.sync="visible"
      size="midden"
      @closed="closed"
    >
      <vue-json-editor
        v-if="visible"
        v-model="componentProps"
        :show-btns="false"
        :lang="'zh'"
        mode="code"
      />
      <template #footer>
        <el-button @click="visible = false">
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="saveComponentProps"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 图标选择 -->
    <srm-dialog
      :title="$t('dataConfMod.selectIcon')"
      size="middle"
      :visible.sync="iconDialogVisible"
      :close-on-click-modal="false"
    >
      <div class="funIcon">
        <el-radio-group v-model="iconModel">
          <el-radio
            v-for="(icon, index) in iconDataList"
            :key="index"
            :label="icon.fileName"
            border
          >
            <img :src="icon.icon" width="40px">
          </el-radio>
        </el-radio-group>
      </div>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="iconDialogVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="selectIcon"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
/* eslint-disable no-template-curly-in-string */
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import vueJsonEditor from 'vue-json-editor'
import CRoleSelector from '@/library/components/c-role-selector'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { dynamicReportConfig } from 'modb@/basicSetting/api/basicSetting'
let seed = 0

function generateTemplateId () {
  const index = ++seed
  return `custom_file_id_${index}`
}
export default {
  name: 'DynamicReportConfigEdit',
  components: {
    CToolbar,
    vueJsonEditor,
    CRoleSelector
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  props: {
    // eslint-disable-next-line vue/require-prop-types
    params: {}
  },
  data () {
    const dictClass = createDictClass({
      MODULE_DIVISION: [],
      VALUE_ATTRS: [],
      QUERY_MATCH_OPERATOR: [
        { value: 'allMatch', label: this.$t('dataConfMod.fuzzyMatch') }, // 模糊匹配
        { value: 'equalMatch', label: this.$t('dataConfMod.exactMatch') } // 精确匹配
      ],
      COMPONENT_TYPE: [
        { value: 'DATE', label: this.$t('dataConfMod.time') }, // 时间
        { value: 'DOWNLOAD', label: this.$t('common.download') }, // 下载
        { value: 'DICTIONARY', label: this.$t('dataConfMod.dictionary') }, // 字典
        { value: 'ORGANIZATION', label: this.$t('components.organization.orgSelect') }, // 组织选择
        { value: 'QUICKSEARCH', label: this.$t('reportSetting.quickSearch') }, // 快查
        { value: 'LINK', label: this.$t('reportSetting.jumpLink') }, // 跳转链接
        { value: 'POPCONTENT', label: this.$t('reportSetting.viewContent') } // 内容查看
      ]
    }, false)
    return {
      dictClass: dictClass,
      activeCollapse: ['1', '2'],
      placeholderConfigArray: [
        { type: 'primary', label: '${user.userId}' },
        { type: 'primary', label: '${user.userName}' },
        { type: 'primary', label: '${user.enabled}' },
        { type: 'primary', label: '${user.nickname}' },
        { type: 'primary', label: '${user.companyId}' },
        { type: 'primary', label: '${user.companyCode}' },
        { type: 'primary', label: '${user.companyName}' }
      ],
      form: {
        name: '',
        nickName: '',
        description: '',
        valueAttr: '',
        queryModule: '',
        querySql: '',
        roleCodeList: [], // 默认采购商
        icon: ''
      },

      visible: false,
      componentProps: '',
      currentRow: null,
      resetForm: {},
      rules: {
        name: [{ required: true }],
        nickName: [{ required: true }],
        roleCodeList: [{ required: false }],
        icon: [{ required: true }],
        queryModule: [{ required: true }],
        valueAttr: [{ required: true }],
        querySql: [{ required: true }]
      },
      formLabelWidth: '100px',
      dataSource: [],
      checkList: [],
      iconDataList: [],
      iconModel: '',
      iconDialogVisible: false
    }
  },
  computed: {
    queryTablesDisabled () {
      const { querySql, queryModule } = this.form
      return !querySql || !queryModule
    },
    isDisabled () {
      return this.params.flag === 'edit'
    },
    isDisabledPk () {
      return !this.dataSource.length
    }
  },
  created () {
    const { flag, sqlId } = this.params
    if (flag === 'edit') {
      this.getOrder(sqlId)
    } else {
      this.dictClass.setDictionary('VALUE_ATTRS', [])
    }
    this.dictClass.loadDictionary('MODULE_DIVISION')

    const icons = import.meta.glob('/src/assets/function-icon/*.png', { eager: false })
    this.iconDataList = Object.keys(icons).map(item => {
      // const fileName = item.replace(/\/public\/assets\/function-icon\/(.*)/, '$1')
      const fileName = item.replace(/\/src\/assets\/function-icon\/(.*)/, '$1')
      return {
        url: item, // '/assets/function-icon/' + fileName,
        icon: item, // '/assets/function-icon/' + fileName,
        fileName
      }
    })
  },
  methods: {
    generateTemplateId () {
      return generateTemplateId()
    },
    getOrder (sqlId) {
      dynamicReportConfig.getById(sqlId).then(({ data }) => {
        const {
          attrs,
          name,
          description,
          valueAttr,
          queryModule,
          querySql,
          sqlId,
          nickName,
          roleCodeList,
          icon
        } = data
        this.form = {
          name,
          description,
          valueAttr,
          queryModule,
          querySql,
          sqlId,
          nickName,
          roleCodeList,
          icon
        }
        this.dataSource = attrs.map(i => ({
          ...i,
          $index: this.generateTemplateId(),
          editable: false
        }))
        this.dictClass.setDictionary(
          'VALUE_ATTRS',
          attrs.map(({ attr }) => ({
            id: attr,
            value: attr,
            label: attr
          }))
        )
      })
    },
    closed () {
      this.currentRow = null
      this.componentProps = {}
    },
    stringfiyProps (props = {}) {
      let result = ''
      try {
        const obj = JSON.parse(`${props}`)
        result = JSON.stringify(obj, null, 4)
      } catch (e) {
        result = ''
      }
      return result
    },
    editComponentProps (scope) {
      this.visible = true
      this.currentRow = scope.row
      let componentProps = {}
      const { componentType, componentProperty } = scope.row
      switch (componentType) {
      case 'DICTIONARY':
        componentProps = componentProperty
          ? JSON.parse(componentProperty)
          : { code: '' }
        break
      case 'QUICKSEARCH':
        componentProps = componentProperty ? JSON.parse(componentProperty)
          : { showKey: '', propKey: '', name: '' }
        break
      default:
        componentProps = componentProperty
          ? JSON.parse(componentProperty)
          : {}
      }
      this.componentProps = componentProps
    },
    saveComponentProps () {
      this.currentRow.componentProperty = JSON.stringify(this.componentProps)
      this.visible = false
    },
    rowClick (row) {
      row.editable = true
    },
    checkChange (select) {
      this.checkList = select
    },
    addColumn () {
      this.dataSource.push({
        $index: this.generateTemplateId(),
        editable: true
      })
    },
    removeColumn () {
      for (let i = 0; i < this.checkList.length; i++) {
        for (let j = 0; j < this.dataSource.length; j++) {
          if (this.checkList[i].$index === this.dataSource[j].$index) {
            this.dataSource.splice(j, 1)
            break
          }
        }
      }
    },
    getTables () {
      const { querySql, queryModule } = this.form
      if (!queryModule || !querySql) {
        return
      }
      dynamicReportConfig
        .getAttrs({
          querySql: this.form.querySql,
          queryModule: this.form.queryModule
        })
        .then(data => {
          // 查询表结构,  更新列表
          this.dataSource = data.data.map(i => ({
            ...i,
            $index: this.generateTemplateId(),
            editable: false
          }))
          this.dictClass.setDictionary(
            'VALUE_ATTRS',
            data.data.map(({ attr }) => ({
              id: attr,
              value: attr,
              label: attr
            }))
          )
        })
    },
    submitOne () {
      for (let i = 0; i < this.dataSource.length; i++) {
        const item = this.dataSource[i]
        if (item.queryItemEnabled === 'Y' && !item.alias) {
          this.$message({
            message: this.$t('reportSetting.fillAlias'),
            type: 'error'
          })
          return
        }
      }
      this.$refs.reportForm.validate(valid => {
        if (valid) {
          const params = {
            attrs: this.dataSource,
            ...this.form
          }
          dynamicReportConfig
            .saveOrUpdate(params)
            .then(data => {
              if (data) {
                this.$message({
                  message: this.$t('common.successSave'), // "保存成功",
                  type: 'success'
                })
              }
              this.cancel()
            })
        } else {
          this.$message.error(this.$t('reportSetting.validateErr'))
          return false
        }
      })
    },
    cancel () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('dynamicReportConfigList.getFormData')
    },
    resetOne () {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dataSource = []
    },
    // 查询图标
    getIconList () {
      this.iconDialogVisible = true
    },
    // 选择图标
    selectIcon () {
      this.form.icon = this.iconModel.toString()
      this.iconDialogVisible = false
    }
  }
}
</script>
<style scoped lang="scss">
.btn-group {
  padding-bottom: 10px;
}
.dynamic-config-edit {
  padding-bottom: 50px;
}
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
<style>
.funIcon .el-radio.is-bordered {
  padding: 5px;
  height: 50px;
  margin-bottom: 10px;
  margin-left: 0;
  margin-right: 10px !important;
}
.funIcon .el-radio.is-bordered:first-child {
  margin-left: 10px;
}
.funIcon .el-radio.is-bordered .el-radio__label img {
  display: inline-block;
  vertical-align: middle;
}
</style>
