<template>
  <!-- 查看商务文件 -->
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewBusinessFile')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      :data="fileList"
      border
      max-height="200"
      style="width: 100%"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="60"
      />
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="60"
      />
      <!-- 包名 -->
      <el-table-column
        v-if="mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="orderDocId"
        :label="$t('dataConfMod.attachmentName')"
        min-width="120"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.orderDocId,
              fileName: scope.row.orderFileName
            }"
            readonly
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="fileType"
        label="附件类型"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BUS_FILE_CONFIG_TYPE', cellValue)"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="orderRemark"
        :label="$t('common.remark')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 签署状态 -->
      <el-table-column
        align="center"
        prop="extSignStatus"
        :label="$t('cusEntry.bidMod.signStatus')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
        show-overflow-tooltip
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
 * 查看商务文件
 */
export default {
  name: 'BusinessFileDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    },
    mergeFlag: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      fileList: []
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

  created () {
    this.getBusOrderFile()
  },

  methods: {
    /* 查询数据 */
    getBusOrderFile () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getBusOrderFile?orderId=${this.editRow.orderId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.fileList = res.data
        }
      })
    }
  }
}
</script>
