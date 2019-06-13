<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item>插件</el-breadcrumb-item>
		</el-breadcrumb>
		<el-button class="plus" type="primary" icon="el-icon-plus" circle @click="addItem"></el-button>
		<el-table :data="tableData" stripe>
			<el-table-column prop="name" label="Name"></el-table-column>
			<el-table-column prop="api" label="API"></el-table-column>
			<!-- <el-table-column prop="consumer" label="Consumer"></el-table-column> -->
			<el-table-column label="Status">
				<template slot-scope="scope">
					<el-switch v-model="scope.row.enabled" active-text="on" inactive-text="off" @change=""></el-switch>
				</template>
			</el-table-column>
			<el-table-column prop="created_at" label="Created"></el-table-column>
			<el-table-column label="#" width="180">
				<template slot-scope="scope">
					<el-button type="primary" icon="el-icon-edit" circle @click="editItem(scope.row.name)"></el-button>
					<el-button type="danger" icon="el-icon-delete" circle @click="deleteItem(scope.row.name)"></el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination class="page" layout="total, prev, pager, next" :current-page="page" :total="totalCount" @current-change="changePage" :page-size="size"></el-pagination>
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				page: 1,
				totalCount: 0,
				size: 10,
				nextUrl: '',
				tableData: []
			}
		},

		created(){
			// this.getTableData();
		},

		computed: {
			itemName(){
				return this.$route.query.name;
			}
		},

		methods: {
			addItem(){
				this.$router.push({name: 'pluginsAdd'});
			},

			editItem(name){
				this.$router.push({
					name: 'pluginsEdit',
					params: {
						name: name
					}
				})
			},

			deleteItem(name){
				this.$confirm(`确定删除吗？`, '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					// this.$delete('/upstreams/' + name).then(res => {
					// 	if(res.status == 204){
					// 		this.$message({
					// 			type: 'success',
					// 			message: '删除成功'
					// 		})

					// 		this.getTableData();
					// 	}else{
					// 		this.$message.error('删除失败');
					// 	}
					// })			
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					})
				})
			},

			changePage(cur){
				if(cur == 1){
					this.getTableData();
				}else{
					this.$get(this.nextUrl).then(res => {
						this.formatTableData(res);
					})
				}
			},

			getTableData(){
				let url = '';
				if(this.itemName){
					url = '/apis/' + this.itemName + '/plugins' + '?size=' + this.size;
				}else{
					// url = '/proxy/plugins?size=' + this.size;
				}

				this.$get(url).then(res => {
					this.totalCount = res.total;
					this.formatTableData(res);
				})
			},

			formatTableData(res){
				if(res.total > this.size && res.next){
					this.nextUrl = res.next;
				}

				this.tableData = res.data;
				for(let i = 0; i < res.data.length; i++){
					let item = res.data[i];
					this.tableData[i].created_at = this.formatDateTime(item.created_at);
					this.tableData[i].api = this.itemName;
				}
			}
		}
	}
</script>