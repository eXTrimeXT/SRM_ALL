<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <main-header>
        <template slot="left">
          <el-button
            type="primary"
            size="mini"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            type="primary"
            size="mini"
            :disabled="!currentRow"
            @click="editTab"
          >
            {{ $t('common.edit') }}
          </el-button>
          <el-button
            type="primary"
            size="mini"
            :disabled="!currentRow"
            @click="deleteOne"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        url="/isales-main/mstQuicksearchConfig/queryList"
        url-for-count="/isales-main/mstQuicksearchConfig/queryCount"
      />
      <!-- 弹框区域-->
      <srm-dialog
        size="middle"
        title="供应商品类管理"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="供应商品类代码"
                :label-width="formLabelWidth"
                prop="purOrg"
              >
                <el-select v-model="form.purOrg" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                label="供应商品类名称"
                :label-width="formLabelWidth"
                prop="carType"
              >
                <el-select v-model="form.carType" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="品类引入状态"
                :label-width="formLabelWidth"
                prop="sampleFlow"
              >
                <el-input v-model="form.carNum" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                label="采购商品类代码"
                :label-width="formLabelWidth"
                prop="sampleFlow"
              >
                <el-input v-model="form.userID" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="采购商品类名称"
                :label-width="formLabelWidth"
                prop="sampleFlow"
              >
                <el-input v-model="form.conductorName" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                label="备注"
                :label-width="formLabelWidth"
                prop="sampleFlow"
              >
                <el-input v-model="form.remark" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'

export default {
  name: 'VendorCategoryManagement',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      form: {
        purOrg: '',
        carType: '',
        carNum: '',
        userID: '',
        conductorName: '',
        telephone: '',
        driveID: '',
        enableDate: '',
        disableDate: ''
      },
      rules: {
        accessType: [{ required: true, message: '请输入供方准入类型' }],
        accessFlow: [{ required: true, message: '请输入准入流程' }],
        sampleFlow: [{ required: true, message: '请输入样品试用流程' }]
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'vendorCategoryCode', label: '供应商品类代码' },
        { prop: 'vendorCategoryName', label: '供应商品类名称' },
        { prop: 'categoryType', label: '品类引入状态' }
      ]
    }
  },
  created () {
    this.tableHeader = [
      { prop: 'vendorCategoryCode', label: '供应商品类代码', width: 150 },
      { prop: 'vendorCategoryName', label: '供应商品类名称', width: 150 },
      { prop: 'categoryType', label: '品类引入状态', width: 100 },
      { prop: 'purCategoryCode', label: '采购商品类代码', width: 150 },
      { prop: 'purCategoryName', label: '采购商品类名称', width: 150 },
      { prop: 'remark', label: '备注', width: 100 }
    ]
    setTimeout(() => {
      let listdata = []
      for (let i = 1; i < 5; i++) {
        listdata.push({
          vendorCategoryCode: 'v' + i,
          vendorCategoryName: 'n' + i,
          categoryType: 'type' + i
        })
      }
      this.$refs[this.gridId].tableData = listdata
    }, 555)
  },
  methods: {
    getQuerydata (v) {
      console.log(v)
    },
    // 编辑tab
    editTab (type) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = ''
        }
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = this.currentRow[i]
        }
      }
      this.dialogFormVisible = true
    },
    deleteOne (val) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          // debugger
        })
        .catch(() => {})
    },
    addOne () {
      // 验证form表单
      this.$refs.form.validate((valid) => {
        if (valid) {
          // =====
        } else {
          return false
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
