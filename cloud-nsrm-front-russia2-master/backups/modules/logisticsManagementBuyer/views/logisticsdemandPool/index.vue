<template>
  <el-container
    class="flex-container-notab the_demandPoolManagementList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="openAssignOne"
          >
            {{
              $t("purchaseDemand.distributionOrTransfer")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="cancelAssignOnes"
          >
            {{
              $t("purchaseDemand.cancelAllot")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="createBidings"
          >
            {{
              $t("logisticsMod.createBidings")
            }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-pd/pr/requirement-head/listPageNew"
      />
      <quick-search
        v-show="false"
        ref="quickSearch"
        :show-input="form.ceeaApplyUserNickname"
        show-key="personInChargeNickname"
        disabled-select
        :scope-data="form"
        name="ceea_pr_division_category1"
        @close-quicksearch="getUserObj2"
      />
    </el-main>
  </el-container>
</template>
<script>
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'LogisticsdemandPool',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'materialAssignRuleTable',
      tableName: 'logisticsdemandPool',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      selectedRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      initActive: true,
      globalorgId: null,
      globalorganizationId: null,
      globalcategoryId: null,
      dialogFormVisible: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      dialogFormVisible4: false,
      followOrderList: [],
      preFormObj: {},
      requirementHead: {
        orderType: null,
        ceeaOrderTypeIdentification: null, // 采购订单类型条目标识
        ceeaIfSupplierConfirm: 'Y',
        ceeaIfPowerStationBusiness: 'N',
        ceeaIfConSignment: 'N'
      },
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'requirementHeadNum',
          label: this.$t('contractMod.applicationOrderNum')
        }, // 申请单号
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'),
          type: 'dict',
          code: 'BUSINESS_MODE'

        }, // 业务模式
        { prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'),
          type: 'dict',
          code: 'TRANSPORT_MODE' }, // 运输方式
        { prop: 'businessType',
          label: this.$t('dataConfMod.businessType'),
          type: 'dict',
          code: 'LOGISTICS_BUSINESS_TYPE' }, // 业务类型
        {
          prop: 'serviceProjectName',
          label: this.$t('logisticsMod.serviceProjectName')
        }, // 服务项目名称
        {
          prop: 'requirementTitle',
          label: this.$t('logisticsMod.requirementTitle')
        }, // 申请主题
        { prop: 'applyBy', label: this.$t('purchaseDemand.applicant') }, // 申请人
        {
          prop: 'ceeaApplyUserNickname',
          label: this.$t('bidMod.quotePurchasor')
        }, // 采购员
        { prop: 'templateName', label: this.$t('logisticsMod.applyTemplate') }, // 申请模板
        {
          prop: 'applyDepartmentName',
          label: this.$t('purchaseDemand.ceeaDepartment')
        }, // 申请部门
        {
          prop: 'ceeaDepartmentName',
          label: this.$t('purchaseDemand.ceeaDepartment'),
          type: 'dict',
          code: 'LOGISTICS_APPLY_ASSIGN_STYLE'
        }, // 申请部门
        {
          prop: 'applyAssignStatus',
          label: this.$t('logisticsMod.applyAssignStatus'),
          type: 'dict',
          code: 'LOGISTICS_APPLY_PROCESS_STATUS'
        } // 分配状态
        // { prop: "requirementStatus", label: '单据状态'},
      ],
      form: {
        ceeaApplyUserId: null,
        ceeaApplyUserName: null,
        ceeaApplyUserNickname: null
      },
      globalSourceType: null,
      sourceTypeList: [
        { label: this.$t('purchaseDemand.priceComparison'), value: '1' },
        { label: this.$t('purchaseDemand.bidding'), value: '0' },
        { label: this.$t('purchaseDemand.priceBidding'), value: '2' }
      ],
      purOrderList: [],
      currentBatchReasons: [],
      currentRows: [],
      assignList: [],
      rules: {
        orgName: [
          { required: true, message: this.$t('orderMod.msgVendorOrder[6]') }
        ],
        materialCode: [
          {
            required: true,
            message: this.$t('purchaseDemand.materialCodeTips')
          }
        ],
        supUserNickname: [
          {
            required: true,
            message: this.$t('purchaseDemand.supUserNicknameTips')
          }
        ]
      },
      queryParam: {},
      paymentType: [],
      dmandLineRequestOpts: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('contractMod.applicationOrderNum'), // 申请单号
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.goTo(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'applyAssignStatus',
        label: this.$t('logisticsMod.applyAssignStatus'), // 分配状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_APPLY_ASSIGN_STYLE'
      },
      {
        prop: 'applyProcessStatus',
        label: this.$t('bidMod.billstatus'), // 单据状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_APPLY_PROCESS_STATUS'
      },
      {
        prop: 'ceeaApplyUserNickname',
        label: this.$t('bidMod.quotePurchasor'),
        width: 100
      }, // 采购员
      {
        prop: 'ceeaApplyUserName',
        label: this.$t('purchaseDemand.buyer'),
        width: 120
      }, // 采购员账号
      {
        prop: 'templateName',
        label: this.$t('logisticsMod.applyTemplate'),
        minWidth: 120
      }, // 申请模板
      {
        prop: 'requirementTitle',
        label: this.$t('logisticsMod.requirementTitle'),
        minWidth: 120
      }, // 申请主题
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), // 业务模式
        width: 100,
        dataType: 'dict',
        code: 'BUSINESS_MODE'
      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 100,
        dataType: 'dict',
        code: 'TRANSPORT_MODE'

      },
      {
        prop: 'businessType',
        label: this.$t('bidMod.businessType'), // 业务类型
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_BUSINESS_TYPE'

      },
      {
        prop: 'serviceProjectName',
        label: this.$t('logisticsMod.serviceProjectName'),
        minWidth: 150
      }, // 服务项目名称
      {
        prop: 'projectTotal',
        label: this.$t('logisticsMod.projectTotal'),
        width: 100
      }, // 项目总量
      {
        prop: 'budgetAmount',
        label: this.$t('purchaseDemand.ceeaTotalBudget'),
        width: 100
      }, // 预算金额
      {
        prop: 'unit',
        label: this.$t('bid_mod.unit'), // 单位
        width: 100,
        dataType: 'dict',
        code: 'SUB_LEVEL'

      },
      {
        prop: 'biddingCode',
        label: this.$t('logisticsMod.bidingCode'),
        width: 150
      }, // 招标单号
      {
        prop: 'applyBy',
        label: this.$t('purchaseDemand.applicant'),
        width: 120
      }, // 申请人
      {
        prop: 'applyDepartmentName',
        label: this.$t('purchaseDemand.ceeaDepartment'),
        width: 120
      }, // 申请部门
      {
        prop: 'priceStartDate',
        label: this.$t('logisticsMod.priceStartDate'), // 价格有效开始日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'priceEndDate',
        label: this.$t('logisticsMod.priceEndDate'), // 价格有效结束日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'creationDate',
        label: this.$t('qualitySynergy.creationDate'), // 创建日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
        width: 130,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 230,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            show: row =>
              ['UNASSIGNED'].includes(row.applyAssignStatus) &&
              ['UNPROCESSED'].includes(row.applyProcessStatus),
            formattor (val) {
              return _this.$t('purchaseDemand.allot') // 分配
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            show: row =>
              ['ASSIGNED'].includes(row.applyAssignStatus) &&
              ['UNPROCESSED'].includes(row.applyProcessStatus),
            formattor (val) {
              return _this.$t('components.approvalHead.headers.commission') // 转办
            }
          },
          {
            callback: function (row) {
              this.cancelAssignOne(row)
            }.bind(this),
            show: row =>
              ['ASSIGNED'].includes(row.applyAssignStatus) &&
              ['UNPROCESSED'].includes(row.applyProcessStatus),
            formattor (val) {
              return _this.$t('purchaseDemand.cancelAllot') // 取消分配
            }
          },
          {
            callback: function (row) {
              this.createBiding(row)
            }.bind(this),
            show: row =>
              ['UNPROCESSED'].includes(row.applyProcessStatus) &&
              ['ASSIGNED'].includes(row.applyAssignStatus),
            formattor (val) {
              return _this.$t('logisticsMod.createBidings') // 创建招投标
            }
          }
          // {
          //   callback: function(row) {
          //     this.deleteOne(row);
          //   }.bind(this),
          //   formattor(val) {
          //     return "删除";
          //   }
          // }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.preFormObj = Object.assign({}, { applyProcessStatus: 'UNPROCESSED' })
    this.$nextTick(() => {
      this.getQuerydata(this.preFormObj)
    })
  },
  methods: {
    goTo (row) {
      this.$router.push({
        name: 'logisticsPurchaseApply',
        params: {
          from: 'fromFun',
          row: row
        }
      })
    },
    getQuerydata (v) {
      console.log('v', v)
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.selectedRows = val
      this.assignList = val.map(v => v.requirementHeadId)
    },
    editTab (type, row) {
      this.assignList = [row.requirementHeadId]
      console.log(this.$refs)
      this.$refs.quickSearch.clickShow()
    },
    getUserObj2 (val, scope) {
      scope.ceeaApplyUserId = val ? val.personInChargeUserId : ''
      scope.ceeaApplyUserName = val ? val.personInChargeUsername : ''
      scope.ceeaApplyUserNickname = val ? val.personInChargeNickname : ''
      let params = {
        ceeaApplyUserId: this.form.ceeaApplyUserId,
        ceeaApplyUserName: this.form.ceeaApplyUserName,
        ceeaApplyUserNickname: this.form.ceeaApplyUserNickname,
        requirementHeadIds: this.assignList
      }
      this.$http({
        url: '/api-pd/pr/requirement-head/bachAssigned',
        method: 'POST',
        data: params,
        loading: true
      })
        .then(data => {
          this.dialogFormVisible = false
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.queryParam)
        })
        .catch(err => {
          console.log(err)
        })
    },
    openAssignOne () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('contractMod.msgSelData'))
        return
      }
      this.$refs.quickSearch.clickShow()
    },
    cancelAssignOnes () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('contractMod.msgSelData'))
        return
      }
      if (this.selectedRows.some(v => v.applyAssignStatus !== 'ASSIGNED')) {
        this.$message.warning(this.$t('logisticsMod.msgPurchaseApply[13]')) // 请选择已分配的数据!
        return
      }
      this.$http({
        url: '/api-pd/pr/requirement-head/bachUnAssigned',
        method: 'POST',
        data: {
          requirementHeadIds: this.selectedRows.map(v => v.requirementHeadId)
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.queryParam)
        })
        .catch(err => {
          console.log(err)
        })
    },
    cancelAssignOne (row) {
      this.$http({
        url: '/api-pd/pr/requirement-head/bachUnAssigned',
        method: 'POST',
        data: {
          requirementHeadIds: [row.requirementHeadId]
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.queryParam)
        })
        .catch(err => {
          console.log(err)
        })
    },
    createBidings () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('contractMod.msgSelData'))
        return
      }
      if (
        this.selectedRows.some(
          i =>
            i.applyProcessStatus != 'UNPROCESSED' ||
            i.applyAssignStatus != 'ASSIGNED'
        )
      ) {
        this.$message.warning(this.$t('logisticsMod.msgPurchaseApply[14]')) // "单据状态为“未处理“并且已经分配采购员的单据才可以创建招标单"
        return
      }
      this.$http({
        url: '/api-pd/logistics/biding/requirementToBidings',
        method: 'POST',
        data: this.selectedRows.map(i => i.requirementHeadId),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.queryParam)
        })
        .catch(err => {
          console.log(err)
        })
    },
    createBiding (row) {
      this.$http({
        url: '/api-pd/logistics/biding/requirementToBidings',
        method: 'POST',
        data: [row.requirementHeadId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.queryParam)
        })
        .catch(err => {
          console.log(err)
        })
    },
    syncFilterParams (values) {
      console.log('values', values)
      this.queryParam = values
    },
    deleteOne (row) {
      this.$confirm(this.$t, {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url:
              '/api-sup-ce/division/divisionMaterial/deleteDivisionMaterial',
            method: 'GET',
            params: { divisionMaterialId: row.divisionMaterialId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata(this.queryParam)
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
