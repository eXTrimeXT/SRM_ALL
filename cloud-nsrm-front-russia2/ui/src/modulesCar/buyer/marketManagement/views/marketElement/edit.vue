<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form ref="form" :rules="rules" :model="form" :disabled="disabledFlag">
            <srm-row>
              <srm-col>
                <el-form-item prop="elementCode" :label="$t('要素')">
                  <DictSelect
                    v-model="form.elementCode"
                    clearable
                    :lazy-init="lazyInit"
                    code="SCC_COST_MARKET_LINK_ELEMENT"
                    @change-value="elementCodeChange"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item prop="marketType" :label="$t('marketBudget.marketType')">
                  <DictSelect
                    v-model="form.marketType"
                    clearable
                    :lazy-init="lazyInit"
                    code="COST_LINK_MARKET_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item prop="applicationScen" :label="$t('marketBudget.usageScene')">
                  <DictSelect
                    v-model="form.applicationScen"
                    clearable
                    :lazy-init="lazyInit"
                    code="COST_LINK_SCENE_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('marketBudget.functionName')" prop="functionName">
                  <QuickSearch
                    :show-input="form.functionName"
                    show-key="functionName"
                    :scope-data="form"
                    name="scc_cost_api_function"
                    :disabled="disabledFlag"
                    @close-quicksearch="getApiFunction"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item prop="status" :label="$t('marketBudget.status')">
                  <DictSelect
                    v-model="form.status"
                    clearable
                    :lazy-init="lazyInit"
                    code="BASE_INFO_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--取消-->
        <el-button type="ghost" @click="back">
          {{ $t('common.cancel') }}
        </el-button>

        <!--保存-->
        <el-button v-if="editFlag" type="primary" @click="saveBill('SAVE')">
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
import TableView from 'lib@/components/Table/TableView'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import DictSelect from 'lib@/components/c-select/dict-select'
import { deepClone } from '@/utils'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MeetModelDetail',

  components: {
    CToolbar,
    QuickSearch,
    TableView,
    DictSelect
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      timer: null,
      queryParam: {
        status: 'VALID'
      },
      lazyInit: true,
      activeDims: ['1', '2', '3'],
      form: {
        element: '',
        elementCode: null,
        marketType: null,
        status: 'DRAFT',
        apiFunctionId: '',
        functionName: '',
        applicationScen: ''

      },
      rules: {
        elementCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        marketType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        functionName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        applicationScen: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      storeMap: null
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

  async created () {
    this.storeMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
      dictCodeList: [
        'SCC_COST_MARKET_LINK_ELEMENT_TYPE_TYPE',
        'BASE_QUOTES_CALCULATE_TYPE',
        'COST_LINK_MARKET_TYPE',
        'BASE_INFO_STATUS'
      ]
    })
    this.lazyInit = false
    let { row } = this.urlParams
    console.log(row)
    if (row && row.elementId) {
      this.getFormDetail(row)
    }
  },
  destroyed () {
    clearTimeout(this.timer)
  },
  methods: {
    elementCodeChange (val, dictItem) {
      if (val) {
        this.form.element = dictItem.label
      }
    },
    getApiFunction (val, scope) {
      scope.apiFunctionId = val ? val.apiFunctionId : ''
      scope.functionName = val ? val.functionName : ''
    },
    getQueryData () {
      clearTimeout(this.timer)
      this.queryParam.marketType = this.form.marketType
      this.timer = setTimeout(() => {
        this.$refs['gridId'].query()
      })
    },

    saveBill (type) {
      let params = this.initParams()
      let url = '/api-cost/api-ql/CostMarketElement/customSave'

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))

      if (!validFlag) {
        this.__focus_error__()
      }
      let formData = transformMQL.save('CostMarketElement', [params], 'customSave')
      this.$http({
        url,
        method: 'POST',
        data: formData,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.back()
      })
    },

    getFormDetail (row) {
      let detail = deepClone(row)
      this.form = {
        ...detail
      }
    },

    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('MarketElementList.getQueryData')
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
}
</style>
