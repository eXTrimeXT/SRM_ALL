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
            :disabled="true"
            class="form-incontainer"
          >
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')">
                  <el-input
                    v-model="form.returnOrderNumber"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('common.vendor')"
                  prop="vendorName"
                >
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info5"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorCode')">
                  <el-input
                    v-model="form.vendorCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('bid_mod.businessEntity')"
                  prop="organizationId"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.organizationId"
                    :jump-login="true"
                    :parent-id="-1"
                    node-type="OU"
                    :scope="form"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.returnDate')"
                  prop="returnDate"
                >
                  <el-date-picker
                    v-model="form.returnDate"
                    type="date"
                    :picker-options="pickerOptions"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('bidMod.datePicker')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.status')">
                  <DictSelect
                    v-model="form.returnStatus"
                    code="RETURN_ORDER_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
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
                    type="date"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('orderMod.returnReason')"
                  prop="returnReason"
                >
                  <el-input
                    v-model="form.returnReason"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('common.remark')"
                  prop="comments"
                >
                  <el-input
                    v-model="form.comments"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.returnDetail')"
          name="2"
        >
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
                    this.$t('purchaseDemand.lineNum')
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
                  $t('orderMod.buyerOrderSynergy.orderNumber') +
                    '|' +
                    this.$t('purchaseDemand.lineNum')
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
                      :disabled="true"
                      :min="1"
                      :max="scope.row.warehouseQuantity"
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
                    type="text"
                    :disabled="true"
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
            {{ $t('common.close') }}
          </el-button>
        </template>
      </CToolbar>
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
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'

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
        returnOrderNumber: null,
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        organizationId: null,
        organizationCode: '',
        organizationName: '',
        returnReason: '',
        returnDate: parseTime(new Date(), '{y}-{m}-{d}'),
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
        returnDate: [{ required: true, message: this.$t('orderMod.msgOrder[47]') }],
        comments: [{ required: true, message: this.$t('orderMod.msgOrder[32]') }]
      },
      tableData: {
        list: [],
        rules: {
          returnNum: [{ required: true, message: this.$t('orderMod.msgOrder[48]') }]
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
      }
    }
  },
  created () {
    this.form.returnOrderId = this.$attrs.params.row.returnOrderId
    this.queryDetail()
  },
  methods: {
    // 取消新增、编辑
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('returnedGoodsNoticeList.getQueryData')
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
    deleteDetials (index, row) {
      this.tableData.list.splice(index, 1)
    },
    // 编辑时获取详情
    queryDetail () {
      this.$http({
        url: '/api-sup-ce/order/returnOrder/getReturnOrderById',
        method: 'GET',
        params: { returnOrderId: this.form.returnOrderId },
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
