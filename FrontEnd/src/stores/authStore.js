import axios from 'axios'
import instance from '../apis/authApi'
import { create } from 'zustand'

// 인증 상태를 관리하는 저장소
// 토큰은 나중에 바꿔야 함
const useAuthStore = create((set, get) => ({
    isAuthenticated: false,
    accessToken: null,
    refreshToken: null,
    loading: true,

    //로그인 시 실행
    login: (accessToken, refreshToken) => {
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)
        set({
            accessToken,
            refreshToken,
            isAuthenticated: true
        })
    },

    //로그아웃 시 실행
    logout: () => {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        set({
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false
        })
        delete instance.defaults.headers.common.Authorization
    },

    //토큰 상태 복구용
    checkAuth: async () => {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');
        if(refreshToken&&accessToken) {
            set({
                accessToken,
                refreshToken,
                isAuthenticated: true,
            })
        }
        // 인증 확인 절차가 끝났으므로 로딩 상태를 해제
        set({ loading: false });
    },

    //accessToken 재발급용
    refreshAuth: async () => {
        const refreshToken = localStorage.getItem('refreshToken'); 
        if (!refreshToken) {
            console.log("refreshToken 없음")
            throw new Error('No refresh token')
        }
        try {
            const res = await axios.post('/api/auth/reissue', {refreshToken: refreshToken})
            console.log(res)
            const { accessToken: newToken, refreshToken: newRefresh } = res.data.data // 💡 data 객체 안의 토큰을 가져오도록 수정
            localStorage.setItem('accessToken', newToken)
            localStorage.setItem('refreshToken', newRefresh)
            set({ accessToken: newToken, refreshToken: newRefresh, isAuthenticated: true })
            return newToken
        } catch (err) {
            console.error("토큰 재발급 실패. 로그아웃합니다.", err)
            // 👇 재발급 실패 시 로그아웃을 호출하여 상태를 초기화합니다.
            get().logout() 
            throw err
        }
    }
}))

export default useAuthStore