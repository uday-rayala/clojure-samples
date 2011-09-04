var chart;
var areas,names;
var codeData;

function getData(){
    var dataURL = "/code-area-worked.json";
    var jsonData = $.ajax({ type: "GET", url: dataURL, async: false }).responseText;
    codeData = JSON.parse(jsonData);

    areas = $.map(codeData[0].changes, function(d) {
        return d.area;
    });

    areas.sort();

    names = $.map(codeData, function(d) {
        return d.name;
    });
}

function byArea() {
    var values = [];

    $.each(areas, function(index, a) {
        var valueData = $.map(codeData, function(d) {
            var element = $.grep(d.changes, function(i) {
                return i.area == a;
            })[0];

            if (element)
                return element.size;
            else return 0;
        });

        values.push({name: a, data: valueData});
    });

    return values;
}

function byPerson() {
    var values = [];

    $.each(names, function(index, n) {
        var valueData = $.grep(codeData, function (d) {
            return d.name == n;
        })[0];

        var vs = $.map(areas, function(a) {
            var element = $.grep(valueData.changes, function(i) {
                return i.area == a;
            })[0];

            if (element)
                return element.size;
            else return 0;
        });

        values.push({name: n, data: vs});
    });

    return values;
}

function drawChart(container, categories, seriesValues) {
    var options = {
      chart: {
         renderTo: container,
         defaultSeriesType: 'column'
      },
      title: {
         text: 'Code commited'
      },
      xAxis: {
         categories: categories
      },
      tooltip: {
         formatter: function() {
            return ''+
                this.series.name +': '+ this.y +'';
         }
      },
      credits: {
         enabled: false
      },
      series: seriesValues
   };
   chart = new Highcharts.Chart(options);
}

$(document).ready(function() {
    getData();

    var i = 0;


    $.each(byArea(), function(index, vs) {
        var containerId = 'container'+i;
        var container = $('<div/>');
        container.attr('id', containerId);
        container.attr('class', 'container');
        $('#by-area').append(container);
        i++;

        drawChart(containerId, names, [vs]);
    });

    $.each(byPerson(), function(index, vs) {
        var containerId = 'container'+i;
        var container = $('<div/>');
        container.attr('id', containerId);
        container.attr('class', 'container');
        $('#by-person').append(container);
        i++;

        drawChart(containerId, areas, [vs]);
    });


});