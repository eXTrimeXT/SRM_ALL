<template>
  <SrmDialog
    :title="$t('bidMod.technicalDocuments.title')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <div v-if="!readonly" style="margin-bottom: 10px">
      <!--需要存在物料才能选-->
      <el-button
        v-if="params.materialCode"
        type="primary"
        @click="openDrawingDialog"
      >
        {{ $t('bidMod.technicalDocuments.drawingButton') }}
      </el-button>

      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </div>

    <el-table
      ref="fileListTable"
      :data="fileList"
      border
      height="333px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--附件名称-->
      <el-table-column
        prop="fileCustomName"
        :label="$t('bidMod.attachmentName')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input v-if="!readonly && !scope.row.isView" v-model="scope.row.fileCustomName" />
          <span v-else>{{ scope.row.fileCustomName }}</span>
        </template>
      </el-table-column>

      <!--附件-->
      <el-table-column
        prop="fileuploadId"
        :label="$t('bidMod.attachment')"
        min-width="200"
        show-overflow-tooltip
      >
        <template v-slot="{ row, $index }">
          <SrmCommonFile
            :default-file="{
              fileId: row.fileuploadId,
              fileName: row.fileName
            }"
            :readonly="readonly || row.isView"
            @on-change="value => fileChange(value, $index)"
          />
        </template>
      </el-table-column>

      <!--附件地址-->
      <el-table-column
        prop="fileLink"
        :label="$t('drawingshead.drawingAddress')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input v-if="!readonly && !scope.row.isView" v-model="scope.row.fileLink" />
          <span v-else>{{ scope.row.fileLink }}</span>
        </template>
      </el-table-column>

      <!--附件类型-->
      <el-table-column
        prop="businessFileType"
        :label="$t('dataConfMod.attachmentType')"
        min-width="120"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="!readonly && !scope.row.isView"
            v-model="scope.row.businessFileType"
            code="DRAWING_TYPE"
          />
          <span v-else>{{ $getDictLabel('DRAWING_TYPE', scope.row.businessFileType) }}</span>
        </template>
      </el-table-column>

      <!--备注-->
      <el-table-column
        prop="remark"
        :label="$t('common.remark')"
        min-width="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly && !scope.row.isView" v-model="scope.row.remark" />
          <span v-else>{{ scope.row.remark }}</span>
        </template>
      </el-table-column>

      <!--操作-->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="{ row, $index }">
          <el-button
            type="text"
            :disabled="row.isView"
            @click="deleteRow($index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <!--确定-->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>

    <DrawingDialog
      v-if="drawingDialogVisible"
      :visible.sync="drawingDialogVisible"
      :params="params"
      @confirm="confirmDrawing"
    />
  </SrmDialog>
</template>

<script>
/**
 * 物料技术文件
 */
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import DrawingDialog from './drawingDialog.vue'
import { sceneFileCompApi } from '@/api/fileApi'

export default {
  name: 'TechnicalDocumentsDialog',

  components: { DrawingDialog },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    params: {
      type: Object,
      default: () => { /* nothing */ }
    },
    detailData: {
      type: [Array, Object],
      default: () => []
    }
  },

  data () {
    return {
      fileList: [],
      drawingDialogVisible: false
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
    if (this.detailData && Array.isArray(this.detailData) && this.detailData.length > 0) {
      // 带入旧数据
      this.arrangeFileList(this.detailData)
    } else if (this.params.businessId) {
      // 从场景附件查询
      this.getSceneFileList()
    }
  },

  methods: {
    /* 查询附件列表 */
    async getSceneFileList () {
      if (!this.params.businessId) {
        return
      }

      const response = await sceneFileCompApi.sceneFileListAll({ businessId: this.params.businessId })
      if (response && response.data) {
        this.arrangeFileList(response.data)
      }
    },

    /* 编排列表数据并赋值 */
    arrangeFileList (fileList) {
      this.fileList = fileList.concat().map(item => {
        return {
          ...item,
          // 标记来源物料图纸的不允许修改
          isView: item.attachmentType === 'DRAWINGSHEAD'
        }
      })
    },

    /* 从物料附件选择 */
    openDrawingDialog () {
      this.drawingDialogVisible = true
    },

    /* 手动新增 */
    addRow () {
      const obj = {
        attachmentType: 'CUSTOM'
      }
      if (this.params.businessId) {
        this.fileList.push({
          ...obj,
          businessId: this.params.businessId
        })
      } else {
        this.fileList.push(obj)
      }
    },

    /* 删除 */
    deleteRow ($index) {
      this.fileList.splice($index, 1)
    },

    /* 手动上传文件 */
    fileChange ({ file }, $index) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileList[$index].fileuploadId = fileId
      this.fileList[$index].fileName = fileName
    },

    /* 从物料附件选择 */
    confirmDrawing (val) {
      this.fileList = this.fileList.concat(val.map(item => {
        let obj = {
          fileCustomName: item.attachName,
          fileuploadId: item.fileuploadId,
          fileName: item.fileName,
          fileLink: item.fileuploadAddress,
          businessFileType: item.drawingsType,
          attachmentType: 'DRAWINGSHEAD',
          isView: true
        }
        if (this.params.businessId) {
          obj = {
            ...obj,
            businessId: this.params.businessId
          }
        }
        return obj
      }))
    },

    /* 确定 */
    confirm () {
      // if (this.fileList.length === 0) {
      //   this.$message.warning(this.$t('bidMod.technicalDocuments.fileListEmptyMessage'))
      //   return
      // }

      if (!validateRequiredColumn(
        this.$refs.fileListTable,
        this.fileList,
        {
          validateScope: true,
          tableRequired: false,
          tableTitle: this.$t('bidMod.technicalDocuments.title')
        }
      )) {
        return
      }

      this.dialogVisible = false

      this.$emit('confirm', this.fileList)
    }
  }
}
</script>
