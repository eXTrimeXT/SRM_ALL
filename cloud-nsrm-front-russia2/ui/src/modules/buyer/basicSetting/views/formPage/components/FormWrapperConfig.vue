<template>
  <div
    class="query-form-container-config form-info-config-page"
    :class="{ formExpand: isActive }"
  >
    <div class="btnGroup">
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
        type="text"
        :class="['toggleBtn', { btnExpand: isActive }]"

        @click="handleClick"
      >
        {{ taggleTex }}
        <em class="el-icon-arrow-down" />
      </el-button>
    </div>
    <el-form
      ref="formData"
      :model="formData"
      :label-width="formLabelWidth"
    >
      <el-row :gutter="32">
        <template v-for="(item, k) in formArray">
          <el-col
            :key="k"
            :span="item.colSpan ? item.colSpan : colNum"
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
                :multiple="item.multiple || false"
                :lazy-init="dictConfig.lazyInit"
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
                :placeholder="$t('components.selectMonth')"
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

              <organization-select-tree
                v-else-if="item.type === 'selectTree'"
                v-model="tempForm.fullPathId"
                :placeholder="item.placeholder"
                :scope="item"
                :multiple="item.mutiple || false"
                @select="organizationSelectTreeChange"
              />
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
                style="height: 32px;"
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
                :placeholder="$t('common.pleaseSelect')"
                :multiple="item.multiple"
                :collapse-tags="item.collapseTags"
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
          </el-col>
        </template>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import CCategorySelect from 'lib@/components/c-category-select'
import OrganizationSelector from 'lib@/components/organization-selector'
import { isNull } from '@/utils'
import DictSelect from 'lib@/components/c-select/dict-select'
import { STORE_COMMON_CACHE } from '@/config/store-config'

export default {
  name: 'FormWrapper',
  components: {
    QuickSearch,
    OrganizationSelectTree,
    CCategorySelect,
    OrganizationSelector,
    DictSelect
  },
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
      colNum: 24 / this.colLength,
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

    this.isActive = !!this.initActive
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
    query () {
      this.$refs['formData'].validate(valid => {
        if (valid) {
          // 查询--不要屏蔽清除空条件，否则有些没有做为空识别的接口请求会报错
          for (let i in this.formData) {
            if (isNull(this.formData[i])) {
              let isCheckbox = false
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

          this.$emit('getFormData', this.formData)
        } else {
          return false
        }
      })
    },
    onQuery () {
      // 查询
      for (let i in this.formData) {
        if (!this.formData[i]) delete this.formData[i]
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
    }
  }
}
</script>
<style scoped>
.query-form-container-config.form-info-config-page{
  border: 0;
  padding: 0;
}
.query-form-container-config.form-info-config-page .el-form{
  padding-top: 16px;
  margin: 0;
}
.vue-treeselect__label {
  font-size: 12px;
  font-weight: 400;
}
.form-info-config-page .el-form-item--small .el-form-item__content {
  height: 28px !important;
  line-height: 28px !important;
}
.form-info-config-page .el-form-item__label {
  padding-right: 8px;
  white-space: normal;
  line-height: 15px !important;
  vertical-align: middle;
  max-height: 30px;
  float: none !important;
  display: inline-block !important;
  width: 35% !important;
  /* 自适应用这两行 */
  /* max-width: 55%;
  width: auto !important; */
  box-sizing: border-box;
  overflow: hidden;
  text-overflow: ellipsis;
  /* display: -webkit-box; */
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
.form-info-config-page .el-form-item__content {
  vertical-align: middle;
  display: inline-block;
  margin-left: 0 !important;
  width: 65% !important;
  box-sizing: border-box;
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
</style>
<style scoped lang="scss">
.query-form-container-config {
  height: 62px;
  position: relative;
  overflow: hidden;
  .el-form {
    padding-right: 215px;
    margin-top: 0px;
  }
  .btnGroup {
    position: absolute;
    top: 16px;
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
      padding: 8px 6px;
      width: 58px;
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
  &.form-info-config-page {
    border-right: 0;
    border-left: 0;
    border-top: 0;
    flex: none;
    .hiddenCol{
      display: none;
    }
  }
}
.formExpand {
  height: auto !important;
}
.btnExpand i {
  transform: rotate(180deg);
}

</style>
