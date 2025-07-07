import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 登录页（独立页面）
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { isPublic: true }
    },
    {
      path: '/',
      redirect: '/login'
    },
    
    // 主页（带侧边栏布局）
     {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        // 首页
        {
          path: 'home',
          name: 'Home',
          component: () => import('@/views/Home.vue')
        },


        //analysis  (管理员)
      {
          path: '/charge-analysis',
          name: 'ChargeAnalysis',
          component: () => import('@/views/analysis/Charge.vue')
        },
        {
          path: '/register-analysis',
          name: 'RegisterAnalysis',
          component: () => import('@/views/analysis/Register.vue')
        },


        //charge (操作员)
        {
          path: '/register-charge',
          name: 'RegisterCharge',
          component: () => import('@/views/charge/RegisterCharge.vue')
        },
        {
          path: '/order-charge',
          name: 'OrderCharge',
          component: () => import('@/views/charge/Prescription.vue')
        },
        {
          path: '/card-recharge',
          name: '/CardRecharge',
          component: () => import('@/views/charge/Recharge.vue')
        },


        //fee-query  (操作员)
        {
          path: '/register-fee-query',
          name: 'RegisterFeeQuery',
          component: () => import('@/views/fee-query/Register.vue')
        },
        {
          path: '/registration-detail/:id',
          name: 'RegisterFeeDetail',
          component: () => import('@/views/fee-query/RegisterDetail.vue')
        },
        {
          path: '/prescription-detail/:id',
          name: 'PrescriptionDetail',
          component: () => import('@/views/fee-query/PrescriptionDetail.vue'),
          meta: { title: '处方详情' }
        },
        {
          path: '/prescription-fee-query',
          name: 'PrescriptionFeeQuery',
          component: () => import('@/views/fee-query/Prescription.vue')
        },
        {
          path: '/card-recharge-query',
          name: '/cardRechargeQuery',
          component: () => import('@/views/fee-query/Recharge.vue')
        },
        {
          path: '/card-settlement-query',
          name: 'CardSettlementQuery',
          component: () => import('@/views/fee-query/Settlement.vue')
        },
    

        //patient (操作员)
         {
          path: '/patient-register',
          name: 'PatientRegister',
          component: () => import('@/views/patient/PatientRegister.vue')
        },
        {
          path: '/patient-list',
          name: 'PatientList',
          component: () => import('@/views/patient/PatientList.vue')
        },
        {
        path: '/patient/detail/:id',
        name: 'PatientDetail',
        component: () => import('@/views/patient/PatientDetail.vue'),
        meta: { title: '患者详情' }
        },
        {
        path: '/patient/edit/:id',
        name: 'PatientEdit',
        component: () => import('@/views/patient/PatientEdit.vue'),
        meta: { title: '编辑患者' }
        },
        {
          path:'/patient/register',
          name: 'RegisterForm',
          component: () => import('@/views/patient/Register.vue')
        },


        //report (管理员)
        {
          path: '/prescription-summary',
          
          component: () => import('@/views/report/Prescription.vue')
        },
        {
          path: '/register-fee-summary',
          
          component: () => import('@/views/report/RegisterFee.vue')
        },
        {
          path: '/daily-report',
          
          component: () => import('@/views/report/Daily.vue')
        },
        {
          path: '/business-report',
          component: () => import('@/views/report/Business.vue')
        },
        {
          path: 'order-fee-summary',
      
          component: () => import('@/views/report/OrderFee.vue')
        },



        //settlement(操作员)
        {
          path: '/order-refund',
      
          component: () => import('@/views/settlement/Refund.vue')
        },
        {
          path: '/card-balance',
      
          component: () => import('@/views/settlement/Balance.vue')
        },
        {
          path: '/daily-settlement',
      
          component: () => import('@/views/settlement/Daily.vue')
        },
      {
          path: '/reg-refund',
      
          component: () => import('@/views/settlement/RegRefund.vue')
        },

        //system(管理员)
        {
          path: '/charge-items',
      
          component: () => import('@/views/system/ChargeItems.vue')
        },
        {
          path: '/receipt-config',
      
          component: () => import('@/views/system/ReceiptConfig.vue')
        },
        {
          path: '/log-management',
      
          component: () => import('@/views/system/LogManagement.vue')
        },


        //settings(公共)
        {
          path:'/settings/profile',
          component: () => import('@/views/settings/Profile.vue'),
        },
        {
          path:'/settings/change-password',
          component: () => import('@/views/settings/ChangePassword.vue'),
        },



        //医生doctor
        {
          path:'/doctor/Consultation',
          component: () => import('@/views/doctor/Consultation.vue'),
        },{
          path:'/doctor/Order',
          component: () => import('@/views/doctor/Order.vue'),
        }

      ]},
    
    // 默认重定向

    
  ]
})

router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  
  // 如果是从登录页跳转且已登录
  if (from.path === '/login' && userInfo.rank) {
    if (userInfo.rank === 'operator' && to.path !== '/patient-register') {
      return next('/patient-register')
    } else if (userInfo.rank === 'doctor' && to.path !== '/doctor/Consultation') {
      return next('/doctor/Consultation')
    }
  }
  
  next()
})



export default router