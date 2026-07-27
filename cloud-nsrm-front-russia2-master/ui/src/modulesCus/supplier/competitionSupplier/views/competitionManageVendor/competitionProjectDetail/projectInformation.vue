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
            <el-form-item :label="$t('bidMod.competitionLts.souNo')">
              <el-input v-model="baseInfoData.souNo" disabled />
            </el-form-item>
          </SrmCol>

          <!--报名开始时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('cusEntry.competition.signUpStartTime')" prop="signUpStartTime">
              <el-date-picker
                v-model="baseInfoData.signUpStartTime"
                type="datetime"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <!--报名截止时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.registrationDeadline')">
              <el-date-picker
                v-model="baseInfoData.signUpEndTime"
                type="datetime"
                :format="$formatDatePickerTime"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>

          <!--竞价开始时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bidStartTime')">
              <el-date-picker
                v-model="baseInfoData.orderStartTime"
                type="datetime"
                :format="$formatDatePickerTime"
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
                :format="$formatDatePickerTime"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>
      <!--附件-->
      <el-collapse-item :title="$t('cusEntry.competition.applyDocumentsFile')" name="2">
        <el-table
          :data="fileConfigList"
          style="width: 100%"
          border
          height="133px"
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
              label: $t('cusEntry.competition.fileName'),
              prop: 'souFileId',
              nameProp: 'souFileName'
            }"
            readonly
          />

          <!--备注-->
          <el-table-column
            align="center"
            prop="souRemark"
            :label="$t('common.remark')"
          />
        </el-table>
      </el-collapse-item>
      <!--保证金信息-->
      <el-collapse-item
        :title="$t('bidMod.competitionLts.bondConfig')"
        name="3"
      >
        <OriginBondConfig
          :base-data.sync="baseInfoData.auctSouProject"
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :form-item-required="true"
        />
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
import OriginBondConfig from 'modcb@/competition/views/competitionManageBuyer/competitionManagementDetail/projectInfo/bondConfig'
export default {
  name: 'ProjectInformation',

  components: {
    OriginContactInfo,
    OriginBondConfig
  },

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
