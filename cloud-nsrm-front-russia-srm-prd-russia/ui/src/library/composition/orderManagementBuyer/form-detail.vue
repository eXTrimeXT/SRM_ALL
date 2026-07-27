<template>
  <div class="form-container list-page-query">
    <!-- <el-form ref="formData" :model="formData" :label-width="formLabelWidth"> -->
    <srm-row :gutter="32">
      <template v-for="(item, k) in formArray">
        <srm-col
          :key="k"
          :initCol="item.colLength"
          :class="[
            {
              hiddenCol:
                item.hidden && typeof item.hidden === 'function' ? item.hidden() : item.hidden,
            },
          ]"
        >
          <el-form-item
            :label="typeof item.label === 'function' ? item.label() : item.label"
            :label-width="formLabelWidth"
            :rules="item.rules"
            :prop="item.prop"
          >
            <template v-if="item.type === 'slot' && item.slot">
              <slot :name="item.slot" :scope="formData" />
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
            <DictSelect
              v-else-if="item.type === 'dict'"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              :code="item.code"
              :multiple="item.multiple || false"
              :lazy-init="dictConfig.lazyInit"
              @change-value="getDictCallback([...arguments, item.prop])"
            />
            <DictSelect
              v-else-if="item.type === 'custom-dict'"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              :code="item.code"
              :multiple="item.multiple || false"
              :custom-select-type="item.customSelectType"
            />
            <el-select
              v-else-if="item.type === 'select' && typeof item.options === 'function'"
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
              value-format="yyyy-MM-dd"
              :placeholder="$t('vendorMod.datePicker')"
            />
            <!-- 选择日期 -->
            <el-date-picker
              v-else-if="item.type === 'datetime'"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              type="datetime"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('vendorMod.datePicker')"
            />
            <el-date-picker
              v-else-if="item.type === 'daterange'"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              type="daterange"
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
              format="yyyy-MM"
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
            <el-input-number
              v-else-if="item.type === 'precision'"
              v-model="formData[item.prop]"
              :precision="item.toFixed"
              :disabled="item.disabled"
              :controls="item.controls"
              class="input-number-precision"
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
              <span ref="bindKey" style="display: none">
                {{ item.showKey }}
              </span>
              <QuickSearch
                ref="quickSearchTool"
                :show-key="item.showKey"
                :show-input="formData[item.prop]"
                :scope-data="item"
                :name="item.name"
                :pre-query-data="item.preQueryData"
                :disabled="item.disabled"
                @close-quicksearch="getQuick"
              />
            </template>

            <OrganizationSelectTree
              v-else-if="item.type === 'selectTree'"
              v-model="tempForm.fullPathId"
              :placeholder="item.placeholder"
              :scope="item"
              :multiple="item.mutiple || false"
              @select="organizationSelectTreeChange"
            />
            <CCategorySelect
              v-else-if="item.type === 'catSelect'"
              ref="categorySelect"
              v-model="formData[item.prop]"
              :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
              :show-key="item.showKey"
              :data-source="item.dataSource"
              @select="callback(item, $event)"
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
            <OrganizationSelector
              v-else-if="item.type === 'OUorganizationSelector'"
              ref="ouSelector"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              :parent-id="-1"
              node-type="OU"
              :placeholder="$t('common.pleaseSelect')"
              :multiple="item.multiple"
              :collapse-tags="item.collapseTags"
              @select="callback(item, $event)"
            />
            <OrganizationSelector
              v-else-if="item.type === 'INVorganizationSelector'"
              ref="invSelector"
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              :placeholder="$t('common.pleaseSelect')"
              :parent-id="item.parentId ? formData[item.parentId] : formData['orgId'] || -1"
              node-type="INV"
              :multiple="item.multiple"
              :collapse-tags="item.collapseTags"
              :auto-select-when-one-item="item.autoSelectWhenOneItem"
              @select="callback(item, $event)"
            />
            <el-input
              v-else
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              clearable
              v-bind="item.attrs"
              @keyup.enter.native="onQuery"
            />
          </el-form-item>
        </srm-col>
      </template>
    </srm-row>
    <!-- </el-form> -->

    <srm-dialog
      title="格式化本文"
      size="middle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-input v-model="globalText" type="textarea" :rows="12" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="formattorOne">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import CCategorySelect from 'lib@/components/c-category-select'
import OrganizationSelector from 'lib@/components/organization-selector'
import DictSelect from 'lib@/components/c-select/dict-select'
import { STORE_COMMON_CACHE } from '@/config/store-config'

export default {
  name: 'FormDetail',
  components: {
    QuickSearch,
    OrganizationSelectTree,
    CCategorySelect,
    OrganizationSelector,
    DictSelect
  },
  props: {
    formData: {
      type: Object,
      default () {
        return {}
      }
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
      default: 4
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
    }
  },
  data () {
    return {
      tempForm: {
        fullPathId: null
      },
      // formData: {},
      // formLabelWidth: '100px',
      dialogFormVisible: false,
      globalProp: null,
      globalText: null,
      dictConfig: {
        lazyInit: true
      }
    }
  },
  watch: {
    formData: {
      handler (data) {
        this.$emit('update:PFormData', data)
      },
      immediate: true,
      deep: true
    }
  },
  updated () {
    this.$emit('synchronous-value', this.formData)
  },
  async created () {
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
  methods: {
    setValue (key, value) {
      this.$set(this.formData, key, value)
    },
    organizationSelectTreeChange (node, value, item) {
      this.formData[item.prop] = node.organizationId
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
          this.formData[key] = this.formData[key].trim()
        }
      }
      this.$emit('getFormData', this.formData)
    },
    getDictCallback (arg) {
      const [value, dictItem, key] = arg
      this.formArray.forEach(item => {
        if (item.prop === key && item.type === 'dict' && item.callback) {
          item.callback(value, dictItem)
        }
      })
    },
    getQuick (val, scope) {
      if (scope.propKey) {
        this.formData[scope.prop] = val ? val[scope.propKey] : ''
      } else {
        this.formData[scope.prop] = val ? val[scope.showKey] : ''
      }
      scope.callback && scope.callback(val, scope)
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
<style scoped lang="scss">
.list-page-query {
  padding: 0;
  :deep(.el-form-item__label) {
    width: auto !important;
  }
  :deep(.el-form-item__content) {
    width: 100% !important;
  }
}
.the_el_input_group__append {
  padding: 8px 0px;
  min-width: 20px;
  text-align: center;
}
.el-form {
  margin-top: -6px;
}
.el-form .el-row .el-form-item:first-child {
  margin-bottom: 10px;
}
.list-page-query {
  padding-top: 0;
  border-right: 0;
  border-left: 0;
  // margin-bottom: 10px;
  border-top: 0;
  flex: none;
  .hiddenCol {
    display: none;
  }
}
.btnExpand i {
  transform: rotate(180deg);
}
.input-number-precision {
  width:100%;
  :deep(.el-input__inner) {
    text-align:left;
    padding-left: 8px;
  }
}
</style>
