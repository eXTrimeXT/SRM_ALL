<template>
  <div class="bond-config">
    <srm-row>
      <srm-col :init-col="3">
        <!--保证金金额（万元）-->
        <el-form-item
          :label="$t('bidMod.bondAmount')"
          prop="auctSouProject.bondAmount"
          :rules="[{required: true, message: $t('bidMod.common.notEmpty')}]"
        >
          <el-input
            v-model="baseDataSync.bondAmount"
            v-input-format="{ type: 'float', negative: false, zero: false, handle: ['blur'] }"
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--保证金提交方式-->
        <el-form-item
          :label="$t('bidMod.bondMethod')"
          prop="auctSouProject.bondMethod"
          :rules="[{required: formItemRequired, message: $t('bidMod.common.notEmpty')}]"
        >
          <DictSelect v-model="baseDataSync.bondMethod" code="BID_BOND_SUBMISSION" />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--保证金提交截止时间-->
        <el-form-item
          :label="$t('bidMod.bondEndDatetime')"
          prop="auctSouProject.bondEndTime"
          :rules="[{required: formItemRequired, message: $t('bidMod.common.notEmpty')}]"
        >
          <el-date-picker
            v-model="baseDataSync.bondEndTime"
            type="datetime"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('bidMod.datePicker')"
            :picker-options="cannotLessCurrentTimeOptions"
          />
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <srm-col :init-col="3">
        <!--保证金缴纳账号-->
        <el-form-item
          :label="$t('bidMod.bankAccountNum')"
          prop="auctSouProject.bankAccountNum"
         :rules="[{required: formItemRequired, message: $t('bidMod.common.notEmpty')}]"
        >
          <el-input v-model="baseDataSync.bankAccountNum" />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--账户名称-->
        <el-form-item
          :label="$t('bidMod.bankAccountName')"
          prop="auctSouProject.bankAccountName"
          :rules="[{required: formItemRequired, message: $t('bidMod.common.notEmpty')}]"
        >
          <el-input v-model="baseDataSync.bankAccountName" />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--开户支行-->
        <el-form-item
          :label="$t('bidMod.bankBranchName')"
          prop="auctSouProject.bankBranchName"
          :rules="[{required: formItemRequired, message: $t('bidMod.common.notEmpty')}]"
        >
          <el-input v-model="baseDataSync.bankBranchName" />
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <srm-col :init-col="1">
        <!--其他说明-->
        <el-form-item :label="$t('bidMod.bondDesc')">
          <el-input
            v-model="baseDataSync.bondDesc"
            type="textarea"
            :rows="2"
          />
        </el-form-item>
      </srm-col>
    </srm-row>
  </div>
</template>

<script>
/**
 * 商务要求
 */
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { mappingPropByBusinessTypeAndKey } from './utils'

export default {
  name: 'BondConfig',

  mixins: [cannotLessCurrentTime],

  props: {
    // 业务类型
    businessType: {
      type: String,
      // 允许为空。
      validator: value => !value || (value && validatorBusinessType(value))
    },
    baseData: {
      type: Object,
      required: true
    },
    formItemRequired: {
      type: Boolean,
      default: true
    },
    bondAmountProp: {
      type: String,
      default: 'bondAmount'
    }
  },

  computed: {
    baseDataSync: {
      get: function () {
        return this.baseData
      },
      set: function (val) {
        this.$emit('update:baseData', val)
      }
    },

    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        bondEndTime: mappingProp('bondEndTime')
      }
    }
  }
}
</script>
