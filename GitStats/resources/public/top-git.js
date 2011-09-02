function drawChart(dataAsJson) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Name');
  data.addColumn('number', 'Core');
  data.addColumn('number', 'Aim');

    var total = dataAsJson.length
  data.addRows(total);

    $j.each(dataAsJson, function(index, pair) {
          data.setValue(index, 0, pair.name);
          data.setValue(index, 1, parseInt(pair.core));
          data.setValue(index, 2, parseInt(pair.aim));
    });


  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
  chart.draw(data, {width: 800, height: 480, title: 'Top Git',
                    hAxis: {title: 'Name', titleTextStyle: {color: 'red'}},
                    isStacked: true
                   });
}

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

function drawScatterMap(dataAsJson) {
           var data = new google.visualization.DataTable();
        data.addColumn('date', 'Date');

            data.addRows(1000);

        var seriesOptions = [];

        // Function to get the Max value in Array
        Array.max = function( array ){
        return Math.max.apply( Math, array );
        };

        // Function to get the Min value in Array
        Array.min = function( array ){
        return Math.min.apply( Math, array );
        };


        var seriesKeys = $j.map(dataAsJson, function(series, outer) {
            return parseInt(series.seriesKey);
        });


        $j.each(dataAsJson, function(outer, series) {
            data.addColumn('datetime', 'Series' + series.seriesKey);
            seriesOptions.push({pointSize: translate(parseInt(series.seriesKey), Array.min(seriesKeys), Array.max(seriesKeys), 20, 80)});
        });

        $j.each(dataAsJson, function(outer, series) {
            $j.each(series.seriesValue, function(inner, value) {
                var date = new Date(value.date);
                var time = new Date(value.date);
                time.setDate(1);
                time.setMonth(1);
                time.setYear(2000);

                var allValues = [date];

                for(i = 0; i < outer; i++)
                    allValues.push(null);

                allValues.push(time);

                for(i = (outer + 1); i < dataAsJson.length; i++)
                    allValues.push(null);

                data.addRow(allValues);
            });
        });


        var chart = new google.visualization.ScatterChart(document.getElementById('code-change'));
        chart.draw(data, {width: 2000, height: 1200,
                          title: 'Code Size',
                          hAxis: {title: 'Day'},
                          vAxis: {title: 'Time'},
                          series: seriesOptions,
                          legend: 'none'
                         });
      }

function translate(value, leftMin, leftMax, rightMin, rightMax) {
    var leftSpan = Math.log(leftMax) - Math.log(leftMin);
    var rightSpan = rightMax - rightMin;

    var valueScaled = (Math.log(value) - Math.log(leftMin))/leftSpan;
    if (valueScaled < 0) valueScaled = valueScaled * -1;

    return rightMin + (valueScaled * rightSpan);
}

function getDataAndDrawChart() {
    $j.get('/top-git.json', drawChart, "json");
    $j.get('/all-words.json', drawTagCloud, "json");
    $j.get('/pair-counts.json', drawPairChart, "json");
    $j.get('/pair-counts-seperate.json', drawHeatMap, "json");
    $j.get('/code-changes.json', drawScatterMap, "json");
}



