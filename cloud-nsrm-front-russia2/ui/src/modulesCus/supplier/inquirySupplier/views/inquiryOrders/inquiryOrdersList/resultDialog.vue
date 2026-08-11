<template>
  <SrmDialog
    :title="$t('bidMod.viewWinResults')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <FormWrapper :form-array="formWrapperConfig" @getFormData="getQueryData" />

    <TableView
      ref="list"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :com-active="$attrs['changeTab']"
      table-height="260px"
      custom-table-key="inquiryOrdersResultDialog"
      :url="tableViewUrl"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 供应商查看中标结果
 */
import { inqSupplierHttp } from 'mods@/inquirySupplier/api'
import { maxNumberOption } from 'lib@/composition/commonComposition'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'ResultDialog',

  components: {
    FormWrapper,
    TableView
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    viewRow: {
      type: Object,
      required: true,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      tableViewUrl: inqSupplierHttp.order.inqOrderHistoryUrl,
      tableData: [],
      tableHeader: [
        // 轮次
        {
          prop: 'round',
          minWidth: 70,
          label: this.$t('bidMod.bidingRound')
        },
        // 物料编码
        {
          prop: 'itemCode',
          minWidth: 120,
          label: this.$t('bidMod.itemCode'),
          formattor: val => targetNumReveal(val)
        },
        // 物料名称
        {
          prop: 'itemDesc',
          minWidth: 150,
          label: this.$t('bidMod.itemName')
        },
        // 本轮入围情况
        {
          prop: 'winStatus',
          label: this.$t('bidMod.winStatus'),
          minWidth: 110,
          dataType: 'dict',
          code: 'SOU_WIN_STATUS'
        },
        // 评选情况
        {
          prop: 'selectStatus',
          label: this.$t('bidMod.selectSituation'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_SELECT_STATUS'
        }
      ],
      queryParam: {},
      formWrapperConfig: [
        // 物料编码
        { prop: 'itemCode', label: () => this.$t('bidMod.itemCode') },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => this.roundOption
        }
      ]
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
    },
    roundOption () {
      return maxNumberOption(this.viewRow.currentRound)
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询参数 */
    getQueryData (val) {
      this.queryParam = Object.assign({ projectId: this.viewRow.projectId }, val)
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    }
  }
}
</script>
