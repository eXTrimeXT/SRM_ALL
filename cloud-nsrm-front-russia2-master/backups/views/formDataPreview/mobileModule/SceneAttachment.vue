/* * 供应商模块单据附件上传，模板配置相关 */
<template>
  <div class="sceneAttachment">
    <el-button
      v-if="!isHasConf && attOpt !== 'view'"
      type="primary"
      size="mini"
      class="attBtn"
      @click="addAttaches"
    >
      {{ $t("common.add") }}
    </el-button>
    <el-table
      ref="attachTable"
      :data="attachData"
      stripe
      border
      :row-class-name="rowClassName"
      style="width: 100%"
      max-height="250px"
      tooltip-effect="dark"
    >
      <el-table-column
        :label="$t('vendorMod.attachment')"
        min-width="150"
      >
        <template slot-scope="scope">
          <!-- <c-upload-file
            v-if="
              (fileModel === 'normal' && !scope.row.fileuploadId) ||
                (fileModel === 'change' &&
                  !scope.row.fileuploadId &&
                  (scope.row.opType === 'add' || scope.row.opType === 'update'))
            "
            :limit="1"
            :show-file-list="false"
            :disabled="scope.row.fileuploadId==''? true :false"
            :cus-data="upFileInfo"
            @upload-success="tableHandleUploadSuccess"
            @button-click="attachButtonClick(scope.$index)"
          /> -->
          <div class="download-link-wrap">
            <c-download-link
              :id="scope.row.fileuploadId"
              :name="scope.row.fileSourceName"
              ellipsis
              class="download-link-item"
            />
            <i
              v-if="
                (fileModel === 'normal' &&
                  scope.row.opType !== 'view' &&
                  attOpt !== 'view') ||
                  (fileModel === 'change' &&
                  attOpt !== 'view' &&
                  (scope.row.opType === 'add' ||
                  scope.row.opType === 'update'))
              "
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="filePureName"
        :label="$t('vendorMod.attachmentName')"
        min-width="120"
      >
        <template slot-scope="scope">
          <template v-if="scope.row.opType === 'add' && !isHasConf">
            <el-input
              v-model="scope.row.filePureName"
              :required="scope.row.required === 'Y'"
            />
          </template>
          <span
            v-else
          ><i
            v-if="scope.row.required === 'Y'"
            style="color:#ff4949;padding-right:4px;"
          >*</i>{{ scope.row.filePureName }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { sceneAttachmentConf } from 'mod@/basicSetting/api/baseSetting'
import { apiGetFileList, fileDel } from '@/api/common'
import { formatDate } from '@/utils'
import _cloneDeep from 'lodash/cloneDeep'
export default {
  name: 'SceneAttachment',
  components: {
    CUploadFile,
    CDownloadLink
  },
  filters: {
    formateDate: function (val) {
      return formatDate(val, 'Y-M-D')
    }
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    data: {
      type: Array,
      default () {
        return []
      }
    },
    senceCode: {
      // 场景编码
      type: String,
      default: () => ''
    },
    businessId: {
      // 业务单据Id
      type: [Number, String],
      default: () => null
    },
    changeData: {
      // 业务单据变更数据
      type: Array,
      default () {
        return []
      }
    },
    attOpt: {
      // 操作类型 add update delete
      type: String,
      default: 'add'
    },
    upFileInfo: {
      // 文件上传配置信息
      type: Object,
      default: () => {}
    },
    fileRefresh: {
      type: Boolean,
      default: () => false
    },
    fileModel: {
      // 附件模式 正常模式 | 变更模式 fileModel==='change'
      type: String,
      default: 'normal' // normal | change
    }
  },
  data () {
    return {
      opType: 'add', // 变更操作类型
      attachData: [], // 附件数据
      isHasConf: false, // 是否有配置魔板信息
      tempData: [], // 模板数据
      uploadData: [], // 已上传数据
      changeUploadData: [] // 变更的数据
    }
  },
  computed: {
    sceneAttaches () {
      let arr = []
      if (this.fileModel === 'change') {
        this.attachData.map(elm => {
          if (
            elm.fileuploadId &&
            (elm.opType === 'add' || elm.opType === 'update')
          ) {
            arr.push(elm)
          }
        })
      } else {
        this.attachData.map(elm => {
          if (elm.fileuploadId) {
            arr.push(elm)
          }
        })
      }
      return arr
    }
  },
  watch: {
    attOpt: {
      immediate: true,
      handler (attOpt) {
        this.resetData() // 重置数据
        if (attOpt === 'add') {
          // 新增
          this.fatchAttachConf() // 查询模板配置信息
        } else {
          // 编辑 查看
          this.fatchAttachConf() // 查询模板配置信息
          this.fatchUploadFiles() // 查询已上传的附件信息
        }
      }
    },
    fileRefresh: {
      immediate: true,
      handler (fileRefresh) {
        this.resetData() // 重置数据
        if (fileRefresh) {
          // 刷新值
          this.fatchAttachConf() // 查询模板配置信息
          this.fatchUploadFiles() // 查询已上传的附件信息
        }
      }
    },
    changeData: {
      immediate: true,
      handler (changeData) {
        if (changeData && changeData.length > 0) {
          // 变更数据
          this.fatchAttachConf() // 查询模板配置信息
          this.fatchUploadFiles() // 查询已上传的附件信息
        }
      }
    }
  },
  mounted () {
    this.opType = this.attOpt
  },
  methods: {
    // 重置数据
    resetData () {
      this.tempData = []
      this.uploadData = []
      this.changeUploadData = []
    },
    // 查询附件配置信息
    fatchAttachConf () {
      let parame = {
        senceCode: this.senceCode,
        enabled: 'Y'
      }
      sceneAttachmentConf(parame).then(res => {
        if (res) {
          let confData = res.data.list
          let confTempData = []
          if (this.fileModel === 'change') {
            // 变更模式
            this.opType = 'view'
          }
          if (confData.length > 0) {
            this.isHasConf = true
            confData.map(item => {
              confTempData.push({
                opType: this.opType,
                filePureName: item.attachmentName, // 模板名称
                sceneFileSourceName: item.fileSourceName, // 模板附件名称
                sceneFileUploadId: item.fileuploadId, // 模板附件Id
                required: item.required, // 是否必传
                fileuploadId: null,
                fileSourceName: '',
                expireTime: '',
                sceneAttachmentId: item.sceneAttachmentId // 场景ID
              })
            })
            this.tempData = confTempData // 模板数据
            if (this.attOpt === 'add') {
              // 新增
              this.attachData = confTempData
            }
          } else {
            this.isHasConf = false
          }
        }
      })
    },
    // 查询已上传附件列表
    fatchUploadFiles () {
      let businessId = this.businessId
      if (this.fileModel === 'change') {
        this.opType = 'view'
      }
      let arr = []
      if (businessId) {
        apiGetFileList({ businessId }).then(res => {
          if (res.data) {
            let resultArr = res.data.list
            if (resultArr.length > 0) {
              resultArr.map(item => {
                arr.push({
                  sceneAttachmentId: item.sceneAttachmentId, // 流程场景ID
                  filePureName: item.filePureName, // 模板名称
                  sceneFileSourceName: item.sceneFileSourceName, // 模板附件名称
                  sceneFileUploadId: item.sceneFileUploadId, // 模板附件Id
                  fileuploadId: item.fileuploadId,
                  fileSourceName: item.fileSourceName,
                  expireTime: item.expireTime
                    ? this.$dayjs(item.expireTime).valueOf()
                    : '',
                  opType: this.opType,
                  fileuploadChangeId: item.fileuploadChangeId || null
                })
              })
            }
            this.uploadData = arr
            this.attachData = this.adaptRes(arr)
          }
        })
      } else {
        this.attachData = this.adaptRes(arr)
      }
    },
    // 适配模板数据和已上传数据 编辑
    adaptRes (upfileData) {
      let upData = upfileData
      let tempData = this.tempData
      let changeData = this.changeUploadData
      let upFormatData = []
      let result = []
      if (this.fileModel === 'normal') {
        // 正常模式
        if (this.isHasConf) {
          // 有模板配置
          if (tempData.length > upData.length) {
            // 模板数据比上传的数据多 说明有部分数据没有上传
            tempData.map(item => {
              let sceneAttachmentId = item.sceneAttachmentId
              let row = upData.find(row => {
                return row.sceneAttachmentId == sceneAttachmentId
              })
              if (row) {
                item.filePureName = row.filePureName // 模板名称
                item.sceneFileSourceName = row.sceneFileSourceName // 模板附件名称
                item.sceneFileUploadId = row.sceneFileUploadId // 模板附件Id
                item.fileuploadId = row.fileuploadId // 用户上传的附件Id
                item.fileSourceName = row.fileSourceName // 用户上传的附件名称
                item.expireTime = row.expireTime
                  ? this.$dayjs(row.expireTime).valueOf()
                  : ''
                item.opType = row.opType
              }
            })
            result = tempData
          } else {
            // 已上传数和模板相同
            upData.map(item => {
              upFormatData.push({
                sceneAttachmentId: item.sceneAttachmentId, // 流程场景ID
                filePureName: item.filePureName, // 模板名称
                sceneFileSourceName: item.sceneFileSourceName, // 模板附件名称
                sceneFileUploadId: item.sceneFileUploadId, // 模板附件Id
                fileuploadId: item.fileuploadId,
                fileSourceName: item.fileSourceName,
                expireTime: item.expireTime
                  ? this.$dayjs(item.expireTime).valueOf()
                  : '',
                opType: this.opType
              })
            })
            result = upFormatData
          }
          this.$emit('change', _cloneDeep(result))
        } else {
          // 无配置
          upData.map(item => {
            upFormatData.push({
              sceneAttachmentId: item.sceneAttachmentId, // 流程场景ID
              filePureName: item.filePureName, // 模板名称
              sceneFileSourceName: item.sceneFileSourceName, // 模板附件名称
              sceneFileUploadId: item.sceneFileUploadId, // 模板附件Id
              fileuploadId: item.fileuploadId,
              fileSourceName: item.fileSourceName,
              expireTime: item.expireTime
                ? this.$dayjs(item.expireTime).valueOf()
                : '',
              opType: this.opType
            })
          })
          result = upFormatData
          this.$emit('change', _cloneDeep(result.slice(0, 1)))
        }
      } else {
        // 变更模式
        if (this.isHasConf) {
          // 配置模板
          // 变更数据和 旧数据对比
          if (this.changeData.length < upData.length) {
            upData.map(item => {
              let sceneAttachmentId = item.sceneAttachmentId
              let row = this.changeData.find(row => {
                return row.sceneAttachmentId == sceneAttachmentId
              })
              if (row) {
                item.filePureName = row.filePureName // 模板名称
                item.sceneFileSourceName = row.sceneFileSourceName // 模板附件名称
                item.sceneFileUploadId = row.sceneFileUploadId // 模板附件Id
                item.fileuploadId = row.fileuploadId // 用户上传的附件Id
                item.fileSourceName = row.fileSourceName // 用户上传的附件名称
                item.expireTime = row.expireTime
                  ? this.$dayjs(row.expireTime).valueOf()
                  : ''
                item.opType = row.opType
                item.fileuploadChangeId = row.fileuploadChangeId // 附件变更ID
              }
            })
          } else {
            upData = this.changeData
          }
          // 变更数据和旧数据合并后
          if (tempData.length > upData.length) {
            // 模板数据比上传的数据多 说明有部分数据没有上传
            tempData.map(item => {
              let sceneAttachmentId = item.sceneAttachmentId
              let row = upData.find(row => {
                return row.sceneAttachmentId == sceneAttachmentId
              })
              if (row) {
                item.filePureName = row.filePureName // 模板名称
                item.sceneFileSourceName = row.sceneFileSourceName // 模板附件名称
                item.sceneFileUploadId = row.sceneFileUploadId // 模板附件Id
                item.fileuploadId = row.fileuploadId // 用户上传的附件Id
                item.fileSourceName = row.fileSourceName // 用户上传的附件名称
                item.expireTime = row.expireTime
                  ? this.$dayjs(row.expireTime).valueOf()
                  : ''
                item.opType = row.opType
                item.fileuploadChangeId = row.fileuploadChangeId
              }
            })
            result = tempData
          } else {
            // 已上传数和模板相同
            upData.map(item => {
              upFormatData.push({
                sceneAttachmentId: item.sceneAttachmentId, // 流程场景ID
                filePureName: item.filePureName, // 模板名称
                sceneFileSourceName: item.sceneFileSourceName, // 模板附件名称
                sceneFileUploadId: item.sceneFileUploadId, // 模板附件Id
                fileuploadId: item.fileuploadId,
                fileSourceName: item.fileSourceName,
                expireTime: item.expireTime
                  ? this.$dayjs(item.expireTime).valueOf()
                  : '',
                opType: item.opType,
                fileuploadChangeId: item.fileuploadChangeId || null
              })
            })
            result = upFormatData
          }
          this.$emit('change', _cloneDeep(result))
        } else {
          // 无配置模板
          if (this.changeData.length < upData.length) {
            upData.map(item => {
              let sceneAttachmentId = item.sceneAttachmentId
              let row = this.changeData.find(row => {
                return row.sceneAttachmentId == sceneAttachmentId
              })
              if (row) {
                item.filePureName = row.filePureName // 模板名称
                item.sceneFileSourceName = row.sceneFileSourceName // 模板附件名称
                item.sceneFileUploadId = row.sceneFileUploadId // 模板附件Id
                item.fileuploadId = row.fileuploadId // 用户上传的附件Id
                item.fileSourceName = row.fileSourceName // 用户上传的附件名称
                item.expireTime = row.expireTime
                  ? this.$dayjs(row.expireTime).valueOf()
                  : ''
                item.opType = row.opType
                item.fileuploadChangeId = row.fileuploadChangeId // 附件变更ID
              } else {
                item.opType = 'view'
                item.expireTime = item.expireTime
                  ? this.$dayjs(item.expireTime).valueOf()
                  : ''
              }
            })
          } else {
            upData = this.changeData
          }
          result = upFormatData
          // this.$emit('change', _cloneDeep(result.slice(0, 1)))
        }
      }
      return result
    },
    // 新增附件
    addAttaches () {
      this.attachData.push({
        opType: 'add',
        filePureName: '',
        fileSourceName: '',
        fileuploadId: null,
        expireTime: '',
        sceneFileSourceName: '',
        sceneFileUploadId: null,
        sceneAttachmentId: null // 场景ID
      })
    },
    // 文件上传========[
    attachButtonClick (index) {
      this.attRowIndex = index
    },
    // 上传附件成功
    tableHandleUploadSuccess (file) {
      const { id, name } = file
      this.attachData[this.attRowIndex].fileuploadId = id.toString()
      this.attachData[this.attRowIndex].fileSourceName = name
      this.$nextTick(() => {
        this.$refs.attachTable.doLayout()
      })
      this.$emit('change', _cloneDeep(this.attachData))
    },
    // 小叉叉 删除文件
    tableHandleAttachmentRemove (row) {
      let id = row.fileuploadId
      fileDel(id).then(res => {
        if (res) {
          this.$message({
            message: res.message,
            type: 'success'
          })
          row.fileuploadId = ''
          row.fileSourceName = ''
        }
      })
      row.fileuploadId = ''
      row.fileSourceName = ''
    },
    // 操作列 删除附件
    delAttaches (index, row) {
      if (row.fileuploadId) {
        let id = row.fileuploadId
        fileDel(id).then(res => {
          if (res) {
            this.attachData.splice(index, 1)
          }
        })
      } else {
        this.attachData.splice(index, 1)
      }
    },
    // 附件上传】】
    // 校验附件上传
    validRequired () {
      let att = this.attachData
      let valid = true
      for (let i = 0; i < att.length; i++) {
        if (att[i].required === 'Y' && !att[i].fileuploadId) {
          this.$message({
            message:
              this.$t('vendorMod.attUnUpload') +
              '："' +
              att[i].filePureName +
              '"',
            type: 'error'
          })
          valid = false
          return false
        }
      }
      return valid
    },
    // 变更模式下
    // 表格样式处理
    rowClassName ({ row, rowIndex }) {
      if (this.fileModel === 'change') {
        if (
          row.opType === 'add' ||
          row.opType === 'update' ||
          row.opType === 'delete'
        ) {
          return 'update-row'
        }
      }
      return ''
    },
    fileRowHandel (index, row, type) {
      if (type === 'delete') {
        // 删除
        this.attachData[index].opType = 'delete'
      } else if (type === 'update') {
        // 编辑
        this.attachData[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.attachData[index].opType = 'view'
      }
    }
  }
}
</script>

<style scope>
.sceneAttachment .attBtn {
  margin-bottom: 10px;
}
.sceneAttachment .el-table.el-table--striped .el-table__row.update-row,
.sceneAttachment
  .el-table.el-table--striped
  tr.el-table__row.el-table__row--striped.update-row
  td {
  background: #fbcdcd !important;
}
</style>
