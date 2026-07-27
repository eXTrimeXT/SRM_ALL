<template>
  <div class="tech-info">
    <p>
      <span style="padding: 0 11px">{{ $t("bidMod.techInfo") }}</span>
      <!--新增-->
      <el-button
        type="primary"
        @click="addSignUpRow"
      >
        {{ $t("common.add") }}
      </el-button>
    </p>

    <div class="table2">
      <el-table
        :data="vendorFileListData"
        style="width: 100%"
        border
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <!--招标要求-->
        <el-table-column
          align="center"
          prop="reqInfo"
          :label="$t('bidMod.bidRequire')"
          show-overflow-tooltip
        />

        <!--参考附件-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.refAttachment'),
            prop: 'reqDocId',
            nameProp: 'reqFileName'
          }"
          readonly
        />

        <!--采购商备注-->
        <el-table-column
          align="center"
          prop="reqComments"
          :label="$t('bidMod.vendorRemark')"
          show-overflow-tooltip
        />

        <!--文件类型-->
        <el-table-column
          align="center"
          prop="fileType"
          :label="$t('bid_mod.referenceFileType')"
        >
          <template slot-scope="scope">
            <dict-select
              v-model="scope.row.fileType"
              :transform-options="transformOptions"
              code="BRG_FILE_CONFIG_TYPE"
              :disabled="scope.row.reqDocId"
            />
          </template>
        </el-table-column>

        <!--投标附件-->
        <SrmCommonFile
          type="table-column"
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('bidMod.bidAttachment'),
            prop: 'vendorDocId',
            nameProp: 'vendorFileName'
          }"
          @on-change="signUpFileChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="vendorComments"
          :label="$t('bidMod.remark')"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.vendorComments" />
          </template>
        </el-table-column>

        <!--操作-->
        <el-table-column
          :label="$t('common.operation')"
          width="80"
        >
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.reqDocId"
              type="text"
              @click="handleDelClick(scope.$index, scope.row)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
/**
 * 技术信息
 */
export default {
  name: 'TechInfo',
  props: {
    bargainData: {
      type: Object
    },
    vendorFileList: {
      type: Array
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBargainList',
        fileType: 'images'
      }
    }
  },
  computed: {
    vendorFileListData: {
      get: function () {
        return this.vendorFileList
      },
      set: function (val) {
        return this.$emit('update:vendorFileList', val)
      }
    }
  },
  methods: {
    /* 编排文件类型 */
    transformOptions (options) {
      // 如果招标类型是 【商务】 那么参考模板文件类型只能是 【商务标】
      if (this.bargainData.bargainType === 'BUSINESS') {
        return options.map(opt => {
          if (opt.value === 'TECHNICAL_BID') {
            return { ...opt, disabled: true }
          }
          return opt
        })
      }
      return options
    },

    /* 新增一行 */
    addSignUpRow () {
      this.vendorFileListData.push({
        vendorFileName: '',
        vendorDocId: '',
        referenceFileType: '',
        comments: '',
        buyerConfig: 'N'
      })
    },

    /* 文件变更 */
    signUpFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorFileListData[$index].vendorDocId = fileId
      this.vendorFileListData[$index].vendorFileName = fileName
    },

    /* 删除行 */
    handleDelClick (index, row) {
      if (row.buyerConfig === 'N' || !row.configFileId) {
        this.vendorFileListData.splice(index, 1)
      }
    }
  }
}
</script>
