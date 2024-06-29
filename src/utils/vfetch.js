import { useTokenState } from "./token"
const token = useTokenState()

const API_URL = "http://joi.work"

export const get = (path) => fetch(`${API_URL}${path}`, { headers: {"Authorization": `Bearer ${token.value}`} }).then( res => res.json() )

export const post = (path, data) => fetch(`${API_URL}${path}`, {
    method: "POST",
    headers: {
        "Authorization": `Bearer ${token.value}`,
        "Content-Type": "application/json"
    },
    body: JSON.stringify(data),
})
    .then( res => res.json() )