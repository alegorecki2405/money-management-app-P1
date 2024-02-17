function createFirstDiagram(balanceHistory) {
    var data = []
    for (const monthYear in balanceHistory) {
        if (balanceHistory.hasOwnProperty(monthYear)) {
            const value = balanceHistory[monthYear];
            data.push({y: value, label: monthYear});
        }
    }
    for (var i = 0; i < data.length; i++) {
        if (data[i].y < 0) {
            data[i].color = "red"; // Set color to red for negative values
        } else {
            data[i].color = "green"; // Set color to green for positive values
        }
    }
    var chart = new CanvasJS.Chart("chartContainer", {
        animationEnabled: true,
        theme: "light2",
        title: {
            text: "Per Month Resume Change"
        },
        data: [{
            type: "column",
            dataPoints: data
        }]
    });
    chart.render();
}