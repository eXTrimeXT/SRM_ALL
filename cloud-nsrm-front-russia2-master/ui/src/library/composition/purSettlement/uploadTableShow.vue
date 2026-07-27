<template>
  <div class="uploadTableDialog">
    <el-upload
      ref="upload"
      class="order-uploader"
      multiple
      :accept="accepts"
      :with-credentials="true"
      :show-file-list="false"
      :headers="headers"
      :data="extraData"
      :action="url"
      :on-error="handleError"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
      :on-progress="onProgress"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <el-button slot="trigger" type="primary" style="margin-bottom: 8px;">
        {{ headerText }}
      </el-button>
    </el-upload>

    <el-table
      :data="tableData"
      style="width: 100%"
      border
      height="250px"
      highlight-current-row
      @selection-change="selectionChange"
    >
      <!-- <el-table-column type="selection" width="55" /> -->
      <!-- 序号 -->
      <el-table-column align="center" type="index" :label="$t('common.sort')" width="60" />
      <!-- 文件名 -->
      <el-table-column
        align="center"
        prop="fileName"
        :label="$t('components.fileupload.name')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <div class="file-name-scope">
            <span>
              {{ scope.row.fileName }}
            </span>
            <span
              v-if="scope.row.isFinesh === 'yes' || scope.row.percentage === 100"
              class="file-check"
            >
              <i
                v-if="scope.row.status === 'success'"
                class="el-icon-circle-check file-check-success"
              />
              <i v-else class="el-icon-circle-close file-check-error" @click="deleteOneContent(scope.$index, scope.row)" />
            </span>
          </div>
          <el-progress
            v-if="scope.row.isFinesh === 'no' || scope.row.status !== 'success'"
            :show-text="false"
            :stroke-width="8"
            :percentage="scope.row.percentage"
            :color="scope.row.color"
            style="margin-bottom: 5px"
          />
        </template>
      </el-table-column>
      <!-- 文件大小 -->
      <el-table-column
        align="center"
        prop="fileSize"
        :label="$t('components.fileupload.size')"
        show-overflow-tooltip
      />
      <!-- 上传状态 -->
      <el-table-column align="center" prop="fileStatus" :label="$t('purSettlementMod.fileStatus')" show-overflow-tooltip />
      <!-- 操作 -->
      <el-table-column :label="$t('common.operation')" width="60" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="deleteOneContent(scope.$index, scope.row)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
export default {
  name: 'UploadTableShow',
  props: {
    headerText: {
      type: String,
      default () {
        return this.$t('purSettlementMod.upload') // 上传
      }
    },
    extraData: {
      type: Object,
      default () {
        return {}
      }
    },
    url: {
      type: String,
      default () {
        return ''
      }
    },
    accept: {
      type: Array,
      default () {
        return []
      }
    },
    tableData: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      isSuccess: false,
      headers: {},
      accepts: '',
      colors: {
        error: '#F56C6C',
        ok: '#409EFF'
      }
    }
  },
  created () {
    this.headers = {
      Authorization: `Bearer ${getToken()}`,
      // 获取本机ip
      ProxyClientIP: this.$store.getters.ip,
      'x-forwarded-for': this.$store.getters.ip
      // contentType: 'form-data',
    }

    this.accepts = `.${this.accept.join(', .')}`
  },
  methods: {
    beforeUpload (file) {
      console.log(file, 'file')
      let mes = ''
      let accept = file.type.split('/')[1]
      if (!this.accept.includes(accept)) {
        mes = this.$t('purSettlementMod.uploadTableMes1') // 发票仅支持JPG、PNG、JPEG格式!
      }
      if (!(file.size / 1024 / 1024 < 60)) {
        mes = this.$t('purSettlementMod.uploadTableMes2') // 上传文件大小不能超过 1MB!
      }
      this.tableData.push({
        uid: file.uid,
        fileName: file.name,
        fileSize: file.size,
        size: Math.ceil(file.size / 1024).toFixed(0) + 'KB',
        fileStatus: mes || this.$t('purSettlementMod.uploading'), // 上传中
        status: file.status,
        mesType: mes ? 'error' : 'ok',
        isFinesh: 'no',
        color: mes ? this.colors.error : this.colors.ok,
        percentage: 0
      })
    },
    onProgress (event, file, fileList) {
      const row = this.getRowFile(file)
      // 判断是否有格式错误，有错误随机一个1-15之间的进度条，否则正常加载
      if (row.mesType === 'ok') {
        if (event.percent.toFixed(0) > 88) {
          let num = 88 + Math.random() * 8
          row.percentage = num.toFixed(1)
        }
      } else {
        let num = Math.random() * 15 + 1
        row.percentage = num.toFixed(1)
      }
    },
    handleSuccess (res, file, fileList) {
      const row = this.getRowFile(file)
      if (res.code === '0') {
        // beforeUpload未检测到大小格式问题，且后台返回0状态码
        if (row.mesType === 'ok') {
          Object.assign(row, {
            ...res.data,
            code: '0',
            percentage: file.percentage,
            fileStatus: res.code === '0' ? this.$t('purSettlementMod.uploadedSuccessfully') : this.$t('purSettlementMod.uploadFailed'),
            status: file.status,
            fileSize: file.size,
            size: Math.ceil(file.size / 1024).toFixed(0) + 'KB'
          })
        }
      } else {
        // beforeUpload未检测到大小格式问题，后台返回异常状态
        if (row.mesType === 'ok') {
          row.fileStatus = res.message || this.$t('purSettlementMod.uploadFailed')
        }
        row.status = 'error'
        row.color = this.colors.error
      }
      row.isFinesh = 'yes'
      console.log(file, row, 'handleSuccess')
      this.$emit('handleFinish', row)
    },
    handleError (mes, file, fileList) {
      const row = this.getRowFile(file)
      Object.assign(row, {
        percentage: file.percentage,
        fileStatus: this.$t('purSettlementMod.uploadFailed'),
        status: file.status,
        color: this.colors.error
      })
    },
    // 获取上传文件所在行
    getRowFile (file) {
      let uids = this.tableData.map(o => o.uid)
      let i = uids.indexOf(file.uid)
      return this.tableData[i]
    },
    // 行选择删除
    deleteOneContent (index, row) {
      this.tableData.splice(index, 1)

      let list = this.$refs.upload.uploadFiles
      list.forEach((item, i) => {
        if (item.uid === row.uid) {
          this.$refs.upload.uploadFiles.splice(i, 1)
        }
      })
    },
    // 表格行数据选择选择
    selectionChange (select) {
      this.$emit('selection-change', select)
    }
  }
}
</script>

<style lang="scss" scoped>
.file-name-scope {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .file-check {
    font-size: 16px;
    .file-check-success {
      color: #67c23a;
    }
    .file-check-error {
      color: #f56c6c;
    }
  }
}
:deep(.el-progress-bar__innerText){
  font-size: 7px;
  margin: 0 5px 2px;
}
</style>
