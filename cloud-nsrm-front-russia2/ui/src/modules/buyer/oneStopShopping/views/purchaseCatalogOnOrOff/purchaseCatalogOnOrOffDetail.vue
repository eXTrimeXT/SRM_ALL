<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 物料信息 -->
        <el-collapse-item :title="$t('bidMod.itemInfo')" name="1">
          <BaseForm
            ref="materialRef"
            :disabled="isReadOnly"
            :form-items="materialInfor"
            :merge-form.sync="materialForm"
            form-name="materialForm"
            :wrapper-col="{ span: 6, gutter: 27 }"
          >
            <template #priceLibraryNo>
              <el-input
                v-model="materialForm.priceLibraryNo"
                :disabled="isReadOnly || getFormObj.status === 'OFF_SHELVES'"
                clearable
                @clear="clearFormData"
              >
                <em
                  slot="suffix"
                  class="iconfont iconselect search-po"
                  :class="[isReadOnly || getFormObj.status === 'OFF_SHELVES' ? 'el-input__icon' : 'off-cursor']"
                  @click="openQueryDialog"
                />
              </el-input>
            </template>
          </BaseForm>
        </el-collapse-item>
        <!-- 价格信息 -->
        <el-collapse-item :title="$t('dataConfMod.priceInfor')" name="2">
          <BaseForm
            ref="priceRef"
            :disabled="isReadOnly"
            :form-items="priceInfor"
            :merge-form.sync="priceForm"
            form-name="priceForm"
            :wrapper-col="{ span: 6, gutter: 27 }"
          />
        </el-collapse-item>
        <!-- 物料参数 -->
        <el-collapse-item :title="$t('dataConfMod.materialParams')" name="3">
          <BaseForm
            ref="paramRef"
            :disabled="isReadOnly"
            :form-items="paramInfor"
            :merge-form.sync="paramForm"
            form-name="paramForm"
            :wrapper-col="{ span: 6, gutter: 27 }"
          >
            <template #orderQuantityMinimum>
              <el-input
                v-model="paramForm.orderQuantityMinimum"
                v-input-format="{ type: 'float', digits: 8, negative: false }"
                maxlength="30"
                :disabled="isReadOnly"
              />
            </template>
          </BaseForm>
        </el-collapse-item>
        <!-- 物料图片 -->
        <el-collapse-item ref="imgRef" name="4">
          <template slot="title">
            <span style="color: red">* </span> {{ $t('dataConfMod.materialPicture') }}
          </template>
          <picture-card
            :fileList="catalogOnShelvesAttaches"
            :isReadOnly="isReadOnly"
            @handleSuccess="handleSuccess"
            @setDefaultPic="setDefaultPic"
          />
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button v-if="showBtn.toggleEdit" type="primary" @click="toggleEdit">
            {{ $t('purchaseDemand.manage') }}
          </el-button>
          <el-button v-if="showBtn.saveBill" type="primary" @click="handleBtnCilck('saveBill')">
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="showBtn.onBill"
            type="primary"
            @click="handleBtnCilck('onBill')"
          >
            {{ $t('bidMod.shelvesUp') }}
          </el-button>
          <el-button
            v-if="showBtn.passBill"
            type="primary"
            @click="handleBtnCilck('passBill')"
          >
            {{ $t('common.toApprove') }}
          </el-button>
          <el-button
            v-if="showBtn.rejectBill"
            type="primary"
            @click="handleBtnCilck('rejectBill')"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
          <el-button
            v-if="showBtn.offBill"
            type="primary"
            @click="handleBtnCilck('offBill')"
          >
            {{ $t('bidMod.shelvesDown') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>

    <price-search
      :priceVisible.sync="priceVisible"
      @priceComfirm="priceComfirm"
      @close="priceVisible = false"
    />

    <!-- 选择父级菜单弹框 -->
    <srm-dialog
      :title="$t('dataConfMod.offShelvesReason')"
      size="small"
      :destroy-on-close="true"
      :visible.sync="offReasonVisible"
      :close-on-click-modal="false"
    >
      <el-input v-model="offReason" type="textarea" :rows="2" maxlength="50" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="offReasonVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="reasonComfirm">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import PopSearch from 'modb@/developmentKits/views/codeGenerate/pop-search'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import priceSearch from 'lib@/composition/oneStopShopping/priceSearch'
import { materialInfor, priceInfor, paramInfor } from './data/detail'
import { tabTodoMixin } from '@/utils/mixins'
export default {
  name: 'PurchaseCatalogOnOrOffDetail',
  components: {
    BaseForm,
    pictureCard,
    CToolbar,
    PopSearch,
    priceSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      catalogOnShelvesAttaches: [],
      priceVisible: false,
      offReasonVisible: false,
      offReason: '',
      activeDims: ['1', '2', '3', '4'],
      otherObjFile: {},
      materialInfor: materialInfor(this),
      materialForm: {
        priceLibraryNo: '',
        priceLibraryId: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        vendorCode: '',
        vendorId: '',
        vendorName: '',
        materialCode: '',
        materialId: '',
        materialName: '',
        categoryId: '',
        categoryCode: '',
        categoryName: '',
        unit: '',
        status: '',
        createdUserName: '',
        creationDate: ''
      },
      priceInfor: priceInfor(this),
      priceForm: {
        taxPrice: '',
        notaxPrice: '',
        taxRate: '',
        taxKey: '',
        currencyId: '',
        currencyCode: '',
        currencyName: '',
        effectiveDate: '',
        expirationDate: ''
      },
      paramInfor: paramInfor(this),
      paramForm: {
        deliveryCycle: '',
        brand: '',
        orderQuantityMinimum: '',
        specification: '',
        weight: '',
        size: '',
        color: ''
      }
    }
  },
  /**
   * 字典：CATALOG_ON_SHELVES_STATUS
   * TO_BE_ON_SHELVES,    //【采购方】待上架
   * TO_BE_SUBMIT,        //【供应商】待提交
   * TO_BE_APPROVED,      //【供应商】待审核
   * REJECTED,            //审核驳回
   * ON_SHELVES,          //已上架
   * OFF_SHELVES;         //已下架
   */
  computed: {
    // 只读判断
    isReadOnly () {
      return (
        this.$attrs.params.flag === 'view' ||
        ['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes(this.getFormObj.status)
      )
    },
    fileList () {
      return this.catalogOnShelvesAttaches.map(item => item.response.data)
    },
    getIntoMethod () {
      const map = new Map([
        ['add', 'ADD'],
        ['edit', 'EDIT'],
        ['view', 'VIEW']
      ])
      return map.get(this.$attrs.params.flag)
    },
    getFormObj () {
      return {
        ...this.materialForm,
        ...this.priceForm,
        ...this.paramForm,
        ...this.otherObjFile
      }
    },
    showBtn () {
      return {
        toggleEdit: this.getIntoMethod === 'VIEW' &&
          !['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes(this.getFormObj.status),
        saveBill:
          this.getIntoMethod === 'ADD' ||
          (['TO_BE_ON_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW'),
        onBill:
          this.getIntoMethod === 'ADD' ||
          (['TO_BE_ON_SHELVES', 'OFF_SHELVES'].includes(this.getFormObj.status) &&
            this.getIntoMethod !== 'VIEW'),
        passBill:
          ['TO_BE_APPROVED'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        rejectBill:
          ['TO_BE_APPROVED'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        offBill: ['ON_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW'
      }
    }
  },
  mounted () {
    this.initDetail()
  },
  methods: {
    initDetail () {
      const { flag, row } = this.$attrs.params
      if (flag !== 'add') {
        this.queryDetails(row.catalogOnShelvesId)
      }
    },
    // 查看状态是切换为编辑界面
    toggleEdit () {
      const readOnly = ['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes(this.getFormObj.status)
      if (!readOnly) this.$attrs.params.flag = 'edit'
    },
    async queryDetails (id) {
      const { data } = await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/getDetailPage',
        method: 'GET',
        params: { id },
        loading: true
      })
      // 渲染表单
      this.matchFileObj(data.catalogOnShelves)
      // 附件渲染
      this.setAttach(data)
    },
    setAttach (data) {
      this.catalogOnShelvesAttaches = data.catalogOnShelvesAttaches.map(item => {
        return {
          ifDefaultPicture: item.ifDefaultPicture,
          response: {
            data: item
          }
        }
      })
    },
    // 打开价格编号弹窗
    openQueryDialog () {
      if (this.isReadOnly || this.getFormObj.status === 'OFF_SHELVES') return
      this.priceVisible = true
    },
    /**
     * @Description: 设置表单字段读取值
     * @param: key 三个表单的字段
     * @param: obj 传值对象
     */
    setMachFile (key, obj) {
      // 获取三个对象融合数组
      const arrForm = [this.materialForm, this.priceForm, this.paramForm]
      // 获取三个对象所有值
      const files = { ...this.materialForm, ...this.priceForm, ...this.paramForm }

      arrForm.forEach(item => {
        // 设置三个表单对应值
        if (Object.keys(item).includes(key)) {
          if (obj[key]) {
            this.$set(item, key, obj[key])
          }
        }
        // 设置后端返回界面未含有值
        if (!Object.keys(files).includes(key)) {
          if (obj[key]) {
            this.$set(this.otherObjFile, key, obj[key])
          }
        }
      })
    },
    // 匹配对象字段，后台只反一个对象，前端做拆分处理
    matchFileObj (obj) {
      for (let key in obj) {
        this.setMachFile(key, obj)
      }
    },
    // 价格编号确认
    priceComfirm (select) {
      this.matchFileObj(select)
      this.priceVisible = false
    },
    // 返回
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('purchaseCatalogOnOrOffList.getQuerydata')
    },
    setDefaultPic (file) {
      this.catalogOnShelvesAttaches.forEach(item => {
        if (!item.response.data.ifDefaultPicture) item.response.data.ifDefaultPicture = 'N'
        if (file.fileuploadId === item.response.data.fileuploadId) {
          if (item.response.data.ifDefaultPicture === 'Y') {
            this.$set(item, 'ifDefaultPicture', 'N')
            this.$set(item.response.data, 'ifDefaultPicture', 'N')
          } else {
            this.$set(item, 'ifDefaultPicture', 'Y')
            this.$set(item.response.data, 'ifDefaultPicture', 'Y')
          }
        } else {
          this.$set(item, 'ifDefaultPicture', 'N')
          this.$set(item.response.data, 'ifDefaultPicture', 'N')
        }
      })
      // this.fileList = this.catalogOnShelvesAttaches
    },
    // 图片上传成功赋值
    handleSuccess (res, file, fileList) {
      this.catalogOnShelvesAttaches = fileList
    },
    clearFormData () {
      // 清空时设置不可修改字段
      const freezeKey = ['status', 'createdUserName', 'creationDate']
      Object.keys(this.materialForm).forEach(key => {
        if (!freezeKey.includes(key)) this.materialForm[key] = null
      })
      Object.keys(this.priceForm).forEach(key => (this.priceForm[key] = null))
      Object.keys(this.paramForm).forEach(key => (this.paramForm[key] = null))
    },
    async getCheckForm () {
      const formFiled = await this.$refs.materialRef.validate()

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 校验是否有图片上传并且有默认主图
    hasDefaultImg () {
      if (this.catalogOnShelvesAttaches.length < 1) {
        this.__jump_error__('imgRef', null, '请至少上传一张物料图片！')
        return false
      }
      const sign = this.catalogOnShelvesAttaches.some(
        item => item.response.data.ifDefaultPicture === 'Y',
      )
      if (!sign) {
        this.__jump_error__('imgRef', null, '请设置一张默认主图！')
        return false
      }
      return true
    },
    async handleBtnCilck (method) {
      if (!this.hasDefaultImg()) {
        return
      }
      const { flag, message } = await this.getCheckForm()
      flag ? this[method]() : this.__focus_error__(message)
    },
    // 暂存
    async saveBill () {
      const { data } = await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/addOrUpdateOnShelves',
        method: 'POST',
        data: {
          catalogOnshelves: this.getFormObj,
          catalogOnShelvesAttaches: this.fileList
        },
        loading: true
      })
      this.queryDetails(data)
    },
    // 上架
    async onBill () {
      const confirmResult = await this.$confirm(this.$t('dataConfMod.isSureOnshelves'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {
        /* nothing */
      })

      if (confirmResult !== 'confirm') return

      await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/onShelves',
        method: 'POST',
        data: {
          catalogOnshelves: this.getFormObj,
          catalogOnShelvesAttaches: this.fileList
        },
        loading: true
      })
      this.$message.success(this.$t('bidMod.onShelfSucc'))
      this.cancelBill()
    },
    // 通过
    async passBill () {
      const confirmResult = await this.$confirm(this.$t('dataConfMod.surePass'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {
        /* nothing */
      })

      if (confirmResult !== 'confirm') return

      await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/accept',
        method: 'GET',
        params: {
          id: this.getFormObj.catalogOnShelvesId
        },
        loading: true
      })
      this.$message.success(this.$t('dataConfMod.passed'))
      this.cancelBill()
    },
    // 驳回
    async rejectBill () {
      const confirmResult = await this.$confirm(this.$t('dataConfMod.isReject'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {
        /* nothing */
      })

      if (confirmResult !== 'confirm') return

      await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/refuse',
        method: 'GET',
        params: {
          id: this.getFormObj.catalogOnShelvesId
        },
        loading: true
      })
      this.$message.success(this.$t('dataConfMod.rejected'))
      this.cancelBill()
    },
    // 确认下架
    async reasonComfirm () {
      await this.$http({
        url: '/api-sup-ce/pr/catalogOnShelves/offShelves',
        method: 'GET',
        params: {
          id: this.getFormObj.catalogOnShelvesId,
          offShelvesReason: this.offReason
        },
        loading: true
      })
      this.$message.success(this.$t('dataConfMod.offShevels'))
      this.cancelBill()
      // this.offReasonVisible = false
    },
    // 下架
    offBill () {
      this.offReason = ''
      this.offReasonVisible = true
    },
    // 业务实体
    selectHandler (node) {
      this.materialForm.orgId = node ? node.organizationId : null
      this.materialForm.orgCode = node ? node.organizationCode : null
      this.materialForm.orgName = node ? node.organizationName : null
    },
    // 库存组织
    selectHandler2 (node) {
      this.materialForm.organizationId = node ? node.organizationId : null
      this.materialForm.organizationCode = node ? node.organizationCode : null
      this.materialForm.organizationName = node ? node.organizationName : null
    }
  }
}
</script>

<style scoped lang="scss">
.off-cursor {
  cursor: pointer;
}
.search-po {
  float: right;
}
:deep(.el-input__clear) {
  font-size: 12px;
  width: 12px;
}
:deep(.el-input__suffix) {
  height: 28px;
  padding: 0 4px;
  color: #96999c;
  line-height: 28px;
  margin: 1px 0;
  &:hover {
    color: #0077ff;
    border-color: #96999c;
    background-color: #f6f6f6;
  }
}
</style>
