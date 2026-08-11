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
          <el-step :title="$t('vendorMod.created')" />
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
            ref="form1"
            :model="form"
            :rules="rules"
          >
            <srm-row>
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
                    :disabled="isDisabled"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item
                  prop="orgId"
                  :label="$t('vendorMod.ceeaOrgName')"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    :disabled="isDisabled"
                    :parent-id="-1"
                    :scope="form"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    @select="addOrgHandle"
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
                    :disabled="isDisabled"
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
                    :disabled="isDisabled"
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
                    :disabled="isDisabled"
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
                    :disabled="isDisabled"
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
                  prop="creationDate"
                  :label="$t('common.creationTime')"
                >
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>
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
          >
            <srm-row>
              <srm-col :initCol="2">
                <el-form-item
                  prop="complaintTheme"
                  :label="$t('vendorMod.complaintTheme')"
                >
                  <el-input
                    v-model="form.complaintTheme"
                    :disabled="isDisabled"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row

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
                    :disabled="isDisabled"
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
          <!--附件-->
          <FileDynamic
            ref="complaintSceneAttachment"
            v-model="form.fileUploads"
            scene-module-code="SCENE_COMPLAINT_INFO_ATTACHMENT"
            :business-id="form.complaintInfoId"
            :editable="curOpt === 'add' || curOpt === 'edit'"
          />
        </el-collapse-item>

        <el-collapse-item
          v-for="(item, index) in content"
          :key="index"
          :title="
            item.userType == 'BUYER' ? $t('vendorMod.responsibleReview') : $t('vendorMod.replyBack')
          "
          :name="String(item.complaintDealId)"
        >
          <el-form
            ref="reviewdata"
            :model="reviewdata"
            :rules="rules"
          >
            <srm-row>
              <srm-col :initCol="1">
                <el-form-item
                  prop="reviewContent"
                  :label="
                    item.userType == 'BUYER'
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
            :editable="curOpt === 'add' || curOpt === 'edit'"
          />
        </el-collapse-item>
        <el-collapse-item
          v-if="form.complaintStatus == 'ANSWERED'"
          :title="$t('vendorMod.replyBack')"
          name="4"
        >
          <el-form
            ref="form"
            :model="reviewdata"
            :rules="rules"
          >
            <srm-row

              :aria-setsize="50"
            >
              <srm-col :initCol="1">
                <el-form-item
                  prop="reviewContent"
                  :label="$t('vendorMod.replyBack')"
                >
                  <el-input
                    v-model="reviewdata.reviewContent"
                    type="textarea"
                    :autosize="{ minRows: 6, maxRows: 8 }"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <FileDynamic
            ref="complaintAddReviewSceneAttachment"
            v-model="reviewdata.fileUploads"
            scene-module-code="SCENE_COMPLAINT_INFO_REVIEW_ATTACHMENT"
            :editable="form.complaintStatus !== 'CLOSED'"
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
            v-if="form.complaintStatus == 'DRAFT' || !form.complaintStatus"
            type="primary"
            :disabled="readOnly"
            @click="save('DRAFT')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="form.complaintStatus == 'DRAFT' || !form.complaintStatus"
            type="primary"
            :disabled="readOnly"
            @click="save('SUBMITTED')"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            v-if="form.complaintStatus == 'ANSWERED'"
            type="primary"
            :disabled="readOnly"
            @click="save('review')"
          >
            {{ $t('vendorMod.submitReply') }}
          </el-button>
          <el-button
            v-if="
              form.complaintStatus == 'ANSWERED' ||
                form.complaintStatus == 'REPLY_FEEDBACK' ||
                form.complaintStatus == 'SUBMITTED'
            "
            type="primary"
            :disabled="readOnly"
            @click="reuqire()"
          >
            {{ $t('vendorMod.complaintClose') }}
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
import OrganizationSelector from 'lib@/components/organization-selector'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { complaintInfo } from 'mods@/vendorManagementSupplier/api'

