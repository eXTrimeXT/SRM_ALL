<template>
  <div class="template-reference">
    <p style="margin-top: 0">
      <!--配置供方参考附件-->
      <span style="padding-right: 11px">{{ $t('bidMod.supplierRefer') }}</span>
      <el-button type="primary" @click="addFileItem">
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      :data="fileConfigList"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--参考文件-->
      <SrmCommonFile
        type="table-column"
        :extra-data="fileInfo"
        :table-column-options="{
          label: $t('bid_mod.referenceFile'),
          prop: 'requireDocId',
          nameProp: 'requireFileName'
        }"
        :readonly="readonly"
        @on-change="fileChange"
      />

      <!--资料要求-->
      <el-table-column
        prop="fileRequire"
        :label="$t('bidMod.fileQualify')"
        min-width="150"
      >
        <template v-slot="scope">
          <el-input v-model="scope.row.fileRequire" />
        </template>
      </el-table-column>

      <!--文件类型-->
      <el-table-column
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
        min-width="150"
      >
        <template v-slot="scope">
          <DictSelect v-model="scope.row.fileType" code="SOU_FILE_CONFIG_TYPE" />
        </template>
      </el-table-column>

      <!--备注-->
      <el-table-column
        prop="requireRemark"
        :label="$t('bidMod.remark')"
        min-width="150"
      >
        <template v-slot="scope">
          <el-input v-model="scope.row.requireRemark" />
        </template>
      </el-table-column>

      <el-table-column
        prop="operation"
        :label="$t('bidMod.operation')"
        width="80"
      >
        <template v-slot="scope">
          <el-button type="text" @click="deleteFileItem(scope.$index, scope.row)">
            {{ $t('common.delete') }}
          </el-button>
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
    detailData: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      fileConfigList: [],
      fileInfo: {
        fileModular: 'comp',
        fileFunction: 'templateReference',
        fileType: 'images'
      }
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.fileConfigList = (val || []).concat()
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 新增行 */
    addFileItem () {
      this.fileConfigList.push({
        fileRequire: '',
        requireRemark: '',
        requireDocId: '',
        requireFileName: '',
        fileType: ''
      })
    },

    /* 删除行 */
    deleteFileItem (index) {
      this.fileConfigList.splice(index, 1)
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileConfigList[$index].requireDocId = fileId
      this.fileConfigList[$index].requireFileName = fileName
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return this.fileConfigList
    }
  }
}
</script>
