<template>
  <el-date-picker
    ref="datePicker"
    :value="value"
    :type="type"
    :placeholder="placeholderObj.placeholder"
    :start-placeholder="placeholderObj.startPlaceholder"
    :end-placeholder="placeholderObj.endPlaceholder"
    v-bind="$attrs"
    v-on="$listeners"
    @input="handleInput"
  />
</template>

<script>
const PLACEHOLDER_MAP = {
  datetimerange: () => this.$t('components.time'), // 时间
  daterange: () => this.$t('components.date'), // 日期
  monthrange: () => this.$t('components.month') // 月份
}

export default {
  name: 'CDatePicker',
  inject: {
    cQueryTable: {
      default: ''
    }
  },
  props: {
    value: {
      type: [Date, Array, String]
    },
    type: {
      type: String
    },
    name: {
      type: [String, Array]
    },
    placeholder: {
      type: String,
      default: ''
    },
    startPlaceholder: {
      type: String,
      default () {
        return ''
      }
    },
    endPlaceholder: {
      type: String,
      default () {
        return ''
      }
    }
  },
  computed: {
    placeholderObj () {
      return {
        placeholder: this.placeholder || this.$t('components.approvalHead.headers.selectNode'),
        startPlaceholder: this.startPlaceholder || this.$t('common.start') + `${PLACEHOLDER_MAP[this.type]}`,
        endPlaceholder: this.endPlaceholder || this.$t('common.end') + `${PLACEHOLDER_MAP[this.type]}`
      }
    }
  },
  methods: {
    handleInput (value) {
      this.updateQueryTableParams(value)
      this.$emit('input', value)
    },
    updateQueryTableParams (value) {
      if (this.cQueryTable) {
        if (
          this.type.indexOf('range') > -1 &&
          this.name &&
          Array.isArray(this.name)
        ) {
          this.name.forEach((nameItem, index) => {
            this.cQueryTable.updateFormModel(
              nameItem,
              value ? value[index] : ''
            )
          })
        } else {
          this.cQueryTable.updateFormModel(this.name, value)
        }
      }
    },
    focus () {
      this.$refs.datePicker.focus()
    }
  }
}
</script>
