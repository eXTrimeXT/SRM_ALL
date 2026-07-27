<template>
  <div class="tech-info">
    <p>
      <!--附件信息-->
      <span style="padding: 0 11px">{{ $t('bidMod.adjunctInformation') }}</span>
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      :data="orderFileListData"
      style="width: 100%"
      border
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--资料要求-->
      <el-table-column
        prop="reqMsg"
        :label="$t('bidMod.fileQualify')"
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
        prop="reqComments"
        :label="$t('bidMod.vendorRemark')"
        show-overflow-tooltip
      />

      <!--文件类型-->
      <el-table-column
        prop="fileType"
        :label="$t('bid_mod.referenceFileType')"
      >
        <template slot-scope="scope">
          <dict-select
            v-model="scope.row.fileType"
            code="SOU_COMP_FILE_CONFIG"
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
          prop: 'orderDocId',
          nameProp: 'orderFileName'
        }"
        @on-change="fileChange"
      />

      <!--备注-->
      <el-table-column
        prop="orderComments"
        :label="$t('common.remark')"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.orderComments" />
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
            @click="deleteRow(scope.$index, scope.row)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 附件信息
 */
export default {
  name: 'TechInfo',

  props: {
    orderFileList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      bankRowIndex: '',
      fileInfo: {
        fileModular: 'comp',
        fileFunction: 'cpmp',
        fileType: 'images'
      }
    }
  },

  computed: {
    orderFileListData: {
      get: function () {
        return this.orderFileList
      },
      set: function (val) {
        return this.$emit('update:orderFileList', val)
      }
    }
  },

  methods: {
    addRow () {
      this.orderFileListData.push({
        orderDocId: '',
        fileType: '',
        orderFileName: '',
        orderComments: ''
      })
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.orderFileListData[$index].orderDocId = fileId
      this.orderFileListData[$index].orderFileName = fileName
    },

    /* 删除行 */
    deleteRow (index, row) {
      if (!row.reqDocId) {
        this.orderFileListData.splice(index, 1)
      }
    },

    /* 删除一个文件 */
    removeFile (row) {
      row.orderDocId = ''
      row.orderFileName = ''
    }
  }
}
</script>
