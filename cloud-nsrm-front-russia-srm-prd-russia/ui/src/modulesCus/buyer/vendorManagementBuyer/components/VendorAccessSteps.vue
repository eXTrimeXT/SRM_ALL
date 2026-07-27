<template>
  <!-- 资质审查、供应商评审，样品确认、物料试用 -->
  <el-steps :active="computedNodes.status" :align-center="true" finish-status="success">
    <el-step v-for="(item, index) in computedNodes.node" :key="index" :title="item.title" />
  </el-steps>
</template>
<script>
export default {
  name: 'VendorAccessSteps',
  props: {
    // 当前所在步骤 // qua | site | sample | material
    accessType: {
      type: String,
      dafault: () => {
        return ''
      }
    },
    approveStatus: {
      type: String,
      dafault: () => {
        return 'DRAFT' // 审批状态
      }
    }
  },
  data () {
    return {
      currentSteps: 0,
      quaStepNodes: [
        { title: '填写资质信息', status: ['DRAFT', 'WITHDRAW', 'REJECTED'] }, // 拟定 撤回 驳回
        { title: '提交成功', status: ['SUBMITTED'] }, // 已提交
        { title: '结果审批', status: ['APPROVED'] } // 已审批 // ABANDONED 已废弃
      ],
      siteStepNodes: [
        { title: '填写现场评审', status: ['DRAFT', 'WITHDRAW', 'REJECTED'] }, // 拟定 撤回 驳回
        { title: '工作人员评审', status: ['PUBLISH'] }, // 已发布
        { title: '评审报告提交', status: ['SUBMITTED'] }, // 已提交
        { title: '结果审批', status: ['APPROVED'] } // 已审批 // ABANDONED 已废弃
      ],
      sampleStepNodes: [
        { title: '发布样品确认单', status: ['DRAFT'] }, // 拟定 撤回 拒绝 REFUSED
        { title: '供应商反馈', status: ['PUBLISHED'] }, // 已发布
        { title: '样品评价', status: ['CONFIRMED', 'SUBMITTED'] }, // 已确认
        { title: '结果审批', status: ['APPROVED'] } // 已审批 // ABANDONED 已废弃
      ],
      materialStepNodes: [
        { title: '发布物料试用单', status: ['DRAFT'] }, // 拟定 撤回 拒绝 REFUSED
        { title: '供应商反馈', status: ['PUBLISHED'] }, // 已发布
        { title: '试用评价', status: ['CONFIRMED', 'SUBMITTED'] }, // 已提交
        { title: '结果审批', status: ['APPROVED'] } // 已审批 // ABANDONED 已废弃
      ]
    }
  },
  computed: {
    computedNodes () {
      let nodeStatus = {}
      let nodeArr = [] // 节点list
      let statusIndex = null // 进度条所在步骤
      if (this.accessType) {
        let currentStatus = this.approveStatus || 'DRAFT'
        switch (this.accessType) {
          case 'qua':
            nodeArr = this.quaStepNodes
            break
          case 'site':
            nodeArr = this.siteStepNodes
            break
          case 'sample':
            nodeArr = this.sampleStepNodes
            break
          case 'material':
            nodeArr = this.materialStepNodes
            break
          default:
            nodeArr = []
            break
        }
        statusIndex = nodeArr.findIndex(i => (i.status.includes(currentStatus)))
        if (statusIndex == nodeArr.length - 1) { // 流程走完的
          statusIndex += 1
        }
        if (currentStatus === 'ABANDONED') { // ABANDONED 废弃的单据
          statusIndex = null
        }
        nodeStatus.node = nodeArr
        nodeStatus.status = statusIndex
        return nodeStatus
      } else {
        nodeStatus.node = []
        nodeStatus.status = statusIndex
        return nodeStatus
      }
    }
  }
}
</script>
