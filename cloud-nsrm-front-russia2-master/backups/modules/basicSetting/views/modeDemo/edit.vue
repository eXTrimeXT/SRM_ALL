<template>
  <el-container
    class="the_contractTemplateEdit_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="mode"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <div class="button_group">
          <el-button
            type="primary"
            @click="preview"
          >
            预览
          </el-button>
          <el-button
            type="primary"
            @click="showFixedPlaceholder"
          >
            固定元素
          </el-button>
          <el-button
            type="primary"
            @click="showDiyPlaceholder"
          >
            自定义元素
          </el-button>
        </div>
        <el-row :gutter="15">
          <el-col :span="6">
            <el-form-item
              label="模板名称"
              prop="modelName"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="form.modelName"
                :disabled="editable"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="状态"
              prop="status"
              :label-width="formLabelWidth"
            >
              <el-select v-model="form.status">
                <el-option
                  v-for="item in contractModelStatus"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="生效日期"
              prop="startDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker v-model="form.startDate" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="失效日期"
              prop="endDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker v-model="form.endDate" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="标准合同"
              prop="enable"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.enable"
                :disabled="editable"
              >
                <el-option
                  key="Y"
                  value="Y"
                  label="是"
                />
                <el-option
                  key="N"
                  value="N"
                  label="否"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item
            label="模板内容"
            prop="content"
            :label-width="formLabelWidth"
          >
            <vue-ueditor-wrap
              v-model="form.content"
              :destroy="true"
              :config="customConfig"
              @ready="ready"
            />
          </el-form-item>
        </el-row>
      </el-form>
      <c-toolbar>
        <template slot="right">
          <el-button

            @click="cancel"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"

            @click="submit"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </template>
      </c-toolbar>
      <srm-dialog
        size="middle"
        :visible.sync="fixedPlaceholderVisible"
        title="固定元素"
      >
        <el-table :data="fixedPlaceholderList">
          <el-table-column
            prop="variableName"
            label="变量名"
          />
          <el-table-column
            prop="variableNameInfo"
            label="变量名信息"
          />
          <el-table-column
            prop="initValue"
            label="初始值"
          />
          <el-table-column
            prop="variableSignInfo"
            label="变量符号信息"
          />
          <el-table-column
            prop="variableSign"
            label="变量符号"
          />
          <el-table-column fixed="right">
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="insertFixedPlaceholder(scope.$index, scope.row)"
              >
                插入变量
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>
      <srm-dialog
        size="middle"
        :visible.sync="diyPlaceholderVisible"
        title="自定义元素"
      >
        <el-button
          style="margin-bottom: 10px;"
          type="primary"
          @click="addNewDiyElement"
        >
          {{ $t("common.add") }}
        </el-button>
        <el-table :data="diyPlaceholderList">
          <el-table-column
            min-width="100px"
            show-overflow-tooltip
            prop="variableName"
            label="变量名"
          >
            <template slot-scope="scope">
              <el-input
                v-if="scope.row.isEditing"
                v-model="scope.row.variableName"
              />
              <span
                v-else
                v-html="scope.row.variableName"
              />
            </template>
          </el-table-column>
          <el-table-column
            min-width="130px"
            show-overflow-tooltip
            prop="variableNameInfo"
            label="变量名信息"
          >
            <template slot-scope="scope">
              <el-input
                v-if="scope.row.isEditing"
                v-model="scope.row.variableNameInfo"
              />
              <span
                v-else
                v-html="scope.row.variableNameInfo"
              />
            </template>
          </el-table-column>
          <el-table-column
            min-width="100px"
            show-overflow-tooltip
            prop="initValue"
            label="初始值"
          >
            <template slot-scope="scope">
              <el-input
                v-if="scope.row.isEditing"
                v-model="scope.row.initValue"
              />
              <span
                v-else
                v-html="scope.row.initValue"
              />
            </template>
          </el-table-column>
          <el-table-column
            min-width="130px"
            prop="variableSignInfo"
            label="变量符号信息"
            show-overflow-tooltip
          />
          <el-table-column
            min-width="130px"
            show-overflow-tooltip
            prop="variableSign"
            label="变量符号"
          >
            <template slot-scope="scope">
              <el-input
                v-if="scope.row.isEditing"
                v-model="scope.row.variableSign"
              />
              <span
                v-else
                v-html="scope.row.variableSign"
              />
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            width="300px"
            label="操作"
          >
            <template slot-scope="scope">
              <el-button
                v-if="!scope.row.isEditing"
                type="text"
                @click="editModelElement(scope.$index, scope.row)"
              >
                {{ $t("common.edit") }}
              </el-button>
              <el-button
                v-else
                type="text"
                @click="saveModelElement(scope.$index, scope.row)"
              >
                {{ $t("common.submit") }}
              </el-button>
              <el-button
                type="text"
                @click="insertFixedPlaceholder(scope.$index, scope.row)"
              >
                插入变量
              </el-button>
              <el-button
                type="text"
                @click="syncFixed(scope.$index, scope.row)"
              >
                同步到固定元素
              </el-button>
              <el-button
                type="text"
                @click="deleteItem(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>
      <srm-dialog
        title="请选择需要打印的字段"
        :visible.sync="generateDialogVisible"
        size="middle"
      >
        <el-select
          v-model="selectColumns"
          multiple
          clearable
          style="width: 100%;"
        >
          <el-option
            v-for="item in tableColumns"
            :key="item.field"
            :label="item.name"
            :value="item.field"
          />
        </el-select>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="generateDialogVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="generateTable"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import VueUeditorWrap from 'vue-ueditor-wrap'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import Preview from './preview'
