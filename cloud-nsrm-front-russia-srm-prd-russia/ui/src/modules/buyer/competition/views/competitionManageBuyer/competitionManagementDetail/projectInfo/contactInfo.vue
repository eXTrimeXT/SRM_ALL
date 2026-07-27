<template>
  <SrmRow>
    <SrmCol :init-col="3">
      <!--姓名-->
      <el-form-item :label="$t('bidMod.linkman')" prop="linkman">
        <el-input
          v-model="contactInfoData.linkman"
          maxlength="80"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--手机号码-->
      <el-form-item label="手机号码" prop="tel">
        <el-input
          v-model="contactInfoData.tel"
          maxlength="100"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--电子邮件-->
      <el-form-item label="电子邮件" prop="email">
        <el-input
          v-model="contactInfoData.email"
          maxlength="80"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>

<script>
import { mapState } from 'vuex'
/**
 * 联系方式表单，暂为采购商
 */
export default {
  name: 'ContactInfo',

  props: {
    // 双向绑定对象数据
    infoData: {
      type: Object,
      required: true,
      default: () => {}
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },

  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    }),
    contactInfoData: {
      get: function () {
        return this.contactInfoInit(this.infoData)
      },
      set: function (val) {
        this.$emit('update:infoData', val)
      }
    }
  },
  methods: {
    contactInfoInit (contactInfoData) {
      if (this.userInfo && !contactInfoData.projectId) {
        contactInfoData.linkman = this.userInfo.nickname
        contactInfoData.tel = this.userInfo.phone
        contactInfoData.email = this.userInfo.email
      }
      return contactInfoData
    }
  }
}
</script>
