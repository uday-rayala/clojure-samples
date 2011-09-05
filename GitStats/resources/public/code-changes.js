var dataURL = "/code-changes-plain.json";
var jsonData = $.ajax({ type: "GET", url: dataURL, async: false }).responseText;
var commits = JSON.parse(jsonData);

var startDate = new Date(commits[0].date);
var endDate = new Date(commits[commits.length - 1].date);
var numberOfDays = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24))

var goodDay = new Date(1990, 1, 1, 5, 0);
var nextGoodDay = new Date(1990, 1, 1, 23, 0);

var data = $.map(commits, function(commit) {
    var commitDate = new Date(commit.date);
    var minutes = new Date(goodDay);
    minutes.setHours(commitDate.getHours());
    minutes.setMinutes(commitDate.getMinutes());

    return {x: commitDate, y: minutes, z: commit.size, m: commit.message};
});

data.sort(function(a, b) {
    return b.z - a.z;
});

var sizes = $.map(data, function (d) {
    return d.z;
});

var minValue = Math.min.apply( Math, sizes);
var maxValue = Math.max.apply( Math, sizes);

var dateFormat = pv.Format.date("%d/%B");
var timeFormat = pv.Format.date("%H:%M");

function createSlider(minV, maxV) {
    $( "#slider-range" ).slider({
        range: true,
        min: minV,
        max: maxV,
        values: [ minValue, maxValue ],
        slide: function( event, ui ) {
            minValue = ui.values[ 0 ];
            maxValue = ui.values[ 1 ];
            $( "#sizes" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
        },
        stop: function( event, ui ) {
            vis.render();
        }
    });
}

$(document).ready(function() {
    createSlider(minValue, maxValue);

    $('#slice-slider').click(function() {
        createSlider(minValue, maxValue);
    });
});