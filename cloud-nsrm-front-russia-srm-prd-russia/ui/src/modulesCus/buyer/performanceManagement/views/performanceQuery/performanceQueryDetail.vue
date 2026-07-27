<template>
  <el-container
    class="flex-container the_main_po_list"
    direction="vertical"
  >
    <el-main>
      <el-collapse v-model="activeDims">
        <el-collapse-item
          :title="$t('perfMod.comperInfos')"
          name="1"
          class="tab-form-style"
        >
          <div class="the_item1">
            <div class="the_display_content">
              <srm-row>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.vendorName") }}：</span>
                  {{ performanceDetailComputed.companyName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("cusEntry.perfMod.fullPathId") }}：</span>
                  {{ performanceDetailComputed.organizationName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span />
                  <!-- <span>{{$t('perfMod.categoryName')}}：</span>
                            {{ performanceDetailComputed.categoryName }} -->
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.perStartMonth") }}：</span>
                  {{
                    performanceDetailComputed.perStartMonth
                      ? `${
                        performanceDetailComputed.perStartMonth.split("-")[0]
                      }-${
                        performanceDetailComputed.perStartMonth.split("-")[1]
                      }`
                      : ""
                  }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.perEndMonth") }}：</span>
                  {{
                    performanceDetailComputed.perEndMonth
                      ? `${
                        performanceDetailComputed.perEndMonth.split("-")[0]
                      }-${
                        performanceDetailComputed.perEndMonth.split("-")[1]
                      }`
                      : ""
                  }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.perModel") }}：</span>
                  {{ performanceDetailComputed.templateName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.scoreAll") }}：</span>
                  {{ performanceDetailComputed.score }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.rankAll") }}：</span>
                  {{
                    performanceDetailComputed.indicatorCount
                      ? `${performanceDetailComputed.rank}/${
                        performanceDetailComputed.indicatorCount
                      }`
                      : performanceDetailComputed.rank
                  }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t("perfMod.levelName") }}：</span>
                  {{ performanceDetailComputed.levelName }}
                </srm-col>
              </srm-row>
            </div>
          </div>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('perfMod.perInformation')"
          name="2"
        >
          <div
            v-for="(item,
                    index) in performanceDetailComputed.perfIndicatorDimScoreList"
            :key="index"
            class="the_item1"
          >
            <!-- <p class="secSubTitle">{{item.indicatorDimensionTypeName}}{{$t('perfMod.perInformation')}}</p> -->
            <div class="the_display_content">
              <srm-row>
                <!-- <srm-col :initCol="3">
                  <span>{{ item.indicatorDimensionTypeName
                  }}{{ $t("perfMod.indicatorDimensionWeight") }}：</span>
                  {{ item.indicatorDimensionWeight }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ item.indicatorDimensionTypeName
                  }}{{ $t("perfMod.rank") }}：</span>
                  {{ item.rank }}
                </srm-col> -->
                <srm-col :initCol="3">
                  <span>{{ item.indicatorDimensionTypeName
                  }}维度：</span>
                  {{ item.score }}
                </srm-col>
              </srm-row>
            </div>
            <el-table
              :data="item.perfIndDimScoreDetailList"
              style="width: 100%"
              border
              max-height="251px"
              class="mutipTablePage"
            >
              <el-table-column
                align="center"
                prop="indicatorName"
                :label="$t('perfMod.indicatorName')"
                :show-overflow-tooltip="true"
              />
              <!-- <el-table-column
                align="center"
                prop="dimensionWeight"
                :label="$t('perfMod.dimensionWeight')"
                :show-overflow-tooltip="true"
              /> -->
              <el-table-column
                align="center"
                prop="dimensionWeight"
                label="评分方式"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <!-- {{ scope.row.evaluation }} -->
                  {{ $getDictLabel('SCORE_IS', scope.row.evaluation) }}
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="score"
                :label="$t('perfMod.indicatorScore')"
                :show-overflow-tooltip="true"
              />
            </el-table>
          </div>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{
              $t("common.backTo")
            }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import { performanceManagement } from 'modc@/buyer/performanceManagement/api/index'
export default {
  name: 'PerformanceQueryDetail',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      performanceDetail: {}, // 结果明细
      activeDims: ['1', '2', '3', '4'],
      indicatorsDim: []
    }
  },
  computed: {
    performanceDetailComputed () {
      let formatDimVal = val => {
        if (val) {
          return this.formatterDimVal(val)
        } else {
          return '--'
        }
      }
      let obj = { ...this.performanceDetail }
      if (
        obj.perfIndicatorDimScoreList &&
        obj.perfIndicatorDimScoreList.length > 0
      ) {
        obj.perfIndicatorDimScoreList.forEach(item => {
          item.indicatorDimensionTypeName = formatDimVal(
            item.indicatorDimensionType
          )
        })
      }
      return obj
    }
  },
  created () {
    if (this.$attrs.params && this.$attrs.params.row) {
      let overallScoreId = this.$attrs.params.row.overallScoreId
      performanceManagement.findOverallScorelById({ overallScoreId }).then(res => {
        if (res.data) {
          this.performanceDetail = res.data
        }
      })
    }
    this.$nextTick(() => {
      this.fatchDictData()
    })
  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'INDICATORS_DIM' } // 指标维度
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [INDICATORS_DIM] = res.data
        this.indicatorsDim = adaptDictData(
          INDICATORS_DIM.INDICATORS_DIM,
          'dict'
        )
      })
    },
    formatterDimVal (value) {
      return this.$getDictLabelByValue(this.indicatorsDim, value)
    },
    cancelBill () {
      this.$emit(
        'tab-remove',
        'performanceQueryDetail' + this.$attrs.params.row.projectName
      )
      this.__setTabTodo('performanceQueryList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.the_main_po_list {
  padding-bottom: 40px;
  .the_render_list {
    display: flex;
    list-style: none;
    li {
      width: 150px;
      border-left: 1px solid #ccc;
      span {
        display: block;
      }
    }
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .the_header {
    > span {
      padding-right: 11px;
    }
    .el-button {
      float: right;
      margin-right: 11px;
    }
  }

  .btn_line {
    margin: 0;
  }
}
.the_display_content {
  padding: 0 5px;
  .srm-row {
    margin-bottom: 11px;
    .srm-col {
      line-height: 28px;
      color: #606266;
    }
    span {
      display: inline-block;
    }
  }
}
.secSubTitle {
  padding: 0 5px;
  line-height: 30px;
  background-color: #f4f5f7;
  font-size: 14px;
}
</style>
