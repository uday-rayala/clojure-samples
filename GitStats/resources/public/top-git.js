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

function getDataAndDrawChart() {
    $j.get('/top-git.json', drawChart, "json");
    $j.get('/all-words.json', drawTagCloud, "json");
    $j.get('/pair-counts.json', drawPairChart, "json");
    $j.get('/pair-counts-seperate.json', drawHeatMap, "json");
}



