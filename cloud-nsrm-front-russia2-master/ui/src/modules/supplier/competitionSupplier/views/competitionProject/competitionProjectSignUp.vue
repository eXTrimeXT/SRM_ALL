<template>
  <el-container class="flex-container sign-up-wrap" direction="vertical">
    <el-main>
      <div class="sign-up-progress">
        <el-steps :active="1">
          <el-step :title="$t('bidMod.published')" />
          <el-step :title="$t('bidMod.signingUp')" />
          <el-step :title="$t('bidMod.registered')" />
          <el-step :title="$t('bidMod.eligibilityConfirm')" />
          <el-step :title="$t('bidMod.bidding')" />
          <el-step :title="$t('bidMod.finishBid')" />
        </el-steps>
      </div>

      <!--距离报名截止还剩余: -->
      <div class="sign-up-deadline">
        <DynamicCutoffTime :label="$t('bidMod.registerDeadline')" :deadline-time="paramsRow.signUpEndTime" />
      </div>

      <MainHeader>
        <template slot="left">
          <span style="padding-right: 11px">{{ $t("bidMod.inputRegisterInfo") }}</span>
          <span style="padding-right: 11px">{{ $t("bidMod.attachmentUpload") }}</span>
          <!--保存-->
          <el-button
            v-if="vendorSignUpFiles.length"
            type="primary"
            @click="saveOrSubmit('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>

          <!--提交-->
          <el-button type="primary" @click="saveOrSubmit('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>

          <!--返回-->
          <el-button @click="backTab">
            {{ $t('common.backTo') }}
          </el-button>
        </template>
      </MainHeader>

      <div class="vendor-file-table-wrap">
        <el-table
          ref="vendorSignUpFilesTable"
          :data="vendorSignUpFiles"
          style="width: 100%"
          border
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />

          <!--附件名称-->
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
            :label="$t('common.remark')"
            min-width="130"
            show-overflow-tooltip
          />

          <!--附件名称-->
          <SrmCommonFile
            type="table-column"
            :extra-data="fileInfo"
            :table-column-options="{
              label: $t('bidMod.fileName'),
              prop: 'signUpDocId',
              nameProp: 'signUpFileName',
              renderHeader: _addStarToColumn
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
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import { tabTodoMixin } from '@/utils/mixins'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import MainHeader from 'lib@/components/Table/MainHeader'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'

export default {
  name: 'CompetitionProjectSignUp',

  components: {
    MainHeader,
    DynamicCutoffTime
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      vendorSignUpFiles: [],
      fileInfo: {
        fileModular: 'comp',
        fileFunction: 'competitionProjectSignUp',
        fileType: 'images'
      },
      paramsRow: {
        projectId: this.$attrs.params.row.projectId,
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
      const response = await compVendorHttp.signUp.getSignUpInfo(this.paramsRow.projectId)
      if (response && response.data) {
        const { signUpFileList } = response.data
        if (signUpFileList && Array.isArray(signUpFileList)) {
          this.vendorSignUpFiles = signUpFileList
        }
      }
    },

    /* 文件变更 */
    vendorSignUpFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorSignUpFiles[$index].signUpDocId = fileId
      this.vendorSignUpFiles[$index].signUpFileName = fileName
    },

    /* 提交保存 */
    async saveOrSubmit (type) {
      if (!validateRequiredColumn(
        this.$refs.vendorSignUpFilesTable,
        this.vendorSignUpFiles,
        {
          validateScope: false,
          tableTitle: this.$t('bidMod.signupFile')
        }
      )) {
        return
      }

      const response = await compVendorHttp.signUp.vendorSignUp({
        projectId: this.paramsRow.projectId,
        signUpFileList: this.vendorSignUpFiles,
        isTempSave: type === 'SAVE'
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
        this.__setTabTodo('CompetitionProjectList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.sign-up-wrap) {
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
