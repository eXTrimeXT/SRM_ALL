<template>
  <el-container class="flex-container bargain-orders-sign-up" direction="vertical">
    <el-main>
      <div class="sign-up-progress">
        <el-steps :active="1">
          <el-step :title="$t('bidMod.published')" />
          <el-step :title="$t('bidMod.signingUp')" />
          <el-step :title="$t('bidMod.registered')" />
          <el-step :title="$t('bidMod.eligibilityConfirm')" />
          <el-step :title="$t('bidMod.quote')" />
          <el-step :title="$t('bidMod.completeQuotation')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="sign-up-deadline">
        <DynamicCutoffTime :label="$t('bidMod.registerDeadline')" :deadline-time="paramsRow.signUpEndTime" />
      </div>

      <MainHeader>
        <template slot="left">
          <span style="padding-right: 11px">{{ $t("bidMod.inputRegisterInfo") }}</span>
          <span style="padding-right: 11px">{{ $t("bidMod.attachmentUpload") }}</span>
          <!--保存-->
          <el-button
            v-if="signUpFileList.length"
            type="primary"
            @click="saveOrSubmit('SAVE')"
          >
            {{ $t("common.save") }}
          </el-button>

          <!--提交-->
          <el-button type="primary" @click="saveOrSubmit('SUBMIT')">
            {{ $t("common.submit") }}
          </el-button>

          <!--返回-->
          <el-button @click="backTab">
            {{ $t("bidMod.backTo") }}
          </el-button>
        </template>
      </MainHeader>

      <div class="vendor-file-table-wrap">
        <el-table
          :data="signUpFileList"
          style="width: 100%"
          border
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />

          <!--参考文件-->
          <SrmCommonFile
            type="table-column"
            :table-column-options="{
              label: $t('bid_mod.referenceFile'),
              prop: 'souDocId',
              nameProp: 'souFileName'
            }"
            readonly
          />

          <!--备注-->
          <el-table-column
            align="center"
            prop="souRemark"
            :label="$t('bidMod.remark')"
            min-width="130"
            show-overflow-tooltip
          />

          <!--附件名称-->
          <SrmCommonFile
            type="table-column"
            :table-column-options="{
              label: $t('bidMod.fileName'),
              prop: 'signUpDocId',
              nameProp: 'signUpFileName'
            }"
            @on-change="vendorSignUpFileChange"
          />
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名
 */
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'

export default {
  name: 'BargainOrdersSignUp',

  components: {
    MainHeader,
    DynamicCutoffTime
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      signUpFileList: [],
      paramsRow: {
        projectId: this.$attrs.params.row.projectId,
        souNo: this.$attrs.params.row.souNo,
        souName: this.$attrs.params.row.souName,
        signUpEndTime: this.$attrs.params.row.signUpEndTime || ''
      }
    }
  },

  created () {
    this.getSignUpInfo()
  },

  methods: {
    /* 查询报名详情 */
    async getSignUpInfo () {
      const response = await brgSupplierHttp.signUp.getSignUpInfo(this.paramsRow.projectId)
      if (response && response.data) {
        this.signUpFileList = response.data.signUpFileList || []
      }
    },

    /* 内部查看文件变更 */
    vendorSignUpFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.signUpFileList[$index].signUpDocId = fileId
      this.signUpFileList[$index].signUpFileName = fileName
    },

    /* 提交保存 */
    async saveOrSubmit (type) {
      for (let item of this.signUpFileList) {
        if (!item.signUpFileName) {
          // 请上传附件！
          this.$message.warning(this.$t('bidMod.pleaseUploadFile'))
          return
        }
      }

      const response = await brgSupplierHttp.signUp.vendorSignUp({
        projectId: this.paramsRow.projectId,
        isTempSave: type === 'SAVE',
        signUpFileList: this.signUpFileList
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        if (type === 'SUBMIT') {
          this.backTab('refresh')
        } else {
          await this.getSignUpInfo()
        }
      }
    },

    /* 返回标签页 */
    backTab (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        // 刷新列表
        this.__setTabTodo('BargainOrdersList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.bargain-orders-sign-up ::v-deep {
  .sign-up-progress {
    padding: 11px;
    background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
    .the_footer_row {
      float: right;
      font-size: 20px;
    }
  }
}
</style>
