import { Box, Tab, Tabs } from '@mui/material';
import { useState } from "react";
import ReactECharts from 'echarts-for-react';

// function covertvalue(inputVal) {
//   let convertedvalue = undefined;
//   if (inputVal) {
//     convertedvalue = Math.abs(Number(inputVal)) >= 1.0e+9
//       ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+9 + Number.EPSILON) * 100) / 100).toString() + "B"
//       : Math.abs(Number(inputVal)) >= 1.0e+6
//         ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+6 + Number.EPSILON) * 100) / 100).toString() + "M"
//         : Math.abs(Number(inputVal)) >= 1.0e+3
//           ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+3 + Number.EPSILON) * 100) / 100).toString() + "K"
//           : Math.abs(Number(inputVal));
//   }
//   return inputVal.toString().charAt(0) === '-' ? '-' + convertedvalue : convertedvalue;
// }

function StockPerfChart(props) {

    const [tabIndex, setTabIndex] = useState(0);
    // const { handleQueryLogs, currentDatabase, trafficLocation } = useContext(AppContext);
    // const [twelveHourData, setTwelveHourData] = useState([]);
    // const [oneDayData, setOneDayData] = useState([]);
    // const [oneWeekData, setOneWeekData] = useState([]);
    // const [oneMonthData, setOneMonthData] = useState([]);
    // const [oneYearData, setOneYearData] = useState([]);
    

    const handleTabChange = (event, newTabIndex) => {
        setTabIndex(newTabIndex);
    };


    const getLabels = (k) => {

      let result = [];

      switch(k){
        case 0:
          result = props.data['1H'].map( (e) => e.label);
          break;
        case 1:
          result = props.data['90MIN'].map( (e) => e.label);
          break;
        case 2:
          result = props.data["1DAY"].map ( (e) => e.label );
          break;
        case 3:
          result = ["WK1", "WK2", "WK3", "WK4"]
          break;
        case 4:
          result = [ "Q1", "Q2", "Q3", "Q4 "]
          break;
        default:
          break;
      }

      return result;

    }


    const getData = (k) => {

      switch(k) {
        case 0:
            return props.data['1H'].map( (e) => e.amount );            
        case 1:
            return props.data['90MIN'].map( (e) => e.amount );
        case 2:
            return props.data["1DAY"].map( (e) => e.amount );
        case 3:
            return props.data["1WEEK"].map( (e) => e.amount );
        case 4:
            return props.data["3MONTH"].map( (e) => e.amount );
        default:
            return props.data["3MONTH"].map( (e) => e.amount );
      }


    }

    const getChartOptions = (k) => {
    let chartOptions = 
      {   
        tooltip: {
          trigger: 'axis',
          // axisPointer: {
          //   type: 'shadow'
          // }
        },    
          xAxis: {
              type: 'category',
              data: getLabels(k)
            },
            yAxis: {
              type: 'value',
              axisLabel:{
                margin: 6,
                formatter: (function(value){ return ( value /1000 ) + 'k' }),
              }
            },
            series: [
              {
                color:'#7879f1',
                data: getData(k),
                type: 'line'
              }
            ]
      
      }

      if ( k % 2 === 0 ){
        return  { ...chartOptions, series: { fcolor:'#7879f1',   data: getData(k), type: 'bar'} }
      }

      return chartOptions;
    }

  
  if ( props.data === null || props.data === undefined) {
    return (<h6>Data loading in progress</h6>)
  }
  else if(props?.resultLength === 0){
    return (<h6>No data to display.</h6>)

  }  
  return (

        <Box sx={{width:"330px"}}>
           <Box sx={{ padding: 0, marginTop:"-30px"}}>
                          <ReactECharts option={getChartOptions(tabIndex)} />
            </Box>
            <Tabs value={tabIndex} variant="scrollable" scrollButtons allowScrollButtonsMobile
            onChange={handleTabChange} sx={{ marginTop: "-30px", paddingBottom: "25px" }}>
              <Tab label="12H"></Tab>
              <Tab label="1D"></Tab>
              <Tab label="1W"></Tab>
              <Tab label="1M"></Tab>
              <Tab label="1Y"></Tab>
            </Tabs>         
        </Box>
      
  )
}

export default StockPerfChart