<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewIpAddress1')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      :data="ipAddressList"
      border
      height="400"
      style="width: 100%"
      highlight-current-row
      :cell-style="cellStyle"
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="supplierCode"
        :label="$t('bidMod.vendorCode')"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="supplierName"
        :label="$t('bidMod.vendorName')"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="ip"
        :label="$t('bidMod.ipAddress')"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="monitorTime"
        :label="$t('cusEntry.biddingSettings.monitorTime')"
        min-width="100"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        align="center"
        prop="source"
        :label="$t('cusEntry.biddingSettings.monitorSource')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_IP_ADDRESS_SOURCE', cellValue)"
        min-width="100"
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 查看IP地址
 */
export default {
  name: 'IpAddressDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    ipAddressList: {
      type: Array,
      required: true
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  methods: {
    // 设置IP地址红色 (不同的供应商 && 相同的ip)
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      if (row.sameIpFlag == 'Y' && column.property == 'ip') {
        return {
          // 'font-weight': 'bold',
          'color': '#FF4A4D'
        }
      }
    }
  }
}
</script>
