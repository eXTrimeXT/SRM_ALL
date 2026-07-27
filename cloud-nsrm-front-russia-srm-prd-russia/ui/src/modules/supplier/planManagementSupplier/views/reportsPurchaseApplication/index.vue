<template>
  <el-container
    class="flex-container-notab the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <TableView
        :ref="gridId"
        bigData
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/pr/requirementLine/listPageRequirementLineChart"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'ReportsPurchaseApplication',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'purchaseApplicationList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      isModify: false,
      globalNickname: null,
      preArr: [
        {
          prop: 'requirementHeadNum',
          label: this.$t('purchaseDemand.requirementHeadNum')
        }, // 申请编号
        { prop: 'purchaseType', label: this.$t('bid_mod.purchaseType') }, // 采购类型
        { prop: 'applyStatus', label: this.$t('purchaseDemand.applyStatus') }, // 单据状态
        {
          prop: 'orgIds',
          label: this.$t('purchaseDemand.businessEntity'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'startApplyDate',
          label: this.$t('purchaseDemand.startApplyDate'),
          type: 'date'
        }, // 申请开始日期
        {
          prop: 'endApplyDate',
          label: this.$t('purchaseDemand.endApplyDate'),
          type: 'date'
        }, // 申请结束日期
        {
          prop: 'ceeaDepartmentId',
          label: this.$t('purchaseDemand.ceeaDepartment'), // 申请部门
          type: 'quicksearch',
          showKey: 'descr',
          propKey: 'deptid',
          name: 'ceea_base_dept'
        },
        { prop: 'createdFullName', label: this.$t('purchaseDemand.applicant') }, // 申请人
        {
          prop: 'categoryId',
          label: this.$t('purchaseDemand.materialCate'), // 物料大类
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category3'
        },
        { prop: 'ceeaProjectNum', label: this.$t('purchaseDemand.projectId') }, // 项目编号
        {
          prop: 'ceeaProjectName',
          label: this.$t('purchaseDemand.projectName')
        } // 项目名称
      ],
      queryParam: {},
      purchaseTypeList: []
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('purchaseDemand.applyOrderNo'), // 申请单编号
        width: 150
      },
      {
        prop: 'auditStatus',
        label: this.$t('purchaseDemand.handleStatus'),
        align: 'center',
        width: 120
      }, // 处理状态
      {
        prop: 'ceeaPerformUserNickname',
        label: this.$t('purchaseDemand.procureExecutor'),
        width: 140
      }, // 采购履行人名称
      {
        prop: 'ceeaStrategyUserNickname',
        label: this.$t('purchaseDemand.strategicPurchaser'),
        width: 140
      }, // 策略采购员名称
      {
        prop: 'applyStatus',
        label: this.$t('common.status'),
        width: 120,
        formattor: val => this.$getDictLabel('APPLICATION_STATUS', val)
      }, // 状态
      {
        prop: 'sourceNo',
        label: this.$t('purchaseDemand.sourceNo'),
        width: 140
      }, // 寻源单据编号
      {
        prop: 'ceeaPurchaseType',
        label: this.$t('purchaseDemand.purchaseType'),
        width: 120
      }, // 采购类型
      {
        prop: 'bigCategoryName',
        label: this.$t('purchaseDemand.applicationType'),
        width: 120
      }, // 申请单类型
      {
        prop: 'ceeaIfDirectory',
        label: this.$t('purchaseDemand.ceeaIfCatalogMaterial'), // 是否目录化
        width: 120,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
        }
      },
      {
        prop: 'requirementDepartment',
        label: this.$t('purchaseDemand.requirementDepartment'),
        width: 120
      }, // 需求部门
      {
        prop: 'ceeaDepartmentName',
        label: this.$t('dataConfMod.descr'),
        width: 120
      }, // 部门名称
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('purchaseDemand.applicant'), // 申请人
        width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 120
      }, // 创建时间
      {
        prop: 'orgName',
        label: this.$t('purchaseDemand.businessEntity'),
        width: 120
      }, // 业务实体
      {
        prop: 'ceeaAssetType',
        label: this.$t('oneStopShopping.assetClass'),
        width: 120
      }, // 资产类别
      { prop: 'ifHold', label: this.$t('purchaseDemand.ifHold'), width: 120 }, // 是否暂挂
      {
        prop: 'enableUnAssigned',
        label: this.$t('purchaseDemand.ifCancel'), // 是否取消
        width: 120,
        formattor (val) {
          return val === 'true'
            ? _this.$t('common.yes')
            : _this.$t('common.no')
        }
      },
      {
        prop: 'rowNum',
        label: this.$t('purchaseDemand.applyOrderRowNo'),
        width: 120
      }, // 申请单行号
      {
        prop: 'organizationName',
        label: this.$t('purchaseDemand.invOrg'),
        width: 120
      }, // 库存组织
      {
        prop: 'materialCode',
        label: this.$t('purchaseDemand.itemCode'),
        width: 120
      }, // 物料编码
      {
        prop: 'materialName',
        label: this.$t('purchaseDemand.itemName'),
        width: 120
      }, // 物料名称
      {
        prop: 'categoryName',
        label: this.$t('bidMod.categoryName'),
        width: 120
      }, /// 物料分类
      {
        prop: 'ProjectName',
        label: this.$t('purchaseDemand.projectName'),
        width: 120
      }, // 项目名称
      { prop: 'unit', label: this.$t('purchaseDemand.unitCode'), width: 120 }, // 单位
      {
        prop: 'notaxPrice',
        label: this.$t('oneStopShopping.unitPrice'),
        width: 120
      }, // 预算单价
      {
        prop: 'requirementQuantity',
        label: this.$t('purchaseDemand.requirementQuantity'),
        width: 120
      }, // 需求数量
      {
        prop: 'orderNumber',
        label: this.$t('logisticsMod.orderNum'),
        width: 120
      }, // 订单编号
      {
        prop: 'ceeaExecutedQuantity',
        label: this.$t('purchaseDemand.ceeaExecutedQuantity'),
        width: 120
      }, // 已下单数量
      {
        prop: 'orderQuantity',
        label: this.$t('purchaseDemand.prRemainOrderQuantity'),
        width: 160
      }, // pr剩余可下单数量
      {
        prop: 'requirementDate',
        label: this.$t('purchaseDemand.requirementDate'),
        width: 120
      }, // 需求日期
      {
        prop: 'vendorName',
        label: this.$t('purchaseDemand.ifSpecify'),
        width: 120
      }, // 是否指定
      {
        prop: 'comments',
        label: this.$t('purchaseDemand.comments1'),
        width: 120
      }, // 备注
      {
        prop: 'ProjectNumber',
        label: this.$t('purchaseDemand.projectId'),
        width: 120
      }, // 项目编号
      {
        prop: 'ProjectName',
        label: this.$t('purchaseDemand.projectName'),
        width: 120
      }, // 项目名称
      {
        prop: 'TaskNumber',
        label: this.$t('contractMod.taskNumber'),
        width: 120
      }, // 任务编号
      { prop: 'TaskName', label: this.$t('contractMod.taskName'), width: 120 }, // 任务名称
      {
        prop: 'contractNo',
        label: this.$t('purchaseDemand.contactSerialNumber'),
        width: 140
      }, // 合同序列号
      {
        prop: 'contractCode',
        label: this.$t('purchaseDemand.contractNum'),
        width: 120
      }
    ]
    this.defaultTableHeader = this.tableHeader
    // 单据状态
      this.$set(this.preArr, 2, {
        prop: 'auditStatus',
        label: this.$t('purchaseDemand.applyStatus'), // 单据状态
        type: 'dict',
        code: 'APPROVAL_STATUS'
      })
    // 采购类型
      this.$set(this.preArr, 1, {
        prop: 'ceeaPurchaseType',
        label: this.$t('purchaseDemand.purchaseType'), // 采购类型
        type: 'dict',
        code: 'PURCHASE_TYPE'
      })
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
    }
  }
}
</script>
<style scoped lang="scss"></style>
