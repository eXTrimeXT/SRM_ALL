<template>
  <div class="flow-history">
    <!-- 移动端 -->
    <div v-if="device === 'device-xs'" class="flow-history-device-xs">
      <div class="flow-device-xs-title">
        <span @click="flowInfoHandel('history')">处理详情</span>
        <span class="flow-node" @click="flowInfoHandel('assginee')">流程概览<i class="el-icon-arrow-right" /></span>
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
              <span class="node-date">{{ item.endDate }}</span>
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
    <el-collapse v-else v-model="flowSctiveLine">
      <el-collapse-item :title="$t('components.flownode.approvalInfo')" name="flowHis">
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
            <el-table-column prop="nodeName" :label="$t('common.node')" />
            <el-table-column prop="access" :label="$t('common.operator')" />
            <el-table-column prop="dealDate" :label="$t('components.flownode.nodeReception')" />
            <el-table-column prop="endDate" :label="$t('components.flownode.nodeProcessing')" />
            <el-table-column prop="description" :label="$t('flowMod.auditNote')" />
          </el-table>
        </div>
      </el-collapse-item>
    </el-collapse>
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
      flowSctiveLine: ['flowHis'],
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
    async getInpormation () {
      if (!this.businessId || !this.businessType) {
        return
      }
      let query = {
        businessId: this.businessId,
        businessType: this.businessType,
        processType: this.businessType
      }
      const { data } = await beforeProcess(query)
      let flowinstanceId = data.flowinstanceId || null
      if (flowinstanceId) {
        this.$http({
          url: '/api-base/flow/event/information',
          method: 'get',
          params: query
        }).then(res => {
          this.assgineeList = res.data.assgineeList || []
          this.historyList = res.data.historyList || []
          this.flowInfo = res.data
          this.getActiveStep(res.data)
        })
      }
    },
    getActiveStep (flowInfo) {
      let taskKey = flowInfo.taskKey
      let assgineeList = flowInfo.assgineeList || []
      let historyList = flowInfo.historyList || []
      let activeIndex = assgineeList.findIndex(i => (i.taskKey == taskKey))
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
  margin-top: 32px;
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
