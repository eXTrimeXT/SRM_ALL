<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
    <MainHeader>
      <template slot="left">
        <ExportExcel
          :page-url="pageUrl"
          export-mode="front"
          :table-header="tableHeader"
          :dict-codes="dictCodes"
          :filter-params="queryParams"
          :title="$t('components.eio.customExport')"
          type="default"
        />
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
export default {
  name: 'ProtocolQueryList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sou/jcAgreement/getJcHeadLinePageList',
      tableHeader: [],
      queryParams: {},
      dictCodes: {},
      pageSize: 15,
      gridList: 'ProtocolQueryList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'companyCode',
        label: () => this.$t('cusEntry.reportManagement.companyName'),
        type: 'OUorganizationSelector',
        customProps: {
          id: 'organizationId',
          label: 'organizationName',
          value: 'organizationCode',
          disabled: 'disabled'
        }
      },
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.materialCode')
      },
      {
        prop: 'materialName',
        label: () => this.$t('cusEntry.reportManagement.materialName')
      },
      {
        prop: 'standards',
        label: () => this.$t('cusEntry.reportManagement.standards')
      },
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName')
      },
      {
        prop: 'supplyArea',
        label: () => this.$t('cusEntry.reportManagement.supplyArea'),
        type: 'dict',
        code: 'REGION'
      },
      {
        prop: 'agreementStatus',
        label: () => this.$t('cusEntry.reportManagement.agreementStatus'),
        type: 'dict',
        code: 'PROTOCOL_STATUS'
      },
      {
        prop: 'agreementType',
        label: () => this.$t('cusEntry.reportManagement.agreementType')
      }
    ]
    this.tableHeader = [
      {
        prop: 'companyName',
        label: () => this.$t('cusEntry.reportManagement.companyName'),
        minWidth: 120
      },
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'supplyArea',
        label: () => this.$t('cusEntry.reportManagement.supplyArea'),
        minWidth: 120,
        formattor: (value, row) => {
          return row.supplyArea.split(',').map(item => this.$getDictLabel('REGION', item)).join()
        }
      },
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.materialCode'),
        minWidth: 120
      },
      {
        prop: 'materialName',
        label: () => this.$t('cusEntry.reportManagement.materialName'),
        minWidth: 120
      },
      {
        prop: 'standards',
        label: () => this.$t('cusEntry.reportManagement.standards'),
        minWidth: 120
      },
      {
        prop: 'unit',
        label: () => this.$t('cusEntry.reportManagement.unit'),
        minWidth: 120
      },
      {
        prop: 'effectiveStartDate',
        label: () => this.$t('cusEntry.reportManagement.effectiveStartDate'),
        minWidth: 120
      },
      {
        prop: 'effectiveEndDate',
        label: () => this.$t('cusEntry.reportManagement.effectiveEndDate'),
        minWidth: 120
      },
      {
        prop: 'buyPersonName',
        label: () => this.$t('cusEntry.reportManagement.buyPersonName'),
        minWidth: 120
      },
      {
        prop: 'agreementStatus',
        label: () => this.$t('cusEntry.reportManagement.agreementStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'PROTOCOL_STATUS'
      },
      {
        prop: 'agreementType',
        label: () => this.$t('cusEntry.reportManagement.agreementType'),
        minWidth: 120
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
    }
  }
}
</script>

