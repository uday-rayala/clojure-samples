function BuildStatus(name, status, message) {
    this.name = name;
    this.status = status;
    this.message = message;
}

function start_radiator(summary_view, notification_view) {
    var build1 = new BuildStatus("main", "passed", "Uday/Mark: #120 Added functional tests");
    var build2 = new BuildStatus("aim", "failed", "Uday/Mark: #120 Added functional tests");
    var build3 = new BuildStatus("sig", "building", "Uday/Mark: #120 Added functional tests");
    var build4 = new BuildStatus("jperf", "passed", "Uday/Mark: #120 Added functional tests");
    var build5 = new BuildStatus("identity", "failed", "Uday/Mark: #120 Added functional tests");

    var builds = [build1, build2, build3, build4, build5];

    $.each(builds, function(index, buildStatus) {
        var $aBuild = $('<div class="build-status"/>')
        $aBuild.append($('<p>' + buildStatus.name + '</p>'));
        $aBuild.addClass(buildStatus.status);

        $(summary_view).append($aBuild);
    });

    var failingBuilds = $.grep(builds, function (buildStatus, index) {
        return buildStatus.status == "failed";
    });

    $.each(failingBuilds, function(index, buildStatus) {
        var $aNotification = $('<h1>' + buildStatus.name + '</h1>')
        $(notification_view).append($aNotification)
    });
}

function all_build_statues(summary_view, notification_view) {
    var url = "/cctray.xml";

    $.get(url, function(data) {
        start_radiator(summary_view, notification_view, "xml");
    });
}