
import store from '../store';

const request = {
	rqText(method, url, body){
		if(method === 'get'){
			body = undefined;
		}else{
			body = body && JSON.stringify(body);
		}

		return fetch(store.getters.kongUrl + url, {
			headers: {
				'token': localStorage.getItem('token')
			},
			method,
			body
		}).then((res) => {
			if(res.status >= 200 && res.status < 300){
				return res.text();
			}else{
				const error = new Error(res.statusText);
				throw error;
			}
		}).then(res => {
			return res;
		}).catch((err) => {
			console.error(err);
		})
	},

	rqJson(method, url, body){
		if(method === 'get'){
			body = undefined;
		}else{
			body = body && JSON.stringify(body);
		}

		return fetch(store.getters.kongUrl + url, {
			headers: {
				'token': localStorage.getItem('token'),
				'Content-Type': 'application/json;charset=UTF-8',
			},
			method,
			body
		}).then((res) => {
			if(res.status >= 200 && res.status < 300){
				return res.json();
			}else{
				const error = new Error(res.statusText);
				throw error;
			}
		}).then(res => {
			return res;
		}).catch((err) => {
			console.error(err);
		})
	}
}

export default request;