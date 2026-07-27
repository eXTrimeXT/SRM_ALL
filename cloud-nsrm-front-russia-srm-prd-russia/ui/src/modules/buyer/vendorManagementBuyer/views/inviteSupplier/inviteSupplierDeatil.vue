<template>
  <el-container
    class="flex-container the-inviteSupplierDeatil-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-form
          ref="ruleForm"
          :model="inviteForm"
          :rules="rules"
          class="form-fill-style"
        >
          <!-- 单据信息 -->
          <el-collapse-item
            :title="$t('vendorMod.receiptInfo')"
            name="1"
          >
            <srm-row :gutter="32">
              <!-- 单据编码 -->
              <srm-col :span="6">
                <el-form-item
                  prop="inviteVendorNo"
                  :label="$t('vendorMod.inviteVendorNo')"
                >
                  <el-input
                    v-model="inviteForm.inviteVendorNo"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <el-form-item
                  prop="inviteReason"
                  :label="$t('vendorMod.inviteReason')"
                >
                  <el-input
                    v-model="inviteForm.inviteReason"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
              <!-- 备注 -->
              <srm-col :span="6">
                <el-form-item
                  prop="remark"
                  :label="$t('vendorMod.remark')"
                >
                  <el-input
                    v-model="inviteForm.remark"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <!-- 联系人信息 -->
          <el-collapse-item
            :title="$t('vendorMod.contactInfo')"
            name="2"
          >
            <srm-row :gutter="32">
              <!-- 联系人 -->
              <srm-col :span="6">
                <el-form-item
                  prop="contactPerson"
                  :label="$t('vendorMod.contactPerson')"
                >
                  <el-input
                    v-model="inviteForm.contactPerson"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
              <!-- 联系邮箱 -->
              <srm-col :span="6">
                <el-form-item
                  prop="contactEmail"
                  :label="$t('vendorMod.contactEmail')"
                >
                  <el-input
                    v-model="inviteForm.contactEmail"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
              <!-- 手机号码 -->
              <srm-col :span="6">
                <el-form-item
                  prop="phoneNumber"
                  :label="$t('vendorMod.mobilePhone')"
                >
                  <el-input
                    v-model="inviteForm.phoneNumber"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <!-- 供应商信息 -->
          <el-collapse-item
            :title="$t('vendorMod.vendorInfo')"
            name="3"
          >
            <srm-row :gutter="32">
              <!-- 供应商名称 -->
              <srm-col :span="6">
                <el-form-item
                  prop="vendorName"
                  :label="$t('vendorMod.vendorName')"
                >
                  <el-input
                    v-model="inviteForm.vendorName"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
              <!-- 统一社会信用代码 -->
              <srm-col :span="6">
                <el-form-item
                  prop="socialCreditCode"
                  :label="$t('vendorMod.socialCreditCode')"
                >
                  <el-input
                    v-model="inviteForm.socialCreditCode"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
        </el-form>
      </el-collapse>

      <CToolbar v-if="!readOnly">
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.backTo') }}
          </el-button>
          <el-button
            :disabled="readOnly"
            @click="saveDataHandle('DRAFT')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="saveDataHandle('PUBLISH')"
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
import { isMobile, isEmail } from 'lib@/utils/validate'
export default {
  name: 'InviteSupplierDeatil',
  components: {
    QuickSearch,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      inviteForm: {
        inviteVendorId: null,
        inviteVendorNo: null,
        inviteStatus: null,
        inviteReason: null,
        remark: null,
        contactPerson: null,
        contactEmail: null,
        phoneNumber: null,
        vendorName: null,
        socialCreditCode: null
      },
      rules: {
        inviteReason: [{ required: true, message: this.$t('bidMod.mgsinviteVendor') }], // 邀请原因
        contactPerson: [{ required: true, message: this.$t('dataConfMod.msgContactName') }], // 联系人
        contactEmail: [
          { required: true, message: this.$t('bidMod.bidMsgList[24]') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('vendorMod.pleaseInputEmail'))) // 请输入邮箱
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('vendorMod.emailFormatIsWrong'))) // 邮箱格式不对,请重新输入
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }],
        phoneNumber: [
          {
            validator: (rule, value, callback) => {
              if (value) {
                if (!isMobile(value)) {
                  callback(new Error(this.$t('vendorMod.phoneFormatIsWrong')))
                } else {
                  callback()
                }
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      readOnly: false,
      curOpt: 'view'
    }
  },
  computed: {},
  created () {},
  mounted () {
    const { flag, row } = this.$attrs.params
    this.readOnly = flag === 'view'
    this.curOpt = flag
    if (flag === 'edit' || flag === 'view') {
      this.getOrder(row.inviteVendorId)
      // this.inviteForm = row;
    }
  },
  methods: {
    // 保存数据操作
    async saveDataHandle (type) {
      let inviteFormData = this.inviteForm
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let me = this
          let url = ''
          // 暂存
          if (type === 'DRAFT') {
            url = '/api-sup/invite/inviteVendor/add'
            if (this.curOpt === 'edit') {
              url = '/api-sup/invite/inviteVendor/modify'
            }
          } else {
            // 发布
            url = '/api-sup/invite/inviteVendor/publish'
          }

          this.$http({
            url: url,
            method: 'POST',
            data: inviteFormData,
            loading: true
          })
            .then(async (res) => {
              if (me.curOpt === 'edit' || me.curOpt === 'add') {
                if (type === 'DRAFT') {
                  this.inviteForm.inviteVendorId = res.data
                  this.curOpt = 'edit'
                  if (res.data) {
                    this.getOrder(res.data)
                  }
                  this.$message.success(this.$t('vendorMod.temporarySuccess'))
                } else if (type === 'PUBLISH') {
                  this.$message({
                    type: 'success',
                    message: this.$t('common.successSubmit')
                  }) // 提交成功
                  this.cancelBill()
                }
              }
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    getOrder (id) {
      this.$http({
        url: '/api-sup/invite/inviteVendor/get',
        method: 'get',
        params: { id },
        loading: true
      })
        .then((res) => {
          this.inviteForm = res.data
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 这里校验邮件手机，统一信用代码信息
    async validateForm () {
      return true
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'inviteSupplierDeatil')
      } else {
        this.$emit('tab-remove', 'inviteSupplierDeatil' + row.inviteVendorNo)
      }
      this.__setTabTodo('inviteSupplierList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.inviteVendorEdit {
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
