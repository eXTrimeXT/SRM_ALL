<template>
  <div class="self-mode">
    <div class="common-style border-buttom">
      <div class="order-status">
        <el-row :gutter="10">
          <el-col :span="12">
            <span class="order-title">{{ subject || subjectAdd }}</span>
            <el-tag v-if="fdStatus">
              {{ fdStatus }}
            </el-tag>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="common-style">
      <el-row :gutter="10">
        <el-col
          :span="6"
          class="formInfo"
        >
          <!-- 审批单号 -->
          <label>{{ $t('flowMod.approveNum') }}</label><el-input
            v-model="formDataMap.number"
            disabled
          />
        </el-col>
        <el-col
          :span="6"
          class="formInfo"
        >
          <!-- 创建人 -->
          <label>{{ $t('common.creator') }}</label><el-input
            v-model="formDataMap.createdFullName"
            disabled
          />
        </el-col>
      </el-row>
    </div>
    <div class="common-style last-div">
      <el-tabs
        v-model="flowActiveName"
        @tab-click="handleClick"
      >
        <!-- 审批记录 -->
        <el-tab-pane
          :label="$t('flowMod.approveRecord')"
          name="first"
        >
          <!-- 审批步骤调 -->
          <div
            v-if="flowProcess.nodes.length > 0"
            class="flowSteps"
          >
            <el-steps
              :align-center="true"
              :active="flowProcess.active"
              finish-status="success"
              :process-status="flowProcess.processStatus"
            >
              <el-step
                v-for="step in flowProcess.nodes"
                :key="step.nodeId"
              >
                <div slot="title">
                  {{ step.nodeId }}{{ step.nodeName }}
                </div>
                <div slot="description">
                  <span
                    v-for="(user, index) in step.handlers"
                    :key="user.id"
                  >{{ user.name }}
                    <i v-if="index !== step.handlers.length - 1">,</i></span>
                </div>
              </el-step>
            </el-steps>
          </div>
          <el-row class="preOpt">
            <!-- 审批流程 -->
            <el-col
              :span="14"
            >
              <span>{{ $t('flowMod.approveFlow') }}</span>
            </el-col>
            <el-col
              :span="10"
              style="text-align:right;"
            >
              <!-- <el-button v-if="fdStatusCode==='05'" type="primary" size="small" @click="getPreFlowApprovers">取上次审批人</el-button> -->
            </el-col>
          </el-row>
          <!-- 提交审批表格 -->
          <el-table
            v-if="
              fdStatusCode === '05' ||
                (roleType === 'draft' && fdStatusCode === '11') ||
                fdStatusCode === ''
            "
            ref="processNodesTable"
            :data="processNodes"
            border
            style="width: 100%"
            max-height="300"
          >
            <el-table-column
              min-width="50px"
              type="index"
            />
            <!-- 审批节点 -->
            <el-table-column
              min-width="80px"
              :label="$t('flowMod.approveNode')"
              prop="nodeId"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.nodeId }}</span>
              </template>
            </el-table-column>
            <!-- 流转方式 -->
            <el-table-column
              min-width="80px"
              :label="$t('flowMod.processType')"
              prop="processType"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.processType }}</span>
              </template>
            </el-table-column>
            <!-- 节点名称 -->
            <el-table-column
              min-width="100px"
              :label="$t('flowMod.nodeName')"
              prop="nodeName"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.nodeName }}</span>
              </template>
            </el-table-column>
            <!-- 审批人 -->
            <el-table-column
              min-width="120px"
              :label="$t('flowMod.approvers')"
              prop="handlers"
            >
              <template slot-scope="scope">
                <el-row>
                  <el-col :span="20">
                    <el-tag
                      v-for="item in scope.row.handlers"
                      :key="item.id"
                    >
                      {{ item.name || item.id }}
                    </el-tag>
                  </el-col>
                  <el-col
                    v-if="
                      (fdStatusCode === '05' ||
                        (roleType === 'draft' && fdStatusCode === '11')) &&
                        !(scope.row.activityType === 'startNode') &&
                        !(scope.row.activityType === 'endNode')
                    "
                    :span="4"
                    style="text-align: right;"
                  >
                    <i
                      :class="[
                        'el-icon-s-custom selectPople',
                        { notReq: scope.row.activityType === 'sendNode' }
                      ]"
                      @click="selectPeople(scope.$index)"
                    />
                  </el-col>
                </el-row>
              </template>
            </el-table-column>
          </el-table>
          <!-- 查看审批意见表格 -->
          <el-table
            v-else
            :data="auditData"
            border
            style="width: 100%"
            max-height="300"
          >
            <el-table-column
              min-width="50px"
              type="index"
            />
            <!-- 审批节点 -->
            <el-table-column
              prop="fdNodeId"
              min-width="80px"
              :label="$t('flowMod.approveNode')"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.fdNodeId }}</span>
              </template>
            </el-table-column>
            <!-- 节点名称 -->
            <el-table-column
              min-width="100px"
              :label="$t('flowMod.nodeName')"
              prop="fdNodeName"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.fdNodeName }}</span>
              </template>
            </el-table-column>
            <!-- 操作人 -->
            <el-table-column
              min-width="100px"
              :label="$t('flowMod.operator')"
              prop="fdHandlerName"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.fdHandlerName }}</span>
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              prop="fdOperationName"
              min-width="100px"
              :label="$t('common.operation')"
            >
              <template slot-scope="scope">
                <div class="table-opt-type">
                  <i :class="['fdOptType', scope.row.fdOperationType]" />
                  <span>{{ scope.row.fdOperationName }}</span>
                </div>
              </template>
            </el-table-column>
            <!-- 审批意见 -->
            <el-table-column
              min-width="100px"
              :label="$t('flowMod.approveInfo')"
              prop="fdAuditeInfo"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.fdAuditeInfo }}</span>
              </template>
            </el-table-column>
            <!-- 操作时间 -->
            <el-table-column
              min-width="100px"
              :label="$t('common.operationTime')"
              prop="fdHandlerTime"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.fdHandlerTime }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <!-- 流程图 -->
        <el-tab-pane
          :label="$t('flowMod.flowChart')"
          name="flowChart"
        >
          <div>
            <!-- <CchartPlus :fdId="fdId"/> -->
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <!-- <c-toolbar :isNested="true"> -->
    <div class="approve-opt-div">
      <!-- <label class="el-form-item__label">操作角色</label>
      <el-select v-model="roleModel" placeholder="请选择" class="roleSelect">
        <el-option
          v-for="item in roleList"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select> -->
      <div
        v-for="(btns, index) in operationJsonList"
        :key="index"
        class="opt-div-handler"
      >
        <el-button
          v-for="btn in btns.operationList"
          :key="btn.operationType"
          type="primary"
          @click.native="eventBus(btn.operationType, btn.operationType)"
        >
          {{ btn.name }}
        </el-button>
      </div>
      <el-button
        v-if="fdStatusCode == '05' && operationJsonList.length > 0"
        type="primary"
        @click="saveDraftHandel"
      >
        {{ $t('common.save') }}
      </el-button>
      <el-button
        v-if="!isNested"
        type="primary"
        @click="reback"
      >
        {{ $t('common.backTo') }}
      </el-button>
    </div>
    <!-- 人员选择 -->
    <c-people-selector
      ref="peopleSelector"
      :visible.sync="peopleDialog"
      @on-confirm="getPeople"
    />
    <!-- 审批弹框区域-->
    <!-- 审批意见 -->
    <srm-dialog
      :title="$t('flowMod.approveInfo')"
      :visible.sync="auditDialogVisible"
      size="middle"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form
        ref="auditForm"
        :model="auditForm"
        class="form-incontainer form-fill-style"
        label-position="top"
      >
        <el-row
          v-if="handlerType === 'handler_refuse'"
          :gutter="20"
        >
          <el-col :span="8">
            <!-- 驳回到 -->
            <el-form-item
              :label="$t('flowMod.refuseTo')"
              prop="jumpToNodeId"
            >
              <el-select
                v-model="auditForm.jumpToNodeId"
                :disabled="auditForm.refusePassedToThisNode === 'false'"
                @change="nodeSelect"
              >
                <el-option
                  v-for="item in handlerRefuseNodes"
                  :key="item.nodeId"
                  :label="item.nodeName"
                  :value="item.nodeId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item
              label=""
              prop="auditNote"
              style="padding-top:26px"
            >
              <!-- 重新提交后直接返回本节点 -->
              <el-checkbox
                v-model="auditForm.refusePassedToThisNode"
                true-label="true"
                false-label="false"
                @change="changeValue"
              >
                {{ $t('flowMod.refusePassedToThisNode') }}
              </el-checkbox>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col>
            <!-- 处理意见 -->
            <el-form-item
              :label="$t('flowMod.auditNote')"
              prop="auditNote"
            >
              <el-input
                v-model="auditForm.auditNote"
                type="textarea"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="auditDialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <!-- v-if="fdStatusCode=='20'" -->
        <el-button
          type="primary"
          @click="auditHandle(handlerType)"
        >
          {{
            $t('common.submit')
          }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import CPeopleSelector from '@/library/components/c-people-selector'
import {
  getProcessNodesInfo,
  getCurrentProcessInfo,
  getPrevProcessApprovers,
  getAuditeNoteList,
  getProcessRefuseNode,
  workFlowHandel
} from '@/api/workFlow'
export default {
  name: 'SelfMode',
  components: { CPeopleSelector },
  props: {
    needInit: {
      type: Boolean,
      default: false
    },
    isNested: {
      type: Boolean,
      default: false
    },
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      flowActiveName: 'first', // tab name
      peopleDialog: false,
      auditDialogVisible: false,
      modelId: '', // 流程模板ID 流程图显示fdid 传入的是
      fdId: '',
      roleModel: '', // 角色
      roleList: [], // 角色下拉
      selectPerson: false,
      manualNodes: [],
      processNodes: [], // 流程节点
      subject: '', // 流程标题名称
      subjectAdd: '',
      fdStatus: '', // 流程状态
      fdStatusCode: '', // 流程状态编码
      formDataMap: {},
      flowSubmitParame: {}, // 参数对象
      subData: {
        // 提交数据
        businessId: null, // 业务Id
        businessKey: '',
        fdId: null, // 流程Id
        fdModuleId: '',
        subject: '', // 流程标题
        auditNote: '', // 审批意见
        changeNodeHandler: []
      },
      auditForm: {
        // 审批弹框model字段
        auditNote: this.$t('flowMod.agree'), // "同意",
        attachment: [],
        jumpToNodeId: '',
        refusePassedToThisNode: ''
      },
      flowSteps: {
        fdStatusCode: '', // 当前状态
        currNodeList: [],
        nodes: []
      }, // 审批进度条
      processStatus: 'process', // 当前流程的状态
      submitLoading: false,
      auditData: [], // 审批数据意见列表
      preApprovers: [], // 上一次审批人is
      operationJsonList: [], // 操作类型
      handlerType: '', // 操作类型code
      handlerRefuseNodes: [], // 驳回节点
      roleType: '' // 操作类型 (审批人：approve,起草人：draft)
    }
  },
  created () {
    // this.$attrs.params.flowParame 包含的值有：
    // businessId: "114283781214381112" // 单据Id
    // fdId: "1287408088970805803" // 流程Id
    // functionId: "6845200260595712" // 功能Id
    // modelId: "1242837812143800320" // 流程模板Id
    // subject: "美云智数" // 供应商名称
    // let winHeight = document.body.clientHeight
    // let sysHeaderH = (parseFloat(this.$store.getters.sysHeaderHeight) + 50) / 100
    // this.iframeHeight = ((1 - sysHeaderH) * 100) + '%'
    const routerParams = this.$route.query // 路由跳转 首页|任务列表 跳入
    if (!routerParams.fdFormTemplateId && !routerParams.fdId) {
      this.initFlow() // 初始化流程
    }
  },
  methods: {
    // SelfIntegrationMode(自带页面模式)才继续处理页面,开始
    initFlow () {
      // SelfIntegrationMode(自带页面模式)才继续处理页面,开始
      if (this.flowIntegrationMode !== 'Self') {
        return
      }
      // 自定义模式下 首页|任务列表 跳入
      const routerParams = this.$route.query // 路由跳转 首页|任务列表 跳入
      let pareme = {} // 查询流程参数
      if (routerParams.fdFormTemplateId && routerParams.fdId) {
        // 路由跳入
        pareme.fdId = routerParams.fdId
        this.fatchAuditeNoteList(pareme)
        this.getFlowReport(pareme) // 查询审批结果和意见
      } else {
        if (this.isNested) {
          // 页面嵌套
          this.subData.fdId = this.funParams.fdId // 流程ID
          this.subjectAdd = this.funParams.subject // 标题
          this.subData.businessId = this.funParams.businessId // 单据ID
          this.subData.businessKey = this.funParams.modelId // 流程模板ID
          this.modelId = this.funParams.modelId // 流程模板ID
          this.fdId = this.funParams.fdId // 流程ID
        } else {
          this.flowSubmitParame = this.$attrs.params.flowParame // 返回参数
          this.subData.fdId = this.$attrs.params.flowParame.fdId
          this.subjectAdd = this.$attrs.params.flowParame.subject // 标题
          this.subData.businessId = this.$attrs.params.flowParame.businessId // 单据ID
          this.subData.businessKey = this.$attrs.params.flowParame.modelId // 流程模板ID
          this.modelId = this.$attrs.params.flowParame.modelId // 流程模板ID
          this.fdId = this.$attrs.params.flowParame.fdId // 流程ID
        }
        pareme = {
          fdId: this.subData.fdId // 流程Id
        }

        if (!pareme.fdId) return
        if (
          (this.$attrs.params || {}).flag === 'add' ||
          this.funParams.flag === 'add'
        ) {
          // 第一次进来
          this.getFlowReport(pareme) // 查询流程相关数据
          const draftPareme = {
            fdId: this.subData.fdId,
            businessId: this.subData.businessId, // 单据ID
            businessKey: this.subData.businessKey // 流程模板ID
          }
          this.modelId = this.subData.businessKey // 流程模板ID
          this.fdId = this.subData.fdId // 流程ID
          getProcessNodesInfo(draftPareme).then(res => {
            this.processNodes = this.adaptNodeData(res.data.processNodes) // 节点
            this.formDataMap = res.data.formDataMap || {}
          })
        } else {
          this.fatchAuditeNoteList(pareme)
          this.getFlowReport(pareme) // 查询审批结果和意见
        }
      }
      // SelfIntegrationMode(自带页面模式)才继续处理页面,结束
    },
    // 查询审批单据信息
    getFlowReport (pareme) {
      getCurrentProcessInfo(pareme).then(res => {
        if (res.data) {
          this.selectPerson = res.data.selectPerson // 是否选择人员
          // this.manualNodes = res.data.manualNodes
          this.subject = res.data.subject // 状态编码
          this.fdStatus = res.data.fdStatus // 流程状态
          this.fdStatusCode = res.data.fdStatusCode // 状态编码
          this.flowSteps.currNodeList = res.data.currNodeList // 当前节点
          this.flowSteps.nodes = res.data.processNodes // 所有节点
          this.flowSteps.fdStatusCode = res.data.fdStatusCode
          this.subData.businessId = res.data.businessId // 业务Id
          this.subData.businessKey = res.data.businessKey // 流程模板ID
          this.subData.businessId = res.data.businessId // 业务Id
          this.subData.fdId = res.data.fdId // fdIf
          this.subData.subject = res.data.subject || this.subjectAdd
          this.fdId = res.data.fdId
          this.modelId = res.data.businessKey // 流程模板ID
          this.operationJsonList = res.data.operationJsonList // 可操作动作列表
          this.formDataMap = res.data.formDataMap || {}
          if (res.data.operationJsonList.length > 0) {
            this.roleType = res.data.operationJsonList[0].roleType
          }
          if (
            res.data.fdStatusCode === '05' ||
            res.data.fdStatusCode === '11'
          ) {
            // 拟定
            this.processNodes = res.data.processNodes
          }
        }
      })
    },
    // 查询审批意见列表
    fatchAuditeNoteList (pareme) {
      setTimeout(() => {
        // 延迟查询
        getAuditeNoteList(pareme).then(res => {
          if (res.data) {
            this.auditData = res.data
          }
        })
      }, 1000)
    },
    // 适配返回节点数据
    adaptNodeData (data) {
      let arr = []
      if (data) {
        data.map(item => {
          arr.push({
            nodeId: item.nodeId,
            nodeName: item.nodeName,
            processType: item.processType,
            nodeDesc: item.nodeDesc,
            activityType: item.activityType,
            handlers: item.handlers,
            canSelected: item.canSelected,
            mustSelected: item.mustSelected
            // handlerIds: item.handlerIds,
            // handlerNames: item.handlerNames
          })
        })
      }
      return arr
    },
    handleClick (tab, event) {
      console.log(tab, event)
    },
    selectPeople (index) {
      this.currentRow = index
      this.peopleDialog = true
    },
    // 获取选择器
    getPeople (data) {
      let users = []
      if (data && data.length > 0) {
        data.map(item => {
          users.push({
            id: item.username, // 用户名
            name: item.nickname, // 用户名名字
            type: 'USER' // 用户部门
          })
        })
      }
      this.processNodes[this.currentRow].handlers = users
    },
    peopleHandleClose (tag) {
      // this.processNodes[this.currentRow].handlers.splice(this.dynamicTags.indexOf(tag), 1)
    },
    // 取上次审批人
    getPreFlowApprovers () {
      let parame = {
        fdId: this.$attrs.params.flowParame.fdId
      }
      getPrevProcessApprovers(parame).then(res => {
        this.preApprovers = res.data.nodeHandlersList
        let nodes = this.processNodes
        if (this.preApprovers.length > 0) {
          this.preApprovers.map(item => {
            // 判断获取的节点相同 则赋值
            let nodeId = item.nodeId
            nodes.map(pro => {
              if (nodeId === pro.nodeId) {
                pro.handlers = item.handlers
              }
            })
          })
        }
        this.processNodes = nodes
        this.$nextTick(() => {
          this.$refs.processNodesTable.doLayout()
        })
      })
    },
    // 驳回节点选择
    nodeSelect (val) {
      if (!val) {
        this.auditForm.refusePassedToThisNode = false
      } else {
        this.auditForm.refusePassedToThisNode = true
      }
    },
    // 驳回复选框
    changeValue (val) {
      if (val === 'false') {
        this.auditForm.jumpToNodeId = ''
      }
    },
    // 保存草稿
    saveDraftHandel () {
      let url = '/api-base/flow/workFlow/saveDraftDirectly'
      let submitData = this.subData
      let subject = submitData.subject
      submitData.subject =
        this.flowSubmitParame.subject || this.subjectAdd || subject // 流程标题
      submitData.changeNodeHandler = this.processNodes // 节点
      workFlowHandel(url, submitData).then(res => {
        if (res) {
          this.$message({
            message: res.data.message,
            type: 'success'
          })
          let fdId = res.data.fdId
          setTimeout(() => {
            // 延迟查询
            this.getFlowReport({ fdId }) // 查询流程信息
          }, 1000)
        }
      })
    },
    // 提交流程 审批相关
    submitHandel () {
      this.submitLoading = true
    },
    eventBus (eveName, parameters) {
      if (eveName !== 'handler_refuse') {
        delete this.auditForm.jumpToNodeId
        delete this.auditForm.refusePassedToThisNode
      }
      this[eveName](parameters)
    },
    draft (type) {
      // 保存草稿
      console.log(1)
    },
    draft_submit (type) {
      // 起草人提交
      // this.dataHandel(type) // 起草人提交
      // 判断是否选中审批人
      let isHasHandlers = false
      this.processNodes.forEach((item, index) => {
        if (
          index > 0 &&
          index < this.processNodes.length - 1 &&
          item.activityType !== 'sendNode'
        ) {
          if (item.handlers.length == 0) {
            isHasHandlers = false
            return false
          } else {
            isHasHandlers = true
          }
        }
      })
      if (!isHasHandlers) {
        this.$message({
          message: this.$t('flowMod.msgApprovers'), // "请选择审批人员!",
          type: 'error'
        })
      } else {
        this.auditDialogVisible = true
        this.handlerType = type
      }
    },
    draft_abandon (type) {
      // 起草人废弃
      this.auditDialogVisible = true
      this.handlerType = type
    },
    draft_return (type) {
      // 起草人撤回
      this.auditDialogVisible = true
      this.handlerType = type
    },
    handler_pass (type) {
      // 审批通过
      this.auditDialogVisible = true
      this.handlerType = type
    },
    handler_refuse (type) {
      // 驳回
      this.auditForm.refusePassedToThisNode = false // 驳回默认值
      this.auditForm.jumpToNodeId = '' // 驳回默认不选节点
      this.auditDialogVisible = true
      this.handlerType = type
      getProcessRefuseNode({ fdId: this.fdId }).then(res => {
        if (res) {
          this.handlerRefuseNodes = res.data
        }
      })
    },
    handler_abandon (type) {
      // 审批人废弃
      this.auditDialogVisible = true
      this.handlerType = type
    },
    handler_commission (type) {
      // 转办
      this.auditDialogVisible = true
      this.handlerType = type
    },
    handler_communicate (type) {
      // 沟通
      this.auditDialogVisible = true
      this.handlerType = type
    },
    handler_returnCommunicate (type) {
      // 回复沟通
    },
    handler_cancelCommunicate (type) {
      // 取消沟通
    },

    circulate (type) {
      // 传阅
    },
    remind (type) {
      // 提醒
    },
    // 数据操作函数 type 操作类型
    // 起草人提交 draft_submit,
    // 保存草稿 draft,
    // 起草人废弃 draft_abandon,
    // 起草人撤回 draft_return,
    // 通过 handler_pass,
    // 驳回 handler_refuse,
    // 沟通 handler_communicate,
    // 转办 handler_commission,
    // 回复沟通 handler_returnCommunicate,
    // 取消沟通 handler_cancelCommunicate,
    // 审批人废弃 handler_abandon,
    // 传阅  circulate,
    // 催办 remind
    dataHandel (type) {
      let url = '/api-base/flow/workFlow/approveProcess'
      let submitData = this.subData
      submitData.operationType = type
      submitData.subject = this.subject || this.subjectAdd // 流程标题
      submitData.changeNodeHandler = this.processNodes // 节点
      workFlowHandel(url, submitData).then(res => {
        if (res) {
          this.$message({
            message: res.data.message,
            type: 'success'
          })
          this.submitLoading = false
          let fdId = res.data.fdId
          setTimeout(() => {
            // 延迟查询
            this.getFlowReport({ fdId }) // 查询流程信息
            this.fatchAuditeNoteList({ fdId }) // 查询审批意见
          }, 1000)
        }
      })
    },
    //
    // 审批时上传附件成功
    handleAttachmentUploadSuccess (file) {
      this.auditForm.attachment.push(file)
    },
    // 删除附件
    handleAttachmentRemove (fileId) {
      this.auditForm.attachment.splice(1)
    },
    // 填写完审批意见后提交操作
    auditHandle (optType) {
      this.subData.auditNote = this.auditForm.auditNote // 审批意见
      if (optType === 'handler_refuse') {
        // 驳回
        this.subData.refusePassedToThisNode = this.auditForm.refusePassedToThisNode // true 时需要传下面的参数对应的节点Id || false 时 直接驳回到初始节点
        this.subData.jumpToNodeId = this.auditForm.jumpToNodeId // 跳转的节点Id
      }
      this.dataHandel(optType) // 审批通过
      this.auditDialogVisible = false
    },
    reback () {
      let routeQueryFdId = this.$route.query.fdId
      if ((this.$attrs.params || {}).tabName) {
        if (routeQueryFdId) {
          this.$router.go(-1)
        } else {
          this.$emit('tab-remove', this.$attrs.params.tabName)
        }
      } else {
        this.$router.go(-1)
      }
    }
    // SelfIntegrationMode(自带页面模式)才继续处理页面,结束
  }
}
</script>
<style scoped lang="scss">
.workflowReport_wrapper {
  .dialog-footer {
    position: absolute;
  }
  .common-style {
    padding: 10px 0;
    &.border-buttom {
      border-bottom: 1px solid #dfe6ec;
    }
    &.last-div {
      padding-bottom: 90px;
      border-bottom: 0;
    }
  }
  .order-status {
    .order-title {
      font-size: 18px;
      line-height: 30px;
      display: inline-block;
      vertical-align: middle;
    }
    .el-tag {
      margin-left: 10px;
      border-radius: 12px;
      font-size: 12px;
      line-height: 22px;
      height: 22px;
      border: 0;
      padding: 0 10px;
      vertical-align: middle;
    }
  }
  .roleSelect {
    width: 110px;
    margin-right: 10px;
  }
  .selectPople {
    cursor: pointer;
    position: relative;
    font-size: 14px;
    padding: 2px 4px 2px 10px;
    border-left: 1px solid #dedede;
    color: #666;
    &:after {
      position: absolute;
      content: '*';
      color: #f00;
      font-size: 12px;
      left: 6px;
      top: 4px;
    }
    &.notReq {
      &:after {
        display: none;
      }
    }
  }
  .preOpt {
    margin-bottom: 10px;
    span {
      display: inline-block;
      line-height: 30px;
      font-size: 16px;
    }
  }
  .el-col {
    min-height: 16px;
    .el-tag {
      margin-right: 5px;
    }
  }
  .flowSteps {
    padding: 15px 20px;
  }
  .table-opt-type {
    .fdOptType {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 10px;
      background: #bb3939;
      margin-right: 5px;
      vertical-align: middle;
      &.handler_pass {
        // 通过
        background: #31bf9f;
      }
      &.handler_refuse {
        // 驳回
        background: #bb3939;
      }
      &.draft_submit {
        // 起草人提交
        background: #369be4;
      }
      &.draft {
        // 草稿
        background: #bb3939;
      }
      &.draft {
        // 草稿
        background: #bb3939;
      }
      &.draft_abandon {
        // 草稿废弃
        background: #bb3939;
      }
      &.draft_return {
        // 审批人废弃
        background: #bb3939;
      }
      &.handler_abandon {
        // 传阅
        background: #bb3939;
      }
      &.remind {
        // 提醒
        background: #bb3939;
      }
    }
    span {
      display: inline-block;
      vertical-align: middle;
    }
  }
  .opt-div-handler {
    display: inline-block;
  }
  .approve-opt-div {
    position: absolute;
    top: 10px;
    right: 0px;
    .el-button {
      margin-left: 10px;
    }
  }
}
</style>
<style>
.workflowReport_wrapper .el-tabs__content {
  min-height: 200px;
}
.workflowReport_wrapper .el-tabs__item {
  font-size: 16px;
  font-weight: normal !important;
}
.formInfo label {
  display: inline-block;
  width: 40%;
  text-align: right;
  padding-right: 5px;
  box-sizing: border-box;
  color: #606266;
  font-size: 12px;
}
.formInfo .el-input.el-input--mini {
  display: inline-block;
  width: 60%;
}

</style>
