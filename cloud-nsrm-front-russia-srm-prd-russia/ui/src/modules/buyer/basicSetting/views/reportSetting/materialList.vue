<template>
  <div class="form-fill-style">
    <!-- 采购物料去年冻结单价设置 -->
    <srm-dialog
      :visible.sync="value"
      :title="$t('dataConfMod.lastYearFrozenPriceSetting')"
      size="large"
      :close-on-click-modal="false"
      @close="close"
    >
      <main-header>
        <template slot="right">
          <el-button

            @click="$emit('input', false)"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"

            @click="confirm"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </main-header>
      <el-table
        ref="formTable"
        stripe
        border
        height="355"
        :data="materialList"
        @selection-change="handleSelectionChange"
      >
        <!-- 物料名称 -->
        <el-table-column
          prop="materialName"
          align="center"
          :label="$t('common.materialName')"
          show-overflow-tooltip
        >
          <template slot="header">
            <div style="display: inline-block">
              {{ $t("common.materialName") }}
            </div>
            <div style="display: block;padding-bottom: 5px;">
              <el-input
                v-model="params.materialName"
                clearable
                @keyup.native.enter="search('enter')"
              />
            </div>
          </template>
        </el-table-column>
        <!-- 物料编码 -->
        <el-table-column
          prop="materialCode"
          align="center"
          :label="$t('common.materialCode')"
          show-overflow-tooltip
        >
          <template slot="header">
            <div style="display: inline-block">
              {{ $t("common.materialCode") }}
            </div>
            <div style="display: block;padding-bottom: 5px;">
              <el-input
                v-model="params.materialCode"
                clearable
                @keyup.native.enter="search('enter')"
              />
            </div>
          </template>
        </el-table-column>
        <!-- 去年冻结单价（元） -->
        <el-table-column
          prop="price"
          align="center"
          :label="$t('dataConfMod.forzenUnitPriceLastY')"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.price"
              :placeholder="$t('common.pleaseInput')"
              type="number"
            />
          </template>
        </el-table-column>
      </el-table>
      <el-footer class="page-bar">
        <c-pagination
          ref="pager"
          style="margin: 10px 0px 0;"
          class="c-query-table-pagination"
          :total="queryTotal"
          :page-num="viewIndex"
          :page-size="viewSize"
          @current-change="changeCurrentIndex"
          @size-change="changeCurrentSize"
        />
      </el-footer>
    </srm-dialog>
  </div>
</template>

<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
export default {
  components: {
    MainHeader,
    CPagination
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
      dataCount: 0,
      queryTotal: -1, // 共几条 -1就是问号
      viewIndex: 1,
      viewSize: 10,
      rules: {
        materialNames: [{ required: true }]
      },
      selectDelList: [],
      params: {
        materialName: null,
        materialCode: null
      },
      paramData: {
        pageNum: 1,
        pageSize: 10,
        params: JSON.stringify({
          query: JSON.stringify({}),
          extendQuery: JSON.stringify({
            _quickKey: 'scc_base_material_item',
            entityId: 10
          })
        })
      },
      materialList: []
    }
  },
  watch: {
    form: {
      // 监听列表
      deep: true,
      immediate: true,
      handler (newVal) {
        if (newVal.materialList.length) {
          newVal.materialList.forEach(i => {
            this.materialList.map(j => {
              if (i.materialIds == j.materialId) {
                j.price = i.price
                j.setMaterialId = i.setMaterialId
              }
            })
          })
        } else {
          this.materialList.map(j => {
            j.price = null
            j.setMaterialId = null
          })
        }
      }
    }
  },
  mounted () {
    this.search()
  },
  methods: {
    pageQuery () {
      this.$emit('getCrSetMaterial', { setId: this.form.setId })
      let obj = {}
      if (this.params.materialCode) {
        obj = Object.assign({}, obj, {
          't.MATERIAL_CODE': this.params.materialCode
        })
      }
      if (this.params.materialName) {
        obj = Object.assign({}, obj, {
          't.MATERIAL_NAME': this.params.materialName
        })
      }
      let paramData = {
        pageNum: this.paramData.pageNum,
        pageSize: this.paramData.pageSize,
        params: JSON.stringify({
          query: JSON.stringify(obj),
          extendQuery: JSON.stringify({
            _quickKey: 'scc_base_material_item',
            entityId: 10
          })
        })
      }
      let successList = []
      this.materialList.map(i => {
        if (i.price) {
          successList.push({
            setId: this.form.setId,
            price: i.price,
            materialIds: i.materialId,
            materialNames: i.materialName,
            setMaterialId: i.setMaterialId || null
          })
        }
      })
      this.$emit('success', successList)
      this.query(paramData)
    },
    search (type) {
      let obj = {}
      if (this.params.materialCode) {
        obj = Object.assign({}, obj, {
          't.MATERIAL_CODE': this.params.materialCode
        })
      }
      if (this.params.materialName) {
        obj = Object.assign({}, obj, {
          't.MATERIAL_NAME': this.params.materialName
        })
      }
      let paramData = {
        pageNum: 1,
        pageSize: 10,
        params: JSON.stringify({
          query: JSON.stringify(obj),
          extendQuery: JSON.stringify({
            _quickKey: 'scc_base_material_item',
            entityId: 10
          })
        })
      }
      if ((type == 'enter')) {
        this.query(paramData)
      } else {
        this.query(this.paramData)
      }
    },
    query (params) {
      this.$http({
        // 获取数据---展示在table页面上的数据
        url: '/api-base/quicksearch/quicksearchConfig/listByFormCondition',
        method: 'POST',
        data: params
      })
        .then(data => {
          let list = data.data.data
          list.map(i => {
            i.price = null
          })
          this.materialList = list
          this.dataCount = this.materialList.length
          this.queryTotal = data.data.totalCount
          if (this.form.materialList && this.form.materialList.length) {
            this.form.materialList.forEach(i => {
              this.materialList.map(j => {
                if (i.materialIds == j.materialId) {
                  j.price = i.price
                  j.setMaterialId = i.setMaterialId
                }
              })
            })
          } else {
            this.materialList.map(j => {
              j.price = null
              j.setMaterialId = null
            })
          }
        })
    },
    close () {
      this.$emit('input', false)
    },
    confirm () {
      let successList = []
      this.materialList.map(i => {
        if (i.price) {
          successList.push({
            setId: this.form.setId,
            price: i.price,
            materialIds: i.materialId,
            materialNames: i.materialName,
            setMaterialId: i.setMaterialId || null
          })
        }
      })
      this.$emit('input', false)
      this.$emit('success', successList)
    },
    handleSelectionChange (val) {
      this.selectDelList = val.map(i => i.id || i.setMaterialId)
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.paramData.pageNum = currentNum
      this.pageQuery()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.paramData.pageSize = currentSize
      this.pageQuery()
    }
  }
}
</script>
