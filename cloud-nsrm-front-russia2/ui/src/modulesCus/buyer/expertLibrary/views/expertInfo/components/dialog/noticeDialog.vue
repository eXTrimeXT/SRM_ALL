<template>

<!-- 注册须知 -->
  <SrmDialog
    :title="$t('vendorMod.registrationPolicy')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :show-close="false"
    :close-on-press-escape="false"
    class="notice-dialog"
  >
    <div class="notice-wrapper">
      <div class="notice-title">
        <!-- 评标专家注册须知 -->
        {{ $t("cusEntry.supplement20250205.evaluationExpertRegistrationNotice") }}
      </div>
      <div class="notice-content">
        <div class="notice-block">
          <div class="title">
            <!-- 一、总则 -->
            {{ $t("cusEntry.supplement20250205.generalText1") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText2") }}</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            {{ $t("cusEntry.supplement20250205.generalText3") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText4") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText5") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText6") }}</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            {{ $t("cusEntry.supplement20250205.generalText7") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText8") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText9") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText10") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText11") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText12") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText13") }}</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            {{ $t("cusEntry.supplement20250205.generalText14") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText15") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText16") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText17") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText18") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText19") }}</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
           {{ $t("cusEntry.supplement20250205.generalText20") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText21") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText22") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText23") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText24") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText25") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText26") }}</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            {{ $t("cusEntry.supplement20250205.generalText27") }}
          </div>
          <div class="info">
            <p>{{ $t("cusEntry.supplement20250205.generalText21") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText29") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText30") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText31") }}</p>
            <p>{{ $t("cusEntry.supplement20250205.generalText32") }}</p>
          </div>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <div class="check">
        <el-checkbox v-model="checked">
          {{ $t("vendorMod.doAgree") }}
        </el-checkbox>
      </div>
      <el-button @click="handleCancel">
        {{ $t("cusEntry.supplement20250205.generalText34") }}
      </el-button>
      <el-button type="primary" :disabled="!checked" @click="handleConfirm">
        {{ $t("cusEntry.supplement20250205.generalText35") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>

export default {
  name: 'NoticeDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      checked: false
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  created () {

  },
  methods: {
    handleConfirm () {
      // if (!this.checked) return this.$message.warning('请勾选确认')
      if (!this.checked) return this.$message.warning(this.$t('cusEntry.supplement20250205.confirmationText'))
      this.$emit('confirm')
    },
    handleCancel () {
      this.dialogVisible = false
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({
        name: 'dashboard'
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.dialog-footer {
  text-align: center;
}
.check {
  font-size: 12px;
  margin-bottom: 10px;
}
.notice-wrapper {
  .notice-title {
    text-align: center;
    font-weight: bold;
    font-size: 16px;
  }
  .notice-block {
    .title {
      font-weight: bold;
    }
  }
}
</style>
<style>
.notice-dialog .el-dialog__footer {
  height: auto;
}
</style>
