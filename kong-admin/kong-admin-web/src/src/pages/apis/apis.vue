<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item>APIs</el-breadcrumb-item>
		</el-breadcrumb>
		<el-button class="plus" type="primary" icon="el-icon-plus" circle @click="addItem"></el-button>
		<el-table :default-sort = "{prop: 'name'}" :data="tableData" stripe>
			<el-table-column sortable prop="name" label="Name"></el-table-column>
			<el-table-column prop="uris" label="Uri(s)"></el-table-column>
			<el-table-column prop="methods" label="Method(s)"></el-table-column>
			<el-table-column prop="upstream_url" label="Upstream url"></el-table-column>
			<el-table-column sortable prop="created_at" label="Created"></el-table-column>
			<el-table-column label="#" width="180">
				<template slot-scope="scope">
					<el-button type="primary" icon="el-icon-edit" circle @click="editItem(scope.row.name)"></el-button>
					<el-button type="warning" icon="el-icon-setting" circle @click="toPlugins(scope.row.name)"></el-button>
					<el-button type="danger" icon="el-icon-delete" circle @click="deleteItem(scope.row.name)"></el-button>
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
				totalCount: 0,
				size: 10,
				nextUrl: '',
				tableData: []
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
			changePage(cur){
				if(cur == 1){
					this.getTableData();
				}else{
					this.nextUrl = this.Base64(cur.toString());
					this.$get('/apis?size=' + this.size + '&offset=' + this.nextUrl).then(res => {
						this.formatTableData(res);
					})
				}
			},

			getTableData(){
				this.$get('/apis?size=' + this.size).then(res => {
					this.totalCount = res.total;
					this.formatTableData(res);
				});								
			},

			formatTableData(res){
				if(res.total > this.size && res.next){
					this.nextUrl = res.offset;
				}

				this.tableData = res.data;
				for(let i = 0; i < res.data.length; i++){
					let item = res.data[i];
					this.tableData[i].created_at = this.formatDateTime(item.created_at);

					if(item.uris && item.uris.length > 1){
						this.tableData[i].uris = item.uris.join(',');
					}
				}
			},

			addItem(){
				this.$router.push({name: 'apisAdd'});
			},

			editItem(name){
				this.$router.push({
					name: 'apisEdit',
					params: {
						itemName: name
					}
				})
			},

			deleteItem(name){
				this.$confirm(`确定删除API "${name}"`, '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$delete('/apis/' + name).then(res => {
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

			toPlugins(name){
				this.$router.push({
					name: 'apiPlugins',
					params: {
						name: name
					}
				})
			}
		}
	}
</script>