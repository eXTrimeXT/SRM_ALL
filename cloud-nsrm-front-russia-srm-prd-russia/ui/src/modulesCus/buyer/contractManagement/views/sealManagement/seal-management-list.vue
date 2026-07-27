<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
    <MainHeader>
      <template slot="left">
        <AuthorityButton
          type="primary"
          code="contact:seal:add"
          @click="edit('add')"
        >
          {{ $t("common.add") }}
        </AuthorityButton>
      </template>
    </MainHeader>
    <TableView
      :ref="gridList"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParams"
      open-custom-table
      :auto-query="true"
      :com-active="$attrs['changeTab']"
      :url="pageUrl"
    />
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import SealManagementDetail from './seal-management-detail'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { contractManagement } from 'modcb@/contractManagement/api'
export default {
  name: 'SealManagementList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      preArr: [],
      pageUrl: '/api-pj/contract/seal/listPage',
      tableHeader: [],
      queryParams: {},
      pageSize: 15,
      gridList: 'SealManagementList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'signCompanyName',
        label: () => this.$t('cusEntry.sealManagement.signCompanyName')
      },
      {
        prop: 'sealName',
        label: () => this.$t('cusEntry.sealManagement.sealName')
      },
      {
        prop: 'sealId',
        label: () => this.$t('cusEntry.sealManagement.sealId')
      }
    ]
    this.tableHeader = [
      {
        prop: 'signCompanyName',
        label: () => this.$t('cusEntry.sealManagement.signCompanyName'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.edit('view', row)
      },
      {
        prop: 'sealName',
        label: () => this.$t('cusEntry.sealManagement.sealName'),
        minWidth: 120
      },
      {
        prop: 'sealId',
        label: () => this.$t('cusEntry.sealManagement.sealId'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.sealManagement.creationDate'),
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('cusEntry.sealManagement.createdFullName'),
        minWidth: 120
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
            callback: row => this.edit('edit', row),
            formattor: () => this.$t('common.edit')
          },
          {
            callback: row => this.delete(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
  },
  methods: {
    // 查询
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    },
    // 编辑
    edit (type, row = {}) {
      this.$emit('tab-add', {
        component: SealManagementDetail,
        name: type === 'add' ? 'SealManagementDetail' : `SealManagementDetail${row.sealId}`,
        params: {
          tabName: type === 'add' ? 'SealManagementDetail' : `SealManagementDetail${row.sealId}`,
          type,
          row
        },
        title: type === 'add' ? this.$t('cusEntry.sealManagement.addSeal') : row.signCompanyName
      })
    },
    // 删除
    delete (row) {
      contractManagement.seal.delete({ id: row.contractSealId }).then(res => {
        this.$message.success(this.$t('common.deleteSave'))
        this.getQueryData(this.queryParams)
      })
    }
  }
}
</script>
