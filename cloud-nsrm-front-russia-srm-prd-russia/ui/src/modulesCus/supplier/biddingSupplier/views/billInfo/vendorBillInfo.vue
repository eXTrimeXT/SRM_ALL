<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main class="el-main">
      <el-form
        ref="billInfoForm"
        :model="billInfoForm"
        :rules="rules"
      >
        <srm-row>
          <srm-col :init-col="4">
            <el-form-item
              prop="vendorName"
              label="开票公司"
            >
              <el-input v-model="billInfoForm.vendorName" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              prop="taxPayer"
              label="纳税人识别号"
            >
              <el-input v-model="billInfoForm.taxPayer" maxlength="20" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              prop="phone"
              label="电话"
            >
              <el-input v-model="billInfoForm.phone" maxlength="20" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              prop="email"
              label="发票接收邮箱"
            >
              <el-input v-model="billInfoForm.email" maxlength="50" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              prop="bankAccount"
              label="开户银行账户"
            >
              <el-input v-model="billInfoForm.bankAccount" maxlength="30" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              prop="bankName"
              label="开户银行名称"
            >
              <el-input v-model="billInfoForm.bankName" maxlength="100" type="textarea" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              prop="address"
              label="地址"
            >
              <el-input v-model="billInfoForm.address" type="textarea" maxlength="80" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <CToolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="saveInfo"
          >
            {{ $t('common.save') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'VendorBillInfo',
  components: { CToolbar },
  data () {
    return {
      billInfoForm: {
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        taxPayer: null,
        phone: null,
        email: null,
        bankName: null,
        bankAccount: null,
        address: null,
        status: null
      },
      rules: {
        taxPayer: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        phone: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        email: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        bankName: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        bankAccount: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        address: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }]
      }
    }
  },
  created () {
    const { companyId, companyCode, companyName } = this.$store.getters.userInfo
    this.billInfoForm.vendorId = companyId
    this.billInfoForm.vendorCode = companyCode
    this.billInfoForm.vendorName = companyName
    this.getFormDetail(companyId)
  },
  methods: {
    getFormDetail (companyId) {
      const searchData = {
        type: 'SouInvoiceInfo',
        action: 'query',
        lang: 'zh-cn',
        payload: {
          filter: {
            vendorId: {
              eq: companyId
            }
          }
        },
        query: { '*': {} }
      }
      this.$http({
        url: '/api-sou/api-ql/SouInvoiceInfo/query',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res.data && res.data.ref && res.data.ref.SouInvoiceInfo) {
          let souInvoiceInfo = res.data.ref.SouInvoiceInfo
          let invoiceInfoId = Object.keys(souInvoiceInfo)[0]
          this.billInfoForm = souInvoiceInfo[invoiceInfoId]
        }
      })
    },
    saveInfo () {
      this.$refs.billInfoForm.validate(valid => {
        if (valid) {
          const saveData = transformMQL.save('SouInvoiceInfo', [this.billInfoForm], 'save')
          this.$http({
            url: '/api-sou/api-ql/SouInvoiceInfo/save',
            method: 'POST',
            data: saveData,
            loading: true
          }).then(res => {
            this.$message.success(this.$t('common.success'))
            this.getFormDetail(this.billInfoForm.vendorId)
          })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>

</style>
