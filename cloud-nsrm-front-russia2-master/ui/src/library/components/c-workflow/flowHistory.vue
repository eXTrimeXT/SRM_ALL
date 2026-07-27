<template>
  <div class="flow-history">
    <!-- 移动端 -->
    <div v-if="device === 'device-xs'" class="flow-history-device-xs">
      <div class="flow-device-xs-title">
        <span @click="flowInfoHandel('history')">
          <!-- 处理详情 -->
          {{ $t('cusEntry.library.processDetails') }}
        </span>
        <span class="flow-node" @click="flowInfoHandel('assginee')">
          <!-- 流程概览 -->
          {{ $t('cusEntry.library.processOverview') }}<i class="el-icon-arrow-right" />
        </span>
      </div>
      <div class="history-list">
        <!-- 处理详情 -->
        <el-steps
          v-show="flowIndex=='history'"
          :active="hisActiveStep"
          finish-status="success"
          direction="vertical"
          class="history-list-vertical-steps"
        >
          <el-step
            v-for="item in historyList"
            :key="item.nodeName+'_'+item.taskId"
            :title="item.nodeName"
            :description="item.access"
          >
            <template slot="title">
              <span class="node-name">{{ item.nodeName }}</span>
              <span class="node-date">{{ $parseTime(item.endDate) }}</span>
            </template>
            <template slot="description">
              <div class="description">
                {{ item.access }}
              </div>
              <div class="description">
                {{ item.description }}
              </div>
            </template>
          </el-step>
        </el-steps>
        <!-- 流程概览 -->
        <el-steps
          v-show="flowIndex=='assginee'"
          :active="activeStep"
          finish-status="success"
          direction="vertical"
          class="history-list-vertical-steps"
        >
          <el-step
            v-for="item in assgineeList"
            :key="item.taskKey"
            :title="item.taskName"
            :description="item.assigneeName"
          />
        </el-steps>
      </div>
    </div>
        <!-- PC 端 -->
        <div v-if="assgineeList.length>0" class="assginee-list">
          <el-steps
            :active="activeStep"
            finish-status="success"
            direction="horizontal"
            align-center
          >
            <el-step
              v-for="item in assgineeList"
              :key="item.taskKey"
              :title="item.taskName"
              :description="item.assigneeName"
              :status="item.comment === '审批中' ? 'process' : null"
            />
          </el-steps>
        </div>
        <div class="history-list">
          <el-table
            :data="historyList"
            style="width: 100%"
            border
            max-height="390px"
            highlight-current-row
          >
            <el-table-column prop="taskName" :label="$t('common.node')" />
            <el-table-column prop="operatorName" :label="$t('common.operator')" />
            <el-table-column prop="operateTypeText" :label="$t('common.operation')">
              <template slot-scope="scope">
                {{ scope.row.operateTypeText }}
              </template>
            </el-table-column>
            <!--附件-->
            <el-table-column prop="operateTypeText" :label="$t('bidMod.attachment')">
              <template slot-scope="scope">
                <SrmCommonFile
                  :fileList="scope.row.attachs"
                  multiple
                  :readonly="true"
                />
              </template>
            </el-table-column>
            <el-table-column prop="comment" :label="$t('flowMod.auditNote')" />
            <el-table-column prop="operateTime" :label="$t('components.flownode.nodeProcessing')" >
              <template slot-scope="scope">
                {{$parseTime(scope.row.operateTime, '{d}-{m}-{y} {h}:{i}:{s}')}}
              </template>
            </el-table-column>
          </el-table>
        </div>
  </div>
</template>

<script>
import { beforeProcess } from '@/api/workFlow'
export default {
  name: 'FlowHistory',
  props: {
    businessType: {
      type: String,
      default: ''
    },
    businessId: {
      type: [String, Number],
      default: ''
    }
  },
  data () {
    return {
      assgineeList: [], // 审批人
      historyList: [], // 审批历史
      flowInfo: {},
      activeStep: null,
      hisActiveStep: null,
      flowIndex: 'history' // history | assginee
    }
  },
  computed: {
    device () {
      return this.$store.state.app.device
    }
  },
  watch: {
    businessId: {
      handler (data) {
        if (data) {
          this.getInpormation()
        }
      },
      deep: true
    }
  },
  created () {
    this.getInpormation()
  },
  methods: {
    flowInfoHandel (info) {
      this.flowIndex = info
    },
    // 修改传入的数据并抛出处理后的数据
    changeListData (dataList) {
      let attr = []
      dataList.forEach(item => {
        if (item.childTasks && item.childTasks.length > 0) { // 如果有子节点的情况
          item.childTasks.forEach(itemChild => {
            attr.push(itemChild)
          })
        } else {
          attr.push(item)
        }
      })
      return attr
    },
    async getInpormation () {
      if (!this.businessId || !this.businessType) {
        return
      }
      let query = {
        "actionCode": this.businessType,
        "formDataId": this.businessId // 业务单据ID(如果不传递，则是默认流程配置数据)
      }
          // 查询stap信息
          this.$http({
            url: '/api-base/flow/event/v2/instance/info/list',
            method: 'post',
            data: query
          }).then(res => {
            this.assgineeList = this.changeListData(res.data)
            // 根据用户账号判断当前审批节点
            let username = this.$store.getters.userInfo.username
            let flowData = this.assgineeList.find(i => i.comment === '审批中' && i.assignees.some(item => item.assigneeId === username)) || {}
            this.flowInfo = flowData
            this.$emit('updateFlowData', flowData)
            this.$emit('returnAssgines', this.assgineeList)
            // 查询历史信息
            this.$http({
              url: '/api-base/flow/event/v2/instance/record/list',
              method: 'post',
              data: {
                "actionCode": this.businessType,
                "formDataId": this.businessId // 业务单据ID(如果不传递，则是默认流程配置数据)
              }
            }).then(res => {
              for (let i = 0; i < res.data.length; i++) {
                res.data[i].attachs.forEach(e => {
                  e.fileName = e.filename
                  e.fileId = e.filePath
                })
              }
              this.historyList = res.data
              this.getActiveStep()
            })
          })


    },
    getActiveStep () {
      let assgineeList = this.assgineeList || []
      let historyList = this.historyList || []
      let activeIndex = assgineeList.findIndex(i => i.comment === '审批中')
      if (activeIndex > -1) {
        this.activeStep = activeIndex
      } else {
        let passTime = 0
        assgineeList.forEach(elm => {
          if (elm.isPass == 'Y') {
            passTime += 1
          }
        })
        if (passTime == assgineeList.length) {
          this.activeStep = passTime
        } else {
          this.activeStep = null
        }
      }
      this.hisActiveStep = historyList.length - 1
    }
  }
}
</script>

<style lang="scss">
.flow-history{
  .el-collapse{
    border-top: none;
    margin-top: -5px;
  }
  .flow-history-device-xs{
    .flow-device-xs-title{
      background-color: #f4f5f7;
      height: 32px;
      line-height: 32px;
      padding: 0 16px;
      font-size: 12px;
      display: flex;
      span{
        width: 50%;
        cursor: pointer;
        &.flow-node{
          color: #96999c;
          text-align: right;
          line-height: 32px;
        }
      }
    }
  }
  .assginee-list{
    padding: 0px;
    margin-bottom: 16px;
  }
  .history-list{
    padding: 0px 0 16px;
    .history-list-vertical-steps{
      .node-name{
        color: #393E45;
      }
      .node-date{
        float: right;
        color: #96999C;
        font-weight: normal;
      }
      .description{
        color: #96999C;
        line-height: 20px;
      }
    }
  }
}
</style>
