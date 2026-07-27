<template>
  <SrmDialog
    title="注册须知"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :show-close="false"
    :close-on-press-escape="false"
    class="notice-dialog"
  >
    <div class="notice-wrapper">
      <div class="notice-title">
        评标专家注册须知
      </div>
      <div class="notice-content">
        <div class="notice-block">
          <div class="title">
            一、总则
          </div>
          <div class="info">
            <p>评标专家在注册/使用本平台提供的各项服务之前，应仔细阅读本服务协议。如您不同意本服务协议，可以放弃注册申请，一旦完成注册或使用本平台的服务，即视为您已了解并完全同意本服务协议各项内容。</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            二、注册要求
          </div>
          <div class="info">
            <p>1、必须使用实名注册；</p>
            <p>2、专家部分基础信息来自集团人力资源系统，无需填写（如与实际情况不符，请联系人力资源部门更新），其他信息需专家自行填写，须保证信息完整、准确；</p>
            <p>3、各专家需根据专家申报条件选择合适的专家等级。</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            三、评标要求
          </div>
          <div class="info">
            <p>1、按照规定时间参加评标，不得无故不参加评标；</p>
            <p>2、依据评标规则进行评价、打分，并对结果负责；</p>
            <p>3、独立评标，不影响其他专家评标或被其他专家影响；</p>
            <p>4、不组织或参与围标、串标等违反招标要求的活动；</p>
            <p>5、不私下接触或联系投标供应商，不得接受或索要投标单位的任何形式的贿赂；</p>
            <p>6、对评标内容保密，不泄露评标信息。</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            四、敏感事项回避
          </div>
          <div class="info">
            <p>为保证评标过程公平公正，如遇以下情况，评标专家应主动回避：</p>
            <p>1、曾在本次投标单位有过工作履历的；</p>
            <p>2、家属在本次投标单位工作的；</p>
            <p>3、与投标单位有经济利益关系；</p>
            <p>4、其他可能影响评标公平公正性的。</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            五、违规处罚
          </div>
          <div class="info">
            <p>已注册专家发生如下情况，将视情况予以警告、退库等处理，并依据C3版《长城汽车监察处置条例》对相关责任人予以处置。</p>
            <p>1、私下联系、接触投标单位的；</p>
            <p>2、收受投标单位的财物或者其他好处的；</p>
            <p>3、得知敏感事项未主动回避的；</p>
            <p>4、向他人透露投标、评标信息的；</p>
            <p>5、其他违规情形的。</p>
          </div>
        </div>
        <div class="notice-block">
          <div class="title">
            六、信息和隐私保护
          </div>
          <div class="info">
            <p>已注册专家发生如下情况，将视情况予以警告、退库等处理，并依据C3版《长城汽车监察处置条例》对相关责任人予以处置。</p>
            <p>1、评标专家应及时、诚信地向本平台提供真实、准确、完整、合法有效的信息资料。信息资料如有变动，应及时更新。</p>
            <p>2、评标专家账号和密码不得以任何方式转让、赠与或继受，也不得外借他人使用。</p>
            <p>3、长城汽车股份有限公司对评标专家管理采取集团内部监督方式，评标专家的基本情况、评标工作评价等信息将依照相关法律、法规及集团公司相关制度程序由集团公司相关机构进行监督。评标专家完成注册或使用本平台的服务，即视为您授权同意长城汽车股份有限公司依照相关规定适度公开评标专家相关信息。</p>
            <p>4、本平台承诺不会公开、转让或透露评标专家的不可公开的注册资料及用于本平台的非公开内容（除有关法律、法规、规章的强制性规定；有关司法、行政机关的要求；保持或维护本平台的合法权益；事先获得评标专家的明确授权等）。</p>
          </div>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <div class="check">
        <el-checkbox v-model="checked">
          我已阅读并同意
        </el-checkbox>
      </div>
      <el-button @click="handleCancel">
        不同意
      </el-button>
      <el-button type="primary" :disabled="!checked" @click="handleConfirm">
        同意并继续
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>

export default {
  name: 'NoticeDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      checked: false
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  created () {

  },
  methods: {
    handleConfirm () {
      if (!this.checked) return this.$message.warning('请勾选确认')
      this.$emit('confirm')
    },
    handleCancel () {
      this.dialogVisible = false
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({
        name: 'dashboard'
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.dialog-footer {
  text-align: center;
}
.check {
  font-size: 12px;
  margin-bottom: 10px;
}
.notice-wrapper {
  .notice-title {
    text-align: center;
    font-weight: bold;
    font-size: 16px;
  }
  .notice-block {
    .title {
      font-weight: bold;
    }
  }
}
</style>
<style>
.notice-dialog .el-dialog__footer {
  height: auto;
}
</style>
