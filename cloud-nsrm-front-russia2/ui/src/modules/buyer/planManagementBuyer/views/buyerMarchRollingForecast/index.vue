<template>
  <el-container
    class="flex-container-notab the_buyerMarchRollingForecast_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formwrapper"
        :form-array="preArr"
        :select-dictionary="selectDictionary"
        form-label-width="120px"
        :col-length="4"
        :p-form-data.sync="queryParam"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="add"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            @click="save()"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            @click="submit"
          >
            {{
              $t('common.submit')
            }}
          </el-button>
          <el-button
            @click="deleteList()"
          >
            {{
              $t('common.delete')
            }}
          </el-button>
          <MImport
            ref="import"
            type="default"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            :isShowTip="false"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/plan/orderForecast/listPage"
        @afterQuery="getMonthLabel"
      >
        <template #firstMonthLabel="props">
          <label style="font-weight: normal;"> {{ props.scope.row.firstMonthLabel }}{{ $t("time.months") }} </label>
          <el-input
            v-model="props.scope.row[props.scope.column.property]"
            class="month-input"
            :type="props.inputType || 'string'"
          />
        </template>
        <template #secondMonthLabel="props">
          <label style="font-weight: normal;"> {{ props.scope.row.secondMonthLabel }}{{ $t("time.months") }} </label>
          <el-input
            v-model="props.scope.row[props.scope.column.property]"
            class="month-input"
            :type="props.inputType || 'string'"
          />
        </template>
        <template #threeMonthLabel="props">
          <label style="font-weight: normal;"> {{ props.scope.row.threeMonthLabel }}{{ $t("time.months") }} </label>
          <el-input
            v-model="props.scope.row[props.scope.column.property]"
            class="month-input"
            :type="props.inputType || 'string'"
          />
        </template>
        <!-- 业务实体 -->
        <template #orgId="{scope}">
          <OrganizationSelector
            v-if="scope.row.editable === true"
            v-model="scope.row.orgId"
            :parent-id="-1"
            node-type="OU"
            :scope="scope.row"
            :placeholder="$t('common.pleaseSelect')"
            @select="selectHandler"
          />
          <span v-else>{{ scope.row.orgName }}</span>
        </template>
        <!-- 库存组织 -->
        <template #organizationId="{scope}">
          <OrganizationSelector
            v-if="scope.row.editable === true"
            v-model="scope.row.organizationId"
            :parent-id="scope.row.orgId"
            node-type="INV"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope.row"
            @select="selectHandler2"
          />
          <span v-else>{{ scope.row.organizationName }}</span>
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { getToken } from '@/utils/auth'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import OrganizationSelector from 'lib@/components/organization-selector'
import { orderForecastBuyerApi } from 'modb@/planManagementBuyer/api'

