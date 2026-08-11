<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <MainHeader>
        <template slot="left">
          <el-button
            v-if="baseInfo.projectStatus !== 'FILE' && !isPageView"
            type="primary"
            @click="addFile"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <el-table
        border
        :data="fileList"
        max-height="250"
      >
        <el-table-column
          type="index"
          align="center"
          width="50"
        />
        <SrmCommonFile
          type="table-column"
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('cusEntry.competition.archivesName'),
            prop: 'docId',
            nameProp: 'fileName',
            renderHeader: _addStarToColumn
          }"
          :readonly="baseInfo.projectStatus === 'FILE' || isPageView"
          @on-change="fileUploadSuccess"
        />
        <el-table-column
          :label="$t('common.remark')"
          prop="fileRemark"
          align="center"
          min-width="120"
        >
          <template slot-scope="scope">
            <el-input
              v-if="baseInfo.projectStatus !== 'FILE' && !isPageView"
              v-model="scope.row.fileRemark"
            />
            <span v-else>{{ scope.row.fileRemark }}</span>
          </template>
        </el-table-column>
        <el-table-column
          v-if="baseInfo.projectStatus !== 'FILE' && !isPageView"
          :label="$t('common.operation')"
          width="60"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="deleteFile(scope.$index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import MainHeader from 'lib@/components/Table/MainHeader'
export default {
  name: 'Archives',
  components: {
    MainHeader
  },
  props: {
    fileList: {
      type: Array,
      default: () => []
    },
    baseInfo: {
      type: Object,
      default: () => ({})
    },
    isPageView: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'bid',
        fileFunction: 'expertDatabase',
        fileType: 'images'
      }
    }
  },
  watch: {
    fileList: {
      immediate: true,
      deep: true,
      handler (newValue) {
        this.$emit('update:fileList', newValue)
      }
    }
  },
  methods: {
    /* 新增归档文件 */
    addFile () {
      this.fileList.unshift({
        fileType: 'PLACEONFILE',
        docId: '',
        fileName: ''
      })
    },
    /* 删除附件 */
    deleteFile (index) {
      this.fileList.splice(index, 1)
    },
    /* 附件上传成功 */
    fileUploadSuccess ({ file, $index }) {
      const { fileId, fileName } = file || {}
      this.fileList[$index].docId = fileId
      this.fileList[$index].fileName = fileName
    },
    /* 获取附件信 */
    getFileList () {
      return this.fileList
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
