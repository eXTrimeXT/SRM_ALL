<template>
  <div class="recommended-supplier-control">
    <SrmRow>
      <!--排除黑名单供应商-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.excludeBlackVendors"
          true-label="Y"
          false-label="N"
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
        >
          {{ $t('bidMod.excludeOrgQuitVendors') }}
        </el-checkbox>
      </SrmCol>
    </SrmRow>

    <SrmRow style="margin-top: 15px">
      <!--排除 XX 状态品类供应商 排除品类状态为XXX的供应商-->
      <SrmCol :init-col="2">
        <el-checkbox
          :value="categoryStatusCheck"
          true-label="Y"
          false-label="N"
          disabled
        >
          {{ $t('bidMod.categoryStatusCheck1') }}
          <DictSelect
            v-model="excludeOrgCategoryStatus"
            code="CATEGORY_STATUS"
            multiple
            style="width: 250px"
            :transform-options="transformOptions"
          />
          {{ $t('bidMod.categoryStatusCheck2') }}
        </el-checkbox>
      </SrmCol>
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
