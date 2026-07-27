<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <el-collapse-item title="基础信息" name="1">
          <el-row>
            <el-col :span="8">
              <div>
                <h3>项目信息</h3>
                <p>项目代码: <span>{{ projectCode }}</span></p>
                <p>量产时间: <span>{{ productTime }}</span></p>
                <p>计划定点时间: <span>{{ baseInfo.planFixedTime }}</span></p>
                <p>规划生命周期产量: <span>{{ demandQuantity }}</span></p>
                <p>RFQ包名称: <span>{{ baseInfo.rfqName }}</span></p>
                <p>询价单号: <span>{{ baseInfo.inquiryNo }}</span></p>
              </div>
              <div>
                <h3>团队成员</h3>
                <p>采购工程师: <span>{{ purchaseList }}</span></p>
                <p>研发工程师: <span>{{ projectList }}</span></p>
                <p>质量工程师: <span>{{ qualityList }}</span></p>
                <p>成本核算: <span>{{ baseInfo.costUser }}</span></p>
              </div>
            </el-col>
            <el-col :span="16">
              <div class="price-line-chart-wrap">
                <h3 class="chart-tips">
                  项目生命周期量纲
                </h3>
                <div
                  ref="priceLineChart"
                  :class="className"
                  :style="{ height: height, width: width }"
                />
              </div>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="供应商评估" name="2">
          <div style="min-height:300px;">
            <el-button
              type="primary"
              style="margin-bottom:16px;"
              @click="toReviewDetail"
            >
              查看评审
            </el-button>

            <BaseTable
              ref="table"
              style="height:300px;"
              :columns="columns"
              :data-source="dataSource"
              :initialize="false"
              row-key="vendorCode"
              border
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <template #isPartake="{ scope }">
                <DictSelect
                  v-model="scope.row.isPartake"
                  :lazy-init="lazyInit"
                  :disabled="baseInfo.topicStatus === 'FINISH'"
                  code="YES_OR_NO"
                />
              </template>
            </BaseTable>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          关闭
        </el-button>
        <el-button
          v-if="baseInfo.topicStatus !== 'FINISH'"
          type="primary"
          @click="save"
        >
          暂存
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import echarts from 'echarts'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import resize from '@/library/composition/origin/vendorQoutePriceLineChart/resize'
// import reviewManagementDetail from 'modb@/inquiryBySimpleBuyer/views/reviewManagement/detail'
import 'echarts/theme/macarons'

