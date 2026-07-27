<template>
  <el-container
    class="flex-container-notab the_priceInquiry_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <!-- <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">

        </template>
      </main-header> -->
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-inq/price/priceLibrary/listPage"
      />
      <srm-dialog
        v-el-drag-dialog
        title="阶梯价"
        size="middle"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <p class="the_title1">
          物料编码：{{ thisItemCode }} 物料名称：{{ thisItemName }}
        </p>
        <p>阶梯价类型：标准阶梯价</p>
        <el-table
          :data="tableData2"
          style="width: 100%"
          border
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            prop="beginQuantity"
            :label="$t('bidMod.beginQuantity')"
            width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="endQuantity"
            :label="$t('bidMod.endQuantity')"
            width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="unit"
            :label="$t('bidMod.unit')"
            width="100"
            :show-overflow-tooltip="true"
            :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
          />
          <el-table-column
            align="center"
            prop="price"
            :label="$t('bidMod.notaxSelectedPrice')"
            min-width="100"
            :show-overflow-tooltip="true"
          />
        </el-table>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="dialogFormVisible = false"
          >
            {{
              $t('common.cancel')
            }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'

export default {
  name: 'PriceInquiry',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    let _this = this
    return {
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      tableData2: [],
      thisItemCode: '',
      thisItemName: '',
      dialogFormVisible: false,
      statusList: [],
      orderStatusOpts: [],
      preArr: [
        {
          prop: 'organizationId',
          label: _this.$t('bidMod.orgName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'itemCode',
          label: _this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        { prop: 'purType', label: _this.$t('bidMod.taxRate'), type: 'select', options: [] },
        { prop: 'creationDate', label: _this.$t('bidMod.creationDate'), type: 'daterange' }
      ],
      queryParam: {}
    }
  },
  mounted () {},
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'organizationName', label: _this.$t('bidMod.orgName'), minWidth: 150 },
      { prop: 'categoryName', label: _this.$t('bidMod.purcategoryName'), width: 120 },
      { prop: 'itemCode', label: _this.$t('bidMod.itemCode'), width: 120 },
      { prop: 'itemDesc', label: _this.$t('bidMod.itemName'), minWidth: 150 },
      { prop: 'currency', label: _this.$t('bidMod.allAurrency'), width: 100 },
      { prop: 'taxRate', label: _this.$t('bidMod.taxRate'), width: 100 },
      {
        prop: 'isLadder',
        label: _this.$t('bidMod.isLadder'),
        width: 110,
        formattor (val) {
          return val === 'Y' ? '是' : '否'
        }
      },
      {
        prop: 'isLadder',
        label: _this.$t('bidMod.ladderQuote'),
        width: 110,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.openLadderPrice(row)
        }.bind(this),
        formattor (val) {
          return val === 'Y' ? _this.$t('bidMod.ladderQuote') : ''
        }
      },
      { prop: 'notaxPrice', label: _this.$t('bidMod.quotenotaxPrice2'), width: 110 },
      { prop: 'taxPrice', label: _this.$t('bidMod.quotetaxPrice2'), width: 100 },
      { prop: 'effectiveDate', label: _this.$t('bidMod.effectiveDate'), width: 150 },
      { prop: 'expirationDate', label: _this.$t('bidMod.expirationDate'), width: 150 },
      { prop: 'minOrderQuantity', label: _this.$t('bidMod.minOrderQuantity'), width: 100 },
      { prop: 'minPackQuantity', label: _this.$t('bidMod.minPackQuantity'), width: 100 },
      { prop: 'billType', label: _this.$t('bidMod.billType'), width: 100 },
      { prop: 'billCode', label: _this.$t('bidMod.billCode'), width: 100 },
      { prop: 'remarks', label: _this.$t('bidMod.remark'), width: 100 }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      // debugger
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    openLadderPrice (row) {
      this.$http({
        url: '/api-inq/price/priceLadderPrice/listPage',
        method: 'POST',
        data: {
          priceLibraryId: row.priceLibraryId
        },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.tableData2 = data.data.list
            this.thisItemCode = row.itemCode
            this.thisItemName = row.itemDesc
            for (let i of this.tableData2) {
              i.unit = row.unit
            }
            this.dialogFormVisible = true
          }
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss"></style>
