<template>
  <el-container
    class="flex-container the_biddingProject_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      >
        <!-- 业务模式 -->
        <template #businessModeCode="{ scope }">
          <DictSelect
            v-model="scope.businessModeCode"
            code="BUSINESS_MODE"
          />
        </template>
        <!-- 运输方式 -->
        <template #transportModeCode="{ scope }">
          <DictSelect
            v-model="scope.transportModeCode"
            code="TRANSPORT_MODE"
          />
        </template>
        <!-- 业务类型 -->
        <template #businessType="{ scope }">
          <DictSelect
            v-model="scope.businessType"
            code="LOGISTICS_BUSINESS_TYPE"
          />
        </template>
        <!-- 项目状态 -->
        <template #bidingStatus="{ scope }">
          <DictSelect
            v-model="scope.bidingStatus"
            code="LOGISTICS_PROJECT_STATUS"
          />
        </template>
      </form-wrapper>

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="bid:biddingProjectList:add"
            type="primary"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <el-button
            type="primary"
            @click="deleteMore"
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
        :pre-query-data="queryParam"
        :check-change="checkChange"
        :comActive="$attrs['changeTab']"
        url="/api-pd/logistics/biding/listPage"
        @afterQuery="afterQuery"
      >
        <template #revokeReason="props">
          <el-popover
            v-if="Array.isArray(props.scope.row.revokeReason)"
            placement="top"
            width="400"
            trigger="click"
          >
            <el-table
              border
              :data="props.scope.row.revokeReason"
            >
              <!-- 撤回原因 -->
              <el-table-column
                align="center"
                prop="reason"
                width="200"
                :label="$t('bidMod.withdrawReason')"
                :show-overflow-tooltip="true"
              />
              <!-- 撤回时间 -->
              <el-table-column
                align="center"
                prop="time"
                width="200"
                :label="$t('logisticsMod.withdrawTime')"
                :show-overflow-tooltip="true"
              />
            </el-table>
            <el-button
              slot="reference"
              type="text"
            >
              {{
                $t("common.view")
              }}
            </el-button>
          </el-popover>
        </template>
      </table-view>
    </el-main>

    <!-- 请填写撤回原因 -->
    <srm-dialog
      :visible.sync="withdrawVisible"
      :title="$t('logisticsMod.msgWithdrawReason')"
      size="middle"
    >
      <el-form
        ref="form"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="withdReason">
          <el-input
            v-model="form.withdReason"
            type="textarea"
            :rows="2"
            :placeholder="$t('common.pleaseTypeContents')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="withdrawSumbit"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="withdrawVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 投标供应商 -->
    <srm-dialog
      :visible.sync="biddingSuppliersVisible"
      :title="$t('bidMod.bidingvendorName')"
      size="middle"
    >
      <el-table
        :data="biddingSuppliersTable"
        style="width: 100%"
        border
      >
        <!-- 供应商编码 -->
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          :show-overflow-tooltip="true"
        />
        <!-- 供应商名称 -->
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('common.vendorName')"
          :show-overflow-tooltip="true"
        />
        <!-- 投标状态 -->
        <el-table-column
          align="center"
          prop="status"
          :label="$t('bidMod.orderStatus')"
          :show-overflow-tooltip="true"
          :formatter="formattorVendor"
        />
      </el-table>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="biddingSuppliersVisible = false">
          {{ $t("common.backTo") }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import biddingProjectDetail from './biddingProjectDetail'
import { parseTime } from '@/utils'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'BiddingProjectList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    biddingProjectDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { tableName: this.name, defaultTableHeader: this.tableHeader }
  },
  data () {
    return {
      status: false,
      name: 'biddingProjectTable',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      biddingSuppliersTable: [],
      currentRow: null,
      currentRows: [],
      bidingId: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      tableData2: [],
      statusList: [],
      form: {
        withdReason: ''
      },
      rules: {
        withdReason: [
          { required: true, message: this.$t('logisticsMod.msgWithdrawReason') }
        ]
      },
      isModify: false,
      withdrawVisible: false,
      biddingSuppliersVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'bidingNum', label: this.$t('bidMod.bidingNum') }, // 项目编号
        { prop: 'bidingName', label: this.$t('bidMod.bidingName') }, // 项目名称
        {
          prop: 'bidingStatus',
          label: this.$t('bidMod.bidingStatus'), // 项目状态
          slot: 'bidingStatus',
          type: 'slot'
        },
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'), // 业务模式
          slot: 'businessModeCode',
          type: 'slot'
        },
        {
          prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'), // 运输方式
          slot: 'transportModeCode',
          type: 'slot'
        },
        {
          prop: 'businessType',
          label: this.$t('bidMod.businessType'), // 业务类型
          slot: 'businessType',
          type: 'slot'
        },
        { prop: 'currentRound', label: this.$t('bidMod.currentRound') }, // 当前轮次
        {
          prop: 'requirementHeadNum',
          label: this.$t('contractMod.applicationOrderNum')
        }, // 申请单号
        { prop: 'createdByName', label: this.$t('bidMod.quotePurchasor') } // 采购员
      ],
      queryParam: {},
      bidingStatusList: [],
      bidingProStatusList: []
    }
  },
  watch: {
    $route: {
      immediate: true,
      deep: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'biddingProject'
        ) {
          // 工作流 或者其他地方 跳转过来
          let bidingId = null
          let title = null
          // if(this.$route.params.fdFormInstanceId){
          //   bidingId = Number(this.$route.params.fdFormInstanceId);
          //   title = this.$route.params.fdSubject; // 流程标题
          // }else
          if (this.$route.params.formId) {
            bidingId = Number(this.$route.params.formId)
            title = this.$route.params.formNo // 单据号
          }
          let row = {
            ...this.$route.params,
            bidingId,
            bidingName: title // tab 标题显示
          }
          this.editOrgData(row) // 点开
        }
        if (this.$route.params.type === 'BIDDINGBi') {
          this.showBindProjectDetail(
            this.$route.params.bidingId,
            this.$route.params.inquiryNumber
          )
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'bidingNum', label: _this.$t('bidMod.bidingNum'), width: 150 },
      {
        prop: 'bidingName',
        label: _this.$t('bidMod.bidingName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('edit', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'bidingStatus',
        label: _this.$t('bidMod.bidingStatus'),
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_PROJECT_STATUS'
      },
      {
        prop: 'currentRound',
        label: this.$t('bidMod.currentRound'), // 当前轮次
        width: 100
      },
      {
        prop: 'requirementHeadNum',
        label: this.$t('contractMod.applicationOrderNum'), // 申请单号
        width: 150
      },
      {
        prop: 'createdByName',
        label: this.$t('bidMod.quotePurchasor'), // 采购员
        width: 150
      },
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), // 业务模式
        width: 120,
        dataType: 'dict',
        code: 'BUSINESS_MODE'
      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 120,
        dataType: 'dict',
        code: 'TRANSPORT_MODE'
      },
      {
        prop: 'businessType',
        label: this.$t('bidMod.businessType'), // 业务类型
        width: 120,
        dataType: 'dict',
        code: 'LOGISTICS_BUSINESS_TYPE'

      },
      {
        prop: 'serviceProjectName',
        label: this.$t('logisticsMod.serviceProjectName'), // 服务项目名称
        width: 120
      },
      {
        prop: 'biddingSuppliers',
        label: this.$t('logisticsMod.biddingSuppliers'), // 已投标供应商
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.biddingSuppliers(row)
        }.bind(this)
      },
      {
        prop: 'enrollEndDatetime',
        label: _this.$t('bidMod.enrollEndDatetime'),
        width: 150
      },
      {
        prop: 'revokeReason',
        label: this.$t('bidMod.withdrawReason'), // 撤回原因
        width: 120,
        showType: 'slot',
        slot: 'revokeReason'
      },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('bidMod.publishBy'),
