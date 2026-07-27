<template>
  <el-container
    class="flex-container-notab base_price_list_wrapper"
    direction="vertical"
    style="height: 500px;width: 100%;"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left" />
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        url="/api-log/businessInfoLog/listPage"
        :open-custom-table="false"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'LoggerList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  props: {
    businessId: {
      required: true
    },
    visible: {
      required: true
    }
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'LoggerList',
      gridId: 'from',
      queryParam: {},
      pageSize: 15,
      tableHeader: [
        // {
        //   label: "单据号",
        //   prop: "businessNo",
        //   minWidth: 150
        // },
        {
          label: () => this.$t('dataConfMod.nickname1'), // 操作人
          prop: 'nickname',
          minWidth: 150,
          align: 'center'
        },
        {
          label: () => this.$t('common.operation'), // 操作
          prop: 'operateType',
          align: 'center',
          minWidth: 150
        },
        // {
        //   label: "操作说明",
        //   prop: "operateInfo",
        //   minWidth: 150
        // },
        {
          label: () => this.$t('dataConfMod.operationTime'), // 操作时间
          prop: 'operateTime',
          formattor: val => val.replace(/T/g, ' '),
          align: 'center',
          minWidth: 150
        },
        {
          label: () => this.$t('dataConfMod.username1'), // 操作账号
          prop: 'username',
          align: 'center',
          minWidth: 150
        }
        // {
        //   label: "用户类型",
        //   prop: "userType",
        //   minWidth: 150,
        //   formattor: val => (val === "BUYER" ? "采购商" : "供应商")
        // },
        // {
        //   label: "操作说明",
        //   prop: "operateInfo",
        //   minWidth: 150
        // },
        // {
        //   label: "所属模块",
        //   prop: "model",
        //   minWidth: 150
        // }
      ],
      queryForm: [
        {
          prop: 'operateType',
          label: () => this.$t('dataConfMod.operateType') //  操作类型
        },
        {
          prop: 'username',
          label: () => this.$t('dataConfMod.username1') // 操作人账号
        }
      ],
      tableData: []
    }
  },
  watch: {
    visible (newValue, oldValue) {
      if (newValue !== oldValue) {
        this.getQuerydata()
      }
    }
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = {
        ...v,
        businessId: this.businessId
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
