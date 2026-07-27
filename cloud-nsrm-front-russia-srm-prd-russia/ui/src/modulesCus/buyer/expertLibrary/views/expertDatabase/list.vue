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
            退出
          </AuthorityButton>
          <AuthorityButton code="expertDatabase:freeze" @click="handleFreeze">
            冻结
          </AuthorityButton>
          <AuthorityButton code="expertDatabase:unFreeze" @click="handleUnFreeze">
            解除冻结
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
            title="初始化导入"
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
          label: '申请编号'
        },
        {
          prop: 'applyTime',
          label: '申请日期',
          type: 'daterange'
        },
        {
          prop: 'applyBy',
          label: '姓名'
        },
        {
          prop: 'applyStatus',
          label: '单据状态',
          type: 'dict',
          code: 'EXT_SOU_EXPERT_APPLY_STATUS'
        },
        {
          prop: 'hasQuite',
          label: '是否退出',
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
        label: '是否退出',
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'quiteReason',
        label: '退出原因',
        minWidth: 150
      },
      {
        prop: 'expertUserCode',
        label: '工号',
        minWidth: 120
      },
      {
        prop: 'expertFullName',
        label: '姓名',
        minWidth: 120
      },
      {
        prop: 'highestDegree',
        label: '最高学历',
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_EDUCATION',
        minWidth: 120
      },
      {
        prop: 'studyDateTo',
        label: '毕业时间',
        minWidth: 120
      },
      {
        prop: 'sex',
        label: '性别',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_SEX'
      },
      {
        prop: 'buName',
        label: '板块',
        minWidth: 120
      },
      {
        prop: 'orgOuName',
        label: '所属公司',
        minWidth: 120
      },
      {
        prop: 'departmentName',
        label: '部门/科室',
        minWidth: 120
      },
      {
        prop: 'job',
        label: '职务',
        minWidth: 120
      },
      {
        prop: 'jobRank',
        label: '序列等级',
        minWidth: 120
      },
      {
        prop: 'jobStatus',
        label: '在职状态',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_JOB_STATUS'
      },
      {
        prop: 'phone',
        label: '手机号码',
        minWidth: 120
      },
      {
        prop: 'hireDate',
        label: '入厂时间',
        minWidth: 120
      },
      {
        prop: 'hireDate',
        label: '入厂年限',
        minWidth: 120,
        formattor: (value) => {
          //返回年限
          return value ? (new Date().getFullYear() - new Date(value).getFullYear()) : ''
        }
      },
      {
        prop: 'studyCollege',
        label: '毕业院校',
        minWidth: 120
      },
      {
        prop: 'major',
        label: '所学专业',
        minWidth: 120
      },
      {
        prop: 'applyLevel',
        label: '申报等级',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_LEVEL'
      },
      {
        prop: 'greenReason',
        label: '专家绿色通道',
        minWidth: 120
      },
      {
        prop: 'applyFromType',
        label: '数据来源',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_APPLY_FROM_TYPE'
      },
      {
        prop: 'applyTime',
        label: '申请日期',
        minWidth: 120
      },
      {
        prop: 'expertApplyNo',
        label: '申请单号',
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
        label: '是否冻结',
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'frozenStatus',
        label: '冻结状态',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_FROZEN_STATUS'
      },
      {
        prop: 'frozenReason',
        label: '冻结/解冻原因',
        minWidth: 150
      },
      {
        prop: 'frozenRejectReason',
        label: '拒绝说明',
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
            formattor: () => '冻结确认',
            code: 'expertDatabase:confirmFreeze',
            callback: row => {
              this.confirmFreeze('frozenExpertConfirm', '确定进行冻结确认？', row)
            }
          },
          // 待确认冻结
          {
            show: row => ['FROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            formattor: () => '拒绝冻结',
            code: 'expertDatabase:refuseFreeze',
            callback: row => {
              this.confirmFreeze('frozenExpertReject', '确定进行拒绝冻结？', row)
            }
          },
          // 待确认解冻
          {
            show: row => ['UNFROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            formattor: () => '解冻确认',
            code: 'expertDatabase:confirmUnFreeze',
            callback: row => {
              this.confirmFreeze('unfrozenExpertConfirm', '确定进行解冻确认？', row)
            }
          },
          // 待确认解冻
          {
            show: row => ['UNFROZEN_UN_CONFIRM'].includes(row.frozenStatus),
            formattor: () => '拒绝解冻',
            code: 'expertDatabase:refuseUnFreeze',
            callback: row => {
              this.confirmFreeze('unfrozenExpertReject', '确定进行拒绝解冻？', row)
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
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      this.dialogVisible = true
      this.type = 'quite'
      this.dialogTitle = '退出'
      this.$refs.discardDialog.resetFields()
    },

    handleFreeze () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      this.dialogVisible = true
      this.type = 'freeze'
      this.dialogTitle = '冻结'
      this.$refs.discardDialog.resetFields()
    },

    handleUnFreeze () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      this.dialogVisible = true
      this.type = 'unFreeze'
      this.dialogTitle = '解冻'
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
        this.dialogTitle = '拒绝冻结'
        this.$refs.discardDialog.resetFields()
      } else {
        this.dialogVisible = true
        this.type = 'unfrozenExpertReject'
        this.dialogTitle = '拒绝解冻'
        this.$refs.discardDialog.resetFields()
      }
    }
  }
}
</script>
