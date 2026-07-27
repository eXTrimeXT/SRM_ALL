<template>
  <SrmDialog
    title="查看供应商风险"
    size="fullscreen"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    class="riskDialog"
  >
    <div class="risk-info red">
      当前供应商存在{{ riskCount }}处风险
    </div>
    <BaseTable
      stripe
      index
      :data="riskData"
      :columns="riskColumns"
      :empty-text="$t('components.noData')"
      border
      :cell-class-name="setCellClass"
    />
    <div class="abnormal-info">
      异常详情
    </div>
    <BaseTable
      stripe
      index
      :data="abnormalData"
      :columns="abnormalColumns"
      :empty-text="$t('components.noData')"
      border
    />
    <div class="abnormal-info">
      供应商履约信息（同类品）
      <el-date-picker v-model="myDate" style="width: 200px;" type="daterange" range-separator="至" value-format="yyyy-MM-dd" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      <el-button size="small" type="primary" style="margin-left: 10px" @click="searchQuery">查询</el-button>
    </div>
    <div class="abnormal-info">时间：近三年</div>
    <div class="my_chart" >
      <div class="my_chart_line">
        <div v-for="(item, index) in 6" :key="item" class="chat_line_item">
          <div class="chart_line_name">{{100-20*index}}</div>
          <div class="chart_line_content"></div>
        </div>
      </div>
      <div ref="myChartContent" class="my_chart_content">
        <div ref="chartContentInner" class="chart_content_inner" :style="styles">
          <div v-for="(one, idx) in chartArr" :key="idx" class="chart_content_item">
            <el-popover placement="right" trigger="hover">
              <div style="padding: 20px">
                <div style="font-size: 16px;font-weight: 700;margin-bottom:20px">{{one.name}}</div>
                <div v-for="(contract, j) in one.list" :key="j" class="chart_popover">
                  <div class="chart_popover_inner" :style="{backgroundColor:j>chartOption.length-1?chartOption[j-chartOption.length+1].color : chartOption[j].color}"></div>
                  <div style="margin-left: 10px;font-size: 14px">{{contract.contractName}}：{{contract.score}}</div>
                </div>
              </div>
              <div slot="reference"  class="content_item_child">
                <div v-for="(o, i) in one.list" :key="i" class="item_child_inner" :style="{height: o.score*3+'px',backgroundColor:i>chartOption.length-1?chartOption[i-chartOption.length+1].color : chartOption[i].color}">
                  <div class="child_inner_num">{{o.score}}</div>
                </div>
              </div>
            </el-popover>
            <div style="height: 30px;line-height: 30px;padding: 0 20px;text-align: center;white-space: nowrap">{{one.name}}</div>
          </div>
        </div>
      </div>
    </div>
    <BaseTable
      stripe
      index
      :data="supplierList"
      :columns="supplierColumns"
      :empty-text="$t('components.noData')"
      border
    />
    <div slot="footer" class="dialog-footer">
      <!-- <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button> -->
    </div>
  </SrmDialog>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import recommendHttp from 'modcb@/supplierRecommend/api'

