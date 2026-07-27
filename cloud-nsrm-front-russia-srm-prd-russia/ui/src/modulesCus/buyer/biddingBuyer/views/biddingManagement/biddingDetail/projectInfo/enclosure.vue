<template>
  <div class="the_file_part">
    <!--申请资料-->
    <div class="left_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("cusEntry.bidMod.applyFileList") }}</span>
        <!--添加-->
        <el-button type="primary" @click="innerFilesAddRow">
          {{ $t("common.new") }}
        </el-button>
      </p>

      <el-table
        ref="applyFileList"
        :data="applyFileList"
        style="width: 100%"
        border
        height="150px"
      >
        <el-table-column
          align="center"
          type="index"
          fixed="left"
          :label="$t('common.sort')"
          width="50"
        />

        <!--包名-->
        <el-table-column
          v-if="biddingBase.mergeFlag"
          align="center"
          prop="extPackageName"
          :label="$t('cusEntry.biddingSettings.bagName')"
          min-width="100"
        >
          <template v-slot="scope">
            <el-select v-model="scope.row.extPackageName">
              <el-option
                v-for="(item, index) in packNameList"
                :key="index"
                :label="item"
                :value="item"
              />
            </el-select>
          </template>
        </el-table-column>

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :extra-data="fileInfo"
          :readonly="readOnly"
          @on-change="innerFilesChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="150"
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
          <template v-slot="{ $index }">
            <el-button type="text" @click="innerFilesDeleteRow($index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--招标文件-->
    <div class="right_div">
      <p style="margin: 0 0 10px 0">
        <span>{{ $t("cusEntry.bidMod.bidFileList") }}</span>
        <!--添加-->
        <el-button type="primary" @click="outerFilesAddRow">
          {{ $t("common.new") }}
        </el-button>
      </p>

      <el-table
        ref="bidFileList"
        :data="bidFileList"
        style="width: 100%"
        border
        height="150px"
      >
        <el-table-column
          align="center"
          type="index"
          fixed="left"
          :label="$t('common.sort')"
          width="50"
        />

        <!--包名-->
        <el-table-column
          v-if="biddingBase.mergeFlag"
          align="center"
          prop="extPackageName"
          :label="$t('cusEntry.biddingSettings.bagName')"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-select v-model="scope.row.extPackageName">
              <el-option
                v-for="(item, index) in packNameList"
                :key="index"
                :label="item"
                :value="item"
              />
            </el-select>
          </template>
        </el-table-column>

        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.fileName'),
            prop: 'souDocId',
            nameProp: 'souFileName'
          }"
          :extra-data="fileInfo"
          :readonly="readOnly"
          @on-change="outerFilesChange"
        />

        <!--备注-->
        <el-table-column
          align="center"
          prop="souRemark"
          :label="$t('bidMod.remark')"
          min-width="150"
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
    biddingBase: {
      type: Object,
      required: true
    },
    packNameList: {
      type: Array,
      default: () => []
    },
    detailData: {
      type: Object,
      default: () => {}
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      applyFileList: [],
      bidFileList: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'BID_PUBLISH',
        fileType: 'images'
      }
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.applyFileList = val.applyFileList || []
        this.bidFileList = val.bidFileList || []
        this.$nextTick(() => {
          this.$refs.applyFileList.doLayout()
          this.$refs.bidFileList.doLayout()
        })
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 新增一行内部查看附件 */
    innerFilesAddRow () {
      this.applyFileList.push({
        fileType: 'APPLY',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 内部查看文件变更 */
    innerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.applyFileList[$index].souDocId = fileId
      this.applyFileList[$index].souFileName = fileName
    },

    /* 删除一行内部查看附件行 */
    innerFilesDeleteRow (index) {
      this.applyFileList.splice(index, 1)
    },

    /* 新增一行供方查看附件 */
    outerFilesAddRow () {
      this.bidFileList.push({
        fileType: 'BID',
        souDocId: '',
        souFileName: '',
        souRemark: ''
      })
    },

    /* 删除一行供方查看附件行 */
    outerFilesDeleteRow (index) {
      this.bidFileList.splice(index, 1)
    },

    /* 内部查看文件变更 */
    outerFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.bidFileList[$index].souDocId = fileId
      this.bidFileList[$index].souFileName = fileName
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return {
        applyFileList: this.applyFileList,
        bidFileList: this.bidFileList
      }
    },

    /* 清除数据 */
    clearData () {
      this.applyFileList = []
      this.bidFileList = []
    }
  }
}
</script>
