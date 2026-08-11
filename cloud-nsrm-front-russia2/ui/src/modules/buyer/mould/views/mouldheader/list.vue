<template>
  <el-container
    class="flex-container mouldheader_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            :disabled="currentRows.length === 0"
            @click="scrapHandle"
          >
            {{ $t('mould.scrap') }}
          </AuthorityButton>
          <AuthorityButton
            :disabled="currentRows.length === 0"
            @click="changeHandle"
          >
            {{ $t('mould.transfer') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-sup-ce/mould/mouldheader/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :timeout="1000000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="mouldheader.list"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { numberToPercent } from '@/library/utils/number'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import mouldheaderEdit from './edit.vue'
import mouldScrap from './mouldScrap.vue'
import mouldlog from './mouldlog.vue'
import mouldChange from './mouldChange.vue'
import ExportExcel from 'lib@/components/export-excel'
import { mouldheader } from 'modb@/mould/api'
export default {
  name: 'MouldheaderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      mouldheader: mouldheader,
      name: 'mouldheaderList',
      tableName: 'mouldheaderTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {
        mouldTypeCode: 'MOULD_TYPE',
        mouldStatusCode: 'MOULD_STATUS',
        approveStatus: 'MOULD_APPROVE_STATUS',
        storageEntity: 'STORAGE_ENTITY',
        mouldItemType: 'MOULD_ITEM_TYPE',
        itemProductType: 'ITEM_PRODUCT_TYPE',
        mouldShareFlag: 'YES_OR_NO',
        mouldShareStatus: 'MOULD_SHARE_STATUS',
        mouldReturnFlag: 'YES_OR_NO',
        originalMouldFlag: 'ORIGINAL_MOULD_FLAG',
        machiningTypeCode: 'MACHINING_TYPE',
        mouldPriceAssumerCode: 'MOULD_PRICE_ASSUMER',
        mouldContinuityFlag: 'YES_OR_NO'
      },
      filterParams: {},
      tableHeader: [],

      filterConfig: [
        { prop: 'mouldName', label: () => this.$t('mould.mouldName') }, // 模具名称
        { prop: 'itemNumber', label: () => this.$t('mould.itemNumber') }, // 物料编码
        {
          prop: 'orgId',
          label: () => this.$t('mould.orgId'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: false
        },
        {
          prop: 'mouldStatusCode',
          label: () => this.$t('mould.mouldStatus'), // '管理状态'
          type: 'dict',
          code: 'MOULD_STATUS'
        },
        {
          prop: 'mouldTypeCode',
          label: () => this.$t('mould.mouldType'), // '模具类型'
          type: 'dict',
          code: 'MOULD_TYPE'
        },
        {
          prop: 'mouldReturnFlag',
          label: () => this.$t('mould.mouldReturnFlag'), // '模具返还状态'
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'mouldShareFlag',
          label: () => this.$t('mould.mouldShareFlag'), // '进行分摊'
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'mouldShareStatus',
          label: () => this.$t('mould.mouldShareStatus'), // '分摊状态'
          type: 'dict',
          code: 'MOULD_SHARE_STATUS'
        },
        {
          prop: 'storageEntity',
          label: () => this.$t('mould.storageEntity'), // '存放对象'
          type: 'dict',
          code: 'STORAGE_ENTITY'
        },
        { prop: 'supplierCode', label: () => this.$t('mould.supplierCode') }, // 供应商编码
        {
          prop: 'mouldItemType',
          label: () => this.$t('mould.mouldItemType'), // '模具与制件对应关系'
          type: 'dict',
          code: 'MOULD_ITEM_TYPE'
        },
        {
          prop: 'originalMouldFlag',
          label: () => this.$t('mould.originalMouldFlag'), // '原模/备模'
          type: 'dict',
          code: 'ORIGINAL_MOULD_FLAG'
        }
      ],
      queryParam: {},
      mouldStatusOpts: [],
      haveSupplierFlagOpts: []
    }
  },
  created () {
    const _this = this
    this.tableHeader = [
      {
        prop: 'mouldCode',
        label: this.$t('mould.mouldNumber'),
        width: 100
      },
      {
        prop: 'mouldName',
        label: this.$t('mould.mouldName'),
        width: 100
      },
      {
        prop: 'orgName',
        label: this.$t('mould.orgId'),
        width: 100
      },
      {
        prop: 'itemNumber',
        label: this.$t('common.materialCode'),
        width: 100
      },
      {
        prop: 'itemDescZhs',
        label: this.$t('common.materialName'),
        width: 100
      },
      {
        prop: 'mouldTypeCode',
        label: this.$t('mould.mouldType'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_TYPE'

      },
      {
        prop: 'mouldStatusCode',
        label: this.$t('mould.mouldStatus'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_STATUS'

      },
      {
        prop: 'approveStatus',
        label: this.$t('mould.moldAccountStatus'),
        width: 120,
        dataType: 'dict',
        code: 'MOULD_APPROVE_STATUS'
      },
      {
        prop: 'storageEntity',
        label: this.$t('mould.storageEntity'),
        width: 100,
        dataType: 'dict',
        code: 'STORAGE_ENTITY'

      },
      {
        prop: 'supplierCode',
        label: this.$t('common.vendorCode'),
        width: 100
      },
      {
        prop: 'supplierName',
        label: this.$t('common.vendorName'),
        width: 100
      },
      {
        prop: 'storageAddress',
        label: this.$t('mould.storageAddress'),
        width: 100
      },
      {
        prop: 'mouldShiftTime',
        label: this.$t('mould.mouldShiftTime'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'supplierResponerName',
        label: this.$t('mould.supplierResponer'),
        width: 100
      },
      {
        prop: 'supplierResponerEmail',
        label: this.$t('mould.supplierResponerEmail'),
        width: 100
      },
      {
        prop: 'purchaserResponerName',
        label: this.$t('mould.purchaserResponer'),
        width: 100
      },
      {
        prop: 'purchaserResponerEmail',
        label: this.$t('mould.purchaserResponerEmail'),
        width: 100
      },
      {
        prop: 'mouldHoleNumber',
        label: this.$t('mould.mouldHoleNumber'),
        width: 100
      },
      {
        prop: 'mouldItemType',
        label: this.$t('mould.mouldWorkpieceType'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_ITEM_TYPE'

      },
      {
        prop: 'sameShareId',
        label: this.$t('mould.sameShareId'),
        width: 100
      },
      {
        prop: 'itemProductType',
        label: this.$t('mould.itemProductType'),
        width: 100,
        dataType: 'dict',
        code: 'ITEM_PRODUCT_TYPE'

      },
      {
        prop: 'mouldShareFlag',
        label: this.$t('mould.mouldShareFlag'),
        width: 100,
        dataType: 'dict',
        code: 'YES_OR_NO'

      },
      {
        prop: 'mouldShareStatus',
        label: this.$t('mould.mouldShareStatus'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_SHARE_STATUS'

      },
      {
        prop: 'totalShareAmount',
        label: this.$t('mould.totalShareAmount'),
        width: 100
      },
      {
        prop: 'sharedAmount',
        label: this.$t('mould.sharedAmount'),
        width: 100
      },
      {
        prop: 'sharedNum',
        label: this.$t('mould.sharedNum'),
        width: 100
      },
      {
        prop: 'sharedPercent',
        label: this.$t('mould.sharedPercent'),
        width: 100,
        formattor (val) {
          return numberToPercent(val)
        }
      },
      {
        prop: 'predictUseTime',
        label: this.$t('mould.predictUseTime'),
        width: 100
      },
      {
        prop: 'usedTime',
        label: this.$t('mould.usedTime'),
        width: 100
      },
      {
        prop: 'usedPercent',
        label: this.$t('mould.usedPercent'),
        width: 100,
        formattor (val) {
          return numberToPercent(val)
        }
      },
      {
        prop: 'mouldReturnFlag',
        label: this.$t('mould.isMouldReturn'),
        width: 100,
        dataType: 'dict',
        code: 'YES_OR_NO'

      },
      {
        prop: 'mouldReturnNum',
        label: this.$t('mould.mouldReturnNum'),
        width: 100
      },
      {
        prop: 'totalReturnTime',
        label: this.$t('mould.totalReturnTime'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'originalMouldFlag',
        label: this.$t('mould.mouldFlagOriginal'),
        width: 100,
        dataType: 'dict',
        code: 'ORIGINAL_MOULD_FLAG'
      },
      {
        prop: 'processingProcedure',
        label: this.$t('mould.processingProcedure'),
        width: 100
      },
      {
        prop: 'formingCycle',
        label: this.$t('mould.formingCycle'),
        width: 100
      },
      {
        prop: 'shrinkage',
        label: this.$t('mould.shrinkage'),
        width: 100
      },
      {
        prop: 'surplusSize',
        label: this.$t('mould.surplusSize'),
        width: 100
      },
      {
        prop: 'materialSpec',
        label: this.$t('mould.materialSpec'),
        width: 100
      },
      {
        prop: 'closingHeight',
        label: this.$t('mould.closingHeight'),
        width: 100
      },
      {
        prop: 'mouldTotalWeight',
        label: this.$t('mould.mouldTotalWeight'),
        width: 100
      },
      {
        prop: 'nozzleWeight',
        label: this.$t('mould.nozzleWeight'),
        width: 100
      },
      {
        prop: 'applyOrgName',
        label: this.$t('purchaseDemand.ceeaDepartment'),
        width: 100
      },
      {
        prop: 'applyId',
        label: this.$t('mould.applyPersonId'),
        width: 100
      },
      {
        prop: 'machiningTypeCode',
        label: this.$t('mould.machiningTypeCode'),
        width: 100,
        dataType: 'dict',
        code: 'MACHINING_TYPE'
      },
      {
        prop: 'mouldPriceAssumerCode',
        label: this.$t('mould.mouldPriceAssumerCode'),
        width: 100,
        dataType: 'dict',
        code: 'MOULD_PRICE_ASSUMER'
      },
      {
        prop: 'openMoldTime',
        label: this.$t('mould.openMoldDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'warehousingTime',
        label: this.$t('orderMod.warehouseDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'ceeaRequirementHeadNum',
        label: this.$t('mould.ceeaRequirementHeadNum'),
        width: 100
      },
      {
        prop: 'sourceNumber',
        label: this.$t('bidMod.approvalNo'),
        width: 100
      },
      {
        prop: 'orderNumber',
        label: this.$t('orderMod.orderNumber'),
        width: 100
      },
      {
        prop: 'warehouseReceiptNumber',
        label: this.$t('purSettlementMod.warehouseReceiptNumber'),
        width: 100
      },
      {
        prop: 'contractNo',
        label: this.$t('contractMod.contractNo'),
        width: 100
      },
      {
        prop: 'maintainCycle',
        label: this.$t('mould.maintainCycle'),
        width: 100
      },
      {
        prop: 'maintainCostTime',
        label: this.$t('mould.maintainCostTime'),
        width: 100
      },
      {
        prop: 'maintainTime',
        label: this.$t('mould.maintainTime'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'maintainNextTime',
        label: this.$t('mould.maintainNextTime'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'mouldContinuityFlag',
        label: this.$t('mould.mouldContinuityFlag'),
        width: 100,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('mould.creationDate'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'lastUpdatedBy',
        label: this.$t('priceTemplate.lastUpdateBy'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('contractMod.lastUpdateDate'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 160,
        buttons: [
          {
            callback: row => this.updateHandle(row),
            show: row => row.approveStatus === 'APPROVAL',
            formattor: () => {
              return this.$t('common.modify')
            }
          },
          {
            callback: row => this.viewHandle(row),
            // 不等于审批中和已审批
            show: row => row.approveStatus === 'SUBMITTED' || row.approveStatus === 'APPROVAL',
            formattor: () => {
              return this.$t('common.view')
            }
          },
          {
            callback: row => this.editHandle(row),
            show: row => row.approveStatus !== 'SUBMITTED' &&
              row.approveStatus !== 'APPROVAL' &&
              row.approveStatus !== 'ABANDONED',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.deleteHandle(row),
            // code: "pr:requirementApply:edit",
            // show: row => row.status === "DRAFT",
            show: row => row.approveStatus !== 'SUBMITTED' &&
              row.approveStatus !== 'APPROVAL' &&
              row.approveStatus !== 'ABANDONED',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: row => this.logHandle(row),
            formattor: () => {
              return this.$t('mould.log')
            }
          }
        ],
        selectList: []
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.getQuerydata()
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          mouldheader.delete(row.mouldHeaderId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {
        })
    },
    logHandle (row) {
      const tab = {
        component: mouldlog,
        params: {
          mouldCode: row.mouldCode
        },
        title: this.$t('mould.moldDatingInsert') + row.mouldCode
      }
      this.$emit('tab-add', tab)
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: mouldheaderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'mouldheaderEdit'
        },
        title: this.$t('mould.newMoldLedger'),
        name: 'mouldheaderEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 转移
    changeHandle () {
      let vailStr = ''
      for (let i = 0; i < this.currentRows.length; i++) {
        const item = this.currentRows[i]
        if (item.approveStatus !== 'APPROVAL') {
          vailStr += item.mouldCode + this.$t('mould.moldNotApproved')
        }
      }
      if (vailStr !== '') {
        this.$message.error(vailStr)
        return
      }
      this.mode = 'change'
      const tab = {
        component: mouldChange,
        params: {
          flag: this.mode,
          dataList: this.currentRows
        },
        title: this.$t('mould.transferMold'),
        name: 'mouldChangeEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 报废
    scrapHandle () {
      // 校验选择的内容必须是新建已审批通过并且不是待报废的
      let vailStr = ''
      for (let i = 0; i < this.currentRows.length; i++) {
        const item = this.currentRows[i]
        if (item.approveStatus !== 'APPROVAL') {
          vailStr += item.mouldCode + this.$t('mould.notDiscard')
        }
        if (item.mouldStatusCode === '4' || item.mouldStatusCode === '5') {
          vailStr += item.mouldCode + this.$t('mould.notScrappedAgain')
        }
      }
      if (vailStr !== '') {
        this.$message.error(vailStr)
        return
      }
      this.mode = 'scrap'
      const tab = {
        component: mouldScrap,
        params: {
          flag: this.mode,
          dataList: this.currentRows
        },
        title: this.$t('mould.moldScrapped'),
        name: 'mouldScrapEdit'
      }
      this.$emit('tab-add', tab)
    },
    viewHandle (row) {
      this.mode = 'view'
      const tab = {
        component: mouldheaderEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: true,
          tabName: 'mouldheaderEdit' + row.mouldHeaderId
        },
        title: this.$t('mould.checkMoldledger'),
        name: 'mouldheaderEdit' + row.mouldHeaderId
      }
      this.$emit('tab-add', tab)
    },
    updateHandle (row) {
      this.mode = 'update'
      const tab = {
        component: mouldheaderEdit,
        params: {
          row,
          flag: this.mode,
          firstFlag: true,
          tabName: 'mouldheaderEdit' + row.mouldHeaderId
        },
        title: this.$t('mould.moldLedgerChange'),
        name: 'mouldheaderEdit' + row.mouldHeaderId
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: mouldheaderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'mouldheaderEdit' + row.mouldHeaderId
        },
        title: this.$t('mould.editMouldLedger'),
        name: 'mouldheaderEdit' + row.mouldHeaderId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    // TODO 没有这个方法, 报错，补充一个空
    syncFilterParams () {}
  }
}
</script>
