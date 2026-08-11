<template>
  <el-container
    class="flex-container complaintinfo_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      >
        <template #orgName="{ scope }">
          <OrganizationSelector
            ref="ouSelector"
            v-model="scope.orgId"
            :parent-id="-1"
            node-type="OU"
            :placeholder="$t('common.pleaseSelect')"
          />
        </template>
        <template #complaintType="{ scope }">
          <DictSelect
            v-model="scope.complaintType"
            code="COMPLAINT_TYPE"
          />
        </template>
        <template #complaintStatus="{ scope }">
          <DictSelect
            v-model="scope.complaintStatus"
            code="COMPLAINT_STATUS"
          />
        </template>
      </FormWrapper>
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
              $t('common.add')
            }}
          </AuthorityButton>
          <ExportExcel
            :filter-params="queryParam"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            page-url="/api-sup-ce/sup/complaintinfo/listPage"
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
        :source="complaintInfo.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import complaintinfoEdit from './edit.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import MImport from 'lib@/components/import'
import { complaintInfo } from 'mods@/vendorManagementSupplier/api'

export default {
  name: 'ComplaintinfoList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      complaintInfo: complaintInfo,
      dictCodes: {
        complaintType: 'COMPLAINT_TYPE',
        complaintStatus: 'COMPLAINT_STATUS'
      },
      pageSize: 15,
      gridId: 'list',
      filterParams: {},
      currentRows: [],
      required: {
        complaintInfoId: null,
        complaintStatus: null
      },
      tableHeader: [
        {
          prop: 'complaintNo',
          label: () => this.$t('vendorMod.complaintInfoId'),
          width: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: (row) => {
            this.viewHandle(row, 'see')
          }
        },
        {
          prop: 'complaintTheme',
          label: () => this.$t('vendorMod.complaintTheme'),
          width: 100
        },
        {
          prop: 'complaintType',
          label: () => this.$t('vendorMod.complaintType'),
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'COMPLAINT_TYPE' // 字典code
        },
        {
          prop: 'complaintStatus',
          label: () => this.$t('vendorMod.complaintStatus'),
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'COMPLAINT_STATUS' // 字典code
        },
        {
          prop: 'orgName',
          label: () => this.$t('vendorMod.ceeaOrgName'),
          width: 100
        },
        {
          prop: 'complaintUserName',
          label: () => this.$t('vendorMod.complaintUserName'),
          width: 130
        },
        {
          prop: 'categoryName',
          label: () => this.$t('vendorMod.categoryName'),
          width: 120
        },
        {
          prop: 'creationDate',
          label: () => this.$t('vendorMod.creatTime2'),
          width: 160,
          dataType: 'dateTime'
        },
        {
          prop: 'complaintEndDate',
          label: () => this.$t('vendorMod.complaintEndDate'),
          width: 160,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 100,
          buttons: [
            {
              callback: (row) => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: (row) => row.complaintStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row) => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: (row) => row.complaintStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: (row) => this.viewHandle(row),
              // code: "pr:requirementApply:edit",
              show: (row) => row.complaintStatus === 'SUBMITTED',
              formattor: () => {
                return this.$t('common.view')
              }
            },
            {
              callback: (row) => this.requireComplaint('accept', row),
              // code: "pr:requirementApply:edit",
              show: (row) => row.complaintStatus === 'APPLICATION_CLOSED',
              formattor: () => {
                return this.$t('common.toApprove')
              }
            },
            {
              callback: (row) => this.requireComplaint('refuse', row),
              // code: "pr:requirementApply:edit",
              show: (row) => row.complaintStatus === 'APPLICATION_CLOSED',
              formattor: () => {
                return this.$t('common.refused')
              }
            },
            {
              callback: (row) => this.viewHandle(row),
              // code: "pr:requirementApply:edit",
              show: (row) =>
                row.complaintStatus === 'REPLY_FEEDBACK' ||
                row.complaintStatus === 'ANSWERED' ||
                row.complaintStatus === 'CLOSED',
              formattor: () => {
                return this.$t('common.view')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'complaintNo', label: () => this.$t('vendorMod.complaintInfoId') },
        {
          prop: 'orgName',
          label: () => this.$t('supplierRating.entity'),
          type: 'slot',
          slot: 'orgName'
        },
        { prop: 'categoryName', label: () => this.$t('vendorMod.categoryName') },
        {
          prop: 'complaintType',
          label: () => this.$t('vendorMod.complaintType'),
          type: 'slot',
          slot: 'complaintType'
        },
        {
          prop: 'complaintStatus',
          label: () => this.$t('vendorMod.complaintStatus'),
          type: 'slot',
          slot: 'complaintStatus'
        },
        { prop: 'creationDate', label: () => this.$t('vendorMod.creatTime2'), type: 'daterange' }
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
    handleSuccess () {
      this.getQuerydata()
    },
    selectHandler (val, scope) {
      scope.orgName = val.orgName || ''
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = JSON.parse(JSON.stringify(params || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.complaintStartDate = creationDate[0]
        this.queryParam.complaintEndDate = creationDate[1]
      } else if (params && !params.creationDate) {
        delete this.queryParam.complaintStartDate
        delete this.queryParam.complaintEndDate
      }
      delete this.queryParam.creationDate
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
          complaintInfo.delete(row.complaintInfoId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: complaintinfoEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.complaintAdd'),
        name: 'complaintinfoEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: complaintinfoEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.complaintEdit'),
        name: 'complaintinfoEdit' + row.complaintNo
      }
      this.$emit('tab-add', tab)
    },
    viewHandle (row, type) {
      this.mode = 'view'
      let readOnly = type === 'see'
      const tab = {
        component: complaintinfoEdit,
        params: {
          row,
          flag: this.mode,
          readOnly
        },
        title: this.$t('vendorMod.complaintView'),
        name: 'complaintinfoEdit' + row.complaintNo
      }
      this.$emit('tab-add', tab)
    },
    requireComplaint (flag, row) {
      if (flag === 'accept') {
        this.required.complaintStatus = 'CLOSED'
      } else if (flag === 'refuse') {
        this.required.complaintStatus = 'REPLY_FEEDBACK'
      }
      this.required.complaintInfoId = row.complaintInfoId
      complaintInfo.requireComplaint(this.required).then(({ res }) => {
        this.getQuerydata()
      })
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
