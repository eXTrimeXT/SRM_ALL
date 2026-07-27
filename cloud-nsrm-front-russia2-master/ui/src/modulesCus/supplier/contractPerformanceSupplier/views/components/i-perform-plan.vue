<!-- 合同履行计划 -->
<template>
  <IMiniTable
    :columns="columns"
    :data="data"
    border
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'
import CPeopleSelector from '@/library/components/c-people-selector'
import { Tooltip } from 'vxe-table'

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
  props: ['data', 'mode', 'dictClass', 'disabled', 'readPretty'],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceBillEdit',
        fileType: 'images'
      },
      columns: [
        { prop: 'index', type: 'index', label: () => this.$t('components.processTable.headers.fdNodeName'), width: 50 },  //节点
        {
          prop: 'milestoneType',
          label: this.$t('contract_mod.processNodeName'),
          formatter: (...args) => {
            return this.$getDictLabel('MILESTONE_SCHEDULE', args[2])
          }
        },
        {
          prop: 'planStatus',
          // label: '状态',
          label: () => this.$t('components.stratProcess.headers.docStatusValue'),
          formatter: (...args) => {
            return this.$getDictLabel('MILESTONE_STATE', args[2])
          }
        },
        {
          prop: 'nodePersonName',
          // label: this.$t('节点负责人'),
          label: this.$t('common.nodeLeader'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return (
                scope.row.nodePersonName
              )
            } else {
              return (
                <IPersonSelector
                  value={scope.row.nodePersonName}
                  row={scope.row}
                  disabled={this.disabled}
                />
              )
            }
          }
        },
        {
          prop: 'planStartDate',
          // label: this.$t('计划开始时间'),
          label: this.$t('vendorMod.planStartDate'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return $parseTime(scope.row.planStartDate)
            } else {
              return (
                <el-date-picker
                  v-model={scope.row.planStartDate}
                  type="date"
                  disabled={this.disabled}
                  format={this.$formatDatePicker}
                  value-format="yyyy-MM-dd"
                />
              )
            }
          }
        },
        {
          prop: 'planEndDate',
          // label: this.$t('计划结束时间'),
          label: this.$t('perfMod.planEndDate'),
          renderHeader: this._addStarToColumn,
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return $parseTime(scope.row.planEndDate)
            } else {
              return (
                <el-date-picker
                  v-model={scope.row.planEndDate}
                  type="date"
                  disabled={this.disabled}
                  format={this.$formatDatePicker}
                  value-format="yyyy-MM-dd"
                />
              )
            }
          }
        },
        {
          prop: 'remarks',
          // label: this.$t('特殊备注'),
          label: this.$t('perfMod.specialRemarks'),
          width: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.remarks
            } else {
              return (
                <el-tooltip content={scope.row.remarks} placement="top">
                  <el-input
                    type="text"
                    v-model={scope.row.remarks}
                    disabled={this.disabled}
                    maxlength={30}
                    show-word-limit
                  />
                </el-tooltip>
              )
            }
          }
        },
        // {
        //   prop: 'fileId',
        //   label: this.$t('附件模板'),
        //   render: (h, scope) => {
        //     return (
        //       <SrmCommonFile
        //         default-file= {
        //           {
        //             fileId: scope.row.fileId,
        //             fileName: scope.row.fileName
        //           }
        //         }
        //         readonly={ true}
        //       />
        //     )
        //   }
        // },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          render: (h, scope) => {
            // 完成状态
            const complete = scope.row.planStatus === 'COMPLETE'

            if (!complete && ['SHOW'].includes(scope.row.deliveryButton)) { // SHOW显示，NOT_SHOW 不显示
              return (
                <el-button type="text" disabled={this.disabled && this.mode !== 'manage'} onClick={() => this.handOver(scope.row, 'deliver')} >
                  { this.$t('supRisk.deliver') }
                </el-button>
              )
            }

            if (complete) {
              return (
                <el-button type="text" onClick={() => this.handOver(scope.row, 'file')} >
                  { `${this.$t('components.workedProcess.headers.attachment')}（${scope.row.filenum || 0}）` }
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

  },
  watch: {},
  created () {},
  mounted () {
  },
  methods: {
    handOver (row, type) {
      this.$emit('handover', row, type)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
