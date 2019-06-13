<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'upstreams'}">服务</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'targets'}">targets</el-breadcrumb-item>
			<el-breadcrumb-item>添加</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">添加</p>
			<el-form :model="formData" :rules="rules" ref="form" label-width="140px" size="small" class="form">
				<el-form-item label="Upstream">
					<el-input v-model="itemName" disabled></el-input>
				</el-form-item>
				<el-form-item label="Target" prop="target">
					<el-input v-model="formData.target" placeholder="127.0.0.1:8080"></el-input>
				</el-form-item>
				<el-form-item label="Weight" prop="weight">
					<el-input v-model.number="formData.weight"></el-input>
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
					target: '',
					weight: ''
				},
				rules: {
					target: [	
						{ required: true, message: '必填项', trigger: 'blur' },
						{
							pattern: /^([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})(\:[0-9]{1,4})?$/,
							message: '地址输入有误',
							trigger: 'blur'
						}
					],
					weight: [
						{ required: true, type: 'number', message: 'weight是必填项', trigger: 'blur' }
					]
				}
			}
		},

		computed: {
			itemName(){
				return this.$route.params.name;
			}
		},

		methods: {
			submitForm(form){
				this.$refs[form].validate((valid) => {
					if(valid){
						this.$post('/upstreams/' + this.itemName + '/targets', this.formData).then(res => {
							if(typeof(res) == 'object'){
								this.$message({
									message: '新增成功！',
									type: 'success'
								})
								setTimeout(() => {
									this.$router.push({name: 'targets'});
								}, 1000);
							}else{
								this.$message.error('新增失败！');
							}
						})
					}else{
						this.$message({
							type: 'warning',
							message: '缺少必填项或填写有误！'
						})
					}
				})
			}
		}
	}
</script>