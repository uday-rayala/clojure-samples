var chart;
var areas,names;
var codeData;

function getData(){
    var dataUrl = "/functional-areas-and-commiters.json"
    var jsonData = $.ajax({type : 'GET', url:dataUrl, async:false}).responseText;
    stories = JSON.parse(jsonData);	
}

function byStory() {
   var values = [];
   $.each(stories, function(index, entries) {
     var value = { area: index, commiters: [] };
     var commiters = _(entries).chain().map(function(entry) { return entry.commiters; }).flatten().uniq().value();
     var commits = _(entries).chain().map(function(entry) { return entry.commits; }).value();

	 var commitersAndSize = {};
	 $.each(commiters, function(index, commiter) {
	   commitersAndSize[commiter] = _(commits).chain().flatten().filter(function(commit) { return _(commit.people).include(commiter); }).reduce(function(acc, commit) { return acc + commit.size;  }, 0).value();
	 });


     $.each(commiters, function(index, commiter) {
	   value.commiters.push([commiter, commitersAndSize[commiter]]);
     })	;
     values.push(value);
   });
   return values;

	 //    var values = [];
	 //    $.each(stories, function(index, story) {
	 //      var value = { story:story.story, area: story.area, commiters: [] };
	 // 	
	 // var commiters = {};
	 // $.each(story.commiters, function(index, commiter) {
	 //   commiters[commiter] = _(story.commits).chain().filter(function(commit) { return _(commit.people).include(commiter); }).reduce(function(acc, commit) { return acc + commit.size;  }, 0).value();
	 // });
	 // 
	 //      var numberOfCommiters = story.commiters.length;
	 //      $.each(story.commiters, function(index, commiter) {
	 //   value.commiters.push([commiter, commiters[commiter]]);
	 //      })	;
	 //      values.push(value);
	 //    });
	 //    return values;

}


function drawChart(container, area, seriesValues) {

   var options = {
	   		      chart: { renderTo: container},
	   		      title: {
	   		        text: area
	   		      },
	   		      tooltip: {
	   		         formatter: function() {
	   		            return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
	   		         }
	   		      },
	   		      plotOptions: {
	   		         pie: {
	   		            allowPointSelect: true,
	   		            cursor: 'pointer',
	   		            dataLabels: {
	   		               enabled: true,
	   		               color: Highcharts.theme.textColor || '#000000',
	   		               connectorColor: Highcharts.theme.textColor || '#000000',
	   		               formatter: function() {
	   		                  return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
	   		               }
	   		            }
	   		         }
	   		      },
	   		       series: [{
	   		         type: 'pie',
	   		         name: 'Story Commiters',
	   		         data: seriesValues
	   		      }]
	   		   }

   new Highcharts.Chart(options);

}

$(document).ready(function() {
    getData();
    var i = 0;

    $.each(byStory(), function(index, story) {
        var containerId = 'container'+i;
        var container = $('<div/>');
        container.attr('id', containerId);
        container.attr('class', 'container');
        $('#by-story').append(container);
        i++;

        drawChart(containerId, story.area, story.commiters);
    });
});
