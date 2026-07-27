<template>
  <el-tooltip
    v-if="showTooltip"
    effect="dark"
    placement="top"
    :content="tooltipContent"
  >
    <el-select
      v-bind="$attrs"
      :value="value"
      filterable
      :clearable="typeof $attrs.clearable !== 'undefined' ? $attrs.clearable : true"
      :data-dict-code="code"
      v-on="$listeners"
      @change="dictChange"
      @visible-change="visibleChange"
    >
      <slot>
        <el-option
          v-for="(option, index) in optionsInfo"
          :key="index"
          v-bind="option"
          :disabled="option.disabled"
        />
      </slot>
    </el-select>
  </el-tooltip>

  <div v-else>
    <el-select
      v-if="emptyOptionCanCreat"
      :allow-create="!optionsInfo.length"
      v-bind="$attrs"
      :value="value"
      filterable
      :clearable="typeof $attrs.clearable !== 'undefined' ? $attrs.clearable : true"
      :data-dict-code="code"
      v-on="$listeners"
      @change="dictChange"
      @visible-change="visibleChange"
    >
      <slot>
        <el-option
          v-for="(option, index) in optionsInfo"
          :key="index"
          v-bind="option"
          :disabled="option.disabled"
        />
      </slot>
    </el-select>
    <el-select
      v-else
      v-bind="$attrs"
      :value="value"
      filterable
      :clearable="typeof $attrs.clearable !== 'undefined' ? $attrs.clearable : true"
      :data-dict-code="code"
      v-on="$listeners"
      @change="dictChange"
      @visible-change="visibleChange"
    >
      <slot>
        <el-option
          v-for="(option, index) in optionsInfo"
          :key="index"
          v-bind="option"
          :disabled="option.disabled"
        />
      </slot>
    </el-select>
  </div>
</template>

<script>
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { loadCustomSelect } from '@/library/utils/dict/dict-utils'
import { mapState } from 'vuex'

