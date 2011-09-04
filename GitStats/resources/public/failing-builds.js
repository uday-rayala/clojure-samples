function firstNonZeroElement(arr) {
    var value = 0;
    var counting = true;

    $.each(arr, function (index, element) {
        if(counting && element != 0) {
            value = index;
            counting = false;
        }
    });

    return value;
}

var dataURL = "/failed-builds-by-day.json";
var jsonData = $.ajax({ type: "GET", url: dataURL, async: false }).responseText;
var failedBuilds = JSON.parse(jsonData);

var start = firstNonZeroElement(failedBuilds);
var end = failedBuilds.length - firstNonZeroElement(failedBuilds.slice(0).reverse());

var nonZeroElements = failedBuilds.slice(start, end);

var data = nonZeroElements;
