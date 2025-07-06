import http from './axios'

export const login = (data) => {
    return http.post('/api/auth/login', null, {
        params: {
            account: data.account,
            password: data.password
        }
    })
}


export const changePassword = (data) => {
    return http.put('/api/auth/password', null, {
        params: {
            account: data.account,
            oldPassword: data.oldPassword,
            newPassword: data.newPassword
        }
    })
}