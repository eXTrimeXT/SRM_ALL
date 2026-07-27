<template>
  <el-container class="flex-container the_orderAcceptanceDetail_wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          ref="formRef"
          :model="form"
          :disabled="isReadOnly"
        >
          <el-collapse-item :title="$t('vendorMod.receiptInfo')" name="1">
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item
                  prop="orgId"
                  :label="$t('oneStopShopping.businessEntity')"
                  :rules="[{ required: true, message: $t('quota.orgIdTips') }]"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="isReadOnly || !!detailTableData.length"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <!-- 验收单据号 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.checkOrderNumber')">
                  <el-input v-model="form.checkOrderNumber" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="vendorName"
                  :label="$t('orderMod.buyerOrderSynergy.vendorName')"
                  :rules="[{ required: true, message: $t('quota.vendorTips') }]"
                >
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info_all"
                    :disabled="isReadOnly || !!detailTableData.length"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.billstatus')">
                  <DictSelect v-model="form.checkOrderStatus" code="CHECK_ORDER_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.creationDate')">
                  <el-date-picker
                    v-model="form.creationDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('purchaseDemand.ceeaDepartment')">
                  <el-input v-model="form.departmentName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.companyName')">
                  <el-input v-model="form.orgName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('cusEntry.common.plate')">
                  <el-input v-model="form.orgBuName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bid_mod.currencyName')">
                  <el-input v-model="form.currencyName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contractMod.totalAmountTax')">
                  <el-input-number
                    v-model="form.taxTotalAmount"
                    style="width: 100%;"
                    :controls="false"
                    :precision="4"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contractMod.totalAmountNoTax2')">
                  <el-input-number
                    v-model="form.noTaxTotalAmount"
                    style="width: 100%;"
                    :controls="false"
                    :precision="4"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="approveUserName"
                  :label="$t('cusEntry.inq.departmentLeader')"
                  :rules="[{ required: true, message: $t('cusEntry.orderMod.departmentLeaderMsg') }]"
                >
                  <QuickSearch
                    :show-input="form.approveUserName"
                    show-key="nickname"
                    :scope-data="form"
                    name="scc_rbac_user_display"
                    :disabled="isReadOnly"
                    @close-quicksearch="getUserObj"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <!-- 验收单明细 -->
          <el-collapse-item :title="$t('cusEntry.orderMod.checkOrderDetail')" name="2">
            <el-button
              type="primary"
              style="margin-bottom: 10px;"
              @click="addCheckOrder"
            >
              {{ $t('common.add') }}
            </el-button>
            <el-table
              :data="detailTableData"
              style="width: 100%;"
              border
              max-height="500px"
            >
              <el-table-column
                align="center"
                :label="$t('purSettlementMod.tabindex')"
                type="index"
                fixed="left"
                width="60"
              />
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="lineNum"
                :label="$t('orderMod.orderLineNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('sourcingBuyer.categoryType')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialName"
                :label="$t('common.materialName')"
                min-width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('dataConfMod.unit')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="orderNum"
                :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 可验收数量 = 订单数量(orderNum) - 验收数量(extCheckQty)-->
              <el-table-column
                align="center"
                prop="extCheckQtyToDo"
                :label="$t('cusEntry.orderMod.extCheckQty')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 本次验收数量 -->
              <el-table-column
                align="center"
                prop="checkQty"
                :label="$t('cusEntry.orderMod.checkQty')"
                min-width="120"
                :render-header="_addStarToColumn"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.checkQty"
                    style="width: 100%;"
                    :controls="false"
                    :min="0"
                    :max="scope.row.extCheckQtyToDo"
                    :disabled="isReadOnly"
                    @change="setAmount(scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 未税单价 -->
              <el-table-column
                prop="ceeaUnitNoTaxPrice"
                :label="$t('purSettlementMod.unitPriceNoTax')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 税率 -->
              <el-table-column
                prop="ceeaTaxRate"
                :label="$t('bidMod.taxRate2')"
                minWidth="100"
                align="center"
                show-overflow-tooltip
              />
              <!-- 含税单价 -->
              <el-table-column
                prop="ceeaUnitTaxPrice"
                :label="$t('bid_mod.taxUnitPrice')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 未税总价 -->
              <el-table-column
                prop="noTaxAmount"
                :label="$t('cusEntry.bidMod.orderNotaxAmount')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 含税总价 -->
              <el-table-column
                prop="taxAmount"
                :label="$t('cusEntry.bidMod.orderTaxAmount')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <el-table-column
                :label="$t('common.operation')"
                fixed="right"
                width="80"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="deleteDetails(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 验收意见 -->
          <el-collapse-item :title="$t('cusEntry.orderMod.checkAdvice')" name="3">
            <srm-row>
              <srm-col :initCol="1">
                <el-form-item
                  prop="checkAdvice"
                  :label="$t('cusEntry.orderMod.checkAdvice')"
                  :rules="[{ required: true, message: $t('cusEntry.orderMod.checkAdviceMsg') }]"
                >
                  <el-input
                    v-model="form.checkAdvice"
                    type="textarea"
                    :disabled="isReadOnly"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item :title="$t('quota.fileInfo')" name="4">
            <div class="btn_line">
              <el-button
                type="primary"
                style="margin-bottom: 10px;"
                :disabled="isReadOnly"
                @click="addFileRow"
              >
                {{ $t('common.add') }}
              </el-button>
            </div>
            <el-table
              :data="attachList"
              border
              style="width: 100%; margin-bottom: 16px;"
              max-height="250px"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="60"
              />
              <el-table-column
                align="center"
                prop="attachName"
                :label="$t('bidMod.fileName')"
                minWidth="180"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.attachId,
                      fileName: scope.row.attachName
                    }"
                    :readonly="isReadOnly"
                    @on-change="({file}) => uploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="createdFullName"
                :label="$t('quota.uploadBy')"
                minWidth="120"
              />
              <!-- 上传账号 -->
              <el-table-column
                align="center"
                prop="createdBy"
                :label="$t('cusEntry.orderMod.uploadBy')"
                minWidth="120"
              />
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('quota.uploadDate')"
                minWidth="120"
              />
              <el-table-column :label="$t('common.operation')" width="80">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="deleteFileRow(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ isReadOnly ? $t('common.close') : $t('common.cancel') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submitBill('SAVE')">
            {{ $t('common.save') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submitBill('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>
          <el-button v-if="isManageWithdraw" type="primary" @click="withdrawHandler">
            {{ $t('bidMod.withdraw') }}
          </el-button>
          <el-button v-if="isManageApprove" type="primary" @click="refuseHandler">
            {{ $t('purchaseDemand.refuse') }}
          </el-button>
          <el-button v-if="isManageApprove" type="primary" @click="approveHandler">
            {{ $t('common.toApprove') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <SrmDialog
      v-if="dialogVisible"
      title="选择服务类订单"
      size="xLarge"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      append-to-body
    >
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="false"
        :checkbox="true"
        :checkChange="handleSelectionChange"
        :adeptMeiQl="true"
        :comActive="$attrs['changeTab']"
        row-key="orderDetailId"
        url="/api-sup-ce/api-ql/OrderDetail/listDetailForBuyer"
      >
        <!-- 订单行附件 -->
        <template #extAttachId="{ scope }">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.extAttachId,
              fileName: scope.row.extAttachName
            }"
            readonly
          />
        </template>
      </TableView>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="comfirmAddDetail">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </SrmDialog>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'OrderAcceptanceDetail',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'order',
        fileFunction: 'order',
        fileType: 'images'
      },
      form: {
        orgId: null,
        orgCode: null,
        orgName: null,
        checkOrderNumber: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        checkOrderStatus: 'DRAFT',
        createdFullName: null,
        creationDate: null,
        taxTotalAmount: null, // 含税总金额
        noTaxTotalAmount: null, // 未税总金额
        currencyName: '人民币',
        currencyCode: 'RMB',
        departmentName: null,
        departmentCode: null,
        departmentId: null,
        orgBuCode: null,
        orgBuId: null,
        orgBuName: null,
        approveUserName: null,
        approveUserId: null,
        approveUserCode: null
      },
      detailTableData: [],
      detailTableDataDelete: [],
      attachList: [],
      attachListDelete: [],
      dialogVisible: false,
      preArr: [
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        }
      ],
      gridId: 'orderDetailList',
      pageSize: 15,
      queryParam: {},
      tableData: [],
      tableHeader: [
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
          formattor: (val, row) => {
            return row.orderId.orderNumber || '--'
          },
          minWidth: 150
        },
        {
          prop: 'lineNum',
          label: this.$t('purSettlementMod.orderLineNumber'), // 采购订单行号
          formattor: val => val || '--',
          minWidth: 120
        },
        {
          prop: 'buyerName',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
          formattor: (val, row) => {
            return row.orderId.buyerName
          },
          minWidth: 100
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          minWidth: 120
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          minWidth: 120
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          minWidth: 120
        },
        {
          prop: 'ceeaUnitTaxPrice',
          label: () => this.$t('purchaseDemand.taxPrice'), // 含税单价
          minWidth: 120
        },
        {
          prop: 'ceeaUnitNoTaxPrice',
          label: () => this.$t('contractMod.notaxPrice'), // 未税单价
          minWidth: 120
        },
        {
          prop: 'ceeaTaxRate',
          label: () => this.$t('bidMod.taxRate'), // 税率
          minWidth: 120
        },
        {
          prop: 'extUserName',
          label: '使用人',
          minWidth: 120
        },
        {
          prop: 'extUseDepartmentName',
          label: '使用部门',
          minWidth: 120
        },
        {
          prop: 'comments',
          label: '订单行备注',
          minWidth: 120
        },
        {
          prop: 'extAttachId',
          label: '订单附件',
          showType: 'slot',
          slot: 'extAttachId',
          minWidth: 200
        }
      ],
      selection: []
    }
  },
  computed: {
    // 管理按钮进来，创建人撤回单据
    isManageWithdraw () {
      return this.$attrs.params.flag === 'manage' && this.$store.getters.userInfo.userId === this.form.createdId
    },
    // 管理按钮进来，采购科长审批单据
    isManageApprove () {
      return this.$attrs.params.flag === 'manage' && this.$store.getters.userInfo.userId === this.form.approveUserId
    },
    isReadOnly () {
      return ['view', 'manage'].includes(this.$attrs.params.flag)
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    if (flag === 'add') {
      this.form.createdFullName = this.$store.getters.userInfo.nickname
      this.form.creationDate = new Date()
      this.getOrgInfos()
    } else {
      this.queryDetails(row.checkOrderId)
    }
  },
  methods: {
    // 创建人撤回
    withdrawHandler () {
      const params = { checkOrderId: this.form.checkOrderId }
      const saveData = transformMQL.save('CheckOrder', [params], 'withdraw')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrder/withdraw',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    // 部门领导驳回
    refuseHandler () {
      const params = { checkOrderId: this.form.checkOrderId }
      const saveData = transformMQL.save('CheckOrder', [params], 'reject')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrder/reject',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    // 部门领导通过审批
    approveHandler () {
      const params = { checkOrderId: this.form.checkOrderId }
      const saveData = transformMQL.save('CheckOrder', [params], 'approve')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrder/approve',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'orderAcceptanceDetail')
      } else {
        this.$emit('tab-remove', 'orderAcceptanceDetail' + this.form.checkOrderNumber)
      }
      this.__setTabTodo('orderAcceptanceList.getQuerydata')
    },
    getOrgInfos () {
      // 查询 申请部门、公司、板块
      this.$http({
        url: `/api-pj/pj-anon/user/getHrUserOrgnizationByUsername?username=${this.$store.getters.userInfo.username}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res.data) {
          this.form.departmentName = res.data.departmentOrganization?.organizationName
          this.form.departmentCode = res.data.departmentOrganization?.organizationCode
          this.form.departmentId = res.data.departmentOrganization?.organizationId
          this.form.orgName = res.data.ouOrganization?.organizationName
          this.form.orgCode = res.data.ouOrganization?.organizationCode
          this.form.orgId = res.data.ouOrganization?.organizationId
          this.form.orgBuName = res.data.buOrganization?.organizationName
          this.form.orgBuCode = res.data.buOrganization?.organizationCode
          this.form.orgBuId = res.data.buOrganization?.organizationId
        }
      })
    },
    getUserObj (val, scope) {
      scope.approveUserId = val ? val.userId : null
      scope.approveUserCode = val ? val.username : null
      scope.approveUserName = val ? val.nickname : null
    },
    // 设置小数点位数4位
    setNumberPrecision (row, column, cellValue, index) {
      return cellValue ? cellValue.toFixed(4) : null
    },
    addCheckOrder () {
      if (!this.form.orgId || !this.form.vendorId) {
        this.$message.warning('请先选择业务实体与供应商')
        return
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.getQuerydata()
      })
    },
    getQuerydata (params) {
      // 默认查出该业务实体、供应商下的实物类订单，且订单数量大于已验收数量
      // 查询条件：orderNumber 查询关联头表，materialCode查询这张明细表
      let queryFilter = {
        '$condition': {
          '$strictQuery': true,
          filter: {
            ceeaOrgId: { eq: this.form.orgId },
            vendorId: { eq: this.form.vendorId },
            extOrderProperty: { eq: 'SERVICE' }
          }
        }
      }
      const { orderNumber, materialCode } = params || {}
      if (orderNumber) {
        queryFilter['$condition'].filter.orderNumber = { contains: orderNumber }
      }
      let filter = { 'orderNum': { 'gt': { '$field': 'extCheckQty' } } }
      if (materialCode) {
        filter.materialCode = { contains: materialCode }
      }
      this.queryParam = {
        type: 'OrderDetail',
        action: 'listDetailForBuyer',
        payload: {
          filter: { ...filter },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: {
          '*': {},
          'orderId': {
            '*': {},
            ...queryFilter
          }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    comfirmAddDetail () {
      if (this.selection.length == 0) {
        this.$message.warning('请先选择数据')
        return
      }
      // 明细单去重 根据orderDetailId
      let repeatFlag = this.selection.some(item => {
        if (this.detailTableData.some(v => v.orderDetailId == item.orderDetailId)) {
          return true
        }
      })
      if (repeatFlag) {
        this.$message.warning('请勿添加重复订单')
        return
      }
      this.selection.map(item => {
        const { orderId = {}, ...rest } = item
        const row = {
          ...rest,
          ...orderId,
          // 可验收数量 = 订单数量(orderNum) - 验收数量(extCheckQty)
          extCheckQtyToDo: item.orderNum - item.extCheckQty,
          // 本次验收数量 默认 = 可验收数量
          checkQty: item.orderNum - item.extCheckQty
        }
        this.detailTableData.push(row)
        this.setAmount(row)
      })
      this.dialogVisible = false
    },
    // 计算头上金额 未税总金额 含税总金额
    setTotalAmount () {
      this.form.noTaxTotalAmount = this.detailTableData.map(v => v.noTaxAmount)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      this.form.taxTotalAmount = this.detailTableData.map(v => v.taxAmount)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
    },
    // 计算金额
    setAmount (row) {
      // 未税总价 = 本次验收数量 * 未税单价
      if (!!row.checkQty && !!row.ceeaUnitNoTaxPrice) {
        row.noTaxAmount = Number(row.checkQty) * Number(row.ceeaUnitNoTaxPrice)
      } else {
        row.noTaxAmount = null
      }
      // 含税总价 = 本次验收数量 * 含税单价
      if (!!row.checkQty && !!row.ceeaUnitTaxPrice) {
        row.taxAmount = Number(row.checkQty) * Number(row.ceeaUnitTaxPrice)
      } else {
        row.taxAmount = null
      }
      this.setTotalAmount()
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : null
      scope.vendorCode = val ? val.companyCode : null
      scope.vendorName = val ? val.companyName : null
    },
    queryDetails (checkOrderId) {
      const searchData = {
        type: 'CheckOrder',
        action: 'read',
        payload: [checkOrderId],
        query: {
          '*': {},
          'detailList': {
            '*': {},
            'orderDetailId': {
              '*': {},
              'orderId': { '*': {} }
            },
            'checkOrderDetailId': { '*': {} }
          },
          attachList: { '*': {} }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrder/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { detailList = [], attachList = [], ...rest } = res.data[0]
          this.form = { ...rest }
          !this.form.orgId && this.getOrgInfos()
          this.attachList = attachList
          // 数据组装：明细表格数据有些字段来自订单详情表
          this.detailTableData = detailList.map(item => {
            const { orderDetailId = {}, ...rest } = item
            const { orderId = {} } = orderDetailId
            const row = {
              ...rest,
              ...orderDetailId,
              ...orderId,
              // 单据保存时，验收数量就会回显，所以单据为了正确展示得加上本次验收数量
              extCheckQtyToDo: orderDetailId.orderNum - orderDetailId.extCheckQty + item.checkQty
            }
            this.setAmount(row)
            return row
          })
          this.setTotalAmount()
        }
      })
    },
    deleteDetails (index, row) {
      if (row.checkOrderDetailId) {
        this.detailTableDataDelete.push({ $delete: row.checkOrderDetailId })
      }
      this.detailTableData.splice(index, 1)
      this.setAmount(row)
    },
    addFileRow () {
      this.attachList.push({
        attachId: null,
        attachName: null,
        createdFullName: null,
        createdBy: null,
        creationDate: null
      })
    },
    uploadSuccess (file, row) {
      const { fileId = null, fileName = null, createdFullName, createdBy, creationDate } = file || {}
      row.attachId = fileId
      row.attachName = fileName
      row.createdFullName = createdFullName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    deleteFileRow (index, row) {
      if (row.checkOrderAttachId) {
        this.attachListDelete.push({ $delete: row.checkOrderAttachId })
      }
      this.attachList.splice(index, 1)
    },
    saveBill (type) {
      const params = {
        ...this.form,
        checkOrderStatus: type == 'SAVE' ? 'DRAFT' : 'APPROVING',
        detailList: [...this.detailTableData, ...this.detailTableDataDelete],
        attachList: [...this.attachList, ...this.attachListDelete]
      }
      const saveData = transformMQL.save('CheckOrder', [params], 'saveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrder/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        let checkOrderId = res.data[0]?.checkOrderId
        if (type == 'SAVE') {
          this.__setTabTodo('orderAcceptanceList.getQuerydata')
          this.queryDetails(checkOrderId)
          this.detailTableDataDelete = []
          this.attachListDelete = []
        } else {
          this.cancelBill()
        }
      })
    },
    submitBill (type) {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          let errFlag = false
          let errMsg = ''
          if (this.detailTableData.length == 0) {
            errFlag = true
            errMsg = '至少添加一条验收单明细'
          }
          // 校验【本次验收数量】必填
          this.detailTableData.some((item, index) => {
            if (!item.checkQty) {
              errFlag = true
              errMsg = `验收单明细第${index + 1}行未填写本次验收数量`
              return true // 跳出循环
            }
          })
          if (errFlag) {
            this.$message.error(errMsg)
            return
          }
          this.saveBill(type)
        } else {
          return this.$message.error(this.$t('common.pleasefinishRequired'))
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
