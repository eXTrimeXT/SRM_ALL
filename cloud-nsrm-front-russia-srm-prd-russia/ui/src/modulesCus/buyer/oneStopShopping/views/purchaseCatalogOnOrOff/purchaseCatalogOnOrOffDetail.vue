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
            :form-items="priceInfor"
            :merge-form.sync="priceForm"
            form-name="priceForm"
            :wrapper-col="{ span: 6, gutter: 27 }"
            disabled
          />
        </el-collapse-item>
        <!-- 物料参数 -->
        <el-collapse-item :title="$t('dataConfMod.materialParams')" name="3">
          <BaseForm
            ref="paramRef"
            :form-items="paramInfor"
            :merge-form.sync="paramForm"
            form-name="paramForm"
            :wrapper-col="{ span: 6, gutter: 27 }"
            disabled
          />
        </el-collapse-item>
        <!-- 物料图片 -->
        <el-collapse-item ref="imgRef" :title="$t('dataConfMod.materialPicture')" name="4">
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
          <!-- <el-button v-if="showBtn.toggleEdit" type="primary" @click="toggleEdit">
            {{ $t('purchaseDemand.manage') }}
          </el-button>
          <el-button v-if="showBtn.saveBill" type="primary" @click="handleBtnCilck('saveBill')">
            {{ $t('common.staging') }}
          </el-button> -->
          <el-button
            v-if="showBtn.onBill"
            type="primary"
            @click="handleBtnCilck('onBill')"
          >
            {{ $t('bidMod.shelvesUp') }}
          </el-button>
          <el-button
            v-if="showBtn.timeOnBill"
            type="primary"
            @click="handleBtnCilck('timeOnBill')"
          >
            定时上架
          </el-button>
          <el-button
            v-if="showBtn.timeOffBill"
            type="primary"
            @click="handleBtnCilck('timeOffBill')"
          >
            取消定时上架
          </el-button>
          <!-- <el-button
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
          </el-button> -->
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
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import priceSearch from './priceSearch'
import { materialInfor, priceInfor, paramInfor } from './data/detail'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PurchaseCatalogOnOrOffDetail',
  components: {
    BaseForm,
    pictureCard,
    CToolbar,
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
        priceLibraryNo: null,
        priceLibraryId: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        vendorCode: null,
        vendorId: null,
        vendorName: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        unit: null,
        status: null,
        createdUserName: null,
        creationDate: null,
        extAreaName: null,
        extAreaId: null,
        extAreaCode: null,
        effectiveDate: null,
        expirationDate: null,
        extGoodsCode: null,
        extProtocolRowNo: null,
        extOrgIdList: null, // 业务实体ID集合
        extOrgCodeList: null, // 业务实体编码集合
        extOrgNameList: null, // 业务实体名称集合
        extGoodsName: null,
        extMaterialType: null
      },
      priceInfor: priceInfor(this),
      priceForm: {
        taxPrice: null,
        notaxPrice: null,
        taxRate: null,
        taxKey: null,
        currencyId: null,
        currencyCode: null,
        currencyName: null,
        extReferencePrice: null
      },
      paramInfor: paramInfor(this),
      paramForm: {
        orderQuantityMinimum: null,
        deliveryCycle: null,
        extShelfLife: null,
        brand: null,
        specification: null
      }
    }
  },

  computed: {
    // 只读判断
    // 商品状态：已定时待上架、待上架、已上架、已下架
    isReadOnly () {
      return this.$attrs.params.flag === 'view' ||
        ['SCHEDULED_SHELVES', 'TO_BE_APPROVED', 'ON_SHELVES', 'OFF_SHELVES'].includes(this.getFormObj.status)
    },
    fileList () {
      return this.catalogOnShelvesAttaches?.map(item => item.response.data)
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
        // 定时上架按钮
        timeOnBill: ['UNTIMED_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        // 取消定时上架按钮
        timeOffBill: ['SCHEDULED_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        // 下架
        offBill: ['ON_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        // 上架
        onBill: this.getIntoMethod === 'ADD' ||
          (['TO_BE_ON_SHELVES', 'OFF_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW'),

        toggleEdit: this.getIntoMethod === 'VIEW' &&
          !['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes(this.getFormObj.status),
        saveBill:
          this.getIntoMethod === 'ADD' ||
          (['TO_BE_ON_SHELVES'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW'),
        passBill: ['TO_BE_APPROVED'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW',
        rejectBill: ['TO_BE_APPROVED'].includes(this.getFormObj.status) && this.getIntoMethod !== 'VIEW'
      }
    }
  },
  created () {
    this.materialForm.createdUserName = this.$store.getters.userInfo.nickname
    // console.log('userInfo', this.$store.getters.userInfo)
    this.materialForm.creationDate = parseTime(new Date())
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
    queryDetails (catalogOnShelvesId) {
      const searchData = transformMQL.save(
        'CatalogOnShelves',
        [catalogOnShelvesId],
        'read',
        {
          '*': {},
          'catalogOnShelvesAttaches': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        const { catalogOnShelvesAttaches = [], ...rest } = res.data[0]
        // 渲染表单
        this.matchFileObj({ ...rest })
        // 附件渲染
        this.setAttach(catalogOnShelvesAttaches)
      })
    },
    setAttach (data) {
      this.catalogOnShelvesAttaches = data?.map(item => {
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
      const { materialCode, extMaterialType } = this.materialForm
      if (extMaterialType === 'JD' && materialCode) { // 京东平台物料
        this.getJdPic(materialCode)
      }
    },
    // 价格编号确认
    priceComfirm (select) {
      this.matchFileObj(select)
      this.priceVisible = false
    },
    // 京东商品获取物料图片
    async getJdPic (materialCode) {
      let transformParams = transformMQL.save('ExternalMaterial', [{
        materialCode
      }], 'getSkuImage')
      const response = await this.$http({
        url: '/api-sup-ce/api-ql/ExternalMaterial/getSkuImage',
        method: 'POST',
        data: transformParams,
        loading: true
      })
      console.log('response###', response)
      if(response.data && response.data.records.length){
        this.setAttach(response.data.records)
      }
    },
    // 返回SS
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('PurchaseCatalogOnOrOffList.getQuerydata')
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
    },
    // 图片上传成功赋值
    handleSuccess (res, file, fileList) {
      this.catalogOnShelvesAttaches = fileList
    },
    clearFormData () {
      // 清空时设置不可修改字段
      const freezeKey = ['status', 'createdUserName', 'creationDate', 'extGoodsCode']
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
      // if (this.catalogOnShelvesAttaches.length < 1) {
      //   this.__jump_error__('imgRef', null, '请至少上传一张物料图片！')
      //   return false
      // }
      if (this.catalogOnShelvesAttaches.length == 0) {
        return true
      }
      if (this.catalogOnShelvesAttaches.length == 1) {
        this.catalogOnShelvesAttaches[0].response.data.ifDefaultPicture = 'Y'
        return true
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
    // 定时上架
    async timeOnBill () {
      const confirmResult = await this.$confirm('是否确认定时上架？', {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {
        /* nothing */
      })

      if (confirmResult !== 'confirm') return
      const params = {
        ...this.getFormObj,
        catalogOnShelvesAttaches: this.fileList
      }
      const saveData = transformMQL.save('CatalogOnShelves', [params], 'batchScheduledShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/batchScheduledShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success('定时上架成功！')
        this.cancelBill()
      })
    },
    // 取消定时上架
    async timeOffBill () {
      const confirmResult = await this.$confirm('是否确认取消定时上架？', {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {
        /* nothing */
      })

      if (confirmResult !== 'confirm') return
      const params = {
        ...this.getFormObj,
        catalogOnShelvesAttaches: this.fileList
      }
      const saveData = transformMQL.save('CatalogOnShelves', [params], 'batchUntimedShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/batchUntimedShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success('取消定时上架成功')
        this.cancelBill()
      })
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
      let params = {
        ...this.getFormObj,
        catalogOnShelvesAttaches: this.fileList
      }
      let saveData = transformMQL.save('CatalogOnShelves', [params], 'onShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/onShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('bidMod.onShelfSucc'))
        this.cancelBill()
      })
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
      const params = {
        catalogOnShelvesId: this.getFormObj.catalogOnShelvesId,
        offShelvesReason: this.offReason
      }
      const saveData = transformMQL.save('CatalogOnShelves', [params], 'offShelves')
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelves/offShelves',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('dataConfMod.offShevels'))
        this.cancelBill()
      })
    },
    // 下架
    offBill () {
      this.offReason = ''
      this.offReasonVisible = true
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
