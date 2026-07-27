<template>
  <el-container
    class="flex-container-notab the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-collapse-item :title="$t('supRisk.baseInfo')" name="1">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            :label-width="formLabelWidth"
            label-position="top"
            class="form-incontainer"
            :disabled="flag === 'view'"
          >
            <srm-row :gutter="32">
              <srm-col :span="6">
                <el-form-item
                  :label="$t('orderMod.warehouseReceiptNo')"
                  prop="warehouseReceiptNumber"
                >
                  <el-input v-model="form.warehouseReceiptNumber" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item :label="$t('common.vendor')" prop="vendorName">
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info_all"
                    :disabled="isFormOnlyRead"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorCode')">
                  <el-input v-model="form.vendorCode" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item :label="$t('purchaseDemand.businessEntity')" prop="orgId">
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    :jump-login="true"
                    :placeholder="$t('common.pleaseSelect')"
                    :parent-id="-1"
                    node-type="OU"
                    :scope="form"
                    :disabled="isFormOnlyRead"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 库存组织 -->
                <el-form-item :label="$t('purchaseDemand.invOrg')" prop="organizationId">
                  <OrganizationSelector
                    ref="organizationSelector2"
                    v-model="form.organizationId"
                    :parent-id="form.orgId"
                    node-type="INV"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="isFormOnlyRead"
                    @select="selectHandler2"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item :label="$t('oneStopShopping.receiveAddress')" prop="receiveAddress">
                  <DictSelect
                    v-model="form.receiveAddress"
                    :code="form.organizationId"
                    :custom-select-type="form.organizationId ? 'RECEIVE_ADDRESS' : ''"
                    @change-value="(val, element) => changeSiteInfo(form, element)"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item :label="$t('orderMod.warehouseDate')" prop="warehouseDate">
                  <el-date-picker
                    v-model="form.warehouseDate"
                    :picker-options="pickerOptions"
                    value-format="yyyy-MM-dd"
                    type="date"
                    :placeholder="$t('purchaseDemand.datePicker')"
                  />
                </el-form-item>
              </srm-col>
              <template v-if="flag != 'add'" type="flex">
                <srm-col>
                  <el-form-item :label="$t('common.status')">
                    <DictSelect
                      v-model="form.warehouseReceiptStatus"
                      code="WAREHOUSE_RECEIPT_STATUS"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('common.creator')" prop="createdUserName">
                    <el-input v-model="form.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('common.creationTime')" prop="creationDate">
                    <el-date-picker v-model="form.creationDate" disabled type="date" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.lastUpdateBy')"
                    prop="lastUpdatedUserName"
                  >
                    <el-input v-model="form.lastUpdatedUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.lastUpdateDate')" prop="lastUpdateDate">
                    <el-date-picker v-model="form.lastUpdateDate" disabled type="date" />
                  </el-form-item>
                </srm-col>
              </template>
              <srm-col :initCol="1">
                <el-form-item :label="$t('common.remark')" prop="comments">
                  <el-input v-model="form.comments" type="textarea" :rows="2" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item :title="$t('orderMod.inboundDetail')" name="2">
          <p v-if="flag != 'view'" class="btn_line">
            <el-button type="primary" class="detail-pbtn" @click="addOneStorageDetail">
              {{
                $t('common.add')
              }}
            </el-button>
          </p>
          <el-form
            ref="tableForm"
            :model="tableData"
            :rules="tableData.rules"
            :disabled="flag == 'view'"
          >
            <el-table :data="tableData.list" style="width: 100%" border height="222px">
              <el-table-column align="center" :label="$t('common.sort')" type="index" width="80" />
              <el-table-column
                align="center"
                prop="warehouseReceiptRowNum"
                :label="$t('purchaseDemand.lineNum')"
                width="60"
              />
              <el-table-column
                align="center"
                :label="
                  $t('orderMod.buyerOrderSynergy.deliveryNumber') +
                    '|' +
                    $t('purchaseDemand.lineNum')
                "
                width="160"
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
                prop="notWarehouseQuantity"
                :label="$t('orderMod.abledWarehouseNum')"
                width="120"
              />
              <el-table-column
                align="center"
                prop="warehouseQuantity"
                :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
                width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :rules="tableData.rules.warehouseQuantity"
                    :prop="`list.${scope.$index}.warehouseQuantity`"
                  >
                    <el-input-number
                      v-model="scope.row.warehouseQuantity"
                      :min="1"
                      :max="flag !== 'view' ? scope.row.notWarehouseQuantity : undefined"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                v-if="flag != 'view'"
                :label="$t('common.operation')"
                width="60"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="delOneStorage(scope.$index, scope.row)">
                    {{
                      $t('common.delete')
                    }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ flag !== 'view' ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button v-if="flag !== 'view'" type="primary" @click="saveBillHandle('SAVE')">
            {{ $t('common.staging') }}
          </el-button>
          <el-button v-if="flag !== 'view'" type="primary" @click="saveBillHandle('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
      <!-- 新增入库明细弹窗 -->
      <srm-dialog
        :title="$t('orderMod.selWarehousingDetail')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        class="delivery-dialog"
      >
        <FormWrapper
          ref="wrapper1"
          :form-array="queryForm1"
          form-label-width="120px"
          @getFormData="getQuerydata1"
        />

        <MainHeader :l-span="22" :r-span="2">
          <template slot="left">
            <el-button type="primary" :disabled="!currentRows1.length" @click="handleAdd">
              {{
                $t('common.affirm')
              }}
            </el-button>
            <el-button type="primary" @click="dialogFormVisible = false">
              {{
                $t('common.cancel')
              }}
            </el-button>
          </template>
        </MainHeader>
        <TableView
          ref="storageDetailList"
          :table-header="tableHeader1"
          :check-change="handleCurrentChange1"
          :page-size="pageSize1"
          :checkbox="true"
          :pre-query-data="queryParam1"
          url="/api-sup-ce/po/deliveryNoteDetail/listInWarehouseReceipt"
          :reserve-selection="true"
          row-key="deliveryNoteDetailId"
          :rowDblclick="rowDblclick"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { setRepeatData, getCheckForm } from 'lib@/utils/util'

export default {
  name: 'OrderStorageDetail',
  components: {
    TableView,
    FormWrapper,
    MainHeader,
    CToolbar,
    OrganizationSelector,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2'],
      flag: this.$attrs.params.flag,
      form: {
        warehouseReceiptId: '',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        receiveAddress: null,
        warehouseDate: parseTime(new Date(), '{y}-{m}-{d}'),
        comments: '',
        warehouseReceiptStatus: '',
        createdBy: '',
        creationDate: '',
        lastUpdatedBy: '',
        lastUpdateDate: ''
      },
      tableData: {
        list: [],
        rules: {
          warehouseQuantity: [{ required: true, message: this.$t('orderMod.msgOrder[39]') }]
        }
      },
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }],
        organizationId: [{ required: true, message: this.$t('purchaseDemand.organizationIdTips') }], // 请选择库存组织
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        receiveAddress: [{ required: true, message: this.$t('orderMod.msgOrder[25]') }],
        warehouseDate: [{ required: true, message: this.$t('orderMod.msgOrder[41]') }]
      },
      formLabelWidth: '120px',
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
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        }
      ],
      tableHeader1: [
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
            '|' +
            this.$t('purchaseDemand.lineNum'),
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
            this.$t('purchaseDemand.lineNum'),
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
          label: () => this.$t('orderMod.abledWarehouseNum'),
          prop: 'notWarehouseQuantity',
          width: 120
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          prop: 'deliveryDate',
          width: 100
        }
      ],
      pageSize1: 15,
      queryParam1: {},
      currentRows1: []
      // 新增弹窗参数--结束
    }
  },
  computed: {
    // 判断明细行是否有数据控制表单必填项只读
    isFormOnlyRead () {
      return this.tableData.list.length > 0
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    const dictionaryCodes = [{ dictCode: 'WAREHOUSE_RECEIPT_STATUS' }]
    if (flag != 'add') {
      this.form.warehouseReceiptId = row.warehouseReceiptId
      this.queryDetail(row.warehouseReceiptId)
    }
  },
  mounted () {
  },
  methods: {
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element ? element.receiver : null)
      this.$set(row, 'receiveTelephone', element ? element.receiverPhone : null)
      this.$set(row, 'receiveAddress', element ? element.siteName : null)
    },
    // 选择业务实体
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      this.tableData.list = []

      if (this.form.organizationId) {
        this.form.organizationId = null
        this.form.organizationCode = null
        this.form.organizationName = null
      }
    },
    // 库存组织
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : ''
      this.form.organizationCode = node ? node.organizationCode : ''
      this.form.organizationName = node ? node.organizationName : ''
    },
    // 选择供应商
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      this.tableData.list = []
    },
    // 新增入库明细信息
    addOneStorageDetail () {
      if (!this.form.vendorName || !this.form.orgId || !this.form.organizationId) {
        this.$message.warning(this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg2'))
        return
      }
      this.dialogFormVisible = true
      this.getQuerydata1()
    },
    // 查询入库明细列表
    getQuerydata1 (v) {
      this.queryParam1 = v || {}
      this.queryParam1.orgId = this.form.orgId
      this.queryParam1.organizationId = this.form.organizationId
      this.queryParam1.vendorName = this.form.vendorName
      if (this.queryParam1.orgName) {
        delete this.queryParam1.orgName
      }
      this.$nextTick(() => {
        this.$refs['storageDetailList'].query()
        this.$refs['wrapper1'].setValue('orgName', this.form.orgName)
        this.$refs['wrapper1'].setValue('vendorName', this.form.vendorName)
      })
    },
    // 选择送货明细
    handleCurrentChange1 (val) {
      this.currentRows1 = val
    },
    // 双击确认添加明细
    rowDblclick (row, event, column) {
      this.currentRows1 = [row]
      this.handleAdd()
    },
    // 新增
    handleAdd () {
      setRepeatData(this.tableData.list, this.currentRows1, 'deliveryNoteDetailId', row => {
        return {
          warehouseReceiptRowNum: '',
          warehouseQuantity: row.notWarehouseQuantity
        }
      })
      this.dialogFormVisible = false
    },
    delOneStorage (index, row) {
      this.tableData.list.splice(index, 1)
    },
    // 编辑、查看单据时获取详情
    async queryDetail (id) {
      await this.$http({
        url: '/api-sup-ce/po/warehouseReceipt/detail',
        method: 'GET',
        params: { id },
        loading: true
      }).then((res) => {
        if (res.data && res.data.warehouseReceipt) {
          this.form = Object.assign(this.form, res.data.warehouseReceipt)
        }
        if (res.data && res.data.warehouseReceiptDetailList) {
          this.tableData.list = res.data.warehouseReceiptDetailList
        } else {
          this.tableData.list = []
        }
      })
    },
    handleData () {
      let submitData = {}
      submitData.warehouseReceipt = this.form
      submitData.warehouseReceiptDetailList = this.tableData.list
      return submitData
    },
    async saveBillHandle (type) {
      const { flag, message } = await getCheckForm([this.$refs['form'], this.$refs['tableForm']])

      if (flag) {
        if (this.tableData.list.length <= 0) {
          return this.$message.warning(this.$t('orderMod.msgOrder[43]'))
        }
        if (type === 'SUBMIT') {
          this.confirmBill()
        } else {
          this.stagingBill()
        }
      } else {
        this.__focus_error__(message)
      }
    },
    // 暂存
    stagingBill () {
      let submitData = this.handleData()
      this.$http({
        url: '/api-sup-ce/po/warehouseReceipt/temporarySave',
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
    confirmBill () {
      let submitData = this.handleData()
      this.$http({
        url: '/api-sup-ce/po/warehouseReceipt/submit',
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
    },
    // 取消
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'orderStorageDetail')
      } else {
        this.$emit('tab-remove', 'orderStorageDetail' + this.$attrs.params.row.warehouseReceiptId)
      }
      this.__setTabTodo('OrderStorageListBuyer.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorGreenChannelDetail-detail {
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
  .delivery-dialog {
    :deep(.el-table__body-wrapper) {
      height: 200px !important;
    }
  }
}
</style>
<style>
.el-form-item .el-input-number--mini .el-input-number__increase,
.el-form-item .el-input-number--mini .el-input-number__decrease {
  height: 24px !important;
}
</style>
