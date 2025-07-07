import http from './axios'
export const login = (data) => {
    return http.post('/auth/login', 
        {
            account: data.account,
            password: data.password
        }, 
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }
    )
}

export const changePassword = (data) => {
    return http.put('/auth/password', null, {
        params: {
            account: data.account,
            oldPassword: data.oldPassword,
            newPassword: data.newPassword
        }
    })
}