<template>
  <div class="wrapper">
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <!-- 是否中标 -->
      <template #isWin="scope">
        <span>{{ $getDictLabel('YES_OR_NO',scope.row.isWin) }}</span>
      </template>
      <!-- 合同签署单位 -->
      <template #contractSignUnitList="scope">
        <DictSelect
          v-model="scope.row.contractSignUnitList"
          multiple
          code="ORG_INFO"
          custom-select-type="ORG_INFO"
          :disabled="readonly"
          @change="(val) => contractSignUnitChange(val,scope.row,scope.$index)"
        />
      </template>
      <!-- 合同周期 -->
      <template #contractPeriod="scope">
        <el-input v-if="!readonly" v-model="scope.row.contractPeriod" />
        <span v-else>{{ scope.row.contractPeriod }}</span>
      </template>
      <!-- 是否履约评价 -->
      <template #isPerformanceEvaluated="scope">
        <DictSelect v-if="!readonly" v-model="scope.row.isPerformanceEvaluated" code="YES_OR_NO" />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isPerformanceEvaluated) }}</span>
      </template>
      <!-- 不履约评价的原因 -->
      <template #nonPerformanceReason="scope">
        <el-input v-if="!readonly" v-model="scope.row.nonPerformanceReason" />
        <span v-else>{{ scope.row.nonPerformanceReason }}</span>
      </template>
      <!-- 是否现场考察 -->
      <template #isOnSiteInspected="scope">
        <DictSelect v-if="!readonly" v-model="scope.row.isOnSiteInspected" code="YES_OR_NO" />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isOnSiteInspected) }}</span>
      </template>
      <!-- 考察详情 -->
      <template #assessDetail="scope">
        <el-button type="text" @click="viewAssess(scope.row)">
          查看
        </el-button>
      </template>
      <!-- 发送通知书 -->
      <template #sendNotice="scope">
        <el-button type="text">
          发送
        </el-button>
      </template>
      <!-- 通知书附件 -->
      <template #noticeAttachmentName="scope">
        <SrmCommonFile
          readonly
          :default-file="{
            fileId: scope.row.noticeAttachmentId,
            fileName: scope.row.noticeAttachmentName
          }"
        />
      </template>
    </BaseTable>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'

export default {
  components: {
    BaseTable
  },
  props: {
    form: {
      type: Object,
      default: () => {}
    },
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      tableColumns: []
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  created () {
    this.tableColumns = [
      {
        attrs: {
          label: '序号',
          type: 'index',
          width: 60
        }
      },
      {
        attrs: {
          prop: 'vendorCode',
          label: '供应商编码',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'vendorName',
          label: '供应商名称',
          minWidth: 150,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'isWin',
          label: '是否中标',
          minWidth: 100
        },
        slot: 'isWin'
      },
      {
        attrs: {
          prop: 'winAmount',
          label: '中标金额（万元）',
          minWidth: 130
        }
      },
      {
        attrs: {
          prop: 'contractSignUnitList',
          label: '合同签署单位',
          minWidth: 230
        },
        slot: 'contractSignUnitList'
      },
      {
        attrs: {
          prop: 'contractPeriod',
          label: '合同周期',
          minWidth: 120
        },
        slot: 'contractPeriod'
      },
      {
        attrs: {
          prop: 'isPerformanceEvaluated',
          label: '是否履约评价',
          minWidth: 130,
          renderHeader: this._addStarToColumn
        },
        slot: 'isPerformanceEvaluated'
      },
      // 履约评价为否，必填
      {
        attrs: {
          prop: 'nonPerformanceReason',
          label: '不履约评价的原因',
          minWidth: 130
        },
        slot: 'nonPerformanceReason'
      },
      {
        attrs: {
          prop: 'isOnSiteInspected',
          label: '是否现场考察',
          minWidth: 130
        },
        slot: 'isOnSiteInspected'
      },
      {
        attrs: {
          prop: 'assessDetail',
          label: '考察详情',
          minWidth: 120
        },
        slot: 'assessDetail'
      },
      {
        attrs: {
          prop: 'noticeAttachmentName',
          label: '通知书附件',
          minWidth: 230
        },
        slot: 'noticeAttachmentName'
      },
      {
        attrs: {
          prop: 'isSend',
          label: '是否发送',
          minWidth: 120,
          formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        }
      }
    ]
  },
  methods: {
    filesChange ({ file }, $index) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].noticeAttachmentId = fileId
      this.tableData[$index].noticeAttachmentName = fileName
    },
    contractSignUnitChange (val, row, index) {
      let newRow = { ...row, contractSignUnitList: val, contractSignUnit: val.join(',') }
      this.tableData.splice(index, 1, newRow)
    },
    viewAssess (row) {
      if (!row.inspectId) return
      this.$router.push({
        name: 'inspectManage',
        params: {
          from: 'bidNotice',
          row: {
            inspectId: row.inspectId,
            reportNum: row.reportNum
          }
        }
      })
    }
  }
}
</script>
