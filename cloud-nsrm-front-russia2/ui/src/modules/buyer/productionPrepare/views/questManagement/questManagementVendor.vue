<template>
  <quest-management-detail v-bind="$attrs" v-on="$listeners" />
</template>
<script>
export default {
  name: 'QuestManagementVendor',
  components: {
    questManagementDetail: () => import('./questManagementDetail')
  }
}
</script>
