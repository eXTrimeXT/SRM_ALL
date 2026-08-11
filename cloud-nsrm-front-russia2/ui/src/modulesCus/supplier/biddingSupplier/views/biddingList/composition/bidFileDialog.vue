<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('cusEntry.biddingSettings.viewBidFile')"
    size="middle"
    append-to-body
    :close-on-click-modal="false"
  >
  <p class="btn_line">
      <el-button
        type="primary"
         @click="downLoadBusFile"
      >
      {{ $t('cusEntry.common.batchDownload') }}
      </el-button>
    </p>
    <el-table
      border
      max-height="200"
      :data="bidFileData"
    >

      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="50"
      />
      <!--附件名称-->
      <SrmCommonFileBid
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.attachmentName'),
          prop: 'souDocId',
          nameProp: 'souFileName'
        }"
        readonly
      />
      <!-- <el-table-column
        v-if="baseInfo.mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        minWidth="100"
      /> -->
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
        minWidth="100"
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
/**
 * 供应商查看招标文件
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import SrmCommonFileBid from './srm-common-file'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'BidFileDialog',
  components: { SrmCommonFileBid },
  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: '',
          row: {}
        }
      }
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      bidFileData: [],
      isDeadline: false
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
        if (!val) {
          this.$bus.$off('downLoadHandle')
        }
      }
    }
  },
  created () {
    this.getBidFile()
  },
  mounted () {
    this.$bus.$on('downLoadHandle', () => {
      this.$http({
        url: `/api-sou/ext/vendor/bid/updateBidFileDownloadTime?projectId=${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(() => {})
    })
  },
  methods: {
    // 查看招标文件
    getBidFile () {
      this.$http({
        url: `/api-sou/ext/vendor/bid/getBidSouFileList?projectId=${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.bidFileData = res.data.fileList
        }
      })
    },
    // 下载商务附件
    // /api-file/bid/businessfile/downloadVendorBusinessFile?projectId=537040539456000&vendorId=535599696320128
    downLoadBusFile () {
      console.log(this.baseInfo.row.projectId,this.baseInfo.row.vendorId)
      // 文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。
      this.$confirm(this.$t('cusEntry.supplement20250121.promptTips6'), this.$t('common.tips'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLink(
        `/api-file/bid/businessfile/downloadVendorBusinessFile?projectId=${this.baseInfo.row.projectId}&vendorId=${this.baseInfo.row.vendorId}`,
        `this.$t('cusEntry.supplement20250314.bidProjectAttachment').zip`
        // this.$t('cusEntry.supplement20250121.bidTips13', { extProjectNo: this.biddingBase.extProjectNo })
      ).catch(res => {
        this.$message.error(res.message)
      })
    }
  },
  beforeDestory () {
    this.$bus.$off('downLoadHandle')
  }
}
</script>
<style lang="scss" scoped>
</style>
