<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="searchFormConfig"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        custom-table-key="techExchangeManagementFeedbackViewTable"
        :url="techExchangeBuyerApi.feedbackListPageUrl"
      >
        <template #tecExcFiles="{ scope }">
          <!--下载-->
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.fileuploadId,
              fileName: scope.row.fileName
            }"
            readonly
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 查看供应商反馈
 */
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { techExchangeBuyerApi } from 'modb@/techExchange/api'
export default {
  name: 'TechExchangeManagementFeedbackView',

  components: {
    FormWrapper,
    TableView
  },

  props: {
    attrsParamsRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      techExchangeBuyerApi: techExchangeBuyerApi,
      tableData: [],
      tableHeader: [
        // 供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.vendorCode'),
          minWidth: 130
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('bidMod.vendorName'),
          minWidth: 130
        },
        // 方案附件
        {
          prop: 'tecExcFiles',
          label: '方案附件',
          minWidth: 130,
          showType: 'slot',
          slot: 'tecExcFiles'
        },
        // 备注
        {
          prop: 'remark',
          label: this.$t('common.remark'),
          minWidth: 130
        }
      ],
      queryParam: {},
      searchFormConfig: [
        // 交流标题
        {
          prop: 'vendorId',
          label: this.$t('bidMod.vendorName'),
          type: 'select',
          options: []
        }
      ]
    }
  },

  created () {
    this.getExcInfo()
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询详情，获取邀请供应商列表 */
    async getExcInfo () {
      if (!this.attrsParamsRow.technicalExchangeId) {
        return
      }

      const response = await techExchangeBuyerApi.getExcInfo(this.attrsParamsRow.technicalExchangeId)
      if (response && response.data) {
        this.searchFormConfig[0].options = response.data.tecExcVendors.map(item => {
          return {
            label: item.vendorName,
            value: item.vendorId
          }
        })
      }
    },

    /* 查询 */
    getQueryData (payload) {
      this.queryParam = {
        ...(payload || this.queryParam),
        technicalExchangeId: this.attrsParamsRow.technicalExchangeId
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    }
  }
}
</script>
