<template>
  <el-form
    ref="approvalFilesForm"
    :model="approvalFilesForm"
    :disabled="isDisabledTable"
  >
    <div class="left_div">
      <p style="margin: 5px 0 10px">
        <span style="padding: 0 11px">{{ $t("bidMod.uploadFileList") }}</span>
        <!--b 添加-->
        <el-button
          v-if="!isDisabledTable"
          type="primary"
          class="detail-pbtn"
          @click="addOne"
        >
          {{ $t("common.new") }}
        </el-button>
      </p>

      <el-table
        :data="approvalFileListData"
        style="width: 100%"
        border
        max-height="251px"
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />

        <!--t 附件名称-->
        <SrmCommonFile
          type="table-column"
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'fileRelationId',
            nameProp: 'fileName',
            minWidth: '200'
          }"
          :readonly="isDisabledTable"
          @on-change="innerFileChange"
        />

        <!--t 备注-->
        <el-table-column
          align="center"
          prop="remark"
          :label="$t('bidMod.remark')"
          min-width="250"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.remark" />
          </template>
        </el-table-column>

        <!--t 操作-->
        <el-table-column
          align="center"
          prop="operation"
          :label="$t('bidMod.operation')"
          width="100"
        >
          <template v-slot="scope">
            <el-button
              type="text"
              @click="deleteRow(scope.$index, scope.row)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-form>
</template>

<script>
/**
 * 上传附件
 */
export default {
  name: 'ApprovalFiles',

  props: {
    pageFlag: Object,
    pageType: Object,
    approvalFileList: Array
  },
  data () {
    return {
      approvalFilesForm: {},
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'inquiry',
        fileType: 'images'
      },
      bankRowIndex: ''
    }
  },
  computed: {
    approvalFileListData: {
      get: function () {
        return this.approvalFileList
      },
      set: function (val) {
        this.$emit('update:approvalFileList', val)
      }
    },
    isDisabledTable () {
      return this.pageFlag.isReadonly || this.pageFlag.isApproval || !this.pageType.isHandMake
    }
  },
  methods: {
    /* 新增行 */
    addOne () {
      this.approvalFileListData.push({
        fileRelationId: '',
        fileName: '',
        remark: ''
      })
    },

    /* 文件变更 */
    innerFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.approvalFileListData[$index].fileRelationId = fileId
      this.approvalFileListData[$index].fileName = fileName
    },

    /* 删除行 */
    deleteRow (index) {
      this.approvalFileListData.splice(index, 1)
    }
  }
}
</script>
