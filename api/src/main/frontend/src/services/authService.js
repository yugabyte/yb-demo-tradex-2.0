import { getJSON, postJSON } from "./rest";

export async function doLogin(data) {
    return postJSON('/api/auth/sign-in', data);
}

export async function doLogout(data) {
    return postJSON('/api/auth/sign-out', data);
}

export async function doSignUp(data) {
    return postJSON('/api/auth/sign-up', data);
}

export async function doPassReset(data) {
    return postJSON('/api/auth/password-reset', data);
}