<template>
  <el-container
    class="flex-container the_vendorEffect_wrapper"
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
            {{ $t("common.add") }}
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
        url="/api-pef/perf/indicatorsHeader/findIndicatorsHeaderPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import indicatorsDetail from './indicatorsDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'IndicatorsList',
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
      tableName: 'indicatorsList',
      defaultTableHeader: [],
      indicatorsStatus: [], // 指标状态 INDICATORS_STATUS
      indicatorsType: [], // 指标类型 INDICATORS_TYPE
      indicatorsDim: [], // 指标维度
      scoreIs: [], // 评分方式
      pageSize: 15,
      gridId: 'indicatorsList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        {
          prop: 'indicatorType',
          label: () => this.$t('perfMod.indicatorType'),
          type: 'dict', // 字典类型
          code: 'INDICATORS_TYPE' // 字典code
        },
        {
          prop: 'indicatorDimension',
          label: () => this.$t('perfMod.indicatorDimension'),
          type: 'dict', // 字典类型
          code: 'INDICATORS_DIM' // 字典code
        },
        {
          prop: 'enableFlag',
          label: () => this.$t('perfMod.enableFlag'),
          type: 'dict', // 字典类型
          code: 'INDICATORS_STATUS' // 字典code
        },
        {
          prop: 'indicatorName',
          label: () => this.$t('perfMod.indicatorName')// 指标名称
        }
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'indicatorType',
        label: () => this.$t('perfMod.indicatorType'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'INDICATORS_TYPE' // 字典code
      },
      {
        prop: 'indicatorDimension',
        label: () => this.$t('perfMod.indicatorDimension'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'INDICATORS_DIM' // 字典code
      },
      {
        prop: 'indicatorName',
        label: () => this.$t('perfMod.indicatorName'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'indicatorLogic',
        label: () => this.$t('perfMod.indicatorLogic'),
        minWidth: 150
      },
      {
        prop: 'evaluation',
        label: () => this.$t('perfMod.evaluation'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'SCORE_IS' // 字典code
      },
      // { prop: 'markLimit',
      //   label: ()=>this.$t('perfMod.markLimit'),
      //   width:100,
      // },
      {
        prop: 'enableFlag',
        label: () => this.$t('perfMod.enableFlag'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'INDICATORS_STATUS' // 字典code
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 140,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.handleRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('common.disable')
            },
            show: function (row) {
              if (row.enableFlag === 'Y') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.handleRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('common.enable')
            },
            show: function (row) {
              if (row.enableFlag === 'N') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.handleDraft(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('common.edit')
            },
            show: function (row) {
              if (row.enableFlag === 'DRAFT') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.handleDelete(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('components.userSelection.delete') // 删除
            },
            show: function (row) {
              if (row.enableFlag === 'DRAFT') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      // this.$refs[this.gridId].query()
      this.getQuerydata()
    })
  },
  methods: {
    // 删除
    handleDelete (row) {
      let submitData = {
        indicatorHeadId: row.indicatorHeadId
      }
      performanceManagement.delIndication(submitData).then(res => {
        if (res.code == '0') {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        } else {
          this.$message({
            message: res.message,
            type: 'error'
          })
        }
      })
    },
    // 编辑
    handleDraft (row) {
      let tab = {
        component: indicatorsDetail,
        ctrlHeight: true,
        params: {
          flag: 'edit',
          orderId: row.indicatorHeadId,
          tabName: 'indicatorsDetail'
        },
        title: row.indicatorName,
        name: 'indicatorsDetail'
      }
      this.$emit('tab-add', tab)
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: indicatorsDetail,
          ctrlHeight: true,
          params: {
            flag: 'add',
            tabName: 'indicatorsDetail'
          },
          title: this.$t('perfMod.newIndicators'),
          name: 'indicatorsDetail'
        }
      } else if (type === 'view') {
        // 修改
        tab = {
          component: indicatorsDetail,
          ctrlHeight: true,
          params: {
            flag: 'view',
            orderId: row.indicatorHeadId,
            tabName: 'indicatorsDetail' + row.indicatorHeadId
          },
          title: row.indicatorName,
          name: 'indicatorsDetail' + row.indicatorHeadId
        }
      } else {
        // 修改
        tab = {
          component: indicatorsDetail,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            orderId: row.indicatorHeadId,
            tabName: 'indicatorsDetail' + row.indicatorHeadId
          },
          title: row.indicatorName,
          name: 'indicatorsDetail' + row.indicatorHeadId
        }
      }
      this.$emit('tab-add', tab)
    },
    handleRowData (row) {
      let params = {}
      params.indicatorHeadId = row.indicatorHeadId
      let tips = ''
      if (row.enableFlag === 'Y') {
        // 启用状态
        params.enableFlag = 'N'
        tips = this.$t('perfMod.isDisabled')
      } else {
        // 启用状态
        params.enableFlag = 'Y'
        tips = this.$t('perfMod.isEnabeled')
      }
      this.$confirm(tips, {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          performanceManagement.enableOrDisabledIndication(params).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    exportOne () {},
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