export default {
  name: 'ComplaintinfoEdit',
  components: {
    MainHeader,
    CToolbar,
    OrganizationSelector,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      // 文件上传配置信息
      lazyLoading: true,
      isDisabled: false,
      curOpt: 'edit',
      content: [],
      form: {
        fileUploads: [],
        complaintInfoId: null,
        complaintNo: null,
        complaintType: null,
        complaintStatus: '',
        orgId: null,
        orgCode: null,
        orgName: null,
        userType: '',
        complaintUserName: null,
        complaintUserEmail: null,
        complaintUserPhone: null,
        categoryName: null,
        complaintRelationId: null,
        complaintTheme: null,
        complaintContent: null,
        creationDate: null,
        reviewContent: null
      },
      required: {
        complaintInfoId: null,
        complaintStatus: null
      },
      reviewdata: {
        fileUploads: [],
        complaintInfoId: null,
        reviewContent: null,
        complaintdealparentid: null
      },
      rules: {
        orgId: [{ required: true, message: this.$t('dataConfMod.msgSelDivision') }],
        complaintType: [{ required: true, message: this.$t('vendorMod.required') }],
        orgName: [{ required: true, message: this.$t('vendorMod.required') }],
        complaintTheme: [{ required: true, message: this.$t('vendorMod.required') }],
        complaintContent: [{ required: true, message: this.$t('vendorMod.required') }],
        reviewContent: [{ required: true, message: this.$t('vendorMod.required') }]
      },
      readOnly: false,
      activeDims: []
    }
  },
  computed: {
    curStatus: function () {
      if (this.form.complaintStatus === '') {
        // 创建中
        return 0
      }
      if (this.form.complaintStatus === 'DRAFT') {
        // 拟定
        return 1
      } else if (this.form.complaintStatus === 'SUBMITTED') {
        // 供应商商发布
        return 2
      } else if (this.form.complaintStatus === 'ANSWERED') {
        // 采购商回复
        return 3
      } else if (this.form.complaintStatus === 'REPLY_FEEDBACK') {
        // 供应商答复反馈
        return 4
      } else if (this.form.complaintStatus === 'CLOSED') {
        // 已关闭
        return 5
      } else {
        return 0
      }
    }
  },
  watch: {},

  created () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.curOpt = flag
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.form = row
      this.activeDims = ['1', '2', '3']
    }
    if (flag === 'view') {
      this.form = row
      this.isDisabled = true
    }
    if (flag === 'add') {
      this.activeDims = ['1', '2', '3']
      this.$nextTick(() => {
        this.$refs.complaintSceneAttachment.loadFileInfo()
      })
    }

    if (row.complaintInfoId) {
      complaintInfo.getByCompliantInfoId(row.complaintInfoId).then(({ data }) => {
        this.content = data
        if (this.content.length >= 1) {
          this.form.complaintDealId = this.content[this.content.length - 1].complaintDealId
          this.reviewdata.complaintdealparentid = this.content[this.content.length - 1].complaintDealId
          this.activeDims = [String(this.form.complaintDealId), '4']

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
            if (this.form.complaintStatus === 'ANSWERED') {
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
      let result, result1
      this.$refs.form.validate((valid) => result = valid)
      this.$refs.form1.validate((valid) => result1 = valid)
      if (flag !== 'DRAFT' && (!result || !result1)) {
        this.__focus_error__()
        return
      }
      // 新增时不用提交主键值
      // const { complaintInfoId, ...rest } = this.form;
      if (flag === 'SUBMITTED' || flag === 'DRAFT') {
        this.form.complaintStatus = flag
        complaintInfo.saveOrUpdate(this.form).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      } else if (flag === 'review') {
        this.reviewdata.complaintInfoId = this.form.complaintInfoId
        complaintInfo.review(this.reviewdata).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      }
    },
    reuqire () {
      this.required.complaintInfoId = this.form.complaintInfoId
      this.required.complaintStatus = 'CLOSED'
      complaintInfo.requireComplaint(this.required).then(({ res }) => {
        this.cancelBill()
      })
    },
    // 选择组织
    addOrgHandle (e, id, scope) {
      this.form.orgId = e ? e.organizationId : null
      this.form.orgCode = e ? e.organizationCode : ''
      this.form.orgName = e ? e.organizationName : null
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
