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
          :title="$t('supRisk.baseInfo')"
          name="1"
        >
          <el-form
            ref="deliveryAppoint"
            :model="formData"
            :disabled="true"
            :label-width="formLabelWidth"
            label-position="top"
            class="form-incontainer"
          >
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.vendorName')"
                  prop="vendorName"
                >
                  <el-input
                    v-model="formData.vendorName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.businessEntity')"
                  prop="orgId"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="formData.orgId"
                    :jump-login="true"
                    :parent-id="-1"
                    node-type="OU"
                    :scope="formData"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 库存组织 -->
                <el-form-item :label="$t('purchaseDemand.invOrg')" prop="organizationId">
                  <OrganizationSelector
                    ref="organizationSelector2"
                    v-model="formData.organizationId"
                    :parent-id="formData.orgId"
                    node-type="INV"
                    :placeholder="$t('common.pleaseSelect')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveAddress')"
                  prop="receiveAddress"
                >
                  <DictSelect
                    v-model="formData.receiveAddress"
                    :code="formData.orgId"
                    :custom-select-type="formData.orgId ? 'RECEIVE_ADDRESS' : ''"
                    @change-value="(val, element) => changeSiteInfo(formData, element)"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.respondents')"
                  prop="respondents"
                >
                  <QuickSearch
                    :show-input="formData.respondents"
                    show-key="nickname"
                    :scope-data="formData"
                    name="scc_rbac_user_display"
                    @close-quicksearch="getUserObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.respondentsNo')"
                  prop="respondentsNo"
                >
                  <el-input
                    v-model="formData.respondentsNo"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.respondentsPhone')"
                  prop="respondentsPhone"
                >
                  <el-input
                    v-model="formData.respondentsPhone"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.respondentsGound')"
                  prop="respondentsGound"
                >
                  <el-input
                    v-model="formData.respondentsGound"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.carType')"
                  prop="carType"
                >
                  <DictSelect
                    v-model="formData.carType"
                    code="CAR_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.licensePlate')"
                  prop="licensePlate"
                >
                  <el-select
                    v-model="formData.licensePlate"
                    filterable
                    remote
                    :placeholder="$t('bidMod.msgKeyword')"
                    :remote-method="remoteMethod"
                    clearable
                    automatic-dropdown
                    @change="selectLicensePlate"
                  >
                    <el-option
                      v-for="item in selectOpts.carList"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.entryTime')"
                  prop="entryTime"
                >
                  <el-date-picker
                    v-model="formData.entryTime"
                    :picker-options="pickerOptions"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    type="date"
                    :placeholder="$t('bidMod.msgKeyword')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.deliveryLocation')"
                  prop="deliveryLocation"
                >
                  <el-input v-model="formData.deliveryLocation" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.comments')"
                  prop="comments"
                >
                  <el-input v-model="formData.comments" />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="flag != 'add'"
              >
                <el-form-item :label="$t('common.status')">
                  <DictSelect
                    v-model="formData.deliveryAppointStatus"
                    code="DELIVERY_APPOINT_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <template
                v-if="flag != 'add'"
              >
                <srm-col>
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdUserName"
                  >
                    <el-input
                      v-model="formData.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.creationDate')"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="formData.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.lastUpdateBy')"
                    prop="lastUpdatedUserName"
                  >
                    <el-input
                      v-model="formData.lastUpdatedUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.lastUpdateDate')"
                    prop="lastUpdateDate"
                  >
                    <el-date-picker
                      v-model="formData.lastUpdateDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </template>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.appointDeliveryNotesList')"
          name="2"
        >
          <el-table
            :data="deliveryNotes.list"
            style="width: 100%"
            border
            height="160px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <el-table-column
              align="center"
              prop="deliveryNumber"
              :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
              min-width="150"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.deliveryNumber"
                  disabled
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="deliveryDate"
              :label="$t('orderMod.buyerOrderSynergy.deliveryDate2')"
              min-width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.deliveryDate"
                  style="width:100%;"
                  :picker-options="pickerOptions"
                  disabled
                  type="date"
                  :format="$formatDatePicker"
                  :placeholder="$t('bidMod.msgKeyword')"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="comments"
              :label="$t('orderMod.buyerOrderSynergy.comments')"
              min-width="200"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.comments"
                  disabled
                />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.operation')"
              min-width="60"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  disabled
                  @click="delDelivery(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.visitorsList')"
          name="3"
        >
          <el-form
            ref="visitors"
            :model="visitorsNotes"
          >
            <el-table
              :data="visitorsNotes.list"
              style="width: 100%"
              border
              height="161px"
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                prop="visitorName"
                :label="$t('orderMod.buyerOrderSynergy.visitorName')"
                min-width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="`list.${scope.$index}.visitorName`"
                  >
                    <el-input
                      v-model="scope.row.visitorName"
                      disabled
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="idType"
                :label="$t('orderMod.buyerOrderSynergy.idType')"
                min-width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="`list.${scope.$index}.idType`"
                  >
                    <DictSelect
                      v-model="scope.row.idType"
                      code="ID_TYPE"
                      disabled
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="idNo"
                :label="$t('orderMod.buyerOrderSynergy.idNo')"
                min-width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="`list.${scope.$index}.idNo`"
                  >
                    <el-input
                      v-model="scope.row.idNo"
                      disabled
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="linkPhone"
                :label="$t('orderMod.buyerOrderSynergy.linkPhone')"
                min-width="150"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="`list.${scope.$index}.linkPhone`"
                  >
                    <el-input
                      v-model="scope.row.linkPhone"
                      disabled
                      type="number"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('orderMod.buyerOrderSynergy.comments')"
                min-width="200"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.comments"
                    disabled
                  />
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('common.operation')"
                min-width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    disabled
                    @click="delVisitors(scope.$index, scope.row)"
                  >
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
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import { deliveryAppointmentApi } from 'modb@/orderManagementBuyer/api'

