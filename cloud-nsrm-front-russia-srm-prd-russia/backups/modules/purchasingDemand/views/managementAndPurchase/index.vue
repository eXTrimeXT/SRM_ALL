<template>
  <el-container
    class="flex-container-notab the_managementAndPurchase_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :select-dictionary="selectDictionary"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- <AuthorityButton
            code="managementAndPurchase_pull"
            type="primary"
            @click="pull"
            >{{ $t("purchaseDemand.pull") }}</AuthorityButton
          > -->
          <AuthorityButton
            code="pr:requirementManage:allot"
            type="primary"
            @click="allot"
          >
            {{ $t("purchaseDemand.allot") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:requirementManage:cancelAllot"
            type="primary"
            @click="cancelAllot"
          >
            {{ $t("purchaseDemand.cancelAllot") }}
          </AuthorityButton>
          <!-- <AuthorityButton
            code="managementAndPurchase_remark"
            type="primary"
            @click="save"
            >{{ $t("purchaseDemand.remark") }}</AuthorityButton
          > -->
          <!-- <AuthorityButton
            code="managementAndPurchase_exportRemark"
            type="primary"
            @click="exportRemark"
            >{{ $t("purchaseDemand.exportRemark") }}</AuthorityButton
          > -->
          <AuthorityButton
            code="pr:requirementManage:reject"
            type="primary"
            @click="refuse"
          >
            {{ $t("purchaseDemand.refuse") }}
          </AuthorityButton>
          <AuthorityButton
            code="managementAndPurchase_createOrder"
            type="primary"
            @click="createOrder"
          >
            {{ $t("purchaseDemand.createOrder") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:requirementManage:merge"
            type="primary"
            @click="merge"
          >
            {{ $t("purchaseDemand.merge") }}
          </AuthorityButton>
          <AuthorityButton
            code="managementAndPurchase_createSourcing"
            type="primary"
            @click="createSourcing"
          >
            {{ $t("purchaseDemand.createSourcing") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :row-index="true"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="managementAndPurchaseApi.listApprovedApplyByPage"
      />
    </el-main>
    <!-- 人员选择 -->
    <CPeopleSelector
      ref="peopleSelector"
      :visible.sync="peopleDialog"
      :multi-select="false"
      @on-confirm="getPeople"
    />
    <srm-dialog
      :visible.sync="mergeDialogVisible"
      :title="$t('purchaseDemand.merge')"
      size="large"
    >
      <el-main>
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 需合并的行 -->
          <el-collapse-item
            :title="$t('purchaseDemand.mergeRow')"
            name="1"
          >
            <el-table
              ref="table"
              :data="currentRows"
              stripe
              border
              highlight-current-row
            >
              <el-table-column
                type="index"
                align="center"
                fixed
              />
              <el-table-column
                width="130"
                :label="$t('purchaseDemand.requirementHeadNum')"
                prop="requirementHeadNum"
              />
              <el-table-column
                width="80"
                :label="$t('purchaseDemand.rowNum')"
                prop="rowNum"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.applyStatus')"
                prop="applyStatus"
                :formatter="formatterApplyStatus"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.unit')"
                prop="unit"
                :formatter="formatterUnit"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.costType')"
                prop="costType"
                :formatter="formatterCostType"
              />
              <el-table-column
                width="130"
                :label="$t('purchaseDemand.fullPathId')"
                prop="purchaseOrganization"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementDepartment')"
                prop="requirementDepartment"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.categoryName')"
                prop="categoryName"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.itemCode')"
                prop="itemCode"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.itemName')"
                prop="itemDesc"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementQuantity')"
                prop="requirementQuantity"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementDate')"
                prop="requirementDate"
                :formatter="formatterDate"
              />
              <el-table-column
                width="130"
                :label="$t('purchaseDemand.inventoryPlace')"
                prop="inventoryPlace"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.costNum')"
                prop="costNum"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.brand')"
                prop="brand"
              />
            </el-table>
          </el-collapse-item>
          <!-- 合并结果 -->
          <el-collapse-item
            :title="$t('purchaseDemand.mergeResult')"
            name="2"
          >
            <el-table
              ref="table"
              :data="mergedRows"
              stripe
              border
              highlight-current-row
            >
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.applyStatus')"
                prop="applyStatus"
                :formatter="formatterApplyStatus"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.unit')"
                prop="unit"
                :formatter="formatterUnit"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.costType')"
                prop="costType"
                :formatter="formatterCostType"
              />
              <el-table-column
                width="130"
                :label="$t('purchaseDemand.fullPathId')"
                prop="purchaseOrganization"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementDepartment')"
                prop="requirementDepartment"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.categoryName')"
                prop="categoryName"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.itemCode')"
                prop="itemCode"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.itemName')"
                prop="itemDesc"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementQuantity')"
                prop="requirementQuantity"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.requirementDate')"
                prop="requirementDate"
                :formatter="formatterDate"
              />
              <el-table-column
                width="130"
                :label="$t('purchaseDemand.inventoryPlace')"
                prop="inventoryPlace"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.costNum')"
                prop="costNum"
              />
              <el-table-column
                width="100"
                :label="$t('purchaseDemand.brand')"
                prop="brand"
              />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-main>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="mergeDialogVisible = false"
        >
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="submitMerge"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
    <srm-dialog
      :visible.sync="sourceVisible"
      :title="$t('purchaseDemand.createSourcing')"
      size="small"
    >
      <label>寻源方式：</label>
      <el-select v-model="sourceType">
        <el-option
          v-for="item in sourceSelect"
          :key="item.id"
          :value="item.id"
          :label="item.label"
        />
      </el-select>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="sourceVisible = false"
        >
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="submitGenSource"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
    <srm-dialog
      :visible.sync="orderVisible"
      :title="$t('purchaseDemand.createOrder')"
      size="large"
    >
      <el-table
        ref="table"
        :data="orderRows"
        stripe
        border
        highlight-current-row
      >
        <el-table-column
          type="index"
          align="center"
          fixed
        />
        <el-table-column
          width="140"
          :label="$t('purchaseDemand.requirementHeadNum')"
          prop="requirementHeadNum"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.rowNum')"
          prop="rowNum"
        />
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.fullPathId')"
          prop="organizationName"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.categoryName')"
          prop="categoryName"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.itemCode')"
          prop="itemCode"
        />
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.itemName')"
          prop="itemDesc"
          show-overflow-tooltip
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.unit')"
          prop="unit"
          :formatter="formatterUnit"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.taxRate2')"
          prop="taxRate"
          :formatter="formatterTax"
        />
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.vendorCode')"
          prop="vendorCode"
        />
        <el-table-column
          width="150"
          :label="$t('purchaseDemand.vendorName')"
          prop="vendorName"
          show-overflow-tooltip
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.quota')"
          prop="quota"
        >
          <template slot-scope="scope">
            <el-input
              v-model.number="scope.row.quota"
              @input="quotaChangeHandle(scope)"
            >
              <template
                slot="suffix"
              >
                %
              </template>
            </el-input>
          </template>
        </el-table-column>
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.orderQuota')"
          prop="orderQuota"
        />
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.notaxPrice')"
          prop="notaxPrice"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.priceUnit')"
          prop="priceUnit"
        />
        <el-table-column
          width="130"
          :label="$t('purchaseDemand.currency')"
          prop="currency"
          :formatter="formatterCurrency"
        />
        <el-table-column
          width="100"
          :label="$t('purchaseDemand.requirementQuantity')"
          prop="requirementQuantity"
        />
      </el-table>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="orderVisible = false"
        >
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="submitOrder"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import {
  getDictItemList,
  getAllPurUnit,
  getAllPurTax,
  getAllPurCurrency
} from '@/api/common'
import CPeopleSelector from '@/library/components/c-people-selector'
import { SOURCE_TYPE } from './enum'
import { managementAndPurchaseApi } from 'modb@/purchasingDemand/api'

export default {
  name: 'ManagementAndPurchase',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPeopleSelector
  },
  provide () {
    return { context: this }
  },
  data () {
    const userInfo = this.$store.getters.user.userInfo
    const { permissions = [] } = userInfo
    return {
      name: 'managementAndPurchase',
      permissions,
      sourceVisible: false,
      orderVisible: false,
      activeDims: ['1', '2'],
      orderRows: [],
      peopleDialog: false,
      mergeDialogVisible: false,
      tableName: 'managementAndPurchase',
      reviewFormNumber: '',
      selectDictionary: {},
      mergedRows: [],
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      currentRow: {},
      sourceType: SOURCE_TYPE.BID,
      sourceSelect: [
        { label: this.$t('purchaseDemand.bidding'), id: SOURCE_TYPE.BID }, // 招标
        { label: this.$t('purchaseDemand.priceComparison'), id: SOURCE_TYPE.INQ }// 询比价
      ],
      title: this.$t('common.edit'),
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'itemCode',
          label: () => this.$t('purchaseDemand.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // {
        //   prop: "materialName",
        //   label: () => this.$t("purchaseDemand.itemName"),//物料名称
        //   type: "quicksearch",
        //   showKey: "materialName",
        //   name: "scc_base_material_item"
        // },
        { prop: 'applyStatus', label: () => this.$t('purchaseDemand.applyStatus'), type: 'dict', code: 'APPLICATION_STATUS' },
        { prop: 'purchaseOrganization', label: () => this.$t('purchaseDemand.fullPathId') },
        { prop: 'categoryName', label: () => this.$t('purchaseDemand.categoryName') },
        { prop: 'costType', label: () => this.$t('purchaseDemand.costType'), type: 'dict', code: 'COST_TYPE' },
        { prop: 'requirementDepartment', label: () => this.$t('purchaseDemand.requirementDepartment') },
        { prop: 'costNum', label: () => this.$t('purchaseDemand.costNum') },
        { prop: 'requirementHeadNum', label: () => this.$t('purchaseDemand.requirementHeadNum') }
      ],
      queryParam: {},
      costType: [],
      applyStatus: [],
      requirementSource: [],
      haveSupplier: [],
      unitList: [],
      currencyList: [],
      taxList: [],
      haveEffectivePrice: []
    }
  },
  created () {
    this.title = this.$t('common.edit')
    this.tableHeader = [
      {
        prop: 'categoryName',
        label: () => this.$t('purchaseDemand.categoryName'),
        width: 100
      },
      { prop: 'rowNum', label: () => this.$t('purchaseDemand.rowNum'), width: 100 },
      {
        prop: 'applyStatus',
        label: () => this.$t('purchaseDemand.applyStatus'),
        width: 100,
        dataType: 'dict',
        code: 'APPLICATION_STATUS'
      },
      { prop: 'rejectReason', label: () => this.$t('purchaseDemand.rejectReason'), width: 130 },
      { prop: 'requirementHeadNum', label: () => this.$t('purchaseDemand.requirementHeadNum'), width: 130 },
      { prop: 'purchaseOrganization', label: () => this.$t('purchaseDemand.fullPathId'), width: 130 },
      // { prop: "requirementDepartment", label: ()=>this.$t('purchaseDemand.requirementDepartment'), width: 100 },
      { prop: 'buyer', label: () => this.$t('purchaseDemand.buyer'), width: 100 },
      { prop: 'buyerName', label: () => this.$t('purchaseDemand.buyerName'), width: 100 },
      { prop: 'itemCode', label: () => this.$t('purchaseDemand.itemCode'), width: 130 },
      { prop: 'itemDesc', label: () => this.$t('purchaseDemand.itemName'), width: 100 },
      {
        prop: 'currentInventory',
        label: () => this.$t('purchaseDemand.currentInventory'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: row =>
          row.applyStatus !== 'APPLICATION_STATUS' &&
          this.hasPermission('managementAndPurchase_remark')
      },
      { prop: 'requirementQuantity', label: () => this.$t('purchaseDemand.requirementQuantity'), width: 100 },
      { prop: 'orderQuantity', label: () => this.$t('purchaseDemand.orderQuantity'), width: 100 },
      {
        prop: 'requirementDate',
        label: () => this.$t('purchaseDemand.requirementDate'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'unit',
        label: () => this.$t('purchaseDemand.unit'),
        width: 100,
        formattor: val => this.getLabel(this.unitList, val)
      },
      {
        prop: 'haveSupplier',
        label: () => this.$t('purchaseDemand.haveSupplier'),
        width: 100,
        dataType: 'dict',
        code: 'SOURCING_SUPPLIER'
      },
      {
        prop: 'haveEffectivePrice',
        label: () => this.$t('purchaseDemand.haveEffectivePrice'),
        width: 100,
        dataType: 'dict',
        code: 'EFFECTIVE_PRICE'
      },
      {
        prop: 'requirementSource',
        label: () => this.$t('purchaseDemand.requirementSource'),
        width: 100,
        dataType: 'dict',
        code: 'DEMAND_SOURCE'
      },
      { prop: 'externalApplyCode', label: () => this.$t('purchaseDemand.externalApplyCode'), width: 100 },
      { prop: 'externalApplyRowNum', label: () => this.$t('purchaseDemand.externalApplyRowNum'), width: 100 },
      { prop: 'requirementDepartment', label: () => this.$t('purchaseDemand.requirementDepartment'), width: 100 },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('purchaseDemand.createdBy'),
        width: 100
      },
      { prop: 'createdFullName', label: () => this.$t('purchaseDemand.createdFullName'), width: 100 },
      // { prop: "requirementDate", label: ()=>this.$t('purchaseDemand.requirementDate'), width: 100, formattor: val => val ? parseTime(val, "{y}-{m}-{d}") : "" },
      { prop: 'followFormCode', label: () => this.$t('purchaseDemand.followFormCode'), width: 100 },
      // { prop: "followFormName", label: ()=>this.$t('purchaseDemand.followFormName'), width: 100 },
      {
        prop: 'internalComments',
        label: () => this.$t('purchaseDemand.internalComments'),
        width: 100,
        showType: 'input',
        editable: row =>
          row.applyStatus !== 'APPLICATION_STATUS' &&
          this.hasPermission('managementAndPurchase_remark')
      },
      {
        prop: 'externalComments',
        label: () => this.$t('purchaseDemand.externalComments'),
        width: 100,
        showType: 'input',
        editable: row =>
          row.applyStatus !== 'APPLICATION_STATUS' &&
          this.hasPermission('managementAndPurchase_remark')
      },
      {
        prop: 'costType',
        label: () => this.$t('purchaseDemand.costType'),
        width: 100,
        dataType: 'dict',
        code: 'COST_TYPE'
      },
      { prop: 'costNum', label: () => this.$t('purchaseDemand.costNum'), width: 100 },
      { prop: 'brand', label: () => this.$t('purchaseDemand.brand'), width: 100 },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.edit(row),
            code: 'managementAndPurchase_remark',
            show: row => row.applyStatus !== 'APPLICATION_STATUS',
            formattor: () => {
              return this.$t('common.save')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.initDictionary()
  },
  methods: {
    quotaChangeHandle (scope) {
      const { quota, orderQuantity } = scope.row
      console.log(quota, orderQuantity)
      scope.row.orderQuota = (quota * orderQuantity) / 100
    },
    hasPermission (code) {
      // TODO: 正式配置完所有权限之后 需要去掉这个判断
      if (!code) return true
      const permissions = this.permissions
      if (permissions.length) {
        return permissions.findIndex(i => i === code) > -1
      } else {
        return false
      }
    },
    formatterApplyStatus (row, column, cellValue, index) {
      return this.getLabel(this.applyStatus, cellValue)
    },
    formatterUnit (row, column, cellValue, index) {
      return this.getLabel(this.unitList, cellValue)
    },
    formatterTax (row, column, cellValue, index) {
      return this.getLabel(this.taxList, cellValue)
    },
    formatterCurrency (row, column, cellValue, index) {
      return this.getLabel(this.currencyList, cellValue)
    },
    formatterCostType (row, column, cellValue, index) {
      return this.getLabel(this.costType, cellValue)
    },
    formatterDate (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    submitMerge () {
      const requirementLineIds = this.currentRows
        .map(i => i.requirementLineId)
        .join(',')
        managementAndPurchaseApi.bachRequirementMerge(requirementLineIds).then(res => {
        this.$message({ type: 'success', message: res.message })
        this.mergeDialogVisible = false
        this.getQuerydata()
      })
    },
    back () {
      this.dialogFormVisible = false
    },
    initDictionary () {
      // 获取所有币种
      getAllPurCurrency().then(res => {
        this.currencyList = adaptDictData(res.data, 'currency')
      })
      // 获取所有税率
      getAllPurTax().then(res => {
        this.taxList = adaptDictData(res.data, 'tax')
      })
      // 获取所有单位
      getAllPurUnit().then(res => {
        this.unitList = adaptDictData(res.data, 'unit')
      })
      const codes = [
        'COST_TYPE',
        'APPLICATION_STATUS',
        'DEMAND_SOURCE',
        'EFFECTIVE_PRICE',
        'SOURCING_SUPPLIER'
      ].map(i => ({
        dictCode: i
      }))
      getDictItemList(codes).then(res => {
        const [
          COST_TYPE,
          APPLICATION_STATUS,
          DEMAND_SOURCE,
          EFFECTIVE_PRICE,
          SOURCING_SUPPLIER
        ] = res.data
        this.costType = adaptDictData(COST_TYPE.COST_TYPE)
        this.applyStatus = adaptDictData(APPLICATION_STATUS.APPLICATION_STATUS)
        this.requirementSource = adaptDictData(DEMAND_SOURCE.DEMAND_SOURCE)
        this.haveEffectivePrice = adaptDictData(
          EFFECTIVE_PRICE.EFFECTIVE_PRICE
        )
        this.haveSupplier = adaptDictData(SOURCING_SUPPLIER.SOURCING_SUPPLIER)
        this.selectDictionary = {
          applyStatus: this.applyStatus,
          costType: this.costType
        }
      })
    },
    createSourcing () {
      if (!this.currentRows.length) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.minLimitMsg')
        })
        return
      }
      // if (this.currentRows.length > 1) {
      //   this.$message({
      //     type: "warning",
      //     message: this.$t("purchaseDemand.maxLimitMsg")
      //   });
      //   return;
      // }
      this.sourceVisible = true
    },
    submitGenSource () {
      const data = {
        requirementLineList: this.currentRows,
        businessGenType: this.sourceType
      }
      managementAndPurchaseApi.genSourceBusiness(data).then(res => {
        this.$message({ type: 'success', message: res.message })
        this.sourceVisible = false
        this.getQuerydata()
      })
    },
    pull () {},
    getPeople (data) {
      const { userId, nickname, username } = data[0]
      if (data.length > 0) {
        const params = {
          requirementLineIds: this.currentRows
            .map(i => i.requirementLineId)
            .join(','),
          applyStatus: 'ASSIGNED',
          buyerId: userId,
          buyerName: nickname,
          buyer: username
        }
        managementAndPurchaseApi.bachAssigned(params).then(res => {
          this.$message({ message: res.message, type: 'success' })
          this.getQuerydata()
        })
      }
    },
    allot () {
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.allotTips1')
        })
}
      const submitStaus = ['UNASSIGNED']
      if (
        this.currentRows.some(
          i => submitStaus.findIndex(j => j === i.applyStatus) === -1
        )
      ) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.allotTips2')
        })
        return
      }
      this.isAssigned = true
      this.peopleDialog = true
    },
    cancelAllot () {
      const data = this.currentRows
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.cancelAllotTips1')
        })
      }
      const submitStaus = ['ASSIGNED']
      if (
        this.currentRows.some(
          i => submitStaus.findIndex(j => j === i.applyStatus) === -1
        )
      ) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.cancelAllotTips2')
        })
        return
      }
      const params = {
        requirementLineIds: this.currentRows
          .map(i => i.requirementLineId)
          .join(','),
        applyStatus: 'UNASSIGNED'
        // buyerId: '',
        // buyerName: ''
      }
      managementAndPurchaseApi.bachAssigned(params).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    save (row) {
      const { taxRate, ...rest } = row
      const data = {
        taxKey: (this.taxList.find(i => i.value === taxRate) || {}).key || '',
        taxRate,
        ...rest
      }
      managementAndPurchaseApi.modifyLine(data).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
        this.back()
      })
    },
    exportRemark () {},
    refuse () {
      if (!this.currentRows.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.selectRefuseData')
        })
      }
      if (this.currentRows.some(i => i.applyStatus !== 'UNASSIGNED')) {
        return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.cancelBeforeRefuse')
        })
      }
      const requirementLineIds = this.currentRows
        .map(i => i.requirementLineId)
        .join(',')
      this.$prompt(
        this.$t('purchaseDemand.rejectPrompt'),
        this.$t('purchaseDemand.rejectPromptTitle'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )
        .then(({ value }) => {
          managementAndPurchaseApi.bachRejectRequirement({
              requirementLineIds,
              rejectReason: value
            })
            .then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.getQuerydata()
            })
        })
        .catch(() => {})
    },
    createOrder () {
      if (!this.currentRows.length) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.minLimitMsg')
        })
        return
      }
      // if (this.currentRows.length > 1) {
      //   this.$message({
      //     type: "warning",
      //     message: this.$t("purchaseDemand.maxLimitMsg")
      //   });
      //   return;
      // }
      const flag = this.currentRows.some(i => {
        const { haveSupplier, haveEffectivePrice } = i
        return haveEffectivePrice !== 'Y' || haveSupplier !== 'Y'
      })
      if (flag) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.orderCreateMsg1')
        })
        return
      }
      managementAndPurchaseApi.listRecommendVendor(this.currentRows).then(res => {
        this.orderVisible = true
        const obj = (res.data || []).reduce((n, i) => {
          n[i.requirementLineId]
            ? n[i.requirementLineId].push(i)
            : (n[i.requirementLineId] = [i])
          return n
        }, {})
        this.orderRows = Object.values(obj).reduce((n, i) => n.concat(i), [])
      })
    },
    async merge () {
      if (this.currentRows.length > 1) {
        const requirementLineIds = this.currentRows
          .map(i => i.requirementLineId)
          .join(',')
        const res = await managementAndPurchaseApi.checkMergeRequirement(
          requirementLineIds
        )
        this.mergeDialogVisible = true

        const rs = await managementAndPurchaseApi.findRequirementMergeList(
          requirementLineIds
        )

        this.mergedRows = rs.data
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.minLimitMsg')
        })
      }
    },
    edit (row) {
      this.save(row)
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    submitOrder () {
      console.log(this.orderRows)
      if (this.orderRows.some(i => !i.quota && i.quota !== 0)) {
        this.$message({
          type: 'error',
          message: this.$t('purchaseDemand.orderCreateMsg2')
        })
        return
      }
      const group = this.orderRows
        .filter(i => i.quota > 0)
        .reduce((n, i) => {
          const { requirementLineId } = i
          if (n[requirementLineId]) {
            n[requirementLineId].push(i)
          } else {
            n[requirementLineId] = [i]
          }
          return n
        }, {})
      const flag = Object.values(group).some(
        g => g.map(i => i.quota).reduce((n, i) => n + i, 0) > 100
      )
      if (flag) {
        this.$message({
          type: 'error',
          message: this.$t('purchaseDemand.orderCreateMsg3')
        })
        return
      }
      const formatData = this.orderRows
        .filter(i => i.quota > 0)
        .map(i => {
          const { quota, taxKey, ...rest } = i
          const formatItem = {
            quota: quota / 100,
            taxKey,
            taxRate:
              (this.taxList.find(i => i.value === taxKey) || {}).key || '',
            ...rest
          }
          return formatItem
        })
      if (formatData.length) {
        managementAndPurchaseApi.genOrder(formatData).then(res => {
          this.$message({ type: 'success', message: res.message })
          this.getQuerydata()
          this.orderVisible = false
        })
      } else {
        this.$message({
          type: 'error',
          message: this.$t('purchaseDemand.submitOrderTips1')
        })
      }
    },
    exportOne () {}
  }
}
</script>
<style scoped lang="scss">
</style>
