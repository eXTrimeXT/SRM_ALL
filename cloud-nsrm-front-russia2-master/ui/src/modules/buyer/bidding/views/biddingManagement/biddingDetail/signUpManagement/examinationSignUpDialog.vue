<template>
  <SrmDialog
    :title="$t('bidMod.entryManagement')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--供应商基本信息-->
    <VendorInfo :vendor-info-data="vendorInfo" />

    <!--报名附件-->
    <h3>{{ $t("bidMod.signupFile") }}</h3>
    <el-table
      :data="signUpFileList"
      style="width: 100%"
      border
      height="150"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--资料要求-->
      <el-table-column
        prop="souFileName"
        :label="$t('bidMod.fileQualify')"
        min-width="150"
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

      <!--备注-->
      <el-table-column
        prop="souRemark"
        :label="$t('bidMod.remark')"
        min-width="250"
        show-overflow-tooltip
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 审查报名资料
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import VendorInfo from 'lib@/composition/origin/vendorInfo'

export default {
  name: 'EntryManagementDialog',

  components: { VendorInfo },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    }
  },

  data () {
    return {
      signUpFileList: [],
      vendorInfo: {
        address: '',
        companyName: '',
        companyCreationDate: '',
        overseasRelationName: '',
        registeredCapital: '',
        companyType: '',
        legalPerson: '',
        businessStartDate: '',
        businessEndDate: '',
        businessScope: ''
      }
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  mounted () {
    this.getSignUpInfo()
  },

  methods: {
    /* 查询报名资料 */
    async getSignUpInfo () {
      if (!this.editRow) {
        return
      }

      const response = await bidBuyerHttp.signUp.getSignUpInfo({
        projectId: this.projectId,
        vendorId: this.editRow.vendorId
      })
      if (response) {
        const {
          companyInfo = {},
          signUpFileList = []
        } = response.data || {}
        this.vendorInfo = companyInfo
        this.signUpFileList = signUpFileList
      }
    }
  }
}
</script>

<style scoped lang="scss">
.labelWithValue {
  font-size: 14px;
  padding-left: 100px;
  position: relative;
  color: #333;
  line-height: 22px;
  span {
    line-height: 22px;
    position: absolute;
    left: 0;
    width: 100px;
    text-align: right;
    color: #999;
  }
}
</style>
