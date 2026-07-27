<template>
  <el-select
    v-bind="$attrs"
    :value="value"
    filterable
    clearable
    v-on="$listeners"
  >
    <slot>
      <el-option
        v-for="(option, index) in options"
        :key="index"
        v-bind="option"
      />
    </slot>
  </el-select>
</template>

<script>
import { listRoleInfo } from '@/api/common'
export default {
  name: 'CRoleSelector',
  props: {
    value: {
      type: [String, Number, Boolean, Array]
    },
    roleType: {
      type: String,
      default: function () {
        return null
      }
    },
    valueKey: {
      type: String,
      default: function () {
        return 'roleId'
      }
    }
  },
  data () {
    return {
      options: []
    }
  },
  computed: {
  },
  watch: {
    roleType () {
      if (this.roleType) {
        this.initOption()
      }
    }
  },
  mounted () {
    this.initOption()
  },
  methods: {
    initOption () {
      listRoleInfo({ pageSize: 999, pageNum: 1, roleType: this.roleType }).then(res => {
        if (res.data) {
          let roleArray = []
          for (let i = 0; i < res.data.length; i++) {
            roleArray.push({ id: res.data[i].roleId, label: res.data[i].roleName, value: res.data[i][this.valueKey] })
          }
          this.options = roleArray
        }
      })
    }
  }
}
</script>

<style lang="scss">
.c-select-load-more {
  width: 100%;
  padding: 0 8px;

  .el-button {
    margin-left: 0;
  }
}

.c-select-no-more {
  padding: 8px 0;
  text-align: center;
  color: #cccccc;
}
</style>
