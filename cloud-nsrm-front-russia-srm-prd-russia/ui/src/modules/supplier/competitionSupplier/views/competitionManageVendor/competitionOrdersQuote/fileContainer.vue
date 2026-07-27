<template>
  <div class="file-container">
    <h3>
      附件信息
    </h3>
    <el-button
      type="primary"
      :disabled="readonly"
      @click="addRow"
    >
      {{ $t('common.add') }}
    </el-button>

    <el-table
      :data="orderFileListData"
      style="width: 100%;margin-top:10px;"
      border
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--资料要求-->
      <el-table-column
        prop="fileRequire"
        :label="$t('bidMod.fileQualify')"
        show-overflow-tooltip
      />

      <!--参考附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.refAttachment'),
          prop: 'requireDocId',
          nameProp: 'requireFileName'
        }"
        readonly
      />

      <!--采购商备注-->
      <el-table-column
        prop="requireRemark"
        :label="$t('bidMod.vendorRemark')"
        show-overflow-tooltip
      />

      <!--文件类型-->
      <el-table-column
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.fileType"
            code="SOU_FILE_CONFIG_TYPE"
            :transform-options="transformFileTypeOptions"
            :disabled="!!scope.row.requireDocId || readonly"
          />
        </template>
      </el-table-column>

      <!--要求上传附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.bidAttachment'),
          prop: 'orderDocId',
          nameProp: 'orderFileName'
        }"
        :readonly="readonly"
        @on-change="fileChange"
      />

      <!--备注-->
      <el-table-column
        prop="orderRemark"
        :label="$t('common.remark')"
      >
        <template v-slot="scope">
          <el-input v-model="scope.row.orderRemark" :disabled="readonly" />
        </template>
      </el-table-column>

      <!--操作-->
      <el-table-column :label="$t('common.operation')" width="80">
        <template v-slot="{ row, $index }">
          <el-button
            v-if="!row.requireDocId"
            type="text"
            :disabled="readonly"
            @click="deleteRow($index)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { SOU_FILE_CONFIG_TYPE_ENUM } from 'lib@/composition/origin/enum'
/**
 * 附件信息
 */
export default {
  name: 'FileContainer',

  props: {
    orderFileList: {
      type: Array,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  computed: {
    orderFileListData: {
      get: function () {
        return this.orderFileList
      },
      set: function (value) {
        this.$emit('update:orderFileList', value)
      }
    }
  },

  methods: {
    /* 新增行 */
    addRow () {
      this.orderFileListData.push({
        fileType: '',
        orderDocId: '',
        orderFileName: '',
        orderRemark: ''
      })
    },

    /* 删除行 */
    deleteRow ($index) {
      this.orderFileListData.splice($index, 1)
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.orderFileListData[$index].orderDocId = fileId
      this.orderFileListData[$index].orderFileName = fileName
    },
     /* 过滤文件类型 */
    transformFileTypeOptions (options) {
      return options.filter(item => item.value !== SOU_FILE_CONFIG_TYPE_ENUM.TECH_FILE)
    },
  }
}
</script>
