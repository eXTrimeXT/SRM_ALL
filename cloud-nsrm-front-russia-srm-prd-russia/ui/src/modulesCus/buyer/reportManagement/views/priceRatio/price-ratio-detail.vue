<template>
  <el-container class="flex-contianer" direction="vertical">
    <el-main>
      <TableView
        :table-header="tableHeader"
        :table-infor="tableData"
        :page-size="pageSize"
        :pre-query-data="queryParams"
        :auto-query="true"
        :com-active="$attrs['changeTab']"
        :url="pageUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
export default {
  name: 'ProjectDetail',
  components: {
    TableView
  },
  data () {
    return {
      tableData: [],
      tableHeader: [],
      pageUrl: '/api-sup-ce/FixPriceTimelinessRatio/detail',
      queryParams: {},
      pageSize: 15,
      gridList: 'ProjectDetail'
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'createUserOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.createUserOrgOuName'),
        minWidth: 120
      },
      {
        prop: 'createdBy',
        label: () => this.$t('cusEntry.reportManagement.createdBy'),
        minWidth: 120
      },
      {
        prop: 'requirementHeadNum',
        label: () => this.$t('cusEntry.reportManagement.requirementHeadNum'),
        minWidth: 120
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.orgName'),
        minWidth: 120
      },
      {
        prop: 'extUseDepartmentName',
        label: () => this.$t('cusEntry.reportManagement.extUseDepartmentName'),
        minWidth: 120
      },
      {
        prop: 'extApproveTime',
        label: () => this.$t('cusEntry.reportManagement.extApproveTime'),
        minWidth: 120
      },
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.extMaterialCode'),
        minWidth: 120
      },
      {
        prop: 'materialName',
        label: () => this.$t('cusEntry.reportManagement.materialName'),
        minWidth: 120
      },
      {
        prop: 'extMaterialModel',
        label: () => this.$t('cusEntry.reportManagement.extMaterialModel'),
        minWidth: 120
      },
      {
        prop: 'unit',
        label: () => this.$t('cusEntry.reportManagement.extUnit'),
        width: 140
      },
      {
        prop: 'brand',
        label: () => this.$t('cusEntry.reportManagement.brand'),
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'requirementDate',
        label: () => this.$t('cusEntry.reportManagement.requirementDate'),
        width: 140
      },
      {
        prop: 'requirementQuantity',
        label: () => this.$t('cusEntry.reportManagement.requirementQuantity'),
        minWidth: 120
      },
      {
        prop: 'extClosedCause',
        label: () => this.$t('cusEntry.reportManagement.extClosedCause'),
        minWidth: 120
      },
      {
        prop: 'auditStatus',
        label: () => this.$t('cusEntry.reportManagement.auditStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'APPROVAL_STATUS'
      },
      {
        prop: 'extPoolStatus',
        label: () => this.$t('cusEntry.reportManagement.extPoolStatus'),
        minWidth: 120,
        formattor: (val, row) => this.extPoolStatusFun(row)
      },
      {
        prop: 'extBuyType',
        label: () => this.$t('cusEntry.reportManagement.extBuyType'),
        minWidth: 120,
        dataType: 'dict',
        code: 'PR_BUY_TYPE'
      },
      {
        prop: 'fixPriceNo',
        label: () => this.$t('cusEntry.reportManagement.fixPriceNo'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.reportManagement.priceCreationDate'),
        minWidth: 120
      },
      {
        prop: 'onTime',
        label: () => this.$t('cusEntry.reportManagement.onTime'),
        minWidth: 120
      }
    ]
    this.queryParams = {
      jobNumber: this.$attrs.params.row.createdBy
    }
  },
  methods: {
    extPoolStatusFun (row) {
      let status = null
      const extPoolStatus = row?.extPoolStatus
      const orderQuantity = row?.orderQuantity
      if (extPoolStatus == 'Y') {
        if (orderQuantity == 0) {
          status = '已完成'
        } else {
          status = '有效'
        }
      } else if (extPoolStatus == 'N') {
        status = '已关闭'
      }
      return status
    }
  }
}
</script>
