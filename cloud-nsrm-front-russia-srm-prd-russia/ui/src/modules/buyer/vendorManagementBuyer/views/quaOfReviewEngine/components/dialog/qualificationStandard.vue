<template>
  <div ref="printer">
    <el-collapse v-model="activeDims">
      <el-collapse-item
        v-for="item in data"
        :ref="item.name"
        :key="item.name"
        :title="item.title"
        name="1"
      >
        <srm-row :gutter="32">
          <srm-col v-for="(item2, index) in item.content" :key="index" :initCol="2">
            {{ item2.fieldName }}：
            <div style="border:1px solid #ccc;border-radius:5px;padding: 5px;">
              {{ item2.fieldContent }}
            </div>
          </srm-col>
        </srm-row>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>

export default {
    name: 'QualificationStandard',
    props: {
        data: {
            type: Array
        }
    },

    data () {
        return {
            activeDims: ['1', '2', '3', '4', '5', '6', '7']

        }
    }
}
</script>
