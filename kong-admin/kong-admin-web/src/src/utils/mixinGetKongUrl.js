
import { mapMutations, mapGetters } from 'vuex';

export default{
	data(){
		return{
			//
		}
	},

	created(){},

	computed: {
		...mapGetters([
			'suffix'
		])
	},

	methods: {
		...mapMutations([
			'KONG_ADMIN_ULR'
		]),

		getKongUrl(){
			this.$get('/kong/url' + this.suffix).then(res => {
				if(res.code == 0){
					this.KONG_ADMIN_ULR(res.data.server);						
				}else{
					this.$message({
						type: 'info',
						message: res.message
					});
				}
			})
		}
	}
}