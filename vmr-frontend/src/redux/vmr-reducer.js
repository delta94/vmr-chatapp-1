let initialState = {
  user: {
    jwt: localStorage.getItem("jwtToken"),
    userId: localStorage.getItem("userId")
  }
};

export default function appReducer(state = initialState, action) {
  let data = action.data;
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      state = handleLogin(state, data);
  }
  return state;
}

function handleLogin(state, data) {
  console.log(data);
  return Object.assign({}, state, {
    user: {
      jwt: data.jwt,
      userId: data.userId
    }
  });
}
