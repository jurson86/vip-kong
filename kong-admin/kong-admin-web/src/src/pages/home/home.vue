<template>
	<div>
		<h3>Welcome to kong admin</h3>
		<el-card class="form-card">
			<div slot="header" class="clearfix" style="line-height: 40px;">
				<span>kong服务列表</span>			
				<div style="float: right;">
					<el-button type="text" size="small" @click="clearCache">清理全部缓存</el-button>
					<!-- <el-button type="text" size="small" @click="dialogFormVisible = true">新增kong服务</el-button> -->
				</div>
			</div>
			<el-table :data="kongUrlList" :row-class-name="tableRowClassName">
				<el-table-column type="index" width="50"></el-table-column>
				<el-table-column prop="server" label="kongUrl"></el-table-column>
				<el-table-column label="操作">
					<template slot-scope="scope">
						<el-button type="info" v-if="scope.row.server == kongAdminUrl" size="small">已绑定</el-button>
						<el-button type="primary" v-else size="small" @click="changeKongUrl(scope.row.server)">绑定</el-button>						
						<!-- <el-button type="warning" size="small" @click="deleteKong(scope.row.server)">删除</el-button> -->
						<el-button type="danger" size="small" @click="clearSingleCache(scope.row.server)">清理缓存</el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>
		<!-- <el-dialog title="新增kong服务" :visible.sync="dialogFormVisible" width="500px" @close="closeDialog">
			<el-form ref="form" :model="formData" :rules="rules" label-width="100px" size="small">
				<el-form-item label="kong url" prop="kongUrl">
					<el-input v-model="formData.kongUrl" placeholder="kong url"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="submitForm('form')">确定</el-button>
					<el-button @click="dialogFormVisible = false">取消</el-button>
				</el-form-item>
			</el-form>
		</el-dialog> -->
	</div>
</template>

<script type="es6">
	import { mapMutations, mapGetters } from 'vuex';
	import mixinGetKongUrl from '@/utils/mixinGetKongUrl';

	export default{
		mixins: [mixinGetKongUrl],

		data(){
			return{				
				dialogFormVisible: false,
				kongUrlList: [],
				formData: {
					kongUrl: ''
				},
				rules: {
					kongUrl: [	
						{ required: true, message: '必填项', trigger: 'blur' }
					]
				}
			}
		},

		computed: {
	        ...mapGetters([
	        	'kongAdminUrl',
	        	// 'suffix',
	        	'curUserGroup'
	        ])
		},

		created(){
			this.getKongUrl();
			this.getKongUrlList();
		},

		watch: {
			curUserGroup(val){
				if(val){
					this.getKongUrlList();
					this.getKongUrl();
				}
			}
		},

		methods: {
			getKongUrlList(){
				this.kongUrlList = [];

				this.$get('/kong/url/list' + this.suffix).then(res => {
					if(res.code == 0){	
						this.kongUrlList = res.data;					
					}else{
						this.$message({
							type: 'info',
							message: res.message
						});
					}
				})
			},

			tableRowClassName({row, rowIndex}){
				if(row.server == this.kongAdminUrl){
					return 'primary-row';
				}

				return '';
			},

			changeKongUrl(val){
				this.$confirm('确定修改吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$post('/user/group/bind_server' + this.suffix, {
						server: val,
						groupName: this.curUserGroup
					}).then(res => {
						if(res.code == 0){
							this.$message({
								type: 'success',
								message: '修改kong服务成功！'
							});
							this.getKongUrl();
							this.getKongUrlList();
						}else{
							this.$message({
								type: 'warning',
								message: res.message
							})
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消修改'
					})
				})				
			},

			// submitForm(form){
			// 	this.$refs[form].validate((valid) => {
			// 		if(valid){
			// 			this.$post('/kong/url?kongUrl=' + this.formData.kongUrl + this.suffix).then(res => {
			// 				if(res.code == 0){
			// 					this.$message({
			// 						type: 'success',
			// 						message: 'kong服务设置成功！'
			// 					});

			// 					setTimeout(() => {
			// 						this.dialogFormVisible = false;
			// 						this.getKongUrlList();
			// 					}, 1000);
			// 				}else{
			// 					this.$message({
			// 						type: 'warning',
			// 						message: res.message
			// 					})
			// 				}
			// 			})
			// 		}
			// 	})
			// },

			closeDialog(){
				this.$refs.form.resetFields();
			},

			clearCache(){
				this.$confirm('清理缓存时，确保当前网关无流量进入！确认清理全部缓存吗？', '提示', {
					type: 'warning',
					confirmButtonText: '确定',
					cancelButtonText: '取消'
				}).then(() => {
					this.$delete('/kong/delete/caches' + this.suffix).then(res => {
						if(res.data.code == 0){
							this.$message({
								type: 'success',
								message: '缓存清理成功！'
							})

							setTimeout(() => {
								this.getKongUrlList();
							}, 1000);
						}else{
							this.$message({
								type: 'warning',
								message: res.data.message
							})
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消'
					})
				})
			},

			// deleteKong(url){
			// 	this.$confirm('确定删除吗？', '提示', {
			// 		confirmButtonText: '确定',
			// 		cancelButtonText: '取消',
			// 		type: 'warning'
			// 	}).then(() => {
			// 		this.$delete('/kong/url?kongUrl=' + url + this.suffix).then(res => {
			// 			if(res.data.code == 0){
			// 				this.$message({
			// 					type: 'success',
			// 					message: '删除kong服务成功！'
			// 				});

			// 				setTimeout(() => {
			// 					this.getKongUrlList();
			// 				}, 1000);
			// 			}else{
			// 				this.$message({
			// 					type: 'warning',
			// 					message: res.data.message
			// 				})
			// 			}
			// 		})
			// 	}).catch(() => {
			// 		this.$message({
			// 			type: 'info',
			// 			message: '已取消'
			// 		})
			// 	})			
			// },

			clearSingleCache(url){
				this.$confirm('清理缓存时，确保当前网关无流量进入！确认清理缓存吗？', '提示', {
					type: 'warning',
					confirmButtonText: '确定',
					cancelButtonText: '取消'
				}).then(() => {
					this.$delete('/kong/delete/cache?kongUrl=' + url + this.suffix).then(res => {
						console.info(res)
						if(res.data.code == 0){
							this.$message({
								type: 'success',
								message: '缓存清理成功！'
							})

							setTimeout(() => {
								this.getKongUrlList();
							}, 1000);
						}else{
							this.$message({
								type: 'warning',
								message: res.data.message
							})
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消'
					})
				})
			}
		}
	}
</script>