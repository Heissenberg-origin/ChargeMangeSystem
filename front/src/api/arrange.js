import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function getArrangeByDoctorAndDate(doctorId, date) {
  return http.get(`/arrange/QueryBydoctoranddate/${doctorId}/${date}`, jsonConfig)
}