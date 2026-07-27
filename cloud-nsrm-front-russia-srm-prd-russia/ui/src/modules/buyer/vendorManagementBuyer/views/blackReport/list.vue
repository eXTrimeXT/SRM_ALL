<template>
  <el-container
    class="flex-container black_list_wrapper"
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
          <ExportExcel
            page-url="/api-sup/sup/black/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="blackApi.list"
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { blackApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlackList',

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
      blackApi: blackApi,
      gridId: 'list',
      currentRows: [],
      dictCodes: { companyType: 'COMPANY_NATURE' },
      filterParams: {},
      tableHeader: [],
      filterConfig: [
        { prop: 'blackCode', label: this.$t('black.blacklistApprovalNumber') },
        { prop: 'companyName', label: this.$t('common.vendorName') },
        { prop: 'socialCreditCode', label: this.$t('vendorMod.lcCode') },
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
        width: 150
      },
      {
        prop: 'socialCreditCode',
        label: this.$t('vendorMod.lcCode'),
        width: 150
      },
      {
        prop: 'companyCreationDate',
        label: this.$t('bidMod.companyCreationDate'),
        width: 100,
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : ''
      },
      {
        prop: 'registeredCapital',
        label: this.$t('vendorMod.ceeaRegisteredCapital')
      },
      {
        prop: 'companyType',
        label: this.$t('vendorMod.companyType'),
        dataType: 'dict',
        code: 'COMPANY_NATURE'
      },
      {
        prop: 'legalPerson',
        label: this.$t('vendorMod.corporateRepresentative')
      },
      {
        prop: 'blackCode',
        label: this.$t('black.blacklistApprovalNumber'),
        width: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle(row)
      },
      {
        prop: 'blackType',
        label: this.$t('black.blackType'),
        formattor: value => this.$getDictLabel('BLACK_TYPE', value)
      },
      {
        prop: 'effectiveTime',
        label: this.$t('vendorMod.startDate'),
        width: 100,
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : ''
      },
      {
        prop: 'expirationTime',
        label: this.$t('vendorMod.endDate'),
        width: 100,
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : ''
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : ''
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    getQuerydata (params) {
      if (!params) {
        params = {}
      }
      params.approveStatus = 'APPROVED'
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
