<template>
  <SrmDialog
    :title="$t('bidMod.clarification')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
    append-to-body
  >
    <!--表单-->
    <el-form
      ref="answerForm"
      :model="answerFormData"
      label-position="top"
      :rules="answerFormRules"
      :disabled="readonly"
    >
      <SrmRow>
        <!-- 澄清编号 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.answerNum')">
            <el-input v-model="answerFormData.answerNum" disabled />
          </el-form-item>
        </SrmCol>

        <!-- 项目名称 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
            <QuickSearch
              :show-input="answerFormData.souName"
              :scope-data="answerFormData"
              show-key="souName"
              name="sou_answer_projet"
              :pre-query-data="{ 't.sou_type': souType }"
              :disabled="readonly || isFromQuestion"
              @close-quicksearch="setSouProject"
            />
          </el-form-item>
        </SrmCol>

        <!-- 澄清标题 -->
        <SrmCol :init-col="3">
          <el-form-item prop="answerTitle" :label="$t('bidMod.answerTitle')">
            <el-input v-model="answerFormData.answerTitle" :disabled="readonly || isFromQuestion" />
          </el-form-item>
        </SrmCol>

        <!-- <SrmCol :init-col="3" v-if="userTypeFlag.isBuyer">
          <el-form-item prop="answerTitle" label="澄清来源">
            <DictSelect
              v-model="answerFormData.extSource"
              disabled
              code="ANSWER_SOURCE_TYPE"
            />
          </el-form-item>
        </SrmCol> -->

        <SrmCol v-if="userTypeFlag.isBuyer" :init-col="3">
          <el-form-item prop="questionTitle" :label="$t('bidMod.questionTitle')">
            <el-input v-model="answerFormData.questionTitle" disabled />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="userTypeFlag.isBuyer" :init-col="3">
          <el-form-item prop="creationDate" :label="$t('bidMod.dateCreated')">
            <el-date-picker
              v-model="answerFormData.creationDate"
              type="date"
              :format="$formatDatePicker"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <el-form-item prop="answerStatus" :label="$t('bidMod.answerStatus')">
            <DictSelect
              v-model="answerFormData.answerStatus"
              disabled
              code="CLARIFIED_STATUS"
            />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="userTypeFlag.isBuyer" :init-col="3">
          <el-form-item prop="createdUserName" :label="$t('bidMod.bidingCreatedBy')">
            <el-input v-model="answerFormData.createdUserName" disabled />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="userTypeFlag.isBuyer" :init-col="3">
          <el-form-item prop="lastUpdateDate" :label="$t('common.lastUpdateDate2')">
            <el-date-picker
              v-model="answerFormData.lastUpdateDate"
              type="date"
              :format="$formatDatePicker"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="userTypeFlag.isBuyer" :init-col="3">
          <!-- 招标专家 -->
          <el-form-item prop="extBidNickname" :label="$t('cusEntry.bidSuperviseReport.souPrincipal')">
            <el-input v-model="answerFormData.extBidNickname" disabled />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="!userTypeFlag.isBuyer" :init-col="3">
          <!-- 最新回复时间 -->
          <el-form-item prop="lastUpdateDate" :label="$t('cusEntry.bidSuperviseReport.lastUpdateDate')">
            <el-date-picker
              v-model="answerFormData.lastUpdateDate"
              type="date"
              :format="$formatDatePicker"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="answerFormData.answerStatus==='ABANDON'" :init-col="1">
          <!-- 废弃说明 -->
          <el-form-item :label="$t('cusEntry.supplement20250121.reasonDesc')">
            <el-input v-model="answerFormData.reasonDesc" disabled />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!-- 澄清内容 -->
    <h3>{{ $t("bidMod.clarifyContent") }}</h3>
    <Tinymce
      id="answerTinymce"
      v-model="answerInfo"
      :height="260"
      @setup="readyBuyer"
    />

    <h3 v-if="userTypeFlag.isBuyer">
      {{ $t('common.vendor') }}
    </h3>
    <QuickSearch
      v-if="userTypeFlag.isBuyer"
      :disabled="readonly"
      :showButton="true"
      :btnTitle="$t('common.add')"
      showKey="companyName"
      propKey="companyName"
      :pre-query-data="{'t.PROJECT_ID':answerFormData.projectId}"
      :multiSelect="true"
      name="answer_vendor_search"
      @close-quicksearch="getUserObj"
    />
    <el-table
      v-if="userTypeFlag.isBuyer"
      :data="companyList"
      style="width: 100%;margin-top: 10px"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('purSettlementMod.tabindex')"
        width="50"
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
      />
      <!-- 最新回复时间 -->
      <el-table-column
        align="center"
        prop="lastReplayTime"
        :label="$t('cusEntry.supplement20250121.lastUpdateDate')"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        v-if="answerFormData.answerStatus == 'ISSUED'"
        align="center"
        prop="confirmStatus"
        :label="$t('bidMod.qa.confirmStatus')"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.confirmStatus"
            disabled
            code="ANSWER_CONFIRM_STATUS"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="60">
        <template slot-scope="scope">
          <el-button
            type="text"
            :disabled="readonly"
            @click="handleDelClick(scope.$index, scope.row, 'list')"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 外部附件 -->
    <!-- 待发布状态可编辑 -->
    <!-- 招标专家在创建的时候可以编辑 -->
    <h3>{{ $t('cusEntry.supplement20250121.externalFile') }}</h3>
    <FileDynamic
      ref="sceneAttachment"
      v-model="sceneFiles"
      scene-module-code="SCENE_SOU_ANSWER_FILE_ATTACHMENT"
      :business-id="editRow.answerId || ''"
      :editable="!readonly && isFromQuestion !== 1 && (editRow.answerStatus === 'WAIT_PUBLISH' ||
        ((pageType === 'add' || editRow.answerStatus === 'DRAFT') && userId === extBidUserId))"
      :need-init="false"
    />

    <!-- 内部附件 -->
    <!-- 只有创建时or拟定状态可编辑 -->
    <div v-if="userTypeFlag.isBuyer">
      <h3>{{ $t('cusEntry.supplement20250121.innerFile') }}</h3>
      <FileDynamic
        ref="sceneAttachmentInner"
        v-model="innerFiles"
        scene-module-code="SCENE_SOU_ANSWER_INNER_FILE_ATTACHMENT"
        :business-id="editRow.answerId || ''"
        :editable="!readonly && isFromQuestion !== 1 && (pageType === 'add' || editRow.answerStatus === 'DRAFT')"
        :need-init="false"
      />
    </div>

    <h3 v-if="userTypeFlag.isBuyer && answerFormData.answerStatus == 'ISSUED' && username == answerFormData.extBidNickname">
      <!-- 澄清回复供应商 -->
      {{ $t('cusEntry.supplement20250121.respondSuppliers') }}
    </h3>
    <el-tabs v-if="userTypeFlag.isBuyer && ['COMFIRMED', 'ISSUED'].includes(answerFormData.answerStatus) && username == answerFormData.extBidNickname" type="card" @tab-click="handleClick">
      <el-tab-pane v-for="item in conpanyHuifu" :key="item.companyId" :label="item.vendorName" :name="item.companyName">
        <el-button
          v-if="item.confirmStatus !== 'COMFIRMED'"
          type="primary"
          style="margin-bottom: 8px"
          @click="chengqing(item)"
        >
          <!-- 澄清确认 -->
          {{ $t('cusEntry.supplement20250121.clarifyConfirm') }}
        </el-button>
        <Tinymce
          id="answerTinymce"
          v-model="item.replayContent"
          :height="260"
          @setup="ready"
        />
        <!-- .find(e => e.isDelete !== 'Y') -->
        <el-button
          style="margin-top: 10px;"
          type="primary"
          @click="batchDownload"
        >
          {{ $t('cusEntry.common.batchDownload') }}
        </el-button>
        <el-table
          :data="isDeleteFun(item.sceneFiles)"
          style="width: 100%;margin-top: 10px"
          border
          max-height="250px"
          @selection-change="selectFileChange"
        >
          <el-table-column
            align="center"
            type="selection"
            width="50"
          />
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <!-- 附件 -->
          <el-table-column
            align="center"
            prop="attachName"
            :label="$t('purchaseDemand.attachment')"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :readonly="userTypeFlag.isBuyer && ['ISSUED', 'COMFIRMED'].includes(answerFormData.answerStatus)"
                :default-file="{
                  fileId: scope.row.fileId,
                  fileName: scope.row.fileName
                }"
                @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="replayTime"
            :label="$t('bidMod.replyDatetime')"
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            :show-overflow-tooltip="true"
          />
          <!-- 签署状态 -->
          <el-table-column
            align="center"
            prop="signStatus"
            :label="$t('cusEntry.bidMod.signStatus')"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <DictSelect
                v-model="scope.row.signStatus"
                disabled
                code="SOU_BID_SIGN_STATUS"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="remark"
            :label="$t('common.remark')"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.remark" :disabled="userTypeFlag.isBuyer && answerFormData.answerStatus == 'ISSUED'" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.operation')" width="60">
            <template slot-scope="scope">
              <el-button
                type="text"
                :disabled="userTypeFlag.isBuyer && ['ISSUED', 'COMFIRMED'].includes(answerFormData.answerStatus)"
                @click="handleDelClick2(scope.$index, scope.row, 'del')"
              >
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!--供应商回复-->
    <h3 v-if="isFromQuestion === 1">
      <!-- 澄清回复 -->
      {{ $t('cusEntry.supplement20250121.respond') }}
    </h3>
    <Tinymce
      v-if="isFromQuestion === 1"
      id="answerTinymce"
      v-model="replayContent"
      :height="260"
    />

    <h3 v-if="isFromQuestion === 1">
      <!-- 供方回复附件 -->
      {{ $t('cusEntry.supplement20250121.respondFile') }}
    </h3>
    <!--附件上传-->
    <p class="btn_line">
      <el-button
        v-if="isFromQuestion === 1 && (!readonly || pageType === 'huifuView')"
        type="primary"
        class="detail-pbtn"
        @click="addUploadOne"
      >
        {{ $t('common.add') }}
      </el-button>
      <el-button
        v-if="isFromQuestion === 1 && contractVerification == 'Y' && (!readonly || pageType === 'huifuView')"
        type="primary"
        class="detail-pbtn"
        @click="xianshangqianshu"
      >
        <!-- 线上签署 -->
        {{ $t('cusEntry.biddingSettings.onlineSign') }}
      </el-button>
      <el-button
        v-if="isFromQuestion === 1 && contractVerification == 'Y' && (!readonly || pageType === 'huifuView')"
        type="primary"
        class="detail-pbtn"
        @click="shuaxinFun"
      >
        <!-- 刷新签署状态 -->
        {{ $t('cusEntry.supplement20250121.refresh') }}
      </el-button>
    </p>
    <el-table
      v-if="isFromQuestion === 1"
      :disabled="(readonly || pageType !== 'huifuView')"
      :data="sceneFilesHuiFu"
      style="width: 100%"
      border
      max-height="250px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('purSettlementMod.tabindex')"
        width="50"
      />
      <el-table-column
        type="selection"
        width="55"
      />
      <!-- 附件 -->
      <el-table-column
        align="center"
        prop="attachName"
        :label="$t('purchaseDemand.attachment')"
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :extra-data="fileInfo"
            :readonly="userTypeFlag.isBuyer && answerFormData.answerStatus == 'ISSUED'"
            :default-file="{
              fileId: scope.row.fileId,
              fileName: scope.row.fileName
            }"
            @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.remark" :disabled="userTypeFlag.isBuyer && answerFormData.answerStatus == 'ISSUED'" />
        </template>
      </el-table-column>
      <!-- 签署状态 -->
      <el-table-column
        align="center"
        prop="signStatus"
        :label="$t('cusEntry.bidMod.signStatus')"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.signStatus"
            disabled
            code="SOU_BID_SIGN_STATUS"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="60">
        <template slot-scope="scope">
          <el-button
            type="text"
            :disabled="userTypeFlag.isBuyer && answerFormData.answerStatus == 'ISSUED'"
            @click="handleDelClick2(scope.$index, scope.row, 'del')"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>

      <template v-if="!readonly">
        <!--采购商按钮-->
        <template v-if="userTypeFlag.isBuyer">
          <!--暂存 来源质疑单只能直接发布-->
          <el-button
            type="primary"
            @click="tempSaveOrSubmit('save')"
          >
            {{ $t("common.staging") }}
          </el-button>

          <el-button type="primary" @click="tempSaveOrSubmit('submit')">
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </template>

      <!--供应商按钮-->
      <template v-if="userTypeFlag.isVendor && (!readonly || pageType === 'huifuView')">
        <!--接受澄清 存在质疑ID，并且已发布-->
        <el-button
          type="primary"
          @click="acceptAnswer"
        >
          <!-- 澄清回复 -->
          {{ $t('cusEntry.supplement20250121.respond') }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 新增 / 查看 澄清弹窗
 */
import { qaBuyerHttp, qaVendorHttp } from './api'
import { mapGetters } from 'vuex'
import { USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Tinymce from '@/components/Tinymce/index.vue'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic.vue'
import QuickSearch from 'lib@/components/QuickSearch.vue'
import { transformMQL } from '@/library/utils/util'
import { qa } from 'modc@/buyer/purchasingDemand/api'
import { downloadFileLinkByPost } from 'lib@/utils/file'
export default {
  name: 'AnswerDetail',

  components: {
    Tinymce,
    FileDynamic,
    QuickSearch
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    // 寻源类型
    souType: {
      type: String,
      required: true
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
      type: [Boolean, Number],
      default: false
    }
  },

  data () {
    return {
      conpanyHuifu: [],
      multipleSelection: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      sceneFilesHuiFu: [],
      sceneFilesHuiFuDel: [],
      replayContent: '',
      answerFormData: {
        extSource: 'QUESTION',
        extBidNickname: null,
        projectId: null,
        souName: null,
        souNo: null,
        answerNum: null,
        answerTitle: null
      },
      companyList: [],
      companyListDel: [],
      options: [],
      sceneFiles: [],
      innerFiles: [],
      answerInfo: null,
      answerFormRules: {
        // 请选择项目
        souName: { required: true, message: this.$t('bidMod.msgSelProject') },
        // 请填写标题
        answerTitle: { required: true, message: this.$t('bidMod.msgInputTitle') }
      },
      bankRowIndex: '',
      canAccept: false,
      contractVerification: null, // 认证供应商
      allowBidWithoutSealFlag: null,
      allowClearWithoutSealFlag: null, // 是否允许澄清免签章
      extBidUserId: null, // 招标专家工号
      userId: null,
      selectFileData: []
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

    ...mapGetters(['userType', 'username']),

    // api入口，用户类型转小写
    apiUserType () {
      return this.userType.toLowerCase()
    },

    userTypeFlag () {
      return {
        isBuyer: this.userType === USER_TYPE_ENUM.BUYER,
        isVendor: this.userType === USER_TYPE_ENUM.VENDOR
      }
    },

    // api入口
    qaHttp () {
      return this.userTypeFlag.isBuyer ? qaBuyerHttp : qaVendorHttp
    },

    readonly () {
      return this.pageType === 'view' || this.pageType === 'huifuView'
    }
  },

  mounted () {
    if (this.pageType === 'add') {
      // 新增
      if (this.isFromQuestion) {
        // 来自质疑
        this.answerFormData = {
          ...this.answerFormData,
          ...this.editRow
        }
      }
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
      this.$nextTick(() => {
        this.$refs.sceneAttachmentInner.loadFileInfo()
      })
    } else {
      this.getDetail()
    }
    this.userId = this.$store.getters.userId || null
    const companyId = this.$store.getters.companyId
    if (companyId) {
      const searchData = transformMQL.listPageData({
        type: 'CompanyInfo',
        action: 'query',
        params: { companyId: companyId },
        query: {
          '*': {}
        }
      })
      this.$http({
        url: '/api-sup/api-ql/CompanyInfo/query',
        method: 'POST',
        data: searchData,
        loading: true
      })
        .then(res => {
          // console.log(res, 'res')
          const { contractVerification, allowBidWithoutSealFlag, allowClearWithoutSealFlag } = res.data.records[0]
          this.contractVerification = contractVerification
          this.allowBidWithoutSealFlag = allowBidWithoutSealFlag
          this.allowClearWithoutSealFlag = allowClearWithoutSealFlag
        })
        .catch(err => {
          console.log(err)
        })
    }
  },

  methods: {
    // 附件选择
    selectFileChange (data) {
      this.selectFileData = data
    },
    // 批量下载
    batchDownload () {
      const selectData = this.selectFileData
      if (selectData.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.pleaseSelectFile'))
        return false
      }
      // 文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。
      this.$confirm(this.$t('cusEntry.supplement20250121.promptTips6'), this.$t('common.tips'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLinkByPost('/api-file/extfileupload/batch-download', null, { fileIdList: selectData.map(item => item.fileId) }).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.dowmloadSuccess'))
      }).catch(() => {
        this.$message.warning(this.$t('cusEntry.tipMessage.dowmloadError'))
      })
    },
    isDeleteFun (data) {
      let attr = []
      data.forEach(e => {
        if (e.isDelete !== 'Y') {
          attr.push(e)
        }
      })
      return attr
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    shuaxinFun () {
      this.getDetail()
    },
    xianshangqianshu () {
      // 附件列表
      // console.log(this.multipleSelection, 'multipleSelection')
      if (!this.multipleSelection.length) {
        // 请先勾选需要线上签署的文件
        return this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips17'))
      }
      let sceneFilesHuiFu = this.sceneFilesHuiFu
      let multipleSelection = this.multipleSelection
      sceneFilesHuiFu.forEach(e => {
        multipleSelection.forEach(e2 => {
          if (e.replayFileId == e2.replayFileId) {
            e.signStatus = 'A'
          }
        })
      })
      const sceneFilesHuiFuAll = this.sceneFilesHuiFu.concat(this.sceneFilesHuiFuDel)
      const dataObj = {
        'answerId': this.answerFormData.answerId,
        'answerVendorId': this.answerFormData.answerVendorId,
        'replayId': this.answerFormData.replayId,
        'replayContent': this.replayContent,
        'sceneFiles': sceneFilesHuiFuAll
      }
      qa.pushSgin(dataObj).then(res => {
        window.open(res.data.signUrl)
      })
    },
    chengqing (item) {
      // console.log(item)
      const answerVendorId = item.answerVendorId
      const saveData = transformMQL.save('AnswerVendor', [{ 'answerVendorId': answerVendorId, answerId: item.answerId }], 'confirm')
      qa.confirm(saveData).then((datas) => {
        this.$message.success(this.$t('common.successConfirm'))
        // this.dialogVisible = false
      })
    },
    addUploadOne () {
      this.sceneFilesHuiFu.push({
        fileId: null,
        fileName: ''
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
    },
    handleClick (tab, event) {

    },
    handleDelClick (index, row) {
      this.companyList.splice(index, 1)
      if (row?.answerVendorId) {
        this.companyListDel.push({ '$delete': row?.answerVendorId })
      }
    },
    handleDelClick2 (index, row) {
      this.sceneFilesHuiFu.splice(index, 1)
      if (row.replayFileId) {
        row.isDelete = 'Y'
        this.sceneFilesHuiFuDel.push(row)
      }
    },
    getUserObj (val) {
      // console.log(val, 'val')
      val.forEach(datas => {
        let hasSelectedIndex = this.companyList.findIndex(i => i.vendorId == datas.vendorId)
        if (hasSelectedIndex < 0) {
          const obj = {
            vendorName: datas.vendorName,
            vendorId: datas.vendorId,
            vendorCode: datas.vendorCode
          }
          this.companyList.push(obj)
        }
      })
    },
    /* 查询澄清详情 */
    async getDetail () {
      const allparam = { answerId: this.editRow.answerId }
      const query = {
        '*': {},
        'sceneFiles': { '*': {} },
        'innerFiles': { '*': {} },
        'answerVendors': { '*': {} }
      }

      if (this.userTypeFlag.isBuyer) {
        const saveData = transformMQL.save('Answer', [allparam], 'read', query)
        qa.answerRead(saveData).then((datas) => {
          const { answerContent, sceneFiles, innerFiles, answerVendors, ...res } = datas.data[0]
          this.answerFormData = res
          this.answerInfo = answerContent
          this.sceneFiles = sceneFiles
          this.innerFiles = innerFiles
          this.companyList = answerVendors
          this.extBidUserId = res.extBidUserId

          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
          })
          this.$nextTick(() => {
            this.$refs.sceneAttachmentInner.loadFileInfo()
          })
          const query2 = {
            '*': {},
            'sceneFiles': { '*': {} }
          }
          let allparam2 = []
          answerVendors.forEach(e => {
            if (e.replayId) {
              const obj = {
                replayId: e.replayId
              }
              allparam2.push(obj)
            }
          })
          const saveData2 = transformMQL.save('Replay', allparam2, 'read', query2)
          qa.readByAnswer(saveData2).then((datas2) => {
            let datas3 = datas2.data
            datas3.forEach(e => {
              const name = answerVendors.find(item => item.replayId == e.replayId)
              e.vendorName = name.vendorName
              e.answerVendorId = name.answerVendorId
              e.confirmStatus = name.confirmStatus
            })
            const mockVendorRecords = answerVendors.filter(vendor => !datas3.map(data => data.answerVendorId).includes(vendor.answerVendorId)).map(item => {
              return {
                ...item,
                replayContent: '',
                sceneFiles: []
              }
            })
            this.conpanyHuifu = [...datas3, ...mockVendorRecords]
          })
        })
      } else {
        const saveData = transformMQL.save('VendorAnswer', [allparam], 'read', query)
        qa.answerReadVendor(saveData).then((datas) => {
          // console.log(datas.data[0], 'datas')
          const { answerContent, sceneFiles, answerVendors, ...res } = datas.data[0]
          this.answerFormData = res
          this.answerInfo = answerContent
          this.sceneFiles = sceneFiles
          this.companyList = answerVendors
          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
          })
          if (this.answerFormData?.replayId) {
            const query2 = {
              '*': {},
              'sceneFiles': { '*': {} }
            }
            const allparam2 = { replayId: this.answerFormData.replayId }
            const saveData2 = transformMQL.save('Replay', [allparam2], 'read', query2)
            qa.readByAnswer(saveData2).then((datas2) => {
              // console.log(datas2, 'datas2')
              const datas3 = datas2.data[0]
              this.replayContent = datas3?.replayContent
              let sceneFiles = []
              datas3?.sceneFiles?.forEach(e => {
                if (e.isDelete !== 'Y') {
                  sceneFiles.push(e)
                }
              })
              this.sceneFilesHuiFu = sceneFiles
              // console.log(this.sceneFilesHuiFu, 'sceneFilesHuiFu')
            })
          }
        })
      }
    },

    /* 选择一个项目 */
    setSouProject (val) {
      const { projectId = '', souName = '', souNo = '', userId = null } = val || {}
      this.answerFormData.projectId = projectId
      this.answerFormData.souName = souName
      this.answerFormData.souNo = souNo
      // 招标专家工号
      this.extBidUserId = userId
    },

    /* 供应商 - 接受澄清 */
    async acceptAnswer () {
      let flag = false
      // 【契约认证】为是 && 【是否允许澄清免签章】为否，提交时，校验供应商回复附件为已签署状态
      if (this.contractVerification == 'Y' && this.allowClearWithoutSealFlag != 'Y') {
        flag = this.sceneFilesHuiFu.some(item => item.signStatus == 'SIGN')
        if (!flag && this.sceneFilesHuiFu.length) {
          // 供方回复附件未签署，若已签署，请点击【刷新签署状态】
          this.$message.error(this.$t('cusEntry.supplement20250121.promptTips18'))
          return
        }
      }
      const sceneFilesHuiFuAll = this.sceneFilesHuiFu.concat(this.sceneFilesHuiFuDel)
      const allparam = {
        answerId: this.answerFormData.answerId,
        answerVendorId: this.answerFormData.answerVendorId,
        replayId: this.answerFormData?.replayId,
        replayContent: this.replayContent,
        sceneFiles: sceneFilesHuiFuAll
      }
      const saveData = transformMQL.save('Replay', [allparam], 'save')
      qa.replaySave(saveData).then((datas) => {
        // 澄清回复成功
        this.$message.success(this.$t('cusEntry.supplement20250121.promptTips19'))
        this.$emit('refresh')
        this.dialogVisible = false
      })
    },

    /* 采购商 - 暂存/提交发布 澄清单 */
    async tempSaveOrSubmit (type) {
      if (!this.answerInfo) {
        // 请填写澄清内容
        this.$message.warning(this.$t('bidMod.msgClarifyContent'))
        return false
      }

      // 校验
      // const valid = await this.$refs.answerForm.validate().catch(() => { /* noting */ })
      // if (!valid) {
      //   this.__focus_error__()
      //   return
      // }

      const companyList = this.companyList.concat(this.companyListDel)
      if (this.userTypeFlag.isBuyer) {
        const allparam = {
          ...this.answerFormData,
          answerContent: this.answerInfo,
          sceneFiles: this.sceneFiles,
          innerFiles: this.innerFiles,
          answerVendors: companyList
        }
        if (type === 'save') {
          const saveData = transformMQL.save('Answer', [allparam], 'save')
          qa.answerSave(saveData).then((datas) => {
            this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

            this.$emit('refresh')
            this.dialogVisible = false
          })
        } else {
          const saveData = transformMQL.save('Answer', [allparam], 'submit')
          qa.answerSubmit(saveData).then((datas) => {
            this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

            this.$emit('refresh')
            this.dialogVisible = false
          })
        }
      } else { // 供应商
        const allparam = { // 待修改
          ...this.answerFormData,
          answerContent: this.answerInfo,
          sceneFiles: this.sceneFiles,
          answerVendors: companyList
        }
        if (type === 'save') {
          const saveData = transformMQL.save('VendorAnswer', [allparam], 'save')
          qa.answerSaveVendor(saveData).then((datas) => {
            this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

            this.$emit('refresh')
            this.dialogVisible = false
          })
        } else {
          const saveData = transformMQL.save('VendorAnswer', [allparam], 'submit')
          qa.answerSubmitVendor(saveData).then((datas) => {
            this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

            this.$emit('refresh')
            this.dialogVisible = false
          })
        }
      }
    },

    /* 富文本加载完毕 */
    ready (editorInstance) {
      if (this.readonly) {
        // 设置只读
        editorInstance.setMode('readonly')
      }
    },
    readyBuyer (editorInstance) {
      if (this.readonly || !this.userTypeFlag.isBuyer) {
        // 设置只读
        editorInstance.setMode('readonly')
      }
    }
  }
}
</script>
