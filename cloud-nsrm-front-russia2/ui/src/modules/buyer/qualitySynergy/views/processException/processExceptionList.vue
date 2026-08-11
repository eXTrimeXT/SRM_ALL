<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" @synchronous-value="syncFilterParams" />

      <MainHeader v-if="userType === 'BUYER'" :l-span="22" :r-span="2">
        <template slot="left">
          <el-button type="primary" @click="addOne">
            {{ $t('common.add') }}
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
        url="/api-pef/quaProcessException/listPage"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import processExceptionDetail from './processExceptionDetail'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch } from '@/utils/mixins'
import { inspectionStandard } from 'modb@/qualitySynergy/api'

export default {
  name: 'ProcessExceptionList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    processExceptionDetail,
    ExportExcel
  },
  mixins: [tabTodoWatch],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preList: [
        // 单据编码
        { prop: 'billCode', label: this.$t('dataConfMod.sequenceCode') },
        // 需要8D报告
        {
          prop: 'report8D',
          label: this.$t('qualitySynergy.need8DReport'),
          type: 'dict',
          code: 'INS_PROCESS_REPORT_8D'

        },
        // 物料编码
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 发布时间
        {
          prop: 'creationDate',
          label: this.$t('qualitySynergy.releaseDate'),
          type: 'date'
        }
      ],
      gridId: 'list',
      curRole: this.$store.getters.userType,
      name: 'processExceptionTable',
      tableName: 'processExceptionList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      queryParam: {},
      isModify: false,
      globalNickname: null
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    },
    preArr () {
      if (this.userType === 'BUYER') {
        return [
          ...this.preList,
          // 供应商
          {
            prop: 'vendorName',
            label: () => this.$t('common.vendor'),
            type: 'quicksearch',
            showKey: 'companyName',
            name: 'scc_sup_company_info_display_buyer'
          },
          {
            // 单据状态
            prop: 'processExceptionStatus',
            label: () => this.$t('qualitySynergy.paymentPlanStatus'),
            width: 180,
            type: 'dict',
            code: 'PERF_PROCESS_EXCEPTION_STATUS'
          }
        ]
      } else {
        return this.preList
      }
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
          this.$route.params.funName === 'processException'
        ) {
          let billCode = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            billCode,
            requirementHeadNum: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    let tableHeader = [
      {
        prop: 'billCode',
        label: () => this.$t('dataConfMod.sequenceCode'), // 单据编码
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'orgName',
        label: () => this.$t('dataConfMod.orgId'), // 业务实体
        minWidth: 150
      },
      {
        prop: 'organizationName',
        label: () => this.$t('qualitySynergy.organizationName'), // 库存组织
        minWidth: 130
      },
      {
        prop: 'processExceptionStatus',
        label: () => this.$t('qualitySynergy.paymentPlanStatus'),
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_PROCESS_EXCEPTION_STATUS' // 字典code
      },
      {
        prop: 'orderCode',
        label: () => this.$t('qualitySynergy.workOrder'), // 工单
        minWidth: 120
      },
      {
        prop: 'productionCode',
        label: this.$t('qualitySynergy.productionCode'), // 产品代码
        minWidth: 120
      },
      {
        prop: 'productionDesc',
        label: () => this.$t('qualitySynergy.productionDesc'), // 产品描述
        minWidth: 120
      },
      {
        prop: 'materialCode',
        label: () => this.$t('common.materialCode'), // 物料编码
        minWidth: 120
      },
      {
        prop: 'materialName',
        label: () => this.$t('common.materialName'), // 物料名称
        minWidth: 120
      },
      {
        prop: 'deliveryNumber',
        label: () => this.$t('qualitySynergy.deliveryNumber'), // 送货单号
        minWidth: 120
      },
      {
        prop: 'batchCode',
        label: () => this.$t('qualitySynergy.batchNum'), // 批号
        minWidth: 100
      },
      {
        prop: 'report8D',
        label: () => this.$t('qualitySynergy.ifCreate8DReport'),
        minWidth: 100,
        dataType: 'dict',
        code: 'INS_PROCESS_REPORT_8D'
      }, // 是否创建8D报告
      {
        prop: 'problemStatus',
        label: () => this.$t('qualitySynergy.problemStatus'),
        minWidth: 100
      }, // 问题状态
      {
        prop: 'handleResult',
        label: () => this.$t('qualitySynergy.handleResult'),
        minWidth: 100
      }, // 处理结果
      {
        prop: 'unqualifiedDesc',
        label: () => this.$t('qualitySynergy.ngDescribe'),
        minWidth: 100
      }, /// 不良描述
      {
        prop: 'processAgent',
        label: () => this.$t('dataConfMod.principal'),
        width: 100
      }, // 负责人
      {
        prop: 'outsourcingFactory',
        label: () => this.$t('qualitySynergy.outsourcingFactory'),
        minWidth: 100
      }, // 外协工厂
      { prop: 'vendorName', label: () => this.$t('common.vendor'), width: 100 }, // 供应商
      { prop: 'processComments', label: () => this.$t('common.remark'), width: 100 }, // 备注
      {
        prop: 'investmentTotal',
        label: () => this.$t('qualitySynergy.investmentTotal'),
        minWidth: 100
      }, // 投入数量
      {
        prop: 'unqualifiedTotal',
        label: () => this.$t('qualitySynergy.unqualifiedTotal1'),
        minWidth: 100
      }, // 不良数量
      { prop: 'buName', label: () => this.$t('qualitySynergy.buName'), width: 120 }, // 分属事业部
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('common.creator'),
        minWidth: 100
      }, // 创建人
      {
        prop: 'creationDate',
        label: () => this.$t('qualitySynergy.creationDate'),
        minWidth: 100,
        dataType: 'dateTime'
      } // 创建日期
    ]
    if (this.userType === 'BUYER') {
      let obj1 = {
        prop: 'vendorName',
        label: () => this.$t('common.vendor'), // 供应商
        minWidth: 120
      }
      let obj2 = {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            // 编辑
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            show: row => row.processExceptionStatus !== 'PUBLISHED',
            formattor (val, row) {
              return _this.$t('common.edit')
            }
          },
          {
            // 删除
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            show: row => row.processExceptionStatus !== 'PUBLISHED',
            formattor (val) {
              return _this.$t('common.delete')
            }
          }
        ]
      }

      tableHeader.splice(9, 0, obj1)
      tableHeader.push(obj2)
    }
    this.tableHeader = tableHeader
    // this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    getFooter (data) {
      this.getFooterNum = data.value
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    exportList () {
      let params = {}
      if (this.getFooterNum && this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: this.getFooterSizeNum },
        )
      } else if (this.getFooterNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: 15 },
        )
      } else if (this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: 1 },
          { pageSize: this.getFooterSizeNum },
        )
      } else {
        params = Object.assign({}, this.queryParam, { pageNum: 1 }, { pageSize: 15 })
      }
    },
    addOne () {
      this.$emit('tab-add', {
        component: processExceptionDetail,
        params: {
          flag: 'add',
          tabName: 'processExceptionDetail'
        },
        title: () => this.$t('qualitySynergy.addProcessExc'), // 创建制程异常处理单
        name: 'processExceptionDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: processExceptionDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'processExceptionDetail' + row.billCode
        },
        title: row.billCode,
        name: 'processExceptionDetail' + row.billCode
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: processExceptionDetail,
        params: {
          flag: 'readOnly',
          row: row,
          tabName: 'processExceptionDetail' + row.billCode
        },
        title: row.billCode,
        name: 'processExceptionDetail' + row.billCode
      })
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          inspectionStandard.processExceptionDelete({ billCode: row.billCode })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => { })
    }
  }
}
</script>
