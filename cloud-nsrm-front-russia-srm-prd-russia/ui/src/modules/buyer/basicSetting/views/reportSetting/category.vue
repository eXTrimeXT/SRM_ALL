<template>
  <div>
    <!-- 年度采购品类目标降本率设置 -->
    <srm-dialog
      :visible.sync="value"
      :title="$t('dataConfMod.allReductionRateSetting')"
      size="middle"
      :close-on-click-modal="false"
      @close="close"
    >
      <el-form
        ref="searchForm"
        :model="form"
        class="form-fill-style"
      >
        <el-row
          type="flex"
          style="margin-bottom: 12px;"
          :gutter="32"
        >
          <el-col :span="12">
            <el-button
              type="primary"
              @click="addCategory"
            >
              {{ $t("common.add") }}
            </el-button>
            <el-button
              @click="delSelect"
            >
              {{ $t("common.delete") }}
            </el-button>
          </el-col>
        </el-row>
        <el-table
          ref="formTable"
          stripe
          border
          max-height="250px"
          :data="form.categoryList"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            align="center"
            width="55"
          />
          <!-- 品类选择 -->
          <el-table-column
            prop="categoryNames"
            align="center"
            :label="$t('dataConfMod.categorySelect')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-form-item
                :prop="'categoryList.' + scope.$index + '.categoryNames'"
                :rules="rules.categoryNames"
              >
                <!-- 选择品类 -->
                <el-input
                  v-model="scope.row.categoryNames"
                  :placeholder="$t('dataConfMod.selectCategory')"
                  class="input-with-select"
                >
                  <el-button
                    slot="append"
                    type="text"
                    @click="selectCat(scope.row, scope.$index)"
                  >
                    <!-- 选择 -->
                    {{ $t("common.select") }}
                  </el-button>
                </el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 目标降本率（%） -->
          <el-table-column
            prop="rate"
            align="center"
            :label="$t('dataConfMod.targetCostReductionRate')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-form-item
                :prop="'categoryList.' + scope.$index + '.rate'"
                :rules="rules.rate"
              >
                <!-- 请输入 -->
                <el-input
                  v-model="scope.row.rate"
                  :placeholder="$t('common.pleaseInput')"
                  type="number"
                />
              </el-form-item>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <CPagination
        :total="form.total"
        :page-num="form.pageNum"
        :page-size="form.pageSize"
        @current-change="parentDataCurrentChange"
        @size-change="parentDataSizeChange"
      />
      <div slot="footer">
        <el-button
          @click="$emit('input', false)"
        >
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="confirm"
        >
          {{
            $t("common.submit")
          }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 品类选择 -->
    <srm-dialog
      :visible.sync="showCatDialog"
      :title="$t('dataConfMod.categorySelect')"
      size="middle"
    >
      <div style="height: 360px;overflow: auto;">
        <Treeselect
          v-model="categorys"
          :normalizer="catNormalizer"
          :no-children-text="$t('vendorMod.noChildrenText')"
          :placeholder="$t('dataConfMod.msgPurCategory')"
          :append-to-body="false"
          multiple
          flatten-search-results
          value-format="object"
          value-consists-of="LEAF_PRIORITY"
          :searchable="true"
          flat
          :options="options"
          :auto-load-root-options="false"
          :always-open="true"
          auto-select-descendants
          auto-deselect-descendants
        />
      </div>
      <div slot="footer">
        <el-button @click="showCatDialog = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="addOneCat"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import CPagination from 'lib@/components/c-pagination'
import Treeselect, { LOAD_CHILDREN_OPTIONS, LOAD_ROOT_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { reportSetting, purchaseCategoryMaintenance } from 'modb@/basicSetting/api/basicSetting'

function setTreeList (data) {
  let map = {}
  let val = []
  // 生成数据对象集合
  data.forEach(it => {
    map[it.categoryId] = it // code为每个节点的id
  })
  // 生成结果集
  data.forEach(it => {
    const parent = map[it.parentId] // parent为父节点的id
    if (parent) {
      if (!Array.isArray(parent.children)) parent.children = []
      parent.children.push(it)
    } else {
      val.push(it)
    }
  })
  return val
}
const findMenuInfoByPath = (leafId, nodes) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = nodes[i]
    if (leafId == nodes[i].categoryId) {
      return tmpObj
    }
    if (nodes[i].children) {
      const findResult = findMenuInfoByPath(leafId, nodes[i].children, tmpObj)
      if (findResult) {
        return findResult
      }
    }
  }
}
export default {
  components: {
    CPagination,
    Treeselect
  },
  props: {
    value: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: null
    }
  },
  data () {
    return {
      showCatDialog: false,
      categorys: [],
      options: [],
      selectDelList: [],
      categoryTreeOptions: null,
      rules: {
        categoryNames: [{ required: true }],
        rate: [{ required: true }]
      }
    }
  },
  watch: {},
  async mounted () {
    const { data } = await reportSetting.getCatChildrenAllData({})
    this.options = setTreeList(data)
  },
  methods: {
    close () {
      this.$emit('input', false)
    },
    confirm () {
      this.$refs.searchForm.validate(valid1 => {
        if (valid1) {
          this.$emit('input', false)
          this.$emit('success', this.form.categoryList)
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    parentDataCurrentChange (num) {
      this.form.pageNum = num
      this.$emit('parentDataCurrentChange', this.form)
    },
    parentDataSizeChange (size) {
      this.form.pageNum = 1
      this.form.pageSize = size
    },
    delSelect () {
      if (!this.selectDelList.length) {
        this.$message({
          message: this.$t('common.msgSelectDelData'), // 请勾选要删除的数据
          type: 'error'
        })
        return
      }
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.form.categoryList.forEach(item => {
            if (item.setCategoryId) {
              if (this.selectDelList.indexOf(item.setCategoryId) < 0) {
                arr.push(item)
              }
            }
            if (item.id) {
              if (this.selectDelList.indexOf(item.id) < 0) {
                arr.push(item)
              }
            }
          })
          this.form.categoryList = arr
        })
    },
    handleSelectionChange (val) {
      this.selectDelList = val.map(i => i.id || i.setCategoryId)
    },
    catNormalizer (node) {
      return {
        id: node.categoryId, // id
        label: node.categoryName, // label
        children: node.children
      }
    },
    // 品类加载
    catLoadOptions ({ action, parentNode, callback }) {
      if (action === LOAD_ROOT_OPTIONS) {
        // 加载跟节点
        purchaseCategoryMaintenance.getCatChildrenData({ categoryId: -1 })
          .then(response => {
            this.categoryTreeOptions = response.data.map(i => ({
              ...i,
              children: null
            }))
            callback()
          })
          .catch(err => {
            callback(new Error(err.message))
          })
      } else if (action === LOAD_CHILDREN_OPTIONS) {
        purchaseCategoryMaintenance.getCatChildrenData({ categoryId: parentNode.categoryId })
          .then(res => {
            parentNode.children = res.data.map(i => ({ ...i, children: null }))
            callback()
          })
          .catch(err => {
            parentNode.children = null
            callback(new Error(err.message))
          })
      }
    },
    addOneCat () {
      let categoryNames = []
      let categoryIds = []
      this.categorys.forEach(i => {
        categoryNames.push(i.categoryName)
        categoryIds.push(i.categoryId)
      })
      let categoryIdsList = []
      this.form.categoryList.forEach((item, index) => {
        if (this.showIndex != index) {
          if (item.categoryIds) {
            categoryIdsList = [
              ...categoryIdsList,
              ...item.categoryIds.split(',')
            ]
          }
        }
      })
      if (categoryIdsList.some(i => categoryIds.join(',').indexOf(i) > -1)) {
        this.$message({
          message: this.$t('dataConfMod.msgNotRepeatCate'), // 添加品类不能重复
          type: 'error'
        })
        return
      }
      this.form.categoryList[this.showIndex].categoryNames = categoryNames.join(
        ','
      )
      this.form.categoryList[this.showIndex].categoryIds = categoryIds.join(
        ','
      )
      this.showCatDialog = false
    },
    selectCat (row, index) {
      this.showCatDialog = true
      this.showIndex = index
      this.categorys = []
      if (
        this.form.categoryList.length &&
        this.form.categoryList[this.showIndex].categoryIds
      ) {
        let categoryIds = this.form.categoryList[this.showIndex].categoryIds.split(',')
        this.categorys = categoryIds
          .map(item => {
            return findMenuInfoByPath(item, this.options)
          })
          .filter(i => !!i)
      }
    },
    addCategory () {
      this.form.categoryList.push({
        id: Math.floor(Math.random() * 1000000),
        setId: this.form.setId,
        categoryNames: null,
        rate: null
      })
    }
  }
}
</script>

<style scoped lang="scss">
.input-with-select {
  :deep(.el-input-group__append),
  :deep(.el-input-group__prepend) {
    width: 60px;
    color: #46a6ff;
    text-align: center;
  }
  :deep(.el-input__suffix) {
    right: 25px;
  }
}
</style>
