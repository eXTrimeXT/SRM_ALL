<template>
  <el-container class="box">
    <!--左侧树形组织结构-->
    <el-aside class="left">
      <el-scrollbar style="height: 100%">
        <el-tree
          ref="tree"
          v-loading="orgLoading"
          element-loading-background="rgba(0, 0, 0, 0.4)"
          lazy
          node-key="id"
          :expand-on-click-node="false"
          :data="catTreeData"
          :props="catTreeProps"
          :load="loadNode"
          @node-click="nodeClick"
        />
      </el-scrollbar>
      <div
        class="resize"
        :title="$t('dataConfMod.shrinkSidebar')"
      >
        ⋮
      </div>
    </el-aside>
    <el-main
      class="mid"
      style="
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        position: relative;
        height: 100%;
        padding-left:16px;
      "
    >
      <FormWrapper
        ref="formWrapper"
        :form-array="queryForm"
        :p-form-data.sync="PFormData"
        :col-length="colSpan"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 自定义导出 -->
          <ExportExcel
            page-url="/api-base/pj/category/listPageByParm"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
            code="base:purchaseCategoryMaintenance:export"
          />
          <!-- 批量是否招标范围 -->
          <AuthorityButton
            type="primary"
            code="base:purchaseCategoryMaintenance:ifBid"
            :disabled="currentRows.length == 0"
            @click="editBatchOpen('ifBid')"
          >
            {{ $t('cusEntry.supplement20250314.batchTenderRange') }}
          </AuthorityButton>
          <!-- 批量是否俄罗斯品类 -->
          <AuthorityButton
            type="primary"
            code="base:purchaseCategoryMaintenance:ifRussianCategory"
            :disabled="currentRows.length == 0"
            @click="editBatchOpen('ifRussianCategory')"
          >
            {{ $t('cusEntry.supplement20250314.batchIsRussianCategory') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :checkbox="true"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        url="/api-base/pj/category/listPageByParm"
      />
      <!-- 批量维护 - 弹窗 -->
      <srm-dialog :title="editBatchDialog.title" :visible.sync="editBatchDialog.visible" :close-on-click-modal="false" size="small">
        <div>
          <DictSelect
            v-if="editBatchDialog.type === 'dict'"
            v-model="editBatchDialog.value"
            :code="editBatchDialog.code"
          />
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="editBatchDialog.visible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="updateDataBatch">{{ $t('common.confirm') }}</el-button>
        </div>
      </srm-dialog>
      <!-- 编辑俄文信息-->
      <srm-dialog
        :title="$t('cusEntry.dataConfMod.editRuInfo')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-form
          ref="orgform"
          :model="catModel.catForm"
          :rules="catModel.rules"
          label-position="top"
        >
          <srm-row>
            <srm-col :init-col="2">
              <!-- 品类名称俄文 -->
              <el-form-item
                :label="$t('cusEntry.dataConfMod.categoryName')"
                prop="categoryName"
              >
                <el-input v-model="catModel.catForm.categoryName" />
              </el-form-item>
              <!-- 对应的俄罗斯品类 -->
              <el-form-item
                :label="$t('cusEntry.supplement20250218.correspondingRussianCategory')"
                prop="russianCategoryName"
              >
                <el-input v-model="catModel.catForm.russianCategoryName" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            <!-- 取 消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="saveOrUpdateOrgHandle"
          >
            <!-- 确 定 -->
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import ExportExcel from 'lib@/components/export-excel'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { purchaseCategoryMaintenance } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'Rate',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    ExportExcel
  },
  data () {
    return {
      dictCodes: {
        mainMaterial: 'MAIN_MATERIAL',
        enabled: 'YES_OR_NO',
        ceeaEnableSynErp: 'YES_OR_NO', // 是否需同步ERP
        ceeaIfDeliverPlan: 'YES_OR_NO', // 是否用于执行到货计划
        ceeaIfBeyondDeliver: 'YES_OR_NO', // 是否允许超计划发货
        lastLevelFlag: 'YES_OR_NO', // 是否末级
        supplierCountLimitFlag: 'YES_OR_NO', // 是否强控品类供应商上限
        ifBid: 'YES_OR_NO', // 是否招标范围
        ifRussianCategory: 'YES_OR_NO' // 是否招标范围
      },
      colSpan: 2,
      pageSize: 15,
      PFormData: {},
      gridId: 'OrgList',
      queryParam: {},
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      mainMaterialList: [],
      tableData: [],
      currentRows: [],
      catModel: {
        catForm: {
          categoryName: '',       // 品类名称
          russianCategoryName: '' // 俄罗斯品类
        },
        rules: {
          // categoryName: [{ required: true, message: this.$t('common.pleaseInput') }], // '请输入'
          // russianCategoryName: [{ required: true, message: this.$t('common.pleaseInput') }] // '请输入'
        }
      },
      dialogFormVisible: false,
      firstLoad: true,
      departmentLoading: false,
      orgLoading: false,
      // 品类树数据
      catTreeData: [
        {
          childrens: [],
          categoryName: ''
        }
      ],
      // 品类树配置选项
      catTreeProps: {
        children: 'childrens',
        label: 'categoryName',
        isLeaf: data => {
          return data.isLeaf
        }
      },
      editBatchDialog: {
        visible: false,
        title: '批量维护',
        type: '',
        code: '',
        field: '',
        value: '',
        otherField: {}
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'categoryName',
        label: this.$t('common.categoryName') // 品类名称
      },
      {
        prop: 'categoryNameChn',
        label: this.$t('common.categoryName') + this.$t('cusEntry.dataConfMod.langSignZh') // '品类名称（中文）'
      },
      {
        prop: 'categoryCode',
        label: this.$t('common.categoryCode') // '品类编码'
      },
      {
        prop: 'parentName',
        label: this.$t('dataConfMod.parentCategory') // '父品类名称'
      },
      {
        prop: 'parentNameChn',
        label: this.$t('dataConfMod.parentCategory') + this.$t('cusEntry.dataConfMod.langSignZh') // '父品类名称（中文）'
      },
      {
        prop: 'parentCode',
        label: this.$t('dataConfMod.parentCategoryCode') // '父品类编码'
      },
      {
        prop: 'ifRussianCategory',
        label: this.$t('cusEntry.dataConfMod.isRuCate'), // 是否俄罗斯品类
        type: 'dict',
        code: 'YES_OR_NO'
      }
    ]
    this.tableHeader = [
      {
        prop: 'categoryName',
        label: () => this.$t('common.categoryName'), // '品类名称'
        minWidth: '160'
      },
      {
        prop: 'categoryNameChn',
        label: () => this.$t('common.categoryName') + this.$t('cusEntry.dataConfMod.langSignZh'), // '品类名称（中文）'
        minWidth: '160'
      },
      {
        prop: 'categoryCode',
        label: () => this.$t('common.categoryCode'), // '品类编码'
        minWidth: '120'
      },
      {
        prop: 'russianCategoryName',
        label: () => this.$t('cusEntry.supplement20250218.russianCategory'), // '俄罗斯品类'
        minWidth: '120'
      },
      {
        prop: 'parentName',
        label: () => this.$t('dataConfMod.parentCategory'), // '父品类名称'
        minWidth: '150'
      },
      {
        prop: 'parentNameChn',
        label: () => this.$t('dataConfMod.parentCategory') + this.$t('cusEntry.dataConfMod.langSignZh'), // '父品类名称（中文）'
        minWidth: '150'
      },
      {
        prop: 'parentCode',
        label: () => this.$t('dataConfMod.parentCategoryCode'), // '父品类编码'
        minWidth: '120'
      },
      {
        prop: 'categoryFullName',
        label: () => this.$t('dataConfMod.categoryFullName'), // '品类全称'
        minWidth: '150'
      },
      {
        prop: 'categoryFullNameChn',
        label: () => this.$t('dataConfMod.categoryFullName') + this.$t('cusEntry.dataConfMod.langSignZh'), // '品类全称（中文）'
        minWidth: '160'
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        width: '100',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ifBid',
        label: () => this.$t('cusEntry.dataConfMod.ifBid'), // 是否招标范围
        width: '120',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ifRussianCategory',
        label: () => this.$t('cusEntry.dataConfMod.isRuCate'), // 是否俄罗斯品类
        width: '130',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // '更新时间'
        minWidth: '140',
        dataType: 'dateTime'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople'), // '更新人'
        minWidth: '120'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: this.$store.getters.language == 'zh_CN' ? '120' : '180',
        fixed: 'right',
        editType: 'none',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editOrgData(row),
            code: 'base:purchaseCategoryMaintenance:edit',
            formattor: () => {
              if (this.$store.getters.language == 'zh_CN') {
                return this.$t('cusEntry.dataConfMod.editRuInfo') // '编辑俄文信息'
              } else {
                return `<span style="white-space: pre-wrap;">${this.$t('cusEntry.dataConfMod.editRuInfo')}</span>`
              }
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.dragControllerDiv()
    // 即将进行【导入采购分类】，您需要完成：1、导入企业管理的采购分类；2、按采购分类层级关系分步骤导入；
    let purCatTip = localStorage.getItem('purCatTip') || 'Y'
    if (purCatTip === 'Y') {
      this.$confirm(
        this.$t('dataConfMod.purchaseCategoryMaintenanceAlert'),
        this.$t('common.tips'),
        {
          distinguishCancelAndClose: true,
          confirmButtonText: this.$t('common.start'),
          cancelButtonText: this.$t('common.toNotshowTip')
        }
      )
        .then(() => {
          // 点击开始
        })
        .catch(() => {
          // 不再提示
          localStorage.setItem('purCatTip', 'N')
        })
    }
  },
  methods: {
    // 左右区域 - 拖拽改变宽度
    dragControllerDiv () {
      let resize = document.getElementsByClassName('resize')
      let left = document.getElementsByClassName('left')
      let mid = document.getElementsByClassName('mid')
      let box = document.getElementsByClassName('box')
      for (let i = 0; i < resize.length; i++) {
        // 鼠标按下事件
        resize[i].onmousedown = function (e) {
          // 颜色改变提醒
          resize[i].style.background = '#818181'
          let startX = e.clientX
          resize[i].left = resize[i].offsetLeft
          // 鼠标拖动事件
          document.onmousemove = function (e) {
            let endX = e.clientX
            let moveLen = resize[i].left + (endX - startX) // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
            let maxT = box[i].clientWidth - resize[i].offsetWidth // 容器宽度 - 左边区域的宽度 = 右边区域的宽度

            if (moveLen < 32) moveLen = 32 // 左边区域的最小宽度为32px
            if (moveLen > maxT - 150) moveLen = maxT - 150 // 右边区域最小宽度为150px

            resize[i].style.left = moveLen // 设置左侧区域的宽度

            for (let j = 0; j < left.length; j++) {
              left[j].style.width = moveLen + 'px'
              mid[j].style.width = box[i].clientWidth - moveLen - 10 + 'px'
            }
          }
          // 鼠标松开事件
          document.onmouseup = function () {
            // 颜色恢复
            resize[i].style.background = '#d6d6d6'
            document.onmousemove = null
            document.onmouseup = null
            resize[i].releaseCapture && resize[i].releaseCapture() // 当你不在需要继续获得鼠标消息就要应该调用ReleaseCapture()释放掉
          }
          resize[i].setCapture && resize[i].setCapture() // 该函数在属于当前线程的指定窗口里设置鼠标捕获
          return false
        }
      }
    },
    nodeClick (data) {
      const { categoryNameChn } = data || {}
      if (this.PFormData.parentNameChn !== categoryNameChn) {
        this.getQuerydata({ parentNameChn: categoryNameChn })
        this.$refs.formWrapper.setValue('parentNameChn', categoryNameChn)
      }
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.currentRows = []
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (node.level === 0) {
        let queryParma = { categoryId: -1 }
        this.getDepartmentTree(queryParma, resolve) // 查询一级节点
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        let nodeParme = {}
        nodeParme.categoryId = node.data.categoryId
        this.getDepartmentTree(nodeParme, resolve)
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      if (this.firstLoad) {
        this.departmentLoading = true
      }
      purchaseCategoryMaintenance.getCatChildrenData(parmes)
        .then(response => {
          if (response && response.data) {
            // 懒加载自定义叶子节点 设置 isLeaf = true 即可去除左侧三角形
            let resData = []
            response.data.map(item => {
              if (item.lastLevelFlag == 'Y') {
                item.isLeaf = true
              }
              resData.push(item)
            })
            resolve(resData)
          } else {
            // '数据获取失败：'
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg,
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
          this.departmentLoading = false
        })
    },
    // 加载一级节点
    loasFirstNode () {
      this.catTreeData = []
      let queryParma = { categoryId: -1 }
      purchaseCategoryMaintenance.getCatChildrenData(queryParma)
        .then(response => {
          if (response && response.data) {
            let resData = response.data
            resData.forEach((item, index) => {
              this.catTreeData.push(item)
              this.catTreeData[index].childrens = []
            })
          } else {
            // '数据获取失败：'
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg,
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
          this.departmentLoading = false
        })
    },

    // 编辑品类数据
    editOrgData (row) {
      this.catModel.catForm = { ...row }
      this.dialogFormVisible = true
    },
    // 新增编辑品类数据
    saveOrUpdateOrgHandle () {
      this.$refs.orgform.validate(val => {
        if (!val) {
          this.$message.warning(this.$t('common.pleasefinishRequired'))
          return false
        }
        let subArr = [this.catModel.catForm]
        purchaseCategoryMaintenance.saveOrUpdateCat(subArr).then(res => {
          if (res) {
            // 返回数据处理
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata() // 重新查询数据
            this.loasFirstNode() // 重新查询树形一级数据
            this.dialogFormVisible = false
          }
        })
      })
    },
    getCategory (getData) {
      return this.$http({
        url: '/api-base/purchase/category-purchase-type/listPurchaseTypes',
        method: 'GET',
        params: getData,
        loading: true
      })
    },
    // 批量维护--打开维护弹窗
    editBatchOpen (val) {
      let obj = {
        ifBid: {
          type: 'dict',
          title: this.$t('cusEntry.dataConfMod.ifBid'),
          code: 'YES_OR_NO'
        },
        ifRussianCategory: {
          type: 'dict',
          title: this.$t('cusEntry.dataConfMod.isRuCate'),
          code: 'YES_OR_NO'
        }
      }
      this.editBatchDialog.type = obj[val].type
      this.editBatchDialog.code = obj[val].code
      this.editBatchDialog.title = obj[val].title
      this.editBatchDialog.field = val
      this.editBatchDialog.value = ''
      this.editBatchDialog.visible = true
    },
    // 批量维护--确认
    updateDataBatch () {
      if (!this.editBatchDialog.value) {
        this.$message.warning('请填写需要维护的内容！')
        return
      }
      let submitData = this.currentRows.map(item => {
        let obj = { categoryId: item.categoryId }
        obj[this.editBatchDialog.field] = this.editBatchDialog.value
        let otherField = Object.keys(this.editBatchDialog.otherField)
        if (otherField.length > 0) {
          otherField.forEach(key => {
            obj[key] = this.editBatchDialog.otherField[key]
          })
        }
        return obj
      })
      this.$http({
        url: '/api-base/pj/category/updateCategoryList',
        method: 'POST',
        data: submitData,
        loading: true
      }).then(res => {
        this.editBatchDialog.visible = false
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    }
  }
}
</script>

<style scoped lang="scss">
/* 拖拽相关样式 */
/*包围div样式*/
.box {
  width: 100%;
  height: 100%;
  overflow: hidden;
  //  box-shadow: -1px 9px 10px 3px rgba(0, 0, 0, 0.11);
  ::-webkit-scrollbar {
    width: 7px !important;
  }
}
/*左侧div样式*/
.left {
  width: calc(20% - 10px); /*左侧初始化宽度*/
  height: 100%;
  background: #ffffff;
  position: relative;
  overflow: hidden;
  padding-left: 0;
  padding-right: 7px !important;
}
.left .el-tree {
  overflow-x: hidden;
}
/*拖拽区div样式*/
.resize {
  cursor: col-resize;
  position: absolute;
  top: 36%;
  right: 0px;
  background-color: #d6d6d6;
  border-radius: 5px;
  margin-top: -10px;
  width: 8px;
  height: 42px;
  background-size: cover;
  background-position: center;
  /*z-index: 99999;*/
  font-size: 30px;
  color: white;
}
/*拖拽区鼠标悬停样式*/
.resize:hover {
  color: #444444;
}
/*右侧div'样式*/
.mid {
  width: calc(80%); /*右侧初始化宽度*/
  height: 100%;
  overflow: hidden;
  background: #fff;
  box-shadow: -1px 4px 5px 3px rgba(0, 0, 0, 0.11);
}
.common-line{
  font-size: 12px;
  line-height: 20px;
  margin-bottom: 15px;
}
</style>
