<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :init-active="true"
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template
          v-if="curRole === 'BUYER'"
          slot="left"
        >
          <el-button
            type="primary"
            @click="addOne"
          >
            {{
              $t("common.add")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="copyMore"
          >
            {{
              $t("common.copy")
            }}
          </el-button>
          <el-button
            @click="delMore"
          >
            {{
              $t("common.delete")
            }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        checkbox
        :check-change="checkChange"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pd/pr/requirement-head/listPage"
      />
      <!-- 受邀供应商清单 -->
      <srm-dialog
        v-el-drag-dialog
        :title="$t('logisticsMod.invitedSupplierList')"
        size="middle"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-table
          :data="displayVendorTable"
          style="width: 100%"
          border
          height="251px"
          highlight-current-row
        >
          <el-table-column
            type="index"
            width="55"
          />
          <!-- 供应商编码 -->
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('common.vendorCode')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
          <!-- 供应商名称 -->
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
        </el-table>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import logisticsPurchaseApplyDetail from './logisticsPurchaseApplyDetail'
import { parseTime } from '@/utils'

export default {
  name: 'LogisticsPurchaseApplyList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    logisticsPurchaseApplyDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'logisticsPurchaseApplyList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      dialogFormVisible: false,
      displayVendorTable: [],
      globalNickname: null,
      preArr: [
        {
          prop: 'requirementHeadNum',
          label: this.$t('contractMod.applicationOrderNum')
        }, // 申请单号
        {
          prop: 'requirementTitle',
          label: this.$t('logisticsMod.requirementTitle')
        }, // 申请主题
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'),
          type: 'dict',
          code: 'BUSINESS_MODE'
        }, // 业务模式
        { prop: 'ceeaProjectNum',
          label: this.$t('bid_mod.transportType')
        }, // 运输方式
        { prop: 'businessType',
          label: this.$t('dataConfMod.businessType'),
          type: 'dict',
          code: 'LOGISTICS_BUSINESS_TYPE'
        }, // 业务类型
        { prop: 'requirementStatus',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_APPLY_STATUS'
        }, // 状态
        { prop: 'applyBy', label: this.$t('purchaseDemand.applicant') }, // 申请人
        {
          prop: 'applyDepartmentName',
          label: this.$t('purchaseDemand.ceeaDepartment')
        } // 申请部门
        // {  prop: 'ceeaDepartmentId',
        //   label: '申请部门',
        //   type: 'quicksearch',
        //   showKey: 'descr',
        //   propKey: "deptid",
        //   name: 'ceea_base_dept',
        // },
      ],
      queryParam: {},
      statusList: [],
      prTypeList: [],
      purchaseTypeList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'fromFun') {
          let row = {
            ...this.$route.params.row
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.nickname
      : null
    let _this = this
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('contractMod.applicationOrderNum'), // 申请单号
        width: 120,
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
        prop: 'requirementStatus',
        label: this.$t('common.status'), /// 状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_APPLY_STATUS'
      },
      {
        prop: 'requirementTitle',
        label: this.$t('logisticsMod.requirementTitle'), // 申请主题
        minWidth: 150
      },
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
        label: this.$t('dataConfMod.businessType'), // 业务类型
        width: 120,
        dataType: 'dict',
        code: 'LOGISTICS_BUSINESS_TYPE'
      },
      {
        prop: 'serviceProjectName',
        label: this.$t('logisticsMod.serviceProjectName'),
        width: 150
      }, // 服务项目名称
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
        prop: 'ceeaApplyUserNickname',
        label: this.$t('bidMod.quotePurchasor'),
        width: 100
      }, // 采购员
      {
        prop: 'bidingCode',
        label: this.$t('logisticsMod.bidingCode'),
        width: 100
      }, // 招标单号
      {
        prop: 'businessOpenBidTime',
        label: this.$t('bidMod.techOpenBidTime'),
        width: 150
      }, // 开标时间
      {
        prop: 'vendorName',
        label: this.$t('logisticsMod.invitedSupplier'), // 受邀供应商
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOneVendor(row)
        }.bind(this),
        formattor (val, row) {
          return row.bidingId ? _this.$t('common.view') : ''
        }
      },
      { prop: 'comments', label: this.$t('common.remark'), width: 150 }, // 备注
      {
        prop: 'applyBy',
        label: this.$t('purchaseDemand.applicant'),
        width: 100
      }, // 申请人
      {
        prop: 'applyDepartmentName',
        label: this.$t('purchaseDemand.ceeaDepartment'), // 申请部门
        width: 100
      },
      { prop: 'createdByName', label: this.$t('common.creator'), width: 100 },
      {
        prop: 'creationDate',
        label: this.$t('qualitySynergy.creationDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.updatePeople'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('qualitySynergy.updateDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val, row) {
              return _this.$t('common.edit')
            },
            show: row =>
              ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.requirementStatus)
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            },
            // show: row =>(['DRAFT'].includes(row.requirementStatus) && row.createdFullName ===this.globalNickname)
            show: row => ['DRAFT'].includes(row.requirementStatus)
            // 新建 change by liwenhong 删掉已撤回、已驳回字段
          },
          {
            callback: function (row) {
              this.abandonOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.abandon') // 废弃
            },
            show: row =>
              (row.requirementStatus === 'WITHDRAW' ||
                row.requirementStatus === 'REJECTED') &&
              row.createdFullName === this.globalNickname
          }
          // change by liwenhong  增加废弃按钮
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    abandonOne (row) {
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/abandon',
        method: 'GET',
        params: { requirementHeadId: row.requirementHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
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
    addOne () {
      this.$emit('tab-add', {
        component: logisticsPurchaseApplyDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('logisticsMod.addLogisticsPurchaseApply'), // 创建物流采购申请单
        name: 'logisticsPurchaseApplyDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: logisticsPurchaseApplyDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.requirementHeadNum,
        name: 'logisticsPurchaseApplyDetail' + row.requirementHeadNum
      })
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: logisticsPurchaseApplyDetail,
        params: {
          flag: 'approvalOnly',
          row: row
        },
        title: row.requirementHeadNum,
        name: 'logisticsPurchaseApplyDetail' + row.requirementHeadNum
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: logisticsPurchaseApplyDetail,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.requirementHeadNum,
        name: 'logisticsPurchaseApplyDetail' + row.requirementHeadNum
      })
    },
    readOneVendor (row) {
      this.dialogFormVisible = true
      this.$http({
        url:
          '/api-pd/logistics/biding/getLgtBidVendorByBidingIdAndStatus',
        method: 'GET',
        params: { bidingId: row.bidingId },
        loading: true
      })
        .then(data => {
          this.displayVendorTable = data.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    enableOne () {},
    disableOne () {},
    approvalOneItem (row) {
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/approval',
        method: 'GET',
        params: { requirementHeadId: row.requirementHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/pr/requirement-head/delete',
            method: 'GET',
            params: { id: row.requirementHeadId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    checkChange (data) {
      this.currentRows = data
    },
    delMore (row) {
      if (!this.currentRows.length) {
        this.$message.error(this.$t('logisticsMod.msgSelOneDataDel'))
        return
      }
      if (this.currentRows.some(i => i.requirementStatus != 'DRAFT')) {
        this.$message.error(this.$t('logisticsMod.msgDraftDataDel'))
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/pr/requirement-head/batchDelete',
            method: 'post',
            data: this.currentRows.map(i => i.requirementHeadId),
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    copyMore (row) {
      if (!this.currentRows.length) {
        this.$message.error(this.$t('logisticsMod.msgCopyOneData'))
        return
      }
      this.$http({
        url: '/api-pd/pr/requirement-head/batchCopy',
        method: 'post',
        data: this.currentRows.map(i => i.requirementHeadId),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('logisticsMod.copySuccess'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss"></style>
