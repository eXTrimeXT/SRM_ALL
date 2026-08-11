<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="centralizedAgree:add" type="primary" @click="editTab('add',{})">
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="jc:contractAgree:exprotDetail"
            @click="exprotDetail"
          >
            {{ $t('cusEntry.common.exprotDetail') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="jc:contractAgree:exprotHeader"
            @click="exprotHeader"
          >
            {{ $t('cusEntry.common.exprotContractAgree') }}
          </AuthorityButton>
          <!-- 导入协议头 -->
          <MImport
            ref="import"
            :title="$t('cusEntry.supplement20250121.importProtocolHeader')"
            up-load-url="/api-sou/jcAgreement/importHtAgreementHead"
            type="default"
            code="centralizedAgree:importHead"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate(2)"
            @handleSuccess="handleSuccess"
          />
          <!-- 导入协议行 -->
          <MImport
            ref="import"
            :title="$t('cusEntry.supplement20250121.importProtocolLine')"
            up-load-url="/api-sou/jcAgreement/importHtAgreementLine"
            type="default"
            code="centralizedAgree:importLine"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate(3)"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        @afterQuery="aftereQuery"
      />
    </el-main>
  </el-container>
</template>

<script>
import { centralHttp } from 'modcb@/jcAgreement/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import ContractAgreeDetail from './edit'

export default {
  name: 'ContractAgreeList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      extraData: {
        fileModular: 'sou',
        fileFunction: 'contractAgree',
        fileType: 'excel'
      },
      tableViewUrl: centralHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'agreementCode',
          label: this.$t('cusEntry.supplement20250121.protocolCode'), //'协议编号
        },
        {
          prop: 'supName',
          label: this.$t('common.vendorName'), //'供应商名称
        },
        {
          prop: 'buyPersonId',
          label: this.$t('bidMod.quotePurchasor'), //'采购员'
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'agreementName',
          label: this.$t('cusEntry.supplement20250121.protocolName'), //'协议名称
        },
        {
          prop: 'companyId',
          label: this.$t('cusEntry.supplement20250121.companyMain'), //'公司主体'
          type: 'quicksearch',
          showKey: 'organizationName',
          propKey: 'organizationId',
          name: 'scc_base_organization_invoice'
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.operator'), //'操作人
        },
        {
          prop: 'effectiveDate',
          label: this.$t('cusEntry.supplement20250121.protocolEffectDate'), //'协议有效期'
          type: 'daterange'
        },
        {
          prop: 'agreementStatus',
          label: this.$t('cusEntry.reportManagement.agreementStatus'), //'协议状态'
          type: 'dict',
          code: 'AGREEMENT_STATUS'
        },
        {
          prop: 'creationDate',
          label: this.$t('common.operationTime'), //'操作时间'
          type: 'daterange'
        },
        {
          prop: 'buyOrgId',
          label: this.$t('cusEntry.supplement20250121.purchaseOrg'), //'采购组织'
          type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'), //'物料号'
          type: 'quicksearch',
          name: 'scc_base_material_item_contract',
          showKey: 'materialCode'
        }
      ],
      queryParam: {},
      dictCodes: {
        agreementStatus: 'AGREEMENT_STATUS'
      },
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'agreementCode',
        label: this.$t('cusEntry.supplement20250121.protocolCode'), //'协议编号
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'agreementName',
        label: this.$t('cusEntry.supplement20250121.protocolName'), //'协议名称
        minWidth: 150
      },
      {
        prop: 'changeVersion',
        label: this.$t('closeTask.version'), // '版本',
        minWidth: 100
      },
      {
        prop: 'companyName',
        label: this.$t('cusEntry.supplement20250121.companyMain'), //'公司主体'
        minWidth: 120
      },
      {
        prop: 'supName',
        label: this.$t('common.vendorName'), //'供应商名称
        minWidth: 120
      },
      {
        prop: 'effectiveStartDate',
        label: this.$t('dataConfMod.effectiveDateFrom'), // '有效开始日期',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'effectiveEndDate',
        label: this.$t('dataConfMod.withRetrospectiveEffect'), // '有效结束日期',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'creationDate',
        label: this.$t('common.operationTime'), // '操作时间',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'buyOrgNames',
        label: this.$t('cusEntry.supplement20250121.purchaseOrg'), //'采购组织'
        minWidth: 130
      },
      {
        prop: 'buyPersonName',
        label: this.$t('bidMod.quotePurchasor'), //'采购员'
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.operator'), //'操作人
        minWidth: 120
      },
      {
        prop: 'discardReason',
        label: this.$t('contractMod.reasonTermination'), //'终止原因'
        minWidth: 120
      },
      {
        prop: 'agreementStatus',
        label: this.$t('common.status'), //'状态',
        dataType: 'dict',
        code: 'AGREEMENT_STATUS',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 拟定可以编辑
          {
            show: row => ['DRAFT'].includes(row.agreementStatus),
            formattor: () => this.$t('common.edit'),
            code: 'contractAgree:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 待执行、执行中可以变更
          {
            show: row => ['EXECUTE', 'EXECUTING'].includes(row.agreementStatus),
            formattor: () => this.$t('common.change'), // '变更
            code: 'contractAgree:change',
            callback: row => {
              this.editTab('change', row)
            }
          },
          // 拟定 可以提交 提交之后变成待执行
          {
            show: row => ['DRAFT'].includes(row.agreementStatus),
            formattor: () => this.$t('common.submit'), // '提交
            code: 'contractAgree:submit',
            callback: row => {
              this.changeStatus('EXECUTE', row, this.$t('cusEntry.supplement20250121.submitTips'))
            }
          },
          // 拟定、待执行、执行中 可以终止
          {
            show: row => ['DRAFT', 'EXECUTE', 'EXECUTING'].includes(row.agreementStatus),
            formattor: () => this.$t('cusEntry.supplement20250121.stop'), // '终止
            code: 'contractAgree:stop',
            callback: row => {
              this.changeStatus('STOP', row, this.$t('cusEntry.supplement20250121.stopTips'))
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    // 导出头
    exprotHeader () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectExportLine'))
        return false
      }
      const queryParam = {
        agreementType: '合同协议', // 合同协议: 查询入参不能国际化
        agreementIds: this.selectedRows.map(item => item.agreementId)
      }
      downloadFileLinkByPost(
        '/api-sou/jcAgreement/exportJcHtHeadLineData',
        this.$t('cusEntry.inq.contractAgreeExportHeader'),
        queryParam
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 导出明细
    exprotDetail () {
      downloadFileLinkByPost(
        '/api-sou/jcAgreement/exportJcHtHeadLineData',
        this.$t('cusEntry.inq.contractAgreeExport'),
        this.queryParam
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      let transformParams = {}
      const { creationDate, effectiveDate, ...rest } = params
      if (creationDate && creationDate.length) {
        transformParams.operatorStartDate = creationDate[0]
        transformParams.operatorEndDate = creationDate[1]
      }
      if (effectiveDate && effectiveDate.length) {
        transformParams.effectiveStartDate = effectiveDate[0]
        transformParams.effectiveEndDate = effectiveDate[1]
      }
      this.queryParam = {
        ...rest,
        ...transformParams,
        agreementType: '合同协议' // 合同协议: 查询入参不能国际化
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    aftereQuery (data) {
      console.log('data', data)
      data.forEach(item => {
        if (item.sccSouJcAgreementOrgList && item.sccSouJcAgreementOrgList.length) {
          item.buyOrgNames = item.sccSouJcAgreementOrgList.map(innerItem => innerItem.buyOrgName).join(';')
        }
      })
    },

    handleSuccess () {
      this.getQueryData()
    },
    downloadTemplate (type) {
      // 1、协议头导入模板。2、协议行导入模板。3、编辑页协议行导入模板
      downloadFileLink(
        `/api-sou/jcAgreement/downloadTemplate?type=${type}`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: ContractAgreeDetail,
            params: {
              flag: type,
              row,
              tabName: 'contractAgree'
            },
            title: this.$t('cusEntry.inq.contractAgreeExportHeader'),
            name: 'contractAgree'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: ContractAgreeDetail,
            params: {
              flag: type,
              row,
              tabName: row.agreementCode
            },
            title: this.$t('cusEntry.inq.contractAgreeExportHeader') + (row.agreementCode || ''),
            name: row.agreementCode
          }
        ],
        // 查看
        [
          'view',
          {
            component: ContractAgreeDetail,
            params: {
              flag: type,
              row,
              tabName: row.agreementCode
            },
            title: this.$t('cusEntry.inq.contractAgreeExportHeader') + (row.agreementCode || ''),
            name: row.agreementCode
          }
        ],
        // 变更
        [
          'change',
          {
            component: ContractAgreeDetail,
            params: {
              flag: type,
              row,
              tabName: row.agreementCode
            },
            title: this.$t('cusEntry.inq.contractAgreeExportHeader') + (row.agreementCode || ''),
            name: row.agreementCode
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      this.selectedRows = val
    },

    /** 状态变更 */
    async changeStatus (status, row, message) {
      if (status === 'STOP') {
        this.$prompt('', this.$t('cusEntry.tipMessage.stopReason'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputType: 'textarea',
          inputValidator: (value) => {
            if (!value) {
              return this.$t('cusEntry.tipMessage.stopReasonMsg')
            }
            return true
          }
        }).then(async ({ value }) => {
          const response = await centralHttp.changeStatus({
            agreementId: row.agreementId,
            operationType: status,
            stopReason: value
          })
          if (response) {
            this.$message.success(this.$t('common.success'))
            this.getQueryData(this.queryParam)
          }
        }).catch(() => {})
      } else {
        const confirmResult = await this.$confirm(message, {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).catch(() => {})
        if (confirmResult !== 'confirm') return
        const response = await centralHttp.changeStatus({
          agreementId: row.agreementId,
          operationType: status
        })
        if (response) {
          this.$message.success(this.$t('common.success'))
          this.getQueryData(this.queryParam)
        }
      }
    },

    /** 废弃 */
    abandonRows () {

    }
  }
}
</script>
