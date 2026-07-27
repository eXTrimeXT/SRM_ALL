<!-- 附件管理组件 -->
<template>
  <div>
    <div class="btn-group">
      <!-- 没有业务id允许新增附件，否则附件无法关联单据 -->
      <el-button
        v-if="addition"
        class="detail-pbtn"
        type="primary"
        @click="add"
      >
        {{ $t('common.add') }}
      </el-button>
      <el-button
        v-if="showMultiDeletion"
        class="detail-pbtn"
        type="primary"
        @click="multiDeleteFiles"
      >
        {{ $t('common.delete') }}
      </el-button>
    </div>
    <el-table
      stripe
      border
      fit
      :height="height"
      :data="value"
      :max-height="maxHeight"
      @selection-change="selectionChange"
    >
      <el-table-column
        type="selection"
        fixed="left"
      />
      <el-table-column
        type="index"
        align="center"
        :label="$t('序号')"
        fixed="left"
      />
      <el-table-column
        :label="$t('附件类型')"
        prop="attachmentType"
        align="center"
      >
        <template slot-scope="scope">
          <render-select
            v-model="scope.row.attachmentType"
            :code="sceneModuleCode"
            :store="store"
            clearable
          />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('模板下载')"
        prop="attachmentName"
        align="center"
      >
        <template slot-scope="scope">
          <c-download-link
            :id="scope.row.templateFileId"
            :name="scope.row.attachmentName"
            ellipsis
            class="download-link-item"
          />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('附件上传')"
        prop="fileUploadId"
        align="center"
      >
        <template slot-scope="scope">
          <c-upload-file
            v-if="!scope.row.fileUploadId"
            :limit="1"
            show-progress
            :show-file-list="false"
            :cus-data="fileInfo"
            @upload-success="value => handleUploadSuccess(value, scope)"
          />
          <div
            v-else
            class="download-link-wrap"
          >
            <c-download-link
              :id="scope.row.fileUploadId"
              :name="scope.row.fileName"
              ellipsis
              class="download-link-item"
            />
            <i
              class="el-icon-close close-icon"
              @click="handleAttachmentRemove(scope.row)"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('是否必填')"
        prop="required"
        align="center"
        :formatter="(...args) => store.getLabel('required', args[2])"
      />
      <el-table-column
        :label="$t('备注')"
        prop="remark"
        align="center"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.remark"
            clearable
          />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('更新时间')"
        prop="lastUpdateDate"
        align="center"
      />
      <el-table-column
        :label="$t('更新人')"
        prop="lastUpdatedUserName"
        align="center"
      />
      <el-table-column
        :label="$t('操作')"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="deleteFile(scope)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { createStore, RenderSelect } from 'lib@/utils/easy-dictionary'
import CDownloadLink from 'lib@/components/c-download-link'
import CUploadFile from '@/library/components/c-upload-file'

let seed = 0

function generateTemplateId (fileTag) {
  const index = ++seed
  return `custom_file_id_${fileTag}${index}`
}

function isContain (ids, item) {
  const id = item.sceneFileId || item.tplId
  return ids.includes(id)
}

function deleteFiles (files, ids) {
  if (files.some(item => isContain(ids, item))) {
    const index = files.findIndex(item => isContain(ids, item))
    files.splice(index, 1)
    deleteFiles(files, ids)
  }
}

export default {
  name: 'CFileManagement',
  components: { RenderSelect, CDownloadLink, CUploadFile },
  model: {
    event: 'change',
    prop: 'value'
  },
  props: {
    // 单据新建的时候没有businessId
    businessId: {
      type: [Number, String]
    },
    height: {
      type: [Number, String],
      default: 300
    },
    maxHeight: {
      type: [Number, String],
      default: 300
    },
    sceneModuleCode: {
      type: String,
      required: true
    },
    value: {
      type: Array
    },
    showAddition: {
      type: Boolean,
      default: true
    },
    showMultiDeletion: {
      type: Boolean,
      default: true
    },
    fileInfo: {
      type: Object,
      default: () => {
        return {
          uploadType: 'FASTDFS',
          sourceType: 'WEB_APP',
          fileModular: 'base', // 文件所属模块 -》基础模块
          fileFunction: 'CFileManagement', // 文件所属功能
          fileType: 'images' // 文件所属类型
        }
      }
    }
  },
  data () {
    return {
      store: createStore({
        [this.sceneModuleCode]: [],
        required: [
          { label: '是', value: 'Y', id: 0 },
          { label: '否', value: 'N', id: 1 }
        ]
      }),
      fileList: [],
      selection: []
    }
  },
  computed: {
    addition () {
      if (this.businessId) {
        return this.showAddition
      }
      return false || true
    },
    deletion () {
      if (this.businessId) {
        return this.showMultiDeletion
      }
      return false || true
    }
  },
  watch: {},
  created () {
    this.store.commit('loadDictionary', this.sceneModuleCode)
    this.initFileList()
  },
  mounted () {},
  methods: {
    selectionChange (value) {
      this.selection = value
    },
    async initFileList () {
      if (!this.businessId) {
        this.fileList = []
        return
      }
      const { list } = await this.$api.base.fileManagement({
        path: 'listAll',
        parameter: { businessId: this.businessId }
      })
      this.fileList = list
    },
    handleUploadSuccess (file, scope) {
      const { id, name } = file
      scope.row.fileUploadId = id.toString()
      scope.row.fileName = name
    },
    handleAttachmentRemove (row) {
      row.fileUploadId = ''
      row.fileName = ''
    },
    deleteFile (scope) {
      if (scope.row.sceneFileId) {
        this.$confirm(this.$t('common.delRow'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$api.base.fileManagement({
              path: 'delete',
              method: 'GET',
              data: { id: scope.row.sceneFileId }
            }).then((res) => {
              this.$message.success(res.message)
            })
          })
          .catch(() => {})
      } else {
        this.fileList.splice(scope.$index, 1)
      }
      this.updateValue()
    },
    multiDeleteFiles () {
      if (this.selection.length) {
        const ids = []
        const tplIds = []
        this.selection.forEach(item => {
          if (item.tplId) {
            tplIds.push(item.tplId)
          } else {
            ids.push(item.sceneFileId)
          }
        })
        deleteFiles(this.fileList, tplIds)
        if (!ids.length) return
        this.$confirm(this.$t('确认删除选中行？'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$api.base.fileManagement({
              path: 'bathDelete',
              method: 'GET',
              data: { ids }
            }).then((res) => {
              this.$message.success(res.message)
              deleteFiles(this.fileList, ids)
            })
          })
          .catch(() => {})
      } else {
        this.$message.warning(this.$t('请选择要删除的数据！'))
      }
      this.updateValue()
    },
    add () {
      this.fileList.push({
        tplId: generateTemplateId('custom_file'),
        fileName: '',
        fileUploadId: '',
        attachmentName: '',
        templateFileId: '',
        required: ''
      })
      this.updateValue()
    },
    updateValue () {
      this.$emit('change', this.fileList)
    }
  }
}
</script>
<style scoped lang="scss">
  .btn-group {
    margin-bottom: 10px;
  }
</style>
