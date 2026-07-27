<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form ref="form" :rules="rules" :model="form" :disabled="disabledFlag">
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item prop="formulaName" :label="$t('marketBudget.formulaName')">
                  <el-input v-model="form.formulaName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="marketType" :label="$t('marketBudget.marketType')">
                  <DictSelect
                    v-model="form.marketType"
                    clearable
                    code="COST_LINK_MARKET_TYPE"
                    @change-value="marketTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="frequency" :label="$t('marketBudget.linkFrequency')">
                  <DictSelect v-model="form.frequency" code="COST_LINK_FREQUENCY" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="applicationScen" :label="$t('marketBudget.usageScene')">
                  <DictSelect
                    v-model="form.applicationScen"
                    clearable
                    code="COST_LINK_SCENE_TYPE"
                    @change-value="usageSceneChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="1">
                <el-form-item prop="formulaDetailed" :label="$t('公式明细')">
                  <el-input v-model="formulaDetail" type="textarea" :rows="3" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item :title="$t('公式定义')" name="2">
          <el-button v-if="editFlag" type="primary" style="margin-bottom: 10px" @click="addItem">
            {{ $t("common.add") }}
          </el-button>
          <div style="height:500px;">
            <BaseTable
              ref="table"
              :columns="columns"
              :data-source="dataSource"
              :initialize="false"
              height="500"
              row-key="formulaLineId"
              border
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <template #formulaItem="{ scope }">
                <DictSelect
                  v-model="scope.row.formulaItem"
                  clearable
                  code="SCC_COST_MARKET_LINK_FORMULA_ITEM"
                  @change-value="changeType"
                />
              </template>
              <template #elementCode="{ scope }">
                <el-select
                  v-show="scope.row.formulaItem == 'ELEMENT' "
                  v-model="scope.row.elementCode"
                  placeholder="请选择"
                  @change="changeElementCode(scope.row)"
                >
                  <el-option
                    v-for="item in elementCodeOptions"
                    :key="item.elementId"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
              <template #priceMode="{ scope }">
                <DictSelect
                  v-show="scope.row.formulaItem == 'ELEMENT' && scope.row.elementCode =='PRICE' && scope.row.marketType "
                  v-model="scope.row.priceMode"
                  code="COST_LINK_PRICE_MODE"
                />
              </template>
              <template #elementValue="{ scope }">
                <el-select
                  v-if="scope.row.formulaItem == 'ELEMENT' "
                  v-model="scope.row.value"
                  placeholder="请选择"
                  @change="changeMaterial(scope.row)"
                >
                  <el-option
                    v-for="item in materialOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.label"
                  />
                </el-select>
                <el-select
                  v-if="scope.row.formulaItem == 'OPERATOR' "
                  v-model="scope.row.value"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="item in operatorOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <el-input
                  v-if="scope.row.formulaItem == 'NUMBER'"
                  v-model="scope.row.value"
                  v-input-format="{ type: 'float'}"
                  placeholder="请输入"
                />
              </template>
            </BaseTable>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!-- 详情页关闭，编辑页取消-->
        <el-button type="ghost" @click="back">
          {{ disabledFlag ? $t('common.close') : $t('common.cancel') }}
        </el-button>
        <!--保存-->
        <el-button v-if="editFlag" type="primary" @click="saveBill()">
          {{ $t('common.save') }}
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
import BaseTable from 'lib@/components/BaseTable/baseTable'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { deepClone } from '@/utils'
import { transformMQL } from '@/library/utils/util'
export default {
  name: 'MarketFormulaDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    BaseTable
    // RenderSelect
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      // store: createStore({ BID_MARKET_BUDGET_PRICE_TYPE: []}),
      dataSource: [],
      realDataSource: [],
      activeDims: ['1', '2', '3'],
      form: {
        formulaName: null,
        marketType: null,
        frequency: null,
        applicationScen: null,
        status: 'DRAFT'
      },
      rules: {
        marketType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        applicationScen: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      options: [
        {
          label: '要素',
          value: 'ELEMENT'
        },
        {
          label: '运算符',
          value: 'OPERATOR'
        },
        {
          label: '数字',
          value: 'NUMBER'
        }
      ],
      elementCodeOptions: [],
      materialOptions: [],
      operatorOptions: [
        {
          label: '+',
          value: '+'
        },
        {
          label: '-',
          value: '-'
        },
        {
          label: '*',
          value: '*'
        },
        {
          label: '/',
          value: '/'
        },
        {
          label: '(',
          value: '('
        },
        {
          label: ')',
          value: ')'
        }
      ],
      storeMap: null,
      columns: [],
      deleteLineCache: []
    }
  },

  computed: {
    formulaDetail () {
      let result = ''
      this.realDataSource.forEach(item => {
        if (item.value) {
          if (item.formulaItem == 'ELEMENT') {
            let matchElementCode = this.elementCodeOptions.find(ele => {
              return ele.value == item.elementCode
            })
            matchElementCode = matchElementCode || {}
            if (matchElementCode && matchElementCode.marketType && item.priceMode) {
              let priceModeName = this.storeMap['COST_LINK_PRICE_MODE'][item.priceMode]

              let str = `${matchElementCode.label}_${priceModeName}_${item.value}`
              result += str
            } else {
              let str = `${item.value}`
              result += str
            }
          } else if (item.formulaItem == 'OPERATOR' || item.formulaItem == 'NUMBER') {
            result += item.value
          }
        }
      })
      return result
    },
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

  async created () {
    this.storeMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
      dictCodeList: ['COST_LINK_MARKET_TYPE', 'COST_LINK_SCENE_TYPE', 'COST_LINK_PRICE_MODE']
    })
    // this.store.commit("loadDictionary", ["BID_MARKET_BUDGET_PRICE_TYPE"]);
    this.lazyInit = false
    let { row, flag } = this.urlParams
    if (row && row.formulaId) {
      this.getFormDetail(row, true)
    }

    this.columns = [
      {
        attrs: {
          prop: 'formulaItem',
          label: '类型',
          formatter: (cellValue, row) => this.$getDictLabel('SCC_COST_MARKET_LINK_FORMULA_ITEM', cellValue)
        },
        slot: 'formulaItem',
        rules: { required: true, message: '必填' }
      },
      {
        attrs: {
          prop: 'elementCode',
          label: '要素',
          formatter: (cellValue, row) =>
            this.$getDictLabelByValue(this.elementCodeOptions, cellValue)
        },
        slot: 'elementCode',
        rules: { required: true, message: '必填' }
      },
      {
        attrs: {
          prop: 'priceMode',
          label: '价格方式',
          formatter: (cellValue, row) => this.$getDictLabel('COST_LINK_PRICE_MODE', cellValue)
        },
        slot: 'priceMode',
        rules: { required: true, message: '必填' }
      },
      {
        attrs: {
          prop: 'value',
          label: '值(原材料名称)'
        },
        slot: 'elementValue',
        rules: { required: true, message: '必填' }
      },
      {
        attrs: {
          prop: 'rawMaterialCode',
          label: '原材料编码'
        }
      },
      {
        attrs: {
          prop: 'operation',
          label: '操作',
          width: 150,
          fixed: 'right'
        },
        operations: [
          {
            event: 'deleteItem',
            name: this.$t('common.delete'),
            func: this.deleteLine,
            show: () => this.editFlag
          },
          {
            event: 'insertItem',
            name: this.$t('插入一行'),
            func: this.insertLine,
            show: () => this.editFlag
          }
        ]
      }
    ]
  },

  methods: {
    changeMaterial (row) {
      let result = this.materialOptions.find(item => {
        return item.label == row.value
      })
      row.rawMaterialCode = result.value
      row.rawMaterialId = result.rawMaterialId
    },
    changeType (row) {
      if (row.elementCode) this.$set(row, 'elementCode', null)
      if (row.value) this.$set(row, 'value', null)
      if (row.priceMode) this.$set(row, 'priceMode', null)
      if (row.rawMaterialCode) this.$set(row, 'rawMaterialCode', null)
    },
    changeElementCode (row) {
      let result = this.elementCodeOptions.find(item => {
        return item.value == row.elementCode
      })
      row.marketType = result.marketType
      row.elementId = result.elementId
      row.element = result.element
    },
    addItem () {
      this.$refs['table'].form.dataSource.push({
        __edit_key__: true,
        __add_key__: true,
        __update_key__: true
      })
      this.$refs['table'].doLayout()
    },
    usageSceneChange () {
      this.setFormulaName()
      this.getElement()
      this.getMaterial()
    },

    marketTypeChange () {
      if (!this.form.marketType) return
      // this.getFrequency()
      // 获取要素下拉列表
      this.getElement()
      this.getMaterial()
      this.setFormulaName()
    },
    setFormulaName () {
      let marketTypeName = this.storeMap['COST_LINK_MARKET_TYPE'][this.form.marketType] || ''
      let usageSceneName = this.storeMap['COST_LINK_SCENE_TYPE'][this.form.applicationScen] || ''
      this.form.formulaName = `${marketTypeName}-${usageSceneName}`
    },
    getFrequency () {
      // 获取联动频次
      // this.$http({
      //   url: '/api-cost/api-ql/marketType/material/getLinkFrequency',
      //   method: 'GET',
      //   params: { marketType: this.form.marketType },
      //   loading: true,
      // }).then(res => {
      //   let result = res.data || {}
      //   this.form.frequency = result
      // })
    },
    getMaterial () {
      if (!this.form.marketType || !this.form.applicationScen) return
      let params = { status: 'VALID', marketType: this.form.marketType, applicationScen: this.form.applicationScen }
      let data = transformMQL.listGetData('CostMarketRawMaterial', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      data.payload.page.pageSize = 999
      data.payload.page.pageNum = 1
      this.$http({
        url: '/api-cost/api-ql/CostMarketRawMaterial/customQuery',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        let result = res.data || {}
        let list = result.records || []
        this.materialOptions = list.map(item => {
          return {
            value: item.rawMaterialCode,
            label: item.rawMaterialName,
            rawMaterialId: item.rawMaterialId
          }
        })
      })
    },
    getElement (callback) {
      if (!this.form.marketType || !this.form.applicationScen) return
      let params = { status: 'VALID', marketType: this.form.marketType, applicationScen: this.form.applicationScen }
      let data = transformMQL.listGetData('CostMarketElement', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      data.payload.page.pageSize = 999
      data.payload.page.pageNum = 1
      this.$http({
        url: '/api-cost/api-ql/CostMarketElement/customQuery',
        method: 'POST',
        loading: true,
        data: data
      }).then(res => {
        let result = res.data.records || []
        this.elementCodeOptions = result.map(item => {
          console.log('item', item)
          return {
            value: item.elementCode,
            label: item.element,
            marketType: item.marketType,
            elementCode: item.elementCode,
            element: item.element,
            elementId: item.elementId
          }
        })
        if (callback && typeof callback === 'function') callback()
      })
    },
    // 明细行--删除
    deleteLine (scope, data) {
      this.deleteLineCache.push(scope)
      data.splice(scope.$index, 1)
    },
    insertLine (scope, data) {
      data.splice(scope.$index + 1, 0, {
        __edit_key__: true,
        __add_key__: true,
        __update_key__: true
      })
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    saveBill (type) {
      let params = this.initParams()
      let url = '/api-cost/api-ql/CostMarketFormula/customSave'

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))
      if (!validFlag) {
        return this.__focus_error__()
      }

      this.$refs.table.validate()
      let tableFlag = true
      this.realDataSource.forEach(item => {
        if (item.formulaItem == 'ELEMENT') {
          if (item.marketType) {
            // eslint-disable-next-line curly
            if (item.elementCode == 'PRICE') {
              if (
                !item.formulaItem ||
              !item.elementCode ||
              // !item.priceStyle ||
              !item.priceMode ||
              !item.value
              ) { tableFlag = false }
            } else if (item.elementCode == 'WEIGHT') {
              if (
                !item.formulaItem ||
              !item.elementCode ||
              // !item.priceStyle ||
              // !item.priceMode ||
              !item.value
              ) { tableFlag = false }
            }
          } else {
            if (!item.formulaItem || !item.elementCode || !item.value) tableFlag = false
          }
        } else if (item.formulaItem == 'OPERATOR' || item.formulaItem == 'NUMBER') {
          if (!item.formulaItem || !item.value) tableFlag = false
        } else {
          tableFlag = false
        }
      })
      if (!tableFlag) {
        return this.$message({
          message: this.$t('请输入必填信息'), // '请输入单据必填信息'
          type: 'error'
        })
      }
      let formData = transformMQL.save('CostMarketFormula', [params], 'customSave')
      this.$http({
        url,
        method: 'POST',
        data: formData,
        loading: true
      }).then(res => {
        this.$message.success(res.message)

        if (res.data) {
          let { tabName } = this.$attrs.params
          this.deleteLineCache = []
          this.$emit('tab-remove', tabName)
          this.__setTabTodo('MarketFormulaList.getQueryData')
        }
      })
    },

    getFormDetail (row, isInit = false) {
      this.form = deepClone(row)
      let details = this.form.costMarketFormulaLines || []
      if (this.editFlag) {
        if (details.length) {
          details.forEach(item => {
            item.__edit_key__ = true
          })
        }
      }

      if (isInit) {
        this.getElement(() => {
          this.dataSource = this.setMarketType(details)
        })
        this.getMaterial()
      } else {
        this.dataSource = this.setMarketType(details)
      }
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    setMarketType (details) {
      if (details.length) {
        details.forEach(item => {
          let result = this.elementCodeOptions.find(ele => {
            return ele.value == item.elementCode
          })
          if (result) {
            item.marketType = result.marketType
          }
        })
      }
      return details
    },

    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('marketFormulaList.getQueryData')
    },

    initParams () {
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.formulaDetailed = this.formulaDetail
      params.costMarketFormulaLines = deepClone(this.realDataSource) || []
      // params.details.forEach((item, index) => {
      //   item.sortNum = index + 1
      // })
      if (this.deleteLineCache.length) {
        this.deleteLineCache.forEach(item => {
          if (item.row?.formulaLineId) {
            params.costMarketFormulaLines.push({ '$delete': item.row?.formulaLineId })
          }
        })
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
}
</style>