export default {
  name: 'InviteDialog',
  components: {
    TableView,
    BaseTable
  },
  props: {
    applicantNo: {
      type: String,
      default: ''
    },
    projectId: {
      type: [String, Number],
      default: ''
    },
    idList: {
      type: Array,
      default: () => []
    },
    nameList: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      judgeScroll: false,
      riskCount: 0,
      myDate: [],
      riskColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            label: '供应商编码',
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '推荐供应商',
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'timeLimitFlag',
            label: '时间受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsMainPoint',
            label: '重点关注',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'groupBlacklistFlag',
            label: '是否集团黑名单',
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'positionLimitFlag',
            label: '是否单位受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'categoryLimitFlag',
            label: '是否品类受限',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'keySupervisionFlag',
            label: '是否重点监督',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsDishonesty',
            label: '是否失信',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'extIsBizAnomaly',
            label: '是否经营异常',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'contackRepeatFlag',
            label: '联系人是否重复',
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'telRepeatFlag',
            label: '联系人电话是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'emailRepeatFlag',
            label: '联系人邮箱是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'holderRepeatFlag',
            label: '股东是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'legalRepeatFlag',
            label: '法人是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'relBlacklistRepeatFlag',
            label: '关联关系供应商是否黑名单',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'mainPeopleRepeatFlag',
            label: '主要人员是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        }
      ],
      riskData: [],
      abnormalColumns: [
        {
          attrs: {
            type: 'index',
            label: '序号',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'type',
            label: '异常类型',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'description',
            label: '异常说明',
            showOverflowTooltip: true
          }
        }
      ],
      abnormalData: [],
      supplierColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'companyCode',
            label: '供应商编码',
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'companyName',
            label: '供应商名称',
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'performanceType',
            label: '履约类型',
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true,
            formatter: (row, column, cellValue) => this.$getDictLabel('PERF_PERFORMANCE_TYPE', cellValue)
          }
        },
        {
          attrs: {
            prop: 'bidCode',
            label: '招标编号',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'projectName',
            label: '评分项目名称',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'buOrganizationName',
            label: '板块',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'ouOrganizationName',
            label: '公司',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'score',
            label: '得分',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        // {
        //   attrs: {
        //     prop: 'contractNo',
        //     label: '合同编码',
        //     minWidth: 150,
        //     showOverflowTooltip: true
        //   }
        // },
        {
          attrs: {
            prop: 'contractName',
            label: '合同名称',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'performanceCode',
            label: '履约阶段',
            minWidth: 150,
            showOverflowTooltip: true,
            formatter: (row, column, cellValue) => this.$getDictLabel('MILESTONE_SCHEDULE', cellValue)
          }
        },
        {
          attrs: {
            prop: 'categoryName',
            label: '品类',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'lastUpdateDate',
            label: '评分时间',
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'projectStatus',
            label: '整体评分状态',
            minWidth: 150,
            showOverflowTooltip: true,
            formatter: (row, column, cellValue) => this.$getDictLabel('PROJECT_SCORE_HEADER_STATUS', cellValue)
          }
        },

      ],
      supplierList: [],
      chartOption: [
        { color: '#E4F7FF' },
        { color: '#B2E4FF' },
        { color: '#C7EA3C' },
        { color: '#90D61E' },
        { color: '#46C917' },
        { color: '#3930A8' },
        { color: '#005C40' }
      ],
      chartArr: []
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.myDate = []
          this.getFormDetail().then(() => {
            return this.getChartInfo()
          }).then(() => {
            this.getChartList()
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    setCellClass({ row, column, rowIndex, columnIndex }) {
      if (row[column.property] === 'Y') return 'red'
    },
    async getFormDetail () {
      if (!this.idList || !this.idList.length) return
      let vendorList = this.idList.map(item => ({
        vendorId: item
      }))
      let transformParams = transformMQL.save('Recommvendor',
        {
          applicantNo: this.applicantNo,
          recommvendorList: vendorList
        }
        , 'queryRisk')
      const response = await recommendHttp.queryRisk(transformParams)
      if (response.data.records.length) {
        const { riskItemList = [], vendorRiskList = [] } = response.data.records[0].vendorRisk
        this.riskData = vendorRiskList || []
        this.abnormalData = riskItemList || []
        if (this.riskData.length) {
          let count = 0
          for (let item of this.riskData) {
            for (let key in item) {
              if (item[key] === 'Y') {
                count++
              }
            }
          }
          this.riskCount = count
        }
      }
    },
    searchQuery () {
      this.getChartInfo().then(() => {
        this.getChartList()
      })
    },
    async getChartInfo () {
      if (!this.idList || !this.idList.length) return
      let vendorList = this.idList.map(item => ({
        vendorId: item
      }))
      let calcDateStart = null
      let calcDateEnd = null
      if (this.myDate && this.myDate.length > 0) {
        calcDateStart = this.myDate[0]
        calcDateEnd = this.myDate[1]
      }
      let transformParams = transformMQL.save('Recommvendor',
        {
          applicantNo: this.applicantNo,
          // projectId: '478965737319680',
          recommvendorList: vendorList,
          calcDateStart: calcDateStart,
          calcDateEnd: calcDateEnd
        }
        , 'queryHisScore')
      const response = await recommendHttp.chartInfo(transformParams)
      if (response.data.records.length) {
        this.supplierList = response.data.records[0].hisScore
      }
    },
    async getChartList () {
      let xArr = []
      if (this.nameList && this.nameList.length > 0) {
        this.nameList.forEach(item => {
          const obj = {
            name: item
          }
          if (this.supplierList && this.supplierList.length > 0) {
            obj.list = this.supplierList.filter(one => one.companyName === item)
            obj.list.sort(function (a, b) { return b.score - a.score })
          } else {
            obj.list = []
          }
          xArr.push(obj)
        })
      }
      this.chartArr = xArr
      console.log(this.chartArr)
    }
  }
}
</script>
<style style="scss" scoped>
  .my_chart {
    /*width: 800px;*/
    position: relative;
    margin-top: 20px;
    margin-bottom: 10px;
    height: 380px;
  }

  .my_chart .my_chart_line {
    width: 100%;
    position: absolute;
    top: 0;
  }

  .my_chart .my_chart_line .chat_line_item {
    height: 60px;
    display: flex;
    align-items: center;
  }

  .my_chart .my_chart_line .chat_line_item .chart_line_name {
    width: 60px;
    text-align: right;
    margin-right: 5px;
  }

  .my_chart .my_chart_line .chat_line_item .chart_line_content {
    width: calc(100% - 60px);
    height: 1px;
    background: rgba(0, 0, 0, 0.3);
  }

  .my_chart_content {
    width: calc(100% - 80px);
    position: absolute;
    top: 0;
    /*width: 100%;*/
    height: 100%;
    margin-left: 60px;
    overflow-x: auto;
    padding-top: 30px;
  }

  .my_chart_content .chart_content_inner {
    height: 330px;
    display: flex;
    align-items: flex-end;
    /*justify-content: center;*/
    gap: 20px;
  }

  .my_chart_content .chart_content_item {
    flex: 1;
  }

  .my_chart_content .chart_content_item .content_item_child {
    display: flex;
    align-items: flex-end;
    justify-content: center;
    gap: 10px;
    padding: 0 30px;
  }

  .my_chart_content .chart_content_item .content_item_child .item_child_inner {
    width: 20px;
    position: relative;
  }
  .my_chart_content .chart_content_item .content_item_child .child_inner_num{
    position: absolute;
    width: 30px;
    height: 20px;
    line-height: 20px;
    top: -22px;
    left: -5px;
    text-align: center;
    min-width: 100%;
  }
  .chart_popover{
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
  .chart_popover:last-child{
    margin-bottom: 0;
  }
  .chart_popover .chart_popover_inner {
    width: 12px;
    height: 12px;
    border-radius: 50%;
  }

  .red {
    color: red;
  }

  .risk-info {
    margin-bottom: 10px;
  }

  .abnormal-info {
    font-size: 14px;
    font-weight: bold;
    margin: 10px 0;
  }
</style>
<style style="scss">
  .red .cell {
    color: red;
  }

  .riskDialog .srm-dialog-content {
    max-height: 100% !important;
  }

  .riskDialog .el-dialog__footer {
    height: 0 !important;
  }
</style>
