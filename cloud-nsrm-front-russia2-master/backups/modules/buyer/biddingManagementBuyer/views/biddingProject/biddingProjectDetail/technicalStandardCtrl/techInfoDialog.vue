<template>
  <srm-dialog
    :title="$t('bidMod.bidDetail')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <h3>{{ $t('bidMod.vendorInfo') }}</h3>
    <div class="the_display_content">
      <srm-row>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.companyName') }}</span>{{ techInfo.companyName }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.companyType') }}</span>{{ $getDictLabel('COMPANY_NATURE', techInfo.companyType) }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.companyCreationDate') }}</span>{{ techInfo.companyCreationDate }}
        </srm-col>
      </srm-row>

      <srm-row>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.overseasRelationName') }}</span>{{ techInfo.overseasRelationName }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.legalPerson') }}</span>{{ techInfo.legalPerson }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.registeredCapital') }}</span>{{ techInfo.registeredCapital }}
        </srm-col>
      </srm-row>

      <srm-row>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.address') }}</span>{{ techInfo.address }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.businessStartDate') }}</span>{{ techInfo.businessStartDate }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.businessEndDate') }}</span>{{ techInfo.businessEndDate }}
        </srm-col>
      </srm-row>

      <srm-row>
        <srm-col :init-col="1">
          <div>
            <span>{{ $t('bidMod.businessScope') }}</span>
          </div>
          <div>{{ techInfo.businessScope }}</div>
        </srm-col>
      </srm-row>
    </div>

    <!-- 技术标附件 -->
    <h3>{{ $t('bidMod.techBidAttach') }}</h3>
    <el-table
      :data="techInfoFile"
      style="width: 100%"
      border
      max-height="250"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        align="center"
        prop="reqFileName"
        :label="$t('bidMod.fileQualify')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.fileName'),
          prop: 'docId',
          nameProp: 'fileName'
        }"
        readonly
      />

      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bidMod.remark')"
        min-width="250"
        show-overflow-tooltip
      />
    </el-table>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 投标详情
 */
export default {
  name: 'TechInfoDialog',

  props: {
    visible: {
      type: Boolean
    },
    bidingId: {
      type: [Number, String]
    },
    editRow: {
      type: Object
    }
  },
  data () {
    return {
      techInfo: {
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
      },
      techInfoFile: []
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
          this.getTechInfo()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询数据 */
    getTechInfo () {
      this.$http({
        url: '/api-bid/signUpManagement/management/getTechInfo',
        method: 'GET',
        params: {
          bidingId: this.bidingId,
          vendorId: this.editRow.vendorId
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.techInfo = data.data.signUpBaseInfoVO
          this.techInfoFile = data.data.signUpFileVOList
        }
      })
    }
  }
}
</script>
