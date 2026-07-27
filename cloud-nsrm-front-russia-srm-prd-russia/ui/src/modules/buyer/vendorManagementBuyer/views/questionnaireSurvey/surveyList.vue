<template>
  <el-container
    class="flex-container the_sampleConfirmedList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-base/base/surveyheader/listPage"
      >
        <template #totalCount="props">
          <span>{{ props.scope.row.backCount }}/{{ props.scope.row.totalCount }}</span>
        </template>
      </TableView>
    </el-main>

    <el-dialog
      :title="$t('vendorMod.relegation.reminder')"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <span>{{ $t('survey.list1') }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteSuryvey">{{ $t('survey.list2') }}</el-button>
        <el-button type="primary" @click="keepSurvey">{{ $t('survey.list3') }}</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import surveyResult from './surveyResult'
import surveyDetail from './surveyDetail'
import { parseTime } from '@/utils'

export default {
  name: 'SurveyList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  provide () {
    return { context: this }
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      gridId: 'list',
      tableData: [],
      tableHeader: [],
      queryParam: {},
      queryForm: [
        // 业务实体
        {
          prop: 'buId',
          label: () => '业务实体',
          type: 'OUorganizationSelector'
        },
        // 问卷标题
        {
          prop: 'surveyTitle',
          label: () => this.$t('dataConfMod.questionnaire')
        },
        // 问卷编号
        {
          prop: 'surveyNum',
          label: () => this.$t('dataConfMod.questionnaireNumber')
        },
        // 创建人
        {
          prop: 'createdBy',
          label: () => this.$t('dataConfMod.creator'),
          type: 'quicksearch',
          showKey: 'username',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 问卷状态
        {
          prop: 'statusCode',
          label: () => this.$t('dataConfMod.statusInventory'),
          type: 'dict',
          code: 'STATUS_CODE'
        }
      ],
      surveyResultIdList: [],
      currentRow: {},
      dialogVisible: false
    }
  },

  mounted () {
    this.tableHeader = [
      // 业务实体
      {
        prop: 'buName',
        label: () => '业务实体',
        width: 120
      },
      // 问卷标题
      {
        prop: 'surveyTitle',
        label: () => '问卷标题',
        width: 120
      },
      // 问卷编号
      {
        prop: 'surveyNum',
        label: () => '问卷编号',
        minWidth: '140',
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      // 发布时间
      {
        prop: 'publishDate',
        label: () => '发布时间',
        width: 120,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'endDate',
        label: () => '反馈截止时间',
        width: 120,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'statusCode',
        label: () => '问卷状态',
        width: 120,
        dataType: 'dict',
        code: 'STATUS_CODE'
      },
      {
        prop: 'totalCount',
        label: () => '反馈结果',
        width: 120,
        showType: 'slot',
        slot: 'totalCount'
      },
      {
        prop: 'createdBy',
        label: () => '创建人',
        width: 120
      },
      {
        prop: 'creationDate',
        label: () => '创建时间',
        width: 120
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.editTab('edit', row),
            show: row => row.statusCode === 'DRAFT',
            formattor: () => this.$t('common.edit')
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            show: row => row.statusCode === 'DRAFT',
            formattor: () => this.$t('common.delete')
          },
          // 查看
          {
            callback: row => this.editTab('view', row),
            show: row => row.statusCode !== 'DRAFT',
            formattor: () => this.$t('common.view')
          },
          // 查看结果
          {
            callback: row => this.editTab('toResult', row),
            show: row => row.statusCode !== 'DRAFT',
            formattor: () => '查看结果'
          },
          // 撤回问卷
          {
            callback: row => this.withdraw(row),
            show: row => ['PUBLISHED'].includes(row.statusCode) && row.createdBy === this.$store.getters.user.userInfo.username,
            formattor: () => '撤回问卷'
          },
          // 复制问卷
          {
            callback: row => this.copy(row),
            show: row => ['COMPLETED'].includes(row.statusCode) && row.createdBy === this.$store.getters.user.userInfo.username,
            formattor: () => '复制问卷'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    // 撤回问卷
    async withdraw (row) {
      this.currentRow = row
      let res = await this.$http({
        url: '/api-base/base/surveyheader/checkResultEmpty',
        method: 'post',
        data: { surveyId: row.surveyId }
      })
      if (res.data) {
        let { surveyResultIdList, exitFlag } = res.data
        this.surveyResultIdList = surveyResultIdList
        if (exitFlag) {
          this.dialogVisible = true
        } else {
          this.withdrawSurvey(surveyResultIdList, true)
        }
      }
    },

    // 删除答卷
    deleteSuryvey () {
      this.withdrawSurvey(this.surveyResultIdList, true).then(() => {
        this.dialogVisible = false
      })
    },

    // 保留答卷
    keepSurvey () {
      this.withdrawSurvey(this.surveyResultIdList, false).then(() => {
        this.dialogVisible = false
      })
    },

    async withdrawSurvey (surveyResultIdList = [], deleteFlag = true) {
      return new Promise(resolve => {
        this.$http({
          url: '/api-base/base/surveyheader/withdrawSurvey',
          method: 'post',
          data: {
            surveyResultIdList,
            deleteFlag,
            surveyId: this.currentRow.surveyId
          }
        }).then(res => {
          this.$message.success('撤回成功')
          this.$refs[this.gridId].query()
          resolve(res)
        })
      })
    },
    // 复制问卷
    copy (row) {
      this.$http({
        url: '/api-base/base/surveyheader/copyValue',
        method: 'post',
        data: {
          surveyId: row.surveyId
        }
      }).then(() => {
        this.$message.success('复制成功')
        this.$refs[this.gridId].query()
      })
    },

    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: surveyDetail,
          params: {
            flag: 'add',
            tabName: 'surveyDetail'
          },
          title: '问卷调查新增',
          name: 'surveyDetail'
        }
      }
      if (type === 'edit') {
        // 编辑
        tab = {
          component: surveyDetail,
          params: {
            flag: 'edit',
            tabName: 'surveyDetail' + row.surveyTitle,
            ...row
          },
          title: row.surveyTitle,
          name: 'surveyDetail' + row.surveyTitle
        }
      }
      if (type === 'view') {
        // 查看详情
        tab = {
          component: surveyDetail,
          params: {
            flag: 'view',
            tabName: 'surveyDetail' + row.surveyTitle,
            ...row
          },
          title: row.surveyTitle,
          name: 'surveyDetail' + row.surveyTitle
        }
      }
      // 查看结果
      if (type === 'toResult') {
        tab = {
          component: surveyResult,
          params: {
            tabName: 'surveyResult' + row.surveyId,
            row
          },
          title: '问卷调查反馈结果',
          name: 'surveyResult' + row.surveyId
        }
      }
      this.$emit('tab-add', tab)
    },

    deleteOne ({ surveyId }) {
      // 当前操作将永久删除此数据，确认删除此数据？
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-base/base/surveyheader/delete?id=' + surveyId,
          method: 'get'
        })
          .then((data) => {
            if (data) {
              this.$message.success(this.$t('common.successDelete'))
              this.$refs[this.gridId].query()
            }
          })
      })
    }
  }
}
</script>
