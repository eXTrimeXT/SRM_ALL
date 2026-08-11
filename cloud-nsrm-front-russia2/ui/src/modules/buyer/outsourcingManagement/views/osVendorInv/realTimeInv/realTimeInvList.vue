<template>
  <el-container
    class="flex-container drawingshead_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <!-- 查询条件 -->
      <FormWrapper
        :form-array="formArray"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <!-- 按钮域 -->
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AsyncFileExportButton
            style="margin-right: 10px;"
            model="OS_VENDOR_REAL_TIME_INV_IMPORT_EXPORT"
            url="/api-sup-ce/os/inv/realTime/exportExcel"
            :query-param="filterParams"
          />
          <!-- 下载列表 -->
          <AsyncFileShowButton
            :title="$t('components.export.dowloadList')"
            model="OS_VENDOR_REAL_TIME_INV_IMPORT_EXPORT"
          />
        </template>
      </MainHeader>

      <!-- 列表 -->
      <TableView
        :ref="tableInfo.gridId"
        :table-data="tableInfo.tableData"
        :table-header="tableInfo.tableHeader"
        :row-index-fixed="false"
        :page-size="tableInfo.pageSize"
        :checkbox="false"
        :pre-query-data="tableInfo.queryParam"
        :source="vendorInvApi.realTimelistPage"
        :open-custom-table="true"
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
import AsyncFileExportButton from 'lib@/components/async-import-export/asyncFileExportButton.vue'
import AsyncFileShowButton from 'lib@/components/async-import-export/asyncFileShowButton.vue'
import { vendorInvApi } from 'modb@/outsourcingManagement/api'
export default {
  name: 'OsVendorRealTimeInvList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    AsyncFileExportButton,
    AsyncFileShowButton
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      vendorInvApi: vendorInvApi,
      formArray: [ // 列表查询参数定义
        {// 物料编码
          prop: 'baseMaterialId',
          label: () => this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {// 供应商
          prop: 'vendorId',
          // '供应商'
          label: this.$t('common.vendor'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info'
        },
        {// 业务实体
          prop: 'orgId',
          // '业务实体'
          label: this.$t('components.organization.ORG'),
          type: 'OUorganizationSelector'
        }
      ],
      tableInfo: { // 列表信息
        gridId: 'osVendorRealTimeTable',
        tableData: [],
        tableHeader: [],
        pageSize: 15,
        queryParam: {} // 查询参数
      },
      filterParams: {}
    }
  },
  created () {
    // 列表定义
    this.tableInfo.tableHeader = [
        {// 业务实体
            label: this.$t('components.organization.ORG'),
            prop: 'orgName',
            width: 160
        },
        {// 库存组织
            label: this.$t('components.organization.INV'),
            prop: 'organizationName',
            width: 160
        },
        {// 委外组件编码
            label: this.$t('outsourcingBomNew.materialCode'),
            prop: 'baseMaterialCode',
            width: 160
        },
        {// 委外组件名称
            label: this.$t('outsourcingBomNew.materialName'),
            prop: 'baseMaterialName',
            width: 160
        },
        {// 供应商编码
            label: this.$t('common.vendorCode'),
            prop: 'vendorCode',
            width: 160
        },
        {// 供应商名称
            label: this.$t('common.companyName'),
            prop: 'vendorName',
            width: 160
        },
        {// 供方库存
            label: this.$t('outsource.supplierInventory'),
            prop: 'vendorInvAmount',
            width: 160
        }
    ]
    this.getQuerydata()
  },
  methods: {
    // 列表查询
    getQuerydata (v) {
      this.tableInfo.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.tableInfo.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.tableInfo.queryParam = values

      this.filterParams = { ...values }
    }
  }
}
</script>
