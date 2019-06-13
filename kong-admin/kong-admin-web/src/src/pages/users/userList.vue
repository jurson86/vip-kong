<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item>用户列表</el-breadcrumb-item>
		</el-breadcrumb>
		<div class="plus">
			<el-input prefix-icon="el-icon-search" placeholder="请输入真实姓名回车搜索" v-model="params.realName" @keyup.enter.native="searchData" style="width: 60%; margin-right: 10px;"></el-input>
			<el-button type="primary" icon="el-icon-plus" circle @click="addItem" title="新增用户"></el-button>
			<el-button type="info" icon="el-icon-info" circle @click="dialogFormVisible = true" title="为当前组添加用户"></el-button>
		</div>
		<el-table :data="tableData" stripe>
			<el-table-column prop="realName" label="真实姓名"></el-table-column>
			<el-table-column sortable prop="username" label="登录账号"></el-table-column>
			<!-- <el-table-column prop="email" label="email地址"></el-table-column> -->
			<el-table-column prop="mobile" label="用户号码"></el-table-column>
			<el-table-column label="状态">
				<template slot-scope="scope">
					<el-select v-model="scope.row.status" size="small" @change="changeStatus(scope.$index)">
						<el-option :value="0" label="禁用"></el-option>
						<el-option :value="1" label="启用"></el-option>
					</el-select>
				</template>
			</el-table-column>
			<el-table-column sortable prop="create_time" label="创建时间"></el-table-column>
			<el-table-column label="#" width="180">
				<template slot-scope="scope">
					<el-button type="warning" icon="el-icon-edit" circle @click="editItem(scope.row.userId)" title="修改用户"></el-button>
					<el-button type="danger" icon="el-icon-delete" circle @click="deleteItem(scope.row.userId)" title="删除用户"></el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination class="page" layout="total, prev, pager, next" :current-page="params.pageNum" :total="totalCount" @current-change="changePage" :page-size="params.pageSize"></el-pagination>

		<el-dialog title="添加组用户" :visible.sync="dialogFormVisible" width="500px" @close="closeDialog">
			<el-form ref="form" :model="formData" :rules="rules" label-width="100px" size="small">
				<el-form-item label="当前组">
					<el-input v-model="formData.curUserGroup" disabled placeholder="当前组"></el-input>
				</el-form-item>
				<el-form-item label="用户名" prop="username">
					<el-input v-model="formData.username" placeholder="用户名"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="addUserGroup('form')">确定</el-button>
					<el-button @click="dialogFormVisible = false">取消</el-button>
				</el-form-item>
			</el-form>
		</el-dialog>
	</div>
</template>

<script type="es6">
	import { mapGetters } from 'vuex';

	export default{
		data(){
			return{
				dialogFormVisible: false,
				tableData: [],
				totalCount: 0,
				params: {
					pageNum: 1,
					pageSize: 10,
					realName: ''
				},
				formData: {
					curUserGroup: sessionStorage.getItem('curUserGroup'),
					username: ''
				},
				rules: {
					username: [
						{ required: true, message: '请输入用户名', trigger: 'blur' }
					] 
				}
			}
		},

		created(){
			this.getTableData();
		},

		computed: {
	        ...mapGetters([
	        	'suffix',
	        	'curUserGroup'
	        ])
		},

		watch: {
			curUserGroup(val){
				if(val){
					this.getTableData();
					this.formData.curUserGroup = sessionStorage.getItem('curUserGroup');
				}
			}
		},

		methods: {
			addItem(){
				this.$router.push({name: 'userAdd'});
			},

			editItem(id){
				this.$router.push({
					name: 'userEdit',
					params: {
						id: id
					}
				})
			},

			deleteItem(id){
				this.$confirm('确认从组中删除该用户吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$get('/user/group/delete/' + id + this.suffix).then(res => {
						if(res.code == 0){
							this.$message({
								type: 'success',
								message: '删除成功'
							});

							setTimeout(() => {
								this.getTableData();
							}, 1000);
						}else{
							this.$message({
								type: 'warning',
								message: res.message
							})
						}
					})
				}).catch(() => {})
			},

			addUserGroup(form){
				this.$refs[form].validate((valid) => {
					if(valid){
						this.$post('/user/group/add' + this.suffix, {
							username: this.formData.username
						}).then(res => {
							if(res.code == 0){
								this.$message({
									type: 'success',
									message: '添加组用户成功'
								});
								setTimeout(() => {
									this.dialogFormVisible = false;
									this.getTableData();
								}, 1000)
							}else{
								this.$message({
									type: 'warning',
									message: res.message
								})
							}
						})
					}
				})
			},

			closeDialog(){
				this.$refs.form.resetFields();
			},

			changeStatus(index){
				this.$confirm('确定修改状态吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
						this.$get('/user/update_status/' + this.tableData[index].userId + '/' + this.tableData[index].status + this.suffix).then(res => {
						if(res.code == 0){
							this.$message({
								type: 'success',
								message: '修改成功'
							});
							this.getTableData();
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

			getTableData(){
				this.$post('/user/list' + this.suffix, this.params).then(res => {
					if(res.code == 0){
						this.totalCount = res.data.totalCount;
						this.tableData = res.data.dataList;
					}else{
						this.$message({
							type: 'warning',
							message: res.message
						})
					}
				})
			},

			changePage(cur){
				this.params.pageNum = cur;
				this.getTableData();
			},

			searchData(){
				this.params.pageNum = 1;
				this.getTableData();
			}
		}
	}
</script>