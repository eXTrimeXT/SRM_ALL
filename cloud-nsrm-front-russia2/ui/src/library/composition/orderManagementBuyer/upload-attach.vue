<template>
  <div>
    <el-table :data="attachData" style="width: 100%" border max-height="250px">
      <el-table-column align="center" type="index" :label="$t('common.sort')" width="60" />
      <el-table-column align="center" :prop="attachName" :label="$t('bidMod.fileName')">
        <template slot-scope="scope">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.fileuploadId,
              fileName: scope.row[attachName]
            }"
            :filePreviewOptions="{
              vWidth: '800',
              vHeight: '300'
            }"
            :readonly="readonly"
            @on-change="({file}) => uploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createdUserName" :label="$t('quota.uploadBy')" />
      <el-table-column align="center" prop="creationDate" :label="$t('quota.uploadDate')">
        <template slot-scope="scope">
          {{$parseTime(scope.row.endDate)}}
        </template>
      </el-table-column>
      <el-table-column v-if="isOperation" :label="$t('common.operation')" width="80">
        <template slot-scope="scope">
          <el-button type="text" :disabled="readonly" @click="delInvoiceTaxControls(scope.$index, scope.row)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'UploadAttach',
  components: {
  },
  props: {
    readonly: {
      type: Boolean,
      default () {
        return false
      }
    },
    isOperation: {
      type: Boolean,
      default () {
        return true
      }
    },
    attachData: {
      type: Array,
      default () {
        return []
      }
    },
    attachName: {
      type: String,
      default () {
        return 'attachName'
      }
    },
    fileInfo: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      curRowIndex: 0
    }
  },
  methods: {
    uploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row[this.attachName] = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
      this.$emit('upload-success', file)
    },
    // 删除
    delInvoiceTaxControls (index, row) {
      this.attachData.splice(index, 1)
    }
  }
}
</script>

<style></style>
