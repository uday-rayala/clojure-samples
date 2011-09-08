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

var realMinValue = minValue;
var realMaxValue = maxValue;

var zoom = 100;

var dateFormat = pv.Format.date("%d/%B");
var timeFormat = pv.Format.date("%H:%M");

function createSizeSlider(minV, maxV) {
    $( "#sizes" ).text(minV + " - " + maxV );

    $( "#slider-range" ).slider({
        range: true,
        min: minV,
        max: maxV,
        values: [ minValue, maxValue ],
        slide: function( event, ui ) {
            minValue = ui.values[ 0 ];
            maxValue = ui.values[ 1 ];
            updateValues();
        },

        stop: function( event, ui ) {
            vis.render();
        }
    });
}

function updateValues () {
    $( "#sizes" ).text(minValue + " - " + maxValue);
}

function updateSliders() {
    $( "#slider-range").slider("values", 0, minValue);
    $( "#slider-range").slider("values", 1, maxValue);
    $( "#slider-range").slider("refresh");
    updateValues();
    vis.render();
}

$(document).ready(function() {
    createSizeSlider(minValue, maxValue);

    $('#slice-slider').click(function() {
        createSizeSlider(minValue, maxValue);
    });

    $( "#slider-vertical" ).slider({
        orientation: "vertical",
        range: "min",
        min: 100,
        max: 500,
        value: 100,
        slide: function( event, ui ) {
            zoom = ui.value;
            $('#zoom-text').text(zoom + " %");
        },
        stop: function( event, ui ) {
            vis.render();
        }
    });

    $('#very-small-commits').click(function() {
        minValue = 0;
        maxValue = 50;
        updateSliders();
    });
    $('#small-commits').click(function() {
        minValue = 50;
        maxValue = 200;
        updateSliders();
    });
    $('#medium-commits').click(function() {
        minValue = 200;
        maxValue = 1000;
        updateSliders();
    });
    $('#big-commits').click(function() {
        minValue = 1000;
        maxValue = 10000;
        updateSliders();
    });
    $('#very-big-commits').click(function() {
        minValue = 10000;
        maxValue = realMaxValue;
        updateSliders();
    });
});