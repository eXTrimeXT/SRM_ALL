<template>
  <srm-dialog
    title="技术文件"
    :visible.sync="dialogVisible"
    append-to-body
    size="middle"
    :close-on-click-modal="false"
  >
    <p
      v-if="!readonly"
      style="margin-top: 0"
    >
      <el-button
        type="primary"
        @click="addItem"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      ref="itemFilesTable"
      :data="itemFilesData"
      style="width: 100%;"
      border
      height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--附件名称-->
      <el-table-column
        align="center"
        prop="fileCustomName"
        :label="$t('contract_mod.fileName')"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.fileCustomName"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--附件-->
      <SrmCommonFile
        type="table-column"
        :extra-data="fileInfo"
        :table-column-options="{
          label: $t('perfMod.accessory'),
          prop: 'fileuploadId',
          nameProp: 'fileName',
          minWidth: '200',
          renderHeader: _addStarToColumn
        }"
        :readonly="readonly"
        @on-change="fileChange"
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.remark"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="!readonly"
        fixed="right"
        align="center"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="scope">
          <!--删除-->
          <el-button
            type="text"
            @click="deleteItem(scope.$index)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        v-if="!readonly"
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 技术文件
 */
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import { sceneFileCompApi } from '@/api/fileApi'
export default {
  name: 'ItemFilesDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: false
    },
    // 是否只读
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      itemFilesData: [],
      fileInfo: {
        fileModular: 'techExchangeManagement',
        fileFunction: 'techExchangeManagement',
        fileType: 'file'
      }
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
    if (this.editRow.itemFiles && Array.isArray(this.editRow.itemFiles) && this.editRow.itemFiles.length > 0) {
      this.itemFilesData = this.editRow.itemFiles
    } else if (this.editRow.businessId) {
      this.getSceneFileList()
    }
  },

  methods: {
    /* 查询附件列表 */
    async getSceneFileList () {
      if (!this.editRow.businessId) {
        return
      }

      const response = await sceneFileCompApi.sceneFileListAll({ businessId: this.editRow.businessId })
      if (response && response.data) {
        this.itemFilesData = response.data
      }
    },

    /* 新增行 */
    addItem () {
      if (this.editRow.businessId) {
        this.itemFilesData.push({
          businessId: this.editRow.businessId
        })
      } else {
        this.itemFilesData.push({})
      }
    },

    /* 删除行 */
    deleteItem (index) {
      this.itemFilesData.splice(index, 1)
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.itemFilesData[$index].fileuploadId = fileId
      this.itemFilesData[$index].fileName = fileName
    },

    /* 确定提交数据 */
    confirm () {
      // 校验附件名称，附件，附件类型必填
      if (!validateRequiredColumn(
        this.$refs.itemFilesTable,
        this.itemFilesData,
        {
          validateScope: true,
          tableTitle: '技术文件'
        }
      )) {
        return
      }
      this.$emit('save', this.itemFilesData)
      this.dialogVisible = false
    }
  }
}
</script>
