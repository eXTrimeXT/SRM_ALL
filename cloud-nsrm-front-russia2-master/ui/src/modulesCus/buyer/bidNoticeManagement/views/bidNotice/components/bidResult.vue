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
      <template #winAmount="scope">
        <el-input-number
          v-if="!readonly"
          v-model="scope.row.winAmount"
          :disabled="scope.row.isWin === 'N'"
          style="width:100%;"
          :min="0"
          :precision="2"
        />
        <span v-else>{{ scope.row.winAmount }}</span>
      </template>
      <!-- 合同签署单位 -->
      <template #contractSignUnitList="scope">
        <QuickSearch
          :disabled="readonly || scope.row.isWin === 'N'"
          :show-input="scope.row.contractSignUnit"
          :scope-data="scope.row"
          name="pj_bpm_incorporated_company_quicksearch"
          multiSelect
          disabledSelect
          @close-quicksearch="getCompany"
        />
      </template>
      <!-- 合同周期 -->
      <template #contractPeriod="scope">
        <el-input v-if="!readonly" v-model="scope.row.contractPeriod" />
        <span v-else>{{ scope.row.contractPeriod }}</span>
      </template>
      <!-- 是否履约评价 -->
      <template #isPerformanceEvaluated="scope">
        <DictSelect v-if="!readonly" v-model="scope.row.isPerformanceEvaluated" :disabled="scope.row.isWin === 'N'" code="YES_OR_NO" />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isPerformanceEvaluated) }}</span>
      </template>
      <!-- 不履约评价的原因 -->
      <template #nonPerformanceReason="scope">
        <el-input v-if="!readonly" v-model="scope.row.nonPerformanceReason" :disabled="scope.row.isWin === 'N'" />
        <span v-else>{{ scope.row.nonPerformanceReason }}</span>
      </template>
      <!-- 是否现场考察 -->
      <template #isOnSiteInspected="scope">
        <span>{{ $getDictLabel('YES_OR_NO',scope.row.isOnSiteInspected) }}</span>
      </template>
      <!-- 考察详情 -->
      <!-- <template #assessDetail="scope">
        <el-button type="text" @click="viewAssess(scope.row)">
          {{ $t('common.view') }}
        </el-button>
      </template> -->
      <!-- 通知书附件模板 -->
      <template #noticeTemplate="scope">
        <el-button type="text" @click="downloadTemplate(scope.row)">
          {{ $t('dataConfMod.attachTemplate') }}
        </el-button>
      </template>
      <!-- 发送通知书 -->
      <template #sendNotice="scope">
        <el-button v-if="form.status === 'APPROVED'" type="text" @click="sendNotice(scope.row)">
          {{ $t('mailManagement.send') }}
        </el-button>
      </template>
      <!-- 通知书附件 -->
      <template #noticeAttachmentName="scope">
        <!--附件名称-->
        <SrmCommonFile
          v-if="!readonly"
          type="default"
          :default-file="{
            fileId: scope.row.noticeAttachmentId,
            fileName: scope.row.noticeAttachmentName
          }"
          :validateOptions="{
            accept:['.pdf'],
            size: null
          }"
          @on-change="(value) => filesChange(value,scope.$index)"
        />
        <SrmCommonFile
          v-else
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
import QuickSearch from 'lib@/components/QuickSearch'
import { transformMQL } from 'lib@/utils/util'
import bidNoticeHttp from '../api'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  components: {
    BaseTable,
    QuickSearch
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
      tableColumns: [],
      companyInfo: [],
      tableData: []
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      }
    },
    form: {
      handler () {
        this.initTable()
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    // this.getOrgInfoList()
  },
  methods: {
    initTable () {
      this.tableColumns = [
        {
          attrs: {
            label: this.$t('common.sort'), // '序号'
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: this.$t('common.vendorName'), // '供应商名称'
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'isWin',
            label: this.$t('cusEntry.bidMod.isWin'), // '是否中标'
            minWidth: 120,
            renderHeader: this._addStarToColumn
          },
          slot: 'isWin'
        },
        {
          attrs: {
            prop: 'noticeAttachmentName',
            label: this.$t('cusEntry.supplement20250121.noticeAttachmentId'), // 通知书附件
            minWidth: 200,
            renderHeader: this._addStarToColumn
          },
          slot: 'noticeAttachmentName'
        },
        {
          attrs: {
            prop: 'contractPeriod',
            label: this.$t('cusEntry.supplement20250121.contractPeriod'), // '合同周期'
            minWidth: 120
          },
          slot: 'contractPeriod'
        },
        {
          attrs: {
            prop: 'isOnSiteInspected',
            label: this.$t('cusEntry.supplement20250205.isOnSiteInspected'), // 是否现场考察
            minWidth: 120
          },
          slot: 'isOnSiteInspected'
        },
        {
          attrs: {
            prop: 'contractSignUnitList',
            label: this.$t('cusEntry.supplement20250121.contractSign'), // '合同签署单位',
            minWidth: 200,
            renderHeader: this._addStarToColumn
          },
          slot: 'contractSignUnitList'
        },
        {
          attrs: {
            prop: 'vendorCode',
            label: this.$t('common.vendorCode'), // '供应商编码'
            minWidth: 120,
            showOverflowTooltip: true
          }
        },
        // {
        //   attrs: {
        //     prop: 'winAmount',
        //     label: this.$t('cusEntry.supplement20250121.winAmount'), // '中标金额（卢布）'
        //     minWidth: 150,
        //     renderHeader: this._addStarToColumn
        //   },
        //   slot: 'winAmount'
        // },
        // {
        //   attrs: {
        //     prop: 'isPerformanceEvaluated',
        //     label: '是否履约评价',
        //     minWidth: 130,
        //     renderHeader: this._addStarToColumn
        //   },
        //   slot: 'isPerformanceEvaluated'
        // },
        // // 履约评价为否，必填
        // {
        //   attrs: {
        //     prop: 'nonPerformanceReason',
        //     label: '不履约评价的原因',
        //     minWidth: 130,
        //     renderHeader: this._addStarToColumn
        //   },
        //   slot: 'nonPerformanceReason'
        // },
        // {
        //   attrs: {
        //     prop: 'assessDetail',
        //     label: '考察详情',
        //     minWidth: 120
        //   },
        //   slot: 'assessDetail'
        // },
        // {
        //   attrs: {
        //     prop: 'noticeTemplate',
        //     label: this.$t('cusEntry.supplement20250205.noticeTemplate'), // 通知书附件模板
        //     minWidth: 150
        //   },
        //   slot: 'noticeTemplate'
        // },
      ]
      if (this.form.status != 'DRAFT' && this.form.status != 'REJECTED' && this.form.status != 'WITHDRAW') {
        this.tableColumns.splice(4, 0, {
          attrs: {
            prop: 'sendNotice',
            label: this.$t('cusEntry.supplement20250205.sendNotice'), // 发送通知书
            minWidth: 120
          },
          slot: 'sendNotice'
        })
        this.tableColumns.splice(6, 0, {
          attrs: {
            prop: 'isSend',
            label: this.$t('cusEntry.supplement20250205.isSend'), // 是否已发送
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        })
      }
    },
    downloadTemplate (row) {
      downloadFileLink(
        `/api-sou/sou/api/v1/bidNotice/downloadProjectNoticeTemplate?bidNoticeDetailId=${row.bidNoticeDetailId}`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    getOrgInfoList () {
      this.$http({
        url: '/api-pj/organization/organization/listAllOrganization',
        method: 'POST',
        data: {
          organizationTypeCode: 'OU',
          pageNum: 1,
          pageSize: 2000
        },
        loading: false
      })
        .then(res => {
          const companys = []
          res.data.list.forEach(v => {
            companys.push({
              value: v.organizationId.toString(),
              label: v.organizationName
            })
          })
          this.companyInfo = companys
        })
        .catch(err => {
          console.log(err)
        })
    },
    changisPerformanceEvaluated (val, i) {
      val == 'Y' && (this.tableData[i].nonPerformanceReason = '')
    },
    filesChange ({ file }, $index) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].noticeAttachmentId = fileId
      this.tableData[$index].noticeAttachmentName = fileName
    },
    contractSignUnitChange (val, row, index) {
      let newRow = { ...row, contractSignUnitList: val, contractSignUnit: val.join(',') }
      this.tableData.splice(index, 1, newRow)
    },
    getCompany (val, scope) {
      let contractSignUnitId = []
      let contractSignUnitCode = []
      let contractSignUnit = []
      let contractSignUnitCredit = []
      for (let item of val) {
        contractSignUnitId.push(item.bpmIncorporatedCompanyId)
        contractSignUnitCode.push(item.companyNo)
        contractSignUnit.push(item.companyName)
        contractSignUnitCredit.push(item.creditCode)
      }
      scope.contractSignUnitId = contractSignUnitId.join(',')
      scope.contractSignUnitCode = contractSignUnitCode.join(',')
      scope.contractSignUnit = contractSignUnit.join(',')
      scope.contractSignUnitCredit = contractSignUnitCredit.join(',')
    },
    async sendNotice (row) {
      let transformParams = transformMQL.save('BidNoticeDetail', [{ ...row }], 'send')
      const response = await bidNoticeHttp.vendorSendNotice(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.$emit('send')
      }
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
    },
    getParamsData () {
      return this.tableData
    }
  }
}
</script>
