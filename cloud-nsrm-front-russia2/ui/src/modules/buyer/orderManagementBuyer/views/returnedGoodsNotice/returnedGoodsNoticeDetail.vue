<template>
  <el-container
    class="the-returnedGoodsNoticeDetail-buyer-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('priceModel.costElement.baseInfo')"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            :label-width="formLabelWidth"
            label-position="top"
            class="form-incontainer"
          >
            <srm-row :gutter="32">
              <!-- 退货单号 -->
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')">
                  <el-input
                    v-model="form.returnOrderNumber"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 供应商 -->
              <srm-col>
                <el-form-item
                  :label="$t('common.vendor')"
                  prop="vendorName"
                >
                  <QuickSearch
                    :disabled="VIEW_PAGE"
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info_all"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <!-- 供应商编码 -->
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorCode')">
                  <el-input
                    v-model="form.vendorCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 业务实体 -->
              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.businessEntity')"
                  prop="organizationId"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.organizationId"
                    :disabled="VIEW_PAGE"
                    :jump-login="true"
                    :parent-id="-1"
                    :placeholder="$t('common.pleaseSelect')"
                    node-type="OU"
                    :scope="form"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>

              <!-- 不良类型 -->
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.badType')"
                  prop="badType"
                >
                  <DictSelect
                    v-model="form.badType"
                    code="RETURN_ORDER_BAD_TYPE"
                    :disabled="VIEW_PAGE"
                  />
                </el-form-item>
              </srm-col>
              <!-- 退货时间 -->
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.returnDate')"
                  prop="returnDate"
                >
                  <el-date-picker
                    v-model="form.returnDate"
                    :disabled="VIEW_PAGE"
                    type="date"
                    :picker-options="pickerOptions"
                    value-format="yyyy-MM-dd"
                    :format="$formatDatePicker"
                    :placeholder="$t('purchaseDemand.datePicker')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="flag != 'add'"
                :span="8"
              >
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.status')">
                  <DictSelect
                    v-model="form.returnStatus"
                    code="RETURN_ORDER_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <template
                v-if="flag != 'add'"
                type="flex"
              >
                <srm-col>
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdUserName"
                  >
                    <el-input
                      v-model="form.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.creationTime')"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="form.creationDate"
                      disabled
                      :format="$formatDatePickerTime"
                      type="date"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.lastUpdateBy')"
                    prop="lastUpdatedUserName"
                  >
                    <el-input
                      v-model="form.lastUpdatedUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.lastUpdateDate')"
                    prop="lastUpdateDate"
                  >
                    <el-date-picker
                      v-model="form.lastUpdateDate"
                      disabled
                      :format="$formatDatePickerTime"
                      type="date"
                    />
                  </el-form-item>
                </srm-col>
              </template>
              <!-- 退货原因 -->
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('orderMod.returnReason')"
                  prop="returnReason"
                >
                  <el-input
                    v-model.trim="form.returnReason"
                    :disabled="VIEW_PAGE"
                    type="textarea"
                    :rows="2"
                    maxlength="50"
                    show-word-limit
                  />
                </el-form-item>
              </srm-col>
              <!-- 备注 -->
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('common.remark')"
                  prop="comments"
                >
                  <el-input
                    v-model="form.comments"
                    :disabled="VIEW_PAGE"
                    type="textarea"
                    :rows="2"
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 退货明细信息 -->
        <el-collapse-item
          :title="$t('orderMod.returnDetail')"
          name="2"
        >
          <MainHeader
            :l-span="22"
            :r-span="2"
            style="padding: 10px 0"
          >
            <template slot="left">
              <el-button
                v-if="!VIEW_PAGE"
                type="primary"
                @click="addDelivery"
              >
                {{
                  $t('orderMod.addReturnDetail')
                }}
              </el-button>
            </template>
          </MainHeader>
          <el-form
            ref="tableForm"
            :model="tableData"
            :rules="tableData.rules"
          >
            <el-table
              :data="tableData.list"
              style="width: 100%"
              border
              height="222px"
            >
              <el-table-column
                align="center"
                :label="$t('common.sort')"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                :label="
                  $t('orderMod.buyerOrderSynergy.deliveryLineNum') +
                    '|' +
                    $t('purchaseDemand.lineNum')
                "
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.deliveryNumber }}|{{ scope.row.lineNum }}</span>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :label="
                  $t('orderMod.buyerOrderSynergy.orderNumber') + '|' + $t('purchaseDemand.lineNum')
                "
                :show-overflow-tooltip="true"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.orderNumber }}|{{ scope.row.orderLineNum }}</span>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('vendorMod.category')"
                width="100"
              />
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('orderMod.buyerOrderSynergy.materialCode')"
                :show-overflow-tooltip="true"
                width="150"
              />
              <el-table-column
                align="center"
                prop="materialName"
                :show-overflow-tooltip="true"
                :label="$t('orderMod.buyerOrderSynergy.materialName')"
                min-width="150"
              />
              <el-table-column
                align="center"
                prop="deliveryQuantity"
                :label="$t('orderMod.buyerOrderSynergy.deliveryQuantity')"
                width="100"
              />
              <el-table-column
                align="center"
                prop="warehouseQuantity"
                :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
                width="100"
              />
              <el-table-column
                align="center"
                prop="notReturnedNum"
                :label="$t('orderMod.notReturnedNum')"
                width="100"
              />
              <el-table-column
                align="center"
                prop="returnNum"
                :label="$t('orderMod.buyerOrderSynergy.returnNum')"
                width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :rules="tableData.rules.returnNum"
                    :prop="`list.${scope.$index}.returnNum`"
                  >
                    <el-input-number
                      v-model="scope.row.returnNum"
                      :min="0"
                      :max="scope.row.warehouseQuantity"
                      :disabled="VIEW_PAGE"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('common.operation')"
                min-width="60"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="!VIEW_PAGE"
                    type="text"
                    @click="deleteDetials(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ !VIEW_PAGE ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button
            v-if="!VIEW_PAGE"
            type="primary"
            @click="save"
          >
            {{
              $t('common.staging')
            }}
          </el-button>
          <el-button
            v-if="!VIEW_PAGE"
            type="primary"
            @click="saveOrSubmit('SUBMIT')"
          >
            {{
              $t('common.submit')
            }}
          </el-button>
        </template>
      </CToolbar>
      <!-- 送货订单明细弹窗 -->
      <srm-dialog
        :title="$t('orderMod.selDeliveryOrderDetail')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <FormWrapper
          ref="wrapper1"
          :form-array="queryForm1"
          form-label-width="120px"
          @getFormData="getQuerydata1"
        />

        <MainHeader
          :l-span="22"
          :r-span="2"
        >
          <template slot="left">
            <el-button
              type="primary"
              :disabled="!currentRows1.length"
              @click="handleAdd"
            >
              {{
                $t('common.affirm')
              }}
            </el-button>
            <el-button
              type="primary"
              @click="dialogFormVisible = false"
            >
              {{
                $t('common.cancel')
              }}
            </el-button>
          </template>
        </MainHeader>
        <TableView
          ref="deliveryList"
          table-height="260px"
          :table-header="tableHeader1"
          :check-change="handleCurrentChange1"
          :page-size="pageSize1"
          :checkbox="true"
          :pre-query-data="queryParam1"
          url="/api-sup-ce/po/deliveryNoteDetail/listInReturnOrder"
          :reserve-selection="true"
          row-key="deliveryNoteDetailId"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { setRepeatData, getCheckForm } from 'lib@/utils/util'

