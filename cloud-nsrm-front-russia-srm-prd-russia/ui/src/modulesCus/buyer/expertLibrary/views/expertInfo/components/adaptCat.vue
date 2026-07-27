<template>
  <div class="wrapper">
    <div class="header">
      <span class="red">*</span>
      适用品类
    </div>
    <div class="btns mg-10">
      <!-- <CCategorySelect
        v-if="!readonly"
        select-type="button"
        :multiple="true"
        :selected-lines="tableData"
        @select="addCategorysList"
      /> -->
      <el-button v-if="!readonly" type="primary" @click="addCategory">
        新增
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
      <el-table-column
        type="index"
        label="序号"
        width="60"
      />

      <el-table-column
        prop="categoryName"
        label="品类名称"
      />

      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
      >
        <template v-slot="scope">
          <el-button type="text" @click="deleteRow(scope)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加品类 -->
    <srm-dialog
      :visible.sync="showCatDialog"
      title="选择评标品类"
      size="middle"
      :close-on-click-modal="false"
    >
      <el-cascader
        v-model="catVal"
        :collapse-tags="true"
        :props="props"
        clearable
        style="width:80%"
      />
      <div slot="footer">
        <el-button @click="showCatDialog=false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="catDialogConfirm">
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import CCategorySelect from 'lib@/components/c-category-select'

export default {
  components: {
    CCategorySelect
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      categoryId: -1,
      props: {
        multiple: true,
        lazy: true,
        checkStrictly: false,
        lazyLoad: async (node, resolve) => {
          console.log('node', node)
          if (node.value) {
            this.categoryId = node.value
          }
          const response = await this.$http({
            url: '/api-base/purchase/purchaseCategory/listChildren',
            method: 'POST',
            params: {
              categoryId: this.categoryId
            }
          })
          let nodes = response.data.map(item => ({
            value: item.categoryId,
            label: item.categoryName,
            code: item.categoryCode
          }))
          resolve(nodes)
        }
      },
      options: [],
      catVal: [],
      showCatDialog: false,
      selectedRows: []
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
  methods: {
    add () {
      this.tableData.push({})
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    addCategorysList (data) {
      console.log('data', data)
      if (data.length) {
        this.tableData = Array.from(new Set(data))
      }
    },
    addCategory () {
      this.showCatDialog = true
    },
    async catDialogConfirm () {
      console.log('$$$', this.catVal)
      if (this.catVal.length) {
        let idList = []
        for (let item of this.catVal) {
          if (item.length) {
            idList.push(item[item.length - 1])
          }
        }
        const response = await this.$http({
          url: '/api-base/pj/category/listLastLevelCategoryByCodes',
          method: 'POST',
          data: idList,
          loading: true
        })
        if (response.data.length) {
          let categoryIdList = this.tableData.map(item => item.categoryId)
          for (let item of response.data) {
            if (!categoryIdList.includes(item.categoryId)) {
              this.tableData.push({
                categoryId: item.categoryId,
                categoryCode: item.categoryCode,
                categoryName: item.categoryName
              })
            }
          }
        }
        this.showCatDialog = false
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.mg-10 {
  margin: 10px 0;
}
</style>
