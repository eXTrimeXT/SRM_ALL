<template>
  <div class="the-file-part">
    <!--内部查看附件-->
    <div class="left-div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.innerFileList") }}</span>
        <!--新增-->
        <el-button
          v-if="!readonly"
          type="primary"
          @click="innerFilesDataAddRow"
        >
          {{ $t('common.add') }}
        </el-button>
      </p>

      <el-table
        :data="innerFilesData"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--上传附件-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :readonly="readonly"
          @on-change="innerFilesDataChange"
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
            <el-button type="text" @click="innerFilesDataDeleteRow($index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--供方查看附件-->
    <div class="right-div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("bidMod.supplierFileList") }}</span>
        <!--新增-->
        <el-button
          v-if="!readonly"
          type="primary"
          @click="outerFilesDataAddRow"
        >
          {{ $t('common.add') }}
        </el-button>
      </p>

      <el-table
        :data="outerFilesData"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--上传附件-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :readonly="readonly"
          @on-change="outerFilesDataChange"
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
            <el-button type="text" @click="outerFilesDataDeleteRow($index)">
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
    innerFiles: {
      type: Array,
      default: () => []
    },
    outerFiles: {
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
      innerFilesData: [],
      outerFilesData: []
    }
  },

  watch: {
    innerFiles: {
      handler (val) {
        this.innerFilesData = val && Array.isArray(val) ? val.concat() : []
      },
      immediate: true,
      deep: true
    },
    outerFiles: {
      handler (val) {
        this.outerFilesData = val && Array.isArray(val) ? val.concat() : []
      },
      immediate: true,
      deep: true
    }

  },

  methods: {
    /* 新增一行内部查看附件 */
    innerFilesDataAddRow () {
      this.innerFilesData.push({
        fileType: 'INNER',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 删除一行内部查看附件行 */
    innerFilesDataDeleteRow (index) {
      this.innerFilesData.splice(index, 1)
    },

    /* 内部查看文件变更 */
    innerFilesDataChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.innerFilesData[$index].souDocId = fileId
      this.innerFilesData[$index].souFileName = fileName
    },

    /* 内部查看文件变更 */
    outerFilesDataChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.outerFilesData[$index].souDocId = fileId
      this.outerFilesData[$index].souFileName = fileName
    },

    /* 新增一行供方查看附件 */
    outerFilesDataAddRow () {
      this.outerFilesData.push({
        fileType: 'OUTER',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 删除一行供方查看附件行 */
    outerFilesDataDeleteRow (index) {
      this.outerFilesData.splice(index, 1)
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return {
        innerFiles: this.innerFilesData.concat(),
        outerFiles: this.outerFilesData.concat()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.the-file-part {
  .left-div,
  .right-div {
    width: 50%;
    padding: 3px;
    float: left;
    > p {
      margin: 0;
      span {
        padding-right: 11px;
      }
    }
  }
}
</style>
