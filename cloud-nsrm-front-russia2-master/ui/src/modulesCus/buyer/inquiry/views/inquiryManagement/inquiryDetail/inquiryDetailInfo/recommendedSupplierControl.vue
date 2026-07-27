<template>
  <div class="recommended-supplier-control">
    <SrmRow>
      <!--排除黑名单供应商-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.excludeBlackVendors"
          true-label="Y"
          false-label="N"
          disabled
        >
          {{ $t('bidMod.excludeBlackVendors') }}
        </el-checkbox>
      </SrmCol>

      <!--排除非本业务实体供应商-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.excludeNoCurrentOrgVendors"
          true-label="Y"
          false-label="N"
          disabled
        >
          {{ $t('bidMod.excludeNoCurrentOrgVendors') }}
        </el-checkbox>
      </SrmCol>

      <!--排除业务实体退出/冻结供应商-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.excludeOrgQuitVendors"
          true-label="Y"
          false-label="N"
          disabled
        >
          {{ $t('bidMod.excludeOrgQuitVendors') }}
        </el-checkbox>
      </SrmCol>
    </SrmRow>

    <SrmRow style="margin-top: 15px">
      <SrmCol :init-col="2">
        <el-form-item prop="extVendorPerformanceRank">
          <span
            style="font-size: 12px; margin-right: 5px;"
          >
            <i class="requiredStart">*</i>
            {{ $t('cusEntry.bidMod.supplierRankNew') }}
          </span>
          <DictSelect
            v-model="headerData.extVendorPerformanceRank"
            code="RANK"
            style="width: 250px"
            :transform-options="transformOptions"
          />
        </el-form-item>
      </SrmCol>
      <!-- <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.extIsRandom"
          true-label="Y"
          false-label="N"
        >
          {{ $t('cusEntry.bidMod.ifRandom') }}
        </el-checkbox>
      </SrmCol> -->
    </SrmRow>
  </div>
</template>

<script>
/**
 * 智能推荐供应商控制
 */
export default {
  name: 'RecommendedSupplierControl',

  props: {
    header: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header || {}
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },

    excludeOrgCategoryStatus: {
      get: function () {
        const str = this.headerData.excludeOrgCategoryStatus || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.headerData.excludeOrgCategoryStatus = val.toString()
      }
    },

    // 排除状态的供应勾选
    categoryStatusCheck () {
      return this.excludeOrgCategoryStatus.length > 0
    }
  },

  methods: {
    /* 编排品类类型 */
    transformOptions (options) {
      // 过滤合格
      return options.filter(item => item.value !== 'QUALIFIED')
    }
  }
}
</script>

<style lang="scss" scoped>
.requiredStart {
  color: red;
  margin-right: 4px;
}
</style>
