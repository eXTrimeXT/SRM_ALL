<template>
  <div class="material-file-wrap">
    <!--新增材料-->
    <el-button
      type="primary"
      :disabled="readonly"
      @click="addRow"
    >
      {{ $t('meeting.addMaterial') }}
    </el-button>

    <el-table
      :data="materialFileData"
      border
      stripe
      style="margin-top: 10px"
    >
      <el-table-column
        type="index"
        width="60"
        :label="$t('common.sort')"
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :extra-data="fileInfo"
        :table-column-options="{
          label: $t('vendorMod.attachmentName'),
          prop: 'fileuploadId',
          nameProp: 'materialMould'
        }"
        :readonly="readonly"
        @on-change="fileChange"
      />

      <!--创建人-->
      <el-table-column prop="createdFullName" :label="$t('common.creator')" />

      <!--创建时间-->
      <el-table-column prop="creationDate" :label="$t('common.creationTime')" />

      <el-table-column :label="$t('common.operation')" width="100">
        <template v-slot="{ $index }">
          <el-button
            type="text"
            :disabled="readonly"
            @click="deleteRow($index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 上会材料
 */
export default {
  name: 'MeetTopicsDetailMaterialFile',

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    detailData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      fileInfo: {
        fileModular: 'meetManagement',
        fileFunction: 'meetTopics',
        fileType: 'file'
      },
      materialFileData: []
    }
  },

  watch: {
    detailData: {
      handler (val) {
        if (val && Array.isArray(val)) {
          this.materialFileData = val.concat()
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 新增行 */
    addRow () {
      this.materialFileData.push({
        fileuploadId: '',
        materialMould: ''
      })
    },

    /* 删除行 */
    deleteRow ($index) {
      this.materialFileData.splice($index, 1)
    },

    /* 表格文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.materialFileData[$index].fileuploadId = fileId
      this.materialFileData[$index].materialMould = fileName
    }
  }
}
</script>
