<template>
  <el-container
    class="flex-container the-statementBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          disabled
          :model="statementHead"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('purSettlementMod.basicInfo')"
              name="1"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.statementNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>

                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.vendorCode')"
                    :label-width="formLabelWidth"
                    prop="vendorCode"
                  >
                    <quick-search
                      :show-input="statementHead.vendorCode"
                      show-key="companyCode"
                      :scope-data="statementHead"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.fullPathId')"
                    :label-width="formLabelWidth"
                    prop="organizationId"
                  >
                    <organization-select-tree
                      v-model="statementHead.organizationId"
                      :scope="statementHead"
                      @select="addOrgHandle"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.paymentMethod')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.paymentMethod"
                      disabled
                    >
                      <el-option
                        v-for="item in payModeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.termOfPayment')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.termOfPayment"
                      disabled
                    >
                      <el-option
                        v-for="item in paymentTermsList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.currency')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="statementHead.currency"
                      code="currency"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="statementHead.taxKey"
                      code="tax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.receiptAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.receiptAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.returnAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.returnAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementTotalAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.statementTotalAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementStatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.statementStatus"
                      disabled
                    >
                      <el-option
                        v-for="item in statusList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementStartTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="statementHead.statementStartTime"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementEndTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="statementHead.statementEndTime"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
              </el-row>

              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.supplierNote')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.supplierNote"
                      type="textarea"
                      :rows="2"
                      :disabled="curRole !== 'VENDOR'"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.purchaserNote')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.purchaserNote"
                      type="textarea"
                      :rows="2"
                      :disabled="curRole !== 'BUYER'"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.addUploadFile')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  @click="addUploadOne"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="fileuploadList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('purSettlementMod.purchaserNote')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.receiptDetail')"
              name="3"
            >
              <el-table
                :data="receiptList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptNumber"
                  :label="$t('purSettlementMod.warehouseReceiptNumber')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptRowNum"
                  :label="$t('purSettlementMod.warehouseReceiptRowNum')"
                  width="90"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="happenDate"
                  :label="$t('purSettlementMod.happenDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('purSettlementMod.materialCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('purSettlementMod.materialName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('purSettlementMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderLineNumber"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptQuantity"
                  :label="$t('purSettlementMod.warehouseReceiptQuantity')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.returnDetail')"
              name="4"
            >
              <el-table
                :data="returnList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="returnOrderNumber"
                  :label="$t('purSettlementMod.returnOrderNumber')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="returnLineNum"
                  :label="$t('purSettlementMod.returnLineNum')"
                  width="90"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="happenDate"
                  :label="$t('purSettlementMod.happenDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('purSettlementMod.materialCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('purSettlementMod.materialName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('purSettlementMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderLineNumber"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="returnNum"
                  :label="$t('purSettlementMod.returnNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent2(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import {
  getDictItem
} from '@/api/common'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelectTree from 'lib@/components/organization-selector'

