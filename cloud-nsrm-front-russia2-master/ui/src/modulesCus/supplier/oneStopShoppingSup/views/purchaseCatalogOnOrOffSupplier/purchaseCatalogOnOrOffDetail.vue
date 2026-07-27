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
                clearable
                disabled
                @clear="clearFormData"
              >
                <em
                  slot="suffix"
                  class="iconfont iconselect search-po"
                  :class="[isReadOnly || getFormObj.status === 'REJECTED' ? 'el-input__icon' : 'off-cursor']"
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
          </el-button>
          <el-button
            v-if="showBtn.submitBill"
            type="primary"
            @click="handleBtnCilck('submitBill')"
          >
            {{ $t('dataConfMod.submitCheck') }}
          </el-button> -->
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import PopSearch from 'modb@/developmentKits/views/codeGenerate/pop-search'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import priceSearch from 'lib@/composition/oneStopShopping/priceSearch'
import { tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from 'lib@/utils/util'
import { materialInfor, priceInfor, paramInfor } from 'modcb@/oneStopShopping/views/purchaseCatalogOnOrOff/data/detail'

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
        extGoodsName: null
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
        extReferencePrice: ''
      },
      paramInfor: paramInfor(this),
      paramForm: {
        orderQuantityMinimum: '',
        deliveryCycle: '',
        extShelfLife: '',
        brand: '',
        specification: ''
      }
    }
  },
  computed: {
    // 只读判断
    isReadOnly () {
      return this.$attrs.params.flag === 'view'
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
        toggleEdit: this.getIntoMethod === 'VIEW' && ['TO_BE_SUBMIT', 'REJECTED'].includes(this.getFormObj.status),
        saveBill:
          this.getIntoMethod === 'ADD' ||
          (['TO_BE_SUBMIT', 'REJECTED'].includes(this.getFormObj.status) &&
            this.getIntoMethod !== 'VIEW'),
        submitBill:
          this.getIntoMethod === 'ADD' ||
          (['TO_BE_SUBMIT', 'REJECTED'].includes(this.getFormObj.status) &&
            this.getIntoMethod !== 'VIEW')
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
      if (this.$attrs.params.flag === 'view') this.$attrs.params.flag = 'edit'
    },
    async queryDetails (catalogOnShelvesId) {
      const searchData = transformMQL.save(
        'CatalogOnShelvesVendor',
        [catalogOnShelvesId],
        'read',
        {
          '*': {},
          'catalogOnShelvesAttaches': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/CatalogOnShelvesVendor/read',
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
      if (this.isReadOnly || this.getFormObj.status === 'REJECTED') return
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
        this.__jump_error__('imgRef', null, this.$t('cusEntry.supplement20250205.uploadAtLeastOneMaterialImage'))  // '请至少上传一张物料图片！'
        return false
      }
      const sign = this.catalogOnShelvesAttaches.some(
        item => item.response.data.ifDefaultPicture === 'Y',
      )
      if (!sign) {
        this.__jump_error__('imgRef', null, this.$t('cusEntry.supplement20250205.defaultMainImage'))  // '请设置一张默认主图！'
        return false
      }
      return true
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
</style>
