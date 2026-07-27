<template>
  <SrmDialog
    :title="$t('bidMod.bidDetail')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <vendor-info :vendor-info-data="companyInfo" />

    <!-- 技术标附件 -->
    <h3>{{ $t('bidMod.techBidAttach') }}</h3>
    <el-table
      :data="techFileList"
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
          prop: 'orderDocId',
          nameProp: 'orderFileName',
          minHeight: '250'
        }"
        readonly
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="orderRemark"
        :label="$t('bidMod.remark')"
        min-width="250"
        show-overflow-tooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 投标详情
 */
import { brgBuyerHttp } from 'modb@/bargain/api'
import vendorInfo from 'lib@/composition/origin/vendorInfo'

export default {
  name: 'TechInfoDialog',

  components: {
    vendorInfo
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    editRow: {
      type: Object,
      default: () => ({})
    }
  },

  data () {
    return {
      companyInfo: {
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
      techFileList: []
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
    this.getTechInfo()
  },

  methods: {
    /* 查询数据 */
    async getTechInfo () {
      if (!this.editRow.orderId) {
        return
      }

      const response = await brgBuyerHttp.tech.vendorTechOrder(this.editRow.orderId)
      if (response) {
        const {
          companyInfo = {},
          techFileList = []
        } = response.data || {}
        this.companyInfo = companyInfo
        this.techFileList = techFileList
      }
    }
  }
}
</script>