export default {
  name: 'DeliveryAppointmentDetailBuyer',
  components: {
    OrganizationSelector,
    QuickSearch,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      flag: this.$attrs.params.flag,
      formLabelWidth: '120px',
      formData: {
        deliveryAppointId: null,
        vendorCode: null,
        vendorId: null,
        vendorName: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        receiveAddress: null,
        respondents: null,
        respondentsNo: null,
        respondentsPhone: null,
        respondentsGound: null,
        respondentsGoundNumber: null,
        carType: null,
        licensePlate: null,
        entryTime: parseTime(new Date(), '{y}-{m}-{d}', true),
        deliveryLocation: null,
        comments: null,
        deliveryAppointStatus: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        lastUpdateDate: null
      },
      deliveryNotes: {
        show: false, // 送货单据选择弹窗
        list: []
      },
      visitorsNotes: {
        list: [],
        rules: {
          visitorName: [{ required: true, message: this.$t('vendorMod.msgNickname') }],
          linkPhone: [{ required: true, message: this.$t('orderMod.msgOrder[22]') }],
          idType: [{ required: true, message: this.$t('orderMod.msgOrder[23]') }],
          idNo: [{ required: true, message: this.$t('orderMod.msgOrder[24]') }]
        }
      },
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }],
        receiveAddress: [{ required: true, message: this.$t('orderMod.msgOrder[25]') }],
        respondents: [{ required: true, message: this.$t('orderMod.msgOrder[27]') }],
        carType: [{ required: true, message: this.$t('orderMod.msgOrder[28]') }],
        licensePlate: [{ required: true, message: this.$t('orderMod.msgOrder[29]') }],
        entryTime: [{ required: true, message: this.$t('orderMod.msgOrder[30]') }],
        deliveryLocation: [{ required: true, message: this.$t('orderMod.msgOrder[31]') }],
        comments: [{ required: true, message: this.$t('orderMod.msgOrder[32]') }]
      },
      selectOpts: {
        // addressList: [],
        carList: [],
        carTypes: [],
        cardTypes: []
      },
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
    const { row } = this.$attrs.params
    this.formData.deliveryAppointId = row.deliveryAppointId
    this.queryDetail()
  },
  methods: {
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('deliveryAppointmentList.getQueryData')
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element.receiver)
      this.$set(row, 'receiveTelephone', element.receiverPhone)
      this.$set(row, 'receiveAddress', element.siteName)
    },
    // 业务实体
    selectHandler (node, value, scope) {
      this.formData.orgId = node ? node.organizationId : null
      this.formData.orgCode = node ? node.organizationCode : null
      this.formData.orgName = node ? node.organizationName : null
    },
    // 快速查询受访人员
    getUserObj (val, scope) {
      scope.respondents = val ? val.nickname : ''
      scope.respondentsNo = val ? val.username : ''
      scope.respondentsPhone = val ? val.phone : ''
      scope.respondentsGound = val ? val.department : ''
      scope.respondentsGoundNumber = val ? val.ceeaDeptid : ''
    },
    // 选择车牌号码
    selectLicensePlate (licensePlate) {
      const item = this.selectOpts.carList.find((i) => i.value === licensePlate)
      if (item) {
        this.formData.carType = item.type
      }
    },
    // 获取可选的车辆信息
    remoteMethod (licensePlate) {
      const params = { pageNum: 1, pageSize: 15, status: 'EFFECTIVE' }
      if (licensePlate) {
        params.licensePlate = licensePlate
      }
      this.$http({
        url: '/api-sup-ce/order/carInfo/listPage',
        method: 'POST',
        data: params
      }).then((res) => {
        this.selectOpts.carList = res.data.list.map((i) => ({
          id: i.carInfoId,
          value: i.licensePlate,
          label: i.licensePlate,
          type: i.carType
        }))
      })
    },
    // 删除送货单据
    delDelivery (index, row) {
      this.deliveryNotes.list.splice(index, 1)
    },
    delVisitors (index) {
      this.visitorsNotes.list.splice(index, 1)
    },
    // 如果是编辑、查看详情，根据deliveryAppointId获取数据
    queryDetail () {
      deliveryAppointmentApi.getDeliveryAppointById({
        deliveryAppointId: this.formData.deliveryAppointId
      }).then((res) => {
        if (res.data) {
          this.formData = Object.assign({}, this.formData, res.data.deliveryAppoint)
          this.deliveryNotes.list = res.data.appointDeliveryNotes
          this.visitorsNotes.list = res.data.visitors
        }
      })
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
