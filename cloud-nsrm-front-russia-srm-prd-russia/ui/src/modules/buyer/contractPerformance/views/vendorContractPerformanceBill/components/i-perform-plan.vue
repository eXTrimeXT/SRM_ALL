<!-- 合同履行计划 -->
<template>
  <i-mini-table
    :columns="columns"
    :data="data"
    border
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'
import CPeopleSelector from '@/library/components/c-people-selector'

const IPersonSelector = {
  props: ['value', 'row', 'disabled'],
  data () {
    return {
      visible: false
    }
  },
  components: { CPeopleSelector },
  render (h) {
    const listeners = {
      'on-confirm': (data) => {
        if (!data) {
          return
        }
        const [user = {}] = data
        this.row.nodePersonName = user.nickname || ''
        this.row.nodePersonId = user.userId || ''
        this.row.nodePersonBy = user.username || ''
      },
      'update:visible': (value) => (this.visible = value)
    }
    return (
      <div>
        <el-input value={this.value} disabled={this.disabled}>
          <div slot="append">
            <el-button
              icon="el-icon-search"
              disabled={this.disabled}
              size="medium"
              onClick={() => (this.visible = true)}
            />
          </div>
        </el-input>
        <c-people-selector on={{ ...listeners }} visible={this.visible} multiSelect={false} />
      </div>
    )
  }
}

export default {
  name: 'IPerformPlan',
  components: {
    IMiniTable
  },
  props: ['data', 'mode', 'store'],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceBillEdit',
        fileType: 'images'
      },
      columns: [
        { prop: 'index', type: 'index' },
        {
          prop: 'isSelected',
          label: '是否有效',
          render: (h, scope) => {
            return (
              <el-checkbox
                onChange={() => this.$emit('payment-change', scope.row)}
                true-label="Y"
                false-label="N"
                disabled={this.disabled}
                v-model={scope.row.isSelected}
              />
            )
          }
        },
        {
          prop: 'paymentNode',
          label: this.$t('付款节点'),
          render: (h, scope) => {
            return (
              <el-checkbox
                onChange={() => this.$emit('payment-change', scope.row)}
                true-label="Y"
                false-label="N"
                disabled={this.disabled}
                v-model={scope.row.paymentNode}
              />
            )
          }
        },
        {
          prop: 'milestoneType',
          label: this.$t('contract_mod.processNodeName'),
          formatter: (...args) => {
            return this.store.getLabel('MILESTONE_SCHEDULE', args[2])
          }
        },
        {
          prop: 'planStatus',
          label: this.$t('状态'),
          formatter: (...args) => {
            return this.store.getLabel('MILESTONE_STATE', args[2])
          }
        },
        {
          prop: 'nodePersonName',
          label: this.$t('节点负责人'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            return (
              <IPersonSelector
                value={scope.row.nodePersonName}
                row={scope.row}
                disabled={this.disabled}
              />
            )
          }
        },
        {
          prop: 'planStartDate',
          label: this.$t('计划开始时间'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            return (
              <el-date-picker
                v-model={scope.row.planStartDate}
                type="date"
                disabled={this.disabled}
                value-format="yyyy-MM-dd"
              />
            )
          }
        },
        {
          prop: 'planEndDate',
          label: this.$t('计划结束时间'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            return (
              <el-date-picker
                v-model={scope.row.planEndDate}
                type="date"
                disabled={this.disabled}
                value-format="yyyy-MM-dd"
              />
            )
          }
        },
        { prop: 'nodePlanNum', label: this.$t('节点交付数量') },
        { prop: 'practicallyEndDate', label: this.$t('实际结束时间') },
        { prop: 'rejectionReason', label: this.$t('驳回原因') },
        { prop: 'remarks', label: this.$t('特殊备注') },
        {
          prop: 'fileId',
          label: this.$t('附件模板'),
          render: (h, scope) => {
            return (
              <SrmCommonFile
                default-file= {
                  {
                    fileId: scope.row.fileId,
                    fileName: scope.row.fileName
                  }
                }
                readonly={ true}
              />
            )
          }
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          render: (h, scope) => {
            // 交付
            if (this.mode !== 'handOver') return null
            let text = this.$t('common.view')
            let disabled = true
            if (['REJECTED', 'UNFINISHED_WORK'].includes(scope.row.planStatus)) {
              text = this.$t('交付')
              disabled = false
            }
            if (scope.row.isSelected === 'Y' && scope.row.show) {
              return (
                <el-button type="text" onClick={() => this.handOver(scope.row, disabled)}>
                  { text }
                </el-button>
              )
            }
            return null
          }
        }
      ]
    }
  },
  computed: {
    disabled () {
      return ['handOver', 'approved', 'view'].includes(this.mode)
    }
  },
  watch: {},
  created () {},
  mounted () {
  },
  methods: {
    handOver (row, disabled) {
      this.$emit('handover', row, disabled)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