export default {
  name: 'CDictSelect',
  props: {
    value: {
      type: [String, Number, Boolean, Array]
    },
    code: {
      type: [String, Number, Object],
      default: function () {
        return ''
      }
    },
    customSelectType: {
      // 自定义选择类型，在dict-config中配置
      type: String,
      default: () => {
        return null
      }
    },
    transformOptions: {
      type: [Object, Function],
      default: () => {
        return null
      }
    },
    store: {
      type: Object,
      default: () => {
        return null
      }
    },
    lazyInit: {
      // 延迟初始化
      type: Boolean,
      default: () => {
        return false
      }
    },
    dictClass: {
      type: Object,
      default: () => {
        return null
      }
    },
    // 下拉需要过滤的项 根据条件判断入参
    filterItem: {
      type: Array,
      default: () => {
        return []
      }
    },
    emptyOptionCanCreat: {
      // 是否允许无下拉数据时手工创建
      type: Boolean,
      default: () => {
        return false
      }
    }
  },

  data () {
    return {
      options: [],
      visible: false,
      // 自定义接口返回类型开启缓存
      customTypeOpenCache: ['PROVINCE', 'CITY', 'ORG_TYPE_ALL', 'ELEMNAME', 'payExplain']
    }
  },

  computed: {
    ...mapState({
      // 箭头函数使代码更简练
      customDictMap: state => state.common_cache.customDictMap,
      customLockMap: state => state.common_cache.customLockMap,
      dictLockMap: state => state.common_cache.dictLockMap,
      selectDictMap: state => state.common_cache.selectDictMap
    }),
    optionsInfo () {
      if (this.visible) {
        for (let i = 0; i < this.options.length; i++) {
          if (this.options[i].disabled) {
            // eslint-disable-next-line vue/no-side-effects-in-computed-properties
            this.options.splice(i, 1)
            i--
          }
        }

        if (this.filterItem && this.filterItem.length > 0) {
          for (let i = 0; i < this.options.length; i++) {
            if (this.filterItem.includes(this.options[i].value)) {
            // eslint-disable-next-line vue/no-side-effects-in-computed-properties
              this.options.splice(i, 1)
              i--
            }
          }
        }
      }
      return this.options
    },

    showTooltip () {
      return !!(
        (this.$attrs.multiple || this.$attrs.multiple === '') &&
        (this.$attrs['collapse-tags'] || this.$attrs['collapse-tags'] === '') &&
        // (this.$attrs.disabled || this.$attrs.disabled === '') &&
        this.value &&
        Array.isArray(this.value)
      )
    },

    tooltipContent () {
      return this.showTooltip ? this.$getDictLabel(this.code, this.value, { multiple: true }) : '-'
    }
  },
  watch: {
    code: {
      handler (newCode, oldCode) {
        this.initOption('CODE')
      },
      deep: true,
      immediate: true
    },
    'dictClass.dictStore.dictStates': {
      handler (newCode, oldCode) {
        this.initOption('DICTIONARY')
      },
      deep: true,
      immediate: true
    },
    'store.dictStates': {
      handler (newCode, oldCode) {
        this.initOption('DICTIONARY')
      },
      deep: true,
      immediate: true
    },
    lazyInit () {
      this.initOption('INIT')
    },
    customLockMap: {
      handler (newCode, oldCode) {
        this.initOption('LOCK')
      },
      deep: true,
      immediate: true
    },
    dictLockMap: {
      handler (newCode, oldCode) {
        this.initOption('LOCK')
      },
      deep: true,
      immediate: true
    },
    optionsInfo: {
      handler (newCode, oldCode) {
        this.$forceUpdate()
      },
      deep: true
    }
  },

  mounted () {
    this.initOption('INIT')
  },

  methods: {
    visibleChange (visible) {
      this.visible = visible
    },
    dictChange (value) {
      if (value === this.value) {
        return
      }
      const dictItem = this.options.find(item => item.value === value) || {}
      this.$emit('change-value', value, dictItem)
    },
    getDictionary () {
      if (this.dictClass) {
        return this.dictClass.dictStore.dictStates
      }
      return this.store ? this.store.dictStates : null
    },
    initOption (initType) {
      const dictionary = this.getDictionary()
      if (this.lazyInit) {
        return
      }
      if (!this.code) {
        this.options = []
        return
      }
      if (this.customSelectType) {
        const currentCode = this.code + ''
        // 开启缓存
        const openCache = this.customTypeOpenCache.includes(this.customSelectType)
        if (openCache) {
          let hasValue = false
          if (this.customLockMap[this.customSelectType] &&
            this.customLockMap[this.customSelectType][currentCode] &&
            this.customLockMap[this.customSelectType][currentCode]['lock']) {
            return
          }
          if (this.customDictMap[this.customSelectType] &&
            this.customDictMap[this.customSelectType][currentCode] &&
            (this.customDictMap[this.customSelectType][currentCode] instanceof Array)) {
            this.options = this.customDictMap[this.customSelectType][currentCode]
            hasValue = true
          } else {
            this.$store.commit(STORE_COMMON_CACHE.SET_CUSTOM_LOCK,
              { type: this.customSelectType, code: currentCode, lock: true }
            )
          }
          if (!hasValue) {
            loadCustomSelect(this.customSelectType, currentCode, dictionaryArray => {
              this.options = dictionaryArray

              if (this.customTypeOpenCache.includes(this.customSelectType)) {
                this.$store.commit(STORE_COMMON_CACHE.SET_CUSTOM_DICT_MAP,
                  { type: this.customSelectType, code: currentCode, list: dictionaryArray }
                )
                this.$store.commit(STORE_COMMON_CACHE.SET_CUSTOM_LOCK,
                  { type: this.customSelectType, code: currentCode, lock: false }
                )
              }
            })
          }
        } else {
          if (initType === 'LOCK') {
            return
          }
          loadCustomSelect(this.customSelectType, currentCode, dictionaryArray => {
            this.options = dictionaryArray
          })
        }
      } else if (!this.store && !this.dictClass) {
        if (this.transformOptions && typeof this.transformOptions === 'function') {
          if (initType === 'LOCK') {
            return
          }
          this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL,
            { dictCode: this.code }
          ).then(data => {
            const options = data
            if (this.transformOptions && typeof this.transformOptions === 'function') {
              this.options = this.transformOptions(options)
            }
          })

          return
        }

        let hasValue = false
        if (this.dictLockMap[this.code] &&
          this.dictLockMap[this.code]['lock']) {
          return
        }
        if (this.selectDictMap[this.code] &&
          (this.selectDictMap[this.code] instanceof Array)) {
          this.options = this.selectDictMap[this.code]
          hasValue = true
        } else {
          this.$store.commit(STORE_COMMON_CACHE.SET_DICT_LOCK,
            { code: this.code, lock: true }
          )
        }

        if (!hasValue) {
          this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL,
            { dictCode: this.code }
          ).then(data => {
            const options = data
            let destOptions = []
            if (this.transformOptions && typeof this.transformOptions === 'function') {
              destOptions = this.transformOptions(options)
            } else {
              destOptions = options
            }
            this.options = destOptions

            this.$store.commit(STORE_COMMON_CACHE.SET_SELECT_DICT_MAP,
              { code: this.code, list: destOptions }
            )

            this.$store.commit(STORE_COMMON_CACHE.SET_DICT_LOCK,
              { code: this.code, lock: false }
            )
          })
        }
      } else if (initType === 'CODE' && this.dictClass && this.dictClass.customSelectType) {
        this.dictClass.loadCustomSelectType(this.code)
      } else {
        if (!dictionary) return []
        const options = dictionary[this.code]
        // console.log('[options]', store, this.code, options)
        if (this.transformOptions && typeof this.transformOptions === 'function') {
          this.options = this.transformOptions(options)
        } else {
          this.options = options
        }
      }
    }
  }
}
</script>

<style lang="scss">
.c-select-load-more {
  width: 100%;
  padding: 0 8px;

  .el-button {
    margin-left: 0;
  }
}

.c-select-no-more {
  padding: 8px 0;
  text-align: center;
  color: #cccccc;
}
</style>
