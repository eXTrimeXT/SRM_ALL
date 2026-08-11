<template>
  <div class="the_file_part">
    <!--内部查看附件-->
    <div class="left_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.innerFileList") }}</span>
        <!--添加-->
        <el-button type="primary" @click="innerFilesAddRow">
          {{ $t("common.add") }}
        </el-button>
      </p>

      <el-table
        :data="innerFiles"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :readonly="readonly"
          @on-change="innerFilesChange"
        />

        <!--备注-->
        <el-table-column
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.souRemark" />
          </template>
        </el-table-column>

        <el-table-column
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="{ $index }">
            <el-button type="text" @click="innerFilesDeleteRow($index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--供方查看附件-->
    <div class="right_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.supplierFileList") }}</span>
        <!--添加-->
        <el-button type="primary" @click="outerFilesAddRow">
          {{ $t("common.add") }}
        </el-button>
      </p>

      <el-table
        :data="outerFiles"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :readonly="readonly"
          @on-change="outerFilesChange"
        />

        <!--备注-->
        <el-table-column
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.souRemark" />
          </template>
        </el-table-column>

        <el-table-column
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="{ $index }">
            <el-button type="text" @click="outerFilesDeleteRow($index)">
              {{ $t('common.delete') }}
            </el-button>
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
      type: Object,
      default: () => {}
    },
    readonly: {
      type: Boolean,
      default: false
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
        this.innerFiles = (val?.innerFiles || []).concat()
        this.outerFiles = (val?.outerFiles || []).concat()
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
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 内部查看文件变更 */
    innerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.innerFiles[$index].souDocId = fileId
      this.innerFiles[$index].souFileName = fileName
    },

    /* 删除一行内部查看附件行 */
    innerFilesDeleteRow (index) {
      this.innerFiles.splice(index, 1)
    },

    /* 新增一行供方查看附件 */
    outerFilesAddRow () {
      this.outerFiles.push({
        fileType: 'OUTER',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 删除一行供方查看附件行 */
    outerFilesDeleteRow (index) {
      this.outerFiles.splice(index, 1)
    },

    /* 内部查看文件变更 */
    outerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.outerFiles[$index].souDocId = fileId
      this.outerFiles[$index].souFileName = fileName
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return {
        innerFileList: this.innerFiles,
        outerFileList: this.outerFiles
      }
    },

    /* 清除数据 */
    clearData () {
      this.innerFiles = []
      this.outerFiles = []
    }
  }
}
</script>
