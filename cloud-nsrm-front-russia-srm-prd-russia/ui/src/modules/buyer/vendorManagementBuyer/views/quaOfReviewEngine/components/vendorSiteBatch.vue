<template>
  <el-popover v-model="visible" placement="top" width="400">
    <div style="padding:11px;">
      <p>
        <!-- 请选择国家 -->
        <span style="padding-right:5px;">{{ $t('vendorMod.msgSelCountry') }}</span>
        <DictSelect v-model="form.globalCountry" code="country" filterable clearable />
      </p>
      <p v-if="isNewVendor">
        <!-- 请选择地点名称 -->
        <span style="padding-right:5px;">{{ $t('vendorMod.msgAddressName') }}</span>
        <DictSelect v-model="form.globalAddress" code="VENDOR_SITE_CODE" filterable clearable />
      </p>
      <p>
        <!-- 请输入详细地址 -->
        <span style="padding-right:5px;">{{ $t('vendorMod.msgDetailAddr') }}</span>
        <el-input v-model="form.globalAddressDetail" clearable style="display:block;" />
      </p>
    </div>
    <div style="text-align: right; margin: 0">
      <el-button type="text" @click="visible = false">
        {{
          $t('common.cancel')
        }}
      </el-button>
      <el-button type="text" @click="batchSelectCountry">
        {{
          $t('common.confirm')
        }}
      </el-button>
    </div>
    <!-- 批量维护 -->
    <el-button slot="reference" type="primary" class="detail-pbtn">
      {{ $t('vendorMod.batchMaintain') }}
    </el-button>
  </el-popover>
</template>
<script>

export default {
    name: 'VendorSiteBatch',
    props: {
        isNewVendor: {
            type: Boolean
        }
    },
    data () {
        return {
            visible: false,
            form: {
                globalCountry: '',
                globalAddress: '',
                globalAddressDetail: ''
            }
        }
    },
    methods: {
        batchSelectCountry () {
            this.visible = false
            this.$emit('batchSelectCountry', this.form)
        }
    }
}
</script>
