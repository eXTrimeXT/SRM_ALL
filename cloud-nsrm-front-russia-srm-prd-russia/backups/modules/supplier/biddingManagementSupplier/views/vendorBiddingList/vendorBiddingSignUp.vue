<template>
  <el-container class="flex-container the_vendorBiddingSignUp_wrapper" direction="vertical">
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

      <!--报价截止倒计时-->
      <div class="sign-up-deadline">
        <dynamic-cutoff-time
          :label="$t('bidMod.registerDeadline')"
          :deadline-time="paramsRow.enrollEndDatetime"
        />
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
            {{ $t("common.save") }}
          </el-button>

          <!--提交-->
          <el-button type="primary" @click="saveOrSubmit('SUBMIT')">
            {{ $t("common.submit") }}
          </el-button>

          <!--返回-->
          <el-button v-if="!$route.query.bidingId" @click="backTab">
            {{ $t("bidMod.backTo") }}
          </el-button>
        </template>
      </MainHeader>

      <div class="vendor-file-table-wrap">
        <el-table :data="vendorSignUpFiles" style="width: 100%" border>
          <el-table-column align="center" type="index" width="50" />

          <!--参考文件-->
          <SrmCommonFile
            type="table-column"
            :table-column-options="{
              label: $t('bid_mod.referenceFile'),
              prop: 'requireDocId',
              nameProp: 'requireFileName'
            }"
            readonly
          />

          <!--备注-->
          <el-table-column
            align="center"
            prop="requireComments"
            :label="$t('bidMod.remark')"
            min-width="130"
            show-overflow-tooltip
          />

          <!--附件名称-->
          <SrmCommonFile
            type="table-column"
            :extra-data="fileInfo"
            :table-column-options="{
              label: $t('bidMod.fileName'),
              prop: 'vendorDocId',
              nameProp: 'vendorFileName'
            }"
            @on-change="vendorSignUpFilesChange"
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
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import dynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'

export default {
  name: 'VendorBiddingSignUp',

  components: {
    MainHeader,
    dynamicCutoffTime
  },

  mixins: [tabTodoMixin],
  data () {
    return {
      vendorSignUpFiles: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingList',
        fileType: 'images'
      },
      paramsRow: {
        // bidingId: this.$attrs.params.row.bidingId,
        // bidingNum: this.$attrs.params.row.bidingNum,
        // bidingName: this.$attrs.params.row.bidingName,
        // enrollEndDatetime: this.$attrs.params.row.enrollEndDatetime || ''
      },
      editIndex: ''
    }
  },
  created () {
    // 2022-08-09：需要从公告直接跳转招标详情
    if (this.$route.query.bidingId) {
      let { query } = this.$route
      this.paramsRow = {
        bidingId: query.bidingId,
        bidingNum: query.bidingNum,
        bidingName: query.bidingName,
        enrollEndDatetime: query.enrollEndDatetime || ''
      }
    } else {
      this.paramsRow = {
        bidingId: this.$attrs.params.row.bidingId,
        bidingNum: this.$attrs.params.row.bidingNum,
        bidingName: this.$attrs.params.row.bidingName,
        enrollEndDatetime: this.$attrs.params.row.enrollEndDatetime || ''
      }
    }
    this.getSignUpInfo()
  },
  methods: {
    /* 查询报名详情 */
    getSignUpInfo () {
      this.$http({
        url: `/api-bid/supplierCooperate/bidSingUp/getBidSignUpFilesForVendor/${this.paramsRow.bidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.vendorSignUpFiles = data.data
        }
      })
    },

    /* 供应商报名文件变更 */
    vendorSignUpFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorSignUpFiles[$index].vendorDocId = fileId
      this.vendorSignUpFiles[$index].vendorFileName = fileName
    },

    /* 返回标签页 */
    backTab (type) {
      this.$emit('tab-remove', `vendorBiddingSignUp${this.paramsRow.bidingNum}`)
      if (type === 'todo') {
        // 刷新列表
        this.__setTabTodo('VendorBiddingList.getQueryData')
      }
    },

    /* 提交保存 */
    saveOrSubmit (type) {
      for (let item of this.vendorSignUpFiles) {
        if (!item.vendorFileName) {
          // 请上传附件！
          this.$message.warning(this.$t('bidMod.pleaseUploadFile'))
          return
        }
      }
      this.$http({
        url: '/api-bid/supplierCooperate/bidSingUp/tempSaveOrSubmitFiles',
        method: 'POST',
        data: {
          bidingId: this.paramsRow.bidingId,
          isTempSave: type === 'SAVE',
          signUpFiles: this.vendorSignUpFiles
        },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successSubmit'))

        if (!this.$route.query.bidingId) {
          this.backTab('todo')
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_vendorBiddingSignUp_wrapper) {
  .sign-up-progress {
    padding: 11px;
    margin: 10px;
    background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
    .the_footer_row {
      float: right;
      font-size: 20px;
    }
  }
  .sign-up-deadline {
    padding-left: 17px;
  }
  .vendor-file-table-wrap {
    padding: 0 20px;
  }
}
</style>
