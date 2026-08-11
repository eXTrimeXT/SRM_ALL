<template>
  <el-form
    ref="form"
    :model="bidingBase"
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
              <el-input v-model="bidingBase.souNo" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--项目名称-->
            <el-form-item :label="$t('bidMod.bidingName')">
              <el-input v-model="bidingBase.souName" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol v-if="bidingBase.signUpEndTime" :init-col="3">
            <!--报名截止时间-->
            <el-form-item :label="$t('bidMod.registrationDeadline')">
              <el-date-picker
                v-model="bidingBase.signUpEndTime"
                type="datetime"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :placeholder="$t('bidMod.datePicker')"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--预计报价开始时间-->
            <el-form-item :label="$t('bidMod.beginQuote')">
              <el-date-picker
                v-model="bidingBase.orderStartTime"
                type="datetime"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :placeholder="$t('bidMod.datePicker')"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--报价截止时间-->
            <el-form-item :label="$t('bidMod.quotedeadline')">
              <el-date-picker
                v-model="bidingBase.orderEndTime"
                type="datetime"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                disabled
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--预计询价地点-->
            <el-form-item :label="$t('bidMod.bidingSite1')">
              <el-input v-model="bidingBase.orderSite" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--询价范围-->
            <el-form-item :label="$t('bidMod.bidingScope')">
              <!-- <DictSelect v-model="bidingBase.publishScope" code="SOU_PUBLISH_SCOPE" disabled /> -->
              <el-input v-model="bidingBase.publishScopeName" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--询价类型-->
            <el-form-item :label="$t('bidMod.bidingType1')">
              <!-- <DictSelect v-model="bidingBase.bargainType" code="SOU_BRG_TYPE" disabled /> -->
              <el-input v-model="bidingBase.bargainTypeName" disabled />
            </el-form-item>
          </SrmCol>
          <SrmCol :init-col="3">
            <!--价格有效期自-->
            <el-form-item :label="$t('bid_mod.defaultPriceValidFrom')">
              <el-date-picker
                v-model="bidingBase.priceStartTime"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
                disabled
              />
            </el-form-item>
          </SrmCol>

          <SrmCol :init-col="3">
            <!--价格有效期至-->
            <el-form-item :label="$t('bid_mod.defaultPriceValidTo')">
              <el-date-picker
                v-model="bidingBase.priceEndTime"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
                disabled
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>
      </el-collapse-item>

      <!--报价币种-->
      <el-collapse-item :title="$t('bidMod.bidingCurrency2')" name="2">
        <srm-row :gutter="16">
          <srm-col :init-col="3">
            <!--本位币-->
            <el-form-item :label="$t('bid_mod.standardCurrency')">
              <!-- <dict-select v-model="bidingBase.standardCurrency" code="currency" /> -->
              <el-input v-model="bidingBase.standardCurrencyName" disabled />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--价格精度-->
            <el-form-item :label="$t('bid_mod.pricePrecision')">
              <el-input v-model="bidingBase.pricePrecision" type="number" max-length="1" />
            </el-form-item>
          </srm-col>
        </srm-row>

        <srm-row :gutter="16">
          <srm-col :init-col="1">
            <!--以下是投标人可以使用的外币清单-->
            <p>{{ $t("bid_mod.quoteCurrencyExplain") }}</p>
            <el-table :data="bidingBase.currencyList || []" style="width: 100%" border max-height="140px">
              <el-table-column align="center" type="index" width="50" />

              <!--币种-->
              <!-- <el-table-column
                align="center"
                prop="currencyCode"
                :label="$t('bid_mod.currencyName')"
                width="200"
                :formatter="(row, column, value) => $getDictLabel('currency', value)"
              /> -->
              <el-table-column
                align="center"
                prop="currencyName"
                :label="$t('bid_mod.currencyName')"
                width="200"
              />

              <!--描述-->
              <el-table-column align="center" prop="currencyDesc" :label="$t('bidMod.quickSearchDescription')" />

              <!--汇率-->
              <el-table-column align="center" prop="priceTax" :label="$t('bid_mod.priceTax')" />

              <!--价格精度-->
              <el-table-column align="center" prop="pricePrecision" :label="$t('bid_mod.pricePrecision')" />
            </el-table>
          </srm-col>
        </srm-row>
      </el-collapse-item>

      <!--采购商的联系方式-->
      <el-collapse-item :title="$t('bidMod.buyerContact')" name="3">
        <srm-row :gutter="16">
          <srm-col :init-col="3">
            <!--联系人姓名-->
            <el-form-item :label="$t('bidMod.bidContactName')">
              <el-input v-model="bidingBase.linkman" />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--手机-->
            <el-form-item :label="$t('bidMod.bidMobilePhone')">
              <el-input v-model="bidingBase.tel" type="number" />
            </el-form-item>
          </srm-col>

          <srm-col :init-col="3">
            <!--邮箱-->
            <el-form-item :label="$t('bid_mod.email')">
              <el-input v-model="bidingBase.email" />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-collapse-item>

      <!--附件-->
      <el-collapse-item :title="$t('bidMod.attachment')" name="4">
        <el-table :data="outerFiles" style="width: 100%" border height="133px">
          <el-table-column align="center" type="index" width="50" />
          <el-table-column align="center" prop="fileName" :label="$t('sourcingBuyer.attach')">
            <template slot-scope="scope">
              <span>{{scope.row.souFileName}}</span>
              <em class="el-icon-download operation-icon" style="margin-left: 10px;cursor: pointer" @click="downloadF(scope.row.souDocId, scope.row.projectId, scope.row.souFileName)"></em>
            </template>
          </el-table-column>
          <!--附件名称-->
<!--          <SrmCommonFile-->
<!--            type="table-column"-->
<!--            :table-column-options="{-->
<!--              label: $t('bidMod.attachmentName'),-->
<!--              prop: 'souDocId',-->
<!--              nameProp: 'souFileName',-->
<!--              minWidth: '200'-->
<!--            }"-->
<!--            readonly-->
<!--          />-->

          <el-table-column align="center" prop="souRemark" :label="$t('bid_mod.remark')" show-overflow-tooltip />
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
import {downloadFileLink} from "@/library/utils/file";

/**
 * 项目信息
 */
export default {
  name: 'ProjectInformation',
  props: {
    bidingBase: {
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
  },
  methods: {
    downloadF (souDocId, projectId, souFileName) {
      downloadFileLink(
        `/api-sou/sou-firstPage/getSouInfo/souFile?projectId=${projectId}&docId=${souDocId}`,
        souFileName
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>
