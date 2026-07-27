<template>
  <el-container
    class="flex-container the_comparisonTable_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addRowData">
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/api-ql/InvoicePrincipal/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import TableView from './TableViewNew'

export default {
  name: 'ComparisonTableList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'comparisonTableList',
      pageSize: 15,
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'orgName',
          label: this.$t('vendorMod.orgEntityName')
        },
        // 开票主体名称
        {
          prop: 'principalName',
          label: this.$t('cusEntry.supplement20250205.principalName')
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'orgCode',
        label: this.$t('orderMod.buyerOrderSynergy.organizationCode'),
        showType: 'OUorganizationSelector',
        editable: row => row.editable,
        callback: this.selectHandler,
        customProps: {
          id: 'organizationId',
          label: 'organizationCode',
          value: 'organizationCode'
        },
        minWidth: 150
      },
      {
        prop: 'orgName',
        label: this.$t('vendorMod.orgEntityName'),
        minWidth: 150
      },
      // 开票主体编码
      {
        prop: 'principalCode',
        label: this.$t('cusEntry.supplement20250205.principalCode'),
        showType: 'input',
        editable: row => row.editable,
        minWidth: 150
      },
      // 开票主体名称
      {
        prop: 'principalName',
        label: this.$t('cusEntry.supplement20250205.principalName'),
        showType: 'input',
        editable: row => row.editable,
        minWidth: 150
      },
      // 利润中心编码
      {
        prop: 'profitCenterCode',
        label: this.$t('cusEntry.supplement20250205.profitCenterCode'),
        showType: 'input',
        editable: row => row.editable,
        minWidth: 150
      },
      // 利润中心名称
      {
        prop: 'profitCenterName',
        label: this.$t('cusEntry.supplement20250205.profitCenterName'),
        showType: 'input',
        editable: row => row.editable,
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editRowData(row),
            formattor: _ => this.$t('common.edit'),
            show: row => !row.editable
          },
          {
            callback: row => this.saveRowData(row),
            formattor: _ => this.$t('common.save'),
            show: row => row.editable
          },
          {
            callback: (row, scope) => this.delRowData(row, scope.$rowIndex),
            formattor: _ => this.$t('common.delete')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      let params = v || {}
      this.queryParam = transformMQL.listPageData({
        type: 'InvoicePrincipal',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 业务实体快查
    selectHandler (row, scope, value) {
      row.orgId = value ? value.organizationId : null
      row.orgCode = value ? value.organizationCode : null
      row.orgName = value ? value.organizationName : null
    },
    // 新增
    addRowData () {
      this.$refs[this.gridId].tableData.unshift({
        editable: true,
        orgCode: null,
        orgId: null,
        orgName: null,
        principalCode: null,
        principalId: null,
        principalName: null,
        createdFullName: this.$store.getters.userInfo.nickname
      })
    },
    // 编辑
    editRowData (row) {
      this.$set(row, 'editable', true)
    },
    // 保存
    saveRowData (row) {
      const saveData = transformMQL.save('InvoicePrincipal', [row], 'save')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoicePrincipal/save',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 删除
    async delRowData (row, index) {
      if (!row.principalId) {
        this.$refs[this.gridId].tableData.splice(index, 1)
        return
      }
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      const params = { principalId: row.principalId }
      const saveData = transformMQL.save('InvoicePrincipal', [params], 'delete')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoicePrincipal/delete',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