import cloneDeep from 'lodash/cloneDeep'

export default {
  name: 'Edit',
  components: { VueUeditorWrap, CToolbar },
  mixins: [tabTodoMixin],
  data () {
    return {
      editorInstance: null,
      formLabelWidth: '100px',
      form: {},
      selectColumns: [],
      tableType: '',
      rules: {
        status: [{ required: true, message: '请填写' }],
        startDate: [{ required: true, message: '请填写' }],
        modelName: [{ required: true, message: '请填写' }]
      },
      materialListFields: [],
      payPlanFields: [],
      customConfig: {
        // 编辑器不自动被内容撑高
        autoHeightEnabled: false,
        // 初始容器高度
        initialFrameHeight: 240,
        // 初始容器宽度
        initialFrameWidth: '100%',
        // 上传文件接口（这个地址是我为了方便各位体验文件上传功能搭建的临时接口，请勿在生产环境使用！！！）
        // serverUrl: "http://35.201.165.105:8000/controller.php",
        // UEditor 资源文件的存放路径，如果你使用的是 vue-cli 生成的项目，通常不需要设置该选项，vue-ueditor-wrap 会自动处理常见的情况，如果需要特殊配置，参考下方的常见问题2
        UEDITOR_HOME_URL: '/UEditor/'
      },
      childContext: null,
      contractModelStatus: [],
      tableColumns: [],
      visible: true,
      fixedPlaceholderVisible: false,
      diyPlaceholderVisible: false,
      generateDialogVisible: false,
      fixedPlaceholderList: [],
      diyPlaceholderList: [],
      html: null
    }
  },
  computed: {
    editable () {
      if (this.$attrs.params.flag === 'add') {
        return false
      }
      return this.$attrs.params.row.status !== 'DRAFT'
    }
  },
  mounted () {
    this.initDictionary()
    this.initData()
  },
  methods: {
    editModelElement (index, row) {
      row.isEditing = true
    },
    saveModelElement (index, row) {
      if (row.elementId) {
        this.$api.cm.modelElement.modify(row).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          row.isEditing = false
        })
      } else {
        this.$api.cm.modelElement.add(row).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          row.isEditing = false
        })
      }
    },
    addNewDiyElement () {
      this.diyPlaceholderList.push({
        isEditing: true,
        variableName: '',
        variableNameInfo: '',
        variableSign: '',
        variableSignInfo: '',
        initValue: ''
      })
    },
    initData () {
      this.$api.cm.modelElement
        .getElement({
          isFixed: 'Y',
          isAct: 'Y'
        })
        .then(res => {
          this.fixedPlaceholderList = res.data.map(i => ({
            ...i,
            isEditing: false
          }))
        })
      this.$api.cm.modelElement
        .getElement({
          isFixed: 'N'
        })
        .then(res => {
          this.diyPlaceholderList = res.data.map(i => ({
            ...i,
            isEditing: false
          }))
        })
    },
    submit () {
      const { row, flag } = this.$attrs.params
      const { startDate, endDate, ...rest } = this.form
      const formData = {
        ...rest,
        startDate: this.$dayjs(startDate).format('YYYY-MM-DD'),
        endDate: this.$dayjs(endDate).format('YYYY-MM-DD')
      }
      if (flag === 'add') {
        this.$api.cm.add(formData).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancel()
        })
      } else {
        if (row.status === 'DRAFT') {
          this.$api.cm.modifyAll(formData).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else {
          this.$api.cm.modify(formData).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        }
      }
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'add')
      } else {
        this.$emit(
          'tab-remove',
          `edit_${row.modelHeadId ? row.modelHeadId : ''}`
        )
      }
      this.__setTabTodo('contractTemplateList.getQuerydata')
    },
    initDictionary () {
      const codes = ['CONTRACT_MODEL_STATUS']
      const params = codes.map(i => ({ dictCode: i }))
      getDictItemList(params).then(res => {
        const [CONTRACT_MODEL_STATUS] = res.data
        this.contractModelStatus = adaptDictData(
          CONTRACT_MODEL_STATUS.CONTRACT_MODEL_STATUS
        )
        // this.selectDictionary = {
        //   returnStatus: this.returnStatus
        // };
      })
    },
    preview () {
      const row = this.form
      const tab = {
        component: Preview,
        params: { row },
        title: `预览${row.modelName ? '-' + row.modelName : ''}`,
        name: `preview_${row.modelHeadId ? row.modelHeadId : ''}`
      }
      this.$emit('tab-add', tab)
    },
    showFixedPlaceholder () {
      this.fixedPlaceholderVisible = true
    },
    showDiyPlaceholder () {
      this.diyPlaceholderVisible = true
    },
    syncFixed (index, row) {
      this.$api.cm.modelElement.syncFixed(row.elementId).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
      })
    },
    deleteItem (index, row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          if (row.elementId) {
            this.$api.cm.modelElement.deleteItem(row.elementId).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
            })
          } else {
            this.diyPlaceholderList.splice(index, 1)
          }
          this.initData()
        })
        .catch(() => {})
    },
    generateTable () {
      if (!this.tableType) {
        return null
      }
      let table =
        '<table width="100%" data-table="' + this.tableType + '"><tbody>'
      let emptyRow = '<tr>'
      this.selectColumns.forEach((field, index) => {
        const name = `${this.tableType}-column:${field}`
        if (index === 0) {
          table +=
            '<tr class="firstRow"><td width="206" valign="top" style="word-break: break-all;">${' +
            name +
            '}</td>'
          emptyRow +=
            '<td width="206" valign="top" style="word-break: break-all;"></td>'
        } else if (index === this.selectColumns.length - 1) {
          table +=
            '<td width="206" valign="top" style="word-break: break-all;">${' +
            name +
            '}</td></tr>'
          emptyRow +=
            '<td width="206" valign="top" style="word-break: break-all;"></td></tr>'
        } else {
          table +=
            '<td width="206" valign="top" style="word-break: break-all;">${' +
            name +
            '}</td>'
          emptyRow +=
            '<td width="206" valign="top" style="word-break: break-all;"></td>'
        }
      })
      this.editorInstance.focus()
      this.editorInstance.execCommand(
        'inserthtml',
        table + emptyRow + '</tbody></table>'
      )
      this.fixedPlaceholderVisible = false
      this.diyPlaceholderVisible = false
      this.generateDialogVisible = false
    },
    showGenerateDialog (sign) {
      this.selectColumns = []
      const columns = {
        materialList: [
          { name: '来源单号', field: 'sourceNumber' },
          { name: '来源单行号', field: 'sourceLineNumber' },
          { name: '物料编码', field: 'materialCode' },
          { name: '物料名称', field: 'materialName' },
          { name: '采购分类', field: 'categoryName' },
          { name: '不含税金额', field: 'amount' },
          { name: '合同数量', field: 'contractQuantity' },
          { name: '未税单价', field: 'untaxedPrice' },
          { name: '单位', field: 'unitName' }
        ],
        payPlan: [
          { name: '里程碑阶段', field: 'milestoneStage' },
          { name: '里程碑', field: 'milestone' },
          { name: '里程碑说明', field: 'milestoneExplain' },
          { name: '里程碑日期', field: 'milestoneDate' },
          { name: '里程碑状态', field: 'milestoneStatus' },
          { name: '付款类型', field: 'payType' },
          { name: '付款阶段', field: 'payStage' },
          { name: '付款说明', field: 'payExplain' },
          { name: '付款状态', field: 'payStatus' },
          { name: '付款比例(%)', field: 'payRatio' },
          { name: '付款方式', field: 'payMethod' },
          { name: '已付金额', field: 'paidAmount' },
          { name: '不含税金额', field: 'excludeTaxPayAmount' }
        ]
      }
      if (sign === '${material-list:materialList}') {
        this.tableColumns = columns.materialList
        this.tableType = 'material-list'
      }
      if (sign === '${pay-plan:payPlan}') {
        this.tableColumns = columns.payPlan
        this.tableType = 'pay-plan'
      }
      this.generateDialogVisible = true
    },
    insertFixedPlaceholder (index, row) {
      // this.editorInstance.setContent("${el-input:vendorName}", true);
      let content = row.variableSign
      const table = ['${material-list:materialList}', '${pay-plan:payPlan}']
      if (table.includes(content)) {
        this.showGenerateDialog(content)
        return
      }
      this.editorInstance.focus()
      this.editorInstance.execCommand('inserthtml', content)
      this.fixedPlaceholderVisible = false
      this.diyPlaceholderVisible = false
    },
    ready (editorInstance) {
      console.log(editorInstance)
      const { flag, row } = this.$attrs.params
      if (row.status !== 'DRAFT' && this.$attrs.params.flag === 'edit') {
        editorInstance.setDisabled()
      }
      this.editorInstance = editorInstance
      console.log('key: ' + editorInstance.key)

      if (flag === 'edit') {
        this.$nextTick(() => {
          this.form = cloneDeep(row)
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractTemplateEdit_wrapper {
  .form-incontainer {
    padding-bottom: 45px;
  }
  .button_group {
    padding: 10px 25px;
  }
}
</style>
