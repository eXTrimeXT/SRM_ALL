<template>
  <el-popover
    placement="top"
    width="400"
    trigger="click"
  >
    <el-table
      :data="data"
      border
      max-height="350px"
    >
      <el-table-column
        width="150"
        property="buName"
        :label="$t('bid_mod.bu')"
      />
      <el-table-column
        width="100"
        property="ouName"
        :label="$t('bid_mod.businessEntity')"
      />
      <el-table-column
        width="300"
        property="invName"
        :label="$t('bid_mod.inv')"
      />
    </el-table>

    <el-button
      slot="reference"
      type="text"
      @click="queryDetail"
    >
      {{ referenceText }}
    </el-button>
  </el-popover>
</template>

<script>
/**
 * 显示OU组详情
 */
export default {
  name: 'OuInfoPopover',
  props: {
    id: {
      type: [String, Number]
    },
    referenceText: {
      type: String
    }
  },
  data () {
    return {
      data: []
    }
  },
  methods: {
    queryDetail () {
      if (!this.id) return

      this.$http({
        url: '/api-base/base/base-ou-group/queryById',
        method: 'GET',
        params: { id: this.id },
        loading: true
      }).then(({ data }) => {
        this.data = data.details
      })
    }
  }
}
</script>
