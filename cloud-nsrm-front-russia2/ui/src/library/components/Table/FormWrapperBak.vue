<template>
  <div
    class="query-form-container list-page-query-form-wrapper"
    :class="{ formExpand: isActive, hasConfig: hiddingConfig ,'device-xs': device==='device-xs'}"
  >
    <el-form
      ref="formData"
      :model="formData"
      :label-width="formLabelWidth"
      :class="{ 'innerformExpand': isActive }"
    >
      <srm-row ref="formRow" :gutter="16">
        <template v-for="(item, k) in formArray">
          <srm-col
            :key="k"
            :init-col="colLength"
            :class="[{ 'hiddenCol': item.hidden && typeof item.hidden === 'function' ? item.hidden() : item.hidden }]"
          >
            <el-form-item
              :label="
                typeof item.label === 'function' ? item.label() : item.label
              "
              :label-width="formLabelWidth"
              :rules="item.rules"
              :prop="item.prop"
            >
              <template v-if="item.type === 'slot' && item.slot">
                <slot
                  :name="item.slot"
                  :scope="formData"
                />
              </template>
              <el-select
                v-else-if="item.type === 'select' && Array.isArray(item.options)"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                clearable
                filterable
              >
                <el-option
                  v-for="subitem in item.options"
                  :key="subitem.value + '.' + Math.random()"
                  :value="subitem.value"
                  :label="subitem.label"
                />
              </el-select>
              <dict-select
                v-else-if="item.type === 'dict'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                :code="item.code"
                :filterItem="typeof item.filterItem === 'function' ? item.filterItem() : []"
                :multiple="item.multiple || false"
                :transform-options="typeof item.transformOptions === 'function' ? item.transformOptions : null"
                :lazy-init="dictConfig.lazyInit"
              />
              <dict-select
                v-else-if="item.type === 'custom-dict'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                :code="item.code"
                :filterItem="typeof item.filterItem === 'function' ? item.filterItem() : []"
                :multiple="item.multiple || false"
                :transform-options="typeof item.transformOptions === 'function' ? item.transformOptions : null"
                :custom-select-type="item.customSelectType"
              />
              <el-select
                v-else-if="
                  item.type === 'select' && typeof item.options === 'function'
                "
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                clearable
                filterable
              >
                <el-option
                  v-for="subitem in item.options()"
                  :key="subitem.value + '.' + Math.random()"
                  :value="subitem.value"
                  :label="subitem.label"
                />
              </el-select>
              <el-select
                v-else-if="item.type === 'select' && !item.options"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                clearable
                filterable
              >
                <el-option
                  v-for="subitem in selectDictionary[item.prop]"
                  :key="subitem.value + '.' + Math.random()"
                  :value="subitem.value"
                  :label="subitem.label"
                />
              </el-select>
              <!-- 选择日期 -->
              <el-date-picker
                v-else-if="item.type === 'date'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('vendorMod.datePicker')"
              />
              <!-- 选择日期 -->
              <el-date-picker
                v-else-if="item.type === 'datetime'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="datetime"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :placeholder="$t('vendorMod.datePicker')"
              />
              <el-date-picker
                v-else-if="item.type === 'daterange'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="daterange"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                range-separator="~"
                :start-placeholder="$t('dataConfMod.startDay')"
                :end-placeholder="$t('dataConfMod.endDay')"
              />
              <el-date-picker
                v-else-if="item.type === 'month'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="month"
                value-format="yyyy-MM"
                :format="$formatDatePicker"
                :placeholder="$t('components.selectMonth')"
              />
              <el-date-picker
                v-else-if="item.type === 'year'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="year"
                value-format="yyyy"
              />
              <el-date-picker
                v-else-if="item.type === 'monthrange'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="monthrange"
                :unlink-panels="true"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :range-separator="$t('components.pager.to')"
                :start-placeholder="$t('components.startMonth')"
                :end-placeholder="$t('components.endMonth')"
              />
              <el-input
                v-else-if="item.type === 'number'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                type="number"
                @keyup.enter.native="onQuery"
              />
              <el-input
                v-else-if="item.type === 'formattorText'"
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                clearable
                class="input_group__append"
                @blur="formatSingleText(item)"
              >
                <el-button
                  slot="append"
                  class="the_el_input_group__append"
                  icon="el-icon-circle-plus-outline"
                  @click="onFocus(item)"
                />
              </el-input>
              <template v-else-if="item.type === 'quicksearch'">
                <span
                  ref="bindKey"
                  style="display: none"
                >
                  {{ item.showKey }}
                </span>
                <quick-search
                  ref="quickSearchTool"
                  :show-key="item.showKey"
                  :scope-data="item"
                  :name="item.name"
                  :pre-query-data="item.preQueryData"
                  :disabled="item.disabled"
                  @close-quicksearch="getObj"
                />
              </template>
              <c-category-select
                v-else-if="item.type === 'catSelect'"
                ref="categorySelect"
                v-model="formData[item.prop]"
                :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
                :show-key="item.showKey"
                :data-source="item.dataSource"
                @select="callback(item,$event)"
              />
              <el-switch
                v-else-if="item.type === 'switch'"
                v-model="formData[item.prop]"
                active-value="Y"
                inactive-value="N"
              />
              <el-checkbox
                v-else-if="item.type === 'checkbox'"
                v-model="formData[item.prop]"
                true-label="Y"
                false-label="N"
              >
                {{ item.labelValue }}
              </el-checkbox>
              <organization-selector
                v-else-if="item.type === 'OUorganizationSelector'"
                ref="ouSelector"
                v-model="formData[item.prop]"
                :parent-id="-1"
                node-type="OU"
                selectType="input"
                :placeholder="$t('common.pleaseSelect')"
                :multiple="item.multiple"
                :collapse-tags="item.collapseTags"
                @select="orgSelect"
              />
              <organization-selector
                v-else-if="item.type === 'INVorganizationSelector'"
                ref="invSelector"
                v-model="formData[item.prop]"
                :placeholder="$t('common.pleaseSelect')"
                :parent-id="
                  item.parentId
                    ? formData[item.parentId]
                    : formData['orgId'] || -1
                "
                node-type="INV"
                selectType="input"
                :multiple="item.multiple"
                :collapse-tags="item.collapseTags"
                :auto-select-when-one-item="item.autoSelectWhenOneItem"
              />
              <el-input
                v-else
                v-model="formData[item.prop]"
                :disabled="item.disabled"
                clearable
                @keyup.enter.native="onQuery"
              />
            </el-form-item>
          </srm-col>
        </template>
      </srm-row>
    </el-form>
    <div class="btnGroup">
      <div>
        <el-button
          type="primary"
          class="searchBtn"
          @click="query"
        >
          {{ $t("common.search") }}
        </el-button>
        <el-button
          class="resetBtn"
          @click="reset"
        >
          {{ $t("common.reset") }}
        </el-button>
        <el-button
          v-if="showToggleButton || hasConfigData"
          type="text"
          :class="['toggleBtn', { btnExpand: isActive }]"
          @click="handleClick"
        >
          {{ taggleTex }}
          <em class="el-icon-arrow-down" />
        </el-button>
      </div>
      <div class="pop-query-div">
        <el-popover
          v-model="configVisible"
          placement="bottom"
          trigger="manual"
          width="200"
          :tabindex="0"
          popper-class="query-config-wrap"
          @hide="popoverHide"
        >
          <div v-if="hasConfigData && isActive" ref="queryConfigInner">
            <div class="query-title">
              {{ $t("components.conditional") }}
            </div>
            <div class="query-item">
              <div v-for="qItem in configData" :key="qItem.prop">
                <em class="el-icon-rank drag-icon" />
                <el-checkbox
                  v-model="qItem.queryItemSelect"
                  :label="typeof qItem.label === 'function' ? qItem.label() : qItem.label"
                  :disabled="qItem.rules ? qItem.rules.required : false"
                />
              </div>
            </div>
            <div class="configBtn">
              <el-button type="text" class="c-reset-btn" style="" @click="resetConfig">
                {{ $t("common.reset") }}
              </el-button>
              <el-button class="cancel-btn" @click="cancelConfig">
                {{ $t("common.cancel") }}
              </el-button>
              <el-button type="primary" class="c-confirm-btn" @click="setConfig">
                {{ $t("common.confirm") }}
              </el-button>
            </div>
          </div>
          <em
            v-show="hasConfigData && isActive"
            slot="reference"
            class="el-icon-setting querySettingBtn"
            @click="configVisible = true"
          >{{ $t("components.conditional") }}</em>
        </el-popover>
      </div>
    </div>
    <!-- 格式化本文 -->
    <srm-dialog
      :title="$t('cusEntry.library.textFormat')"
      size="middle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-input
        v-model="globalText"
        type="textarea"
        :rows="12"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="formattorOne"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import OrganizationSelector from 'lib@/components/organization-selector'
