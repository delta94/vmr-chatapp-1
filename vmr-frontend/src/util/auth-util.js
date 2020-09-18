export function jwtLogin(jwtToken, userId) {
  localStorage.setItem("jwtToken", jwtToken);
  localStorage.setItem("userId", userId);
}

export function getUserId() {
  return localStorage.getItem("userId");
}

export function getJwtToken() {
  return localStorage.getItem("jwtToken");
}

export function logout() {
  localStorage.clear();
}
