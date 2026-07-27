<template>
  <div class="the-file-part">
    <div class="left-div">
      <p style="margin-top: 0">
        <span>{{ $t('cusEntry.competition.applyDocumentsFile') }}</span>
        <el-button
          type="primary"
          @click="addInnerFileItem"
        >
          {{ $t('common.new') }}
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
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('cusEntry.competition.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :readonly="readonly"
          @on-change="innerFileChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.souRemark" />
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="scope">
            <el-button type="text" @click="deleteInnerFileItem(scope.$index, scope.row)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="right-div">
      <p style="margin-top: 0">
        <span>{{ $t('cusEntry.competition.bidDocumentsFile') }}</span>
        <!--添加-->
        <el-button
          type="primary"
          @click="addOuterFileItem"
        >
          {{ $t('common.new') }}
        </el-button>
      </p>

      <el-table
        ref="outerFilesTable"
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
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('cusEntry.competition.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName',
            renderHeader: _addStarToColumn
          }"
          :readonly="readonly"
          @on-change="outerFileChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.souRemark" />
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="operation"
          :label="$t('bidMod.operation')"
          width="80"
        >
          <template v-slot="scope">
            <el-button type="text" @click="deleteOuterFileItem(scope.$index, scope.row)">
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
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'

export default {
  name: 'Enclosure',

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
        fileModular: 'comp',
        fileFunction: 'enclosure',
        fileType: 'images'
      },
      bankRowIndex: ''
    }
  },
  computed: {
    innerFiles () {
      return this.detailData.filter(item => item.fileType === 'INNER')
    },
    outerFiles () {
      return this.detailData.filter(item => item.fileType === 'OUTER')
    }
  },
  methods: {
    /* 添加内部查看附件行 */
    addInnerFileItem () {
      this.innerFiles.push({
        fileType: 'INNER',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 添加外部查看附件行 */
    addOuterFileItem () {
      this.outerFiles.push({
        fileType: 'OUTER',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 删除内部查看附件行 */
    deleteInnerFileItem (index) {
      this.innerFiles.splice(index, 1)
    },

    /* 删除外部查看附件行 */
    deleteOuterFileItem (index) {
      this.outerFiles.splice(index, 1)
    },

    /* 文件变更 */
    innerFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.innerFiles[$index].souDocId = fileId
      this.innerFiles[$index].souFileName = fileName
    },

    /* 文件变更 */
    outerFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.outerFiles[$index].souDocId = fileId
      this.outerFiles[$index].souFileName = fileName
    },

    /* 校验供方报名附件 */
    validateOuterFilesTable () {
      // 校验表格必填项
      return validateRequiredColumn(
        this.$refs.outerFilesTable,
        this.outerFiles,
        {
          validateScope: false,
          tableTitle: '供方查看附件'
        }
      )
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return {
        innerFiles: this.innerFiles,
        outerFiles: this.outerFiles
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

    > p > span {
      padding-right: 11px;
    }
  }
}
</style>
