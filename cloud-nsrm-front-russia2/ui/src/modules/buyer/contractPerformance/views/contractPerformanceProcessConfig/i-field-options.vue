<!-- 编辑字段配置 -->
<template>
  <div class="form-container">
    <el-form :model="attributes">
      <el-row :gutter="32">
        <el-col
          v-for="item in compOption"
          :key="item.id"
          :span="8"
        >
          <el-form-item
            :label="item.label"
            :prop="item.prop"
          >
            <el-select
              v-if="item.type === 'el-select'"
              v-model="attributes[item.prop]"
              :disabled="disabled"
            >
              <el-option
                v-for="i in item.options"
                :key="i.id"
                :label="i.label"
                :value="i.value"
              />
            </el-select>
            <el-input
              v-else-if="item.type === 'el-input'"
              v-model="attributes[item.prop]"
              :placeholder="item.placeholder"
              :disabled="disabled"
            />
            <el-switch
              v-else-if="item.type === 'el-switch'"
              v-model="attributes[item.prop]"
              class="switch-class"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import isNil from 'lodash/isNil'

export default {
  name: 'IFieldOptions',
  model: {
    event: 'change',
    prop: 'value'
  },
  props: {
    fieldType: {
      required: true,
      type: String
    },
    value: {
      type: String,
      default: '{}'
    },
    disabled: {
      type: Boolean
    }
  },
  data () {
    return {
      formatValue: {},
      attributes: {},
      componentOptions: {
        'el-input': [
          { label: this.$t('contract_mod.isCleared'), prop: 'clearable', type: 'el-switch', default: true, id: 0 },
          {
            label: this.$t('contract_mod.inputType'),
            options: [
              { id: 0, value: '', label: this.$t('contract_mod.normalDropDown') },
              { id: 1, value: 'textarea', label: this.$t('contract_mod.textField') }
            ],
            prop: 'type',
            type: 'el-select',
            default: '',
            id: 1
          }
        ],
        'el-select': [
          {
            id: 0,
            label: this.$t('contract_mod.dropDownOptions'),
            prop: 'options',
            type: 'el-input',
            default: '',
            placeholder: this.$t('contract_mod.separateMultipleOptions')
          },
          { id: 1, label: this.$t('contract_mod.isCleared'), prop: 'clearable', type: 'el-switch', default: true }
        ],
        'el-date-picker': [
          { id: 0, label: this.$t('contract_mod.isCleared'), prop: 'clearable', type: 'el-switch', default: true }
        ],
        'el-checkbox': [
          { id: 0, label: this.$t('contract_mod.valueSelected'), prop: 'true-label', type: 'el-input', default: 'Y' },
          { id: 0, label: this.$t('contract_mod.notSelectValue'), prop: 'false-label', type: 'el-input', default: 'N' }
        ]
      }
    }
  },
  computed: {
    compOption () {
      return this.componentOptions[this.fieldType]
    }
  },
  watch: {
    attributes: {
      deep: true,
      handler () {
        console.log(this.attributes, 'attributes')
        this.updateValue()
      }
    },

    value () {
      this.initData()
    }
  },
  created () {
    this.initData()
  },
  mounted () {},
  methods: {
    initData () {
      this.formatValue = JSON.parse(this.value || '{}')
      this.attributes = this.compOption.reduce((last, item) => {
        let defaultValue = item.default
        if (typeof item.default === 'function') {
          defaultValue = defaultValue()
        }
        const formatValue = this.formatValue[item.prop]
        last[item.prop] = isNil(formatValue) ? defaultValue : formatValue
        return last
      }, {})
    },
    updateValue () {
      this.formatValue = JSON.parse(JSON.stringify(this.attributes || {}))
      this.$emit('change', JSON.stringify(this.formatValue))
    }
  }
}
</script>
<style scoped lang="scss">
.switch-class {
  position: relative;
  display: inline-block;
  width: 100%;
}
</style>
