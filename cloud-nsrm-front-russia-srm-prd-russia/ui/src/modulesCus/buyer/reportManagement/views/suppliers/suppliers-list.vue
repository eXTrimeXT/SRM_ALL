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
import ProjectDetail from './project-detail'
export default {
  name: 'SuppliersList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sup/supplier/report/forms/getSupRepFormsList',
      tableHeader: [],
      queryParams: {},
      dictCodes: {},
      pageSize: 15,
      gridList: 'SuppliersList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName')
      },
      {
        prop: 'contactName',
        label: () => this.$t('cusEntry.reportManagement.contactName')
      },
      {
        prop: 'contactTel',
        label: () => this.$t('cusEntry.reportManagement.contactPhone')
      },
      {
        prop: 'supCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode')
      },
      {
        prop: 'address',
        label: () => this.$t('cusEntry.reportManagement.companyAddress')
      },
      {
        prop: 'province',
        label: () => this.$t('cusEntry.reportManagement.province')
      },
      {
        prop: 'city',
        label: () => this.$t('cusEntry.reportManagement.city')
      },
      {
        label: () => this.$t('cusEntry.reportManagement.partyRate'),
        type: 'inputNumberRange',
        propsMap: {
          start: 'cylStart',
          end: 'cylEnd'
        }
      },
      {
        prop: 'projectName',
        label: () => this.$t('cusEntry.reportManagement.projectName')
      },
      {
        label: () => this.$t('cusEntry.reportManagement.bidRate'),
        type: 'inputNumberRange',
        propsMap: {
          start: 'zblStart',
          end: 'zblEnd'
        }
      },
      {
        prop: 'categoryId',
        label: () => this.$t('cusEntry.reportManagement.categoryName'),
        type: 'quicksearch',
        showKey: 'categoryName',
        propKey: 'categoryId',
        name: 'scc_base_purchase_category4'
      },
      {
        label: () => this.$t('cusEntry.reportManagement.registeredCapital'),
        type: 'inputNumberRange',
        propsMap: {
          start: 'zcFundStart',
          end: 'zcFundEnd'
        }
      },
      {
        prop: 'contactEmail',
        label: () => this.$t('cusEntry.reportManagement.email')
      },
      {
        prop: 'createDate',
        label: () => this.$t('cusEntry.reportManagement.createYear'),
        type: 'daterange'
      },
      {
        prop: 'zzLevel',
        label: () => this.$t('cusEntry.reportManagement.qualifications')
      },
      {
        prop: 'projectCode',
        label: () => this.$t('cusEntry.reportManagement.projectNo')
      }
    ]
    this.tableHeader = [
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'supCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'contactName',
        label: () => this.$t('cusEntry.reportManagement.contactName'),
        minWidth: 120
      },
      {
        prop: 'contactTel',
        label: () => this.$t('cusEntry.reportManagement.contactPhone'),
        minWidth: 120
      },
      {
        prop: 'contactEmail',
        label: () => this.$t('cusEntry.reportManagement.email'),
        minWidth: 120
      },
      {
        prop: 'registeredCapital',
        label: () => this.$t('cusEntry.reportManagement.registeredCapital'),
        width: 140
      },
      {
        prop: 'bzCode',
        label: () => this.$t('cusEntry.reportManagement.currency'),
        dataType: 'dict',
        code: 'currency'
      },
      {
        prop: 'zzLevel',
        label: () => this.$t('cusEntry.reportManagement.qualificationsOrLevel'),
        minWidth: 120
      },
      {
        prop: 'comUp',
        label: () => this.$t('cusEntry.reportManagement.createYear'),
        minWidth: 120
      },
      {
        prop: 'regDate',
        label: () => this.$t('cusEntry.reportManagement.registerDate'),
        width: 140
      },
      {
        prop: 'address',
        label: () => this.$t('cusEntry.reportManagement.officeAddress'),
        minWidth: 120
      },
      {
        prop: 'province',
        label: () => this.$t('cusEntry.reportManagement.province'),
        minWidth: 120
      },
      {
        prop: 'city',
        label: () => this.$t('cusEntry.reportManagement.city'),
        minWidth: 120
      },
      {
        prop: 'cy',
        label: () => this.$t('cusEntry.reportManagement.partyRate'),
        minWidth: 120
      },
      {
        prop: 'zb',
        label: () => this.$t('cusEntry.reportManagement.bidRate'),
        minWidth: 120
      },
      {
        prop: 'hgNum',
        label: () => this.$t('cusEntry.reportManagement.qualifiedPerformanceCount'),
        width: 120
      },
      {
        prop: 'bhgNum',
        label: () => this.$t('cusEntry.reportManagement.performanceFailureCount'),
        width: 140
      },
      {
        prop: 'operation',
        label: () => this.$t('cusEntry.reportManagement.projectDetail'),
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
      const { createDate = [], ...rest } = params || {}
      this.queryParams = rest
      if (createDate && createDate.length > 0) {
        this.queryParams.clYearStart = createDate[0]
        this.queryParams.clYearEnd = createDate[1]
      }
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    },
    // 查看详情
    read (row) {
      this.$emit('tab-add', {
        component: ProjectDetail,
        name: `ProjectDetail${row.projectName}`,
        title: row.supName,
        params: {
          row,
          queryParams: this.queryParams,
          tabName: `ProjectDetail${row.projectName}`
        }
      })
    }
  }
}
</script>
