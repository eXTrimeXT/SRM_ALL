<template>
  <div>
    <div style="margin: 10px 0">
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
      <el-button
        type="primary"
        :disabled="archiveFileList.length==0"
        @click="saveArchiveFile"
      >
        {{ $t('common.save') }}
      </el-button>
      <AuthorityButton
        type="primary"
        code="bid:archive:download"
        :disabled="isDownload"
        @click="downloadArchive"
      >
        下载归档文件
      </AuthorityButton>
    </div>

    <el-table
      border
      style="width: 100%"
      :data="archiveFileList"
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
        prop="souDocId"
        :label="$t('bidMod.attachmentName')"
        min-width="180"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.souDocId,
              fileName: scope.row.souFileName
            }"
            :extra-data="fileInfo"
            :readonly="readonly"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
        min-width="180"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.souRemark" />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="100"
      >
        <template slot-scope="scope">
          <el-button type="text" @click="deleteRow(scope.$index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 下载归档文件 -->
    <ArchivedFileDialog
      v-if="archivedFileDialogVisible"
      :visible.sync="archivedFileDialogVisible"
      :projectId="biddingBase.projectId"
    />
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { judgeManagement } from 'lib@/composition/biddingLts/utils'
import { downloadFileLink } from 'lib@/utils/file'
import ArchivedFileDialog from './bidArchive/archivedFileDialog'
export default {
  name: 'BidArchive',

  components: { ArchivedFileDialog },

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    createApprovalStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      archivedFileDialogVisible: false,
      archiveFileList: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'BID_FILE',
        fileType: 'images'
      },
      isDownload: false
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getArchiveFile()
        }
      },
      immediate: true
    }
  },

  methods: {
    addRow () {
      this.archiveFileList.push({
        souDocId: null,
        souFileName: null,
        souRemark: null
      })
    },
    deleteRow (index) {
      this.archiveFileList.splice(index, 1)
    },
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.souDocId = fileId
      row.souFileName = fileName
    },
    saveArchiveFile (type) {
      const params = {
        // 是否是暂存
        tempSave: type !== 'nextOne',
        projectId: this.biddingBase.projectId,
        archiveFileList: this.archiveFileList
      }
      bidBuyerHttp.archive.saveArchiveFile(params).then(res => {
        if (res && res.data) {
          this.$message.success(this.$t('common.success'))
          if (type !== 'nextOne') {
            // 更新列表
            this.getArchiveFile()
          } else {
            // 提交审批成功回调-关闭单据
            this.$emit('after-submit')
          }
        }
      })
    },
    /* 查询数据 */
    async getArchiveFile () {
      if (!this.biddingBase.projectId) {
        return
      }
      bidBuyerHttp.archive.getArchiveFile(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          this.archiveFileList = res.data.archiveFileList
        }
      })
    },
    // 下载文件
    downloadArchive () {
      this.isDownload = true
      downloadFileLink(
        `/api-file/bid/archivist?projectId=${this.biddingBase.projectId}`
      ).then(() => {
        this.isDownload = false
      }).catch(() => {
        this.isDownload = false
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
