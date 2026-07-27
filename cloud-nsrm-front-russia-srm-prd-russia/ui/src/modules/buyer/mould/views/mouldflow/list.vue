<template>
  <el-container
    class="flex-container mouldheader_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="batchPass"
          >
            {{ $t('mould.batchApproval') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="mouldflow.listFlowPage"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import mouldheaderEdit from 'modb@/mould/views/mouldheader/edit'
import mouldScrap from 'modb@/mould/views/mouldheader/mouldScrap'
import mouldChange from 'modb@/mould/views/mouldheader/mouldChange'
import { mouldflow } from 'modb@/mould/api'

export default {
  name: 'MouldflowList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      mouldflow: mouldflow,
      name: 'mouldflowList',
      tableName: 'mouldheaderTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {},
      filterParams: {},
      tableHeader: [],

      filterConfig: [
        { prop: 'mouldFlowCode', label: () => this.$t('mould.mouldFlowCode') }, // 单据编号
        { prop: 'mouldCode', label: () => this.$t('mould.mouldCode') }, // 模具编码
        { prop: 'itemNumber', label: () => this.$t('mould.itemNumber') }, // 物料编码
        { prop: 'supplierCode', label: () => this.$t('mould.supplierCode') }, // 供应商编码
        {
          prop: 'mouldStatusCode',
          label: () => this.$t('mould.mouldStatus'), // '管理状态'
          type: 'dict',
          code: 'MOULD_STATUS'
        },
        {
          prop: 'flowBusinessType',
          label: () => this.$t('mould.mouldFlowType'), // '单据类型'
          type: 'dict',
          code: 'MOULD_FLOW_TYPE'
        },
        {
          prop: 'approveStatus',
          label: () => this.$t('purchaseDemand.auditStatus'), // '审批状态'
          type: 'dict',
          code: 'MOULD_APPROVE_STATUS'
        },
        { prop: 'createdBy', label: () => this.$t('mould.createdBy') }// 创建人
      ],
      queryParam: {}
    }
  },
  created () {
    const _this = this
    this.tableHeader = [
      {
        prop: 'flowBusinessType',
        label: this.$t('bidMod.billType'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_FLOW_TYPE'
      },
      {
        prop: 'approveStatus',
        label: this.$t('purchaseDemand.auditStatus'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_APPROVE_STATUS'
      },
      {
        prop: 'mouldFlowCode',
        label: this.$t('vendorMod.relegation.receiptNum'),
        width: 100,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          _this.editHandle(row, 'view')
        }
      },
      {
        prop: 'mouldCode',
        label: this.$t('mould.mouldCode'),
        width: 100
      },
      {
        prop: 'mouldName',
        label: this.$t('mould.mouldName'),
        width: 100
      },
      {
        prop: 'itemNumber',
        label: this.$t('supplierCapacityReport.materialCode'),
        width: 100
      },
      {
        prop: 'itemDescZhs',
        label: this.$t('supplierCapacityReport.materialName'),
        width: 100
      },
      {
        prop: 'supplierCode',
        label: this.$t('supplierCapacityReport.vendorCode'),
        width: 100
      },
      {
        prop: 'mouldStatusCode',
        label: this.$t('mould.mouldStatus'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_STATUS'
      },
      {
        prop: 'createdBy',
        label: this.$t('supplierCapacityReport.createdBy'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('elementDefinition.creationDate'),
        width: 100
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: row => this.editHandle(row),
            // code: "pr:requirementApply:edit",
            show: row => row.approveStatus === 'UNDER_SUBMIT',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.deleteHandle(row),
            // code: "pr:requirementApply:edit",
            show: row => row.approveStatus === 'UNDER_SUBMIT',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: (row) => this.editHandle(row),
            show: (row) => row.approveStatus === 'SUBMITTED',
            formattor: () => {
              return this.$t('vendorMod.doApproval')
            }
          }
        ],
        selectList: []
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    batchPass () {
      if (this.currentRows.length === 0) {
        this.$message.error(this.$t('mould.selectDetailedRows'))
        return
      }
      this.$confirm(this.$t('mould.isSureApprove'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let vailStr = ''
        const idList = []
        for (let i = 0; i < this.currentRows.length; i++) {
          const item = this.currentRows[i]
          if (item.flowBusinessType !== 'CREATE') {
            vailStr += this.$t('mould.receiptNumber') + item.mouldFlowCode + this.$t('mould.nonNewProcess')
          } else if (item.flowBusinessType === 'CREATE' && item.approveStatus !== 'SUBMITTED') {
            vailStr += this.$t('mould.receiptNumber') + item.mouldFlowCode + this.$t('mould.statusNotSubmitted')
          }
          idList.push(item.mouldHeaderId)
        }
        if (vailStr !== '') {
          this.$message.error(vailStr)
          return
        }
        mouldflow.batchPassMould(idList).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      })
    },
    editHandle (row, type) {
      console.log(row)
      if (row.flowBusinessType === 'CREATE') { // 新建
        this.mode = type || 'edit'
        const tab = {
          component: mouldheaderEdit,
          params: {
            row,
            flag: this.mode,
            readOnly: true
          },
          title: this.$t('mould.editMouldLedger'),
          name: 'mouldheaderEdit' + row.mouldHeaderId
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'UPDATE') { // 变更
        this.mode = 'update'
        const tab = {
          component: mouldheaderEdit,
          params: {
            row,
            flag: this.mode,
            readOnly: !!type
          },
          title: this.$t('mould.moldLedgerChange'),
          name: 'mouldheaderEdit' + row.mouldHeaderId
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'SCRAP') { // 报废
        this.mode = type || 'scrap'
        const tab = {
          component: mouldScrap,
          params: {
            flag: this.mode,
            mouldFlowLogId: row.mouldFlowLogId
          },
          title: this.$t('mould.moldScrapped'),
          name: 'mouldScrapEdit'
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'CHANGE') { // 模具转移
        this.mode = type || 'change'
        const tab = {
          component: mouldChange,
          params: {
            flag: this.mode,
            mouldFlowLogId: row.mouldFlowLogId
          },
          title: this.$t('mould.transferMold'),
          name: 'mouldChangeEdit'
        }
        this.$emit('tab-add', tab)
      }
    },
    // 根据字典展示值,特殊处理
    getDictLabelByValue (dataList, value) {
      if (value.indexOf(',') !== -1) {
        var values = value.split(',')
        var result = ''
        for (const val of values) {
          for (const item of dataList) {
            if (item.value === val) {
              result += item.label + ','
            }
          }
        }
        return result.substring(0, result.lastIndexOf(','))
      } else {
        for (const item of dataList) {
          if (item.value === value) {
            return item.label
          }
        }
      }
      return value
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          mouldflow.deleteFlow(row.mouldFlowLogId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {
        })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    // TODO 没有这个方法, 报错，补充一个空
    syncFilterParams () {}
  }
}
</script>
