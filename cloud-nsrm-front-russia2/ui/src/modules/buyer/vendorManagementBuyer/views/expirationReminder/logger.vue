<template>
  <el-container
    class="flex-container-notab base_price_list_wrapper"
    direction="vertical"
    style="height: 500px; width: 100%"
  >
    <el-main>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="false"
        url="/api-sup/expireReminder/getReminderRecord"
        :open-custom-table="false"
      >
        <template #authTypeOld="{ scope }">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.fileUploadIdOld,
              fileName: scope.row.authTypeOld
            }"
            :readonly="true"
          />
        </template>
        <template #authType="{ scope }">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.fileUploadId,
              fileName: scope.row.authType
            }"
            :readonly="true"
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import { adaptDictData } from '@/utils'
import { vendorProfileApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'Logger',
  components: {
    TableView
  },
  props: {
    expireReminder: {
      type: Object,
      required: true,
      default: () => {
        return {}
      }
    }
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'LoggerList',
      gridId: 'from',
      queryParam: {},
      pageSize: 10,
      tableHeader: [],
      formTypeList: [],
      tableData: []
    }
  },

  watch: {
    // visible(newValue, oldValue) {
    //   if (newValue !== oldValue) {
    //     this.getQuerydata();
    //   }
    // }
  },
  created () {
    // this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'), // 供应商code
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'dataSources',
        label: () => this.$t('vendorMod.sourceBill'), // 来源单据
        width: 135
      },
      {
        prop: 'formType',
        label: () => this.$t('contractMod.sourceType'), // 来源类型
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'ExpiredCertificateType' // 字典code
        // formattor(val, row) {
        //   return _this.$getDictLabelByValue(_this.formTypeList, val)
        // }
      },
      {
        prop: 'endDateOld',
        label: () => this.$t('vendorMod.certUntil'), // 证件有效期至
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'authTypeOld',
        label: () => this.$t('vendorMod.authFileName'), // 认证文件
        width: 150,
        showType: 'slot',
        slot: 'authTypeOld'
      },
      {
        prop: 'endDate',
        label: () => this.$t('vendorMod.certUntilNewModify'), // 证件有效期至(新修改)
        width: 170,
        dataType: 'dateTime'
      },
      {
        prop: 'authType',
        label: () => this.$t('vendorMod.certDocNewModify'), // 认证文件(新修改)
        width: 150,
        showType: 'slot',
        slot: 'authType'
      },
      {
        prop: 'creationDate',
        label: () => this.$t('vendorMod.modifyTime'), // 修改时间
        width: 150,
        dataType: 'dateTime'
      }
    ]
  },
  methods: {
    getOperationRecord () {
      this.queryParam = this.expireReminder
      delete this.queryParam.pageSize
      vendorProfileApi.getReminderRecord(this.expireReminder).then(async (data) => {
          const tableData = data.data
          await this.$refs[this.gridId].loadDict(tableData)
          this.$refs[this.gridId].tableData = tableData
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
