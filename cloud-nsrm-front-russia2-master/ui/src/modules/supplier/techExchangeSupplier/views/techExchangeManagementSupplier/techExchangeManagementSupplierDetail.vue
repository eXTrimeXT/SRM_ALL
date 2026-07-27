<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <el-form
        :model="detailFormData"
        label-position="top"
        class="detail-form-wrap form-incontainer"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!--交流信息-->
          <el-collapse-item
            :title="$t('bidMod.exchangeInfo')"
            name="1"
          >
            <DetailInfo
              :form-data.sync="detailFormData"
              readonly
            />
          </el-collapse-item>

          <!--需求信息-->
          <el-collapse-item
            :title="$t('bidMod.requireInfo')"
            name="2"
          >
            <RequirementsInfo
              :info-data="tecExcMaterialItems"
              readonly
            />
          </el-collapse-item>

          <!--查看附件-->
          <el-collapse-item
            :title="$t('bidMod.techRequireAttachment')"
            name="3"
          >
            <FileDynamic
              ref="tecExcFilesFileDynamic"
              v-model="tecExcFiles"
              scene-module-code="SCENE_TECHNICAL_EXCHANGE_ATTACHMENT"
              :business-id="technicalExchangeId"
              :editable="false"
              :need-init="false"
            />
          </el-collapse-item>

          <!--联系方式-->
          <el-collapse-item
            :title="$t('vendorMod.contactMethod')"
            name="4"
          >
            <OriginContactInfo
              business-type="TECH_EXCHANGE"
              :info-data.sync="detailFormData"
              read-only
            />
          </el-collapse-item>

          <!--方案附件-->
          <el-collapse-item
            :title="$t('bidMod.planAttachment')"
            name="5"
          >
            <FileDynamic
              ref="sceneAttachment"
              v-model="tecExcFilesSupplier"
              scene-module-code="SCENE_TECHNICAL_EXCHANGE_FEEDBACK_ATTACHMENT"
              :business-id="technicalExchangeFeedbackId"
              :editable="false"
              :need-init="false"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>
import DetailInfo from 'lib@/composition/techExchangeManagement/detailInfo'
import RequirementsInfo from 'lib@/composition/techExchangeManagement/requirementsInfo'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import { techExchangeSupApi } from 'mods@/techExchangeSupplier/api'

export default {
  name: 'TechExchangeManagementSupplierDetail',

  components: {
    DetailInfo,
    RequirementsInfo,
    FileDynamic,
    OriginContactInfo
  },

  data () {
    return {
      technicalExchangeId: this.$attrs.params.row.technicalExchangeId || '',
      // 反馈单据ID，用于反馈附件绑定
      technicalExchangeFeedbackId: '',
      detailFormData: {
        technicalExchangeFormCode: '',
        technicalExchangeTitle: '',
        orgOuId: '',
        orgOuCode: '',
        orgOuName: '',
        technicalExchangeType: '',
        technicalExchangeStartTime: '',
        technicalExchangeEndTime: '',
        creationDate: '',
        technicalExchangeFormStatus: '',
        createdUserName: '',
        remark: '',
        linkMan: '',
        phone: '',
        email: ''
      },
      activeDims: ['1', '2', '3', '4', '5', '6'],
      tecExcMaterialItems: [],
      tecExcFiles: [],
      tecExcFilesSupplier: []
    }
  },

  created () {
    this.getExcInfo()
  },

  methods: {
    async getExcInfo () {
      if (!this.technicalExchangeId) {
        return
      }

      const response = await techExchangeSupApi.getInfo(this.technicalExchangeId)
      if (response && response.data) {
        let formData = {}
        Object.keys(this.detailFormData).forEach(item => {
          formData = {
            ...formData,
            [item]: response.data[item]
          }
        })
        this.detailFormData = formData
        this.tecExcMaterialItems = response.data.tecExcMaterialItems
        this.technicalExchangeFeedbackId = response.data.technicalExchangeFeedbackId
        this.$nextTick(() => {
          // 更新采购方技术要求附件表格
          this.$refs.tecExcFilesFileDynamic.loadFileInfo()
          this.$refs.sceneAttachment.loadFileInfo()
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.flex-container {
  padding-bottom: 50px;
}
.detail-form-wrap {
  padding: 15px 0;
}
</style>
