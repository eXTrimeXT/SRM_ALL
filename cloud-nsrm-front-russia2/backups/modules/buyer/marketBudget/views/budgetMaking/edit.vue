<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('common.baseInfo')" name="1">
          <el-form
            ref="form"
            :rules="rules"
            :model="form"
            :disabled="disabledFlag"
          >
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item
                  prop="versionName"
                  :label="$t('单据名称')"
                >
                  <el-input
                    v-model="computedVersionName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="versionYear"
                  :label="$t('年度')"
                >
                  <el-date-picker
                    v-model="form.versionYear"
                    type="year"
                    value-format="yyyy"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="marketType"
                  :label="$t('市况类型')"
                >
                  <el-select v-model="form.marketType" placeholder="请选择">
                    <el-option
                      v-for="item in marketTypeList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="isSupplement"
                  :label="$t('是否增补')"
                >
                  <DictSelect
                    v-model="form.isSupplement"
                    clearable
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="planVersion"
                  :label="$t('产销计划版本')"
                >
                  <el-input
                    v-model="form.planVersion"
                  >
                    <el-button
                      slot="append"
                      icon="el-icon-search"
                      @click="openVersionList('open')"
                    />
                  </el-input>
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="developVersion"
                  :label="$t('编制版本')"
                >
                  <DictSelect
                    v-model="form.developVersion"
                    clearable
                    code="MARKET_BUDGET_DEVELOP_VERSION"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="status"
                  :label="$t('状态')"
                >
                  <DictSelect
                    v-model="form.status"
                    code="MARKET_BUDGET_VERSION_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="quoteStatus"
                  :label="$t('试算状态')"
                >
                  <DictSelect
                    v-model="form.quoteStatus"
                    code="BUDGET_VERSION_QUOTE_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="currencyCode"
                  :label="$t('币种')"
                >
                  <DictSelect
                    v-model="form.currencyCode"
                    clearable
                    code="currency"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="budgetType"
                  :label="$t('产销场景')"
                >
                  <DictSelect
                    v-model="form.budgetType"
                    code="BID_MARKET_PRODUCTION_SCENE"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="remark"
                  :label="$t('备注')"
                >
                  <el-input
                    v-model="form.remark"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item :title="$t('市况预算')" name="2">
          <el-tabs
            v-if="showTable"
            v-model="editableTabsValue"
            type="border-card"
            style="padding-bottom: 44px"
            @tab-click="handleClick"
          >
            <el-tab-pane
              :label="$t('平均单台市况预算')"
              name="1"
            >
              <el-button type="primary" style="margin-bottom:8px;" :loading="exportLoading" @click="exportHandle(1)">
                导出
              </el-button>
              <TableView
                :ref="'gridId1'"
                url="/api-cost/budget/budgetVersion/listDetail"
                :preQueryData="queryParam1"
                :table-header="tableHeader1"
                :comActive="$attrs['changeTab']"
              />
            </el-tab-pane>
            <el-tab-pane
              :label="$t('单台市况预算')"
              name="2"
            >
              <el-button type="primary" style="margin-bottom:8px;" :loading="exportLoading" @click="exportHandle(2)">
                导出
              </el-button>
              <TableView
                :ref="'gridId2'"
                url="/api-cost/budget/budgetVersion/listDetail"
                :preQueryData="queryParam2"
                :table-header="tableHeader2"
                :comActive="$attrs['changeTab']"
              />
            </el-tab-pane>
            <el-tab-pane
              :label="$t('零件单台市况预算')"
              name="3"
            >
              <el-button type="primary" style="margin-bottom:8px;" :loading="exportLoading" @click="exportHandle(3)">
                导出
              </el-button>
              <TableView
                :ref="'gridId3'"
                url="/api-cost/budget/budgetVersion/listDetail"
                :preQueryData="queryParam3"
                :table-header="tableHeader3"
                :comActive="$attrs['changeTab']"
              />
            </el-tab-pane>
            <el-tab-pane
              :label="$t('市况总额')"
              name="4"
            >
              <el-button type="primary" style="margin-bottom:8px;" :loading="exportLoading" @click="exportHandle(4)">
                导出
              </el-button>
              <TableView
                :ref="'gridId4'"
                url="/api-cost/budget/budgetVersion/listDetail"
                :preQueryData="queryParam4"
                :table-header="tableHeader4"
                :comActive="$attrs['changeTab']"
              />
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>
      </el-collapse>
      <!-- 产销计划版本模态框 -->
      <srm-dialog
        size="largePlus"
        title="产销计划版本"
        :visible.sync="visible"
        @closed="closedVisible"
      >
        <div style="padding:10px">
          <FormWrapper
            :form-array="yearForm"
            form-label-width="120px"
            @getFormData="openVersionList"
          />
          <el-table
            ref="multipleTable"
            :data="tableData"
            border
            style="width: 100%;margin-top:10px"
            class="checkTable"
          >
            <el-table-column
              type="selection"
              width="55"
            >
              <template slot-scope="scope">
                <el-radio
                  v-model="radio"
                  :label="scope.$index"
                  style="color: #fff;padding-left: 10px; margin-right: -25px;"
                  class="radioChange"
                  @change.native="getCurrentRow(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="index"
              label="序号"
              width="60"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.$index+1 }}
              </template>
            </el-table-column>
            <el-table-column
              prop="planYear"
              label="年度"
              minWidth="130"
              align="center"
            />
            <el-table-column
              prop="planVersion"
              label="版本"
              minWidth="130"
              align="center"
            />
            <el-table-column
              prop="planVerName"
              label="版本名称"
              minWidth="130"
              align="center"
            />
            <el-table-column
              prop="effStatus"
              label="是否生效"
              minWidth="130"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.effStatus=='Y'?'是':scope.row.effStatus=='N'?'否':'' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="budgetType"
              label="场景"
              minWidth="130"
              align="center"
            >
              <template slot-scope="scope">
                {{ $getDictLabel("BUDGET_BNS_TYPE", scope.row.budgetType) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <template #footer>
          <el-button
            type="primary"
            @click="comfigVersion"
          >
            确认
          </el-button>
        </template>
      </srm-dialog>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--试算-->
        <el-button v-if="editFlag" type="primary" @click="countVersion()">
          {{ $t('marketBudget.contResult') }}
        </el-button>

        <!--保存-->
        <el-button v-if="editFlag" type="primary" @click="saveBill()">
          {{ $t('common.save') }}
        </el-button>

        <!--返回-->
        <el-button type="ghost" @click="back">
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
import OrganizationSelector from 'lib@/components/organization-selector'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'MeetModelDetail',

  components: {
    TableView,
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    FormWrapper,
    CPagination
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      showTable: false,
      versionId: null,
      salePlanData: [], // 销售总额
      salePlanDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      marketTypeList: [],
      lazyInit: true,
      radio: '',
      checkRow: {},
      checkStatus: false,
      editableTabsValue: '1',
      visible: false,
      tableData: [],
      activeDims: ['1', '2', '3'],
      yearForm: [
        {
          prop: 'year',
          label: () => '年度',
          type: 'year'
        },
        {
          prop: 'planVerName',
          label: () => '产销计划名称'
        },
        {
          prop: 'effStatus',
          label: () => '状态',
          type: 'select',
          options: [
            { value: 'Y', label: '是' },
            { value: 'N', label: '否' }
          ]
        }
      ],
      form: {
        versionName: null,
        versionYear: null,
        marketType: null,
        planVersion: null, // 产销计划版本
        developVersion: null, // 编制版本
        isSupplement: null,
        remark: null,
        budgetType: null,
        status: 'DRAFT',
        currencyCode: 'CNY'

      },
      rules: {
        versionName: [{ required: true, message: this.$t('common.pleaseInput') }],
        versionYear: [{ required: true, message: this.$t('common.pleaseSelect') }],
        marketType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        isSupplement: [{ required: true, message: this.$t('common.pleaseSelect') }],
        planVersion: [{ required: true, message: this.$t('common.pleaseSelect') }],
        developVersion: [{ required: true, message: this.$t('common.pleaseSelect') }]

      },
      tableHeader1: [],
      tableHeader2: [],
      tableHeader3: [],
      tableHeader4: [],
      storeMap: null

    }
  },

  computed: {
    computedVersionName () {
      let name = ''
      let marketTypeIndex = this.marketTypeList.findIndex(item => item.value == this.form.marketType)
      let marketTypeName = ''
      if (marketTypeIndex > -1 && this.marketTypeList.length) {
        marketTypeName = this.marketTypeList[marketTypeIndex].label
      }
      let yearName = this.form.versionYear || ''
      let budgetTypeName = ''
      if (this.storeMap) {
        budgetTypeName = this.storeMap['BID_MARKET_PRODUCTION_SCENE'][this.form.budgetType]
      }
      if (marketTypeName) name += marketTypeName
      if (yearName) name += '-' + yearName
      if (budgetTypeName) name += '-' + budgetTypeName
      return name
    },
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    editFlag () {
      return ['add', 'edit'].includes(this.urlParams.flag)
    },
    queryParam1 () {
      return {
        versionId: this.versionId,
        tabIndex: 1
      }
    },
    queryParam2 () {
      return {
        versionId: this.versionId,
        tabIndex: 2
      }
    },
    queryParam3 () {
      return {
        versionId: this.versionId,
        tabIndex: 3
      }
    },
    queryParam4 () {
      return {
        versionId: this.versionId,
        tabIndex: 4
      }
    }
  },

  async created () {
    this.$store
      .dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL, { dictCode: 'COST_LINK_MARKET_TYPE' })
      .then((data) => {
        let result = data || []
        let temp = [...result]
        temp.unshift({ 'label': '全选', 'value': 'ALL' })
        this.marketTypeList = temp
      })
    this.storeMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, { dictCodeList: ['BID_MARKET_PRODUCTION_SCENE'] })

    let { row, flag } = this.urlParams
    if (row && row.versionId) {
      this.versionId = row.versionId
      this.getFormDetail(this.versionId)
    }
    // 动态表格
    this.setTableConfig()
  },

  methods: {
    toDecimal2 (x) {
      let f = parseFloat(x)
      if (isNaN(f)) {
        return false
      }
      f = Math.round(x * 100) / 100
      let s = f.toString()
      let rs = s.indexOf('.')
      if (rs < 0) {
        rs = s.length
        s += '.'
      }
      while (s.length <= rs + 2) {
        s += '0'
      }
      s = this.formatAmount(s)
      return s
    },
    formatAmount (num) {
      // 改成千分位
      if (num) {
        num = num.toString().replace(/\$|\,/g, '')
        if (num == '' || isNaN(num)) { return 'Not a Number ! ' }
        var sign = num.indexOf('-') > 0 ? '-' : ''
        var cents = num.indexOf('.') > 0 ? num.substr(num.indexOf('.')) : ''
        cents = cents.length > 1 ? cents : ''// 注意：这里如果是使用change方法不断的调用，小数是输入不了的
        num = num.indexOf('.') > 0 ? num.substring(0, (num.indexOf('.'))) : num
        if (cents == '') { if (num.length > 1 && num.substr(0, 1) == '0') { return 'Not a Number ! ' } } else { if (num.length > 1 && num.substr(0, 1) == '0') { return 'Not a Number ! ' } }
        for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++) {
          num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3))
        }
        return (sign + num + cents)
      }
    },
    setTableConfig () {
      this.$http({
        url: '/api-bid/buyer/quote-temp/preview/374728291211264',
        method: 'GET',
        loading: true
      }).then((res) => {
        const { attrMap } = res.data
        for (let k in attrMap) {
          if (attrMap[k].attr.attrName == '单台市况平均预算') {
            this.tableHeader1 = this.transferAttr(attrMap[k].fieldList)
          } else if (attrMap[k].attr.attrName == '单台市况预算') {
            this.tableHeader2 = this.transferAttr(attrMap[k].fieldList)
          } else if (attrMap[k].attr.attrName == '零件单台市况预算') {
            this.tableHeader3 = this.transferAttr(attrMap[k].fieldList)
          } else if (attrMap[k].attr.attrName == '销量总额') {
            this.tableHeader4 = this.transferAttr(attrMap[k].fieldList)
          }
        }
        this.showTable = true
        this.handleClick({ name: '1' })
      })
    },
    transferAttr (list) {
      let headerList = []
      if (list.length) {
        list.forEach(item => {
          // displayed=N的时候是显示，历史遗留问题
          if (item.displayed == 'N') {
            let header = {
              prop: String(item.fieldId),
              label: item.fieldName,
              width: 120
            }
            if (item.fieldName == '市况类型') header.formattor = (val) => this.$getDictLabel('COST_LINK_MARKET_TYPE', val)
            if (item.fieldName == '产销场景') header.formattor = (val) => this.$getDictLabel('BID_MARKET_PRODUCTION_SCENE', val)
            if (item.fieldType == 'FORMULA') {
              header.formattor = (val) => this.toDecimal2(val)
              header.align = 'center'
            }
            headerList.push(header)
          }
        })
      }
      return headerList
    },
    querySalePlanData () {
      this.$http({
        url: '/api-cost/budget/budgetVersion/listSales',
        method: 'POST',
        data: { versionId: this.versionId },
        loading: true
      }).then((res) => {
        const { list, pageNum = 0, pageSize = 0, total } = res.data
          this.salePlanData = list
          this.salePlanDataPage = { pageNum, pageSize, total }
      })
    },
    changeCurrentSize (currentSize) {
      this.salePlanDataPage.pageSize = currentSize
      this.$nextTick(() => this.querySalePlanData())
    },
    handleCurrentChange (val) {
      this.salePlanDataPage.pageNum = val
      this.$nextTick(() => this.querySalePlanData())
    },
    countVersion () {
      if (!this.versionId) return this.$message.warning('请先保存')
      let params = {
        'versionName': this.computedVersionName,
        'versionYear': this.form.versionYear,
        'marketType': this.form.marketType,
        'planVersion': this.form.planVersion,
        'developVersion': this.form.developVersion,
        'isSupplement': this.form.isSupplement,
        'currencyCode': this.form.currencyCode,
        'remark': this.form.remark,
        'budgetType': this.form.budgetType,
        'versionId': this.versionId
      }
      this.$http({
        url: '/api-cost/budget/budgetVersion/countVersion',
        method: 'POST',
        data: params,
        loading: false
      }).then((res) => {
        // this.editableTabsValue = '1'
        // this.$nextTick(() => {
        //   this.$refs[`gridId1`].query()
        // })
      })
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('BudgetMakingList.getQueryData')
    },
    handleClick (tab) {
      if (this.versionId) {
        this.$nextTick(() => {
          this.$refs[`gridId${tab.name}`].query()
        })
      }
    },
    async exportHandle (tabIndex) {
      if (this.exportLock) {
          return false
      }
      this.exportLock = true
      let params = {
        tabIndex: tabIndex,
        versionId: this.versionId
      }
      this.exportLoading = true
      downloadFileLinkByPost('/api-cost/budget/budgetVersion/exportDetail', `市况预算编制-导出${parseTime(new Date())}.xlsx`, params).then(res => {
          console.log('完成')
          this.exportLock = false
          this.exportLoading = false
      }).catch(err => {
          console.log('失败', err)
          this.exportLock = false
          this.exportLoading = false
      })
    },
    comfigVersion () {
      if (this.checkStatus) {
        this.form.budgetType = this.checkRow.budgetType
        this.form.planVersion = this.checkRow.planVersion
        this.visible = false
        this.checkStatus = false
      } else {
        this.$message.warning('请选择一行数据')
      }
    },
    getCurrentRow (row) {
      this.checkRow = { ...row }
      this.checkStatus = true
    },
    openVersionList (v) {
      let params = v == 'open' ? {} : v
      this.$http({
        url: '/api-base/sale-plan/queryListInExRateBudget',
        method: 'POST',
        data: params,
        loading: true
      }).then((res) => {
        if (res.code == 'R000') {
          this.visible = true
          this.tableData = res.data
        }
      })
    },
    saveBill (type) {
      this.form.versionName = this.computedVersionName
      let params = this.initParams()
      let url = '/api-cost/budget/budgetVersion/saveOrUpdate'
      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))

      if (!validFlag) {
        return this.__focus_error__()
      }
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (res.data) {
          this.versionId = res.data
          this.form.versionId = this.versionId
          this.getFormDetail(res.data)
        } else if (this.versionId) {
          this.getFormDetail(this.versionId)
        }
      })
    },

    getFormDetail (id) {
      this.$http({
        url: '/api-cost/budget/budgetVersion/get',
        method: 'GET',
        params: { id },
        loading: true
      }).then(res => {
        this.form = res.data || {}
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
<style scoped>
.radioChange >>> .el-radio__label {
  display: none !important;
}
.checkTable >>> .el-checkbox {
  display: none !important;
}
</style>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.wrapper {
  padding-bottom: 40px;
}

</style>
