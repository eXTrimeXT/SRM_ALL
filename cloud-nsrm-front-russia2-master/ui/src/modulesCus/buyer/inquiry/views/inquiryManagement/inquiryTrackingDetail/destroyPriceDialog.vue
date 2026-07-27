<template>
  <SrmDialog
    size="middle"
    :title="$t('bidMod.cancelBiding1')"
    :visible.sync="destoryFormVisible"
    :close-on-click-modal="false"
  >
    <p>
      <!-- 作废说明 -->
      <span>{{ $t('bidMod.discardDesc') }}</span>
      <el-input
        v-model="destoryText"
        type="textarea"
        :rows="2"
        :placeholder="$t('common.pleaseTypeContents')"
      />
    </p>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button
        @click="destoryFormVisible = false"
      >
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="submitDestoryText"
      >
        {{ $t("common.submit") }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 作废报价
 */
export default {
  name: 'DestroyPriceDialog',
  data () {
    return {
      destoryText: '',
      destoryFormVisible: false
    }
  },
  methods: {
    submitDestoryText () {
      this.destoryFormVisible = false
    }
  }
}
</script>