export default {
  name: 'ReturnedGoodsNoticeDetail',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    CToolbar,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2'],
      flag: this.$attrs.params.flag,
      formLabelWidth: '120px',
      form: {
        returnOrderId: null,
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        organizationId: null,
        organizationCode: '',
        organizationName: '',
        badType: '',
        returnReason: '',
        returnDate: parseTime(new Date(), '{y}-{m}-{d}', true),
        comments: '',
        returnStatus: '',
        createdBy: '',
        creationDate: '',
        lastUpdatedBy: '',
        lastUpdateDate: ''
      },
      rules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        organizationId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }],
        returnReason: [{ required: true, message: this.$t('orderMod.msgOrder[46]') }],
        badType: [{ required: true, message: this.$t('orderMod.pleaseEnterBadType') }],
        returnDate: [{ required: true, message: this.$t('orderMod.msgOrder[47]') }],
        comments: [{ required: true, message: this.$t('orderMod.msgOrder[32]') }]
      },
      tableData: {
        list: [],
        rules: {
          returnNum: [{ validator: this.validateReturnNum, required: true }]
        }
      },
      options: [],
      modalVisible: false,
      // 选择日期的限制
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      // 新增弹窗参数--开始
      dialogFormVisible: false,
      queryForm1: [
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          disabled: true
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          disabled: true
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        },
        {
          prop: 'deliveryNoticeNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum')
        }
      ],
      tableHeader1: [
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
            '|' +
            this.$t('orderMod.buyerOrderSynergy.lineNum'),
          prop: 'deliveryNumber',
          width: 190,
          formattor: (val, row) => {
            return val + '|' + row.lineNum
          }
        },
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 150
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          prop: 'vendorName',
          width: 150
        },
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' +
            this.$t('orderMod.buyerOrderSynergy.orderLineNum'),
          prop: 'orderNumber',
          width: 210,
          formattor: (val, row) => {
            return val + '|' + row.orderLineNum
          }
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          prop: 'materialCode',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          prop: 'materialName',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryQuantity'),
          prop: 'deliveryQuantity',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'), // 入库数量
          prop: 'warehouseQuantity',
          width: 100
        },
        {
          label: () => this.$t('orderMod.notReturnedNum'), // 可退货数量
          prop: 'notReturnedNum',
          width: 110
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          prop: 'deliveryDate',
          width: 100,
          dataType: 'dateTime'
        }
      ],
      pageSize1: 15,
      queryParam1: {},
      currentRows1: []
      // 新增弹窗参数--结束
    }
  },
  computed: {
    VIEW_PAGE () {
      return this.$attrs.params.flag !== 'add' && this.$attrs.params.flag !== 'edit'
    }
  },
  created () {
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.form.returnOrderId = this.$attrs.params.row.returnOrderId
      this.queryDetail()
    }
  },
  methods: {
    // 校验退货数量
    validateReturnNum (rule, _value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号

      if (typeof this.tableData.list[index].returnNum === 'undefined') {
        // 请输入退货数量
        callback(new Error(this.$t('orderMod.msgOrder[48]')))
      }
      if (this.tableData.list[index].returnNum < 1) {
        // 退货数量至少为 1
        callback(new Error(this.$t('returnedGoodsNotice.prompt7')))
      }

      const sign = this.tableData.list.some((row, i) => {
        return (
          index === i &&
          row.notReturnedNum < row.returnNum
        )
      })
      // 退货数量不可大于可退货数量！
      sign && callback(new Error(`${this.$t('returnedGoodsNotice.prompt8')}${index + 1}${this.$t('returnedGoodsNotice.prompt9')}`))
      callback()
    },
    // 选择供应商
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      this.tableData.list = []
    },
    // 选择业务实体
    selectHandler (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
      this.tableData.list = []
    },
    addDelivery () {
      // 根据当前已选采购组织筛选出需要发货的订单明细
      const { organizationId, vendorId } = this.form
      if (!vendorId) {
        return this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgVendor') + '！'
        })
      }
      if (!organizationId) {
        return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.orgIdTips') + '！'
        })
      }
      this.dialogFormVisible = true
      this.getQuerydata1()
    },
    getQuerydata1 (v) {
      this.queryParam1 = v || {}
      this.queryParam1.orgId = this.form.organizationId
      this.queryParam1.vendorName = this.form.vendorName
      if (this.queryParam1.orgName) {
        delete this.queryParam1.orgName
      }
      this.$nextTick(() => {
        this.$refs['deliveryList'].query()
        this.$refs['wrapper1'].setValue('orgName', this.form.organizationName)
        this.$refs['wrapper1'].setValue('vendorName', this.form.vendorName)
      })
    },
    // 选择明细
    handleCurrentChange1 (val) {
      this.currentRows1 = val
    },
    handleAdd () {
      setRepeatData(this.tableData.list, this.currentRows1, 'deliveryNoteDetailId')
      this.dialogFormVisible = false
    },
    deleteDetials (index, row) {
      this.tableData.list.splice(index, 1)
    },
    // 编辑时获取详情
    queryDetail (id = '') {
      this.$http({
        url: '/api-sup-ce/order/returnOrder/getReturnOrderById',
        method: 'GET',
        params: { returnOrderId: this.form.returnOrderId || id },
        loading: true
      }).then((res) => {
        if (res.data && res.data.returnOrder) {
          this.form = res.data.returnOrder
        }
        if (res.data && res.data.returnDetailList) {
          this.tableData.list = res.data.returnDetailList
        } else {
          this.tableData.list = []
        }
      })
    },
    // 取消新增、编辑
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('ReturnedGoodsNoticeList.getQueryData')
    },
    handleData () {
      let submitData = {}
      submitData.returnOrder = this.form
      submitData.returnDetailList = this.tableData.list.map(
        ({ deliveryNoteDetailId, returnNum }) => ({
          deliveryNoteDetailId,
          returnNum
        })
      )
      return submitData
    },
    async save () {
      const { flag, message } = await getCheckForm([this.$refs['form']])

      if (flag) {
        if (this.tableData.list.length <= 0) {
          return this.$message.warning(this.$t('orderMod.msgOrder[49]'))
        }
        this.stagingBill()
      } else {
        this.__focus_error__(message)
      }
    },
    // 暂存或提交
    async saveOrSubmit (type) {
      const { flag, message } = await getCheckForm([this.$refs['form'], this.$refs['tableForm']])

      if (flag) {
        if (this.tableData.list.length <= 0) {
          return this.$message.warning(this.$t('orderMod.msgOrder[49]'))
        }
        if (type === 'SUBMIT') {
          this.confirmBill()
        }
        // else {
        //   this.stagingBill()
        // }
      } else {
        this.__focus_error__(message)
      }
    },
    // 暂存
    stagingBill () {
      let submitData = this.handleData()
      this.$http({
        url: '/api-sup-ce/po/returnOrder/temporarySave',
        method: 'POST',
        data: submitData,
        loading: true
      }).then((res) => {
        this.$message({
          type: 'success',
          message: this.$t('orderMod.succTemporyStorage')
        })
        this.queryDetail(res.data)
      })
    },
    // 提交
    confirmBill () {
      let submitData = this.handleData()
      this.$http({
        url: '/api-sup-ce/po/returnOrder/submit',
        method: 'POST',
        data: submitData,
        loading: true
      }).then((res) => {
        this.$message({
          type: 'success',
          message: this.$t('common.successSubmit')
        })
        this.cancelBill()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-returnedGoodsNoticeDetail-buyer-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-input-number--mini {
    width: 100%;
  }
}
</style>
<style>
.el-form-item .el-input-number--mini .el-input-number__increase,
.el-form-item .el-input-number--mini .el-input-number__decrease {
  height: 24px !important;
}
</style>
