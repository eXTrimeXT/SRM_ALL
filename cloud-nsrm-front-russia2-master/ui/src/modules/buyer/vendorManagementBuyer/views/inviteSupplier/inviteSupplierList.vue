<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/invite/inviteVendor/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import inviteSupplierDeatil from './inviteSupplierDeatil'

export default {
  name: 'InviteSupplierList',
  components: {
    FormWrapper,
    TableView,
    MainHeader
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'inviteSupplierList',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      pageSize: 15,
      preArr: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName') // '供应商名称'
          /* type: "quicksearch",
          showKey: "companyName",
          name: "scc_sup_company_info_display" */
        },
        {
          prop: 'contactPerson',
          label: this.$t('vendorMod.contactPerson') // 联系人
        },
        {
          prop: 'contactEmail',
          label: this.$t('vendorMod.contactEmail') // 联系邮箱
        },
        {
          prop: 'inviteStatus',
          label: this.$t('vendorMod.inviteStatus'), // 状态
          type: 'dict', // 字典类型
          code: 'INVITE_SUPPLIER_STATUS' // 字典code
        },
        {
          prop: 'dateList',
          label: this.$t('vendorMod.dateList'), // 邀请时间
          type: 'daterange'
        }
      ]
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'inviteVendorNo',
        label: this.$t('vendorMod.inviteVendorNo2'), // 邀请单据编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('vendorMod.vendorName') // 供应商名称
      },
      {
        prop: 'contactPerson',
        label: this.$t('vendorMod.contactPerson') // 联系人
      },
      {
        prop: 'socialCreditCode',
        label: this.$t('vendorMod.socialCreditCode3'), // 统一信用代码
        width: 120
      },
      {
        prop: 'publishDate',
        dataType: 'dateTime',
        label: this.$t('vendorMod.publishDate') // 邀请时间
      },
      {
        prop: 'inviteStatus',
        label: this.$t('vendorMod.inviteStatus'), // 状态
        dataType: 'dict', // 数据类型为字典
        code: 'INVITE_SUPPLIER_STATUS' // 字典code
      },
      {
        prop: 'contactEmail',
        label: this.$t('vendorMod.contactEmail') // 联系邮箱
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: (row) => this.editTab('edit', row),
            formattor () {
              return _this.$t('common.edit') // '编辑'
            },
            // show: row =>
            // ["DRAFT", "REJECTED", "WITHDRAW"].includes(row.importStatus)
            show: (row) => row.inviteStatus === 'DRAFT'
          },
          {
            callback: (row) => this.deleteHandle(row),
            // code: "pr:requirementApply:edit",
            // show: row => row.status === "DRAFT",
            formattor: () => {
              return this.$t('common.delete')
            },
            show: (row) => row.inviteStatus === 'DRAFT'
          },
          {
            callback: (row) => this.editTab('view', row),
            // code: "pr:requirementApply:edit",
            // show: row => row.status === "DRAFT",
            formattor: () => {
              return this.$t('common.view')
            },
            show: (row) => row.inviteStatus !== 'DRAFT'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    getQuerydata (v) {
      if (v && v.dateList) {
        v.publishStartDate = v.dateList[0]
        v.publishEndDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.publishStartDate
        delete v.publishEndDate
      }
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: inviteSupplierDeatil,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.inviteSuppliersToAdd'), // 邀请供应商新增
        name: 'inviteSupplierDeatil'
      }
      this.$emit('tab-add', tab)
    },
    editTab (type, row) {
      this.mode = type
      let title = this.$t('vendorMod.editInviteSuppliers') // 编辑邀请供应商
      if (type === 'view') {
        title = this.$t('vendorMod.viewInvitedSuppliers') // 查看邀请供应商
      }
      let tab = {
        component: inviteSupplierDeatil,
        ctrlHeight: true,
        params: {
          flag: this.mode,
          tabName: 'inviteSupplierDeatil',
          row
        },
        title: title,
        name: 'inviteSupplierDeatil' + row.inviteVendorNo
      }
      this.$emit('tab-add', tab)
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        /* .then(() => {
          this.$api.vendorManagementBuyer.inviteVendor.delete(row.)
            .then(res => {
            this.$message.success(res.message);
            this.getQuerydata();
          });
        })
        .catch(() => {
        }); */
        .then(() => {
          this.$http({
            url: '/api-sup/invite/inviteVendor/delete',
            method: 'GET',
            params: { id: row.inviteVendorId }
          }).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
<style lang="scss" scoped></style>