width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('bidMod.creationDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'releaseDatetime',
        label: _this.$t('bidMod.releaseDatetime'),
        width: 150
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('contractMod.lastUpdatedBy'), // 最后更新人
        width: 150
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('dataConfMod.lastUpdateDate'), // 最后更新时间
        width: 150
      },
      {
        prop: 'operation',
        label: _this.$t('bidMod.operation'),
        width: 250,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOrgData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.management')
            }
          },
          {
            callback: function (row) {
              this.release(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.publish') // 发布
            },
            show: function (row) {
              // 审批状态为 已审批
              if (['DRAW_UP'].includes(row.bidingStatus)) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.withdraw(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.withdraw') // 撤回
            },
            show: function (row) {
              // 审批状态为 已审批
              if (['ACCEPT_BID'].includes(row.bidingStatus)) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.deleteItem(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            show: function (row) {
              if (row.bidingStatus == 'DRAW_UP') {
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
      this.getQuerydata()
    })
  },
  mounted () {
    this.status = this.$route.params.status
      ? this.$route.params.status
      : this.status
    if (this.status) {
      this.showBindProjectDetail(
        this.$route.params.bidingId,
        this.$route.params.inquiryNumber
      )
    }
  },
  methods: {
    afterQuery (data) {
      data.map(i => {
        if (i.revokeReason.length) {
          i.revokeReason = JSON.parse(i.revokeReason)
        }
      })
    },
    biddingSuppliers (row) {
      this.biddingSuppliersVisible = true
      this.$http({
        url: '/api-pd/logistics/biding/previewVendor',
        method: 'get',
        params: {
          bidingId: row.bidingId
        },
        loading: true
      })
        .then(res => {
          this.biddingSuppliersTable = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    formattorVendor (row) {
      return this.$getDictLabel('BIDDING_ORDER_STATES', row.status)
    },
    // 管理招标项目
    editOrgData (row) {
      let tab = {
        component: biddingProjectDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'biddingProjectDetail' + row.bidingName
        },
        title: row.bidingName,
        name: 'biddingProjectDetail' + row.bidingName
      }
      this.$emit('tab-add', tab)
    },
    // 发布
    release (row) {
      this.$http({
        url: '/api-pd/logistics/biding/release',
        method: 'get',
        params: {
          bidingId: row.bidingId
        },
        loading: true
      })
        .then(data => {
          this.$message.success(data.message)
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 结果审批
    jiexiangWorkflow (row) {
      this.$http({
        url: '/api-pd/bidInitiating/biding/endProjectApproval',
        method: 'GET',
        params: { bidingId: row.bidingId },
        loading: true
      })
        .then(data => {
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    withdraw (row) {
      this.withdrawVisible = true
      this.bidingId = row.bidingId
    },
    withdrawSumbit () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/biding/withdraw',
            method: 'get',
            params: {
              bidingId: this.bidingId,
              reason: this.form.withdReason
            },
            loading: true
          })
            .then(data => {
              this.$message({
                type: 'success',
                message: data.message
              })
              this.getQuerydata()
              this.withdrawVisible = false
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    deleteMore () {
      if (!this.currentRows.length) {
        this.$message.error(this.$t('logisticsMod.msgSelOneDataDel')) // 请至少选择一条数据删除
        return
      }
      if (this.currentRows.some(i => i.bidingStatus != 'DRAW_UP')) {
        this.$message.error(this.$t('logisticsMod.msgDraftDataDel')) // 只有拟定的数据可以删除
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/biding/delete',
            method: 'post',
            data: this.currentRows.map(i => i.bidingId),
            loading: true
          })
            .then(data => {
              if (data) {
                this.$message({
                  message: this.$t('common.successDelete'),
                  type: 'success'
                })
                this.getQuerydata()
              }
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    deleteItem (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/biding/delete',
            method: 'post',
            data: [row.bidingId],
            loading: true
          })
            .then(data => {
              if (data) {
                this.$message({
                  message: this.$t('common.successDelete'),
                  type: 'success'
                })
                this.getQuerydata()
              }
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },

    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    checkChange (val) {
      this.currentRows = val
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: biddingProjectDetail,
          params: {
            flag: 'add',
            tabName: 'biddingProjectDetail'
          },
          title: this.$t('bidMod.createProject'), // 创建项目
          name: 'biddingProjectDetail'
        }
      } else {
        // 修改
        tab = {
          component: biddingProjectDetail,
          params: {
            flag: 'edit',
            readOnly: true,
            row: row,
            tabName: 'biddingProjectDetail' + row.bidingName
          },
          title: row.bidingName,
          name: 'biddingProjectDetail' + row.bidingName
        }
      }
      this.$emit('tab-add', tab)
    },
    showBindProjectDetail (inquiryId, inquiryNumber) {
      let row = { bidingId: inquiryId, bidingName: inquiryNumber }
      this.editTab('edit', row)
    }
  }
}
</script>
<style scoped lang="scss">
.the_biddingProject_wrapper /deep/ {
  .el-button-group .el-button {
    margin-left: 5px !important;
  }
}
</style>
