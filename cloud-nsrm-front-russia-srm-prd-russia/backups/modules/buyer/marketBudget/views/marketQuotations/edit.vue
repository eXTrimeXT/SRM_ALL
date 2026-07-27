<template>
  <el-container
    class="flex-container wrapper"
    direction="vertical"
  >
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item
          :title="$t('meeting.baseInfo')"
          name="1"
        >
          <el-form
            ref="form"
            :rules="rules"
            :model="form"
            :disabled="disabledFlag"
          >
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item
                  prop="budgetYear"
                  :label="$t('marketBudget.budgetYear')"
                >
                  <el-date-picker
                    v-model="form.budgetYear"
                    type="year"
                    value-format="yyyy"
                    @change="yearChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="marketType"
                  :label="$t('marketBudget.marketType')"
                >
                  <DictSelect
                    v-model="form.marketType"
                    clearable
                    code="COST_LINK_MARKET_TYPE"
                    @change="marketTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="priceType"
                  :label="$t('marketBudget.priceType')"
                >
                  <DictSelect
                    v-model="form.priceType"
                    class="blue"
                    clearable
                    code="BID_MARKET_BUDGET_PRICE_TYPE"
                    @change="priceTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="countType"
                  :label="$t('marketBudget.countType')"
                >
                  <DictSelect
                    v-model="form.countType"
                    clearable
                    code="BID_MARKET_COUNT_TYPE"
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  :label="$t('marketBudget.marketStartTime')"
                  prop="marketStartTime"
                >
                  <el-date-picker
                    v-model="form.marketStartTime"
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  :label="$t('marketBudget.marketEndTime')"
                  prop="marketEndTime"
                >
                  <el-date-picker
                    v-model="form.marketEndTime"
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="scene"
                  :label="$t('marketBudget.scene')"
                >
                  <DictSelect
                    v-model="form.scene"
                    clearable
                    code="BUDGET_BNS_TYPE"
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="linkFrequency"
                  :label="$t('marketBudget.linkFrequency')"
                >
                  <DictSelect
                    v-model="form.linkFrequency"
                    clearable
                    code="BID_LINK_FREQUENCY"
                    disabled
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="status"
                  :label="$t('marketBudget.status')"
                >
                  <DictSelect
                    v-model="form.status"
                    clearable
                    code="BID_MARKET_BUDGET_STATUS"
                    disabled
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('预算行情试算结果')"
          name="2"
        >
          <el-table
            :data="budgetMarketResults"
            border
            style="width: 100%"
          >
            <el-table-column
              prop="materialName"
              :label="$t('marketBudget.materialName')"
              width="180"
            />
            <el-table-column
              prop="systemCountPrice"
              :label="$t('marketBudget.systemCountPrice')"
              width="180"
            />
            <el-table-column
              prop="confirmPrice"
              :label="$t('marketBudget.confirmPrice')"
            >
              <template v-slot="{ row }">
                <el-input v-model="row.confirmPrice" v-input-format="{ type: 'float2' }" class="blue" :disabled="disabledFlag" />
              </template>
            </el-table-column>
            <el-table-column
              prop="unitName"
              :label="$t('marketBudget.unit')"
              :formatter="formattorResults"
            />
            <el-table-column
              prop="currencyCode"
              :label="$t('marketBudget.currency')"
            />
            <el-table-column
              prop="remark"
              :label="$t('marketBudget.remark')"
            >
              <template v-slot="{ row }">
                <el-input v-model="row.remark" :disabled="disabledFlag" />
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('marketBudget.materialQuotationsDetail')"
          name="3"
        >
          <div style="padding:0 0 12px 0">
            <el-button
              type="primary"
              :loading="exportLoading"
              @click="exportHandle"
            >
              导出
            </el-button>
          </div>
          <TableView
            ref="gridId"
            :table-header="materialTableHeader"
            :tableInfor="budgetMarketDetails"
            :comActive="$attrs['changeTab']"
            :pageEnabled="false"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--保存-->
        <el-button
          v-if="!disabledFlag"
          type="primary"
          @click="saveBill('STAGING')"
        >
          {{ $t('common.save') }}
        </el-button>

        <!-- 试算 -->
        <el-button
          v-if="!disabledFlag"
          type="primary"
          @click="countResult"
        >
          {{ $t('marketBudget.contResult') }}
        </el-button>

        <!--返回-->
        <el-button
          type="ghost"
          @click="back"
        >
          {{ $t('common.backTo') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import TableView from 'lib@/components/Table/TableView'
import OrganizationSelector from 'lib@/components/organization-selector'
import ExportExcel from 'lib@/components/export-excel'
import DictSelect from 'lib@/components/c-select/dict-select'
import { parseTime } from '@/utils'
import { downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'MeetModelDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    ExportExcel,
    TableView,
    DictSelect
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3'],
      form: {
        budgetYear: null,
        marketType: null,
        priceType: null,
        countType: null,
        marketStartTime: null,
        marketEndTime: null,
        scene: null,
        linkFrequency: '',
        status: 'DRAFT'

      },
      rules: {
        budgetYear: [{ required: true, message: this.$t('common.pleaseInput') }],
        marketType: [{ required: true, message: this.$t('common.pleaseInput') }],
        priceType: [{ required: true, message: this.$t('common.pleaseInput') }],
        countType: [{ required: true, message: this.$t('common.pleaseInput') }],
        marketStartTime: [{ required: true, message: this.$t('common.pleaseInput') }],
        marketEndTime: [{ required: true, message: this.$t('common.pleaseInput') }],
        scene: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      budgetMarketResults: [],
      budgetMarketDetails: [],
      materialTableHeader: [
        {
          prop: 'startTime',
          label: this.$t('marketBudget.marketStartTime'),
          minWidth: 130,
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'endTime',
          label: this.$t('marketBudget.marketEndTime'),
          minWidth: 130,
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'materialCode',
          label: this.$t('marketBudget.materialCode'),
          minWidth: 130
        },
        {
          prop: 'materialName',
          label: this.$t('marketBudget.materialName'),
          minWidth: 130
        },
        {
          prop: 'spec',
          label: this.$t('marketBudget.spec'),
          minWidth: 130
        },
        {
          prop: 'unitPrice',
          label: this.$t('marketBudget.unitPrice'),
          minWidth: 130
        },
        {
          prop: 'currencyCode',
          label: this.$t('marketBudget.currency'),
          minWidth: 130
        },
        {
          prop: 'exchangeRate',
          label: this.$t('marketBudget.exchangeRate'),
          minWidth: 130
        },
        {
          prop: 'unitName',
          label: this.$t('marketBudget.unit'),
          minWidth: 130
        },
        {
          prop: 'priceType',
          label: this.$t('marketBudget.quotationType'),
          minWidth: 130,
          formattor: (val) => {
            if (val === 'DAY') {
                return '日价格'
            } else if (val === 'MONTH') {
                return '月价格'
            } else if (val === 'YEAR') {
                return '年度价格'
            } else if (val === 'TRY_CALCULATE') {
                return '财务试算'
            } else {
                return val
            }
          }
        },
        {
          prop: 'quotationStatus',
          label: this.$t('marketBudget.quotationStatus'),
          minWidth: 130,
          formattor: (val) => {
            if (val === 'DARFT') {
                return '拟定'
            } else if (val === 'INVALID') {
                return '失效'
            } else if (val === 'VALID') {
                return '生效'
            } else {
                return val
            }
          }
        }
      ]
    }
  },

  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    editFlag () {
      return ['add', 'edit'].includes(this.urlParams.flag)
    }
  },

  created () {
    let { row, flag } = this.urlParams
    if (row && row.budgetId) {
      this.getFormDetail(row.budgetId)
    }
  },

  methods: {
    formattorResults (row) {
      return this.$getDictLabel('unit', row.unitCode)
    },
    exportHandle () {
      if (this.exportLock) {
          return false
      }

      this.exportLock = true
      let params = {
        budgetYear: this.form.budgetYear,
        marketType: this.form.marketType,
        priceType: this.form.priceType,
        countType: this.form.countType,
        marketStartTime: this.form.marketStartTime,
        marketEndTime: this.form.marketEndTime,
        scene: this.form.scene,
        linkFrequency: this.form.linkFrequency
      }
      this.exportLoading = true
      downloadFileLinkByPost('/api-cost/budget/budgetMarket/exportMarketDetail', `原材料行情明细${parseTime(new Date())}.xlsx`, params).then(res => {
          console.log('完成')
          this.exportLock = false
          this.exportLoading = false
      }).catch(err => {
          console.log('失败', err)
          this.exportLock = false
          this.exportLoading = false
      })
    },
    priceTypeChange () {
      this.getQuotationsTime()
    },
    yearChange () {
      this.getQuotationsTime()
    },
    marketTypeChange () {
      // 获取联动频次
      this.$http({
        url: '/api-cost/marketType/material/getLinkFrequency',
        method: 'GET',
        params: { marketType: this.form.marketType },
        loading: true
      }).then(res => {
        let result = res.data || ''
        this.form.linkFrequency = result
        this.getQuotationsTime()
      })
    },
    countResult () {
      let params = {
        budgetYear: this.form.budgetYear,
        marketType: this.form.marketType,
        priceType: this.form.priceType,
        countType: this.form.countType,
        marketStartTime: this.form.marketStartTime,
        marketEndTime: this.form.marketEndTime,
        scene: this.form.scene,
        linkFrequency: this.form.linkFrequency
      }
      // 获取试算结果
      this.$http({
        url: '/api-cost/budget/budgetMarket/countResult',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        let result = res.data || {}
        this.budgetMarketResults = result.budgetMarketResults
        this.budgetMarketDetails = result.budgetMarketDetails
      })
    },
    topicTypeChange () {

    },
    // 获取默认行情开始时间和结束时间
    getQuotationsTime () {
      // frequency: YEAR 年度 ; HALF_YEAR 半年度; QUARTER 季度; MONTH;月度
      // priceType : STANDARD 基准 ; FORECAST 预估;
      let year = this.form.budgetYear
      let frequency = this.form.linkFrequency
      let priceType = this.form.priceType
      if (!(year && frequency && priceType)) return
      let result = []
      if (priceType === 'STANDARD') {
        switch (frequency) {
          case 'YEAR':
            if (this.form.marketType == 'GB') {
              result = [`${year - 1}-01-01`, `${year - 1}-01-31`]
            } else {
              result = [`${year - 2}-01-01`, `${year - 2}-12-31`]
            }
            break
          case 'YEAR_STEEL':
            result = [`${year - 1}-01-01`, `${year - 1}-01-31`]
            break
          case 'HALF_YEAR':
            result = [`${year - 1}-01-01`, `${year - 1}-06-30`]
            break
          case 'QUARTER':
            result = [`${year - 1}-07-01`, `${year - 1}-09-30`]
            break
          case 'MONTH':
            result = [`${year - 1}-09-01`, `${year - 1}-09-30`]
            break
        }
      } else if (priceType === 'FORECAST') {
        switch (frequency) {
          case 'YEAR':
            if (this.form.marketType == 'GB') {
              result = [`${year}-01-01`, `${year}-01-31`]
            } else {
              result = [`${year - 1}-01-01`, `${year - 1}-12-31`]
            }

            break
          case 'YEAR_STEEL':
            result = [`${year}-01-01`, `${year}-01-31`]
            break
          case 'HALF_YEAR':
            result = [`${year - 1}-07-01`, `${year - 1}-12-31`]
            break
          case 'QUARTER':
            result = [`${year - 1}-10-01`, `${year - 1}-12-31`]
            break
          case 'MONTH':
            result = [`${year - 1}-12-01`, `${year - 1}-12-31`]
            break
        }
      }
      if (result.length >= 2) {
        this.form.marketStartTime = result[0]
        this.form.marketEndTime = result[1]
      }
      return result
    },
    saveBill () {
      let params = this.initParams()
      let allParams = {
        budgetMarketHead: params,
        budgetMarketDetails: this.budgetMarketDetails,
        budgetMarketResults: this.budgetMarketResults
      }
      let url = '/api-cost/budget/budgetMarket/addOrUpdate'

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))

      if (!validFlag) {
        return this.__focus_error__()
      }
      this.$http({
        url,
        method: 'POST',
        data: allParams,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (!res.data) return this.back()
        this.getFormDetail(res.data)
      })
    },

    getFormDetail (budgetId) {
      this.$http({
        url: '/api-cost/budget/budgetMarket/get',
        method: 'GET',
        params: { id: budgetId },
        loading: true
      }).then(res => {
        let result = res.data || {}
        this.form = result
        this.budgetMarketDetails = result.budgetMarketDetails || []
        this.budgetMarketResults = result.budgetMarketResults || []
      })
    },

    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('MeetTodoList.getQueryData')
    },

    initParams () {
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      return params
    }
  }
}
</script>

<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.wrapper {
  padding-bottom: 40px;
  ::v-deep .blue  .el-input__inner{
    color: #409eff;
  }
}

</style>
