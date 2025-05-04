import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/auth/LoginView.vue'
import Register from '../views/auth/RegisterView.vue'
import TreeHole from '../views/admin/TreeHole.vue'
import TreeHoleDetail from '../views/admin/TreeHoleDetail.vue'
import ConsultationRecord from '../views/admin/ConsultationRecord.vue'
import Dashboard from '../views/admin/Dashboard.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import ConsultantManagement from '../views/admin/ConsultantManagement.vue'
import SupervisorManagement from '../views/admin/SupervisorManagement.vue'
import DutySchedule from '../views/admin/DutySchedule.vue'
import Notification from '../views/admin/Notification.vue'
import ConsultantDashboard from '../views/consultant/Dashboard.vue'
import ConsultantConsultationRecord from '../views/consultant/ConsultationRecord.vue'
import ConsultationDetail from '../views/consultant/ConsultationDetail.vue'
import Chat from '@/views/consultant/Chat.vue'
import ConsultantNotification from '@/views/consultant/Notification.vue'
import Schedule from '@/views/consultant/Schedule.vue'
import SupervisorDashboard from '@/views/supervisor/SupervisorDashboard.vue'
import SupervisorNotification from '@/views/supervisor/Notification.vue'
import SupervisorRecords from '@/views/supervisor/Records.vue'
import ConsultantTreeHole from '@/views/consultant/TreeHole.vue'
import ConsultantTreeHoleDetail from '@/views/consultant/TreeHoleDetail.vue'
import SupervisorChat from '@/views/supervisor/Chat.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/consultant/dashboard',
            name: 'consultantDashboard',
            component: ConsultantDashboard,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/',
            redirect: '/consultant/dashboard'
        },
        {
            path: '/login',
            name: 'login',
            component: Login
        },
        {
            path: '/register',
            name: 'register',
            component: Register
        },
        {
            path: '/admin/dashboard',
            name: 'adminDashboard',
            component: Dashboard
        },
        {
            path: '/admin/tree-hole',
            name: 'adminTreeHole',
            component: TreeHole
        },
        {
            path: '/admin/tree-hole/:id',
            name: 'adminTreeHoleDetail',
            component: TreeHoleDetail,
            props: true
        },
        {
            path: '/admin/consultation',
            name: 'adminConsultation',
            component: ConsultationRecord
        },
        {
            path: '/admin/users',
            name: 'adminUsers',
            component: UserManagement
        },
        {
            path: '/admin/consultant',
            name: 'adminConsultant',
            component: ConsultantManagement
        },
        {
            path: '/admin/supervisor',
            name: 'adminSupervisor',
            component: SupervisorManagement
        },
        {
            path: '/admin/schedule',
            name: 'adminSchedule',
            component: DutySchedule
        },
        {
            path: '/admin/notification',
            name: 'adminNotification',
            component: Notification
        },
        {
            path: '/admin/leave-approval',
            name: 'LeaveApproval',
            component: () => import('@/views/admin/LeaveApproval.vue'),
            meta: {
                requiresAuth: true,
                role: 'ADMIN'
            }
        },
        {
            path: '/consultant/consultation',
            name: 'consultantConsultation',
            component: ConsultantConsultationRecord,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/consultation/:id',
            name: 'consultantConsultationDetail',
            component: ConsultationDetail,
            props: true,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/chat/:id',
            name: 'consultantChat',
            component: Chat,
            props: true,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/notification',
            name: 'consultantNotification',
            component: ConsultantNotification,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/schedule',
            name: 'consultantSchedule',
            component: Schedule,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/supervisor/dashboard',
            name: 'supervisorDashboard',
            component: SupervisorDashboard,
            meta: {
                role: 'supervisor'
            }
        },
        {
            path: '/supervisor/notification',
            name: 'supervisorNotification',
            component: SupervisorNotification,
            meta: {
                role: 'supervisor'
            }
        },
        {
            path: '/supervisor/records',
            name: 'supervisorRecords',
            component: SupervisorRecords,
            meta: {
                role: 'supervisor'
            }
        },
        {
            path: '/supervisor/tree-hole',
            name: 'supervisorTreeHole',
            component: () => import('@/views/supervisor/TreeHole.vue'),
            meta: {
                role: 'supervisor'
            }
        },
        {
            path: '/supervisor/tree-hole/:id',
            name: 'supervisorTreeHoleDetail',
            component: () => import('@/views/supervisor/TreeHoleDetail.vue'),
            props: true,
            meta: {
                role: 'supervisor'
            }
        },
        {
            path: '/consultant/tree-hole',
            name: 'consultantTreeHole',
            component: ConsultantTreeHole,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/tree-hole/:id',
            name: 'consultantTreeHoleDetail',
            component: ConsultantTreeHoleDetail,
            props: true,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/supervisor/chat/:id',
            name: 'supervisorChat',
            component: SupervisorChat,
            props: true,
            meta: {
                role: 'supervisor'
            }
        }
    ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : null

    // 如果访问登录或注册页面，直接放行
    if (to.path === '/login' || to.path === '/register') {
        next()
        return
    }

    // 如果用户未登录，重定向到登录页
    if (!user) {
        next('/login')
        return
    }

    // 检查用户角色权限
    if (to.meta.role && to.meta.role !== user.role) {
        // 如果角色不匹配，重定向到对应的首页
        switch (user.role) {
            case 'ADMIN':
                next('/admin/dashboard')
                break
            case 'consultant':
                next('/consultant/dashboard')
                break
            case 'supervisor':
                next('/supervisor/dashboard')
                break
            default:
                next('/login')
        }
        return
    }

    next()
})

export default router 