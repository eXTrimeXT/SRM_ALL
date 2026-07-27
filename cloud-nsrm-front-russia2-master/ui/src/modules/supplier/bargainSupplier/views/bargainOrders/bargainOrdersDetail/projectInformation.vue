<template>
  <el-form
    ref="form"
    :model="bargainBase"
    disabled
    label-width="120px"
    label-position="top"
    class="form-incontainer"
  >
    <el-collapse v-model="activeDims" class="tab-form-style">
      <!--项目信息-->
      <el-collapse-item :title="$t('bidMod.projectInformation')" name="1">
        <SrmRow>
          <SrmCol :init-col="3">
            <!--项目编号-->
            <el-form-item :label="$t('bidMod.bidingNum')">
              <el-input v-model="bargainBase.souNo" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--项目名称-->
            <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
              <el-input v-model="bargainBase.souName" />
            </el-form-item>
          </SrmCol>

          <SrmCol v-if="hasSignUpNode" :init-col="3">
            <!--报名截止时间-->
            <el-form-item :label="$t('bidMod.registrationDeadline')" prop="signUpEndTime">
              <el-date-picker
                v-model="bargainBase.signUpEndTime"
                type="datetime"
                :format="$formatDatePickerTime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--报价开始时间-->
            <el-form-item :label="$t('bidMod.bidingStartDatetime1')">
              <el-date-picker
                v-model="bargainBase.orderStartTime"
                type="datetime"
                :format="$formatDatePickerTime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--报价截止时间-->
            <el-form-item :label="$t('bidMod.quotedeadline')">
              <el-date-picker
                v-model="bargainBase.orderEndTime"
                type="datetime"
                :format="$formatDatePickerTime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--预计询价地点-->
            <el-form-item :label="$t('bidMod.bidingSite1')" prop="orderSite">
              <el-input v-model="bargainBase.orderSite" />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--询价范围-->
            <el-form-item :label="$t('bidMod.bidingScope1')" prop="publishScope">
              <DictSelect v-model="bargainBase.publishScope" code="SOU_PUBLISH_SCOPE" />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--询价类型-->
            <el-form-item :label="$t('bidMod.inquiryType')" prop="bargainType">
              <DictSelect v-model="bargainBase.bargainType" code="SOU_BRG_TYPE" />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--价格有效期自-->
            <el-form-item :label="$t('bid_mod.defaultPriceValidFrom')" prop="priceStartTime">
              <el-date-picker
                v-model="bargainBase.priceStartTime"
                type="date"
                value-format="yyyy-MM-dd"
                :format="$formatDatePicker"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--价格有效期至-->
            <el-form-item :label="$t('bid_mod.defaultPriceValidTo')" prop="priceEndTime">
              <el-date-picker
                v-model="bargainBase.priceEndTime"
                type="date"
                value-format="yyyy-MM-dd"
                :format="$formatDatePicker"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--报价币种-->
      <el-collapse-item :title="$t('bidMod.bidingCurrency2')" name="2">
        <SrmRow>
          <SrmCol :init-col="3">
            <!--本位币-->
            <el-form-item :label="$t('bid_mod.standardCurrency')" prop="standardCurrency">
              <DictSelect v-model="bargainBase.standardCurrency" code="currency" />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--价格精度-->
            <el-form-item :label="$t('bid_mod.pricePrecision')" prop="pricePrecision">
              <el-input
                v-model="bargainBase.pricePrecision"
                type="number"
                max-length="1"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <SrmCol :init-col="1">
            <!--以下是投标人可以使用的外币清单-->
            <p>{{ $t("bid_mod.quoteCurrencyExplain1") }}</p>
            <el-table
              :data="bargainBase.currencyList || []"
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
                :label="$t('bid_mod.currencyName')"
                width="200"
                :formatter="(row, column, cellValue) => $getDictLabel('currency', cellValue)"
              />

              <!--描述-->
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('bidMod.quickSearchDescription')"
              />

              <!--汇率-->
              <el-table-column
                align="center"
                prop="priceTax"
                :label="$t('bid_mod.priceTax')"
              />

              <!--价格精度-->
              <el-table-column
                align="center"
                prop="pricePrecision"
                :label="$t('bid_mod.pricePrecision')"
              />
            </el-table>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--采购商的联系方式-->
      <el-collapse-item :title="$t('bidMod.buyerContact')" name="3">
        <OriginContactInfo
          :business-type="BUSINESS_TYPE_ENUM.BARGAIN_LTS"
          :info-data.sync="bargainBase"
          :set-default="false"
        />
      </el-collapse-item>

      <!--附件-->
      <el-collapse-item :title="$t('bidMod.attachment')" name="4">
        <el-table
          :data="outerFiles"
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
              label: $t('bidMod.attachmentName'),
              prop: 'souDocId',
              nameProp: 'souFileName',
              minHeight: '200'
            }"
            readonly
          />

          <el-table-column
            align="center"
            prop="souRemark"
            :label="$t('bid_mod.remark')"
            show-overflow-tooltip
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

  components: {
    OriginContactInfo
  },

  props: {
    bargainBase: {
      type: Object,
      required: true
    },
    outerFiles: {
      type: Array,
      required: true
    },
    hasSignUpNode: {
      type: Boolean,
      required: true
    }
  },
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6'],
      BUSINESS_TYPE_ENUM
    }
  }
}
</script>
