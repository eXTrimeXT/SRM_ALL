<template>
  <srm-dialog
    :title="$t('route.sourcingApplicationBuyer')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <FormWrapper
      ref="formRef"
      :form-array="preArr"
      init-active
      @getFormData="getQuerydata"
      @synchronous-value="syncFilterParams"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      reserveSelection
      row-key="inspectionProjectId"
      :check-change="checkChange"
      :setSelectable="setSelectable"
      url="/api-inq/inq/reqhead/list/reqform"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="save">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'InspectionItemDialog',
  components: {
    FormWrapper,
    TableView
  },

  props: {
    visible: {
      type: Boolean
    }
  },

  data () {
    return {
      selections: [],
      selected: [],
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 需求标题
        {
          prop: 'souReqTitile',
          label: this.$t('sourcingBuyer.souReqTitile')
        },
        // 寻源单号
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        }
      ],
      queryParam: {},
      vendorId: null
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
    this.tableHeader = [
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        minWidth: 120
      },
      // 需求标题
      {
        prop: 'souReqTitile',
        label: this.$t('sourcingBuyer.souReqTitile'),
        minWidth: 120
      },
      // 状态
      {
        prop: 'auditStatus',
        label: this.$t('sourcingBuyer.status'),
        minWidth: 120,
        dataType: 'dict',
        code: 'APPROVE_STATUS'
      },
      // 截止时间
      {
        prop: 'expirationTime',
        label: this.$t('sourcingBuyer.expirationTime'),
        minWidth: 120
      },
      // 创建人
      {
        prop: 'reviewCreatedBy',
        label: this.$t('sourcingBuyer.createdFullName'),
        minWidth: 120
      },
      // 创建时间
      {
        prop: 'reviewCreationDate',
        label: this.$t('sourcingBuyer.creationDate'),
        minWidth: 120
      }
    ]
  },

  methods: {
    init (selected, vendorId) {
      this.vendorId = vendorId
      this.getQuerydata()
      this.selected = selected
    },
    getQuerydata (obj = {}) {
      this.queryParam = { ...obj, vendorId: this.vendorId }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = { ...values, vendorId: this.vendorId }
    },
    checkChange (val) {
      this.selections = val
    },
    setSelectable (row) {
      return this.selected.findIndex(item => item.reqHeadId === row.reqHeadId) === -1
    },
    save () {
      this.dialogVisible = false
      this.$emit('getSelections', this.selections)
    }
  }
}
</script>
