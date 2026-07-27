<template>
  <el-collapse v-model="colVal">
    <!-- 物资信息 -->
    <el-collapse-item :title="$t('cusEntry.bidMod.itemInfo')" name="1">
      <ItemInfo
        ref="itemInfo"
        :value.sync="form.itemList"
        :readonly="readonly"
        :form="form"
        @file-success="$emit('file-success')"
        @merge-data="$emit('merge-data')"
      />
    </el-collapse-item>
    <!-- 供应商信息 -->
    <el-collapse-item :title="$t('vendorMod.vendorInfo')" name="2">
      <VendorInfo
        ref="vendorInfo"
        :value.sync="form.vendorList"
        :form="form"
        :readonly="readonly"
      />
    </el-collapse-item>
  </el-collapse>
</template>
<script>
import ItemInfo from './requireTab/itemInfo'
import VendorInfo from './requireTab/vendorInfo'

export default {
  name: 'RequireTab',
  components: {
    ItemInfo,
    VendorInfo
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      colVal: ['1', '2']
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  }
}
</script>
<style scoped lang="scss">
.mt-10 {
  margin-top: 10px;
}
</style>
