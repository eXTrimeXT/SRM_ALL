// 资质审查、供方评审、物料试用、样品确认 使用该组件
<template>
  <div class="accessAttachment">
    <el-button
      type="primary"
      class="attBtn detail-pbtn"
      :disabled="attOpt === 'view'"
      @click="addAttaches"
    >
      {{ $t("common.add") }}
    </el-button>
    <el-table
      ref="attachTable"
      :data="data"
      stripe
      border
      style="width: 100%"
      max-height="250px"
      tooltip-effect="dark"
    >
      <!-- 附件名称 -->
      <el-table-column
        prop="templateDesc"
        :label="$t('vendorMod.attachmentName')"
        min-width="120"
      >
        <template slot-scope="scope">
          <el-input
            v-if="!scope.row.fileConfigId && attOpt !== 'view'"
            v-model="scope.row.templateDesc"
          />
          <span v-else>
            <i
              v-if="scope.row.ifRequired === 'Y'"
              style="color:#ff4949;padding-right:4px;"
            >*</i>{{ scope.row.templateDesc }}</span>
        </template>
      </el-table-column>
      <!-- 模板下载 -->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('vendorMod.templateDownload'),
          prop: 'templateFileId',
          nameProp: 'templateFileName'
        }"
        readonly
      />
      <!-- 附件上传 -->
      <el-table-column
        prop="fileValidDate"
        width="160"
        class-name="validDate"
        :label="$t('vendorMod.attachmentUpload')"
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.fileId,
              fileName: scope.row.fileName
            }"
            :readonly="attOpt == 'view'"
            @on-change="(file) => templateReferenceFilesChange(file,scope)"
          />
        </template>
      </el-table-column>
      <!-- 备注 -->
      <el-table-column
        v-if="senceCode === 'AUTH' || senceCode === 'QUA'"
        prop="remark"
        :label="$t('vendorMod.remark')"
        min-width="120"
      >
        <template slot-scope="scope">
          <el-input
            v-if="attOpt !== 'view'"
            v-model="scope.row.remark"
          />
          <span v-else>{{ scope.row.remark }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="attOpt !== 'view'"
        fixed="right"
        :label="$t('common.operation')"
        width="80"
        align="center"
      >
        <template slot-scope="scope">
          <el-button
            v-if="!scope.row.fileConfigId"
            type="text"
            @click="delAttaches(scope.$index, scope.row)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { formatDate } from '@/utils'
import _cloneDeep from 'lodash/cloneDeep'
import {
  parseTime
} from '@/utils'
export default {
  name: 'SceneAttachment',
  filters: {
    formateDate: function (val) {
      return parseTime(val)
    }
  },
  // model: {
  //   prop: 'data',
  //   event: 'change'
  // },
  props: {
    value: {
      type: Array,
      default () {
        return []
      }
    },
    senceCode: {
      // 场景编码 AUTH:供应商评审,SAMPLE:样品确认,MATERIAL:物料试用,QUA:资质审查
      type: String,
      default: () => ''
    },
    businessId: {
      // 业务单据Id
      type: [Number, String],
      default: () => null
    },
    attOpt: {
      // 操作类型 add edit view
      type: String,
      default: 'add'
    },
    upFileInfo: {
      // 文件上传配置信息
      type: Object,
      default: () => {}
    },
    fileRefresh: {
      type: Boolean,
      default: () => false
    },
    validate: {
      // 标识有效期至
      type: Boolean,
      default: () => false
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorManagement',
        fileType: 'images'
      },
      total: null,
      pageSize_approvalBiddingItemLis: 40, // 每页数量
      currentPage: 1, // 分页初始页码
      attachData: [], // 附件数据
      isHasConf: false, // 是否有配置魔板信息
      tempData: [], // 模板数据
      uploadData: [], // 已上传数据
      changeUploadData: [] // 变更的数据
    }
  },
  computed: {
    data () {
      return this.value
    }
  },


  created () {
    console.log(this.data)
  },
  mounted () {
    console.log(this.data)
  },
  methods: {
    // 修改表格的时候调用
    tableChange (data) {
      this.$emit('changes', data)
    },
    // 新增附件
    addAttaches () {
      this.value.push({
        templateDesc: null,
        templateFileId: null,
        templateFileName: null,
        fileId: null,
        fileName: null,
        ifRequired: 'N',
        ifValidDate: 'N',
        fileValidDate: null,
        reviewPeople: null,
        vendorAssessor: null,
        reviewDate: null,
        score: null,
        authResult: null,
        remark: null
      })
    },
    // 上传附件成功
    templateReferenceFilesChange (file,scope) {
      const { fileId = null, fileName = '' } = file.file || {}
      this.value.splice(scope.$index,1,{...scope.row,fileId,fileName})
    },
    // 操作列 删除附件
    delAttaches (index, row) {
      this.value.splice(index, 1)
    },
    // 附件上传】】
    // 校验附件上传
    validRequired () {
      let fileRecords = this.value
      let valid = true
      for (let i = 0; i < fileRecords.length; i++) {
        if (fileRecords[i].ifRequired === 'Y' && !fileRecords[i].fileId) {
          // 设置了必传
          // 请上传
          this.$message.error(
            this.$t('vendorMod.msgUpload') +
              fileRecords[i].templateDesc +
              this.$t('vendorMod.msgAttachInfo')
          )
          valid = false
          return false
        }
        if (
          fileRecords[i].ifValidDate === 'Y' &&
          !fileRecords[i].fileValidDate
        ) {
          // 设置了有效期必填
          this.$message.error(
            this.$t('vendorMod.msgMaintain') +
              fileRecords[i].templateDesc +
              this.$t('vendorMod.msgAttachDeadline')
          )
          valid = false
          return false
        }
      }
      return valid
    }
  }
}
</script>

<style scope>
.accessAttachment .attBtn {
  margin-bottom: 16px;
}
.required {
  color: #ff4949;
  padding-right: 4px;
}
.validDate .cell {
  display: flex;
  flex: 1;
}
</style>
