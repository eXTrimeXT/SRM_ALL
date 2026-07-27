<template>
  <el-container
    class="flex-container capacityreport_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <!--        @synchronous-value="syncFilterParams"-->
        <template #orgCode="{scope}">
          <div class="form-item-line">
            <QuickSearch
              ref="quickOrg"
              show-key="purchaseOrgName"
              prop-key="purchaseOrgCode"
              :scope-data="scope"
              name="purchase_catalog_org_buyer"
              @close-quicksearch="getQuickSearch"
            />
          </div>
        </template>
        <template #invOrgCode="{scope}">
          <div class="form-item-line">
            <QuickSearch
              ref="quickInv"
              show-key="invName"
              prop-key="invCode"
              :preQueryData="{'t.PURCHASE_ORG_CODE':orgCode}"
              :scope-data="scope"
              name="purchase_catalog_inv_buyer"
              @close-quicksearch="getQuickSearchInv"
            />
          </div>
        </template>
      </FormWrapper>
      <MainHeader
        v-if="aut || imp"
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            v-if="aut"
            type="primary"
            @click="addHandle"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
          <MImport
            v-if="imp"
            :title="$t('common.import')"
            up-load-url="/api-sup/sup/capacityreport/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="capacityreport.list"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import capacityreportEdit from './edit.vue'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { parseTime } from '@/utils'
import { capacityreport } from 'modb@/planManagementBuyer/api/index'

export default {
  name: 'CapacityreportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      orgCode: null,
      capacityreport: capacityreport,
      name: 'capacityreportList',
      tableName: 'capacityreportTable',
      pageSize: 15,
      imp: true,
      out: true,
      aut: true,
      gridId: 'list',
      currentRows: [],
      extraData: {
        fileModular: 'base',
        fileFunction: 'capacityreport',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'materialCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          width: 100
        },
        {
          prop: 'materialName',
          label: this.$t('supplierCapacityReport.materialName'),
          width: 100
        },
        {
          prop: 'orgName',
          label: this.$t('common.orgId'),
          width: 100
        },
        {
          prop: 'invOrgName',
          label: this.$t('common.invOrg'),
          width: 100
        },
        {
          prop: 'vendorCode',
          label: this.$t('supplierCapacityReport.vendorCode'),
          width: 100
        },
        {
          prop: 'vendorName',
          label: this.$t('supplierCapacityReport.vendorName'),
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('supplierCapacityReport.categoryName'),
          width: 100
        },

        {
          prop: 'productionDayNumber',
          label: this.$t('supplierCapacityReport.productionDayNumber'),
          width: 100
        },
        {
          prop: 'monthlyWorkDays',
          label: this.$t('supplierCapacityReport.monthlyWorkDays'),
          width: 100
        },
        {
          prop: 'productionMonthNumber',
          label: this.$t('supplierCapacityReport.productionMonthNumber'),
          width: 100
        },

        {
          prop: 'monthTotalCapacity',
          label: this.$t('supplierCapacityReport.monthTotalCapacity'),
          width: 100
        },

        {
          prop: 'isNot',
          label: this.$t('supplierCapacityReport.isNot'),
          width: 100
        },

        {
          prop: 'startTime',
          label: this.$t('supplierCapacityReport.startTime'),
          width: 100,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          },
          editType: 'none'
        },

        {
          prop: 'endTime',
          label: this.$t('supplierCapacityReport.endTime'),
          width: 100,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },

        {
          prop: 'createdBy',
          label: this.$t('supplierCapacityReport.createdBy'),
          width: 100
        },

        {
          prop: 'lastUpdatedBy',
          label: this.$t('supplierCapacityReport.lastUpdatedBy'),
          width: 100
        },

        {
          prop: 'lastUpdateDate',
          label: this.$t('supplierCapacityReport.lastUpdateDate'),
          width: 100
        },

        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [

            {
              callback: (row) => this.confirmHandle(row),
              show: (row) => row.isNot === '否',
              formattor: (val) => {
                return this.$t('common.confirm')
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'materialCode',
          label: this.$t('supplierCapacityReport.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'purchase_catalog_material_valid'
        },
        {
          prop: 'vendorName',
          label: this.$t('supplierCapacityReport.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          prop: 'categoryName',
          label: this.$t('supplierCapacityReport.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'orgCode',
          label: this.$t('common.orgId'),
          type: 'slot',
          slot: 'orgCode'
        },
        {
          prop: 'invOrgCode',
          label: this.$t('common.invOrg'),
          type: 'slot',
          slot: 'invOrgCode'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })

    // 采购
    if (this.$store.getters.userType == 'BUYER') {

      this.imp = false
      this.aut = false

    }
  },
  methods: {
    syncFilterParams (values) {
      if (values?.orgCode == null) {
        this.$refs.quickOrg.inputModel = null
        this.orgCode = null
      }
      if (values?.invOrgCode == null) {
        this.$refs.quickInv.inputModel = null
      }
    },
    getQuickSearch (val, scope) {
      scope.orgCode = val?.purchaseOrgCode
      this.orgCode = val?.purchaseOrgCode
    },
    getQuickSearchInv (val, scope) {
      scope.invOrgCode = val?.invCode
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLinkByPost(
        '/api-sup/sup/capacityreport/exportTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    confirmHandle (row) {
      this.$confirm(this.$t('bidMod.confirmedFlag'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          capacityreport.confirmHandle(row.reportId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: capacityreportEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('supplierCapacityReport.supplierCapacityReported'),
        name: 'capacityreportEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: capacityreportEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('supplierCapacityReport.supplierCapacityReportedEdit'),
        name: 'capacityreportEdit' + row.reportId
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
