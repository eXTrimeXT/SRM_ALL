<template>
  <el-form
    ref="form"
    :model="baseInfo"
    disabled
    label-width="120px"
    label-position="top"
    class="form-incontainer"
  >
    <el-collapse v-model="activeDims">
      <!--项目信息-->
      <el-collapse-item :title="$t('bidMod.projectInformation')" name="1">
        <SrmRow>
          <!--竞价单号-->
          <SrmCol :init-col="3">
            <el-form-item label="竞价单号">
              <el-input v-model="baseInfoData.souNo" disabled />
            </el-form-item>
          </SrmCol>

          <!--竞价标题-->
          <SrmCol :init-col="3">
            <el-form-item label="竞价标题">
              <el-input v-model="baseInfoData.souName" />
            </el-form-item>
          </SrmCol>

          <!--竞价状态-->
          <SrmCol :init-col="3">
            <el-form-item label="竞价状态">
              <el-input :value="$getDictLabel('SOU_AUCT_PROJECT_STATUS', baseInfoData.projectStatus)" />
            </el-form-item>
          </SrmCol>

          <!--报名截止时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.registrationDeadline')">
              <el-date-picker
                v-model="baseInfoData.signUpEndTime"
                type="datetime"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>

          <!--竞价开始时间-->
          <SrmCol :init-col="3">
            <el-form-item label="竞价开始时间">
              <el-date-picker
                v-model="baseInfoData.orderStartTime"
                type="datetime"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>

          <!--竞价截止时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bidClosingTime')">
              <el-date-picker
                v-model="baseInfoData.orderEndTime"
                type="datetime"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>

          <!--评分规则-->
          <SrmCol :init-col="3">
            <el-form-item label="评分规则">
              <el-input :value="$getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', baseInfoData.scoreRuleType)" />
            </el-form-item>
          </SrmCol>

          <!--报价币种-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.currencyType')">
              <el-input :value="$getDictLabel('currency', baseInfoData.standardCurrency)" />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--竞价规则-->
      <el-collapse-item title="竞价规则" name="2">
        <SrmRow>
          <!--竞价规则-->
          <SrmCol :init-col="3">
            <el-form-item label="竞价规则" prop="auctRule">
              <el-input :value="$getDictLabel('SOU_AUCT_RULE', baseInfoData.auctRule)" />
            </el-form-item>
          </SrmCol>

          <!---当竞价规则为"不允许报相同价格,前几名不允许相同价格"时-->
          <SrmCol v-if="baseInfoData.auctRule === 'NO_ALLOW_SAME_PRICE'" :init-col="3">
            <el-form-item label="前几名不允许相同价格" prop="noAllowSamePriceCount">
              <el-input v-model="baseInfoData.noAllowSamePriceCount" disabled />
            </el-form-item>
          </SrmCol>

          <!--涨降幅百分比(%)-->
          <SrmCol :init-col="3">
            <el-form-item label="涨降幅百分比(%)">
              <el-input v-model="baseInfoData.minPercent" disabled />
            </el-form-item>
          </SrmCol>

          <!--涨降金额-->
          <SrmCol :init-col="3">
            <el-form-item label="涨降金额">
              <el-input v-model="baseInfoData.minAmount" disabled />
            </el-form-item>
          </SrmCol>

          <!--公开规则-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.competitionLts.openRules')" prop="scopeRule">
              <el-input :value="$getDictLabel('SOU_AUCT_SCOPE_RULE', baseInfoData.scopeRule)" />
            </el-form-item>
          </SrmCol>

          <template v-if="baseInfoData.allowExtendTime === 'Y'">
            <!--延时竞价触发点(分钟)-->
            <SrmCol :init-col="3">
              <el-form-item label="延时竞价触发点(分钟)" prop="extendTrigger">
                <el-input v-model="baseInfoData.extendTrigger" />
              </el-form-item>
            </SrmCol>

            <!--延时分钟数-->
            <SrmCol :init-col="3">
              <el-form-item label="延时分钟数" prop="extendMinute">
                <el-input v-model="baseInfoData.extendMinute" />
              </el-form-item>
            </SrmCol>

            <!--延时竞价最多可报次数-->
            <SrmCol :init-col="3">
              <el-form-item label="延时竞价最多可报次数" prop="extendMaxOrderCount">
                <el-input v-model="baseInfoData.extendMaxOrderCount" />
              </el-form-item>
            </SrmCol>
            <!--延时触发的最大次数-->
            <SrmCol :init-col="3">
              <el-form-item label="延时触发的最大次数" prop="extendTriggerCount">
                <el-input v-model="baseInfoData.extendTriggerCount" />
              </el-form-item>
            </SrmCol>
            <!--首次延时触发后最长延时时间限制 -->
            <SrmCol :init-col="3">
              <el-form-item label="首次延时触发后最长延时时间限制" prop="extendMaxMinute">
                <el-input v-model="baseInfoData.extendMaxMinute" />
              </el-form-item>
            </SrmCol>
          </template>
        </SrmRow>
      </el-collapse-item>

      <!--采购商的联系方式-->
      <el-collapse-item :title="$t('vendorMod.vendorContactInfor')" name="3">
        <SrmRow>
          <SrmCol :init-col="3">
            <!--姓名-->
            <el-form-item :label="$t('bidMod.linkman')" prop="linkman">
              <el-input
                v-model="baseInfoData.linkman"
                maxlength="80"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--手机号码-->
            <el-form-item label="手机号码" prop="tel">
              <el-input
                v-model="baseInfoData.tel"
                maxlength="100"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--电子邮箱-->
            <el-form-item :label="$t('common.email')" prop="email">
              <el-input
                v-model="baseInfoData.email"
                maxlength="80"
                disabled
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--附件-->
      <el-collapse-item :title="$t('bidMod.accessory')" name="4">
        <el-table
          :data="fileConfigList"
          style="width: 100%"
          border
          height="133px"
        >
          <el-table-column
            type="index"
            :label="$t('common.sort')"
            width="50"
          />

          <!--附件名称-->
          <SrmCommonFile
            type="table-column"
            :table-column-options="{
              label: $t('bidMod.fileName'),
              prop: 'requireDocId',
              nameProp: 'requireFileName'
            }"
            readonly
          />

          <!--备注-->
          <el-table-column
            prop="requireRemark"
            :label="$t('common.remark')"
          />
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
/**
 * 基础信息
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'

export default {
  name: 'ProjectInformation',

  components: { OriginContactInfo },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    currencyList: {
      type: Array,
      required: true
    },
    fileConfigList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    baseInfoData: {
      get: function () {
        return this.baseInfo
      },
      set: function (val) {
        this.$emit('update:baseInfo', val)
      }
    }
  }
}
</script>
