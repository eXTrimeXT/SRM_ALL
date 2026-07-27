<template>
  <el-container
    class="flex-container the_usersAccessInfo_wrapper"
    direction="vertical"
  >
    <el-main>
      <div class="common-style">
        <el-form
          ref="form"
          :model="flowForm"
          :rules="flowFormRules"
          label-position="top"
          style="padding-bottom: 16px;"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <!-- 业务名称 -->
              <el-form-item
                :label="$t('dataConfMod.businessName')"
                prop="templateId"
              >
                <el-input v-model="flowForm.businessName" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 模板编码 -->
              <el-form-item
                :label="$t('dataConfMod.templateCode')"
                prop="templateCode"
              >
                <el-input v-model="flowForm.templateCode" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 模型ID -->
              <el-form-item
                :label="$t('dataConfMod.modeId')"
                prop="modelId"
              >
                <el-input v-model="flowForm.modelId" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 待办URL -->
              <el-form-item
                :label="$t('dataConfMod.pendingApproveUrl')"
                prop="pendingApproveUrl"
              >
                <el-input v-model="flowForm.pendingApproveUrl" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 分流服务 -->
              <el-form-item
                :label="$t('dataConfMod.feignClient')"
                prop="feignClient"
              >
                <el-input v-model="flowForm.feignClient" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 业务服务 -->
              <el-form-item
                :label="$t('dataConfMod.bussinessClass')"
                prop="bussinessClass"
              >
                <el-input v-model="flowForm.bussinessClass" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 表单详情地址 -->
              <el-form-item
                :label="$t('dataConfMod.tableUrl')"
                prop="tableUrl"
              >
                <el-input v-model="flowForm.tableUrl" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 集成模式 -->
              <el-form-item
                :label="$t('dataConfMod.integrationMode')"
                prop="integrationMode"
              >
                <el-select v-model="flowForm.integrationMode">
                  <el-option
                    v-for="item in integrationMode"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 流程标题模版 -->
              <el-form-item
                :label="$t('dataConfMod.titleTemplate')"
                prop="attribute1"
              >
                <template slot="label">
                  {{ $t('dataConfMod.titleTemplate') }}
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                    :content="$t('dataConfMod.titleTemplateToolTip')"
                  >
                    <i class="el-icon-question" />
                  </el-tooltip>
                </template>
                <el-input v-model="flowForm.attribute1" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 是否启用 -->
              <el-form-item
                :label="$t('dataConfMod.enabledUse')"
                prop="enableFlag"
              >
                <el-switch
                  v-model="flowForm.enableFlag"
                  active-value="Y"
                  inactive-value="N"
                  style="line-height:30px"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-collapse
          v-model="activeDims"
          class="collapseStyle"
        >
          <el-collapse-item
            :title="$t('dataConfMod.eventSent')"
            name="1"
          >
            <div style="margin-bottom: 8px">
              <el-button
                type="primary"
                class="detail-pbtn"
                @click="addEvent"
              >
                <!-- 添加 -->
                {{ $t("common.new") }}
              </el-button>
            </div>

            <div class="last-div">
              <el-table
                ref="evenTable"
                :data="eventData"
                border
                style="width: 100%"
                max-height="300"
              >
                <!-- 事件名称 -->
                <el-table-column
                  min-width="80px"
                  :label="$t('dataConfMod.eventName')"
                  prop="description"
                >
                  <template slot-scope="scope">
                    <template
                      v-if="
                        scope.row.editType === 'add' ||
                          scope.row.editType === 'edit'
                      "
                    >
                      <DictSelect
                        v-model="scope.row.description"
                        code="EVENT_TYPE"
                        @focus="selectFocus(scope.$index)"
                        @change="bussinessTypeChange"
                      />
                    </template>
                    <span v-else>{{ scope.row.description }}</span>
                  </template>
                </el-table-column>
                <!-- 事件类型 -->
                <el-table-column
                  min-width="80px"
                  :label="$t('dataConfMod.eventType')"
                  prop="bussinessType"
                >
                  <template slot-scope="scope">
                    <span>{{ scope.row.bussinessType }}</span>
                  </template>
                </el-table-column>
                <!-- 事件处理过程 -->
                <el-table-column
                  min-width="80px"
                  :label="$t('dataConfMod.eventHandle')"
                  prop="bussinessFunction"
                >
                  <template slot-scope="scope">
                    <span>{{ scope.row.bussinessFunction }}</span>
                  </template>
                </el-table-column>
                <!-- 操作 -->
                <el-table-column
                  align="center"
                  :label="$t('common.operation')"
                  width="180"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      v-if="!scope.row.editType !== 'add'"
                      type="text"
                      @click="editHandle(scope.$index, scope.row)"
                    >
                      <!-- 编辑 -->
                      {{ $t("common.edit") }}
                    </el-button>
                    <el-button
                      type="text"
                      @click="delEvent(scope.$index, scope.row)"
                    >
                      <!-- 删除 -->
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <!-- 事件分配 -->

      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="saveHandle"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import { getAllLangList } from '@/api/common'
import { workflowSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'WorkflowTempInfo',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      curOpt: 'add',
      selectList: [],
      currentRow: null,
      templateHeadId: '',
      templateNames: [],
      eventData: [],
      flowForm: {
        templateId: '', // 业务名称
        templateCode: '', // 业务编码
        modelId: '', // IFlow_FDID
        feignClient: '', // 分流服务
        bussinessClass: '', // 业务服务
        pendingApproveUrl: '', // 代办URL
        language: '', // 语言
        integrationMode: '', // 集成模式
        businessName: '', // 业务编码
        tableUrl: '', // 表单详情地址
        enableFlag: 'Y', // 是否启用
        attribute1: '' // 流程标题模版
      },
      flowFormRules: {
        templateCode: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.templateCode') }
        ],
        modelId: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.modeId') }
        ],
        feignClient: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.feignClient') }
        ],
        bussinessClass: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.bussinessClass') }
        ],
        pendingApproveUrl: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.pendingApproveUrl') }
        ],
        tableUrl: [
          { required: true, message: this.$t('common.pleaseInput') + this.$t('dataConfMod.tableUrl') }
        ]
      },
      langList: [], // 语言类型
      eventTypeList: [], // 事件类型
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      activeDims: ['1'],
      preArr: [
        {
          prop: 'userID',
          label: () => this.$t('dataConfMod.userID') // '账号'
        },
        {
          prop: 'userName',
          label: () => this.$t('dataConfMod.userName') // '姓名'
        },
        {
          prop: 'enableDate',
          label: () => this.$t('dataConfMod.startDate'), // '生效日期'
          type: 'date'
        }
      ],
      curOrderId: null,
      integrationMode: [
        { value: 'Product', label: this.$t('cusEntry.supplement20250211.productWorkflowIntegrationMode') }, // '产品工作流集成模式'
        { value: 'IdeFlow', label: this.$t('cusEntry.supplement20250211.ideWorkflowIntegrationMode') },  // 'IDE工作流集成模式'
        { value: 'Iframe', label: this.$t('cusEntry.supplement20250211.iframeEmbeddedPageMode') },  // 'iframe嵌入页面模式'
        { value: 'Self', label: this.$t('cusEntry.supplement20250211.selfPageMode') },  // '自带页面模式'
        { value: 'Push', label: this.$t('cusEntry.supplement20250211.noPagePushMode') },  // '无页面推送模式'
        { value: 'IdeSdk', label: this.$t('cusEntry.supplement20250211.ideSdkPushMode') },  // 'IdeSdk推送模式'
        { value: 'None', label: this.$t('cusEntry.supplement20250211.noWorkflowMode') }  // '无工作流模式'
      ]
    }
  },
  created () {
    this.fatchDictData() // 字典
    this.fatchTemplateName() // 模板名称
    this.$nextTick(() => {
      this.curOpt = this.$attrs.params.flag
      if (this.$attrs.params.flag === 'edit') {
        this.templateHeadId = this.$attrs.params.templateHeadId
        this.fatchOldData() // 查询旧数据
      }
    })
  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 语言列表
      getAllLangList().then(res => {
        if (res) {
          this.langList = res.data
        }
      })
    },
    fatchTemplateName () {
      workflowSetting.queryEnablePermission().then(res => {
        if (res) {
          this.templateNames = res.data
        }
      })
    },
    selectFocus (index) {
      this.currentRow = index
    },
    // 事件切换选择
    bussinessTypeChange (val) {
      let row = this.eventTypeList.find(item => {
        return item.label === val
      })
      this.eventData[this.currentRow].bussinessType = row.value
      this.eventData[this.currentRow].bussinessFunction = row.value
    },
    // 模板切换  暂时不用
    templateChange (val) {
      let row = this.templateNames.find(item => {
        return item.functionId === val
      })
      if (val && row) {
        this.flowForm.templateCode = row.functionCode
      }
    },
    // 查询旧数据
    fatchOldData () {
      let templateHeadId = this.templateHeadId
      if (templateHeadId) {
        workflowSetting.queryProcessTemplateById({ templateHeadId }).then(res => {
          if (res && res.data) {
            let formData = res.data
            this.flowForm.templateHeadId = formData.templateHeadId
            this.flowForm.templateId = formData.templateId
            this.flowForm.templateCode = formData.templateCode // 业务编码
            this.flowForm.modelId = formData.modelId // IFlow_FDID
            this.flowForm.feignClient = formData.feignClient // 分流服务
            this.flowForm.bussinessClass = formData.bussinessClass // 业务服务
            this.flowForm.pendingApproveUrl = formData.pendingApproveUrl // 代办URL
            this.flowForm.language = formData.language // 语言
            this.flowForm.enableFlag = formData.enableFlag // 是否启用
            this.flowForm.businessName = formData.businessName // 是否启用
            this.flowForm.integrationMode = formData.integrationMode // 是否启用
            this.flowForm.tableUrl = formData.tableUrl // 表单详情地址
            this.flowForm.attribute1 = formData.attribute1 // 流程标题模版
            this.eventData = res.data.templateLinesList.map(i => ({
              ...i,
              editType: ''
            }))
          }
        })
      }
    },
    // 保存
    saveHandle () {
      this.$refs.form.validate(async (valid, object) => {
        if (valid) {
          let url = ''
          let submitData = {}
          submitData.templateHeader = this.flowForm
          submitData.templateLinesList = this.eventData
          if (this.curOpt === 'add') {
            url = '/api-base/flow/processTemplent/saveProcessTemplate'
            delete submitData.templateHeader.templateHeadId
          } else {
            url = '/api-base/flow/processTemplent/updateProcessTemplate'
          }
          workflowSetting.saveOrUpdateProcessTemplate(url, submitData).then(res => {
            if (res) {
              this.templateHeadId = res.data
              this.curOpt = 'edit'
              this.$message({
                message: res.message,
                type: 'success'
              })
              if (this.templateHeadId) {
                this.fatchOldData() // 查询旧数据
              }
            }
          })
        } else {
          return this.__focus_and_alter_error__(object)
        }
      })
    },
    // 取消
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'workflowInfo')
      } else {
        this.$emit('tab-remove', 'workflowInfo' + this.$attrs.params.row.templateCode)
      }
      this.__setTabTodo('WorkflowList.getQuerydata')
    },
    // 新增
    addEvent () {
      this.eventData.unshift({
        editType: 'add'
      })
    },
    // 编辑数据
    editHandle (index) {
      this.eventData[index].editType = 'edit'
      this.$nextTick(() => {
        this.$refs.evenTable.doLayout()
      })
    },
    // 删除事件
    delEvent (index, row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let templateLinesId = row.templateLinesId
          if (templateLinesId) {
            workflowSetting.deleteTemplateLines({ templateLinesId }).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.eventData.splice(index, 1)
            })
          } else {
            this.eventData.splice(index, 1)
          }
        })
    },

    // 取消编辑
    cancelEdit (row) {
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // '取消更新',
        type: 'warning'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_usersAccessInfo_wrapper {
  .common-style {
    // padding: 16px 16px 70px 16px;
    // border: 1px solid #dfe6ec;
    // border-bottom: 0;
    padding-bottom: 50px;
    &:first-child {
      // border-top: 0;
    }
    &.last-div {
      padding-bottom: 70px;
      // border-bottom: 1px solid #dfe6ec;
    }
  }
}
.collapseStyle {
  border: 1px solid #dfe6ec;
}
</style>
