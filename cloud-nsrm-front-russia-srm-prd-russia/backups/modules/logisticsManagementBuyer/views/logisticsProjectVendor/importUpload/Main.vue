<template>
  <div style="display: inline-block;margin-right: 10px;">
    <AuthorityButton
      :code="code"
      type="primary"
      :disabled="disabled"
      @click.prevent="clickHandle"
    >
      {{ title }}
    </AuthorityButton>
    <srm-dialog
      :visible.sync="show"
      size="middle"
      class="inportAbcd"
      :title="title"
      :append-to-body="true"
    >
      <el-steps
        :active="current"
        simple
        finish-status="success"
      >
        <el-step
          :title="$t('components.eio.template')"
          icon="el-icon-download"
        />
        <!-- <el-step title="导入设置" icon="el-icon-setting"></el-step> -->
        <el-step
          :title="$t('components.eio.dataImport')"
          icon="el-icon-upload2"
        />
        <el-step
          :title="$t('logisticsMod.excelField')"
          icon="el-icon-upload2"
        />
        <el-step
          :title="$t('components.eio.resultConfirm')"
          icon="el-icon-files"
        />
      </el-steps>

      <div
        v-if="current === 0"
        class="step-container"
      >
        <div v-if="isDownLoadTemp">
          <p
            class="form-item-tip"
            style="display: inline-block"
          >
            {{ $t("components.eio.msgFillInTemp") }}
          </p>
          <span
            style="color:#61b5ef;margin-left: 20px;cursor:pointer"
            @click="downloadTemplate"
          >{{ $t("components.eio.template") }}</span>
        </div>
        <div v-else>
          {{ $t("components.eio.msgNextStepImp") }}
        </div>
      </div>
      <!-- <div v-else-if="current === 1" class="step-container">
      </div> -->
      <div
        v-else-if="current === 1"
        class="step-container"
      >
        <el-progress
          v-if="showProgress && progressFlag"
          :text-inside="true"
          :stroke-width="14"
          :percentage="progressPercent"
          class="progress"
        />
        <el-upload
          v-else
          ref="upload"
          class="order-uploader"
          :with-credentials="true"
          :show-file-list="false"
          :headers="headers"
          :data="extraData"
          :action="upLoadUrl"
          :http-request="httpRequest"
          :on-error="handleError"
          :on-success="handleSuccess"
          :before-upload="beforeUpload2"
          :on-progress="onProgress"
        >
          <el-button
            slot="trigger"
            type="ghost"
            icon="el-icon-upload"
          >
            {{ $t("components.eio.fileUpload") }}
          </el-button>
        </el-upload>
      </div>
      <div
        v-else-if="current === 2"
        class="step-container"
      >
        <el-table
          :data="fieldTable"
          style="width: 100%"
          border
          highlight-current-row
        >
          <!-- 供方可操作字段 -->
          <el-table-column
            align="center"
            prop="label"
            :label="$t('logisticsMod.supplierOperableField')"
            :show-overflow-tooltip="true"
          />
          <!-- 对应导入excel的列标题 -->
          <el-table-column
            align="center"
            prop="value"
            :label="$t('logisticsMod.importExcelColumn')"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-select v-model="scope.row.value">
                <el-option
                  v-for="item in titles"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div
        v-else-if="current === 3"
        class="step-container"
      >
        <div class="form-item-tip">
          <h3 v-if="hasErrorFile">
            {{ $t("components.eio.msgImportOver")
            }}<span
              style="cursor:pointer;color:red"
              @click="downInportResult"
            >{{ $t("components.eio.headers.attachmentId") }}</span>{{ $t("components.eio.viewImportResult") }}
          </h3>
        </div>
      </div>
      <div slot="footer">
        <el-button
          v-if="current > 0 && current < 3"
          @click="prev"
        >
          {{
            $t("common.prevOne")
          }}
        </el-button>
        <el-button
          v-if="current < 3"
          type="primary"
          @click="next"
        >
          {{
            $t("common.nextOne")
          }}
        </el-button>
        <el-button
          v-else
          type="success"
          @click="complete"
        >
          {{
            $t("common.finish")
          }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import { getToken } from '@/utils/auth'
import { downloadWithParam } from 'lib@/utils/file'
import { FILE_DOWNLOAD } from '@/api/common'

export default {
  name: 'MImport',
  props: {
    upLoadUrl: {
      // 下载模板路径
      type: String
    },
    confirmLoadUrl: {
      // 下载模板路径
      type: String
    },
    code: {
      type: String
    },
    title: {
      type: String
    },
    extraData: {
      type: Object,
      default: function () {
        return null
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    showProgress: {
      type: Boolean,
      default: true
    },
    isDownLoadTemp: {
      type: Boolean,
      default: true
    },
    timeout: {
      type: Number,
      default: 35000
    }
  },
  data () {
    return {
      current: 0,
      fieldTable: [],
      titles: [],
      headers: {},
      hasErrorFile: false,
      errorFile: {},
      show: false,
      progressPercent: 0,
      progressFlag: false,
      instance: null,
      vendorId: null,
      file: null
    }
  },
  created () {
    this.headers = {
      Authorization: `Bearer ${getToken()}`
      // contentType: 'form-data',
    }
    this.vendorId = this.$store.getters.userInfo.companyId
  },
  methods: {
    beforeUpload2 (file) {
      this.instance = this.$pageLoading.open()
    },
    // 覆盖el-upload上传方式
    httpRequest (fileObj) {
      let headers = fileObj.headers
      this.file = fileObj.file
      const formData = new FormData()
      formData.append('file', fileObj.file)
      formData.append('bidingId', this.extraData.bidingId)
      this.$http({
        headers: {
          ...headers,
          'Content-Type': 'multipart/form-data'
        },
        timeout: this.timeout,
        url: this.upLoadUrl,
        method: 'post',
        data: formData,
        onUploadProgress: progressEvent => {
          const complete =
            ((progressEvent.loaded / progressEvent.total) * 100) | 0
          fileObj.onProgress({ percent: complete })
        }
      })
        .then(res => {
          // this.handleSuccessNew(res.data);
          this.instance.close()
          let fieldCodeName = res.data.fieldCodeName
          this.fieldTable = Object.keys(fieldCodeName).map(i => ({
            key: i,
            label: fieldCodeName[i],
            value: null
          }))
          this.titles = res.data.titles
          this.current++
        })
        .catch(error => {
          console.log(error)
          this.handleError(error)
        })
    },
    onProgress (event) {
      this.progressPercent = Math.abs(event.percent.toFixed(0))
      if (event.percent !== 100) {
        this.progressFlag = true
      } else {
        this.progressFlag = false
      }
    },
    downloadTemplate: function () {
      this.$emit('downloadTemplate')
    },
    handleSuccessNew ({ code, message, data }) {
      this.instance.close()
      const isError = code !== '0'
      const resStatus = data.status || 'Y' // 供应商改善|绩效考核导入 失败返回的状态信息
      const mesByResStatus = data.message || message
      this.$message({
        type: isError || resStatus === 'N' ? 'error' : 'success',
        message: isError ? message : mesByResStatus
      })
      if (resStatus === 'N') {
        try {
          if (data.fileuploadId) {
            this.hasErrorFile = true
            this.errorFile.fileuploadId = data.fileuploadId
            this.errorFile.fileSourceName = data.fileName
            this.current++
          }
        } catch (e) {}
      } else {
        this.show = false
      }
      this.$emit('handleSuccess', { code, message, data })
    },
    handleSuccess ({ code, message, data }, file, fileList) {
      this.instance.close()
      const isError = code !== '0'
      const resStatus = data.status || 'Y' // 供应商改善|绩效考核导入 失败返回的状态信息
      // const mesByResStatus = data.message
      const successMessage = this.$t('components.eio.importSuccess')
      const errorMessage = message
      this.$message({
        type: isError || resStatus === 'N' ? 'error' : 'success',
        message: isError || resStatus === 'N' ? errorMessage : successMessage
      })
      if (isError) {
        this.$refs['upload'].clearFiles()
        this.$emit('iModalClose')
        try {
          const errorMsg = JSON.parse(message)
          if (errorMsg.fileuploadId) {
            this.hasErrorFile = true
            this.errorFile = errorMsg
            this.current++
          }
        } catch (e) {}
        // this.current--;
        // TODO: 如果是有错误文件 则显示第三步 让用户点击下载错误文件 hasErrorFile
      } else {
        if (resStatus === 'N') {
          // 导入失败 // 供应商改善|绩效考核导入
          this.$refs['upload'].clearFiles()
          this.$emit('iModalClose')
          try {
            if (data.fileuploadId) {
              this.hasErrorFile = true
              this.errorFile.fileuploadId = data.fileuploadId
              this.errorFile.fileSourceName = data.fileName
              this.current++
            }
          } catch (e) {}
        } else {
          this.show = false
        }
      }
      this.$emit('handleSuccess', { code, message, data })
    },
    complete () {
      this.current = 0
      this.show = false
    },
    handleError () {
      this.$message({
        type: 'error',
        message: this.$t('components.eio.msgUploadFail')
      })
    },
    clickHandle () {
      this.$emit('beforeUpload') // 点击之前的数据数据
      this.show = true
    },
    downInportResult () {
      const { fileuploadId, fileSourceName } = this.errorFile
      if (fileuploadId) console.log('errorFile', this.errorFile)
      downloadWithParam(
        fileuploadId,
        fileSourceName
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
      this.$emit('downLoadResult', this.errorFile)
    },
    handleComplete () {
      // this.show = false;
      // this.current = 0;
      const formData = new FormData()
      let param = {}
      this.fieldTable.map(i => {
        param[i.key] = i.value
      })
      formData.append('file', this.file)
      formData.append('bidingId', this.extraData.bidingId)
      formData.append('param', JSON.stringify(param))
      formData.append('vendorId', this.vendorId)
      this.$http({
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        timeout: this.timeout,
        url: this.confirmLoadUrl,
        method: 'post',
        data: formData
      })
        .then(res => {
          this.handleSuccessNew(res)
        })
        .catch(error => {
          console.log(error)
        })
      // this.$emit("complete");
    },
    prev () {
      this.current--
    },
    next () {
      if (this.current == 2) {
        this.handleComplete()
      }
      if (this.current < 1) {
        this.current++
      }
    }
  }
}
</script>
<style lang="scss" scoped="scoped">
.step-header {
  width: 310px;
  padding: 0 10px 0 30px;
  position: relative;
  overflow: hidden;
  margin: auto;

  ul {
    padding: 0;
    width: 375px;
    float: left;
    position: relative;

    li {
      list-style-type: none;
      float: left;
      position: relative;
      width: 125px;

      &.active {
        span {
          background: #2d8cf0;
        }

        p {
          color: #333333;
        }
      }

      p {
        font-weight: bold;
        margin-top: 5px;
        font-size: 13px;
        color: #999999;
        float: left;
        position: relative;
        left: -50%;
        text-align: center;
        width: 125px;
      }

      span {
        position: relative;
        height: 20px;
        width: 20px;
        border-radius: 10px;
        background: #ccc;
        left: -10px;
        display: block;
      }
    }
  }
}

.step-bar {
  width: 250px;
  height: 13px;
  background: #ccc;
  position: absolute;
  border-radius: 12px;
  top: 4px;
  overflow: hidden;
}

.step-bar-active {
  width: 0px;
  height: 13px;
  background: #2d8cf0;
}
.step-container {
  padding: 8px 15px;
}
</style>
<style>
.inportAbcd .m-modal-body {
  padding: 10px 30px 20px 30px;
}
.inportAbcd .m-modal-footer {
  padding: 0px 30px 30px 30px;
  text-align: right;
}
</style>
