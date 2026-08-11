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
import PriceRatioDetail from './price-ratio-detail'
export default {
  name: 'PriceRatioList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sup-ce/FixPriceTimelinessRatio/head',
      tableHeader: [],
      queryParams: {},
      dictCodes: {},
      pageSize: 15,
      gridList: 'PriceRatioList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'createUserOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.createUserOrgOuName'),
        type: 'OUorganizationSelector',
        customProps: {
          id: 'organizationId',
          label: 'organizationName',
          value: 'organizationName'
        }
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('cusEntry.reportManagement.createdBy')
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.orgName'),
        type: 'OUorganizationSelector',
        customProps: {
          id: 'organizationId',
          label: 'organizationName',
          value: 'organizationName'
        }
      },
      {
        prop: 'extClosedCause',
        label: () => this.$t('cusEntry.reportManagement.extClosedCause')
      },
      {
        prop: 'extUseDepartmentName',
        label: () => this.$t('cusEntry.reportManagement.extUseDepartmentName')
      },
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.extMaterialCode')
      },
      {
        prop: 'vendorName',
        label: () => this.$t('cusEntry.reportManagement.vendorName')
      },
      {
        label: () => this.$t('cusEntry.reportManagement.extApproveTime'),
        prop: 'extApproveTime',
        type: 'daterange'
      }
    ]
    this.tableHeader = [
      {
        prop: 'createdBy',
        label: () => this.$t('cusEntry.reportManagement.jobNumber'),
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('cusEntry.reportManagement.createdByName'),
        minWidth: 150
      },
      {
        prop: 'ratio',
        label: () => this.$t('cusEntry.reportManagement.priceRatio'),
        minWidth: 150,
        formattor: val => {
          return val ? `${val}%` : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('cusEntry.reportManagement.readDetail'),
        showType: 'buttons',
        btnStyle: 'text',
        width: 80,
        fixed: 'right',
        buttons: [
          {
            callback: row => this.read(row),
            formattor: () => this.$t('common.view')
          }
        ]
      }
    ]
  },
  methods: {
    // 查询
    getQueryData (params) {
      if (params.extApproveTime) {
        const [beginTime, endTime] = params.extApproveTime
        params.beginTime = beginTime
        params.endTime = endTime
        Reflect.deleteProperty(params, 'extApproveTime')
      } else {
        this.$message.warning(this.$t('cusEntry.tipMessage.extApproveTimeMsg'))
        return false
      }
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    },
    // 查看详情
    read (row) {
      this.$emit('tab-add', {
        component: PriceRatioDetail,
        name: `PriceRatioDetail${row.createdFullName}`,
        title: row.createdFullName,
        params: {
          row,
          queryParams: this.queryParams,
          tabName: `PriceRatioDetail${row.createdFullName}`
        }
      })
    }
  }
}
</script>
