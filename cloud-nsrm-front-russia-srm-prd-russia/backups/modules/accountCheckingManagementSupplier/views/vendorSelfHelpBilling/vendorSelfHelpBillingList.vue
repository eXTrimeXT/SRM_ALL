<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t("accountMod.createInvioce")
            }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/pss/member/dict/list"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorSelfHelpBillingDetail from './vendorSelfHelpBillingDetail'
import { parseTime } from '@/utils'

export default {
  name: 'VendorSelfHelpBillingList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
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
      statusList: [],
      preArr: [
        {
          prop: 'purOrg',
          label: this.$t('common.orgName'), // 采购组织
          type: 'OUorganizationSelector'
        },
        {
          prop: 'invoiceType',
          label: this.$t('accountMod.invoiceType'), // 发票类型
          type: 'select',
          options: []
        },
        {
          prop: 'startDate',
          label: this.$t('accountMod.timeLimit'), // 时间范围
          type: 'date'
        },
        {
          prop: 'poInvNum',
          label: this.$t('contractMod.invocieNumber') // 发票编号
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'poInvNum',
        label: this.$t('contractMod.invocieNumber'), // 发票编号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab()
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      { prop: 'purOrg', label: this.$t('common.orgName'), width: 200 }, //  采购组织
      {
        prop: 'invoiceDate',
        label: this.$t('accountMod.invoiceCreateDate'),
        minWidth: 120
      }, // 发票创建日期
      {
        prop: 'invoiceType',
        label: this.$t('accountMod.invoiceType'),
        width: 100
      }, // 发票类型
      {
        prop: 'invoiceStatus',
        label: this.$t('purSettlementMod.invoiceStatus'),
        width: 100
      }, // 发票状态
      {
        prop: 'noTaxAmount',
        label: this.$t('contractMod.excludeTaxPayAmount'),
        width: 120
      }, // 不含税金额
      { prop: 'interest', label: this.$t('bid_mod.taxRate'), width: 100 }, // 税率
      {
        prop: 'taxAmount2',
        label: this.$t('contractMod.taxQuota'),
        width: 100
      }, // 税额
      { prop: 'taxAmount', label: this.$t('contractMod.amount2'), width: 100 }, // 含税金额
      {
        prop: 'taxInvNum',
        label: this.$t('accountMod.taxControlInvoiceNum'), // 税控发票号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab()
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'taxInvDate',
        label: this.$t('accountMod.taxControlInvoiceData'),
        width: 120
      }, // 税控发票日期
      {
        prop: 'taxInvAmount',
        label: this.$t('accountMod.taxControlInvoiceAmount'),
        width: 120
      }, // 税控发票金额
      {
        prop: 'taxAmount2',
        label: this.$t('accountMod.taxAmountCrtl'),
        width: 120
      }, // 税额(税控)
      {
        prop: 'currencyType',
        label: this.$t('bid_mod.currencyName'),
        width: 100
      }, // 币种
      {
        prop: 'interest',
        label: this.$t('accountMod.taxRateCtrl'),
        width: 110
      }, // 税率(税控)
      {
        prop: 'fileName',
        label: this.$t('accountMod.fareDifference'),
        width: 100
      }, // 发票价差
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      { prop: 'remark', label: this.$t('common.remark'), width: 100 }
    ]

    // this.$nextTick(() => {
    //   // this.getQuerydata()
    //   let listdata = [];
    //   for (let i = 1; i < 5; i++) {
    //     listdata.push({
    //       poInvNum: "POINV0000" + i,
    //       taxInvNum: "SK202003050" + i,
    //       invoiceStatus: "待引入",
    //       currencyType: "RMB",
    //       invoiceType: "type" + i,
    //       interest: i + "%",
    //       taxAmount2: 10 + i,
    //       purOrg: "OOUU_冰箱事业部_顺德工厂" + i,
    //       createdBy: "杨可乐" + i,
    //       invoiceDate: "2020-3-" + i,
    //       taxInvDate: "2020-8-" + i,
    //       creationDate: "2020-11-" + i
    //     });
    //   }
    //   this.$refs[this.gridId].tableData = listdata;
    // });
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    editTab (type) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: vendorSelfHelpBillingDetail,
          params: { flag: 'add' },
          title: this.$t('accountMod.addStatement'), // 新增对账单
          name: 'vendorSelfHelpBillingDetail'
        }
      } else {
        // 修改
        tab = {
          component: vendorSelfHelpBillingDetail,
          params: {
            flag: 'edit',
            row: this.currentRow
          },
          title: this.currentRow.poInvNum,
          name: 'vendorSelfHelpBillingDetail' + this.currentRow.poInvNum
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the_quotationPrices_wrapper {
}
</style>
