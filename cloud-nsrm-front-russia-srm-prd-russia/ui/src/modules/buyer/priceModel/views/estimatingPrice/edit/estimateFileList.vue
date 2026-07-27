<template>
  <div class="estimate-file-list">
    <div
      v-if="!readOnly"
      style="margin: 8px 0;"
    >
      <el-button
        type="primary"
        class="detail-pbtn"
        @click="addFormula"
      >
        {{ $t("common.add") }}
      </el-button>
    </div>

    <BaseTable
      stripe
      :data="fileTableData"
      :columns="fileColumns"
      columns-name="fileColumns"
      :empty-text="$t('priceModel.costElement.noData')"
      border
      @deleteFile="deleteFile"
    >
      <!-- 备注 -->
      <template #comment="scope">
        <el-input
          v-model.trim="fileTableData[scope.$index].comments"
          :disabled="readOnly"
        />
      </template>

      <template #fileUploadId="{ row, $index }">
        <SrmCommonFile
          :default-file="{
            fileId: row.fileUploadId,
            fileName: row.fileUploadName
          }"
          :extra-data="fileInfo"
          :readonly="readOnly"
          @on-change="value => fileChange(value, $index)"
        />
      </template>
    </BaseTable>
  </div>
</template>

<script>
/**
 * 文件上传
 */
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'EstimateFileList',

  components: { BaseTable },

  props: {
    detailData: {
      type: [Object, Array],
      required: true
    },
    readOnly: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'estimatingPrice',
        fileType: 'images'
      },
      fileTableData: [],
      fileColumns: [
        {// 序号
          attrs: {
            align: 'center',
            type: 'index',
            label: t => t.$t('priceModel.costElement.sequenceFlag'),
            width: 60
          }
        },
        {// 文件上传
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.estimatingPrice.fileUploadId'),
            prop: 'fileUploadId'
          },
          slot: 'fileUploadId'
        },
        {// 备注
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '备注',
            prop: 'comment'
          },
          slot: 'comment'
        },
        {// 操作
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteAttr',
              event: 'deleteFile',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.fileTableData = val && Array.isArray(val) ? val.concat() : []
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 新增一行文件上传信息 */
    addFormula () {
      this.fileTableData.push({
        fileUploadId: '',
        fileUploadName: ''
      })
    },

    /* 文件变更 */
    fileChange ({ file }, $index) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileTableData[$index].fileUploadId = fileId
      this.fileTableData[$index].fileUploadName = fileName
    },

    /* 删除文件行 */
    deleteFile (scope) {
      this.fileTableData.splice(scope.$index, 1)
    },

    /* 给父组件回调参数 */
    getParamsData () {
      return this.fileTableData
    }
  }
}
</script>
