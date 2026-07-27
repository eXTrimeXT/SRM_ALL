export default {
  path: '/quoteTemplate',
  name: 'quoteTemplate',
  component: () => import('@/layout'),
  redirect: {
    name: 'quoteAttr'
  },
  children: [
    // 报价属性 /quoteTemplate/attr
    {
      path: 'attr',
      component: () => import('modb@/quoteTemplate/views/quoteAttr/index.vue'),
      name: 'quoteAttr',
      meta: {
        title: 'route.quoteAttr',
        defaultActive: 'quoteAttr',
        requiresAuth: true
      }
    },
    // 报价模式 /quoteTemplate/template
    {
      path: 'template',
      component: () => import('modb@/quoteTemplate/views/quoteTemplateList/index.vue'),
      name: 'quoteTemplateList',
      meta: {
        title: 'route.quoteTemplateList',
        defaultActive: 'quoteTemplate',
        requiresAuth: true
      }
    }
  ]
}
