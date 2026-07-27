<template>
  <el-container
    class="the-sourcingPublicityConfigDetail-detail"
    direction="vertical"
  >
    <el-main class="form-container">
      <el-form
        ref="detailForm"
        :model="detailForm"
        :rules="rules"
        :disabled="!editable"
        label-position="top"
      >
        <srm-row>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.pubconfigName')"
              prop="pubconfigName"
            >
              <el-input v-model="detailForm.pubconfigName" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('perfMod.templateStatus')"
              prop="status"
            >
              <dict-select v-model="detailForm.status" code="SOURCE_PUBCONFIG_STATUS" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('common.creator')"
              prop="createdFullName"
            >
              <el-input v-model="detailForm.createdFullName" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('common.creationDate')"
              prop="creationDate"
            >
              <el-date-picker 
                v-model="detailForm.creationDate"
                type="date"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                disabled
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.common.applicablePlate')"
              prop="organizationName"
            >
              <quick-search
                :disabled="!editable"
                :show-input="detailForm.organizationName"
                show-key="organizationName"
                :scope-data="detailForm"
                name="plate_scc_base_organization"
                @close-quicksearch="getOrgObj"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.common.pubPerson')"
              prop="chargeFullName"
            >
              <quick-search
                :disabled="!editable"
                :show-input="detailForm.chargeFullName"
                show-key="nickname"
                :scope-data="detailForm"
                name="scc_rbac_user_display"
                @close-quicksearch="getUserObj"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
        <srm-row>
          <srm-col :init-col="1">
            <p style="font-weight: bold;">
              {{ $t('cusEntry.biddingSettings.account') }}
            </p>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.bankName')"
              prop="bankName"
            >
              <el-input v-model="detailForm.bankName" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.bankNumber')"
              prop="bankNumber"
            >
              <el-input v-model="detailForm.bankNumber" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.bankAccount')"
              prop="bankAccount"
            >
              <el-input v-model="detailForm.bankAccount" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.bankAccountName')"
              prop="bankAccountName"
            >
              <el-input v-model="detailForm.bankAccountName" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
        </srm-row>
        <srm-row style="margin-top:10px">
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.signType')"
              prop="signType"
            >
              <Tinymce
                id="signTypeTinymce"
                v-model="detailForm.signType"
                :height="300"
                :minHeight="300"
                @setup="ready"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.earnestDescr')"
              prop="earnestDescr"
            >
              <Tinymce
                id="earnestDescrTinymce"
                v-model="detailForm.earnestDescr"
                :height="300"
                :minHeight="300"
                @setup="ready"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.pubMedium')"
              prop="pubMedium"
            >
              <Tinymce
                id="pubMediumTinymce"
                v-model="detailForm.pubMedium"
                :height="300"
                :minHeight="300"
                @setup="ready"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.disputeReso')"
              prop="disputeReso"
            >
              <Tinymce
                id="disputeResoTinymce"
                v-model="detailForm.disputeReso"
                :height="300"
                :minHeight="300"
                @setup="ready"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.groupTipOff')"
              prop="groupTipOff"
            >
              <Tinymce
                id="groupTipOffTinymce"
                v-model="detailForm.groupTipOff"
                :height="300"
                :minHeight="300"
                @setup="ready"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <CToolbar>
        <template slot="right">
          <el-button @click="backBill">
            {{ !editable ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button
            v-if="editable"
            type="primary"
            @click="savePubconfig('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            v-if="editable"
            type="primary"
            @click="submitPubconfig('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import Tinymce from '@/modulesCus/buyer/biddingSettings/components/Tinymce'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'SourcingPublicityConfigDetail',
  components: {
    CToolbar,
    QuickSearch,
    Tinymce
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      editable: '',
      detailForm: {
        pubconfigId: '',
        configNumber: '',
        pubconfigName: '',
        status: '',
        createdFullName: '',
        creationDate: '',
        bankName: '',
        bankNumber: '',
        bankAccount: '',
        bankAccountName: '',
        signType: '',
        earnestDescr: '',
        pubMedium: '',
        disputeReso: '',
        groupTipOff: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        chargeFullName: '',
        chargeUserName: ''
      },
      rules: {
        pubconfigName: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        bankName: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        bankNumber: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        bankAccount: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        bankAccountName: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        signType: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        earnestDescr: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        pubMedium: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        disputeReso: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        groupTipOff: [{ required: true, trigger: 'blur', message: this.$t('cusEntry.common.pleaseFill') }],
        organizationName: [{ required: true, trigger: ['blur', 'change'], message: this.$t('common.pleaseSelect') }],
        chargeFullName: [{ required: true, trigger: ['blur', 'change'], message: this.$t('common.pleaseSelect') }]
      }
    }
  },
  created () {
    const { createdUserName } = this.$store.getters.userInfo
    const { flag, row } = this.$attrs.params
    this.editable = flag !== 'view'
    if (flag == 'add') {
      this.detailForm.status = 'DRAFT'
      this.detailForm.createdFullName = createdUserName
      this.detailForm.creationDate = parseTime(new Date(), '{y}-{m}-{d}')
    } else {
      this.getFormDetail(row)
    }
  },
  methods: {
    getOrgObj (val, scope) {
      scope.organizationId = val ? val.organizationId : ''
      scope.organizationCode = val ? val.organizationCode : ''
      scope.organizationName = val ? val.organizationName : ''
    },
    getUserObj (val, scope) {
      scope.chargeUserName = val ? val.username : ''
      scope.chargeFullName = val ? val.nickname : ''
    },
    ready (editorInstance) {
      if (!this.editable) {
        editorInstance.setMode('readonly')
      }
    },
    backBill () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'sourcingPublicityConfigDetail' + this.$attrs.params.row.pubconfigId)
      } else {
        this.$emit('tab-remove', 'sourcingPublicityConfigDetail')
      }
    },
    getFormDetail (row) {
      this.$http({
        url: '/api-pj/source/pubconfig/queryPubconfig',
        method: 'GET',
        params: { pubconfigId: row.pubconfigId },
        loading: true
      }).then(res => {
        this.detailForm = res.data
      })
    },
    savePubconfig (type) {
      const params = {
        type,
        sourcePubconfig: { ...this.detailForm }
      }
      this.$http({
        url: '/api-pj/source/pubconfig/savePubconfig',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        type == 'SAVE' && this.getFormDetail(res.data)
        type == 'SUBMIT' && this.backBill()
        this.__setTabTodo('sourcingPublicityConfigList.getQuerydata')
      })
    },
    submitPubconfig (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          this.savePubconfig(type)
        } else {
          return this.$message.warning(this.$t('common.pleasefinishRequired'))
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.the-sourcingPublicityConfigDetail-detail{
  .form-container{
    margin: 16px;
  }
}
</style>
