<template>
  <el-form
    ref="form"
    :model="baseInfo"
    disabled
    label-width="120px"
    label-position="top"
    class="form-incontainer"
  >
    <el-collapse
      v-model="activeDims"
      class="tab-form-style"
    >
      <!--项目信息-->
      <el-collapse-item :title="$t('bidMod.projectInformation')" name="1">
        <SrmRow>
          <!--项目编号-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bidingNum')">
              <el-input v-model="baseInfoData.souNo" disabled />
            </el-form-item>
          </SrmCol>

          <!--项目名称-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
              <el-input v-model="baseInfoData.souName" />
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
        </SrmRow>

        <SrmRow>
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

          <!--预计竞价地点-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.expectedBiddingPlace')" prop="orderSite">
              <el-input v-model="baseInfoData.orderSite" />
            </el-form-item>
          </SrmCol>

          <!--价格有效开始时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('vendorMod.priceEffectiveStartTime')" prop="priceStartTime">
              <el-date-picker
                v-model="baseInfoData.priceStartTime"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <!--价格有效结束时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('vendorMod.validEndTime')" prop="priceEndTime">
              <el-date-picker
                v-model="baseInfoData.priceEndTime"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--商务要求-->
      <el-collapse-item :title="$t('bidMod.businessDemand')" name="2">
        <SrmRow>
          <!--保证金金额-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.earnestMoney')" prop="bondAmount">
              <el-input v-model="baseInfoData.bondAmount" />
            </el-form-item>
          </SrmCol>

          <!--保证金提交方式-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bondMethod')" prop="bondMethod">
              <DictSelect v-model="baseInfoData.bondMethod" code="BID_BOND_SUBMISSION" />
            </el-form-item>
          </SrmCol>

          <!--保证金提交截止时间-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bondEndDatetime')">
              <el-date-picker
                v-model="baseInfoData.bondEndTime"
                type="date"
                :format="$formatDatePicker"
                :placeholder="$t('bidMod.optionDate')"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <!--保证金缴纳账号-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bankAccountNum')" prop="bankAccountNum">
              <el-input v-model="baseInfoData.bankAccountNum" />
            </el-form-item>
          </SrmCol>

          <!--账号名称-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bankAccountName')" prop="bankAccountName">
              <el-input v-model="baseInfoData.bankAccountName" />
            </el-form-item>
          </SrmCol>

          <!--开户支行-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bidMod.bankBranchName')" prop="bankBranchName">
              <el-input v-model="baseInfoData.bankBranchName" />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <p>{{ $t('bidMod.tech_quoteCurrency') }}</p>
        <SrmRow>
          <!--本位币-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('bid_mod.standardCurrency')" prop="standardCurrency">
              <DictSelect v-model="baseInfoData.standardCurrency" code="currency" />
            </el-form-item>
          </SrmCol>

          <!--价格精度-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('vendorMod.priceAccuracy')" prop="pricePrecision">
              <el-input
                v-model="baseInfoData.pricePrecision"
                type="number"
                max-length="1"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow :gutter="24">
          <SrmCol :init-col="1">
            <!--以下是报价人可以使用的外币清单-->
            <p>{{ $t('bid_mod.quoteCurrencyExplain1') }}</p>
            <el-table
              :data="currencyList"
              style="width: 100%"
              border
              max-height="140px"
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />

              <!--币种-->
              <el-table-column
                align="center"
                prop="currencyCode"
                :label="$t('bidMod.allAurrency')"
                width="200"
                :formatter="(row, column, value) => $getDictLabel('currency', value)"
              />

              <!--描述-->
              <el-table-column
                align="center"
                prop="currencyDesc"
                :label="$t('bid_mod.costDescription')"
              />

              <!--汇率-->
              <el-table-column
                align="center"
                prop="priceTax"
                :label="$t('bidMod.appraisRate')"
              />

              <!--价格精度-->
              <el-table-column
                align="center"
                prop="pricePrecision"
                :label="$t('vendorMod.priceAccuracy')"
              />
            </el-table>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <!--外部可见说明-->
          <SrmCol :init-col="1">
            <el-form-item :label="$t('vendorMod.externalVisible')">
              <el-input
                v-model="baseInfoData.bondDesc"
                type="textarea"
                :rows="2"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--采购商的联系方式-->
      <el-collapse-item :title="$t('vendorMod.vendorContactInfor')" name="3">
        <OriginContactInfo
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :info-data="baseInfoData"
          read-only
        />
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
            align="center"
            type="index"
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

          <!--资料要求-->
          <el-table-column
            align="center"
            prop="fileRequire"
            :label="$t('bidMod.fileQualify')"
            min-width="150"
          />

          <!--文件类型-->
          <el-table-column
            align="center"
            prop="fileType"
            :label="$t('bid_mod.referenceFileType')"
            min-width="150"
            :formatter="(row, colunm, value) => $getDictLabel('SOU_FILE_CONFIG_TYPE', value)"
          />

          <!--备注-->
          <el-table-column
            align="center"
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
 * 项目信息
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
