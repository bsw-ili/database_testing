<template>
    <div class="main">
        <el-page-header title="返回" @back="this.$router.back()">
            <template #content>
                <span class="text-large font-600 mr-3"> {{ name }} </span>
            </template>
            <template #extra>
                <el-dropdown>
                    <span class="el-dropdown-link">
                        {{ live.weather }}&nbsp;
                        {{ live.temperature }}℃
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-card class="box-card">
                            <template #header>
                                <div class="card-header">
                                    <span>
                                        {{ city }}
                                        <el-icon size="25px">
                                            <component :is="getIcon(live.weather)"></component>
                                        </el-icon>
                                    </span>

                                    <span class="reporttime">更新时间：{{ forecasts[0].reporttime }}</span>
                                </div>
                            </template>
                            <div v-for="o in forecasts[0].casts" :key="o" class="item">
                                <span>星期{{ changeToCh(o.week) }}&nbsp;&nbsp;</span>
                                <span>{{ o.date }}</span>&nbsp;&nbsp;
                                <div style="float: right;">
                                    <span
                                        style="margin-right: 10px;">{{ isEquals(o.dayweather, o.nightweather) }}</span>&nbsp;&nbsp;
                                    <span>{{ o.nighttemp }}/{{ o.daytemp }}℃</span>
                                </div>
                            </div>
                        </el-card>
                    </template>
                </el-dropdown>
            </template>
        </el-page-header>

        <div class="rate">
            <div class="score">
                <div class="score_num_wrap">
                    <span class="num_text">{{ parseFloat(commentAggregation.scoreAvg).toFixed(1) }}</span>
                    <span class="total_text">/5</span>
                </div>
                <div class="score_tip">好评率{{ commentAggregation.goodRatePercent }}%</div>
            </div>
            <div class="tag_score">
                <div class="tag_score_item" v-for="i in commentAggregation.subItemTags">
                    <div class="tag_score_title">{{ i.displayName }}</div>
                    <el-rate v-model="i.scoreAvg" disabled show-score text-color="#ff9900" score-template="{value}"
                        size="large" />
                </div>
            </div>
            <div ref="keyWords" class="keyWords"></div>
        </div>

        <div class="comments">
            <div class="comments_title">评论</div>
            <!-- <el-scrollbar height="500px"> -->
                <div class="comment">
                    <div ref="scroll" class="vs-list-container" :style="{height: itemHeight * size + 'px'}" @scroll="handleScroll">
                        <div v-for="(item, i) in comments" :key="i" class="author-title reply-father">
                            <el-avatar class="header-img" :size="40" :src="item.userInfo.avatarUrl" />
                            <div class="author-info">
                                <span class="author-name">{{ item.userInfo.displayName }}</span>
                                <span class="author-time">{{ this.parseTime(item.commentTime) }}</span>
                            </div>
                            <div class="comment_tag">
                                <el-tag class="mx-1" effect="plain" round>
                                    {{ item.score }}分
                                </el-tag>
                                <el-tag :type="tag(item.tourTypeInfo.tourTypeName)"
                                    style="margin-left: 3px">{{ item.tourTypeInfo.tourTypeName }}</el-tag>
                            </div>
                            <div class="talk-box">
                                <p>
                                    <span class="reply"> {{ item.content }}</span>
                                </p>

                                <el-row :gutter="20" v-if="item.attachments">
                                    <el-col :span="3" v-for=" (j, index) in item.attachments">
                                        <el-image lazy class="img" :src="j" :preview-src-list="item.attachments" fit="cover"
                                            :initial-index="index" />
                                    </el-col>
                                </el-row>
                            </div>
                        </div>
                    </div>
                </div>
            <!-- </el-scrollbar> -->
        </div>
        <el-pagination class="page" @current-change="handleCurrentChange" v-model:current-page="pageNum"
			:page-size="pageSize" layout="prev, pager, next" :total="(total*10)">
		</el-pagination>
    </div>
