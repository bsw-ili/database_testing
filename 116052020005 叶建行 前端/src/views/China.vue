<template>
    <div class="centent" ref="echarts"></div>
    <dv-border-box-1>省热度排行
        <dv-scroll-ranking-board :config="config" />
    </dv-border-box-1>
    <div class="radar" ref="radar">
    </div>
    <el-button class="btn" @click="changeOption()" size="large" type="primary" plain>{{ radarOption.title }}</el-button>
</template>

<script>
// 引入echarts
import echarts from 'echarts';
// 引入地图
import 'echarts/lib/chart/map';
// 引入js
import 'echarts/map/js/china.js';
import Charts from '@jiaminghi/charts'

export default {
    components: {},
    data() {
        return {
            myChart: null,
            config: {
                sort: false,
                unit: "人",
                rowNum: 8, // 表行数
                data: []
            },
            radarOption: '',
            option1: {
                title: "市热度TOP6",
                radar: {
                    indicator: [
                        { name: '西峡', max: 300 },
                        { name: '周口', max: 300 },
                        { name: '南阳', max: 300 },
                        { name: '驻马店', max: 300 },
                        { name: '郑州', max: 300 },
                        { name: '洛阳', max: 300 }
                    ],
                    axisLabel: {
                        style: {
                            fontSize: 15,
                        }
                    }
                },
                series: [
                    {
                        type: 'radar',
                        data: [111, 256, 178, 152, 266, 132]
                    }
                ]
            },

            option2: {
                title: "市景点数TOP6",
                radar: {
                    indicator: [
                        { name: '西峡', max: 300 },
                        { name: '周口', max: 300 },
                        { name: '南阳', max: 300 },
                        { name: '驻马店', max: 300 },
                        { name: '郑州', max: 300 },
                        { name: '洛阳', max: 300 }
                    ],
                    axisLabel: {
                        style: {
                            fontSize: 15,
                        }
                    }
                },
                series: [
                    {
                        type: 'radar',
                        data: [223, 189, 214, 265, 178, 155]
                    }
                ],
                axisLabel: {
                    show: true,
                    style: {
                        fontSize: 15,
                    }
                }
            },
        }
    },

    mounted() {
        this.radarOption = this.option1
        this.initMap();
        this.getRadarData();
        setTimeout(() => {
            this.initRadar();
        }, 500)

    },
    // 方法集合
    methods: {
        initMap() {
            this.$axios.get('/province/getAll')
                .then(response => {
                    let dataList = response.data.data;
                    this.config.data = response.data.data;

                    let myChart = echarts.init(this.$refs.echarts);
                    let option = {
                        title: {
                            text: "全国旅游热点图",
                            left: "center",
                            textStyle: {
                                color: "black",
                                fontSize: 30,
                                fontStyle: 'normal',
                                fontWeight: "bold",
                                fontFamily: "YouYuan",
                            },
                        },
                        tooltip: {
                            // 鼠标移到图里面的浮动提示框
                            formatter: function (e, t, n) {
                                return e.name + "：" + e.value + "人";
                            },
                        },
                        // 热力地图
                        visualMap: {
                            min: 0,
                            max: 120000,
                            left: 10,
                            textStyle: {
                                color: "black",
                            },

                            // type: 'piecewise',
                            calculable: true,
                        },

                        series: [
                            {
                                name: "好友数",
                                data: dataList,
                                type: "map",
                                map: "china",
                                zoom: 1.2,
                                aspectScale: 0.75,
                                label: {
                                    // 默认文本标签样式
                                    normal: {
                                        show: false
                                    },
                                    // 高亮文本标签样式
                                    emphasis: {
                                        color: "white",
                                        fontSize: 12,
                                        fontWeight: "bold",
                                    },
                                },
                                itemStyle: {
                                    // 默认区域样式
                                    normal: {
                                        // 区域背景透明
                                        areaColor: "transparent",
                                        borderColor: "rgba(39,211,233, 1)",
                                        borderWidth: 1,
                                    },
                                    // 高亮区域样式
                                    emphasis: {
                                        // 高亮区域背景色
                                        areaColor: "#01ADF2",
                                    },
                                },
                            },
                        ],
                    };
                    myChart.setOption(option);
                    var _this = this;
                    myChart.on("click", function (params) {
                        if (params.data.name) {
                            let cities = params.data.cities
                            _this.$router.push({
                                path: '/tourProduct',
                                query: { value: params.name, cities: cities },
                            });
                            return;
                        }
                    });

                    window.addEventListener('resize', function () {
                        myChart.resize();
                    });

                    this.$message({
                        type: 'success',
                        message: response.data.message
                    });
                });
        },
        initRadar() {
            this.myChart = new Charts(this.$refs.radar)
            this.myChart.setOption(this.radarOption)
            // myChart.resize()
        },
        changeOption() {
            (this.radarOption == this.option1) ?
                this.radarOption = this.option2 : this.radarOption = this.option1
            this.myChart.setOption(this.radarOption)
        },
        getRadarData() {
            this.$axios.get("/attraction/analyse"
            ).then(response => {
                const res = response.data.data
                this.option1.radar.indicator = res.heat.indicator
                this.option1.series[0].data = res.heat.data

                this.option2.radar.indicator = res.num.indicator
                this.option2.series[0].data = res.num.data

            })
        },
    }
};
</script>
<style lang="scss" scoped>
.centent {
    position: absolute;
    width: 600px;
    height: 600px;
    top: 20px;
    left: 300px;
}

.dv-border-box-1 {
    position: absolute;
    left: 1000px;
    top: 30px;
    width: 250px;
    height: 380px;
}

.dv-scroll-ranking-board {
    color: black;
    margin: 0 auto;
    width: 210px;
    height: 320px;
    margin-top: 10px;
}

.radar {
    position: absolute;
    width: 400px;
    height: 400px;
    // border: 1px red solid;
    left: 940px;
    top: 420px;
}

.btn {
    position: absolute;
    left: 1200px;
    top: 430px;
}
</style>
