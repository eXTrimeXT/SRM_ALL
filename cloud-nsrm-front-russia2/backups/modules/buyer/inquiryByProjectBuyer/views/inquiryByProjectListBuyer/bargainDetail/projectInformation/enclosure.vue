<template>
  <div class="the_file_part">
    <!--内部查看附件-->
    <div class="left_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.innerFileList") }}</span>
        <!--添加-->
        <el-button
          type="primary"
          class="detail-pbtn"
          @click="innerFilesAddRow"
        >
          {{ $t("common.new") }}
        </el-button>
      </p>

      <el-table
        :data="innerFiles"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          align="center"
          type="index"
          width="30"
        />

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'docId',
            nameProp: 'fileName'
          }"
          :readonly="readOnly"
          @on-change="innerFilesChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="comments"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.comments" />
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="scope">
            <el-button
              type="primary"
              icon="el-icon-delete"
              class="el-button-icon"
              @click="innerFilesDeleteRow(scope.$index)"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--供方查看附件-->
    <div class="right_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.supplierFileList") }}</span>
        <!--添加-->
        <el-button
          type="primary"
          class="detail-pbtn"
          @click="outerFilesAddRow"
        >
          {{ $t("common.new") }}
        </el-button>
      </p>

      <el-table
        :data="outerFiles"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          align="center"
          type="index"
          width="30"
        />

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'docId',
            nameProp: 'fileName'
          }"
          :readonly="readOnly"
          @on-change="outerFilesChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="comments"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.comments" />
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="scope">
            <el-button
              type="primary"
              icon="el-icon-delete"
              class="el-button-icon"
              @click="outerFilesDeleteRow(scope.$index)"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
/**
 * 查看附件
 */

export default {
  name: 'Enclosure',

  props: {
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
      innerFiles: [],
      outerFiles: []
    }
  },
  watch: {
    detailData: {
      handler (val) {
        if (val && Array.isArray(val)) {
          this.innerFiles = val.filter(v => v.fileType === 'INNER')
          this.outerFiles = val.filter(v => v.fileType === 'OUTER')
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    /* 新增一行内部查看附件 */
    innerFilesAddRow () {
      this.innerFiles.push({
        fileType: 'INNER',
        docId: '',
        fileName: '',
        comments: ''
      })
    },

    /* 内部查看文件变更 */
    innerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.innerFiles[$index].docId = fileId
      this.innerFiles[$index].fileName = fileName
    },

    /* 删除一行内部查看附件行 */
    innerFilesDeleteRow (index) {
      this.innerFiles.splice(index, 1)
    },

    /* 新增一行供方查看附件 */
    outerFilesAddRow () {
      this.outerFiles.push({
        fileType: 'OUTER',
        docId: '',
        fileName: '',
        comments: ''
      })
    },

    /* 删除一行供方查看附件行 */
    outerFilesDeleteRow (index) {
      this.outerFiles.splice(index, 1)
    },

    /* 内部查看文件变更 */
    outerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.outerFiles[$index].docId = fileId
      this.outerFiles[$index].fileName = fileName
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return [...this.innerFiles, ...this.outerFiles].map(item => {
        return {
          fileType: item.fileType,
          docId: item.docId,
          fileName: item.fileName,
          comments: item.comments
        }
      })
    },

    /* 清除数据 */
    clearData () {
      this.innerFiles = []
      this.outerFiles = []
    }
  }
}
</script>
