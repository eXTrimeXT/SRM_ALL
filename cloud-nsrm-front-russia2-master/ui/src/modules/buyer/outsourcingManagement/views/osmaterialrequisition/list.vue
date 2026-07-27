<template>
  <el-container
    class="flex-container osmaterialrequisition_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
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
            {{
              $t("common.add")
            }}
          </AuthorityButton>
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
        :source="osMaterialRequisitionApi.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import osmaterialrequisitionEdit from './edit.vue'
import { parseTime } from '@/utils'
import { osMaterialRequisitionApi } from 'modb@/outsourcingManagement/api'

export default {
  name: 'OsmaterialrequisitionList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      osMaterialRequisitionApi: osMaterialRequisitionApi,
      name: 'osmaterialrequisitionList',
      tableName: 'osmaterialrequisitionTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'materialRequisitionNumber',
          label: this.$t('outsource.materialReqNum'),  // '委外领料单号'
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne(row),
          formattor (val) {
            return val || '--'
          }
        },
        {
          prop: 'orgName',
          label: this.$t('components.organization.ORG'),  // '业务实体'
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: this.$t('components.organization.INV'),  // '库存组织'
          minWidth: 150
        },
        {
          prop: 'requirementHeadNum',
          label: this.$t('cusEntry.supplement20250211.purchaseRequest'),  // '采购申请'
          minWidth: 150
        },
        {
          prop: 'orderNumber',
          label: this.$t('route.buyerPurchaseOrder'),  //'采购订单'
          minWidth: 150
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),  // '物料编码'
          minWidth: 150
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'),  // '物料名称'
          minWidth: 150
        },
        {
          prop: 'orderNum',
          label: this.$t('bidMod.purchaseOrderQuantity'),  // '采购订单数量'
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),  // '供应商编码'
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName'),  // '供应商名称'
          minWidth: 150
        },
        {
          prop: 'handleStatus',
          label: this.$t('purchaseDemand.handleStatus'),  // '处理状态'
          minWidth: 150,
          dataType: 'dict',
          code: 'MATERIAL_REQUISITION_STATUS'

        },
        {
          prop: 'createdBy',
          label: this.$t('common.creator'),  // '创建人'
          minWidth: 150
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),  // '创建时间'
          minWidth: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: this.$t('components.headers.operation'),  // '操作'
          width: 170,
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => (row.handleStatus === 'CREATE' || row.handleStatus === 'VSUBMIT'),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.replyHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.handleStatus === 'REFUSE',
              formattor: () => {
                return this.$t('bidMod.reply')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.handleStatus === 'CREATE',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'materialRequisitionNumber', label: this.$t('outsource.materialReqNum') },  //'委外领料单号'
        {
          prop: 'handleStatus',
          label: () => this.$t('purchaseDemand.applyStatus'),
          type: 'dict',
          code: 'MATERIAL_REQUISITION_STATUS'
        },
        { prop: 'createdBy', label: this.$t('common.creator') },  // '创建人'
        {
          prop: 'creationStartDate',
          label: () => this.$t('purchaseDemand.startApplyDate'),
          type: 'date'
        }, // 申请开始日期
        {
          prop: 'creationEndDate',
          label: () => this.$t('purchaseDemand.endApplyDate'),
          type: 'date'
        } // 申请结束日期
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
    // 单据状态
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    replyHandle (row) {
      this.mode = 'reply'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: false
        },
        // title: '委外领料单',
        title: this.$t('route.outsourceMaterials'),
        name: 'osmaterialrequisitionEdit' + row.materialRequisitionId
      }
      this.$emit('tab-add', tab)
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          osMaterialRequisitionApi.delete(row.materialRequisitionId).then(res => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode
        },
        // '委外领料单新增'
        title: this.$t('cusEntry.supplement20250211.outsourceMaterialRequisitionAdd'),
        name: 'osmaterialrequisitionEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode
        },
        // '委外领料单编辑'
        title: this.$t('cusEntry.supplement20250211.outsourceMaterialRequisitionEdit'),
        name: 'osmaterialrequisitionEdit' + row.materialRequisitionId
      }
      this.$emit('tab-add', tab)
    },
    readOne (row) {
      this.mode = 'view'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        // '委外领料单'
        title: this.$t('route.outsourceMaterials'),
        name: 'osmaterialrequisitionEdit' + row.materialRequisitionId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
