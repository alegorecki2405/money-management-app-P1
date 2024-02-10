function calculateIncomes(incomes) {
    let perType = new Map();

    incomes.forEach(function (income) {
        if (!perType.has(income.type)) {
            perType.set(income.type, income.amount);
        } else {
            let previousAmount = perType.get(income.type);
            perType.set(income.type, previousAmount + income.amount);
        }
    });
    var data = []
    var sum = 0;
    perType.forEach((value, key) => {
        sum += value;
        console.log(key);
    })
    perType.forEach((value, key) => {
        var percent = (value / sum) * 100
        data.push({y: percent, label: key})
    })
    return data;
}

//pay income
function payIncome(incomeId) {
    fetch('/update-income-date/' + incomeId, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                console.error('Failed to update income date');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

//filters


// Function to handle start date or end date change
function handleDateChange() {
    var startDate = document.getElementById('startDate').value;
    var endDate = document.getElementById('endDate').value;

    if (startDate !== '' || endDate !== '') {
        document.getElementById('timePeriod').value = '';
    }
}

function handleSelectChange() {
    var timePeriodSelect = document.getElementById('timePeriod').value;

    if (timePeriodSelect !== '') {
        document.getElementById('startDate').value = '';
        document.getElementById('endDate').value = '';
    }
}

function applyFilters() {
    document.getElementById('filterForm').submit();
}

function resetFilters() {
    document.getElementById('typeFilter').value = '';
    document.getElementById('maxAmount').value = '';
    document.getElementById('minAmount').value = '';
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    document.getElementById('timePeriod').value = '';
    document.getElementById('filterForm').submit();
}

function toggleForm() {
    var x = document.getElementById("filterForm")
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
}
