import * as types from './mutation-types'

const mutations = {
	[types.KONG_ADMIN_ULR](state, payload){
		state.kongAdminUrl = payload;
		localStorage.setItem('kongAdminUrl', payload);
	},

	[types.HAS_TOKEN](state, payload){
		state.token = payload;
		localStorage.setItem('token', payload);
	},

	[types.LOGIN_OUT](state){
		state.token = null;
		localStorage.removeItem('token');
		localStorage.removeItem('userInfo');
	},

	[types.SET_USERINFO](state, payload){
		state.userInfo = payload;
		localStorage.setItem('userInfo', payload);
	},

	[types.SET_CURUSERGROUP](state, payload){
		state.curUserGroup = payload;
		sessionStorage.setItem('curUserGroup', payload);
	}
}

export default mutations;
