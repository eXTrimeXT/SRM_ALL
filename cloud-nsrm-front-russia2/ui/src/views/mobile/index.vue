<template>
  <el-container className="login-container">
    <el-header style="height: 50px">
      <!-- <div class="login-header-logo">单点登录</div> -->
    </el-header>
  </el-container>
</template>

<script>
import {getUrlKey} from "@/library/utils/util";
import {setLoginType, setToken} from "@/utils/auth";

export default {
  mixins: [],
  data() {
    return {
      loading: false,
      routeName: undefined,
      tipDialogVisible: false,
      otherQuery: {}
    };
  },
  created() {
    setLoginType('MOBILE');
    if ("routeName" in this.$route.query) {
      this.routeName = this.$route.query.routeName;
    }
    this.routeName = this.routeName ? this.routeName : 'flowTaskView'
    this.otherQuery.from = this.$route.query.from
    this.otherQuery.funName = this.$route.query.funName
    this.otherQuery.formId = this.$route.query.formId
    this.otherQuery.formNo = this.$route.query.formNo

    let token = getUrlKey('token')
    if (token) {
      setToken(token)
    }
    this.initSystem()
  },
  methods: {
    initSystem() {
      this.loading = true;
      // 获取token 用户登录
      this.$store.dispatch("user/initSystem").then(
        res => {
          if (res) {
            let mainType = res.data.mainType;
            let isConfirm = res.data.isConfirm;
            let userType = res.data.userType;
            if (this.routeName === 'flowTaskView') {
              this.$router.push({
                name: 'flowTaskView',
                query: {
                  from: this.otherQuery.from,
                  funName: this.otherQuery.funName, //
                  formId: this.otherQuery.formId, // 业务单据ID
                  formNo: this.otherQuery.formNo // 业务单据No
                }
              })
            }
            // }
            this.loading = false;
          }
        },
        err => {
          console.log(err);
        }
      )
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
