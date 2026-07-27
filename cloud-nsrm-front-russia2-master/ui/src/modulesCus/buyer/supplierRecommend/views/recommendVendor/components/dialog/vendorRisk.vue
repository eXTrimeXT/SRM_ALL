<template>
  <!-- <SrmDialog
    title="查看供应商风险"
    size="fullscreen"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    class="riskDialog"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.viewSupplierRisks')"
    size="fullscreen"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    class="riskDialog"
  >
    <div class="risk-info red">
      <!-- 当前供应商存在{{ riskCount }}处风险 -->
      {{ $t("cusEntry.supplement20250121.theCurrentSupplierExists") }}{{ riskCount }}{{ $t("cusEntry.supplement20250121.riskManagement") }}
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
      <!-- 异常详情 -->
       {{ $t("cusEntry.supplement20250121.exceptionDetails") }}
    </div>
    <BaseTable
      stripe
      index
      :data="abnormalData"
      :columns="abnormalColumns"
      :empty-text="$t('components.noData')"
      border
    />
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
            // label: '序号',
            label: () =>this.$t("components.common.sort"),
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            // label: '供应商编码',
            label: () =>this.$t("common.vendorCode"),
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            // label: '推荐供应商',
            label: () =>this.$t("dataConfMod.ifRecommendVendor"),
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'timeLimitFlag',
            // label: '时间受限',
            label: () =>this.$t("cusEntry.supplement20250121.timeConstrained"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        // {
        //   attrs: {
        //     prop: 'extIsMainPoint',
        //     label: '重点关注',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // },
        {
          attrs: {
            prop: 'groupBlacklistFlag',
            // label: '是否集团黑名单',
            label: () =>this.$t("cusEntry.supplement20250121.isItOnTheGroupBlacklist"),
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'positionLimitFlag',
            // label: '是否单位受限',
            label:  () =>this.$t("cusEntry.supplement20250121.isTheUnitRestricted"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'categoryLimitFlag',
            // label: '是否品类受限',
            label: () =>this.$t("cusEntry.supplement20250121.isTheCategoryRestricted"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'keySupervisionFlag',
            // label: '是否重点监督',
            label: () =>this.$t("cusEntry.supplement20250121.isThereAFocusOnSupervision"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        // {
        //   attrs: {
        //     prop: 'extIsDishonesty',
        //     label: '是否失信',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // },
        // {
        //   attrs: {
        //     prop: 'extIsBizAnomaly',
        //     label: '是否经营异常',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // },
        {
          attrs: {
            prop: 'contackRepeatFlag',
            // label: '联系人是否重复',
            label: () =>this.$t("cusEntry.supplement20250121.isTheContactPersonDuplicated"),
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'telRepeatFlag',
            // label: '联系人电话是否重复',
            label: () =>this.$t("cusEntry.supplement20250121.isTheContactPhoneNumberDuplicated"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'emailRepeatFlag',
            // label: '联系人邮箱是否重复',
            label: () =>this.$t("cusEntry.supplement20250121.isTheContactEmailDuplicated"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        // {
        //   attrs: {
        //     prop: 'holderRepeatFlag',
        //     label: '股东是否重复',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // },
        // {
        //   attrs: {
        //     prop: 'legalRepeatFlag',
        //     label: '法人是否重复',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // },
        {
          attrs: {
            prop: 'relBlacklistRepeatFlag',
            // label: '关联关系供应商是否黑名单',
            label: () =>this.$t("cusEntry.supplement20250121.isTheAffiliatedSupplierBlacklisted"),
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        // {
        //   attrs: {
        //     prop: 'mainPeopleRepeatFlag',
        //     label: '主要人员是否重复',
        //     minWidth: 100,
        //     formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
        //   }
        // }
      ],
      riskData: [],
      abnormalColumns: [
        {
          attrs: {
            type: 'index',
            // label: '序号',
            label: () =>this.$t("components.common.sort"),
            width: 60
          }
        },
        {
          attrs: {
            prop: 'type',
            // label: '异常类型',
            label: () =>this.$t("quality.exceptionCatelog2"),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'description',
            // label: '异常说明',
            label: () =>this.$t("cusEntry.supplement20250121.abnormalDescription"),
            showOverflowTooltip: true
          }
        }
      ],
      abnormalData: [],
      supplierColumns: [
        {
          attrs: {
            // label: '序号',
            label: () =>this.$t("components.common.sort"),
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'companyCode',
            // label: '供应商编码',
            label: () =>this.$t("common.vendorCode"),
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'companyName',
            // label: '供应商名称',
            label: () =>this.$t("common.companyName"),
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'performanceType',
            // label: '履约类型',
            label: () =>this.$t("cusEntry.reportManagement.performanceStatusType"),
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true,
            formatter: (row, column, cellValue) => this.$getDictLabel('PERF_PERFORMANCE_TYPE', cellValue)
          }
        },
        {
          attrs: {
            prop: 'bidCode',
            // label: '招标编号',
            label: () =>this.$t("cusEntry.bidMod.extProjectNo"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'projectName',
            // label: '评分项目名称',
            label: () =>this.$t("cusEntry.perfMod.projectName"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'buOrganizationName',
            // label: '板块',
            label: () =>this.$t("cusEntry.bidSuperviseReport.extOrgBuName"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'ouOrganizationName',
            // label: '公司',
            label: () =>this.$t("components.organization.COMPANY"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'score',
            // label: '得分',
            label: () =>this.$t("vendorMod.vendorSelfScoring"),
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
            // label: '合同名称',
            label: () =>this.$t("vendorMod.contractName"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'performanceCode',
            // label: '履约阶段',
            label: () =>this.$t("cusEntry.supplement20250121.performanceStage"),
            minWidth: 150,
            showOverflowTooltip: true,
            formatter: (row, column, cellValue) => this.$getDictLabel('MILESTONE_SCHEDULE', cellValue)
          }
        },
        {
          attrs: {
            prop: 'categoryName',
            // label: '品类',
            label: () =>this.$t("common.category"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'lastUpdateDate',
            // label: '评分时间',
            label: () =>this.$t("vendorMod.scoringTime"),
            minWidth: 150,
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'projectStatus',
            // label: '整体评分状态',
            label: () =>this.$t("cusEntry.supplement20250121.overallRatingStatus"),
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
          this.getFormDetail()
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
