function drawPairChart(dataAsJson) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Pair');
  data.addColumn('number', 'Count');

    var total = dataAsJson.length
  data.addRows(total);

    $j.each(dataAsJson, function(index, pair) {
          data.setValue(index, 0, pair.pair);
          data.setValue(index, 1, parseInt(pair.count));
    });


  var chart = new google.visualization.ColumnChart(document.getElementById('pair_chart_div'));
  chart.draw(data, {width: 1400, height: 800, title: 'Pair Count',
                    hAxis: {title: 'Name', titleTextStyle: {color: 'red'}}
                   });
}

function drawTagCloud(dataAsJson) {
    var data = new google.visualization.DataTable();
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Label');
    data.addColumn('number', 'Value');

    var total = dataAsJson.length
    data.addRows(total);

    $j.each(dataAsJson, function(index, row) {
        data.setValue(index, 0, row.word);
        data.setValue(index, 1, parseInt(row.count));
    });


    var outputDiv = document.getElementById('tcdiv');
    var tc = new TermCloud(outputDiv);
    tc.draw(data, null);
}

function drawHeatMap(dataAsJson) {
    var data = new google.visualization.DataTable();

    data.addColumn('string', 'Name');

    $j.each(dataAsJson.names, function (index, name) {
      data.addColumn('number', name);
    });


    data.addRows(dataAsJson.names.length);
    $j.each(dataAsJson.names, function (outer, name) {
    data.setCell(outer, 0, name);
    $j.each(dataAsJson.pairing[outer], function (inner, count) {
        data.setCell(outer, inner + 1, parseInt(count));
    });
    });

    heatmap = new org.systemsbiology.visualization.BioHeatMap(document.getElementById('heatmapContainer'));
    heatmap.draw(data, {cellWidth:30, cellHeight:30,
    //          passThroughBlack: true, startColor: {r: 255,g: 0, b: 0,a: 1 }, endColor: {r: 0,g: 255, b: 0,a: 1 }
    });
}

function drawStckedChart(dataAsJson) {
    var names = $j.map(dataAsJson, function(d) { return d.name; });
    var coreData = $j.map(dataAsJson, function(d) { return d.core})
    var aimData = $j.map(dataAsJson, function(d) { return d.aim})
    var data = [{ name: 'Aim', data: aimData }, { name: 'Core', data: coreData }];

    var chart = new Highcharts.Chart({
      chart: {
         renderTo: 'chart_div',
         defaultSeriesType: 'column'
      },
      title: {
         text: 'Top Git'
      },
      xAxis: {
         categories: names
      },
      yAxis: {
         min: 0,
         title: {
            text: 'Number of commits'
         },
         stackLabels: {
            enabled: true,
            style: {
               fontWeight: 'bold',
               color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
            }
         }
      },
      legend: {
         align: 'right',
         x: -100,
         verticalAlign: 'top',
         y: 20,
         floating: true,
         backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',
         borderColor: '#CCC',
         borderWidth: 1,
         shadow: false
      },
      tooltip: {
         formatter: function() {
            return '<b>'+ this.x +'</b><br/>'+
                this.series.name +': '+ this.y +'<br/>'+
                'Total: '+ this.point.stackTotal;
         }
      },
      plotOptions: {
         column: {
            stacking: 'normal',
            dataLabels: {
               enabled: true,
               color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
            }
         }
      },
       series: data
    });
}

function drawTopGitWithPairing(dataAsJson) {
    console.log(dataAsJson);
    dataAsJson.sort(function (a, b) { return a.all / (a.all - a.solo) - b.all / (b.all - b.solo)});
    var names = $j.map(dataAsJson, function(d) { return d.name; });
    var pairing = $j.map(dataAsJson, function(d) { return d.all - d.solo; });
    var solo = $j.map(dataAsJson, function(d) { return d.solo; });
    var data = [{name: 'Solo', data: solo}, {name: 'Pairing', data: pairing}];

    console.log(data);

    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'top-git-pairing',
            defaultSeriesType: 'column'
        },
        title: {
            text: 'Top Git Pairing'
        },
        xAxis: {
            categories: names
        },
        yAxis: {
            min: 0,
            title: {
            text: 'Number of commits'
        }
        },
        tooltip: {
            formatter: function() {
                return ''+ this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)';
            }
        },
        plotOptions: {
            column: {
                stacking: 'percent'
            }
        },
        series: data
    });

}


function getDataAndDrawChart() {
    console.log("Hello");
    $j.get('/top-git.json', drawStckedChart, "json");
    $j.get('/top-git-with-pairing.json', drawTopGitWithPairing, "json");
    $j.get('/all-words.json', drawTagCloud, "json");
    $j.get('/pair-counts.json', drawPairChart, "json");
    $j.get('/pair-counts-seperate.json', drawHeatMap, "json");
}



