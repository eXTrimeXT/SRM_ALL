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
          <AuthorityButton
            type="primary"
            code="base:purchaseCategoryMaintenance:add"
            @click="orgControlHandle('add')"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </AuthorityButton>
          <MImport
            ref="import"
            code="base:purchaseCategoryMaintenance:import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!-- 自定义导出 -->
          <ExportExcel
            page-url="/api-base/purchase/purchaseCategory/listPageByParm"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
            code="base:purchaseCategoryMaintenance:export"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        url="/api-base/purchase/purchaseCategory/listPageByParm"
      />
      <!-- 新增 编辑弹框区域-->
      <srm-dialog
        :title="dialogTitle"
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
              <!-- 品类编码 -->
              <el-form-item
                :label="$t('common.categoryCode')"
                prop="categoryCode"
              >
                <el-input
                  v-model="catModel.catForm.categoryCode"
                  :disabled="curOpt === 'edit'"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 品类名称 -->
              <el-form-item
                :label="$t('common.categoryName')"
                prop="categoryName"
              >
                <el-input v-model="catModel.catForm.categoryName" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 父级品类 -->
              <el-form-item
                :label="$t('dataConfMod.parentCategory')"
                prop="parentId"
              >
                <el-select
                  v-model="catModel.catForm.parentId"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="item in parentCatList"
                    :key="item.id"
                    :label="item.label"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 主材主设备 -->
              <el-form-item :label="$t('dataConfMod.mainMaterial')">
                <DictSelect
                  v-model="catModel.catForm.mainMaterial"
                  code="MAIN_MATERIAL"
                  clearable
                  filterable
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 锁定周期 -->
              <el-form-item
                :label="$t('dataConfMod.ceeaLockPeriod')"
                prop="ceeaLockPeriod"
              >
                <el-input
                  v-model="catModel.catForm.ceeaLockPeriod"
                  v-input-format="{ type: 'number' }"
                  oninput="if(value<0)value=0"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否启用 -->
              <el-form-item
                :label="$t('dataConfMod.enabledUse')"
                prop="enabled"
              >
                <el-checkbox
                  v-model="catModel.catForm.enabled"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否强控品类供应商上限 -->
              <el-form-item
                :label="$t('dataConfMod.supplierCountLimitFlag')"
                prop="supplierCountLimitFlag"
              >
                <el-checkbox
                  v-model="catModel.catForm.supplierCountLimitFlag"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 品类供应商上限 -->
              <el-form-item
                :label="$t('dataConfMod.supplierCountLimit')"
                prop="supplierCountLimit"
              >
                <el-input
                  v-model="catModel.catForm.supplierCountLimit"
                  v-input-format="{ type: 'number' }"
                  oninput="if(value<0)value=0"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否需要同步ERP -->
              <el-form-item
                :label="$t('dataConfMod.ceeaEnableSynErp')"
                prop="ceeaEnableSynErp"
              >
                <el-checkbox
                  v-model="catModel.catForm.ceeaEnableSynErp"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否用于执行到货计划 -->
              <el-form-item
                :label="$t('dataConfMod.ceeaIfDeliverPlan')"
                prop="ceeaIfDeliverPlan"
              >
                <el-checkbox
                  v-model="catModel.catForm.ceeaIfDeliverPlan"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否允许超计划发货 -->
              <el-form-item
                :label="$t('dataConfMod.ceeaIfBeyondDeliver')"
                prop="ceeaIfBeyondDeliver"
              >
                <el-checkbox
                  v-model="catModel.catForm.ceeaIfBeyondDeliver"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否启用送货预约 -->
              <el-form-item
                :label="$t('dataConfMod.deliveryBookingEnable')"
              >
                <el-checkbox
                  v-model="catModel.catForm.deliverySubscribeFlag"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否启用送货单 -->
              <el-form-item
                :label="$t('dataConfMod.deliveryNoteEnable')"
              >
                <el-checkbox
                  v-model="catModel.catForm.deliveryOrderFlag"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 是否末级 -->
              <el-form-item
                :label="$t('dataConfMod.lastLevelFlag')"
              >
                <el-checkbox
                  v-model="catModel.catForm.lastLevelFlag"
                  true-label="Y"
                  false-label="N"
                />
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
            @click="comfirmSave"
          >
            <!-- 确 定 -->
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>

      <!-- 维护采购类型 -->
      <srm-dialog
        :title="maintenanceName"
        :visible.sync="maintenanceDialog"
        :close-on-click-modal="false"
        size="middle"
      >
        <srm-row>
          <srm-col :init-col="2">
            <div class="common-line">
              <span>{{ $t('common.categoryName') + '：' }}</span>
              <span>{{ headMainten.categoryName }}</span>
            </div>
            <div class="common-line">
              <span>{{ $t('common.categoryCode') + '：' }}</span>
              <span>{{ headMainten.categoryCode }}</span>
            </div>
            <div class="common-line">
              <span>{{ $t('dataConfMod.parentCategory') + '：' }}</span>
              <span>{{ headMainten.parentName }}</span>
            </div>
            <div class="common-line">
              <span>{{ $t('dataConfMod.parentCategoryCode') + '：' }}</span>
              <span>{{ headMainten.parentCode }}</span>
            </div>
          </srm-col>
          <srm-col :init-col="2">
            <el-table
              ref="multipleTable"
              :data="assessmentList"
              tooltip-effect="dark"
              @selection-change="maintenanceChange"
            >
              <el-table-column type="selection" />
              <!-- 采购类型 -->
              <el-table-column :label="$t('bid_mod.purchaseType')">
                <template slot-scope="scope">
                  {{ scope.row.label }}
                </template>
              </el-table-column>
            </el-table>
          </srm-col>
        </srm-row>

        <div slot="footer" class="boxBottom">
          <el-button @click="maintenanceDialog = false">
            <!-- 取 消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="maintenanceSave"
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
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { purchaseCategoryMaintenance } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'Rate',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    MImport,
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
        deliveryOrderFlag: 'YES_OR_NO', // 是否启用送货单
        deliverySubscribeFlag: 'YES_OR_NO', // 是否启用送货预约
        lastLevelFlag: 'YES_OR_NO', // 是否末级
        supplierCountLimitFlag: 'YES_OR_NO' // 是否强控品类供应商上限
      },
      maintenanceName: this.$t('common.maintenancePurchase'),
      maintenance: [],
      colSpan: 2,
      pageSize: 15,
      PFormData: {},
      gridId: 'OrgList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      parentCatList: [], // 品类
      mainMaterialList: [],
      tableData: [],
      headMainten: {},
      curOpt: 'add',
      dialogTitle: this.$t('dataConfMod.addCategory'), // '新增品类'
      catModel: {
        catForm: {
          categoryName: '', // 品类名称
          categoryCode: '', // 品类编码
          enabled: 'Y', // 是否启用
          parentId: '', // 父品类ID
          ceeaEnableSynErp: 'N',
          ceeaIfDeliverPlan: 'N',
          ceeaIfBeyondDeliver: 'N',
          ceeaLockPeriod: null,
          deliverySubscribeFlag: 'N',
          mainMaterial: null,
          deliveryOrderFlag: 'N',
          supplierCountLimit: '',
          supplierCountLimitFlag: 'Y',
          lastLevelFlag: 'N'
        },

        rules: {
          categoryName: [{ required: true, message: this.$t('dataConfMod.msgCategoryName') }], // '请输品类名称'
          categoryCode: [{ required: true, message: this.$t('dataConfMod.msgCategoryCode') }], // '请输入品类编码'
          supplierCountLimit: [{ required: false, message: this.$t('dataConfMod.enterSupplierCountLimit') }], // '品类供应商上限'
          enabled: [{ required: false, message: this.$t('dataConfMod.enabledUse') }] // '是否启用'
        }
      },
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-base/purchase/purchaseCategory/importExcel'
      },
      extraData: {
        fileModular: 'perf',
        fileFunction: 'purchaseCategoryMaintenance',
        fileType: 'excel'
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
      maintenanceDialog: false,
      assessmentList: [], // 采购类型
      maintenanceDataList: [],
      maintenanceCategory: {},
      ids: [], // 保存id
      dataRow: [] //
    }
  },
  watch: {
    catModel: {
      immediate: true,
      handler () {
        this.bolF()
      },
      deep: true
    }
  },
  created () {
    this.fatchDictData()

    let _this = this
    this.queryForm = [
      {
        prop: 'categoryName',
        label: () => this.$t('common.categoryName') // '品类名称'
      },
      {
        prop: 'categoryCode',
        label: () => this.$t('common.categoryCode') // '品类编码'
      },
      {
        prop: 'parentName',
        label: () => this.$t('dataConfMod.parentCategory') // '父品类名称'
      },
      {
        prop: 'parentCode',
        label: () => this.$t('dataConfMod.parentCategoryCode') // '父品类编码'
      }
    ]
    this.tableHeader = [
      {
        prop: 'categoryName',
        label: () => this.$t('common.categoryName'), // '品类名称'
        minWidth: '160'
      },
      {
        prop: 'categoryCode',
        label: () => this.$t('common.categoryCode'), // '品类编码'
        minWidth: '120'
      },
      {
        prop: 'parentName',
        label: () => this.$t('dataConfMod.parentCategory'), // '父品类名称'
        minWidth: '120'
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
        prop: 'mainMaterial',
        label: () => this.$t('dataConfMod.mainMaterial'), // 主材主设备
        width: '120',
        dataType: 'dict',
        code: 'MAIN_MATERIAL'
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        width: '100',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ceeaEnableSynErp',
        label: () => this.$t('dataConfMod.ceeaEnableSynErp'), // 是否需同步ERP
        width: '140',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ceeaIfDeliverPlan',
        label: () => this.$t('dataConfMod.ceeaIfDeliverPlan'), // 是否用于执行到货计划
        width: '170',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ceeaIfBeyondDeliver',
        label: () => this.$t('dataConfMod.ceeaIfBeyondDeliver'), // 是否允许超计划发货
        width: '160',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ceeaLockPeriod',
        label: () => this.$t('dataConfMod.ceeaLockPeriod'), // 锁定周期
        width: '100'
      },
      {
        prop: 'deliverySubscribeFlag',
        label: () => this.$t('dataConfMod.deliveryBookingEnable'), // '是否启用送货预约'
        width: '150',
        code: 'YES_OR_NO',
        dataType: 'dict'
      },
      {
        prop: 'ceeaLockPeriod',
        label: () => this.$t('dataConfMod.deliverNotification'), // 送货通知方式
        width: '120'
      },
      {
        prop: 'deliveryOrderFlag',
        label: () => this.$t('dataConfMod.deliveryNoteEnable'), // '是否启用送货单'
        width: '150',
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
        width: '160',
        fixed: 'right',
        editType: 'none',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editOrgData(row)
            }.bind(this),
            code: 'base:purchaseCategoryMaintenance:edit',
            formattor () {
              return _this.$t('common.edit') // '编辑'
            }
          },
          {
            callback: function (row) {
              this.maintenanceData(row)
            }.bind(this),
            code: 'base:purchaseCategoryMaintenance:maintenanceData',
            show: function (row) {
              return row.lastLevelFlag == 'Y'
            },
            formattor () {
              return _this.$t('common.maintenancePurchase') // '维护采购类型'
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
    bolF () {
      if (this.catModel.catForm.supplierCountLimitFlag == 'N' || this.catModel.catForm.supplierCountLimitFlag == '') {
        this.catModel.rules.supplierCountLimit[0].required = false
      } else {
        this.catModel.rules.supplierCountLimit[0].required = true
      }
    },
    nodeClick ({ categoryName }) {
      if (this.PFormData.parentName !== categoryName) {
        this.getQuerydata({ parentName: categoryName })
        this.$refs.formWrapper.setValue('parentName', categoryName)
      }
    },
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [{ dictCode: 'PURCHASE_TYPE' }]
      getDictItemList(dictParamsArr).then(res => {
        const [PURCHASE_TYPE] = res.data
        let assessmentTypeListA = []
        assessmentTypeListA = adaptDictData(PURCHASE_TYPE.PURCHASE_TYPE, 'dict')
        let assessmentTypeList = []
        assessmentTypeListA.forEach(item => {
          let obj = {}
          obj.label = item.label
          obj.value = item.value
          obj.id = item.id
          obj.desc = item.desc
          assessmentTypeList.push(obj)
        })

        this.assessmentList = assessmentTypeList
      })
    },
    downloadTemplate () {
      // 采购分类模板.xlsx
      downloadFileLink(
        '/api-base/purchase/purchaseCategory/importModelDownload',
        this.$t('dataConfMod.purCateXLSX')
      ).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
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
    // 获取品类数据类型
    fatchParentList () {
      purchaseCategoryMaintenance.getCatListParent().then(res => {
        if (res.data) {
          this.parentCatList = res.data.map(item => {
            return {
              id: item.categoryId,
              label: item.categoryName,
              value: item.categoryCode
            }
          })
        }
      })
    },

    // 编辑品类数据
    editOrgData (row) {
      this.orgControlHandle('edit')
      this.getDataForEdit(row)
      this.dialogFormVisible = true
    },
    // 编辑品类
    orgControlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('dataConfMod.addCategory') // '新增品类'
        this.curOpt = 'add'
        this.dialogFormVisible = true
        this.catModel.catForm.categoryName = '' // 品类名称
        this.catModel.catForm.categoryCode = '' // 品类编码
        this.catModel.catForm.enabled = 'Y' // 是否启用
        this.catModel.catForm.parentId = '' // 父品类ID
        this.catModel.catForm.ceeaEnableSynErp = 'N'
        this.catModel.catForm.ceeaIfDeliverPlan = 'N'
        this.catModel.catForm.ceeaIfBeyondDeliver = 'N'
        this.catModel.catForm.ceeaLockPeriod = null
        this.catModel.catForm.deliverySubscribeFlag = 'N'
        this.catModel.catForm.mainMaterial = null
        this.catModel.catForm.deliveryOrderFlag = 'N'
        this.catModel.catForm.supplierCountLimit = '' // 品类上限
        this.catModel.catForm.supplierCountLimitFlag = 'Y'
        this.catModel.catForm.lastLevelFlag = 'N'
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.editCategory') // '编辑品类'
        this.dialogFormVisible = true
        this.curOpt = 'edit'
      }
      this.fatchParentList() // 加载父级品类
    },
    // 编辑之前先获取数据
    getDataForEdit (row) {
      this.catModel.catForm.categoryId = row.categoryId // id
      this.catModel.catForm.categoryName = row.categoryName // 品类名称
      this.catModel.catForm.categoryCode = row.categoryCode // 品类编码
      this.catModel.catForm.enabled = row.enabled // 地址
      this.catModel.catForm.parentId = row.parentId == -1 ? '' : row.parentId // ERPID
      this.catModel.catForm.ceeaEnableSynErp = row.ceeaEnableSynErp
      this.catModel.catForm.ceeaIfDeliverPlan = row.ceeaIfDeliverPlan
      this.catModel.catForm.ceeaIfBeyondDeliver = row.ceeaIfBeyondDeliver
      this.catModel.catForm.ceeaLockPeriod = row.ceeaLockPeriod
      this.catModel.catForm.deliverySubscribeFlag = row.deliverySubscribeFlag
      this.catModel.catForm.mainMaterial = row.mainMaterial
      this.catModel.catForm.deliveryOrderFlag = row.deliveryOrderFlag
      this.catModel.catForm.lastLevelFlag = row.lastLevelFlag
      this.catModel.catForm.supplierCountLimitFlag = row.supplierCountLimitFlag
      this.catModel.catForm.supplierCountLimit = row.supplierCountLimit
    },
    // 删除品类
    delRowData (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let categoryId = row.categoryId
          purchaseCategoryMaintenance.categoryDelete({ categoryId }).then(res => {
            if (res) {
              this.getQuerydata() // 重新查询数据
              this.loasFirstNode() // 重新查询树形一级数据
            }
          })
        })
    },
    // 新增编辑品类数据
    saveOrUpdateOrgHandle (opt) {
      this.$refs.orgform.validate(val => {
        if (!val) {
          this.$message.warning(this.$t('common.pleasefinishRequired'))
          return false
        }
        let submitData = this.catModel.catForm
        if (submitData.ceeaLockPeriod === 0) {
          submitData.ceeaLockPeriod = 0
        } else if (!submitData.ceeaLockPeriod) {
          submitData.ceeaLockPeriod = null
        }
        if (opt === 'add') {
          // 新增
          delete submitData.categoryId
        }
        let subArr = [submitData]
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

    // 点击维护采购类型
    async maintenanceData (row) {
      this.headMainten.categoryName = row.categoryName
      this.headMainten.categoryCode = row.categoryCode
      this.headMainten.parentName = row.parentName
      this.headMainten.parentCode = row.parentCode

      let dataRow = []
      let categoryId = row.categoryId
      let { data } = await this.getCategory({ categoryId })

      data.forEach(item => {
        let obj = {}
        obj.label = item.purchaseTypeName
        obj.value = item.purchaseTypeCode
        obj.id = item.purchaseTypeId
        obj.desc = ''
        dataRow.push(obj)
      })
      this.dataRow = dataRow
      this.maintenanceDialog = true

      this.$nextTick(() => {
        this.$refs.multipleTable.clearSelection()
        this.dataRow.forEach(item => {
          this.$refs.multipleTable.toggleRowSelection(
            this.assessmentList.find(elm => {
              return elm.value == item.value
            })
          )
        })
      })

      let maintenanceCategory = {}
      maintenanceCategory.categoryCode = row.categoryCode
      maintenanceCategory.categoryId = row.categoryId
      maintenanceCategory.categoryName = row.categoryName
      this.maintenanceCategory = maintenanceCategory
    },
    // 勾選采购类型
    maintenanceChange (val) {
      val.forEach(item => {
        item.categoryCode = ''
        item.categoryId = ''
        item.categoryName = ''
        item.purchaseTypeName = item.label
        item.purchaseTypeCode = item.value
        item.purchaseTypeId = item.id
      })
      this.maintenanceDataList = val
    },
    // 提交采购类型
    maintenanceSave () {
      let maintenanceCategory = this.maintenanceCategory || {}
      let putData = {}
      putData.categoryCode = maintenanceCategory.categoryCode
      putData.categoryId = maintenanceCategory.categoryId
      putData.categoryName = maintenanceCategory.categoryName
      putData.purchaseTypes = this.maintenanceDataList
      this.$http({
        url: '/api-base/purchase/category-purchase-type/saveOrUpdateCategoryPurchaseTypes',
        method: 'POST',
        data: putData,
        loading: true
      }).then(res => {
        if (res) {
          this.maintenanceDialog = false
          this.$message({
            type: 'success',
            message: this.$t('common.success')
          })
        }
      })
    },
    // 保存数据
    comfirmSave () {
      if (this.curOpt === 'add') {
        this.saveOrUpdateOrgHandle('add')
      } else {
        this.saveOrUpdateOrgHandle('edit')
      }
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
