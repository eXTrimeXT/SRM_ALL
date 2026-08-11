<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="expertDatabase:exit" type="primary" @click="handleExit">
            <!-- 退出 -->
            {{ $t("cusEntry.supplement20250205.exit") }}
          </AuthorityButton>
          <AuthorityButton code="expertDatabase:freeze" @click="handleFreeze">
            <!-- 冻结 -->
            {{ $t("components.headers.freeze") }}
          </AuthorityButton>
          <AuthorityButton code="expertDatabase:unFreeze" @click="handleUnFreeze">
            <!-- 解除冻结 -->
            {{ $t("cusEntry.supplement20250205.unfreeze") }}
          </AuthorityButton>
          <!-- 自定义导出 -->
          <ExportExcel
            :page-url="tableViewUrl"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            code="expertDatabase:export"
            export-mode="front"
            type="default"
          />
          <!-- 初始化导入 -->
          <MImport
            ref="import"
            :title="$t('cusEntry.supplement20250205.initializeImport')"
            up-load-url="/api-sou/npm/sou-expert/importExcel"
            type="default"
            code="expertDatabase:import"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
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
        :adeptMeiQl="true"
      />
    </el-main>

    <!-- 原因 弹窗 -->
    <DiscardDialog
      ref="discardDialog"
      :visible.sync="dialogVisible"
      :title="dialogTitle"
      @confirm="dialogConfirm"
    />
  </el-container>
</template>

