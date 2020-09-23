let initialState = {
  sideBarActive: false
}

export default function uiReducer(state = initialState, action) {
  let {type, data} = action;

  if (type === 'SIDEBAR_SET_ACTIVE') {
    return {
      ...state,
      sideBarActive: data
    }
  }

  if (type === 'TOGGLE_SIDE_BAR') {
    return {
      ...state,
      sideBarActive: !state.sideBarActive
    }
  }

  return state;
}