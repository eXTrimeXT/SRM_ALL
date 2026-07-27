<template>
  <el-container class="quasupplierenoticeEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          class="form-fill-style"
          :disabled="isReadOnly || form.orderStatus == 'published'"
        >
          <div class="noticeStyle">
            <el-row :gutter="32">
              <el-col :span="6">
                <!-- 通知编号 -->
                <el-form-item prop="noticeId" :label="$t('qualitySynergy.notificationNum')">
                  <el-input v-model="form.noticeId" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 供应商名称 -->
                <el-form-item prop="vendorName" :label="$t('common.vendorName')">
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :disabled="isReadOnly || form.orderStatus == 'published'"
                    :scope-data="form"
                    name="scc_sup_company_info_display"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 发布日期 -->
                <el-form-item prop="releaseDate" :label="$t('dataConfMod.publishTime')">
                  <el-date-picker
                    v-model="form.releaseDate"
                    disabled
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 单据状态 -->
                <el-form-item :label="$t('bidMod.billstatus')">
                  <el-select v-model="form.orderStatus" disabled>
                    <el-option
                      v-for="item in statusList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 业务实体 -->
                <el-form-item prop="orgId" :label="$t('dataConfMod.orgId')">
                  <el-input v-if="isReadOnly" v-model="form.orgName" />
                  <template v-else>
                    <el-select v-model="form.orgId" @change="orgNameF">
                      <el-option
                        v-for="item in orgList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </template>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 采购员 -->
                <el-form-item prop="purchaserName" :label="$t('bidMod.quotePurchasor')">
                  <el-input v-model="form.purchaserName" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 问题类别 -->
                <el-form-item prop="problemType" :label="$t('qualitySynergy.problemType')">
                  <el-select v-model="form.problemType">
                    <el-option
                      v-for="item in problemList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 处罚金额 -->
                <el-form-item prop="fineAmount" :label="$t('qualitySynergy.fineAmount')">
                  <el-input v-model="form.fineAmount" type="number" />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 处理依据 -->
                <el-form-item :label="$t('qualitySynergy.accoundingDeal')">
                  <el-checkbox-group v-model="accoundingDeal">
                    <el-row :gutter="32">
                      <el-col v-for="(item, index) in accoundingDeals" :key="index" :span="6">
                        <el-checkbox :label="item">
                          {{ item }}
                        </el-checkbox>
                      </el-col>
                    </el-row>
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
              <el-col :span="18">
                <!-- 异常问题描述 -->
                <el-form-item prop="exceptionDescribe" :label="$t('qualitySynergy.exceptionDesc')">
                  <el-input v-model="form.exceptionDescribe" type="textarea" />
                </el-form-item>
              </el-col>
              <el-col :span="18">
                <!-- 备注 -->
                <el-form-item prop="noticeComments" :label="$t('common.remark')">
                  <el-input v-model="form.noticeComments" type="textarea" />
                </el-form-item>
              </el-col>
              <el-col :span="18">
                <!-- 异常问题处理结果 -->
                <el-form-item
                  prop="exceptionDealingResult"
                  :label="$t('qualitySynergy.exceptionDealingResult')"
                >
                  <el-input v-model="form.exceptionDealingResult" type="textarea" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <el-collapse v-model="activeDims">
            <!-- 附件上传 -->
            <el-collapse-item
              ref="attachmentInfo"
              :title="$t('vendorMod.attachmentUpload')"
              name="1"
            >
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.fileUploads"
                scene-module-code="SCENE_QUA_SUPPLIER_ENOTICE_ATTACHMENT"
                :business-id="curNoticeId"
                :editable="viewflag!='readOnly'"
              />
            </el-collapse-item>
          </el-collapse>
          <div class="noticeStyle">
            <el-row :gutter="32">
              <el-col :span="6">
                <!-- 供方查阅时间 -->
                <el-form-item
                  prop="supplierReadTime"
                  :label="$t('qualitySynergy.supplierReadTime')"
                >
                  <el-date-picker
                    v-model="form.supplierReadTime"
                    disabled
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-form>
      </div>

      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            {{
              isReadOnly ? $t('common.close') : $t('common.cancel')
            }}
          </el-button>
          <el-button
            v-if="!isReadOnly && form.orderStatus == 'published' && curRole === 'BUYER'"
            @click="save('nullify')"
          >
            {{ $t('common.cancelled') }}
          </el-button>
          <el-button
            v-if="!isReadOnly && form.orderStatus == 'draft' && curRole === 'BUYER'"
            @click="save('staging')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="!isReadOnly && form.orderStatus == 'draft' && curRole === 'BUYER'"
            type="primary"
            @click="save('published')"
          >
            {{ $t('common.publish') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import i18n from '@/lang'
import { excHandlingNotice } from 'modb@/qualitySynergy/api'
const accoundingDealOptions = [
  i18n.t('qualitySynergy.accoundingDealOptions[0]'),
  i18n.t('qualitySynergy.accoundingDealOptions[1]'),
  i18n.t('qualitySynergy.accoundingDealOptions[2]'),
  i18n.t('qualitySynergy.accoundingDealOptions[3]'),
  i18n.t('qualitySynergy.accoundingDealOptions[4]'),
  i18n.t('qualitySynergy.accoundingDealOptions[5]')
]
export default {
  name: 'ExcHandlingNpticeDetail',
  components: {
    CToolbar,
    QuickSearch,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      orgList: [], // 业务实体列表
      curRole: this.$store.getters.userType,
      fileRefresh: false,
      activeDims: ['1'],
      accoundingDeals: accoundingDealOptions,
      accoundingDeal: [],
      form: {
        noticeId: null,
        vendorId: null,
        vendorName: null,
        vendorCode: null,
        releaseDate: parseTime(new Date(), '{y}-{m}-{d}'),
        orderStatus: 'draft',
        orgId: null,
        orgName: null,
        purchaserName: null,
        problemType: null,
        fineAmount: null,
        accoundingDeal: null,
        exceptionDescribe: null,
        noticeComments: null,
        exceptionDealingResult: null,
        supplierReadTime: null,
        fileUploads: []
      },
      globalUserId: null,
      viewflag: '',
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips'), trigger: 'change' }],
        vendorName: [{
          required: true,
          message: this.$t('componentDoc.msgSelVendor'),
          trigger: 'change'
        }],
        problemType: [
          {
            required: true,
            message: this.$t('qualitySynergy.msgSelProblemType'),
            trigger: 'change'
          }
        ],
        fineAmount: [{ required: true, message: this.$t('qualitySynergy.msgFineAmount'), trigger: 'blur' }],
        accoundingDeal: [{ required: true, message: this.$t('qualitySynergy.atLeastSelOne'), trigger: 'change' }]
      },
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      statusList: [
        { value: 'draft', label: this.$t('qualitySynergy.draft') }, // 拟定
        { value: 'published', label: this.$t('qualitySynergy.published') }, // 已发布
        {
          value: 'vendorChecked',
          label: this.$t('qualitySynergy.vendorChecked')
        }, // 供应商已查阅
        { value: 'invalid', label: this.$t('qualitySynergy.invalid') } // 已作废
      ],
      problemList: [
        {
          value: '交易延期',
          label: this.$t('qualitySynergy.problemTypeList[0]')
        }, // 交易延期
        {
          value: '质量问题',
          label: this.$t('qualitySynergy.problemTypeList[1]')
        }, // 质量问题
        {
          value: '质量事故',
          label: this.$t('qualitySynergy.problemTypeList[2]')
        }, //  质量事故
        {
          value: '有害物质超标',
          label: this.$t('qualitySynergy.problemTypeList[3]')
        }, // 有害物质超标
        {
          value: '供应商考核',
          label: this.$t('qualitySynergy.problemTypeList[4]')
        }, // 供应商考核
        {
          value: '其它',
          label: this.$t('qualitySynergy.problemTypeList[5]')
        } // 其它
      ],
      curNoticeId: null // 单据ID
    }
  },
  created () {
    this.globalUserId = this.$store.getters.userInfo.userId
    this.viewflag = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'add') {
      this.form.purchaserName = this.$store.getters.userInfo.nickname
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else {
      this.curNoticeId = this.$attrs.params.noticeId // 单据Id
      this.getFormDetail(this.$attrs.params.noticeId)
      this.fileRefresh = true
    }
  },
  methods: {
    orgNameF (val) {
      console.log(this.orgList)
      this.orgList.forEach(e => {
        if (val == e.value) {
          this.form.orgName = e.label
        }
      })
    },
    listOrgByUserAndCompany (vendorId) {
      excHandlingNotice.listOrgByUserAndCompany(vendorId).then(res => {
        const datas = res.data
        let dataList = []
        datas.forEach(element => {
          let obj = {
            label: element.organizationName,
            value: element.organizationId
          }
          dataList.push(obj)
        })
        this.orgList = dataList
      })
    },
    getVendorObj (val, data) {
      this.form.vendorId = val ? val.companyId : ''
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''

      this.form.orgId = ''
      this.listOrgByUserAndCompany(this.form.vendorId)// 根据供应商Id查询业务实体
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'excHandlingNoticeDetail')
      } else {
        this.$emit('tab-remove', 'excHandlingNoticeDetail' + row.noticeId)
      }
      this.__setTabTodo('excHandlingNoticeList.getQuerydata')
    },
    getFormDetail (noticeId) {
      let url = this.curRole === 'BUYER' ? '/api-pef/quasupplierenotice/get' : '/api-pef/quasupplierenotice/getVendorQuaSupplierEnotice'
      excHandlingNotice.getExcHandlingNoticeDetail(url, { noticeId }).then((res) => {
        if (res.data) {
          this.form = res.data
          this.accoundingDeal = res.data.accoundingDeal ? res.data.accoundingDeal.split(',') : []
          this.listOrgByUserAndCompany(this.form.vendorId)// 根据供应商Id查询业务实体

          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
          })
        }
      })
    },
    save (val) {
      let validFlag
      if (val !== 'staging') {
        this.$refs.form.validate((valid) => { validFlag = valid })
        if (!validFlag) {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
          return
        }
      }
      const { flag } = this.$attrs.params
      // 新增时不用提交主键值
      this.form.accoundingDeal = this.accoundingDeal.toString()
      if (val == 'published') {
        this.form.orderStatus = 'published'
      } else if (val == 'nullify') {
        this.form.orderStatus = 'invalid'
      }
      const { noticeId, ...rest } = this.form
      if (flag === 'add') {
        excHandlingNotice.noticeAdd(rest).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      } else if (flag === 'edit') {
        excHandlingNotice.noticeModify(this.form).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancelBill()
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.quasupplierenoticeEdit {
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
:deep(.el-checkbox-group) {
  padding-top: 30px;
}
.form-container {
  padding-bottom: 50px;
}
.btn_line {
  margin: 0 0 8px 0;
}

.noticeStyle {
  padding-left: 11px;
}
</style>