</template>
<script>
import axios from 'axios'
import echarts from 'echarts';
import 'echarts-wordcloud'
const throttle = (fn, delay) => {
    let stratTimer
    let endTimer
    return function() {
        var args = Array.prototype.slice.call(arguments, 1)
        if (stratTimer) {
            return
        }
        stratTimer = setTimeout(() => {
            clearTimeout(stratTimer)
            stratTimer = null

            endTimer = setTimeout(() => {
                clearTimeout(endTimer)
                endTimer = null
                fn.apply(this, args)
            }, delay)
        }, delay)
        if (endTimer) {
            return
        }
        fn.apply(this, args)
    }
}

export default {
    data() {
        return {
            pageSize: 10,
			total: 1,
			pageNum: 1,
            itemHeight: 20,
            size: 20,
            start: 0,
            end: 20,
            offset: 40, // 显示列表前后多预置的项目
            value: '3.0',
            productId: '',
            type: '',
            city: '',
            name: '',
            live: {},
            forecasts: [{}],
            commentAggregation: {},
            subItemTags: [
            ],
            words: [{
                name: '',
                value: '',
            }],
            comments: [{
                attachments: [],
                content: '',
                tourTypeInfo: {
                    tourTypeName: ''
                },
                userInfo: {
                    "avatarUrl": ''
                }
            }],
        }
    }, 
    computed: {
        // 显示列表需要前后多预置多几项，防止滚动白屏问题
        showList() {
            const list = this.comments.slice(this.start, this.end)
            // 显示列表预制
            // 列表前预制
            for (let i = 1; i <= this.offset; i++) {
                list.unshift(this.list[this.start-i] || '')
            }
            // 列表后预制，若已到达数据列表最底部，不再预制
            for (let i = 1; i <= this.offset; i++) {
                if (this.end + i <= this.list.length) {
                    list.push(this.list[this.end - 1 + i])
                }
            }
            return list
        }
    },

    methods: {
        handleCurrentChange(val) {
			this.pageNum = val;
			this.getComment();
            // console.log(this.$refs.scroll)
            this.$refs['scroll'].scrollTop=0
		},
        handleScroll: throttle(function() {
            // console.log(this.$refs['scroll'].scrollTop)
            const scrollTop = this.$refs.scroll.scrollTop
            this.start = Math.floor(scrollTop / this.itemHeight)
            this.end = this.start + this.size
        }, 100),
        getIcon(val) {
            val = String(val);
            if (val.includes("晴"))
                return "Sunny"
            if (val.includes("云") || val.includes("阴"))
                return "MostlyCloudy"
            if (val.includes("雨"))
                return "Drizzling"
            else
                return null;
        },
        set_total(val,data){
            for(let i=0;i<data.commentAggregation.tourTypeTags.length;i++){
                console.log(data.commentAggregation.tourTypeTags[i].displayName.toString())
                if (data.commentAggregation.tourTypeTags[i].displayName === val) {
                    console.log(data.commentAggregation.tourTypeTags[i].totalCount)
                    this.total = data.commentAggregation.tourTypeTags[i].totalCount % this.pageSize == 0 ? data.commentAggregation.tourTypeTags[i].totalCount / this.pageSize : data.commentAggregation.tourTypeTags[i].totalCount / this.pageSize + 1;
                    this.total = Math.floor(this.total)
                    console.log(this.total)

                    break;
                }
            }

        },
        getComment() {
            this.$axios.get("/comment/getComments?productId=" + this.productId + "&type=" + this.type
            + "&pageNum=" + this.pageNum + "&pageSize=" + this.pageSize).then(response => {
                var data = response.data.data[0]
                this.commentAggregation = data.commentAggregation
                this.comments = data.comments
                this.words = JSON.parse(JSON.stringify(data.commentAggregation.tourTypeTags)
                    .replace(/displayName/g, 'name')
                    .replace(/totalCount/g, 'value')
                )
                switch(this.type){
                    case '':
                        this.total = data.totalCount % this.pageSize == 0 ? data.totalCount / this.pageSize : data.totalCount / this.pageSize + 1 ;
                        this.total = Math.floor(this.total);
                        console.log(Math.floor(this.total))
                        break;
                    case "朋友出游":
                        this.set_total("朋友出游",data)
                        break;
                    case "情侣夫妻":
                        this.set_total("情侣夫妻",data)
                        break;
                    case "家庭亲子":
                        this.set_total("家庭亲子",data)
                        break;
                    case "单独旅行":
                        this.set_total("单独旅行",data)
                        break;
                    case "其他出游":
                        this.set_total("其他出游",data)
                        break;
                }
                // console.log(Math.floor(this.total))
                if (data.commentAggregation.aiTags) {
                    this.words = this.words.concat(
                        JSON.parse(JSON.stringify(data.commentAggregation.aiTags)
                            .replace(/displayName/g, 'name')
                            .replace(/totalCount/g, 'value')
                        )
                    )
                }
            }).catch(error => {
                this.$message({
                    type: 'warning',
                    message: "AI标签暂不可筛选！"
                });
            })
            window.scrollTo(0,0);
        },
        ObjectToString(val) {
            var str = []
            for (let i = 0; i < val.length; i++) {
                str.push(val[i].url)
            }
            // console.log(str);
            return str;
        },
        parseTime(timestamp) {
            var date = new Date(parseInt(timestamp));
            return date.toLocaleDateString().replace(/\//g, "-") + " " + date.toTimeString().substr(0, 8)
        },
        tag(val) {
            switch (val) {
                case "朋友出游":
                    return "";
                case "情侣夫妻":
                    return "danger";
                case "家庭亲子":
                    return "success";
                case "单独旅行":
                    return "warning";
                case "其他出游":
                    return "info";
            }
        },
        getLiveWeather() {
            axios.get("https://restapi.amap.com/v3/weather/weatherInfo?key=bed585491bcee4d57d13fbafc6f27dfb&extensions=base"
                + "&city=" + this.city
            ).then(response => {
                this.live = response.data.lives[0]
            })
        },
        getAllWeather() {
            axios.get("https://restapi.amap.com/v3/weather/weatherInfo?key=bed585491bcee4d57d13fbafc6f27dfb&extensions=all"
                + "&city=" + this.city
            ).then(response => {
                this.forecasts = response.data.forecasts
            })
        },
        changeToCh(val) {
            switch (val) {
                case "1": return "一";
                case "2": return "二";
                case "3": return "三";
                case "4": return "四";
                case "5": return "五";
                case "6": return "六";
                case "7": return "日";
            }
        },
        isEquals(a, b) {
            if (a === b)
                return a;
            else
                return a + '转' + b;
        },
        initEchart() {
            //获取DOM节点并初始化
            const el = this.$refs.keyWords
            const myChart = echarts.init(el)

            const option = {
                tooltip: {
                    show: true
                },
                series: [
                    {
                        //用来调整词之间的距离
                        gridSize: 20,
                        //用来调整字的大小范围
                        sizeRange: [18, 40],
                        //用来调整词的旋转方向，，[0,0]--代表着没有角度，也就是词为水平方向
                        rotationRange: [0, 0],
                        type: 'wordCloud',
                        size: ['95%', '95%'],
                        textRotation: [0, 90],
                        textPadding: 0,
                        autoSize: {
                            enable: true,
                            minSize: 14
                        },
                        textStyle: {
                            normal: {
                                color: function () {
                                    return `rgb(${[
                                        Math.round(Math.random() * 200 + 55),
                                        Math.round(Math.random() * 200 + 55),
                                        Math.round(Math.random() * 200 + 55)
                                    ].join(',')})`
                                }
                            }

                        },
                        data: this.words
                    }
                ]
            }
            //设置图表的参数
            myChart.setOption(option)
            var _this = this
            myChart.on("click", function (params) {
                if (params.data.name) {
                    _this.type = params.data.name
                    // console.log(_this.type);
                    _this.getComment()
                }
            });
        }
    },
    created() {
        this.productId = this.$route.query.productId;
        this.city = this.$route.query.city;
        this.name = this.$route.query.name;
        this.getLiveWeather();
        this.getAllWeather();
        this.getComment()

    },
    mounted() {
        for (let i = 1; i <= 10000; i++) {
            this.comments.push(i)
        }
        setTimeout(() => {
            this.initEchart()
        }, 500)
    },
}
</script>
<style lang="less" scoped>
.vs-list-container {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    border: solid 1px #e1e0e0;
    overflow-y: auto;
}

.vs-list {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
}

.main {
    width: 1000px;
    height: 720px;
}

.box-card {
    width: 480px;
    font-size: 25px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.item {
    font-size: 18px;
    margin-bottom: 18px;
}

.reporttime {
    font-size: 10px;
}

.rate {
    // background-color: #f6f8fa;
    height: 180px;
    width: 1000px;
    margin: 0 auto;
    margin-top: 30px;
    display: flex;
}

.score {
    min-width: 150px;
    width: 150px;
    height: 150px;
    background: #f6f8fa;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.score_num_wrap {
    text-align: center;
}

.num_text {
    font-size: 60px;
    line-height: 60px;
    color: #0072d1;
    font-family: com_font_20th_bold !important;
}

.total_text {
    font-size: 30px;
    line-height: 35px;
    color: #666;
    font-family: com_font_20th !important;
}

.score_tip {
    font-size: 17px;
    line-height: 29px;
    color: #666;
    text-align: center;
    margin-top: 4px;
}

.tag_score {
    height: 130px;
    width: 900px;
    // background-color: aliceblue;
    display: inline;
    margin-left: 20px;
}

.tag_score_item {
    display: flex;
}

.tag_score_title {
    margin-left: 20px;
    margin-right: 20px;
    font-size: 20px;
    align-self: center;
    color: #666;
}

:deep(.el-rate__icon) {
    font-size: 25px
}

:deep(.el-rate__text) {
    font-size: 18px
}

.keyWords {
    width: 1500px;
    height: 130px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.comments {
    width: 1000px;
    background-color: #f6f8fa;
    height: 60%;
}

.comments_title {
    margin: 0 auto;
    text-align: center;
    color: #666;
    font-size: 20px;
}

.comment {
    // width: 750px;
    margin: 0 auto;
    font-family: PingFang SC, HarmonyOS_Regular, Helvetica Neue, Microsoft YaHei,
        sans-serif;
}

.author-title:not(:last-child) {
    border-bottom: 1px solid rgba(178, 186, 194, 0.3);
}

.author-title {
    padding: 10px;
}

.author-title .header-img {
    display: inline-block;
    vertical-align: top;
}

.author-title .author-info {
    display: inline-block;
    margin-left: 5px;
    width: 60%;
    height: 40px;
    line-height: 20px;
}

.author-title .author-info>span {
    display: block;
    cursor: pointer;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.author-title .author-info .author-name {
    color: #303133;
    font-size: 18px;
    font-weight: 500;
}

.reply-box .talk-box {
    color: #606266;
}

.reply-box .talk-box span {
    color: #6298ce;
}

.author-title .author-info .author-time {
    font-size: 14px;
}

.author-time {
    color: #606266;
}


@media screen and (max-width: 1200px) {
    .author-title .icon-btn {
        width: 20%;
        padding: 7px;
    }
}

.author-title .talk-box {
    margin: 0 50px;
}

.author-title .talk-box>p {
    margin: 0;
}

.comment_tag {
    margin-left: 5px;
    height: 20px;
    line-height: 20px;
}

.el-row {
    margin-bottom: 20px;
}

.el-row:last-child {
    margin-bottom: 0;
}

.el-col {
    border-radius: 4px;
}

.img {
    width: 110px;
    height: 110px;
    border-radius: 6px;
}
.page {
    // position: absolute;
    // top:800px;
	margin-left: 300px;
}
</style>