<template>
  <el-select
    :disabled="disabled"
    :value="value"
    :placeholder="$t('common.pleaseSelect')"
    @change="handleChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.label"
    />
  </el-select>
</template>
<script>
export default {
  name: 'CustomSelect',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    organizationId: {
      type: Number,
      default: null
    },
    value: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      options: []
    }
  },
  watch: {
    organizationId: {
      handler (n, o) {
        console.log(n, o)
        if (n && n !== o) {
          this.queryOptions()
        }
      },
      immediate: true
    }
  },
  methods: {
    handleChange (value) {
      this.$emit('change', value)
    },
    queryOptions () {
      if (this.organizationId) {
        const params = {
          organizationTypeCode: 'FAC',
          parentOrganizationId: this.organizationId,
          userId: this.$store.getters.user.userId
        }
        this.$http({
          url:
            '/api-base/organization/organization/getOrganizationByOrgCode',
          method: 'POST',
          data: params
        }).then(res => {
          this.options = res.data.map(
            ({ organizationId, organizationName }) => ({
              value: organizationId,
              label: organizationName
            })
          )
        })
      } else {
        this.options = []
      }
    }
  }
}
</script>
