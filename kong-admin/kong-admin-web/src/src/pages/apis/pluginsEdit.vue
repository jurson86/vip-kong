<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'apis'}">APIs</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'apiPlugins'}">{{apiName}}的插件</el-breadcrumb-item>
			<el-breadcrumb-item>{{breadcrumbName}}</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">{{breadcrumbName}}</p>
			<el-form :model="formData" :rules="rules" ref="form" label-width="240px" size="small" class="form-lg">
				<el-form-item label="Api名称">
					<el-input v-model="apiName" disabled></el-input>
				</el-form-item>
				<el-form-item label="插件名">
					<el-select v-if="!itemId" v-model="formData.pluginsName" class="select-sm" @change="getPluginInfo">
						<el-option v-for="item in pluginsArr" :value="item" :key="item">{{item}}</el-option>
					</el-select>
					<el-input v-if="itemId" v-model="itemName" disabled></el-input>
				</el-form-item>
				<template v-if="formData.pluginsName || itemName">
					<el-form-item v-for="(item, index) in pluginInfo" :key="index" :label="item.key">
						<template v-if="item.value.child">
							<el-card shadow="hover">
								<div class="child-info" v-for="(subItem, subIndex) in item.value.child" :key="subIndex">
									<span>{{subItem.key}}</span>
									<el-input v-if="subItem.value.type != 'number' && subItem.value.type != 'boolean'" v-model="subItem.value.default"></el-input>

									<el-input-number controls-position="right" v-if="subItem.value.type == 'number'" v-model="subItem.value.default" :min="1"></el-input-number>

									<el-checkbox v-if="subItem.value.type == 'boolean'" v-model="subItem.value.default">{{subItem.key}}</el-checkbox>
								</div>
							</el-card>
						</template>
						<template v-else>
							<el-input v-if="(item.value.type == 'string' && !item.value.enum) || (item.value.type == 'array' && !item.value.enum) || item.value.type == 'url'" v-model="item.value.default"></el-input>

							<el-input-number controls-position="right" v-if="item.value.type == 'number'" v-model="item.value.default"></el-input-number>

							<el-select v-if="item.value.enum" :multiple="item.value.type == 'array'" v-model="item.value.default" class="select-sm">
								<el-option v-for="sItem in item.value.enum" :value="sItem" :key="sItem">{{sItem}}</el-option>
							</el-select>

							<el-checkbox v-if="item.value.type == 'boolean'" v-model="item.value.default">{{item.key}}</el-checkbox>
						</template>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="submitForm('form')">确定</el-button>
						<el-button @click="$router.go(-1)">取消</el-button>
					</el-form-item>
				</template>	
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				pluginsArr: [],
				formData: {
					pluginsName: ''
				},
				rules: {},
				pluginInfo: [],
				config: {}
			}
		},

		computed: {
			breadcrumbName(){
				return this.itemId ? '修改' : '新增';
			},

			itemId(){
				return this.$route.query.id;
			},

			itemName(){
				return this.$route.query.name;
			},

			apiName(){
				return this.$route.params.name;
			}
		},

		created(){
			this.getPluginsList();

			if(this.itemName){
				this.getDetail();
			}
		},

		methods: {
			getPluginsList(){
				this.$get('/plugins/enabled').then(res => {
					// this.pluginsArr = res.enabled_plugins;
					for(let item of res.enabled_plugins){
						if(item.match(/tdw/)){
							this.pluginsArr.push(item);
						}
					}
				})
			},

			getPluginInfo(name){
				this.pluginInfo = [];
				this.config = {};

				const pName = name ? name : this.formData.pluginsName;

				this.$get('/plugins/schema/' + pName).then(res => {
					for(let key in res.fields){
						this.pluginInfo.push({
							key: key, 
							value: res.fields[key]
						})
					}

					for(let item of this.pluginInfo){
						if(item.value.schema){
							item.value.child = [];
							for(let key in item.value.schema.fields){
								item.value.child.push({
									key: key,
									value: item.value.schema.fields[key]
								})

								let child = item.value.child;

								for(let cItem of child){
									if((typeof(cItem.value.default) == 'object' || typeof(cItem.value.default) == 'array') && !cItem.value.enum){
										cItem.value.default = JSON.stringify(cItem.value.default) == '{}' ? '' : JSON.stringify(cItem.value.default);
									}
								}
							}
						}

						if(item.value.default && (typeof(item.value.default) == 'object' || typeof(item.value.default) == 'array') && !item.value.enum){
							item.value.default = JSON.stringify(item.value.default) == '{}' ? '' : JSON.stringify(item.value.default);
						}
					}
				});
			},

			submitForm(form){
				for(let item of this.pluginInfo){	

					if((item.value.default || item.value.type === 'boolean') && !item.value.child){
						this.config[item.key] = item.value.default;
					}else if(item.value.child){
						this.config[item.key] = {};
						for(let subItem of item.value.child){

							if(subItem.value.default || subItem.value.type === 'boolean'){
								this.config[item.key][subItem.key] = subItem.value.default;
							}else{
								this.config[item.key][subItem.key] = '';
							}
						}
					}else{
						this.config[item.key] = '';
					}
				}

				if(this.itemId){
					this.$patch('/plugins/' + this.itemId, {
						name: this.itemName,
						config: this.config
					}).then(res => {
						if(res.config){
							this.$message({
								type: 'success',
								message: '修改成功'
							});

							setTimeout(() => {
								this.$router.push({name: 'apiPlugins'});
							}, 1000);
						}else{
							this.$message.error('修改失败');
						}
					})
				}else{
					this.$post('/apis/' + this.apiName + '/plugins', {
						name: this.formData.pluginsName,
						config: this.config
					}).then(res => {
						if(res.config){
							this.$message({
								type: 'success',
								message: '新增成功'
							});

							setTimeout(() => {
								this.$router.push({name: 'apiPlugins'});
							}, 1000);
						}else{
							this.$message.error('新增失败');
						}
					})
				}
			},

			getDetail(){
				this.getPluginInfo(this.itemName);

				let config = [];

				this.$get('/apis/' + this.apiName + '/plugins/' + this.itemId).then(res => {
					for(let key in res.config){

						config.push({
							key: key,
							value: {
								default: res.config[key]
							}
						});					
					}
					
					for(let item of this.pluginInfo){
						for(let cItem of config){
							if(item.value.child){
								cItem.value.child = [];

								for(let key in res.config){
									for(let cKey in res.config[key]){
										for(let childItem of item.value.child){
											if(key == cItem.key && childItem.key == cKey){
												cItem.value.child.push({
													key: cKey,
													value: {
														default: res.config[key][cKey],
														type: childItem.value.type
													}												
												})
											}
										}
									}
								}
							}else{
								if(item.key == cItem.key){
									cItem.value.type = item.value.type;

									if(item.value.enum){
										cItem.value.enum = item.value.enum;

										if(JSON.stringify(cItem.value.default) === '{}'){
											cItem.value.default = [];
										}
									}
								}
							}
						}
					}

					this.pluginInfo = config;

					for(let item of this.pluginInfo){
						if(item.value.default && (typeof(item.value.default) == 'object' || typeof(item.value.default) == 'array') && !item.value.enum){
							item.value.default = JSON.stringify(item.value.default) == '{}' ? '' : JSON.stringify(item.value.default);
						}

						if(item.value.child){
							let child = item.value.child;

							for(let cItem of child){
								if((typeof(cItem.value.default) == 'object' || typeof(cItem.value.default) == 'array') && !cItem.value.enum){
									cItem.value.default = JSON.stringify(cItem.value.default) == '{}' ? '' : JSON.stringify(cItem.value.default);
								}
							}
						}
					}
				})
			}
		}
	}
</script>