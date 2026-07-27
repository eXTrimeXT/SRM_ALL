<template>
  <!-- 选择汇总人 -->
  <srm-dialog
    :visible.sync="visibleDialog"
    :title="$t('oneStopShopping.setSummaryAndNoticeUser')"
    size="small"
    destroy-on-close
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-form
      ref="pubForm"
      :model="sumForm"
      :rules="sumRules"
    >
      <el-form-item
        prop="beginQuote"
        :label="$t('oneStopShopping.summaryUser')"
      >
        <QuickSearch
          :show-input="sumForm.summaryNickname"
          show-key="username"
          :scope-data="sumForm"
          auto-query
          name="scc_rbac_user_display"
          @close-quicksearch="getUserObj"
        />
      </el-form-item>
      <el-form-item
        prop="deadline"
        :label="$t('oneStopShopping.noticeUser')"
      >
        <QuickSearch
          :show-input="sumForm.noticeNickname"
          show-key="username"
          :scope-data="sumForm"
          auto-query
          name="scc_rbac_user_display"
          @close-quicksearch="getUserObjnotice"
        />
      </el-form-item>
    </el-form>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="visibleDialog = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="comfirmSum"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>

import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'NoticeUserDialog',
  components: { QuickSearch },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      visibleDialog: false,
      sumForm: {
        summaryUserId: '', // 汇总人id
        summaryEmpNo: '', // 汇总人工号
        summaryNickname: '', // 汇总人昵称
        noticeUserId: '', // 通知人id
        noticeEmpNo: '', // 通知人工号
        noticeNickname: '' // 通知人昵称
      },
      sumRules: {}
    }
  },
  watch: {
    visible (sign) {
      this.visibleDialog = sign
      if (sign) {
        this.sumForm = {}
      }
    }
  },
  methods: {
    getUserObj (val, scope) {
      scope.summaryUserId = val ? val.userId : ''
      scope.summaryEmpNo = val ? val.username : ''
      scope.summaryNickname = val ? val.nickname : ''
      // summaryUserId:'',  summaryEmpNo:'',  summaryNickname:'',
    },
    // 通知
    getUserObjnotice (val, scope) {
      scope.noticeUserId = val ? val.userId : ''
      scope.noticeEmpNo = val ? val.username : ''
      scope.noticeNickname = val ? val.nickname : ''
      // noticeUserId:'',  noticeEmpNo:'',  noticeNickname:'',
    },
    comfirmSum () {
      this.$emit('comfirmSum', this.sumForm)
    }
  }
}
</script>
