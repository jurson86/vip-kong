import Vue from 'vue'
import Router from 'vue-router'

const Index = () => import(/* webpackChunkName: "group-backstage" */ '../pages/index.vue');
const ModelView = () => import(/* webpackChunkName: "group-backstage" */ '../pages/view.vue');

const Home = () => import(/* webpackChunkName: "group-home" */ '../pages/home/home.vue');
 
const Apis = () => import(/* webpackChunkName: "group-apis" */ '../pages/apis/apis.vue');
const ApisEdit = () => import(/* webpackChunkName: "group-apis" */ '../pages/apis/apisEdit.vue');
const ApiPlugins = () => import(/* webpackChunkName: "group-apis" */ '../pages/apis/plugins.vue');
const ApiPluginsEdit = () => import(/* webpackChunkName: "group-apis" */ '../pages/apis/pluginsEdit.vue');

// const Plugins = () => import(/* webpackChunkName: "group-plugins" */ '../pages/plugins/plugins.vue');
// const PluginsEdit = () => import(/* webpackChunkName: "group-plugins" */ '../pages/plugins/pluginsEdit.vue');

const Upstreams = () => import(/* webpackChunkName: "group-upstreams" */ '../pages/upstreams/upstreams.vue');
const UpstreamsEdit = () => import(/* webpackChunkName: "group-upstreams" */ '../pages/upstreams/upstreamsEdit.vue');
const Targets = () => import(/* webpackChunkName: "group-upstreams" */ '../pages/upstreams/targets.vue');
const TargetsEdit = () => import(/* webpackChunkName: "group-upstreams" */ '../pages/upstreams/targetsEdit.vue');
const Health = () => import(/* webpackChunkName: "group-upstreams" */ '../pages/upstreams/health.vue');

// const RsaConfig = () => import(/* webpackChunkName: "group-config" */ '../pages/config/rsaConfig.vue');

const Login = () => import(/* webpackChunkName: "group-login" */ '../pages/login/login.vue');

const UserList = () => import(/* webpackChunkName: "group-users" */ '../pages/users/userList.vue');
const UserEdit = () => import(/* webpackChunkName: "group-users" */ '../pages/users/userEdit.vue');

Vue.use(Router)

export default new Router({
  	routes: [
  		{
  			path: '/',
  			component: Login
  		},
  		{
  			path: '/login',
  			name: 'login',
  			component: Login
  		},
    	{
	      	path: '/index',
	      	component: Index,
	      	meta: {
                requiresAuth: true
            },
	      	redirect: 'home',	      	
	      	children: [
	      		{
		    		path: '/home',
		    		component: ModelView,
		    		children: [
		    			{
		    				path: '',
		    				name: 'home',
		    				component: Home
		    			}
		    		]
		    	},
		    	{
		    		path: '/apis',
		    		component: ModelView,
		    		children: [
		    			{
		    				path: '',
		    				name: 'apis',
		    				component: Apis
		    			},
		    			{
		    				path: 'add',
		    				name: 'apisAdd',
		    				component: ApisEdit
		    			},
		    			{
		    				path: 'edit/:itemName',
		    				name: 'apisEdit',
		    				component: ApisEdit
		    			},
		    			{
		    				path: 'plugins/:name',
		    				component: ModelView,
		    				children: [
		    					{
		    						path: '',
		    						name: 'apiPlugins',
		    						component: ApiPlugins
		    					},
		    					{
				    				path: 'add',
				    				name: 'apiPluginsAdd',
				    				component: ApiPluginsEdit
				    			},
				    			{
				    				path: 'edit',
				    				name: 'apiPluginsEdit',
				    				component: ApiPluginsEdit
				    			}
		    				]
		    			}
		    		]
		    	},
		    	// {
		    	// 	path: '/plugins',
		    	// 	component: ModelView,
		    	// 	children: [
		    	// 		{
		    	// 			path: '',
		    	// 			name: 'plugins',
		    	// 			component: Plugins
		    	// 		},
		    	// 		{
		    	// 			path: 'add',
		    	// 			name: 'pluginsAdd',
		    	// 			component: PluginsEdit
		    	// 		},
		    	// 		{
		    	// 			path: 'edit/:name',
		    	// 			name: 'pluginsEdit',
		    	// 			component: PluginsEdit
		    	// 		}
		    	// 	]
		    	// },
		    	{
		    		path: '/upstreams',
		    		component: ModelView,
		    		children: [
		    			{
		    				path: '',
		    				name: 'upstreams',
		    				component: Upstreams
		    			},
		    			{
		    				path: 'add',
		    				name: 'upstreamsAdd',
		    				component: UpstreamsEdit
		    			},
		    			{
		    				path: 'edit/:name',
		    				name: 'upstreamsEdit',
		    				component: UpstreamsEdit
		    			},
		    			{
		    				path: 'targets/:name',
		    				component: ModelView,
		    				children: [
		    					{
		    						path: '',
		    						name: 'targets',
		    						component: Targets
		    					},
		    					{
		    						path: 'add',
		    						name: 'targetsAdd',
		    						component: TargetsEdit
		    					}
		    				]
		    			},
		    			{
    						path: 'health/:name',
    						name: 'health',
    						component: Health
    					}
		    		]
		    	},
		    	{
		    		path: '/userList',
		    		component: ModelView,
		    		children: [
		    			{
		    				path: '',
		    				name: 'userList',
		    				component: UserList
		    			},
		    			{
		    				path: 'add',
		    				name: 'userAdd',
		    				component: UserEdit
		    			},
		    			{
		    				path: 'edit/:id',
		    				name: 'userEdit',
		    				component: UserEdit
		    			}
		    		]
		    	}
		    	// {
		    	// 	path: '/rsaConfig',
		    	// 	component: ModelView,
		    	// 	children: [
		    	// 		{
		    	// 			path: '',
		    	// 			name: 'rsaConfig',
		    	// 			component: RsaConfig
		    	// 		}
		    	// 	]
		    	// }
	      	]
    	}   	
  	]
})