export default {
  name: 'SsMaterials',
  components: {
    CToolbar,
    BaseTable
  },
  mixins: [tabTodoMixin, resize],
  data () {
    return {
      lazyInit: true,
      columns: [],
      realDataSource: [],
      dataSource: [],
      className: 'chart',
      width: '100%',
      height: '370px',
      chart: null,
      options: {},
      inquiryId: null,
      topicId: null,
      inqMemberList: [],
      baseInfo: {},
      activeDims: ['1', '2', '3', '4']
    }
  },

  computed: {
    projectCode () {
      let result = ''
      result = this.getString('projectCode')
      return result
    },
    productTime () {
      let result = ''
      result = this.getString('productTime')
      return result
    },
    demandQuantity () {
      let result = ''
      result = this.getString('demandQuantity')
      return result
    },
    purchaseList () {
      let result = ''
      result = this.getMember('PURCHASE')
      return result
    },
    projectList () {
      let result = ''
      result = this.getMember('PROJECT')
      return result
    },
    qualityList () {
      let result = ''
      result = this.getMember('QUALITY')
      return result
    }
  },

  async created () {
    const { row } = this.$attrs.params
    this.inquiryId = row.inquiryId
    this.topicId = row.topicId
    this.getInfo(this.inquiryId)
    this.columns = [
      {
        attrs: {
          prop: 'vendorCode',
          label: '供应商代码'
        }
      },
      {
        attrs: {
          prop: 'vendorName',
          label: '供应商名称'
        }
      },
      {
        attrs: {
          prop: 'supProductionSite',
          label: '生产基地'
        }
      },
      {
        attrs: {
          prop: 'supplierIntention',
          label: '合作意向',
          formatter: (cellValue, row) => {
            return this.$getDictLabel('INTENTION_COOPERATE', cellValue)
          }
        }

      },
      {
        attrs: {
          prop: 'ssDevelopmentOpinion',
          label: '研发意见',
          formatter: (cellValue, row) => {
            return this.$getDictLabel('REVIEW_COMMENTS', cellValue)
          }
        }

      },
      {
        attrs: {
          prop: 'ssQualityOpinion',
          label: '质量意见',
          formatter: (cellValue, row) => {
            return this.$getDictLabel('REVIEW_COMMENTS', cellValue)
          }
        }

      },
      {
        attrs: {
          prop: 'isPartake',
          label: '参与报价供应商',
          formatter: (cellValue, row) => {
            return this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        rules: { required: true, message: '必填' },
        slot: 'isPartake'
      }
    ]
    await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, { dictCodeList: ['YES_OR_NO', 'REVIEW_COMMENTS', 'INTENTION_COOPERATE'] })
    this.lazyInit = false
  },
  mounted () {
    this.$nextTick(() => {
      // 以当前标签创建echarts实例
      this.chart = echarts.init(this.$refs.priceLineChart, 'macarons')
      // if (this.vendorPriceNodes) {
      //   this.setChartData()
      // }
    })
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    // 销毁实例
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    toReviewDetail () {
      let reviewId = this.baseInfo.reviewId
      if (reviewId) {
        // this.$emit('tab-add', {
        //   component: reviewManagementDetail,
        //   name: 'reviewManagementDetail' + this.baseInfo.inquiryId,
        //   title: this.baseInfo.rfqName,
        //   params: {
        //     flag: 'view',
        //     row: { reviewId: reviewId },
        //     tabName: 'reviewManagementDetail' + this.baseInfo.inquiryId
        //   }
        // })
      } else {
        this.$message.warning('未发起评审')
      }
    },
    save () {
      let list = this.realDataSource.map(item => {
        return {
          inquiryId: item.inquiryId,
          vendorId: item.vendorId,
          // vendorCode:item.vendorCode,
          // vendorName:item.vendorName,
          // supProductionSite:item.supProductionSite,
          // supplierIntention:item.supplierIntention,
          // ssDevelopmentOpinion:item.ssDevelopmentOpinion,
          // ssQualityOpinion:item.ssQualityOpinion,
          isPartake: item.isPartake
        }
      })
      // let params = {
      //   inquiryId: this.baseInfo.inquiryId,
      //   inqVendorMeetList: list
      // }

      this.$http({
        url: '/api-inq/inquiry/header/saveSs',
        method: 'POST',
        data: list,
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
        .catch(err => {
          console.log(err)
        })
    },
    back () {
      this.$emit('tab-remove', this.$attrs['tabName'])
    },
    getLabel (type, cellValue) {
      let result = ''
      if (cellValue == 'Y') return '同意'
      if (cellValue == 'N') return '不同意'
      return result
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    setChartData () {
      let brgCarModel = this.baseInfo.brgCarModel
      // brgCarModel = {
      //   cy0: 0,
      //   cy1: 300,
      //   cy2: 220,
      //   cy3: 600,
      //   cy4: 800,
      //   cy5: 1000,
      //   cy6: 1200,
      //   cy7: 0,
      //   cy8: 0,
      //   cy9: 0,
      //   cy10: 0
      // }
      let xDataList = []
      let yDataList = []
      // 量纲有11年的数据
      // index 0 ~ 6 一定显示， 7~10显示到非空为止
      let endIndex = 6
      for (let i = 10; i >= 7; i--) {
        if (brgCarModel[`cy${i}`] > 0) {
          endIndex = i
          break
        }
      }
      for (let i = 0; i <= endIndex; i++) {
        let year = this.baseInfo.projectInfo[0].initialYear
        xDataList.push(`${Number(year) + i}`)
        yDataList.push(brgCarModel[`cy${i}`])
      }

      this.options = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        color: ['#5470C6', '#ee6666'],
        xAxis: {
          type: 'category',
          data: xDataList
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: yDataList,
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            },
            label: {
              show: true,
              position: 'top'
            }
          }
        ]
      }
      this.chart.setOption(this.options)
      this.chart.off('legendselectchanged')
      this.chart.on('legendselectchanged', e => {
        // const selected = this.options.legend.selected
        // Object.keys(selected).map(key => {
        //   selected[key] = !selected[key]
        // })
        // this.chart.setOption(this.options)
      })
    },
    getMember (type) {
      let result = ''
      if (this.inqMemberList && this.inqMemberList.length) {
        result = this.inqMemberList.filter(item => item.bidUser == type).map(item => item.nickname)
      }
      return result.toString()
    },
    getString (type) {
      let result = ''
      if (this.baseInfo.projectInfo && this.baseInfo.projectInfo.length) {
        let projectInfo = this.baseInfo.projectInfo
        result = projectInfo.map(item => item[type])
      }
      return result.toString()
    },
    getInfo () {
      this.$http({
        url: '/api-inq/inquiry/header/getInqInfoByMeeting',
        method: 'GET',
        params: { inquiryId: this.inquiryId, topicId: this.topicId },
        loading: true
      })
        .then(res => {
          const { inqMemberList, inqVendorMeetList, ...rest } = res.data
          this.inqMemberList = inqMemberList || []
          let dataSource = inqVendorMeetList || []
          this.dataSource = dataSource.map(item => {
            let obj = {
              ...item,
              __edit_key__: true
            }
            return obj
          })
          this.baseInfo = rest

          this.setChartData()
        })
        .catch(err => {
          console.log(err)
        })
    }

  }
}
</script>
<style  lang="scss" scoped>
</style>
