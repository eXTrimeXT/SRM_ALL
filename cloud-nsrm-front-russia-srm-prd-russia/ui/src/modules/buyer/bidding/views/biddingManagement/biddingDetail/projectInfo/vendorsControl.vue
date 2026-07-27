<template>
  <div>
    <SrmRow>
      <!--排除黑名单供应商-->
      <SrmCol :init-col="3">
        <el-form-item prop="excludeBlackVendors">
          <el-checkbox
            v-model="biddingBaseInfo.excludeBlackVendors"
            true-label="Y"
            false-label="N"
          >
            排除黑名单供应商
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--排除非本业务实体供应商-->
      <SrmCol :init-col="3">
        <el-form-item prop="excludeNoCurrentOrgVendors">
          <el-checkbox
            v-model="biddingBaseInfo.excludeNoCurrentOrgVendors"
            true-label="Y"
            false-label="N"
          >
            排除非本业务实体供应商
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--排除业务实体退出/冻结供应商-->
      <SrmCol :init-col="3">
        <el-form-item prop="excludeOrgQuitVendors">
          <el-checkbox
            v-model="biddingBaseInfo.excludeOrgQuitVendors"
            true-label="Y"
            false-label="N"
          >
            排除业务实体退出/冻结供应商
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <SrmRow style="margin-top: 15px">
      <!--排除 XX 状态品类供应商-->
      <SrmCol :init-col="1">
        <el-form-item>
          <el-checkbox
            :value="categoryStatusCheck"
            true-label="Y"
            false-label="N"
            disabled
          >
            排除品类状态为
            <DictSelect
              v-model="excludeOrgCategoryStatus"
              code="CATEGORY_STATUS"
              multiple
              style="width: 250px"
              :transform-options="transformOptions"
            />
            的供应商
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 推荐供应商配置
 */
export default {
  name: 'VendorsControl',

  props: {
    biddingBase: {
      type: Object,
      required: true
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    },

    excludeOrgCategoryStatus: {
      get: function () {
        const str = this.biddingBaseInfo.excludeOrgCategoryStatus || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.biddingBaseInfo.excludeOrgCategoryStatus = val.toString()
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