import { isNull } from '@/utils'
import DictSelect from 'lib@/components/c-select/dict-select'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { has } from 'xe-utils'
const localStorageKeyPerfix = 'custom_query_key'

export default {
  name: 'FormWrapper',
  components: {
    QuickSearch,
    CCategorySelect,
    OrganizationSelector,
    DictSelect
  },
  inject: ['context'],
  props: {
    // 初始化展开或者收缩form的状态
    initActive: {
      type: Boolean,
      default: false
    },
    // 父页面传值--->配置项
    formLabelWidth: {
      type: String,
      default: '100px'
    },
    // 当前form绑定的 data 对象
    formArray: {
      type: Array,
      default: null
    },
    colLength: {
      type: Number,
      default: 3
    },
    preFormObj: {
      type: Object,
      default: null
    },
    selectDictionary: {
      type: Object,
      default: null
    },
    selectTreeOptions: {
      type: Array,
      default: function () {
        return []
      }
    },
    pFormData: {
      type: Object,
      default: null
    },
    // 格式化去除单引号
    formattorTextSecon: {
      type: Boolean,
      default: false
    },
    // 是都开启配置功能
    isConfig: {
      type: Boolean,
      default: false
    },
    queryName: { // 如果一个路由下面有多个列表页需要做配置，这个参数必传且唯一
      type: String,
      default: function () {
        return ''
      }
    }
  },
  data () {
    return {
      isActive: false,
      taggleTex: this.$t('common.expandForm'),
      tempForm: {
        fullPathId: null
      },
      formData: {},
      // formLabelWidth: '100px',
      colNum: 24 / this.colLength,
      dialogFormVisible: false,
      globalProp: null,
      globalText: null,
      dictConfig: {
        lazyInit: true
      },
      showToggleButton: false,
      configVisible: false,
      defaultConfig: [], // 默认配置数据
      configData: [],
      hasConfigData: false, // 是否有配置数据
      hiddingConfig: false, // 隐藏条件后小于4个判断
      formArrayBak: [], // 备份formArray数据用于重置的时候还原原来排序
      pageViewConfigCode: '' // 配置key 对应后台配置
    }
  },
  computed: {
    device () {
      return this.$store.getters.device
    }
  },
  watch: {
    formData: {
      handler (data) {
        this.$emit('update:PFormData', data)
      },
      immediate: true,
      deep: true
    },
    formArray: {
      handler (data) {
        this.$nextTick(() => {
          this.handleResize()
        })
      },
      immediate: true,
      deep: true
    },
    // 监听配置弹框
    configVisible (newValue, oldValue) {
      if (newValue && newValue !== oldValue) {
        if (this.isConfig) {
          this.$nextTick(() => this.initSortable())
        }
      }
    },
    // 监听路由变化关掉配置弹框
    $route: {
      deep: true,
      immediate: true,
      handler () {
        if (this.isConfig) {
          this.configVisible = false
        }
      }
    }
  },
  updated () {
    this.$emit('synchronous-value', this.formData)
  },
  async created () {
    this.pageViewConfigCode = this.queryName || this.$route.name
    this.formArrayBak = [...this.formArray]
    this.initConfig(this.formArray) // 初始化配置
    // 字典处理 [[
    let dictSet = []
    for (let i = 0; i < this.formArray.length; i++) {
      if (this.formArray[i].type === 'dict') {
        dictSet.push(this.formArray[i].code)
      }
    }
    if (dictSet.length > 0) {
      await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, { dictCodeList: dictSet })
      this.dictConfig.lazyInit = false
    }
    // 字典处理 ]]
    this.isActive = !!this.initActive
    this.taggleTex = this.isActive ? this.$t('common.collapseForm') : this.$t('common.expandForm')
    // 前置条件赋值
    this.$nextTick(() => {
      if (this.preFormObj && JSON.stringify(this.preFormObj) !== '{}') {
        for (let k in this.preFormObj) {
          if (this.preFormObj[k]) {
            this.$set(this.formData, k, this.preFormObj[k])
          }
        }
      }
    })
  },
  mounted () {
    window.addEventListener('resize', this.handleResize)
    this.$nextTick(() => {
      this.handleResize()
    })
  },
  activated () {
    this.$nextTick(() => {
      this.handleResize()
    })
  },
  destroyed () {
    this.configVisible = false
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    // 选择业务实体就清空库存组织
    orgSelect () {
      this.formArray.forEach(item => {
        if (item.type === 'INVorganizationSelector') {
          this.$set(this.formData, item.prop, '')
          this.$refs.invSelector[0].selectTreeOptions = []
        }
      })
    },
    // 查询接口配置信息
    async fatchConfig () {
      let queryConfig = ''
      if (this.isConfig) {
        let query = { pageViewConfigCode: this.pageViewConfigCode }
        const { data = {} } = await this.$api.base.pageConfig.getCurrentConfig(query)
        console.log('getCurrentConfigqueryConfig')
        queryConfig = data.queryConfig || ''
      }
      return queryConfig
    },
    saveConfig (queryConfig) {
      let query = {
        pageViewConfigCode: this.pageViewConfigCode,
        configType: 'QUERY',
        queryConfig: queryConfig
      }
      this.$api.base.pageConfig.saveUserConfig(query).then(res => {})
    },
    async clearConfig () {
      let query = {
        pageViewConfigCode: this.pageViewConfigCode,
        configType: 'QUERY'
      }
      await this.$api.base.pageConfig.removeUserConfig(query)
    },
    // 设置配置key
    getParams () {
      const userId = this.$store.getters.user.userId
      const key = `${localStorageKeyPerfix}_${userId}_${this.$route.name}_${this.pageViewConfigCode}`
      return key
    },
    // 初始化配置
    async initConfig (initData) {
      if (!this.isConfig) { return }
      this.defaultConfig = (initData || []).map(({ label, prop, hidden = false, rules = { required: false } }) => ({ label, prop, hidden, rules, queryItemSelect: true }))
      const config = await this.queryConfig()
      this.configData = config || this.defaultConfig
      if (this.isConfig && this.configData.length > 0) { // 开启配置 且有3个以上配置的
        this.hasConfigData = true
      }
      this.setConfigData() // 数据设置
    },
    // 查询已有配置
    async queryConfig () {
      const key = this.getParams()
      const JSON_CONFIG = localStorage.getItem(key)
      const JSON_CONFIG_SERVICE = await this.fatchConfig() // 获取配置信息
      const CONFIG_RES = JSON_CONFIG_SERVICE || JSON_CONFIG
      const config = CONFIG_RES ? JSON.parse(CONFIG_RES) : undefined
      const defaultTConfig = this.defaultConfig
      let meta = []
      if (config) { // 本地存在配置数据
        meta = config.map(({ prop, hidden, rules, queryItemSelect }) => {
          const target = defaultTConfig.find(i => i.prop === prop)
          return { ...target, hidden, rules, queryItemSelect }
        })
      } else {
        meta = defaultTConfig.map(
          ({ label, prop, hidden, rules, queryItemSelect }) => ({ label, prop, hidden, rules, queryItemSelect })
        )
      }
      return meta
    },
    // 设置数据 formArrayBak
    setConfigData () {
      let noHiddingLength = 0
      this.configData.forEach((item, index) => {
        let newIndex = index
        let prop = item.prop
        let oldIndex = this.formArray.findIndex(i => i.prop === prop)
        if (!item.queryItemSelect) { // 去掉显示
          this.formArray[oldIndex].hidden = true
          item.hidden = true
        } else {
          this.formArray[oldIndex].hidden = false
          item.hidden = false
          noHiddingLength += 1
        }
        // 调换位置
        const currRow = this.formArray.splice(oldIndex, 1)[0]
        this.formArray.splice(newIndex, 0, currRow)
      })
      this.$forceUpdate()
      if (noHiddingLength < 4 && this.isConfig) {
        this.hiddingConfig = true
      } else {
        this.hiddingConfig = false
      }
    },
    // 设置配置 保存到 localStorage
    async setConfig () {
      await this.setConfigData() // 数据设置
      this.configVisible = false
      const key = this.getParams()
      const config = JSON.stringify(this.configData)
      localStorage.setItem(key, config)
      this.saveConfig(config) // 保存到后台
    },
    // 重置条件配置
    async resetConfig () {
      const key = this.getParams()
      localStorage.removeItem(key) // 置空配置
      await this.clearConfig() // 清除后台
      await this.initConfig(this.formArrayBak)
      this.$forceUpdate()
    },
    // 取消配置
    cancelConfig () {
      this.initConfig(this.formArray)
      this.configVisible = false
    },
    // 弹出层关闭时
    popoverHide () {
      let popDom = document.querySelector('.query-config-wrap')
      popDom.remove() // 删除节点
    },
    // 初四化拖拽
    initSortable () {
      let queryConfigInner = this.$refs.queryConfigInner
      if (queryConfigInner) {
        const divbody = queryConfigInner.querySelector(
          '.query-config-wrap .query-item'
        )
        const _this = this
        if (divbody) {
          Sortable.create(divbody, {
            animation: 180,
            delay: 0,
            onEnd ({ newIndex, oldIndex }) {
              const currRow = _this.configData.splice(oldIndex, 1)[0]
              _this.configData.splice(newIndex, 0, currRow)
            }
          })
        }
      }
    },
    /* 根据表单row高度对比col高度，判断是否显示展开按钮 */
    handleResize () {
      const formRow = this.$refs.formRow
      if (has(formRow, '$el.childNodes[0]')) {
        const rowHeight = formRow.$el.clientHeight
        const colHeight = formRow.$el.childNodes[0].clientHeight
        this.showToggleButton = (rowHeight / colHeight) >= 2
      } else {
        this.showToggleButton = false
      }
    },

    setValue (key, value) {
      this.$set(this.formData, key, value)
    },
    organizationSelectTreeChange (node, value, item) {
      this.formData[item.prop] = node.organizationId
    },
    query () {
      this.$refs['formData'].validate(valid => {
        if (valid) {
          // 查询--不要屏蔽清除空条件，否则有些没有做为空识别的接口请求会报错
          for (let i in this.formData) {
            if (isNull(this.formData[i])) {
              var isCheckbox = false
              for (let j in this.formArray) {
                if (this.formArray[j].prop === i && this.formArray[j].type === 'checkbox') {
                  isCheckbox = true
                }
              }
              if (isCheckbox) {
                this.formData[i] = null
              } else {
                delete this.formData[i]
              }
            }
          }

          this.formatFormDataAndEmit()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    onQuery () {
      // 查询
      for (let i in this.formData) {
        if (!this.formData[i]) delete this.formData[i]
      }
      this.formatFormDataAndEmit()
    },

    /* 格式化查询参数并发起父组件回调 */
    formatFormDataAndEmit () {
      for (let key in this.formData) {
        // 只格式化输入框的 不用trim修饰符，会导致无法在最后追加中间空格输入
        if (this.formArray.find(item => item.prop === key && !item.type)) {
          // 删除输入框字符串两端的空白字符
          let formData = this.formData[key] ? this.formData[key].toString() : ''
          this.formData[key] = formData.trim()
        }
      }
      this.$emit('getFormData', this.formData)
    },

    reset () {
      this.tempForm.fullPathId = null
      // 重置所有过滤条件
      for (let i in this.formData) {
        this.formData[i] = null
      }
      const refNodes = Array.from([].concat(this.$refs.quickSearchTool))
      refNodes.forEach(node => {
        node && node.clearInput()
      })
      const refCatNodes = Array.from([].concat(this.$refs.categorySelect))
      refCatNodes.forEach(node => {
        node && node.clearInput()
      })
    },
    // 切换开合
    handleClick () {
      this.isActive = !this.isActive
      this.taggleTex = this.isActive
        ? this.$t('common.collapseForm')
        : this.$t('common.expandForm')
    },
    getObj (val, scope) {
      if (scope.propKey) {
        this.formData[scope.prop] = val ? val[scope.propKey] : ''
      } else {
        this.formData[scope.prop] = val ? val[scope.showKey] : ''
      }
    },
    callback (item, row, scope) {
      if (item.callback) {
        item.callback(row, scope)
      }
    },
    onFocus (val) {
      this.globalProp = val.prop
      this.globalText = null
      this.dialogFormVisible = true
    },
    formatSingleText (val) {
      if (!this.formData[val.prop]) return
      this.formData[val.prop] = this.formData[val.prop].replace(/''/gi, '\'')
    },
    formattorOne (val) {
      if (!this.globalText) return this.$message.warning(this.$t('components.inputText'))
      // 去掉空格换行回车等--格式化
      let isFormattoredText = ''
      if (this.formattorTextSecon) {
        // eslint-disable-next-line no-useless-escape
        isFormattoredText = this.globalText.replace(/\ +/gi, '').replace(/\n+/gi, ',')
      } else {
        // eslint-disable-next-line no-useless-escape
        isFormattoredText = '\'' + this.globalText.replace(/\ +/gi, '').replace(/\n+/gi, '\',\'') + '\''
      }

      isFormattoredText = isFormattoredText.replace(/,''/gi, '').replace(/''/gi, '')
      this.$set(this.formData, this.globalProp, isFormattoredText)
      this.dialogFormVisible = false
    }
  }
}
</script>
<style>
.query-form-container.list-page-query-form-wrapper{
  border: 0;
  padding: 0;
}
.query-form-container.list-page-query-form-wrapper .el-form{
  /* padding-top: 16px; */
  margin: 0;
}
.vue-treeselect__label {
  font-size: 12px;
  font-weight: 400;
}
.list-page-query-form-wrapper .el-form-item--small .el-form-item__content {
  /* height: 28px !important;
  line-height: 28px !important; */
}
.list-page-query-form-wrapper .el-form-item__label {
  padding-right: 8px;
  white-space: normal;
  line-height: 14px !important;
  text-align: right;
  vertical-align: middle;
  max-height: 28px;
  float: none !important;
  display: inline-block !important;
  width: 35% !important;
  /* 自适应用这两行 */
  /* max-width: 55%;
  width: auto !important; */
  box-sizing: border-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
/* display: -webkit-box; */
.list-page-query-form-wrapper .el-form-item__content {
  vertical-align: middle;
  display: inline-block;
  margin-left: 0 !important;
  width: 65% !important;
  box-sizing: border-box;
  height: 28px;
  /* 自适应开启 */
  /* flex: 1; */
}
.input_group__append .el-input-group__append{
  padding: 0 12px;
  text-align: center;
}
.the_el_input_group__append {
  padding: 8px 0px;
  min-width: 20px;
  text-align: center;
}
.query-config-wrap{
  padding: 0 !important;
}
.query-item .el-checkbox .el-checkbox__label{
  max-width: 130px;
  font-size: 12px;
  white-space: normal;
  vertical-align: middle;
  overflow: hidden;
  text-overflow: ellipsis;
  /* display:-webkit-box; */
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  max-height: 38px;
  color: #393E45 !important;
  word-break: break-word;
  text-align: left;
}
</style>
<style scoped lang="scss">
.query-form-container {
  height: 46px;
  position: relative;
  transition: 0.3s height ease-in-out;
  // overflow: hidden;
  .el-form {
    padding-right: 215px;
    margin-top: 0px;
    height: 36px;
    overflow: hidden;
    transition: 0.3s height ease-in-out;
    > .el-row{
      padding-top: 2px;
    }
    &.innerformExpand{
      height: auto;
    }
  }
  .btnGroup {
    position: absolute;
    top: 2px;
    right: 0px;
    z-index: 994;
    .toggleBtn {
      font-size: 12px;
      min-width: 50px;
      padding-left: 0;
      padding-right: 0;
      color: #51555B;
    }
    .searchBtn,
    .resetBtn {
      // padding: 8px 6px;
      width: 58px;
    }
    .pop-query-div{
      margin-top: 20px;
    }
  }
  .el-form .el-row .el-form-item:first-child {
    margin-bottom: 16px;
    /* 自适应开启 */
    // display: flex;
    // flex-direction: row;
    // flex-wrap: nowrap;
    // justify-content: space-between;
    // align-items: center;
  }
  &.list-page-query-form-wrapper {
    border-right: 0;
    border-left: 0;
    border-top: 0;
    flex: none;
    .hiddenCol{
      display: none;
    }
  }
  &.hasConfig{
    min-height: 76px;
  }
  &.formExpand {
    height: auto !important;
  }
  &.device-xs{
    height: 76px;
    >.el-form{
      padding-right: 0;
    }
    .btnGroup{
      position:static;
    }
  }
}

.btnExpand i {
  transform: rotate(180deg);
}
.querySettingBtn{
  color: #96999C;
  font-size: 12px;
  line-height: 20px;
  cursor: pointer;
}
.query-config-wrap{
  padding: 0 !important;
  .query-title{
    font-size: 14px;
    color: #393E45;
    line-height: 22px;
    height: 40px;
    padding: 9px 12px;
    border-bottom: 1px solid #DCDDDE;
    font-weight: bold;
    box-sizing: border-box;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .query-item{
    font-size: 12px;
    border-bottom: 1px solid #DCDDDE;
    max-height: 296px;
    overflow-y: auto;
    div {
      padding: 6px 8px 5px;
      &:hover{
        background-color: #E6F6FF;
      }
      .el-checkbox{
        vertical-align: middle;
      }
      .drag-icon{
        font-size: 14px;
        margin-right: 12px;
        color: #DCDDDE;
        vertical-align: middle;
        cursor: move;
      }
    }
  }
  .configBtn{
    padding: 9px 12px;
    text-align: right;
    .cancel-btn{
      border-color: #DCDDDE;
      margin-left: 0;
      padding-left: 12px;
      padding-right: 12px;
    }
    .c-confirm-btn{
      margin-left: 8px;
      padding-left: 12px;
      padding-right: 12px;
    }
    .c-reset-btn{
      color:#96999C;
      padding-left: 0 !important;
      float: left;
    }
  }
}
</style>
