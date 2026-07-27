<template>
  <el-container
    v-loading="lazyLoading"
    element-loading-background="rgba(0, 0, 0, 0.4)"
    class="complaintinfoEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-steps
          :active="curStatus"
          :align-center="true"
          finish-status="success"
        >
          <!-- 发布样品确认|供应商回复|样品评价|结果审批 -->
          <el-step :title="$t('vendorMod.submitted')" />
          <el-step :title="$t('vendorMod.buyerDeal')" />
          <el-step :title="$t('vendorMod.supplierDeal')" />
          <el-step :title="$t('vendorMod.closed')" />
        </el-steps>
      </div>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('vendorMod.complaintBaseInfo')"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            style="margin-left: 18px"
            :disabled="readOnly"
          >
            <srm-row :gutter="32">
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintNo"
                  :label="$t('vendorMod.complaintInfoId')"
                >
                  <el-input
                    v-model="form.complaintNo"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintType"
                  :label="$t('vendorMod.complaintType')"
                >
                  <DictSelect
                    v-model="form.complaintType"
                    code="COMPLAINT_TYPE"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintStatus"
                  :label="$t('vendorMod.complaintStatus')"
                >
                  <DictSelect
                    v-model="form.complaintStatus"
                    code="COMPLAINT_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="supplierName"
                  :label="$t('common.vendor')"
                >
                  <el-input
                    v-model="form.supplierName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="supplierCode"
                  :label="$t('common.vendorCode')"
                >
                  <el-input
                    v-model="form.supplierCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintUserName"
                  :label="$t('vendorMod.complaintUserName')"
                >
                  <el-input
                    v-model="form.complaintUserName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintUserPhone"
                  :label="$t('vendorMod.complaintUserPhone')"
                >
                  <el-input
                    v-model="form.complaintUserPhone"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="complaintUserEmail"
                  :label="$t('vendorMod.complaintUserEmail')"
                >
                  <el-input
                    v-model="form.complaintUserEmail"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="authUserName"
                  :label="$t('vendorMod.PersonLiable')"
                >
                  <el-input
                    v-model="form.authUserName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="orgName"
                  :label="$t('vendorMod.ceeaOrgName')"
                >
                  <el-input
                    v-model="form.orgName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="categoryName"
                  :label="$t('vendorMod.categoryName')"
                >
                  <el-input
                    v-model="form.categoryName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col :initCol="3">
                <el-form-item
                  prop="complaintRelationId"
                  :label="$t('vendorMod.complaintRelationId')"
                >
                  <el-input
                    v-model="form.complaintRelationId"
                    disabled
                  />
                </el-form-item>
              </srm-col> -->
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('vendorMod.complaintContent')"
          name="2"
        >
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            :disabled="readOnly"
          >
            <srm-row :gutter="32">
              <srm-col :initCol="2">
                <el-form-item
                  prop="complaintTheme"
                  :label="$t('vendorMod.complaintTheme')"
                >
                  <el-input
                    v-model="form.complaintTheme"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              :gutter="32"
              :aria-setsize="50"
            >
              <srm-col :initCol="1">
                <el-form-item
                  prop="complaintContent"
                  :label="$t('vendorMod.complaintContent')"
                >
                  <el-input
                    v-model="form.complaintContent"
                    type="textarea"
                    :disabled="true"
                    :autosize="{ minRows: 6, maxRows: 8 }"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('vendorMod.relegation.accessory')"
          name="3"
        >
          <FileDynamic
            ref="complaintSceneAttachment"
            v-model="form.fileUploads"
            scene-module-code="SCENE_COMPLAINT_INFO_ATTACHMENT"
            :business-id="form.complaintInfoId"
          />
        </el-collapse-item>
        <div v-if="content">
          <el-collapse-item
            v-for="(item, index) in content"
            :key="index"
            :title="
              item.userType == 'BUYER'
                ? $t('vendorMod.responsibleReview')
                : $t('vendorMod.replyBack')
            "
            :name="String(item.complaintDealId)"
          >
            <el-form
              ref="reviewdata"
              :model="reviewdata"
              :rules="rules"
              :disabled="readOnly"
            >
              <srm-row
                :gutter="32"
                :aria-setsize="50"
              >
                <srm-col :initCol="1">
                  <el-form-item
                    prop="reviewContent"
                    :label="
                      item.userType === 'BUYER'
                        ? $t('vendorMod.responsibleReview')
                        : $t('vendorMod.replyBack')
                    "
                  >
                    <el-input
                      type="textarea"
                      :autosize="{ minRows: 6, maxRows: 8 }"
                      :placeholder="item.reviewContent"
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <FileDynamic
              ref="complaintReviewSceneAttachment"
              v-model="item.fileUploads"
              scene-module-code="SCENE_COMPLAINT_INFO_REVIEW_ATTACHMENT"
              :business-id="item.complaintDealId"
              :editable="false"
            />
          </el-collapse-item>
        </div>
        <el-collapse-item
          v-if="form.complaintStatus === 'SUBMITTED' || form.complaintStatus === 'REPLY_FEEDBACK'"
          :title="$t('vendorMod.buyerAnswer')"
          name="100"
        >
          <el-form
            ref="reviewdataBuyer"
            :model="reviewdata"
            :rules="rules"
            :disabled="readOnly"
          >
            <srm-row
              :gutter="32"
              :aria-setsize="50"
            >
              <srm-col :initCol="1">
                <el-form-item
                  prop="reviewContent"
                  :label="$t('vendorMod.responsibleReview')"
                >
                  <el-input
                    v-model="reviewdata.reviewContent"
                    type="textarea"
                    :autosize="{ minRows: 6, maxRows: 8 }"
                    :disabled="form.complaintStatus === 'CLOSED'"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <FileDynamic
            ref="complaintAddReviewSceneAttachment"
            v-model="reviewdata.fileUploads"
            scene-module-code="SCENE_COMPLAINT_INFO_REVIEW_ATTACHMENT"
            :editable="form.complaintStatus !== 'CLOSED' && !readOnly"
          />
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>

          <el-button
            v-if="form.complaintStatus === 'SUBMITTED' || form.complaintStatus === 'REPLY_FEEDBACK'"
            type="primary"
            :disabled="readOnly"
            @click="save('add')"
          >
            {{ $t('vendorMod.submitAnswer') }}
          </el-button>
          <el-button
            v-if="form.complaintStatus === 'REPLY_FEEDBACK' || form.complaintStatus === 'ANSWERED'"
            type="primary"
            :disabled="readOnly"
            @click="requireComplaint()"
          >
            {{ $t('vendorMod.applyClose') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { complaintInfo } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'ComplaintinfoEdit',
  components: {
    MainHeader,
    CToolbar,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      // 文件上传配置信息
      lazyLoading: true,
      dataAtt: [],
      curOpt: 'view',
      content: [],
      required: {
        complaintInfoId: null,
        complaintStatus: null
      },
      form: {
        fileUploads: [],
        reviewContent: null,
        complaintInfoId: null,
        talkStatus: 'VENDOR',
        complaintNo: null
      },
      reviewdata: {
        fileUploads: [],
        complaintInfoId: null,
        reviewContent: null,
        complaintdealparentid: null
      },
      rules: {
        reviewContent: [{ required: true, message: this.$t('vendorMod.required') }]
      },
      readOnly: false,
      activeDims: []
    }
  },
  computed: {
    curStatus: function () {
      if (
        this.form.complaintStatus === 'SUBMITTED' ||
        this.form.complaintStatus === 'REPLY_FEEDBACK'
      ) {
        // 已提交
        return 1
      } else if (
        this.form.complaintStatus === 'ANSWERED' ||
        this.form.complaintStatus === 'APPLICATION_CLOSED'
      ) {
        // 供应商处理
        return 2
      } else if (this.form.complaintStatus === 'CLOSED') {
        // 已关闭
        return 4
      } else {
        return 0
      }
    }
  },
  watch: {},
  created () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'view') this.readOnly = true
    if (['edit', 'view'].includes(flag)) {
      this.form = row
      this.reviewdata.complaintInfoId = row.complaintInfoId
    }

    if (row.complaintInfoId) {
      complaintInfo.getByCompliantInfoId(row.complaintInfoId).then(({ data }) => {
        this.content = data
        if (this.content.length === 0) {
          this.activeDims = ['1', '2', '3', '100']
        }
        if (this.content.length >= 1) {
          this.form.complaintDealId = this.content[this.content.length - 1].complaintDealId
          this.reviewdata.complaintdealparentid = this.content[this.content.length - 1].complaintDealId
          this.activeDims = [String(this.form.complaintDealId), '4', '100']

          this.$nextTick(() => {
            const reviewItemList = this.$refs.complaintReviewSceneAttachment
            console.log('reviewItemList', reviewItemList)
            for (let i = 0; i < reviewItemList.length; i++) {
              reviewItemList[i].loadFileInfo()
            }
          })
        }
        if (flag === 'add') {
          this.form = {}
          this.content = null
        } else {
          this.$nextTick(() => {
            this.$refs.complaintSceneAttachment.loadFileInfo()
            if (this.form.complaintStatus === 'SUBMITTED' || this.form.complaintStatus === 'REPLY_FEEDBACK') {
              this.$refs.complaintAddReviewSceneAttachment.loadFileInfo()
            }
          })
        }
        this.lazyLoading = false
      })
    } else {
      this.lazyLoading = false
    }
  },

  mounted () {},
  methods: {
    save (flag) {
      this.$refs.reviewdataBuyer.validate((result) => {
        if (result) {
          // 新增时不用提交主键值
          if (flag === 'add') {
            complaintInfo.review(this.reviewdata).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    requireComplaint () {
      this.required.complaintInfoId = this.form.complaintInfoId
      this.required.complaintStatus = 'APPLICATION_CLOSED'
      complaintInfo.requireComplaint(this.required).then(({ res }) => {
        this.cancelBill()
      })
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('complaintinfoList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    // 获取附件信息
    changeScene (val) {
      console.warn('[val]', val)
    }
  }
}
</script>
<style scoped lang="scss">
.complaintinfoEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
