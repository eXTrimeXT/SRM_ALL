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
      {{ text }}
    </el-button>
  </el-popover>
</template>

<script>
export default {
  name: 'ShowOuDetail',
  props: ['ouId', 'text'],
  data () {
    return { data: [] }
  },
  methods: {
    queryDetail () {
      this.$http({
        url: '/api-base/base/base-ou-group/queryById',
        method: 'GET',
        params: { id: this.ouId },
        loading: true
      }).then(({ data }) => {
        this.data = data.details
      })
    }
  }
}
</script>
