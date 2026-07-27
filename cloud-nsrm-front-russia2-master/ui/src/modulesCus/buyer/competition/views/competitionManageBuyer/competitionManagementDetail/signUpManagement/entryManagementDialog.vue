<template>
  <srm-dialog
    :title="$t('bidMod.entryManagement')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--供应商基本信息-->
    <!-- <VendorInfo :vendor-info-data="vendorInfoData" /> -->

    <!--报名附件-->
    <h3>{{ $t("bidMod.signupFile") }}</h3>
    <el-table
      :data="signUpFiles"
      border
      height="150"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
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
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
        min-width="250"
        show-overflow-tooltip
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 审查报名资料
 */
import { carBuyerHttp } from 'modb@/competition/api'
import VendorInfo from 'lib@/composition/origin/vendorInfo'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'EntryManagementDialog',

  components: { VendorInfo },

  props: {
    visible: {
      type: Boolean
    },
    editRow: {
      type: Object
    },
    projectId: {
      type: [Number, String],
      default: ''
    }
  },

  data () {
    return {
      signUpFiles: [],
      vendorInfoData: {
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

  created () {
    this.getSignUpInfo()
  },

  methods: {
    /* 查询报名资料 */
    async getSignUpInfo () {
      if (!this.editRow) {
        return
      }
      const params = {
        projectId: this.projectId,
        vendorId: this.editRow.vendorId
      }
      const response = await carBuyerHttp.signUp.getSignUpInfo(params)
      if (response && response.data && response.data) {
        const { signUpFileList, companyInfo: vendorInfo, bondFileList } = response.data
        const transBondFileList = bondFileList?.map(item => ({
          signUpDocId: item.souFileId,
          signUpFileName: item.souFileName,
          ...item
        }))
        if (signUpFileList && Array.isArray(signUpFileList)) {
          this.signUpFiles = [...signUpFileList, ...transBondFileList]
        }
        if (vendorInfo) {
          this.vendorInfoData = vendorInfo
        }
      }
    }
  }
}
</script>
