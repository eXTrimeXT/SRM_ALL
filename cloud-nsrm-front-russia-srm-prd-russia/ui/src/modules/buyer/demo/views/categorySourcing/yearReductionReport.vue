<template>
  <el-container class="flex-container toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        :preFormObj="formQueryData"
        :hasErrorMsgConfirm="true"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton type="primary" @click="exportHandle">
            导出
          </AuthorityButton>
        </template>
        <template slot="right">
          <el-button @click="goBack">
            返回
          </el-button>
        </template>
      </MainHeader>
      <TableView
        ref="list"
        :table-header="tableHeader"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="false"
        :afterData="afterData"
        :tableInfor="tableInfor"
      />
      <!-- url="/api-base/dynamicsql/listByFormCondition" -->
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, formatTimeToDate } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'YearReductionReport',
  components: {
    TableView,
    FormWrapper,
    MainHeader
  },
  data () {
    return {
      filterConfig: [
        {
          label: '年度',
          prop: 'year',
          type: 'year'
          // rules: { required: true, message: '请输入年度' }
        },
        {
          label: '采购员',
          prop: 'purchaseInfo'
        },
        {
          label: '科室',
          prop: 'purchaseDept'
        }
      ],
      tableHeader: [
        {
          label: '科长',
          prop: 'kz',
          width: 100
        },
        {
          label: '系长',
          prop: 'xz',
          width: 100
        },
        {
          label: '采购员',
          prop: 'buyer',
          width: 100
        },
        {
          label: '年度',
          prop: 'progressYear',
          width: 100
        },
        {
          label: '基准配套总额',
          prop: 'baseBomCost',
          width: 150
        },
        {
          label: '确保目标',
          width: 160,
          children: [
            {
              label: '比例',
              prop: 'ensureRatio'
            },
            {
              label: '金额',
              prop: 'ensureAmount'
            }
          ]
        }
      ],
      pageSize: 15,
      queryParam: {},
      tableData: [],
      exportLock: false,
      testData: [],
      tableInfor: []
    }
  },
  async created () {
    let arr12 = new Array(12).fill(null).map((...arr) => arr[1] + 1)
    let monthArr = arr12.map(item => {
      return {
        label: item + '月',
        prop: '',
        width: 160,
        children: [
          {
            label: '直降比例',
            prop: 'downRatio' + item,
            width: 120
          },
          {
            label: '返利比例',
            prop: 'rebateRatio' + item,
            width: 120
          },
          {
            label: '合计比例',
            prop: 'totalRatio' + item,
            width: 120
          },
          {
            label: '直降金额',
            prop: 'downAmount' + item,
            width: 140
          },
          {
            label: '返利金额',
            prop: 'rebateAmount' + item,
            width: 140
          },
          {
            label: '合计金额',
            prop: 'totalAmount' + item,
            width: 120
          },
          {
            label: item + '月累计达成率',
            prop: 'okRate' + item,
            width: 140
          },
          {
            label: '直降占比',
            prop: 'downRate' + item,
            width: 120
          }
        ]
      }
    })
    this.tableHeader = [...this.tableHeader, ...monthArr]
    this.tableInfor = [
      {
        kz: '王天明',
        xz: '李晓晓',
        buyer: '韩明明',
        progressYear: '2021',
        baseBomCost: '32434.54',
        ensureRatio: '20%',
        ensureAmount: '546453',
        downRatio1: '12',
        rebateRatio1: '2',
        totalRatio1: '3',
        downAmount1: '1',
        rebateAmount1: '1',
        totalAmount1: '2',
        okRate1: 'ew',
        downRate1: '3',

        downRatio2: '12',
        rebateRatio2: '2',
        totalRatio2: '3',
        downAmount2: '1',
        rebateAmount2: '1',
        totalAmount2: '2',
        okRate2: 'ew',
        downRate2: '3',
        downRatio3: '12',
        rebateRatio3: '2',
        totalRatio3: '3',
        downAmount3: '1',
        rebateAmount3: '1',
        totalAmount3: '2',
        okRate3: 'ew',
        downRate3: '3',
        downRatio4: '12',
        rebateRatio4: '2',
        totalRatio4: '3',
        downAmount4: '1',
        rebateAmount4: '1',
        totalAmount4: '2',
        okRate4: 'ew',
        downRate4: '3',
        downRatio5: '12',
        rebateRatio5: '2',
        totalRatio5: '3',
        downAmount5: '1',
        rebateAmount5: '1',
        totalAmount5: '2',
        okRate5: 'ew',
        downRate5: '3',
        downRatio6: '12',
        rebateRatio6: '2',
        totalRatio6: '3',
        downAmount6: '1',
        rebateAmount6: '1',
        totalAmount6: '2',
        okRate6: 'ew',
        downRate6: '3',
        downRatio7: '12',
        rebateRatio7: '2',
        totalRatio7: '3',
        downAmount7: '1',
        rebateAmount7: '1',
        totalAmount7: '2',
        okRate7: 'ew',
        downRate7: '3',
        downRatio8: '12',
        rebateRatio8: '2',
        totalRatio8: '3',
        downAmount8: '1',
        rebateAmount8: '1',
        totalAmount8: '2',
        okRate8: 'ew',
        downRate8: '3',
        downRatio9: '12',
        rebateRatio9: '2',
        totalRatio9: '3',
        downAmount9: '1',
        rebateAmount9: '1',
        totalAmount9: '2',
        okRate9: 'ew',
        downRate9: '3',
        downRatio10: '12',
        rebateRatio10: '2',
        totalRatio10: '3',
        downAmount10: '1',
        rebateAmount10: '1',
        totalAmount10: '2',
        okRate10: 'ew',
        downRate10: '3',
        downRatio11: '12',
        rebateRatio11: '2',
        totalRatio11: '3',
        downAmount11: '1',
        rebateAmount11: '1',
        totalAmount11: '2',
        okRate11: 'ew',
        downRate12: '3',
        downRatio12: '12',
        rebateRatio12: '2',
        totalRatio12: '3',
        downAmount12: '1',
        rebateAmount12: '1',
        totalAmount12: '2',
        okRate12: 'ew'
      }
    ]
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = { ...params }
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    async exportHandle () {
      if (this.exportLock) {
        return false
      }
      let flag = await this.$refs.formWrapper.validate()
      if (!flag) {
        return false
      }
      this.exportLock = true
      downloadFileLinkByPost(`${sysPrefix()}/api-base/reduction-report/exportBuyer`, `年降达成-采购员-导出${parseTime(new Date())}.xlsx`, params).then(res => {
        console.log('完成')
        this.exportLock = false
      }).catch(err => {
        console.log('失败')
        this.exportLock = false
      })
    },
    goBack () {
      history.go(-1)
    }
  }
}
</script>
