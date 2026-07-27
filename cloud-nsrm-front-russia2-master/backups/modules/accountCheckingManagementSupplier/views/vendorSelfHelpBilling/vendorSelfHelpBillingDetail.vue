<template>
  <el-container
    class="the-vendorSelfHelpBillingDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 发票信息 -->
            <el-collapse-item
              :title="$t('accountMod.invoiceInfo')"
              name="1"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 采购组织 -->
                  <el-form-item
                    :label="$t('common.orgName')"
                    :label-width="formLabelWidth"
                  >
                    <!--                  <el-input v-model="form.deliveryAddress"/>-->
                    <treeselect
                      v-model="form.organizationId"
                      :normalizer="normalizer"
                      :no-children-text="$t('dataConfMod.noChildrenText')"
                      :no-options-text="$t('dataConfMod.noOptionsText')"
                      :no-results-text="$t('dataConfMod.noResultsText')"
                      :placeholder="$t('dataConfMod.msgSelectOrganation')"
                      :append-to-body="true"
                      :searchable="true"
                      :options="selectTreeOptions"
                      :multiple="false"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 发票代码 -->
                  <el-form-item
                    :label="$t('accountMod.invoiceCode')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.invoiceNo" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 单据状态 -->
                  <el-form-item
                    :label="$t('bidMod.billstatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.billStatus" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 汇率 -->
                  <el-form-item
                    :label="$t('bid_mod.priceTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="form.interest" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 发票不含税金额 -->
                  <el-form-item
                    :label="$t('accountMod.invoiceAmountNoTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.invoiceNotaxAmount" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 税率 -->
                  <el-form-item
                    :label="$t('bid_mod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <DictSelect
                      v-model="form.taxRate"
                      code="tax"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 税额 -->
                  <el-form-item
                    :label="$t('contractMod.taxQuota')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.taxAmount" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 发票含税金额 -->
                  <el-form-item
                    :label="$t('accountMod.invoiceAmountTax')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.invoiceTaxAmount" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 扣罚金额 -->
                  <el-form-item
                    :label="$t('accountMod.deductionAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.penaltyAmount" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 币种 -->
                  <el-form-item
                    :label="$t('vendorMod.currencyCode')"
                    :label-width="formLabelWidth"
                  >
                    <DictSelect
                      v-model="form.currency"
                      code="currency"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 来源单号 -->
                  <el-form-item
                    :label="$t('contractMod.sourceNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.sourceNumber" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <!-- 备注 -->
                  <el-form-item
                    :label="$t('common.remark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.contractComments"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 发票明细 -->
            <el-collapse-item
              :title="$t('accountMod.invoiceDetail1')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addOne"
                >
                  {{
                    $t("common.add")
                  }}
                </el-button>
              </p>
              <el-table
                :data="tableData"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <quick-search
                      :show-input="scope.row.itemCode"
                      show-key="itemCode"
                      :scope-data="scope.row"
                      name="scc_base_material_item_display"
                      @close-quicksearch="getItemObj"
                    />
                  </template>
                </el-table-column>
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('common.materialName')"
                  min-width="150"
                />
                <!-- 单位 -->
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('bid_mod.unit')"
                  width="100"
                />
                <!-- 数量 -->
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('bid_mod.quantity')"
                  width="100"
                />
                <!-- 单价 -->
                <el-table-column
                  align="center"
                  prop="itemPrice"
                  :label="$t('accountMod.unitPrice')"
                  width="100"
                />
                <!-- 商品总金额 -->
                <el-table-column
                  align="center"
                  prop="totalAmount"
                  :label="$t('accountMod.goodsTotalAmount')"
                  width="100"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 扣罚明细 -->
            <el-collapse-item
              :title="$t('accountMod.deductionOfFines')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addOne2"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="tableData2"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 扣罚单编号 -->
                <el-table-column
                  align="center"
                  prop="deductionNo"
                  :label="$t('accountMod.deductionDocNum1')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.deductionNo" />
                  </template>
                </el-table-column>
                <!-- 扣罚款日期 -->
                <el-table-column
                  align="center"
                  prop="deductionDate"
                  :label="$t('accountMod.deductionDate')"
                  width="160"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.deductionDate"
                      type="date"
                    />
                  </template>
                </el-table-column>
                <!-- 扣罚款类型 -->
                <el-table-column
                  align="center"
                  prop="deductionType"
                  :label="$t('accountMod.deductionType')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.deductionType" />
                  </template>
                </el-table-column>
                <!-- 扣罚说明 -->
                <el-table-column
                  align="center"
                  prop="deductionDesc"
                  :label="$t('accountMod.deductionDesc')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.deductionDesc" />
                  </template>
                </el-table-column>
                <!-- 扣罚金额 -->
                <el-table-column
                  align="center"
                  prop="deductionAmount"
                  :label="$t('accountMod.deductionAmount')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.deductionAmount" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent2(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 税控机发票 -->
            <el-collapse-item
              :title="$t('purSettlementMod.invoiceTaxControls')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addOne3"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="tableData3"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 税控发票号 -->
                <el-table-column
                  align="center"
                  prop="invoiceLineNo"
                  :label="$t('accountMod.taxControlInvoiceNum')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.invoiceLineNo" />
                  </template>
                </el-table-column>
                <!-- 开票日期 -->
                <el-table-column
                  align="center"
                  prop="invoiceDate"
                  :label="$t('purSettlementMod.invoiceDate')"
                  width="160"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.invoiceDate"
                      type="date"
                    />
                  </template>
                </el-table-column>
                <!-- 不含税金额 -->
                <el-table-column
                  align="center"
                  prop="notaxAmount"
                  :label="$t('contractMod.excludeTaxPayAmount')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.notaxAmount" />
                  </template>
                </el-table-column>
                <!-- 税额 -->
                <el-table-column
                  align="center"
                  prop="taxAmount"
                  :label="$t('contractMod.taxQuota')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.taxAmount" />
                  </template>
                </el-table-column>
                <!-- 含税金额 -->
                <el-table-column
                  align="center"
                  prop="taxAllAmount"
                  :label="$t('contractMod.amount2')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.taxAllAmount" />
                  </template>
                </el-table-column>
                <!-- 购方名称 -->
                <el-table-column
                  align="center"
                  prop="buyerName"
                  :label="$t('purSettlementMod.buyerName')"
                  min-width="150"
                />
                <!-- 销方名称 -->
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('purSettlementMod.sellerName')"
                  min-width="150"
                />
                <!-- 附件上传 -->
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('bidMod.attachmentUpload')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.docId,
                        fileName: scope.row.fileName
                      }"
                      :readonly="false"
                      @on-change="({file}) => innerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent3(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>

      <c-toolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="saveOne"
          >
            {{
              $t("common.staging")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="submitOne"
          >
            {{
              $t("common.submit")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="backTo"
          >
            {{
              $t("accountMod.review")
            }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import {
  getDictItem,
  getRegion
} from '@/api/common'

export default {
  name: 'VendorSelfHelpBillingDetail',
  components: {
    MainHeader,
    Treeselect,
    QuickSearch,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorAccountCheckingSynergy', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      selectTreeOptions: [],
      parambiddNum: '',
      form: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      },
      form2: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      tableData: [],
      tableData2: [],
      tableData3: [],
      isModify: false,
      activeDims: ['1', '2', '3', '4']
    }
  },
  created () {
    // 组织架构
    this.$api.accountAccess.organaztionTreehttp({}).then(res => {
      this.selectTreeOptions = res.data
    })
    if (this.$attrs.params.flag == 'edit') {
      this.parambiddNum = this.$attrs.params.row.poInvNum
    }
  },
  mounted () {},
  methods: {
    addOne () {
      this.tableData.push({
        itemCode: '',
        itemName: '',
        unit: '',
        quantity: '',
        itemPrice: '',
        totalAmount: ''
      })
    },
    addOne2 () {
      this.tableData2.push({
        deductionNo: '',
        deductionDate: '',
        deductionType: '',
        deductionDesc: '',
        deductionAmount: ''
      })
    },
    addOne3 () {
      this.tableData3.push({
        invoiceLineNo: '',
        invoiceDate: '',
        notaxAmount: '',
        taxAmount: ''
      })
    },
    deleteOneContent (index, row) {
      this.tableData.splice(index, 1)
    },
    deleteOneContent2 (index, row) {
      this.tableData2.splice(index, 1)
    },
    deleteOneContent3 (index, row) {
      this.tableData3.splice(index, 1)
    },
    getItemObj (val, scope) {
      scope.itemId = val ? val.materialId : ''
      scope.itemCode = val ? val.materialCode : ''
      scope.itemName = val ? val.materialName : ''
      scope.unit = val ? val.unit : ''
    },
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.docId = fileId.toString()
      row.fileName = fileName
    },
    normalizer (node) {
      return {
        id: node.organizationId,
        label: node.organizationName,
        children: node.childOrganRelation
      }
    },
    editOne () {},
    confirmBill () {},
    cancelBill () {},
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    prevOne () {},
    nextOne () {},
    saveOne () {},
    submitOne () {},
    backTo () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'vendorSelfHelpBillingDetail' + this.$attrs.params.row.poInvNum
        )
      } else {
        this.$emit('tab-remove', 'vendorSelfHelpBillingDetail')
      }
      // this.__setTabTodo('inquiryOrdersList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorSelfHelpBillingDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
.btn_line {
  margin: 0 0 10px 0;
}
</style>
