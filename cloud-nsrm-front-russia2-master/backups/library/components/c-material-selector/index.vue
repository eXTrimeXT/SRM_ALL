<template>
  <div class="materialWap">
    <el-popover
      v-model="popVisible"
      width="400"
      trigger="manual"
    >
      <el-input
        slot="reference"
        v-model="inputVal"
        suffix-icon="el-icon-search"
        @focus="focusHandle"
      />
      <div class="material-select">
        <div class="material-wrap">
          <el-table
            ref="parentOrgTable"
            border
            :data="materialData"
            tooltip-effect="dark"
            style="width: 100%"
            height="180px"
            @selection-change="handleSelectionChange"
          >
            <el-table-column
              type="selection"
              width="55"
            />
            <!-- 物料编码 -->
            <el-table-column
              prop="materialCode"
              :label="$t('common.materialCode')"
            />
            <!-- 物料名称 -->
            <el-table-column
              prop="materialName"
              :label="$t('common.materialName')"
            />
            <!-- 品类名称 -->
            <el-table-column
              prop="categoryName"
              :label="$t('common.categoryName')"
            />
          </el-table>
          <!-- <c-pagination
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            layout="prev, pager, next"
            @current-change="currentChange"
            @size-change="sizeChange"
          /> -->
        </div>
        <div class="comfirm-dev">
          <el-button @click="cancelHandle">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSelect"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import CPagination from 'lib@/components/c-pagination'
import { getMaterialItemByParam } from 'mod@/basicSetting/api/baseSetting'

export default {
  name: 'CMaterialSelector',
  components: {
    CPagination
  },
  props: {
    keyName: {
      type: String,
      default () {
        return ''
      }
    },
    keyRule: {
      type: Object,
      default () {
        return {}
      }
    },
    parame: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      orgLoading: false,
      popVisible: false,
      inputVal: '',
      materialData: [],
      materiaSelection: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    data () {
      return this.materialData
    }
  },
  methods: {
    fatchMtData () {
      let parameObj = Object.assign({}, this.pageInfo, this.parame)
      getMaterialItemByParam(parameObj).then(res => {
        if (res.data) {
          // && res.data.list
          this.materialData = res.data
          // this.pageInfo.total = res.data.total
          // this.pageInfo.pageNum = res.data.pageNum
          // this.pageInfo.pageSize = res.data.pageSize
        }
      })
    },
    currentChange (num) {
      this.pageInfo.pageNum = num
      this.fatchMtData()
    },
    sizeChange (size) {
      this.pageInfo.pageSize = size
      this.fatchMtData()
    },
    handleSelectionChange (selection) {
      this.materiaSelection = selection
    },
    // input框获取焦点
    focusHandle () {
      this.popVisible = true
      this.$emit('input-focus')
      this.fatchMtData() // 查询数据
    },
    // 确认选择
    comfirmSelect () {
      let curNode = this.materiaSelection[0]
      this.popVisible = false
      this.inputVal = curNode.materialName
      this.$emit('comfirm', curNode)
    },
    cancelHandle () {
      this.popVisible = false
    }
  }
}
</script>

<style lang="scss">
.material-select {
  padding-bottom: 35px;
  position: relative;
  .material-wrap {
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
  }
  .c-pagination {
    margin-top: 15px;
  }
}
.comfirm-dev {
  padding-top: 10px;
  text-align: right;
  position: absolute;
  width: 100%;
  bottom: 0;
  left: 0;
}
</style>
