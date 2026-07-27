<template>
  <div class="wrapper">
    <div class="header">
      <span class="title">供应商投标情况</span>
      <!-- <span class="desc ml-20">
        本项目共发标<span class="red">3</span>家，实际投标<span class="red">5</span>家，具体信息如下
      </span> -->
    </div>
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <!-- 投标状态 -->
      <template #orderStatus="scope">
        <span>{{ $getDictLabel('SOU_ORDER_STATUS',scope.row.orderStatus) }}</span>
      </template>
      <!-- 供应商属性 -->
      <template #extVendorAttr="scope">
        <template v-if="scope.row.extVendorAttr">
          <span v-for="(item,index) in scope.row.extVendorAttr.split(';')" :key="index">{{ $getDictLabel('SOU_RECOMM_VENDOR_NATRUE',item) }};</span>
        </template>
      </template>
      <!-- 考察历史 -->
      <template #history="scope">
        <QuickSearch
          showButton
          class="quickBtn"
          btnTitle="查看"
          btnType="text"
          :pre-query-data="{'t.vendor_id': scope.row.vendorId}"
          name="scc_npm_inspect_vendor_history"
          @close-quicksearch="getRow"
        />
      </template>
    </BaseTable>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  components: {
    BaseTable,
    QuickSearch
  },
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    applicantNo: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      tableColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '供应商名称',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'extVendorAttr',
            label: '供应商属性'
          },
          slot: 'extVendorAttr'
        },
        {
          attrs: {
            prop: 'tenderPackageName',
            label: '投标包名'
          },
          slot: 'tenderPackageName'
        },
        {
          attrs: {
            prop: 'orderStatus',
            label: '投标状态'
          },
          slot: 'orderStatus'
        },
        {
          attrs: {
            prop: 'extNotjoinReason',
            label: '不参与原因'
          }
        },
        {
          attrs: {
            prop: 'rejectReason',
            label: '废标说明'
          }
        }
        // {
        //   attrs: {
        //     prop: 'history',
        //     label: '考察历史'
        //   },
        //   slot: 'history'
        // }
      ]
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  watch: {
    applicantNo: {
      handler (val) {
        if (!val || val.indexOf(';') === -1) {
          this.tableColumns = [
            {
              attrs: {
                label: '序号',
                type: 'index',
                width: 60
              }
            },
            {
              attrs: {
                prop: 'vendorName',
                label: '供应商名称',
                showOverflowTooltip: true
              }
            },
            {
              attrs: {
                prop: 'extVendorAttr',
                label: '供应商属性'
              },
              slot: 'extVendorAttr'
            },
            {
              attrs: {
                prop: 'orderStatus',
                label: '投标状态'
              },
              slot: 'orderStatus'
            },
            {
              attrs: {
                prop: 'extNotjoinReason',
                label: '不参与原因'
              }
            },
            {
              attrs: {
                prop: 'rejectReason',
                label: '废标说明'
              }
            }
            // {
            //   attrs: {
            //     prop: 'history',
            //     label: '考察历史'
            //   },
            //   slot: 'history'
            // }
          ]
        } else {
          this.tableColumns = [
            {
              attrs: {
                label: '序号',
                type: 'index',
                width: 60
              }
            },
            {
              attrs: {
                prop: 'vendorName',
                label: '供应商名称',
                showOverflowTooltip: true
              }
            },
            {
              attrs: {
                prop: 'extVendorAttr',
                label: '供应商属性'
              },
              slot: 'extVendorAttr'
            },
            {
              attrs: {
                prop: 'tenderPackageName',
                label: '投标包名'
              },
              slot: 'tenderPackageName'
            },
            {
              attrs: {
                prop: 'orderStatus',
                label: '投标状态'
              },
              slot: 'orderStatus'
            },
            {
              attrs: {
                prop: 'extNotjoinReason',
                label: '不参与原因'
              }
            },
            {
              attrs: {
                prop: 'rejectReason',
                label: '废标说明'
              }
            }
            // {
            //   attrs: {
            //     prop: 'history',
            //     label: '考察历史'
            //   },
            //   slot: 'history'
            // }
          ]
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    getRow (val) {
      if (val && val.inspectId) {
        this.$emit('tabAdd', val)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.ml-20 {
  margin-left: 20px;
}
.header {
  margin:10px 0;
  .title {
    font-weight:bold;
  }
}
</style>
