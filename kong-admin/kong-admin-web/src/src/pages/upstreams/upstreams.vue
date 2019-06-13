<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item>服务</el-breadcrumb-item>
		</el-breadcrumb>
		<div class="plus">
			<el-input prefix-icon="el-icon-search" placeholder="请输入ID或name回车搜索" v-model="inputValue" @keyup.enter.native="searchData" style="width: 80%;"></el-input>
			<el-button type="primary" icon="el-icon-plus" circle @click="addItem" style="float: right;" title="新增"></el-button>
		</div>
		<el-table :default-sort = "{prop: 'name'}" :data="tableData" stripe>
			<el-table-column prop="id" label="ID"></el-table-column>
			<el-table-column sortable prop="name" label="Name"></el-table-column>
			<el-table-column sortable prop="created_at" label="Created"></el-table-column>
			<el-table-column label="#" width="240">
				<template slot-scope="scope">
					<el-button type="warning" icon="el-icon-printer" circle @click="toTargets(scope.row.name)" title="查看targets"></el-button>
					<el-button type="info" icon="el-icon-info" circle @click="toHealth(scope.row.name)" title="查看健康状况"></el-button>
					<el-button type="primary" icon="el-icon-edit" circle @click="editItem(scope.row.name)" title="编辑"></el-button>
					<el-button type="danger" icon="el-icon-delete" circle @click="deleteItem(scope.row.name)" title="删除"></el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination class="page" layout="total, prev, pager, next" :current-page="page" :total="totalCount" @current-change="changePage" :page-size="size"></el-pagination>
	</div>
</template>

<script type="es6">
	import { mapMutations, mapGetters } from 'vuex';
	import mixinGetKongUrl from '@/utils/mixinGetKongUrl';

	export default{
		mixins: [mixinGetKongUrl],
		
		data(){
			return{
				page: 1,
				size: 10,
				totalCount: 0,
				nextUrl: '',
				tableData: [],
				inputValue: ''
			}
		},

		created(){
			this.getTableData();
		},

		computed: {
			...mapGetters([
				'curUserGroup'
			])
		},

		watch: {
			curUserGroup(val){
				if(val){
					this.getKongUrl();
					setTimeout(() => {
						this.getTableData();
					}, 1000);
				}
			}
		},

		methods: {
			searchData(){
				if(this.inputValue){
					this.$get('/upstreams/' + this.inputValue).then(res => {
						this.tableData = [];
						this.totalCount = 1;
						this.tableData.push(res);
					})
				}else{
					this.getTableData();
				}
			},

			addItem(){
				this.$router.push({name: 'upstreamsAdd'});
			},

			editItem(name){
				this.$router.push({
					name: 'upstreamsEdit',
					params: {
						name: name
					}
				})
			},

			deleteItem(name){
				this.$confirm(`确定删除Upstream "${name}"`, '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$delete('/upstreams/' + name).then(res => {
						if(res.status == 204){
							this.$message({
								type: 'success',
								message: '删除成功'
							})

							this.getTableData();
						}else{
							this.$message.error('删除失败');
						}
					})			
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					})
				})
			},

			toTargets(name){
				this.$router.push({
					name: 'targets',
					params: {
						name: name
					}
				})
			},

			toHealth(name){
				this.$router.push({
					name: 'health',
					params: {
						name: name
					}
				})
			},

			changePage(cur){
				if(cur == 1){
					this.getTableData();
				}else{
					this.nextUrl = this.Base64(cur.toString());
					this.$get('/upstreams?size=' + this.size + '&offset=' + this.nextUrl).then(res => {
						this.formatTableData(res);
					})
				}
			},

			getTableData(){
				this.tableData = [];
				this.$get('/upstreams?size=' + this.size).then(res => {
					this.totalCount = res.total;
					this.formatTableData(res);
				})
			},

			formatTableData(res){
				if(res.total > this.size && res.next){
					this.nextUrl = res.offset;
				}

				this.tableData = res.data;
				for(let i = 0; i < res.data.length; i++){
					let item = res.data[i];
					this.tableData[i].created_at = this.formatDateTime(item.created_at);
				}
			}
		}
	}
</script>