<template>
  <srm-row>
    <srm-col :init-col="3">
      <!--姓名-->
      <el-form-item
        :label="$t('bidMod.linkman')"
        :prop="keyMap.linkman"
      >
        <el-input
          v-model="contactInfoData[keyMap.linkman]"
          maxlength="80"
          show-word-limit
          :disabled="readOnly"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--手机号码-->
      <el-form-item
        :label="$t('bidMod.tel')"
        :prop="keyMap.tel"
      >
        <el-input
          v-model="contactInfoData[keyMap.tel]"
          maxlength="20"
          show-word-limit
          :disabled="readOnly"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--电子邮箱-->
      <el-form-item
        :label="$t('bidMod.email')"
        :prop="keyMap.email"
      >
        <el-input
          v-model="contactInfoData[keyMap.email]"
          maxlength="80"
          show-word-limit
          :disabled="readOnly"
        />
      </el-form-item>
    </srm-col>
  </srm-row>
</template>

<script>
/**
 * 联系方式表单，暂为采购商
 */
import { mapState } from 'vuex'
import { mappingPropByBusinessTypeAndKey } from './utils'
import { BUSINESS_TYPE } from 'lib@/composition/origin/composition'

export default {
  name: 'ContactInfo',

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => BUSINESS_TYPE.includes(value)
    },
    // 双向绑定对象数据
    infoData: {
      type: Object,
      required: true,
      default: () => {}
    },
    // 只读
    readOnly: {
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
    },

    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        linkman: mappingProp('linkman'),
        tel: mappingProp('tel'),
        email: mappingProp('email')
      }
    }
  },

  methods: {
    contactInfoInit (contactInfoData) {
      // 默认加载采购商联系方式，如果没有才需要填写
      if (this.userInfo && !contactInfoData.linkman) {
        contactInfoData[this.keyMap.linkman] = this.userInfo.nickname
        contactInfoData[this.keyMap.tel] = this.userInfo.phone
        contactInfoData[this.keyMap.email] = this.userInfo.email
      }

      return contactInfoData
    }
  }
}
</script>
