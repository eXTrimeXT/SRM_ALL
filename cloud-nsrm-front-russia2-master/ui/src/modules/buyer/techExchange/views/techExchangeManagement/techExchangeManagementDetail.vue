<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-tabs
      v-if="isInitDetail && pageFlag.isView && detailFormData.technicalExchangeFormStatus !== 'DRAFT'"
      v-model="activeTab"
      type="card"
    >
    <!-- 交流信息 -->
      <el-tab-pane :label="$t('bidMod.exchangeInfo')" name="detail" />
      <!-- 供应商反馈 -->
      <el-tab-pane :label="$t('vendorMod.supplierFeedback')" name="feedback" />
    </el-tabs>

    <el-main v-show="activeTab === 'detail'">
      <el-form
        ref="detailForm"
        :model="detailFormData"
        :rules="detailFormRules"
        label-position="top"
        class="detail-form-wrap form-incontainer"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!--交流信息-->
          <el-collapse-item
            :title="$t('bidMod.exchangeInfo')"
            name="1"
          >
            <DetailInfo
              :form-data.sync="detailFormData"
              :readonly="pageFlag.isView"
            />
          </el-collapse-item>

          <!--需求信息-->
          <el-collapse-item
            :title="$t('bidMod.requireInfo')"
            name="2"
          >
            <RequirementsInfo
              ref="requirementsInfo"
              :info-data="tecExcMaterialItems"
              :readonly="pageFlag.isView"
            />
          </el-collapse-item>

          <!--邀请供应商-->
          <el-collapse-item
            :title="$t('bidMod.inviteSupplier')"
            name="3"
          >
            <OriginInviteSuppliers
              ref="originInviteSuppliers"
              business-type="TECH_EXCHANGE"
              :show-recommend-vendor="false"
              :show-suppliers-permission="false"
              :invite-suppliers-data="tecExcVendors"
              :is-readonly="pageFlag.isView"
            >
              <template #toolbar>
                <span style="margin-right: 10px; line-height: 32px">{{ $t("bidMod.inviteVendorMessage") }}</span>
              </template>
            </OriginInviteSuppliers>
          </el-collapse-item>

          <!--查看附件-->
          <!-- 技术交流附件 -->
          <el-collapse-item
            :title="$t('bidMod.techCommunicateAttachment')"
            name="4"
          >
            <FileDynamic
              ref="sceneAttachment"
              v-model="tecExcFiles"
              scene-module-code="SCENE_TECHNICAL_EXCHANGE_ATTACHMENT"
              :business-id="technicalExchangeId"
              :editable="!pageFlag.isView"
              :need-init="false"
            />
          </el-collapse-item>

          <!--联系方式-->
          <el-collapse-item
            :title="$t('orderMod.buyerOrderSynergy.contactNumber')"
            name="5"
          >
            <OriginContactInfo
              business-type="TECH_EXCHANGE"
              :info-data.sync="detailFormData"
              set-default
              :read-only="pageFlag.isView"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>

      <CToolbar>
        <template slot="right">
          <template v-if="!pageFlag.isView">
            <!--保存-->
            <el-button
              type="primary"
              @click="saveOrSubmit('SAVE')"
            >
              {{ $t('common.save') }}
            </el-button>

            <!--提交-->
            <el-button
              type="primary"
              @click="saveOrSubmit('SUBMIT')"
            >
              {{ $t('common.submit') }}
            </el-button>
          </template>

          <!--返回-->
          <el-button @click="back">
            {{ $t('bidMod.backTo') }}
          </el-button>
        </template>
      </CToolbar>

      <!--技术文件-->
      <ItemFilesDialog
        v-if="itemFilesDialogVisible"
        :visible.sync="itemFilesDialogVisible"
      />
    </el-main>

    <TechExchangeManagementFeedbackView
      v-if="activeTab === 'feedback'"
      :attrs-params-row="attrsParamsRow"
      style="margin-top: 10px"
    />
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { isEmail, isMobile } from '@/library/utils/validate'
import DetailInfo from 'lib@/composition/techExchangeManagement/detailInfo'
import RequirementsInfo from 'lib@/composition/techExchangeManagement/requirementsInfo'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import ItemFilesDialog from 'lib@/composition/techExchangeManagement/requirementsInfo/itemFilesDialog'
import CToolbar from 'lib@/components/c-toolbar'
import TechExchangeManagementFeedbackView from './techExchangeManagementFeedbackView'
import { techExchangeBuyerApi } from 'modb@/techExchange/api'

