import axios from 'axios';
import qs from 'qs';
import Vue from 'vue';
import store from '../store';
import { Message, MessageBox } from 'element-ui';
import utilConfig from '@/utils/config';

// axios.defaults.baseURL = '/api'

const baseUrl = utilConfig.baseUrl;
const suffix = store.state.suffix;

// http request 拦截器
axios.interceptors.request.use(
    config => {
    	const token = localStorage.getItem('token');
    	const kongAdminUrl = localStorage.getItem('kongAdminUrl');
    	const url = config.url;
    	const flag = url.indexOf(suffix) != -1 ? true : false;
      
        if(flag){
        	config.url = url.replace(suffix, '');
        	config.url = baseUrl + config.url;
        	if(token){ 
	            config.headers.token = token;
	        }
        }else{
        	config.url = 'http://' + kongAdminUrl + config.url;
        }

        return config;
    },
    err => {
        return Promise.reject(err);
    });

// http response 拦截器
axios.interceptors.response.use(
    response => {
        if (response.data.code == 401) {
            // 返回 401 清除token信息并跳转到登录页面
            store.commit('LOGIN_OUT');
            MessageBox.confirm('您的登录信息已过期，请重新登录', '提示', {
            	type: 'warning',
            	confirmButtonText: '确定',
				cancelButtonText: '取消'
            }).then(() => {
            	window.location.replace('/#/login?redirect=' + location.hash.slice(1));
            }).catch(() => {
            	new Vue().$message({
					type: 'info',
					message: '已取消重新登录'
				})
            })
        }

        return response;
    },
    error => {
        if (error.response) {
            Message({
                type: 'error',
                message: error.response.data
            })
            console.log(error.response)
        }
        return Promise.reject(error) 
    });

export default{
	install(Vue, option){
		Vue.prototype.$post = function(url, params, sb){
			return new Promise((resolve, reject) => {
				axios.post(url, sb ? qs.stringify(params) : params).then(res => {
					resolve(res.data);
				}, err => {					
					reject(err);
					Message.error(JSON.stringify(err.response.data.message));
				}).catch(error => {
					console.info(error);
				})
			})
		}

		Vue.prototype.$get = function (url, params) {
            // const newParams = Object.assign({noCache: new Date().getTime()}, params);

            return new Promise((resolve, reject) => {
                axios.get(url, {params: params}).then(res => {
                    resolve(res.data)
                }, err => {
                    reject(err);
                    Message.error(JSON.stringify(err.response.data.message));
                }).catch(error => {
                    console.log(error);
                })
            })
        }

        Vue.prototype.$delete = function (url, params) {
            return new Promise((resolve, reject) => {
                axios.delete(url, {params: params}).then(res => {
                    resolve(res)
                }, err => {
                    reject(err);
                    Message.error(JSON.stringify(err.response.data.message));
                }).catch(error => {
                    console.log(error);
                })
            })
        }

        Vue.prototype.$patch = function(url, params, sb){
			return new Promise((resolve, reject) => {
				axios.patch(url, sb ? qs.stringify(params) : params).then(res => {
					resolve(res.data);
				}, err => {
					reject(err);
					Message.error(JSON.stringify(err.response.data.message));
				}).catch(error => {
					console.info(error);
				})
			})
		}
	}
}