<template>
  <el-container
    class="flex-container the_siteAssessment_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/review/siteForm/listPageByParmForSupplier"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import siteAssessmentDetail from './siteAssessmentDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { parseTime } from '@/utils'
import { siteReviewPlan } from 'mods@/vendorManagementSupplier/api'

export default {
  name: 'SiteAssessmentList',
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
      tableName: 'siteAssessmentList',
      defaultTableHeader: [],
      reviewResultList: [],
      approveStatusList: [],
      quaReviewTypeList: [],
      siteTypeList: [],
      yesOrNoList: [],
      pageSize: 15,
      gridId: 'siteAssessmentList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        {
          prop: 'siteFormNumber',
          label: () => this.$t('vendorMod.siteOrderInfoV') // 供应商评审单号
        },
        {
          prop: 'assessmentType',
          label: () => this.$t('vendorMod.siteTypeV'), // 供应商评审类型
          type: 'dict', // 字典类型
          code: 'CEEA_ASSESSMENT_TYPE' // 字典code
        },
        {
          prop: 'reviewResult',
          label: () => this.$t('vendorMod.certificationResult'), // 认证结果
          type: 'dict', // 字典类型
          code: 'CEEA_RESULT_TYPE' // 字典code
        }
      ],
      queryParam: {}
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
          this.$route.params.funName === 'siteAssessment'
        ) {
          let siteFormId = Number(this.$route.params.formId)
          let siteFormNumber = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            siteFormId,
            siteFormNumber: siteFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
        // 证件到期跳转
        if (this.$route.params.dataResources) {
          let siteFormId = Number(this.$route.params.dataResources.managementAttachId)
          let siteFormNumber = this.$route.params.dataResources.dataSources // 流程标题
          let row = {
            siteFormId,
            siteFormNumber: siteFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态'
        width: 90,
        dataType: 'dict', // 数据类型为字典
        code: 'SUPPLIER_APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'siteFormNumber',
        label: () => this.$t('vendorMod.siteOrderInfoV'), // 供应商评审单号
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'assessmentType',
        label: () => this.$t('vendorMod.siteTypeV'), // 供应商评审类型
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'CEEA_ASSESSMENT_TYPE' // 字典code
      },
      {
        prop: 'reviewResult',
        label: () => this.$t('vendorMod.certificationResult'), // 认证结果
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'CEEA_RESULT_TYPE' // 字典code
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // '创建人'
        width: 110
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // '创建时间'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
    this.defaultTableHeader = this.tableHeader // 自定义表格表头
    console.log(this.$store.getters)
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    recall (row) {
      siteReviewPlan.recall(row.siteFormId).then(res => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.$refs[this.gridId].query()
        }
      })
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: siteAssessmentDetail,
          params: {
            flag: 'add',
            tabName: 'siteAssessmentDetail'
          },
          title: this.$t('vendorMod.addSite'), // 供应商评审新增
          name: 'siteAssessmentDetail'
        }
      } else if (type === 'view') {
        // 查看
        tab = {
          component: siteAssessmentDetail,
          params: {
            flag: 'view',
            row: row,
            siteFormId: row.siteFormId,
            tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
          },
          title: row.siteFormNumber,
          name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
        }
      } else if (type === 'reviewView') {
        // 查看
        tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'quaOfReviewDetail' + row.reviewFormNumber
          },
          title: () => this.$t('vendorMod.checkQuaOrderInfo'), // '查看资质审查单',
          name: 'quaOfReviewDetail' + row.reviewFormNumber
        }
      } else {
        // 修改
        tab = {
          component: siteAssessmentDetail,
          params: {
            flag: 'edit',
            row: row,
            siteFormId: row.siteFormId,
            tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
          },
          title: row.siteFormNumber,
          name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
        }
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">

</style>
