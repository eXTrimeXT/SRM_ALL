<template>
  <srm-dialog
    :title="$t('bidMod.clarification')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="the_clarification_dialog">
      <el-form
        ref="clarificationForm"
        :model="clarificationFormData"
        label-position="top"
        label-width="140px"
        :rules="clarificationFormRules"
        :disabled="isReadOnly"
      >
        <srm-row>
          <!-- 澄清编号 -->
          <srm-col :init-col="3">
            <el-form-item :label="$t('bidMod.answerNum')">
              <el-input v-model="clarificationFormData.answerNum" disabled />
            </el-form-item>
          </srm-col>

          <!-- 项目名称 -->
          <srm-col :init-col="3">
            <el-form-item :label="$t('bidMod.bidingName')" prop="bargainName">
              <el-select
                v-model="clarificationFormData.bargainName"
                filterable
                remote
                :placeholder="$t('bidMod.msgKeyword')"
                :remote-method="remoteMethod"
                clearable
                :disabled="isFromQuestion"
                automatic-dropdown
                @change="elSelectChange"
              >
                <el-option
                  v-for="(item, index) in options"
                  :key="item.id + index"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </srm-col>

          <!-- 澄清标题 -->
          <srm-col :init-col="3">
            <el-form-item prop="answerTitle" :label="$t('bidMod.answerTitle')">
              <el-input v-model="clarificationFormData.answerTitle" />
            </el-form-item>
          </srm-col>

          <!-- 项目编号 -->
          <srm-col :init-col="3">
            <el-form-item :label="$t('bidMod.bidingNum')">
              <el-input v-model="clarificationFormData.bargainNum" disabled />
            </el-form-item>
          </srm-col>
        </srm-row>

        <!-- 项目信息变更 -->
        <h3>{{ $t("bidMod.proInfoChange") }}</h3>

        <h4>变更前</h4>

        <el-table :data="prefixTable" style="width: 100%;" border>
          <!--报名截止时间-->
          <el-table-column
            align="center"
            prop="prefixEnrollEndTime"
            :label="$t('bidMod.registrationDeadline')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.prefixEnrollEndTime" disabled />
            </template>
          </el-table-column>

          <!--投标开始时间-->
          <el-table-column
            align="center"
            prop="prefixBargainStartTime"
            :label="$t('bidMod.bidingStartDatetime')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.prefixBargainStartTime" disabled />
            </template>
          </el-table-column>

          <!--投标结束时间-->
          <el-table-column
            align="center"
            prop="prefixBargainEndTime"
            :label="$t('bidMod.bidingEndDatetime')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.prefixBargainEndTime" disabled />
            </template>
          </el-table-column>

          <!--投标地点-->
          <el-table-column
            align="center"
            prop="prefixBargainSite"
            :label="$t('bidMod.bidingSite2')"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.prefixBargainSite" disabled />
            </template>
          </el-table-column>
        </el-table>

        <h4>变更后</h4>

        <el-table :data="suffixTable" style="width: 100%;" border>
          <!--报名截止时间-->
          <el-table-column
            align="center"
            prop="suffixEnrollEndTime"
            :label="$t('bidMod.registrationDeadline')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.suffixEnrollEndTime" :disabled="isReadOnly" />
            </template>
          </el-table-column>

          <!--投标开始时间-->
          <el-table-column
            align="center"
            prop="suffixBargainStartTime"
            :label="$t('bidMod.bidingStartDatetime')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.suffixBargainStartTime" :disabled="isReadOnly" />
            </template>
          </el-table-column>

          <!--投标结束时间-->
          <el-table-column
            align="center"
            prop="suffixBargainEndTime"
            :label="$t('bidMod.bidingEndDatetime')"
          >
            <template v-slot="scope">
              <el-date-picker v-model="scope.row.suffixBargainEndTime" :disabled="isReadOnly" />
            </template>
          </el-table-column>

          <!--投标地点-->
          <el-table-column
            align="center"
            prop="suffixBargainSite"
            :label="$t('bidMod.bidingSite2')"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.suffixBargainSite" :disabled="isReadOnly" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>

      <!-- 附件 -->
      <h3>
        {{ $t("bidMod.attachment") }}
        <el-button v-if="!isReadOnly" type="primary" @click="addOne">
          {{ $t("common.add") }}
        </el-button>
      </h3>

      <el-table :data="fileList" style="width: 100%" border height="121px">
        <el-table-column align="center" type="index" width="50" />

        <!-- 附件名称 -->
        <SrmCommonFile
          type="table-column"
          :extra-data="fileInfo"
          :table-column-options="{
            label: $t('bidMod.attachmentName'),
            prop: 'docId',
            nameProp: 'fileName'
          }"
          :readonly="isReadOnly"
          @on-change="fileChange"
        />

        <!-- 备注 -->
        <el-table-column align="center" prop="remark" :label="$t('bidMod.remark')">
          <template slot-scope="scope">
            <el-input v-model="scope.row.remark" :disabled="isReadOnly" />
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column
          v-if="!isReadOnly"
          align="center"
          prop="operation"
          :label="$t('common.operation')"
          width="100"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="deleOne(scope.$index, scope.row)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 澄清内容 -->
      <h3>{{ $t("bidMod.clarifyContent") }}</h3>
      <Tinymce
        id="inquiryBuyerClarificationTinymce"
        v-model="answerInfo"
        :height="260"
        @setup="ready"
      />
    </div>

    <div slot="footer">
      <!--取消-->
      <el-button @click="cancel">
        {{ $t("common.cancel") }}
      </el-button>

      <!--暂存-->
      <el-button v-if="!isReadOnly" type="primary" @click="tempSaveOrSubmit('save')">
        {{ $t("common.staging") }}
      </el-button>

      <!--发布-->
      <el-button v-if="!isReadOnly" type="primary" @click="tempSaveOrSubmit('submit')">
        {{ $t('common.publish') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 新增 / 查看 澄清弹窗
 */
import Tinymce from '@/components/Tinymce'

export default {
  name: 'ClarificationDetailDialog',
  components: { Tinymce },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRow: {
      type: Object
    },
    pageType: {
      type: String,
      default: 'add'
    },
    // 是否来源质疑
    isFromQuestion: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      clarificationFormData: {},
      options: [],
      fileList: [],
      answerInfo: null,
      clarificationFormRules: {
        // 请选择项目
        bargainName: { required: true, message: this.$t('bidMod.msgSelProject') },
        // 请填写标题
        answerTitle: { required: true, message: this.$t('bidMod.msgInputTitle') }
      },
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'inquiry',
        fileType: 'images'
      },
      prefixTable: [
        {
          prefixEnrollEndTime: '',
          prefixBargainStartTime: '',
          prefixBargainEndTime: '',
          prefixBargainSite: ''
        }
      ],
      suffixTable: [
        {
          suffixEnrollEndTime: '',
          suffixBargainStartTime: '',
          suffixBargainEndTime: '',
          suffixBargainSite: ''
        }
      ],
      bankRowIndex: ''
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    isReadOnly () {
      return this.pageType === 'view'
    }
  },
  created () {
    if (this.pageType === 'add') {
      // 新增
      if (this.isFromQuestion) {
        // 来自质疑
        this.queryBargainInfo(this.editRow.bargainId)
        this.clarificationFormData = {
          ...this.clarificationFormData,
          ...this.editRow
        }
      }
    } else {
      this.getDetail()
    }
  },
  methods: {
    ready (editorInstance) {
      // 禁用
      if (this.isReadOnly) {
        editorInstance.setMode('readonly')
      }
    },
    /* 查询详情 */
    getDetail () {
      this.$api.brg.inquiryByProject.getBargainAnswerDetail(this.editRow.answerId).then(data => {
        if (data && data.data) {
          const { fileList, answerContent, ...rest } = data.data
          this.queryCompanyList({ bargainNum: rest.bargainNum })

          this.clarificationFormData = { ...rest }
          this.answerInfo = answerContent
          this.fileList = fileList

          this.prefixTable = [
            {
              prefixEnrollEndTime: rest.prefixEnrollEndTime,
              prefixBargainStartTime: rest.prefixBargainStartTime,
              prefixBargainEndTime: rest.prefixBargainEndTime,
              prefixBargainSite: rest.prefixBargainSite
            }
          ]

          this.suffixTable = [
            {
              suffixEnrollEndTime: rest.suffixEnrollEndTime,
              suffixBargainStartTime: rest.suffixBargainStartTime,
              suffixBargainEndTime: rest.suffixBargainEndTime,
              suffixBargainSite: rest.suffixBargainSite
            }
          ]
        }
      })
    },

    /* 查询项目列表 */
    queryCompanyList ({ bargainName = '', bargainNum = '' }) {
      this.$api.brg.inquiryByProject.queryCompanyList({
          bargainName,
          bargainNum,
          pageSize: 999,
          pageNum: 1
        }).then(res => {
        this.options = res.data.list.map(i => ({
          id: i.bargainId,
          value: i.bargainNum,
          label: i.bargainName
        }))
      })
    },

    /* 远程查询项目列表 */
    remoteMethod (bargainName) {
      if (bargainName) {
        this.queryCompanyList({ bargainName })
      } else {
        this.options = []
      }
    },

    /* 查询询价单数据 */
    queryBargainInfo (bargainId) {
      return this.$api.brg.inquiryByProject.getProjectInfo(bargainId).then(data => {
        if ((data || {}).data) {
          const {
            enrollEndDatetime,
            bargainStartDatetime,
            bargainSite,
            bargainName,
            bargainEndDatetime,
            bargainNum
          } = data.data.bargain
          this.clarificationFormData = {
            ...this.clarificationFormData,
            enrollEndDatetime,
            bargainStartDatetime,
            bargainSite,
            bargainId,
            bargainName,
            bargainNum
          }
          this.prefixTable = [
            {
              prefixEnrollEndTime: enrollEndDatetime,
              prefixBargainStartTime: bargainStartDatetime,
              prefixBargainEndTime: bargainEndDatetime,
              prefixBargainSite: bargainSite
            }
          ]
        }
      })
    },

    /* 选择项目 */
    elSelectChange (val) {
      if (val) {
        const company = this.options.find(i => i.value === val)
        const { id, label } = company || {}
        this.clarificationFormData = {
          ...this.clarificationFormData,
          bargainId: id,
          bargainName: label
        }
        this.queryBargainInfo(id)
      } else {
        this.clarificationFormData = {
          ...this.clarificationFormData,
          enrollEndDatetime: '',
          bargainStartDatetime: '',
          bargainSite: '',
          bargainId: '',
          bargainName: ''
        }
      }
    },

    /* 新增文件行 */
    addOne () {
      this.fileList.push({
        docId: '',
        fileName: '',
        remark: ''
      })
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileList[$index].docId = fileId
      this.fileList[$index].fileName = fileName
    },

    /* 取消 */
    cancel () {
      this.dialogVisible = false
    },

    /* 删除 */
    deleOne (index, row) {
      if (!row.vendorFileId) {
        this.fileList.splice(index, 1)
        return
      }
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'), // 确定
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      }).then(() => {
        this.$api.brg.inquiryByProject.vendorFileDelete({ vendorFileId: row.vendorFileId }).then(() => {
          this.fileList.splice(index, 1)
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
        })
      }).catch(() => {
        // 已取消删除
        this.$message.info(this.$t('common.cancelDelete'))
      })
    },

    /* 暂存 or 提交发布 */
    tempSaveOrSubmit (type) {
      // 请填写澄清内容
      if (!this.answerInfo) {
        this.$message.warning(this.$t('bidMod.msgClarifyContent'))
        return false
      }

      this.$refs.clarificationForm.validate(status => {
        if (status) {
          const data = {
            ...this.clarificationFormData,
            ...this.prefixTable[0],
            ...this.suffixTable[0],
            answerContent: this.answerInfo,
            fileList: this.fileList,
            tempSave: type === 'save'
          }
          this.$api.brg.inquiryByProject.bargainAnswerSave(data).then(() => {
            this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

            this.$emit('refreshList')
            this.dialogVisible = false
          })
        }
      })
    }
  }
}
</script>
