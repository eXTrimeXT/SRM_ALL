<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
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
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
export default {
  name: 'MaterialPriceTrendsList',
  components: {
    TableView,
    FormWrapper
  },
  data () {
    return {
      gridList: 'MaterialPriceTrendsList',
      preArr: [],
      tableHeader: [],
      pageSize: 15,
      queryParams: {},
      dynamicProps: [
        'elevenPrice',
        'tenPrice',
        'ninePrice',
        'eightPrice',
        'sevenPrice',
        'sixPrice',
        'fivePrice',
        'fourPrice',
        'threePrice',
        'twoPrice',
        'onePrice',
        'currentMonthPrice'
      ],
      pageUrl: '/api-sup-ce/sc/price/trends/analysis/getOrderPriceTrendsList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'areaCode',
        label: () => this.$t('cusEntry.reportManagement.area')
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
        prop: 'materialDescribe',
        label: () => this.$t('cusEntry.reportManagement.materialDesc')
      },
      {
        prop: 'brand',
        label: () => this.$t('cusEntry.reportManagement.brand')
      }
    ]
    const baseHeader = [
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
        prop: 'materialDescribe',
        label: () => this.$t('cusEntry.reportManagement.materialDesc'),
        minWidth: 120
      },
      {
        prop: 'brand',
        label: () => this.$t('cusEntry.reportManagement.brand'),
        minWidth: 120
      },
      {
        prop: 'areaCode',
        label: () => this.$t('cusEntry.reportManagement.area'),
        minWidth: 120
      },
      {
        prop: 'minPrice',
        label: () => this.$t('cusEntry.reportManagement.lowestPrice'),
        minWidth: 120
      }
    ]
    this.tableHeader = [...baseHeader, ...this.arrangeData()]
  },
  methods: {
    // 查询
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    },
    // 编排十二个月的数据
    arrangeData () {
      const date = new Date()
      let result = []
      // 获取当前年月
      let curMonth = date.getMonth() + 1
      let curYear = date.getFullYear()
      result.push({
        label: `${curYear}年${curMonth}月`,
        prop: this.dynamicProps[this.dynamicProps.length - 1],
        minWidth: 120
      })
      for (let i = 11; i > 0; i--) {
        if (curMonth > 1) {
          result.push({
            label: `${curYear}年${curMonth - 1}月`,
            prop: this.dynamicProps[i - 1],
            minWidth: 120
          })
        } else {
          result.push({
            label: `${curYear - 1}年${curMonth + 12 - 1}月`,
            prop: this.dynamicProps[i - 1],
            minWidth: 120
          })
        }
        curMonth--
      }
      return result
    }
  }
}
</script>
