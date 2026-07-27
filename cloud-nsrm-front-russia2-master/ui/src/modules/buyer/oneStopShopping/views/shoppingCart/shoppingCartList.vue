<template>
  <el-container
    class="flex-container the_inquiryApprovalFlow_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="pr:shoppingCart:ceeaSetSummaryAndNoticeUser"
            type="primary"
            @click="ceeaSetSummaryAndNoticeUser"
          >
            {{ $t("oneStopShopping.setSummaryAndNoticeUser") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:shoppingCart:submit_get"
            @click="submit_get"
          >
            {{ $t("oneStopShopping.submitGet") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:shoppingCart:createProjectDetail"
            @click="createProjectDetail"
          >
            {{ $t("oneStopShopping.createProject") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:shoppingCart:submit_out"
            @click="submit_out"
          >
            {{ $t("oneStopShopping.submitOut") }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-sup-ce/pr/shopCart/listPage"
            :table-header="tableHeader"
            :filter-params="queryParam"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
          <AuthorityButton
            code="pr:shoppingCart:Batch_maintenance"
            @click="Batch_maintenance"
          >
            {{ $t("vendorMod.batchMaintain") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:shoppingCart:save"
            @click="keep"
          >
            {{ $t("common.save") }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:shoppingCart:ceeaDeleteByIds"
            @click="ceeaDeleteByIds"
          >
            {{ $t("common.delete") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :header-cell-class-name="tableStyle"
        :current-change="handleCurrentChange"
        :check-change="handleCheckChange"
        :checkbox="true"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/pr/shopCart/listPage"
        :open-custom-table="true"
        @afterQuery="afterQuery"
      >
        <template #purchaseType="props">
          <DictSelect
            v-model="props.scope.row.purchaseType"
            code="PURCHASE_TYPE"
            :disabled="props.scope.row.status == 'APPLIED'"
          />
        </template>
        <template #requirementDate="props">
          <el-date-picker
            v-model="props.scope.row.requirementDate"
            value-format="yyyy-MM-dd"
            type="date"
            :format="$formatDatePicker"
            :disabled="props.scope.row.status == 'APPLIED'"
          />
        </template>
        <template #requirementNum="props">
          <el-input
            v-model="props.scope.row.requirementNum"
            type="number"
            :disabled="props.scope.row.status == 'APPLIED'"
            oninput="if(value<0)value=0"
          />
        </template>
      </TableView>

      <!-- 发布弹框 -->
      <NoticeUserDialog
        :visible.sync="sumFormVisible"
        @comfirmSum="comfirmSum"
        @close="sumFormVisible = false"
      />

      <!-- 批量维护 -->
      <BatchMaintainDialog
        :visible.sync="Batch"
        @comfirmBatch="comfirm_Batch"
        @close="Batch = false"
      />

      <!-- 填写退回原因 -->
      <srm-dialog
        size="small"
        :title="$t('oneStopShopping.inputRejectReason')"
        :visible.sync="outFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="pubForm"
          :model="outForm"
          :rules="sumRules"
        >
          <el-form-item
            prop="beginQuote"
            :label="$t('purchaseDemand.rejectReason')"
          >
            <input
              v-model="outReason"
              type="text"
            >
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            @click="outFormVisible = false"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmOut"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import shoppingCartDetail from './shoppingCartDetail'
import ExportExcel from 'lib@/components/export-excel'
import { shoppingCartApi } from 'modb@/oneStopShopping/api'
import NoticeUserDialog from 'modb@/oneStopShopping/views/shoppingCart/components/noticeUserDialog'
import BatchMaintainDialog from 'modb@/oneStopShopping/views/shoppingCart/components/batchMaintainDialog'

export default {
  name: 'ShoppingCartList',
  components: {
    TableView,
    QuickSearch,
    MainHeader,
    FormWrapper,
    shoppingCartDetail,
    ExportExcel,
    NoticeUserDialog,
    BatchMaintainDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      sumFormBatch: {
        purchaseType: '',
        requirementDate: ''
      },
      dictCodes: {
        status: 'SHOP_CART_STATUS',
        purchaseType: 'PURCHASE_TYPE'
      },
      Batch: false,
      approvalFiles: [],
      show_tab2: false,
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      currentRows: [],
      tableName: 'inquiryApprovalFlow',
      tableHeader: [],
      tableData: [],
      tableData2: [],
      funParams: {},
      queryParam: {},
      dialogFormVisible: false,
      editableTabsValue: 'tab1',
      outForm: {},
      sumRules: {},
      sumFormVisible: false,
      outFormVisible: false,
      outReason: '',
      isActive: false,
      preArr: [
        {
          prop: 'materialId',
          label: () => this.$t('common.materialCode'), // 物料编码
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'status',
          label: () => this.$t('common.status'), // 状态
          type: 'dict',
          code: 'SHOP_CART_STATUS'

        },
        {
          prop: 'orgIds',
          label: () => this.$t('quota.org'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },

        {
          prop: 'categoryName',
          label: () => this.$t('common.category'), // 品类
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'organizationIds',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          parentId: 'orgIds',
          type: 'INVorganizationSelector',
          multiple: true
        },
        {
          prop: 'summaryNickname',
          label: () => this.$t('oneStopShopping.summaryUser'), // 汇总人
          type: 'input'
        },
        {
          prop: 'noticeNickname',
          label: () => this.$t('oneStopShopping.noticeUser'), // 通知人
          type: 'input'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator'), // 创建人
          type: 'input'
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationTime'), // 创建时间
          type: 'date'
        }
      ],
      tableList: [],
      filterParams: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'status',
        label: _this.$t('common.status'), // 状态
        align: 'center',
        formattor: val => this.$getDictLabel('SHOP_CART_STATUS', val)
      },
      {
        prop: 'requirementHeadNum',
        align: 'center',
        label: _this.$t('purchaseDemand.requirementHeadNum'), // 申请单号
        width: 120
      },
      {
        prop: 'returnReason',
        align: 'center',
        label: _this.$t('purchaseDemand.rejectReason'), // 退回原因
        minWidth: 150
      },
      {
        prop: 'summaryNickname',
        align: 'center',
        label: _this.$t('oneStopShopping.summaryUser'), // 汇总人
        width: 120
      },
      {
        prop: 'noticeNickname',
        align: 'center',
        label: _this.$t('oneStopShopping.noticeUser'), // 通知人
        width: 100
      },
      {
        prop: 'purchaseType',
        align: 'center',
        label: _this.$t('purchaseDemand.purchaseType'), // 采购类型
        width: 100,
        showType: 'slot',
        slot: 'purchaseType'
      },
      {
        prop: 'requirementDate',
        align: 'center',
        label: _this.$t('purchaseDemand.requirementDate'), // 需求时间
        width: 150,
        showType: 'slot',
        slot: 'requirementDate'
      },
      {
        prop: 'requirementNum',
        align: 'center',
        label: _this.$t('purchaseDemand.requirementQuantity'), // 数量
        width: 100,
        showType: 'slot',
        slot: 'requirementNum'
      },
      {
        prop: 'ifCatalog',
        align: 'center',
        label: _this.$t('purchaseDemand.ceeaIfCatalogMaterial'), // 是否目录化
        width: 120,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
        }
      },
      {
        prop: 'materialCode',
        label: _this.$t('common.materialCode'), // 物料编码
        width: 120,
        align: 'center'
      },
      {
        prop: 'materialName',
        label: _this.$t('materialMainData.materialDesc'), // 物料名称
        width: 180,
        align: 'center'
      },
      {
        prop: 'categoryName',
        label: _this.$t('common.category'), // 品类
        width: 120,
        align: 'center'
      },
      {
        prop: 'specification',
        label: _this.$t('common.specification'), // 规格/型号
        width: 120,
        align: 'center'
      },
      {
        prop: 'unitName',
        label: _this.$t('dataConfMod.unit'), // 单位
        width: 100,
        align: 'center'
      },
      {
        prop: 'orgName',
        label: _this.$t('purchaseDemand.businessEntity'), // 业务实体
        width: 120,
        align: 'center'
      },
      {
        prop: 'organizationName',
        label: _this.$t('purchaseDemand.invOrg'), // 库存组织
        width: 160,
        align: 'center'
      },
      {
        prop: 'contractNo',
        align: 'center',
        label: _this.$t('purchaseDemand.contractNum'), // 合同编号
        width: 120
      },
      {
        prop: 'unitPrice',
        align: 'center',
        label: _this.$t('oneStopShopping.unitPrice'), // 预算单价
        width: 120
      },
      {
        prop: 'currencyName',
        align: 'center',
        label: _this.$t('purchaseDemand.currency') // 币种
      },
      {
        prop: 'supplierCode',
        align: 'center',
        label: _this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'supplierName',
        align: 'center',
        label: _this.$t('common.vendorName'), // 供应商名称
        width: 120
      },
      {
        prop: 'createdUserName', // createdBy
        align: 'center',
        label: _this.$t('common.creator'), // 创建人
        width: 120
      },
      {
        prop: 'creationDate',
        align: 'center',
        label: _this.$t('common.creationTime'), // 创建时间
        width: 150,
        dataType: 'dateTime'
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    tableStyle ({ row, rowIndex, column, columnIndex }) {
      if (columnIndex == 3 || columnIndex == 2) {
        return 'start'
      }
    },
    afterQuery (data) {
      this.tableList = data
    },
    getQuerydata (v) {
      console.log('v', v)
      this.queryParam = v || this.queryParam
      let params = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncQuerydata (values) {
      this.filterParams = values
      // console.log("values",values)
    },
    // 创建申请菜单
    createProjectDetail (row) {
      let ids = []
      // console.log("this.currentRows:", this.currentRows);
      let arrCurrentRows = []
      this.currentRows.forEach(item => {
        let obj = {}
        obj.purchaseType = item.purchaseType // 采购类型
        obj.requirementDate = item.requirementDate // 需求时间
        obj.requirementNum = item.requirementNum // 数量
        obj.summaryNickname = item.summaryNickname // 汇总人昵称
        obj.noticeNickname = item.noticeNickname // 通知人昵称
        obj.status = item.status // 状态
        arrCurrentRows.push(obj)
      })
      let purchaseType = true
      let requirementDate = true
      let requirementNum = true
      let summaryNickname = true
      let summaryNicknameWrong = true
      let noticeNickname = true
      let status = true
      let appled = arrCurrentRows.find(v => v.status == 'APPLIED') // 您勾选的行已生成申请单”，不能重复创建
      arrCurrentRows.forEach(item => {
        if (!item.purchaseType) {
          purchaseType = false
        }
        if (!item.requirementDate) {
          requirementDate = false
        }
        if (!item.requirementNum) {
          requirementNum = false
        }
        if (!item.summaryNickname) {
          summaryNickname = false
        } else {
          if (item.summaryNickname !== this.$store.getters.userInfo.nickname) {
            summaryNicknameWrong = false
          }
        }
        if (!item.noticeNickname) {
          noticeNickname = false
        }
        if (item.status != 'SUBMITTED') {
          status = false
        }
      })

      if (
        purchaseType &&
        requirementDate &&
        requirementNum &&
        summaryNickname &&
        summaryNicknameWrong &&
        noticeNickname &&
        status
      ) {
        this.currentRows.forEach(elm => {
          ids.push(elm.shopCartId)
        })
        console.log('ids', ids)
        shoppingCartApi.ceeaValidRequiredInfo(ids).then(res => {
          if (res.data == this.$t('oneStopShopping.createProjectMsg1')) {
            this.$message({
              type: 'error',
              message: this.$t('oneStopShopping.createProjectMsg1')
            })
          } else if (res.data == this.$t('oneStopShopping.createProjectMsg2')) {
            this.$message({
              type: 'error',
              message: this.$t('oneStopShopping.createProjectMsg2')
            })
          } else if (res.data == this.$t('oneStopShopping.createProjectMsg3')) {
            this.$message({
              type: 'error',
              message: this.$t('oneStopShopping.createProjectMsg3')
            })
          } else if (!res.data) {
            shoppingCartApi.ceeaCreateRequirements(ids).then(res => {
              if (res.data) {
                if (res.data.length > 0) {
                  console.log('res.data', res, 8888)
                  this.$message({
                    type: 'success',
                    message: this.$t('common.success')
                  })
                  this.getQuerydata(this.filterParams) // 禁用刷新

                  this.$confirm(
                    this.$t('oneStopShopping.createProjectMsg4'),
                    this.$t('common.tips'),
                    {
                      confirmButtonText: this.$t('common.confirm'),
                      cancelButtonText: this.$t('common.cancel'),
                      type: 'warning'
                    }
                  )
                    .then(() => {
                      this.$router.push({
                        name: 'purchaseApplication',
                        params: { autoQuery: true }
                      })
                    })
                    .catch(() => {})
                } else {
                  this.$message({
                    type: 'success',
                    message: this.$t('oneStopShopping.createProjectMsg5')
                  })
                  this.getQuerydata()
                }
              }
            })
          }
        })
      } else if (!purchaseType) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg1')
        })
      } else if (!requirementDate) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg2')
        })
      } else if (!requirementNum) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg3')
        })
      } else if (!summaryNickname) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg6')
        })
      } else if (!summaryNicknameWrong) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg7')
        })
      } else if (!noticeNickname) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg8')
        })
      } else if (appled) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg9')
        })
      } else if (!status) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg10')
        })
      }
    },
    handleCurrentChange (val) {
      this.currentRow = val
      console.log('val', val)
    },
    handleCheckChange (val) {
      this.currentRows = val
      console.log('val', val)
    },
    // 点击汇总人按钮
    ceeaSetSummaryAndNoticeUser () {
      let currentRowsCheck = this.currentRows.find(v => v.status !== 'DRAFT')
      // console.log("currentRowsCheck",currentRowsCheck)
      if (currentRowsCheck) {
        this.$message({
          type: 'warning',
          message: this.$t('oneStopShopping.createProjectMsg11')
        })
        return
      }

      if (this.currentRows.length > 0) {
        this.sumFormVisible = true
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('oneStopShopping.createProjectMsg12')
        })
      }
    },
    // 点击批量维护按钮
    Batch_maintenance () {
      this.sumFormBatch.purchaseType = ''
      this.sumFormBatch.requirementDate = ''
      let APPLIEDdata = []
      APPLIEDdata = this.currentRows.find(v => v.status === 'APPLIED')
      console.log('APPLIEDdata', APPLIEDdata)
      if (this.currentRows.length > 0) {
        if (APPLIEDdata) {
          this.$message({
            type: 'warning',
            message: this.$t('oneStopShopping.createProjectMsg13')
          })
          this.Batch = false
        } else {
          this.Batch = true
        }
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('oneStopShopping.createProjectMsg14')
        })
      }
    },
    // 提交汇总人信息
    comfirmSum (sumForm) {
      let submitData = {}
      let ids = []
      this.currentRows.forEach(elm => {
        ids.push(elm.shopCartId)
      })

      submitData = {
        ...sumForm,
        ids: ids
      }

      console.log('[submitData]', submitData)
      shoppingCartApi.ceeaSetSummaryAndNoticeUser(submitData)
        .then(res => {
          if (res.data) {
            console.log('res.data:', res.data)
            this.$message({ type: 'success', message: res.message })
            this.getQuerydata(this.filterParams) // 禁用刷新
            this.sumFormVisible = false
          }
        })
    },
    // 批量维护
    comfirm_Batch (sumFormBatch) {
      // debugger
      let submitData = {}
      let ids = []
      this.currentRows.forEach(elm => {
        ids.push(elm.shopCartId)
      })
      submitData = {
        ...sumFormBatch,
        ids: ids
      }
      //  debugger
      console.log('[submitData]', submitData)
      shoppingCartApi.ceeaBatchUpdateShopCarts(submitData).then(res => {
        // console.log("res", res);
        this.$message({ type: 'success', message: this.$t('common.success') })
        setTimeout(() => {
          this.getQuerydata(this.filterParams)
        }, 1000)
        // 禁用刷新

        this.Batch = false
      })
    },

    // 提交需求
    submit_get () {
      let ids = []
      // console.log("this.currentRows:", this.currentRows);
      let arrCurrentRows = []
      this.currentRows.forEach(item => {
        let obj = {}

        obj.summaryNickname = item.summaryNickname // 汇总人昵称
        obj.noticeNickname = item.noticeNickname // 通知人昵称
        obj.status = item.status // 状态
        obj.purchaseType = item.purchaseType // 采购类型
        obj.requirementDate = item.requirementDate // 需求时间
        obj.requirementNum = item.requirementNum // 数量
        arrCurrentRows.push(obj)
      })

      let summaryNickname = true
      let noticeNickname = true
      let status = true
      let purchaseType = true
      let requirementDate = true
      let requirementNum = true
      arrCurrentRows.forEach(item => {
        if (!item.summaryNickname) {
          summaryNickname = false
        }
        if (!item.noticeNickname) {
          noticeNickname = false
        }
        if (item.status != 'SUBMITTED') {
          status = false
        }
        if (!item.purchaseType) {
          purchaseType = false
        }
        if (!item.requirementDate) {
          requirementDate = false
        }
        if (!item.requirementNum) {
          requirementNum = false
        }
      })

      if (
        summaryNickname &&
        noticeNickname &&
        !status &&
        purchaseType &&
        requirementDate &&
        requirementNum
      ) {
        this.currentRows.forEach(elm => {
          ids.push(elm.shopCartId)
        })
        console.log('ids', ids)
        // 先保存
        let shopCarts = [...this.currentRows]
        shoppingCartApi.ceeaUpdateShopCarts(shopCarts).then(res => {
          // this.$message({ type: "success", message: res.data });
          console.log(res)
          //  this.getQuerydata(this.filterParams); //禁用刷新
        })

        let submitData = {
          status: 'SUBMITTED',
          ids
        }
        shoppingCartApi.ceeaChangeShopCartStatus(submitData).then(res => {
          console.log(res)
          if (res.data) {
            this.$message({ type: 'success', message: res.data })
            this.getQuerydata(this.filterParams) // 禁用刷新
          } else {
            this.$message({ type: 'error', message: res.data })
          }
        })
      } else if (!summaryNickname) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg6')
        })
      } else if (!noticeNickname) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg8')
        })
      } else if (!purchaseType || !requirementDate || !requirementNum) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.submitGetMsg1')
        })
      } else if (status) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.submitGetMsg2')
        })
      }
    },
    // 退回需求
    submit_out () {
      let userId = this.$store.getters.userInfo.userId
      let submitIds = true
      let summaryUserId = true
      this.currentRows.forEach(item => {
        if (item.status == 'DRAFT') {
          submitIds = false
        }
        if (item.summaryUserId !== userId) {
          summaryUserId = false
        }
      })
      console.log('submitIds', submitIds)
      if (submitIds && summaryUserId) {
        this.outFormVisible = true
      } else if (!submitIds) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.submitOutMsg1')
        })
      } else if (!summaryUserId) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.submitOutMsg2')
        })
      }
    },
    // 退回需求弹出框填写原因
    comfirmOut () {
      console.log('outReason:', this.outReason)
      let ids = []
      this.currentRows.forEach(item => {
        ids.push(item.shopCartId)
      })
      let submitData = {
        status: 'DRAFT',
        ids,
        returnReason: this.outReason
      }
      console.log('currentRows', this.currentRows)
      console.log('submit_get', submitData)
      if (this.outReason) {
        shoppingCartApi.ceeaChangeShopCartStatus(submitData).then(res => {
          // console.log(res)
          if (res.data == '操作成功') {
            this.$message({ type: 'success', message: res.data })
            this.outFormVisible = false
            this.getQuerydata(this.filterParams) // 禁用刷新
          } else {
            this.$message({ type: 'error', message: res.data })
            this.outFormVisible = false
          }
        })
      } else {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.submitOutMsg3')
        })
      }
    },
    // 删除未提交状态
    ceeaDeleteByIds () {
      let delIds = true
      this.currentRows.forEach(item => {
        if (item.status !== 'DRAFT') {
          delIds = false
        }
      })
      let ids = []
      this.currentRows.forEach(elm => {
        ids.push(elm.shopCartId)
      })
      console.log('delIds,ids', delIds, '--', ids)
      if (!delIds) {
        this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.createProjectMsg11')
        })
      } else if (delIds) {
        shoppingCartApi.ceeaDeleteByIds(ids).then(res => {
          if (!res.data) {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.getQuerydata(this.filterParams) // 禁用刷新
          }
        })
      }
    },
    // 保存
    keep () {
      let shopCarts = []
      this.tableList.forEach(item => {
        if (item.status == 'DRAFT' || item.status == 'SUBMITTED') {
          shopCarts.push(item)
        }
      })

      shoppingCartApi.ceeaUpdateShopCarts(shopCarts).then(res => {
        this.$message({ type: 'success', message: res.data })
        this.getQuerydata(this.filterParams) // 禁用刷新
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-dialog__body {
  padding-top: 0 !important;
}
:deep(.el-table table th.star div:before) {
  content: "*";
  color: red;
}
</style>
