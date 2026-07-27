<template>
  <el-container
    class="the-vendorSelfHelpBillingDetail-detail"
    direction="vertical"
  >
    <el-main>
      <main-header>
        <template
          slot="left"
        >
          <h2>
            {{ $t("accountMod.invoiceDetail") + parambiddNum }}
          </h2>
        </template>
      </main-header>
      <div class="form-container">
        <el-steps :active="1">
          <!-- 新建发票 -->
          <el-step :title="$t('accountMod.newInvoice')" />
          <!-- 已提交 -->
          <el-step :title="$t('accountMod.submitted')" />
          <!-- 已审核 -->
          <el-step :title="$t('accountMod.audited')" />
        </el-steps>
      </div>
      <div class="form-container">
        <p class="sub_header">
          <span>{{ $t("accountMod.searchCodition") }}</span>
        </p>
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
        >
          <el-row type="flex">
            <el-col>
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
            <el-col>
              <!-- 供货方 -->
              <el-form-item
                :label="$t('accountMod.vendor')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.billNum" />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 开票类型 -->
              <el-form-item
                :label="$t('accountMod.invoiceType1')"
                :label-width="formLabelWidth"
              >
                <el-select v-model="form.billNum" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 币种 -->
              <el-form-item
                :label="$t('bid_mod.currencyName')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="form.currency"
                  code="currency"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 时间范围 -->
              <el-form-item
                :label="this.$t('accountMod.timeLimit')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.deliveryAddress" />
              </el-form-item>
            </el-col>
            <el-col />
          </el-row>
        </el-form>
      </div>

      <div class="form-container">
        <p class="sub_header">
          <!-- 发票明细 -->
          <span>{{ $t("accountMod.invoiceDetail1") }}</span>
          <span
            style="padding: 0 11px;font-weight: bolder;"
          >{{ $t("accountMod.excludeTaxSummary") }}520.00</span>
        </p>
        <el-tabs
          v-model="editableTabsValue"
          type="border-card"
          @tab-click="handleClick"
        >
          <!-- 发票明细 -->
          <el-tab-pane
            :label="$t('accountMod.invoiceDetail1')"
            name="billListTab"
          >
            <el-table
              :data="tableData"
              style="width: 100%"
              border
              height="222px"
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <!-- 单据类型 -->
              <el-table-column
                align="center"
                prop="billType"
                :label="$t('bidMod.billType')"
                width="100"
              />
              <!-- 单据编码 -->
              <el-table-column
                align="center"
                prop="billCode"
                :label="$t('bidMod.billCode')"
                width="100"
              />
              <!-- 业务日期 -->
              <el-table-column
                align="center"
                prop="billDate"
                :label="$t('accountMod.businessDate')"
                width="100"
              />
              <!-- 订单编号 -->
              <el-table-column
                align="center"
                prop="billNum"
                :label="$t('logisticsMod.orderNum')"
                width="100"
              />
              <!-- 物料编码 -->
              <el-table-column
                align="center"
                prop="itemCode"
                :label="$t('common.materialCode')"
                width="100"
              />
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="itemName"
                :label="$t('common.materialName')"
                width="100"
              />
              <!-- 单位 -->
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bid_mod.unit')"
                width="100"
              />
              <!-- 单价 -->
              <el-table-column
                align="center"
                prop="price"
                :label="$t('accountMod.unitPrice')"
                width="100"
              />
              <!-- 未开票数量 -->
              <el-table-column
                align="center"
                prop="notSettleAmount"
                :label="$t('accountMod.uninvoicedQuantity')"
                width="100"
              />
              <!-- 金额 -->
              <el-table-column
                align="center"
                prop="billAmount"
                :label="$t('orderMod.buyerOrderSynergy.amount')"
                width="100"
              />
            </el-table>
          </el-tab-pane>
          <!-- 物料汇总 -->
          <el-tab-pane
            :label="$t('accountMod.materialSummary')"
            name="itemListTab"
          >
            <el-table
              :data="tableData2"
              style="width: 100%"
              border
              height="222px"
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
                width="100"
              />
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="itemName"
                :label="$t('common.materialName')"
                width="100"
              />
              <!-- 单位 -->
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bid_mod.unit')"
                width="100"
              />
              <!-- 未开票数量 -->
              <el-table-column
                align="center"
                prop="notSettleAmount"
                :label="$t('accountMod.uninvoicedQuantity')"
                width="100"
              />
              <!-- 不含税单价 -->
              <el-table-column
                align="center"
                prop="price1"
                :label="$t('contractMod.notaxPrice')"
                width="100"
              />
              <!-- 不含税金额 -->
              <el-table-column
                align="center"
                prop="price2"
                :label="$t('contractMod.excludeTaxPayAmount')"
                width="100"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>

      <c-toolbar>
        <template slot="right">
          <el-button

            @click="backTo"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
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
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'VendorSelfHelpBillingDetail',
  components: {
    MainHeader,
    Treeselect,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
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
      tableData: [{}],
      tableData2: [{}],
      isModify: false,
      editableTabsValue: 'billListTab'
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
    },
    handleClick (tab, event) {
      console.log(tab)
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
</style>
