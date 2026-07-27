<template>
  <el-container direction="vertical">
    <el-main>
      <div v-if="showTitle">
        <p style="font-weight:bold;font-size: 14px;">
          供应商历史合作信息
        </p>
      </div>
      <el-table
        border
        max-height="400"
        :data="rateList"
      >
        <el-table-column align="center" prop="vendorName" label="" min-width="100" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" prop="cyRate" label="参与率" min-width="120" show-overflow-tooltip>
          <div slot="header">
            <span>参与率</span>
            <el-tooltip
              effect="dark"
              content="参与率=参与投标审核有效性次数/被邀请投标次数*100%"
              placement="top"
              class="table-column-tooltip"
            >
              <em class="el-icon-warning tip-icon" />
            </el-tooltip>
          </div>
          <div slot-scope="scope">{{(scope.row.cyRate*100).toFixed(2)}}<span>%</span></div>
        </el-table-column>
        <el-table-column align="center" prop="zbRate" label="中标率" min-width="80" show-overflow-tooltip>
          <div slot="header">
            <span>中标率</span>
            <el-tooltip
              effect="dark"
              content="中标率=中标次数/参与投标审核有效性次数*100%"
              placement="top"
              class="table-column-tooltip"
            >
              <em class="el-icon-warning tip-icon" />
            </el-tooltip>
          </div>
          <div slot-scope="scope">{{(scope.row.zbRate*100).toFixed(2)}}<span>%</span></div>
        </el-table-column>
        <el-table-column align="center" prop="tj1Rate" label="被推荐率" min-width="100" show-overflow-tooltip>
          <div slot="header">
            <span>被推荐率</span>
            <el-tooltip
              effect="dark"
              content="被推荐率=申请部门推荐次数/推荐表总次数*100%"
              placement="top"
              class="table-column-tooltip"
            >
              <em class="el-icon-warning tip-icon" />
            </el-tooltip>
          </div>
          <div slot-scope="scope">{{(scope.row.tj1Rate*100).toFixed(2)}}<span>%</span></div>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px;">
        <el-table
          border
          max-height="400"
          :data="infoList"
        >
          <el-table-column align="center" prop="vendorName" label="供应商名称" min-width="100" show-overflow-tooltip/>
          <el-table-column align="center" prop="extCategoryName" label="品类" min-width="120" :show-overflow-tooltip="true"/>
          <el-table-column align="center" prop="souName" label="项目名称" min-width="80" show-overflow-tooltip/>
          <el-table-column align="center" prop="submitTime" label="投标时间" min-width="100" show-overflow-tooltip/>
          <el-table-column align="center" prop="isWin" label="是否中标" min-width="100" show-overflow-tooltip/>
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
export default {
  props: {
    idList: null,
    showTitle: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      mergeFlag: false,
      extInvoiceTypeList: [],
      rateList: [],
      infoList: []
    }
  },
  computed: {
    vendorIds () {
      return this.idList || this.$attrs?.params?.idList
    }
  },
  watch: {
    vendorIds: {
      async handler (nVal) {
        if (nVal) {
          this.getDetail(nVal)
          this.getInfo(nVal)
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {

  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'SOU_BIDPRICE_INVOICE_TYPE' } // 发票类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [SOU_BIDPRICE_INVOICE_TYPE] = res.data
        this.extInvoiceTypeList = adaptDictData(SOU_BIDPRICE_INVOICE_TYPE.SOU_BIDPRICE_INVOICE_TYPE)
      })
    },
    /* 构造合计数据结构 */
    setSumStruct (arr) {
      if (arr.length === 0) return arr
      let firstItem = arr[0]
      if (this.mergeFlag) {
        let finalArr = []
        arr.map((item, index) => {
          if (item.extPackageName == arr[index + 1]?.extPackageName) {
            finalArr.push(item)
          } else {
            finalArr.push(item)
            finalArr.push({
              extPackageName: '小计',
              priceList: item?.priceList.map(item => ({
                extPriceSumTax: item.extPacknameProvPriceSumTax
              }))
            })
          }
        })
        return [
          ...finalArr,
          {
            extPackageName: '合计（含税）',
            priceList: firstItem?.priceList.map(item => ({
              extPriceSumTax: item.extTotalProvPriceSumTax
            }))
          },
          {
            extPackageName: '合计（未税）',
            priceList: firstItem?.priceList.map(item => ({
              extPriceSumTax: item.extTotalProvPriceSumNoTax
            }))
          }
        ]
      } else {
        return [
          ...arr,
          {
            itemDesc: '合计（含税）',
            priceList: firstItem?.priceList.map(item => ({
              extPriceSumTax: item.extTotalProvPriceSumTax
            }))
          },
          {
            itemDesc: '合计（未税）',
            priceList: firstItem?.priceList.map(item => ({
              extPriceSumTax: item.extTotalProvPriceSumNoTax
            }))
          }
        ]
      }
    },
    // 设置合并表格行列
    getDetail (ids) {
      let data = []
      if (typeof ids === 'string') {
        data = ids.split(',')
      } else {
        data = ids
      }
      bidBuyerHttp.cooperate.getRate(data).then(res => {
        if (res && res.data) {
          this.rateList = res.data
        }
      })
    },
    getInfo (ids) {
      let data = []
      if (typeof ids === 'string') {
        data = ids.split(',')
      } else {
        data = ids
      }
      bidBuyerHttp.cooperate.getHistory(data).then(res => {
        if (res && res.data) {
          this.infoList = res.data
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
<style>
.comparison-highest-price {
  background-color: yellow;
}
.comparison-lowest-price {
  background-color: #91CE51;
}
</style>
