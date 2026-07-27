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
        url="/api-pef/pj/scoreItemsOrderCheck/listPage"
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
        { prop: 'projectName', label: '评分项目名称' },
        {
          prop: 'organizationName',
          label: '公司'
        },
        {
          prop: 'companyName',
          label: '供应商名称'
        },
        {
          prop: 'createdBy',
          label: '创建人',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'status',
          label: '复核状态',
          type: 'dict', // 字典类型
          code: 'ORDER_CHECK_STATUS' // 字典code
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
  created () {
    let _this = this
    this.tableHeader = [
      { 
        prop: 'projectName', 
        label: '评分项目名称', 
        width: 100 
      },
      {
        prop: 'organizationName',
        label: '公司',
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
        prop: 'status',
        label: '复核状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'ORDER_CHECK_STATUS' // 字典code
      },
      {
        prop: 'perStartMonth',
        label: '绩效开始月份',
        width: 100
      },
      {
        prop: 'perEndMonth',
        label: '绩效结束月份',
        width: 100
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        width: 150
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
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.fuhe('edit',row)
            }.bind(this),
            formattor (val) {
              return '复核'
            },
            show: function (row) {
              if (
                row.status === 'TO_BE_SCORE' ||
                row.status === 'TO_BE_REVIEWED'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.fuhe('view',row)
            }.bind(this),
            formattor (val) {
              return '查看'
            },
            show: function (row) {
              if (
                row.status === 'CALCULATED_SCORE'
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
            tabName: 'fuhe' + row.orderCheckId
          },
          title: row.projectName,
          name: 'fuhe' + row.orderCheckId
        }
      this.$emit('tab-add', tab)
    },
    getQuerydata (v) {
      this.queryParam = JSON.parse(JSON.stringify(v || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.createDateStart = creationDate[0]
        this.queryParam.createDateEnd = creationDate[1]
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
