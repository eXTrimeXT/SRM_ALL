<template>
  <div class="file-wrapper">
    <!-- <div class="btns" style="margin-bottom:10px;">
      <el-button type="primary" @click="downloadTemplate">
        下载内部通知书模板
      </el-button>
    </div> -->
    <el-table
      border
      stripe
      :data="tableData"
      :column="tableColumns"
    >
      <el-table-column
        type="index"
        width="60"
        label="序号"
      />

      <el-table-column
        label="板块"
        prop="extOrgBuName"
        showshowOverflowTooltip
      />

      <el-table-column
        label="公司"
        prop="extOrgOuName"
        showshowOverflowTooltip
      />

      <el-table-column
        label="需求部门"
        prop="demandDepartmentName"
        showshowOverflowTooltip
      />

      <el-table-column
        label="附件模板"
      >
        <template v-slot="scope">
          <el-button type="text" @click="downloadTemplate(scope.row)">
            附件模板
          </el-button>
        </template>
      </el-table-column>

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          align: 'left',
          label: '通知书附件',
          prop: 'attachmentId',
          nameProp: 'attachmentName',
          renderHeader:_addStarToColumn
        }"
        :validateOptions="{
          accept:['.pdf'],
          size: null
        }"
        :readonly="readonly"
        @on-change="filesChange"
      />

      <!--是否发送-->
      <!-- <el-table-column
        v-if="showSend"
        prop="isSend"
        label="是否发送"
        min-width="100"
        :formatter="(row,column,cellValue) => $getDictLabel('YES_OR_NO',cellValue)"
      /> -->

      <!-- 发送通知书 -->
      <!-- <el-table-column
        v-if="showSend"
        label="发送通知书"
      >
        <template v-slot="scope">
          <el-button type="text" @click="sendNotice(scope.row)">
            发送
          </el-button>
        </template>
      </el-table-column> -->
    </el-table>
  </div>
</template>
<script>
import { transformMQL } from 'lib@/utils/util'
import bidNoticeHttp from '../api'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      tableColumns: []
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    },
    showSend () {
      return this.form.status === 'APPROVED'
    }
  },
  methods: {
    addRows () {
      this.tableData.push({
        souDocId: '',
        souFileName: ''
      })
    },
    downloadTemplate (row) {
      downloadFileLink(
        `/api-sou/sou/api/v1/bidNotice/downloadProjectNoticeInternalTemplate?bidNoticeInternalId=${row.internalId}`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 内部查看文件变更 */
    filesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].attachmentId = fileId
      this.tableData[$index].attachmentName = fileName
    },
    deleteRows (index) {
      this.tableData.splice(index, 1)
    },
    getParamsData () {
      return this.tableData
    },
    async sendNotice (row) {
      let transformParams = transformMQL.save('BidNoticeInternal', [{ ...row }], 'send')
      const response = await bidNoticeHttp.internalSendNotice(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.$emit('send')
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.btns {
  margin-bottom: 10px;
}
</style>