<script>
import { expDataHttp, extCommonType } from 'modcb@/expertLibrary/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'
import { transformMQL } from 'lib@/utils/util'
import DiscardDialog from './components/discardDialog'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'ExpertDatabaseList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    DiscardDialog,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      extraData: {
        fileModular: 'sou',
        fileFunction: 'expertDatabase',
        fileType: 'excel'
      },
      tableViewUrl: expDataHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      dialogTitle: '',
      dialogVisible: false,
      searchFormConfig: [
        {
          prop: 'expertApplyNo',
          // label: '申请编号'
          label: () => this.$t('purchaseDemand.requirementHeadNum')
        },
        {
          prop: 'applyTime',
          // label: '申请日期',
          label: () => this.$t('purchaseDemand.applyDate'),
          type: 'daterange'
        },
        {
          prop: 'applyBy',
          // label: '姓名'
          label: () => this.$t('vendorMod.nickname')
        },
        {
          prop: 'applyStatus',
          // label: '单据状态',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'EXT_SOU_EXPERT_APPLY_STATUS'
        },
        {
          prop: 'hasQuite',
          // label: '是否退出',
          label: () => this.$t('cusEntry.supplement20250205.confirmExit'),
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      dictCodes: {
        hasQuite: 'YES_OR_NO',
        highestDegree: 'EXT_SOU_EXPERT_EDUCATION',
        sex: 'EXT_SOU_EXPERT_SEX',
        jobStatus: 'EXT_SOU_EXPERT_JOB_STATUS',
        applyLevel: 'EXT_SOU_EXPERT_LEVEL',
        applyFromType: 'EXT_SOU_EXPERT_APPLY_FROM_TYPE',
        hasFrozen: 'YES_OR_NO',
        frozenStatus: 'EXT_SOU_EXPERT_FROZEN_STATUS'
      },
      type: null,
      actMap: new Map([
        [
          'quite',
          {
            action: 'quiteExpert',
            field: 'quiteReason'
          }
        ],
        [
          'freeze',
          {
            action: 'frozenExpert',
            field: 'frozenReason'
          }
        ],
        [
          'unFreeze',
          {
            action: 'unFrozenExpert',
            field: 'frozenReason'
          }
        ],
        [
          'frozenExpertReject',
          {
            action: 'frozenExpertReject',
            field: 'frozenRejectReason'
          }
        ],
        [
          'unfrozenExpertReject',
          {
            action: 'unfrozenExpertReject',
            field: 'frozenRejectReason'
          }
        ]
      ])
    }
  },

  computed: {
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {

      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'hasQuite',
        // label: '是否退出',
        label: this.$t('cusEntry.supplement20250205.confirmExit'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'quiteReason',
        // label: '退出原因',
        label: this.$t('cusEntry.supplement20250205.exitReason'),
        minWidth: 150
      },
      {
        prop: 'expertUserCode',
        // label: '工号',
        label: this.$t('dataConfMod.jobNum'),
        minWidth: 120
      },
      {
        prop: 'expertFullName',
        // label: '姓名',
        label: this.$t('vendorMod.nickname'),
        minWidth: 120
      },
      // {
      //   prop: 'highestDegree',
      //   label: '最高学历',
      //   dataType: 'dict',
      //   code: 'EXT_SOU_EXPERT_EDUCATION',
      //   minWidth: 120
      // },
      // {
      //   prop: 'studyDateTo',
      //   label: '毕业时间',
      //   minWidth: 120,
      //   dataType: 'dateTime'
      // },
      // {
      //   prop: 'sex',
      //   label: '性别',
      //   minWidth: 120,
      //   dataType: 'dict',
      //   code: 'EXT_SOU_EXPERT_SEX'
      // },
      // {
      //   prop: 'buName',
      //   label: '板块',
      //   minWidth: 120
      // },
      {
        prop: 'orgOuName',
        // label: '所属公司',
        label: () => this.$t('cusEntry.supplement20250121.affiliatedCompany'),
        minWidth: 120
      },
      {
        prop: 'departmentName',
        // label: '部门/科室',
        label: () => this.$t('cusEntry.supplement20250205.department'),
        minWidth: 120
      },
      {
        prop: 'job',
        // label: '岗位',
        label: () => this.$t('components.orgPositionSel.position'),
        minWidth: 120
      },
      // {
      //   prop: 'jobStatus',
      //   label: '在职状态',
      //   minWidth: 120,
      //   dataType: 'dict',
      //   code: 'EXT_SOU_EXPERT_JOB_STATUS'
      // },
      {
        prop: 'phone',
        // label: '手机号码',
        label: () => this.$t('vendorMod.mobilePhone'),
        minWidth: 120
      },
      {
        prop: 'jobRank',
        // label: '邮箱',
        label: () => this.$t('common.email'),
        minWidth: 120
      },
      // {
      //   prop: 'hireDate',
      //   label: '入厂时间',
      //   minWidth: 120,
      //   dataType: 'dateTime'
      // },
      // {
      //   prop: 'hireDate',
      //   label: '入厂年限',
      //   minWidth: 120,
      //   formattor: (value) => {
      //     //返回年限
      //     return value ? (new Date().getFullYear() - new Date(value).getFullYear()) : ''
      //   }
      // },
      // {
      //   prop: 'studyCollege',
      //   label: '毕业院校',
      //   minWidth: 120
      // },
      // {
      //   prop: 'major',
      //   label: '所学专业',
      //   minWidth: 120
      // },
      {
        prop: 'applyLevel',
        // label: '申报等级',
        label: () => this.$t('cusEntry.supplement20250205.applicationLevel'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_LEVEL'
      },
      {
        prop: 'greenReason',
        // label: '专家绿色通道',
        label: () => this.$t('cusEntry.supplement20250205.expertGreenChannel'),
        minWidth: 120
      },
      {
        prop: 'applyFromType',
        // label: '数据来源',
        label:  () => this.$t('vendorMod.dataSources'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_APPLY_FROM_TYPE'
      },
      {
        prop: 'applyTime',
        // label: '申请日期',
        label: () => this.$t('purchaseDemand.applyDate'),
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'expertApplyNo',
        // label: '申请单号',
        label: () => this.$t('contractMod.applicationOrderNum'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          if (!row.expertApplyId) return
          this.$router.push({
            name: 'expertApply',
            params: {
              from: 'expertDatabase',
              row: {
                expertApplyNo: row.expertApplyNo,
                expertApplyId: row.expertApplyId
              }
            }
          })
        }
      },
      {
        prop: 'hasFrozen',
        // label: '是否冻结',
        label: () => this.$t('cusEntry.supplement20250205.confirmFreeze'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'frozenStatus',
        // label: '冻结状态',
        label: () => this.$t('cusEntry.supplement20250205.freezeStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_FROZEN_STATUS'
      },
      {
        prop: 'frozenReason',
        // label: '冻结/解冻原因',
        label: () => this.$t('cusEntry.supplement20250205.freezeUnfreezeReason'),
        minWidth: 150
      },
      {
        prop: 'frozenRejectReason',
        // label: '拒绝说明',
        label: () => this.$t('cusEntry.supplement20250205.rejectionExplanation'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 待确认冻结
          {
            show: row => ['FROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            // formattor: () => '冻结确认',
            formattor: () => this.$t('cusEntry.supplement20250205.freezeConfirmation'),
            code: 'expertDatabase:confirmFreeze',
            callback: row => {
              // this.confirmFreeze('frozenExpertConfirm', '确定进行冻结确认？', row)
              this.confirmFreeze('frozenExpertConfirm', this.$t('cusEntry.supplement20250205.confirmFreezeAction'), row)
            }
          },
          // 待确认冻结
          {
            show: row => ['FROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            // formattor: () => '拒绝冻结',
            formattor: () => this.$t('cusEntry.supplement20250205.rejectFreeze'),
            code: 'expertDatabase:refuseFreeze',
            callback: row => {
              // this.confirmFreeze('frozenExpertReject', '确定进行拒绝冻结？', row)
              this.confirmFreeze('frozenExpertReject', this.$t('cusEntry.supplement20250205.confirmRejectFreeze'), row)
            }
          },
          // 待确认解冻
          {
            show: row => ['UNFROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            // formattor: () => '解冻确认',
            formattor: () => this.$t('cusEntry.supplement20250205.unfreezeConfirmation'),
            code: 'expertDatabase:confirmUnFreeze',
            callback: row => {
              // this.confirmFreeze('unfrozenExpertConfirm', '确定进行解冻确认？', row)
              this.confirmFreeze('unfrozenExpertConfirm', this.$t('cusEntry.supplement20250205.confirmUnfreezeAction'), row)
            }
          },
          // 待确认解冻
          {
            show: row => ['UNFROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            // formattor: () => '拒绝解冻',
            formattor: () => this.$t('cusEntry.supplement20250205.rejectUnfreeze'),
            code: 'expertDatabase:refuseUnFreeze',
            callback: row => {
              // this.confirmFreeze('unfrozenExpertReject', '确定进行拒绝解冻？', row)
              this.confirmFreeze('unfrozenExpertReject', this.$t('cusEntry.supplement20250205.confirmRejectUnfreeze'), row)
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
    handleSuccess () {
      this.getQueryData()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sou/npm/sou-expert/downloadExcel'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      if (params.applyTime && params.applyTime.length) {
        const [applyTimeFrom, applyTimeTo] = params.applyTime
        params.applyTimeFrom = applyTimeFrom
        params.applyTimeTo = applyTimeTo
        delete params.applyTime
      }
      console.log('params', params)
      this.queryParam = transformMQL.save(extCommonType, {
        ...params
      }, 'queryExperts')
      // this.queryParam = transformMQL.listPageData({
      //   type: extCommonType,
      //   action: 'queryExperts',
      //   params
      // })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    async dialogConfirm (val) {
      const { action, field } = this.actMap.get(this.type)
      let params = this.selectedRows.map(item => ({
        expertId: item.expertId,
        [field]: val
      }))
      let transformParams = transformMQL.save(extCommonType, params, action)
      const response = await expDataHttp[action](transformParams)
      if (response) {
        this.dialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },

    async handleExit () {
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      this.dialogVisible = true
      this.type = 'quite'
      // this.dialogTitle = '退出'
      this.dialogTitle = this.$t('cusEntry.supplement20250205.exit')
      this.$refs.discardDialog.resetFields()
    },

    handleFreeze () {
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      this.dialogVisible = true
      this.type = 'freeze'
      // this.dialogTitle = '冻结'
      this.dialogTitle = this.$t('components.headers.freeze')
      this.$refs.discardDialog.resetFields()
    },

    handleUnFreeze () {
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      this.dialogVisible = true
      this.type = 'unFreeze'
      // this.dialogTitle = '解冻'
      this.dialogTitle = this.$t('cusEntry.supplement20250205.unfreeze2')
      this.$refs.discardDialog.resetFields()
    },

    async confirmFreeze (type, message, row) {
      this.selectedRows = [row]
      const confirmResult = await this.$confirm(message, {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      if (['frozenExpertConfirm', 'unfrozenExpertConfirm'].includes(type)) {
        let transformParams = transformMQL.save('ExtSouExpertForBuyer', [{ expertId: row.expertId }], type)
        await expDataHttp[type](transformParams)
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      } else if (type === 'frozenExpertReject') {
        this.dialogVisible = true
        this.type = 'frozenExpertReject'
        // this.dialogTitle = '拒绝冻结'
        this.dialogTitle = this.$t('cusEntry.supplement20250205.rejectFreeze')
        this.$refs.discardDialog.resetFields()
      } else {
        this.dialogVisible = true
        this.type = 'unfrozenExpertReject'
        // this.dialogTitle = '拒绝解冻'
        this.dialogTitle = this.$t('cusEntry.supplement20250205.rejectUnfreeze')
        this.$refs.discardDialog.resetFields()
      }
    }
  }
}
</script>
