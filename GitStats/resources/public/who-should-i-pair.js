var dataURL = "/pair-counts-seperate.json";
var jsonData = $.ajax({ type: "GET", url: dataURL, async: false }).responseText;
var pairingMatrix = JSON.parse(jsonData);

var names = pairingMatrix.names;
var allPairs = {};

$.each(names, function(i, outer_name){
    var pairs = pairingMatrix.pairing[i];
    var obj = {};
    var max = Math.max.apply(Math, pairs) + 1;


    $.each(names, function(j, inner_name) {
        if (inner_name != outer_name)
            obj[inner_name] = max - pairs[j];
    });

    allPairs[outer_name] = obj;
});

$(document).ready(function() {
    $.each(names, function(i, n) {
        var element = $('<div/>');
        element.attr('id', n);
        element.append('<h1>' + n + '</h1>');
        $('#center').append(element);

        var script = '<script type="text/javascript+protovis">showData("' + n +'");</script>';
        $('#' + n).append(script);
    });

});


