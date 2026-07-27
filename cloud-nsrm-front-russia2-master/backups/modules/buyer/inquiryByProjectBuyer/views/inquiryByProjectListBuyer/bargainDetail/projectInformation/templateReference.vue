<template>
  <div class="template-reference">
    <p style="margin: 0 0 10px 0">
      <span style="padding-right: 11px">
        <!-- 配置供方必须上传附件 -->
        {{ $t("bidMod.confVendorUpAttach") }}
      </span>
      <el-button
        type="primary"
        class="detail-pbtn"
        @click="templateReferenceAddRow"
      >
        {{ $t("common.add") }}
      </el-button>
    </p>

    <el-table
      :data="templateReferenceList"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!-- 新增文件类型和参考文件上传 -->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bid_mod.referenceFile'),
          prop: 'docId',
          nameProp: 'fileName'
        }"
        :readonly="readOnly"
        @on-change="templateReferenceFilesChange"
      />

      <!--资料要求-->
      <el-table-column
        align="center"
        prop="fileRequire"
        :label="$t('bidMod.fileQualify')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.fileRequire" />
        </template>
      </el-table-column>

      <!--文件类型-->
      <el-table-column
        align="center"
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
        min-width="150"
      >
        <template slot-scope="scope">
          <dict-select
            v-model="scope.row.fileType"
            :transform-options="transformOptions"
            code="BRG_FILE_CONFIG_TYPE"
          />
        </template>
      </el-table-column>

      <!--备注-->
      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bidMod.remark')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.comments" />
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="80"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            icon="el-icon-delete"
            class="el-button-icon"
            @click="templateReferenceDeleteRow(scope.$index)"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 模板参考
 */
export default {
  name: 'TemplateReference',

  props: {
    bargainId: {
      type: [Number, String]
    },
    bargainBaseInfo: {
      type: Object
    },
    detailData: {
      type: Array,
      default: () => []
    },
    readOnly: {
      type: Boolean
    }
  },
  data () {
    return {
      templateReferenceList: [] // 附件要求配置
    }
  },
  watch: {
    detailData: {
      handler (val) {
        this.templateReferenceList = val || []
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    /* 编排文件类型 */
    transformOptions (options) {
      // 如果招标类型是 【商务】 那么参考模板文件类型只能是 【商务标】
      if ((this.bargainBaseInfo || {}).bargainType === 'BUSINESS') {
        return options.map(opt => {
          if (opt.value === 'TECHNICAL_BID') {
            return { ...opt, disabled: true }
          }
          return opt
        })
      }
      return options
    },

    /* 新增一行参考模板 */
    templateReferenceAddRow () {
      this.templateReferenceList.push({
        fileName: '',
        comments: '',
        docId: '',
        fileType: '',
        fileRequire: ''
      })
    },

    /* 删除模板参考文件行 */
    templateReferenceDeleteRow (index) {
      this.templateReferenceList.splice(index, 1)
    },

    /* 内部查看文件变更 */
    templateReferenceFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.templateReferenceList[$index].docId = fileId
      this.templateReferenceList[$index].fileName = fileName
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return this.templateReferenceList
    },

    /* 清除数据 */
    clearData () {
      this.templateReferenceList = []
    }
  }
}
</script>
