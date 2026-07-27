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
          label: () => '业务实体',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'surveyTitle',
          label: () => '问卷标题'
        },
        {
          prop: 'resultFlag',
          label: () => '反馈状态',
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
        label: () => '业务实体',
        minWidth: 150
      },
      {
        prop: 'surveyNum',
        label: '问卷编号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab(row, 'view')
      },
      {
        prop: 'surveyTitle',
        label: () => '问卷标题',
        minWidth: 150
      },
      {
        prop: 'publishDate',
        label: () => '发布时间',
        minWidth: 150,
        formattor: val =>
          val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'endDate',
        label: () => '反馈截止时间',
        minWidth: 150,
        formattor: val =>
          val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'resultFlag',
        label: () => '反馈状态',
        minWidth: 150,
        formattor: val => this.$getDictLabel('RESULT_FLAG', val)
      },
      {
        prop: 'operation',
        label: '操作',
        width: 120,
        fixed: 'right',
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab(row, 'edit'),
            show: row => row.statusCode === 'PUBLISHED' && row.resultFlag === 'N',
            formattor: () => '填写问卷'
          },
          {
            callback: row => this.withDraw(row),
            show: row => row.statusCode === 'PUBLISHED' && row.resultFlag === 'Y',
            formattor: () => '撤回问卷'
          },
          {
            callback: row => this.editTab(row, 'view'),
            show: row => row.statusCode === 'COMPLETED',
            formattor: () => '查看详情'
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
      this.$confirm('撤回问卷？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-base/basesupplier/surveyresultsupplier/callBack',
          method: 'post',
          data: { vendorScopeId: row.vendorScopeId }
        }).then(() => {
          this.$message.success('撤回成功')
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
