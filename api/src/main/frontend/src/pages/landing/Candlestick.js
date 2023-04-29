import { useEffect } from "react";
import Chart from 'react-apexcharts';
import dayjs from "dayjs";


function CandleStick(props) {
  useEffect(() => {

  }, []);

  function getChartData(){
    let candleChartData = props?.chartData?.map(data =>{
      let axisData = {
        x: new Date(Date.parse(data.date)),
        y:[data.open, data.high,data.low,data.open]
      }

      return axisData;
    })
    return candleChartData;
  }

  let state = {
    series: [{
      name: 'candle',
      data: getChartData()
    }],
    options: {
      chart: {
        height: 350,
        type: 'candlestick',
      },
      tooltip: {
        enabled: true,
      },
      xaxis: {
        type: 'category',
        labels: {
          formatter: function (val) {
            return dayjs(val).format('MMM DD HH:mm')
          }
        }
      },
      yaxis: {
        tooltip: {
          enabled: true
        }
      }
    },
  }

  

  return (
    <div id="chart">
      <Chart options={state.options} series={state.series} type="candlestick" height={250} />
    </div>

  )
}

export default CandleStick