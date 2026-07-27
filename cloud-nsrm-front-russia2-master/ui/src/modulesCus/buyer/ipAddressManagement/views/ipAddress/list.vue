<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            page-url="/api-sou/bids/ip/address/ipAddress/list"
            :title="$t('common.download')"
            :filterParams="queryParam"
            :table-header="tableHeader"
            :timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :cell-style="cellStyle"
        url="/api-sou/bids/ip/address/ipAddress/list"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'IpAddress',
  components: {
    MainHeader,
    ExportExcel,
    TableView,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preFormObj: {},
      queryForm: []
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'supplierName',
        label: () => this.$t('common.vendorName')
      },
      {
        prop: 'source',
        label: () => this.$t('cusEntry.biddingSettings.monitorSource'),
        type: 'dict',
        code: 'SOU_IP_ADDRESS_SOURCE'
      },
      {
        prop: 'ip',
        label: () => this.$t('cusEntry.biddingSettings.ipAddress')
      },
      {
        prop: 'monitorTime',
        label: () => this.$t('cusEntry.biddingSettings.monitorTime'),
        type: 'daterange'
      }
    ]
    _this.tableHeader = [
      {
        prop: 'supplierCode',
        label: () => this.$t('bidMod.vendorCode'),
        minWidth: 150
      },
      {
        prop: 'supplierName',
        label: () => this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'ip',
        label: () => this.$t('bidMod.ipAddress'),
        minWidth: 150
      },
      {
        prop: 'monitorTime',
        label: () => this.$t('cusEntry.biddingSettings.monitorTime'),
        minWidth: 150
      },
      {
        prop: 'source',
        label: () => this.$t('cusEntry.biddingSettings.monitorSource'),
        formattor: (val) => this.$getDictLabel('SOU_IP_ADDRESS_SOURCE', val),
        minWidth: 120
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 设置IP地址红色 (不同的供应商 && 相同的ip)
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      if (row.sameIpFlag == 'Y' && column.property == 'ip') {
        return {
          // 'font-weight': 'bold',
          'color': '#FF4A4D'
        }
      }
    },
    getQuerydata (v) {
      const {
        monitorTime,
        ...rest
      } = v || {}
      if (monitorTime) {
        const [start, end] = monitorTime
        rest.monitorTimeFrom = start
        rest.monitorTimeTo = end
      }
      this.queryParam = rest || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
