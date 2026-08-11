<template>
  <div>
    <FormWrapper
      ref="formRef"
      :form-array="preArr"
      init-active
      @getFormData="getQuerydata"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      :check-change="checkChange"
      url="/api-inq/inq/reqhead/list/reqform"
    />
  </div>
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
        init: {
            type: Boolean
        },
        vendorId: {
            type: [Number, String]
        }
    },
    data () {
        return {
            selections: [],
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
            queryParam: {}
        }
    },
    watch: {
        init (newValue) {
            if (newValue) {
                this.getQuerydata()
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
                minWidth: 120,
                dataType: 'dateTime'
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
                minWidth: 120,
                dataType: 'dateTime'
            }
        ]
    },

    methods: {
        getQuerydata (obj = {}) {
            this.queryParam = { ...obj, vendorId: this.vendorId }
            this.$nextTick(() => {
                this.$refs[this.gridId].query()
            })
        },
        checkChange (val) {
            this.selections = val
            this.$emit('getSelections', this.selections)
        }
    }
}
</script>
