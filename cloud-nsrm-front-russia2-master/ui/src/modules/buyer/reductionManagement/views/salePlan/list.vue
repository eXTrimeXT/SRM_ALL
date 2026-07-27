<template>
  <el-container
    class="flex-container toolinginfo_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        @getFormData="getQuerydata"
      >
        <template #date="{ scope }">
          <!-- 开始时间 -->
          <el-date-picker
            v-model="scope.date"
            type="daterange"
            value-format="yyyy-MM-dd"
            :format="$formatDatePicker"
            range-separator="~"
            :start-placeholder="$t('components.common.startTime')"
            :end-placeholder="$t('components.common.endTime')"
          />
        </template>
      </FormWrapper>

      <MainHeader
        :lSpan="22"
        :rSpan="2"
      >
        <template slot="left">
          <AuthorityButton
            code="base:salePlan:export"
            @click="exportHandle"
          >
            <!-- 导出 -->
            {{ $t("common.export") }}
          </AuthorityButton>
          <!-- 导入计划 -->
          <MImport
            style="display: inline-block;  margin-right: 10px;"
            :title="$t('cusEntry.supplement20250211.importPlan')"
            upLoadUrl="/api-sou/sale-plan/importEstimate"
            :extraData="extraData"
            code="base:salePlan:importEstimate"
            @downloadTemplate="downloadItemTemplateEstimates"
            @handleSuccess="handleSuccess"
          />
          <!-- 导入实际 -->
          <MImport
            style="display: inline-block;"
            :title="$t('cusEntry.supplement20250211.importActual')"
            upLoadUrl="/api-sou/sale-plan/importActual"
            :extraData="extraData"
            code="base:salePlan:importActual"
            @downloadTemplate="downloadItemTemplateActual"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton
            type="primary"
            code="base:salePlan:delete"
            @click="deleteHandle"
          >
            <!-- 删除 -->
            {{ $t("components.common.delete") }}
          </AuthorityButton>

          <AuthorityButton
            type="primary"
            @click="rebuildBomInBatch"
          >
            <!-- 刷新bom结构 -->
            {{ $t("cusEntry.supplement20250211.refreshBomStructure") }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="genMonthlyByMtocs"
          >
            <!-- 生成本年月度成本数据 -->
            {{ $t("cusEntry.supplement20250211.generateCurrentMonthCostData") }}
          </AuthorityButton>
        </template>
        <template slot="left" style="margin-top:10px">
          <el-form
            ref="form"
            :model="form"
            label-width="100px"
            :disabled="disabledFlag"
            label-position="left"
          >
            <srm-row style="padding-top:20px">
              <srm-col :init-col="4">
                <!-- 全年国内计划合计 -->
                <el-form-item
                  prop="yearPlanTotal"
                  :label="$t('cusEntry.supplement20250211.yearlyDomesticPlanTotal')"
                >
                  <span class="totalSpan">{{ form.yearPlanTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <!-- 全年国内实际合计 -->
                <el-form-item
                  prop="yearActualTotal"
                  :label="$t('cusEntry.supplement20250211.yearlyDomesticTotal')"
                >
                  <span class="totalSpan">{{ form.yearActualTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <!-- 全年海外计划合计 -->
                <el-form-item
                  prop="yearPlanSeaTotal"
                  :label="$t('cusEntry.supplement20250211.overseasPlanTotal')"
                >
                  <span class="totalSpan">{{ form.yearPlanSeaTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <!-- 全年海外实际合计 -->
                <el-form-item
                  prop="yearActualSeaTotal"
                  :label="$t('cusEntry.supplement20250211.overseasActualTotalAnnual')"
                >
                  <span class="totalSpan">{{ form.yearActualSeaTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :checkChange="handleCurrentChange"
        :openCustomTable="true"
        url="/api-sou/sale-plan/query"
        :checkbox="true"
      >
        <!-- <template #linkagePriceNo="{ scope }">
                    <span style="color:#1890ff" @click="goEdit(scope.row,'view')">{{scope.row.linkagePriceNo}}</span>
                </template> -->
      </TableView>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { sysPrefix } from '@/config/ipConfig'
import { adaptDictData, parseTime } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import MImport from 'lib@/components/import'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { toThousandFilter } from '@/filters'

let month = [
  'jan',
  'feb',
  'mar',
  'apr',
  'may',
  'jun',
  'jul',
  'aug',
  'sep',
  'oct',
  'nov',
  'dec'
]

export default {
  name: 'SalePlanList',
  components: {
    TableView,
    FormWrapper,
    MainHeader,
    MImport
  },
  filters: {
    toThousandFixed2Fliter (val) {
      if (val) {
        let num2 = val.toFixed(2)
        let [numInt, numFloat] = num2.split('.')
        numInt = toThousandFilter(numInt)
        return numInt + '.' + numFloat
      } else {
        return val
      }
    }
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      disabledFlag: true,
      form: {
        yearPlanTotal: '',
        yearActualTotal: '',
        yearPlanSeaTotal: '',
        yearActualSeaTotal: ''
      },
      pageSize: 15,
      queryParam: {},
      filterConfig: [
        {
          prop: 'year',
          // '年度'
          label: this.$t('vendorMod.YEAR'),
          type: 'year'
        },
        {
          prop: 'planVersion',
          // '产销计划版本'
          label: this.$t('cusEntry.supplement20250211.productionSalesPlanVersion')
        },
        {
          prop: 'modelCode',
          // '车型编码'
          label: this.$t('problemManagement.motorcycleTypeCode'),
          type: 'quicksearch',
          showKey: 'modelCode',
          name: 'gacm_delmodules_search'
        },
        {
          prop: 'modelName',
          // '车型名称'
          label: this.$t('problemManagement.motorcycleTypeName')
        },
        {
          prop: 'mtos',
          type: 'formattorText',
          // '请输入MTO'
          title: this.$t('cusEntry.supplement20250211.mtoInput'),
          label: 'MTO'
        },
        {
          prop: 'mtocs',
          type: 'formattorText',
          // '请输入MTOC'
          title: this.$t('cusEntry.supplement20250211.mtocInput'),
          label: 'MTOC'
        },
        {
          prop: 'engineName',
          // '发动机'
          label: this.$t('cusEntry.supplement20250211.engine')
        },
        {
          prop: 'gearboxCategory',
          // '变速箱'
          label: this.$t('cusEntry.supplement20250211.transmissionBox')
        },
        {
          prop: 'planningDerivation',
          // '是否企划派生'
          label: this.$t('cusEntry.supplement20250211.isPlanDerived'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'orgId',
          // '工厂'
          label: this.$t('qualitySynergy.factory'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'modelType',
          // '车型状态'
          label: this.$t('reduce.dimensionStatus'),
          type: 'dict',
          code: 'AUTO_MODEL_STATUS'
        },
        {
          prop: 'budgetType',
          // '预算场景类型'
          label: this.$t('cusEntry.supplement20250211.budgetScenarioType'),
          type: 'dict',
          code: 'BUDGET_BNS_TYPE'
        },
        {
          prop: 'effStatus',
          // '是否生效'
          label: this.$t('dataConfMod.enabled'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'planType',
          // '计划类型'
          label: this.$t('vendorMod.planType'),
          type: 'dict',
          code: 'SALE_PLAN_TYPE'
        },
        {
          prop: 'planVerName',
          // '产销计划版本名称'
          label: this.$t('cusEntry.supplement20250211.productionPlanVersionName')
        }
      ],
      tableHeader: [
        {
          prop: 'planYear',
          // '年度'
          label: this.$t('vendorMod.YEAR'),
          minWidth: 160
        },
        {
          prop: 'orgName',
          // '生产工厂'
          label: this.$t('cusEntry.supplement20250211.productionFactory'),
          minWidth: 160
        },
        {
          prop: 'modelType',
          // '车型状态'
          label: this.$t('reduce.dimensionStatus'),
          minWidth: 160,
          formattor: (val) =>
            this.modelTypeMap.has(val) ? this.modelTypeMap.get(val) : val
        },
        {
          prop: 'modelCode',
          // '车型编码'
          label: this.$t('problemManagement.motorcycleTypeCode'),
          minWidth: 160
        },
        {
          prop: 'modelName',
          // '车型名称'
          label: this.$t('problemManagement.motorcycleTypeName'),
          minWidth: 160
        },
        {
          prop: 'mto',
          label: 'MTO',
          minWidth: 160
        },
        {
          prop: 'mtoc',
          label: 'MTOC',
          minWidth: 160
        },
        {
          prop: 'planningDerivation',
          // '是否企划派生'
          label: this.$t('cusEntry.supplement20250211.isPlanDerived'),
          minWidth: 160,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'configName',
          // '配置名称'
          label: this.$t('todolistConfig.configName'),
          minWidth: 160
        },
        {
          prop: 'engineName',
          // '发动机'
          label: this.$t('cusEntry.supplement20250211.engine'),
          minWidth: 160
        },
        {
          prop: 'gearboxCategory',
          // '变速箱类别'
          label: this.$t('cusEntry.supplement20250211.gearboxCategory'),
          minWidth: 160
        },
        {
          prop: 'planType',
          // '计划类型'
          label: this.$t('vendorMod.planType'),
          minWidth: 160
        },
        {
          prop: 'planVersion',
          // '计划版本'
          label: this.$t('cusEntry.supplement20250211.planVersion'),
          minWidth: 160
        },
        //
        {
          prop: 'planVerName',
          // '计划版本名称'
          label: this.$t('cusEntry.supplement20250211.planVersionName'),
          minWidth: 160
        },
        {
          prop: 'budgetType',
          // '预算场景类型'
          label: this.$t('cusEntry.supplement20250211.budgetScenarioType'),
          minWidth: 160,
          dataType: 'dict',
          code: 'BUDGET_BNS_TYPE'
        },
        {
          prop: 'effStatus',
          // '是否生效'
          label: this.$t('dataConfMod.enabled'),
          minWidth: 160,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'sourceFrom',
          // '数据来源'
          label: this.$t('vendorMod.dataSources'),
          minWidth: 160,
          dataType: 'dict',
          code: 'BNS_SOURCE_FROM'
        },

        {
          prop: 'yearPlanTotal',
          // '全年国内计划合计'
          label: this.$t('cusEntry.supplement20250211.yearlyDomesticPlanTotal'),
          minWidth: 160,
          formattor: (val) => {
            if (val) {
              return val.toString().replace(/\d+/, function (n) {
                return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
              })
            }
          }
        },
        {
          prop: 'yearActualTotal',
          // '全年国内实际合计'
          label: this.$t('cusEntry.supplement20250211.yearlyDomesticTotal'),
          minWidth: 160,
          formattor: (val) => {
            if (val) {
              return val.toString().replace(/\d+/, function (n) {
                return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
              })
            }
          }
        },

        {
          prop: 'yearPlanSeaTotal',
          // '全年海外计划合计'
          label: this.$t('cusEntry.supplement20250211.overseasPlanTotal'),
          minWidth: 160,
          formattor: (val) => {
            if (val) {
              return val.toString().replace(/\d+/, function (n) {
                return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
              })
            }
          }
        },
        {
          prop: 'yearActualSeaTotal',
          // '全年海外实际合计'
          label: this.$t('cusEntry.supplement20250211.overseasActualTotalAnnual'),
          minWidth: 160,
          formattor: (val) => {
            if (val) {
              return val.toString().replace(/\d+/, function (n) {
                return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
              })
            }
          }
        }
      ],
      selectArr: [],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'salePlan',
        fileType: 'excel'
      },
      modelTypeMap: new Map()
    }
  },
  created () {
    let objArr = month.map((item, index) => {
      return {
        prop: '',
        // '月'
        label: index + 1 + this.$t('time.months'),
        minWidth: 160,
        children: [
          {
            prop: item + 'PlanTotal',
            // '国内预计'
            label: this.$t('cusEntry.supplement20250211.domesticEstimate'),
            minWidth: 80,
            formattor: (val) => {
              if (val) {
                return val.toString().replace(/\d+/, function (n) {
                  return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
                })
              }
            }
          },
          {
            prop: item + 'ActualTotal',
            // '国内实际'
            label: this.$t('cusEntry.supplement20250211.internalActual'),
            minWidth: 80,
            formattor: (val) => {
              if (val) {
                return val.toString().replace(/\d+/, function (n) {
                  return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
                })
              }
            }
          },
           {
            prop: item + 'PlanSea',
            // '海外预计'
            label: this.$t('cusEntry.supplement20250211.overseasForecast'),
            minWidth: 80,
            formattor: (val) => {
              if (val) {
                return val.toString().replace(/\d+/, function (n) {
                  return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
                })
              }
            }
          },
           {
            prop: item + 'ActualSea',
            // '海外实际'
            label: this.$t('cusEntry.supplement20250211.overseasActual'),
            minWidth: 80,
            formattor: (val) => {
              if (val) {
                return val.toString().replace(/\d+/, function (n) {
                  return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
                })
              }
            }
          }
        ]
      }
    })

    let createArr = [
      {
        prop: 'oldMto',
        // '原MTO'
        label: this.$t('cusEntry.supplement20250211.originalMto'),
        minWidth: 100
      },
      {
        prop: 'createdBy',
        // '创建人'
        label: this.$t('common.creator'),
        minWidth: 100
      },
      {
        prop: 'creationDate',
        // '创建时间'
        label: this.$t('common.creationTime'),
        minWidth: 130,
        dataType: 'dateTime',
        sortMethod: (a, b) => {
          return (
            new Date(a.creationDate).getTime() -
            new Date(b.creationDate).getTime()
          )
        }
      },
      {
        prop: 'lastUpdatedBy',
        // '更新人'
        label: this.$t('common.updatePeople'),
        minWidth: 100
      },
      {
        prop: 'lastUpdateDate',
        // '更新时间'
        label: this.$t('components.workedProcess.headers.fdEndDate'),
        minWidth: 130,
        dataType: 'dateTime',
        sortMethod: (a, b) => {
          return (
            new Date(a.lastUpdateDate).getTime() -
            new Date(b.lastUpdateDate).getTime()
          )
        }
      }
    ]

    this.tableHeader = [...this.tableHeader, ...objArr, ...createArr]

    this.$store
      .dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL, {
        dictCode: 'AUTO_MODEL_STATUS'
      })
      .then((data) => {
        let modelTypeArr = data.map((item) => {
          return [item.value, item.label]
        })
        this.modelTypeMap = new Map(modelTypeArr)
      })
  },
  methods: {
    getTotalForm () {
       this.$http({
        url: '/api-sou/sale-plan/aggregationSumYearTotal',
        data: this.queryParam,
        method: 'POST'
      }).then((res) => {
        this.form = res.data
      })
    },
    getQuerydata (params) {
      this.queryParam = params
      if (this.queryParam.date && this.queryParam.date.length === 2) {
        this.queryParam.importFromDate = this.queryParam.date[0]
        this.queryParam.importToDate = this.queryParam.date[1]
      } else {
        this.queryParam.importFromDate = ''
        this.queryParam.importToDate = ''
      }
      this.$nextTick(() => {
        this.getTotalForm()
        this.$refs.list.query()
      })
    },
    reloadData () {
      this.getQuerydata(this.queryParam)
    },
    deleteHandle () {
      let ids = this.selectArr.map((item) => item.planId)
      this.$http({
        url: '/api-sou/sale-plan/removeByIds',
        data: ids,
        method: 'POST'
      }).then((res) => {
        if (res.code === 'R000') {
          // '删除成功'
          this.$message.success(this.$t('common.successDelete'))
          this.reloadData()
        }
      })
    },
    exportHandleEstimate () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/exportEstimate',
        // 产销计划计划导出-导出
        `${this.$t('cusEntry.supplement20250211.exportProductionPlan')}${parseTime(new Date())}.xlsx`,
        params
      )
    },
    exportHandleActual () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/exportActual',
        // 产销计划实际-导出
        `${this.$t('cusEntry.supplement20250211.productionSalesPlanActualExport')}${parseTime(new Date())}.xlsx`,
        params
      )
    },
    exportHandle () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/export',
        // 产销计划-导出
        `${this.$t('cusEntry.supplement20250211.productionSalesPlanExport')}${parseTime(new Date())}.xlsx`,
        params
      )
    },
    handleCurrentChange (e) {
      console.log(e)
      this.selectArr = e
    },
    rebuildBomInBatch () {
      if (this.selectArr.length === 0) {
        // '请先勾选需要刷新的MTOC数据'
        this.$message.error(this.$t('cusEntry.supplement20250211.selectMtocDataToRefresh'))
      }
      const params = this.selectArr.map((item) => item.mtoc)
      this.$http({
        url: '/api-sou/bom/rebuildBomInBatch',
        method: 'POST',
        data: params,
        loading: true
      }).then((res) => {
        if (res.code === 'R000') {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    genMonthlyByMtocs () {
      if (this.selectArr.length === 0) {
        // '请先勾选需要生成本年月度成本的MTOC数据'
        this.$message.error(this.$t('cusEntry.supplement20250211.generateMonthlyCostMtocData'))
      }
      // const params = this.selectArr.map(item=> item.mtoc)
      const params = this.selectArr
      this.$http({
        url: '/api-sou/bom-cost/rebuildMonthlyAsync',
        method: 'POST',
        data: params,
        loading: true
      }).then((res) => {
        if (res.code === 'R000') {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },

    handleSuccess () {
      // '导入成功'
      this.$message.success(this.$t('components.eio.importSuccess'))
    },
    downloadItemTemplateActual () {
      downloadFileLink(
        '/api-sou/sale-plan/exportHeadActual',
        // '产销计划实际导入模板.xlsx'
        this.$t('cusEntry.supplement20250211.productionSalesPlanActualImportTemplate2')
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    downloadItemTemplateEstimates () {
      downloadFileLink(
        '/api-sou/sale-plan/exportHeadEstimates',
        // '产销计划计划导入模板.xlsx'
        this.$t('cusEntry.supplement20250211.productionSalesPlanTemplate')
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    thousand (num) {
      console.log('num', num)
      return num.toString().replace(/\d+/, function (n) {
        return n.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
      })
    }
  }
}
</script>

<style scoped>
.totalSpan{
  cursor:default;
  color:#1890ff;
  height:28px;
  line-height: 28px;
  border:1px solid #DCDFE6;
  width:180px;
  display:inline-block;
  padding:0 5px;

}
</style>
