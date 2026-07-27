export default {
  path: '/qualitySynergySupplier',
  name: 'qualitySynergySupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'incomingException'
  },
  children: [
    {
      path: 'pdcaPage',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/pdcaPage'),
      name: 'pdcaPage',
      meta: {
        title: 'route.pdcaPageQa', // 过程PDCA
        requiresAuth: true
      }
    },
    {
      path: 'qualityProject',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/qualityProject'),
      name: 'qualityProject',
      meta: {
        title: 'route.qualityProjectQa', // 监控项目管理
        requiresAuth: true
      }
    },
    {
      path: 'spcData',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/spcData'),
      name: 'spcData',
      meta: {
        title: 'route.spcDataQa', // SPC数据管理
        requiresAuth: true
      }
    },
    {
      path: 'spcStandard',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/spcStandard'),
      name: 'spcStandard',
      meta: {
        title: 'route.spcStandardQa', // SPC标准维护
        requiresAuth: true
      }
    },
    {
      path: 'companyMaterial',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/companyMaterial'),
      name: 'companyMaterial',
      meta: {
        title: 'route.companyMaterialQa', // 公司物料
        requiresAuth: true
      }
    },
    {
      path: 'customRelation',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/customRelation'),
      name: 'customRelation',
      meta: {
        title: 'route.customRelationQa', // 客户关系
        requiresAuth: true
      }
    },
    {
      path: 'relationMaterial',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/relationMaterial'),
      name: 'relationMaterial',
      meta: {
        title: 'route.relationMaterialQa', // 客户料号对应关系
        requiresAuth: true
      }
    },
    {
      path: 'lineCode',
      component: () => import('@/modulesQa/supplier/qualitySynergySupplier/views/lineCode'),
      name: 'lineCode',
      meta: {
        title: 'route.lineCodeQa', // 产线管理
        requiresAuth: true
      }
    }
  ]
}
