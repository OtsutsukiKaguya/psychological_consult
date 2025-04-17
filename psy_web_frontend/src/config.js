export const BASE_URL = 'http://47.117.102.116:8080';

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
        LOGIN: `${BASE_URL}/api/auth/login`,
    },
    // 通知相关接口
    NOTIFICATION: {
        SEND: `${BASE_URL}/api/notification`,
        GET_SENT_LIST: (senderId) => `${BASE_URL}/api/notification/sender/${senderId}`,
        GET_RECEIVED_LIST: (receiverId) => `${BASE_URL}/api/notification/receiver/${receiverId}`
    },
    // 预约相关接口
    RESERVATION: {
        GET_COUNSELOR_RESERVATIONS: `${BASE_URL}/api/reservations/counselor`
    }
}; 