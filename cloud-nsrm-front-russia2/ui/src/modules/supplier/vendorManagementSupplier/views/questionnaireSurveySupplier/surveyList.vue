<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        url="/api-base/basesupplier/surveyheadersupplier/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import surveyAnswer from './surveyAnswer'
import { parseTime } from '@/utils'

export default {
  name: 'SurveyList',

  components: {
    TableView,
    FormWrapper
  },

  provide () {
    return { context: this }
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableData: [],
      resultFlagList: [],
      tableHeader: [],
      preFormObj: {},
      queryParam: {},
      gridId: 'list',
      queryForm: [
        {
          prop: 'buId',
          label: () => this.$t('components.organization.ORG'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'surveyTitle',
          label: () => this.$t('dataConfMod.questionnaire')
        },
        {
          prop: 'resultFlag',
          label: () => this.$t('dashboard.loopMode'),
          type: 'dict',
          code: 'RESULT_FLAG'
        }
      ]
    }
  },

  created () {
    this.tableHeader = [
      {
        prop: 'buName',
        label: () => this.$t('components.organization.ORG'),
        minWidth: 150
      },
      {
        prop: 'surveyNum',
        label: () => this.$t('dataConfMod.questionnaireNumber'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab(row, 'view')
      },
      {
        prop: 'surveyTitle',
        label: () => this.$t('dataConfMod.questionnaire'),
        minWidth: 150
      },
      {
        prop: 'publishDate',
        label: () => this.$t('components.notice.publishTime'),
        minWidth: 150,
        formattor: val =>
          val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'endDate',
        label: () => this.$t('dashboard.endDate'),
        minWidth: 150,
        formattor: val =>
          val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'resultFlag',
        label: () => this.$t('dashboard.loopMode'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('RESULT_FLAG', val)
      },
      {
        prop: 'operation',
        label: () => this.$t('components.headers.operation'),
        width: 120,
        fixed: 'right',
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab(row, 'edit'),
            show: row => row.statusCode === 'PUBLISHED' && row.resultFlag === 'N',
            formattor: () => this.$t('common.fillOutQuestionnaire')
          },
          {
            callback: row => this.withDraw(row),
            show: row => row.statusCode === 'PUBLISHED' && row.resultFlag === 'Y',
            formattor: () => this.$t('common.withdrawQuestionnaire')
          },
          {
            callback: row => this.editTab(row, 'view'),
            show: row => row.statusCode === 'COMPLETED',
            formattor: () => this.$t('orderMod.viewDetail')
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    withDraw (row) {
      if (!row.vendorScopeId) return
      // 撤回问卷？
      this.$confirm(this.$t('cusEntry.supplement20250211.withdrawQuestionnaire'), this.$t('components.approvalHead.tips.tip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-base/basesupplier/surveyresultsupplier/callBack',
          method: 'post',
          data: { vendorScopeId: row.vendorScopeId }
        }).then(() => {
          // 撤回成功
          this.$message.success(this.$t('cusEntry.tipMessage.recallSuccess'))
          this.$nextTick(() => this.getQuerydata())
        })
      })
    },

    dolayout () {
      this.$refs[this.gridId].doLayout()
    },

    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    editTab (row, type) {
      let tab = {}
      tab = {
        component: surveyAnswer,
        params: {
          flag: type,
          tabName: 'surveyAnswer' + row.surveyTitle,
          ...row
        },
        title: row.surveyTitle,
        name: 'surveyAnswer' + row.surveyTitle
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
