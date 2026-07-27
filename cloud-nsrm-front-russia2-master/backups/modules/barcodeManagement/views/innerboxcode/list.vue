<template>
  <el-container
    class="flex-container innerboxcode_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <main-header
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
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.generate.innerboxcode.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import innerboxcodeEdit from './edit.vue'
export default {
  name: 'InnerboxcodeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'innerboxcodeList',
      tableName: 'innerboxcodeTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'innerBoxCode',
          label: '内箱条码编号',
          minWidth: 100
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          minWidth: 100
        },
        {
          prop: 'vendorCode',
          label: '供应商编码',
          minWidth: 100
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          minWidth: 100
        },
        {
          prop: 'materialName',
          label: '物料名称',
          minWidth: 100
        },
        {
          prop: 'categoryName',
          label: '品类名称',
          minWidth: 100
        },
        {
          prop: 'printCount',
          label: '打印次数',
          minWidth: 100
        },
        {
          prop: 'productionDate',
          label: '生产日期',
          minWidth: 100
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          minWidth: 100
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          minWidth: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '更新人',
          minWidth: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'vendorId',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'materialId',
          label: this.$t('materialMainData.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        { prop: 'innerBoxCode', label: '内箱条码' }
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
          this.$api.generate.innerboxcode.delete(row.innerBoxId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '内箱条码新增',
        name: 'innerboxcodeEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '内箱条码编辑',
        name: 'innerboxcodeEdit' + row.innerBoxId
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
