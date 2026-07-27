<!-- 合同履行计划 -->
<template>
  <IMiniTable
    :columns="columns"
    :data="data"
    border
    @selection-change="selectionChange"
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
        { prop: 'index', type: 'index', label: '节点', width: 60 },
        {
          prop: 'milestoneType',
          label: this.$t('contract_mod.processNodeName'),
          minWidth: 130,
          formatter: (...args) => {
            return this.dictClass.getDictLabel('MILESTONE_SCHEDULE', args[2])
          }
        },
        {
          prop: 'planStatus',
          label: '状态',
          minWidth: 90,
          formatter: (...args) => {
            return this.dictClass.getDictLabel('MILESTONE_STATE', args[2])
          }
        },
        {
          prop: 'nodePersonName',
          label: this.$t('节点负责人'),
          renderHeader: this._addStarToColumn,
          minWidth: 150,
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
          label: this.$t('计划开始时间'),
          renderHeader: this._addStarToColumn,
          minWidth: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.planStartDate
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
          label: this.$t('计划结束时间'),
          renderHeader: this._addStarToColumn,
          minWidth: 150,
          render: (h, scope) => {
            if (this.readPretty == true) {
              return scope.row.planEndDate
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
          prop: 'actualCompleteDate',
          label: '实际结束时间',
          minWidth: 130,
          dataType: 'dateTime'
        },
        {
          prop: 'extCreatePerformFlag',
          label: '是否已创绩效评分项目',
          minWidth: 90,
          formatter: (...args) => {
            return this.dictClass.getDictLabel('YES_OR_NO', args[2])
          }
        },
        {
          prop: 'remarks',
          label: this.$t('特殊备注'),
          minWidth: 150,
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
        //   minWidth: 90,
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
          minWidth: 120,
          fixed: 'right',
          render: (h, scope) => {
            let btnArr = []
            const complete = scope.row.planStatus === 'COMPLETE'

            if (!complete && ['SHOW'].includes(scope.row.deliveryButton)) { // SHOW显示，NOT_SHOW 不显示
              btnArr.push(
                <el-button type="text" disabled={(this.disabled && this.mode !== 'manage') || ['COMPLETE'].includes(scope.row.planStatus)} onClick={() => this.handOver(scope.row, 'deliver')} >
                  { this.$t('交付') }
                </el-button>
              )
            }
            if (complete) {
              btnArr.push(
                <el-button type="text" onClick={() => this.handOver(scope.row, 'file')} >
                  { `附件（${scope.row.fileNum || 0}）` }
                </el-button>
              )
            }
            /**
             * 评分按钮显示逻辑
             * 1.履约状态已完成
             * 2.是否已创绩效评分项目为是（存在对应评分单据）
             * 3.合同对应的品类在绩效模型有项目化配置 TODO
             * 4.合同里面的是否需要履约评分为是   TODO
             */
            const { evalFlag } = scope.row
            if (evalFlag === 'Y') {
              btnArr.push(
                <el-button type="text" onClick={() => this.handOver(scope.row, 'score')}>
                  评分
                </el-button>
              )
            }
            return btnArr
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
    },
    selectionChange (row) {
      this.$emit('selection-change', row)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
