<template>
  <el-table
    :data="signUpFiles"
    border
    height="345px"
  >
    <el-table-column
      type="index"
      :label="$t('common.sort')"
      width="50"
    />

    <!--资料要求-->
    <SrmCommonFile
      type="table-column"
      :table-column-options="{
        label: $t('bidMod.fileQualify'),
        prop: 'souDocId',
        nameProp: 'souFileName',
        minWidth: '250'
      }"
      readonly
    />

    <!--备注-->
    <el-table-column
      prop="souRemark"
      :label="$t('common.remark')"
      show-overflow-tooltip
    />

    <!--附件名称-->
    <SrmCommonFile
      type="table-column"
      :table-column-options="{
        label: $t('bidMod.fileName'),
        prop: 'signUpDocId',
        nameProp: 'signUpFileName',
        minWidth: '250'
      }"
      readonly
    />
  </el-table>
</template>

<script>
/**
 * 报名信息
 */
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ApplyInfo',

  inject: ['attrsParamsRow'],

  props: {
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      signUpListData: [],
      signUpFiles: []
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getApplyInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取详情 */
    async getApplyInfo () {
      if (!this.attrsParamsRow.projectId) {
        return
      }

      let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.attrsParamsRow.projectId }], 'getVendorSignUpDetail')
      const response = await carVendorHttp.order.getSignUpInfo(transformParams)
      if (response?.data?.records.length) {
        let { signUpFileList } = response.data.records[0]
        this.signUpFiles = signUpFileList || []
      }
    }
  }
}
</script>
