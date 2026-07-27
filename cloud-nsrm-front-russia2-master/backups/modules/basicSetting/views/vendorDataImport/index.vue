<template>
  <el-container
    class="flex-container-notab the_supplierDataImport_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"

            @click="addOne"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"

            @click="saveOne"
          >
            {{ $t("common.save") }}
          </el-button>
        </template>
      </main-header>

      <table-edit
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :page-enabled="true"
        :auto-query="false"
        url="/api-base/dict/base-dict/queryPageByConditions"
        style="overflow:hidden;"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableEdit from 'lib@/components/Table/TableEdit'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'

export default {
  name: 'CategoryDivision',
  components: {
    TableEdit,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      tableData: [],
      tableHeader: [],
      queryParam: {},
      preArr: [
        {
          prop: 'pCode',
          label: '批次号',
          type: 'select',
          options: [
            { value: 'v1', label: '001' },
            { value: 'v2', label: '002' },
            { value: 'v3', label: '003' },
            { value: 'v4', label: '004' }
          ]
        },
        { prop: 'lastUpdateBy', label: '导入用户' },
        { prop: 'crationDate', label: '创建日期', type: 'date' },
        {
          prop: 'status',
          label: '状态',
          type: 'select',
          options: [
            { label: '拟定', value: 'v1' },
            { label: '校验中', value: 'v2' },
            { label: '警告', value: 'v3' },
            { label: '通过', value: 'v4' },
            { label: '完成', value: 'v5' }
          ]
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'importSupplier',
        label: '导入供应商',
        editType: 'none',
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.deleteOneItem(row)
        }.bind(this),
        formattor (val) {
          return 'Vendor.xlsx'
        }
      },
      {
        prop: 'importSupplier',
        label: '导入组织与品类',
        editType: 'none',
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.deleteOneItem(row)
        }.bind(this),
        formattor (val) {
          return 'VendorCat.xlsx'
        }
      },
      { prop: 'creationDate', label: '导入日期', width: 150, type: 'date' },
      { prop: 'lastUpdateBy', label: '导入用户', editType: 'none', width: 120 },
      {
        prop: 'status',
        label: '状态',
        width: 150,
        type: 'select',
        options: {
          status: [
            { label: '拟定', value: 'v1' },
            { label: '校验中', value: 'v2' },
            { label: '警告', value: 'v3' },
            { label: '通过', value: 'v4' },
            { label: '完成', value: 'v5' }
          ]
        }
      },
      {
        prop: 'exportSupplier',
        label: '导出供应商',
        editType: 'none',
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.deleteOneItem(row)
        }.bind(this),
        formattor (val) {
          return 'vendor.xlsx'
        }
      },
      {
        prop: 'exportOrg',
        label: '导出组织与品类',
        editType: 'none',
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.deleteOneItem(row)
        }.bind(this),
        formattor (val) {
          return 'vendor_category.xlsx'
        }
      },
      {
        prop: 'operation',
        label: '操作',
        editType: 'none',
        fixed: 'right',
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.deleteOneItem(row)
        }.bind(this),
        formattor (val) {
          return '删除'
        }
      }
    ]
    this.$nextTick(() => {
      let listdata = []
      for (let i = 1; i < 5; i++) {
        listdata.push({
          category: Math.random() > 0.5 ? 'v1' : 'v2',
          userName: '用户' + i,
          userId: '321312' + i,
          enableDate: '2020-2-' + i,
          disableDate: '2020-3-' + i,
          lastUpdateDate: '2020-4-' + i,
          lastUpdateBy: '最后更新人' + i
        })
      }
      this.$refs[this.gridId].tableData = listdata
      // this.getQuerydata()
    })
  },
  methods: {
    editTab () {},
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addOne () {
      this.$refs[this.gridId].add()
    },
    deleteOneItem () {
      if (this.$refs[this.gridId].selected.length !== 1) {
        this.$message.error('请选择一条数据')
        return
      }
      if (!this.$refs[this.gridId].selected[0].id) {
        // 直接删除的数据(新增但未保存的数据可以直接删除)
        this.$refs[this.gridId].deleteFromView()
        return
      }
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let addStr = '?id=' + this.$refs[this.gridId].selected[0].id
          this.$http({
            url: '/pss/member/dict/item' + addStr,
            method: 'DELETE',
            data: {},
            loading: true
          })
            .then(data => {
              if (data && data.success) {
                this.$message({
                  message: '删除成功',
                  type: 'success'
                })
                this.$emit(
                  'tab-remove',
                  'dictionaryDetail' + this.form.itemCode
                )
              }
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    saveOne () {
      // debugger
      if (
        !this.$refs[this.gridId].current ||
        !this.$refs[this.gridId].current.dictItemCode
      ) {
        this.$message.error('请录入数据')
        return
      }
      // this.$refs[this.gridId].current.___add---》为true表示是新增的明细行
      let url = ''
      let methodType = ''
      let params = {}
      if (this.$refs[this.gridId].current.___add) {
        // 新增模式
        methodType = 'PUT'
        url = '/pss/member/dict/item'
        params = {
          dictId: this.$attrs.params.row.id,
          materialCode: this.form.materialCode,
          dictItemCode: this.$refs[this.gridId].current.dictItemCode,
          dictItemName: this.$refs[this.gridId].current.dictItemName,
          description: this.$refs[this.gridId].current.description
        }
      } else {
        methodType = 'POST'
        url = '/pss/member/dict/item/modify'
        params = {
          id: this.$refs[this.gridId].current.id,
          dictItemName: this.$refs[this.gridId].current.dictItemName,
          description: this.$refs[this.gridId].current.description
        }
      }
      this.$http({
        url: url,
        method: methodType,
        data: params,
        loading: true
      })
        .then(data => {
          if (data && data.success) {
            this.dialogFormVisible = false
            this.$message({
              message: '保存成功',
              type: 'success'
            })
            this.$emit(
              'tab-remove',
              'dictionaryDetail' + this.form.materialCode
            )
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    importOne () {},
    exportOne () {}
  }
}
</script>
<style scoped lang="scss">
.the_supplierDataImport_wrapper {
}
</style>