export default {
  name: 'StatementBillDetailRead',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelectTree
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      statementHead: {
        'vendorId': null,
        'vendorCode': '',
        'vendorName': '',
        'organizationId': '',
        'organizationName': '',
        'organizationCode': '',
        'currency': '',
        'taxKey': '',
        'taxRate': '',
        'statementStatus': 'CREATE',
        'statementStartTime': '',
        'statementEndTime': '',
        'paymentType': '',
        'termOfPayment': '',
        'receiptAmount': '',
        'returnAmount': '',
        'statementTotalAmount': '',
        'supplierNote': '',
        'purchaserNote': ''
      },
      payModeList: [],
      fileuploadList: [],
      receiptList: [],
      returnList: [],
      diaReturnList: [],
      receptionList3: [],
      diaReceptionList: [],
      diaReceptionList2: [],
      diaReceptionList3: [],
      activeDims: ['1', '2', '3', '4', '5'],
      rules: {
        statementNumber: [{ required: true, message: this.$('bidMod.bidMsgList[1]') }],
        templateType: [{ required: true, message: this.$('bidMod.bidMsgList[39]') }],
        stopTime: [{ required: true, message: this.$('bidMod.bidMsgList[40]') }],
        publishRange: [{ required: true, message: this.$('bidMod.bidMsgList[41]') }],
        fullPathId: [{ required: true, message: this.$('perfMod.selectOrg') }]
      },
      formLabelWidth: '120px',
      dialogFormVisible: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      isModify: false,
      statusList: [],
      pubRangeList: [],
      paymentTermsList: [],
      receplistSelection: [],
      returnlistSelection: [],
      curRole: this.$store.getters.userType
    }
  },
  created () {
      this.getFormDetail()
    // get---
    // 付款条件
    getDictItem('PAYMENT_TERMS').then(res => {
        this.paymentTermsList = adaptDictData(res.data, 'dict')
      })
    // 对账状态
    getDictItem('RECONCILIATION_STATUS').then(res => {
        this.statusList = adaptDictData(res.data, 'dict')
      })
    // 发布范围
    getDictItem('PUBLISH_RANGE').then(res => {
      this.pubRangeList = adaptDictData(res.data, 'dict')
    })
    getDictItem('PAYMENT_MODE').then(res => {
      this.payModeList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/getStatementById',
        method: 'GET',
        params: { statementHeadId: this.$attrs.params.row.statementHeadId },
        loading: true
      })
        .then(data => {
          this.statementHead = data.data.statementHead
          this.receiptList = data.data.receiptList
          this.returnList = data.data.returnList
          this.fileuploadList = data.data.fileuploadList
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.receiptList.splice(index, 1)
    },
    deleteOneContent2 (index, row) {
      this.returnList.splice(index, 1)
    },
    deleteOneContent3 (index, row) {
      this.receptionList3.splice(index, 1)
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'statementBillDetailRead' + this.$attrs.params.row.statementNumber
        )
      } else {
        this.$emit('tab-remove', 'statementBillDetailRead')
      }
      this.__setTabTodo('statementBillList.getQuerydata')
    },
    addOneContent () {
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/listStatementReceiptDTOPage',
        method: 'POST',
        data: {
          // vendorId: this.statementHead.vendorId,
          // organizationId: this.statementHead.organizationId,
        },
        loading: true
      }).then(data => {
          if (data && data.data) {
            this.diaReceptionList = data.data.list
            this.dialogFormVisible = true
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneContent2 () {
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/listStatementReturnDTOPage',
        method: 'POST',
        data: {
          // vendorId: this.statementHead.vendorId,
          // organizationId: this.statementHead.organizationId,
        },
        loading: true
      }).then(data => {
          if (data && data.data) {
            this.diaReturnList = data.data.list
            this.dialogFormVisible2 = true
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneContent3 () {
      // this.receptionList3.push({})
      this.dialogFormVisible3 = true
    },
    handleSelectionChange (selection) {
      this.receplistSelection = selection
    },
    handleSelectionChange2 (selection) {
      this.returnlistSelection = selection
    },
    // 选择组织2
    addOrgHandle (e, value, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
      // this.queryCompanyList({ orgId: e.organizationId });

      this.$http({
        url: '/api-sup/info/financeInfo/getByCompanyIdAndOrgId',
        method: 'GET',
        params: {
          orgId: e.organizationId,
          companyId: this.statementHead.vendorId
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.statementHead.taxRate = data.data.taxRate
            this.statementHead.currency = data.data.clearCurrency
            this.statementHead.paymentMethod = data.data.paymentMethod
            this.statementHead.termOfPayment = data.data.paymentTerms
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneReturnItem () {
      this.returnList = this.returnList.concat(this.returnlistSelection)
      this.dialogFormVisible2 = false
    },
    addOneReceptionItem () {
      this.receiptList = this.receiptList.concat(this.receplistSelection)
      this.dialogFormVisible = false
    },
    addUploadOne () {
      this.fileuploadList.push({
        fileuploadId: null,
        fileSourceName: ''
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.fileuploadList.splice(index, 1)
    },
    backToList () {
          this.$emit(
            'tab-remove',
            'statementBillDetailRead' + this.$attrs.params.row.statementNumber
          )
          this.__setTabTodo('statementBillList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-statementBillDetailRead-detail {
  .form-container2{padding: 5px;}
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
