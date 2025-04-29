export const BASE_URL = 'http://47.117.102.116:8080';
export const CHAT_BASE_URL = 'http://47.117.102.116:8081';

export const API = {
    // 树洞相关接口
    TREE_HOLE: {
        GET_ALL_POSTS: `${BASE_URL}/api/posts`,
        GET_POST_DETAIL: (postId) => `${BASE_URL}/api/posts/${postId}`,
        GET_POST_REPLIES: (postId) => `${BASE_URL}/api/posts/${postId}/replys`,
        DELETE_POST: (postId) => `${BASE_URL}/api/posts/${postId}`,
    },
    // 认证相关接口
    AUTH: {
        LOGIN: `${BASE_URL}/api/login`,
    },
    // 通知相关接口
    NOTIFICATION: {
        SEND: `${BASE_URL}/api/notification`,
        GET_SENT_LIST: (senderId) => `${BASE_URL}/api/notification/sender/${senderId}`,
        GET_RECEIVED_LIST: (receiverId) => `${BASE_URL}/api/notification/receiver/${receiverId}`,
        READ: (notificationId, receiverId) => `${BASE_URL}/api/notification/read/${notificationId}/${receiverId}`
    },
    // 预约相关接口
    RESERVATION: {
        GET_COUNSELOR_RESERVATIONS: `${BASE_URL}/api/reservations/counselor`
    },
    // 咨询师管理相关接口
    COUNSELOR: {
        SEARCH: `${BASE_URL}/api/searchCounselor`,
        INSERT: `${BASE_URL}/api/counselor/insert`,
        GET_ALL_CONSULTANTS: (params) => `${BASE_URL}/api/counselor/list?page=${params.page}&pageSize=${params.pageSize}`,
        UPDATE: `${BASE_URL}/api/counselor/update`,
        DELETE: (id) => `${BASE_URL}/api/counselor/delete/${id}`
    },
    // 用户管理相关接口
    USER: {
        SEARCH: `${BASE_URL}/api/user/search`
    },
    // 督导管理相关接口
    TUTOR: {
        SEARCH: `${BASE_URL}/api/tutor/search`,
        INSERT: `${BASE_URL}/api/tutor/insert`,
        UPDATE: `${BASE_URL}/api/tutor/update`,
        DELETE: (id) => `${BASE_URL}/api/tutor/delete/${id}`
    },
    // 排班相关接口
    DUTY: {
        GET_BY_DATE: (date) => `${BASE_URL}/api/duty/getdutybydate/${date}`,
        GET_BY_ID: (id) => `${BASE_URL}/api/duty/getdutybyid/${id}`,
        ADD: `${BASE_URL}/api/duty/add`,
        REMOVE: `${BASE_URL}/api/duty/remove`
    },
    LEAVE: {
        SHOW_LEAVE: `${BASE_URL}/api/leave/showleave`,
        ADD_LEAVE_AGREE: `${BASE_URL}/api/leave/addleaveagree`
    },
    MESSAGES: {
        SESSION: `${CHAT_BASE_URL}/api/messages/session`
    }
}; 