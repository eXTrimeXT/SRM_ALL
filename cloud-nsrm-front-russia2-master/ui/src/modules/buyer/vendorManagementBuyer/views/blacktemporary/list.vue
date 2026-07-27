<template>
  <el-container
    class="flex-container black_list_wrapper"
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
            code="base:blacktemporary:add"
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>

          <ExportExcel
            page-url="/api-base/base/blacktemporary/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :source="blackTemporaryApi.list"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import blacktemporaryEdit from './edit.vue'
import blackEdit from 'modb@/vendorManagementBuyer/views/black/edit.vue'
import ExportExcel from 'lib@/components/export-excel'
import { blackTemporaryApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlacktemporaryList',

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
      blackTemporaryApi: blackTemporaryApi,
      gridId: 'list',
      currentRows: [],
      dictCodes: {
        approveStatus: 'APPROVE_STATUS_TYPE'
      },
      filterParams: {},
      tableHeader: [],
      filterConfig: [
        { prop: 'blackCode', label: this.$t('black.blacklistApprovalNumber') },
        {
          prop: 'companyName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_base_black_company'
        },
        { prop: 'socialCreditCode', label: this.$t('vendorMod.lcCode') },
        {
          prop: 'approveStatus',
          label: this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'APPROVE_STATUS_TYPE'
        },
        {
          prop: 'dateList',
          width: 180,
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        {
          prop: 'createdId',
          label: this.$t('common.creator'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
        }
      ],
      queryParam: {}
    }
  },

  created () {
    this.tableHeader = [
      {
        prop: 'companyName',
        label: this.$t('common.vendorName'),
        width: 120
      },
      {
        prop: 'socialCreditCode',
        label: this.$t('vendorMod.lcCode'),
        width: 150
      },
      {
        prop: 'blackTemporaryCode',
        label: this.$t('black.blackTemporaryCode'),
        width: 170,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle(row, 'view', 1)
      },
      {
        prop: 'blackCode',
        label: this.$t('black.blacklistApprovalNumber'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle(row, 'view', 2)
      },
      {
        prop: 'approveStatus',
        label: this.$t('vendorMod.relegation.documentStatus'),
        width: 180,
        dataType: 'dict',
        code: 'APPROVE_STATUS_TYPE'
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        formattor: val => val ? this.$parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.editHandle(row, 'edit'),
            code: 'base:blacktemporary:edit',
            show: row => row.approveStatus === 'DRAFT' || row.approveStatus === 'REJECTED',
            formattor: () => this.$t('common.edit')
          },
          {
            callback: row => this.deleteHandle(row),
            code: 'base:blacktemporary:delete',
            show: row => row.approveStatus === 'DRAFT',
            formattor: () => this.$t('common.delete')
          },
          {
            callback: row => this.editHandle(row, 'doApproval'),
            code: 'base:blacktemporary:doApproval',
            show: row => row.approveStatus === 'SUBMITTED',
            formattor: () => this.$t('vendorMod.doApproval')
          },
          {
            callback: row => this.editHandle(row, 'doApproval'),
            code: 'base:blacktemporary:doApproval',
            show: row => row.approveStatus === 'REJECTED',
            formattor: () => this.$t('common.abandon')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },

    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.creationStartDate = dateList[0]
        params.creationEndDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
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
          blackTemporaryApi.delete(row.blackTemporaryId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },

    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: blacktemporaryEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'blacktemporaryEdit'
        },
        title: this.$t('black.blackTemporaryCode'),
        name: 'blacktemporaryEdit'
      }
      this.$emit('tab-add', tab)
    },

    editHandle (row, type, num) {
      this.mode = type
      let tab = {}
      if (num == 2) {
        tab = {
          component: blackEdit,
          params: {
            row,
            flag: this.mode,
            tabName: 'blackEdit' + row.blackId,
            activeWorkflowTab: false,
            readOnly: false
          },
          title: row.blackCode,
          name: 'blackEdit' + row.blackId
        }
      } else {
        tab = {
          component: blacktemporaryEdit,
          params: {
            row,
            flag: this.mode,
            tabName: 'blacktemporaryEdit' + row.blackTemporaryId
          },
          title: row.blackTemporaryCode,
          name: 'blacktemporaryEdit' + row.blackTemporaryId
        }
      }
      this.$emit('tab-add', tab)
    },

    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
