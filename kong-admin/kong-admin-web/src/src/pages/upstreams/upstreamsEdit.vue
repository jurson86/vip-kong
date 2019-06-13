<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'upstreams'}">服务</el-breadcrumb-item>
			<el-breadcrumb-item>{{breadcrumbName}}</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">{{breadcrumbName}}</p>
			<el-form :model="formData" :rules="rules" ref="form" label-width="140px" size="small" class="form">
				<template v-if="itemName && formData.id">
					<el-form-item label="ID">
						<el-input v-model="formData.id" disabled></el-input>
					</el-form-item>
				</template>
				<el-form-item label="名称" prop="name">
					<el-input v-model="formData.name" placeholder="名称"></el-input>
				</el-form-item>
				<el-form-item label="健康状态检测地址">
					<el-input v-model="formData.healthchecks.active.http_path" placeholder="健康状态检测地址"></el-input>
				</el-form-item>
				<el-form-item label="健康检测间隔时间">
					<el-input v-model="formData.healthchecks.active.healthy.interval"></el-input>
					<p class="text">设置健康检测间隔时间，设置为：0 不启用任何健康机制</p>
				</el-form-item>
				<el-form-item label="请求失败阀值">
					<el-input v-model="formData.healthchecks.active.unhealthy.tcp_failures"></el-input>
					<p class="text">设置请求失败阀值，是否启动访问性健康检测，不启用为：0</p>
				</el-form-item>
				<el-form-item label="超时时间">
					<el-input v-model="formData.healthchecks.active.unhealthy.timeouts"></el-input>
					<p class="text">设置超时时间，启动超时健康检测机制，不启用为：0</p>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="submitForm('form')">确定</el-button>
					<el-button @click="$router.go(-1)">取消</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				formData: {
					name: '',
					healthchecks: { 
						active : { 
							http_path : '/health' ,
							healthy: {
								interval: 0
							},
							unhealthy: {
								tcp_failures: 0,
								timeouts: 0
							}
						}
					}
				},
				rules: {
					name: [	
						{ required: true, message: '名称是必填项', trigger: 'blur' }
					]
				}
			}
		},

		computed: {
			breadcrumbName(){
				return this.itemName ? '修改' : '添加';
			},

			itemName(){
				return this.$route.params.name;
			}
		},

		created(){
			if(this.itemName){
				this.getDetail();
			}
		},

		methods: {
			submitForm(form){
				this.$refs[form].validate((valid) => {
					if(valid){
						if(this.itemName){
							this.$patch('/upstreams/' + this.itemName, this.formData).then(res => {
								if(typeof(res) === 'object'){
									this.$message({
										message: '修改成功！',
										type: 'success'
									})
									setTimeout(() => {
										this.$router.push({name: 'upstreams'});
									}, 1000);
								}
							})
						}else{
							this.formData.hash_on = 'none';
							this.formData.hash_fallback = 'none';

							this.$post('/upstreams', this.formData).then(res => {
								if(typeof(res) === 'object'){
									this.$message({
										message: '新增成功！',
										type: 'success'
									})
									setTimeout(() => {
										this.$router.push({name: 'upstreams'});
									}, 1000);
								}else{
									this.$message.error('新增失败！');
								}
							})
						}
					}else{
						this.$message({
							type: 'warning',
							message: '缺少必填项或填写有误！'
						})
					}
				})
			},

			getDetail(){
				this.$get('/upstreams/' + this.itemName).then(res => {
					this.formData = res;
				})
			}
		}
	}
</script>