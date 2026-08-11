<template>
  <div>
    <srm-row>
      <!--排除黑名单供应商-->
      <srm-col :init-col="3">
        <el-form-item prop="excludeBlackVendors">
          <el-checkbox
            v-model="bargainBaseInfo.excludeBlackVendors"
            true-label="Y"
            false-label="N"
          >
            排除黑名单供应商
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--排除非本业务实体供应商-->
      <srm-col :init-col="3">
        <el-form-item prop="excludeNoCurrentOrgVendors">
          <el-checkbox
            v-model="bargainBaseInfo.excludeNoCurrentOrgVendors"
            true-label="Y"
            false-label="N"
          >
            排除非本业务实体供应商
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--排除业务实体退出/冻结供应商-->
      <srm-col :init-col="3">
        <el-form-item prop="excludeOrgQuitVendors">
          <el-checkbox
            v-model="bargainBaseInfo.excludeOrgQuitVendors"
            true-label="Y"
            false-label="N"
          >
            排除业务实体退出/冻结供应商
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row style="margin-top: 15px">
      <!--排除 XX 状态品类供应商-->
      <srm-col :init-col="1">
        <el-form-item>
          <el-checkbox
            :value="categoryStatusCheck"
            true-label="Y"
            false-label="N"
            disabled
          >
            排除
            <dict-select
              v-model="excludeOrgCategoryStatus"
              code="CATEGORY_STATUS"
              multiple
              style="width: 250px"
            />
            状态品类供应商
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>
  </div>
</template>

<script>
/**
 * 推荐供应商配置
 */
export default {
  name: 'VendorsControl',
  props: {
    bargainBase: {
      type: Object
    }
  },
  computed: {
    bargainBaseInfo: {
      get: function () {
        return this.bargainBase
      },
      set: function (val) {
        this.$emit('update:bargainBase', val)
      }
    },
    excludeOrgCategoryStatus: {
      get: function () {
        const str = this.bargainBaseInfo.excludeOrgCategoryStatus || ''
        return str ? str.split(',') : []
      },
      set: function (val) {
        this.bargainBaseInfo.excludeOrgCategoryStatus = val.toString()
      }
    },
    // 排除状态的供应勾选
    categoryStatusCheck () {
      return this.excludeOrgCategoryStatus.length > 0
    }
  }
}
</script>
