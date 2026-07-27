<template>
  <srm-dialog
    :title="$t('bidMod.entryManagement')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--供应商基本信息-->
    <h3>{{ $t("bidMod.vendorInfo") }}</h3>
    <div class="the_display_content">
      <srm-row :gutter="20">
        <srm-col :init-col="3">
          <!--企业名称-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.companyName") }}</span>{{ applicantForm.companyName }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--企业性质-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.companyType") }}</span>{{ $getDictLabel('COMPANY_NATURE', applicantForm.companyType) }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--成立日期-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.companyCreationDate") }}</span>{{ applicantForm.companyCreationDate }}
          </div>
        </srm-col>
      </srm-row>

      <srm-row :gutter="20">
        <srm-col :init-col="3">
          <!--境内外关系-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.overseasRelationName") }}</span>{{ applicantForm.overseasRelationName }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--法定代表人-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.legalPerson") }}</span>{{ applicantForm.legalPerson }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--注册资本-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.registeredCapital") }}</span>{{ applicantForm.registeredCapital }}
          </div>
        </srm-col>
      </srm-row>

      <srm-row :gutter="20">
        <srm-col :init-col="3">
          <!--营业地址-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.address") }}</span>{{ applicantForm.address }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--营业执照生效日期-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.businessStartDate") }}</span>{{ applicantForm.businessStartDate }}
          </div>
        </srm-col>

        <srm-col :init-col="3">
          <!--营业执照失效日期-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.businessEndDate") }}</span>{{ applicantForm.businessEndDate }}
          </div>
        </srm-col>
      </srm-row>

      <srm-row>
        <srm-col :init-col="1">
          <!--经营范围-->
          <div class="labelWithValue">
            <span>{{ $t("bidMod.businessScope") }}</span>{{ applicantForm.businessScope }}
          </div>
        </srm-col>
      </srm-row>
    </div>

    <!--报名附件-->
    <h3>{{ $t("bidMod.signupFile") }}</h3>
    <el-table
      :data="applicantFIleItems"
      style="width: 100%"
      border
      height="150"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--资料要求-->
      <el-table-column
        align="center"
        prop="requireFileName"
        :label="$t('bidMod.fileQualify')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.fileName'),
          prop: 'vendorDocId',
          nameProp: 'vendorFileName',
          minWidth: '250'
        }"
        readonly
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="requireComments"
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
  </srm-dialog>
</template>

<script>
/**
 * 审查报名资料
 */

export default {
  name: 'EntryManagementDialog',

  props: {
    visible: {
      type: Boolean
    },
    editRow: {
      type: Object
    },
    bargainId: {
      type: [Number, String]
    }
  },
  data () {
    return {
      applicantFIleItems: [],
      applicantForm: {
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
  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal) {
          this.getSignUpInfo()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询报名资料 */
    getSignUpInfo () {
      if (!this.editRow) return

      this.$api.brg.inquiryByProject.querySignUpDetailInfo({
          bargainId: this.bargainId,
          vendorId: this.editRow.vendorId
        }).then(data => {
        if (data && data.data) {
          this.applicantForm = data.data.vendorInfo
          this.applicantFIleItems = data.data.signUpFiles
        }
      })
    }
  }
}
</script>
