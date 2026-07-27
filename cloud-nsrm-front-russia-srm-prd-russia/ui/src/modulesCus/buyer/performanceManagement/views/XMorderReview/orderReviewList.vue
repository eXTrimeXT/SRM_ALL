<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <!-- <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t("common.add")
            }}
          </el-button> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/pj/projectScoreItems/listPageForCheck"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import performanceModelDetail from './orderReviewDetail'
import { getDictItemList } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'PerformanceModelList',
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
      name: 'performanceModelList',
      tableName: 'performanceModelList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        { prop: 'contractName', label: '合同名称' },
        {
          prop: 'bidCode',
          label: '招标编号'
        },
        {
          prop: 'performanceCode',
          label: '履约阶段',
          type: 'dict',
          code: 'MILESTONE_SCHEDULE'
        },
        {
          prop: 'projectName',
          label: '评分项目名称'
        },
        {
          prop: 'checkStatus',
          label: '复核状态',
          type: 'dict', // 字典类型
          code: 'PROJECT_SCORE_ITEM_CHECK_STATUS' // 字典code
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationTime'), // 创建时间
          type: 'daterange'
        }
      ],
      queryParam: {},
      statusList: [],
      pubRangeList: [],
      projectTypeList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'XMorderReview'
        ) {
          let row = {
            projectScoreItemsId: this.$route.params.formId,
            projectName: this.$route.params.formNo
          }
          if (this.$route.params.taskIndex === 1) {
            this.fuhe('edit', row)
          } else {
            this.fuhe('view', row)
          }
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'projectName',
        label: '评分项目名称',
        width: 100
      },
      {
        prop: 'contractNo',
        label: '合同单号',
        width: 100
      },
      {
        prop: 'contractName',
        label: '合同名称',
        width: 100
      },
      {
        prop: 'companyCode',
        label: '供应商编码',
        width: 100
      },
      {
        prop: 'companyName',
        label: '供应商名称',
        width: 100
      },
      {
        prop: 'performanceCode',
        label: '履约阶段',
        width: 100,
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE'
      },
      {
        prop: 'checkStatus',
        label: '复核状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PROJECT_SCORE_ITEM_CHECK_STATUS' // 字典code
      },
      {
        prop: 'ouOrganizationName',
        label: '公司',
        width: 100
      },
      {
        prop: 'bidCode',
        label: '招标编号',
        width: 100
      },
      {
        prop: 'bidEndDate',
        label: '招标结束时间',
        width: 100
      },
      {
        prop: 'bidManager',
        label: '招标负责人',
        width: 100
      },
      {
        prop: 'bidManagerFullPath',
        label: '招标负责人部门',
        width: 100
      },
      {
        prop: 'contractManager',
        label: '合同经办人',
        width: 100
      },
      {
        prop: 'contractManagerFullPath',
        label: '合同经办人部门',
        width: 100
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'extCancelStatus',
        label: () => this.$t('cusEntry.common.extCancelStatus'),
        width: 120,
        formattor: val => {
          return val == '1' ? '是' : '否'
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.fuhe('edit', row)
            }.bind(this),
            formattor (val) {
              return '复核'
            },
            show: function (row) {
              if (
                row.checkStatus === 'DRAFT' ||
                row.checkStatus === 'WITHOUT_CHECK'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.fuhe('view', row)
            }.bind(this),
            formattor (val) {
              return '查看'
            },
            show: function (row) {
              if (
                row.checkStatus === 'CALCULATED_SCORE'
              ) {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.$refs[this.gridId].query()
    })
  },
  methods: {
    fuhe (type, row) {
      const tab = {
        component: performanceModelDetail,
        params: {
          flag: type,
          row,
          tabName: 'fuhe' + row.projectScoreItemsId
        },
        title: row.projectName,
        name: 'fuhe' + row.projectScoreItemsId
      }
      this.$emit('tab-add', tab)
    },
    getQuerydata (v) {
      this.queryParam = JSON.parse(JSON.stringify(v || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.startDate = creationDate[0]
        this.queryParam.endDate = creationDate[1]
      }
      delete this.queryParam.creationDate
      // this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
