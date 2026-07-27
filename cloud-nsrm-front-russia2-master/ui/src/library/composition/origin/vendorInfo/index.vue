<template>
  <div class="vendor-info-wrap">
    <!--供应商基本信息-->
    <h3>{{ $t('bidMod.vendorInfo') }}</h3>
    <srm-row>
      <!--企业名称-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.companyName') }}</span>{{ vendorInfoData.companyName }}
      </srm-col>
      <!--企业性质-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.companyType') }}</span>{{ $getDictLabel('COMPANY_NATURE', vendorInfoData.companyType) }}
      </srm-col>
      <!--成立日期-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.companyCreationDate') }}</span>{{ $parseTime(vendorInfoData.companyCreationDate) }}
      </srm-col>
    </srm-row>

    <srm-row>
      <!--境内外关系-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.overseasRelationName') }}</span>{{ $getDictLabel('RELATION', vendorInfoData.overseasRelation) }}
      </srm-col>
      <!--法定代表人-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.legalPerson') }}</span>{{ vendorInfoData.legalPerson }}
      </srm-col>
      <!--注册资本-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.registeredCapital') }}</span>{{ vendorInfoData.registeredCapital }}
      </srm-col>
    </srm-row>

    <srm-row>
      <!--营业地址-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.address') }}</span>{{ vendorInfoData.companyAddress }}
      </srm-col>
      <!--营业执照生效日期-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.businessStartDate') }}</span>{{ $parseTime(vendorInfoData.businessStartDate) }}
      </srm-col>
      <!--营业执照失效日期-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.businessEndDate') }}</span>{{ $parseTime(vendorInfoData.businessEndDate) }}
      </srm-col>
    </srm-row>

    <srm-row>
      <!--经营范围-->
      <srm-col :init-col="1" class="info-col">
        <span>{{ $t('bidMod.businessScope') }}</span>{{ vendorInfoData.businessScope }}
      </srm-col>
    </srm-row>

    <!--默认插槽，一般放评审意见啥的-->
    <slot />
  </div>
</template>

<script>
/**
 * 供应商基本信息
 */
export default {
  name: 'VendorInfo',

  props: {
    vendorInfoData: {
      type: Object,
      default: () => ({})
    }
  }
}
</script>

<style lang="scss" scoped>
.vendor-info-wrap {
  .el-row {
    margin-bottom: 11px;
    .info-col {
      font-size: 12px;
      padding-left: 110px!important;
      position: relative;
      color: #333;
      line-height: 22px;
      min-height: 22px;
      span {
        position: absolute;
        display: inline-block;
        line-height: 22px;
        left: 0;
        width: 110px;
        text-align: right;
        color: #999;
        padding-right: 11px;
      }
    }
  }
}
</style>
