<template>
  <el-container
    class="supplierinventoryEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                prop="itemCode"
                :label="$t('common.materialCode')"
              >
                <el-input
                  v-model="form.itemCode"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="itemDesc"
                :label="$t('bidMod.itemDesc')"
              >
                <el-input
                  v-model="form.itemDesc"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="categoryName"
                :label="$t('supplierInventory.categoryName')"
              >
                <el-input
                  v-model="form.categoryName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="categoryCode"
                :label="$t('supplierInventory.categoryCode')"
              >
                <el-input
                  v-model="form.categoryCode"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorCode"
                :label="$t('supplierRating.vendorCode')"
              >
                <el-input
                  v-model="form.vendorCode"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorName"
                :label="$t('supplierRating.supplierName')"
              >
                <el-input
                  v-model="form.vendorName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="ceeaOrgName"
                :label="$t('supplierRating.entity')"
              >
                <el-input
                  v-model="form.ceeaOrgName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                prop="inventoryNumber"
                :label="$t('supplierInventory.inventoryNumber')"
              >
                <el-input
                  v-model="form.inventoryNumber"
                  v-input-format="{ type: 'float' }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="inAndOutNumber"
                :label="$t('supplierInventory.inAndOutNumber')"
              >
                <el-input
                  v-model="form.inAndOutNumber"
                  v-input-format="{ type: 'float' }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="unit"
                :label="$t('materialPrice.unit')"
              >
                <el-input
                  v-model="form.unit"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import { supplierInventoryApi } from 'modb@/planManagementBuyer/api/inventory'

export default {
  name: 'SupplierinventoryEdit',
  components: {
    MainHeader,
    CToolbar
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      form: {
        itemCode: null,
        itemDesc: null,
        categoryName: null,
        categoryCode: null,
        vendorCode: null,
        vendorName: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        inventoryNumber: null,
        inAndOutNumber: null,
        unit: null,
        unitCode: null,
        createdBy: null,
        creationDate: null
      },
      rules: {},
      readOnly: false
    }
  },
  computed: {},
  watch: {},
  created () {
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.form = row
    }
  },
  methods: {
    save () {
      this.$refs.form.validate((result) => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { supplierInventoryId, ...rest } = this.form
          console.log(this.form)
          // 该正则数字且大于等于0
          var numReg = /^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/
          var numRe = new RegExp(numReg)
          var inventoryNumber = this.form.inventoryNumber
          var inAndOutNumber = this.form.inAndOutNumber
          if (!inventoryNumber) {
            this.$message({
              type: 'warning',
              message: this.$t('supplierRating.quantityAvailableStock'),
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (!numRe.test(inventoryNumber)) {
            this.$message({
              type: 'warning',
              message: this.$t('supplierRating.correctQuantityStock'),
              duration: 10000,
              showClose: true
            })
            return false
          }
          if ((Number(inventoryNumber) + Number(inAndOutNumber)) < 0) {
            this.$message({
              type: 'warning',
              message: this.$t('supplierRating.quantityLessWarehouse'),
              duration: 10000,
              showClose: true
            })
            return false
          }
          if (flag === 'add') {
            supplierInventoryApi.add(rest).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            supplierInventoryApi.update(this.form)
              .then((res) => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancelBill()
              })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'supplierinventoryEdit')
      } else {
        this.$emit(
          'tab-remove',
          'supplierinventoryEdit' + row.supplierInventoryId
        )
      }
      this.__setTabTodo('supplierinventoryList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.supplierinventoryEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
