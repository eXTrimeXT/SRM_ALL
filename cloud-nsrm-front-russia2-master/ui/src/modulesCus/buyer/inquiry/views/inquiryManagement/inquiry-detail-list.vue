<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <!--头部查询 -->
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQueryData"
      />
      <!-- 头部按钮 -->
      <MainHeader>
        <template slot="left">
          <ExportExcel
            :page-url="pageListUrl"
            :filter-params="queryParams"
            :table-header="tableHeaderNew"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <!--表格组件 -->
      <TableView
        ref="tableList"
        :table-header="tableHeader"
        big-data
        :pre-query-data="queryParams"
        open-custom-table
        :auto-query="true"
        :com-active="$attrs['changeTab']"
        :url="pageListUrl"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import inquiryTrackingDetail from './inquiryTrackingDetail'
import inquiryDetail from './inquiryDetail'
export default {
  name: 'InquiryDetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      queryParams: {},
      tableHeader: [],
      dictCodes: {
        unit: 'unit'
      },
      pageListUrl: inqBuyerHttp.init.detailPage
    }
  },
  computed: {
    tableHeaderNew () {
      return this.tableHeader.map(item => {
        if (item.prop === 'materialNameShow') {
          item.prop = 'itemDesc'
        }
        if (item.prop === 'extMaterialModelShow') {
          item.prop = 'extMaterialModel'
        }
        return item
      })
    }
  },
  created () {
    /* 查询头初始化 */
    this.preArr = [
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo')
      },
      // 创建人
      {
        prop: 'buyerUsername',
        label: this.$t('cusEntry.inq.purchaser'),
        type: 'quicksearch',
        propKey: 'username',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      },
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode')
      },
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemDesc')
      },
      {
        prop: 'extMaterialModel',
        label: this.$t('cusEntry.bidMod.specification')
      },
      {
        prop: 'creationDate',
        label: this.$t('cusEntry.inq.inqCreationDate'),
        type: 'daterange'
      },
      {
        prop: 'quoteEndDate',
        label: this.$t('cusEntry.inq.quoteEndDate'),
        type: 'daterange'
      },
      // 报价 次数
      // {
      //   prop: 'orderCount',
      //   label: this.$t('cusEntry.inq.quoteCount')
      // },
      // 询价状态
      {
        prop: 'projectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        type: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      }
    ]
    /* 表头初始化 */
    this.tableHeader = [
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openDetailTab(row)
      },
      // t 单据状态
      {
        prop: 'projectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      },
      {
        prop: 'orderCount',
        label: this.$t('cusEntry.inq.quoteCount'),
        minWidth: 130
      },
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode'),
        minWidth: 120
      },
      {
        prop: 'materialNameShow',
        label: this.$t('bidMod.itemDesc'),
        minWidth: 120
      },
      {
        prop: 'extMaterialModelShow',
        label: this.$t('cusEntry.bidMod.specification'),
        minWidth: 120
      },
      {
        prop: 'requireQuantity',
        label: this.$t('cusEntry.inq.quantity'),
        minWidth: 120
      },
      {
        prop: 'unit',
        label: this.$t('cusEntry.inq.baseMeasurmentUnit'),
        minWidth: 120,
        dataType: 'dict',
        code: 'unit'
      },
      {
        prop: 'buyerNickName',
        label: this.$t('cusEntry.inq.pruchaser'),
        minWidth: 120
      },
      {
        prop: 'souCreationDate',
        label: this.$t('cusEntry.inq.inqCreationDate'),
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'orderEndTime',
        label: this.$t('cusEntry.inq.quoteEndDate'),
        minWidth: 120,
        dataType: 'dateTime'
      }
    ]
  },
  methods: {
    /* 打开页签 */
    openDetailTab (row) {
      let options = {}
      if (row.projectStatus === 'DRAFT') {
        options = {
          component: inquiryDetail,
          params: {
            flag: 'edit',
            readOnly: false,
            row: row,
            tabName: row.souNo || ''
          },
          title: row.souNo || '',
          name: row.souNo || ''
        }
      } else {
        options = {
          component: inquiryTrackingDetail,
          params: {
            flag: 'edit',
            row: row,
            tabName: row.souNo || ''
          },
          title: row.souNo || '',
          name: row.souNo || ''
        }
      }
      this.$emit('tab-add', options)
    },
    /* 查询 */
    getQueryData (params) {
      const {
        creationDate,
        quoteEndDate
      } = params || {}
      if (creationDate) {
        const [souCreationDateFrom, souCreationDateTo] = creationDate
        params.souCreationDateFrom = souCreationDateFrom
        params.souCreationDateTo = souCreationDateTo
        Reflect.deleteProperty(params, 'creationDate')
      }
      if (quoteEndDate) {
        const [orderEndTimeFrom, orderEndTimeTo] = quoteEndDate
        params.orderEndTimeFrom = orderEndTimeFrom
        params.orderEndTimeTo = orderEndTimeTo
        Reflect.deleteProperty(params, 'quoteEndDate')
      }
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs.tableList.query()
      })
    },
    afterQuery (data) {
      this.$refs.tableList.setTableData(async tableData => {
        const materialIds = tableData.map(item => item.itemId)
        const response = await this.$http({
          url: '/api-base/material/materialItem/ext/multilingual',
          method: 'POST',
          data: { materialIds, language: this.$i18n.locale },
          loading: true
        })
        
        tableData.forEach((item, index) => {
          const data = response.data.find(it => it.materialId === item.itemId)
          item.materialNameShow = data?.materialName
          item.extMaterialModelShow = data?.extMaterialModel
          this.$set(tableData, index, item)
        })
      })
    }
  }
}
</script>
