<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.uploadTechSolution')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      border
      :data="fileList"
      max-height="200"
      style="width: 100%"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 包名 -->
      <el-table-column
        v-if="mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="120"
        :render-header="_addStarToColumn"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-select v-model="scope.row.extPackageName">
            <el-option
              v-for="(item, index) in packNameList"
              :key="index"
              :label="item"
              :value="item"
            />
          </el-select>
        </template>
      </el-table-column>
      <!-- 上传脱敏文件 -->
      <el-table-column
        align="center"
        prop="orderDocId"
        :label="$t('cusEntry.bidMod.uploadTechFile')"
        min-width="120"
        :render-header="_addStarToColumn"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.orderDocId,
              fileName: scope.row.orderFileName
            }"
            :readonly="readonly"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <el-button type="primary" @click="confirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 技术方案脱敏上传
 */
export default {
  name: 'TechSolutionFileDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    mergeFlag: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      readonly: false,
      packNameList: [],
      fileList: []
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

  async created () {
    this.mergeFlag && await this.getProjectPackName()
    this.getSecretFileList()
  },

  methods: {
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.orderDocId = fileId
      row.orderFileName = fileName
    },
    // 查询包名
    getProjectPackName () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getProjectPackName?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.packNameList = res.data
        }
      })
    },
    /* 查询数据 */
    getSecretFileList () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getSecretFileList?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.fileList = res.data
        }
      })
    },
    confirm () {
      let flag = this.fileList.some(item => (!item.extPackageName && this.mergeFlag) || !item.orderDocId)
      if (flag) {
        this.$message.error('请补全必填项')
        return
      }
      const params = {
        projectId: this.projectId,
        secretFileList: this.fileList
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/editSecretFile',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.dialogVisible = false
      })
    }
  }
}
</script>
