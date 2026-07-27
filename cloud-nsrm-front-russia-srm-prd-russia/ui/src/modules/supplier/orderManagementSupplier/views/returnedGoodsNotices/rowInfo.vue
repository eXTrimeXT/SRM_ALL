<template>
  <el-container
    class="the-returnedGoodsNoticeDetail-buyer-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
        >
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.organizationName')"
                :label-width="formLabelWidth"
                prop="organizationId"
              >
                <OrganizationSelectTree
                  v-model="form.organizationId"
                  :disabled="canEdit"
                  @select="treeselectChange"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                prop="vendorId"
                :label="$t('common.vendor')"
                :label-width="formLabelWidth"
              >
                <el-select
                  v-model="form.vendorId"
                  filterable
                  remote
                  :placeholder="$t('bidMod.msgKeyword')"
                  :remote-method="remoteMethod"
                  :loading="loading"
                  clearable
                  disabled
                  automatic-dropdown
                  @change="elSelectChange"
                >
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('orderMod.returnType')"
                :label-width="formLabelWidth"
              >
                <el-select
                  v-model="form.returnType"
                  disabled
                >
                  <el-option
                    v-for="item in returnType"
                    :key="item.id"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                prop="returnDate"
                :label="$t('orderMod.returnDate')"
                :label-width="formLabelWidth"
              >
                <el-date-picker
                  v-model="form.returnDate"
                  type="date"
                  :placeholder="$t('bidMod.datePicker')"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('common.vendorCode')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.vendorCode"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.returnOrderNumber')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.returnOrderNumber"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('common.status')"
                :label-width="formLabelWidth"
              >
                <el-select
                  v-model="form.returnStatus"
                  disabled
                >
                  <el-option
                    v-for="item in returnStatus"
                    :key="item.id"
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
                :label="$t('common.remark')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="form.comments"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="form-container">
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
            prop="orderNumber"
            :label="$t('orderMod.buyerOrderSynergy.orderNumber2')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.orderNumber"
                disabled
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="materialCode"
            :label="$t('orderMod.buyerOrderSynergy.materialCode')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.materialCode"
                disabled
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="materialName"
            :label="$t('common.materialName')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.materialName"
                disabled
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="orderNum"
            :label="$t('orderMod.buyerOrderSynergy.orderNum')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.orderNum"
                disabled
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="receivedNum"
            :label="$t('orderMod.buyerOrderSynergy.receivedNum')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.receivedNum"
                disabled
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="replenishNum"
            :label="$t('orderMod.buyerOrderSynergy.replenishNum')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.replenishNum"
                disabled
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="returnNum"
            :label="$t('orderMod.buyerOrderSynergy.returnNum')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.returnNum"
                disabled
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="deliveryType"
            :label="$t('orderMod.deliveryType')"
            width="100"
          >
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.deliveryType"
                disabled
              >
                <el-option
                  v-for="item in deliveryTypes"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('common.remark')"
            width="100"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.comments"
                disabled
              />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            :disabled="canEdit"
            type="primary"
            @click="confirmBill"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import { returnedGoodsNoticesApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'RowInfo',
  components: {
    MainHeader,
    OrganizationSelectTree,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      canEdit: true,
      queryParams: {},
      form: {
        comments: '',
        returnType: '',
        organizationCode: '',
        organizationId: null,
        organizationName: '',
        vendorId: '',
        vendorCode: '',
        vendorName: ''
      },
      formLabelWidth: '120px',
      tableData: [],
      isModify: false,
      returnType: [],
      returnStatus: [],
      loading: false,
      options: [],
      deliveryTypes: [],
      modalVisible: false
    }
  },
  created () {
    if (this.$attrs.params.flag === 'edit') {
      this.form = this.$attrs.params.row
      this.queryList()
    }
  },
  mounted () {
    this.initDictionary()
    this.remoteMethod()
  },
  methods: {
    queryList () {
      const { returnOrderId } = this.$attrs.params.row
      returnedGoodsNoticesApi.returnDetailListPage({ returnOrderId }).then(res => {
        this.tableData = res.data.list
      })
    },
    elSelectChange (val) {
      const company = this.options.find(i => i.value === val)
      const { id, label } = company || {}
      this.form = { ...this.form, vendorName: label, vendorCode: id }
    },
    remoteMethod (keyWord) {
      if (!this.form.organizationId) {
        return this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[19]')
        })
      }
      this.queryCompanyList({ keyWord, orgId: this.form.organizationId })
    },
    queryCompanyList ({ keyWord = '', orgId }) {
      returnedGoodsNoticesApi.listPageByOrgIdAndKeyWord({ keyWord, orgId }).then(res => {
        this.options = res.data.map(i => ({
          id: i.companyCode,
          value: i.companyId,
          label: i.companyName
        }))
      })
    },
    treeselectChange (node, instanceId, scope) {
      const { organizationCode, organizationName, organizationId } = node
      this.queryParams = { organizationId }
      this.form.organizationCode = organizationCode
      this.form.organizationName = organizationName
      this.form.organizationId = organizationId
      this.queryCompanyList({ orgId: organizationId })
    },

    initDictionary () {
      const codes = ['DELIVERY_TYPE', 'RETURN_TYPE', 'RETURN_STATUS']
      const params = codes.map(i => ({ dictCode: i }))
      getDictItemList(params).then(res => {
        const [DELIVERY_TYPE, RETURN_TYPE, RETURN_STATUS] = res.data
        this.deliveryTypes = adaptDictData(DELIVERY_TYPE.DELIVERY_TYPE)
        this.returnType = adaptDictData(RETURN_TYPE.RETURN_TYPE)
        this.returnStatus = adaptDictData(RETURN_STATUS.RETURN_STATUS)
      })
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'rowInfo')
      } else {
        this.$emit('tab-remove', 'rowInfo' + this.form.returnOrderId)
      }
      this.__setTabTodo('returnedGoodsNoticeList.getQuerydata')
    },
    confirmBill () {
      const data = {
        returnOrder: this.form,
        detailList: this.tableData
      }
      if (this.$attrs.params.flag == 'add') {
        returnedGoodsNoticesApi.returnOrderSave(data).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.$emit('tab-remove', 'rowInfo')
        })
      } else {
        returnedGoodsNoticesApi.returnOrderUpdate(data).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.$emit('tab-remove', 'rowInfo' + this.form.returnOrderId)
        })
      }
      this.__setTabTodo('returnedGoodsNoticeList.getQuerydata')
    },
    addDelivery () {
      // 根据当前已选采购组织筛选出需要发货的订单明细
      const { organizationId } = this.form
      if (!organizationId) {
        return this.$message({
          type: 'warning',
          message: this.$t('bidMod.bidMsgList[23]')
        })
      }
      this.modalVisible = true
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
}
</style>