export default {
  name: 'BuyerMarchRollingForecast',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    OrganizationSelector
  },
  data () {
    return {
      iModal: {
        title: this.$t('common.excelImport'), // Excel导入
        upLoadUrl: '/api-sup-ce/plan/orderForecast/saveByExcel'
      },
      curRole: this.$store.getters.userType, // vendor buyer
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'buyerMarchRollingForecast',
        fileType: 'excel'
      },
      name: '',
      reviewFormNumber: '',
      selectDictionary: {},
      gridData: [],
      tableName: 'marchRollingForecast_buyer',
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      headers: {},
      preArr: [
        {
          prop: 'categoryId',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category4'
        },
        {
          prop: 'materialId',
          label: this.$t('common.materialCode'), // 物料编码
          type: 'quicksearch',
          showKey: 'materialName',
					propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'planMonth',
          label: this.$t('planMod.planMonth'),
          type: 'month'
        }, // 计划月
        { prop: 'status', label: this.$t('common.status'), type: 'select' },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        }, // 供应商名称
        {
          prop: 'createdId',
          label: () => this.$t('common.creator'),
          type: 'quicksearch',
          propKey: 'userId',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector' // 库存组织
        }
      ],
      queryParam: {}
    }
  },
  provide () {
    return { context: this }
  },
  created () {
    var _this = this
    this.headers = {
      Authorization: `Bearer ${getToken()}`
    }
    this.tableHeader = [
      {
        prop: 'planMonth',
        label: this.$t('planMod.planMonth'),
        minWidth: 100,
        editable: val => val.editable === true && val.isNew === true,
        showType: 'date',
        type: 'month',
        valueFormat: 'yyyy-MM',
        format: 'yyyy-MM',
        formatter: true,
        addStarToColumn: true,
        pickerOptions: {
          disabledDate: time => {
            return time < new Date()
          }
        },
        callback: function (row, scope) {
          if (row.planMonth) {
            const currentMonth = parseInt(row.planMonth.split('-')[1])
            row.firstMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
            row.secondMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
            row.threeMonthLabel = currentMonth + 3 < 13 ? currentMonth + 3 : currentMonth - 9
            row.lastPlanFirstMonthLabel = currentMonth
            row.lastPlanSecondMonthLabel =
              currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
            row.lastPlanThreeMonthLabel =
              currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
            this.$set(this.$refs[this.gridId].tableData, scope.$index, row)
          }
        }.bind(this)
      }, // 计划月
      {
        prop: 'orgId',
        label: this.$t('purchaseDemand.businessEntity'), // 业务实体
        minWidth: 150,
        showType: 'slot',
        slot: 'orgId'
      },
      {
        prop: 'organizationId',
        label: this.$t('purchaseDemand.invOrg'), // 库存组织
        minWidth: 150,
        showType: 'slot',
        slot: 'organizationId'
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        minWidth: 120,
        showType: 'quicksearch',
        editable: val => val.editable === true,
        showKey: 'materialCode',
        name: 'scc_base_material_item',
        getObj: function (val, row, prop, scope) {
          row[prop] = val ? val.materialCode : ''
          row.materialName = val ? val.materialName : ''
          row.unit = val ? val.unit : ''
          this.getLastMonth(row, scope)
        }.bind(this),
        addStarToColumn: true
      }, // 物料编码
      {
        prop: 'materialName',
        label: this.$t('common.materialName'),
        minWidth: 100
      }, // 物料名称
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        width: 150,
        editable: val => val.editable === true,
        showType: 'quicksearch',
        showKey: 'companyCode',
        name: 'scc_sup_company_info_all',
        addStarToColumn: true,
        getObj: function (val, row, prop, scope) {
          row[prop] = val ? val.companyCode : ''
          row.vendorName = val ? val.companyName : ''
          this.getLastMonth(row, scope)
        }.bind(this)
      }, // 供应商编码
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        width: 180
      }, // 供应商名称
      {
        prop: 'lastMonthPlan',
        label: this.$t('planMod.lastMonthPlan'), // 上月计划
        minWidth: 130,
        children: [
          {
            prop: 'lastMonthFirst',
            label: this.$t('planMod.currentMonthAmount'), // 本月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanFirstMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'lastMonthSecond',
            label: this.$t('planMod.nextMonthAmount'), // 下月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanSecondMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'lastMonthThree',
            label: this.$t('planMod.afterNextMonthAmount'), // 后月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanThreeMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          }
        ]
      },
      {
        prop: 'currentMonthPlan',
        label: this.$t('planMod.currentMonthPlan'), // 上月计划
        minWidth: 170,
        children: [
          {
            prop: 'firstMonth',
            label: this.$t('planMod.firstMonthAfterPlan'), // 计划月后第一个月
            minWidth: 170,
            show: val => val.editable === true,
            addStarToColumn: true,
            showType: 'slot',
            slot: 'firstMonthLabel',
            formattor: (label, row) => {
              return label ? '(' + row.firstMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'secondMonth',
            label: this.$t('planMod.secondMonthAfterPlan'), // 计划月后第二个月
            minWidth: 170,
            show: val => val.editable === true,
            addStarToColumn: true,
            showType: 'slot',
            slot: 'secondMonthLabel',
            formattor: (label, row) => {
              return label ? '(' + row.secondMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'threeMonth',
            label: this.$t('planMod.thirdMonthAfterPlan'), // 计划月后第三个月
            minWidth: 170,
            show: val => val.editable === true,
            addStarToColumn: true,
            showType: 'slot',
            slot: 'threeMonthLabel',
            formattor: (label, row) => {
              return label ? '(' + row.threeMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          }
        ]
      },
      {
        prop: 'rejectReason',
        label: this.$t('planMod.supplierRejectReason'), // 供方驳回原因
        minWidth: 150
      },
      { prop: 'unit', label: this.$t('bid_mod.unit'), width: 100 }, // 单位
      {
        prop: 'status',
        label: this.$t('purchaseDemand.applyStatus'), // 单据状态
        width: 100,
        formattor: val => this.getLabel(this.orderForecastStatusOpts, val)
      },
      { prop: 'categoryName', label: this.$t('purchaseDemand.materialCateSub'), width: 100 }, // 物料小类
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        class: 'fixed-column',
        buttons: [
          {
            callback: function (row) {
              this.saveOne(row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.save') // '保存'
            },
            show: function (row) {
              if (row.editable) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row, scope) {
              // row.editable = true
              this.$set(row, 'editable', true)
              // this.$set(this.$refs[this.gridId].tableData, scope.$index, row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('purchaseDemand.editTitle') // '编辑'
            },
            show: function (row) {
              if (
                (row.status === 'CONFIRM' || row.status === 'CREATE' || row.status === 'REJECT') &&
                this.curRole === 'BUYER' &&
                !row.editable
              ) {
                return true
              } else {
                return false
              }
            }.bind(this)
          },
          {
            callback: function () {
              this.getQuerydata()
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.cancel') // '取消'
            },
            show: function (row) {
              if (this.curRole === 'BUYER' && row.editable && !row.isNew) {
                return true
              } else {
                return false
              }
            }.bind(this)
          },
          {
            callback: function (row, scope) {
              this.abandonRowData(row, scope)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.abandon') // '废弃'
            },
            show: function (row) {
              if (
                row.status === 'CREATE' ||
                (row.status === 'REJECT' && this.curRole === 'BUYER')
              ) {
                return true
              } else {
                return false
              }
            }.bind(this)
          },
          {
            callback: function (row, scope) {
              this.rowDataSumit(row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.submit') // '提交'
            },
            show: function (row) {
              if (
                (row.status === 'CREATE' || row.status === 'REJECT') &&
                this.curRole === 'BUYER' &&
                !row.editable
              ) {
                return true
              } else {
                return false
              }
            }.bind(this)
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.fetchDictionary()
  },
  methods: {
    selectHandler (node, value, scope) {
      scope.organizationId = null
      scope.organizationCode = null
      scope.organizationName = null
      if (node) {
        this.$set(scope, 'orgId', node.organizationId)
        this.$set(scope, 'orgCode', node.organizationCode)
        this.$set(scope, 'orgName', node.organizationName)
      }
    },
    selectHandler2 (node, value, scope) {
      scope.organizationId = node ? node.organizationId : ''
      scope.organizationCode = node ? node.organizationCode : ''
      scope.organizationName = node ? node.organizationName : ''
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/plan/orderForecast/importModelDownload',
        this.$t('accountMod.marchRollForecastTemp') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    fetchDictionary () {
      const dictionaryCodes = ['ORDER_FORECAST_STATUS'].map(i => ({
        dictCode: i
      }))
      getDictItemList(dictionaryCodes).then(res => {
        const [ORDER_FORECAST_STATUS] = res.data
        this.orderForecastStatusOpts = adaptDictData(ORDER_FORECAST_STATUS.ORDER_FORECAST_STATUS)
        this.selectDictionary = {
          status: this.orderForecastStatusOpts
        }
      })
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    onSuccess ({ code, message }) {
      const isError = code !== '0'
      this.$message({
        type: isError ? 'error' : 'success',
        message,
        duration: 0,
        showClose: true
      })
    },
    onError (err) {
      this.$message({
        type: 'error',
        message: err.message
      })
    },
    submit () {
      const data = this.currentRows
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[0]') // 请选择要发布的计划！
        })
      }
      const submitStaus = ['CREATE']
      if (this.currentRows.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[1]') // 只有状态为拟定状态的才能提交！
        })
        return
      }
      if (this.currentRows.some(i => i.editable)) {
        this.$confirm(this.$t('planMod.msgList[13]'), {
          // 未保存的内容会丢失
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            const params = data.filter(i => i.orderForecastId).map(i => i.orderForecastId)
            orderForecastBuyerApi.submitBatch(params).then(res => {
              this.$message({ message: res.message, type: 'success' })
              this.getQuerydata()
            })
          })
          .catch(() => {})
      } else {
        const params = data.filter(i => i.orderForecastId).map(i => i.orderForecastId)
        orderForecastBuyerApi.submitBatch(params).then(res => {
          this.$message({ message: res.message, type: 'success' })
          this.getQuerydata()
        })
      }
    },
    // 单行提交
    rowDataSumit (row) {
      orderForecastBuyerApi.submitBatch([row.orderForecastId]).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    exportOne () {},
    // 获取tableHeader初始化空字段
    getTableHeaderProps () {
      const keys = {}
      this.tableHeader.forEach(item => {
        keys[item.prop] = ''
      })
      return keys
    },
    add () {
      const row = {
        ...this.getTableHeaderProps(),
        planMonth: new Date(),
        status: 'CREATE',
        editable: true,
        isNew: true,
        uid: new Date().getTime() + '_uid'
      }
      const currentMonth = parseInt(this.$dayjs().month())
      row.firstMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
      row.secondMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
      row.threeMonthLabel = currentMonth + 3 < 13 ? currentMonth + 3 : currentMonth - 9
      row.lastPlanFirstMonthLabel = currentMonth
      row.lastPlanSecondMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
      row.lastPlanThreeMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10

      this.$refs[this.gridId].addOneEditableColumn(row)
    },
    async getLastMonth (row, scope) {
      if (row.planMonth && row.organizationId && row.materialCode && row.vendorCode) {
        const lastMonth = this.$dayjs(row.planMonth)
          .subtract(1, 'month')
          .format('YYYY-MM')
        const queryLastMonth = {
          planMonth: lastMonth,
          organizationId: row.organizationId,
          materialCode: row.materialCode,
          vendorCode: row.vendorCode,
          status: 'CONFIRM'
        }
        const res = await this.$http({
          url: '/api-sup-ce/plan/orderForecast/listPage',
          method: 'POST',
          data: queryLastMonth,
          loading: true
        })
        if (res.data.list.length > 0) {
          row.lastMonthFirst = res.data.list[0].firstMonth
          row.lastMonthSecond = res.data.list[0].secondMonth
          row.lastMonthThree = res.data.list[0].threeMonth
        }
      } else {
        row.lastMonthFirst = ''
        row.lastMonthSecond = ''
        row.lastMonthThree = ''
      }
      this.$set(this.$refs[this.gridId].tableData, scope.$index, row)
    },
    saveOne (plan) {
      const planList = new Array(plan)
      this.save(planList)
    },
    save (planList) {
      if (!planList) {
        const data = this.currentRows
        if (!data.length) {
          return this.$message({
            type: 'warning',
            message: this.$t('planMod.msgList[6]') // 请选择要保存的计划！
          })
        }

        if (!this.currentRows.some(i => i.editable && i.editable === true)) {
          this.$message({
            type: 'warning',
            message: this.$t('planMod.msgList[7]') // 必须有一个可编辑状态的计划才能点保存！
          })
          return
        }
        planList = this.currentRows.filter(row => row.editable)
      }
      let validate = true

      planList.forEach(plan => {
        if (!plan.planMonth) {
          this.$message({
            type: 'warning',
            message: this.$t('planMod.validate') // 请检查是否有必填项目为空！
          })
          validate = false
        } else {
          plan.planMonth = this.$dayjs(plan.planMonth).format('YYYY-MM')
          plan.status = 'CREATE'
        }
      })
      const addPlanList = planList.filter(i => i.isNew === true)
      const checkExist = new Map()
      addPlanList.forEach(plan => {
        const checkKey =
          plan.planMonth +
          '_' +
          plan.organizationId +
          '_' +
          plan.materialCode +
          '_' +
          plan.vendorCode
        if (checkExist[checkKey]) {
          this.$message({
            type: 'warning',
            message: this.$t('planMod.duplicate') // 每月只能新增一条数据(相同采购组织，物料和供应商)！
          })
          validate = false
        } else {
          checkExist[checkKey] = checkKey
        }
      })
      planList.forEach(plan => {
        if (!this.validate(plan)) {
          this.$message({
            type: 'warning',
            message: this.$t('planMod.validate') // 请检查是否有必填项目为空！
          })
          validate = false
        }
      })
      if (!validate) {
        return
      }

      orderForecastBuyerApi.checkSaveBatch(planList).then(res => {
        if (res.data === true) {
          this.$confirm(this.$t('planMod.abandonWarn'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          })
            .then(() => {
              this.saveBatch(planList)
            })
            .catch(() => {})
        } else {
          this.saveBatch(planList)
        }
      })
    },
    saveBatch (planList) {
      orderForecastBuyerApi.saveBatch(planList).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    delRowData (row, scope) {
      if (row.isNew || !row.orderForecastId) {
        this.$refs[this.gridId].deleteRow(scope.$index)
      } else {
        const deleteList = []
        deleteList.push(row.orderForecastId)
        this.deleteList(deleteList)
      }
    },
    deleteList () {
      const data = this.currentRows
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[2]') // 请选择要删除的计划！
        })
      }

      if (this.currentRows.some(i => i.status && i.status !== 'CREATE')) {
        this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[3]') // 只有拟定状态的计划才能删除！
        })
        return
      }

      const tableData = this.$refs[this.gridId].getTableData()
      let noIds = [] // 前端删除id
      let ids = [] // 后端删除id集合
      this.currentRows.forEach((row, i) => {
        if (!row.orderForecastId) {
          row.uid && noIds.push(row.uid)
        } else {
          ids.push(row.orderForecastId)
        }
      })

      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          if (noIds.length > 0) {
            tableData.forEach((row, i) => {
              if (noIds.includes(row.uid)) {
                this.$refs[this.gridId].deleteRow(i)
              }
            })
          }
          if (ids.length > 0) {
            orderForecastBuyerApi.deleteBatch(ids).then(res => {
              this.$message({ message: res.message, type: 'success' })
              this.getQuerydata()
            })
          }
        })
        .catch(() => {})
    },
    abandonRowData (row) {
      const abandonList = []
      abandonList.push(row.orderForecastId)
      this.$confirm(this.$t('common.confirmAbandon'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          orderForecastBuyerApi.abandonBatch(abandonList).then(res => {
            this.$message({ message: res.message, type: 'success' })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    validate (plan) {
      if (!plan.organizationId || plan.organizationId === '') {
        return false
      }
      if (!plan.materialCode || plan.materialCode === '') {
        return false
      }
      if (!plan.vendorCode || plan.vendorCode === '') {
        return false
      }
      if (!plan.firstMonth || plan.firstMonth === '') {
        return false
      }
      if (!plan.secondMonth || plan.secondMonth === '') {
        return false
      }
      if (!plan.threeMonth || plan.threeMonth === '') {
        return false
      }
      return true
    },
    getMonthLabel () {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach(row => {
          const currentMonth = parseInt(row.planMonth.split('-')[1])
          row.firstMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
          row.secondMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
          row.threeMonthLabel = currentMonth + 3 < 13 ? currentMonth + 3 : currentMonth - 9
          row.lastPlanFirstMonthLabel = currentMonth
          row.lastPlanSecondMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
          row.lastPlanThreeMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_buyerMarchRollingForecast_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .month-input {
    width: 75%;
    margin-left: 5px;
    float: right;
  }
}
</style>
