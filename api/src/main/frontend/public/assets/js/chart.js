 var ctx5 = document.getElementById("bar-chart").getContext("2d");
  
  new Chart(ctx5, {
    type: "bar",
    data: {
      labels: ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'],
      datasets: [{
        label: "Total Balance: $74,902",
        weight: 5,
        borderWidth: 0,
        borderRadius: 20,
        backgroundColor: '#7879F1',
        data: [4, 25, 50, 75, 20, 15, 40],
        fill: false,
        maxBarThickness: 35
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        }
      },
      scales: {
        y: {
          grid: {
            drawBorder: false,
            display: true,
            drawOnChartArea: true,
            drawTicks: false,
            borderDash: [5, 5]
          },
          ticks: {
            display: true,
            padding: 10,
            color: '#9ca2b7'
          }
        },
        x: {
          grid: {
            drawBorder: false,
            display: false,
            drawOnChartArea: true,
            drawTicks: true,
          },
          ticks: {
            display: true,
            color: '#9ca2b7',
            padding: 10
          }
        },
      },
    },
  });