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
                  prop="elementType"
                  :label="$t('marketBudget.elementType')"
                >
                  <DictSelect
                    v-model="form.elementType"
                    clearable
                    :lazyInit="lazyInit"
                    code="BASE_MARKET_LINK_ELEMENT_TYPE"
                    @change="elementTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item
                  prop="elementName"
                  :label="$t('marketBudget.elementName')"
                >
                  <el-input
                    v-model="form.elementName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col v-if="form.elementType == 'MATERIAL_QUOTES'" :init-col="3">
                <el-form-item
                  prop="marketType"
                  :label="$t('marketBudget.marketType')"
                >
                  <DictSelect
                    v-model="form.marketType"
                    clearable
                    :lazyInit="lazyInit"
                    code="COST_LINK_MARKET_TYPE"
                    @change="marketTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col v-if="form.elementType == 'MATERIAL_QUOTES'" :init-col="3">
                <el-form-item
                  prop="calculateType"
                  :label="$t('marketBudget.calculateType')"
                >
                  <DictSelect
                    v-model="form.calculateType"
                    clearable
                    :lazyInit="lazyInit"
                    code="BASE_QUOTES_CALCULATE_TYPE"
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
                    :lazyInit="lazyInit"
                    code="BASE_INFO_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item v-if="form.elementType == 'MATERIAL_QUOTES'" :title="$t('meeting.baseInfo')" name="2">
          <TableView
            :ref="'gridId'"
            url="/api-cost/marketType/material/listPage"
            :table-header="tableHeader"
            :comActive="$attrs['changeTab']"
            :pre-query-data="queryParam"
          />
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
import OrganizationSelector from 'lib@/components/organization-selector'
import TableView from 'lib@/components/Table/TableView'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import DictSelect from 'lib@/components/c-select/dict-select'

export default {
  name: 'MeetModelDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    TableView,
    DictSelect
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      queryParam: {
        status: 'VALID'
      },
      lazyInit: true,
      tableHeader: [
        {
          prop: 'materialCode',
          label: '原材料编码'
        },
        {
          prop: 'materialName',
          label: '原材料名称'
        }
      ],
      activeDims: ['1', '2', '3'],
      form: {
        elementType: null,
        elementName: null,
        marketType: null,
        calculateType: null,
        status: 'DRAFT'
      },
      rules: {
        elementType: [{ required: true, message: this.$t('common.pleaseInput') }],
        elementName: [{ required: true, message: this.$t('common.pleaseInput') }],
        marketType: [{ required: true, message: this.$t('common.pleaseInput') }],
        calculateType: [{ required: true, message: this.$t('common.pleaseInput') }]
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
    this.storeMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, { dictCodeList: ['BASE_MARKET_LINK_ELEMENT_TYPE', 'BASE_QUOTES_CALCULATE_TYPE', 'COST_LINK_MARKET_TYPE', 'BASE_INFO_STATUS'] })
    this.lazyInit = false
    let { row, flag } = this.urlParams
    if (row && row.id) {
      this.getFormDetail(row.id)
    }
  },

  methods: {
    elementTypeChange () {
      this.setElementName()
    },
    marketTypeChange () {
      this.setElementName()
      this.getQueryData()
    },
    getQueryData () {
      this.queryParam.marketType = this.form.marketType
      this.$nextTick(() => {
        this.$refs['gridId'].query()
      })
    },
    setElementName () {
      let elementTypeName = this.storeMap['BASE_MARKET_LINK_ELEMENT_TYPE'][this.form.elementType] || ''
      let marketTypeName = this.storeMap['COST_LINK_MARKET_TYPE'][this.form.marketType] || ''
      if (this.form.elementType == 'MATERIAL_QUOTES') {
        this.form.elementName = `${elementTypeName}-${marketTypeName}`
      } else {
        this.form.elementName = elementTypeName
      }
    },
    saveBill (type) {
      let params = this.initParams()
      let url = '/api-cost/marketLink/element/save'

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))

      if (!validFlag) {
        this.__focus_error__()
      }
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (!res.data) return this.back()
        this.getFormDetail(res.data)
      })
    },

    getFormDetail (id) {
      this.$http({
        url: `/api-cost/marketLink/element/${id}`,
        method: 'GET',
        params: { id },
        loading: true
      }).then(res => {
        this.form = res.data || {}
        if (this.form.elementType == 'MATERIAL_QUOTES') {
          this.getQueryData()
        }
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
}
</style>
