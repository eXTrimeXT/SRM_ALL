<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form
            ref="form"
            :rules="rules"
            :model="form"
            :disabled="disabledFlag"
          >
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item
                  prop="name"
                  :label="$t('marketBudget.formulaName')"
                >
                  <el-input
                    v-model="form.name"
                    disabled
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
                  prop="frequency"
                  :label="$t('marketBudget.linkFrequency')"
                >
                  <DictSelect
                    v-model="form.frequency"
                    disabled
                    code="BID_LINK_FREQUENCY"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="usageScene"
                  :label="$t('marketBudget.usageScene')"
                >
                  <DictSelect
                    v-model="form.usageScene"
                    clearable
                    code="BASE_FORMULA_USAGE_SCENE"
                    @change="usageSceneChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="1">
                <el-form-item
                  prop="detail"
                  :label="$t('公式明细')"
                >
                  <el-input
                    v-model="formulaDetail"
                    type="textarea"
                    :rows="3"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item :title="$t('公式定义')" name="2">
          <el-button
            v-if="editFlag"
            type="primary"
            style="margin-bottom: 10px"
            @click="addItem"
          >
            {{ $t("common.add") }}
          </el-button>
          <div style="height:500px;">
            <BaseTable
              ref="table"
              :columns="columns"
              :data-source="dataSource"
              :initialize="false"
              height="500"
              row-key="detailId"
              border
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <template #formulaType="{ scope }">
                <el-select v-model="scope.row.type" placeholder="请选择" @change="changeType(scope.row)">
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
              <template #elementId="{ scope }">
                <el-select v-show="scope.row.type == 'ELEMENT' " v-model="scope.row.elementId" placeholder="请选择" @change="changeElementCode(scope.row)">
                  <el-option
                    v-for="item in elementCodeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
              <template #priceStyle="{ scope }">
                <DictSelect
                  v-show="scope.row.type == 'ELEMENT' && scope.row.marketType "
                  v-model="scope.row.priceStyle"
                  code="BID_MARKET_BUDGET_PRICE_TYPE"
                />
              </template>
              <template #elementValue="{ scope }">
                <el-select v-show="scope.row.type == 'ELEMENT' " v-model="scope.row.value" placeholder="请选择" @change="changeMaterial(scope.row)">
                  <el-option
                    v-for="item in materialOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.label"
                  />
                </el-select>
                <el-select v-show="scope.row.type == 'OPERATOR' " v-model="scope.row.value" placeholder="请选择">
                  <el-option
                    v-for="item in operatorOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <el-input v-show="scope.row.type == 'NUMBER'" v-model="scope.row.value" v-input-format="{ type: 'float'}" placeholder="请输入" />
              </template>
            </BaseTable>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--保存-->
        <el-button v-if="editFlag" type="primary" @click="saveBill()">
          {{ $t('common.save') }}
        </el-button>
        <!--取消-->
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
import BaseTable from 'lib@/components/BaseTable/baseTable'
import { STORE_COMMON_CACHE } from '@/config/store-config'
// import { createStore, RenderSelect } from 'lib@/utils/easy-dictionary'
export default {
  name: 'MeetModelDetail',

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
        name: null,
        marketType: null,
        frequency: null,
        usageScene: null
      },
      rules: {
        marketType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        usageScene: [{ required: true, message: this.$t('common.pleaseSelect') }]

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
      columns: []
    }
  },

  computed: {
    formulaDetail () {
      let result = ''
      this.realDataSource.forEach(item => {
        if (item.value) {
          if (item.type == 'ELEMENT') {
            let matchElementCode = this.elementCodeOptions.find(ele => {
              return ele.value == item.elementId
            })
            matchElementCode = matchElementCode || {}
            if (matchElementCode && matchElementCode.marketType) {
              let priceTypeMap = {
                'STANDARD': '基准价',
                'FORECAST': '预估价'
              }
              let str = `${item.value}_${priceTypeMap[item.priceStyle]}`
              result += str
            } else {
              let str = `${item.value}_${matchElementCode.label}`
              result += str
            }
          } else if (item.type == 'OPERATOR' || item.type == 'NUMBER') {
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
    this.storeMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, { dictCodeList: ['COST_LINK_MARKET_TYPE', 'BASE_FORMULA_USAGE_SCENE'] })
    // this.store.commit("loadDictionary", ["BID_MARKET_BUDGET_PRICE_TYPE"]);
    this.lazyInit = false
    let { row, flag } = this.urlParams
    if (row && row.formulaId) {
      this.getFormDetail(row.formulaId, true)
    }

    this.columns = [
        {
          attrs: {
            prop: 'type',
            label: '类型',
            formatter: (cellValue, row) =>
              this.$getDictLabelByValue(this.options, cellValue)
          },
          slot: 'formulaType',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            prop: 'elementId',
            label: '要素',
            formatter: (cellValue, row) =>
              this.$getDictLabelByValue(this.elementCodeOptions, cellValue)
          },
          slot: 'elementId',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            prop: 'priceStyle',
            label: '价格方式',
            formatter: (cellValue, row) =>
              this.$getDictLabel('BID_MARKET_BUDGET_PRICE_TYPE', cellValue)

          },
          slot: 'priceStyle',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            prop: 'value',
            label: '值'
          },
          slot: 'elementValue',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            prop: 'materialCode',
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
      row.materialCode = result.value
    },
    changeType (row) {
      this.$set(row, 'elementId', null)
      this.$set(row, 'marketType', null)
      this.$set(row, 'value', null)
      this.$set(row, 'priceStyle', null)
      this.$set(row, 'materialCode', null)
    },
    changeElementCode (row) {
      let result = this.elementCodeOptions.find(item => {
        return item.value == row.elementId
      })
      row.marketType = result.marketType
    },
    addItem () {
      this.$refs['table'].form.dataSource.push({
        '__edit_key__': true,
        '__add_key__': true,
        '__update_key__': true
      })
      this.$refs['table'].doLayout()
    },
    usageSceneChange () {
      this.setFormulaName()
    },

    marketTypeChange () {
      if (!this.form.marketType) return
      this.getFrequency()
      // 获取要素下拉列表
      this.getElement()
      this.getMaterial()
      this.setFormulaName()
    },
    setFormulaName () {
      let marketTypeName = this.storeMap['COST_LINK_MARKET_TYPE'][this.form.marketType] || ''
      let usageSceneName = this.storeMap['BASE_FORMULA_USAGE_SCENE'][this.form.usageScene] || ''
      this.form.name = `${marketTypeName}-${usageSceneName}`
    },
    getFrequency () {
      // 获取联动频次
      this.$http({
        url: '/api-cost/marketType/material/getLinkFrequency',
        method: 'GET',
        params: { marketType: this.form.marketType },
        loading: true
      }).then(res => {
        let result = res.data || {}
        this.form.frequency = result
      })
    },
    getMaterial () {
      this.$http({
        url: '/api-cost/marketType/material/listPage',
        method: 'POST',
        data: { 'pageNum': 1, 'pageSize': 999, 'status': 'VALID', 'marketType': this.form.marketType },
        loading: true
      })
        .then((res) => {
          let result = res.data || {}
          let list = result.list || []
          this.materialOptions = list.map(item => {
            return {
              value: item.materialCode,
              label: item.materialName
            }
          })
        })
    },
    getElement (callback) {
      this.$http({
        url: `/api-cost/marketLink/formula/element/${this.form.marketType}`,
        method: 'GET',
        loading: true
      })
        .then((res) => {
          let result = res.data || []
          this.elementCodeOptions = result.map(item => {
            return {
              value: item.id,
              label: item.elementName,
              marketType: item.marketType
            }
          })
          if (callback && typeof callback === 'function')callback()
        })
    },
    // 明细行--删除
    deleteLine (scope, data) {
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
      let url = '/api-cost/marketLink/formula/save'

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))
      if (!validFlag) {
        return this.__focus_error__()
      }

      this.$refs.table.validate()
      let tableFlag = true
      this.realDataSource.forEach(item => {
        if (item.type == 'ELEMENT') {
          if (item.marketType) {
            if (!item.type || !item.elementId || !item.priceStyle || !item.value) tableFlag = false
          } else {
            if (!item.type || !item.elementId || !item.value) tableFlag = false
          }
        } else if (item.type == 'OPERATOR' || item.type == 'NUMBER') {
           if (!item.type || !item.value) tableFlag = false
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

      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (res.data) {
          this.form.formulaId = res.data
          this.getFormDetail(res.data)
        }
      })
    },

    getFormDetail (id, isInit = false) {
      this.$http({
        url: `/api-cost/marketLink/formula/${id}`,
        method: 'GET',
        params: { id },
        loading: true
      }).then(res => {
        this.form = res.data || {}
        let details = this.form.details || []
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
      })
    },
    setMarketType (details) {
      if (details.length) {
          details.forEach(item => {
          let result = this.elementCodeOptions.find(ele => {
            return ele.value == item.elementId
            }
          )
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
      params.detail = this.formulaDetail
      params.details = this.realDataSource || []
      params.details.forEach((item, index) => {
        item.sortNum = index + 1
      })
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
