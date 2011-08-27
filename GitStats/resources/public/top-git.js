function drawChart(dataAsJson) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Name');
  data.addColumn('number', 'Core');
  data.addColumn('number', 'Aim');

    var total = dataAsJson.length
  data.addRows(total);

    $.each(dataAsJson, function(index, pair) {
          data.setValue(index, 0, pair.name);
          data.setValue(index, 1, parseInt(pair.core));
          data.setValue(index, 2, parseInt(pair.aim));
    });


  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
  chart.draw(data, {width: 800, height: 480, title: 'Top Git',
                    hAxis: {title: 'Name', titleTextStyle: {color: 'red'}}
                   });
}

function drawPairChart(dataAsJson) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Pair');
  data.addColumn('number', 'Count');

    var total = dataAsJson.length
  data.addRows(total);

    $.each(dataAsJson, function(index, pair) {
          data.setValue(index, 0, pair.pair);
          data.setValue(index, 1, parseInt(pair.count));
    });


  var chart = new google.visualization.ColumnChart(document.getElementById('pair_chart_div'));
  chart.draw(data, {width: 1400, height: 480, title: 'Pair Count',
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

    $.each(dataAsJson, function(index, row) {
        data.setValue(index, 0, row.word);
        data.setValue(index, 1, parseInt(row.count));
    });


    var outputDiv = document.getElementById('tcdiv');
    var tc = new TermCloud(outputDiv);
    tc.draw(data, null);
}

function getDataAndDrawChart() {
    $.get('/top-git.json', drawChart, "json");
    $.get('/all-words.json', drawTagCloud, "json");
    $.get('/pair-counts.json', drawPairChart, "json");
}



