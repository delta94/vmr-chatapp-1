import {actionType} from "./vmr-action";

function createInitState() {
  return {
    sideBarActive: false,
    searchUserActive: false,
    currentTab: 'chat',
    friendReloadFlag: true,
    currentWalletTab: 'balance'
  };
}

let initialState = createInitState();

export default function uiReducer(state = initialState, action) {
  let {type, data} = action;

  if (type === actionType.SET_SIDE_BAR) {
    return {
      ...state,
      sideBarActive: data
    }
  }

  if (type === actionType.SET_SEARCH_MODAL) {
    return {
      ...state,
      searchUserActive: data
    }
  }

  if (type === actionType.SET_TAB) {
    return {
      ...state,
      currentTab: data
    }
  }

  if (type === actionType.FRIEND_RELOAD) {
    return {
      ...state,
      friendReloadFlag: !state.friendReloadFlag
    }
  }

  if (type === actionType.SET_WALLET_TAB) {
    return {
      ...state,
      currentWalletTab: data
    }
  }

  return state;
}