export default {
  name: 'TechExchangeManagementDetail',

  components: {
    DetailInfo,
    RequirementsInfo,
    OriginInviteSuppliers,
    FileDynamic,
    OriginContactInfo,
    ItemFilesDialog,
    CToolbar,
    TechExchangeManagementFeedbackView
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'detail',
      technicalExchangeId: '',
      detailFormData: {
        technicalExchangeFormCode: '',
        technicalExchangeTitle: '',
        orgOuId: '',
        orgOuCode: '',
        orgOuName: '',
        technicalExchangeType: '',
        technicalExchangeStartTime: '',
        technicalExchangeEndTime: '',
        creationDate: '',
        technicalExchangeFormStatus: '',
        createdUserName: '',
        remark: '',
        linkMan: '',
        phone: '',
        email: ''
      },
      detailFormRules: {
        technicalExchangeTitle: [{ required: true, message: this.$t('bidMod.enterCommunicateTitle') }],  // '请输入交流标题'
        orgOuId: [{ required: true, message: this.$t('common.BusinessEntity') }],  // '请选择业务实体'
        technicalExchangeType: [{ required: true, message: this.$t('bidMod.selectCommunicateType') }],  // '请选择交流类型'
        technicalExchangeStartTime: [{ required: true, message: this.$t('bidMod.selectStartTime') }],  // '请选择预计开始时间'
        technicalExchangeEndTime: [
          { required: true, message: this.$t('bidMod.selectEndTime') },  // '请选择预计结束时间'
          {
            validator: (rule, value, callback) => {
              if (value && this.detailFormData.technicalExchangeStartTime) {
                const startDate = new Date(this.detailFormData.technicalExchangeStartTime)
                const endDate = new Date(value)
                if (startDate.getTime() >= endDate.getTime()) {
                  // '预计结束时间需要大于预计开始时间'
                  callback(new Error(this.$t('bidMod.endTimeAndStartTime')))
                }
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        linkMan: [{ required: true, message: this.$t('bidMod.bidMsgList[21]'), trigger: 'blur' }],
        phone: [
          { required: true, message: this.$t('vendorMod.vendorModmsgMobilePhone') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                // 手机格式不合法
                callback(new Error(this.$t('bidMod.bidMsgList[26]')))
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        email: [
          // 请输入邮箱
          { required: true, message: this.$t('bidMod.bidMsgList[24]') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                // 请输入邮箱
                callback(new Error(this.$t('bidMod.bidMsgList[24]')))
              } else if (!isEmail(value)) {
                // 邮箱格式不合法
                callback(new Error(this.$t('bidMod.bidMsgList[25]')))
              }
              callback()
            },
            trigger: 'blur'
          }
        ]
      },
      activeDims: ['1', '2', '3', '4', '5', '6'],
      tecExcMaterialItems: [],
      tecExcFiles: [],
      tecExcVendors: [],
      itemFilesDialogVisible: false,
      attrsParamsRow: this.$attrs.params.row,
      isInitDetail: false
    }
  },

  computed: {
    /* 当前页面状态 */
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'view', 'feedbackView]
      const flag = this.$attrs.params.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isView: ['view', 'feedbackView'].includes(flag)
      }
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      this.technicalExchangeId = this.attrsParamsRow.technicalExchangeId
      this.getExcInfo()
    } else {
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
  },

  methods: {
    /* 查询单据 */
    async getExcInfo () {
      if (!this.technicalExchangeId) {
        return
      }

      const response = await techExchangeBuyerApi.getExcInfo(this.technicalExchangeId)
      this.isInitDetail = true
      if (response && response.data) {
        let formData = {}
        Object.keys(this.detailFormData).forEach(item => {
          formData = {
            ...formData,
            [item]: response.data[item]
          }
        })
        this.detailFormData = formData
        this.tecExcMaterialItems = response.data.tecExcMaterialItems
        this.tecExcVendors = response.data.tecExcVendors
        this.$nextTick(() => {
          // 更新附件表格
          this.$refs.sceneAttachment.loadFileInfo()
        })

        if (this.$attrs.params.flag === 'feedbackView') {
          // 查看反馈
          this.activeTab = 'feedback'
        }
      }
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      // 校验
      const valid = await this.$refs.detailForm.validate().catch(() => { /* noting */ })
      if (!valid) {
        this.__focus_error__()
        return
      }

      let submitData = {
        ...this.detailFormData,
        tecExcMaterialItems: [],
        tecExcVendors: [],
        tecExcFiles: this.tecExcFiles
      }

      if (this.technicalExchangeId) {
        submitData = {
          ...submitData,
          technicalExchangeId: this.technicalExchangeId
        }
      }

      // 校验并获取需求信息
      const materialItemsData = this.$refs.requirementsInfo.getParamsData()
      if (!materialItemsData.status) {
        return
      } else {
        submitData.tecExcMaterialItems = materialItemsData.data
      }

      // 校验并获取邀请供应商信息
      const vendorsData = this.$refs.originInviteSuppliers.getSuppliersPermissionData()
      if (vendorsData.length === 0) {
        // '请至少新增一项邀请供应商！'
        this.$message.warning(this.$t('bidMod.addOneInvitedSupplier'))
        return
      } else {
        submitData.tecExcVendors = vendorsData
      }

      if (type === 'SUBMIT') {
        const confirm = await this.$confirm(
          // '确定提交该技术交流单据吗？'
          this.$t('bidMod.sureSubmitTechnicalExchange'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }
        )
        if (confirm !== 'confirm') {
          return
        }
      }

      try {
        const response = await techExchangeBuyerApi[type.toLowerCase()](submitData)
        if (response) {
          if (type === 'SAVE') {
            this.$message.success(this.$t('common.successSave'))
            this.technicalExchangeId = response.data
            // 查询
            await this.getExcInfo()
          }
          if (type === 'SUBMIT') {
            this.$message.success(this.$t('common.successSubmit'))
            this.back('refresh')
          }
        }
      } catch (e) {
        console.error(e)
      }
    },

    /* 返回 */
    back (type) {
      this.$emit('tab-remove', this.$attrs.tabName)
      if (type === 'refresh') {
        this.__setTabTodo('TechExchangeManagementList.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.flex-container {
  padding-bottom: 50px;
}
.detail-form-wrap {
  padding: 15px 0;
}
</style>
