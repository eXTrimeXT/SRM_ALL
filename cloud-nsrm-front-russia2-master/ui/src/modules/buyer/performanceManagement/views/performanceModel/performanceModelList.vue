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
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t("common.add")
            }}
          </el-button>
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
        url="/api-pef/template/listPefTemplateHeaderPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import performanceModelDetail from './performanceModelDetail'
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
        { prop: 'templateName', label: () => this.$t('perfMod.templateName2') },
        {
          prop: 'categoryId',
          label: () => this.$t('perfMod.category2'),
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'templateStatus',
          label: () => this.$t('perfMod.templateStatus2'),
          type: 'dict', // 字典类型
          code: 'PERF_TEMPLATE_STATUS' // 字典code
        },
        {
          prop: 'organizationId',
          label: () => this.$t('perfMod.fullPathId'),
          type: 'OUorganizationSelector',
          placeholder: () => this.$t('perfMod.selectOrganization')
        },
        { prop: 'version', label: () => this.$t('perfMod.version') }
      ],
      queryParam: {},
      statusList: [],
      pubRangeList: [],
      projectTypeList: []
    }
  },
  created () {
    this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'templateName',
        label: () => this.$t('perfMod.templateName2'),
        align: 'center',
        showType: 'button',
        btnStyle: 'text',
        width: 120,
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      { prop: 'version', label: () => this.$t('perfMod.version'), width: 100 },
      {
        prop: 'templateStatus',
        label: () => this.$t('perfMod.templateStatus2'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_TEMPLATE_STATUS' // 字典code
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('perfMod.createdFullName'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('perfMod.creationDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdatedFullName',
        label: () => this.$t('perfMod.lastUpdatedFullName'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('perfMod.lastUpdateDate'),
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
              this.enableOrDisOpt('INVALID', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.disable')
            },
            show: function (row) {
              if (row.templateStatus === 'VALID') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.enableOrDisOpt('VALID', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.enable')
            },
            show: function (row) {
              if (
                row.templateStatus === 'INVALID' ||
                row.templateStatus === 'DRAFT'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: function (row) {
              if (
                row.templateStatus === 'DRAFT' ||
                row.templateStatus === 'INVALID'
              ) {
                // 拟定和失效状态可删除
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: function (row) {
              if (
                row.templateStatus === 'DRAFT' ||
                row.templateStatus === 'INVALID'
              ) {
                // 拟定状态可删除
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.copyTemplate(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.duplicatingModel')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.$refs[this.gridId].query()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'PERF_TEMPLATE_STATUS' }, // 指标维度
        { dictCode: 'PERF_PERIOD' } // 绩效评价期间
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [PERF_TEMPLATE_STATUS, PERF_PERIOD] = res.data
        this.templateStatus = adaptDictData(
          PERF_TEMPLATE_STATUS.PERF_TEMPLATE_STATUS,
          'dict'
        )
        this.perfPeriod = adaptDictData(PERF_PERIOD.PERF_PERIOD, 'dict')
        this.preArr[2].options = this.templateStatus
        // this.preArr[3].options = this.perfPeriod
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: performanceModelDetail,
          params: {
            flag: 'add',
            tabName: 'performanceModelDetail'
          },
          title: this.$t('perfMod.newPerformanceModel'),
          name: 'performanceModelDetail'
        }
      } else {
        // 修改
        tab = {
          component: performanceModelDetail,
          params: {
            flag: type,
            orderId: row.templateHeadId,
            tabName: 'performanceModelDetail' + row.templateHeadId
          },
          title: row.templateName,
          name: 'performanceModelDetail' + row.templateHeadId
        }
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 禁用启用
    enableOrDisOpt (type, row) {
      let params = {}
      params.templateHeadId = row.templateHeadId
      if (type === 'VALID') {
        params.templateStatus = 'VALID' // 生效
      } else {
        params.templateStatus = 'INVALID' // 失效
      }
      performanceManagement.enableOrDisPefTemplateHeader(params).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    // 复制模型
    copyTemplate (row) {
      let templateHeadId = row.templateHeadId
      performanceManagement.copyTemplate({ templateHeadId }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    // 删除
    delRowData (row) {
      let templateHeadId = row.templateHeadId
      this.$confirm(this.$t('perfMod.sureDeleteData'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          performanceManagement.delPefTemplate({ templateHeadId }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
