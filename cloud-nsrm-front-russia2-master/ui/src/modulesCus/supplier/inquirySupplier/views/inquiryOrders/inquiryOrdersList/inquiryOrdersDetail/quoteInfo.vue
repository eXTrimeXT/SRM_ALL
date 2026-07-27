<template>
  <div class="quote-info">
    <el-form :model="header" :rules="rules" ref="quoteForm" :disabled="readonly">
      <SrmRow>
        <SrmCol :init-col="4">
          <!--f 报价有效期 -->
          <el-form-item :label="$t('cusEntry.bidMod.quoteEffictDate')" prop="priceActiveDay">
            <el-input
              v-model="header.priceActiveDay"
              v-input-format="{
                type: 'integer',
                negative: false
              }" 
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!--f 报价人-->
          <el-form-item :label="$t('cusEntry.bidMod.quoter')" prop="extOrderByNickname">
            <el-input v-model="header.extOrderByNickname" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!--f 联系人方式-->
          <el-form-item :label="$t('cusEntry.bidMod.quotePhone')" prop="extOrderPhone">
            <el-input v-model="header.extOrderPhone" />
          </el-form-item>
        </SrmCol>
        <!--上传附件-->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('cusEntry.bidMod.quoteFile')">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: header.orderDocId,
                fileName: header.orderFileName
              }"
              @on-change="quoteFileChange"
              :readonly="readonly"
            />
          </el-form-item>
        </SrmCol>
        <!-- 是否预付 -->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('cusEntry.bidMod.extIsPrepaid')" prop="extIsPrepaid">
            <DictSelect
              v-model="header.extIsPrepaid"
              code="YES_OR_NO"
              :disabled="readonly"
            />
          </el-form-item>
        </SrmCol>
        <!-- 预付比例% -->
        <SrmCol :init-col="4">
          <el-form-item
            prop="extPrepaidRatio"
            :label="$t('cusEntry.bidMod.extPrepaidRatio')"
            :rules="[{ required: header.extIsPrepaid === 'Y', message: this.$t('cusEntry.tipMessage.extPrepaidRatioMsg') }]"
          >
            <el-input
              v-model="header.extPrepaidRatio"
              v-input-format="{type: 'float', digits: 2, negative: false}"
              :disabled="readonly"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
  </div>
</template>

<script>
/**
 * 报价信息
 */
import { validatePhone } from '@/utils/validate'
export default {
  name: 'QuoteInfo',
  props: {
    header: {
      type: Object,
      required: true
    },
    orderNo: {
      type: String,
      required: true
    },
    /* 是否只读 */
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    // 验证手机
    const validatePhoneFn = (rule, value, callback) => {
      if (!validatePhone(value)) {
        this.$message.warning(this.$t('cusEntry.tipMessage.correctPhoneNumber'))
        return callback()
      } else {
        return callback()
      }
    }
    return {
      fileInfo: {
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'SceneFileManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      rules: {
        priceActiveDay: [{ required: true, message: this.$t('cusEntry.tipMessage.priceActiveDayMsg') }],
        extOrderByNickname: [{ required: true, message: this.$t('cusEntry.tipMessage.extOrderByNicknameMsg') }],
        extIsPrepaid: [{ required: true, message: this.$t('cusEntry.tipMessage.extIsPrepaidMsg') }],
        extOrderPhone: [
          { required: true, message: this.$t('cusEntry.tipMessage.extOrderPhoneMsg') }
          // { validator: validatePhoneFn, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    /* 报价附件上传 */
    quoteFileChange ({ file }) {
      const { fileId, fileName } = file || {}
      this.header.orderDocId = fileId || null
      this.header.orderFileName = fileName || ''
    },
    /* 表单校验 */
    validateForm () {
      return new Promise((resolve, reject) => {
        this.$refs.quoteForm.validate((valid) => {
          if (valid) {
            // /* 校验手机格式 */
            // if (this.header.extOrderPhone && !validatePhone(this.header.extOrderPhone)) {
            //   this.$message.warning(this.$t('cusEntry.tipMessage.correctPhoneNumber'))
            //   return false
            // }
            resolve(true)
          } else {
            resolve(false)
          }
        })
      })
    },
    
  }
}
</script>
