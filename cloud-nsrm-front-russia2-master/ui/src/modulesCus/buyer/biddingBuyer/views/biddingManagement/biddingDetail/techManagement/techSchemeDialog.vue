<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewTechScheme')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!-- 投标文件 -->
    <div style="margin-bottom: 12px; font-weight: bold">
      {{ $t('cusEntry.bidMod.bidFile') }}
      <el-button
        type="primary"
        style="margin-left: 10px;"
        @click="downLoadTechFile"
      >
        {{ $t('common.download') }}
      </el-button>
    </div>
    <el-table
      border
      :data="orderFileList"
      max-height="200"
      style="width: 100%"
      highlight-current-row
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
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="fileType"
        :label="$t('dataConfMod.fileExportType')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
        show-overflow-tooltip
      />
      <!-- 包名 -->
      <!-- <el-table-column
        v-if="mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="120"
        show-overflow-tooltip
      /> -->
      <!-- 标段 -->
      <el-table-column
        v-if="orderFileList.some(item => !!item.extBidSection)"
        align="center"
        prop="extBidSection"
        :label="$t('cusEntry.bidMod.extBidSection')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="orderDocId"
        :label="$t('dataConfMod.attachmentName')"
        min-width="180"
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
        prop="orderRemark"
        :label="$t('common.remark')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 签署状态 -->
      <!-- <el-table-column
        align="center"
        prop="extSignStatus"
        :label="$t('cusEntry.bidMod.signStatus')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
        min-width="120"
        show-overflow-tooltip
      /> -->
    </el-table>

    <!-- 脱敏文件 -->
    <!-- <div style="margin: 24px 0 12px 0; font-weight: bold">
      {{ $t('cusEntry.bidMod.desensitizationFile') }}
    </div>
    <el-table
      border
      :data="secretFileList"
      max-height="200"
      style="width: 100%"
      highlight-current-row
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
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="orderDocId"
        :label="$t('cusEntry.bidMod.desensitizationFile')"
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
    </el-table> -->

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 查看技术方案
 */
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'TechSchemeDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    mergeFlag: {
      type: Boolean,
      default: false
    },
    extProjectNo: {
      type: String,
      default: ''
    }
  },

  data () {
    return {
      orderFileList: [],
      secretFileList: []
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
    this.getTechScheme()
  },

  methods: {
    downLoadTechFile () {
      // 文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。
      this.$confirm(this.$t('cusEntry.supplement20250121.promptTips6'), this.$t('common.tips'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLink(
        `/api-file/bid/techfile/downloadTechFile?projectId=${this.projectId}`,
        // 招标项目[${this.extProjectNo}]技术方案.zip
        this.$t('cusEntry.supplement20250205.recognized7', { extProjectNo: this.extProjectNo })
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    /* 查询数据 */
    getTechScheme () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getTechPlan?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.orderFileList = res.data.orderFileList
          this.secretFileList = res.data.secretFileList
        }
      })
    }
  }
}
</script>
