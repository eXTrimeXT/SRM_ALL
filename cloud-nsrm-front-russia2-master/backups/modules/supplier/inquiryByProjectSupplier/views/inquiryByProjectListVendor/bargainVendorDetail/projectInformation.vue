<template>
  <el-form
    ref="form"
    :model="bargainBase"
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
      <el-collapse-item
        :title="$t('bidMod.projectInformation')"
        name="1"
      >
        <srm-row>
          <srm-col :init-col="3">
            <!--项目编号-->
            <el-form-item :label="$t('bidMod.bidingNum')">
              <el-input
                v-model="bargainBase.bargainNum"
                disabled
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--项目名称-->
            <el-form-item
              :label="$t('bidMod.bidingName')"
              prop="bargainName"
            >
              <el-input v-model="bargainBase.bargainName" />
            </el-form-item>
          </srm-col>

          <srm-col
            v-if="hasSignUpNode"
            :init-col="3"
          >
            <!--报名截止时间-->
            <el-form-item
              :label="$t('bidMod.registrationDeadline')"
              prop="enrollEndDatetime"
            >
              <el-date-picker
                v-model="bargainBase.enrollEndDatetime"
                type="datetime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--报价开始时间-->
            <el-form-item :label="$t('bidMod.bidingStartDatetime1')">
              <el-date-picker
                v-model="bargainBase.bargainStartDatetime"
                type="datetime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--报价截止时间-->
            <el-form-item :label="$t('bidMod.quotedeadline')">
              <el-date-picker
                v-model="bargainBase.bargainEndDatetime"
                type="datetime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--预计询价地点-->
            <el-form-item
              :label="$t('bidMod.bidingSite1')"
              prop="bargainSite"
            >
              <el-input v-model="bargainBase.bargainSite" />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--询价范围-->
            <el-form-item
              :label="$t('bidMod.bidingScope1')"
              prop="bargainScope"
            >
              <dict-select
                v-model="bargainBase.bargainScope"
                code="BARGAIN_SCOPE"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--询价类型-->
            <el-form-item
              :label="$t('bidMod.inquiryType')"
              prop="bargainType"
            >
              <dict-select
                v-model="bargainBase.bargainType"
                code="BARGAIN_TYPE"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--价格有效期自-->
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidFrom')"
              prop="priceStartTime"
            >
              <el-date-picker
                v-model="bargainBase.priceStartTime"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--价格有效期至-->
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidTo')"
              prop="priceEndTime"
            >
              <el-date-picker
                v-model="bargainBase.priceEndTime"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-collapse-item>

      <!--报价币种-->
      <el-collapse-item
        :title="$t('bidMod.bidingCurrency2')"
        name="2"
      >
        <srm-row>
          <srm-col :init-col="3">
            <!--本位币-->
            <el-form-item
              :label="$t('bid_mod.standardCurrency')"
              prop="standardCurrency"
            >
              <dict-select
                v-model="bargainBase.standardCurrency"
                code="currency"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--价格精度-->
            <el-form-item
              :label="$t('bid_mod.pricePrecision')"
              prop="pricePrecision"
            >
              <el-input
                v-model="bargainBase.pricePrecision"
                type="number"
                max-length="1"
              />
            </el-form-item>
          </srm-col>
        </srm-row>

        <srm-row>
          <srm-col :init-col="1">
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
                :formatter="(row, column, value) => $getDictLabel('currency', value)"
              />

              <!--描述-->
              <el-table-column
                align="center"
                prop="currencyDesc"
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
          </srm-col>
        </srm-row>
      </el-collapse-item>

      <!--采购商的联系方式-->
      <el-collapse-item
        :title="$t('bidMod.buyerContact')"
        name="3"
      >
        <srm-row>
          <srm-col :init-col="3">
            <!--联系人姓名-->
            <el-form-item
              :label="$t('bidMod.bidContactName')"
              prop="brgContactName"
            >
              <el-input v-model="bargainBase.brgContactName" />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--手机-->
            <el-form-item
              :label="$t('bidMod.bidMobilePhone')"
              prop="brgMobilePhone"
            >
              <el-input
                v-model="bargainBase.brgMobilePhone"
                type="number"
              />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--邮箱-->
            <el-form-item
              :label="$t('bid_mod.email')"
              prop="brgEmail"
            >
              <el-input v-model="bargainBase.brgEmail" />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-collapse-item>

      <!--附件-->
      <el-collapse-item
        :title="$t('bidMod.attachment')"
        name="4"
      >
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
              prop: 'docId',
              nameProp: 'fileName',
              minHeight: '200'
            }"
            readonly
          />

          <el-table-column
            align="center"
            prop="comments"
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

export default {
  name: 'ProjectInformation',
  props: {
    bargainBase: {
      type: Object
    },
    outerFiles: {
      type: Array
    },
    hasSignUpNode: {
      type: Boolean
    }
  },
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6']
    }
  }
}
</script>
