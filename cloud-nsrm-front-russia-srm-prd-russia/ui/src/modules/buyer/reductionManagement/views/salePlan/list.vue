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
          <el-date-picker
            v-model="scope.date"
            type="daterange"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
            range-separator="~"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
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
            导出
          </AuthorityButton>
          <MImport
            style="display: inline-block;  margin-right: 10px;"
            title="导入计划"
            upLoadUrl="/api-sou/sale-plan/importEstimate"
            :extraData="extraData"
            code="base:salePlan:importEstimate"
            @downloadTemplate="downloadItemTemplateEstimates"
            @handleSuccess="handleSuccess"
          />
          <MImport
            style="display: inline-block;"
            title="导入实际"
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
            删除
          </AuthorityButton>

          <AuthorityButton
            type="primary"
            @click="rebuildBomInBatch"
          >
            刷新bom结构
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="genMonthlyByMtocs"
          >
            生成本年月度成本数据
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
                <el-form-item
                  prop="yearPlanTotal"
                  label="全年国内计划合计"
                >
                  <span class="totalSpan">{{ form.yearPlanTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="yearActualTotal"
                  label="全年国内实际合计"
                >
                  <span class="totalSpan">{{ form.yearActualTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="yearPlanSeaTotal"
                  label="全年海外计划合计"
                >
                  <span class="totalSpan">{{ form.yearPlanSeaTotal|toThousandFixed2Fliter }}</span>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="yearActualSeaTotal"
                  label="全年海外实际合计"
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
          label: '年度',
          type: 'year'
        },
        {
          prop: 'planVersion',
          label: '产销计划版本'
        },
        {
          prop: 'modelCode',
          label: '车型编码',
          type: 'quicksearch',
          showKey: 'modelCode',
          name: 'gacm_delmodules_search'
        },
        {
          prop: 'modelName',
          label: '车型名称'
        },
        {
          prop: 'mtos',
          type: 'formattorText',
          title: '请输入MTO',
          label: 'MTO'
        },
        {
          prop: 'mtocs',
          type: 'formattorText',
          title: '请输入MTOC',
          label: 'MTOC'
        },
        {
          prop: 'engineName',
          label: '发动机'
        },
        {
          prop: 'gearboxCategory',
          label: '变速箱'
        },
        {
          prop: 'planningDerivation',
          label: '是否企划派生',
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'orgId',
          label: '工厂',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'modelType',
          label: '车型状态',
          type: 'dict',
          code: 'AUTO_MODEL_STATUS'
        },
        {
          prop: 'budgetType',
          label: '预算场景类型',
          type: 'dict',
          code: 'BUDGET_BNS_TYPE'
        },
        {
          prop: 'effStatus',
          label: '是否生效',
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'planType',
          label: '计划类型',
          type: 'dict',
          code: 'SALE_PLAN_TYPE'
        },
        {
          prop: 'planVerName',
          label: '产销计划版本名称'
        }
      ],
      tableHeader: [
        {
          prop: 'planYear',
          label: '年度',
          minWidth: 160
        },
        {
          prop: 'orgName',
          label: '生产工厂',
          minWidth: 160
        },
        {
          prop: 'modelType',
          label: '车型状态',
          minWidth: 160,
          formattor: (val) =>
            this.modelTypeMap.has(val) ? this.modelTypeMap.get(val) : val
        },
        {
          prop: 'modelCode',
          label: '车型编码',
          minWidth: 160
        },
        {
          prop: 'modelName',
          label: '车型名称',
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
          label: '是否企划派生',
          minWidth: 160,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'configName',
          label: '配置名称',
          minWidth: 160
        },
        {
          prop: 'engineName',
          label: '发动机',
          minWidth: 160
        },
        {
          prop: 'gearboxCategory',
          label: '变速箱类别',
          minWidth: 160
        },
        {
          prop: 'planType',
          label: '计划类型',
          minWidth: 160
        },
        {
          prop: 'planVersion',
          label: '计划版本',
          minWidth: 160
        },
        //
        {
          prop: 'planVerName',
          label: '计划版本名称',
          minWidth: 160
        },
        {
          prop: 'budgetType',
          label: '预算场景类型',
          minWidth: 160,
          dataType: 'dict',
          code: 'BUDGET_BNS_TYPE'
        },
        {
          prop: 'effStatus',
          label: '是否生效',
          minWidth: 160,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'sourceFrom',
          label: '数据来源',
          minWidth: 160,
          dataType: 'dict',
          code: 'BNS_SOURCE_FROM'
        },

        {
          prop: 'yearPlanTotal',
          label: '全年国内计划合计',
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
          label: '全年国内实际合计',
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
          label: '全年海外计划合计',
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
          label: '全年海外实际合计',
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
        label: index + 1 + '月',
        minWidth: 160,
        children: [
          {
            prop: item + 'PlanTotal',
            label: '国内预计',
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
            label: '国内实际',
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
            label: '海外预计',
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
            label: '海外实际',
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
        label: '原MTO',
        minWidth: 100
      },
      {
        prop: 'createdBy',
        label: '创建人',
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        minWidth: 130,
        sortMethod: (a, b) => {
          return (
            new Date(a.creationDate).getTime() -
            new Date(b.creationDate).getTime()
          )
        }
      },
      {
        prop: 'lastUpdatedBy',
        label: '更新人',
        minWidth: 100
      },
      {
        prop: 'lastUpdateDate',
        label: '更新时间',
        minWidth: 130,
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
          this.$message.success('删除成功')
          this.reloadData()
        }
      })
    },
    exportHandleEstimate () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/exportEstimate',
        `产销计划计划导出-导出${parseTime(new Date())}.xlsx`,
        params
      )
    },
    exportHandleActual () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/exportActual',
        `产销计划实际-导出${parseTime(new Date())}.xlsx`,
        params
      )
    },
    exportHandle () {
      const params = this.$refs.formWrapper.formData
      // detailExport(),
      downloadFileLinkByPost(
        '/api-sou/sale-plan/export',
        `产销计划-导出${parseTime(new Date())}.xlsx`,
        params
      )
    },
    handleCurrentChange (e) {
      console.log(e)
      this.selectArr = e
    },
    rebuildBomInBatch () {
      if (this.selectArr.length === 0) {
        this.$message.error('请先勾选需要刷新的MTOC数据')
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
        this.$message.error('请先勾选需要生成本年月度成本的MTOC数据')
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
      this.$message.success('导入成功')
    },
    downloadItemTemplateActual () {
      downloadFileLink(
        '/api-sou/sale-plan/exportHeadActual',
        '产销计划实际导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },

    downloadItemTemplateEstimates () {
      downloadFileLink(
        '/api-sou/sale-plan/exportHeadEstimates',
        '产销计划计划导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
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
