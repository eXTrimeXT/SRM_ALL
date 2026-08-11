<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.appointDeliveryFormList')"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            label-width="80px"
            label-position="top"
            :disabled="!isDisabled"
            class="form-incontainer"
          >
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.deliveryNumber" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.deliveryDate2')"
                  :label-width="formLabelWidth"
                  prop="deliveryDate"
                >
                  <el-date-picker
                    v-model="form.deliveryDate"
                    type="date"
                    :format="$formatDatePicker"
                    :placeholder="$t('bidMod.datePicker')"
                    :picker-options="pickerOptions"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.vendorCode')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.vendorCode" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.vendorName')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.vendorName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.businessEntity')"
                  :label-width="formLabelWidth"
                  prop="orgId"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :limit="false"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('dataConfMod.organizationId')"
                  :label-width="formLabelWidth"
                  prop="organizationId"
                >
                  <OrganizationSelector
                    ref="organizationSelector2"
                    v-model="form.organizationId"
                    :parent-id="form.orgId"
                    node-type="INV"
                    :limit="false"
                    :placeholder="$t('common.pleaseSelect')"
                    @select="selectHandler2"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveAddress')"
                  :label-width="formLabelWidth"
                  prop="receivedFactory"
                >
                  <DictSelect
                    v-model="form.receivedFactory"
                    :code="form.organizationId"
                    :custom-select-type="form.organizationId ? 'RECEIVE_ADDRESS' : ''"
                    @change-value="(val, element) => changeSiteInfo(form, element)"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.billstatus')" :label-width="formLabelWidth">
                  <DictSelect
                    v-model="form.deliveryNoteStatus"
                    code="DELIVERY_NOTE_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 订单来源 -->
              <srm-col>
                <el-form-item :label="$t('orderMod.orderSource')" :label-width="formLabelWidth" prop="orderSource">
                  <el-select v-model="form.orderSource" disabled>
                    <el-option
                      v-for="item in orderSourceList"
                      :key="item.label"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.comments')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.comments" type="textarea" :rows="2" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.vendorDeliveryList')"
          name="2"
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
            <el-table-column
              align="center"
              prop="lineNum"
              :label="$t('orderMod.buyerOrderSynergy.deliveryLineNum')"
              width="120"
            />
            <el-table-column
              align="center"
              prop="orderNumber"
              :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
              :show-overflow-tooltip="true"
              width="140"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.orderNumber" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="categoryName"
              :label="$t('orderMod.buyerOrderSynergy.categoryName')"
              width="150"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.categoryName" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="materialCode"
              :label="$t('orderMod.buyerOrderSynergy.materialCode')"
              :show-overflow-tooltip="true"
              width="150"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.materialCode" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="materialName"
              :show-overflow-tooltip="true"
              :label="$t('orderMod.buyerOrderSynergy.materialName')"
              min-width="150"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.materialName" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="orderNum"
              :label="$t('orderMod.buyerOrderSynergy.orderNum')"
              width="100"
            >
              <template slot-scope="scope">
                <span
                  type="number"
                  v-html="scope.row.orderNum"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="receiveSum"
              :label="$t('orderMod.buyerOrderSynergy.receiveSum')"
              width="100"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.receiveSum" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="deliveryQuantity"
              :label="$t('orderMod.buyerOrderSynergy.deliveryQuantity')"
              width="100"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.deliveryQuantity" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="unit"
              :label="$t('orderMod.buyerOrderSynergy.unit')"
              width="70"
            >
              <template slot-scope="scope">
                <span>{{ getLabel('unitList', scope.row.unit) }}</span>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="inventoryPlace"
              :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
              :show-overflow-tooltip="true"
              width="100"
            >
              <template slot-scope="scope">
                <span v-html="scope.row.inventoryPlace" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="requirementDate"
              :label="$t('orderMod.buyerOrderSynergy.requirementDateStr')"
              width="160"
            >
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.requirementDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="saveAndGoTagHandel">
            {{ $t('orderMod.goTag') }}
          </el-button>
          <!-- <el-button
            v-if="isAble == false"
            type="primary"
            @click="confirmBill"
          >
            {{ $t('common.confirm') }}
          </el-button> -->
          <el-button v-if="form.deliveryNoteStatus === 'DELIVERED'" type="primary" @click="printBill">
            {{ $t('route.pdfPrint') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { getAllPurUnit, getDictItemList } from '@/api/common'
import OrganizationSelector from 'lib@/components/organization-selector'
import { deliveryOrderApi } from 'modb@/orderManagementBuyer/api'
import tagManage from 'mods@/orderManagementSupplier/views/vendorDeliveryOrderEngine/tagManage.vue'

export default {
  name: 'BuyerDeliveryOrderDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      orderSourceList: [
        {
          value: 'PURCHASE_ORDER',
          label: this.$t('route.buyerPurchaseOrder')
        },
        // {
        //   value: 'ARRIVAL_PLAN',
        //   label: this.$t('orderMod.arrivalPlanOrder')
        // },
        {
          value: 'DELIVERY_NOTICE',
          label: this.$t('orderMod.arrivalNotice')
        }
      ],
      selectionDetails: [],
      isAble: false,
      selectTreeOptions: [],
      unitList: [],
      activeDims: ['1', '2'],
      form: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        purchaseConfirmStatus: '',
        remark: '',
        adjustReason: ''
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      tableData: [],
      isModify: false,
      ableSelectTreeNodes: [],
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
    const { flag, row } = this.$attrs.params
    if (flag !== 'add') {
      this.form = row
      if (this.form.purchaseConfirmStatus === 'Y') {
        this.isAble = true
      }
      this.queryList()
    }
  },
  mounted () {
    // 获取所有单位
    getAllPurUnit().then((res) => {
      this.unitList = adaptDictData(res.data, 'unit')
    })
  },
  methods: {
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element.receiver)
      this.$set(row, 'receiveTelephone', element.receiverPhone)
      this.$set(row, 'receivedFactory', element.siteName)
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null

      if (!this.form.organizationId) return
      // 清空库存组织
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    getLabel (dictionary, val) {
      const dict = this[dictionary] || []
      const labelOpt = dict.find((i) => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    queryList () {
      const { deliveryNoteId } = this.$attrs.params.row
      deliveryOrderApi.deliveryNoteDetailListPage({ deliveryNoteId }).then((res) => {
        this.tableData = res.data.list
      })
    },
    confirmBill () {
      /* if (this.$attrs.params.flag == "add") {
          // const data = {
          //   deliveryNote: this.form,
          //   detailList: this.tableData,
          // };
          // deliveryNoteSave(data).then(res => {
          //   this.$message({
          //     type: 'success',
          //     message: res.message,
          //   });
          // });
          this.$emit("tab-remove", "buyerDeliveryOrderDetail");
        } else {
          // const data = {
          //   deliveryNote: this.form,
          //   detailList: this.tableData,
          // };
          // deliveryNoteUpdate(data).then(res => {
          //   this.$message({
          //     type: 'success',
          //     message: res.message,
          //   });
          // });
          this.$emit(
            "tab-remove",
            "buyerDeliveryOrderDetail" + this.form.deliveryNumber
          );
        } */

      this.$http({
        url: '/api-sup-ce/order/deliveryNote/confirmOrderStatus',
        method: 'GET',
        params: { deliveryNoteId: this.$attrs.params.row.deliveryNoteId },
        loading: true
      }).then(_ => {
        this.cancelBill()
      })
    },
    printBill () {
      const xml = encodeURIComponent('database:database:送货单打印.ureport.xml')
      const params = encodeURIComponent(`param=${this.form.deliveryNumber}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('buyerDeliveryOrderList.getQuerydata')
    },
    // 保存并保存条码
    async saveAndGoTagHandel () {
      this.goTagManage(this.form) // 跳转绑定条码
    },
    goTagManage (row) {
      let params = {
        deliveryNumber: row.deliveryNumber,
        deliveryNoteId: row.deliveryNoteId,
        deliveryNoteStatus: row.deliveryNoteStatus
      }
      let name = params.deliveryNumber ?? ''
      let tab = {
        component: tagManage,
        params: {
          status: row.deliveryNoteStatus || 'CREATE',
          row: row || '',
          tabName: name ? 'tagManage' + name : 'tagManage'
        },
        title: this.$t('orderMod.buyerOrderSynergy.tagManage') + name,
        name: name ? 'tagManage' + name : 'tagManage'
      }
      this.$emit('tab-add', tab)
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
}
</style>
