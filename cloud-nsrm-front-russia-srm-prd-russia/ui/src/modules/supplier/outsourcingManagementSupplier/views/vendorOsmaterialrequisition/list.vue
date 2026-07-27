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
      <!-- <main-header :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">{{
            $t("common.add")
            }}</AuthorityButton>
        </template>
      </main-header> -->
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="osMaterialRequisitionApi.vendorList"
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
import { adaptDictData, parseTime } from '@/utils'
import { osMaterialRequisitionApi } from 'mods@/outsourcingManagementSupplier/api'

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
          label: '委外领料单号',
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
          label: '业务实体',
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: '库存组织',
          minWidth: 150
        },
        {
          prop: 'requirementHeadNum',
          label: '采购申请',
          minWidth: 150
        },
        {
          prop: 'orderNumber',
          label: '采购订单',
          minWidth: 150
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          minWidth: 150
        },
        {
          prop: 'materialName',
          label: '物料名称',
          minWidth: 150
        },
        {
          prop: 'orderNum',
          label: '采购订单数量',
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: '供应商编码',
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          minWidth: 150
        },
        {
          prop: 'handleStatus',
          label: '处理状态',
          minWidth: 150,
          formattor: value => this.$getDictLabel('MATERIAL_REQUISITION_STATUS', value)
        },
        {
          prop: 'createdBy',
          label: '创建人',
          minWidth: 150
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          minWidth: 150
        },
        {
          prop: 'operation',
          label: '操作',
          width: 170,
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          buttons: [
            {
              callback: row => this.replyHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.handleStatus === 'SUBMIT',
              formattor: () => {
                return this.$t('bidMod.reply')
              }
            },
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.handleStatus === 'VCREATE',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.handleStatus === 'VCREATE',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'materialRequisitionNumber', label: '委外领料单号' },
        { prop: 'orgId', label: '业务实体', type: 'OUorganizationSelector' },
        { prop: 'orderNumber', label: '采购订单号' },
        { prop: 'orderCreateBy', label: '采购订单创建人' },
        { prop: 'requirementHeadNum', label: '采购申请号' },
        {
          prop: 'materialId',
          label: '物料',
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          preQueryData: { 't.MATERIAL_ATTR': 'OUTSOURCING' },
          name: 'scc_base_material_item'
        },
        {
          prop: 'handleStatus',
          label: () => this.$t('purchaseDemand.applyStatus'),
          type: 'dict',
          code: 'MATERIAL_REQUISITION_STATUS'

        },
        { prop: 'createdBy', label: '创建人' },
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
  },
  methods: {
    readOne (row) {
      this.mode = 'view'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        title: '委外领料单',
        name: 'osmaterialrequisitionEdit' + row.materialRequisitionId
      }
      this.$emit('tab-add', tab)
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: osmaterialrequisitionEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '委外领料单新增',
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
        title: '委外领料单编辑',
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
          osMaterialRequisitionApi.delete(row.materialRequisitionId)
            .then(res => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
        })
        .catch(() => {})
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
        title: '委外领料单',
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
