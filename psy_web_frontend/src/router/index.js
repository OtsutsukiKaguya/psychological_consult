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
            props: true  // 允许通过 props 传递路由参数
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
            path: '/consultant/consultation',
            name: 'consultantConsultation',
            component: ConsultantConsultationRecord,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/consultation/:id',
            name: 'consultationDetail',
            component: ConsultationDetail,
            props: true,
            meta: {
                role: 'consultant'
            }
        },
        {
            path: '/consultant/chat/:id',
            name: 'ConsultantChat',
            component: Chat
        }
    ]
})

export default router 