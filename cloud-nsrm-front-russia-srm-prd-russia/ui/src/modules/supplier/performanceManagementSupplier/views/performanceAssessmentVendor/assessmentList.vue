<template>
  <el-container
    class="flex-container the_vendorEffect_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader>

      </MainHeader>
      <!-- :current-change="handleCurrentChange" -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="checkChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/vendorAsses/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import assessmentDetail from './assessmentDetail'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { getAllPurCurrency } from '@/api/common'
import { adaptDictData } from '@/utils'
import { downloadFileLink } from 'lib@/utils/file'
import { perVendorApi } from 'mods@/performanceManagementSupplier/api'

export default {
  name: 'AssessmentList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        status: 'VENDOR_ASSES_STATUS',
        indicatorDimension: 'INDICATORS_DIM',
        sourceType: 'PER_CHECK_FORM_SOURCE'
      },
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      tableName: 'assessmentList',
      approveStatusList: [], // 审批状态
      indicatorsDim: [], // 指标维度
      pageSize: 15,
      gridId: 'assessmentList',
      selectList: [],
      tableHeader: [],
      tableData: [],
      statusList: [],
      currencyList: [],
      preArr: [
        { prop: 'assessmentNo', label: () => this.$t('perfMod.assessmentNo') },
        {
          prop: 'categoryName',
          label: () => this.$t('perfMod.smallClassMaterial'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'status',
          label: () => this.$t('perfMod.assessmentStatus'),
          type: 'dict', // 字典类型
          code: 'VENDOR_ASSES_STATUS' // 字典code
        },
        {
          prop: 'indicatorDimension',
          label: () => this.$t('perfMod.indicatorDimension'),
          type: 'dict', // 字典类型
          code: 'INDICATORS_DIM' // 字典code
        },
        {
          /* prop: "organizationId",
          label: () => this.$t("perfMod.fullPathId"),
          type: "selectTree",
          normalizer: this.normalizer,
          placeholder: "请选择组织" */
          prop: 'organizationId',
          label: () => this.$t('perfMod.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'dateList',
          label: () => this.$t('perfMod.assessmentDate2'),
          type: 'daterange'
        }
      ],
      preArrSell: [
        { prop: 'assessmentNo', label: () => this.$t('perfMod.assessmentNo') },
        {
          prop: 'categoryName',
          label: () => this.$t('perfMod.smallClassMaterial'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        // {
        //   prop: "vendorName",
        //   label: () => this.$t("perfMod.vendorName"),
        //   type: "quicksearch",
        //   showKey: "companyName",
        //   name: "scc_sup_company_info_display"
        // },
        {
          prop: 'status',
          label: () => this.$t('perfMod.assessmentStatus'),
          type: 'dict', // 字典类型
          code: 'VENDOR_ASSES_STATUS' // 字典code
        },
        {
          prop: 'indicatorDimension',
          label: () => this.$t('perfMod.indicatorDimension'),
          type: 'dict', // 字典类型
          code: 'INDICATORS_DIM' // 字典code
        },
        {
          /* prop: "organizationId",
          label: () => this.$t("perfMod.fullPathId"),
          type: "selectTree",
          normalizer: this.normalizer,
          placeholder: "请选择组织" */
          prop: 'organizationId',
          label: () => this.$t('perfMod.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'dateList',
          label: () => this.$t('perfMod.assessmentDate2'),
          type: 'daterange'
        }
      ],
      queryParam: {},
      currentRow: null,
      currentRows: null,
      filterParams: {},
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-pef/vendorAsses/importExcel'
      },
      preFormObj: {}
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (Object.keys(this.$route.params).length > 0) {
          if (
            this.$route.params.from === 'workCount' &&
            this.$route.params.funName === 'performanceAssessment'
          ) {
            // 供应商 工作台跳转
            this.queryParam.status = this.$route.params.status
            this.preFormObj = Object.assign({}, { status: this.$route.params.status })
          } else if (
            // 其他功能跳转
            this.$route.params.from === 'fromFun'
          ) {
            let vendorAssesId = Number(this.$route.params.formId)
            let formNo = this.$route.params.formNo // 流程标题
            let row = {
              ...this.$route.params,
              vendorAssesId,
              assessmentNo: formNo // tab 标题显示
            }
            this.editTab('doApproval', row)
          } else { // 供应商画像跳转
            this.editTab('view', this.$route.params.row)
          }
        }
      }
    }
  },
  created () {
    this.preArr = this.preArrSell
    this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'assessmentNo',
        label: () => this.$t('perfMod.assessmentNo'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      // 来源单据类型
      {
        prop: 'sourceType',
        label: '来源单据类型',
        width: '120',
        dataType: 'dict', // 数据类型为字典
        code: 'PER_CHECK_FORM_SOURCE' // 字典code
      },
      {
        prop: 'status',
        label: () => this.$t('perfMod.assessmentStatus'),
        width: '100',
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_ASSES_STATUS' // 字典code
      },
      {
        prop: 'assessmentDate',
        label: () => this.$t('perfMod.assessmentDate2'),
        width: '140'
      },
      {
        prop: 'respFullName',
        label: () => this.$t('perfMod.respFullName'),
        width: '120'
      },
      {
        prop: 'indicatorDimension',
        label: () => this.$t('perfMod.indicatorDimension'),
        width: '100',
        dataType: 'dict', // 数据类型为字典
        code: 'INDICATORS_DIM' // 字典code
      },
      {
        prop: 'indicatorName',
        width: 120,
        label: () => this.$t('perfMod.indicatorName')
      },
      {
        prop: 'organizationName',
        label: () => this.$t('perfMod.businessEntity'),
        minWidth: '150'
      },
      {
        prop: 'assessmentPenalty',
        label: () => this.$t('perfMod.assessmentPenalty'),
        width: '130',
        align: 'right'
      },
      {
        prop: 'actualAssessmentAmountY',
        label: () => this.$t('perfMod.actualAssessmentAmountY'),
        width: '160',
        align: 'right'
      },
      {
        prop: 'currencyCode',
        label: () => this.$t('perfMod.currencyCode'),
        width: '100',
        formattor (val) {
          return val ? _this.$getDictLabelByValue(_this.currencyList, val) : ''
        }
      },
      {
        prop: 'vendorName',
        label: () => this.$t('perfMod.vendorName'),
        width: '150'
      },
      {
        prop: 'categoryName',
        width: 120,
        label: () => this.$t('perfMod.categoryName')
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('feedback', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.retroaction')
            },
            show: function (row) {
              if (
                _this.curRole === 'VENDOR' &&
                row.status === 'IN_FEEDBACK' &&
                row.vIsFeedback === 'N'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.view')
            },
            show: function (row) {
              if (row.status === 'ASSESSED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: row => this.adandonOne(row),
            formattor (val) {
              return _this.$t('common.abandon')
            },
            show: row => ['WITHDRAW', 'WITHDRAWN', 'REJECTED'].includes(row.status) // change by liwenhong
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    adandonOne (row) {
      this.$http({
        url: '/api-pef/vendorAsses/abandon',
        method: 'GET',
        params: { vendorAssesId: row.vendorAssesId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('components.approvalHead.tips.approvalCompletion'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pef/vendorAsses/importModelDownload',
        `${this.$t('perfMod.appraisalTemplate')}.xls`
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      // if (v && v.dateList) {
      //   v.assessmentDateStart = v.dateList[0];
      //   v.assessmentDateEnd = v.dateList[1];
      //   delete v.dateList;
      // }
      // let query = v || this.preFormObj;
      const { dateList, ...rest } = v || {}
      let params = { ...rest }
      if (dateList) {
        const [assessmentDateStart, assessmentDateEnd] = dateList
        params = { ...rest, assessmentDateStart, assessmentDateEnd }
      }

      this.queryParam = params
      delete this.queryParam.dateList
      if (!params.assessmentDateStart) delete this.queryParam.assessmentDateStart
      if (!params.assessmentDateEnd) delete this.queryParam.assessmentDateEnd
      // this.queryParam = v;
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 获取所有币种
      getAllPurCurrency().then(res => {
        this.currencyList = adaptDictData(res.data, 'currency')
      })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 选中
    checkChange (val) {
      this.currentRows = val
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: assessmentDetail,
          params: {
            flag: 'add',
            tabName: 'assessmentDetail'
          },
          ctrlHeight: true,
          title: this.$t('perfMod.newPerformAppraisal'),
          name: 'assessmentDetail'
        }
      } else {
        // edit view process feedback  编辑 | 查看 | 处理 | 反馈
        tab = {
          component: assessmentDetail,
          ctrlHeight: true,
          params: {
            flag: type,
            orderId: row.vendorAssesId,
            tabName: 'assessmentDetail' + row.vendorAssesId
          },
          title: row.assessmentNo,
          name: 'assessmentDetail' + row.vendorAssesId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 批量删除数据
    delRowDatas () {},
    // 通知供应商
    notifyVendor () {
      if (this.currentRows && this.currentRows.length > 0) {
        console.log('123')
          if (this.currentRows.some(i => i.status !== 'REVIEWED')) {
            this.$message.warning(this.$t('perfMod.approvedInform'))
            return false
          }
        let rows = this.currentRows
        // let arr = [];
        // rows.forEach(item => {
        //   if (item.status === "DRAFT" || item.status === "WITHDRAWN") {
        //     // 拟定和撤回的数据可通知供应商
        //     arr.push(item);
        //   }
        // });
        perVendorApi.notifySupplier(rows).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.$message.warning(this.$t('vendorMod.checkInformation'))
      }
    },
    // 删除数据
    delRowData (row) {
      let vendorAssesId = row.vendorAssesId
      this.$confirm(this.$t('perfMod.sureDeleteData'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          perVendorApi.vendorAssesDel({ vendorAssesId }).then(res => {
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
